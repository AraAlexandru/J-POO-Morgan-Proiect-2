package org.poo.commerciants;

import org.poo.banking.ClassicAccount;
import org.poo.banking.Currency;
import org.poo.banking.Graph;
import org.poo.banking.User;
import org.poo.utils.CashbackHandler;

/**
 * Strategy pt "nrOfTransactions".
 * La fiecare tranzactie, incrementam contorul
 * si daca atinge 2 / 5 / 10, setam discounturile corespunzatoare
 */
public class NrOfTransactionsStrategy implements CashbackStrategy {

    @Override
    public void applyCashback(ClassicAccount account,
                              double convertedAmount,
                              Currency currentAccountCurrency,
                              Graph<Currency> currencyGraph,
                              User user,
                              Commerciant currentCommerciant) {

        CashbackHandler.applyNrOfTransactions(account, currentCommerciant);
    }
}
