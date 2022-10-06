package app;//package app;
//
//import app.statusbar.MessageTone;
//import bindings.CurrWinCharsAndNotchPosBinding;
//import body.BodyController;
//import body.screen3.candidate.tile.CandidateTileController;
//import candidate.Candidate;
//import dm.difficultylevel.DifficultyLevel;
//import dto.*;
//import engine.Engine;
//import engine.EnigmaEngine;
//import header.HeaderController;
//import header.Skin;
//import javafx.animation.FadeTransition;
//import javafx.beans.binding.Bindings;
//import javafx.beans.property.*;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.control.Label;
//import javafx.scene.control.TabPane;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.shape.Rectangle;
//import javafx.util.Duration;
//import problem.Problem;
//import statistics.StatisticRecord;
//import ui.adapter.UIAdapter;
//
//import java.io.IOException;
//import java.net.URL;
//
//public class MainController {
//
//    /**
//     * app private members
//     */
//    private final Engine engine = new EnigmaEngine();
//    @FXML
//    private GridPane header;
//    @FXML
//    private HeaderController headerController;
//
//    @FXML
//    private GridPane appGridPane;
//
//    @FXML
//    private ImageView startImage;
//
//    @FXML
//    private TabPane body;
//    @FXML
//    private BodyController bodyController;
//
//    @FXML
//    private HBox statusBar;
//
//    @FXML
//    private Label statusLabel;
//
//    @FXML
//    private Label messageLabel;
//
//    @FXML
//    private Rectangle statusBackShape;
//
//    private FadeTransition messageFadeTransition;
//
//    private DTOsecretConfig configStatus; // do we still need this!!!!!!?????
//
//    /**
//     * property stuff
//     */
//    private BooleanProperty isMachineConfiguredProperty;
//    private BooleanProperty isMachineLoadedProperty;
//
//    // these properties would store the data of current config component.
//    private ListProperty<Integer> inUseRotorsIDsProperty;
//    private StringProperty currentWindowsCharactersProperty;
//    private StringProperty inUseReflectorSymbolProperty;
//    private StringProperty inUsePlugsProperty;
//    private ListProperty<Integer> currentNotchDistances;
//
//    private CurrWinCharsAndNotchPosBinding currWinCharsAndNotchPosBinding;
//    private IntegerProperty cipherCounterProperty;
//    private BooleanProperty isCharByCharModeProperty;
//    private ListProperty<StatisticRecord> statisticsProperty;
//
//    /**
//     * bruteforce stuff
//     */
//
//    private IntegerProperty totalDistinctCandidates;
//    private IntegerProperty totalProcessedConfigurations;
//    private LongProperty totalPossibleConfigurations;
//    private DoubleProperty bruteForceProgress;
//    private StringProperty bruteForceProgressBarPercentageProperty;
//    private StringProperty bruteForceStatusMessage;
//    private BooleanProperty isBruteForceTaskActive;
//    private BooleanProperty isBruteForceTaskPaused;
//    private LongProperty totalPossibleWindowsPositions;
//    private DoubleProperty averageTasksProcessTimeProperty;
//    private LongProperty totalTimeDecryptProperty;
//    private StringProperty dictionaryExcludeCharsProperty;
//
//    private BooleanProperty isAnimationProperty;
//
//
//    @FXML
//    public void initialize() {
//
//        // controller initialize
//        if (headerController != null && bodyController != null) {
//            headerController.setMainController(this);
//            bodyController.setMainController(this);
//
//            // property initialize
//            this.isMachineConfiguredProperty = new SimpleBooleanProperty(false);
//            this.isMachineLoadedProperty = new SimpleBooleanProperty(false);
//            this.inUseRotorsIDsProperty = new SimpleListProperty<>();
//            this.currentWindowsCharactersProperty = new SimpleStringProperty("");
//            this.inUseReflectorSymbolProperty = new SimpleStringProperty("");
//            this.inUsePlugsProperty = new SimpleStringProperty("");
//            this.currentNotchDistances = new SimpleListProperty<>();
//            this.isCharByCharModeProperty = new SimpleBooleanProperty(false);
//            this.cipherCounterProperty = new SimpleIntegerProperty(0);
//            this.statisticsProperty = new SimpleListProperty<>();
//            this.totalDistinctCandidates = new SimpleIntegerProperty();
//            this.totalProcessedConfigurations = new SimpleIntegerProperty();
//            this.totalPossibleConfigurations = new SimpleLongProperty();
//            this.messageFadeTransition = new FadeTransition(Duration.millis(5000), statusBackShape);
//            this.isBruteForceTaskActive = new SimpleBooleanProperty(false);
//            this.isBruteForceTaskPaused = new SimpleBooleanProperty(false);
//            this.averageTasksProcessTimeProperty = new SimpleDoubleProperty();
//            this.dictionaryExcludeCharsProperty = new SimpleStringProperty();
//            this.isAnimationProperty = new SimpleBooleanProperty(true);
//            this.bruteForceProgress = new SimpleDoubleProperty();
//            this.bruteForceStatusMessage = new SimpleStringProperty("");
//            this.bruteForceProgressBarPercentageProperty = new SimpleStringProperty("0%");
//            this.totalPossibleWindowsPositions = new SimpleLongProperty();
//            this.totalTimeDecryptProperty = new SimpleLongProperty();
//
//            // binding initialize
//            bodyController.bindComponents(isMachineConfiguredProperty, inUseRotorsIDsProperty,
//                    currentWindowsCharactersProperty, inUseReflectorSymbolProperty, inUsePlugsProperty,
//                    currentNotchDistances, isCharByCharModeProperty, cipherCounterProperty, totalDistinctCandidates,
//                    totalProcessedConfigurations, totalPossibleConfigurations, bruteForceProgress,
//                    bruteForceProgressBarPercentageProperty, bruteForceStatusMessage, isBruteForceTaskActive,
//                    isBruteForceTaskPaused, averageTasksProcessTimeProperty, totalTimeDecryptProperty);
//
//            // general setting to initialize sub components
//            body.visibleProperty().bind(isMachineLoadedProperty);
//            startImage.fitHeightProperty().bind(Bindings.when(isMachineLoadedProperty.not()).then(500).otherwise(1));
//            startImage.fitWidthProperty().bind(Bindings.when(isMachineLoadedProperty.not()).then(900).otherwise(1));
//            messageLabel.textProperty().bind(statusLabel.textProperty());
//            messageLabel.opacityProperty().bind(statusBackShape.opacityProperty());
//            statusBackShape.heightProperty().bind(Bindings.add(2, statusLabel.heightProperty()));
//            statusBackShape.widthProperty().bind(statusLabel.widthProperty());
//            statusBackShape.setStrokeWidth(0);
//            statusBackShape.setOpacity(0);
//
//            // header bindings & settings
//            headerController.setProperties(isAnimationProperty, isMachineLoadedProperty);
//            headerController.bindSettings(isAnimationProperty);
//
//            bodyController.setIsAnimationPropertyEncryptDecrypt(isAnimationProperty);
//
//            isMachineConfiguredProperty.addListener((observable, oldValue, newValue) -> clearOldComponents());
//
//            bruteForceStatusMessage.addListener((observable, oldValue, newValue) -> setStatusMessage("Decrypt Manager: " + newValue, MessageTone.INFO));
//        }
//    }
//
//    private void clearOldComponents() {
//        bodyController.clearOldComponents();
//    }
//
//    /**
//     * Q1 + Q2 Load the machine
//     *
//     * @param selectedMachineFile fileName
//     */
//    public void loadMachineFromFile(String selectedMachineFile) {
//        // check if brute force task is running ?
//        if (isBruteForceTaskActive.getValue()) {
//
//            // let's stop the process
//            stopBruteForceProcess();
//
//        }
//
//        // then load new machine
//        DTOstatus loadStatus = engine.buildMachineFromXmlFile(selectedMachineFile);
//        if (!loadStatus.isSucceed()) {
//            setStatusMessage("Could not load that file. " + convertProblemToMessage(loadStatus.getDetails()), MessageTone.ERROR);
//        } else {
//            headerController.displayFilePath();
//            DTOspecs specsStatus = engine.displayMachineSpecifications();
//            int rotorsCount = specsStatus.getInUseRotorsCount();
//            int alphabetLength = engine.getMachineAlphabet().length();
//            setStatusMessage("Machine Loaded Successfully!", MessageTone.SUCCESS);
//
//            // clear old current config
//            inUseRotorsIDsProperty.clear();
//            currentWindowsCharactersProperty.set("");
//            inUseReflectorSymbolProperty.set("");
//            currentNotchDistances.clear();
//            inUsePlugsProperty.set("");
//
//            // set new stuff
//            bodyController.setDictionaryWords(engine.getDictionaryWords().getDictionary(), engine.getMachineAlphabet());
//            bodyController.displayMachineSpecs(specsStatus);
//            cipherCounterProperty.set(0);
//            bodyController.setLightBulb(engine.getMachineAlphabet());
//            bodyController.displayStatistics();
//            this.totalPossibleWindowsPositions.setValue(Math.pow(alphabetLength, rotorsCount));
//            bodyController.setEncryptExcludeCharsValue(dictionaryExcludeCharsProperty);
//            bodyController.setDMOperetionalSettings(totalPossibleWindowsPositions, specsStatus.getNumOfAvailableAgents(), specsStatus);
//            headerController.enableLoadButtonTransition(false);
//            isMachineConfiguredProperty.setValue(Boolean.FALSE);
//            isMachineLoadedProperty.setValue(Boolean.TRUE);
//            dictionaryExcludeCharsProperty.setValue(specsStatus.getDictionaryExcludeCharacters());
//            bodyController.setCodeCalibration(specsStatus.getInUseRotorsCount(), specsStatus.getAvailableRotorsCount(), engine.getMachineAlphabet(), specsStatus.getAvailableReflectorsCount());
//        }
//    }
//
//    /**
//     * Q3 set manual config
//     *
//     * @param rotors    rotors ids
//     * @param windows   window characters
//     * @param reflector reflector number
//     * @param plugs     plugs
//     */
//    public void setManualMachineConfig(String rotors, String windows, int reflector, String plugs) {
//        DTOsecretConfig configStatus = engine.selectConfigurationManual(rotors, windows, reflector, plugs);
//
//        ObservableList<Integer> rotorsObservableList = FXCollections.observableArrayList(configStatus.getRotors());
//        inUseRotorsIDsProperty.setValue(rotorsObservableList);
//
//        currentWindowsCharactersProperty.setValue(configStatus.getWindows());
//        inUseReflectorSymbolProperty.setValue(configStatus.getReflectorSymbol());
//        inUsePlugsProperty.setValue(configStatus.getPlugs());
//
//        ObservableList<Integer> notchDistanceObservableList = FXCollections.observableArrayList(configStatus.getNotchDistances());
//        currentNotchDistances.setValue(notchDistanceObservableList);
//
//        // display original config in machine specs
//        bodyController.displayOriginalConfig(configStatus.getRotors(), configStatus.getWindows(), configStatus.getReflectorSymbol(), configStatus.getPlugs(), configStatus.getNotchDistances());
//
//        setStatusMessage("Configured Successfully", MessageTone.SUCCESS);
//
//        isMachineConfiguredProperty.setValue(Boolean.TRUE);
//    }
//
//    /**
//     * Q4 set configuration auto
//     */
//    public void setRandomMachineConfig() {
//        DTOsecretConfig configStatus = engine.selectConfigurationAuto();
//
//        ObservableList<Integer> rotorsObservableList = FXCollections.observableArrayList(configStatus.getRotors());
//        inUseRotorsIDsProperty.setValue(rotorsObservableList);
//
//        currentWindowsCharactersProperty.setValue(configStatus.getWindows());
//        inUseReflectorSymbolProperty.setValue(configStatus.getReflectorSymbol());
//        inUsePlugsProperty.setValue(configStatus.getPlugs());
//
//        ObservableList<Integer> notchDistanceObservableList = FXCollections.observableArrayList(configStatus.getNotchDistances());
//        currentNotchDistances.setValue(notchDistanceObservableList);
//
//        setStatusMessage("Configured Successfully", MessageTone.SUCCESS);
//
//        isMachineConfiguredProperty.setValue(Boolean.TRUE);
//
//        bodyController.displayOriginalConfig(inUseRotorsIDsProperty.getValue(), currentWindowsCharactersProperty.getValue(), inUseReflectorSymbolProperty.getValue(), inUsePlugsProperty.getValue(), currentNotchDistances.getValue());
//    }
//
//    /**
//     * Q5 cipher character
//     *
//     * @param character String that contains one character
//     * @return ciphered Character
//     */
//    public DTOciphertext cipher(String character) {
//        DTOciphertext cipherStatus = engine.cipherInputText(character);
//        DTOspecs specsStatus = engine.displayMachineSpecifications();
//
//        // update configuration
//        currentWindowsCharactersProperty.setValue(specsStatus.getCurrentWindowsCharacters());
//        ObservableList<Integer> notchDistanceObservableList = FXCollections.observableArrayList(specsStatus.getNotchDistancesToWindow());
//        currentNotchDistances.setValue(notchDistanceObservableList);
//        cipherCounterProperty.setValue(specsStatus.getCipheredTextsCount());
//
//        DTOstatistics statsStatus = engine.getHistoryAndStatistics();
//        // update statistics
//        ObservableList<StatisticRecord> statisticsObservableList = FXCollections.observableArrayList(statsStatus.getStats());
//        statisticsProperty.setValue(statisticsObservableList);
//
//        return cipherStatus;
//    }
//
//    /**
//     * Q6 reset configuration
//     */
//    public void resetMachineConfiguration() {
//        engine.resetConfiguration();
//
//        DTOspecs specsStatus = engine.displayMachineSpecifications();
//
//        currentWindowsCharactersProperty.setValue(specsStatus.getOriginalWindowsCharacters()); // current should be the same here
//
//        ObservableList<Integer> notchDistanceObservableList = FXCollections.observableArrayList(specsStatus.getOriginalNotchPositions()); // current should be the same here
//        currentNotchDistances.setValue(notchDistanceObservableList);
//
//        setStatusMessage("Reset Successfully", MessageTone.SUCCESS);
//    }
//
//    /**
//     * gets all the statistics data from the engine
//     *
//     * @return all the stats gathered at the engine statistics
//     */
//    public DTOstatistics fetchStats() {
//        return engine.getHistoryAndStatistics();
//    }
//
//    /**
//     * gets all the specification data from the engine
//     *
//     * @return all the specification .of the current machine .from the engine
//     */
//    public DTOspecs fetchSpecs() {
//        return engine.displayMachineSpecifications();
//    }
//
//    /**
//     * configure the engine to which cipher state it needs to answer.
//     *
//     * @param newCharByCharCipherMode true for char-by-char mode, false otherwise
//     */
//    public void setCharByCharCipherMode(boolean newCharByCharCipherMode) {
//        engine.setCharByCharState(newCharByCharCipherMode);
//        isCharByCharModeProperty.set(newCharByCharCipherMode);
//        if (newCharByCharCipherMode) {
//            setStatusMessage("Switched to \"Char-By-Char\" Mode", MessageTone.INFO);
//        } else {
//            setStatusMessage("Switched to \"Full Line\" Mode", MessageTone.INFO);
//        }
//    }
//
//    /**
//     * this method initiates the brute force task at the engine
//     *
//     * @param textToDecipher     the text to decipher
//     * @param difficultyLevel    Easy, Medium, Hard or Impossible.
//     * @param taskSize           the size of single task every agent going to scan through
//     * @param numOfAgentSelected how many agents going wo work on this process
//     */
//    public void startBruteForceProcess(String textToDecipher, DifficultyLevel difficultyLevel, int taskSize, int numOfAgentSelected) {
//        isBruteForceTaskActive.set(true);
//        cleanOldResults();
//        UIAdapter uiAdapter = createUIAdapter();
//
//        //fire up the DM
//        engine.startBruteForceProcess(uiAdapter, textToDecipher, difficultyLevel, taskSize, numOfAgentSelected);
//    }
//
//    /**
//     * clear all findings of last process and labels progress
//     */
//    private void cleanOldResults() {
//        bodyController.clearOldResultsOfBruteForce();
//        bruteForceProgress.set(0);
//        totalDistinctCandidates.set(0);
//        totalProcessedConfigurations.set(0);
//        averageTasksProcessTimeProperty.set(0);
//    }
//
//    /**
//     * create & configure how the ui adapter will change each property of Main Controller.
//     *
//     * @return ui adapter object
//     */
//    private UIAdapter createUIAdapter() {
//        return new UIAdapter(
//                this::createCandidateTile,
//                () -> totalDistinctCandidates.set(totalDistinctCandidates.get() + 1),
//                (delta) -> totalProcessedConfigurations.set(totalProcessedConfigurations.get() + delta),
//                (status) -> bruteForceStatusMessage.set(status),
//                (percentage) -> bruteForceProgress.set(percentage),
//                (percentage) -> {
//                    double percent = percentage;
//                    int tmpPercentValue = (int) (percent * 100);
//                    String percentValue = tmpPercentValue + "%";
//                    bruteForceProgressBarPercentageProperty.set(percentValue);
//                },
//                (totalConfigs) -> totalPossibleConfigurations.setValue(totalConfigs),
//                (isActive) -> isBruteForceTaskActive.set(isActive),
//                (averageTasksProcessTime) -> averageTasksProcessTimeProperty.set(averageTasksProcessTime / (double) 1000000),
//                (totalTimeDecrypt) -> totalTimeDecryptProperty.set(totalTimeDecrypt / 1000000)
//        );
//    }
//
//    /**
//     * creates a Candidate that shows in the flow-pane at the ui
//     *
//     * @param candidate the candidate to create a tile from
//     */
//    private void createCandidateTile(Candidate candidate) {
//        try {
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(getClass().getResource("/body/screen3/candidate/tile/candidateTile.fxml"));
//            Node singleCandidateTile = loader.load();
//            CandidateTileController candidateTileController = loader.getController();
//
//            candidateTileController.setDecipheredText(candidate.getDecipheredText());
//            candidateTileController.setRotorsIDs(candidate.getRotorsIDs());
//            candidateTileController.setWindowsCharsAndNotches(candidate.getWindowChars(), candidate.getNotchPositions());
//            candidateTileController.setReflectorSymbol(candidate.getReflectorSymbol());
//            candidateTileController.setProcessedByAgentName(candidate.getProcessedByAgentName());
//
//            bodyController.insertCandidateToFlowPane(singleCandidateTile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * tells the engine to save ciphered characters to statistics and to stop holding characters in the buffer.
//     */
//    public void doneCurrentCipherProcess() {
//        engine.doneCurrentCipherProcess();
//        DTOspecs specsStatus = engine.displayMachineSpecifications();
//        cipherCounterProperty.set(specsStatus.getCipheredTextsCount());
//    }
//
//    /**
//     * set the Status m
//     *
//     * @param newStatus   the status text message to show
//     * @param messageTone Red for Errors, Blue for normal Status updates.
//     */
//    public void setStatusMessage(String newStatus, MessageTone messageTone) {
//        statusBackShape.setOpacity(1);
//        statusBackShape.getStyleClass().add(messageTone.colorClassOfMessage());
//        messageTone.removeAllStyleClassExcept(statusBackShape.getStyleClass());
//        messageFadeTransition.stop();
//
//        messageFadeTransition = new FadeTransition(Duration.millis(5000), statusBackShape);
//        statusLabel.setText(newStatus);
//        messageFadeTransition.setFromValue(1.0);
//        messageFadeTransition.setToValue(0.0);
//        messageFadeTransition.setDelay(Duration.millis(3000));
//
//        if (isAnimationProperty.getValue()) {
//            messageFadeTransition.play();
//        }
//    }
//
//    /**
//     * validate rotors
//     *
//     * @param rotors the rotors ids
//     * @return status
//     */
//    public DTOstatus validateRotorsInput(String rotors) {
//        return engine.validateRotors(rotors);
//    }
//
//    /**
//     * validate window characters
//     *
//     * @param windowChars the window characters
//     * @return status
//     */
//    public DTOstatus validateWindowsCharsInput(String windowChars) {
//        return engine.validateWindowCharacters(windowChars);
//    }
//
//    /**
//     * validate reflector
//     *
//     * @param currentReflector reflector number
//     * @return status
//     */
//    public DTOstatus validateReflectorInput(int currentReflector) {
//        return engine.validateReflector(currentReflector);
//    }
//
//    /**
//     * validate plugs
//     *
//     * @param plugs the plugs
//     * @return status
//     */
//    public DTOstatus validatePlugsInput(String plugs) {
//        return engine.validatePlugs(plugs);
//    }
//
//    /**
//     * stops & cancel the engine Brute-Force process
//     */
//    public void stopBruteForceProcess() {
//        isBruteForceTaskActive.set(false);
//        isBruteForceTaskPaused.set(false);
//        engine.stopBruteForceProcess();
//    }
//
//    /**
//     * changes the skin theme across the entire app.
//     *
//     * @param skin the skin to change to: Dark, Normal, Spacial
//     */
//    public void setAppSkin(Skin skin) {
//
//        System.out.println("before remove");
//        System.out.println(header.getStylesheets());
//
//
//        // removes all stylesheets
//        header.getStylesheets().removeAll(header.getStylesheets());
//        System.out.println(header.getStylesheets());
//        appGridPane.getStylesheets().removeAll(appGridPane.getStylesheets());
//        body.getStylesheets().removeAll(body.getStylesheets());
//        statusBar.getStylesheets().removeAll(statusBar.getStylesheets());
//
//        System.out.println("after remove");
//        System.out.println(header.getStylesheets());
//
//        // adds "app", "header", "body", "statusbar" stylesheets
//        URL appUrl = getClass().getResource("/app/app-" + skin.skinName() + ".css");
//        header.getStylesheets().add(appUrl.toString());
//        appGridPane.getStylesheets().add(appUrl.toString());
//        body.getStylesheets().add(appUrl.toString());
//        statusBar.getStylesheets().add(appUrl.toString());
//
//        URL headerUrl = getClass().getResource("/header/header-" + skin.skinName() + ".css");
//        header.getStylesheets().add(headerUrl.toString());
//
//        URL bodyUrl = getClass().getResource("/body/body-" + skin.skinName() + ".css");
//        body.getStylesheets().add(bodyUrl.toString());
//
//        URL statusbarUrl = getClass().getResource("/app/statusbar/statusbar-" + skin.skinName() + ".css");
//        statusBar.getStylesheets().add(statusbarUrl.toString());
//
//        System.out.println("after add");
//        System.out.println(header.getStylesheets());
//
//        // adds stylesheets to the body components
//        bodyController.setComponentsSkin(appUrl.toString(), skin);
//
//        // sets images
//        headerController.setImages(skin);
//
//    }
//
//    /**
//     * pause the engine brute force process
//     */
//    public void pauseBruteForceProcess() {
//        isBruteForceTaskPaused.set(true);
//        engine.pauseBruteForceProcess();
//    }
//
//    /**
//     * resume the engine brute force process
//     */
//    public void resumeBruteForceProcess() {
//        isBruteForceTaskPaused.set(false);
//        engine.resumeBruteForceProcess();
//    }
//
//    /**
//     * check if text words are from dictionary
//     *
//     * @param textToCipher textToCipher
//     * @return .True - if all words are in dictionary, False otherwise
//     */
//    public boolean isAllWordsInDictionary(String textToCipher) {
//        return engine.isAllWordsInDictionary(textToCipher);
//    }
//
//    public String convertProblemToMessage(Problem problem) {
//        switch (problem) {
//            case CIPHER_INPUT_EMPTY_STRING:
//                return "Please enter some text.";
//            case CIPHER_INPUT_NOT_IN_ALPHABET:
//                return "The text should contain only letters from the machine's alphabet.";
//            case FILE_NOT_ENOUGH_ROTORS:
//                return "There are not enough rotors.";
//            case FILE_NUM_OF_REFLECTS_IS_NOT_HALF_OF_ABC:
//                return "All reflectors should contains exactly a half of the alphabet amount inputs.";
//            case FILE_ODD_ALPHABET_AMOUNT:
//                return "The alphabet amount should be an even number.";
//            case FILE_OUT_OF_RANGE_NOTCH:
//                return "All notches should have a position within the range of available inputs in their rotor.";
//            case FILE_REFLECTOR_ID_DUPLICATIONS:
//                return "Each reflector should have a unique symbol.";
//            case FILE_REFLECTOR_INVALID_ID_RANGE:
//                return "All reflectors should have symbol of I - V only.";
//            case FILE_REFLECTOR_MAPPING_DUPPLICATION:
//                return "All reflectors should be valid, and each input should lead into a unique output.";
//            case FILE_REFLECTOR_MAPPING_NOT_IN_ALPHABET:
//                return "All reflectors should be valid, and all inputs should be from the machine's alphabet.";
//            case FILE_REFLECTOR_OUT_OF_RANGE_ID:
//                return "All reflectors should have symbol of I - V only.";
//            case FILE_REFLECTOR_SELF_MAPPING:
//                return "All reflectors should be valid, and each input should lead into a unique output.";
//            case FILE_ROTOR_COUNT_HIGHER_THAN_99:
//                return "Should have up to 99 rotors only.";
//            case FILE_ROTOR_INVALID_ID_RANGE:
//                return "All rotors should have in an running indexing starting from 1 only.";
//            case FILE_ROTOR_MAPPING_DUPPLICATION:
//                return "All rotors should be valid, and each input should lead into a unique output.";
//            case FILE_ROTOR_MAPPING_NOT_A_SINGLE_LETTER:
//                return "All rotors should contain inputs and outputs that represent one letter.";
//            case FILE_ROTOR_MAPPING_NOT_EQUAL_TO_ALPHABET_LENGTH:
//                return "All rotors should contain exactly all of the letters as inputs and outputs.";
//            case FILE_ROTOR_MAPPING_NOT_IN_ALPHABET:
//                return "All rotors should be valid, and all inputs should be from the machine's alphabet.";
//            case FILE_ROTORS_COUNT_BELOW_TWO:
//                return "Should have at least 2 rotors.";
//            case FILE_TOO_MANY_REFLECTORS:
//                return "Should have up to 5 reflectors only.";
//            case FILE_TOO_LITTLE_AGENTS:
//                return "Should have at least 2 agents.";
//            case FILE_TOO_MANY_AGENTS:
//                return "Should have up to 50 agents only.";
//            case ROTOR_VALIDATE_EMPTY_STRING:
//                return "Please enter all the required rotors.";
//            case ROTOR_DUPLICATION:
//                return "Cannot use a rotor more than once.";
//            case ROTOR_INPUT_NOT_ENOUGH_ELEMENTS:
//                return "Please enter all the required rotors.";
//            case WINDOW_INPUT_TOO_FEW_LETTERS:
//                return "Please enter all the required windows.";
//            case NO_REFLECTOR_BEEN_CHOSEN:
//                return "Please choose a reflector.";
//            case SELF_PLUGGING:
//                return "Each plug should have different inputs and outputs.";
//            case ALREADY_PLUGGED:
//                return "Each plug should have unique inputs and outputs.";
//            case PLUGS_MISSING_VALUES:
//                return "Please complete the plug selection.";
//            default:
//                return "";
//        }
//    }
//
//}
