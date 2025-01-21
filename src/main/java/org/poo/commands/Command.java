package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.banking.Bank;


public interface Command {

    /**
     * Functia executa comanda.
     */
    void execute(Bank bank, ArrayNode output, ObjectMapper mapper, int timestamp);
}
