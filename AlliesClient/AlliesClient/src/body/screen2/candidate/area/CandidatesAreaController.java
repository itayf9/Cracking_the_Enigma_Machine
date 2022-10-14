package body.screen2.candidate.area;

import body.BodyController;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class CandidatesAreaController {

    private BodyController parentController;

    @FXML
    private FlowPane candidatesFlowPane;

    @FXML
    private Label numberOfDistinctCandidatesLabel;

    public void setParentController(BodyController parentController) {
        this.parentController = parentController;
    }

    public void bindInitPropertiesToLabels(IntegerProperty totalDistinctCandidates) {
        numberOfDistinctCandidatesLabel.textProperty().bind(totalDistinctCandidates.asString());
    }

    public void insertCandidateToFlowPane(Node singleCandidateTile) {
        candidatesFlowPane.getChildren().add(singleCandidateTile);
    }

    public void clearOldResult() {
        candidatesFlowPane.getChildren().clear();
    }
}
