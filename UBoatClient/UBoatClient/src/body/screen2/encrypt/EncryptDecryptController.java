package body.screen2.encrypt;

import app.MessageTone;
import body.BodyController;
import dto.DTOciphertext;
import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.*;
import javafx.util.Duration;

public class EncryptDecryptController {

    BodyController parentController;

    String cipheredLetter = "";

    String alphabet;

    @FXML
    private ScrollPane lightBulbsScrollPane;

    @FXML
    private Label outputLabel;

    @FXML
    private TextField inputTextField;

    @FXML
    private Label cipheredOutputHeadline;

    @FXML
    private Label animationCipherLabel;

    @FXML
    private Line lineCipher;

    @FXML
    private Button processButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button clearButton;

    private StringProperty dictionaryExcludeCharactersProperty;

    private final PathTransition cipherPathTransition = new PathTransition();

    private final FadeTransition cipherFadeTransition = new FadeTransition();

    private final ImageView processButtonIcon = new ImageView("/resource/buttonicons/gears-solid.png");
    private BooleanProperty isAnimationProperty;


    @FXML
    public void initialize() {
        processButton.setText("Process");
        cipheredOutputHeadline.visibleProperty().bind(Bindings.when(outputLabel.textProperty().isEqualTo("")).then(true).otherwise(false));
        animationCipherLabel.textProperty().bind(outputLabel.textProperty());

        cipherPathTransition.setDuration(Duration.millis(1500));
        cipherPathTransition.setNode(animationCipherLabel);
        cipherPathTransition.setPath(lineCipher);

        cipherFadeTransition.setFromValue(0);
        cipherFadeTransition.setToValue(1);
        cipherFadeTransition.setDuration(Duration.millis(500));
        cipherFadeTransition.setDelay(Duration.millis(1500));
        cipherFadeTransition.setNode(outputLabel);

        processButtonIcon.setFitWidth(17);
        processButtonIcon.setFitHeight(17);

        processButton.setGraphic(processButtonIcon);

        processButton.setContentDisplay(ContentDisplay.LEFT);

    }

    /* /**
     * handles the Process btn action
     *
     * @param ignored mouseEvent ignored
     *//*
    @FXML
    void processHandler(MouseEvent ignored) {

        DTOciphertext cipheredLineStatus = parentController.cipher(inputTextField.getText().toUpperCase());
        if (!cipheredLineStatus.isSucceed()) {
            inputTextField.getStyleClass().add("invalid-input-text-field");
            parentController.setStatusMessage("Could not cipher that text. " +
                    parentController.convertProblemToMessage(cipheredLineStatus.getDetails()), MessageTone.ERROR);
        } else {
            outputLabel.setText(cipheredLineStatus.getCipheredText());

            if (isAnimationProperty.getValue()) {
                animationCipherLabel.setOpacity(1);
                cipherPathTransition.play();
                outputLabel.setOpacity(0);
                cipherFadeTransition.play();
            }
        }
    }*/

    /**
     * handles the Process btn action
     *
     * @param ignored mouseEvent ignored
     */
    @FXML
    void processHandlerBruteForce(MouseEvent ignored) {

        StringBuilder textBuilder = new StringBuilder();
        String textToCipher;

        // excludes all the excludedChars
        for (Character regularCharacter : inputTextField.getText().toUpperCase().toCharArray()) {
            if (!dictionaryExcludeCharactersProperty.getValue().contains(regularCharacter.toString())) {
                textBuilder.append(regularCharacter);
            }
        }
        textToCipher = textBuilder.toString();
        // ciphers the text
        parentController.cipher(textToCipher);
    }

    /**
     * Q6 -> going up the chain to parent controller
     *
     * @param ignored mouse event ignored
     */
    @FXML
    void ResetConfiguration(MouseEvent ignored) {
        parentController.resetMachineConfiguration();
    }

    /**
     * clear the textBox
     *
     * @param ignored mouse event ignored
     */
    @FXML
    void clearCurrentCipher(MouseEvent ignored) {
        clearTextFields();
    }

    public void clearTextFields() {
        inputTextField.setText("");
        outputLabel.setText("");
    }

    public void setParentController(BodyController parentController) {
        this.parentController = parentController;
    }

    public void setAllowEncryptDecrypt(boolean isAllow) {
        inputTextField.setDisable(!isAllow);
        processButton.setDisable(!isAllow);
        resetButton.setDisable(!isAllow);
        clearButton.setDisable(!isAllow);
        inputTextField.setText("");
        outputLabel.setText("");
    }

    public StringProperty getOutputLabelProperty() {
        return outputLabel.textProperty();
    }

    public void showLightBulbs(boolean needToShow) {
        lightBulbsScrollPane.setVisible(needToShow);
        lightBulbsScrollPane.setHmax(0);
    }

    public void bindCipherMode(BooleanProperty isCharByCharModeProperty) {
        processButton.textProperty().bind(Bindings.when(isCharByCharModeProperty.not()).then("Process").otherwise("Done"));
    }

    public void appendNewWordToInputCipherText(String newWord) {
        if (inputTextField.getText().equals("")) {
            inputTextField.setText(newWord);
        } else {
            inputTextField.setText(inputTextField.getText() + " " + newWord);
        }
    }

    public void setOnActionProcessToBruteForceMode() {
        processButton.setOnMouseClicked(this::processHandlerBruteForce);
    }

    public void setEncryptExcludeCharsValue(StringProperty dictionaryExcludeCharsProperty) {
        this.dictionaryExcludeCharactersProperty = dictionaryExcludeCharsProperty;
    }

    public void setIsAnimationPropertyEncryptDecrypt(BooleanProperty isAnimationProperty) {
        this.isAnimationProperty = isAnimationProperty;
    }

    public void setCipherOutput(DTOciphertext cipherStatus) {
        if (!cipherStatus.isSucceed()) {
            inputTextField.getStyleClass().add("invalid-input-text-field");

        } else {
            outputLabel.setText(cipherStatus.getCipheredText());
        }
    }
}
