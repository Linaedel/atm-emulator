package ru.sbrf.ku.atm.vm.impl;

import ru.sbrf.ku.atm.ATMRuntimeException;
import ru.sbrf.ku.atm.Nominal;
import ru.sbrf.ku.atm.atm.ATM;
import ru.sbrf.ku.atm.atm.ATMService;
import ru.sbrf.ku.atm.vm.ViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ViewModelImpl implements ViewModel {
    private final ATM atm;
    private String configFilepath;
    private Scanner scanner;
    private String operation;

    public ViewModelImpl(ATM atm) {
        this.atm = atm;
        this.scanner = new Scanner(System.in);
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void setConfigurationPath(String configFilepath) {
        this.configFilepath = configFilepath;
    }

    @Override
    public void startClientInteraction() {
        System.out.println("Hello, my dear friend. What's your name?");
        String name = readNotBlank(scanner);
        System.out.print("Hello " + name + "! ");
        String next = "";
        do  {
            System.out.println("What's your "+next+"command? (add, get, exit)");
            operation = readNotBlank(scanner);
            switch (operation.toLowerCase()) {
                case "add":
                    try {
                        addCash();
                    } catch (ATMRuntimeException ignored) { }
                    break;
                case "get":
                    try {
                        getCash();
                    } catch (ATMRuntimeException ignored) { }
                    break;
                default:
                    System.out.println("Incorrect command");
            }
            next = "next ";
        } while (!"exit".equalsIgnoreCase(operation));
        stopClientInteraction();
    }

    private void addCash(){
        String reply;
        do {
            System.out.println("Add all banknotes, separated with space, please. Type (return) to return to previous step.");
            operation = readNotBlank(scanner);
            if ("return".equalsIgnoreCase(operation)){
                throw new ATMRuntimeException("Return");
            }
            List<Nominal> banknotesList = getBanknotesList(operation);
            if (banknotesList.size() != 0) {
                atm.putCash(banknotesList);
                reply = "Banknotes added successfully. Wish to add more? Type (yes) to continue.";
            } else {
                reply = "Wish to try again? Type (yes) to repeat.";
            }
            System.out.println(reply);
            operation = returnIfNotGiven(scanner, "yes");
        } while ("yes".equalsIgnoreCase(operation));
    }

    private void getCash() {
        String reply = "";
        do {
            System.out.println("How much do you want to withdraw? Type (return) to return to previous step.");
            operation = readNotBlank(scanner);
            if ("return".equalsIgnoreCase(operation)){
                throw new ATMRuntimeException("Return");
            }
            int withdrawalAmount = 0;
            try {
                withdrawalAmount = Integer.parseInt(operation.trim());
            } catch (NumberFormatException e) {
                System.err.println(operation + " is not a valid amount. Only amount, set with digital input, allowed.");
                reply = "Wish to try again? Type (yes) to repeat." + System.lineSeparator();
            }
            if (withdrawalAmount != 0) {
                try {
                    List<Nominal> banknotesList = atm.getCash(withdrawalAmount);
                    System.out.println("Take your cash, please!");
                    for (Nominal nominal : banknotesList) {
                        System.out.println("["+nominal.getNominal()+"]");
                    }
                    throw new ATMRuntimeException("Return");
                } catch (ATMRuntimeException e) {
                    if("Return".equalsIgnoreCase(e.getMessage())){
                        throw e;
                    }
                    System.err.println(e.getMessage());
                    reply = "Wish to try again? Type (yes) to repeat." + System.lineSeparator();
                }

            }
            System.out.print(reply);
            operation = returnIfNotGiven(scanner,"yes");
        } while (!"".equals(reply) && "yes".equalsIgnoreCase(operation));
    }

    @Override
    public void stopClientInteraction(){
        try {
            ((ATMService) atm).saveToFile(configFilepath);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    private List<Nominal> getBanknotesList(String incString) {
        List<Nominal> loadedBanknotesList = new ArrayList<>();
        String[] banknotesArr = incString.split("\\s");
        if (banknotesArr.length != 0) {
            for (String banknote : banknotesArr) {
                try {
                    Nominal nominal = Nominal.getNominalFromInt(Integer.parseInt(banknote.trim()));
                    loadedBanknotesList.add(nominal);
                } catch (ATMRuntimeException e) {
                    System.err.println(e.getMessage());
                } catch (NumberFormatException e) {
                    System.err.println("Sorry, " + banknote + " is not a valid banknote. Try another one.");
                }
            }
        } else {
            try {
                Nominal nominal = Nominal.getNominalFromInt(Integer.parseInt(incString.trim()));
                loadedBanknotesList.add(nominal);
            } catch (ATMRuntimeException e) {
                System.err.println(e.getMessage());
            } catch (NumberFormatException e) {
                System.err.println("Sorry, " + incString + " is not a valid banknote. Try another one.");
            }
        }
        if (loadedBanknotesList.size() == 0){
            System.err.println("There is no banknotes added to receiver. ");
        }
        return loadedBanknotesList;
    }

    private String readNotBlank(Scanner scanner) {
        String buffer = "";
        while ("".equals(buffer)){
            buffer = scanner.nextLine();
        }
        return buffer;
    }

    private String returnIfNotGiven(Scanner scanner, String checkedStr){
        String buffer = scanner.nextLine();
        if (!checkedStr.equalsIgnoreCase(buffer)){
            throw new ATMRuntimeException("Return");
        }
        return buffer;
    }

}
