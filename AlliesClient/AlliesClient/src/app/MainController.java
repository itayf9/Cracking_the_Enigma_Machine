package app;

import body.BodyController;
import body.screen1.contest.tile.ContestTileController;
import body.screen2.candidate.tile.CandidateTileController;
import candidate.AgentConclusion;
import candidate.Candidate;
import header.HeaderController;
import info.allie.AllieInfo;
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
import okhttp3.*;
import problem.Problem;
import tasks.*;

import java.io.IOException;
import java.util.List;

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
    private BooleanProperty isContestActive;


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
                fetchActiveTeamsInfoTimer.cancel();
                ActiveTeamsInfoTimer.cancel();

                // schedule fetch candidates timer
                candidatesTimer.schedule(fetchCandidatesTimer, REFRESH_RATE, REFRESH_RATE);
            } else {
                // contest == not active => winner found
                fetchAllCandidatesTimer.cancel();
                allCandidatesTimer.cancel();
            }
        });*/

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

    }

    /**
     * #2 register the app to a battlefield with http request to the server
     */
    public void subscribeToBattlefield() {

    }

    /**
     * #3 let the server know we are ready for contest to start
     */
    public void setReady() {

    }

    /**
     * #4 fetch static info about the contest from the server via http request
     */
    public void fetchStaticInfoContest() {

    }

    /**
     * #5 fetch dynamic info about the contest from the server via http request timer...
     */
    public void fetchDynamicInfoContest() {

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
