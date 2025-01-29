package org.poo.transactions;

import org.poo.banking.Visitor;

public final class MinimumAgeTransaction extends Transaction {
    private int timestamp;

    public MinimumAgeTransaction(final int timestamp) {
        super(timestamp, "You don't have the minimum age required.", null);

    }

    @Override
    public void accept(final Visitor v) {
        v.visit(this);
    }
}
