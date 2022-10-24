package uboat.app;

import uboat.bindings.CurrWinCharsAndNotchPosBinding;
import uboat.body.BodyController;
import uboat.body.screen2.candidate.tile.CandidateTileController;
import candidate.Candidate;
import com.google.gson.Gson;
import dto.*;
import uboat.header.HeaderController;
import info.allie.AllieInfo;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import okhttp3.*;
import problem.Problem;
import uboat.tasks.FetchAlliesInfoTimer;
import uboat.tasks.FetchCandidatesTimer;
import uboat.tasks.FetchContestStatusTimer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Timer;

import static http.url.QueryParameter.*;
import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.*;

public class MainController {

    private OkHttpClient client;

    private StringProperty usernameProperty;

    private GridPane login;

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

    private StringProperty originalWindowsPositionsProperty;
    private StringProperty inUseReflectorSymbolProperty;
    private StringProperty inUsePlugsProperty;
    private ListProperty<Integer> currentNotchDistances;
    private CurrWinCharsAndNotchPosBinding currWinCharsAndNotchPosBinding;
    private IntegerProperty cipherCounterProperty;
    private BooleanProperty isClientReady;
    private BooleanProperty textHasBeenCiphered;

    /**
     * bruteforce stuff
     */
    private IntegerProperty totalDistinctCandidates;
    private LongProperty totalPossibleWindowsPositions;
    private StringProperty dictionaryExcludeCharsProperty;

    /**
     * contest stuff
     */
    public final static int REFRESH_RATE = 2000;
    private BooleanProperty isContestActive;
    private Timer fetchContestStatusTimer;
    private FetchContestStatusTimer fetchContestStatusTimerTask;
    private Timer fetchAlliesInfoTimer;
    private FetchAlliesInfoTimer fetchAlliesInfoTimerTask;
    private Timer fetchCandidatesTimer;
    private FetchCandidatesTimer fetchCandidatesTimerTask;
    private StringProperty originalText;
    private BooleanProperty isProcessedText;


    @FXML
    public void initialize() {
        // controller initialize
        headerController.setMainController(this);
        bodyController.setMainController(this);

        // property initialize
        this.isMachineConfiguredProperty = new SimpleBooleanProperty(false);
        this.isMachineLoadedProperty = new SimpleBooleanProperty(false);
        this.inUseRotorsIDsProperty = new SimpleListProperty<>();
        this.currentWindowsCharactersProperty = new SimpleStringProperty("");
        this.originalWindowsPositionsProperty = new SimpleStringProperty("");
        this.inUseReflectorSymbolProperty = new SimpleStringProperty("");
        this.inUsePlugsProperty = new SimpleStringProperty("");
        this.currentNotchDistances = new SimpleListProperty<>();
        this.cipherCounterProperty = new SimpleIntegerProperty(0);
        this.totalDistinctCandidates = new SimpleIntegerProperty();
        this.dictionaryExcludeCharsProperty = new SimpleStringProperty();
        this.totalPossibleWindowsPositions = new SimpleLongProperty();
        this.isClientReady = new SimpleBooleanProperty(false);
        this.textHasBeenCiphered = new SimpleBooleanProperty(false);
        this.isContestActive = new SimpleBooleanProperty(false);
        this.originalText = new SimpleStringProperty();
        this.usernameProperty = new SimpleStringProperty("");
        this.isProcessedText = new SimpleBooleanProperty(false);

        // isContestActive event listener
        isContestActive.addListener((o, oldVal, newVal) -> {
            if (newVal) {
                // contest == active
                // stop allies & status timers
                setStatusMessage("Contest has started", MessageTone.INFO);
                fetchContestStatusTimerTask.cancel();
                fetchContestStatusTimer.cancel();

                // schedule fetch candidates timer
                this.fetchCandidatesTimer = new Timer();
                this.fetchCandidatesTimerTask = new FetchCandidatesTimer(this, inUseRotorsIDsProperty, originalWindowsPositionsProperty, inUseReflectorSymbolProperty, originalText, client);
                fetchCandidatesTimer.schedule(fetchCandidatesTimerTask, REFRESH_RATE, REFRESH_RATE);

                cleanOldResults();
            } else {
                // contest == not active => winner found
                fetchCandidatesTimerTask.cancel();
                fetchCandidatesTimer.cancel();
            }
        });


        // binding initialize
        bodyController.bindComponents(isMachineConfiguredProperty, inUseRotorsIDsProperty,
                currentWindowsCharactersProperty, inUseReflectorSymbolProperty, inUsePlugsProperty,
                currentNotchDistances, cipherCounterProperty, totalDistinctCandidates, isClientReady, isProcessedText);

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
        headerController.bindComponents(isMachineLoadedProperty, usernameProperty, isContestActive);
        isMachineConfiguredProperty.addListener((observable, oldValue, newValue) -> clearOldComponents());
    }

    /**
     * clears the old data from the components
     */
    private void clearOldComponents() {
        bodyController.clearOldComponents();
        totalDistinctCandidates.set(0);
    }

    /**
     * Q1 + Q2 Load the machine
     *
     * @param selectedMachineFile file name to load the machine from
     */
    public void loadMachineFromFile(String selectedMachineFile) {

        // prevents double loading
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
            HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + LOAD_XML_SRC).newBuilder();
            Request request = new Request.Builder()
                    .url(urlBuilder.build().toString())
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println("load machine status resp " + "Code: " + response.code());
                    String dtoAsStr = response.body().string();
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
                            fetchAlliesInfoTimer = new Timer();
                            fetchAlliesInfoTimerTask = new FetchAlliesInfoTimer(client, getMainController());
                            fetchAlliesInfoTimer.schedule(fetchAlliesInfoTimerTask, REFRESH_RATE, REFRESH_RATE);
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

        String body = "";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + CALIB_MANUAL_SRC).newBuilder();
        urlBuilder.addQueryParameter(ROTORS_IDS, rotors);
        urlBuilder.addQueryParameter(WINDOWS_CHARS, windows);
        urlBuilder.addQueryParameter(REFLECTOR_ID, String.valueOf(reflector));
        urlBuilder.addQueryParameter(PLUGS, plugs);
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(body.getBytes()))
                .build();
        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("setManualMachineConfig resp " + "Code: " + response.code());

                String dtoAsStr = response.body().string();
                Gson gson = new Gson();
                if (response.code() != 200) {
                    DTOstatus configStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        setStatusMessage(convertProblemToMessage(configStatus.getDetails()), MessageTone.ERROR);
                    });
                    return;
                }

                Platform.runLater(() -> {
                    DTOsecretConfig configStatus = gson.fromJson(dtoAsStr, DTOsecretConfig.class);
                    ObservableList<Integer> rotorsObservableList = FXCollections.observableArrayList(configStatus.getRotors());
                    inUseRotorsIDsProperty.setValue(rotorsObservableList);
                    originalWindowsPositionsProperty.setValue(configStatus.getWindows());
                    currentWindowsCharactersProperty.setValue(configStatus.getWindows());
                    inUseReflectorSymbolProperty.setValue(configStatus.getReflectorSymbol());
                    inUsePlugsProperty.setValue(configStatus.getPlugs());
                    ObservableList<Integer> notchDistanceObservableList = FXCollections.observableArrayList(configStatus.getNotchDistances());
                    currentNotchDistances.setValue(notchDistanceObservableList);
                    // display original config in machine specs
                    bodyController.displayOriginalConfig(configStatus.getRotors(), configStatus.getWindows(),
                            configStatus.getReflectorSymbol(), configStatus.getPlugs(), configStatus.getNotchDistances());
                    setStatusMessage("Configured Successfully", MessageTone.SUCCESS);
                    isMachineConfiguredProperty.setValue(Boolean.TRUE);
                });
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }

    /**
     * Q4 set configuration auto
     */
    public void setRandomMachineConfig() {

        String body = "";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + CALIB_AUTO_SRC).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(body.getBytes()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("setRandomMachineConfig resp " + "Code: " + response.code());

                String dtoAsStr = response.body().string();
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
                    originalWindowsPositionsProperty.setValue(configStatus.getWindows());
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
     * @param line String that contains the text to cipher
     */
    public void cipher(String line) {

        originalText.set(line);

        String body = "";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + CIPHER_SRC).newBuilder();
        urlBuilder.addQueryParameter(TEXT_TO_CIPHER, line);
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(body.getBytes()))
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("cipher resp " + "Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();


                if (response.code() != 200) {
                    DTOstatus cipherStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        setStatusMessage(convertProblemToMessage(cipherStatus.getDetails()), MessageTone.ERROR);
                        bodyController.setCipherOutput(cipherStatus.getDetails(), "");
                    });

                } else {
                    DTOciphertext cipherStatus = gson.fromJson(dtoAsStr, DTOciphertext.class);

                    Platform.runLater(() -> {
                        currentWindowsCharactersProperty.setValue(cipherStatus.getCurrentWindowsCharacters());
                        ObservableList<Integer> notchDistanceObservableList = FXCollections.observableArrayList(cipherStatus.getCurrentNotchDistances());
                        currentNotchDistances.setValue(notchDistanceObservableList);
                        cipherCounterProperty.setValue(cipherStatus.getCipherCounter());
                        bodyController.setCipherOutput(cipherStatus.getDetails(), cipherStatus.getCipheredText());
                        textHasBeenCiphered.set(Boolean.TRUE);
                        isProcessedText.set(Boolean.TRUE);
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }

    /**
     * Q6 reset configuration
     */
    public void resetMachineConfiguration() {
        String body = "";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + RESET_CONFIG_SRC).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(body.getBytes()))
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("resetMachineConfiguration resp " + "Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();

                if (response.code() != 200) {
                    DTOstatus resetStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        setStatusMessage(convertProblemToMessage(resetStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    DTOresetConfig resetStatus = gson.fromJson(dtoAsStr, DTOresetConfig.class);

                    Platform.runLater(() -> {
                        currentWindowsCharactersProperty.setValue(resetStatus.getCurrentWindowsCharacters()); // current should be the same here
                        ObservableList<Integer> notchDistanceObservableList = FXCollections.observableArrayList(resetStatus.getCurrentNotchDistances()); // current should be the same here
                        currentNotchDistances.setValue(notchDistanceObservableList);
                        isProcessedText.set(Boolean.FALSE);
                        setStatusMessage("Reset Successfully", MessageTone.SUCCESS);
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }

    /**
     * send http request to the server to report that uboat is ready to start the contest.
     */
    public void setReady() {
        if (textHasBeenCiphered.get() == Boolean.FALSE) {
            setStatusMessage("please cipher some text", MessageTone.ERROR);
            return;
        }

        String body = "";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + CLIENT_IS_READY_SRC).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(body.getBytes()))
                .build();
        client.newCall(request).enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("setReady status resp " + "Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();


                if (response.code() != 200) {
                    DTOstatus resetStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        setStatusMessage(convertProblemToMessage(resetStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    Platform.runLater(() -> {
                        isClientReady.set(Boolean.TRUE);
                        fetchContestStatusTimer = new Timer();
                        fetchContestStatusTimerTask = new FetchContestStatusTimer(isContestActive, client);
                        fetchContestStatusTimer.schedule(fetchContestStatusTimerTask, REFRESH_RATE, REFRESH_RATE);
                        setStatusMessage("uboat is ready", MessageTone.SUCCESS);
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });

    }

    /**
     * clear all findings of last process and labels progress
     */
    private void cleanOldResults() {
        headerController.clearOldResults();
        bodyController.clearOldResultsOfBruteForce();
    }

    /**
     * creates a Candidate that shows in the flow-pane at the ui
     *
     * @param candidate the candidate to create a tile from
     */
    public void createCandidateTile(Candidate candidate, String AllieName, String AgentName) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/uboat/body/screen2/candidate/tile/candidateTile.fxml"));
            Node singleCandidateTile = loader.load();
            CandidateTileController candidateTileController = loader.getController();

            // builds the new candidate tile
            candidateTileController.setDecipheredText(candidate.getDecipheredText());
            candidateTileController.setRotorsIDs(candidate.getRotorsIDs());
            candidateTileController.setWindowsCharsAndNotches(candidate.getWindowChars(), candidate.getNotchPositions());
            candidateTileController.setReflectorSymbol(candidate.getReflectorSymbol());
            candidateTileController.setProcessedByAllieName(AllieName);
            candidateTileController.setProcessedByAgentName(AgentName);

            // updates the total distinct candidates counter
            totalDistinctCandidates.set(totalDistinctCandidates.get() + 1);

            // inserts the new candidate to the area
            bodyController.insertCandidateToFlowPane(singleCandidateTile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * set the Status message
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
    public void announceTheWinnerOfTheContest(String allieWinnerName) {
        String body = "";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + WINNER_FOUND_SRC).newBuilder();
        urlBuilder.addQueryParameter(ALLIE_NAME, allieWinnerName);
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(body.getBytes()))
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("announceTheWinnerOfTheContest resp " + "Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();

                DTOstatus resetStatus = gson.fromJson(dtoAsStr, DTOstatus.class);

                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        setStatusMessage(convertProblemToMessage(resetStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    Platform.runLater(() -> {
                        setStatusMessage("A Winner Was Found. The Winner Team Is: " + allieWinnerName, MessageTone.INFO);
                        headerController.announceWinner(allieWinnerName);
                        isContestActive.set(false);
                        isClientReady.set(false);
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });

    }

    /**
     * sets the okHttp client
     *
     * @param okHttpClient the okHttpClient to be set
     */
    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.client = okHttpClient;
    }

    /**
     * converts a {@link Problem} to a specific message
     *
     * @param problem the {@link Problem} to convert
     * @return a {@link String} representing the problem
     */
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
                return problem.name();
        }
    }

    /**
     * updates the active teams information
     *
     * @param alliesInfoList a list of {@link info.agent.AgentInfo} representing the active teams information
     */
    public void updateAlliesInfo(List<AllieInfo> alliesInfoList) {
        bodyController.updateAlliesInfo(alliesInfoList);
    }

    public OkHttpClient getHTTPClient() {
        return client;
    }

    public void setUserName(String username) {
        this.usernameProperty.set(username);
    }

    public void logoutUBoat(MouseEvent event) {
        if (isMachineLoadedProperty.get()) {
            fetchAlliesInfoTimer.cancel();
            fetchAlliesInfoTimerTask.cancel();
        }
        if (isClientReady.get()) {
            if (fetchContestStatusTimer != null) {
                fetchContestStatusTimer.cancel();
                fetchContestStatusTimerTask.cancel();
            }
        }
        if (isContestActive.get()) {
            if (fetchCandidatesTimer != null) {
                fetchCandidatesTimer.cancel();
                fetchCandidatesTimerTask.cancel();
            }
        }
        String body = "";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + LOGOUT_SRC).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(body.getBytes()))
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("logged out resp " + "Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();

                DTOstatus resetStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        setStatusMessage(convertProblemToMessage(resetStatus.getDetails()), MessageTone.ERROR);
                    });
                } else {
                    Platform.runLater(() -> {
                        isContestActive.set(false);
                        FXMLLoader loader = null;
                        try {
                            loader = new FXMLLoader();
                            URL loginFxml = getClass().getResource("/uboat/login/login.fxml");
                            loader.setLocation(loginFxml);
                            login = loader.load();

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Scene loginScene = new Scene(login, 300, 300);
                        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        primaryStage.setScene(loginScene);
                        primaryStage.show();
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }

    public MainController getMainController() {
        return this;
    }

    public void approveUboatFinishGame() {
        cleanOldResults();
    }
}
