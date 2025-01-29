package org.poo.transactions;

import lombok.Getter;
import org.poo.banking.Visitor;

@Getter
public final class CashWithdrawalTransaction extends Transaction {
    private double amount;
    private String errorMessage;

    public CashWithdrawalTransaction(final int timestamp, final String accountIban,
                                     final double amount) {
        super(timestamp, "Cash withdrawal of " + amount, accountIban);
        this.amount = amount;
    }
    public CashWithdrawalTransaction(final int timestamp, final String accountIban,
                                     final double amount, final String errorMessage) {
        super(timestamp, errorMessage, accountIban);
        this.amount = amount;
        this.errorMessage = errorMessage;
    }
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
}
