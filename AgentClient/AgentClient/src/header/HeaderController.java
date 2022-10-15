package header;

import app.MainController;
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

    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
