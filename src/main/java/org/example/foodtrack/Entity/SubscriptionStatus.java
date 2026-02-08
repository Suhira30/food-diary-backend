package org.example.foodtrack.Entity;

public enum SubscriptionStatus {
    INACTIVE("Inactive"),
    ACTIVE("Active"),
    TRIALING("Trial"),
    PAST_DUE("Past Due"),
    CANCELED("Canceled"),
    UNPAID("Unpaid"),
    EXPIRED("Expired");

    private final String displayName;

    SubscriptionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SubscriptionStatus fromStripe(String stripeStatus) {
        if (stripeStatus == null) return INACTIVE;

        switch (stripeStatus.toLowerCase()) {
            case "active":
                return ACTIVE;
            case "trialing":
                return TRIALING;
            case "past_due":
                return PAST_DUE;
            case "canceled":
                return CANCELED;
            case "unpaid":
                return UNPAID;
            default:
                return INACTIVE;
        }
    }
}