package org.poo.banking;


import lombok.Getter;

@Getter
public class AddInterestTransaction extends Transaction {
    private final double amount;
    private final Currency currency;

    public AddInterestTransaction( final int timestamp, final String accountIban,
                                   final double amount, final Currency currency) {
        super(timestamp,"Interest rate income",  accountIban);
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
