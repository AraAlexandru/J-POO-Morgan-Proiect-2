package org.poo.transactions;

import org.poo.banking.Currency;
import org.poo.banking.Visitor;

public class TransferTransaction extends Transaction {
    private String senderIBAN;
    private String receiverIBAN;
    private double amount;
    private Currency type;
    private String transferType;

    /**
     * Returneaza IBAN-ul expeditoruli.
     * @return IBAN-ul expeditorului.
     */
    public String getSenderIBAN() {
        return senderIBAN;
    }

    /**
     * Returneaza iban-ul destinatarului.
     * @return IBAN-ul destinatarului.
     */
    public String getReceiverIBAN() {
        return receiverIBAN;
    }

    /**
     * Returneaza suma transferata.
     * @return Suma transferata sub forma de double.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returneaza tipul de moneda utilizat in transferul curent.
     * @return Tipul de moneda sub forma de Currency.
     */
    public Currency getType() {
        return type;
    }

    /**
     * Returneaza tipul transferului ("sent" || "received").
     * @return Tipul de transfer sub forma de String.
     */
    public String getTransferType() {
        return transferType;
    }

    public TransferTransaction(final int timestamp, final String description,
                               final String senderIBAN,
                               final String receiverIBAN, final double amount,
                               final Currency type,
                               final String transferType,
                               final String iban) {
        super(timestamp, description, iban);
        this.senderIBAN = senderIBAN;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.type = type;
        this.transferType = transferType;
    }

    /**
     * Aceasta metoda permite aplicarea design pattern-ului Visitor.
     * Ofera vizitatorului posibilitatea de a efectua operatii specifice,
     * pentru tranzactia de transfer a unui cont.
     */
    @Override
    public void accept(final Visitor v) {
        v.visit(this);
    }

}
