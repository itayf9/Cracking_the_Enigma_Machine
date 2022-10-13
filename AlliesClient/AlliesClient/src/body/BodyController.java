package body;

import app.MainController;
import app.MessageTone;
import body.screen1.agentsinfo.AgentsInfoController;
import body.screen1.contest.area.ContestsAreaController;
import body.screen2.activeteamsarea.ActiveTeamsController;
import body.screen2.agentsprogress.AgentsProgressController;
import body.screen2.candidate.area.CandidatesAreaController;
import body.screen1.contest.tile.ContestTileController;
import candidate.AgentConclusion;
import info.agent.AgentInfo;
import info.allie.AllieInfo;
import info.battlefield.BattlefieldInfo;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import jobprogress.JobProgressInfo;
import problem.Problem;

import java.util.List;
import java.util.Set;

public class BodyController {

    private MainController mainController;

    /**
     * screen 1
     */
    @FXML
    private GridPane agentsInfo;
    @FXML
    private AgentsInfoController agentsInfoController;

    @FXML
    private GridPane contestsArea;
    @FXML
    private ContestsAreaController contestsAreaController;

    /**
     * screen 2
     */
    @FXML
    private GridPane candidatesArea;
    @FXML
    private CandidatesAreaController candidatesAreaController;

    @FXML
    private GridPane activeTeams;
    @FXML
    private ActiveTeamsController activeTeamsController;

    @FXML
    private GridPane agentProgress;
    @FXML
    private AgentsProgressController agentProgressController;

    @FXML
    private GridPane contestTile;
    @FXML
    private ContestTileController contestTileController;


    /**
     * set up the application, connecting the controllers to their main controller
     */
    @FXML
    public void initialize() {

        //screen 1
        agentsInfoController.setParentController(this);
        contestsAreaController.setParentController(this);

        //screen 2
        candidatesAreaController.setParentController(this);
        activeTeamsController.setParentController(this);
        agentProgressController.setParentController(this);
        contestTileController.setParentController(this);
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
     * init the binding of all subclass components
     *
     * @param totalDistinctCandidates totalDistinctCandidates
     */
    public void bindComponents(IntegerProperty totalDistinctCandidates, BooleanProperty isSubscribedToContest) {

        // brute force dashboard labels bind
        candidatesAreaController.bindInitPropertiesToLabels(totalDistinctCandidates);


    }

    public void insertCandidateToFlowPane(Node singleCandidateTile) {
        candidatesAreaController.insertCandidateToFlowPane(singleCandidateTile);
    }

    public void clearOldResultsOfBruteForce() {
        candidatesAreaController.clearOldResultsOfBruteForce();
    }

    public void setStatusMessage(String statusMessage, MessageTone messageTone) {
        mainController.setStatusMessage(statusMessage, messageTone);
    }

    public String convertProblemToMessage(Problem problem) {
        return mainController.convertProblemToMessage(problem);
    }

    public void updateActiveTeamsInfo(List<AllieInfo> alliesInfoList) {
        activeTeamsController.setTeams(alliesInfoList);
    }

    public void subscribeToBattlefieldAction(String uboatName) {
        mainController.subscribeToBattlefield(uboatName);
    }

    public void updateLoggedAgentsInfo(Set<AgentInfo> loggedAgents) {
        agentsInfoController.setAgents(loggedAgents);
    }

    public void displayStaticContestInfo(List<AllieInfo> alliesInfo, BattlefieldInfo battlefieldInfo) {
        activeTeamsController.setTeams(alliesInfo);
        contestTileController.setContestInfo(battlefieldInfo);
    }

    public void displayDynamicContestInfo(Set<AgentInfo> agentsInfo, JobProgressInfo jobStatus, List<AgentConclusion> allCandidates) {

    }

    public void insertContestToFlowPane(Node singleContestTile) {
        contestsAreaController.insertContestToFlowPane(singleContestTile);
    }

    public void clearContests() {
        contestsAreaController.clearContests();
    }

    public void setReady(int taskSize) {
        mainController.setReady(taskSize);
    }
}
