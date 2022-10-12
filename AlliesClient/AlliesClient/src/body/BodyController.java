package body;

import app.MainController;
import app.MessageTone;
import body.screen1.agentsinfo.AgentsInfoController;
import body.screen1.contest.area.contestsAreaController;
import body.screen2.activeteamsarea.ActiveTeamsController;
import body.screen2.agentsprogress.AgentsProgressController;
import body.screen2.candidate.area.CandidatesAreaController;
import body.screen1.contest.tile.contestTileController;
import info.allie.AllieInfo;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import problem.Problem;

import java.util.List;

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
    private GridPane allContestsInfo;
    @FXML
    private contestsAreaController allContestsInfoController;

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
    private GridPane myContestInfo;
    @FXML
    private contestTileController myContestInfoController;


    /**
     * set up the application, connecting the controllers to their main controller
     */
    @FXML
    public void initialize() {

        //screen 1
        agentsInfoController.setParentController(this);
        allContestsInfoController.setParentController(this);

        //screen 2
        candidatesAreaController.setParentController(this);
        activeTeamsController.setParentController(this);
        agentProgressController.setParentController(this);
        myContestInfoController.setParentController(this);

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
    public void bindComponents(IntegerProperty totalDistinctCandidates) {

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
}
