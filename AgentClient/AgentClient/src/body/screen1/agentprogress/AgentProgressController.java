package body.screen1.agentprogress;

import app.MainController;
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
}
