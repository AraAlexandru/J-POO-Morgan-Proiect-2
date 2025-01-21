package org.poo.banking;
/**
 * Aceasta interfata defineste metoda necesara pentru implementarea design pattern-ului visitor
 */
public interface Visitable {
    /**
     * Metoda care accepta un vizitator pentru a procesa obiectul curent
     */
    void accept(Visitor v);
}
