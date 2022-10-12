package body.screen2.activeteamsarea;

import body.BodyController;
import info.allie.AllieInfo;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ActiveTeamsController {

    private BodyController parentController;

    @FXML
    private TableView<AllieInfo> teamsTable;

    @FXML
    private TableColumn<AllieInfo, String> nameColumn;

    @FXML
    private TableColumn<AllieInfo, String> numberOfAgentsColumn;

    @FXML
    private TableColumn<AllieInfo, String> taskSizeColumn;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("allieName"));
        nameColumn.setSortable(false);
        numberOfAgentsColumn.setCellValueFactory(new PropertyValueFactory<>("numOfAgents"));
        numberOfAgentsColumn.setSortable(false);
        taskSizeColumn.setCellValueFactory(new PropertyValueFactory<>("taskSize"));
        taskSizeColumn.setSortable(false);

    }

    public void setParentController(BodyController parentController) {
        this.parentController = parentController;
    }

    public void setTeams(List<AllieInfo> alliesInfoList) {
        teamsTable.getItems().clear();
        for (AllieInfo allieInfo : alliesInfoList) {
            teamsTable.getItems().add(allieInfo);
        }
    }
}
