package org.poo.transactions;

import lombok.Getter;
import org.poo.banking.Visitor;

@Getter
public final class WithdrawSavingsTransaction extends Transaction {
    private boolean error = false;

    public WithdrawSavingsTransaction(final int timestamp, final String accountIban) {
        super(timestamp, "Withdraw savings", accountIban);
        error = false;
    }

    public WithdrawSavingsTransaction(final int timestamp, final String accountIban,
                                      final String errorMsg) {
        super(timestamp, errorMsg, accountIban);
        error = true;
    }

    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
}
