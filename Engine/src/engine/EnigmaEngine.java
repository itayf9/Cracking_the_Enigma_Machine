package engine;

import dm.agent.AgentTask;
import dm.agent.DTOtasks;
import info.agent.AgentInfo;
import info.allie.AllieInfo;
import battlefield.Battlefield;
import info.battlefield.BattlefieldInfo;
import candidate.AgentConclusion;
import dm.decryptmanager.DecryptManager;
import dictionary.Dictionary;
import difficultylevel.DifficultyLevel;
import dto.*;
import javafx.util.Pair;
import jobprogress.JobProgressInfo;
import machine.EnigmaMachine;

import machine.Machine;
import machine.component.Reflector;
import machine.component.Rotor;
import machine.jaxb.generated.*;
import problem.Problem;
import statistics.StatisticRecord;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;


import static difficultylevel.DifficultyLevel.getDifficultyLevelFromString;
import static utill.Utility.*;


public class EnigmaEngine implements Engine {

    // The engine contains Machine instance and machine records object.
    private boolean charByCharState;
    private long currentCipherProcessTimeElapsed;
    private String currentCipherProcessOutputText;
    private String currentCipherProcessInputText;
    private final List<StatisticRecord> machineRecords;

    private final Map<String, Battlefield> uboatName2battleField;
    private final Map<String, Set<AgentInfo>> loggedAllieName2loggedAgents;
    private final Map<String, AgentInfo> agentName2agentInfo;

    private final Object waitingTasksQueueLock;
    private final Object conclusionQueueLock;

    public static String JAXB_XML_PACKAGE_NAME;

    public EnigmaEngine() {
        JAXB_XML_PACKAGE_NAME = "machine.jaxb.generated";
        this.charByCharState = false;
        this.machineRecords = new ArrayList<>();
        this.uboatName2battleField = new HashMap<>();
        this.loggedAllieName2loggedAgents = new HashMap<>();
        this.agentName2agentInfo = new HashMap<>();
        this.waitingTasksQueueLock = new Object();
        this.conclusionQueueLock = new Object();
    }

    /**
     * creating new machine instance using all the parts the machine needs.
     */
    public void buildMachine(List<Rotor> availableRotors, List<Reflector> availableReflectors, int rotorsCount, String alphabet, Map<Character,
            Integer> character2index, String userName) {
        //  currentCipherProcessOutputText = "";
        //  currentCipherProcessInputText = "";
        uboatName2battleField.get(userName).setMachine(new EnigmaMachine(availableRotors, availableReflectors, rotorsCount, alphabet, character2index));
    }

    /**
     * updating the current machine configurations.
     * based on String of input from the user.
     *
     * @param rotorsIDs    list of the wanted rotor's id's, sorted to the wanted order
     * @param windowsChars String of characters that represents the window characters for each wanted rotor
     * @param reflectorID  the id of the wanted reflector, as an integer
     * @param plugs        String that represents plugs, with no spaces or separators
     */
    public void updateConfiguration(List<Integer> rotorsIDs, String windowsChars, int reflectorID, String plugs, String userName) {

        // build windowOffsets
        List<Integer> windowOfssets = new ArrayList<>();

        for (int i = 0; i < windowsChars.length(); i++) {
            Rotor nextRotor = uboatName2battleField.get(userName).getMachine().getRotorByID(rotorsIDs.get(i));
            int offset = nextRotor.translateChar2Offset(windowsChars.charAt(i));
            windowOfssets.add(offset);
        }

        // sets the new configuration into the machine.
        uboatName2battleField.get(userName).getMachine().setMachineConfiguration(rotorsIDs, windowOfssets, reflectorID, plugs);

        // adds new configuration to statistical records.
        StatisticRecord newRecord = new StatisticRecord(rotorsIDs, windowsChars, reflectorID, plugs, uboatName2battleField.get(userName).getMachine().getOriginalNotchPositions());
        machineRecords.add(newRecord);
    }

    /**
     * ciphering text with the cipher method of "machine".
     *
     * @param toCipher a String of text to cipher
     * @return a String of the ciphered text
     */
    public String cipherText(String toCipher, String userName) {
        StringBuilder cipheredText = new StringBuilder();

        // goes through the character in the string
        for (Character currentChar : toCipher.toCharArray()) {
            cipheredText.append(uboatName2battleField.get(userName).getMachine().cipher(currentChar));
        }
        if (!charByCharState) {
            // increasing the cipher counter
            uboatName2battleField.get(userName).getMachine().advanceCipherCounter();
        }

        return cipheredText.toString();
    }

    /**
     * @return the rotors count of the machine
     */
    @Override
    public int getRotorsCount(String userName) {
        return uboatName2battleField.get(userName).getMachine().getRotorsCount();
    }

    /**
     * gets fileName from user and loads XML file to build a new machine.
     * then, builds the machine.
     *
     * @param fileContent string - name of xml file
     * @return DTOstatus object that describes the status of the operation
     */
    public DTOspecs buildMachineFromXmlFile(String fileContent, String userName) {
        boolean isSucceeded = true;
        Problem details;

        // checks if the file is in the .xml format.
        try {
            InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());
            CTEEnigma cteEnigma = deserializeFrom(inputStream);
            details = buildEnigmaFromCTEEnigma(cteEnigma, userName);
            if (details != Problem.NO_PROBLEM) {
                isSucceeded = false;
            }
        } catch (JAXBException e) {
            details = Problem.JAXB_ERROR;
            isSucceeded = false;
        }
        // resets the statistics when loading a new machine
        if (isSucceeded) {
            machineRecords.clear();
            return displayMachineSpecifications(userName);
        }

        // here we create an undefined DTO in order to send DTOSpecs with the original build problem.
        return new DTOspecs(false, details, 0, 0, new ArrayList<>(),
                new ArrayList<>(), 0, 0, new ArrayList<>(),
                "", "", "", "", "", "", new Dictionary("", ""));
    }

    /**
     * validates the CTEEngine
     *
     * @return detailed Problem if occurred. if valid, returns Problem.NO_PROBLEM
     */
    private Problem validateCTEEnigma(CTEEnigma cteEnigma) {
        Problem problem = Problem.NO_PROBLEM;
        CTEMachine cteMachine = cteEnigma.getCTEMachine();

        String abc = cteMachine.getABC().trim().toUpperCase();
        abc = convertXMLSpecialCharsInSeq(abc);

        // check for alphabet length to be even
        if (abc.length() % 2 == 1) {
            return Problem.FILE_ODD_ALPHABET_AMOUNT;
        }

        // check if rotors count is not higher than 99.
        if (cteMachine.getRotorsCount() > 99) {
            return Problem.FILE_ROTOR_COUNT_HIGHER_THAN_99;
        }

        // check if rotors count is less than available rotors.
        if (cteMachine.getCTERotors().getCTERotor().size() < cteMachine.getRotorsCount()) {
            return Problem.FILE_NOT_ENOUGH_ROTORS;
        }

        // check if rotors count is less than 2
        if (cteMachine.getRotorsCount() < 2) {
            return Problem.FILE_ROTORS_COUNT_BELOW_TWO;
        }

        // check if all rotors ids are being a running counting from 1-N
        Comparator<CTERotor> CTERotorComparator = Comparator.comparingInt(CTERotor::getId);
        List<CTERotor> cteRotors = cteMachine.getCTERotors().getCTERotor();
        cteRotors.sort(CTERotorComparator);
        for (int i = 0; i < cteRotors.size(); i++) {
            if (cteRotors.get(i).getId() != i + 1) {
                return Problem.FILE_ROTOR_INVALID_ID_RANGE;
            }
            // check notch positions in cteRotors
            if (cteRotors.get(i).getNotch() > abc.length() || cteRotors.get(i).getNotch() < 1) {
                return Problem.FILE_OUT_OF_RANGE_NOTCH;
            }
        }

        //check for duplicate mapping in every rotor.
        for (CTERotor currentRotor : cteRotors) {

            // init flags for every rotor we scan through
            List<Boolean> rotorRightMappingFlags = new ArrayList<>(Collections.nCopies(abc.length(), false));
            List<Boolean> rotorLeftMappingFlags = new ArrayList<>(Collections.nCopies(abc.length(), false));

            // check if the current rotor's mapping is at the size of the alphabet length
            if (currentRotor.getCTEPositioning().size() != abc.length()) {
                return Problem.FILE_ROTOR_MAPPING_NOT_EQUAL_TO_ALPHABET_LENGTH;
            }

            // goes through all positions
            for (CTEPositioning currentPosition : currentRotor.getCTEPositioning()) {

                if (currentPosition.getRight().toUpperCase().length() != 1 ||
                        currentPosition.getLeft().toUpperCase().length() != 1) {
                    return Problem.FILE_ROTOR_MAPPING_NOT_A_SINGLE_LETTER;
                } else if (abc.indexOf(currentPosition.getRight().toUpperCase().charAt(0)) == -1 ||
                        abc.indexOf(currentPosition.getLeft().toUpperCase().charAt(0)) == -1) {
                    return Problem.FILE_ROTOR_MAPPING_NOT_IN_ALPHABET;
                } else {
                    if (rotorRightMappingFlags.get(abc.indexOf(currentPosition.getRight().toUpperCase().charAt(0)))) {
                        return Problem.FILE_ROTOR_MAPPING_DUPPLICATION;
                    } else if (rotorLeftMappingFlags.get(abc.indexOf(currentPosition.getLeft().toUpperCase().charAt(0)))) {
                        return Problem.FILE_ROTOR_MAPPING_DUPPLICATION;
                    } else {
                        rotorRightMappingFlags.set(abc.indexOf(currentPosition.getRight().toUpperCase().charAt(0)), true);
                        rotorLeftMappingFlags.set(abc.indexOf(currentPosition.getLeft().toUpperCase().charAt(0)), true);
                    }
                }
            }
        }
        // if we got here safely then the rotor's mappings are OK!

        // check if all reflectors ids are being a running counting from 1-N
        List<CTEReflector> cteReflectors = cteMachine.getCTEReflectors().getCTEReflector();
        List<Boolean> reflectorIDFlags = new ArrayList<>(Collections.nCopies(5, false));

        // check for reflector count < 5
        if (cteReflectors.size() > 5) {
            return Problem.FILE_TOO_MANY_REFLECTORS;
        }

        // goes through all reflectors
        for (CTEReflector cteReflector : cteReflectors) {

            // init mapping booleans array to check duplicate mapping in reflector
            List<Boolean> reflectorMappingFlags = new ArrayList<>(Collections.nCopies(abc.length(), false));


            int currentID = romanToDecimal(cteReflector.getId());

            // fill reflectorID flag list
            if (currentID == NOT_VALID_ROMAN_TO_DECIMAL) {
                return Problem.FILE_REFLECTOR_OUT_OF_RANGE_ID;
            } else if (reflectorIDFlags.get(currentID - 1)) {
                return Problem.FILE_REFLECTOR_ID_DUPLICATIONS;
            } else {
                reflectorIDFlags.set(currentID - 1, true);
            }

            List<CTEReflect> cteReflectPairs = cteReflector.getCTEReflect();

            // check for number of reflect pairs in each reflector
            if (cteReflectPairs.size() != abc.length() / 2) {
                return Problem.FILE_NUM_OF_REFLECTS_IS_NOT_HALF_OF_ABC;
            }

            // check for self mapping in each reflector
            for (CTEReflect cteReflectPair : cteReflectPairs) {

                if (cteReflectPair.getInput() == cteReflectPair.getOutput()) {
                    return Problem.FILE_REFLECTOR_SELF_MAPPING;
                }
            }

            // check for duplicate mapping in each reflector
            for (CTEReflect cteReflectPair : cteReflectPairs) {
                try {
                    // run through all input if true then duplicate found
                    if (reflectorMappingFlags.get(cteReflectPair.getInput() - 1)) {
                        return Problem.FILE_REFLECTOR_MAPPING_DUPPLICATION;
                    } // run through all output if true then duplicate found
                    else if (reflectorMappingFlags.get(cteReflectPair.getOutput() - 1)) {
                        return Problem.FILE_REFLECTOR_MAPPING_DUPPLICATION;
                    } else {
                        // if false then set true
                        reflectorMappingFlags.set(cteReflectPair.getInput() - 1, true);
                        reflectorMappingFlags.set(cteReflectPair.getOutput() - 1, true);
                    }
                } catch (IndexOutOfBoundsException e) {
                    return Problem.FILE_REFLECTOR_MAPPING_NOT_IN_ALPHABET;
                }
            }
        }

        int firstFalse = reflectorIDFlags.indexOf(false);

        if (firstFalse != -1) {
            for (int i = firstFalse + 1; i < reflectorIDFlags.size(); i++) {
                if (reflectorIDFlags.get(i)) {
                    return Problem.FILE_REFLECTOR_INVALID_ID_RANGE;
                }
            }
        }

        CTEBattlefield battlefield = cteEnigma.getCTEBattlefield();

        // check for a valid difficulty level
        if (getDifficultyLevelFromString(battlefield.getLevel()).equals(DifficultyLevel.UNDEFINED)) {
            return Problem.INVALID_DIFFICULTY_LEVEL;
        }

        // check if battlefield name already exist
        String battleFieldName = battlefield.getBattleName();

        if (uboatName2battleField.entrySet().stream().anyMatch(stringBattlefieldEntry -> stringBattlefieldEntry.getValue().getBattlefieldName().equals(battleFieldName))) {
            return Problem.BATTLEFIELD_NAME_ALREADY_EXIST;
        }

        // check for number of allies
        if (battlefield.getAllies() < 1) {
            return Problem.ALLIES_COUNT_LESS_THAN_1;
        }

        return problem;
    }

    /**
     * unmarshalling the schema of provided XML file to create jaxb classes.
     * in order to build the machine from an XML file.
     *
     * @param in a InputStream representing the file source
     * @return a CTEEnigma object, representing a jaxb generated engine
     * @throws JAXBException for an error that occurred in the jaxb process
     */
    private static CTEEnigma deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (CTEEnigma) u.unmarshal(in);
    }

    /**
     * converts all data from jaxb classes to the normal classes.
     * a machine can be built only once per username.
     *
     * @param cteEnigma the engine generated from jaxb
     * @return a Problem describing the problem that occurred. if valid, returns Problem.NO_PROBLEM
     */
    private Problem buildEnigmaFromCTEEnigma(CTEEnigma cteEnigma, String userName) {

        if (uboatName2battleField.get(userName).isBattlefieldConfigured()) {
            return Problem.BATTLEFIELD_ALREADY_CONFIGURED;
        }

        Problem problem = validateCTEEnigma(cteEnigma);
        if (problem != Problem.NO_PROBLEM) {
            return problem;
        }

        CTEMachine cteMachine = cteEnigma.getCTEMachine();

        List<Rotor> availableRotors = new ArrayList<>();
        List<Reflector> availableReflectors = new ArrayList<>();
        int rotorsCount = cteMachine.getRotorsCount();

        // initializes alphabet and character-2-index map
        String alphabet = cteMachine.getABC().trim().toUpperCase();

        alphabet = convertXMLSpecialCharsInSeq(alphabet);

        Map<Character, Integer> character2index = new HashMap<>();
        for (int i = 0; i < alphabet.length(); i++) {
            character2index.put(alphabet.charAt(i), i);
        }

        // initializes rotors
        for (CTERotor cteRotor : cteMachine.getCTERotors().getCTERotor()) {

            int currentID = cteRotor.getId();
            int currentNotchIndex = cteRotor.getNotch() - 1;

            List<Integer> mapRotorForward = new ArrayList<>(Collections.nCopies(alphabet.length(), 0));
            List<Integer> mapRotorBackward = new ArrayList<>(Collections.nCopies(alphabet.length(), 0));
            Map<Character, Integer> forwardTranslatorChar2index = new HashMap<>();
            Map<Character, Integer> backwardTranslatorChar2index = new HashMap<>();
            int index = 0;

            // goes through all right positions to build the translators Char to index
            for (CTEPositioning ctePosition : cteRotor.getCTEPositioning()) {
                forwardTranslatorChar2index.put(ctePosition.getRight().toUpperCase().charAt(0), index);
                backwardTranslatorChar2index.put(ctePosition.getLeft().toUpperCase().charAt(0), index);
                index++;
            }

            int indexOfLeftSide = 0;

            // goes through all left positions to build the forwarding map
            // alert !! this is hard logic code, and it won't be readable for unworthy personal.
            for (CTEPositioning ctePosition : cteRotor.getCTEPositioning()) {
                int indexOfRightSide = forwardTranslatorChar2index.get(ctePosition.getLeft().toUpperCase().charAt(0));
                mapRotorForward.set(indexOfRightSide, indexOfLeftSide);
                indexOfLeftSide++;
            }

            indexOfLeftSide = 0;

            // goes through all right positions to build the backward map
            for (CTEPositioning ctePosition : cteRotor.getCTEPositioning()) {
                int indexOfRightSide = backwardTranslatorChar2index.get(ctePosition.getRight().toUpperCase().charAt(0));
                mapRotorBackward.set(indexOfRightSide, indexOfLeftSide);
                indexOfLeftSide++;
            }

            // creates a new rotor
            Rotor currentRotor = new Rotor(currentID, currentNotchIndex,
                    forwardTranslatorChar2index, alphabet.length(),
                    mapRotorForward, mapRotorBackward);

            availableRotors.add(currentRotor);
        }

        //initializes reflectors
        for (CTEReflector cteReflector : cteMachine.getCTEReflectors().getCTEReflector()) {

            int currentID = romanToDecimal(cteReflector.getId());

            List<Integer> reflectorMapping = new ArrayList<>(Collections.nCopies(alphabet.length(), 0));

            // creates the mapping of the reflector
            for (CTEReflect cteReflect : cteReflector.getCTEReflect()) {
                int input = cteReflect.getInput() - 1;
                int output = cteReflect.getOutput() - 1;
                reflectorMapping.set(input, output);
                reflectorMapping.set(output, input);
            }

            // creates a new reflector
            Reflector currentReflector = new Reflector(currentID, reflectorMapping);

            availableReflectors.add(currentReflector);
        }

        // sorts rotors and reflector by id
        Comparator<Rotor> rotorComparator = Comparator.comparingInt(Rotor::getId);
        availableRotors.sort(rotorComparator);
        Comparator<Reflector> reflectorComparator = Comparator.comparingInt(Reflector::getId);
        availableReflectors.sort(reflectorComparator);

        // builds the machine with the components that were initialized
        buildMachine(availableRotors, availableReflectors, rotorsCount, alphabet, character2index, userName);

        // initializes dictionary
        String words = cteEnigma.getCTEDecipher().getCTEDictionary().getWords().trim().toUpperCase();
        String excludeChars = cteEnigma.getCTEDecipher().getCTEDictionary().getExcludeChars();

        uboatName2battleField.get(userName).setDictionary(new Dictionary(words, excludeChars));

        // initializes number of allies
        uboatName2battleField.get(userName).setNumOfRequiredAllies(cteEnigma.getCTEBattlefield().getAllies());

        // initializes battle name
        uboatName2battleField.get(userName).setBattlefieldName(cteEnigma.getCTEBattlefield().getBattleName());

        // initializes difficulty level
        uboatName2battleField.get(userName).setDifficultyLevel(getDifficultyLevelFromString(cteEnigma.getCTEBattlefield().getLevel()));

        // sets the isConfigured flag of that battlefield
        uboatName2battleField.get(userName).setBattlefieldConfigured(true);

        return problem; // for code readability -> problem = Problem.NO_PROBLEM;
    }

    /**
     * fetches the current machine specifications.
     *
     * @return DTOspecs object that represents the specs.
     */
    @Override
    public DTOspecs displayMachineSpecifications(String username) {

        boolean isSucceeded = true;
        Problem details = Problem.NO_PROBLEM;

        List<Integer> inUseRotorsIDs = new ArrayList<>();
        String originalWindowsCharacters = "";
        String currentWindowsCharacters = "";
        String inUseReflectorSymbol = "";
        String inUsePlugs = "";
        String dictionaryExcludeCharacters = "";
        String machineAlphabet = "";
        List<Integer> notchDistancesToWindow = new ArrayList<>();
        List<Integer> originalNotchPositions = new ArrayList<>();
        int availableRotorsCount = 0;
        int inUseRotorsCount = 0;
        int availableReflectorsCount = 0;
        int cipheredTextsCount = 0;

        Machine currentMachine = uboatName2battleField.get(username).getMachine();
        Dictionary currentDictionary = uboatName2battleField.get(username).getDictionary();

        try {
            availableRotorsCount = currentMachine.getAvailableRotorsLen();
            inUseRotorsCount = currentMachine.getRotorsCount();
            availableReflectorsCount = currentMachine.getAvailableReflectorsLen();
            cipheredTextsCount = currentMachine.getCipherCounter();
            dictionaryExcludeCharacters = currentDictionary.getExcludeChars();
            machineAlphabet = currentMachine.getAlphabet();

            if (currentMachine.isConfigured()) {
                inUseRotorsIDs = currentMachine.getInUseRotorsIDs();
                originalWindowsCharacters = currentMachine.getOriginalWindowsCharacters();
                currentWindowsCharacters = currentMachine.getCurrentWindowsCharacters();
                inUseReflectorSymbol = decimalToRoman(currentMachine.getInUseReflector().getId());
                inUsePlugs = currentMachine.getAllPlugPairs();
                notchDistancesToWindow = currentMachine.getInUseNotchDistanceToWindow();
                originalNotchPositions = currentMachine.getOriginalNotchPositions();
            } else {
                details = Problem.NO_CONFIGURATION;
            }
        } catch (NullPointerException e) {
            isSucceeded = false;
            details = Problem.NO_LOADED_MACHINE;
        }

        return new DTOspecs(isSucceeded, details, availableRotorsCount, inUseRotorsCount,
                notchDistancesToWindow, originalNotchPositions, availableReflectorsCount, cipheredTextsCount,
                inUseRotorsIDs, originalWindowsCharacters, currentWindowsCharacters, inUseReflectorSymbol, dictionaryExcludeCharacters,
                inUsePlugs, machineAlphabet, currentDictionary);
    }

    /**
     * prepares the new configuration from user, to be in the right format for the machine.
     * then, updates the machine's configuration.
     *
     * @param rotorsIDs   a String that represents a list of the wanted rotor's id's, sorted to the wanted order
     * @param windows     String of characters that represents the window characters for each wanted rotor
     * @param reflectorID the id of the wanted reflector, as an integer
     * @param plugs       String that represents plugs, with no spaces or separators
     * @return DTOstatus object that represents the status of the operation
     */
    @Override
    public DTOsecretConfig selectConfigurationManual(String rotorsIDs, String windows, int reflectorID, String plugs, String userName) {
        boolean isSucceed = true;
        Problem details = Problem.NO_PROBLEM;

        // converts rotors from a String to list of Integers,
        // because problems can happen only from user and not with random generating,
        // so only this function gets effected.

        List<Integer> rotorsIDList = new ArrayList<>();

        String[] arrayOfStringRotorsIds = rotorsIDs.split(",");

        for (int i = arrayOfStringRotorsIds.length - 1; i >= 0; i--) {

            rotorsIDList.add(Integer.parseInt(arrayOfStringRotorsIds[i]));
        }
        // here we have a list of integers representing rotors ids

        StringBuilder reversedWindows = new StringBuilder();

        for (int i = windows.length() - 1; i >= 0; i--) {
            reversedWindows.append(windows.charAt(i));
        }
        // here we have String of window characters representing rotor's position according to the window.

        updateConfiguration(rotorsIDList, reversedWindows.toString(), reflectorID, plugs, userName);

        List<Integer> notchPositions = uboatName2battleField.get(userName).getMachine().getOriginalNotchPositions();

        return new DTOsecretConfig(isSucceed, details, rotorsIDList, reversedWindows.toString(), decimalToRoman(reflectorID), plugs, notchPositions);
    }

    /**
     * randomizes a new configuration.
     * then updates the machine configuration.
     *
     * @return DTOsecretConfig object representing the new configuration
     */
    @Override
    public DTOsecretConfig selectConfigurationAuto(String userName) {
        boolean isSucceeded = true;
        Problem details = Problem.NO_PROBLEM;

        String alphabet = uboatName2battleField.get(userName).getMachine().getAlphabet();

        List<Integer> randomGeneratedRotorIDs = new ArrayList<>();
        int randomGeneratedReflectorID;
        StringBuilder randomGeneratedWindowCharacters = new StringBuilder();


        int randomPlugsCount = (int) Math.floor(Math.random() * (alphabet.length() + 1)) / 2;
        StringBuilder randomGeneratedPlugs = new StringBuilder();
        List<Boolean> alreadyPlugged = new ArrayList<>(Collections.nCopies(alphabet.length(), false));

        // randomizes rotors ID and order
        for (int i = 0; i < uboatName2battleField.get(userName).getMachine().getRotorsCount(); i++) {
            int randomRotorID = (int) Math.ceil(Math.random() * uboatName2battleField.get(userName).getMachine().getAvailableRotorsLen());
            while (randomGeneratedRotorIDs.contains(randomRotorID)) {
                randomRotorID = (int) Math.ceil(Math.random() * uboatName2battleField.get(userName).getMachine().getAvailableRotorsLen());
            }

            randomGeneratedRotorIDs.add(randomRotorID);
        }

        // randomizes reflector ID
        randomGeneratedReflectorID = (int) Math.ceil(Math.random() * uboatName2battleField.get(userName).getMachine().getAvailableReflectorsLen());

        // randomizes window offsets
        for (int i = 0; i < uboatName2battleField.get(userName).getMachine().getRotorsCount(); i++) {
            // get random index
            int randomIndex = (int) Math.floor(Math.random() * alphabet.length());
            // convert random index to Character from the alphabet.
            randomGeneratedWindowCharacters.append(alphabet.charAt(randomIndex));
        }

        // randomizes plugs
        for (int i = 0; i < randomPlugsCount; i++) {
            int firstInPlugIndex = (int) Math.floor(Math.random() * alphabet.length());
            int secondInPlugIndex = (int) Math.floor(Math.random() * alphabet.length());

            while (alreadyPlugged.get(firstInPlugIndex)) {
                firstInPlugIndex = (int) Math.floor(Math.random() * alphabet.length());
            }
            alreadyPlugged.set(firstInPlugIndex, true);

            while (alreadyPlugged.get(secondInPlugIndex)) {
                secondInPlugIndex = (int) Math.floor(Math.random() * alphabet.length());
            }
            alreadyPlugged.set(secondInPlugIndex, true);

            String currentPlug = "" + alphabet.charAt(firstInPlugIndex) + alphabet.charAt(secondInPlugIndex);
            randomGeneratedPlugs.append(currentPlug);
        }

        // updates the configuration
        updateConfiguration(randomGeneratedRotorIDs, randomGeneratedWindowCharacters.toString(),
                randomGeneratedReflectorID, "", userName);

        // get Notch Distances from window to display to user.
        List<Integer> notchDistances = new ArrayList<>();

        for (Integer rotorId : randomGeneratedRotorIDs) {
            Rotor currentRotor = uboatName2battleField.get(userName).getMachine().getRotorByID(rotorId);
            int currentNotchDistance = (currentRotor.getOriginalNotchIndex() - currentRotor.getOffset()
                    + uboatName2battleField.get(userName).getMachine().getAlphabet().length()) % uboatName2battleField.get(userName).getMachine().getAlphabet().length();
            notchDistances.add(currentNotchDistance);
        }

        return new DTOsecretConfig(isSucceeded, details, randomGeneratedRotorIDs,
                randomGeneratedWindowCharacters.toString(), decimalToRoman(randomGeneratedReflectorID),
                "", notchDistances);
    }

    /**
     * validates the input text from the user and calls method "cipherText" to cipher the text.
     *
     * @param inputText string of the input text
     * @return DTOciphertext object which has the ciphered text
     */
    @Override
    public DTOciphertext cipherInputText(String inputText, String userName) {

        boolean isSucceed = false;
        String outputText = "";
        Problem problem;

        if (inputText.length() == 0) {
            problem = Problem.CIPHER_INPUT_EMPTY_STRING;
            return new DTOciphertext(false, problem, outputText, "", new ArrayList<>(), 0);
        }

        // check battlefield existence
        Battlefield battlefield = uboatName2battleField.get(userName);
        if (battlefield == null) {
            return new DTOciphertext(false, Problem.UBOAT_NAME_DOESNT_EXIST, outputText, "", new ArrayList<>(), 0);
        }

        if (!battlefield.getMachine().isConfigured()) {
            return new DTOciphertext(false, Problem.NO_CONFIGURATION, outputText, "", new ArrayList<>(), 0);
        }


        // check valid ABC
        problem = isAllCharsInAlphabet(inputText, userName);

        if (problem.equals(Problem.NO_PROBLEM)) {
            isSucceed = true;

            // cipher in line-by-line mode
            if (!charByCharState) {
                long startMeasureTime = System.nanoTime();
                outputText = cipherText(inputText, userName);
                long timeElapsed = System.nanoTime() - startMeasureTime;
                Pair<Pair<String, String>, Long> inputTextToOutputTextToTimeElapsed = new Pair<>(new Pair<>(inputText, outputText), timeElapsed);

                machineRecords.get(machineRecords.size() - 1).getCipherHistory().add(inputTextToOutputTextToTimeElapsed);

            } else {
                // cipher in char-by-char mode
                long startMeasureTime = System.nanoTime();
                outputText = cipherText(inputText, userName);
                // sets the text to decipher in the battlefield
                battlefield.setTextToDecipher(outputText);
                long timeElapsed = System.nanoTime() - startMeasureTime;
                currentCipherProcessTimeElapsed += timeElapsed; // value that engine holds for current  cipher time
                currentCipherProcessInputText += inputText;
                currentCipherProcessOutputText += outputText;
            }
        }

        String currentWindowCharacters = uboatName2battleField.get(userName).getMachine().getCurrentWindowsCharacters();
        List<Integer> currentNotchDistances = uboatName2battleField.get(userName).getMachine().getInUseNotchDistanceToWindow();
        int cipherCounter = uboatName2battleField.get(userName).getMachine().getCipherCounter();

        return new DTOciphertext(isSucceed, problem, outputText, currentWindowCharacters, currentNotchDistances, cipherCounter);
    }

    /**
     * checks if all characters in a given string are in the alphabet
     *
     * @param inputText a String to check
     * @return a Problem that represents the problem of the text. if valid, returns Problem.NO_PROBLEM
     */
    private Problem isAllCharsInAlphabet(String inputText, String userName) {
        final int NOT_FOUND = -1;

        for (Character currentCharacter : inputText.toCharArray()) {
            if (uboatName2battleField.get(userName).getMachine().getAlphabet().indexOf(currentCharacter) == NOT_FOUND) {
                return Problem.CIPHER_INPUT_NOT_IN_ALPHABET;
            }
        }

        return Problem.NO_PROBLEM;
    }

    /**
     * resetting the offset of each rotor in configuration of machine to its original values.
     *
     * @return DTOresetConfig representing the status of the operation
     */
    @Override
    public DTOresetConfig resetConfiguration(String userName) {

        boolean isSucceed = true;
        Problem details = Problem.NO_PROBLEM;
        Machine currentMachine = uboatName2battleField.get(userName).getMachine();

        for (int i = 0; i < currentMachine.getRotorsCount(); i++) {

            int currentOffset = currentMachine.getInUseWindowsOffsets().get(i);
            currentMachine.getInUseRotors().get(i).rotateToOffset(currentOffset);
        }

        return new DTOresetConfig(isSucceed, details, currentMachine.getOriginalWindowsCharacters(), currentMachine.getOriginalNotchPositions());
    }

    /**
     * validates rotors input from user.
     *
     * @param rotorsIDs a String representing the rotor's id's
     * @return DTOstatus representing the status of the operation
     */
    @Override
    public DTOstatus validateRotors(String rotorsIDs, String userName) {
        boolean isSucceed = true;
        Problem details = Problem.NO_PROBLEM;


        // check for empty string input
        if (rotorsIDs.length() == 0) {
            isSucceed = false;
            details = Problem.ROTOR_VALIDATE_EMPTY_STRING;
        } else if (!rotorsIDs.contains(",")) {
            isSucceed = false;
            details = Problem.ROTOR_VALIDATE_NO_SEPERATOR;
        } else if (rotorsIDs.contains(" ")) {
            isSucceed = false;
            details = Problem.ROTOR_INPUT_HAS_SPACE;
        } else {
            List<Boolean> rotorIdFlags = new ArrayList<>(Collections.nCopies(uboatName2battleField.get(userName).getMachine().getAvailableRotorsLen(), false));

            // create list of Strings contains rotors id's to validate through
            String[] rotorsIdArray = rotorsIDs.split(",");


            // check if rotorsIDs size is exactly the required size.
            if (rotorsIdArray.length < uboatName2battleField.get(userName).getMachine().getRotorsCount()) {
                isSucceed = false;
                details = Problem.ROTOR_INPUT_NOT_ENOUGH_ELEMENTS;
            } else if (rotorsIdArray.length > uboatName2battleField.get(userName).getMachine().getRotorsCount()) {
                isSucceed = false;
                details = Problem.ROTOR_INPUT_TOO_MANY_ELEMENTS;
            } else {
                //check for duplicates rotors in list
                for (String rotorID : rotorsIdArray) {
                    int integerRotorId;

                    try {
                        integerRotorId = Integer.parseInt(rotorID);
                    } catch (NumberFormatException e) {
                        isSucceed = false;
                        details = Problem.ROTOR_INPUT_NUMBER_FORMAT_EXCEPTION;
                        break;
                    }

                    // check if the rotorID exists in this machine.
                    if (integerRotorId <= 0 || integerRotorId > uboatName2battleField.get(userName).getMachine().getAvailableRotorsLen()) {
                        isSucceed = false;
                        details = Problem.ROTOR_INPUT_OUT_OF_RANGE_ID;
                        break;
                    }
                    if (!rotorIdFlags.get(integerRotorId - 1)) {
                        rotorIdFlags.set(integerRotorId - 1, true);
                    } else {
                        isSucceed = false;
                        details = Problem.ROTOR_DUPLICATION;
                        break;
                    }
                }
            }
        }

        return new DTOstatus(isSucceed, details);
    }

    /**
     * validates window characters input from user.
     *
     * @param windowChars string of characters representing the windows characters
     * @return DTOstatus representing the status of the operation
     */
    @Override
    public DTOstatus validateWindowCharacters(String windowChars, String userName) {
        boolean isSucceed = true;
        Problem details = Problem.NO_PROBLEM;
        final int CHAR_NOT_FOUND = -1;

        if (windowChars.length() > uboatName2battleField.get(userName).getMachine().getRotorsCount()) {
            isSucceed = false;
            details = Problem.WINDOW_INPUT_TOO_MANY_LETTERS;
        } else if (windowChars.length() < uboatName2battleField.get(userName).getMachine().getRotorsCount()) {
            isSucceed = false;
            details = Problem.WINDOW_INPUT_TOO_FEW_LETTERS;
        } else {
            for (Character currentWindowCharacter : windowChars.toCharArray()) {
                if (uboatName2battleField.get(userName).getMachine().getAlphabet().indexOf(currentWindowCharacter) == CHAR_NOT_FOUND) {
                    isSucceed = false;
                    details = Problem.WINDOWS_INPUT_NOT_IN_ALPHABET;
                    break;
                }
            }
        }

        return new DTOstatus(isSucceed, details);
    }

    /**
     * validate reflector input from the user.
     *
     * @param reflectorID the reflector's id as an integer
     * @return DTOstatus representing the status of the operation
     */
    @Override
    public DTOstatus validateReflector(int reflectorID, String userName) {
        boolean isSucceed = true;
        Problem details = Problem.NO_PROBLEM;

        int NOT_A_NUMBER_CODE = -1;
        int EMPTY_STR_CODE = -2;
        if (reflectorID == NOT_A_NUMBER_CODE) {
            isSucceed = false;
            details = Problem.REFLECTOR_INPUT_NOT_A_NUMBER;
        } else if (reflectorID == EMPTY_STR_CODE) {
            isSucceed = false;
            details = Problem.REFLECTOR_INPUT_EMPTY_STRING;
        } else {
            // check if the reflectorID exists in this machine.
            if (reflectorID <= 0 || reflectorID > uboatName2battleField.get(userName).getMachine().getAvailableReflectorsLen()) {
                isSucceed = false;
                details = Problem.REFLECTOR_INPUT_OUT_OF_RANGE_ID;
            }
        }

        return new DTOstatus(isSucceed, details);
    }

    /**
     * validate plugs input from the user.
     *
     * @param plugs a String of plugs with no spaces of separators
     * @return DTOstatus representing the status of the operation
     */
    @Override
    public DTOstatus validatePlugs(String plugs, String userName) {
        boolean isSucceed = true;
        Problem details = Problem.NO_PROBLEM;
        List<Boolean> alreadyPlugged = new ArrayList<>(Collections.nCopies(uboatName2battleField.get(userName).getMachine().getAlphabet().length(), false));
        final int CHAR_NOT_FOUND = -1;


        if (plugs.length() % 2 == 1) {
            isSucceed = false;
            details = Problem.PLUGS_INPUT_ODD_ALPHABET_AMOUNT;
        } else {
            // goes through all the plugs (go through pairs)
            for (int i = 0; i < plugs.length(); i += 2) {
                int firstInPlugIndex = uboatName2battleField.get(userName).getMachine().getAlphabet().indexOf(plugs.charAt(i));
                int secondInPlugIndex = uboatName2battleField.get(userName).getMachine().getAlphabet().indexOf(plugs.charAt(i + 1));

                // check if both characters are the same.
                if (firstInPlugIndex == secondInPlugIndex) {
                    isSucceed = false;
                    details = Problem.SELF_PLUGGING;
                    break;
                }

                // check if both characters in the current plug is in the alphabet.
                if (firstInPlugIndex == CHAR_NOT_FOUND || secondInPlugIndex == CHAR_NOT_FOUND) {
                    isSucceed = false;
                    details = Problem.PLUGS_INPUT_NOT_IN_ALPHABET;
                    break;
                } else {
                    // check if both characters are not plugged yet.
                    if (!alreadyPlugged.get(firstInPlugIndex) && !alreadyPlugged.get(secondInPlugIndex)) {
                        alreadyPlugged.set(firstInPlugIndex, true);
                        alreadyPlugged.set(secondInPlugIndex, true);
                    } else {
                        isSucceed = false;
                        details = Problem.ALREADY_PLUGGED;
                        break;
                    }
                }

            }
        }

        return new DTOstatus(isSucceed, details);
    }

    /**
     * gets all the history and statistics of the current machine
     *
     * @return DTOstatistics including the statistics of the machine
     */
    public DTOstatistics getHistoryAndStatistics() {
        boolean isSucceeded = true;
        Problem details = Problem.NO_PROBLEM;

        return new DTOstatistics(isSucceeded, details, machineRecords);
    }

    /**
     * @return true is the machine is configured. false otherwise
     */
    public boolean getIsMachineConfigured(String userName) {
        return uboatName2battleField.get(userName).getMachine().isConfigured();
    }

    /**
     * @param uboatUserName uboat name
     */
    @Override
    public void startBruteForceProcess(String uboatUserName) {

        // assuming battlefield is configured because all participants are ready.
        // participants can be ready only for configured battlefields.
        Battlefield battlefield = uboatName2battleField.get(uboatUserName);
        battlefield.setActive(true);
        Set<DecryptManager> allies = battlefield.getAllies();
        allies.forEach(DecryptManager::startDecrypt);
    }

    /**
     * stop all BLOCKED THREADS from executing
     *
     * @param userName the username of the uboat, to find the battlefield and the agents that are registered to it.
     * @return DTOstatus that represents the status of the stop action
     */
    @Override
    public DTOstatus stopBruteForceProcess(String userName) {
        Battlefield battlefield = uboatName2battleField.get(userName);

        if (battlefield == null) {
            return new DTOstatus(false, Problem.UBOAT_NAME_DOESNT_EXIST);
        }
        battlefield.setActive(false); // stopping collector and producer from executing anymore

        // take care of all BLOCKED collector and producer
        Set<DecryptManager> allies = battlefield.getAllies();
        allies.forEach(DecryptManager::stopDecrypt);
        return new DTOstatus(true, Problem.NO_PROBLEM);
    }

    @Override
    public void pauseBruteForceProcess(String userName) {
        Set<DecryptManager> allies = uboatName2battleField.get(userName).getAllies();

        // allies.for each DM need to stop all tasks
        // decryptManager.pauseDecrypt();
    }

    @Override
    public void resumeBruteForceProcess(String userName) {
        Set<DecryptManager> allies = uboatName2battleField.get(userName).getAllies();

        // allies.for each DM need to stop all tasks
        // decryptManager.resumeDecrypt();
    }

    /**
     * @return the machine's alphabet as a String
     */
    @Override
    public String getMachineAlphabet(String userName) {
        if (uboatName2battleField.get(userName).getMachine() != null) {
            return uboatName2battleField.get(userName).getMachine().getAlphabet();
        }

        return "";
    }

    /**
     * true - for char by char cipher, false - for line by line.
     *
     * @param newCharByCharState the wanted state
     */
    @Override
    public void setCharByCharState(boolean newCharByCharState) {
        if (!newCharByCharState) {
            resetCurrentCipherProcess();
        }

        this.charByCharState = newCharByCharState;
    }

    /**
     * resets the current cipher process (for char-by-char mode)
     */
    private void resetCurrentCipherProcess() {
        currentCipherProcessTimeElapsed = 0;
        currentCipherProcessInputText = "";
        currentCipherProcessOutputText = "";
    }

    /**
     * finishes the current cipher process (in char-by-char mode)
     */
    @Override
    public void doneCurrentCipherProcess() {
//        Pair<Pair<String, String>, Long> inputTextToOutputTextToTimeElapsed = new Pair<>(new Pair<>(currentCipherProcessInputText, currentCipherProcessOutputText), currentCipherProcessTimeElapsed);
//        machineRecords.get(machineRecords.size() - 1).getCipherHistory().add(inputTextToOutputTextToTimeElapsed);
//        resetCurrentCipherProcess();
//        machine.advanceCipherCounter();
    }

    @Override
    public DTOdictionary getDictionaryWords(String userName) {
        return new DTOdictionary(uboatName2battleField.get(userName).getDictionary().getDictionaryWords());
    }

    @Override
    public boolean isAllWordsInDictionary(String text, String userName) {
        return uboatName2battleField.get(userName).getDictionary().isAllWordsInDictionary(text);
    }

    public Map<String, Battlefield> getBattleFieldManager() {
        return uboatName2battleField;
    }

    @Override
    public DTOallies getAlliesInfo(String uboatUserName) {
        boolean isSucceeded = true;
        Problem details = Problem.NO_PROBLEM;

        List<AllieInfo> alliesInfo = new ArrayList<>();

        for (DecryptManager allie : uboatName2battleField.get(uboatUserName).getAllies()) {
            String allieName = allie.getAllieName();
            int numOfAgents = allie.getNumOfAgents();
            int taskSize = allie.getTaskSize();

            AllieInfo currentAllieInfo = new AllieInfo(allieName, numOfAgents, taskSize);
            alliesInfo.add(currentAllieInfo);
        }

        return new DTOallies(isSucceeded, details, alliesInfo);
    }

    @Override
    public boolean getIsUboatReady(String uboatName) {
        return uboatName2battleField.get(uboatName).getIsUboatReady();
    }

    @Override
    public void setUboatReady(String uboatUserName, boolean isReady) {
        uboatName2battleField.get(uboatUserName).setIsUboatReady(isReady);
    }

    @Override
    public DTOstatus setAllieReady(String allieUserName, String uboatUserName, boolean isReady, int taskSize) {
        Set<DecryptManager> allies = uboatName2battleField.get(uboatUserName).getAllies();
        Optional<DecryptManager> myAllie = allies.stream().filter((allie) -> allie.getAllieName().equals(allieUserName)).findFirst();

        if (myAllie.isPresent()) {
            DecryptManager allie = myAllie.get();
            allie.setTaskSize(taskSize);
            allie.setDMReady(isReady);
            return new DTOstatus(true, Problem.NO_PROBLEM);
        } else {
            return new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS);
        }

    }

    @Override
    public boolean allClientsReady(String uboatName) {
        final boolean[] isAllReady = {true};

        Set<DecryptManager> allies = uboatName2battleField.get(uboatName).getAllies();
        Optional<DecryptManager> unreadyDM = allies.stream().filter(allie -> !allie.getDMReady()).findFirst();

        unreadyDM.ifPresent(decryptManager -> isAllReady[0] = false);
        return isAllReady[0];
    }

    /**
     * get candidates from the uboatCandidateQueue in order to send the candidates to the uboat.
     *
     * @param uboatUserName uboat name
     * @return dto of candidates
     */
    public DTOagentConclusions fetchNextCandidates(String uboatUserName) {
        final int CONCLUSION_LIMIT = 50;
        BlockingQueue<AgentConclusion> uboatCandidatesQueue = uboatName2battleField.get(uboatUserName).getUboatCandidatesQueue();
        List<AgentConclusion> nextAgentConclusions = new ArrayList<>();
        int conclusionCounter = 0;
        boolean isThereMoreCandidates = true;
        while (isThereMoreCandidates && conclusionCounter < CONCLUSION_LIMIT) {
            AgentConclusion nextConclusion = uboatCandidatesQueue.poll();
            if (nextConclusion == null) {
                isThereMoreCandidates = false;
            } else {
                conclusionCounter++;
                nextAgentConclusions.add(nextConclusion);
            }
        }

        return new DTOagentConclusions(true, Problem.NO_PROBLEM, nextAgentConclusions);
    }

    @Override
    public DTOagentConclusions fetchCandidatesToDisplay(String uboatName, String allieName) {
        Set<DecryptManager> allies = uboatName2battleField.get(uboatName).getAllies();
        Optional<DecryptManager> myAllie = allies.stream().filter(allie -> allie.getAllieName().equals(allieName)).findFirst();

        if (myAllie.isPresent()) {
            DecryptManager allie = myAllie.get();
            return new DTOagentConclusions(true, Problem.NO_PROBLEM, allie.getDecipherCandidates());
        } else {
            return new DTOagentConclusions(false, Problem.UNAUTHORIZED_CLIENT_ACCESS, null);
        }
    }

    @Override
    public Map<String, Set<AgentInfo>> getLoggedAlliesNamesManager() {
        return loggedAllieName2loggedAgents;
    }

    @Override
    public DTObattlefields getBattleFieldsInfo(String uboatUserName, boolean onlyMy) {
        List<BattlefieldInfo> allBattlefieldsInfo = new ArrayList<>();

        if (onlyMy) { // fetching the info of a specific battlefield
            Battlefield battlefield = uboatName2battleField.get(uboatUserName);
            if (battlefield == null) {
                return new DTObattlefields(false, Problem.MISSING_QUERY_PARAMETER, allBattlefieldsInfo);
            } else if (!battlefield.isBattlefieldConfigured()) {
                return new DTObattlefields(false, Problem.BATTLEFIELD_NOT_CONFIGURED, allBattlefieldsInfo);
            } else {
                String battlefieldName = battlefield.getBattlefieldName();
                boolean isActive = battlefield.isActive().get();
                DifficultyLevel difficultyLevel = battlefield.getDifficultyLevel();
                Dictionary dictionary = battlefield.getDictionary();
                int numOfRequiredAllies = battlefield.getNumOfRequiredAllies();
                int numOfLoggedAllies = battlefield.getAllies().size();
                String textToDecipher = battlefield.getTextToDecipher();
                allBattlefieldsInfo.add(new BattlefieldInfo(battlefieldName, uboatUserName, isActive,
                        difficultyLevel, dictionary, numOfRequiredAllies, numOfLoggedAllies, textToDecipher));
            }
        } else { // fetching the info of all battlefields
            for (Map.Entry<String, Battlefield> entry : uboatName2battleField.entrySet()) {

                if (!entry.getValue().isBattlefieldConfigured()) {
                    continue;
                }

                String battlefieldName = entry.getValue().getBattlefieldName();
                boolean isActive = entry.getValue().isActive().get();
                DifficultyLevel difficultyLevel = entry.getValue().getDifficultyLevel();
                Dictionary dictionary = entry.getValue().getDictionary();
                int numOfRequiredAllies = entry.getValue().getNumOfRequiredAllies();
                int numOfLoggedAllies = entry.getValue().getAllies().size();
                String textToDecipher = entry.getValue().getTextToDecipher();

                allBattlefieldsInfo.add(new BattlefieldInfo(battlefieldName, entry.getKey(), isActive,
                        difficultyLevel, dictionary, numOfRequiredAllies, numOfLoggedAllies, textToDecipher));
            }
        }

        return new DTObattlefields(true, Problem.NO_PROBLEM, allBattlefieldsInfo);
    }

    @Override
    public DTOloggedAgents getLoggedAgentsOfAllie(String usernameFromSession) {
        Set<AgentInfo> agents = this.loggedAllieName2loggedAgents.get(usernameFromSession);

        return new DTOloggedAgents(true, Problem.NO_PROBLEM, agents);
    }

    @Override
    public DTOloggedAllies fetchAllLoggedAllies() {
        Set<String> allies = this.loggedAllieName2loggedAgents.keySet();

        return new DTOloggedAllies(true, Problem.NO_PROBLEM, allies);
    }

    @Override
    public DTOstatus assignAgentToAllie(String agentName, String allieNameToJoin, int numOfThreads, int numOfMissionsToPull) {
        Set<AgentInfo> agentsOfAllie = loggedAllieName2loggedAgents.get(allieNameToJoin);
        if (agentsOfAllie == null) {
            return new DTOstatus(false, Problem.ALLIE_NAME_NOT_FOUND);
        }

        AgentInfo agentInfoToInsert = new AgentInfo(agentName, numOfThreads, numOfMissionsToPull);

        agentsOfAllie.add(agentInfoToInsert);
        agentName2agentInfo.put(agentName, agentInfoToInsert);

        return new DTOstatus(true, Problem.NO_PROBLEM);
    }

    @Override
    public DTOstaticContestInfo getStaticContestInfo(String uboatName) {
        DTObattlefields battlefields = getBattleFieldsInfo(uboatName, true);
        DTOallies allies = getAlliesInfo(uboatName);

        List<AllieInfo> alliesList = new ArrayList<>();
        BattlefieldInfo battlefieldInfo = new BattlefieldInfo();

        if (!battlefields.isSucceed() || !allies.isSucceed()) {
            // uboat Name probably is trash
            return new DTOstaticContestInfo(false, Problem.UBOAT_NAME_DOESNT_EXIST, alliesList, battlefieldInfo);
        } else {
            alliesList = allies.getAllies();
            battlefieldInfo = battlefields.getAllBattlefields().get(0); // assuming there is only 1 battlefield

            return new DTOstaticContestInfo(true, Problem.NO_PROBLEM, alliesList, battlefieldInfo);
        }
    }

    @Override
    public Map<String, AgentInfo> getLoggedAgentNamesManager() {
        return agentName2agentInfo;
    }

    @Override
    public DTOdynamicContestInfo getDynamicContestInfo(String uboatName, String allieName) {
        Set<AgentInfo> agentsInfo = new HashSet<>();
        List<AgentConclusion> allConclusions = new ArrayList<>();
        JobProgressInfo jobProgressInfo = new JobProgressInfo();

        Battlefield battlefield = uboatName2battleField.get(uboatName);
        if (battlefield == null) {
            return new DTOdynamicContestInfo(false, Problem.UBOAT_NAME_DOESNT_EXIST, agentsInfo, allConclusions, jobProgressInfo);
        }

        Optional<DecryptManager> allieMaybe = battlefield.getAllies().stream()
                .filter(decryptManager -> decryptManager.getAllieName().equals(allieName)).findFirst();

        if (!allieMaybe.isPresent()) {
            return new DTOdynamicContestInfo(false, Problem.ALLIE_NAME_NOT_FOUND, agentsInfo, allConclusions, jobProgressInfo);
        }

        DecryptManager allie = allieMaybe.get();

        // fetch my agents info
        agentsInfo = loggedAllieName2loggedAgents.get(allieName);

        // fetch all candidates
        allConclusions = allie.getAllConclusions();

        // fetch overall progress
        jobProgressInfo = allie.getJobProgressInfo();

        return new DTOdynamicContestInfo(true, Problem.NO_PROBLEM, agentsInfo, allConclusions, jobProgressInfo);
    }

    @Override
    public DTOstatus assignAllieToBattlefield(String alliesName, String uboatUserName) {
        Battlefield battlefield = uboatName2battleField.get(uboatUserName);

        if (battlefield == null) {
            return new DTOstatus(false, Problem.UBOAT_NAME_DOESNT_EXIST);
        }

        battlefield.addDecryptManager(alliesName, agentName2agentInfo); // cant fail..
        return new DTOstatus(true, Problem.NO_PROBLEM);
    }

    @Override
    public DTOstatus setAllieWinnerInfo(String uboatName, String allieName) {
        Battlefield battlefield = uboatName2battleField.get(uboatName);
        if (battlefield == null) {
            return new DTOstatus(false, Problem.UBOAT_NAME_DOESNT_EXIST);
        }

        Set<DecryptManager> allies = battlefield.getAllies();

        Optional<DecryptManager> allieMaybe = allies.stream().filter(decryptManager -> decryptManager.getAllieName().equals(allieName)).findFirst();
        if (!allieMaybe.isPresent()) {
            return new DTOstatus(false, Problem.ALLIE_NAME_NOT_FOUND);
        }

        DecryptManager allie = allieMaybe.get();

        int numOfAgents = allie.getNumOfAgents();
        int taskSize = allie.getTaskSize();
        AllieInfo winnerAllieInfo = new AllieInfo(allieName, numOfAgents, taskSize);

        battlefield.setWinnerAllieInfo(winnerAllieInfo);

        return new DTOstatus(true, Problem.NO_PROBLEM);

    }

    @Override
    public DTOallies getAllieWinnerInfo(String usernameFromSession, String allieName) {

        List<AllieInfo> allieInfos = new ArrayList<>();
        Battlefield battlefield = uboatName2battleField.get(usernameFromSession);
        if (battlefield == null) {
            return new DTOallies(false, Problem.UBOAT_NAME_DOESNT_EXIST, allieInfos);
        }
        allieInfos.add(battlefield.getWinnerAllieInfo());
        return new DTOallies(true, Problem.NO_PROBLEM, allieInfos);
    }

    @Override
    public DTOactive getAllieApprovalStatus(String allieName, String uboatName) {
        Battlefield battlefield = uboatName2battleField.get(uboatName);
        if (battlefield == null) {
            return new DTOactive(false, Problem.UBOAT_NAME_DOESNT_EXIST, false);
        }

        Optional<DecryptManager> allieMaybe = battlefield.getAllies().stream().filter((decryptManager) -> decryptManager.getAllieName().equals(allieName)).findFirst();
        if (!allieMaybe.isPresent()) {
            return new DTOactive(false, Problem.ALLIE_NAME_NOT_FOUND, false);
        }

        return new DTOactive(true, Problem.NO_PROBLEM, allieMaybe.get().isDMapprovedFinishGame());
    }

    @Override
    public DTOstatus setAllieApprovalStatus(boolean isApprove, String allieName, String uboatName) {
        Battlefield battlefield = uboatName2battleField.get(uboatName);
        if (battlefield == null) {
            return new DTOactive(false, Problem.UBOAT_NAME_DOESNT_EXIST, false);
        }

        Optional<DecryptManager> allieMaybe = battlefield.getAllies().stream().filter((decryptManager) -> decryptManager.getAllieName().equals(allieName)).findFirst();
        if (!allieMaybe.isPresent()) {
            return new DTOactive(false, Problem.ALLIE_NAME_NOT_FOUND, false);
        }

        allieMaybe.get().setDMapprovedFinishGame(isApprove);
        return new DTOstatus(true, Problem.NO_PROBLEM);
    }

    @Override
    public DTOstatus removeBattlefield(String uboatName) {
        Battlefield removedBattlefield = uboatName2battleField.remove(uboatName);
        if (removedBattlefield == null) {
            return new DTOstatus(false, Problem.UBOAT_NAME_DOESNT_EXIST);
        }

        return new DTOstatus(true, Problem.NO_PROBLEM);
    }

    @Override
    public DTOtasks getNextTasks(String agentName, String allieName, String uboatName) {
        List<AgentTask> taskList = new ArrayList<>();
        boolean hasTasks = true;

        Battlefield battlefield = uboatName2battleField.get(uboatName);
        if (battlefield == null) {
            return new DTOtasks(false, Problem.UBOAT_NAME_DOESNT_EXIST, new ArrayList<>());
        }

        Optional<DecryptManager> allieMaybe = battlefield.getAllies().stream().filter(decryptManager -> decryptManager.getAllieName().equals(allieName)).findFirst();
        if (!allieMaybe.isPresent()) {
            return new DTOtasks(false, Problem.ALLIE_NAME_NOT_FOUND, new ArrayList<>());
        }

        AgentInfo agentInfo = agentName2agentInfo.get(agentName);
        if (agentInfo == null) {
            return new DTOtasks(false, Problem.AGENT_NAME_DOESNT_EXIST, new ArrayList<>());
        }

        int numOfTaskPerPull = agentInfo.getNumOfTasksPerPull();

        synchronized (waitingTasksQueueLock) {
            for (int i = 0; i < numOfTaskPerPull && hasTasks; i++) {
                AgentTask nextTask = (AgentTask) allieMaybe.get().getWaitingTasksBlockingQueue().poll();
                if (nextTask == null) {
                    hasTasks = false;
                } else {
                    taskList.add(nextTask);
                }
            }
        }

        return new DTOtasks(true, Problem.NO_PROBLEM, taskList);
    }

    @Override
    public DTOstatus submitConclusions(List<AgentConclusion> conclusions, String allieName, String uboatName) {
        Battlefield battlefield = uboatName2battleField.get(uboatName);
        if (battlefield == null) {
            return new DTOstatus(false, Problem.UBOAT_NAME_DOESNT_EXIST);
        }

        Optional<DecryptManager> allieMaybe = battlefield.getAllies().stream().filter((decryptManager) -> decryptManager.getAllieName().equals(allieName)).findFirst();
        if (!allieMaybe.isPresent()) {
            return new DTOstatus(false, Problem.ALLIE_NAME_NOT_FOUND);
        }

        BlockingQueue<AgentConclusion> conclusionQueue = allieMaybe.get().getCandidatesQueue();

        synchronized (conclusionQueueLock) {
            for (AgentConclusion conclusion : conclusions) {
                try {
                    conclusionQueue.put(conclusion);
                } catch (InterruptedException ignored) {

                }
            }
        }

        return new DTOstatus(true, Problem.NO_PROBLEM);
    }

    @Override
    public boolean checkNameValidity(String username) {
        return !uboatName2battleField.containsKey(username) && !loggedAllieName2loggedAgents.containsKey(username) && !agentName2agentInfo.containsKey(username);
    }

    @Override
    public String toString() {
        return "engine.EnigmaEngine{" +
                "machine=" + uboatName2battleField +
                '}';
    }
}