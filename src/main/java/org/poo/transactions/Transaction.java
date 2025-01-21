package org.poo.banking;

public abstract class Transaction implements Visitable {
    private int timestamp;
    private String description;
    private String accountIban;

    /**
     * Returneaza timestamp-ul tranzactiei.
     * @return Timestamp-ul tranzactiei sub forma de int.
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * Returneaza descrierea tranzactiei.
     * @return Descrierea tranzactiei sub forma de String.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returneaza IBAN-ul asociat acestui cont.
     *
     * @return String care reprezinta IBAN-ul contului.
     */
    public String getIban() {
        return accountIban;
    }


    public Transaction(final int timestamp, final String description, final String accountIban) {
        this.timestamp = timestamp;
        this.description = description;
        this.accountIban = accountIban;
    }

    /**
     * Returneaza IBAN-ul asociat acestui cont.
     *
     * @return String care reprezinta IBAN-ul contului.
     */
    public String getAccountIban() {
        return accountIban;
    }

    /**
     * Verifica daca tranzactia este o plata cu cardul.
     * @return False implicit, poate fi suprascris in clasele derivate.
     */
    public boolean isCardPayment() {
        return false;
    }

    /**
     * Metoda abstracta pentru aplicarea design pattern-ului Visitor.
     * Permite vizitatorului sa proceseze tranzactia curenta.
     * @param visitor Vizitatorul care proceseaza tranzactia.
     */
    public abstract void accept(Visitor visitor);
}
