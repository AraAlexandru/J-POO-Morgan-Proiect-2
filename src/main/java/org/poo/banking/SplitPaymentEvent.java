package org.poo.banking;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Getter
public final class SplitPaymentEvent {
    private final Currency currency;
    private final List<ClassicAccount> accountsInvolved;
    private final double amount;
    private final List<Double> amountsToPay;
    private final boolean[] accepted;
    private final String type;
    private final int timestamp;
    private final Bank bank = Bank.getInstance();
    private final Graph graph;

    public SplitPaymentEvent(final Currency currency,
                             final List<ClassicAccount> accountsInvolved,
                             final double amount,
                             final List<Double> amountsToPay,
                             final String type, final int timestamp,
                             final Graph graph) {
        this.amount = amount;
        this.currency = currency;
        this.accountsInvolved = accountsInvolved;
        this.amountsToPay = amountsToPay;
        this.accepted = new boolean[accountsInvolved.size()];
        Arrays.fill(accepted, false);
        this.timestamp = timestamp;
        this.type = type;
        this.graph = graph;
    }

    private boolean checkAllAccepted() {
        for (boolean accept : accepted) {
            if (!accept) {
                return false;
            }
        }
        return true;
    }

    private void addTransactions(final String error) {
        List<String> ibanList = new ArrayList<>();
        for (ClassicAccount account : accountsInvolved) {
            ibanList.add(account.getIban());
        }

        for (int i = 0; i < accountsInvolved.size(); i++) {
            final SplitPaymentTransaction transaction =
                    new SplitPaymentTransaction(timestamp, amount, currency, ibanList,
                            accountsInvolved.get(i).getIban(),
                            amountsToPay, type, error);
            addTransaction(i, transaction);
        }
    }


    private void addTransaction(final int i, final SplitPaymentTransaction transaction) {
        User user = bank.getUserByAccount(accountsInvolved.get(i).getIban());
        user.addTransaction(transaction);
        user.getTransactions().sort(Comparator.comparingLong(Transaction::getTimestamp));
        user.removeSplitPayment(this);
    }

    private void notEnoughFunds(final String iban) {
        addTransactions("Account " + iban + " has insufficient funds for a split payment.");
    }

    /**
     * Rejects the split payment.
     *
     * @param user the user that rejects the payment
     */
    public void reject(final User user) {
        for (final ClassicAccount account : accountsInvolved) {
            User owner = bank.getUserByAccount(account.getIban());
            if (owner == user) {
                addTransactions("One user rejected the payment.");
                return;
            }
        }
    }

    /**
     * Checks if the user has already accepted the payment.
     *
     * @param user the user to check
     * @return true if the user has already accepted the payment, false otherwise
     */
    public boolean hasAccepted(final User user) {
        for (int i = 0; i < accountsInvolved.size(); i++) {
            User owner = bank.getUserByAccount(accountsInvolved.get(i).getIban());
            if (owner == user) {
                return accepted[i];
            }
        }
        return false;
    }

    private String subtractFunds() {
        for (int i = 0; i < accountsInvolved.size(); i++) {
            double localAmount = amountsToPay.get(i);
            ArrayList<Graph<Double>.Edge> paths =
                    graph.getPath(currency, accountsInvolved.get(i).getCurrency());
            if (paths != null) {
                double rate = 1.0;
                for (Graph<Double>.Edge path : paths) {
                    rate *= path.getCost();
                }
                localAmount *= rate;
            }
            if (accountsInvolved.get(i)
                    .getBalance() < localAmount) {
                return accountsInvolved.get(i).getIban();
            }
        }
        for (int i = 0; i < accountsInvolved.size(); i++) {
            double localAmount = amountsToPay.get(i);
            ArrayList<Graph<Double>.Edge> paths =
                    graph.getPath(currency, accountsInvolved.get(i).getCurrency());
            if (paths != null) {
                double rate = 1.0;
                for (Graph<Double>.Edge path : paths) {
                    rate *= path.getCost();
                }
                localAmount *= rate;
            }
            accountsInvolved.get(i).addFunds(-localAmount);
        }
        return null;
    }

    /**
     * Accepts the split payment.
     *
     * @param user the user that accepts the payment
     */
    public void accept(final User user) {
        for (int i = 0; i < accountsInvolved.size(); i++) {
            User owner = bank.getUserByAccount(accountsInvolved.get(i).getIban());
            if (owner == user) {
                accepted[i] = true;
                if (checkAllAccepted()) {
                    try {
                        final String iban = subtractFunds();
                        if (iban != null) {
                            notEnoughFunds(iban);
                        } else {
                            addTransactions(null);
                        }

                    } catch (Exception e) {
                        addTransactions("An error occurred while processing the split payment.");
                        return;
                    }
                }
                return;
            }
        }
    }
}
