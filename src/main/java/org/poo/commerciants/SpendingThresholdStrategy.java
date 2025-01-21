package org.poo.commerciants;

import org.poo.banking.ClassicAccount;
import org.poo.banking.Currency;
import org.poo.banking.Graph;
import org.poo.banking.User;
import org.poo.utils.CashbackHandler;

/**
 * Strategy pt "spendingThreshold".
 */
public class SpendingThresholdStrategy implements CashbackStrategy {
    @Override
    public void applyCashback(ClassicAccount account,
                              double convertedAmount,
                              Currency currentAccountCurrency,
                              Graph<Currency> currencyGraph,
                              User user,
                              Commerciant currentCommerciant) {

        CashbackHandler.applySpendingThreshold(account,
                convertedAmount,
                currentAccountCurrency,
                currencyGraph,
                user,
                currentCommerciant);
    }
}
