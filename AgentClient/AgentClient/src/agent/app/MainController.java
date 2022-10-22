package agent.app;

import agent.AgentTask;
import agent.tasks.*;
import agent.screen1.agentprogress.AgentProgressController;
import agent.screen1.candidate.area.CandidatesAreaController;
import agent.screen1.candidate.tile.CandidateTileController;
import agent.screen1.contest.ContestAndTeamAreaController;
import candidate.AgentConclusion;
import candidate.Candidate;
import com.google.gson.Gson;
import dictionary.Dictionary;
import dto.*;
import agent.header.HeaderController;
import http.url.QueryParameter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import okhttp3.*;
import problem.Problem;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.*;

import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.*;

public class MainController {

    private OkHttpClient client;

    private StringProperty usernameProperty;

    /**
     * app private members
     */

    @FXML
    private GridPane appGridPane;

    /**
     * screen 1 components
     */
    @FXML
    private GridPane header;
    @FXML
    private HeaderController headerController;

    @FXML
    private GridPane contestAndTeamArea;

    @FXML
    private ContestAndTeamAreaController contestAndTeamAreaController;

    @FXML
    private GridPane agentProgress;

    @FXML
    private AgentProgressController agentProgressController;

    @FXML
    private GridPane candidatesArea;

    @FXML
    private CandidatesAreaController candidatesAreaController;

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
    private Dictionary dictionary;
    private ExecutorService threadPool;
    private BlockingQueue<AgentConclusion> conclusionsQueue;
    private IntegerProperty numOfTotalPulledTasks;
    private IntegerProperty numOfTasksInQueue;
    private IntegerProperty numOfTotalCompletedTasks;
    private CountDownLatch cdl;

    /**
     * contest stuff
     */
    public final static int REFRESH_RATE = 2000;
    private BooleanProperty isContestActive;
    private String agentName;
    private StringProperty allieName;
    private StringProperty uboatName;
    private int numOfThreads;
    private int numOfTasksToPull;
    private Timer fetchContestStatusTimer;
    private FetchContestStatusTimer fetchContestStatusTimerTask;
    private Timer submitConclusionsTimer;
    private SubmitConclusionsTimer submitConclusionsTimerTask;
    private FetchTasksThread fetchTasksThread;
    private Timer fetchSubscribeTimer;
    private FetchSubscriptionStatusTimer fetchSubscribeTimerTask;
    private Timer fetchStaticInfoContestTimer;
    private FetchStaticContestInfoTimer fetchStaticInfoContestTimerTask;
    private Timer fetchIsAgentCanGetOutOfWaitingModeTimer;
    private FetchIsAgentCanGetOutOfWaitingModeTimer fetchIsAgentCanGetOutOfWaitingModeTimerTask;
    private BooleanProperty isSubscribed;
    private boolean uboatLoggedOut = false;


    @FXML
    public void initialize() {

        // controller initialize
        headerController.setMainController(this);
        candidatesAreaController.setParentController(this);
        contestAndTeamAreaController.setParentController(this);
        agentProgressController.setParentController(this);
        this.conclusionsQueue = new LinkedBlockingQueue<>();

        // property initialize
        this.totalDistinctCandidates = new SimpleIntegerProperty();
        this.isContestActive = new SimpleBooleanProperty(false);
        this.isSubscribed = new SimpleBooleanProperty();
        this.numOfTasksInQueue = new SimpleIntegerProperty(0);
        this.numOfTotalPulledTasks = new SimpleIntegerProperty();
        this.numOfTotalCompletedTasks = new SimpleIntegerProperty();
        this.uboatName = new SimpleStringProperty("");
        this.allieName = new SimpleStringProperty("");
        this.usernameProperty = new SimpleStringProperty("");

        // Timers
        this.fetchTasksThread = new FetchTasksThread(this, allieName, uboatName);


        isSubscribed.addListener((o, oldVal, newVal) -> {
            if (newVal) {
                // allie just subscribed
                setStatusMessage("Allie has subscribed to a Contest", MessageTone.INFO);
                uboatLoggedOut = false;
                this.fetchStaticInfoContestTimer = new Timer();
                this.fetchStaticInfoContestTimerTask = new FetchStaticContestInfoTimer(this, allieName, client);
                fetchStaticInfoContestTimer.schedule(fetchStaticInfoContestTimerTask, REFRESH_RATE, REFRESH_RATE);
                this.fetchContestStatusTimer = new Timer();
                this.fetchContestStatusTimerTask = new FetchContestStatusTimer(isContestActive, allieName, client, this);
                fetchContestStatusTimer.schedule(fetchContestStatusTimerTask, REFRESH_RATE, REFRESH_RATE);
            } else {
                fetchContestStatusTimer.cancel();
                fetchContestStatusTimerTask.cancel();
                fetchStaticInfoContestTimer.cancel();
                fetchStaticInfoContestTimerTask.cancel();
                // allie has unsubscribed, when the contest is finished
                setStatusMessage("Team has unsubscribed from the game", MessageTone.INFO);
                cleanOldResults();
            }
        });

        isContestActive.addListener((o, oldVal, newVal) -> {
            if (newVal) {
                threadPool = Executors.newFixedThreadPool(numOfThreads);
                this.cdl = new CountDownLatch(numOfTasksInQueue.get());
                new Thread(this.fetchTasksThread).start();
                // contest == active
                // stop allies & status timers
                setStatusMessage("Contest has started", MessageTone.INFO);
                fetchStaticInfoContestTimerTask.run();
                cancelStaticInfoTimer();
                // schedule fetch candidates timer & fetch active teams
                this.submitConclusionsTimer = new Timer();
                this.submitConclusionsTimerTask = new SubmitConclusionsTimer(this, allieName, uboatName, client);
                submitConclusionsTimer.schedule(submitConclusionsTimerTask, REFRESH_RATE, REFRESH_RATE);
            } else {
                // contest == not active =>
                if (!uboatLoggedOut) {
                    fetchWinnerMessage();
                    setStatusMessage("Winner Found", MessageTone.INFO);
                }
                threadPool.shutdownNow();
                submitConclusionsTimer.cancel();
                submitConclusionsTimerTask.cancel();
            }
        });

        // bindings
        candidatesAreaController.bindInitPropertiesToLabels(totalDistinctCandidates);
        contestAndTeamAreaController.bindComponents(allieName);
        agentProgressController.bindComponents(numOfTasksInQueue, numOfTotalPulledTasks, numOfTotalCompletedTasks);
        contestAndTeamAreaController.bindIsActiveLabel(isContestActive);

        // general setting to initialize sub components
        messageLabel.textProperty().bind(statusLabel.textProperty());
        messageLabel.opacityProperty().bind(statusBackShape.opacityProperty());
        statusBackShape.heightProperty().bind(Bindings.add(2, statusLabel.heightProperty()));
        statusBackShape.widthProperty().bind(statusLabel.widthProperty());
        statusBackShape.setStrokeWidth(0);
        statusBackShape.setOpacity(0);

        headerController.bindComponents(isContestActive, usernameProperty);
    }

    /**
     * fetch the winner allie name from server and display it to everyone
     */
    private void fetchWinnerMessage() {
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
                    Platform.runLater(() -> setStatusMessage(convertProblemToMessage(winnerStatus.getDetails()), MessageTone.ERROR));

                } else {
                    DTOwinner winnerStatus = gson.fromJson(dtoAsStr, DTOwinner.class);
                    Platform.runLater(() -> {
                        setStatusMessage("Found a Winner !", MessageTone.SUCCESS);
                        headerController.displayWinnerMessage(winnerStatus.getAllieWinner());
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }

    /**
     * fetch static info about the contest from the server via http request
     */

    /**
     * display all candidates from server
     *
     * @param conclusions agents conclusions objects
     */
    public void displayAllCandidates(List<AgentConclusion> conclusions) {

        // goes through all the conclusions
        for (AgentConclusion conclusion : conclusions) {
            String currentAllieName = conclusion.getAllieName();
            String currentAgentName = conclusion.getAgentName();

            // goes through all the candidates of each conclusion
            for (Candidate candidate : conclusion.getCandidates()) {

                // adds a new tile to the candidates area
                createCandidateTile(candidate, currentAllieName, currentAgentName);
            }
        }
    }

    /**
     * clear all findings of last contest and labels of progress
     */
    public void cleanOldResults() {
        headerController.approveAllieFinishGameAction();
        totalDistinctCandidates.set(0);
        numOfTasksInQueue.set(0);
        numOfTotalPulledTasks.set(0);
        numOfTotalCompletedTasks.set(0);
        contestAndTeamAreaController.clearOldResult();
        candidatesAreaController.clearOldResult();
    }

    /**
     * creates a Candidate that shows in the flow-pane at the ui
     *
     * @param candidate the candidate to create a tile from
     * @param allieName the name of the allie of the current agent
     * @param agentName the name of the uboat of the current contest
     */
    private void createCandidateTile(Candidate candidate, String allieName, String agentName) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/agent/screen1/candidate/tile/candidateTile.fxml"));
            Node singleCandidateTile = loader.load();
            CandidateTileController candidateTileController = loader.getController();

            candidateTileController.setDecipheredText(candidate.getDecipheredText());
            candidateTileController.setRotorsIDs(candidate.getRotorsIDs());
            candidateTileController.setWindowsCharsAndNotches(candidate.getWindowChars(), candidate.getNotchPositions());
            candidateTileController.setReflectorSymbol(candidate.getReflectorSymbol());
            candidateTileController.setProcessedByAllieName(allieName);
            candidateTileController.setProcessedByAgentName(agentName);
            totalDistinctCandidates.set(totalDistinctCandidates.get() + 1);
            candidatesAreaController.insertCandidateToFlowPane(singleCandidateTile);
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
     *
     * @param okHttpClient the okHttpClient to be set
     */
    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.client = okHttpClient;
        this.fetchTasksThread.setClient(client);
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
     * @return a list of AgentConclusion that are fetched from the conclusions queue
     */
    public DTOagentConclusions getConclusions() {
        List<AgentConclusion> conclusions = new ArrayList<>();
        while (!conclusionsQueue.isEmpty()) {
            AgentConclusion conclusion = conclusionsQueue.poll();
            if (conclusion != null) {
                conclusions.add(conclusion);
            } else {
                break;
            }
        }
        return new DTOagentConclusions(true, Problem.NO_PROBLEM, conclusions);
    }

    /**
     * sets the isSubscribed property
     *
     * @param isSubscribed the boolean value to set the property to
     */
    public void setIsSubscribed(boolean isSubscribed) {
        this.isSubscribed.set(isSubscribed);
    }

    /**
     * sets some settings of the current agent after the login
     *
     * @param allieName         the name of the allie of the agent
     * @param numOfThreads      the number of threads as mentioned in the login screen
     * @param numOfTasksPerPull the number of tasks per pull as mentioned in the login screen
     * @param agentName         the username of the current agent
     */
    public void setInitialSettings(String allieName, int numOfThreads, int numOfTasksPerPull, String agentName, boolean isContestActive) {
        this.allieName.set(allieName);
        this.numOfThreads = numOfThreads;
        this.numOfTasksToPull = numOfTasksPerPull;
        this.agentName = agentName;
        setUserName(agentName);
        if (isContestActive) {
            this.fetchIsAgentCanGetOutOfWaitingModeTimer = new Timer();
            this.fetchIsAgentCanGetOutOfWaitingModeTimerTask = new FetchIsAgentCanGetOutOfWaitingModeTimer(allieName, client, this);
            return;
        }

        this.fetchSubscribeTimer = new Timer();
        this.fetchSubscribeTimerTask = new FetchSubscriptionStatusTimer(isSubscribed, this.allieName, client);
        fetchSubscribeTimer.schedule(fetchSubscribeTimerTask, REFRESH_RATE, REFRESH_RATE);
    }

    public CountDownLatch getCountDownLatch() {
        return cdl;
    }

    public void updateStaticContestInfo(DTOstaticContestInfo staticInfoStatus) {
        uboatName.set(staticInfoStatus.getBattlefieldInfo().getUboatName());
        contestAndTeamAreaController.displayStaticContestInfo(staticInfoStatus.getBattlefieldInfo(), allieName.get());
        dictionary = staticInfoStatus.getBattlefieldInfo().getDictionary();
    }

    public void setUserName(String username) {
        this.usernameProperty.set(username);
    }

    public void updateTaskProperty(List<AgentTask> taskList) {

    }

    /**
     * given some AgentTask, injects some information to the AgentTask and sends in to the thread pool to execute
     *
     * @param taskList a list of the AgentTask to execute
     */
    public void executeTasks(List<AgentTask> taskList) {

        numOfTotalPulledTasks.setValue(numOfTotalPulledTasks.get() + taskList.size());
        numOfTasksInQueue.setValue(numOfTasksInQueue.get() + taskList.size());

        if (isContestActive.get()) {
            this.cdl = new CountDownLatch(numOfTasksInQueue.get());
            new Thread(this.fetchTasksThread).start();
        }

        for (AgentTask task : taskList) {
            task.setAgentName(agentName);
            task.setDictionary(dictionary);
            task.setIsContestActiveProperty(isContestActive);
            task.setCandidatesQueue(conclusionsQueue);
            task.setNumOfTasksInQueueProperty(numOfTasksInQueue);
            task.setNumOfCompletedTasksProperty(numOfTotalCompletedTasks);
            task.setCdl(cdl);

            // checks that the contests is still active and the thread pool is not shut down
            if (isContestActive.get()) {
                threadPool.execute(task);
            }
        }
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void logoutAgent(MouseEvent event) {
        fetchSubscribeTimer.cancel();
        fetchSubscribeTimerTask.cancel();

        if (isSubscribed.get()) {
            fetchStaticInfoContestTimer.cancel();
            fetchStaticInfoContestTimerTask.cancel();
            fetchContestStatusTimer.cancel();
            fetchContestStatusTimerTask.cancel();
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
                        switchToLoginScreen(event);
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });

    }

    public void switchToLoginScreen(MouseEvent event) {
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader();
            URL loginFxml = getClass().getResource("/agent/login/login.fxml");
            loader.setLocation(loginFxml);
            login = loader.load();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene loginScene = new Scene(login, 300, 300);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public void cancelStaticInfoTimer() {
        fetchStaticInfoContestTimer.cancel();
        fetchStaticInfoContestTimerTask.cancel();
    }

    public void allieUnsubscribedFromCurrentContest() {
        uboatLoggedOut = true;
        isContestActive.set(false);
        isSubscribed.set(false);
        cleanOldResults();
    }

    public void setAgentIsOutOfWaitingMode() {
        this.fetchSubscribeTimer = new Timer();
        this.fetchSubscribeTimerTask = new FetchSubscriptionStatusTimer(isSubscribed, this.allieName, client);
        fetchSubscribeTimer.schedule(fetchSubscribeTimerTask, REFRESH_RATE, REFRESH_RATE);

        this.fetchIsAgentCanGetOutOfWaitingModeTimer.cancel();
        this.fetchIsAgentCanGetOutOfWaitingModeTimerTask.cancel();

    }
}

