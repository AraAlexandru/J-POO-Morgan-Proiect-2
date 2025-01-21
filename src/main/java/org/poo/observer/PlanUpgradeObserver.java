package org.poo.observer;

import org.poo.banking.*;
import org.poo.banking.Transaction;
import org.poo.banking.PlanType;
import org.poo.commands.SendMoneyCommand;

public class PlanUpgradeObserver implements UserObserver {

    @Override
    public void onNewTransaction(User user, Transaction transaction) {
        if (user.getPlanType() == PlanType.SILVER) {
            double amountInAccountCurrency = getAmountFromTransaction(transaction);
            Currency accCurrency = getCurrencyFromTransaction(transaction);

            double amountInRON = SendMoneyCommand.convertToRon(amountInAccountCurrency, accCurrency,
                    Bank.getInstance().getCurrencyGraph());

            if (amountInRON >= 300) {
                user.incrementSilverEligiblePayments();
            }
        }
    }

    private double getAmountFromTransaction(Transaction t) {

        if (t instanceof TransferTransaction) {
            return ((TransferTransaction) t).getAmount();
        }
        if (t instanceof CardPaymentTransaction) {
            return ((CardPaymentTransaction) t).getAmount();
        }
        return 0.0;
    }

    private Currency getCurrencyFromTransaction(Transaction t) {
        if (t instanceof TransferTransaction) {
            return ((TransferTransaction) t).getType();
        }
        if (t instanceof CardPaymentTransaction) {
            return ((CardPaymentTransaction) t).getCurrency();
        }
        return Currency.RON;
    }
}
