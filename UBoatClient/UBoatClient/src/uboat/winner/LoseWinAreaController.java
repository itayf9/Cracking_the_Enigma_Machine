package uboat.winner;

import uboat.body.BodyController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LoseWinAreaController {

    private BodyController parentController;
    @FXML
    private Label winnerTeamLabel;

    public void setParentController(BodyController bodyController) {
        this.parentController = bodyController;
    }

}
