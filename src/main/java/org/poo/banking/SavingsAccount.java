package org.poo.banking;

public class SavingsAccount extends ClassicAccount {
    private double interestRate;

    public SavingsAccount(final String iban, final Currency currency, final double interestRate) {
        super(iban, currency);
        this.interestRate = interestRate;
    }

    /**
     * Returneaza tipul contului, "savings" pentru conturile de economii.
     * @return "savings"
     */
    @Override
    public String getAccountType() {
        return "savings";
    }

    /**
     * Seteaza dobanda contului de economii.
     * @param interestRate Noua dobanda pentru contul de economii.
     */
    public void setInterestRate(final double interestRate) {
        this.interestRate = interestRate;
    }

    /**
     * Returneaza dobanda contului de economii.
     * @return Dobanda contului.
     */
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Ne spune daca acesta este un cont de economii.
     * @return true pentru ca acesta este cont de economii.
     */
    @Override
    public boolean isSavingsAccount() {
        return true;
    }

    public double addInterest(int timestamp) {
        var value = getBalance() * getInterestRate();
        addFunds(value);
        return value;
    }

    @Override
    public boolean isClassicAccount() {
        return false;
    }
}
