package body;

import app.MainController;
import app.MessageTone;
import body.screen1.codecallibration.CodeCalibrationController;
import body.screen1.machinedetails.MachineDetailsController;
import body.screen2.activeteamsarea.ActiveTeamsController;
import body.screen2.candidate.area.CandidatesAreaController;
import body.screen2.currentconfig.CurrentConfigController;
import body.screen2.dictionary.DictionaryController;
import body.screen2.encrypt.EncryptDecryptController;
import dto.DTOciphertext;
import dto.DTOspecs;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import problem.Problem;

import java.util.List;
import java.util.Set;


public class BodyController {

    private MainController mainController;

    /**
     * screen 1 controllers and components
     */
    @FXML
    private GridPane codeCalibration;

    @FXML
    private CodeCalibrationController codeCalibrationController;

    @FXML
    private GridPane machineDetails;

    @FXML
    private MachineDetailsController machineDetailsController;

    /**
     * screen 2 controllers and components
     */

    @FXML
    private GridPane currentConfig;

    @FXML
    private CurrentConfigController currentConfigController;

    @FXML
    private GridPane encryptDecrypt;

    @FXML
    private EncryptDecryptController encryptDecryptController;

    @FXML
    private GridPane candidatesArea;

    @FXML
    private CandidatesAreaController candidatesAreaController;

    @FXML
    private GridPane dictionary;

    @FXML
    private DictionaryController dictionaryController;

    @FXML
    private GridPane activeTeams;

    @FXML
    private ActiveTeamsController activeTeamsController;


    /**
     * set up the application, connecting the controllers to their main controller
     */
    @FXML
    public void initialize() {

        //screen 1
        codeCalibrationController.setParentController(this);
        machineDetailsController.setParentController(this);

        //screen 2
        currentConfigController.setParentController(this);
        encryptDecryptController.setParentController(this);
        candidatesAreaController.setParentController(this);
        dictionaryController.setParentController(this);
        activeTeamsController.setParentController(this);
    }

    /**
     * set Parent Controller
     *
     * @param mainController the Parent Controller
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Q2 display machine specifications
     *
     * @param specsStatus DTO contains machine specs
     */
    public void displayMachineSpecs(DTOspecs specsStatus) {
        machineDetailsController.displayMachineDetails(specsStatus);
    }

    /**
     * Q4 -> going up the chain to the main controller.
     */
    public void setRandomMachineConfig() {
        mainController.setRandomMachineConfig();
        //encryptDecryptController.setAllowEncryptDecrypt(true);
    }

    /**
     * Q3 -> assuming all components valid and machine will be configured!
     *
     * @param rotors    rotor ids
     * @param windows   window characters
     * @param reflector reflector number
     * @param plugs     plugs
     */
    public void setManualMachineConfig(String rotors, String windows, int reflector, String plugs) {
        mainController.setManualMachineConfig(rotors, windows, reflector, plugs);
    }

    /**
     * Q5 -> going up the chain to the main controller
     *
     * @param character key pressed
     * @return status and ciphered key
     */
    public void cipher(String character) {
        mainController.cipher(character);
    }

    /**
     * Q6 -> going up the chain to mainController
     */
    public void resetMachineConfiguration() {
        mainController.resetMachineConfiguration();
    }

    /**
     * init the binding of all subclass components
     *
     * @param isMachineConfiguredProperty          isMachineConfiguredProperty
     * @param inUseRotorsIDsProperty               inUseRotorsIDsProperty
     * @param currentWindowsCharactersProperty     currentWindowsCharactersProperty
     * @param inUseReflectorSymbolProperty         inUseReflectorSymbolProperty
     * @param inUsePlugs                           inUsePlugs
     * @param currentNotchDistances                currentNotchDistances
     * @param cipherCounterProperty                cipherCounterProperty
     * @param totalDistinctCandidates              totalDistinctCandidates
     * @param totalProcessedConfigurations         totalProcessedConfigurations
     * @param totalPossibleConfigurations          totalPossibleConfigurations
     * @param bruteForceProgressBar                bruteForceProgressBar
     * @param bruteForceProgressBarPercentageLabel bruteForceProgressBarPercentageLabel
     * @param bruteForceStatusMessage              bruteForceStatusMessage
     * @param isBruteForceTaskActive               isBruteForceTaskActive
     * @param totalTimeDecryptProperty             totalTimeDecryptProperty
     */
    public void bindComponents(BooleanProperty isMachineConfiguredProperty, ListProperty<Integer> inUseRotorsIDsProperty,
                               StringProperty currentWindowsCharactersProperty, StringProperty inUseReflectorSymbolProperty,
                               StringProperty inUsePlugs, ListProperty<Integer> currentNotchDistances, IntegerProperty cipherCounterProperty,
                               IntegerProperty totalDistinctCandidates, IntegerProperty totalProcessedConfigurations,
                               LongProperty totalPossibleConfigurations, DoubleProperty bruteForceProgressBar,
                               StringProperty bruteForceProgressBarPercentageLabel, StringProperty bruteForceStatusMessage,
                               BooleanProperty isBruteForceTaskActive, DoubleProperty averageTasksProcessTimeProperty, LongProperty totalTimeDecryptProperty) {

        // binds the components that need the isConfigured Boolean property.
        encryptDecrypt.disableProperty().bind(isMachineConfiguredProperty.not().or(isBruteForceTaskActive));
        candidatesArea.disableProperty().bind(isMachineConfiguredProperty.not());
        dictionary.disableProperty().bind(isMachineConfiguredProperty.not().or(isBruteForceTaskActive));

        // config bindings
        currentConfigController.bindConfigComponents(inUseRotorsIDsProperty, currentWindowsCharactersProperty, inUseReflectorSymbolProperty, inUsePlugs, currentNotchDistances, isMachineConfiguredProperty);

        // cipher counter property bind
        machineDetailsController.bindCipherCounterProperty(cipherCounterProperty);

        // brute force dashboard labels bind
        candidatesAreaController.bindInitPropertiesToLabels(isBruteForceTaskActive, totalDistinctCandidates, totalProcessedConfigurations, totalPossibleConfigurations,
                bruteForceProgressBar, bruteForceProgressBarPercentageLabel, bruteForceStatusMessage, averageTasksProcessTimeProperty, totalTimeDecryptProperty);

    }

    /**
     * updates the original machine config at every load
     *
     * @param rotorsIDs                rotorsIDs
     * @param currentWindowsCharacters window characters
     * @param inUseReflectorSymbol     I, II, III, IV, X
     * @param inUsePlugs               Plugs
     * @param currentNotchDistances    Notch positions
     */
    public void displayOriginalConfig(List<Integer> rotorsIDs, String currentWindowsCharacters, String inUseReflectorSymbol, String inUsePlugs, List<Integer> currentNotchDistances) {
        machineDetailsController.displayOriginalConfiguration(rotorsIDs, currentWindowsCharacters, inUseReflectorSymbol, inUsePlugs, currentNotchDistances);
    }

    public void insertCandidateToFlowPane(Node singleCandidateTile) {
        candidatesAreaController.insertCandidateToFlowPane(singleCandidateTile);
    }

    public void setDictionaryWords(Set<String> dictionaryWords, String alphabet) {
        dictionaryController.setDictionaryWords(dictionaryWords, alphabet);
    }

    public void clearOldResultsOfBruteForce() {
        candidatesAreaController.clearOldResultsOfBruteForce();
    }

    public void appendNewWordToInputCipherText(String newWord) {
        encryptDecryptController.appendNewWordToInputCipherText(newWord);
    }

    public void setStatusMessage(String statusMessage, MessageTone messageTone) {
        mainController.setStatusMessage(statusMessage, messageTone);
    }

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
//        currentConfigScreen.getStylesheets().removeAll(currentConfigScreen.getStylesheets());
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
//        currentConfigScreen.getStylesheets().add(appUrl);
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
//        currentConfigScreen.getStylesheets().add(currentConfigUrl.toString());
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

    public void setEncryptExcludeCharsValue(StringProperty dictionaryExcludeCharsProperty) {
        encryptDecryptController.setEncryptExcludeCharsValue(dictionaryExcludeCharsProperty);
    }

    public void setCodeCalibration(int inUseRotorsCount, int availableRotorsCount, String machineAlphabet, int availableReflectorsCount) {
        codeCalibrationController.setCodeCalibration(inUseRotorsCount, availableRotorsCount, machineAlphabet, availableReflectorsCount);
    }

    public String convertProblemToMessage(Problem problem) {
        return mainController.convertProblemToMessage(problem);
    }

    public void clearOldComponents() {
        encryptDecryptController.clearTextFields();
        encryptDecryptController.clearTextFields();
    }

    public void setCipherOutput(Problem problem, String cipherText) {
        encryptDecryptController.setCipherOutput(problem, cipherText);
    }
}
