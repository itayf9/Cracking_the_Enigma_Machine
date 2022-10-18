package agent.screen1.contest.tile.controller;

import agent.app.MainController;

import info.battlefield.BattlefieldInfo;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ContestTileController {
    private MainController parentController;

    @FXML
    private Label battlefieldNameLabel;

    @FXML
    private Label uboatNameLabel;

    @FXML
    private Label isActiveStatusLabel;

    @FXML
    private Label difficultyLevelLabel;

    @FXML
    private Label alliesSubscribedRequiredLabel;

    public void setParentController(MainController bodyController) {
        this.parentController = bodyController;
    }

    public void setContestInfo(BattlefieldInfo battlefieldInfo) {
        this.battlefieldNameLabel.setText(battlefieldInfo.getBattleName());
        this.uboatNameLabel.setText(battlefieldInfo.getUboatName());
        this.difficultyLevelLabel.setText(battlefieldInfo.getDifficultyLevel().name());
        this.alliesSubscribedRequiredLabel.setText(battlefieldInfo.getNumOfLoggedAllies() + " / " + battlefieldInfo.getNumOfRequiredAllies());
    }

    public void clearOldResult() {
        battlefieldNameLabel.setText("-");
        uboatNameLabel.setText("-");
        difficultyLevelLabel.setText("-");
        alliesSubscribedRequiredLabel.setText("- / -");
    }

    public void bindIsActiveLabel(BooleanProperty isActive) {
        isActiveStatusLabel.textProperty().bind(Bindings.when(isActive.not()).then("Idle").otherwise("Active"));
    }
}
