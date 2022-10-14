package body.screen1.agentprogress;

import app.MainController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AgentProgressController {
    MainController parentController;
    @FXML
    private Label tasksInQueueLabel;

    @FXML
    private Label totalPulledTasksLabel;

    @FXML
    private Label totalCompletedTasksLabel;

    public void setParentController(MainController mainController) {
        this.parentController = mainController;
    }

    public void bindComponents(IntegerProperty numOfTasksInQueue, IntegerProperty numOfTotalPulledTasks, IntegerProperty numOfTotalCompletedTasks) {
        tasksInQueueLabel.textProperty().bind(Bindings.format("%d", numOfTasksInQueue));
        totalPulledTasksLabel.textProperty().bind(Bindings.format("%d", numOfTotalPulledTasks));
        totalCompletedTasksLabel.textProperty().bind(Bindings.format("%d", numOfTotalCompletedTasks));
    }
}
