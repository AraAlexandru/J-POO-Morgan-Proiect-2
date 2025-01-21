package org.poo.observer;

import org.poo.banking.Transaction;
import org.poo.banking.User;

/**
 * Interfata Observer pentru User
 * Metoda onNewTransaction e chemata cand user adauga tranzactie
 */
public interface UserObserver {
    void onNewTransaction(User user, Transaction transaction);
}
