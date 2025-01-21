package org.poo.banking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reprezinta un cont de business care extinde clasa ClassicAccount.
 * Permite gestionarea utilizatorilor asociati, limitelor tranzactiilor si logarea tranzactiilor de business.
 */
public class BusinessAccount extends ClassicAccount {

    private static final double DEFAULT_LIMIT_INCASARE = 500.0;
    private static final double DEFAULT_LIMIT_TRANSFER = 500.0;
    private String ownerEmail;
    private Set<String> employees;
    private Set<String> managers;
    private Map<String, Double> defaultLimits;
    private Map<String, Double> customLimits;
    private Map<String, String> cardOwnership;
    private List<BusinessTransaction> businessTransactions;

    /**
     * Constructor pentru crearea unui cont de business.
     *
     * @param iban IBAN-ul contului.
     * @param currency Moneda contului.
     * @param ownerEmail Email-ul proprietarului contului.
     */
    public BusinessAccount(final String iban, final Currency currency,
                           final String ownerEmail) {
        super(iban, currency);
        this.ownerEmail = ownerEmail;

        this.employees = new LinkedHashSet<>();
        this.managers = new LinkedHashSet<>();
        this.cardOwnership = new HashMap<>();
        this.businessTransactions = new ArrayList<>();

        this.defaultLimits = new HashMap<>();
        defaultLimits.put("incasare", DEFAULT_LIMIT_INCASARE);
        defaultLimits.put("transfer", DEFAULT_LIMIT_TRANSFER);

        this.customLimits = new HashMap<>();
    }

    /**
     * Verifica daca acest cont este de tip business.
     *
     * @return true daca este cont de business, altfel false.
     */
    @Override
    public boolean isBusinessAccount() {
        return true;
    }

    /**
     * Returneaza email-ul proprietarului contului.
     *
     * @return Email-ul proprietarului.
     */
    public String getOwnerEmail() {
        return ownerEmail;
    }

    /**
     * Verifica daca un utilizator este proprietarul contului.
     *
     * @param email Email-ul utilizatorului.
     * @return true daca utilizatorul este proprietar, altfel false.
     */
    public boolean isOwner(final String email) {
        return ownerEmail != null && ownerEmail.equals(email);
    }

    /**
     * Verifica daca un utilizator este employee in contul de business.
     *
     * @param email Email-ul utilizatorului.
     * @return true daca utilizatorul este employee, altfel false.
     */
    public boolean isEmployee(final String email) {
        return employees.contains(email);
    }

    /**
     * Verifica daca un utilizator este manager in contul de business.
     *
     * @param email Email-ul utilizatorului.
     * @return true daca utilizatorul este manager, altfel false.
     */
    public boolean isManager(final String email) {
        return managers.contains(email);
    }

    /**
     * Verifica daca un utilizator este asociat contului de business.
     *
     * @param email Email-ul utilizatorului.
     * @return true daca utilizatorul este proprietar, manager sau employee, altfel false.
     */
    public boolean isAssociate(final String email) {
        return isOwner(email) || isManager(email) || isEmployee(email);
    }

    /**
     * Adauga un employee in contul de business.
     *
     * @param emailToAdd Email-ul utilizatorului de adaugat.
     * @return true daca adaugarea a fost reusita, altfel false.
     */
    public boolean addEmployee(final String emailToAdd) {
        return employees.add(emailToAdd);
    }

    /**
     * Adauga un manager in contul de business.
     *
     * @param emailToAdd Email-ul utilizatorului de adaugat.
     * @return true daca adaugarea a fost reusita, altfel false.
     */
    public boolean addManager(final String emailToAdd) {
        return managers.add(emailToAdd);
    }

    /**
     * Verifica daca un utilizator este deja employee sau manager.
     *
     * @param email Email-ul utilizatorului de verificat.
     * @return true daca este deja employee sau manager, altfel false.
     */
    public boolean alreadyEmployeeOrManager(final String email) {
        return employees.contains(email) || managers.contains(email);
    }

    /**
     * Seteaza o limita de tranzactie personalizata.
     *
     * @param transactionType Tipul tranzactiei.
     * @param limit Limita pentru tranzactie.
     */
    public void setTransactionLimit(final String transactionType, final double limit) {
        customLimits.put(transactionType, limit);
    }

    /**
     * Returneaza limita de tranzactie pentru un tip de tranzactie.
     *
     * @param transactionType Tipul tranzactiei.
     * @return Limita pentru tranzactie.
     */
    public double getTransactionLimit(final String transactionType) {
        if (customLimits.containsKey(transactionType)) {
            return customLimits.get(transactionType);
        }
        double rawLimitRon = defaultLimits.getOrDefault(transactionType, Double.MAX_VALUE);
        return convertRonToCurrency(rawLimitRon, this.getCurrency());
    }

    /**
     * Converte limitele din RON in moneda contului curent.
     *
     * @param ron Valoarea in RON.
     * @param c Moneda contului.
     * @return Valoarea convertita.
     */
    private double convertRonToCurrency(final double ron, final Currency targetCurrency) {
        if (targetCurrency == Currency.RON) {
            return ron;
        }

        Graph<Currency> graph = Bank.getInstance().getCurrencyGraph();

        ArrayList<Graph<Currency>.Edge> path = graph.getPath(Currency.RON, targetCurrency);
        if (path == null) {
            return ron;
        }

        double rate = 1.0;
        for (Graph<Currency>.Edge edge : path) {
            rate *= edge.getCost();
        }

        return ron * rate;
    }

    /**
     * Verifica daca un utilizator poate executa o tranzactie specifica.
     *
     * @param userEmail Email-ul utilizatorului.
     * @param transactionType Tipul tranzactiei.
     * @param amount Suma tranzactiei.
     * @return true daca tranzactia este permisa, altfel false.
     */
    public boolean canExecuteTransaction(final String userEmail,
                                         final String transactionType,
                                         final double amount) {
        if (isOwner(userEmail) || isManager(userEmail)) {
            return true;
        }
        if (isEmployee(userEmail)) {
            double limit = getTransactionLimit(transactionType);
            return (amount <= limit);
        }
        return false;
    }

    /**
     * Logheaza crearea unui card in contul de business.
     *
     * @param cardNumber Numarul cardului.
     * @param createdByEmail Email-ul utilizatorului care a creat cardul.
     */
    public void recordCardCreation(final String cardNumber, final String createdByEmail) {
        cardOwnership.put(cardNumber, createdByEmail);
    }

    /**
     * Verifica daca un utilizator poate sterge un card specific.
     *
     * @param cardNumber Numarul cardului.
     * @param requesterEmail Email-ul utilizatorului care face cererea.
     * @return true daca stergerea este permisa, altfel false.
     */
    public boolean canDeleteCard(final String cardNumber, final String requesterEmail) {
        if (isOwner(requesterEmail) || isManager(requesterEmail)) {
            return true;
        }
        String cardCreator = cardOwnership.get(cardNumber);
        return (cardCreator != null && cardCreator.equals(requesterEmail));
    }

    /**
     * Logheaza o tranzactie de business in cont.
     *
     * @param trx Tranzactia de logat.
     */
    public void logTransaction(final BusinessTransaction trx) {
        businessTransactions.add(trx);
    }

    /**
     * Calculeaza suma cheltuita de un utilizator intr-un interval de timp specific.
     *
     * @param userEmail Email-ul utilizatorului.
     * @param startTs Timpul de inceput (timestamp).
     * @param endTs Timpul de sfarsit (timestamp).
     * @return Suma cheltuita.
     */
    public double getSpentForUser(final String userEmail, final int startTs, final int endTs) {
        double spent = 0.0;
        for (BusinessTransaction trx : businessTransactions) {
            if (!trx.getUserEmail().equals(userEmail)) continue;
            int ts = trx.getTimestamp();
            if (ts < startTs || ts > endTs) continue;
            if (trx.getType() == TransactionType.SPENT) {
                spent += trx.getAmount();
            }
        }
        return spent;
    }

    /**
     * Calculeaza suma depusa de un utilizator intr-un interval de timp specific.
     *
     * @param userEmail Email-ul utilizatorului.
     * @param startTs Timpul de inceput (timestamp).
     * @param endTs Timpul de sfarsit (timestamp).
     * @return Suma depusa.
     */
    public double getDepositedForUser(final String userEmail, final int startTs,
                                      final int endTs) {
        double dep = 0.0;
        for (BusinessTransaction trx : businessTransactions) {
            if (!trx.getUserEmail().equals(userEmail)) continue;
            int ts = trx.getTimestamp();
            if (ts < startTs || ts > endTs) continue;
            if (trx.getType() == TransactionType.DEPOSIT) {
                dep += trx.getAmount();
            }
        }
        return dep;
    }

    /**
     * Returneaza lista de manageri asociati contului de business.
     *
     * @return Lista de manageri.
     */
    public List<String> getManagers() {
        return new ArrayList<>(this.managers);
    }

    /**
     * Returneaza lista de angajati asociati contului de business.
     *
     * @return Lista de angajati.
     */
    public List<String> getEmployees() {
        return new ArrayList<>(this.employees);
    }

    /**
     * Verifica daca un utilizator are permisiunea de a folosi un card specific.
     *
     * @param user Utilizatorul care solicita verificarea.
     * @param card Cardul pentru care se verifica accesul.
     * @return true daca utilizatorul are acces, altfel false.
     */
    public boolean checkCard(final User user, final ClassicCard card) {
        if (managers.contains(user.getEmail())) {
            return true;
        }
        if (employees.contains(user.getEmail())) {
            return true;
        }
        return user.getEmail().equals(getOwnerEmail());
    }
}
