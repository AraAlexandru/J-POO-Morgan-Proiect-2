package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.transactions.AddInterestTransaction;
import org.poo.banking.Bank;
import org.poo.banking.ClassicAccount;
import org.poo.banking.SavingsAccount;

public class AddInterestCommand implements Command {
    private double interestRate;
    private String iban;

    public AddInterestCommand(final double interestRate, final String iban) {
        this.interestRate = interestRate;
        this.iban = iban;
    }

    /**
     * Executa comanda pentru adaugarea dobanzilor la un cont de economii.
     * Se genereaza un mesaj de eroare daca IBAN-ul nu este asociat unui cont de economii.
     *
     * @param bank      Instanta bancii care contine informatii despre utilizatori si conturi.
     * @param output    Nodul JSON in care se adauga rezultatele comenzii.
     * @param mapper    Obiectul Jackson pentru manipularea JSON-ului.
     * @param timestamp Timpul la care este executata comanda.
     */
    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
        ClassicAccount account = bank.getAccountByIban(iban);

        if (account != null && !account.isSavingsAccount()) {
            ObjectNode commandOutput = mapper.createObjectNode();
            commandOutput.put("command", "addInterest");
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "This is not a savings account");
            commandOutput.set("output", outputNode);
            commandOutput.put("timestamp", timestamp);
            output.add(commandOutput);
        } else if (account != null) {
            SavingsAccount savingsAccount = (SavingsAccount) account;
            var amount = savingsAccount.addInterest(timestamp);
            bank.getUserByAccount(iban).addTransaction(new AddInterestTransaction(timestamp,
                    iban, amount, savingsAccount.getCurrency()));
        }
    }
}
