package allies.body.screen1.contest.tile;

import allies.body.BodyController;
import info.battlefield.BattlefieldInfo;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ContestTileController {
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
    private Label alliesSubscribedRequiredLabel;

    @FXML
    private Button subscribeButton;

    @FXML
    void subscribeToBattlefield(MouseEvent event) {
        parentController.subscribeToBattlefieldAction(uboatNameLabel.getText());
    }

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

    public void bindSubscriptionButton(BooleanProperty isSubscribedToContest) {
        subscribeButton.disableProperty().bind(isSubscribedToContest);
    }

    public void setParentController(BodyController bodyController) {
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

    public void setVisibleSubscribeButton(boolean isVisible) {
        subscribeButton.setVisible(isVisible);
    }
}
