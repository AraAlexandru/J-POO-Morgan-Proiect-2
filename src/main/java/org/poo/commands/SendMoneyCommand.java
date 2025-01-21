package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.banking.Currency;
import org.poo.banking.Graph;
import org.poo.banking.User;
import org.poo.banking.ClassicAccount;
import org.poo.banking.BusinessAccount;
import org.poo.banking.BusinessTransaction;
import org.poo.banking.TransferTransaction;
import org.poo.banking.PlanType;
import org.poo.banking.InsufficientFundsTransaction;
import org.poo.banking.TransactionType;
import org.poo.commerciants.Commerciant;

import java.util.ArrayList;

public class SendMoneyCommand implements Command {
    private String email;
    private String currentIban;
    private String receiverIban;
    private  String description;
    private double amount;
    private Graph<Currency> currencyGraph;
    private static final double INCREMENT_SILVER_PAYMENT = 300;

    public SendMoneyCommand(final String email, final String currentIban,
                            final String receiverIban, final String description,
                            final double amount, final Graph<Currency> currencyGraph) {
        this.email = email;
        this.currentIban = currentIban;
        this.receiverIban = receiverIban;
        this.description = description;
        this.amount = amount;
        this.currencyGraph = currencyGraph;
    }

    /**
     * Executa comanda pentru trimiterea de bani intre doua conturi.
     *
     * @param bank      Instanta bancii care contine toate conturile si utilizatorii.
     * @param output    Nodul JSON in care se adauga rezultatele comenzii.
     * @param mapper    Obiectul Jackson pentru manipularea JSON-ului.
     * @param timestamp Timpul la care este executata comanda.
     */
    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
        User user = bank.getUserByEmail(email);

        if (user != null) {
            ClassicAccount currentAccount = bank.getAccountByIban(currentIban);
            if (currentAccount == null || user != bank.getUserByAccount(currentIban)) {
                var node = mapper.createObjectNode();
                node.put("command", "sendMoney");
                node.put("timestamp", timestamp);
                var obj = node.putObject("output");
                obj.put("description", "User not found");
                obj.put("timestamp", timestamp);
                output.add(node);
                return;
            }
            ClassicAccount receiverAccount = bank.getAccountByIban(receiverIban);

            if (receiverAccount != null) {
                Currency currentAccountCurrency = currentAccount.getCurrency();
                Currency receiverAccountCurrency = receiverAccount.getCurrency();
                double convertedAmount = amount;

                if (!currentAccountCurrency.equals(receiverAccountCurrency)) {
                    ArrayList<Graph<Currency>.Edge> path =
                            currencyGraph.getPath(currentAccountCurrency,
                                    receiverAccountCurrency);

                    if (path != null) {
                        double totalRate = 1.0;
                        for (Graph<Currency>.Edge edge : path) {
                            totalRate *= edge.getCost();
                        }
                        convertedAmount = amount * totalRate;
                    }
                }
                double fee =
                        user.getFeeForTransaction(amount, currencyGraph, currentAccountCurrency);

                double totalDeduction = amount + fee;

                if (currentAccount.getBalance() >= totalDeduction) {
                    currentAccount.addFunds(-totalDeduction);
                    receiverAccount.addFunds(convertedAmount);
                    if (currentAccount.isBusinessAccount()) {
                        BusinessAccount bAcc = (BusinessAccount) currentAccount;
                        bAcc.logTransaction(new BusinessTransaction(
                                user.getEmail(),
                                amount,
                                TransactionType.SPENT,
                                timestamp,
                                currentAccountCurrency
                        ));
                    }

                    user.addTransaction(new TransferTransaction(
                            timestamp,
                            description,
                            currentIban,
                            receiverIban,
                            amount,
                            currentAccountCurrency,
                            "sent",
                            currentIban
                    ));


                    upgradePlanForSilver(user, currentAccountCurrency, amount, currencyGraph);

                    for (User recipient : bank.getUsers()) {
                        if (recipient.getAccounts().contains(receiverAccount)) {
                            recipient.addTransaction(new TransferTransaction(
                                    timestamp,
                                    description,
                                    currentIban,
                                    receiverIban,
                                    convertedAmount,
                                    receiverAccountCurrency,
                                    "received",
                                    receiverIban
                            ));
                            break;
                        }
                    }
                } else {
                    user.addTransaction(new InsufficientFundsTransaction(timestamp));
                }
            } else if (bank.getCommerciantByIban(receiverIban) != null) {
                Commerciant commerciant = bank.getCommerciantByIban(receiverIban);
                Currency currentAccountCurrency = currentAccount.getCurrency();
                double fee =
                        user.getFeeForTransaction(amount, currencyGraph, currentAccountCurrency);
                double totalDeduction = amount + fee;

                if (currentAccount.getBalance() >= totalDeduction) {
                    currentAccount.addFunds(-totalDeduction);

                    if (currentAccount.isBusinessAccount()) {
                        BusinessAccount bAcc = (BusinessAccount) currentAccount;
                        bAcc.logTransaction(new BusinessTransaction(
                                user.getEmail(),
                                amount,
                                TransactionType.SPENT,
                                timestamp,
                                currentAccountCurrency
                        ));
                    }

                    user.addTransaction(new TransferTransaction(
                            timestamp,
                            description,
                            currentIban,
                            receiverIban,
                            amount,
                            currentAccountCurrency,
                            "sent",
                            currentIban
                    ));
                    if (commerciant != null && commerciant.getStrategy() != null) {
                        commerciant.getStrategy().applyCashback(
                                currentAccount,
                                amount,
                                currentAccountCurrency,
                                currencyGraph,
                                user,
                                commerciant
                        );
                    }
                    upgradePlanForSilver(user, currentAccountCurrency, amount, currencyGraph);
                }
            } else {
                ObjectNode node = mapper.createObjectNode();
                node.put("command", "sendMoney");
                node.put("timestamp", timestamp);
                ObjectNode obj = node.putObject("output");
                obj.put("description", "User not found");
                obj.put("timestamp", timestamp);
                output.add(node);
            }
        }
    }

    /**
     * Actualizeaza planul utilizatorului daca este eligibil pentru upgrade la Silver.
     *
     * @param user Utilizatorul care executa tranzactia.
     * @param currentAccountCurrency Moneda contului curent.
     * @param amount Suma tranzactiei.
     * @param currencyGraph Graful de conversie valutara.
     */
    static void upgradePlanForSilver(final User user, final Currency currentAccountCurrency,
                                     final double amount, final Graph<Currency> currencyGraph) {
        if (user.getPlanType() == PlanType.SILVER) {
            double amountInRON = amount;

            if (currentAccountCurrency != Currency.RON) {
                ArrayList<Graph<Currency>.Edge> pathToRON =
                        currencyGraph.getPath(currentAccountCurrency, Currency.RON);
                if (pathToRON != null) {
                    double totalRateToRON = 1.0;
                    for (Graph<Currency>.Edge edge : pathToRON) {
                        totalRateToRON *= edge.getCost();
                    }
                    amountInRON = amount * totalRateToRON;
                }
            }
            if (amountInRON >= INCREMENT_SILVER_PAYMENT) {
                user.incrementSilverEligiblePayments();
            }
        }
    }

    /**
     * Converteste o suma dintr-o moneda in RON folosind graful de conversie.
     *
     * @param amount Suma care trebuie convertita.
     * @param from Moneda initiala.
     * @param currencyGraph Graful de conversie valutara.
     * @return Suma convertita in RON.
     */
    public static double convertToRon(final double amount,
                                      final Currency from,
                                      final Graph<Currency> currencyGraph) {
        if (from == Currency.RON) {
            return amount;
        }

        ArrayList<Graph<Currency>.Edge> path = currencyGraph.getPath(from, Currency.RON);
        if (path == null) {
            return amount;
        }
        double totalRate = 1.0;
        for (Graph<Currency>.Edge edge : path) {
            totalRate *= edge.getCost();
        }
        return amount * totalRate;
    }
}
