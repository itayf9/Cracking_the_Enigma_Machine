//package body.currentconfig;
//
//import bindings.CurrWinCharsAndNotchPosBinding;
//import bindings.InUsePlugsBinding;
//import bindings.InUseReflectorSymbolBinding;
//import bindings.InUseRotorsIDsBinding;
//import body.BodyController;
//import dto.DTOsecretConfig;
//import javafx.beans.binding.Bindings;
//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.ListProperty;
//import javafx.beans.property.StringProperty;
//import javafx.fxml.FXML;
//
//import javafx.scene.control.Label;
//import javafx.scene.layout.FlowPane;
//import problem.Problem;
//
//import java.util.List;
//
//public class CurrentConfigController {
//    private BodyController parentController;
//
//    @FXML
//    private Label rotorsLabel;
//
//    @FXML
//    private Label windowsLabel;
//
//    @FXML
//    private Label reflectorLabel;
//
//    @FXML
//    private Label plugsLabel;
//
//    @FXML
//    private Label noConfigMsg;
//
//    @FXML
//    private Label configTitle;
//
//
//    public void clearOldOriginalConfig() {
//        rotorsLabel.setText("");
//        windowsLabel.setText("");
//        reflectorLabel.setText("");
//        plugsLabel.setText("");
//        noConfigMsg.setText("No configuration yet.");
//    }
//
//    public void setParentController(BodyController parentController) {
//        this.parentController = parentController;
//    }
//
//
//    public void setTitle(String title) {
//        configTitle.setText(title);
//    }
//
//    public void bindConfigComponents(ListProperty<Integer> inUseRotorsIDsProperty,
//                                     StringProperty currentWindowsCharactersProperty,
//                                     StringProperty inUseReflectorSymbolProperty,
//                                     StringProperty inUsePlugs,
//                                     ListProperty<Integer> currentNotchDistances,
//                                     BooleanProperty isMachineConfigured
//    ) {
//        rotorsLabel.textProperty().bind(new InUseRotorsIDsBinding(inUseRotorsIDsProperty));
//        windowsLabel.textProperty().bind(new CurrWinCharsAndNotchPosBinding(currentWindowsCharactersProperty, currentNotchDistances));
//        reflectorLabel.textProperty().bind(new InUseReflectorSymbolBinding(inUseReflectorSymbolProperty));
//        plugsLabel.textProperty().bind(new InUsePlugsBinding(inUsePlugs));
//        noConfigMsg.textProperty().bind(Bindings.when(isMachineConfigured).then("").otherwise("No configuration yet."));
//    }
//
//    public void displayOriginalConfiguration(List<Integer> rotorsIDs, String currentWindowsCharacters, String inUseReflectorSymbol, String inUsePlugs, List<Integer> currentNotchDistances) {
//        StringBuilder rotorsStr = new StringBuilder();
//
//        rotorsStr.append("<");
//        for (int i = rotorsIDs.size() - 1; i >= 0; i--) {
//            rotorsStr.append(rotorsIDs.get(i).toString());
//
//            if (i != 0) {
//                rotorsStr.append(",");
//            }
//        }
//        rotorsStr.append(">");
//
//        // display rotors ids
//        this.rotorsLabel.setText(rotorsStr.toString());
//
//        // display windows
//        StringBuilder reversedWindows = new StringBuilder();
//
//        for (int i = currentWindowsCharacters.length() - 1; i >= 0; i--) {
//            reversedWindows.append(currentWindowsCharacters.charAt(i));
//
//            if (currentNotchDistances.size() > 0) {
//                reversedWindows.append("(")
//                        .append(currentNotchDistances.get(i).toString())
//                        .append(")");
//            }
//        }
//
//
//        this.windowsLabel.setText("<" + reversedWindows.toString() + ">");
//
//        // display reflector
//        this.reflectorLabel.setText("<" + inUseReflectorSymbol + ">");
//
//        StringBuilder plugsBuilder = new StringBuilder();
//
//        if (inUsePlugs.length() > 0) {
//            plugsBuilder.append("<");
//            for (int i = 0; i < inUsePlugs.length(); i += 2) {
//                plugsBuilder.append(inUsePlugs.charAt(i))
//                        .append("|")
//                        .append(inUsePlugs.charAt(i + 1));
//                if (i != inUsePlugs.length() - 2) {
//                    plugsBuilder.append(",");
//                }
//            }
//            plugsBuilder.append(">");
//        }
//
//        // display plugs
//        this.plugsLabel.setText(plugsBuilder.toString());
//
//        this.noConfigMsg.setText("");
//    }
//}
