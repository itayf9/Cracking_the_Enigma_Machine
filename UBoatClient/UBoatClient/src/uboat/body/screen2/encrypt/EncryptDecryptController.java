package uboat.body.screen2.encrypt;

import uboat.body.BodyController;
import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import problem.Problem;

public class EncryptDecryptController {

    BodyController parentController;

    String cipheredLetter = "";

    String alphabet;


    @FXML
    private Label outputLabel;

    @FXML
    private Label cipheredOutputHeadline;

    @FXML
    private TextField inputTextField;


    @FXML
    private Button processButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button clearButton;


    private StringProperty dictionaryExcludeCharactersProperty;

    private final PathTransition cipherPathTransition = new PathTransition();

    private final FadeTransition cipherFadeTransition = new FadeTransition();

    private BooleanProperty isAnimationProperty;


    @FXML
    public void initialize() {
        processButton.setText("Process");
        cipheredOutputHeadline.visibleProperty().bind(Bindings.when(outputLabel.textProperty().isEqualTo("")).then(true).otherwise(false));
        cipherPathTransition.setDuration(Duration.millis(1500));
        cipherFadeTransition.setFromValue(0);
        cipherFadeTransition.setToValue(1);
        cipherFadeTransition.setDuration(Duration.millis(500));
        cipherFadeTransition.setDelay(Duration.millis(1500));
        cipherFadeTransition.setNode(outputLabel);
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
        //outputLabel.setText("");
    }

    @FXML
    void setReady(MouseEvent event) {
        parentController.setReady();
    }

    public void setParentController(BodyController parentController) {
        this.parentController = parentController;
    }

    public void appendNewWordToInputCipherText(String newWord) {
        if (inputTextField.getText().equals("")) {
            inputTextField.setText(newWord);
        } else {
            inputTextField.setText(inputTextField.getText() + " " + newWord);
        }
    }

    public void setEncryptExcludeCharsValue(StringProperty dictionaryExcludeCharsProperty) {
        this.dictionaryExcludeCharactersProperty = dictionaryExcludeCharsProperty;
    }

    public void setIsAnimationPropertyEncryptDecrypt(BooleanProperty isAnimationProperty) {
        this.isAnimationProperty = isAnimationProperty;
    }

    public void setCipherOutput(Problem problem, String cipherText) {
        if (!problem.equals(Problem.NO_PROBLEM)) {
            inputTextField.getStyleClass().add("invalid-input-text-field");
        } else {
            outputLabel.setText(cipherText);
        }
    }

    public void bindProcessButton(BooleanProperty isProcessedText) {
        processButton.disableProperty().bind(isProcessedText);
        clearButton.disableProperty().bind(isProcessedText);
    }
}
