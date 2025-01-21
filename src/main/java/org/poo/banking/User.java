package org.poo.banking;

import lombok.Getter;
import lombok.Setter;
import org.poo.observer.UserObserver;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String birthdate;
    private String occupation;
    private List<ClassicAccount> accounts;
    private ArrayList<Transaction> transactions;

    private List<UserObserver> observers = new ArrayList<>();
    @Getter
    @Setter
    private PlanType planType;
    private int silverEligiblePayments;
    private List<SplitPaymentEvent> splitPaymentEvents;

    public User(final String firstName, final String lastName, final String email,
                final String birthdate, final String occupation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthdate = birthdate;
        this.occupation = occupation;
        accounts = new ArrayList<>();
        transactions = new ArrayList<>();
        if (occupation.equals("student")) {
            this.planType = PlanType.STUDENT;
        } else {
            this.planType = PlanType.STANDARD;
        }
        this.silverEligiblePayments = 0;
        this.splitPaymentEvents = new ArrayList<>();
    }

    /**
     * Incrementeaza numărul de plati eligibiles pentru planul Silver.
     */
    public void incrementSilverEligiblePayments() {
        silverEligiblePayments++;
    }

    /**
     * Returneaza numarul de plati eligibile pentru planul Silver.
     * @return Numarul de plati eligibile.
     */
    public int getSilverEligiblePayments() {
        return silverEligiblePayments;
    }

    /**
     * Resetează numarul de plati eligibile pentru planul Silver la 0.
     */
    public void resetSilverEligiblePayments() {
        silverEligiblePayments = 0;
    }


    /**
     * Adauga o tranzactie in lista de tranzactii ale utilizatorului.
     * @param transaction Tranzactia de adaugat.
     */
    public void addTransaction(final Transaction transaction) {
        transactions.add(transaction);
        notifyNewTransaction(transaction);
    }

    /**
     * Returneaza lista de tranzactii ale utilizatorului.
     * @return Lista de tranzactii.
     */
    public List<Transaction> getTransactions(final String iban) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (iban == null || transaction.getIban() == null || transaction.getIban().equals(iban)) {
                result.add(transaction);
            }
        }
        return result;
    }

    /**
     * Adauga o tranzactie în lista de tranzactii ale utilizatorului.
     *
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Gaseste un cont pe baza IBAN-ului.
     * @param iban IBAN-ul contului cautat.
     * @return Contul gasit sau null daca nu exista.
     */
    public ClassicAccount getAccountByIban(final String iban) {
        for (ClassicAccount account : accounts) {
            if (account.getIban().equals(iban)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Gaseste un cont pe baza numarului de card.
     * @param cardNumber Numarul cardului cautat.
     * @return Contul asociat cardului sau null daca nu exista.
     */
    public ClassicAccount getAccountByCard(final String cardNumber) {
        for (ClassicAccount account : accounts) {
            for (ClassicCard card : account.getCards()) {
                if (card.getCardNumber().equals(cardNumber)) {
                    return account;
                }
            }
        }
        return null;
    }

    /**
     * Gaseste un card pe baza numarului de card.
     * @param cardNumber Numarul cardului cautat.
     * @return Cardul gasit sau null daca nu exista.
     */
    public ClassicCard getCardByNumber(final String cardNumber) {
        for (ClassicAccount account : accounts) {
            for (ClassicCard card : account.getCards()) {
                if (card.getCardNumber().equals(cardNumber)) {
                    return card;
                }
            }
        }
        return null;
    }

    /**
     * Returneaza lista de conturi ale utilizatorului.
     * @return Lista de conturi.
     */
    public List<ClassicAccount> getAccounts() {
        return accounts;
    }

    /**
     * Adauga un cont nou in lista de conturi ale utilizatorului.
     * @param account Contul de adaugat.
     */
    public void addAccount(final ClassicAccount account) {
        accounts.add(account);
    }

    /**
     * Sterge un cont pe baza IBAN-ului daca nu are fonduri ramase.
     * @param iban IBAN-ul contului de sters.
     * @return True daca stergerea a fost realizata, altfel false.
     */
    public Boolean deleteAccountByIban(final String iban) {
        ClassicAccount account = this.getAccountByIban(iban);
        if (account == null || account.getBalance() > 0) {
            return false;
        }
        accounts.remove(account);
        return true;
    }

    public void addSplitPayment(final SplitPaymentEvent splitPaymentEvent) {
        splitPaymentEvents.add(splitPaymentEvent);
    }

    /**
     * Returneaza prenumele utilizatorului.
     * @return Prenumele utilizatorului.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returneaza numele de familie al utilizatorului.
     * @return Numele de familie al utilizatorului.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returneaza adresa de email a utilizatorului.
     * @return Adresa de email a utilizatorului.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returneaza ziua de nastere a utilizatorului.
     * @return Ziua de nastere a utilizatorului.
     */
    public String getBirthdate() {
        return birthdate;
    }

    /**
     * Returneaza ocupatia utilizatorului.
     * @return Ocupatia utilizatorului.
     */
    public String getOccupation() {
        return occupation;
    }

    /**
     * Returneaza varsta utilizatorului.
     * @return Varsta utilizatorului.
     */
    public int getUserAge() {
        LocalDate currentDate = LocalDate.now();
        LocalDate birthdayYear = LocalDate.parse(birthdate);
        return Period.between(birthdayYear, currentDate).getYears();
    }

    /**
     * Returneaza fee-ul tranzactiei.
     * @return fee.
     */
    public double getFeeForTransaction(final double amount, final Graph<Currency> currencyGraph,
                                       final Currency currency) {
        double convertedAmount = amount;
        var path = currencyGraph.getPath(currency, Currency.RON);
        if (path != null) {
            for (Graph<Currency>.Edge edge : path) {
                convertedAmount *= edge.getCost();
            }
        }
        double feeInRon = planType.calculateFee(convertedAmount);
        path = currencyGraph.getPath(Currency.RON, currency);
        if (path != null) {
            for (Graph<Currency>.Edge edge : path) {
                feeInRon *= edge.getCost();
            }
        }
        return feeInRon;
    }

    /**
     * Returneaza comisionul tranzactiei.
     * @return Returneaza comisionul tranzactiei.
     */
    public double getCommisionForTransaction(final double amount) {
        return planType.getCommissionForTransaction(amount);
    }

    /**
     * Accepta o plata impartita de un anumit tip.
     * @param type Tipul platii impartite de acceptat.
     */
    public void acceptSplitPayment(final String type) {
        for (SplitPaymentEvent splitPaymentEvent : splitPaymentEvents) {
            if (splitPaymentEvent.getType().equals(type) && !splitPaymentEvent.hasAccepted(this)) {
                splitPaymentEvent.accept(this);
                return;
            }
        }
    }

    /**
     * Respinge o plata impartita de un anumit tip.
     * @param type Tipul platii impartite de respins.
     */
    public void rejectSplitPayment(final String type) {
        for (SplitPaymentEvent splitPaymentEvent : splitPaymentEvents) {
            if (splitPaymentEvent.getType().equals(type) && !splitPaymentEvent.hasAccepted(this)) {
                splitPaymentEvent.reject(this);
                return;
            }
        }
    }

    /**
     * Elimina o plata impartita din lista de plati ale utilizatorului.
     * @param splitPaymentEvent Plata impartita de eliminat.
     */
    public void removeSplitPayment(final SplitPaymentEvent splitPaymentEvent) {
        splitPaymentEvents.remove(splitPaymentEvent);
    }

    public void addObserver(final UserObserver obs) {
        observers.add(obs);
    }

    public void removeObserver(final UserObserver obs) {
        observers.remove(obs);
    }

    /**
     * Metoda care notifica toti observerii ca a aparut o noua tranzactie.
     */
    private void notifyNewTransaction(final Transaction t) {
        for (UserObserver obs : observers) {
            obs.onNewTransaction(this, t);
        }
    }
}
