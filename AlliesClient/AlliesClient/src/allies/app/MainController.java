package allies.app;

import allies.body.BodyController;
import allies.body.screen1.contest.tile.ContestTileController;
import allies.body.screen2.candidate.tile.CandidateTileController;
import allies.tasks.*;
import candidate.Candidate;
import com.google.gson.Gson;
import dto.*;
import allies.header.HeaderController;
import http.url.QueryParameter;
import info.agent.AgentInfo;
import info.allie.AllieInfo;
import info.battlefield.BattlefieldInfo;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import jobprogress.JobProgressInfo;
import okhttp3.*;
import problem.Problem;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import static http.url.URLconst.*;
import static http.url.URLconst.BASE_URL;
import static http.url.Constants.CONTENT_TYPE;

public class MainController {

    private OkHttpClient client;

    private StringProperty usernameProperty;

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
    private TabPane tabPaneBody;
    @FXML
    private BodyController tabPaneBodyController;

    @FXML
    private HBox statusBar;

    @FXML
    private Label statusLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Rectangle statusBackShape;

    private GridPane login;

    /**
     * bruteforce stuff
     */
    private IntegerProperty totalDistinctCandidates;

    private BooleanProperty isSubscribedToContest;

    /**
     * contest stuff
     */
    public final static int REFRESH_RATE = 2000;
    private BooleanProperty isContestActive;
    private StringProperty uboatName;
    private Timer fetchContestStatusTimer;
    private FetchContestStatusTimer fetchContestStatusTimerTask;
    private Timer fetchAlliesInfoTimer;
    private FetchAlliesInfoTimer fetchAlliesInfoTimerTask;
    private Timer fetchLoggedAgentsInfoTimer;
    private FetchLoggedAgentsInfoTimer fetchLoggedAgentsInfoTimerTask;
    private Timer fetchDynamicContestInfoTimer;
    private FetchDynamicContestInfoTimer fetchDynamicContestInfoTimerTask;
    private Timer fetchContestsInfoTimer;
    private FetchContestsInfoTimer fetchContestsInfoTimerTask;
    private Timer fetchIsSubscribedToContestTimer;
    private FetchIsSubscribedToContestTimer fetchIsSubscribedToContestTimerTask;

    private BooleanProperty isReady;

    @FXML
    public void initialize() {

        // controller initialize
        headerController.setMainController(this);
        tabPaneBodyController.setMainController(this);

        // property initialize
        this.totalDistinctCandidates = new SimpleIntegerProperty();
        this.isSubscribedToContest = new SimpleBooleanProperty();
        this.isContestActive = new SimpleBooleanProperty(false);
        this.uboatName = new SimpleStringProperty();
        this.isReady = new SimpleBooleanProperty(false);
        this.usernameProperty = new SimpleStringProperty("");


        isSubscribedToContest.addListener((o, oldVal, newVal) ->{
            if (newVal) { // subscribed to contest == true
                fetchIsSubscribedToContestTimer = new Timer();
                fetchIsSubscribedToContestTimerTask = new FetchIsSubscribedToContestTimer(isSubscribedToContest, uboatName, client, getMainController());
                fetchIsSubscribedToContestTimer.schedule(fetchIsSubscribedToContestTimerTask, REFRESH_RATE, REFRESH_RATE);
            } else {// when contest is over // or when uboat logged out before contest has begun
                isReady.set(false);
                tabPaneBody.getSelectionModel().selectFirst();
            }
        });

        // isContestActive event listener
        isContestActive.addListener((o, oldVal, newVal) -> {
            if (newVal) {
                // contest == active
                // stop allies & status timers
                setStatusMessage("Contest has started", MessageTone.INFO);
                fetchStaticInfoContest();

                // schedule fetch candidates timer & fetch active teams
                this.fetchAlliesInfoTimer = new Timer();
                this.fetchAlliesInfoTimerTask = new FetchAlliesInfoTimer(this, uboatName, client);
                fetchAlliesInfoTimer.schedule(fetchAlliesInfoTimerTask, REFRESH_RATE, REFRESH_RATE);
                this.fetchDynamicContestInfoTimer = new Timer();
                this.fetchDynamicContestInfoTimerTask = new FetchDynamicContestInfoTimer(this, uboatName, client);
                fetchDynamicContestInfoTimer.schedule(fetchDynamicContestInfoTimerTask, REFRESH_RATE, REFRESH_RATE);
            } else {
                // contest == not active => winner found
                fetchAlliesInfoTimerTask.cancel();
                fetchAlliesInfoTimer.cancel();
                fetchDynamicContestInfoTimer.cancel();
                fetchDynamicContestInfoTimerTask.cancel();
                fetchContestStatusTimer.cancel();
                fetchContestStatusTimerTask.cancel();
                fetchWinnerMessage();
                isReady.set(false);
            }
        });

        // binding initialize
        tabPaneBodyController.bindComponents(totalDistinctCandidates, isSubscribedToContest, isReady, isContestActive);

        // general setting to initialize sub components
        messageLabel.textProperty().bind(statusLabel.textProperty());
        messageLabel.opacityProperty().bind(statusBackShape.opacityProperty());
        statusBackShape.heightProperty().bind(Bindings.add(2, statusLabel.heightProperty()));
        statusBackShape.widthProperty().bind(statusLabel.widthProperty());
        statusBackShape.setStrokeWidth(0);
        statusBackShape.setOpacity(0);

        this.fetchLoggedAgentsInfoTimer = new Timer();
        this.fetchLoggedAgentsInfoTimerTask = new FetchLoggedAgentsInfoTimer(this);
        fetchLoggedAgentsInfoTimer.schedule(fetchLoggedAgentsInfoTimerTask, REFRESH_RATE, REFRESH_RATE);
        this.fetchContestsInfoTimer = new Timer();
        this.fetchContestsInfoTimerTask = new FetchContestsInfoTimer(this);
        fetchContestsInfoTimer.schedule(fetchContestsInfoTimerTask, REFRESH_RATE, REFRESH_RATE);

        headerController.bindComponents(usernameProperty);
    }

    /**
     * fetch the winner allie name from server and display it to everyone
     */
    private void fetchWinnerMessage() {
        String body = "";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_CONTEST_WINNER).newBuilder();
        urlBuilder.addQueryParameter(QueryParameter.UBOAT_NAME, uboatName.get());
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();


                if (response.code() != 200) {
                    DTOstatus winnerStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        setStatusMessage(convertProblemToMessage(winnerStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    DTOwinner winnerStatus = gson.fromJson(dtoAsStr, DTOwinner.class);
                    Platform.runLater(() -> {
                        headerController.displayWinnerMessage(winnerStatus.getAllieWinner());
                        setStatusMessage("Found a Winner !", MessageTone.SUCCESS);
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }

    /**
     * #1 fetch logged agents info from server via http request
     */
    public void fetchLoggedAgentsInfoFromServer() {
        String body = "";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_LOGGED_AGENTS_INFO_SRC).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Code: " + response.code());
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

                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }

    /**
     * #2 register the app to a battlefield with http request to the server
     */
    public void subscribeToBattlefield(String uboatNameToRegister) {
        String body = "";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + SUBSCRIBE_TO_BATTLEFIELD_SRC).newBuilder();
        urlBuilder.addQueryParameter(QueryParameter.UBOAT_NAME, uboatNameToRegister);
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(body.getBytes()))
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();


                if (response.code() != 200) {
                    DTOstatus subscribeStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> setStatusMessage(convertProblemToMessage(subscribeStatus.getDetails()), MessageTone.ERROR));

                } else {
                    DTOsubscribe subscribeStatus = gson.fromJson(dtoAsStr, DTOsubscribe.class);

                    Platform.runLater(() -> {
                        uboatName.set(uboatNameToRegister);
                        isSubscribedToContest.set(true);
                        tabPaneBodyController.setTaskSizeSpinner(subscribeStatus.getTotalPossibleWindowsPositions());
                        setStatusMessage("Subscribed Successfully", MessageTone.SUCCESS);
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }

    /**
     * #3 let the server know we are ready for contest to start
     */
    public void setReady(int taskSize) {
        String body = "";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + CLIENT_IS_READY_SRC).newBuilder();
        urlBuilder.addQueryParameter(QueryParameter.UBOAT_NAME, uboatName.get());
        urlBuilder.addQueryParameter(QueryParameter.TASK_SIZE, String.valueOf(taskSize));
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(body.getBytes()))
                .build();
        client.newCall(request).enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();

                if (response.code() != 200) {
                    DTOstatus readyStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> setStatusMessage(convertProblemToMessage(readyStatus.getDetails()), MessageTone.ERROR));

                } else {
                    Platform.runLater(() -> {
                        fetchContestStatusTimer = new Timer();
                        fetchContestStatusTimerTask = new FetchContestStatusTimer(isContestActive, uboatName, client, getMainController());
                        fetchContestStatusTimer.schedule(fetchContestStatusTimerTask, REFRESH_RATE, REFRESH_RATE);
                        setStatusMessage("Allie is Ready", MessageTone.INFO);
                        isReady.set(true);
                        tabPaneBody.getSelectionModel().selectNext();
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }

    /**
     * #4 fetch static info about the contest from the server via http request
     */
    public void fetchStaticInfoContest() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_STATIC_CONTEST_INFO_SRC).newBuilder();
        urlBuilder.addQueryParameter(QueryParameter.UBOAT_NAME, uboatName.get());
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();


                if (response.code() != 200) {
                    DTOstatus staticInfoStatus = gson.fromJson(dtoAsStr, DTOstatus.class);

                    Platform.runLater(() -> {
                        setStatusMessage(convertProblemToMessage(staticInfoStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    DTOstaticContestInfo staticInfoStatus = gson.fromJson(dtoAsStr, DTOstaticContestInfo.class);
                    Platform.runLater(() -> {
                        tabPaneBodyController.displayStaticContestInfo(staticInfoStatus.getAlliesInfo(), staticInfoStatus.getBattlefieldInfo());
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }

    /**
     * #6 update Active Teams Info - Timer Task
     *
     * @param alliesInfoList
     */
    public void updateActiveTeamsInfo(List<AllieInfo> alliesInfoList) {
        tabPaneBodyController.updateActiveTeamsInfo(alliesInfoList);
    }

    /**
     * confirm the contest is over and alert the server
     */
    public void approveContestIsOver() {
        String body = "";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + APPROVE_ALLIE_FINISH_GAME_SRC).newBuilder();
        urlBuilder.addQueryParameter(QueryParameter.UBOAT_NAME, uboatName.get());
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(body.getBytes()))
                .build();
        client.newCall(request).enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();

                if (response.code() != 200) {
                    DTOstatus approveStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        setStatusMessage(convertProblemToMessage(approveStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    Platform.runLater(() -> {
                        setStatusMessage("Contest is Approved", MessageTone.INFO);
                        unsubscribeFromCurrentContest();
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
        tabPaneBodyController.clearOldResults();
        totalDistinctCandidates.set(0);
    }

    /**
     * creates a Candidate that shows in the flow-pane at the ui
     *
     * @param candidate the candidate to create a tile from
     */
    synchronized public void createCandidateTile(Candidate candidate, String allieName, String agentName) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/allies/body/screen2/candidate/tile/candidateTile.fxml"));
            Node singleCandidateTile = loader.load();
            CandidateTileController candidateTileController = loader.getController();

            candidateTileController.setDecipheredText(candidate.getDecipheredText());
            candidateTileController.setRotorsIDs(candidate.getRotorsIDs());
            candidateTileController.setWindowsCharsAndNotches(candidate.getWindowChars(), candidate.getNotchPositions());
            candidateTileController.setReflectorSymbol(candidate.getReflectorSymbol());
            candidateTileController.setProcessedByAllieName(allieName);
            candidateTileController.setProcessedByAgentName(agentName);
            totalDistinctCandidates.set(totalDistinctCandidates.get() + 1);
            System.out.println(totalDistinctCandidates.get());
            tabPaneBodyController.insertCandidateToFlowPane(singleCandidateTile);
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
     * set the ok http client in the timers
     */
    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.client = okHttpClient;
        this.fetchLoggedAgentsInfoTimerTask.setClient(okHttpClient);
        this.fetchContestsInfoTimerTask.setClient(okHttpClient);
    }

    /**
     * display the relevant message to the user
     *
     * @param problem the kind of message
     * @return string of message
     */
    public String convertProblemToMessage(Problem problem) {
        switch (problem) {
            case CIPHER_INPUT_EMPTY_STRING:
                return "Please enter some text.";
            case CIPHER_INPUT_NOT_IN_ALPHABET:
                return "The text should contain only letters from the machine's alphabet.";
            case NOT_ENOUGH_LOGGED_AGENTS:
                return "Not enough agents are logged to your team.";
            case MISSING_TASK_SIZE:
                return "Please enter a task size before pressing \"ready\".";
            case MISSING_AGENTS_AMOUNT:
                return "Please enter a number of agents before pressing \"ready\".";
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
                
            case UBOAT_LOGGED_OUT:
                return "The UBoat of the contest has logged out.";
            default:
                return problem.name();
        }
    }

    /**
     * display all logged agents infos
     *
     * @param loggedAgents
     */
    public void updateLoggedAgentsInfo(Set<AgentInfo> loggedAgents) {
        tabPaneBodyController.updateLoggedAgentsInfo(loggedAgents);
    }

    /**
     * disaply a list of all contests
     *
     * @param allBattlefields all contests
     */
    public void displayContestsInfo(List<BattlefieldInfo> allBattlefields) {
        tabPaneBodyController.clearContests();

        for (BattlefieldInfo battlefieldInfo : allBattlefields) {
            createContestTile(battlefieldInfo);
        }
    }

    /**
     * create a contest tile object
     *
     * @param battlefieldInfo and load it to display
     */
    private void createContestTile(BattlefieldInfo battlefieldInfo) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/allies/body/screen1/contest/tile/contestTile.fxml"));
            Node singleContestTile = loader.load();
            ContestTileController contestTileController = loader.getController();

            contestTileController.setBattlefieldName(battlefieldInfo.getBattleName());
            contestTileController.setUboatName(battlefieldInfo.getUboatName());
            String isActiveStatusStr = "Idle";
            if (battlefieldInfo.isActive()) {
                isActiveStatusStr = "Active";
            }
            contestTileController.setIsActiveStatus(isActiveStatusStr);
            contestTileController.setDifficultyLevel(battlefieldInfo.getDifficultyLevel().name());
            contestTileController.setAlliesSubscribedRequired(String.valueOf(battlefieldInfo.getNumOfLoggedAllies()), String.valueOf(battlefieldInfo.getNumOfRequiredAllies()));
            contestTileController.bindSubscriptionButton(isSubscribedToContest);
            contestTileController.setParentController(tabPaneBodyController);
            tabPaneBodyController.insertContestToFlowPane(singleContestTile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserName(String username) {
        this.usernameProperty.set(username);
    }

    public void clearOldCandidates() {
        tabPaneBodyController.clearOldCandidates();
    }

    public void displayDynamicContestInfo(Set<AgentInfo> agentsInfo, JobProgressInfo jobStatus) {
        tabPaneBodyController.displayDynamicContestInfo(agentsInfo, jobStatus);

        // clear old candidates from flow-pane
        clearOldCandidates();
        totalDistinctCandidates.set(0);
    }

    public MainController getMainController() {
        return this;
    }

    public void cancelContestStatusTimer() {
        fetchContestStatusTimer.cancel();
        fetchContestStatusTimerTask.cancel();
    }

    public void logoutAllie(MouseEvent event) {

        // cancel this initialized timers
        fetchContestsInfoTimerTask.cancel();
        fetchContestsInfoTimer.cancel();
        fetchLoggedAgentsInfoTimerTask.cancel();
        fetchLoggedAgentsInfoTimer.cancel();

        if (isSubscribedToContest.get()) {
            fetchIsSubscribedToContestTimer.cancel();
            fetchIsSubscribedToContestTimerTask.cancel();
        }
        if (isReady.get()) {
            fetchContestStatusTimer.cancel();
            fetchContestStatusTimerTask.cancel();
        }
        if (isContestActive.get()) {
            fetchDynamicContestInfoTimer.cancel();
            fetchDynamicContestInfoTimerTask.cancel();
            fetchAlliesInfoTimer.cancel();
            fetchAlliesInfoTimerTask.cancel();
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
                System.out.println("logged out resp");
                System.out.println("Code: " + response.code());
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
                            URL loginFxml = getClass().getResource("/allie/login/login.fxml");
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

    public void unsubscribeFromCurrentContest() {
        isContestActive.set(false);
        cleanOldResults();
        isSubscribedToContest.set(false);
        tabPaneBody.getSelectionModel().selectFirst();
    }
}
