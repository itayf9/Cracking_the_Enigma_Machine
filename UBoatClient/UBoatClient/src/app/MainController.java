package app;

import bindings.CurrWinCharsAndNotchPosBinding;
import body.BodyController;
import body.screen2.candidate.tile.CandidateTileController;
import candidate.Candidate;
import com.google.gson.Gson;
import dto.DTOsecretConfig;
import dto.DTOspecs;
import dto.DTOstatus;
import header.HeaderController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import okhttp.cookie.SimpleCookieManager;
import okhttp.url.URLconst;
import okhttp3.*;
import problem.Problem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static okhttp.url.URLconst.BASE_URL;
import static okhttp.url.URLconst.KEY_CONTENT_TYPE;

public class MainController {

    private OkHttpClient client;


    /**
     * app private members
     */
    @FXML
    private GridPane header;
    @FXML
    private HeaderController headerController;

    @FXML
    private GridPane appGridPane;

    @FXML
    private ImageView startImage;

    @FXML
    private TabPane body;
    @FXML
    private BodyController bodyController;

    @FXML
    private HBox statusBar;

    @FXML
    private Label statusLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Rectangle statusBackShape;


    /**
     * property stuff
     */
    private BooleanProperty isMachineConfiguredProperty;
    private BooleanProperty isMachineLoadedProperty;
    private ListProperty<Integer> inUseRotorsIDsProperty;
    private StringProperty currentWindowsCharactersProperty;
    private StringProperty inUseReflectorSymbolProperty;
    private StringProperty inUsePlugsProperty;
    private ListProperty<Integer> currentNotchDistances;
    private CurrWinCharsAndNotchPosBinding currWinCharsAndNotchPosBinding;
    private IntegerProperty cipherCounterProperty;

    /**
     * bruteforce stuff
     */

    private IntegerProperty totalDistinctCandidates;
    private IntegerProperty totalProcessedConfigurations;
    private LongProperty totalPossibleConfigurations;
    private DoubleProperty bruteForceProgress;
    private StringProperty bruteForceProgressBarPercentageProperty;
    private StringProperty bruteForceStatusMessage;
    private BooleanProperty isBruteForceTaskActive;
    private LongProperty totalPossibleWindowsPositions;
    private DoubleProperty averageTasksProcessTimeProperty;
    private LongProperty totalTimeDecryptProperty;
    private StringProperty dictionaryExcludeCharsProperty;


    @FXML
    public void initialize() {
        // controller initialize
        if (headerController != null && bodyController != null) {
            headerController.setMainController(this);
            bodyController.setMainController(this);


            // property initialize
            this.isMachineConfiguredProperty = new SimpleBooleanProperty(false);
            this.isMachineLoadedProperty = new SimpleBooleanProperty(false);
            this.inUseRotorsIDsProperty = new SimpleListProperty<>();
            this.currentWindowsCharactersProperty = new SimpleStringProperty("");
            this.inUseReflectorSymbolProperty = new SimpleStringProperty("");
            this.inUsePlugsProperty = new SimpleStringProperty("");
            this.currentNotchDistances = new SimpleListProperty<>();
            this.cipherCounterProperty = new SimpleIntegerProperty(0);
            this.totalDistinctCandidates = new SimpleIntegerProperty();
            this.totalProcessedConfigurations = new SimpleIntegerProperty();
            this.totalPossibleConfigurations = new SimpleLongProperty();
            this.isBruteForceTaskActive = new SimpleBooleanProperty(false);
            this.averageTasksProcessTimeProperty = new SimpleDoubleProperty();
            this.dictionaryExcludeCharsProperty = new SimpleStringProperty();
            this.bruteForceProgress = new SimpleDoubleProperty();
            this.bruteForceStatusMessage = new SimpleStringProperty("");
            this.bruteForceProgressBarPercentageProperty = new SimpleStringProperty("0%");
            this.totalPossibleWindowsPositions = new SimpleLongProperty();
            this.totalTimeDecryptProperty = new SimpleLongProperty();

            // binding initialize
            bodyController.bindComponents(isMachineConfiguredProperty, inUseRotorsIDsProperty,
                    currentWindowsCharactersProperty, inUseReflectorSymbolProperty, inUsePlugsProperty,
                    currentNotchDistances, cipherCounterProperty, totalDistinctCandidates,
                    totalProcessedConfigurations, totalPossibleConfigurations, bruteForceProgress,
                    bruteForceProgressBarPercentageProperty, bruteForceStatusMessage, isBruteForceTaskActive,
                    averageTasksProcessTimeProperty, totalTimeDecryptProperty);

            // general setting to initialize sub components
            body.visibleProperty().bind(isMachineLoadedProperty);
            startImage.fitHeightProperty().bind(Bindings.when(isMachineLoadedProperty.not()).then(500).otherwise(1));
            startImage.fitWidthProperty().bind(Bindings.when(isMachineLoadedProperty.not()).then(900).otherwise(1));
            messageLabel.textProperty().bind(statusLabel.textProperty());
            messageLabel.opacityProperty().bind(statusBackShape.opacityProperty());
            statusBackShape.heightProperty().bind(Bindings.add(2, statusLabel.heightProperty()));
            statusBackShape.widthProperty().bind(statusLabel.widthProperty());
            statusBackShape.setStrokeWidth(0);
            statusBackShape.setOpacity(0);

            // header bindings & settings
            headerController.setProperties(isMachineLoadedProperty);
            isMachineConfiguredProperty.addListener((observable, oldValue, newValue) -> clearOldComponents());
            bruteForceStatusMessage.addListener((observable, oldValue, newValue) -> setStatusMessage("Decrypt Manager: " + newValue, MessageTone.INFO));
        }
    }

    private void clearOldComponents() {
        bodyController.clearOldComponents();
    }

    /**
     * Q1 + Q2 Load the machine
     *
     * @param selectedMachineFile fileName
     */
    public void loadMachineFromFile(String selectedMachineFile) {
        // check if brute force task is running ?
        if (isBruteForceTaskActive.getValue()) {

            /**
             *  // let's stop the process
             *     stopBruteForceProcess();
             *     logout servlet request
             * */
        }

        if (isMachineLoadedProperty.get()) {
            setStatusMessage("Can't load more then 1 file.", MessageTone.ERROR);
        } else {
            // then load new machine

            File myFile = new File(selectedMachineFile);
            byte[] arr = new byte[(int) myFile.length()];

            try {
                FileInputStream fl = new FileInputStream(myFile);
                try {
                    fl.read(arr);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("contest", selectedMachineFile,
                            RequestBody.create(arr))
                    .build();
            Request request = new Request.Builder()
                    .url("http://localhost:8080/BattleFieldServer_Web_exploded/load")
                    .post(body)
                    .build();
            // should insert also the header of Content Type ????

            System.out.println(request.headers());

            client.newCall(request).enqueue(new Callback() {
                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println("Code: " + response.code());

                    String dtoAsStr = response.body().string();
                    System.out.println("Body: " + dtoAsStr);
                    Gson gson = new Gson();

                    if (response.code() != 200) {
                        DTOstatus loadStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                        Platform.runLater(() -> setStatusMessage("Could not load that file. " + convertProblemToMessage(loadStatus.getDetails()), MessageTone.ERROR));

                    } else {
                        DTOspecs loadStatus = gson.fromJson(dtoAsStr, DTOspecs.class);

                        Platform.runLater(() -> {
                            headerController.displayFilePath();
                            int rotorsCount = loadStatus.getInUseRotorsCount();
                            int alphabetLength = loadStatus.getMachineAlphabet().length();
                            // clear old current config
                            inUseRotorsIDsProperty.clear();
                            currentWindowsCharactersProperty.set("");
                            inUseReflectorSymbolProperty.set("");
                            currentNotchDistances.clear();
                            inUsePlugsProperty.set("");

                            // set new stuff
                            bodyController.setDictionaryWords(loadStatus.getDictionary().getDictionaryWords(), loadStatus.getMachineAlphabet());
                            bodyController.displayMachineSpecs(loadStatus);
                            cipherCounterProperty.set(0);
                            totalPossibleWindowsPositions.setValue(Math.pow(alphabetLength, rotorsCount));
                            bodyController.setEncryptExcludeCharsValue(dictionaryExcludeCharsProperty);
                            isMachineConfiguredProperty.setValue(Boolean.FALSE);
                            isMachineLoadedProperty.setValue(Boolean.TRUE);
                            dictionaryExcludeCharsProperty.setValue(loadStatus.getDictionaryExcludeCharacters());
                            bodyController.setCodeCalibration(loadStatus.getInUseRotorsCount(), loadStatus.getAvailableRotorsCount(), loadStatus.getMachineAlphabet(),
                                    loadStatus.getAvailableReflectorsCount());
                            setStatusMessage("Machine Loaded Successfully!", MessageTone.SUCCESS);
                        });
                    }
                }

                public void onFailure(Call call, IOException e) {
                    System.out.println("Oops... something went wrong..." + e.getMessage());
                }
            });
        }
    }

    /**
     * Q3 set manual config
     *
     * @param rotors    rotors ids
     * @param windows   window characters
     * @param reflector reflector number
     * @param plugs     plugs
     */
    public void setManualMachineConfig(String rotors, String windows, int reflector, String plugs) {

        /**
         *   DTOsecretConfig configStatus = engine.selectConfigurationManual(rotors, windows, reflector, plugs);
         * */

        ObservableList<Integer> rotorsObservableList = FXCollections.observableArrayList(configStatus.getRotors());
        inUseRotorsIDsProperty.setValue(rotorsObservableList);
        currentWindowsCharactersProperty.setValue(configStatus.getWindows());
        inUseReflectorSymbolProperty.setValue(configStatus.getReflectorSymbol());
        inUsePlugsProperty.setValue(configStatus.getPlugs());
        ObservableList<Integer> notchDistanceObservableList = FXCollections.observableArrayList(configStatus.getNotchDistances());
        currentNotchDistances.setValue(notchDistanceObservableList);
        // display original config in machine specs
        bodyController.displayOriginalConfig(configStatus.getRotors(), configStatus.getWindows(), configStatus.getReflectorSymbol(), configStatus.getPlugs(), configStatus.getNotchDistances());
        setStatusMessage("Configured Successfully", MessageTone.SUCCESS);
        isMachineConfiguredProperty.setValue(Boolean.TRUE);
    }

    /**
     * Q4 set configuration auto
     */
    public void setRandomMachineConfig() {

        String body = "";
        Request request = new Request.Builder()
                .url(BASE_URL + "/calibrate/auto")
                .addHeader(KEY_CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(body.getBytes()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Code: " + response.code());

                String dtoAsStr = response.body().string();
                System.out.println("Body: " + dtoAsStr);
                System.out.println("headers: " + response.headers());
                Gson gson = new Gson();

                if (response.code() != 200) {

                    DTOstatus configStatus = gson.fromJson(dtoAsStr, DTOstatus.class);

                    Platform.runLater(() -> {
                        setStatusMessage("Could not set a code. " + convertProblemToMessage(configStatus.getDetails()), MessageTone.ERROR);
                    });
                    return;
                }


                Platform.runLater(() -> {

                    DTOsecretConfig configStatus = gson.fromJson(dtoAsStr, DTOsecretConfig.class);
                    ObservableList<Integer> rotorsObservableList = FXCollections.observableArrayList(configStatus.getRotors());
                    inUseRotorsIDsProperty.setValue(rotorsObservableList);

                    currentWindowsCharactersProperty.setValue(configStatus.getWindows());
                    inUseReflectorSymbolProperty.setValue(configStatus.getReflectorSymbol());
                    inUsePlugsProperty.setValue(configStatus.getPlugs());
                    ObservableList<Integer> notchDistanceObservableList = FXCollections.observableArrayList(configStatus.getNotchDistances());
                    currentNotchDistances.setValue(notchDistanceObservableList);
                    setStatusMessage("Configured Successfully", MessageTone.SUCCESS);
                    isMachineConfiguredProperty.setValue(Boolean.TRUE);
                    bodyController.displayOriginalConfig(inUseRotorsIDsProperty.getValue(), currentWindowsCharactersProperty.getValue(), inUseReflectorSymbolProperty.getValue(), inUsePlugsProperty.getValue(), currentNotchDistances.getValue());
                });
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });

    }


    /**
     * Q5 cipher line
     *
     * @param line String that contains one character
     * @return ciphered Character
     */
    public void cipher(String line) {

        /**
         * only one http request
         *
         *         DTOciphertext cipherStatus = engine.cipherInputText(line);
         * if (cipherStatus.isSucceed()) {
         *      // update configuration
         *
         *         currentWindowsCharactersProperty.setValue(cipherStatus.getCurrentWindowsCharacters());
         *         ObservableList<Integer> notchDistanceObservableList = FXCollections.observableArrayList(cipherStatus.getNotchDistancesToWindow());
         *         currentNotchDistances.setValue(notchDistanceObservableList);
         *
         *         cipherCounterProperty.setValue(cipherStatus.getCipheredTextsCount());
         *
         * } else {
         *     setStatusMessage("Could not cipher that text. " +
         *      convertProblemToMessage(cipheredLineStatus.getDetails()), MessageTone.ERROR);
         *
         * }
         *
         *   bodyController.setCipherOutput(cipherStatus);
         **/

    }

    /**
     * Q6 reset configuration
     */
    public void resetMachineConfiguration() {
        /**
         *  engine.resetConfiguration();
         *  DTOspecs specsStatus = engine.displayMachineSpecifications();
         *
         *  currentWindowsCharactersProperty.setValue(specsStatus.getOriginalWindowsCharacters()); // current should be the same here
         *
         *         ObservableList<Integer> notchDistanceObservableList = FXCollections.observableArrayList(specsStatus.getOriginalNotchPositions()); // current should be the same here
         *         currentNotchDistances.setValue(notchDistanceObservableList);
         * */

        // create servlet for resetting machine - servlet will return DTOspecs


        setStatusMessage("Reset Successfully", MessageTone.SUCCESS);
    }

    /**
     * clear all findings of last process and labels progress
     */
    private void cleanOldResults() {
        bodyController.clearOldResultsOfBruteForce();
        bruteForceProgress.set(0);
        totalDistinctCandidates.set(0);
        totalProcessedConfigurations.set(0);
        averageTasksProcessTimeProperty.set(0);
    }

    /**
     * creates a Candidate that shows in the flow-pane at the ui
     *
     * @param candidate the candidate to create a tile from
     */
    private void createCandidateTile(Candidate candidate, String AllieName, String AgentName) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/body/screen3/candidate/tile/candidateTile.fxml"));
            Node singleCandidateTile = loader.load();
            CandidateTileController candidateTileController = loader.getController();

            candidateTileController.setDecipheredText(candidate.getDecipheredText());
            candidateTileController.setRotorsIDs(candidate.getRotorsIDs());
            candidateTileController.setWindowsCharsAndNotches(candidate.getWindowChars(), candidate.getNotchPositions());
            candidateTileController.setReflectorSymbol(candidate.getReflectorSymbol());
            candidateTileController.setProcessedByAllieName(AllieName);
            candidateTileController.setProcessedByAgentName(AgentName);
            bodyController.insertCandidateToFlowPane(singleCandidateTile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * set the Status m
     *
     * @param newStatus   the status text message to show
     * @param messageTone Red for Errors, Blue for normal Status updates.
     */
    public void setStatusMessage(String newStatus, MessageTone messageTone) {
        statusBackShape.setOpacity(1);
        statusBackShape.getStyleClass().add(messageTone.colorClassOfMessage());
        messageTone.removeAllStyleClassExcept(statusBackShape.getStyleClass());
        statusLabel.setText(newStatus);
    }

    /**
     * stops & cancel the engine Brute-Force process
     */
    public void announceTheWinnerOfTheContest() {
        isBruteForceTaskActive.set(false);
        /**
         * http request to found winner servlet
         * engine.stopBruteForceProcess();
         * */

    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.client = okHttpClient;
    }

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

    public String convertProblemToMessage(Problem problem) {
        switch (problem) {
            case CIPHER_INPUT_EMPTY_STRING:
                return "Please enter some text.";
            case CIPHER_INPUT_NOT_IN_ALPHABET:
                return "The text should contain only letters from the machine's alphabet.";
            case FILE_NOT_ENOUGH_ROTORS:
                return "There are not enough rotors.";
            case FILE_NUM_OF_REFLECTS_IS_NOT_HALF_OF_ABC:
                return "All reflectors should contains exactly a half of the alphabet amount inputs.";
            case FILE_ODD_ALPHABET_AMOUNT:
                return "The alphabet amount should be an even number.";
            case FILE_OUT_OF_RANGE_NOTCH:
                return "All notches should have a position within the range of available inputs in their rotor.";
            case FILE_REFLECTOR_ID_DUPLICATIONS:
                return "Each reflector should have a unique symbol.";
            case FILE_REFLECTOR_INVALID_ID_RANGE:
                return "All reflectors should have symbol of I - V only.";
            case FILE_REFLECTOR_MAPPING_DUPPLICATION:
                return "All reflectors should be valid, and each input should lead into a unique output.";
            case FILE_REFLECTOR_MAPPING_NOT_IN_ALPHABET:
                return "All reflectors should be valid, and all inputs should be from the machine's alphabet.";
            case FILE_REFLECTOR_OUT_OF_RANGE_ID:
                return "All reflectors should have symbol of I - V only.";
            case FILE_REFLECTOR_SELF_MAPPING:
                return "All reflectors should be valid, and each input should lead into a unique output.";
            case FILE_ROTOR_COUNT_HIGHER_THAN_99:
                return "Should have up to 99 rotors only.";
            case FILE_ROTOR_INVALID_ID_RANGE:
                return "All rotors should have in an running indexing starting from 1 only.";
            case FILE_ROTOR_MAPPING_DUPPLICATION:
                return "All rotors should be valid, and each input should lead into a unique output.";
            case FILE_ROTOR_MAPPING_NOT_A_SINGLE_LETTER:
                return "All rotors should contain inputs and outputs that represent one letter.";
            case FILE_ROTOR_MAPPING_NOT_EQUAL_TO_ALPHABET_LENGTH:
                return "All rotors should contain exactly all of the letters as inputs and outputs.";
            case FILE_ROTOR_MAPPING_NOT_IN_ALPHABET:
                return "All rotors should be valid, and all inputs should be from the machine's alphabet.";
            case FILE_ROTORS_COUNT_BELOW_TWO:
                return "Should have at least 2 rotors.";
            case FILE_TOO_MANY_REFLECTORS:
                return "Should have up to 5 reflectors only.";
            case FILE_TOO_LITTLE_AGENTS:
                return "Should have at least 2 agents.";
            case FILE_TOO_MANY_AGENTS:
                return "Should have up to 50 agents only.";
            case ROTOR_VALIDATE_EMPTY_STRING:
                return "Please enter all the required rotors.";
            case ROTOR_DUPLICATION:
                return "Cannot use a rotor more than once.";
            case ROTOR_INPUT_NOT_ENOUGH_ELEMENTS:
                return "Please enter all the required rotors.";
            case WINDOW_INPUT_TOO_FEW_LETTERS:
                return "Please enter all the required windows.";
            case NO_REFLECTOR_BEEN_CHOSEN:
                return "Please choose a reflector.";
            case SELF_PLUGGING:
                return "Each plug should have different inputs and outputs.";
            case ALREADY_PLUGGED:
                return "Each plug should have unique inputs and outputs.";
            case PLUGS_MISSING_VALUES:
                return "Please complete the plug selection.";
            default:
                return "";
        }
    }

}
