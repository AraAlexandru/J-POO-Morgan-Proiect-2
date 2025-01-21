package org.poo.banking;

public enum PlanType {
    STANDARD(0.002, "standard"),
    STUDENT(0.0, "student"),
    SILVER(0.001, "silver"),
    GOLD(0.0, "gold");

    private final double commissionRate;
    private final String displayName;

    PlanType(double commissionRate, String displayName) {
        this.commissionRate = commissionRate;
        this.displayName = displayName;
    }

    public double getCommissionRate() {
        return commissionRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double calculateFee(double amount) {
        if (this == SILVER && amount < 500) {
            return 0.0;
        }

        return amount * commissionRate;
    }

    public double getCommissionForTransaction(double amount) {
        if (this == SILVER && amount < 500) {
            return 0.0;
        }

        return commissionRate;
    }
}
