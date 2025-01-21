package org.poo.banking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class TransactionPrinter implements Visitor {
    private ArrayNode transactionsArray;
    private ObjectMapper mapper;

    public TransactionPrinter(final ArrayNode transactionsArray, final ObjectMapper mapper) {
        this.transactionsArray = transactionsArray;
        this.mapper = mapper;
    }

    /**
     * Viziteaza o tranzactie de creare a unui cont si adauga informatia in JSON.
     *
     * @param accountCreated tranzactia de creare a unui cont.
     */
    @Override
    public void visit(final AccountCreatedTransaction accountCreated) {
        ObjectNode node = mapper.createObjectNode();
        node.put("timestamp", accountCreated.getTimestamp());
        node.put("description", accountCreated.getDescription());
        transactionsArray.add(node);
    }

    /**
     * Viziteaza o tranzactie de fonduri insuficiente si adauga informatia in JSON.
     *
     * @param insufficientFunds tranzactia de fonduri insuficiente.
     */
    @Override
    public void visit(final InsufficientFundsTransaction insufficientFunds) {
        ObjectNode node = mapper.createObjectNode();
        node.put("timestamp", insufficientFunds.getTimestamp());
        node.put("description", insufficientFunds.getDescription());
        transactionsArray.add(node);
    }

    /**
     * Viziteaza o tranzactie de transfer si adauga informatia in JSON.
     *
     * @param transferTransaction tranzactia de transfer.
     */
    @Override
    public void visit(final TransferTransaction transferTransaction) {
        ObjectNode node = mapper.createObjectNode();
        node.put("timestamp", transferTransaction.getTimestamp());
        node.put("description", transferTransaction.getDescription());
        node.put("senderIBAN", transferTransaction.getSenderIBAN());
        node.put("receiverIBAN", transferTransaction.getReceiverIBAN());
        node.put("amount", transferTransaction.getAmount() + " " + transferTransaction.getType());
        node.put("transferType", transferTransaction.getTransferType());
        transactionsArray.add(node);
    }

    /**
     * Viziteaza o tranzactie de creare a unui card si adauga informatia in JSON.
     *
     * @param cardCreated tranzactia de creare a unui card.
     */
    @Override
    public void visit(final CardCreatedTransaction cardCreated) {
        ObjectNode node = mapper.createObjectNode();
        node.put("timestamp", cardCreated.getTimestamp());
        node.put("description", cardCreated.getDescription());
        node.put("card", cardCreated.getCardNumber());
        node.put("cardHolder", cardCreated.getEmail());
        node.put("account", cardCreated.getAccountIban());
        transactionsArray.add(node);
    }

    /**
     * Viziteaza o tranzactie de plata cu cardul si adauga informatia in JSON.
     *
     * @param cardPayment tranzactia de plata cu cardul.
     */
    @Override
    public void visit(final CardPaymentTransaction cardPayment) {
        ObjectNode node = mapper.createObjectNode();
        node.put("timestamp", cardPayment.getTimestamp());
        node.put("description", cardPayment.getDescription());
        node.put("amount", cardPayment.getAmount());
        node.put("commerciant", cardPayment.getCommerciant());
        transactionsArray.add(node);
    }

    /**
     * Viziteaza o tranzactie de distrugere a unui card si adauga informatia in JSON.
     *
     * @param cardDestroyed tranzactia de distrugere a unui card.
     */
    @Override
    public void visit(final CardDestroyedTransaction cardDestroyed) {
        ObjectNode node = mapper.createObjectNode();
        node.put("timestamp", cardDestroyed.getTimestamp());
        node.put("description", cardDestroyed.getDescription());
        node.put("card", cardDestroyed.getCardNumber());
        node.put("cardHolder", cardDestroyed.getEmail());
        node.put("account", cardDestroyed.getAccountIban());
        transactionsArray.add(node);
    }

    /**
     * Viziteaza o tranzactie de blocare temporara a unui card si adauga informatia in JSON.
     *
     * @param freezeCard tranzactia de blocare temporara a unui card.
     */
    @Override
    public void visit(final FreezeCardTransaction freezeCard) {
        ObjectNode node = mapper.createObjectNode();
        node.put("timestamp", freezeCard.getTimestamp());
        node.put("description", freezeCard.getDescription());
        transactionsArray.add(node);
    }

    /**
     * Viziteaza o tranzactie de blocare a unui card si adauga informatia in JSON.
     *
     * @param cardFrozen tranzactia de blocare a unui card.
     */
    @Override
    public void visit(final CardFrozenTransaction cardFrozen) {
        ObjectNode node = mapper.createObjectNode();
        node.put("timestamp", cardFrozen.getTimestamp());
        node.put("description", cardFrozen.getDescription());
        transactionsArray.add(node);
    }

    /**
     * Viziteaza o tranzactie de plata impartita si adauga informatia in JSON.
     *
     * @param splitPayment tranzactia de plata impartita.
     */
    @Override
    public void visit(final SplitPaymentTransaction splitPayment) {
        ObjectNode node = mapper.createObjectNode();
        node.put("timestamp", splitPayment.getTimestamp());
        node.put("description", splitPayment.getDescription());
        node.put("currency", splitPayment.getCurrency().toString());
        node.put("splitPaymentType", splitPayment.getSplitPaymentType());
        if (splitPayment.getSplitPaymentType().equals("custom")) {
            ArrayNode transactionsArray = mapper.createArrayNode();
            for (Double amount : splitPayment.getAmountsForUsers()) {
                transactionsArray.add(amount);
            }
            node.put("amountForUsers", transactionsArray);
        } else {
            node.put("amount", splitPayment.getTotalAmount() / splitPayment.getInvolvedAccounts().size());
        }
        ArrayNode involvedAccountsArray = mapper.createArrayNode();
        for (String iban : splitPayment.getInvolvedAccounts()) {
            involvedAccountsArray.add(iban);
        }
        node.put("involvedAccounts", involvedAccountsArray);

        if (splitPayment.getError() != null) {
            node.put("error", splitPayment.getError());
        }
        transactionsArray.add(node);
    }

    /**
     * Viziteaza o tranzactie de stergere a unui cont si adauga informatia in JSON.
     *
     * @param deleteAccount tranzactia de stergere a unui cont.
     */
    @Override
    public void visit(final DeleteAccountTransaction deleteAccount) {
        ObjectNode node = mapper.createObjectNode();
        node.put("timestamp", deleteAccount.getTimestamp());
        node.put("description", deleteAccount.getDescription());
        transactionsArray.add(node);
    }

    /**
     * Viziteaza o tranzactie de modificare a ratei dobanzii si adauga informatia in JSON.
     *
     * @param changeInterestRate tranzactia de modificare a ratei dobanzii.
     */
    @Override
    public void visit(final ChangeInterestRateTransaction changeInterestRate) {
        ObjectNode node = mapper.createObjectNode();
        node.put("timestamp", changeInterestRate.getTimestamp());
        node.put("description", changeInterestRate.getDescription());
        transactionsArray.add(node);
    }

    @Override
    public void visit(MinimumAgeTransaction minimumAge) {
        ObjectNode node = mapper.createObjectNode();
        node.put("description", minimumAge.getDescription());
        node.put("timestamp", minimumAge.getTimestamp());
        transactionsArray.add(node);
    }

    @Override
    public void visit(UpgradePlanTransaction upgradePlan) {
        ObjectNode node = mapper.createObjectNode();
        node.put("accountIBAN", upgradePlan.getAccountIban());
        node.put("description", upgradePlan.getDescription());
        node.put("newPlanType", upgradePlan.getNewPlanType().toString().toLowerCase());
        node.put("timestamp", upgradePlan.getTimestamp());
        transactionsArray.add(node);
    }

    @Override
    public void visit(CashWithdrawalTransaction cashWithdrawal) {
        ObjectNode node = mapper.createObjectNode();
        node.put("description", cashWithdrawal.getDescription());
        node.put("timestamp", cashWithdrawal.getTimestamp());
        if (cashWithdrawal.getErrorMessage() == null) {
            node.put("amount", cashWithdrawal.getAmount());
        }
        transactionsArray.add(node);
    }

    @Override
    public void visit(AddInterestTransaction addInterestTransaction) {
        ObjectNode node = mapper.createObjectNode();
        node.put("timestamp", addInterestTransaction.getTimestamp());
        node.put("description", addInterestTransaction.getDescription());
        node.put("amount", addInterestTransaction.getAmount());
        node.put("currency", addInterestTransaction.getCurrency().toString());
        transactionsArray.add(node);
    }

    @Override
    public void visit(WithdrawSavingsTransaction withdrawSavingsTransaction) {
        ObjectNode node = mapper.createObjectNode();
        node.put("timestamp", withdrawSavingsTransaction.getTimestamp());
        node.put("description", withdrawSavingsTransaction.getDescription());
        if(!withdrawSavingsTransaction.isError()) {
            // TODO
        }
        transactionsArray.add(node);
    }
}
