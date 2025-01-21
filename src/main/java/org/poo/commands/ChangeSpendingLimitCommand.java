package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.banking.BusinessAccount;
import org.poo.banking.ClassicAccount;
import org.poo.banking.User;

public class ChangeSpendingLimitCommand implements Command {
    private String email;
    private String account;
    private double amount;
    private int timestamp;

    public ChangeSpendingLimitCommand(String email, String account, double amount, int timestamp) {
        this.email = email;
        this.account = account;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    @Override
    public void execute(Bank bank, ArrayNode output, ObjectMapper mapper, int ts) {
        ObjectNode node = mapper.createObjectNode();
        node.put("command", "changeSpendingLimit");

        User user = bank.getUserByEmail(email);
        if (user == null) {
            node.put("output", "User not found");
            output.add(node);
            return;
        }

        ClassicAccount acc = bank.getAccountByIban(account);
        if (acc == null) {
            node.put("output", "Account not found");
            output.add(node);
            return;
        }
        if (!acc.isBusinessAccount()) {
            node.put("output", "Account is not of type business");
            output.add(node);
            return;
        }

        BusinessAccount bAcc = (BusinessAccount) acc;
        if (!bAcc.isOwner(user.getEmail())) {
            ObjectNode outNode = mapper.createObjectNode();
            outNode.put("timestamp", timestamp);
            outNode.put("description", "You must be owner in order to change spending limit.");
            node.set("output", outNode);
            node.put("timestamp", timestamp);
            output.add(node);
            return;
        }

        bAcc.setTransactionLimit("transfer", amount);
    }
}
