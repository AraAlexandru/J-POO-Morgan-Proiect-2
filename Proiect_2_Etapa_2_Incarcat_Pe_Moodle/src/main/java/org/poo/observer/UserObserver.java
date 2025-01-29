package org.poo.observer;

import org.poo.transactions.Transaction;
import org.poo.banking.User;

/**
 * Interfata Observer pentru User
 * Metoda onNewTransaction e chemata cand user adauga tranzactie
 */
public interface UserObserver {
    /**
     * Metoda apelata atunci cand un utilizator efectueaza o tranzactie noua.
     *
     * @param user Utilizatorul care a efectuat tranzactia.
     * @param transaction Tranzactia efectuata de utilizator.
     */
    void onNewTransaction(User user, Transaction transaction);
}
