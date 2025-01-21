package org.poo.commerciants;

import org.poo.banking.ClassicAccount;
import org.poo.banking.Currency;
import org.poo.banking.Graph;
import org.poo.banking.User;

/**
 * Strategy pentru calcul & aplicare de cashback.
 */
public interface CashbackStrategy {
    /**
     * Executa logica de cashback.
     *
     * @param account        contul unde scad/actualizez
     * @param convertedAmount suma deja convertita la account.getCurrency()
     * @param currentAccountCurrency moneda cont
     * @param currencyGraph  pt conversii
     * @param user           user care face tranzactia
     * @param currentCommerciant obj. comerciant
     */
    void applyCashback(ClassicAccount account,
                       double convertedAmount,
                       Currency currentAccountCurrency,
                       Graph<Currency> currencyGraph,
                       User user,
                       Commerciant currentCommerciant);
}
