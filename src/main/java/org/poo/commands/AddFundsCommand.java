package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.banking.BusinessAccount;
import org.poo.banking.BusinessTransaction;
import org.poo.banking.ClassicAccount;
import org.poo.banking.TransactionType;
import org.poo.banking.User;

public final class AddFundsCommand implements Command {
    private String iban;
    private double amount;
    private String email;

    public AddFundsCommand(final String iban, final double amount,
                           final String email) {
        this.iban = iban;
        this.amount = amount;
        this.email = email;
    }

    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
        User initiator = bank.getUserByEmail(email);
        if (initiator == null) {
            ObjectNode node = mapper.createObjectNode();
            node.put("command", "addFunds");
            node.put("timestamp", timestamp);
            node.put("output", "User not found");
            output.add(node);
            return;
        }

        ClassicAccount account = bank.getAccountByIban(iban);
        if (account == null) {
            ObjectNode node = mapper.createObjectNode();
            node.put("command", "addFunds");
            node.put("timestamp", timestamp);
            node.put("output", "Account not found");
            output.add(node);
            return;
        }

        if (account.isBusinessAccount()) {
            BusinessAccount bAcc = (BusinessAccount) account;
            if (!bAcc.isAssociate(initiator.getEmail())) {
                ObjectNode node = mapper.createObjectNode();
                node.put("command", "addFunds");
                node.put("timestamp", timestamp);
                node.put("output", "You are not authorized to make this transaction.");
                output.add(node);
                return;
            }

            if (!bAcc.canExecuteTransaction(initiator.getEmail(), "incasare", amount)) {
                /* ObjectNode node = mapper.createObjectNode();
                node.put("command", "addFunds");
                node.put("timestamp", timestamp);
                node.put("output", "You are not authorized to make this transaction.");
                output.add(node); */
                return;
            }

            bAcc.addFunds(amount);

            bAcc.logTransaction(new BusinessTransaction(
                    initiator.getEmail(),
                    amount,
                    TransactionType.DEPOSIT,
                    timestamp,
                    bAcc.getCurrency()
            ));
        } else {
            account.addFunds(amount);
        }
    }
}
