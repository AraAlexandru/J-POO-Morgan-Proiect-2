package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.banking.BusinessAccount;
import org.poo.banking.ClassicAccount;
import org.poo.banking.User;

public class ChangeDepositLimitCommand implements Command {
    private String email;
    private String account;
    private double amount;
    private int timestamp;

    public ChangeDepositLimitCommand(final String email, final String account,
                                     final double amount, final int timestamp) {
        this.email = email;
        this.account = account;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int ts) {

        User user = bank.getUserByEmail(email);
        if (user == null) {
            ObjectNode node = mapper.createObjectNode();
            node.put("command", "changeDepositLimit");
            node.put("timestamp", ts);
            node.put("output", "User not found");
            output.add(node);
            return;
        }

        ClassicAccount acc = bank.getAccountByIban(account);
        if (acc == null) {
            ObjectNode node = mapper.createObjectNode();
            node.put("command", "changeDepositLimit");
            node.put("timestamp", ts);
            node.put("output", "Account not found");
            output.add(node);
            return;
        }
        if (!acc.isBusinessAccount()) {
            ObjectNode node = mapper.createObjectNode();
            node.put("command", "changeDepositLimit");
            node.put("timestamp", ts);
            node.put("output", "Account is not of type business");
            output.add(node);
            return;
        }

        BusinessAccount bAcc = (BusinessAccount) acc;

        if (!bAcc.isOwner(user.getEmail())) {
            ObjectNode node = mapper.createObjectNode();
            node.put("command", "changeDepositLimit");
            node.put("timestamp", ts);
            ObjectNode outNode = mapper.createObjectNode();
            outNode.put("description", "You must be owner in order to change spending limit.");
            outNode.put("timestamp", timestamp);
            node.set("output", outNode);
            node.put("timestamp", timestamp);
            output.add(node);
            return;
        }

        bAcc.setTransactionLimit("incasare", amount);
    }
}
