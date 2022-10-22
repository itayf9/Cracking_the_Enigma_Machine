package allies.header;

import allies.app.MainController;
import allies.winner.controller.LoseWinAreaController;
import info.allie.AllieInfo;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class HeaderController {

    private MainController mainController;

    @FXML
    private GridPane loseWinArea;

    @FXML
    private LoseWinAreaController loseWinAreaController;

    @FXML
    private Label usernameLabel;

    @FXML
    private Button logoutButton;

    @FXML
    public void initialize() {
        loseWinAreaController.setParentController(this);
        loseWinArea.setVisible(false);
    }

    @FXML
    void logoutAction(MouseEvent event) {
        mainController.logoutAllie(event);
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

    public void bindComponents(StringProperty usernameProperty) {
        usernameLabel.textProperty().bind(usernameProperty);
    }

    public void clearOldResults() {
        loseWinArea.setVisible(false);
    }
}
