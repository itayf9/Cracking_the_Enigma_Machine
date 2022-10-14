//package header;
//
//import app.MainController;
//import app.statusbar.MessageTone;
//import body.screen3.candidate.tile.CandidateTileController;
//import javafx.animation.*;
//import javafx.beans.binding.Bindings;
//import javafx.beans.property.BooleanProperty;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.GridPane;
//import javafx.stage.FileChooser;
//import javafx.stage.Modality;
//import javafx.stage.Stage;
//import javafx.util.Duration;
//import problem.Problem;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.URL;
//
//public class HeaderController {
//
//    private FileChooser fileChooser = new FileChooser();
//
//    private String selectedMachineFile;
//
//    private FadeTransition loadButtonFadeTransition;
//
//    private BooleanProperty isAnimationProperty;
//
//    private BooleanProperty isMachineLoadedProperty;
//
//    private MainController mainController;
//
//
//    @FXML
//    private RadioMenuItem skinDefaultButton;
//
//    @FXML
//    private ToggleGroup skinsGroup;
//
//    @FXML
//    private RadioMenuItem skinDarkButton;
//
//    @FXML
//    private RadioMenuItem skinSpecialButton;
//
//    @FXML
//    private MenuButton settingsMenuButton;
//
//    @FXML
//    private MenuItem animationMenu;
//
//    @FXML
//    private MenuItem aboutMenu;
//
//    @FXML
//    private ImageView settingsButtonImg;
//
//    @FXML
//    private Label filePathLoadMachineLabel;
//
//    @FXML
//    private Button loadFileButton;
//
//
//    @FXML
//    public void initialize() {
//
//        filePathLoadMachineLabel.setText("");
//        loadButtonFadeTransition = new FadeTransition(Duration.millis(1500), loadFileButton);
//        loadButtonFadeTransition.setFromValue(1.0);
//        loadButtonFadeTransition.setToValue(0.3);
//        loadButtonFadeTransition.setCycleCount(Animation.INDEFINITE);
//        loadButtonFadeTransition.setAutoReverse(true);
//
//        enableLoadButtonTransition(true);
//
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
//
//
//    }
//
//
//    @FXML
//    private void loadMachineFile(MouseEvent event) {
//        fileChooser.setTitle("Load Machine");
//        fileChooser.getExtensionFilters().add((new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml")));
//        File chosenFile = fileChooser.showOpenDialog(new Stage());
//
//        if (chosenFile != null) {
//            this.selectedMachineFile = chosenFile.getAbsolutePath();
//            mainController.loadMachineFromFile(selectedMachineFile);
//        }
//
//    }
//
//    public void setMainController(MainController mainController) {
//        this.mainController = mainController;
//    }
//
//    public void displayHeaderProblem(Problem details) {
//
//    }
//
//    public void displayFilePath() {
//        filePathLoadMachineLabel.setText(selectedMachineFile);
//    }
//
//    @FXML
//    void toggleAnimation(ActionEvent event) {
//        isAnimationProperty.setValue(!isAnimationProperty.getValue());
//    }
//
//    @FXML
//    void toogleConfigAppearance(ActionEvent event) {
//
//    }
//
//    @FXML
//    void showAboutScene(ActionEvent event) {
//        Stage stage = new Stage();
//        stage.initModality(Modality.APPLICATION_MODAL);
//        stage.setResizable(false);
//        stage.setHeight(400);
//        stage.setWidth(400);
//
//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("/header/about/credits.fxml"));
//        try {
//            Node creditsWindow = loader.load();
//
//            //CandidateTileController candidateTileController = loader.getController();
//
//            Scene s = new Scene((Parent) creditsWindow);
//            stage.setScene(s);
//            stage.show();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//
//    public void enableLoadButtonTransition(boolean isAllow) {
//        if (isAllow) {
//            loadButtonFadeTransition.play();
//        } else {
//            loadButtonFadeTransition.stop();
//            FadeTransition recover = new FadeTransition(Duration.millis(500), loadFileButton);
//            recover.setToValue(1.0);
//            recover.play();
//        }
//    }
//
//    public void setImages(Skin skin) {
//
//        URL settingsImgUrl = getClass().getResource("/header/settings_gear_" + skin.skinName() + ".png");
//
//        settingsButtonImg.setImage(new Image(settingsImgUrl.toString()));
//    }
//
//    public void bindSettings(BooleanProperty isAnimationProperty) {
//        animationMenu.textProperty().bind(Bindings.when(isAnimationProperty).then("Disable Animations").otherwise("Enable Animations"));
//    }
//
//    public void setProperties(BooleanProperty isAnimationProperty, BooleanProperty isMachineLoadedProperty) {
//        this.isMachineLoadedProperty = isMachineLoadedProperty;
//        this.isAnimationProperty = isAnimationProperty;
//        isAnimationProperty.addListener((observable, oldValue, newValue) -> {
//            if (!isMachineLoadedProperty.getValue()) {
//                enableLoadButtonTransition(newValue);
//            }
//
//        });
//
//        isAnimationProperty.addListener((observable, oldValue, newValue) -> {
//            if (!newValue) {
//                mainController.setStatusMessage("Animations: OFF", MessageTone.INFO);
//            } else {
//                mainController.setStatusMessage("Animations: ON", MessageTone.INFO);
//            }
//        });
//    }
//}
