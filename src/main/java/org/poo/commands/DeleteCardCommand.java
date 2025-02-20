package org.poo.commands;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.banking.Bank;
import org.poo.transactions.CardDestroyedTransaction;
import org.poo.banking.ClassicAccount;
import org.poo.banking.User;

public class DeleteCardCommand implements Command {
    private String email;
    private String cardNumber;

    public DeleteCardCommand(final String email, final String cardNumber) {
        this.email = email;
        this.cardNumber = cardNumber;
    }

    /**
     * Executa comanda pentru stergerea cardului asociat utilizatorului.
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
            for (ClassicAccount account : user.getAccounts()) {
                boolean resultDeleted = account.deleteCardByNumber(cardNumber);
                if (resultDeleted) {
                    String iban = account.getIban();
                    user.addTransaction(new CardDestroyedTransaction(timestamp,
                            email, cardNumber, iban));
                    break;
                }
            }
        }
    }
}
