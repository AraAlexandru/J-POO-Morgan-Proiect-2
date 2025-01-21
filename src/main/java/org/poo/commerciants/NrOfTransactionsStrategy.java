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
public final class NrOfTransactionsStrategy implements CashbackStrategy {

    @Override
    public void applyCashback(final ClassicAccount account,
                              final double convertedAmount,
                              final Currency currentAccountCurrency,
                              final Graph<Currency> currencyGraph,
                              final User user,
                              final Commerciant currentCommerciant) {

        CashbackHandler.applyNrOfTransactions(account, currentCommerciant);
    }
}
