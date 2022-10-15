package body.screen1.contest.tile.controller;

import app.MainController;

import info.battlefield.BattlefieldInfo;
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


    public void setBattlefieldName(String battlefieldName) {
        this.battlefieldNameLabel.setText(battlefieldName);
    }

    public void setUboatName(String uboatName) {
        this.uboatNameLabel.setText(uboatName);
    }

    public void setIsActiveStatus(String isActiveStatus) {
        this.isActiveStatusLabel.setText(isActiveStatus);
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevelLabel.setText(difficultyLevel);
    }

    public void setAlliesSubscribedRequired(String alliesSubscribed, String alliesRequired) {
        this.alliesSubscribedRequiredLabel.setText(alliesSubscribed + " / " + alliesRequired);
    }

    public void setParentController(MainController bodyController) {
        this.parentController = bodyController;
    }

    public void setContestInfo(BattlefieldInfo battlefieldInfo) {
        this.battlefieldNameLabel.setText(battlefieldInfo.getBattleName());
        this.uboatNameLabel.setText(battlefieldInfo.getUboatName());
        String isActiveStr = "Idle";
        if (battlefieldInfo.isActive()) {
            isActiveStr = "Active";
        }
        this.isActiveStatusLabel.setText(isActiveStr);
        this.difficultyLevelLabel.setText(battlefieldInfo.getDifficultyLevel().name());
        this.alliesSubscribedRequiredLabel.setText(battlefieldInfo.getNumOfLoggedAllies() + " / " + battlefieldInfo.getNumOfRequiredAllies());
    }

    public void clearOldResult() {
        battlefieldNameLabel.setText("-");
        uboatNameLabel.setText("-");
        isActiveStatusLabel.setText("-");
        difficultyLevelLabel.setText("-");
        alliesSubscribedRequiredLabel.setText("- / -");
    }
}
