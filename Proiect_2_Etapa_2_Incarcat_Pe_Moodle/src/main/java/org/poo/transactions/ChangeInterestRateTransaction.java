package org.poo.transactions;


import org.poo.banking.Visitor;

public class ChangeInterestRateTransaction extends Transaction {
    private double interestRate;

    public ChangeInterestRateTransaction(final int timestamp, final double interestRate) {
        super(timestamp, "Interest rate of the account changed to "
                + String.format("%.2f", interestRate), null);
        this.interestRate = interestRate;
    }

    /**
     * Returneaza interestRate.
     * @return
     */
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Aceasta metoda permite aplicarea design pattern-ului Visitor.
     * Ofera vizitatorului posibilitatea de a efectua operatii specifice,
     * pentru tranzactia de schimbare a campului interestRate.
     */
    @Override
    public void accept(final Visitor v) {
        v.visit(this);
    }
}
