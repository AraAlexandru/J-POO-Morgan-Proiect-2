package org.poo.transactions;

import org.poo.banking.PlanType;
import org.poo.banking.Visitor;

public final class UpgradePlanTransaction extends Transaction {
    private PlanType newPlanType;
    private int timestamp;

    public UpgradePlanTransaction(final int timestamp, final String accountIban,
                                  final PlanType newPlanType) {
        super(timestamp, "Upgrade plan", accountIban);
        this.newPlanType = newPlanType;
    }

    @Override
    public void accept(final Visitor v) {
        v.visit(this);
    }

    public PlanType getNewPlanType() {
        return newPlanType;
    }
}
