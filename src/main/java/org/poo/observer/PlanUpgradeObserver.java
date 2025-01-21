package org.poo.observer;

import org.poo.banking.Bank;
import org.poo.banking.CardPaymentTransaction;
import org.poo.banking.Currency;
import org.poo.banking.PlanType;
import org.poo.banking.Transaction;
import org.poo.banking.TransferTransaction;
import org.poo.banking.User;
import org.poo.commands.SendMoneyCommand;


public final class PlanUpgradeObserver implements UserObserver {
    private static final double SILVER_ELIGIBLE_PAYMENTS = 300;

    @Override
    public void onNewTransaction(final User user, final Transaction transaction) {
        if (user.getPlanType() == PlanType.SILVER) {
            double amountInAccountCurrency = getAmountFromTransaction(transaction);
            Currency accCurrency = getCurrencyFromTransaction(transaction);

            double amountInRON = SendMoneyCommand.convertToRon(amountInAccountCurrency, accCurrency,
                    Bank.getInstance().getCurrencyGraph());

            if (amountInRON >= SILVER_ELIGIBLE_PAYMENTS) {
                user.incrementSilverEligiblePayments();
            }
        }
    }

    private double getAmountFromTransaction(final Transaction t) {

        if (t instanceof TransferTransaction) {
            return ((TransferTransaction) t).getAmount();
        }
        if (t instanceof CardPaymentTransaction) {
            return ((CardPaymentTransaction) t).getAmount();
        }
        return 0.0;
    }

    private Currency getCurrencyFromTransaction(final Transaction t) {
        if (t instanceof TransferTransaction) {
            return ((TransferTransaction) t).getType();
        }
        if (t instanceof CardPaymentTransaction) {
            return ((CardPaymentTransaction) t).getCurrency();
        }
        return Currency.RON;
    }
}
