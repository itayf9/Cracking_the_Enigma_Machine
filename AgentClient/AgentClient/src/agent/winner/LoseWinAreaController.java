package agent.winner;

import agent.header.HeaderController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LoseWinAreaController {

    private HeaderController parentController;
    @FXML
    private Label winnerTeamLabel;


    public void setParentController(HeaderController headerController) {
        this.parentController = headerController;
    }

    public void setWinnerTeamLabelName(String allieName) {
        winnerTeamLabel.setText(allieName);
    }
}
