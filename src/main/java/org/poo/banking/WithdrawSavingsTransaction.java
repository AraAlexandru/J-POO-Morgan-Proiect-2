package org.poo.banking;

import lombok.Getter;

@Getter
public class WithdrawSavingsTransaction extends Transaction{
    private boolean error = false;

    public WithdrawSavingsTransaction(final int timestamp, final String accountIban) {
        super(timestamp, "Withdraw savings", accountIban);
        error = false;
    }

    public WithdrawSavingsTransaction(final int timestamp, final String accountIban, final String errorMsg) {
        super(timestamp, errorMsg, accountIban);
        error = true;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
