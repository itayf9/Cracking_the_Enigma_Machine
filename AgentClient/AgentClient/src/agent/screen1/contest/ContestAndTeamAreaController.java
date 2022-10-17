package agent.screen1.contest;

import agent.app.MainController;
import agent.screen1.contest.tile.controller.ContestTileController;
import info.battlefield.BattlefieldInfo;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class ContestAndTeamAreaController {

    MainController parentController;

    @FXML
    private Label teamNameLabel;

    @FXML
    private GridPane contestInfo;

    @FXML
    private ContestTileController contestInfoController;

    public void setParentController(MainController mainController) {
        this.parentController = mainController;
    }

    public void displayStaticContestInfo(BattlefieldInfo battlefieldInfo, String allieName) {
        contestInfoController.setContestInfo(battlefieldInfo);

    }

    public void clearOldResult() {
        contestInfoController.clearOldResult();
    }

    public void bindComponents(StringProperty allieName) {
        teamNameLabel.textProperty().bind(allieName);
    }

    public void bindIsActiveLabel(BooleanProperty isContestActive) {
        contestInfoController.bindIsActiveLabel(isContestActive);
    }
}
