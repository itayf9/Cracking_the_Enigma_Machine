package body.screen2.activeteamsarea;

import body.BodyController;
import info.allie.AllieInfo;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class ActiveTeamsController {

    private BodyController parentController;

    @FXML
    private TableView<AllieInfo> teamsTable;

    public void addTeam(AllieInfo allieInfo) {

    }

    public void setParentController(BodyController parentController) {
        this.parentController = parentController;
    }
}
