package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.banking.ClassicAccount;
import org.poo.banking.Currency;
import org.poo.banking.Graph;
import org.poo.transactions.InsufficientFundsTransaction;
import org.poo.banking.PlanType;
import org.poo.transactions.UpgradePlanTransaction;
import org.poo.banking.User;

import java.util.ArrayList;

public final class UpgradePlanCommand implements Command {
    private String account;
    private String newPlanType;
    private Graph<Currency> currencyGraph;
    private static final double STANDARD_OR_STUDENT_TO_SILVER = 100;
    private static final double FEE_FOR_GOLD = 350;
    private static final double VALUE_FOR_ELIGIBLE_PAYMENTS = 0;
    private static final double SILVER_TO_GOLD = 250;
    private static final int SILVER_ELIGIBLE_PAYMENT_COUNT = 5;

    public UpgradePlanCommand(final String account, final String newPlanType,
                              final Graph<Currency> currencyGraph) {
        this.account = account;
        this.newPlanType = newPlanType;
        this.currencyGraph = currencyGraph;
    }

    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
        User user = bank.getUserByAccount(account);

        if (user == null) {
            ObjectNode node = makeOutput(mapper, timestamp, "Account not found");
            output.add(node);
        } else {
            PlanType currentPlan = user.getPlanType();
            PlanType targetPlan;
            try {
                targetPlan = PlanType.valueOf(newPlanType.toUpperCase());
            } catch (Exception e) {
                ObjectNode node = makeOutput(mapper, timestamp, "Invalid plan type");
                output.add(node);
                return;
            }

            if (currentPlan == targetPlan) {
                return;
            }

            if (checkDowngradePlan(currentPlan, targetPlan)) {
                return;
            }

            double feeInRON = 0.0;
            if (currentPlan == PlanType.STANDARD || currentPlan == PlanType.STUDENT) {
                if (targetPlan == PlanType.SILVER) {
                    feeInRON = STANDARD_OR_STUDENT_TO_SILVER;
                } else if (targetPlan == PlanType.GOLD) {
                    feeInRON = FEE_FOR_GOLD;
                }
            } else if (currentPlan == PlanType.SILVER && targetPlan == PlanType.GOLD) {
                if (user.getSilverEligiblePayments() >= SILVER_ELIGIBLE_PAYMENT_COUNT) {
                    feeInRON = VALUE_FOR_ELIGIBLE_PAYMENTS;
                    user.resetSilverEligiblePayments();
                } else {
                    feeInRON = SILVER_TO_GOLD;
                }
            }

            ClassicAccount classicAccount = bank.getAccountByIban(account);
            if (classicAccount == null) {
                ObjectNode node = makeOutput(mapper, timestamp, "Account not found");
                output.add(node);
            } else {
                double feeInAccountCurrency = feeInRON;
                Currency currentAccountCurrency = classicAccount.getCurrency();

                if (currentAccountCurrency != Currency.RON) {
                    ArrayList<Graph<Currency>.Edge> path =
                            currencyGraph.getPath(Currency.RON,
                                    currentAccountCurrency);

                    if (path != null) {
                        double totalRate = 1.0;
                        for (Graph<Currency>.Edge edge : path) {
                            totalRate *= edge.getCost();
                        }
                        feeInAccountCurrency = feeInRON * totalRate;
                    }
                }

                if (classicAccount.getBalance() >= feeInAccountCurrency) {
                    classicAccount.addFunds(-feeInAccountCurrency);
                    user.setPlanType(targetPlan);
                    user.addTransaction(new UpgradePlanTransaction(timestamp, account, targetPlan));
                } else {
                    user.addTransaction(new InsufficientFundsTransaction(timestamp));
                }

            }
        }
    }

    /**
     * Verifica daca planul țintă reprezintă un downgrade față de planul curent.
     *
     * @param currentPlan Planul curent al utilizatorului.
     * @param targetPlan Planul țintă.
     * @return True dacă este un downgrade, altfel false.
     */
    public boolean checkDowngradePlan(final PlanType currentPlan, final PlanType targetPlan) {
        if (targetPlan == PlanType.STANDARD) {
            return true;
        }
        if (targetPlan == PlanType.STUDENT) {
            return true;
        }
        if (targetPlan == PlanType.SILVER && currentPlan == PlanType.GOLD) {
            return true;
        }
        return false;
    }

    public String getAccount() {
        return account;
    }

    public String getNewPlanType() {
        return newPlanType;
    }

    private ObjectNode makeOutput(final ObjectMapper mapper,
                                  final int timestamp, final String description) {
        ObjectNode commandNode = mapper.createObjectNode();
        commandNode.put("command", "upgradePlan");
        commandNode.put("timestamp", timestamp);

        ObjectNode outNode = mapper.createObjectNode();
        outNode.put("description", description);
        outNode.put("timestamp", timestamp);

        commandNode.set("output", outNode);
        return commandNode;
    }

}
