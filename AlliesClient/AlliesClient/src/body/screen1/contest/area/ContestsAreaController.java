package body.screen1.contest.area;

import app.MainController;
import body.BodyController;
import info.allie.AllieInfo;
import info.battlefield.BattlefieldInfo;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

import java.util.List;


public class ContestsAreaController {

    private BodyController parentController;

    @FXML
    private FlowPane contestsFlowpane;

    public void setParentController(BodyController bodyController) {
        this.parentController = bodyController;
    }

    public void displayStaticContestInfo(List<AllieInfo> alliesInfo, BattlefieldInfo battlefieldInfo) {
    }

    public void insertContestToFlowPane(Node singleContestTile) {
        contestsFlowpane.getChildren().add(singleContestTile);
    }

    public void clearContests() {
        contestsFlowpane.getChildren().clear();
    }
}
