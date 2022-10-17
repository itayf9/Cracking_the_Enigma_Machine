package allies.body.screen1.agentsinfo;

import allies.body.BodyController;
import info.agent.AgentInfo;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Set;

public class AgentsInfoController {

    private BodyController parentController;

    @FXML
    private TableView<AgentInfo> agentsTable;

    @FXML
    private TableColumn<AgentInfo, String> nameColumn;

    @FXML
    private TableColumn<AgentInfo, String> numberOfThreadsColumn;

    @FXML
    private TableColumn<AgentInfo, String> numberOfTasksPerPullColumn;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("agentName"));
        nameColumn.setSortable(false);
        numberOfThreadsColumn.setCellValueFactory(new PropertyValueFactory<>("numOfThreads"));
        numberOfThreadsColumn.setSortable(false);
        numberOfTasksPerPullColumn.setCellValueFactory(new PropertyValueFactory<>("numOfTasksPerPull"));
        numberOfTasksPerPullColumn.setSortable(false);
    }

    public void setParentController(BodyController bodyController) {
        this.parentController = bodyController;
    }

    public void setAgents(Set<AgentInfo> loggedAgents) {
        agentsTable.getItems().clear();
        for (AgentInfo agentInfo : loggedAgents) {
            agentsTable.getItems().add(agentInfo);
        }
    }
}
