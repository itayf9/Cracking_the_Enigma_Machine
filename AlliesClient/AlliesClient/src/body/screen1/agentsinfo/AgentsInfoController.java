package body.screen1.agentsinfo;

import body.BodyController;
import info.agent.AgentInfo;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AgentsInfoController {

    private BodyController parentController;

    @FXML
    private TableView<AgentInfo> agentsTable;

    @FXML
    private TableColumn<AgentInfo, String> nameColumn;

    @FXML
    private TableColumn<AgentInfo, String> numberOfThreadsColumn;

    @FXML
    private TableColumn<AgentInfo, String> NumberOfTasksPerPullColumn;

    public void setParentController(BodyController bodyController) {
        this.parentController = bodyController;
    }
}
