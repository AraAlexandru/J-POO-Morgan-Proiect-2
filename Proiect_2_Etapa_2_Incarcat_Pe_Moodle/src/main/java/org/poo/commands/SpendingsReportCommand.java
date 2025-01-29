package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.transactions.CardPaymentTransaction;
import org.poo.banking.ClassicAccount;
import org.poo.transactions.Transaction;
import org.poo.banking.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpendingsReportCommand implements Command {
    private String iban;
    private int startTimestamp;
    private int endTimestamp;

    public SpendingsReportCommand(final String iban, final int startTimestamp,
                                  final int endTimestamp) {
        this.iban = iban;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    /**
     * Executa comanda pentru generarea raportului de cheltuieli.
     *
     * @param bank      Instanta bancii care contine toate conturile si utilizatorii.
     * @param output    Nodul JSON in care se adauga rezultatele comenzii.
     * @param mapper    Obiectul Jackson pentru manipularea JSON-ului.
     * @param timestamp Timpul la care este executata comanda.
     */
    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
        ClassicAccount account = bank.getAccountByIban(iban);

        if (account != null) {
            if (account.isSavingsAccount()) {
                ObjectNode commandOutput = mapper.createObjectNode();
                commandOutput.put("command", "spendingsReport");
                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("error",
                        "This kind of report is not supported for a saving account");
                commandOutput.put("output", outputNode);
                commandOutput.put("timestamp", timestamp);
                output.add(commandOutput);
            } else {
                User user = bank.getUserByAccount(iban);
                if (user != null) {
                    List<CardPaymentTransaction> filteredTransactions = new ArrayList<>();
                    Map<String, Double> commerciantTotals = new HashMap<>();

                    for (Transaction transaction : user.getTransactions(iban)) {
                        if (transaction.isCardPayment()
                                && transaction.getTimestamp() >= startTimestamp
                                && transaction.getTimestamp() <= endTimestamp
                                && ((CardPaymentTransaction)
                                transaction).getAccount().getIban().equals(iban)) {
                            CardPaymentTransaction cardPaymentTransaction =
                                    (CardPaymentTransaction) transaction;
                            filteredTransactions.add(cardPaymentTransaction);
                            String commerciant = cardPaymentTransaction.getCommerciant();
                            commerciantTotals.put(commerciant,
                                    commerciantTotals.getOrDefault(commerciant, 0.0)
                                            + cardPaymentTransaction.getAmount());
                        }
                    }

                    ObjectNode outputNode = mapper.createObjectNode();
                    outputNode.put("IBAN", iban);
                    outputNode.put("balance", account.getBalance());
                    outputNode.put("currency", account.getCurrency().toString());

                    ArrayNode transactionsArray = mapper.createArrayNode();
                    for (CardPaymentTransaction transaction : filteredTransactions) {
                        ObjectNode transactionNode = mapper.createObjectNode();
                        transactionNode.put("timestamp", transaction.getTimestamp());
                        transactionNode.put("description", transaction.getDescription());
                        transactionNode.put("amount", transaction.getAmount());
                        transactionNode.put("commerciant", transaction.getCommerciant());
                        transactionsArray.add(transactionNode);
                    }
                    outputNode.set("transactions", transactionsArray);

                    List<Map.Entry<String, Double>> sortedCommerciants =
                            new ArrayList<>(commerciantTotals.entrySet());
                    sortedCommerciants.sort(Map.Entry.comparingByKey());

                    ArrayNode commerciantsArray = mapper.createArrayNode();
                    for (Map.Entry<String, Double> entry : sortedCommerciants) {
                        ObjectNode commerciantNode = mapper.createObjectNode();
                        commerciantNode.put("commerciant", entry.getKey());
                        commerciantNode.put("total", entry.getValue());
                        commerciantsArray.add(commerciantNode);
                    }
                    outputNode.set("commerciants", commerciantsArray);

                    ObjectNode commandOutput = mapper.createObjectNode();
                    commandOutput.put("command", "spendingsReport");
                    commandOutput.put("output", outputNode);
                    commandOutput.put("timestamp", timestamp);
                    output.add(commandOutput);
                }
            }
        } else {
            ObjectNode commandOutput = mapper.createObjectNode();
            commandOutput.put("command", "spendingsReport");
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "Account not found");
            commandOutput.put("output", outputNode);
            commandOutput.put("timestamp", timestamp);
            output.add(commandOutput);
        }
    }
}
