package org.poo.banking;

public class BusinessTransaction {
    private String userEmail;       // cine a făcut tranzacția (manager/employee/owner)
    private double amount;          // cât s-a “cheltuit” sau “depus”
    private TransactionType type;   // SPENT sau DEPOSIT
    private int timestamp;
    private Currency currency;      // (opțional) dacă vrei să știi în ce monedă s-a făcut

    public BusinessTransaction(String userEmail,
                               double amount,
                               TransactionType type,
                               int timestamp,
                               Currency currency) {
        this.userEmail = userEmail;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
        this.currency = currency;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public Currency getCurrency() {
        return currency;
    }
}
