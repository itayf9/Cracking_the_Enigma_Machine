package body.screen1.alliesetting;

import body.BodyController;

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
    private void initialize() {
//        taskSizeSpinner.valueFactoryProperty().set(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, (totalPossibleWindowsPositions.getValue().intValue())));
//
//
//        taskSizeSpinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
//            try {
//                int newValueAsInt = Integer.parseInt(newValue);
//                if (newValueAsInt > 0 && newValueAsInt <= totalPossibleWindowsPositions.getValue()) {
//                    taskSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, totalPossibleWindowsPositions.getValue().intValue(), newValueAsInt));
//                } else {
//                    taskSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, totalPossibleWindowsPositions.getValue().intValue(), Integer.parseInt(oldValue)));
//                    taskSizeSpinner.getEditor().setText(oldValue);
//                }
//            } catch (NumberFormatException e) {
//                taskSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, totalPossibleWindowsPositions.getValue().intValue(), Integer.parseInt(oldValue)));
//                taskSizeSpinner.getEditor().setText(oldValue);
//            }
//        });
    }

    @FXML
    void setReady(MouseEvent event) {
        parentController.setReady(taskSizeSpinner.getValue());
    }

    public void setParentController(BodyController bodyController) {
        this.parentController = bodyController;
    }

}
