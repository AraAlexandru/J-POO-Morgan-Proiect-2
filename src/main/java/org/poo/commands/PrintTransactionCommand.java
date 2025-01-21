package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.banking.ClassicAccount;
import org.poo.banking.Transaction;
import org.poo.banking.TransactionPrinter;
import org.poo.banking.User;

import java.util.List;

public class PrintTransactionCommand implements  Command {
    private String email;

    public PrintTransactionCommand(final String email) {
        this.email = email;
    }

    /**
     * Executa comanda pentru afisarea tranzactiilor asociate unui utilizator.
     *
     * @param bank      Instanta bancii care contine toti utilizatorii si conturile acestora.
     * @param output    Nodul JSON in care se adauga rezultatele comenzii.
     * @param mapper    Obiectul Jackson pentru manipularea JSON-ului.
     * @param timestamp Timpul la care este executata comanda.
     */
    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
        User user = bank.getUserByEmail(email);

        if (user != null) {
            List<ClassicAccount> userAccounts = user.getAccounts();
            List<Transaction> transactions = user.getTransactions(null);
            ArrayNode transactionsArray = mapper.createArrayNode();
            TransactionPrinter transactionPrinter =
                    new TransactionPrinter(transactionsArray, mapper);

            for (Transaction transaction : transactions) {
                transaction.accept(transactionPrinter);
            }

            ObjectNode commandOutput = mapper.createObjectNode();
            commandOutput.put("command", "printTransactions");
            commandOutput.put("output", transactionsArray);
            commandOutput.put("timestamp", timestamp);
            output.add(commandOutput);
        }
    }
}
