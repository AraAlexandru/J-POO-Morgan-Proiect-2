package org.poo.banking;

public class InsufficientFundsTransaction extends Transaction {

    public InsufficientFundsTransaction(final int timestamp) {
        super(timestamp, "Insufficient funds", null);
    }

    /**
     * Aceasta metoda permite aplicarea design pattern-ului Visitor.
     * Ofera vizitatorului posibilitatea de a efectua operatii specifice,
     * pentru tranzactiile de tipul InsufficientFunds.
     */
    @Override
    public void accept(final Visitor v) {
        v.visit(this);
    }

}
