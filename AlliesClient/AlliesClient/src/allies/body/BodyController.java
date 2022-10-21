package allies.body;

import allies.app.MainController;
import allies.body.screen1.agentsinfo.AgentsInfoController;
import allies.body.screen1.alliesetting.AllieSettingController;
import allies.body.screen1.contest.area.ContestsAreaController;
import allies.body.screen2.activeteamsarea.ActiveTeamsController;
import allies.body.screen2.agentsprogress.AgentsProgressController;
import allies.body.screen2.candidate.area.CandidatesAreaController;
import allies.body.screen1.contest.tile.ContestTileController;
import info.agent.AgentInfo;
import info.allie.AllieInfo;
import info.battlefield.BattlefieldInfo;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import jobprogress.JobProgressInfo;

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

    @FXML
    private GridPane allieSettings;
    @FXML
    private AllieSettingController allieSettingsController;

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
        allieSettingsController.setParentController(this);

        //screen 2
        candidatesAreaController.setParentController(this);
        activeTeamsController.setParentController(this);
        agentProgressController.setParentController(this);
        contestTileController.setParentController(this);

        contestTileController.setVisibleSubscribeButton(false);
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
    public void bindComponents(IntegerProperty totalDistinctCandidates, BooleanProperty isSubscribedToContest, BooleanProperty isReady, BooleanProperty isContestActive) {

        // brute force dashboard labels bind
        candidatesAreaController.bindInitPropertiesToLabels(totalDistinctCandidates);

        allieSettingsController.bindComponents(isSubscribedToContest, isReady);

        contestTileController.bindLabels(isContestActive);
    }

    public void insertCandidateToFlowPane(Node singleCandidateTile) {
        candidatesAreaController.insertCandidateToFlowPane(singleCandidateTile);
    }

    public void clearOldResults() {
        candidatesAreaController.clearOldResult();
        activeTeamsController.clearOldResult();
        agentProgressController.clearOldResult();
        contestTileController.clearOldResult();
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
        agentProgressController.setTextToDecipher(battlefieldInfo.getTextToDecipher());
    }

    public void displayDynamicContestInfo(Set<AgentInfo> agentsInfo, JobProgressInfo jobStatus) {
        agentProgressController.setProgressInfo(agentsInfo, jobStatus);
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

    public void setTaskSizeSpinner(long totalPossibleWindowsPositions) {
        allieSettingsController.setTaskSizeSpinner(totalPossibleWindowsPositions);
    }

    public void clearOldCandidates() {
        candidatesAreaController.clearOldResult();
    }
}
