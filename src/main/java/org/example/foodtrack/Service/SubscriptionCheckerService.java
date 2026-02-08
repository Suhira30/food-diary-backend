package org.example.foodtrack.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodtrack.Entity.Subscription;
import org.example.foodtrack.Entity.SubscriptionStatus;
import org.example.foodtrack.Entity.User;
import org.example.foodtrack.Repo.SubscriptionRepository;
import org.example.foodtrack.Repo.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionCheckerService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    /**
     * Check expired subscriptions every day at midnight
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkExpiredSubscriptions() {
        log.info("Starting expired subscriptions check...");

        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findExpiredSubscriptions(LocalDateTime.now());

        int expiredCount = 0;

        for (Subscription subscription : expiredSubscriptions) {
            if (!subscription.getCancelAtPeriodEnd()) {
                // Only expire if not set to cancel at period end
                subscription.setStatus(SubscriptionStatus.EXPIRED);
                subscriptionRepository.save(subscription);

                // Update user Pro status
                User user = subscription.getUser();
                user.setIsPro(false);
                userRepository.save(user);

                expiredCount++;
                log.info("Expired subscription for user: {}", user.getEmail());
            }
        }

        log.info("Subscription check complete. {} subscriptions expired.", expiredCount);
    }

    /**
     * Send reminder emails for expiring subscriptions (optional)
     * Runs at 9 AM daily
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendExpirationReminders() {
        log.info("Checking for expiring subscriptions...");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysFromNow = now.plusDays(3);

        List<Subscription> expiringSubscriptions = subscriptionRepository
                .findExpiringSubscriptions(now, threeDaysFromNow);

        for (Subscription subscription : expiringSubscriptions) {
            // TODO: Send email notification
            log.info("Subscription expiring soon for user: {} on {}",
                    subscription.getUser().getEmail(),
                    subscription.getExpiresAt());
        }

        log.info("Found {} subscriptions expiring in the next 3 days",
                expiringSubscriptions.size());
    }
}