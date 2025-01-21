package org.poo.banking;

public class AccountCreatedTransaction extends Transaction {

    public AccountCreatedTransaction(final int timestamp) {
        super(timestamp, "New account created", null);
    }

    /**
     * Aceasta metoda permite aplicarea design pattern-ului Visitor.
     * Ofera vizitatorului posibilitatea de a efectua operatii specifice,
     * pentru tranzactia de creare a unui cont.
     */
    @Override
    public void accept(final Visitor v) {
        v.visit(this);
    }

}
