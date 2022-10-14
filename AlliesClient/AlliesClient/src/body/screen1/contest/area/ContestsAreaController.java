package body.screen1.contest.area;

import body.BodyController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;


public class ContestsAreaController {

    private BodyController parentController;

    @FXML
    private FlowPane contestsFlowpane;

    public void setParentController(BodyController bodyController) {
        this.parentController = bodyController;
    }

    public void insertContestToFlowPane(Node singleContestTile) {
        contestsFlowpane.getChildren().add(singleContestTile);
    }

    public void clearContests() {
        contestsFlowpane.getChildren().clear();
    }
}
