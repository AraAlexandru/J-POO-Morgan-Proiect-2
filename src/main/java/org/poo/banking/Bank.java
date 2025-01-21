package org.poo.banking;

import org.poo.commerciants.Commerciant;

import java.util.ArrayList;
import java.util.List;

public final class Bank {
    private static Bank instance;
    private List<User> users;
    private List<Commerciant> commerciants;

    private Bank() {
        users = new ArrayList<>();
        commerciants = new ArrayList<>();
    }

    /**
     * Metoda statica pentru obtinerea instantei clasei Bank.
     * S-a folosit design pattern-ul Singleton pentru a avea
     * o singura instanta a bancii.
     * @return instanta unica a clasei Bank.
     */
    public static Bank getInstance() {
        if (instance == null) {
            instance = new Bank();
        }
        return instance;
    }

    /**
     * Gaseste un utilizator pe baza unui IBAN al unui cont.
     *
     * @param iban IBAN-ul contului.
     * @return utilizatorul care detine contul sau null daca nu este gasit.
     */
    public User getUserByAccount(final String iban) {
        for (User user : users) {
            for (ClassicAccount account : user.getAccounts()) {
                if (account.getIban().equals(iban)) {
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Gaseste un cont clasic pe baza IBAN-ului.
     *
     * @param iban IBAN-ul contului.
     * @return contul clasic gasit sau null daca nu este gasit.
     */
    public ClassicAccount getAccountByIban(final String iban) {
        for (User user : users) {
            ClassicAccount account = user.getAccountByIban(iban);
            if (account != null) {
                return account;
            }
        }
        return null;
    }

    /**
     * Gaseste un utilizator pe baza numarului cardului.
     *
     * @param cardNumber numarul cardului.
     * @return utilizatorul care detine cardul sau null daca nu este gasit.
     */
    public User getUserByCardNumber(final String cardNumber) {
        for (User user : users) {
            if (user.getCardByNumber(cardNumber) != null) {
                return user;
            }
        }
        return null;
    }

    /**
     * Gaseste un card clasic pe baza numarului cardului.
     *
     * @param cardNumber numarul cardului.
     * @return cardul clasic gasit sau null daca nu este gasit.
     */
    public ClassicCard getCardByNumber(final String cardNumber) {
        for (User user : users) {
            ClassicCard card = user.getCardByNumber(cardNumber);
            if (card != null) {
                return card;
            }
        }
        return null;
    }

    /**
     * Gaseste un utilizator pe baza adresei de email.
     *
     * @param email adresa de email a utilizatorului.
     * @return utilizatorul gasit sau null daca nu este gasit.
     */
    public User getUserByEmail(final String email) {
        for (User user : users) {
            if (email != null && email.equals(user.getEmail())) {
                return user;
            }
        }
        return null;
    }

    /**
     * Reseteaza lista de utilizatori.
     */
    public void reset() {
        users.clear();
        commerciants.clear();
    }

    /**
     * Returneaza lista de utilizatori ai bancii.
     *
     * @return lista de utilizatori.
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Adauga un utilizator in lista bancii.
     *
     * @param user utilizatorul de adaugat.
     */
    public void addUser(final User user) {
        users.add(user);
    }

    public void addCommerciant(final Commerciant commerciant) {
        commerciants.add(commerciant);
    }

    public List<Commerciant> getCommerciants() {
        return commerciants;
    }

    public Commerciant getCommerciantByName(final String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        for (Commerciant commerciant : commerciants) {
            if (commerciant.getName().equals(name)) {
                return commerciant;
            }
        }
        return null;
    }

    public Commerciant getCommerciantByIban(final String account) {
        if (account == null || account.isEmpty()) {
            return null;
        }
        for (Commerciant commerciant : commerciants) {
            if (commerciant.getAccount().equals(account)) {
                return commerciant;
            }
        }
        return null;
    }

    public ClassicAccount getAccountByCardNumber(final String cardNumber) {
        // iterează toți userii, toate conturile,
        // caută contul care conține cardNumber în .getCards().
        for (User u : users) {
            for (ClassicAccount acc : u.getAccounts()) {
                if (acc.getCardByNumber(cardNumber) != null) {
                    return acc;
                }
            }
        }
        return null; // nu e găsit
    }

    private Graph<Currency> currencyGraph; // = null initial

    public Graph<Currency> getCurrencyGraph() {
        return currencyGraph;
    }

    public void setCurrencyGraph(Graph<Currency> currencyGraph) {
        this.currencyGraph = currencyGraph;
    }

}
