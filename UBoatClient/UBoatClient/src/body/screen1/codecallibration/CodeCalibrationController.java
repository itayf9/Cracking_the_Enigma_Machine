package body.screen1.codecallibration; //package body.screen1.codecalibration;

import body.BodyController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;


import static utill.Utility.decimalToRoman;
import static utill.Utility.romanToDecimal;

public class CodeCalibrationController {

    BodyController parentController;

    @FXML
    private Button randomCalibrationButton;

    @FXML
    private Button setCalibrationButton;

    @FXML
    private HBox rotorsHbox;

    @FXML
    private HBox windowsCharHbox;

    @FXML
    private HBox plugsHBox;

    @FXML
    private Button removePlugButton;

    @FXML
    private Button addPlugButton;

    @FXML
    private HBox reflectorBox;

    private ToggleGroup reflectorToggles;
    private String alphabet;

    String rotorsInput;
    String windowsInput;
    String plugsInput;

    @FXML
    public void initialize() {
        reflectorToggles = new ToggleGroup();
    }

    @FXML
    void randomMachineConfig(MouseEvent ignored) {
        parentController.setRandomMachineConfig();
    }

    @FXML
    void setMachineConfig(MouseEvent ignored) {
        RadioButton currentReflector = (RadioButton) reflectorToggles.getSelectedToggle();
        parentController.setManualMachineConfig(rotorsInput, windowsInput, romanToDecimal(currentReflector.getText()), plugsInput);
    }

    public void setParentController(BodyController parentController) {
        this.parentController = parentController;
    }

    public void setCodeCalibration(int inUseRotorsCount, int availableRotorsCount, String machineAlphabet, int availableReflectorsCount) {

        this.alphabet = machineAlphabet;
        // remove old machine stuff
        rotorsHbox.getChildren().clear();
        windowsCharHbox.getChildren().clear();
        reflectorBox.getChildren().clear();
        reflectorToggles.getToggles().clear();
        plugsHBox.getChildren().clear();

        // create new components

        for (int i = 0; i < inUseRotorsCount; i++) {
            ComboBox<Integer> nextRotorComboBox = new ComboBox<>();
            ComboBox<Character> nextWindowComboBox = new ComboBox<>();

            nextRotorComboBox.getStyleClass().add("code-calibration-combo-box");
            nextWindowComboBox.getStyleClass().add("code-calibration-combo-box");

            nextRotorComboBox.setMinWidth(53);
            nextWindowComboBox.setMinWidth(53);

            nextRotorComboBox.setPrefWidth(53);
            nextWindowComboBox.setPrefWidth(53);

            nextRotorComboBox.setMinHeight(25);
            nextWindowComboBox.setMinHeight(25);

            nextRotorComboBox.setPrefHeight(25);
            nextWindowComboBox.setPrefHeight(25);


            for (int j = 1; j <= availableRotorsCount; j++) {
                nextRotorComboBox.setPromptText("-");
                nextRotorComboBox.getItems().add(j);
            }
            rotorsHbox.getChildren().add(nextRotorComboBox);

            for (Character window : machineAlphabet.toCharArray()) {
                nextWindowComboBox.setPromptText("-");
                nextWindowComboBox.getItems().add(window);
            }

            windowsCharHbox.getChildren().add(nextWindowComboBox);
        }

        for (int i = 1; i <= availableReflectorsCount; i++) {
            RadioButton nextReflectorRadioButton = new RadioButton();
            nextReflectorRadioButton.setText(decimalToRoman(i));
            nextReflectorRadioButton.setToggleGroup(reflectorToggles);

            reflectorBox.getChildren().add(nextReflectorRadioButton);
        }
    }

    @FXML
    void addPlugAction(MouseEvent event) {
        HBox plugPair = new HBox();
        ComboBox<Character> firstInPair = new ComboBox<>();
        ComboBox<Character> secondInPair = new ComboBox<>();

        firstInPair.getStyleClass().add("code-calibration-combo-box");
        secondInPair.getStyleClass().add("code-calibration-combo-box");

        firstInPair.setPromptText("-");
        secondInPair.setPromptText("-");

        firstInPair.setMinWidth(53);
        secondInPair.setMinWidth(53);

        firstInPair.setPrefWidth(53);
        secondInPair.setPrefWidth(53);

        firstInPair.setMinHeight(25);
        secondInPair.setMinHeight(25);

        firstInPair.setPrefHeight(25);
        secondInPair.setPrefHeight(25);

        for (Character letter : alphabet.toCharArray()) {

            firstInPair.getItems().add(letter);
            secondInPair.getItems().add(letter);
        }

        plugPair.getChildren().addAll(firstInPair, secondInPair);
        plugsHBox.getChildren().add(plugPair);
    }

    @FXML
    void removePlugAction(MouseEvent event) {
        if (plugsHBox.getChildren().size() != 0) {
            plugsHBox.getChildren().remove(plugsHBox.getChildren().size() - 1);
        }
    }

}
