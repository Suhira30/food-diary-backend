package org.example.foodtrack.Repo;

import org.example.foodtrack.Entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // Find active subscription by user
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.status = 'ACTIVE'")
    Optional<Subscription> findActiveSubscriptionByUserId(@Param("userId") Long userId);

    // Find by Stripe IDs
    Optional<Subscription> findByStripeCustomerId(String stripeCustomerId);
    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);

    // Find all subscriptions for a user (history)
    List<Subscription> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Find expiring subscriptions (for reminders)
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' " +
            "AND s.expiresAt IS NOT NULL " +
            "AND s.expiresAt BETWEEN :startDate AND :endDate")
    List<Subscription> findExpiringSubscriptions(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Find expired subscriptions
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' " +
            "AND s.expiresAt IS NOT NULL " +
            "AND s.expiresAt < :now")
    List<Subscription> findExpiredSubscriptions(@Param("now") LocalDateTime now);

    // Count active Pro subscriptions
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = 'ACTIVE' " +
            "AND (s.plan = 'PRO_MONTHLY' OR s.plan = 'PRO_YEARLY')")
    Long countActiveProSubscriptions();
}