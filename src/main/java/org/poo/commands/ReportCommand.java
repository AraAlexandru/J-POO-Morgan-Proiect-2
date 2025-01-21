package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.banking.ClassicAccount;
import org.poo.banking.Transaction;
import org.poo.banking.TransactionPrinter;
import org.poo.banking.User;

import java.util.ArrayList;
import java.util.List;

public class ReportCommand implements Command {
    private String iban;
    private int startTimestamp;
    private int endTimestamp;

    public ReportCommand(final String iban, final int startTimestamp,
                         final int endTimestamp) {
        this.iban = iban;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    /**
     * Executa comanda pentru generarea raportului de tranzactii.
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
            List<Transaction> transactions = new ArrayList<>();
            for (User user : bank.getUsers()) {
                if (user.getAccounts().contains(account)) {
                    transactions = user.getTransactions(iban);
                    break;
                }
            }

            List<Transaction> filteredTransactions = new ArrayList<>();
            for (Transaction transaction : transactions) {
                if (transaction.getTimestamp() >= startTimestamp
                        && transaction.getTimestamp() <= endTimestamp) {
                    filteredTransactions.add(transaction);
                }
            }

            ObjectNode reportNode = mapper.createObjectNode();
            reportNode.put("IBAN", account.getIban());
            reportNode.put("balance", account.getBalance());
            reportNode.put("currency", account.getCurrency().toString());

            ArrayNode transactionsArray = mapper.createArrayNode();
            TransactionPrinter transactionPrinter =
                    new TransactionPrinter(transactionsArray, mapper);

            for (Transaction transaction : filteredTransactions) {
                transaction.accept(transactionPrinter);
            }

            reportNode.put("transactions", transactionsArray);

            ObjectNode commandOutput = mapper.createObjectNode();
            commandOutput.put("command", "report");
            commandOutput.put("output", reportNode);
            commandOutput.put("timestamp", timestamp);
            output.add(commandOutput);
        } else {
            ObjectNode commandOutput = mapper.createObjectNode();
            commandOutput.put("command", "report");
            ObjectNode reportNode = mapper.createObjectNode();
            reportNode.put("timestamp", timestamp);
            reportNode.put("description", "Account not found");
            commandOutput.put("output", reportNode);
            commandOutput.put("timestamp", timestamp);
            output.add(commandOutput);
        }
    }
}
