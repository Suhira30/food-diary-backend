package org.example.foodtrack.Controller;

import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodtrack.Dto.Request.CreateCheckoutRequest;
import org.example.foodtrack.Dto.Response.CheckoutResponse;
import org.example.foodtrack.Dto.Response.FoodDiaryResponse;
import org.example.foodtrack.Dto.Response.SubscriptionStatusResponse;
import org.example.foodtrack.Service.StripePaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/food-diary/subscription")
public class SubscriptionController {

    private final StripePaymentService stripePaymentService;

    /**
     * Create checkout session
     * POST /v1/food-diary/subscription/create-checkout
     * Request body:
     * {
     *   "plan": "MONTHLY" or "YEARLY"
     * }
     * Response:
     * {
     *   "sessionId": "cs_test_...",
     *   "checkoutUrl": "https://checkout.stripe.com/...",
     *   "message": "Checkout session created successfully"
     * }
     */
    @PostMapping("/create-checkout")
    public ResponseEntity<CheckoutResponse> createCheckout(
            @Valid @RequestBody CreateCheckoutRequest request,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            CheckoutResponse response = stripePaymentService.createCheckoutSession(request, email);
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            log.error("Stripe error creating checkout: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CheckoutResponse(null, null, "Payment error: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating checkout: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CheckoutResponse(null, null, "Error: " + e.getMessage()));
        }
    }

    /**
     * Get subscription status
     * GET /v1/food-diary/subscription/status
     * Response:
     * {
     *   "isPro": true,
     *   "plan": "Pro Monthly",
     *   "status": "Active",
     *   "startedAt": "2026-01-15T10:00:00",
     *   "expiresAt": "2026-02-15T10:00:00",
     *   "currentPeriodEnd": "2026-02-15T10:00:00",
     *   "stripeSubscriptionId": "sub_...",
     *   "canCancel": true,
     *   "cancelAtPeriodEnd": false
     * }
     */
    @GetMapping("/status")
    public ResponseEntity<SubscriptionStatusResponse> getSubscriptionStatus(
            Authentication authentication) {
        try {
            String email = authentication.getName();
            SubscriptionStatusResponse response = stripePaymentService.getSubscriptionStatus(email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching subscription status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cancel subscription
     * POST /v1/food-diary/subscription/cancel
     * Cancels the user's active subscription. User will retain Pro access 
     * until the end of the current billing period.
     * Response:
     * {
     *   "message": "Subscription canceled successfully...",
     *   "statusCode": 200
     * }
     */
    @PostMapping("/cancel")
    public ResponseEntity<FoodDiaryResponse> cancelSubscription(
            Authentication authentication) {
        try {
            String email = authentication.getName();
            stripePaymentService.cancelSubscription(email);
            return ResponseEntity.ok(new FoodDiaryResponse(
                    "Subscription canceled successfully. You will have access until the end of your billing period.",
                    HttpStatus.OK.value()
            ));
        } catch (StripeException e) {
            log.error("Stripe error canceling subscription: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FoodDiaryResponse(
                            "Error canceling subscription: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    ));
        } catch (Exception e) {
            log.error("Error canceling subscription: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FoodDiaryResponse(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST.value()
                    ));
        }
    }

    /**
     * Stripe webhook endpoint
     * POST /v1/food-diary/subscription/webhook
     * This endpoint receives events from Stripe when subscription status changes.
     * Events handled:
     * - checkout.session.completed: When payment is successful
     * - customer.subscription.created: When subscription is created
     * - customer.subscription.updated: When subscription is renewed/modified
     * - customer.subscription.deleted: When subscription is canceled
     * - invoice.payment_succeeded: When payment succeeds
     * - invoice.payment_failed: When payment fails
     * Note: This endpoint is NOT authenticated (allowed in SecurityConfig)
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            log.info("Received webhook from Stripe");
            stripePaymentService.handleWebhook(payload, sigHeader);
            return ResponseEntity.ok("Webhook handled successfully");
        } catch (Exception e) {
            log.error("Webhook processing error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Webhook error: " + e.getMessage());
        }
    }

    /**
     * Health check for subscription service
     * GET /v1/food-diary/subscription/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Subscription service is running");
    }
}