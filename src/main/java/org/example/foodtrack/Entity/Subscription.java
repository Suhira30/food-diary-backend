package org.example.foodtrack.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions",
        indexes = {
                @Index(name = "idx_user_status", columnList = "user_id, status"),
                @Index(name = "idx_stripe_customer", columnList = "stripe_customer_id"),
                @Index(name = "idx_stripe_subscription", columnList = "stripe_subscription_id")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false)
    private SubscriptionPlan plan = SubscriptionPlan.FREE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status = SubscriptionStatus.INACTIVE;

    // Stripe integration fields
    @Column(name = "stripe_customer_id", unique = true)
    private String stripeCustomerId;

    @Column(name = "stripe_subscription_id", unique = true)
    private String stripeSubscriptionId;

    @Column(name = "stripe_price_id")
    private String stripePriceId;

    // Subscription period
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    // Payment tracking
    @Column(name = "current_period_start")
    private LocalDateTime currentPeriodStart;

    @Column(name = "current_period_end")
    private LocalDateTime currentPeriodEnd;

    @Column(name = "trial_end")
    private LocalDateTime trialEnd;

    // Metadata
    @Column(name = "cancel_at_period_end")
    private Boolean cancelAtPeriodEnd = false;

    @Column(name = "auto_renew")
    private Boolean autoRenew = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE &&
                (expiresAt == null || LocalDateTime.now().isBefore(expiresAt));
    }

    public boolean isPro() {
        return isActive() &&
                (plan == SubscriptionPlan.PRO_MONTHLY || plan == SubscriptionPlan.PRO_YEARLY);
    }

    public boolean canCancel() {
        return status == SubscriptionStatus.ACTIVE && !cancelAtPeriodEnd;
    }
}