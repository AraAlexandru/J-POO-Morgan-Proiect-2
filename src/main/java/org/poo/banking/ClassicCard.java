package org.poo.banking;

public class ClassicCard {
    private String cardNumber;
    private Boolean status;

    public ClassicCard(final String cardNumber) {
        this.cardNumber = cardNumber;
        this.status = true;
    }

    /**
     * Seteaza statusul cardului.
     * @param status Noua valoare a statusului.
     */
    public void setStatus(final boolean status) {
        this.status = status;
    }

    public void freeze() {
        this.setStatus(false);
    }

    public void activate() {
        this.setStatus(true);
    }

    /**
     * Returneaza numarul cardului.
     * @return Numarul cardului ca String.
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Seteaza numarul cardului.
     * @param cardNumber Noua valoare a numarului cardului.
     */
    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Returneaza statusul cardului.
     * @return True daca cardul este activ, false daca este blocat.
     */
    public Boolean isActive() {
        return status;
    }

    /**
     * Indica daca acest card este un card de tip OneTimeCard.
     * @return False, deoarece acesta este un card clasic si nu un card OneTimeCard.
     */
    public boolean isOneTimeCard() {
        return false;
    }
}
