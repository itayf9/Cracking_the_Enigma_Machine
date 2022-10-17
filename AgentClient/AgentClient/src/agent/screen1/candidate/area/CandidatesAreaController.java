package agent.screen1.candidate.area;

import agent.app.MainController;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class CandidatesAreaController {

    private MainController parentController;

    @FXML
    private FlowPane candidatesFlowPane;

    @FXML
    private Label numberOfDistinctCandidatesLabel;

    public void setParentController(MainController parentController) {
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
