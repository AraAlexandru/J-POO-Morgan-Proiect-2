package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.banking.ClassicAccount;
import org.poo.banking.ClassicCard;
import org.poo.banking.User;

import java.util.List;

public class PrintUsersCommand implements Command {

    /**
     * Executa comanda pentru afisarea utilizatorilor si a conturilor acestora.
     *
     * @param bank      Instanta bancii care contine toti utilizatorii si conturile.
     * @param output    Nodul JSON in care se adauga rezultatele comenzii.
     * @param mapper    Obiectul Jackson pentru manipularea JSON-ului.
     * @param timestamp Timpul la care este executata comanda.
     */
    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
//        System.out.println("Print users at timestamp " + timestamp);
        List<User> users =  bank.getUsers();
        ArrayNode usersArray = mapper.createArrayNode();

        for (User user : users) {
            ObjectNode node = mapper.createObjectNode();
            node.put("firstName", user.getFirstName());
            node.put("lastName", user.getLastName());
            node.put("email", user.getEmail());
            ArrayNode accounts = mapper.createArrayNode();
            for (ClassicAccount account : user.getAccounts()) {
                ObjectNode accountNode = mapper.createObjectNode();
                accountNode.put("IBAN", account.getIban());
                accountNode.put("balance", account.getBalance());
                accountNode.put("currency", account.getCurrency().toString());
                accountNode.put("type", account.getAccountType());
                ArrayNode cards = mapper.createArrayNode();

                //TODO de adaugat cardurile in output - DONE
                for (ClassicCard card : account.getCards()) {
                    ObjectNode cardNode = mapper.createObjectNode();
                    cardNode.put("cardNumber", card.getCardNumber());
                    if (card.isActive()) {
                        cardNode.put("status", "active");
                    } else {
                        cardNode.put("status", "frozen");
                    }
                    cards.add(cardNode);
                }
                accountNode.put("cards", cards);
                accounts.add(accountNode);
            }

            node.put("accounts", accounts);
            usersArray.add(node);
        }
        ObjectNode commandOutput = mapper.createObjectNode();
        commandOutput.put("command", "printUsers");
        commandOutput.put("output", usersArray);
        commandOutput.put("timestamp", timestamp);
        output.add(commandOutput);
    }
}
