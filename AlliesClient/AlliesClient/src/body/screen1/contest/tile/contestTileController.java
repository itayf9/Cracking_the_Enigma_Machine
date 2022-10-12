package body.screen1.contest.tile;

import body.BodyController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class contestTileController {
    private BodyController parentController;

    @FXML
    private Label battlefieldNameLabel;

    @FXML
    private Label uboatNameLabel;

    @FXML
    private Label isActiveStatusLabel;

    @FXML
    private Label difficultyLevelLabel;

    @FXML
    private Label subscribedAlliesLabel;

    @FXML
    private Label requiredAlliesLabel;


    public void setParentController(BodyController bodyController) {
        this.parentController = bodyController;
    }
}
