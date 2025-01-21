package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.banking.Bank;
import org.poo.banking.Currency;
import org.poo.banking.Graph;
import org.poo.banking.User;
import org.poo.checker.Checker;
import org.poo.checker.CheckerConstants;
import org.poo.commands.*;
import org.poo.commerciants.Commerciant;
import org.poo.fileio.*;
import org.poo.observer.PlanUpgradeObserver;
import org.poo.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        var sortedFiles = Arrays.stream(Objects.requireNonNull(directory.listFiles())).
                sorted(Comparator.comparingInt(Main::fileConsumer))
                .toList();

        for (File file : sortedFiles) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * Functia de creare de useri.
     * @param users Vectorul de useri.
     */
    public static void createUsers(final UserInput[] users) {
        Bank bank = Bank.getInstance();
        for (UserInput userInput : users) {
            User user = new User(userInput.getFirstName(),
                    userInput.getLastName(), userInput.getEmail(),
                    userInput.getBirthDate(), userInput.getOccupation());

            user.addObserver(new PlanUpgradeObserver());
            bank.addUser(user);
        }
    }

    public static void createCommerciants(final CommerciantInput[] commerciants) {
        Bank bank = Bank.getInstance();
        for (CommerciantInput commerciantInput : commerciants) {
            Commerciant commerciant = new Commerciant(commerciantInput.getCommerciant(),
                    commerciantInput.getId(), commerciantInput.getAccount(),
                    commerciantInput.getType(), commerciantInput.getCashbackStrategy()
            );
            bank.addCommerciant(commerciant);
        }
    }

    /**
     * Functia care executa fiecare comanda in parte.
     */
    private static Command createCommand(final CommandInput commandInput,
                                         final Bank bank,
                                         final ArrayNode output,
                                         final ObjectMapper mapper,
                                         final Graph<Currency> currencyGraph,
                                         final int timestamp) {
        switch (commandInput.getCommand()) {
            case "printUsers":
                return new PrintUsersCommand();

            case "addAccount":
                return new AddAccountCommand(
                        commandInput.getEmail(),
                        commandInput.getAccountType(),
                        commandInput.getCurrency(),
                        commandInput.getInterestRate()
                );

            case "createCard":
                return new CreateCardCommand(
                        commandInput.getEmail(),
                        commandInput.getAccount()
                );

            case "createOneTimeCard":
                return new CreateOneTimeCardCommand(
                        commandInput.getEmail(),
                        commandInput.getAccount()
                );

            case "addFunds":
                return new AddFundsCommand(
                        commandInput.getAccount(),
                        commandInput.getAmount(),
                        commandInput.getEmail()
                );

            case "deleteAccount":
                return new DeleteAccountCommand(
                        commandInput.getEmail(),
                        commandInput.getAccount()
                );

            case "deleteCard":
                return new DeleteCardCommand(
                        commandInput.getEmail(),
                        commandInput.getCardNumber()
                );

            case "payOnline":
                return new PayOnlineCommand(
                        commandInput.getCardNumber(),
                        commandInput.getAmount(),
                        commandInput.getCurrency(),
                        commandInput.getDescription(),
                        commandInput.getCommerciant(),
                        commandInput.getEmail(),
                        currencyGraph
                );

            case "sendMoney":
                return new SendMoneyCommand(
                        commandInput.getEmail(),
                        commandInput.getAccount(),
                        commandInput.getReceiver(),
                        commandInput.getDescription(),
                        commandInput.getAmount(),
                        currencyGraph
                );

            case "printTransactions":
                return new PrintTransactionCommand(
                        commandInput.getEmail()
                );

            case "setMinimumBalance":
                return new SetMinimumBalanceCommand(
                        commandInput.getAccount(),
                        commandInput.getMinBalance()
                );

            case "checkCardStatus":
                return new CheckCardStatusCommand(
                        commandInput.getCardNumber()
                );

            case "splitPayment":
                return new SplitPaymentCommand(
                        commandInput.getAccounts(),
                        commandInput.getAmount(),
                        commandInput.getSplitPaymentType(),
                        commandInput.getAmountForUsers(),
                        commandInput.getCurrency(),
                        currencyGraph
                );

            case "report":
                return new ReportCommand(
                        commandInput.getAccount(),
                        commandInput.getStartTimestamp(),
                        commandInput.getEndTimestamp()
                );

            case "spendingsReport":
                return new SpendingsReportCommand(
                        commandInput.getAccount(),
                        commandInput.getStartTimestamp(),
                        commandInput.getEndTimestamp()
                );

            case "changeInterestRate":
                return new ChangeInterestRateCommand(
                        commandInput.getInterestRate(),
                        commandInput.getAccount()
                );

            case "addInterest":
                return new AddInterestCommand(
                        commandInput.getInterestRate(),
                        commandInput.getAccount()
                );

            case "withdrawSavings":
                return new WithdrawSavingsCommand(
                        commandInput.getAccount(),
                        commandInput.getAmount(),
                        commandInput.getCurrency()
                );

            case "upgradePlan":
                return new UpgradePlanCommand(
                        commandInput.getAccount(),
                        commandInput.getNewPlanType(),
                        currencyGraph
                );
            case "cashWithdrawal":
                return new CashWithdrawalCommand(
                        commandInput.getCardNumber(),
                        commandInput.getAmount(),
                        commandInput.getEmail(),
                        commandInput.getLocation(),
                        currencyGraph
                );
            case "acceptSplitPayment":
                return new AcceptSplitPaymentCommand(
                        commandInput.getEmail(),
                        commandInput.getSplitPaymentType()
                );
            case "rejectSplitPayment":
                return new RejectSplitPaymentCommand(
                        commandInput.getEmail(),
                        commandInput.getSplitPaymentType()
                );
            case "addNewBusinessAssociate":
                return new AddNewBusinessAssociateCommand(
                        commandInput.getAccount(),
                        commandInput.getRole(),
                        commandInput.getEmail()
                );

            case "changeSpendingLimit":
                return new ChangeSpendingLimitCommand(
                        commandInput.getEmail(),
                        commandInput.getAccount(),
                        commandInput.getAmount(),
                        commandInput.getTimestamp()
                );
            case "businessReport":
                return new BusinessReportCommand(
                        commandInput.getType(),
                        commandInput.getAccount(),
                        commandInput.getStartTimestamp(),
                        commandInput.getEndTimestamp(),
                        commandInput.getTimestamp()
                );
            case "changeDepositLimit":
                return new ChangeDepositLimitCommand(
                        commandInput.getEmail(),
                        commandInput.getAccount(),
                        commandInput.getAmount(),
                        commandInput.getTimestamp()
                );
            default:
                return null;
        }
    }


    /**
     * Executa fiecare comanda.
     */
    public static void executeCommands(final CommandInput[] commands,
                                       final ExchangeInput[] exchangeRates,
                                       final CommerciantInput[] commerciantInputs,
                                       final ArrayNode output, final ObjectMapper mapper) {
        Bank bank = Bank.getInstance();

        Graph<Currency> currencyGraph = new Graph<>();

        for (ExchangeInput exchangeInput : exchangeRates) {
            Currency fromCurrency = Currency.valueOf(exchangeInput.getFrom());
            Currency toCurrency = Currency.valueOf(exchangeInput.getTo());
            double rate = exchangeInput.getRate();

            currencyGraph.addEdge(fromCurrency, toCurrency, rate);

            if (rate != 0) {
                currencyGraph.addEdge(toCurrency, fromCurrency, 1.0 / rate);
            }
        }

        bank.setCurrencyGraph(currencyGraph);

        for (CommandInput commandInput : commands) {
            Command command = createCommand(commandInput, bank,
                    output, mapper, currencyGraph, commandInput.getTimestamp());
            try {
                if (command != null) {
                    command.execute(bank, output, mapper, commandInput.getTimestamp());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        System.out.println("Processing: " + filePath1);
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(CheckerConstants.TESTS_PATH + filePath1);
        ObjectInput inputData = objectMapper.readValue(file, ObjectInput.class);
        createUsers(inputData.getUsers());

        createCommerciants(inputData.getCommerciants());

        ArrayNode output = objectMapper.createArrayNode();
        executeCommands(inputData.getCommands(), inputData.getExchangeRates(),
                inputData.getCommerciants(), output, objectMapper);

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
        Bank bank = Bank.getInstance();
        bank.reset();
        Utils.resetRandom();
    }

    /**
     * Method used for extracting the test number from the file name.
     *
     * @param file the input file
     * @return the extracted numbers
     */
    public static int fileConsumer(final File file) {
        String fileName = file.getName()
                .replaceAll(CheckerConstants.DIGIT_REGEX, CheckerConstants.EMPTY_STR);
        return Integer.parseInt(fileName.substring(0, 2));
    }
}
