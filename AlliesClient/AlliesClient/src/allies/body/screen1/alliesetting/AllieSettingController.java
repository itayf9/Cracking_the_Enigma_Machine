package allies.body.screen1.alliesetting;

import allies.body.BodyController;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;

public class AllieSettingController {

    private BodyController parentController;

    @FXML
    private Spinner<Integer> taskSizeSpinner;

    @FXML
    private Button isReadyButton;

    @FXML
    private Spinner<Integer> numberOfAgentsSpinner;

    @FXML
    public void initialize() {
        numberOfAgentsSpinner.valueFactoryProperty().set(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE));


        numberOfAgentsSpinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int newValueAsInt = Integer.parseInt(newValue);
                if (newValueAsInt > 0) {
                    numberOfAgentsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, newValueAsInt));
                } else {
                    numberOfAgentsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, Integer.parseInt(oldValue)));
                    numberOfAgentsSpinner.getEditor().setText(oldValue);
                }
            } catch (NumberFormatException e) {
                numberOfAgentsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, Integer.parseInt(oldValue)));
                numberOfAgentsSpinner.getEditor().setText(oldValue);
            }
        });
    }

    @FXML
    void setReady(MouseEvent ignored) {
        parentController.setReady(taskSizeSpinner.getValue(), numberOfAgentsSpinner.getValue());
    }

    public void setParentController(BodyController bodyController) {
        this.parentController = bodyController;
    }

    public void setTaskSizeSpinner(long totalPossibleWindowsPositions) {
        taskSizeSpinner.valueFactoryProperty().set(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, ((int) totalPossibleWindowsPositions)));


        taskSizeSpinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int newValueAsInt = Integer.parseInt(newValue);
                if (newValueAsInt > 0 && newValueAsInt <= totalPossibleWindowsPositions) {
                    taskSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, (int) totalPossibleWindowsPositions, newValueAsInt));
                } else {
                    taskSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, (int) totalPossibleWindowsPositions, Integer.parseInt(oldValue)));
                    taskSizeSpinner.getEditor().setText(oldValue);
                }
            } catch (NumberFormatException e) {
                taskSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, (int) totalPossibleWindowsPositions, Integer.parseInt(oldValue)));
                taskSizeSpinner.getEditor().setText(oldValue);
            }
        });
    }

    public void bindComponents(BooleanProperty isSubscribedToContest, BooleanProperty isReady) {
        isReadyButton.disableProperty().bind(isSubscribedToContest.not().or(isReady));
        taskSizeSpinner.disableProperty().bind(isSubscribedToContest.not().or(isReady));
        numberOfAgentsSpinner.disableProperty().bind(isSubscribedToContest.not().or(isReady));

    }
}
