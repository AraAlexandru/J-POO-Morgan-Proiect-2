package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.banking.BusinessAccount;
import org.poo.banking.ClassicAccount;
import org.poo.banking.User;

public class AddNewBusinessAssociateCommand implements Command {
    private String accountIban;
    private String role;
    private String email;

    public AddNewBusinessAssociateCommand(final String accountIban,
                                          final String role,
                                          final String email) {
        this.accountIban = accountIban;
        this.role = role;
        this.email = email;
    }

    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
        ObjectNode node = mapper.createObjectNode();
        node.put("command", "addNewBusinessAssociate");
        node.put("timestamp", timestamp);

        ClassicAccount account = bank.getAccountByIban(accountIban);
        if (account == null) {
            node.put("output", "Account not found");
            output.add(node);
            return;
        }
        if (!account.isBusinessAccount()) {
            node.put("output", "Account is not of type business");
            output.add(node);
            return;
        }
        BusinessAccount bAcc = (BusinessAccount) account;

        String ownerEmail = bAcc.getOwnerEmail();
        User ownerUser = bank.getUserByEmail(ownerEmail);
        if (ownerUser == null) {
            ObjectNode outNode = mapper.createObjectNode();
            outNode.put("description", "You must be owner in order to change spending limit.");
            outNode.put("timestamp", timestamp);
            node.set("output", outNode);
            node.put("timestamp", timestamp);
            output.add(node);
            return;
        }

        if (!bAcc.isOwner(ownerEmail)) {
            ObjectNode outNode = mapper.createObjectNode();
            outNode.put("description", "You must be owner in order to change spending limit.");
            outNode.put("timestamp", timestamp);
            node.set("output", outNode);
            node.put("timestamp", timestamp);
            output.add(node);
            return;
        }

        User userToAdd = bank.getUserByEmail(email);
        if (userToAdd == null) {
            node.put("output", "User not found");
            output.add(node);
            return;
        }

        if (bAcc.alreadyEmployeeOrManager(email)) {
            node.put("output", "The user is already an associate of the account.");
            output.add(node);
            return;
        }

        if ("manager".equals(role)) {
            bAcc.addManager(email);
        } else {
            bAcc.addEmployee(email);
        }
    }
}
