//package body;
//
//import app.MainController;
//import app.statusbar.MessageTone;
//import body.currentconfig.CurrentConfigController;
//import body.screen1.codecalibration.CodeCalibrationController;
//import body.screen1.machinedetails.MachineDetailsController;
//import body.screen2.encrypt.EncryptDecryptController;
//import body.screen2.statistics.StatisticsController;
//import body.screen3.candidate.area.CandidatesAreaController;
//import body.screen3.dictionary.DictionaryController;
//import body.screen3.dm.operational.dmOperationalController;
//import dm.difficultylevel.DifficultyLevel;
//import dto.*;
//import header.Skin;
//import javafx.beans.property.*;
//import javafx.fxml.FXML;
//import javafx.scene.Node;
//import javafx.scene.layout.GridPane;
//import problem.Problem;
//
//import java.net.URL;
//import java.util.List;
//import java.util.Set;
//
//public class BodyController {
//
//    private MainController mainController;
//
//    /**
//     * screen 1 controllers and components
//     */
//    @FXML
//    private GridPane codeCalibration;
//
//    @FXML
//    private CodeCalibrationController codeCalibrationController;
//
//    @FXML
//    private GridPane machineDetails;
//
//    @FXML
//    private MachineDetailsController machineDetailsController;
//
//    @FXML
//    private GridPane currentConfigScreen1;
//
//    @FXML
//    private CurrentConfigController currentConfigScreen1Controller;
//
//    /**
//     * screen 2 controllers and components
//     */
//
//    @FXML
//    private GridPane currentConfigScreen2;
//
//    @FXML
//    private CurrentConfigController currentConfigScreen2Controller;
//
//    @FXML
//    private GridPane encryptDecrypt;
//
//    @FXML
//    private EncryptDecryptController encryptDecryptController;
//
//    @FXML
//    private GridPane statistics;
//
//    @FXML
//    private StatisticsController statisticsController;
//
//    /**
//     * screen 3 controllers and components
//     */
//
//    @FXML
//    private GridPane currentConfigScreen3;
//
//    @FXML
//    private CurrentConfigController currentConfigScreen3Controller;
//
//    @FXML
//    private GridPane encryptDecrypt2;
//
//    @FXML
//    private EncryptDecryptController encryptDecrypt2Controller;
//
//    @FXML
//    private GridPane dmOperational;
//
//    @FXML
//    private dmOperationalController dmOperationalController;
//
//    @FXML
//    private GridPane candidatesArea;
//
//    @FXML
//    private CandidatesAreaController candidatesAreaController;
//
//    @FXML
//    private GridPane dictionary;
//
//    @FXML
//    private DictionaryController dictionaryController;
//
//
//    /**
//     * set up the application, connecting the controllers to their main controller
//     */
//    @FXML
//    public void initialize() {
//
//        //screen 1
//        codeCalibrationController.setParentController(this);
//        machineDetailsController.setParentController(this);
//        currentConfigScreen1Controller.setParentController(this);
//
//        //screen 2
//        currentConfigScreen2Controller.setParentController(this);
//        encryptDecryptController.setParentController(this);
//        statisticsController.setParentController(this);
//
//        //screen 3
//        currentConfigScreen3Controller.setParentController(this);
//        encryptDecrypt2Controller.setParentController(this);
//        dmOperationalController.setParentController(this);
//        candidatesAreaController.setParentController(this);
//        dictionaryController.setParentController(this);
//
//        dmOperationalController.bindTextToDecipherPropertyToOutputCipher(encryptDecrypt2Controller.getOutputLabelProperty());
//
//        encryptDecrypt2Controller.setAvailabilityOfCharByCharMode(false);
//        encryptDecrypt2Controller.showLightBulbs(false);
//        encryptDecrypt2Controller.setOnActionProcessToBruteForceMode();
//    }
//
//    /**
//     * set Parent Controller
//     *
//     * @param mainController the Parent Controller
//     */
//    public void setMainController(MainController mainController) {
//        this.mainController = mainController;
//    }
//
//    /**
//     * Q2 display machine specifications
//     *
//     * @param specsStatus DTO contains machine specs
//     */
//    public void displayMachineSpecs(DTOspecs specsStatus) {
//        machineDetailsController.displayMachineDetails(specsStatus);
//    }
//
//    /**
//     * Q4 -> going up the chain to the main controller.
//     */
//    public void setRandomMachineConfig() {
//        mainController.setRandomMachineConfig();
//        //encryptDecryptController.setAllowEncryptDecrypt(true);
//    }
//
//    /**
//     * rotors validation
//     *
//     * @param rotorsIds the rotors ids
//     * @return status
//     */
//    public DTOstatus validateRotorsInput(String rotorsIds) {
//        return mainController.validateRotorsInput(rotorsIds);
//    }
//
//    /**
//     * window character validation
//     *
//     * @param windows the windows characters
//     * @return status
//     */
//    public DTOstatus validateWindowsCharsInput(String windows) {
//        return mainController.validateWindowsCharsInput(windows);
//    }
//
//    /**
//     * validate chosen reflector
//     *
//     * @param currentReflector reflector number
//     * @return status
//     */
//    public DTOstatus validateReflectorInput(int currentReflector) {
//        return mainController.validateReflectorInput(currentReflector);
//    }
//
//    /**
//     * validate plugs
//     *
//     * @param plugs the plugs
//     * @return status
//     */
//    public DTOstatus validatePlugsInput(String plugs) {
//        return mainController.validatePlugsInput(plugs);
//    }
//
//    /**
//     * Q3 -> assuming all components valid and machine will be configured!
//     *
//     * @param rotors    rotor ids
//     * @param windows   window characters
//     * @param reflector reflector number
//     * @param plugs     plugs
//     */
//    public void setManualMachineConfig(String rotors, String windows, int reflector, String plugs) {
//        mainController.setManualMachineConfig(rotors, windows, reflector, plugs);
//        //encryptDecryptController.setAllowEncryptDecrypt(true);
//    }
//
//    /**
//     * Q5 -> going up the chain to the main controller
//     *
//     * @param character key pressed
//     * @return status and ciphered key
//     */
//    public DTOciphertext cipher(String character) {
//        return mainController.cipher(character);
//    }
//
//    /**
//     * Q6 -> going up the chain to mainController
//     */
//    public void resetMachineConfiguration() {
//        mainController.resetMachineConfiguration();
//    }
//
//    /**
//     * gets the stats from main controller and send it to ths stats controller
//     */
//    public void displayStatistics() {
//        DTOstatistics statistics = mainController.fetchStats();
//        statisticsController.displayStats(statistics);
//    }
//
//    /**
//     * init the lightBulbs
//     *
//     * @param machineAlphabet String of alphabet
//     */
//    public void setLightBulb(String machineAlphabet) {
//        encryptDecryptController.initAlphabetLightBulbs(machineAlphabet);
//    }
//
//    /**
//     * a function that tells the main controller to update the char-by-char mode in the engine
//     *
//     * @param newCharByCharCipherMode true or false
//     */
//    public void setCharByCharCipherMode(boolean newCharByCharCipherMode) {
//        mainController.setCharByCharCipherMode(newCharByCharCipherMode);
//    }
//
//    /**
//     * function that tells the engine we finished ciphering in char-by-char
//     */
//    public void doneCurrentCipherProcess() {
//        mainController.doneCurrentCipherProcess();
//    }
//
//    /**
//     * init the binding of all subclass components
//     *
//     * @param isMachineConfiguredProperty          isMachineConfiguredProperty
//     * @param inUseRotorsIDsProperty               inUseRotorsIDsProperty
//     * @param currentWindowsCharactersProperty     currentWindowsCharactersProperty
//     * @param inUseReflectorSymbolProperty         inUseReflectorSymbolProperty
//     * @param inUsePlugs                           inUsePlugs
//     * @param currentNotchDistances                currentNotchDistances
//     * @param isCharByCharModeProperty             isCharByCharModeProperty
//     * @param cipherCounterProperty                cipherCounterProperty
//     * @param totalDistinctCandidates              totalDistinctCandidates
//     * @param totalProcessedConfigurations         totalProcessedConfigurations
//     * @param totalPossibleConfigurations          totalPossibleConfigurations
//     * @param bruteForceProgressBar                bruteForceProgressBar
//     * @param bruteForceProgressBarPercentageLabel bruteForceProgressBarPercentageLabel
//     * @param bruteForceStatusMessage              bruteForceStatusMessage
//     * @param isBruteForceTaskActive               isBruteForceTaskActive
//     * @param totalTimeDecryptProperty             totalTimeDecryptProperty
//     */
//    public void bindComponents(BooleanProperty isMachineConfiguredProperty, ListProperty<Integer> inUseRotorsIDsProperty,
//                               StringProperty currentWindowsCharactersProperty, StringProperty inUseReflectorSymbolProperty,
//                               StringProperty inUsePlugs, ListProperty<Integer> currentNotchDistances, BooleanProperty isCharByCharModeProperty,
//                               IntegerProperty cipherCounterProperty, IntegerProperty totalDistinctCandidates,
//                               IntegerProperty totalProcessedConfigurations, LongProperty totalPossibleConfigurations,
//                               DoubleProperty bruteForceProgressBar, StringProperty bruteForceProgressBarPercentageLabel,
//                               StringProperty bruteForceStatusMessage, BooleanProperty isBruteForceTaskActive, BooleanProperty isBruteForceTaskPaused,
//                               DoubleProperty averageTasksProcessTimeProperty, LongProperty totalTimeDecryptProperty) {
//
//        // binds the components that need the isConfigured Boolean property.
//        encryptDecrypt.disableProperty().bind(isMachineConfiguredProperty.not());
//        encryptDecrypt2.disableProperty().bind(isMachineConfiguredProperty.not().or(isBruteForceTaskActive));
//        statistics.disableProperty().bind(isMachineConfiguredProperty.not());
//        dmOperational.disableProperty().bind(isMachineConfiguredProperty.not());
//        candidatesArea.disableProperty().bind(isMachineConfiguredProperty.not());
//        dictionary.disableProperty().bind(isMachineConfiguredProperty.not().or(isBruteForceTaskActive));
//
//        // config bindings
//        currentConfigScreen1Controller.bindConfigComponents(inUseRotorsIDsProperty, currentWindowsCharactersProperty, inUseReflectorSymbolProperty, inUsePlugs, currentNotchDistances, isMachineConfiguredProperty);
//        currentConfigScreen2Controller.bindConfigComponents(inUseRotorsIDsProperty, currentWindowsCharactersProperty, inUseReflectorSymbolProperty, inUsePlugs, currentNotchDistances, isMachineConfiguredProperty);
//        currentConfigScreen3Controller.bindConfigComponents(inUseRotorsIDsProperty, currentWindowsCharactersProperty, inUseReflectorSymbolProperty, inUsePlugs, currentNotchDistances, isMachineConfiguredProperty);
//
//        // cipher mode (Char By Char / Full line) bindings
//        encryptDecryptController.bindCipherMode(isCharByCharModeProperty);
//
//        // cipher counter property bind
//        machineDetailsController.bindCipherCounterProperty(cipherCounterProperty);
//
//        // brute force dashboard labels bind
//        candidatesAreaController.bindInitPropertiesToLabels(isBruteForceTaskActive, totalDistinctCandidates, totalProcessedConfigurations, totalPossibleConfigurations,
//                bruteForceProgressBar, bruteForceProgressBarPercentageLabel
//                , bruteForceStatusMessage, averageTasksProcessTimeProperty, totalTimeDecryptProperty);
//
//        // dm operational bind
//        dmOperationalController.bindComponents(isBruteForceTaskActive, isBruteForceTaskPaused);
//    }
//
//    /**
//     * updates the original machine config at every load
//     *
//     * @param rotorsIDs                rotorsIDs
//     * @param currentWindowsCharacters window characters
//     * @param inUseReflectorSymbol     I, II, III, IV, X
//     * @param inUsePlugs               Plugs
//     * @param currentNotchDistances    Notch positions
//     */
//    public void displayOriginalConfig(List<Integer> rotorsIDs, String currentWindowsCharacters, String inUseReflectorSymbol, String inUsePlugs, List<Integer> currentNotchDistances) {
//        machineDetailsController.displayOriginalConfiguration(rotorsIDs, currentWindowsCharacters, inUseReflectorSymbol, inUsePlugs, currentNotchDistances);
//    }
//
//    /**
//     * sets dmController settings like spinner Max-Value & agent Max-Value.
//     *
//     * @param totalPossibleWindowsPositions the amount needed to complete one lap of all windows configs possible
//     * @param maxNumOfAgents                Max num of agent from the xml file
//     * @param specStatus                    machine specifications
//     */
//    public void setDMOperetionalSettings(LongProperty totalPossibleWindowsPositions, int maxNumOfAgents, DTOspecs specStatus) {
//        dmOperationalController.setSettings(totalPossibleWindowsPositions, maxNumOfAgents, specStatus);
//    }
//
//
//    public void startBruteForce(String textToDecipher, DifficultyLevel difficultyLevel, int taskSize, int numOfAgentSelected) {
//        mainController.startBruteForceProcess(textToDecipher, difficultyLevel, taskSize, numOfAgentSelected);
//    }
//
//    public void insertCandidateToFlowPane(Node singleCandidateTile) {
//        candidatesAreaController.insertCandidateToFlowPane(singleCandidateTile);
//    }
//
//    public void setDictionaryWords(Set<String> dictionaryWords, String alphabet) {
//        dictionaryController.setDictionaryWords(dictionaryWords, alphabet);
//    }
//
//    public void stopBruteForce() {
//        mainController.stopBruteForceProcess();
//    }
//
//    public void clearOldResultsOfBruteForce() {
//        candidatesAreaController.clearOldResultsOfBruteForce();
//    }
//
//    public DTOspecs getMachineSpecs() {
//        return mainController.fetchSpecs();
//    }
//
//    public void appendNewWordToInputCipherText(String newWord) {
//        encryptDecrypt2Controller.appendNewWordToInputCipherText(newWord);
//    }
//
//    public void setStatusMessage(String statusMessage, MessageTone messageTone) {
//        mainController.setStatusMessage(statusMessage, messageTone);
//    }
//
//    public void pauseBruteForce() {
//        mainController.pauseBruteForceProcess();
//    }
//
//    public void resumeBruteForce() {
//        mainController.resumeBruteForceProcess();
//    }
//
//    /**
//     * changes skin of all subcomponents
//     *
//     * @param appUrl appUrl
//     * @param skin   skin
//     */
//    public void setComponentsSkin(String appUrl, Skin skin) {
//
//        // removes all stylesheets
//        currentConfigScreen1.getStylesheets().removeAll(currentConfigScreen1.getStylesheets());
//        machineDetails.getStylesheets().removeAll(machineDetails.getStylesheets());
//        codeCalibration.getStylesheets().removeAll(codeCalibration.getStylesheets());
//
//        currentConfigScreen2.getStylesheets().removeAll(currentConfigScreen2.getStylesheets());
//        encryptDecrypt.getStylesheets().removeAll(encryptDecrypt.getStylesheets());
//        statistics.getStylesheets().removeAll(statistics.getStylesheets());
//
//        currentConfigScreen3.getStylesheets().removeAll(currentConfigScreen3.getStylesheets());
//        encryptDecrypt2.getStylesheets().removeAll(encryptDecrypt2.getStylesheets());
//        dictionary.getStylesheets().removeAll(dictionary.getStylesheets());
//        dmOperational.getStylesheets().removeAll(dmOperational.getStylesheets());
//        candidatesArea.getStylesheets().removeAll(candidatesArea.getStylesheets());
//
//
//        // adds new "app" stylesheet
//        currentConfigScreen1.getStylesheets().add(appUrl);
//        machineDetails.getStylesheets().add(appUrl);
//        codeCalibration.getStylesheets().add(appUrl);
//
//        currentConfigScreen2.getStylesheets().add(appUrl);
//        encryptDecrypt.getStylesheets().add(appUrl);
//        statistics.getStylesheets().add(appUrl);
//
//        currentConfigScreen3.getStylesheets().add(appUrl);
//        encryptDecrypt2.getStylesheets().add(appUrl);
//        dictionary.getStylesheets().add(appUrl);
//        dmOperational.getStylesheets().add(appUrl);
//        candidatesArea.getStylesheets().add(appUrl);
//
//        // adds new "specific" stylesheets
//        URL currentConfigUrl = getClass().getResource("/body/currentconfig/currentConfig-" + skin.skinName() + ".css");
//        currentConfigScreen1.getStylesheets().add(currentConfigUrl.toString());
//        currentConfigScreen2.getStylesheets().add(currentConfigUrl.toString());
//        currentConfigScreen3.getStylesheets().add(currentConfigUrl.toString());
//
//        URL machineDetailsUrl = getClass().getResource("/body/screen1/machinedetails/machineDetails-" + skin.skinName() + ".css");
//        machineDetails.getStylesheets().add(machineDetailsUrl.toString());
//
//        URL codeCalibrationUrl = getClass().getResource("/body/screen1/codecalibration/codeCalibration-" + skin.skinName() + ".css");
//        codeCalibration.getStylesheets().add(codeCalibrationUrl.toString());
//
//        URL encryptDecryptUrl = getClass().getResource("/body/screen2/encrypt/encrypt-decrypt-" + skin.skinName() + ".css");
//        encryptDecrypt.getStylesheets().add(encryptDecryptUrl.toString());
//        encryptDecrypt2.getStylesheets().add(encryptDecryptUrl.toString());
//
//        URL statisticsUrl = getClass().getResource("/body/screen2/statistics/statistics-" + skin.skinName() + ".css");
//        statistics.getStylesheets().add(statisticsUrl.toString());
//
//        URL dictionaryUrl = getClass().getResource("/body/screen3/dictionary/dictionary-" + skin.skinName() + ".css");
//        dictionary.getStylesheets().add(dictionaryUrl.toString());
//
//        URL dmOperationalUrl = getClass().getResource("/body/screen3/dm/operational/dmOperational-" + skin.skinName() + ".css");
//        dmOperational.getStylesheets().add(dmOperationalUrl.toString());
//
//        URL candidatesAreaUrl = getClass().getResource("/body/screen3/candidate/area/candidatesArea-" + skin.skinName() + ".css");
//        candidatesArea.getStylesheets().add(candidatesAreaUrl.toString());
//
//        // sets the original config component
//        machineDetailsController.setComponentsSkin(currentConfigUrl);
//
//    }
//
//    public void setEncryptExcludeCharsValue(StringProperty dictionaryExcludeCharsProperty) {
//        encryptDecrypt2Controller.setEncryptExcludeCharsValue(dictionaryExcludeCharsProperty);
//    }
//
//    public boolean isAllWordsInDictionary(String textToCipher) {
//        return mainController.isAllWordsInDictionary(textToCipher);
//    }
//
//    public void setIsAnimationPropertyEncryptDecrypt(BooleanProperty isAnimationProperty) {
//        encryptDecryptController.setIsAnimationPropertyEncryptDecrypt(isAnimationProperty);
//    }
//
//    public void setCodeCalibration(int inUseRotorsCount, int availableRotorsCount, String machineAlphabet, int availableReflectorsCount) {
//        codeCalibrationController.setCodeCalibration(inUseRotorsCount, availableRotorsCount, machineAlphabet, availableReflectorsCount);
//    }
//
//    public String convertProblemToMessage(Problem problem) {
//        return mainController.convertProblemToMessage(problem);
//    }
//
//    public void clearOldComponents() {
//        encryptDecryptController.clearTextFields();
//        encryptDecrypt2Controller.clearTextFields();
//    }
//}
