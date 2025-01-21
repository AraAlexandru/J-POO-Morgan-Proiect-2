package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.banking.ChangeInterestRateTransaction;
import org.poo.banking.ClassicAccount;
import org.poo.banking.SavingsAccount;
import org.poo.banking.User;

public class ChangeInterestRateCommand implements Command {
    private double interestRate;
    private String iban;

    public ChangeInterestRateCommand(final double interestRate, final String iban) {
        this.interestRate = interestRate;
        this.iban = iban;
    }

    /**
     * Executa comanda pentru schimbarea ratei dobanzilor unui cont de economii.
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

        if (account != null) {
            if (account.isSavingsAccount()) {
                SavingsAccount savingsAccount = (SavingsAccount) account;
                savingsAccount.setInterestRate(interestRate);

                User user = bank.getUserByAccount(iban);
                if (user != null) {
                    user.addTransaction(new ChangeInterestRateTransaction(timestamp,
                            interestRate));
                }
            } else {
                ObjectNode commandOutput = mapper.createObjectNode();
                commandOutput.put("command", "changeInterestRate");
                ObjectNode outputNode =
                        mapper.createObjectNode();
                outputNode.put("timestamp", timestamp);
                outputNode.put("description", "This is not a savings account");
                commandOutput.set("output", outputNode);
                commandOutput.put("timestamp", timestamp);
                output.add(commandOutput);
            }
        }
    }
}
