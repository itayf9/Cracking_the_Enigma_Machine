//package body.screen1.codecalibration;
//
//import app.statusbar.MessageTone;
//import body.BodyController;
//import dto.DTOsecretConfig;
//import dto.DTOstatus;
//import javafx.fxml.FXML;
//import javafx.scene.Node;
//import javafx.scene.control.*;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.HBox;
//import problem.Problem;
//
//import static utill.Utility.decimalToRoman;
//import static utill.Utility.romanToDecimal;
//
//public class CodeCalibrationController {
//
//    BodyController parentController;
//
//    @FXML
//    private Button randomCalibrationButton;
//
//    @FXML
//    private Button setCalibrationButton;
//
//    @FXML
//    private HBox rotorsHbox;
//
//    @FXML
//    private HBox windowsCharHbox;
//
//    @FXML
//    private HBox plugsHBox;
//
//    @FXML
//    private Button removePlugButton;
//
//    @FXML
//    private Button addPlugButton;
//
//    @FXML
//    private HBox reflectorBox;
//
//    private ToggleGroup reflectorToggles;
//    private String alphabet;
//
//    String rotorsInput;
//    String windowsInput;
//    String plugsInput;
//
//    @FXML
//    public void initialize() {
//        reflectorToggles = new ToggleGroup();
//    }
//
//    @FXML
//    void randomMachineConfig(MouseEvent ignored) {
//        parentController.setRandomMachineConfig();
//    }
//
//
//    DTOstatus validateRotorsInput() {
//        StringBuilder rotorsInputBuilder = new StringBuilder();
//
//        for (int i = 0; i < rotorsHbox.getChildren().size(); i++) {
//            ComboBox<Integer> nextRotorComboBox = (ComboBox<Integer>) rotorsHbox.getChildren().get(i);
//
//            if (nextRotorComboBox.getValue() != null) {
//                rotorsInputBuilder.append(nextRotorComboBox.getValue());
//
//                if (i != rotorsHbox.getChildren().size() - 1) {
//                    rotorsInputBuilder.append(",");
//                }
//            }
//        }
//
//        rotorsInput = rotorsInputBuilder.toString();
//
//        return parentController.validateRotorsInput(rotorsInput);
//    }
//
//    DTOstatus validateWindowsInput() {
//
//        StringBuilder windowsBuilder = new StringBuilder();
//        for (Node window : windowsCharHbox.getChildren()) {
//            ComboBox<Character> windowCharacter = (ComboBox<Character>) window;
//            if (windowCharacter.getValue() != null) {
//                windowsBuilder.append(windowCharacter.getValue());
//            }
//        }
//
//        windowsInput = windowsBuilder.toString();
//
//        return parentController.validateWindowsCharsInput(windowsInput);
//    }
//
//    DTOstatus validateReflectorInput() {
//        RadioButton currentReflector = (RadioButton) reflectorToggles.getSelectedToggle();
//        if (currentReflector == null) {
//            // problemLabelReflector.setText(Problem.NO_REFLECTOR_BEEN_CHOSEN.name());
//            return new DTOstatus(false, Problem.NO_REFLECTOR_BEEN_CHOSEN);
//        }
//
//        return parentController.validateReflectorInput(romanToDecimal(currentReflector.getText()));
//    }
//
//    DTOstatus validatePlugsInput() {
//        StringBuilder plugsInputBuilder = new StringBuilder();
//
//        for (Node nextNode : plugsHBox.getChildren()) {
//            HBox nextPlug = (HBox) nextNode;
//            ComboBox<Character> firstInPlug = (ComboBox<Character>) nextPlug.getChildren().get(0);
//            ComboBox<Character> secondInPlug = (ComboBox<Character>) nextPlug.getChildren().get(1);
//
//            if (firstInPlug.getValue() != null) {
//                plugsInputBuilder.append(firstInPlug.getValue());
//            }
//            if (secondInPlug.getValue() != null) {
//                plugsInputBuilder.append(secondInPlug.getValue());
//            }
//
//            if (plugsInputBuilder.length() % 2 == 1) {
//                return new DTOstatus(false, Problem.PLUGS_MISSING_VALUES);
//            }
//        }
//
//        plugsInput = plugsInputBuilder.toString();
//
//        return parentController.validatePlugsInput(plugsInput);
//    }
//
//    @FXML
//    void setMachineConfig(MouseEvent ignored) {
//
//        boolean isValid;
//        DTOstatus rtrStatus = validateRotorsInput();
//        DTOstatus wndStatus = validateWindowsInput();
//        DTOstatus rflcStatus = validateReflectorInput();
//        DTOstatus plgsStatus = validatePlugsInput();
//
//        // assuming all is valid
//
//        isValid = rtrStatus.isSucceed() &&
//                wndStatus.isSucceed() &&
//                plgsStatus.isSucceed() &&
//                rflcStatus.isSucceed();
//
//        if (isValid) {
//            RadioButton currentReflector = (RadioButton) reflectorToggles.getSelectedToggle();
//            parentController.setManualMachineConfig(rotorsInput, windowsInput, romanToDecimal(currentReflector.getText()), plugsInput);
//        } else {
//            StringBuilder statusMessage = new StringBuilder();
//            if (!rtrStatus.isSucceed()) {
//                statusMessage.append(parentController.convertProblemToMessage(rtrStatus.getDetails())).append(" ");
//            }
//            if (!wndStatus.isSucceed()) {
//                statusMessage.append(parentController.convertProblemToMessage(wndStatus.getDetails())).append(" ");
//            }
//            if (!plgsStatus.isSucceed()) {
//                statusMessage.append(parentController.convertProblemToMessage(plgsStatus.getDetails())).append(" ");
//            }
//            if (!rflcStatus.isSucceed()) {
//                statusMessage.append(parentController.convertProblemToMessage(rflcStatus.getDetails())).append(" ");
//            }
//
//            parentController.setStatusMessage("Could not calibrate the machine. " + statusMessage.toString(), MessageTone.ERROR);
//        }
//    }
//
//    public void setParentController(BodyController parentController) {
//        this.parentController = parentController;
//    }
//
//    public void setCodeCalibration(int inUseRotorsCount, int availableRotorsCount, String machineAlphabet, int availableReflectorsCount) {
//
//        this.alphabet = machineAlphabet;
//        // remove old machine stuff
//        rotorsHbox.getChildren().clear();
//        windowsCharHbox.getChildren().clear();
//        reflectorBox.getChildren().clear();
//        reflectorToggles.getToggles().clear();
//        plugsHBox.getChildren().clear();
//
//        // create new components
//
//        for (int i = 0; i < inUseRotorsCount; i++) {
//            ComboBox<Integer> nextRotorComboBox = new ComboBox<>();
//            ComboBox<Character> nextWindowComboBox = new ComboBox<>();
//
//            nextRotorComboBox.getStyleClass().add("code-calibration-combo-box");
//            nextWindowComboBox.getStyleClass().add("code-calibration-combo-box");
//
//            nextRotorComboBox.setMinWidth(53);
//            nextWindowComboBox.setMinWidth(53);
//
//            nextRotorComboBox.setPrefWidth(53);
//            nextWindowComboBox.setPrefWidth(53);
//
//            nextRotorComboBox.setMinHeight(25);
//            nextWindowComboBox.setMinHeight(25);
//
//            nextRotorComboBox.setPrefHeight(25);
//            nextWindowComboBox.setPrefHeight(25);
//
//
//            for (int j = 1; j <= availableRotorsCount; j++) {
//                nextRotorComboBox.setPromptText("-");
//                nextRotorComboBox.getItems().add(j);
//            }
//            rotorsHbox.getChildren().add(nextRotorComboBox);
//
//            for (Character window : machineAlphabet.toCharArray()) {
//                nextWindowComboBox.setPromptText("-");
//                nextWindowComboBox.getItems().add(window);
//            }
//
//            windowsCharHbox.getChildren().add(nextWindowComboBox);
//        }
//
//        for (int i = 1; i <= availableReflectorsCount; i++) {
//            RadioButton nextReflectorRadioButton = new RadioButton();
//            nextReflectorRadioButton.setText(decimalToRoman(i));
//            nextReflectorRadioButton.setToggleGroup(reflectorToggles);
//
//            reflectorBox.getChildren().add(nextReflectorRadioButton);
//        }
//    }
//
//    /*public void displayCurrentConfigInFields(DTOsecretConfig configStatus) {
//
//        // set rotors label
//        // problemLabelRotors.setText("");
//
//        StringBuilder rotorsStr = new StringBuilder();
//        for (int i = configStatus.getRotors().size() - 1; i >= 0; i--) {
//            rotorsStr.append(configStatus.getRotors().get(i).toString());
//
//            if (i != 0) {
//                rotorsStr.append(",");
//            }
//        }
//        rotorsInput.getStyleClass().removeAll("invalid-input-text-field");
//        rotorsInput.setText(rotorsStr.toString());
//
//
//        // set windows label
//        // problemLabelWindows.setText("");
//
//        windowsCharsInput.getStyleClass().removeAll("invalid-input-text-field");
//        windowsCharsInput.setText(configStatus.getWindows());
//
//        boolean isFoundSelectedRadioButton = false;
//        // set Reflector
//        // problemLabelReflector.setText("");
//
//        ObservableList<Toggle> reflectorButtons = reflectorToggles.getToggles();
//        for (Toggle toggle : reflectorButtons) {
//            RadioButton radioButton = (RadioButton) toggle;
//            if (radioButton.getText().equals(configStatus.getReflectorSymbol())) {
//                radioButton.setSelected(true);
//                isFoundSelectedRadioButton = true;
//            }
//            if (!isFoundSelectedRadioButton) {
//                radioButton.setSelected(false);
//            }
//            ((RadioButton) toggle).getStyleClass().removeAll("invalid-input-text-field");
//        }
//
//        // set plugs label
//        // problemLabelPlugs.setText("");
//        plugsInput.getStyleClass().removeAll("invalid-input-text-field");
//        plugsInput.setText(configStatus.getPlugs());
//    }*/
//
//    @FXML
//    void addPlugAction(MouseEvent event) {
//        HBox plugPair = new HBox();
//        ComboBox<Character> firstInPair = new ComboBox<>();
//        ComboBox<Character> secondInPair = new ComboBox<>();
//
//        firstInPair.getStyleClass().add("code-calibration-combo-box");
//        secondInPair.getStyleClass().add("code-calibration-combo-box");
//
//        firstInPair.setPromptText("-");
//        secondInPair.setPromptText("-");
//
//        firstInPair.setMinWidth(53);
//        secondInPair.setMinWidth(53);
//
//        firstInPair.setPrefWidth(53);
//        secondInPair.setPrefWidth(53);
//
//        firstInPair.setMinHeight(25);
//        secondInPair.setMinHeight(25);
//
//        firstInPair.setPrefHeight(25);
//        secondInPair.setPrefHeight(25);
//
//        for (Character letter : alphabet.toCharArray()) {
//
//            firstInPair.getItems().add(letter);
//            secondInPair.getItems().add(letter);
//        }
//
//        plugPair.getChildren().addAll(firstInPair, secondInPair);
//        plugsHBox.getChildren().add(plugPair);
//    }
//
//    @FXML
//    void removePlugAction(MouseEvent event) {
//        if (plugsHBox.getChildren().size() != 0) {
//            plugsHBox.getChildren().remove(plugsHBox.getChildren().size() - 1);
//        }
//    }
//
//}
