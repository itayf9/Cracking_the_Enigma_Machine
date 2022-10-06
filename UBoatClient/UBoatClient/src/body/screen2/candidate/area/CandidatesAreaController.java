package body.screen2.candidate.area;

import body.BodyController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
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

    @FXML
    private Label totalTimeDecryptLabel;

    @FXML
    private Label totalTimeDecryptSubTitleLabel;

    public void setParentController(BodyController parentController) {
        this.parentController = parentController;
    }

    public void bindInitPropertiesToLabels(BooleanProperty isBruteForceTaskActive, IntegerProperty totalDistinctCandidates, IntegerProperty totalProcessedConfigurations,
                                           LongProperty totalAmountConfigurations, DoubleProperty bruteForceProgress,
                                           StringProperty bruteForceProgressBarPercentageLabel, StringProperty bruteForceStatus,
                                           DoubleProperty averageTasksProcessTimeProperty, LongProperty totalTimeDecryptProperty) {

        numberOfDistinctCandidatesLabel.textProperty().bind(totalDistinctCandidates.asString());
        numberOfScannedConfigurationsLabel.textProperty().bind(Bindings.concat(totalProcessedConfigurations.asString(), " / ", totalAmountConfigurations.asString()));
        progressPercentageLabel.textProperty().bind(bruteForceProgressBarPercentageLabel);
        BFprogressBar.progressProperty().bind(bruteForceProgress);
        BFstatusLabel.textProperty().bind(bruteForceStatus);
        averageProcessTimeLabel.textProperty().bind(Bindings.concat(Bindings.format("%.3f", averageTasksProcessTimeProperty), " ms"));
        totalTimeDecryptLabel.visibleProperty().bind(Bindings.when(isBruteForceTaskActive.not()).then(true).otherwise(false));
        totalTimeDecryptSubTitleLabel.visibleProperty().bind(Bindings.when(isBruteForceTaskActive.not()).then(true).otherwise(false));
        totalTimeDecryptLabel.textProperty().bind(Bindings.concat(totalTimeDecryptProperty, " ms"));
    }

    public void insertCandidateToFlowPane(Node singleCandidateTile) {
        candidatesFlowPane.getChildren().add(singleCandidateTile);
    }

    public void clearOldResultsOfBruteForce() {
        candidatesFlowPane.getChildren().clear();
    }
}
