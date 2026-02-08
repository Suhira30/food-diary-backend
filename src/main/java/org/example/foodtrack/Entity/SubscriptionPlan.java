package org.example.foodtrack.Entity;

public enum SubscriptionPlan {
    FREE("Free", 0.0, null, null),
    PRO_MONTHLY("Pro Monthly", 4.99, "MONTHLY", "price_1ABC123xyz"),
    PRO_YEARLY("Pro Yearly", 49.99, "YEARLY", "price_1DEF456xyz");

    private final String displayName;
    private final Double price;
    private final String interval;
    private final String stripePriceId;

    SubscriptionPlan(String displayName, Double price, String interval, String stripePriceId) {
        this.displayName = displayName;
        this.price = price;
        this.interval = interval;
        this.stripePriceId = stripePriceId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Double getPrice() {
        return price;
    }

    public String getInterval() {
        return interval;
    }

    public String getStripePriceId() {
        return stripePriceId;
    }

    public static SubscriptionPlan fromInterval(String interval) {
        if (interval == null) return FREE;

        switch (interval.toUpperCase()) {
            case "MONTHLY":
                return PRO_MONTHLY;
            case "YEARLY":
                return PRO_YEARLY;
            default:
                return FREE;
        }
    }

    public static SubscriptionPlan fromStripePriceId(String priceId) {
        if (priceId == null) return FREE;

        for (SubscriptionPlan plan : values()) {
            if (priceId.equals(plan.getStripePriceId())) {
                return plan;
            }
        }
        return FREE;
    }
}