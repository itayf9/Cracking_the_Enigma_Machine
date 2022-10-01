package engine;

import battlefield.Battlefield;
import dm.difficultylevel.DifficultyLevel;
import dto.*;
import ui.adapter.UIAdapter;

import java.io.IOException;
import java.util.Map;

public interface Engine {

    /**
     * @return the rotors count of the machine
     */
    int getRotorsCount(String userName);

    /**
     * gets fileName from user and loads XML file to build a new machine.
     * then, builds the machine.
     *
     * @param fileName string - name of xml file
     * @return DTOstatus object that describes the status of the operation
     */
    DTOstatus buildMachineFromXmlFile(String fileName, String userName);

    /**
     * fetches the current machine specifications.
     *
     * @return DTOspecs object that represents the specs.
     */
    DTOspecs displayMachineSpecifications(String username);

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
    DTOsecretConfig selectConfigurationManual(String rotorsIDs, String windows, int reflectorID, String plugs, String userName);

    /**
     * randomizes a new configuration.
     * then updates the machine configuration.
     *
     * @return DTOsecretConfig object representing the new configuration
     */
    DTOsecretConfig selectConfigurationAuto(String userName);

    /**
     * validates the input text from the user and calls method "cipherText" to cipher the text.
     *
     * @param inputText string of the input text
     * @return DTOciphertext object which has the ciphered text
     */
    DTOciphertext cipherInputText(String inputText, String userName);

    /**
     * resetting the offset of each rotor in configuration of machine to its original values.
     *
     * @return DTOresetConfig representing the status of the operation
     */
    DTOresetConfig resetConfiguration(String userName);

    /**
     * gets all the history and statistics of the current machine
     *
     * @return DTOstatistics including the statistics of the machine
     */
    DTOstatistics getHistoryAndStatistics();

    /**
     * validates rotors input from user.
     *
     * @param rotorsIDs a String representing the rotor's id's
     * @return DTOstatus representing the status of the operation
     */
    DTOstatus validateRotors(String rotorsIDs, String userName);

    /**
     * validates window characters input from user.
     *
     * @param windowChars string of characters representing the windows characters
     * @return DTOstatus representing the status of the operation
     */
    DTOstatus validateWindowCharacters(String windowChars, String userName);

    /**
     * validate reflector input from the user.
     *
     * @param reflectorID the reflector's id as an integer
     * @return DTOstatus representing the status of the operation
     */
    DTOstatus validateReflector(int reflectorID, String userName);

    /**
     * validate plugs input from the user.
     *
     * @param plugs a String of plugs with no spaces of separators
     * @return DTOstatus representing the status of the operation
     */
    DTOstatus validatePlugs(String plugs, String userName);

    /**
     * @return true is the machine is configured. false otherwise
     */
    boolean getIsMachineConfigured(String userName);

    /**
     * @return the machine's alphabet as a String
     */
    String getMachineAlphabet(String userName);

    /**
     * true - for char by char cipher, false - for line by line.
     *
     * @param charByCharState the wanted state
     */
    void setCharByCharState(boolean charByCharState);

    /**
     * finishes the current cipher process (in char-by-char mode)
     */
    void doneCurrentCipherProcess();

    /**
     * @param uiAdapter       object that updates the ui element
     * @param textToDecipher  the text to decipher
     * @param difficultyLevel the difficulty level
     * @param taskSize        size of task
     */
    void startBruteForceProcess(UIAdapter uiAdapter, String textToDecipher, DifficultyLevel difficultyLevel,
                                int taskSize, int numOfSelectedAgents, String userName);

    /**
     * get all words in dictionary
     *
     * @return DTO contains Set of Strings
     */
    DTOdictionary getDictionaryWords(String userName);

    /**
     * cancel the thread pool execution
     */
    void stopBruteForceProcess(String userName);

    /**
     * pause the thread pool execution
     */
    void pauseBruteForceProcess(String userName);

    /**
     * resume the thread pool execution after being paused
     */
    void resumeBruteForceProcess(String userName);

    boolean isAllWordsInDictionary(String textToCipher, String userName);

    void addDecryptManager(String alliesName, String uboatUserName);

    Map<String, Battlefield> getBattleFieldManager();

    DTOallies getAlliesInfo(String uboatUserName);

    void setUboatReady(String userNameFromSession, boolean b);

    DTOstatus setAllieReady(String userNameFromSession, String uboatName, boolean b);

    boolean allClientsReady(String uboatName);

    DTOagentConclusions fetchNextCandidates(String uboatUserName);
}