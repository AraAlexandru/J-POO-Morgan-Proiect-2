package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.banking.ClassicAccount;
import org.poo.banking.DeleteAccountTransaction;
import org.poo.banking.User;

public class DeleteAccountCommand implements Command {
    private String email;
    private String iban;

    public DeleteAccountCommand(final String email, final String iban) {
        this.email = email;
        this.iban = iban;
    }

    /**
     * Executa comanda pentru stergerea unui cont asociat utilizatorului.
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
            ClassicAccount account = bank.getAccountByIban(iban);
            boolean resultDeleted =  user.deleteAccountByIban(iban);
            ObjectNode node = mapper.createObjectNode();
            node.put("command", "deleteAccount");
            ObjectNode outputNode = mapper.createObjectNode();
            String error =
                    "Account couldn't be deleted - see org.poo.transactions for details";

            if (account != null) {
                if (account.getBalance() != 0) {
                    String errorMessage =
                            "Account couldn't be deleted - there are funds remaining";

                    user.addTransaction(new DeleteAccountTransaction(timestamp,
                            errorMessage));

                    outputNode.put("error", error);
                } else {
                    if (resultDeleted) {
                        outputNode.put("success", "Account deleted");
                    } else {
                        outputNode.put("error", error);
                    }
                }
            } else {
                outputNode.put("error", "Account not found");
            }

            outputNode.put("timestamp", timestamp);
            node.set("output", outputNode);
            node.put("timestamp", timestamp);
            output.add(node);
        }
    }
}
