package app;

import agent.AgentTask;
import body.screen1.agentprogress.AgentProgressController;
import body.screen1.candidate.area.CandidatesAreaController;
import body.screen1.candidate.tile.CandidateTileController;
import body.screen1.contest.ContestAndTeamAreaController;
import candidate.AgentConclusion;
import candidate.Candidate;
import com.google.gson.Gson;
import dictionary.Dictionary;
import dto.*;
import header.HeaderController;
import http.url.QueryParameter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import okhttp3.*;
import problem.Problem;
import tasks.*;
import winner.LoseWinAreaController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.*;

import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.*;

public class MainController {

    private OkHttpClient client;

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
    private GridPane loseWinArea;

    @FXML
    private LoseWinAreaController loseWinAreaController;

    @FXML
    private HBox statusBar;

    @FXML
    private Label statusLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Rectangle statusBackShape;

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
    private Timer contestStatusTimer;
    private FetchContestStatusTimer fetchContestStatusTimer;
    private Timer submitConclusionsTimer;
    private SubmitConclusionsTimer submitAllConclusionsTimer;
    private Timer tasksTimer;
    private FetchTasksTimer fetchTasksTimer;
    private Timer subscribeTimer;
    private FetchSubscriptionStatusTimer fetchSubscribeTimer;
    private Timer waitForAllieApproveTimer;
    private WaitForAllieApproveFinishGameTimer waitForAllieApprovalTimer;
    private BooleanProperty isSubscribed;


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
        this.numOfTasksInQueue = new SimpleIntegerProperty();
        this.numOfTotalPulledTasks = new SimpleIntegerProperty();
        this.numOfTotalCompletedTasks = new SimpleIntegerProperty();
        this.uboatName = new SimpleStringProperty("");
        this.allieName = new SimpleStringProperty("");

        // Timers
        this.contestStatusTimer = new Timer();
        this.submitConclusionsTimer = new Timer();
        this.tasksTimer = new Timer();
        this.subscribeTimer = new Timer();
        this.waitForAllieApproveTimer = new Timer();
        this.fetchContestStatusTimer = new FetchContestStatusTimer(isContestActive, allieName);
        this.submitAllConclusionsTimer = new SubmitConclusionsTimer(this, allieName, uboatName);
        this.fetchTasksTimer = new FetchTasksTimer(this, allieName, uboatName);
        this.waitForAllieApprovalTimer = new WaitForAllieApproveFinishGameTimer(this, allieName, uboatName);
        this.fetchSubscribeTimer = new FetchSubscriptionStatusTimer(isSubscribed, allieName);


        isSubscribed.addListener((o, oldVal, newVal) -> {
            if (newVal) {
                subscribeTimer.cancel();
                fetchSubscribeTimer.cancel();
                // allie just subscribed
                setStatusMessage("Allie has subscribed to a Contest", MessageTone.INFO);
                fetchStaticInfoContest();
                contestStatusTimer.schedule(fetchContestStatusTimer, REFRESH_RATE, REFRESH_RATE);
            } else {
                // allie has unsubscribed, when the contest is finished
                subscribeTimer.schedule(fetchSubscribeTimer, REFRESH_RATE, REFRESH_RATE);
            }
        });


        isContestActive.addListener((o, oldVal, newVal) -> {
            if (newVal) {
                // contest == active
                // stop allies & status timers
                setStatusMessage("Contest has started", MessageTone.INFO);

                // schedule fetch candidates timer & fetch active teams
                tasksTimer.schedule(fetchTasksTimer, REFRESH_RATE, REFRESH_RATE);
                submitConclusionsTimer.schedule(submitAllConclusionsTimer, REFRESH_RATE, REFRESH_RATE);
            } else {
                // contest == not active => winner found
                fetchWinnerMessage();
                threadPool.shutdownNow();
                waitForAllieApproveTimer.schedule(waitForAllieApprovalTimer, REFRESH_RATE, REFRESH_RATE);
            }
        });

        // bindings
        contestAndTeamAreaController.bindComponents(allieName);
        agentProgressController.bindComponents(numOfTasksInQueue, numOfTotalPulledTasks, numOfTotalCompletedTasks);

        // general setting to initialize sub components
        messageLabel.textProperty().bind(statusLabel.textProperty());
        messageLabel.opacityProperty().bind(statusBackShape.opacityProperty());
        statusBackShape.heightProperty().bind(Bindings.add(2, statusLabel.heightProperty()));
        statusBackShape.widthProperty().bind(statusLabel.widthProperty());
        statusBackShape.setStrokeWidth(0);
        statusBackShape.setOpacity(0);
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
                System.out.println("Body: " + dtoAsStr);
                Gson gson = new Gson();


                if (response.code() != 200) {
                    DTOstatus winnerStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> setStatusMessage(convertProblemToMessage(winnerStatus.getDetails()), MessageTone.ERROR));

                } else {
                    DTOwinner winnerStatus = gson.fromJson(dtoAsStr, DTOwinner.class);
                    Platform.runLater(() -> {
                        setStatusMessage("Found a Winner !", MessageTone.SUCCESS);
                        loseWinAreaController.setWinnerTeamLabelName(winnerStatus.getAllieWinner().getAllieName());
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
    public void fetchStaticInfoContest() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_STATIC_CONTEST_INFO_SRC).newBuilder();
        urlBuilder.addQueryParameter(QueryParameter.ALLIE_NAME, allieName.get());
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                System.out.println("Body: " + dtoAsStr);
                Gson gson = new Gson();

                if (response.code() != 200) {
                    DTOstatus staticInfoStatus = gson.fromJson(dtoAsStr, DTOstatus.class);

                    Platform.runLater(() -> setStatusMessage(convertProblemToMessage(staticInfoStatus.getDetails()), MessageTone.ERROR));

                } else {
                    DTOstaticContestInfo staticInfoStatus = gson.fromJson(dtoAsStr, DTOstaticContestInfo.class);
                    Platform.runLater(() -> {
                        uboatName.set(staticInfoStatus.getBattlefieldInfo().getUboatName());
                        contestAndTeamAreaController.displayStaticContestInfo(staticInfoStatus.getBattlefieldInfo(), allieName.get());
                        dictionary = staticInfoStatus.getBattlefieldInfo().getDictionary();
                    });

                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }

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
            loader.setLocation(getClass().getResource("/body/screen3/candidate/tile/candidateTile.fxml"));
            Node singleCandidateTile = loader.load();
            CandidateTileController candidateTileController = loader.getController();

            candidateTileController.setDecipheredText(candidate.getDecipheredText());
            candidateTileController.setRotorsIDs(candidate.getRotorsIDs());
            candidateTileController.setWindowsCharsAndNotches(candidate.getWindowChars(), candidate.getNotchPositions());
            candidateTileController.setReflectorSymbol(candidate.getReflectorSymbol());
            candidateTileController.setProcessedByAllieName(allieName);
            candidateTileController.setProcessedByAgentName(agentName);
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
        this.fetchContestStatusTimer.setClient(client);
        this.fetchSubscribeTimer.setClient(client);
        this.fetchTasksTimer.setClient(client);
        this.submitAllConclusionsTimer.setClient(client);
        this.waitForAllieApprovalTimer.setClient(client);
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
            default:
                return "";
        }
    }

    /**
     * @return a list of AgentConclusion that are fetched from the conclusions queue
     */
    public List<AgentConclusion> getConclusions() {
        List<AgentConclusion> conclusions = new ArrayList<>();
        while (!conclusionsQueue.isEmpty()) {
            AgentConclusion conclusion = conclusionsQueue.poll();
            if (conclusion != null) {
                numOfTasksInQueue.set(numOfTasksInQueue.get() - 1);
                conclusions.add(conclusion);
            } else {
                break;
            }
        }
        return conclusions;
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
    public void setInitialSettings(String allieName, int numOfThreads, int numOfTasksPerPull, String agentName) {
        this.allieName.set(allieName);
        this.numOfThreads = numOfThreads;
        this.numOfTasksToPull = numOfTasksPerPull;
        this.agentName = agentName;

        // initializes the thread pool
        this.threadPool = Executors.newFixedThreadPool(numOfThreads);

        subscribeTimer.schedule(fetchSubscribeTimer, REFRESH_RATE, REFRESH_RATE);
    }

    /**
     * given some AgentTask, injects some information to the AgentTask and sents in to the thread pool to execute
     *
     * @param taskList a list of the AgentTask to execute
     */
    public void executeTasks(List<AgentTask> taskList) {
        numOfTotalPulledTasks.setValue(numOfTotalPulledTasks.get() + taskList.size());
        numOfTasksInQueue.setValue(numOfTasksInQueue.get() + taskList.size());

        for (AgentTask task : taskList) {
            task.setAgentName(agentName);
            task.setDictionary(dictionary);
            task.setIsContestActiveProperty(isContestActive);
            task.setCandidatesQueue(conclusionsQueue);
            task.setNumOfCompletedTasksProperty(numOfTotalCompletedTasks);

            threadPool.execute(task);
        }
    }
}

