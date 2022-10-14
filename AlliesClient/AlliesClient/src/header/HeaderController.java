package header;

import app.MainController;
import body.screen1.contest.tile.ContestTileController;
import info.allie.AllieInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import winner.LoseWinAreaController;

import java.io.IOException;

public class HeaderController {

    private MainController mainController;

    @FXML
    private GridPane loseWinArea;

    @FXML
    private LoseWinAreaController loseWinAreaController;

    public void initialize() {
        loseWinAreaController.setParentController(this);
    }


    // will need to add logout button if we want to implement bonus
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void displayWinnerMessage(AllieInfo allieWinner) {
        loseWinAreaController.setWinnerTeamLabelName(allieWinner.getAllieName());
        loseWinArea.setVisible(true);
    }

    public void approveAllieFinishGameAction() {
        loseWinArea.setVisible(false);
        mainController.approveContestIsOver();
    }
}
