package header;

import app.MainController;
import app.MessageTone;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import problem.Problem;
import sun.applet.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class HeaderController {

    private FileChooser fileChooser = new FileChooser();

    private String selectedMachineFile;

    private BooleanProperty isMachineLoadedProperty;

    private MainController mainController;

    @FXML
    private Label filePathLoadMachineLabel;

    @FXML
    private Button loadFileButton;

    @FXML
    public void initialize() {

        filePathLoadMachineLabel.setText("");
//        // setting the skin selection
//        skinsGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
//            RadioMenuItem radioMenuItem = (RadioMenuItem) newValue;
//            Skin skin = Skin.DEFAULT;
//
//            switch (radioMenuItem.getId()) {
//                case "skinDefaultButton":
//                    skin = Skin.DEFAULT;
//                    break;
//                case "skinDarkButton":
//                    skin = Skin.DARK;
//                    break;
//                case "skinSpecialButton":
//                    skin = Skin.SPECIAL;
//            }
//
//            mainController.setAppSkin(skin);
//        });

    }

    @FXML
    private void loadMachineFile(MouseEvent event) {
        fileChooser.setTitle("Load Machine");
        fileChooser.getExtensionFilters().add((new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml")));
        File chosenFile = fileChooser.showOpenDialog(new Stage());

        if (chosenFile != null) {
            this.selectedMachineFile = chosenFile.getAbsolutePath();
            mainController.loadMachineFromFile(selectedMachineFile);
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    public void displayFilePath() {
        filePathLoadMachineLabel.setText(selectedMachineFile);
    }

    public void setProperties(BooleanProperty isMachineLoadedProperty) {
        this.isMachineLoadedProperty = isMachineLoadedProperty;
    }
}
