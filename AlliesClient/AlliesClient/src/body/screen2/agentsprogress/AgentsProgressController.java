package body.screen2.agentsprogress;

import body.BodyController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AgentsProgressController {
    private BodyController parentController;

    @FXML
    private TableView<?> agentsTable;

    @FXML
    private TableColumn<?, ?> nameColumn;

    @FXML
    private TableColumn<?, ?> receivedTasksColumn;

    @FXML
    private TableColumn<?, ?> producedCandidatesColumn;

    @FXML
    private Label textToDecipherLabel;

    @FXML
    private Label totalTasksLabel;

    @FXML
    private Label FinishedTasksLabel;

    @FXML
    private Label producedTasksLabel;

    public void setParentController(BodyController bodyController) {
        this.parentController = bodyController;
    }
}
