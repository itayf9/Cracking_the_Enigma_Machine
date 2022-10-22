package uboat.winner;

import javafx.scene.input.MouseEvent;
import uboat.body.BodyController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LoseWinAreaController {

    private BodyController parentController;
    @FXML
    private Label winnerTeamLabel;

    @FXML
    void approveUboatFinishGameAction(MouseEvent event) {
        parentController.approveUboatFinishGame();
    }


    public void setParentController(BodyController bodyController) {
        this.parentController = bodyController;
    }

    public void setWinnerLabel(String allieWinnerName) {
        winnerTeamLabel.setText(allieWinnerName);
    }
}
