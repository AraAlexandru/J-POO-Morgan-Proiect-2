package org.poo.transactions;

import lombok.Getter;
import org.poo.banking.Currency;
import org.poo.banking.Visitor;

import java.util.List;

@Getter
public class SplitPaymentTransaction extends Transaction {
    private Currency currency;
    private double totalAmount;
    private List<String> involvedAccounts;
    private List<Double> amountsForUsers;
    private final String splitPaymentType;
    private String error;

    public SplitPaymentTransaction(final int timestamp, final double totalAmount,
                                   final Currency currency,
                                   final List<String> involvedAccounts,
                                   final String iban,
                                   final List<Double> amountsForUsers,
                                   final String splitPaymentType,
                                   final String error) {
        super(timestamp, "Split payment of "
                + String.format("%.2f", totalAmount) + " " + currency.toString(), iban);
        this.involvedAccounts = involvedAccounts;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.error = error;
        this.splitPaymentType = splitPaymentType;
        this.amountsForUsers = amountsForUsers;
    }

    /**
     * Aceasta metoda permite aplicarea design pattern-ului Visitor.
     * Ofera vizitatorului posibilitatea de a efectua operatia de splitPayment.
     */
    @Override
    public void accept(final Visitor v) {
        v.visit(this);
    }
}
