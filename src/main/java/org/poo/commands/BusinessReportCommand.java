package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Bank;
import org.poo.banking.BusinessAccount;
import org.poo.banking.ClassicAccount;
import org.poo.banking.User;

import java.util.ArrayList;
import java.util.List;

public class BusinessReportCommand implements Command {
    private String type;
    private String accountIban;
    private int startTimestamp;
    private int endTimestamp;
    private int timestamp;

    public BusinessReportCommand(String type, String accountIban,
                                 int startTimestamp, int endTimestamp,
                                 int timestamp) {
        this.type = type;
        this.accountIban = accountIban;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.timestamp = timestamp;
    }

    @Override
    public void execute(Bank bank, ArrayNode output, ObjectMapper mapper, int ignored) {

        ObjectNode node = mapper.createObjectNode();
        node.put("command", "businessReport");


        ClassicAccount acc = bank.getAccountByIban(accountIban);
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

        ObjectNode reportOutput = mapper.createObjectNode();

        reportOutput.put("IBAN", bAcc.getIban());
        reportOutput.put("balance", bAcc.getBalance());
        reportOutput.put("currency", bAcc.getCurrency().toString());

        double spendLimit = bAcc.getTransactionLimit("transfer");
        double depositLimit = bAcc.getTransactionLimit("incasare");

        reportOutput.put("spending limit", spendLimit);
        reportOutput.put("deposit limit", depositLimit);

        reportOutput.put("statistics type", this.type);

        if ("transaction".equals(this.type)) {
            ArrayNode managersArr = mapper.createArrayNode();
            ArrayNode employeesArr = mapper.createArrayNode();
            List<String> managerEmails = new ArrayList<>(bAcc.getManagers());
            List<String> employeeEmails = new ArrayList<>(bAcc.getEmployees());

            double totalSpent = 0.0;
            double totalDeposited = 0.0;

            for (String mgrEmail : managerEmails) {
                User mgrUser = bank.getUserByEmail(mgrEmail);
                double spent = bAcc.getSpentForUser(mgrEmail, startTimestamp, endTimestamp);
                double deposited = bAcc.getDepositedForUser(mgrEmail, startTimestamp, endTimestamp);
                totalSpent += spent;
                totalDeposited += deposited;

                ObjectNode mgrNode = mapper.createObjectNode();
                mgrNode.put("username", buildUserName(mgrUser));
                mgrNode.put("spent", spent);
                mgrNode.put("deposited", deposited);
                managersArr.add(mgrNode);
            }

            for (String empEmail : employeeEmails) {
                User empUser = bank.getUserByEmail(empEmail);
                double spent = bAcc.getSpentForUser(empEmail, startTimestamp, endTimestamp);
                double deposited = bAcc.getDepositedForUser(empEmail, startTimestamp, endTimestamp);
                totalSpent += spent;
                totalDeposited += deposited;

                ObjectNode empNode = mapper.createObjectNode();
                empNode.put("username", buildUserName(empUser));
                empNode.put("spent", spent);
                empNode.put("deposited", deposited);
                employeesArr.add(empNode);
            }

            reportOutput.set("managers", managersArr);
            reportOutput.set("employees", employeesArr);
            reportOutput.put("total spent", totalSpent);
            reportOutput.put("total deposited", totalDeposited);
        } else if ("commerciant".equals(this.type)) {

        }

        node.set("output", reportOutput);
        node.put("timestamp", this.timestamp);
        output.add(node);
    }

    /**
     * Construiește numele complet "LastName FirstName" pentru sortare și afișare.
     */
    private String buildUserName(User u) {
        if (u == null) {
            return "";
        }
        return u.getLastName() + " " + u.getFirstName();
    }

    /**
     * Comparator pt sortare.
     */
    private int compareNames(String email1, String email2, Bank bank) {
        User u1 = bank.getUserByEmail(email1);
        User u2 = bank.getUserByEmail(email2);
        if (u1 == null || u2 == null) {
            return 0;
        }
        String name1 = buildUserName(u1);
        String name2 = buildUserName(u2);
        return name1.compareTo(name2);
    }
}
