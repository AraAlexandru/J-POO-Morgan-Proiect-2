package org.poo.utils;

import org.poo.banking.ClassicAccount;
import org.poo.banking.PlanType;


public class CashBackCalculator {

    // Praguri pentru cashback
    private static final double HIGH_SPENDING_THRESHOLD = 500.0;
    private static final double MEDIUM_SPENDING_THRESHOLD = 300.0;
    private static final double LOW_SPENDING_THRESHOLD = 100.0;

    // Rata cashback pentru planuri diferite și praguri
    private static final double GOLD_HIGH_RATE = 0.007;
    private static final double SILVER_HIGH_RATE = 0.005;
    private static final double STANDARD_HIGH_RATE = 0.0025;

    private static final double GOLD_MEDIUM_RATE = 0.0055;
    private static final double SILVER_MEDIUM_RATE = 0.004;
    private static final double STANDARD_MEDIUM_RATE = 0.002;

    private static final double GOLD_LOW_RATE = 0.005;
    private static final double SILVER_LOW_RATE = 0.003;
    private static final double STANDARD_LOW_RATE = 0.001;

    /**
     * Constructor privat pentru a preveni instantierea clasei utilitare.
     */
    private CashBackCalculator() {

    }

    /**
     * Calculeaza cashback-ul pentru un cont in functie de pragurile de
     * cheltuieli si tipul de plan.
     * @param account Contul pentru care se calculeaza cashback-ul.
     * @param amount Suma adaugata la pragul de cheltuieli.
     * @param convertedAmount Suma convertita pentru cashback.
     * @param planType Tipul de plan al utilizatorului (GOLD, SILVER, STANDARD).
     * @return Valoarea cashback-ului calculat.
     */
    public static double calculateSpendingThresholdCashBack(final ClassicAccount account,
                                                            final double amount,
                                                            final double convertedAmount,
                                                            final PlanType planType) {
        double cashbackRate = 0.0;
        double totalSpending = account.getTotalSpendingThreshold() + amount;

        if (totalSpending >= HIGH_SPENDING_THRESHOLD) {
            cashbackRate = getHighRate(planType);
        } else if (totalSpending >= MEDIUM_SPENDING_THRESHOLD) {
            cashbackRate = getMediumRate(planType);
        } else if (totalSpending >= LOW_SPENDING_THRESHOLD) {
            cashbackRate = getLowRate(planType);
        }

        return convertedAmount * cashbackRate;
    }

    /**
     * Returneaza rata de cashback pentru pragul inalt, in functie de tipul de plan.
     *
     * @param planType Tipul de plan al utilizatorului.
     * @return Rata de cashback corespunzatoare.
     */
    private static double getHighRate(final PlanType planType) {
        switch (planType) {
            case GOLD:
                return GOLD_HIGH_RATE;
            case SILVER:
                return SILVER_HIGH_RATE;
            default:
                return STANDARD_HIGH_RATE;
        }
    }

    /**
     * Returneaza rata de cashback pentru pragul mediu, in functie de tipul de plan.
     *
     * @param planType Tipul de plan al utilizatorului.
     * @return Rata de cashback corespunzatoare.
     */
    private static double getMediumRate(final PlanType planType) {
        switch (planType) {
            case GOLD:
                return GOLD_MEDIUM_RATE;
            case SILVER:
                return SILVER_MEDIUM_RATE;
            default:
                return STANDARD_MEDIUM_RATE;
        }
    }

    /**
     * Returneaza rata de cashback pentru pragul jos, in functie de tipul de plan.
     *
     * @param planType Tipul de plan al utilizatorului.
     * @return Rata de cashback corespunzatoare.
     */
    private static double getLowRate(final PlanType planType) {
        switch (planType) {
            case GOLD:
                return GOLD_LOW_RATE;
            case SILVER:
                return SILVER_LOW_RATE;
            default:
                return STANDARD_LOW_RATE;
        }
    }

    /**
     * Aplica suma cashback-ului calculata la soldul contului specificat.
     *
     * @param account Contul pentru care se aplica cashback-ul.
     * @param cashback Valoarea cashback-ului care trebuie adaugata.
     */
    public static void applySpendingThresholdCashback(final ClassicAccount account,
                                                      final double cashback) {
        account.addFunds(cashback);
    }
}
