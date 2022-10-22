package uboat.winner;

import javafx.scene.input.MouseEvent;
import uboat.body.BodyController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import uboat.header.HeaderController;

public class LoseWinAreaController {

    private HeaderController parentController;
    @FXML
    private Label winnerTeamLabel;

    @FXML
    void approveUboatFinishGameAction(MouseEvent event) {
        parentController.approveUboatFinishGame();
    }


    public void setParentController(HeaderController headerController) {
        this.parentController = headerController;
    }

    public void setWinnerLabel(String allieWinnerName) {
        winnerTeamLabel.setText(allieWinnerName);
    }
}
