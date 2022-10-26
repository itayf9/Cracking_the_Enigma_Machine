package uboat.body;

import uboat.app.MainController;
import uboat.app.MessageTone;
import uboat.body.screen1.codecallibration.CodeCalibrationController;
import uboat.body.screen1.machinedetails.MachineDetailsController;
import uboat.body.screen2.activeteamsarea.ActiveTeamsController;
import uboat.body.screen2.candidate.area.CandidatesAreaController;
import uboat.body.screen2.currentconfig.CurrentConfigController;
import uboat.body.screen2.dictionary.DictionaryController;
import uboat.body.screen2.encrypt.EncryptDecryptController;
import dto.DTOspecs;
import info.allie.AllieInfo;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import problem.Problem;
import uboat.winner.LoseWinAreaController;

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
     */
    public void bindComponents(BooleanProperty isMachineConfiguredProperty, ListProperty<Integer> inUseRotorsIDsProperty,
                               StringProperty currentWindowsCharactersProperty, StringProperty inUseReflectorSymbolProperty,
                               StringProperty inUsePlugs, ListProperty<Integer> currentNotchDistances, IntegerProperty cipherCounterProperty,
                               IntegerProperty totalDistinctCandidates, BooleanProperty isClientReady, BooleanProperty isProcessedText, BooleanProperty isLoseWinAreaMessageVisible) {


        // binds the components that need the isConfigured Boolean property.
        codeCalibration.disableProperty().bind(isClientReady.or(isLoseWinAreaMessageVisible));
        encryptDecrypt.disableProperty().bind(isMachineConfiguredProperty.not().or(isClientReady).or(isLoseWinAreaMessageVisible));
        candidatesArea.disableProperty().bind(isMachineConfiguredProperty.not());
        dictionary.disableProperty().bind(isMachineConfiguredProperty.not().or(isClientReady).or(isLoseWinAreaMessageVisible));
        encryptDecryptController.bindProcessButton(isProcessedText);

        // config bindings
        currentConfigController.bindConfigComponents(inUseRotorsIDsProperty, currentWindowsCharactersProperty, inUseReflectorSymbolProperty, inUsePlugs, currentNotchDistances, isMachineConfiguredProperty);

        // cipher counter property bind
        machineDetailsController.bindCipherCounterProperty(cipherCounterProperty);

        // brute force dashboard labels bind
        candidatesAreaController.bindInitPropertiesToLabels(totalDistinctCandidates);

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
        candidatesAreaController.clearOldResults();
    }

    public void appendNewWordToInputCipherText(String newWord) {
        encryptDecryptController.appendNewWordToInputCipherText(newWord);
    }

    public void setStatusMessage(String statusMessage, MessageTone messageTone) {
        mainController.setStatusMessage(statusMessage, messageTone);
    }

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

    public void setReady() {
        mainController.setReady();
    }

    public void updateAlliesInfo(List<AllieInfo> alliesInfoList) {
        activeTeamsController.setTeams(alliesInfoList);
    }

    public void clearTextFields() {
        encryptDecryptController.clearTextFields();
    }
}
