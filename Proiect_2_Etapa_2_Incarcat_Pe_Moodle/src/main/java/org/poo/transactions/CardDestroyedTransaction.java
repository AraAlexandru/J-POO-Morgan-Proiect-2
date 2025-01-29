package org.poo.transactions;

import org.poo.banking.Visitor;

public class CardDestroyedTransaction extends Transaction {
    private String cardNumber;
    private String email;

    public CardDestroyedTransaction(final int timestamp, final String email,
                                    final String cardNumber, final String accountIban) {
        super(timestamp, "The card has been destroyed", accountIban);
        this.cardNumber = cardNumber;
        this.email = email;
    }

    /**
     * Returneaza numarul cardului care a fost distrus.
     * @return Numarul cardului.
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Returneaza adresa de email a utilizatorului asociat tranzactiei.
     * @return Adresa de email a utilizatorului.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returneaza IBAN-ul contului asociat cardului distrus.
     * @return IBAN-ul contului.
     */
    public String getAccountIban() {
        return getIban();
    }

    /**
     * Aceasta metoda permite aplicarea design pattern-ului Visitor.
     * Ofera vizitatorului posibilitatea de a efectua operatii specifice,
     * pentru tranzactia de stergere a unui card.
     */
    @Override
    public void accept(final Visitor v) {
        v.visit(this);
    }

}
