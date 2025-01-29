package org.poo.transactions;


import lombok.Getter;
import org.poo.banking.Currency;
import org.poo.banking.Visitor;

@Getter
public final class AddInterestTransaction extends Transaction {
    private final double amount;
    private final Currency currency;

    public AddInterestTransaction(final int timestamp, final String accountIban,
                                   final double amount, final Currency currency) {
        super(timestamp, "Interest rate income", accountIban);
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
}
