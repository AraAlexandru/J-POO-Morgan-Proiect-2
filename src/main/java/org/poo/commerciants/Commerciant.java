package org.poo.commerciants;

public final class Commerciant {
    private String name;
    private int id;
    private String account;
    private String type;
    private String cashBackStrategy;

    private CashbackStrategy strategy;

    public Commerciant(final String name,
                       final int id,
                       final String account,
                       final String type,
                       final String cashBackStrategy) {
        this.name = name;
        this.id = id;
        this.account = account;
        this.type = type;
        this.cashBackStrategy = cashBackStrategy;

        if ("nrOfTransactions".equalsIgnoreCase(cashBackStrategy)) {
            this.strategy = new NrOfTransactionsStrategy();
        } else if ("spendingThreshold".equalsIgnoreCase(cashBackStrategy)) {
            this.strategy = new SpendingThresholdStrategy();
        } else {
            this.strategy = null; // fallback
        }
    }

    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }
    public String getAccount() {
        return account;
    }
    public String getType() {
        return type;
    }
    public String getCashBackStrategy() {
        return cashBackStrategy;
    }

    public CashbackStrategy getStrategy() {
        return this.strategy;
    }
}
