package org.poo.banking;

public enum PlanType {
    STANDARD(0.002, "standard"),
    STUDENT(0.0, "student"),
    SILVER(0.001, "silver"),
    GOLD(0.0, "gold");
    private static final double MINIMUM_SILVER_AMOUNT = 500.0;

    private final double commissionRate;
    private final String displayName;

    PlanType(final double commissionRate, final String displayName) {
        this.commissionRate = commissionRate;
        this.displayName = displayName;
    }

    public double getCommissionRate() {
        return commissionRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Calculeaza taxa pentru o tranzactie in functie de planul curent.
     * Daca planul este SILVER si suma este sub pragul minim, taxa este zero.
     *
     * @param amount Suma tranzactiei.
     * @return Valoarea taxei calculate.
     */
    public double calculateFee(final double amount) {
        if (this == SILVER && amount < MINIMUM_SILVER_AMOUNT) {
            return 0.0;
        }

        return amount * commissionRate;
    }

    /**
     * Returneaza rata comisionului aplicabila pentru o tranzactie.
     * Daca planul este SILVER si suma este sub pragul minim, comisionul este zero.
     *
     * @param amount Suma tranzactiei.
     * @return Rata comisionului pentru tranzactie.
     */
    public double getCommissionForTransaction(final double amount) {
        if (this == SILVER && amount < MINIMUM_SILVER_AMOUNT) {
            return 0.0;
        }

        return commissionRate;
    }
}
