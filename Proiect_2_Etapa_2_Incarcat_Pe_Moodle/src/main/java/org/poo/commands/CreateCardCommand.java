package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.banking.Bank;
import org.poo.transactions.CardCreatedTransaction;
import org.poo.banking.ClassicAccount;
import org.poo.banking.ClassicCard;
import org.poo.banking.User;
import org.poo.utils.Utils;

public class CreateCardCommand implements Command {
    private String email;
    private String iban;
    private String getEmail;

    public CreateCardCommand(final String email, final String iban) {
        this.email = email;
        this.iban = iban;
    }

    /**
     * Executa comanda pentru crearea unui card clasic si
     * adaugarea acestuia in contul utilizatorului.
     *
     * @param bank      Instanta bancii care contine informatii despre utilizatori si conturi.
     * @param output    Nodul JSON in care se adauga rezultatele comenzii.
     * @param mapper    Obiectul Jackson pentru manipularea JSON-ului.
     * @param timestamp Timpul la care este executata comanda.
     */
    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
        User user = bank.getUserByEmail(email);
        if (user != null) {
            ClassicAccount account = user.getAccountByIban(iban);
            if (account != null && user.getAccounts().contains(account)) {
                String cardNumber = Utils.generateCardNumber();
                ClassicCard classicCard = new ClassicCard(cardNumber);
                account.addCard(classicCard);
                user.addTransaction(new CardCreatedTransaction(timestamp,
                        email, cardNumber, iban));
            }
        }
    }
}
