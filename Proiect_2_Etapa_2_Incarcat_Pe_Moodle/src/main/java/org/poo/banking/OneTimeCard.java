package org.poo.banking;

public final class OneTimeCard extends ClassicCard {
    public OneTimeCard(final String cardNumber) {
        super(cardNumber);
    }

    @Override
    public boolean isOneTimeCard() {
        return true;
    }
}
