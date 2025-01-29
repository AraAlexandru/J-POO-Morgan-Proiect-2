package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.transactions.AccountCreatedTransaction;
import org.poo.banking.Bank;
import org.poo.banking.BusinessAccount;
import org.poo.banking.ClassicAccount;
import org.poo.banking.Currency;
import org.poo.banking.SavingsAccount;
import org.poo.banking.User;
import org.poo.utils.Utils;

public class AddAccountCommand implements Command {
    private String email;
    private String accountType;
    private String currencyValue;
    private double interestRate;

    public AddAccountCommand(final String email, final String accountType,
                             final String currencyValue, final double interestRate) {
        this.email = email;
        this.accountType = accountType;
        this.currencyValue = currencyValue;
        this.interestRate = interestRate;
    }

    /**
     * Constructorul clasei pentru initializarea parametrilor necesari crearii unui cont.
     *
     * @param email        Adresa de email a utilizatorului.
     * @param accountType  Tipul contului (clasic sau de economii).
     * @param currencyValue Moneda in care va fi deschis contul.
     * @param interestRate Rata dobanzii pentru conturile de economii.
     */
    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
        User user = bank.getUserByEmail(email);
        if (user != null) {
            String iban = Utils.generateIBAN();
            Currency currency = Currency.valueOf(currencyValue);

            if (accountType.equals("classic")) {
                ClassicAccount account = new ClassicAccount(iban, currency);
                user.addAccount(account);
            } else if (accountType.equals("savings")) {
                SavingsAccount account =
                        new SavingsAccount(iban, currency, interestRate);
                user.addAccount(account);
            } else if (accountType.equals("business")) {
                BusinessAccount account =
                        new BusinessAccount(iban, currency, email);
                user.addAccount(account);
            }
            user.addTransaction(new AccountCreatedTransaction(timestamp));
        }
    }
}
