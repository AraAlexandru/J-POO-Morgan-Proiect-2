package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.banking.CashWithdrawalTransaction;
import org.poo.banking.ClassicAccount;
import org.poo.banking.ClassicCard;
import org.poo.banking.Currency;
import org.poo.banking.Graph;
import org.poo.banking.User;

import java.util.ArrayList;

public final class CashWithdrawalCommand implements Command {
    private String cardNumber;
    private double amount;
    private String email;
    private String location;
    private Graph<Currency> currencyGraph;

    public CashWithdrawalCommand(final String cardNumber, final double amount,
                                 final String email, final String location,
                                 final Graph<Currency> currencyGraph) {
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.email = email;
        this.location = location;
        this.currencyGraph = currencyGraph;
    }

    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
//        System.out.println("Executing cash withdrawal command for card " + cardNumber);
        User user = bank.getUserByEmail(email);
        if (user == null) {
            return;
        }
        ClassicCard card = bank.getCardByNumber(cardNumber);
        if (card == null) {
            ObjectNode node = mapper.createObjectNode();
            node.put("command", "cashWithdrawal");
            node.put("timestamp", timestamp);
            ObjectNode obj = node.putObject("output");
            obj.put("description", "Card not found");
            obj.put("timestamp", timestamp);
            output.add(node);
            return;
        }
        ClassicAccount account = bank.getAccountByCardNumber(card.getCardNumber());
        if (account == null) {
            ObjectNode node = mapper.createObjectNode();
            node.put("command", "cashWithdrawal");
            node.put("timestamp", timestamp);
            ObjectNode obj = node.putObject("output");
            obj.put("description", "Account not found");
            obj.put("timestamp", timestamp);
            output.add(node);
            return;
        }
        if (!account.checkCard(user, card)) {
            ObjectNode node = mapper.createObjectNode();
            node.put("command", "cashWithdrawal");
            node.put("timestamp", timestamp);
            ObjectNode obj = node.putObject("output");
            obj.put("description", "User not found");
            obj.put("timestamp", timestamp);
            output.add(node);
            return;
        }
            ArrayList<Graph<Currency>.Edge> path =
                    currencyGraph.getPath(Currency.RON, account.getCurrency());
            double totalRate = 1.0;
            double amountToPay = amount;
            if (path != null) {
                for (int i = 0; i < path.size(); i++) {
                    double rate = path.get(i).getCost();
                    totalRate *= rate;
                }
                amountToPay = amount * totalRate;
            }
            amountToPay += user.getFeeForTransaction(amountToPay,
                    currencyGraph, account.getCurrency());
            if (amountToPay  > account.getBalance()) {
                user.addTransaction(new CashWithdrawalTransaction(timestamp,
                        cardNumber, amount, "Insufficient funds"));
    //            user.addTransaction();
                return;
            } else {
                user.addTransaction(new CashWithdrawalTransaction(timestamp, cardNumber, amount));
                account.addFunds(-amountToPay);
            }
    }
}
