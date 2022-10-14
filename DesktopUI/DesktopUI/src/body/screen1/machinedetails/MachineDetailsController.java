//package body.screen1.machinedetails;
//
//import body.BodyController;
//import body.currentconfig.CurrentConfigController;
//import dto.DTOspecs;
//
//import header.Skin;
//import javafx.beans.property.IntegerProperty;
//import javafx.fxml.FXML;
//import javafx.scene.control.Label;
//import javafx.scene.layout.GridPane;
//import problem.Problem;
//
//import java.net.URL;
//import java.util.List;
//
//import static utill.Utillity.getOriginalConfigFromSpecs;
//
//
//public class MachineDetailsController {
//
//    BodyController parentController;
//
//    @FXML
//    private Label usedRotors;
//
//    @FXML
//    private Label availableRotors;
//
//    @FXML
//    private Label availableReflectors;
//
//    @FXML
//    private Label cipheredTextCount;
//
//    @FXML
//    private GridPane originalConfig;
//
//    @FXML
//    private CurrentConfigController originalConfigController;
//
//    @FXML
//    public void initialize() {
//        originalConfigController.setTitle("Original Machine Configuration");
//    }
//
//    /**
//     * displays the machine specs
//     *
//     * @param specsStatus machine specs
//     */
//    public void displayMachineDetails(DTOspecs specsStatus) {
//        if (specsStatus.getDetails().equals(Problem.NO_LOADED_MACHINE)) {
//            usedRotors.setText("-");
//            availableRotors.setText("-");
//            availableReflectors.setText("-");
//        } else {
//            usedRotors.setText("" + specsStatus.getInUseRotorsCount());
//            availableRotors.setText("" + specsStatus.getAvailableRotorsCount());
//            availableReflectors.setText("" + specsStatus.getAvailableReflectorsCount());
//        }
//
//         originalConfigController.clearOldOriginalConfig();
//    }
//
//    public void setParentController(BodyController parentController) {
//        this.parentController = parentController;
//    }
//
//    public void displayOriginalConfiguration(List<Integer> rotorsIDs, String currentWindowsCharacters, String inUseReflectorSymbol, String inUsePlugs, List<Integer> currentNotchDistances) {
//        originalConfigController.displayOriginalConfiguration(rotorsIDs, currentWindowsCharacters, inUseReflectorSymbol, inUsePlugs, currentNotchDistances);
//    }
//
//    public void bindCipherCounterProperty(IntegerProperty cipherCounterProperty) {
//        cipheredTextCount.textProperty().bind(cipherCounterProperty.asString());
//    }
//
//    public void setComponentsSkin(URL currentConfigUrl) {
//        originalConfig.getStylesheets().removeAll(originalConfig.getStylesheets());
//        originalConfig.getStylesheets().add(currentConfigUrl.toString());
//    }
//}
