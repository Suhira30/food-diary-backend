package org.example.foodtrack.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodtrack.Dto.Request.CreateCheckoutRequest;
import org.example.foodtrack.Dto.Response.CheckoutResponse;
import org.example.foodtrack.Dto.Response.SubscriptionStatusResponse;
import org.example.foodtrack.Entity.Subscription;
import org.example.foodtrack.Entity.SubscriptionPlan;
import org.example.foodtrack.Entity.SubscriptionStatus;
import org.example.foodtrack.Entity.User;
import org.example.foodtrack.Exception.BadRequestException;
import org.example.foodtrack.Exception.NotFoundException;
import org.example.foodtrack.Repo.SubscriptionRepository;
import org.example.foodtrack.Repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Value("${stripe.price.pro.monthly}")
    private String priceIdMonthly;

    @Value("${stripe.price.pro.yearly}")
    private String priceIdYearly;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${app.url}")
    private String appUrl;

    @Transactional
    public CheckoutResponse createCheckoutSession(CreateCheckoutRequest request, String email)
            throws StripeException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Optional<Subscription> existingSub = subscriptionRepository
                .findActiveSubscriptionByUserId(user.getId());

        if (existingSub.isPresent()) {
            throw new BadRequestException("You already have an active subscription");
        }

        String customerId = getOrCreateStripeCustomer(user);
        String priceId = "MONTHLY".equalsIgnoreCase(request.getPlan())
                ? priceIdMonthly
                : priceIdYearly;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setCustomer(customerId)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(priceId)
                                .setQuantity(1L)
                                .build()
                )
                .setSuccessUrl(appUrl + "/subscription/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(appUrl + "/subscription/cancel")
                .putMetadata("userId", String.valueOf(user.getId()))
                .putMetadata("userEmail", user.getEmail())
                .putMetadata("plan", request.getPlan())
                .build();

        Session session = Session.create(params);
        log.info("Created checkout session for user {}: {}", email, session.getId());

        return new CheckoutResponse(
                session.getId(),
                session.getUrl(),
                "Checkout session created successfully"
        );
    }

    private String getOrCreateStripeCustomer(User user) throws StripeException {
        List<Subscription> userSubscriptions = subscriptionRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());

        if (!userSubscriptions.isEmpty() && userSubscriptions.get(0).getStripeCustomerId() != null) {
            return userSubscriptions.get(0).getStripeCustomerId();
        }

        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(user.getEmail())
                .setName(user.getName())
                .putMetadata("userId", String.valueOf(user.getId()))
                .build();

        Customer customer = Customer.create(params);
        log.info("Created Stripe customer for user {}: {}", user.getEmail(), customer.getId());

        return customer.getId();
    }

    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (Exception e) {
            log.error("Webhook signature verification failed: {}", e.getMessage());
            throw new BadRequestException("Invalid webhook signature");
        }

        log.info("Received Stripe webhook event: {}", event.getType());

        switch (event.getType()) {
            case "checkout.session.completed":
                handleCheckoutSessionCompleted(event);
                break;
            case "customer.subscription.created":
            case "customer.subscription.updated":
                handleSubscriptionUpdated(event);
                break;
            case "customer.subscription.deleted":
                handleSubscriptionDeleted(event);
                break;
            case "invoice.payment_succeeded":
                log.info("Invoice payment succeeded - subscription will auto-renew");
                break;
            case "invoice.payment_failed":
                log.warn("Invoice payment failed - user may lose Pro access");
                break;
            default:
                log.info("Unhandled event type: {}", event.getType());
        }
    }

    private void handleCheckoutSessionCompleted(Event event) {
        Session session = (Session) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (session == null) {
            log.error("Failed to deserialize checkout session");
            return;
        }

        String userEmail = session.getMetadata().get("userEmail");
        String planStr = session.getMetadata().get("plan");
        String stripeSubscriptionId = session.getSubscription();
        String stripeCustomerId = session.getCustomer();

        User user = userRepository.findByEmail(userEmail).orElse(null);

        if (user == null) {
            log.error("User not found: {}", userEmail);
            return;
        }

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(SubscriptionPlan.fromInterval(planStr));
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStripeCustomerId(stripeCustomerId);
        subscription.setStripeSubscriptionId(stripeSubscriptionId);
        subscription.setStartedAt(LocalDateTime.now());

        String priceId = "MONTHLY".equalsIgnoreCase(planStr) ? priceIdMonthly : priceIdYearly;
        subscription.setStripePriceId(priceId);

        subscriptionRepository.save(subscription);

        user.setIsPro(true);
        userRepository.save(user);

        log.info("User {} subscribed to {} plan", userEmail, planStr);
    }
    private void handleSubscriptionUpdated(Event event) {
        com.stripe.model.Subscription stripeSubscription =
                (com.stripe.model.Subscription) event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);

        if (stripeSubscription == null) {
            log.error("Failed to deserialize subscription");
            return;
        }

        Optional<Subscription> subscriptionOpt = subscriptionRepository
                .findByStripeSubscriptionId(stripeSubscription.getId());

        Subscription subscription;

        if (subscriptionOpt.isPresent()) {
            subscription = subscriptionOpt.get();
        } else {
            Optional<Subscription> customerSub = subscriptionRepository
                    .findByStripeCustomerId(stripeSubscription.getCustomer());

            if (customerSub.isPresent()) {
                subscription = customerSub.get();
                subscription.setStripeSubscriptionId(stripeSubscription.getId());
            } else {
                log.warn("Subscription not found for: {}", stripeSubscription.getId());
                return;
            }
        }

        // Update basic subscription info
        subscription.setStatus(SubscriptionStatus.fromStripe(stripeSubscription.getStatus()));

        // Set expiration based on plan (simple approach)
        if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
            if (subscription.getPlan() == SubscriptionPlan.PRO_MONTHLY) {
                subscription.setExpiresAt(LocalDateTime.now().plusMonths(1));
            } else if (subscription.getPlan() == SubscriptionPlan.PRO_YEARLY) {
                subscription.setExpiresAt(LocalDateTime.now().plusYears(1));
            }
        }

        // Update plan from price ID
        if (stripeSubscription.getItems() != null &&
                !stripeSubscription.getItems().getData().isEmpty()) {
            String priceId = stripeSubscription.getItems().getData().get(0).getPrice().getId();
            subscription.setPlan(SubscriptionPlan.fromStripePriceId(priceId));
            subscription.setStripePriceId(priceId);
        }

        subscriptionRepository.save(subscription);

        // Update user Pro status
        User user = subscription.getUser();
        user.setIsPro(subscription.isActive());
        userRepository.save(user);

        log.info("Updated subscription for user {}: {}",
                user.getEmail(), subscription.getStatus());
    }

    private void handleSubscriptionDeleted(Event event) {
        com.stripe.model.Subscription stripeSubscription =
                (com.stripe.model.Subscription) event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);

        if (stripeSubscription == null) return;

        Optional<Subscription> subscriptionOpt = subscriptionRepository
                .findByStripeSubscriptionId(stripeSubscription.getId());

        if (subscriptionOpt.isEmpty()) return;

        Subscription subscription = subscriptionOpt.get();
        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscription.setCanceledAt(LocalDateTime.now());

        subscriptionRepository.save(subscription);

        User user = subscription.getUser();
        user.setIsPro(false);
        userRepository.save(user);

        log.info("Subscription canceled for user: {}", user.getEmail());
    }

    public SubscriptionStatusResponse getSubscriptionStatus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Optional<Subscription> subscriptionOpt = subscriptionRepository
                .findActiveSubscriptionByUserId(user.getId());

        SubscriptionStatusResponse response = new SubscriptionStatusResponse();

        if (subscriptionOpt.isPresent()) {
            Subscription sub = subscriptionOpt.get();
            response.setIsPro(sub.isActive());
            response.setPlan(sub.getPlan().getDisplayName());
            response.setStatus(sub.getStatus().getDisplayName());
            response.setStartedAt(sub.getStartedAt());
            response.setExpiresAt(sub.getExpiresAt());
            response.setCurrentPeriodEnd(sub.getCurrentPeriodEnd());
            response.setStripeSubscriptionId(sub.getStripeSubscriptionId());
            response.setCanCancel(sub.canCancel());
            response.setCancelAtPeriodEnd(sub.getCancelAtPeriodEnd());
        } else {
            response.setIsPro(false);
            response.setPlan(SubscriptionPlan.FREE.getDisplayName());
            response.setStatus(SubscriptionStatus.INACTIVE.getDisplayName());
            response.setCanCancel(false);
            response.setCancelAtPeriodEnd(false);
        }

        return response;
    }

    @Transactional
    public void cancelSubscription(String email) throws StripeException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Subscription subscription = subscriptionRepository
                .findActiveSubscriptionByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("No active subscription found"));

        if (subscription.getStripeSubscriptionId() == null) {
            throw new BadRequestException("Invalid subscription");
        }

        com.stripe.model.Subscription stripeSubscription =
                com.stripe.model.Subscription.retrieve(subscription.getStripeSubscriptionId());
        stripeSubscription.cancel();

        log.info("Canceled subscription for user: {}", email);
    }
}