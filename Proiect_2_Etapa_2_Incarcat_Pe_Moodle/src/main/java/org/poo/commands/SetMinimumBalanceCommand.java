package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.banking.Bank;
import org.poo.banking.ClassicAccount;

public class SetMinimumBalanceCommand implements  Command {
    private String iban;
    private double minimmumBalance;

    public SetMinimumBalanceCommand(final String iban, final double minimmumBalance) {
        this.iban = iban;
        this.minimmumBalance = minimmumBalance;
    }

    /**
     * Executa comanda pentru setarea soldului minim al unui cont.
     *
     * @param bank      Instanta bancii care contine toate conturile si utilizatorii.
     * @param output    Nodul JSON in care se adauga rezultatele comenzii.
     * @param mapper    Obiectul Jackson pentru manipularea JSON-ului.
     * @param timestamp Timpul la care este executata comanda.
     */
    @Override
    public void execute(final Bank bank, final ArrayNode output,
                        final ObjectMapper mapper, final int timestamp) {
        ClassicAccount account = bank.getAccountByIban(iban);

        if (account != null && account.getBalance() <= minimmumBalance) {
            account.setMinimumBalance(minimmumBalance);
        }
    }
}
