package header;

import app.MainController;
import info.allie.AllieInfo;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import winner.LoseWinAreaController;

public class HeaderController {


    private MainController mainController;

    @FXML
    private GridPane loseWinArea;

    @FXML
    private LoseWinAreaController loseWinAreaController;


    @FXML
    public void initialize() {
        loseWinAreaController.setParentController(this);
        loseWinArea.setVisible(false);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void displayWinnerMessage(AllieInfo allieWinner) {
        loseWinAreaController.setWinnerTeamLabelName(allieWinner.getAllieName());
        loseWinArea.setVisible(true);
    }

    public void approveAllieFinishGameAction() {
        loseWinArea.setVisible(false);
    }
}
