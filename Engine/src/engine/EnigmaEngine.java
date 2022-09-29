package engine;

import battlefield.Battlefield;
import dm.decryptmanager.DecryptManager;
import dm.dictionary.Dictionary;
import dm.difficultylevel.DifficultyLevel;
import dto.*;
import javafx.util.Pair;
import machine.EnigmaMachine;
import machine.Machine;
import machine.component.Reflector;
import machine.component.Rotor;
import machine.jaxb.generated.*;
import problem.Problem;
import statistics.StatisticRecord;
import ui.adapter.UIAdapter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


import static dm.difficultylevel.DifficultyLevel.getDifficultyLevelFromString;
import static utill.Utility.*;


public class EnigmaEngine implements Engine {

    // The engine contains Machine instance and machine records object.
    private Machine machine;
    private DecryptManager decryptManager;
    private boolean charByCharState = false;
    private long currentCipherProcessTimeElapsed;
    private String currentCipherProcessOutputText;
    private String currentCipherProcessInputText;
    private List<StatisticRecord> machineRecords = new ArrayList<>();

    private final Map<String, Battlefield> uboatName2battleField;

    public static String JAXB_XML_PACKAGE_NAME = "machine.jaxb.generated";

    public EnigmaEngine() {
        this.uboatName2battleField = new HashMap<>();
        System.out.println("inside the default ctor of EnigmaEngine");
    }

    /**
     * creating new machine instance using all the parts the machine needs.
     */
    public void buildMachine(List<Rotor> availableRotors, List<Reflector> availableReflectors, int rotorsCount, String alphabet, Map<Character, Integer> character2index) {
        currentCipherProcessOutputText = "";
        currentCipherProcessInputText = "";
        machine = new EnigmaMachine(availableRotors, availableReflectors, rotorsCount, alphabet, character2index);
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
    public void updateConfiguration(List<Integer> rotorsIDs, String windowsChars, int reflectorID, String plugs) {

        // build windowOffsets
        List<Integer> windowOfssets = new ArrayList<>();

        for (int i = 0; i < windowsChars.length(); i++) {
            Rotor nextRotor = machine.getRotorByID(rotorsIDs.get(i));
            int offset = nextRotor.translateChar2Offset(windowsChars.charAt(i));
            windowOfssets.add(offset);
        }

        // sets the new configuration into the machine.
        machine.setMachineConfiguration(rotorsIDs, windowOfssets, reflectorID, plugs);

        // adds new configuration to statistical records.
        StatisticRecord newRecord = new StatisticRecord(rotorsIDs, windowsChars, reflectorID, plugs, machine.getOriginalNotchPositions());
        machineRecords.add(newRecord);

    }

    /**
     * ciphering text with the cipher method of "machine".
     *
     * @param toCipher a String of text to cipher
     * @return a String of the ciphered text
     */
    public String cipherText(String toCipher) {
        StringBuilder cipheredText = new StringBuilder();

        // goes through the character in the string
        for (Character currentChar : toCipher.toCharArray()) {
            cipheredText.append(machine.cipher(currentChar));
        }
        if (!charByCharState) {
            // increasing the cipher counter
            machine.advanceCipherCounter();
        }

        return cipheredText.toString();
    }

    /**
     * @return the rotors count of the machine
     */
    @Override
    public int getRotorsCount() {
        return machine.getRotorsCount();
    }

    /**
     * gets fileName from user and loads XML file to build a new machine.
     * then, builds the machine.
     *
     * @param fileContent string - name of xml file
     * @return DTOstatus object that describes the status of the operation
     */
    public DTOstatus buildMachineFromXmlFile(String fileContent) {
        boolean isSucceeded = true;
        Problem details;

        // checks if the file is in the .xml format.
        try {
            InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());
            CTEEnigma cteEnigma = deserializeFrom(inputStream);
            details = buildEnigmaFromCTEEnigma(cteEnigma);
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
        }

        return new DTOstatus(isSucceeded, details);
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
     *
     * @param cteEnigma the engine generated from jaxb
     * @return a Problem describing the problem that occurred. if valid, returns Problem.NO_PROBLEM
     */
    private Problem buildEnigmaFromCTEEnigma(CTEEnigma cteEnigma) {

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

        // initializes decrypt manager
        String words = cteEnigma.getCTEDecipher().getCTEDictionary().getWords().trim().toUpperCase();
        String excludeChars = cteEnigma.getCTEDecipher().getCTEDictionary().getExcludeChars();

        decryptManager = new DecryptManager(new Dictionary(words, excludeChars), this.machine);

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
                inUsePlugs, decryptManager.getNumOfAvailableAgents());
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
    public DTOsecretConfig selectConfigurationManual(String rotorsIDs, String windows, int reflectorID, String plugs) {
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

        updateConfiguration(rotorsIDList, reversedWindows.toString(), reflectorID, plugs);

        List<Integer> notchPositions = machine.getOriginalNotchPositions();

        return new DTOsecretConfig(isSucceed, details, rotorsIDList, reversedWindows.toString(), decimalToRoman(reflectorID), plugs, notchPositions);
    }

    /**
     * randomizes a new configuration.
     * then updates the machine configuration.
     *
     * @return DTOsecretConfig object representing the new configuration
     */
    @Override
    public DTOsecretConfig selectConfigurationAuto() {
        boolean isSucceeded = true;
        Problem details = Problem.NO_PROBLEM;

        String alphabet = machine.getAlphabet();

        List<Integer> randomGeneratedRotorIDs = new ArrayList<>();
        int randomGeneratedReflectorID;
        StringBuilder randomGeneratedWindowCharacters = new StringBuilder();
        int randomPlugsCount = (int) Math.floor(Math.random() * (alphabet.length() + 1)) / 2;
        StringBuilder randomGeneratedPlugs = new StringBuilder();
        List<Boolean> alreadyPlugged = new ArrayList<>(Collections.nCopies(alphabet.length(), false));

        // randomizes rotors ID and order
        for (int i = 0; i < machine.getRotorsCount(); i++) {
            int randomRotorID = (int) Math.ceil(Math.random() * machine.getAvailableRotorsLen());
            while (randomGeneratedRotorIDs.contains(randomRotorID)) {
                randomRotorID = (int) Math.ceil(Math.random() * machine.getAvailableRotorsLen());
            }

            randomGeneratedRotorIDs.add(randomRotorID);
        }

        // randomizes reflector ID
        randomGeneratedReflectorID = (int) Math.ceil(Math.random() * machine.getAvailableReflectorsLen());

        // randomizes window offsets
        for (int i = 0; i < machine.getRotorsCount(); i++) {
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
                randomGeneratedReflectorID, randomGeneratedPlugs.toString());

        // get Notch Distances from window to display to user.
        List<Integer> notchDistances = new ArrayList<>();

        for (Integer rotorId : randomGeneratedRotorIDs) {
            Rotor currentRotor = machine.getRotorByID(rotorId);
            int currentNotchDistance = (currentRotor.getOriginalNotchIndex() - currentRotor.getOffset()
                    + machine.getAlphabet().length()) % machine.getAlphabet().length();
            notchDistances.add(currentNotchDistance);
        }

        return new DTOsecretConfig(isSucceeded, details, randomGeneratedRotorIDs,
                randomGeneratedWindowCharacters.toString(), decimalToRoman(randomGeneratedReflectorID),
                randomGeneratedPlugs.toString(), notchDistances);
    }

    /**
     * validates the input text from the user and calls method "cipherText" to cipher the text.
     *
     * @param inputText string of the input text
     * @return DTOciphertext object which has the ciphered text
     */
    @Override
    public DTOciphertext cipherInputText(String inputText) {

        boolean isSucceed = false;
        String outputText = "";
        Problem problem;

        if (inputText.length() == 0) {
            problem = Problem.CIPHER_INPUT_EMPTY_STRING;
            return new DTOciphertext(false, problem, outputText);
        }
        // check valid ABC
        problem = isAllCharsInAlphabet(inputText);

        if (problem.equals(Problem.NO_PROBLEM)) {
            isSucceed = true;

            // cipher in line-by-line mode
            if (!charByCharState) {
                long startMeasureTime = System.nanoTime();
                outputText = cipherText(inputText);
                long timeElapsed = System.nanoTime() - startMeasureTime;
                Pair<Pair<String, String>, Long> inputTextToOutputTextToTimeElapsed = new Pair<>(new Pair<>(inputText, outputText), timeElapsed);

                machineRecords.get(machineRecords.size() - 1).getCipherHistory().add(inputTextToOutputTextToTimeElapsed);
            } else {
                // cipher in char-by-char mode
                long startMeasureTime = System.nanoTime();
                outputText = cipherText(inputText);
                long timeElapsed = System.nanoTime() - startMeasureTime;
                currentCipherProcessTimeElapsed += timeElapsed; // value that engine holds for current  cipher time
                currentCipherProcessInputText += inputText;
                currentCipherProcessOutputText += outputText;
            }

        }

        return new DTOciphertext(isSucceed, problem, outputText);
    }

    /**
     * checks if all characters in a given string are in the alphabet
     *
     * @param inputText a String to check
     * @return a Problem that represents the problem of the text. if valid, returns Problem.NO_PROBLEM
     */
    private Problem isAllCharsInAlphabet(String inputText) {
        final int NOT_FOUND = -1;

        for (Character currentCharacter : inputText.toCharArray()) {
            if (machine.getAlphabet().indexOf(currentCharacter) == NOT_FOUND) {
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
    public DTOresetConfig resetConfiguration() {

        boolean isSucceed = true;
        Problem details = Problem.NO_PROBLEM;

        for (int i = 0; i < machine.getRotorsCount(); i++) {

            int currentOffset = machine.getInUseWindowsOffsets().get(i);
            machine.getInUseRotors().get(i).rotateToOffset(currentOffset);
        }

        return new DTOresetConfig(isSucceed, details);
    }

    /**
     * validates rotors input from user.
     *
     * @param rotorsIDs a String representing the rotor's id's
     * @return DTOstatus representing the status of the operation
     */
    @Override
    public DTOstatus validateRotors(String rotorsIDs) {
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
            List<Boolean> rotorIdFlags = new ArrayList<>(Collections.nCopies(machine.getAvailableRotorsLen(), false));

            // create list of Strings contains rotors id's to validate through
            String[] rotorsIdArray = rotorsIDs.split(",");


            // check if rotorsIDs size is exactly the required size.
            if (rotorsIdArray.length < machine.getRotorsCount()) {
                isSucceed = false;
                details = Problem.ROTOR_INPUT_NOT_ENOUGH_ELEMENTS;
            } else if (rotorsIdArray.length > machine.getRotorsCount()) {
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
                    if (integerRotorId <= 0 || integerRotorId > machine.getAvailableRotorsLen()) {
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
    public DTOstatus validateWindowCharacters(String windowChars) {
        boolean isSucceed = true;
        Problem details = Problem.NO_PROBLEM;
        final int CHAR_NOT_FOUND = -1;

        if (windowChars.length() > machine.getRotorsCount()) {
            isSucceed = false;
            details = Problem.WINDOW_INPUT_TOO_MANY_LETTERS;
        } else if (windowChars.length() < machine.getRotorsCount()) {
            isSucceed = false;
            details = Problem.WINDOW_INPUT_TOO_FEW_LETTERS;
        } else {
            for (Character currentWindowCharacter : windowChars.toCharArray()) {
                if (machine.getAlphabet().indexOf(currentWindowCharacter) == CHAR_NOT_FOUND) {
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
    public DTOstatus validateReflector(int reflectorID) {
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
            if (reflectorID <= 0 || reflectorID > machine.getAvailableReflectorsLen()) {
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
    public DTOstatus validatePlugs(String plugs) {
        boolean isSucceed = true;
        Problem details = Problem.NO_PROBLEM;
        List<Boolean> alreadyPlugged = new ArrayList<>(Collections.nCopies(machine.getAlphabet().length(), false));
        final int CHAR_NOT_FOUND = -1;


        if (plugs.length() % 2 == 1) {
            isSucceed = false;
            details = Problem.PLUGS_INPUT_ODD_ALPHABET_AMOUNT;
        } else {
            // goes through all the plugs (go through pairs)
            for (int i = 0; i < plugs.length(); i += 2) {
                int firstInPlugIndex = machine.getAlphabet().indexOf(plugs.charAt(i));
                int secondInPlugIndex = machine.getAlphabet().indexOf(plugs.charAt(i + 1));

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
    public boolean getIsMachineConfigured() {
        return machine.isConfigured();
    }

    /**
     * saves the existing machine into a file.
     *
     * @param fileName the name of the file to save the machine in
     * @return DTOstatus representing the status of the operation
     * @throws IOException for a problem with the saving
     */
    public DTOstatus saveExistingMachineToFile(String fileName) throws IOException {
        boolean isSucceed = true;
        Problem details = Problem.NO_PROBLEM;

        if (fileName.length() == 0) {
            details = Problem.FILE_NOT_FOUND;
            return new DTOstatus(false, details);
        }
        // else, tries to save to the file
        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             Files.newOutputStream(Paths.get(fileName)))) {
            out.writeObject(machine);
            out.writeObject(machineRecords);
            out.flush();
        }

        return new DTOstatus(isSucceed, details);
    }

    /**
     * loads an existing machine from a file
     *
     * @param fileName the name of the file to load the machine from
     * @return DTOstatus representing the status of the operation
     * @throws IOException            for a problem with the loading
     * @throws ClassNotFoundException for a problem with the serialized class
     */
    public DTOstatus loadExistingMachineFromFile(String fileName) throws IOException, ClassNotFoundException {
        boolean isSucceed = true;
        Problem details = Problem.NO_PROBLEM;

        if (fileName.length() == 0) {
            details = Problem.FILE_NOT_FOUND;
            return new DTOstatus(false, details);
        }
        // else, tries to load from the file
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {

            machine = (EnigmaMachine) in.readObject();
            machineRecords = (List<StatisticRecord>) in.readObject();
        } catch (FileNotFoundException e) {
            isSucceed = false;
            details = Problem.FILE_NOT_FOUND;
        } catch (InvalidClassException | StreamCorruptedException | OptionalDataException e) {
            isSucceed = false;
            details = Problem.FILE_EXISTING_LOAD_FAILED;
        }

        return new DTOstatus(isSucceed, details);
    }

    /**
     * @return a list of all candidates of the deciphering process
     */
    @Override
    public DTOcandidates getDecipherCandidates() {
        boolean isSucceeded = true;
        Problem details = Problem.NO_PROBLEM;

        return new DTOcandidates(isSucceeded, details, decryptManager.getDecipherCandidates());
    }

    @Override
    public void startBruteForceProcess(UIAdapter uiAdapter, String textToDecipher,
                                       DifficultyLevel difficultyLevel, int taskSize, int numOfSelectedAgents) {

        decryptManager.startDecrypt(taskSize, numOfSelectedAgents, textToDecipher, difficultyLevel, uiAdapter);
    }

    @Override
    public void stopBruteForceProcess() {
        decryptManager.stopDecrypt();
    }

    @Override
    public void pauseBruteForceProcess() {
        decryptManager.pauseDecrypt();
    }

    @Override
    public void resumeBruteForceProcess() {
        decryptManager.resumeDecrypt();
    }


    /**
     * @return the machine's alphabet as a String
     */
    @Override
    public String getMachineAlphabet() {
        if (machine != null) {
            return machine.getAlphabet();
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
        Pair<Pair<String, String>, Long> inputTextToOutputTextToTimeElapsed = new Pair<>(new Pair<>(currentCipherProcessInputText, currentCipherProcessOutputText), currentCipherProcessTimeElapsed);
        machineRecords.get(machineRecords.size() - 1).getCipherHistory().add(inputTextToOutputTextToTimeElapsed);
        resetCurrentCipherProcess();
        machine.advanceCipherCounter();
    }

    @Override
    public DTOdictionary getDictionaryWords() {
        return new DTOdictionary(decryptManager.getDictionaryWords());
    }

    @Override
    public boolean isAllWordsInDictionary(String text) {
        return decryptManager.isAllWordsInDictionary(text);
    }

    @Override
    public void addDecryptManager(String alliesName, String uboatUserName) {
        uboatName2battleField.get(uboatUserName).addDecryptManager(alliesName);
    }

    public Map<String, Battlefield> getBattleFieldManager() {
        return uboatName2battleField;
    }

    @Override
    public String toString() {
        return "engine.EnigmaEngine{" +
                "machine=" + machine +
                '}';
    }
}