package body.screen3.candidate.area;

import body.BodyController;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;

public class CandidatesAreaController {

    private BodyController parentController;

    @FXML
    private FlowPane candidatesFlowPane;

    @FXML
    private ProgressBar BFprogressBar;

    @FXML
    private Label progressPercentageLabel;

    @FXML
    private Label numberOfScannedConfigurationsLabel;


    @FXML
    private Label numberOfDistinctCandidatesLabel;

    @FXML
    private Label BFstatusLabel;

    @FXML
    private Label averageProcessTimeLabel;

    public void setParentController(BodyController parentController) {
        this.parentController = parentController;
    }
    
    public void bindInitPropertiesToLabels(IntegerProperty totalDistinctCandidates, IntegerProperty totalProcessedConfigurations,
                                           LongProperty totalAmountConfigurations, DoubleProperty bruteForceProgress,
                                           StringProperty bruteForceProgressBarPercentageLabel, StringProperty bruteForceStatus,
                                           DoubleProperty averageTasksProcessTimeProperty) {

        numberOfDistinctCandidatesLabel.textProperty().bind(totalDistinctCandidates.asString());
        numberOfScannedConfigurationsLabel.textProperty().bind(Bindings.concat(totalProcessedConfigurations.asString(), " / ", totalAmountConfigurations.asString()));
        progressPercentageLabel.textProperty().bind(bruteForceProgressBarPercentageLabel);
        BFprogressBar.progressProperty().bind(bruteForceProgress);
        BFstatusLabel.textProperty().bind(bruteForceStatus);
        averageProcessTimeLabel.textProperty().bind(Bindings.concat(Bindings.format("%.3f", averageTasksProcessTimeProperty), " ms"));
    }

    public void insertCandidateToFlowPane(Node singleCandidateTile) {
        candidatesFlowPane.getChildren().add(singleCandidateTile);
    }

    public void clearOldResultsOfBruteForce() {
        candidatesFlowPane.getChildren().clear();
    }
}
