package org.poo.banking;

public class DeleteAccountTransaction extends Transaction {
    private String errorMessage;

    public DeleteAccountTransaction(final int timestamp, final String errorMessage) {
        super(timestamp, errorMessage, null);
    }

    /**
     * Aceasta metoda permite aplicarea design pattern-ului Visitor.
     * Ofera vizitatorului posibilitatea de a efectua operatii specifice,
     * pentru tranzactia de stergere a unui cont.
     */
    @Override
    public void accept(final Visitor v) {
        v.visit(this);
    }
}
