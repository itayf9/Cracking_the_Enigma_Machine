package body.screen1.contest;

import app.MainController;
import body.screen1.contest.tile.ContestTileController;
import info.battlefield.BattlefieldInfo;
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
}
