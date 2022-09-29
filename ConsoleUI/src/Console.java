import dto.*;
import engine.EnigmaEngine;
import engine.Engine;
import javafx.util.Pair;
import problem.Problem;
import statistics.StatisticRecord;
import utill.Utility;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
/*
public class Console {
    private static final Engine engine = new EnigmaEngine();
    private static boolean isMachineLoaded = false;
    private static boolean isMachineConfigured = false;
    private static final Scanner scanner = new Scanner(System.in);

    private static final String MSG_WELCOME = "\nWelcome to Cracking The Enigma!!\n";
    private static final String MSG_PLEASE_LOAD_MACHINE = "Please load machine from a file before selecting further operation.";
    private static final String MSG_PLEASE_CONFIG_BEFORE_CIPHER = "Please enter configuration, before ciphering some text.";
    private static final String MSG_PLEASE_LOAD_MACHINE_AND_CONFIG_BEFORE_CIPHER = "Please load machine from file, and enter configuration\n  before ciphering some text.";
    private static final String MSG_PLEASE_CONFIG_BEFORE_RESET = "Please enter configuration, before resetting it.";
    private static final String MSG_PLEASE_LOAD_MACHINE_AND_CONFIG_BEFORE_RESET = "Please load machine from file, and enter configuration\n  before resetting it.";
    private static final String MSG_PLEASE_LOAD_MACHINE_BEFORE_STATS = "Please load machine from a file before checking for statistics.";

    public static void main(String[] args) {

    }

    /**
     * runs the Console UI
     */
    /*private static void run() {

        boolean isExit = false;
        System.out.println(MSG_WELCOME);

        printMainMenu();

        Operation choice = getInputUserChoice();

        while (!isExit) {
            try {
                switch (choice) {
                    case READ_SYSTEM_DETAILS_FROM_XML:
                        loadXmlFile();
                        break;
                    case DISPLAY_SPECS:
                        if (isMachineLoaded) {
                            displaySpecifications();
                        } else {
                            printMessageWithBorders(MSG_PLEASE_LOAD_MACHINE);
                        }
                        break;
                    case CHOOSE_INIT_CONFIG_MANUAL:
                        if (isMachineLoaded) {
                            chooseConfigManual();
                        } else {
                            printMessageWithBorders(MSG_PLEASE_LOAD_MACHINE);
                        }
                        break;
                    case CHOOSE_INIT_CONFIG_AUTO:
                        if (isMachineLoaded) {
                            chooseConfigAuto();
                        } else {
                            printMessageWithBorders(MSG_PLEASE_LOAD_MACHINE);
                        }
                        break;
                    case PROCESS_INPUT:
                        if (isMachineConfigured && isMachineLoaded) {
                            processInput();
                        } else if (isMachineLoaded) {
                            printMessageWithBorders(MSG_PLEASE_CONFIG_BEFORE_CIPHER);
                        } else {
                            printMessageWithBorders(MSG_PLEASE_LOAD_MACHINE_AND_CONFIG_BEFORE_CIPHER);
                        }
                        break;
                    case RESET_CURRENT_CODE:
                        if (isMachineConfigured) {
                            resetConfig();
                        } else if (isMachineLoaded) {
                            printMessageWithBorders(MSG_PLEASE_CONFIG_BEFORE_RESET);
                        } else {
                            printMessageWithBorders(MSG_PLEASE_LOAD_MACHINE_AND_CONFIG_BEFORE_RESET);
                        }
                        break;
                    case HISTORY_AND_STATISTICS:
                        if (isMachineLoaded) {
                            getHistoryAndStats();
                        } else {
                            printMessageWithBorders(MSG_PLEASE_LOAD_MACHINE_BEFORE_STATS);
                        }
                        break;
                    case EXIT_SYSTEM:
                        isExit = true;
                        continue;
                    case READ_EXISTING_MACHINE_FROM_FILE:
                        loadExistingMachineFromFile();
                        break;
                    case WRITE_EXISTING_MACHINE_TO_FILE:
                        if (isMachineLoaded) {
                            saveExistingMachineToFile();
                        } else {
                            printMessageWithBorders(MSG_PLEASE_LOAD_MACHINE);
                        }
                        break;
                    default:
                        break;
                }
                printMainMenu();
                choice = getInputUserChoice();
            } catch (Exception e) {
                printMessageWithBorders("An unknown error had occurred. Please try again.");
            }
        }
    }*/
/*
    /**
     * prints the menu operations
     *//*
    private static void printMainMenu() {
        System.out.println("\nSelect an operation:");
        System.out.println("1  -  Load System Details From File.\n" +
                "2  -  Display Enigma Machine Specifications.\n" +
                "3  -  Set Configuration - Manually.\n" +
                "4  -  Set Configuration - Automatically.\n" +
                "5  -  Cipher.\n" +
                "6  -  Reset Configuration.\n" +
                "7  -  History And Statistics.\n" +
                "8  -  Exit.\n" +
                "9  -  Load an existing machine.\n" +
                "10 -  Save an existing machine.\n");
    }

    /**
     * getting the user's choice from the menu
     *
     * @return Operation that represents the user's choice
     *//*
    private static Operation getInputUserChoice() {

        int userChoiceNum;

        try {
            String userChoiceStr = scanner.nextLine().trim();

            while (userChoiceStr.length() == 0) {
                printMessageWithBorders("No input was given.\n  Please enter your choice (select 1 - 10).");
                userChoiceStr = scanner.nextLine().trim();
            }

            userChoiceNum = Integer.parseInt(userChoiceStr);

            if (userChoiceNum < 1 || userChoiceNum > 10) {
                printMessageWithBorders("The option you've chosen does not exist.\n  Select an option from 1-10.");
                userChoiceNum = Operation.INVALID_OPERATION;
            }

        } catch (NumberFormatException e) {
            printMessageWithBorders("The option you've chosen is not a number.\n  Select an option from 1-10.");
            userChoiceNum = Operation.INVALID_OPERATION;
        }
        System.out.println("");


        return Operation.getOperation(userChoiceNum);
    }

    /**
     * asks the user to try again or go back to the menu
     *
     * @return true if wants to try again. false if wants to go back to the menu
     *//*
    private static boolean getInputWantToTryAgain() {

        String userChoice;

        System.out.println("Do you want to try again or go back to the menu?");
        System.out.println("1 - Try Again.\n2 - Back To The Menu.");

        userChoice = scanner.nextLine().trim();

        while (!userChoice.equals("1") && !userChoice.equals("2")) {
            System.out.println("Please enter '1' or '2', depending or your choice.");
            System.out.println("1 - Try Again.\n2 - Back To The Menu.");

            userChoice = scanner.nextLine().trim();
        }

        return userChoice.equals("1");
    }


    /**
     * get rotors ids from the user
     *
     * @param numberOfIntegers the rotors count
     * @return list of integers representing the rotor's id's
     *//*
    private static String getInputStringOfIntegers(int numberOfIntegers) {
        boolean isValid = false;
        String StringOfChoices;


        System.out.println("There is a place for " + numberOfIntegers + " rotors in this machine.");
        System.out.println("Enter the rotor's ID's. e.g - For 3 rotors, enter: ID,ID,ID");
        StringOfChoices = scanner.nextLine();

        while (!isValid) {
            if (StringOfChoices.length() != 0) {
                isValid = true;
                continue;
            }
            System.out.println("Error! Can't be 0 rotors. Try again.");
            StringOfChoices = scanner.nextLine();
        }

        return StringOfChoices;
    }

    /**
     * get alphabet characters from the user
     *
     * @return String of characters
     *//*
    private static String getInputSequenceOfCharacters() {

        boolean isValid = false;
        String stringOfChoices;

        System.out.println("Enter the characters that will appear at the window for each rotor.");
        System.out.println("e.g - For 3 rotors, enter: ABC");

        stringOfChoices = scanner.nextLine().toUpperCase();

        while (!isValid) {
            if (stringOfChoices.length() != 0) {
                isValid = true;
                continue;
            }
            System.out.println("Error! Can't be 0 rotors. Try again.");
            stringOfChoices = scanner.nextLine().toUpperCase();
        }

        return stringOfChoices;

    }

    /**
     * get reflector input from user.
     *
     * @return integer representing reflector id.
     *//*
    private static int getInputInteger() {
        boolean isValid = false;

        System.out.println("Enter the number of the wanted reflector (select 1-5).");
        System.out.println("1. I\n2. II\n3. III\n4. IV\n5. V\n");

        String choiceStr;
        int choiceNum;

        try {
            choiceStr = scanner.nextLine().trim();

            if (choiceStr.length() == 0) {
                return -2;
            }

            choiceNum = Integer.parseInt(choiceStr);
        } catch (NumberFormatException e) {
            return -1;
        }

        return choiceNum;
    }

    /**
     * get the plugs from the user.
     *
     * @return a list of strings, each string contains 2 characters representing a Plug.
     *//*
    private static String getInputListOfStrings() {
        boolean isValid = false;
        String plugs = "";

        while (!isValid) {
            System.out.println("Please enter your chosen plugs, with no spaces or separators.");
            System.out.println("e.g - For 2 plugs, A to B and E to D, enter: ABED");
            plugs = scanner.nextLine().toUpperCase();
            isValid = true;
        }

        return plugs;
    }

    /**
     * get text to cipher from user
     *
     * @return string of text.
     *//*
    private static String getInputCipherText() {

        boolean isValid = false;
        String cipherText = scanner.nextLine().toUpperCase();

        while (!isValid) {
            if (cipherText.length() == 0) {
                System.out.println("Error! No input was given.");
                cipherText = scanner.nextLine().toUpperCase();
            } else {
                isValid = true;
            }
        }

        return cipherText;
    }

    /**
     * get fileName from user.
     *
     * @return string represents the file name.
     *//*
    private static String getXMLFileName() {
        System.out.println("Enter the full path of the file to load machine from (XML file):");

        return scanner.nextLine();
    }

    /**
     * Q1 - loads xml file and builds the enigma machine.
     *//*
    private static void loadXmlFile() {
        String xmlFileName = getXMLFileName();
        DTOstatus buildMachineFromXMLStatus = engine.buildMachineFromXmlFile(xmlFileName);

        if (!buildMachineFromXMLStatus.isSucceed()) {
            displayMessage(buildMachineFromXMLStatus.getDetails());
        } else {
            isMachineLoaded = true;
            isMachineConfigured = false;
            printMessageWithBorders("The machine was built successfully!");
        }
    }

    /**
     * Q2 - display the machine specifications.
     *//*
    private static void displaySpecifications() {
        DTOspecs specsStatus = engine.displayMachineSpecifications();
        if (specsStatus.isSucceed()) {
            printSpecifications(specsStatus);
        }
    }

    /**
     * Q3 - choose new config manually.
     *//*
    private static void chooseConfigManual() {
        boolean isWantToTryAgain;
        int rotorCount = engine.getRotorsCount();

        String rotorsIDs = getInputStringOfIntegers(rotorCount);
        DTOstatus rotorsIDsStatus = engine.validateRotors(rotorsIDs);
        while (!rotorsIDsStatus.isSucceed()) {

            displayMessage(rotorsIDsStatus.getDetails());

            isWantToTryAgain = getInputWantToTryAgain();
            if (isWantToTryAgain) {
                rotorsIDs = getInputStringOfIntegers(rotorCount);
                rotorsIDsStatus = engine.validateRotors(rotorsIDs);
            } else {
                return;
            }

        }
        printMessageWithBorders("All good.");
        System.out.println("\n");

        String windows = getInputSequenceOfCharacters();
        DTOstatus windowCharactersStatus = engine.validateWindowCharacters(windows);
        while (!windowCharactersStatus.isSucceed()) {

            displayMessage(windowCharactersStatus.getDetails());
            isWantToTryAgain = getInputWantToTryAgain();
            if (isWantToTryAgain) {
                windows = getInputSequenceOfCharacters();
                windowCharactersStatus = engine.validateWindowCharacters(windows);
            } else {
                return;
            }

        }
        printMessageWithBorders("All good.");
        System.out.println("\n");

        int reflectorID = getInputInteger();
        DTOstatus reflectorIDStatus = engine.validateReflector(reflectorID);
        while (!reflectorIDStatus.isSucceed()) {

            displayMessage(reflectorIDStatus.getDetails());
            isWantToTryAgain = getInputWantToTryAgain();
            if (isWantToTryAgain) {
                reflectorID = getInputInteger();
                reflectorIDStatus = engine.validateReflector(reflectorID);
            } else {
                return;
            }

        }
        printMessageWithBorders("All good.");
        System.out.println("\n");

        String plugs = getInputListOfStrings();
        DTOstatus plugsStatus = engine.validatePlugs(plugs);
        while (!plugsStatus.isSucceed()) {

            displayMessage(plugsStatus.getDetails());
            isWantToTryAgain = getInputWantToTryAgain();
            if (isWantToTryAgain) {
                plugs = getInputListOfStrings();
                plugsStatus = engine.validatePlugs(plugs);
            } else {
                return;
            }

        }
        printMessageWithBorders("All good.");
        System.out.println("\n");

        DTOstatus configStatus = engine.selectConfigurationManual(rotorsIDs, windows, reflectorID, plugs);
        if (configStatus.isSucceed()) {
            printMessageWithBorders("Machine has been configured successfully.");
            isMachineConfigured = true;
        }
    }

    /**
     * Q4 - choose new config automatically.
     *//*
    static private void chooseConfigAuto() {
        DTOsecretConfig configStatus = engine.selectConfigurationAuto();
        isMachineConfigured = true;

        if (!configStatus.isSucceed()) {
            displayMessage(configStatus.getDetails());
        } else {
            System.out.println("\nSelected Configuration:");
            printConfiguration(configStatus.getRotors(), configStatus.getWindows(),
                    configStatus.getReflectorSymbol(), configStatus.getPlugs(), configStatus.getNotchDistances());
        }
    }

    /**
     * Q5 - gets input from user and send it to machine to cipher.
     *//*
    static private void processInput() {
        printMessageWithBorders("Please enter text to cipher:");
        String inputText = getInputCipherText();
        DTOciphertext cipherStatus = engine.cipherInputText(inputText);
        if (!cipherStatus.isSucceed()) {
            displayMessage(cipherStatus.getDetails());
        } else {
            System.out.println();
            System.out.println("--> " + cipherStatus.getCipheredText());

        }
    }

    /**
     * Q6 - reset machine config to it's original last config.
     *//*
    static private void resetConfig() {
        DTOresetConfig resetStatus = engine.resetConfiguration();
        if (resetStatus.isSucceed()) {
            printMessageWithBorders("Configuration has reset successfully.");
        }
    }

    /**
     * Q7 - gets history and stats from the machine and display to user.
     *//*
    static private void getHistoryAndStats() {
        DTOstatistics statisticsStatus = engine.getHistoryAndStatistics();
        if (!statisticsStatus.isSucceed()) {
            displayMessage(statisticsStatus.getDetails());
        } else {
            System.out.println("Machine's History And Statistics:\n");
            printStatistics(statisticsStatus);
        }
    }

    /**
     * display message for the user.
     *
     * @param problem a detailed enum representing a problem at the code.
     *//*
    private static void displayMessage(Problem problem) {
        String msg = "";
        switch (problem) {
            case ROTOR_INPUT_NOT_ENOUGH_ELEMENTS:
                msg = "You've entered less IDs than expected.";
                break;
            case ROTOR_INPUT_TOO_MANY_ELEMENTS:
                msg = "You've entered more IDs than expected.";
                break;
            case ROTOR_INPUT_OUT_OF_RANGE_ID:
                msg = "One or more of the IDs you've entered\n  doesn't exist in this machine.";
                break;
            case ROTOR_DUPLICATION:
                msg = "You have entered the same rotor twice or more.";
                break;
            case ROTOR_INPUT_HAS_SPACE:
                msg = "You have entered a space somewhere in the rotors ids.";
                break;
            case ROTOR_INPUT_NUMBER_FORMAT_EXCEPTION:
                msg = "At least one of the IDs you've entered isn't a number.";
                break;
            case ROTOR_VALIDATE_EMPTY_STRING:
                msg = "You haven't entered any IDs.";
                break;
            case ROTOR_VALIDATE_NO_SEPERATOR:
                msg = "You haven't seperated all the IDs with a \",\".";
                break;
            case NO_CONFIGURATION:
                msg = "The machine's configuration hasn't been set yet.";
                break;
            case SELF_PLUGGING:
                msg = "One or more of the plugs you've entered is a 'self plug' \n  (e.g AA, DD..).";
                break;
            case ALREADY_PLUGGED:
                msg = "Among the plugs you've entered, at least one input leads \n  to multiple outputs. or multiple inputs lead to one output.\n  for example (AB and AC), or (DR and FR).";
                break;
            case PLUGS_INPUT_NOT_IN_ALPHABET:
                msg = "Among the plugs you've entered, at least one character \n  is not in the alphabet.";
                break;
            case PLUGS_INPUT_ODD_ALPHABET_AMOUNT:
                msg = "You've entered an odd number of characters.";
                break;
            case WINDOW_INPUT_TOO_MANY_LETTERS:
                msg = "You've entered more characters than expected.";
                break;
            case WINDOW_INPUT_TOO_FEW_LETTERS:
                msg = "You've entered less characters than expected.";
                break;
            case WINDOWS_INPUT_NOT_IN_ALPHABET:
                msg = "Among the characters you've entered, at least one character \n  is not in the alphabet.";
                break;
            case REFLECTOR_INPUT_OUT_OF_RANGE_ID:
                msg = "The ID you've entered doesn't exist in this machine.";
                break;
            case REFLECTOR_INPUT_EMPTY_STRING:
                msg = "You didn't enter any input.";
                break;
            case REFLECTOR_INPUT_NOT_A_NUMBER:
                msg = "The ID you've entered isn't a number.";
                break;
            case FILE_NOT_FOUND:
                msg = "The file you've requested couldn't be found.";
                break;
            case JAXB_ERROR:
                msg = "Could not use this file. The XML file you've requested is not \n  a valid enigma machine file.";
                break;
            case FILE_NOT_IN_FORMAT:
                msg = "The file you've requested isn't in the required format \n  (only .xml files are valid).";
                break;
            case FILE_ODD_ALPHABET_AMOUNT:
                msg = "This file is invalid. The amount of the alphabet letters is odd. \n  Only an even number of letters is allowed.";
                break;
            case FILE_NOT_ENOUGH_ROTORS:
                msg = "This file is invalid. The machine requires more rotors. \n  than available ones.";
                break;
            case FILE_ROTORS_COUNT_BELOW_TWO:
                msg = "This file is invalid. The number of places for rotors inside \n  the machine, (referred to \"rotors count\" in the file), is \n  smaller than 2. Machines should have at least 2 rotors.";
                break;
            case FILE_ROTOR_COUNT_HIGHER_THAN_99:
                msg = "This file is invalid. The number of rotors count is higher \n  than the machine supports. (referred to \"rotors count\" in the file).";
                break;
            case FILE_ROTOR_MAPPING_NOT_IN_ALPHABET:
                msg = "This file is invalid. Among the mappings of the rotors, \n  at least one character is not in the alphabet.";
                break;
            case FILE_ROTOR_MAPPING_DUPPLICATION:
                msg = "This file is invalid. Among the mappings of the rotors, \n  at least one rotor has duplicate mapping.";
                break;
            case FILE_ROTOR_MAPPING_NOT_EQUAL_TO_ALPHABET_LENGTH:
                msg = "This file is invalid. Among the mappings of the rotors, \n  at least one rotor has more or less mappings \n  then the alphabet characters.";
                break;
            case FILE_ROTOR_MAPPING_NOT_A_SINGLE_LETTER:
                msg = "This file is invalid. Among the mapping of the rotors, \n  at lease one entry doesn't contains a single letter \n  (e.g, contains more than one letter like \"ABC\" --> \"F\"...).";
                break;
            case FILE_ROTOR_INVALID_ID_RANGE:
                msg = "The Rotors at the loaded file aren't maintaining a \n  \"running-counter\" ids from 1.";
                break;
            case FILE_OUT_OF_RANGE_NOTCH:
                msg = "There is a at-least one rotor at the file loaded, \n  that has a notch position that is in an unreachable location. ";
                break;
            case FILE_NUM_OF_REFLECTS_IS_NOT_HALF_OF_ABC:
                msg = "There is a reflector in the file that is loaded that does not have \n  exactly half the length of the alphabet the amount of reflects.";
                break;
            case FILE_REFLECTOR_INVALID_ID_RANGE:
                msg = "There is a reflector in the loaded file that has an ID \n  that is not supported by this machine.";
                break;
            case FILE_REFLECTOR_SELF_MAPPING:
                msg = "At-least one reflector has a self mapping row in it.";
                break;
            case FILE_REFLECTOR_MAPPING_DUPPLICATION:
                msg = "At-least one reflector has a duplicate mapping in it.";
                break;
            case FILE_REFLECTOR_MAPPING_NOT_IN_ALPHABET:
                msg = "At-least one reflector has an invalid mapping in it.";
                break;
            case FILE_REFLECTOR_ID_DUPLICATIONS:
                msg = "There is 2 or more reflectors at the loaded file with the same id.";
                break;
            case FILE_TOO_MANY_REFLECTORS:
                msg = "There is more then 5 reflectors at the file that was loaded.";
                break;
            case FILE_REFLECTOR_OUT_OF_RANGE_ID:
                msg = "One of the reflectors at the loaded-file \n  is out of possible range of ids.";
                break;
            case CIPHER_INPUT_NOT_IN_ALPHABET:
                msg = "At-least one letter isn't on the alphabet on this machine.";
                break;
            case FILE_EXISTING_LOAD_FAILED:
                msg = "Loading an existing machine from this file has failed. \n  Please use a different file.";
                break;
            case UNKNOWN:
                msg = "There is an unknown error.";
                break;
            case NO_PROBLEM:
                msg = "All OK.";
                break;
        }

        printMessageWithBorders(msg);
    }

    /**
     * print the specs to the user as required.
     *
     * @param dtoSpecs the DTOspecs object, containing the specs of the machine.
     *//*
    private static void printSpecifications(DTOspecs dtoSpecs) {
        StringBuilder strSpecs = new StringBuilder();

        strSpecs.append("\nSpecifications: \n")
                .append(" - Number Of Rotors (In Use / Available): ")
                .append(dtoSpecs.getInUseRotorsCount())
                .append(" / ")
                .append(dtoSpecs.getAvailableRotorsCount())
                .append('\n')
                .append(" - Number Of Reflectors (Available): ")
                .append(dtoSpecs.getAvailableReflectorsCount())
                .append('\n')
                .append(" - Number Of Texts That Were Ciphered So Far: ")
                .append(dtoSpecs.getCipheredTextsCount())
                .append('\n');

        System.out.println(strSpecs);

        if (dtoSpecs.getDetails().equals(Problem.NO_CONFIGURATION)) {
            System.out.println(" - No configuration has been chosen yet.");
        } else {
            System.out.print(" - Original Configuration: ");
            printConfiguration(dtoSpecs.getInUseRotorsIDs(), dtoSpecs.getOriginalWindowsCharacters(), dtoSpecs.getInUseReflectorSymbol(), dtoSpecs.getInUsePlugs(), dtoSpecs.getOriginalNotchPositions());
            System.out.print(" - Current Configuration:  ");
            printConfiguration(dtoSpecs.getInUseRotorsIDs(), dtoSpecs.getCurrentWindowsCharacters(), dtoSpecs.getInUseReflectorSymbol(), dtoSpecs.getInUsePlugs(), dtoSpecs.getNotchDistancesToWindow());
        }
    }

    /**
     * print the secret (configuration) to the user.
     *
     * @param inUseRotorsIDs         a list of the rotors ids
     * @param windowsCharacters      a String of window characters
     * @param inUseReflectorSymbol   a String of the reflector symbol
     * @param inUsePlugs             a String of the plugs in use
     * @param notchDistancesToWindow a list of the current notch positions
     *//*
    private static void printConfiguration(List<Integer> inUseRotorsIDs, String windowsCharacters, String inUseReflectorSymbol, String inUsePlugs, List<Integer> notchDistancesToWindow) {
        StringBuilder strConfig = new StringBuilder();

        // prints rotors' configuration
        // including the in use rotors' IDs, their order, and the current notch distances from the windows
        strConfig.append("<");
        for (int i = inUseRotorsIDs.size() - 1; i >= 0; i--) {
            strConfig.append(inUseRotorsIDs.get(i).toString());
            if (notchDistancesToWindow.size() > 0) {
                strConfig.append("(")
                        .append(notchDistancesToWindow.get(i).toString())
                        .append(")");
            }
            if (i != 0) {
                strConfig.append(",");
            }
        }
        strConfig.append(">");

        // prints windows characters configuration
        strConfig.append("<");
        for (int i = windowsCharacters.length() - 1; i >= 0; i--) {
            strConfig.append(windowsCharacters.charAt(i));
        }
        strConfig.append(">");

        // prints reflector configuration
        strConfig.append("<")
                .append(inUseReflectorSymbol)
                .append(">");

        // prints plugs configuration
        if (inUsePlugs.length() > 0) {
            strConfig.append("<");
            for (int i = 0; i < inUsePlugs.length(); i += 2) {
                strConfig.append(inUsePlugs.charAt(i))
                        .append("|")
                        .append(inUsePlugs.charAt(i + 1));
                if (i != inUsePlugs.length() - 2) {
                    strConfig.append(",");
                }
            }
            strConfig.append(">");
        }

        System.out.println(strConfig);
    }

    /**
     * print all machine stats
     *
     * @param stats DTOstatistics object that contains machine records
     *//*
    private static void printStatistics(DTOstatistics stats) {


        if (stats.getStats().size() == 0) {
            System.out.println("No statistics yet.");
        }

        for (StatisticRecord record : stats.getStats()) {

            StringBuilder cipherRecordStr = new StringBuilder();

            printConfiguration(record.getInUseRotors(), record.getWindowCharacters(),
                    Utility.decimalToRoman(record.getReflectorID()), record.getPlugs(), record.getOriginalNotchPositions());

            for (Pair<Pair<String, String>, Long> currentCipherRecord : record.getCipherHistory()) {
                cipherRecordStr.append("#. ")
                        .append("<")
                        .append(currentCipherRecord.getKey().getKey())
                        .append(">").append(" --> ").append("<")
                        .append(currentCipherRecord.getKey().getValue()).append(">")
                        .append(" (").append(currentCipherRecord.getValue()).append(" nano-seconds)").append("\n");
            }

            System.out.println(cipherRecordStr);
        }
    }

    /**
     * gets a file name for an existing machine
     *
     * @return a String of the file's name
     *//*
    private static String getExistingMachineFileName() {
        System.out.println("Enter the name of the file of the existing machine: ");

        return scanner.nextLine();
    }

    /**
     * loads an existing machine from a file
     *//*
    private static void loadExistingMachineFromFile() {
        String fileName = getExistingMachineFileName();
        DTOstatus loadExistingMachineStatus;
        try {
            loadExistingMachineStatus = engine.loadExistingMachineFromFile(fileName);

            if (!loadExistingMachineStatus.isSucceed()) {
                displayMessage(loadExistingMachineStatus.getDetails());
            } else {
                isMachineLoaded = true;
                if (engine.getIsMachineConfigured()) {
                    isMachineConfigured = true;
                }
                printMessageWithBorders("Machine has been loaded successfully.");
            }
        } catch (IOException | ClassNotFoundException e) {
            printMessageWithBorders("Couldn't load machine from this file. try again with another file.");
        }
    }

    /**
     * saves an existing machine to file
     *//*
    private static void saveExistingMachineToFile() {
        String fileName = getXMLFileName();
        try {
            DTOstatus saveExistingMachineStatus = engine.saveExistingMachineToFile(fileName);
            if (saveExistingMachineStatus.isSucceed()) {
                printMessageWithBorders("Machine has been saved successfully.");
            }
        } catch (IOException e) {
            printMessageWithBorders("Couldn't save machine to this file. try again with another file.");
        }
    }

    /**
     * print a messege with borders
     *
     * @param msg the messege to print
     *//*
    private static void printMessageWithBorders(String msg) {
        printBorder(msg);
        System.out.println("  " + msg);
        printBorder(msg);
    }

    /**
     * prints a border in the size of a messege
     *
     * @param msg the messege
     *//*
    private static void printBorder(String msg) {
        StringBuilder myStr = new StringBuilder();
        myStr.append('|');
        final int BORDER_SIZE = 80;
        for (int i = 0; i < Math.min(BORDER_SIZE, msg.length() + 2); i++) {
            myStr.append('-');
        }
        myStr.append('|');
        System.out.println(myStr);
    }*/
/*}
 */