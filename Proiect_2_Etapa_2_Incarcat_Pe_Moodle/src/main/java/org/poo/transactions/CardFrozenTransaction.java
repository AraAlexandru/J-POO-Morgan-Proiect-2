package org.poo.transactions;

import org.poo.banking.Visitor;

public class CardFrozenTransaction extends Transaction {
    public CardFrozenTransaction(final int timestamp) {
        super(timestamp, "The card is frozen", null);
    }

    /**
     * Aceasta metoda permite aplicarea design pattern-ului Visitor.
     * Ofera vizitatorului posibilitatea de a efectua operatii specifice,
     * pentru tranzactii de tip CardFrozen.
     */
    @Override
    public void accept(final Visitor v) {
        v.visit(this);
    }
}
