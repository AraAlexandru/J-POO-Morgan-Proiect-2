package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.banking.ClassicAccount;
import org.poo.banking.ClassicCard;
import org.poo.transactions.FreezeCardTransaction;
import org.poo.banking.User;

public class CheckCardStatusCommand implements Command {
    private String cardNumber;

    public CheckCardStatusCommand(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Executa comanda pentru verificarea statusului unui card.
     * Daca soldul contului este sub limita minima,
     * cardul va fi blocat si se va adauga o tranzactie corespunzatoare.
     *
     * @param bank      Instanta bancii care contine informatii despre utilizatori si conturi.
     * @param output    Nodul JSON in care se adauga rezultatele comenzii.
     * @param mapper    Obiectul Jackson pentru manipularea JSON-ului.
     * @param timestamp Timpul la care este executata comanda.
     */
    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
        User user = bank.getUserByCardNumber(cardNumber);

        if (user == null) {
            ObjectNode commandOutput = mapper.createObjectNode();
            ObjectNode node = mapper.createObjectNode();
            commandOutput.put("command", "checkCardStatus");
            node.put("timestamp", timestamp);
            node.put("description", "Card not found");
            commandOutput.put("output", node);
            commandOutput.put("timestamp", timestamp);
            output.add(commandOutput);
        } else {
            ClassicAccount account = user.getAccountByCard(cardNumber);
            if (account.getBalance() <= account.getMinimumBalance()) {
                ClassicCard card = user.getCardByNumber(cardNumber);
                card.freeze();
                user.addTransaction(new FreezeCardTransaction(timestamp));
            }
        }
    }
}
