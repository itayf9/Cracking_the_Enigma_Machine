package app;

import body.BodyController;
import body.screen1.contest.tile.ContestTileController;
import body.screen2.candidate.tile.CandidateTileController;
import candidate.AgentConclusion;
import candidate.Candidate;
import com.google.gson.Gson;
import dto.DTOresetConfig;
import dto.DTOstaticContestInfo;
import dto.DTOstatus;
import header.HeaderController;
import http.url.Constants;
import info.agent.AgentInfo;
import info.allie.AllieInfo;
import info.battlefield.BattlefieldInfo;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import jobprogress.JobProgressInfo;
import okhttp3.*;
import problem.Problem;
import tasks.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import static http.url.URLconst.BASE_URL;
import static http.url.URLconst.CONTENT_TYPE;

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
     * bruteforce stuff
     */
    private IntegerProperty totalDistinctCandidates;

    /**
     * contest stuff
     */
    public final static int REFRESH_RATE = 2000;
    private BooleanProperty isContestActive;
    private String uboatName;
    private Timer contestStatusTimer;
    private FetchContestStatusTimer fetchContestStatusTimer;
    private Timer alliesInfoTimer;
    private FetchAlliesInfoTimer fetchAlliesInfoTimer;
    private Timer allCandidatesTimer;
    private FetchCandidatesTimer fetchAllCandidatesTimer;
    private Timer loggedAgentsTimer;
    private FetchLoggedAgentsInfoTimer fetchLoggedAgentsInfoTimer;
    private Timer dynamicContestInfoTimer;
    private FetchDynamicContestInfoTimer fetchDynamicContestInfoTimer;
    private Timer contestsInfoTimer;
    private FetchContestsInfoTimer fetchContestsInfoTimer;

    @FXML
    public void initialize() {

        // controller initialize
        headerController.setMainController(this);
        bodyController.setMainController(this);

        // property initialize
        this.totalDistinctCandidates = new SimpleIntegerProperty();
        this.isContestActive = new SimpleBooleanProperty(false);

        // Timers
        this.contestStatusTimer = new Timer();
        this.fetchContestStatusTimer = new FetchContestStatusTimer(isContestActive);
        this.alliesInfoTimer = new Timer();
        this.fetchAlliesInfoTimer = new FetchAlliesInfoTimer(this);
        this.allCandidatesTimer = new Timer();
        this.fetchAllCandidatesTimer = new FetchCandidatesTimer(this);
        this.loggedAgentsTimer = new Timer();
        this.fetchLoggedAgentsInfoTimer = new FetchLoggedAgentsInfoTimer(this);
        this.dynamicContestInfoTimer = new Timer();
        this.fetchDynamicContestInfoTimer = new FetchDynamicContestInfoTimer(this);
        this.contestsInfoTimer = new Timer();
        this.fetchContestsInfoTimer = new FetchContestsInfoTimer(this);

        isContestActive.addListener((o, oldVal, newVal) -> {
            if (newVal) {
                // contest == active
                // stop allies & status timers
                setStatusMessage("Contest has started", MessageTone.INFO);
                fetchContestStatusTimer.cancel();
                contestStatusTimer.cancel();

                // schedule fetch candidates timer & fetch active teams
                alliesInfoTimer.schedule(fetchAlliesInfoTimer, REFRESH_RATE, REFRESH_RATE);
                allCandidatesTimer.schedule(fetchAllCandidatesTimer, REFRESH_RATE, REFRESH_RATE);
                dynamicContestInfoTimer.schedule(fetchDynamicContestInfoTimer, REFRESH_RATE, REFRESH_RATE);
            } else {
                // contest == not active => winner found
                fetchAllCandidatesTimer.cancel();
                allCandidatesTimer.cancel();
                fetchAlliesInfoTimer.cancel();
                alliesInfoTimer.cancel();
            }
        });

        // binding initialize
        bodyController.bindComponents(totalDistinctCandidates);

        // general setting to initialize sub components
        messageLabel.textProperty().bind(statusLabel.textProperty());
        messageLabel.opacityProperty().bind(statusBackShape.opacityProperty());
        statusBackShape.heightProperty().bind(Bindings.add(2, statusLabel.heightProperty()));
        statusBackShape.widthProperty().bind(statusLabel.widthProperty());
        statusBackShape.setStrokeWidth(0);
        statusBackShape.setOpacity(0);

        loggedAgentsTimer.schedule(fetchLoggedAgentsInfoTimer, REFRESH_RATE, REFRESH_RATE);
        contestsInfoTimer.schedule(fetchContestsInfoTimer, REFRESH_RATE, REFRESH_RATE);
    }

    /**
     * #1 fetch logged agents info from server via http request
     */
    public void fetchLoggedAgentsInfoFromServer() {
        String body = "";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/fetch/agents").newBuilder();
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

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/subscribe").newBuilder();
        urlBuilder.addQueryParameter(Constants.UBOAT_NAME, uboatNameToRegister);
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(body.getBytes()))
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                System.out.println("Body: " + dtoAsStr);
                Gson gson = new Gson();

                DTOstatus subscribeStatus = gson.fromJson(dtoAsStr, DTOstatus.class);

                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        setStatusMessage(convertProblemToMessage(subscribeStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    Platform.runLater(() -> {
                        uboatName = uboatNameToRegister;
                        fetchDynamicContestInfoTimer.setUboatName(uboatName);
                        fetchAlliesInfoTimer.setUboatName(uboatName);

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
    public void setReady() {
        String body = "";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/fetch/contest/static").newBuilder();
        urlBuilder.addQueryParameter(Constants.UBOAT_NAME, uboatName);
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(body.getBytes()))
                .build();
        client.newCall(request).enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                System.out.println("Body: " + dtoAsStr);
                Gson gson = new Gson();


                if (response.code() != 200) {
                    DTOstatus readyStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        setStatusMessage(convertProblemToMessage(readyStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    Platform.runLater(() -> {
                        cleanOldResults();
                        contestStatusTimer.schedule(fetchContestStatusTimer, REFRESH_RATE, REFRESH_RATE);
                        setStatusMessage("Allie is Ready", MessageTone.INFO);
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
        String body = "";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/ready").newBuilder();
        urlBuilder.addQueryParameter(Constants.UBOAT_NAME, uboatName);
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(body.getBytes()))
                .build();
        client.newCall(request).enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                System.out.println("Body: " + dtoAsStr);
                Gson gson = new Gson();


                if (response.code() != 200) {
                    DTOstatus staticInfoStatus = gson.fromJson(dtoAsStr, DTOstatus.class);

                    Platform.runLater(() -> {
                        setStatusMessage(convertProblemToMessage(staticInfoStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    DTOstaticContestInfo staticInfoStatus = gson.fromJson(dtoAsStr, DTOstaticContestInfo.class);
                    Platform.runLater(() -> {
                        bodyController.displayStaticContestInfo(staticInfoStatus.getAlliesInfo(), staticInfoStatus.getBattlefieldInfo());
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
        bodyController.updateActiveTeamsInfo(alliesInfoList);
    }

    /**
     * display all candidates from server
     *
     * @param conclusions
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
     * confirm the contest is over and alert the server
     */
    public void approveContestIsOver() {
        String body = "";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/fetch/contest/static").newBuilder();
        urlBuilder.addQueryParameter(Constants.UBOAT_NAME, uboatName);
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(body.getBytes()))
                .build();
        client.newCall(request).enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                System.out.println("Body: " + dtoAsStr);
                Gson gson = new Gson();

                if (response.code() != 200) {
                    DTOstatus approveStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        setStatusMessage(convertProblemToMessage(approveStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    Platform.runLater(() -> {
                        setStatusMessage("Contest is Approved", MessageTone.INFO);
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
        bodyController.clearOldResultsOfBruteForce();
        totalDistinctCandidates.set(0);
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

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.client = okHttpClient;
        this.fetchLoggedAgentsInfoTimer.setClient(client);
        this.fetchContestStatusTimer.setClient(client);
        this.fetchAllCandidatesTimer.setClient(client);
        this.fetchAlliesInfoTimer.setClient(client);
        this.fetchDynamicContestInfoTimer.setClient(client);
        this.fetchContestsInfoTimer.setClient(client);
    }

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

    public void updateLoggedAgentsInfo(Set<AgentInfo> loggedAgents) {
        bodyController.updateLoggedAgentsInfo(loggedAgents);
    }

    public void displayDynamicContestInfo(Set<AgentInfo> agentsInfo, JobProgressInfo jobStatus, List<AgentConclusion> allCandidates) {
        bodyController.displayDynamicContestInfo(agentsInfo, jobStatus, allCandidates);
    }

    public void displayContestsInfo(List<BattlefieldInfo> allBattlefields) {

        bodyController.clearContests();
        System.out.println("######################");
        System.out.println(allBattlefields.size());
        System.out.println("######################");

        for (BattlefieldInfo battlefieldInfo : allBattlefields) {
            createContestTile(battlefieldInfo);
        }
    }

    private void createContestTile(BattlefieldInfo battlefieldInfo) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/body/screen1/contest/tile/contestTile.fxml"));
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
            contestTileController.setParentController(bodyController);
            bodyController.insertContestToFlowPane(singleContestTile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}