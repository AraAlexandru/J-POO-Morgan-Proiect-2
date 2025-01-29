package org.poo.utils;

import org.poo.banking.ClassicAccount;
import org.poo.banking.Currency;
import org.poo.banking.Graph;
import org.poo.banking.User;
import org.poo.commerciants.Commerciant;

import java.util.ArrayList;

public final class CashbackHandler {
    // Rate
    private static final double FOOD_CASHBACK_RATE = 0.02;
    private static final double CLOTHES_CASHBACK_RATE = 0.05;
    private static final double TECH_CASHBACK_RATE = 0.1;

    private static final int FOOD_CASHBACK_THRESHOLD = 2;
    private static final int CLOTHES_CASHBACK_THRESHOLD = 5;
    private static final int TECH_CASHBACK_THRESHOLD = 10;

    private CashbackHandler() { }

    /**
     * Aplica un numar de tranzactii pentru un comerciant specific.
     * Daca pragurile de cashback sunt atinse, se adauga cashback-ul corespunzator.
     *
     * @param account Contul clasic pentru care se actualizeaza numarul de tranzactii.
     * @param currentCommerciant Comerciantul pentru care se incrementeaza numarul de tranzactii.
     */
    public static void applyNrOfTransactions(final ClassicAccount account,
                                             final Commerciant currentCommerciant) {

        Integer currentNr = account.incrementTransactionCount(currentCommerciant.getName());
        if (currentNr == FOOD_CASHBACK_THRESHOLD
                && account.isGotFoodCashback()) {
            account.addCashback("Food", FOOD_CASHBACK_RATE);
        } else if (currentNr == CLOTHES_CASHBACK_THRESHOLD
                && account.isGotClothesCashback()) {
            account.addCashback("Clothes", CLOTHES_CASHBACK_RATE);
        } else if (currentNr == TECH_CASHBACK_THRESHOLD
                && account.isGotTechCashback()) {
            account.addCashback("Tech", TECH_CASHBACK_RATE);
        }
    }

    /**
     * Aplica pragul de cheltuieli pentru a calcula si adauga cashback-ul.
     *
     * @param account Contul clasic pentru care se verifica pragul de cheltuieli.
     * @param convertedAmount Suma convertita in valuta contului.
     * @param currentAccountCurrency Moneda contului curent.
     * @param currencyGraph Graful de conversie valutara.
     * @param user Utilizatorul asociat contului.
     * @param currentCommerciant Comerciantul asociat tranzactiei.
     */
    public static void applySpendingThreshold(final ClassicAccount account,
                                              final double convertedAmount,
                                              final Currency currentAccountCurrency,
                                              final Graph<Currency> currencyGraph,
                                              final User user,
                                              final Commerciant currentCommerciant) {
        double amountInRON = convertedAmount;
        if (currentAccountCurrency != Currency.RON) {
            ArrayList<Graph<Currency>.Edge> pathToRON =
                    currencyGraph.getPath(currentAccountCurrency, Currency.RON);
            if (pathToRON != null) {
                double totalRateToRON = 1.0;
                for (Graph<Currency>.Edge edge : pathToRON) {
                    totalRateToRON *= edge.getCost();
                }
                amountInRON = convertedAmount * totalRateToRON;
            }
        }

        double cashback = CashBackCalculator.calculateSpendingThresholdCashBack(
                account, amountInRON, convertedAmount, user.getPlanType());
        CashBackCalculator.applySpendingThresholdCashback(account, cashback);
        account.addSpendingThreshold(amountInRON);
    }

    /**
     * Aplica cashback-ul pentru tranzactiile anterioare, daca este cazul.
     *
     * @param account Contul clasic pentru care se verifica cashback-urile anterioare.
     * @param currentCommerciant Comerciantul pentru care se aplica cashback-ul.
     * @return Valoarea cashback-ului aplicat, sau null daca nu exista cashback-uri aplicabile.
     */
    public static Double applyPreviousCashbacks(final ClassicAccount account,
                                                final Commerciant currentCommerciant) {
        if (account.getCashbacks().containsKey(currentCommerciant.getType())) {
            Double cb = account.getCashbacks().get(currentCommerciant.getType());
            account.getCashbacks().remove(currentCommerciant.getType());
            return cb;
        }
        return null;
    }
}
