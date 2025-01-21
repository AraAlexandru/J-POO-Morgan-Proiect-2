package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.banking.Bank;
import org.poo.banking.ClassicAccount;
import org.poo.banking.Currency;
import org.poo.banking.Graph;
import org.poo.banking.SplitPaymentEvent;
import org.poo.banking.User;

import java.util.ArrayList;
import java.util.List;

public class SplitPaymentCommand implements Command {
    private List<String> accountsForSplit;
    private List<Double> splitAmounts;
    private double totalAmount;
    private String currency;
    private String type;
    private Graph<Currency> currencyGraph;

    public SplitPaymentCommand(final List<String> accountsForSplit,
                                   final double totalAmount,
                                   final String type,
                                   final List<Double> splitAmounts,
                                   final String currency,
                                   final Graph<Currency> currencyGraph) {
        this.accountsForSplit = accountsForSplit;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.currencyGraph = currencyGraph;
        if (type.equals("custom")) {
            this.splitAmounts = splitAmounts;
        } else {
            this.splitAmounts = new ArrayList<>();
            for (int i = 0; i < accountsForSplit.size(); i++) {
                this.splitAmounts.add(totalAmount / accountsForSplit.size());
            }
        }
        this.type = type;
    }

    /**
     * Executa comanda de split payment intre conturile specificate.
     *
     * @param bank      Instanta bancii care contine toate conturile si utilizatorii.
     * @param output    Nodul JSON in care se adauga rezultatele comenzii.
     * @param mapper    Obiectul Jackson pentru manipularea JSON-ului.
     * @param timestamp Timpul la care este executata comanda.
     */
    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
        List<ClassicAccount> classicAccounts = accountsForSplit.stream().map(bank::getAccountByIban).toList();
        SplitPaymentEvent splitPaymentEvent = new SplitPaymentEvent(Currency.valueOf(currency), classicAccounts, totalAmount, splitAmounts, type, timestamp, currencyGraph);
        for (ClassicAccount account : classicAccounts) {
            User user = bank.getUserByAccount(account.getIban());
            user.addSplitPayment(splitPaymentEvent);
        }
    }
}
