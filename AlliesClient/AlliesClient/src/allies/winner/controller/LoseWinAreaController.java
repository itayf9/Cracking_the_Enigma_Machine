package allies.winner.controller;

import allies.header.HeaderController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class LoseWinAreaController {

    private HeaderController parentController;
    @FXML
    private Label winnerTeamLabel;

    @FXML
    void approveAllieFinishGameAction(MouseEvent event) {
        parentController.approveAllieFinishGameAction();
    }

    public void setParentController(HeaderController headerController) {
        this.parentController = headerController;
    }

    public void setWinnerTeamLabelName(String allieName) {
        winnerTeamLabel.setText(allieName);
    }
}
