package org.poo.transactions;

import org.poo.banking.Visitor;

public class FreezeCardTransaction extends Transaction {

    public FreezeCardTransaction(final int timestamp) {
        super(timestamp,
                "You have reached the minimum amount of funds, the card will be frozen",
                null);
    }

    /**
     * Aceasta metoda permite aplicarea design pattern-ului Visitor.
     * Ofera vizitatorului posibilitatea de a efectua operatii specifice,
     * pentru tranzactii de tipul freeze card.
     */
    @Override
    public void accept(final Visitor v) {
        v.visit(this);
    }
}
