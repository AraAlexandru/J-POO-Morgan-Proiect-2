package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.banking.BusinessAccount;
import org.poo.banking.BusinessTransaction;
import org.poo.banking.CardCreatedTransaction;
import org.poo.banking.CardDestroyedTransaction;
import org.poo.banking.CardPaymentTransaction;
import org.poo.banking.ClassicAccount;
import org.poo.banking.ClassicCard;
import org.poo.banking.Currency;
import org.poo.banking.Graph;
import org.poo.banking.InsufficientFundsTransaction;
import org.poo.banking.OneTimeCard;
import org.poo.banking.TransactionType;
import org.poo.banking.User;
import org.poo.commerciants.Commerciant;
import org.poo.utils.Utils;
import java.util.ArrayList;

public final class PayOnlineCommand implements Command {
    private String cardNumber;
    private double amountToDeduct;
    private String currencyStr;
    private String description;
    private String commerciant;
    private String email;
    private Graph<Currency> currencyGraph;

    public PayOnlineCommand(final String cardNumber, final double amountToDeduct,
                            final String currencyStr, final String description,
                            final String commerciant, final String email,
                            final Graph<Currency> currencyGraph) {
        this.cardNumber = cardNumber;
        this.amountToDeduct = amountToDeduct;
        this.currencyStr = currencyStr;
        this.description = description;
        this.commerciant = commerciant;
        this.email = email;
        this.currencyGraph = currencyGraph;
    }

    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
        User user = bank.getUserByEmail(email);
        if (user == null) {
            ObjectNode commandOutput = mapper.createObjectNode();
            commandOutput.put("command", "payOnline");
            ObjectNode outNode = mapper.createObjectNode();
            outNode.put("description", "User not found");
            outNode.put("timestamp", timestamp);
            commandOutput.set("output", outNode);
            commandOutput.put("timestamp", timestamp);
            output.add(commandOutput);
            return;
        }
        ClassicCard card = bank.getCardByNumber(cardNumber);
        if (card == null) {
            ObjectNode commandOutput = mapper.createObjectNode();
            commandOutput.put("command", "payOnline");
            ObjectNode outNode = mapper.createObjectNode();
            outNode.put("description", "Card not found");
            outNode.put("timestamp", timestamp);
            commandOutput.set("output", outNode);
            commandOutput.put("timestamp", timestamp);
            output.add(commandOutput);
            return;
        }
        if (!card.isActive()) {
            return;
        }
        if (amountToDeduct == 0) {
            return;
        }
        ClassicAccount account = user.getAccountByCard(cardNumber);
        if (account != null) {
            Currency targetCurrency = Currency.valueOf(currencyStr);
            Currency accountCurrency = account.getCurrency();
            double convertedAmount = amountToDeduct;

            if (!accountCurrency.equals(targetCurrency)) {
                ArrayList<Graph<Currency>.Edge> path =
                        currencyGraph.getPath(targetCurrency, accountCurrency);
                if (path != null) {
                    double totalRate = 1.0;
                    for (Graph<Currency>.Edge edge : path) {
                        totalRate *= edge.getCost();
                    }
                    convertedAmount = amountToDeduct * totalRate;
                }
            }
            double fee = user.getFeeForTransaction(convertedAmount, currencyGraph, accountCurrency);
            double totalAmount = convertedAmount + fee;
            if (account.getBalance() >= totalAmount) {
                account.addFunds(-totalAmount);
                if (account.isBusinessAccount()) {
                    BusinessAccount bAcc = (BusinessAccount) account;
                    bAcc.logTransaction(new BusinessTransaction(user.getEmail(), convertedAmount,
                            TransactionType.SPENT, timestamp, accountCurrency));
                }
                user.addTransaction(new CardPaymentTransaction(timestamp, account,
                        commerciant, convertedAmount, accountCurrency));
                Commerciant c = bank.getCommerciantByName(commerciant);
                if (c != null && c.getStrategy() != null) {
                    c.getStrategy().applyCashback(account, convertedAmount,
                            accountCurrency, currencyGraph, user, c);
                }
                SendMoneyCommand.upgradePlanForSilver(user, accountCurrency,
                        convertedAmount, currencyGraph);
                if (card.isOneTimeCard()) {
                    user.addTransaction(new CardDestroyedTransaction(timestamp, email,
                            cardNumber, account.getIban()));
                    OneTimeCard oneTimeCard = (OneTimeCard) card;
                    String newCardNumber = Utils.generateCardNumber();
                    oneTimeCard.setCardNumber(newCardNumber);
                    user.addTransaction(new CardCreatedTransaction(timestamp, email,
                            newCardNumber, account.getIban()));
                }
            } else {
                user.addTransaction(new InsufficientFundsTransaction(timestamp));
            }
            return;
        }
        ClassicAccount fallbackAcc = bank.getAccountByCardNumber(cardNumber);
        if (fallbackAcc == null) {
            ObjectNode node = mapper.createObjectNode();
            node.put("command", "payOnline");
            node.put("timestamp", timestamp);
            node.put("output", "Account not found");
            output.add(node);
            return;
        }
        Currency targetCurrency = Currency.valueOf(currencyStr);
        Currency accountCurrency = fallbackAcc.getCurrency();
        double convertedAmount = amountToDeduct;

        if (!accountCurrency.equals(targetCurrency)) {
            ArrayList<Graph<Currency>.Edge> path =
                    currencyGraph.getPath(targetCurrency, accountCurrency);

            if (path != null) {
                double totalRate = 1.0;
                for (Graph<Currency>.Edge edge : path) {
                    totalRate *= edge.getCost();
                }
                convertedAmount = amountToDeduct * totalRate;
            }
        }
        double fee = user.getFeeForTransaction(convertedAmount, currencyGraph, accountCurrency);
        double totalAmount = convertedAmount + fee;

        if (fallbackAcc.getBalance() < totalAmount) {
            user.addTransaction(new InsufficientFundsTransaction(timestamp));
            return;
        }
        if (!fallbackAcc.isBusinessAccount()) {
            return;
        }
        BusinessAccount bAcc = (BusinessAccount) fallbackAcc;
        if (!bAcc.isAssociate(user.getEmail())) {
            ObjectNode node = mapper.createObjectNode();
            node.put("command", "payOnline");
            node.put("timestamp", timestamp);
            node.put("output", "You are not authorized to make this transaction.");
            output.add(node);
            return;
        }
        bAcc.addFunds(-totalAmount);
        bAcc.logTransaction(new BusinessTransaction(user.getEmail(), convertedAmount,
                TransactionType.SPENT, timestamp, accountCurrency));
        user.addTransaction(new CardPaymentTransaction(timestamp, fallbackAcc,
                commerciant, convertedAmount, accountCurrency));
        Commerciant c = bank.getCommerciantByName(commerciant);
        if (c != null && c.getStrategy() != null) {
            c.getStrategy().applyCashback(fallbackAcc, convertedAmount,
                    accountCurrency, currencyGraph, user, c);
        }
        SendMoneyCommand.upgradePlanForSilver(user,
                accountCurrency, convertedAmount, currencyGraph);
        if (card.isOneTimeCard()) {
            user.addTransaction(new CardDestroyedTransaction(timestamp, email,
                    cardNumber, fallbackAcc.getIban()));
            OneTimeCard oneTimeCard = (OneTimeCard) card;
            String newCardNumber = Utils.generateCardNumber();
            oneTimeCard.setCardNumber(newCardNumber);
            user.addTransaction(new CardCreatedTransaction(timestamp, email,
                    newCardNumber, fallbackAcc.getIban()));
        }
    }
}
