package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.banking.User;

public class AcceptSplitPaymentCommand implements Command {
    private String email;
    private String type;
    public AcceptSplitPaymentCommand(final String email, final String type) {
        this.email = email;
        this.type = type;
    }
    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
        User user = bank.getUserByEmail(email);
        if (user != null) {
            user.acceptSplitPayment(type);
        }  else {
            ObjectNode node = output.addObject();
            node.put("command", "acceptSplitPayment");
            node.put("timestamp", timestamp);
            ObjectNode inner = node.putObject("output");
            inner.put("description", "User not found");
            inner.put("timestamp", timestamp);
        }
    }
}
