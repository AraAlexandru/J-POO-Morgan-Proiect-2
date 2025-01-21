package org.poo.banking;

public final class CardPaymentTransaction extends Transaction {
    private String commercient;
    private double amount;
    private Currency currency;
    private ClassicAccount account;

    public CardPaymentTransaction(final int timestamp, final ClassicAccount account,
                                  final String commerciant, final double amount,
                                  final Currency currency) {
        super(timestamp, "Card payment", account.getIban());
        this.account = account;
        this.commercient = commerciant;
        this.amount = amount;
        this.currency = currency;
    }

    /**
     * Returneaza comerciantul.
     * @return Returneaza comerciantul.
     */
    public String getCommerciant() {
        return commercient;
    }

    /**
     * Returneaza suma platita in tranzactie.
     *
     * @return Suma platita.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returneaza moneda utilizata pentru tranzactie.
     * @return Moneda tranzactiei.
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Returneaza contul asociat tranzactiei de plata.
     * @return Contul asociat.
     */
    public ClassicAccount getAccount() {
        return account;
    }

    /**
     * Aceasta metoda permite aplicarea design pattern-ului Visitor.
     * Ofera vizitatorului posibilitatea de a efectua operatii specifice,
     * pentru tranzactia de plata.
     */
    @Override
    public void accept(final Visitor v) {
        v.visit(this);
    }

    /**
     * Verifica daca tranzactia este o plata cu cardul.
     * @return True, deoarece este o tranzactie de plata cu cardul.
     */
    @Override
    public boolean isCardPayment() {
        return true;
    }
}
