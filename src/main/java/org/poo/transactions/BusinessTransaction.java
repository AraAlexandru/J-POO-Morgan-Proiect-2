package org.poo.banking;

public class BusinessTransaction {
    private String userEmail;       // cine a facut tranzactia (manager/employee/owner)
    private double amount;          // cat s-a “cheltuit” sau “depus”
    private TransactionType type;   // SPENT sau DEPOSIT
    private int timestamp;
    private Currency currency;      // (opțional) dacă vrei sa stii în ce monedă s-a facut

    public BusinessTransaction(final String userEmail,
                               final double amount,
                               final TransactionType type,
                               final int timestamp,
                               final Currency currency) {
        this.userEmail = userEmail;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
        this.currency = currency;
    }

    /**
     * Returneaza email-ul utilizatorului asociat tranzactiei.
     *
     * @return String care reprezinta email-ul utilizatorului.
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * Returneaza suma asociata tranzactiei.
     *
     * @return Valoarea sumei tranzactiei.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returneaza tipul tranzactiei.
     *
     * @return Tipul tranzactiei, reprezentat ca TransactionType.
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Returneaza timestamp-ul tranzactiei.
     *
     * @return Timpul (timestamp-ul) in care tranzactia a avut loc.
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * Returneaza moneda tranzactiei.
     *
     * @return Moneda tranzactiei, reprezentata ca Currency.
     */
    public Currency getCurrency() {
        return currency;
    }
}
