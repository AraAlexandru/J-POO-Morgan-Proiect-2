package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.banking.Bank;
import org.poo.banking.ClassicAccount;
import org.poo.banking.MinimumAgeTransaction;
import org.poo.banking.User;
import org.poo.banking.WithdrawSavingsTransaction;

public class WithdrawSavingsCommand implements Command {
    private String account;
    private double amount;
    private String currencyValue;
    private static final int MINIMUM_AGE = 21;

    public WithdrawSavingsCommand(final String account, final double amount,
                                  final String currencyValue) {
        this.account = account;
        this.amount = amount;
        this.currencyValue = currencyValue;
    }

    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
        User user = bank.getUserByAccount(account);
        if (user == null) {
            return;
        }
        int userAge = user.getUserAge();

        if (userAge < MINIMUM_AGE) {
            user.addTransaction(new MinimumAgeTransaction(timestamp));
            return;
        }
        ClassicAccount savingsAccount = bank.getAccountByIban(account);
        ClassicAccount targetAccount = null;
        if (savingsAccount != null && savingsAccount.isSavingsAccount()) {
            for (ClassicAccount account : user.getAccounts()) {
                if (account.isClassicAccount()) {
                    targetAccount = account;
                    break;
                }
            }
        }
        if (targetAccount == null) {
            user.addTransaction(new WithdrawSavingsTransaction(timestamp,
                    account, "You do not have a classic account."));
        }
    }
}
