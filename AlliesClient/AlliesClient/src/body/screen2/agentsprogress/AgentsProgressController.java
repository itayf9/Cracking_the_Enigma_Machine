package body.screen2.agentsprogress;

import body.BodyController;
import info.agent.AgentInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import jobprogress.JobProgressInfo;

import java.util.Set;

public class AgentsProgressController {
    private BodyController parentController;

    @FXML
    private TableView<AgentInfo> agentsTable;

    @FXML
    private TableColumn<AgentInfo, String> nameColumn;

    @FXML
    private TableColumn<AgentInfo, String> receivedTasksColumn;

    @FXML
    private TableColumn<AgentInfo, String> producedCandidatesColumn;

    @FXML
    private Label textToDecipherLabel;

    @FXML
    private Label totalTasksLabel;

    @FXML
    private Label finishedTasksLabel;

    @FXML
    private Label producedTasksLabel;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("agentName"));
        nameColumn.setSortable(false);
        receivedTasksColumn.setCellValueFactory(new PropertyValueFactory<>("numOfReceivedTasks"));
        receivedTasksColumn.setSortable(false);
        producedCandidatesColumn.setCellValueFactory(new PropertyValueFactory<>("numOfFoundCandidates"));
        producedCandidatesColumn.setSortable(false);
    }

    public void setParentController(BodyController bodyController) {
        this.parentController = bodyController;
    }

    public void setTextToDecipher(String textToDecipher) {
        this.textToDecipherLabel.setText(textToDecipher);
    }

    public void setProgressInfo(Set<AgentInfo> agentsInfo, JobProgressInfo jobProgressInfo) {

        for (AgentInfo agentInfo : agentsInfo) {
            agentsTable.getItems().add(agentInfo);
        }
        this.totalTasksLabel.setText(String.valueOf(jobProgressInfo.getTotalAmountOfTasks()));
        this.producedTasksLabel.setText(String.valueOf(jobProgressInfo.getNumberOfTasksProduced()));
        this.finishedTasksLabel.setText(String.valueOf(jobProgressInfo.getNumberOfTasksDone()));
    }

    public void clearOldResult() {
        agentsTable.getItems().clear();
        textToDecipherLabel.setText("-");
        totalTasksLabel.setText("-");
        finishedTasksLabel.setText("-");
        producedTasksLabel.setText("-");
    }
}
