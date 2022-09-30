//package body.screen2.encrypt;
//
//import app.statusbar.MessageTone;
//import body.BodyController;
//import dto.DTOciphertext;
//import javafx.animation.FadeTransition;
//import javafx.animation.PathTransition;
//import javafx.beans.binding.Bindings;
//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.StringProperty;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.KeyEvent;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.FlowPane;
//import javafx.scene.shape.*;
//import javafx.util.Duration;
//import org.controlsfx.control.ToggleSwitch;
//
//public class EncryptDecryptController {
//
//    BodyController parentController;
//
//    String cipheredLetter = "";
//
//    String alphabet;
//
//    @FXML
//    private ScrollPane lightBulbsScrollPane;
//
//    @FXML
//    private Label outputLabel;
//
//    @FXML
//    private TextField inputTextField;
//
//    @FXML
//    private FlowPane lightBulbs;
//
//    @FXML
//    private ToggleSwitch cipherModeTS;
//
//    @FXML
//    private Label cipheredOutputHeadline;
//
//    @FXML
//    private Label animationCipherLabel;
//
//    @FXML
//    private Line lineCipher;
//
//    @FXML
//    private Button processButton;
//
//    @FXML
//    private Button resetButton;
//
//    @FXML
//    private Button clearButton;
//
//    private StringProperty dictionaryExcludeCharactersProperty;
//
//    private final PathTransition cipherPathTransition = new PathTransition();
//
//    private final FadeTransition cipherFadeTransition = new FadeTransition();
//
//    private final ImageView processButtonIcon = new ImageView("/resource/buttonicons/gears-solid.png");
//    private BooleanProperty isAnimationProperty;
//
//
//    @FXML
//    public void initialize() {
//        processButton.setText("Process");
//        cipheredOutputHeadline.visibleProperty().bind(Bindings.when(outputLabel.textProperty().isEqualTo("")).then(true).otherwise(false));
//        lightBulbs.disableProperty().bind(cipherModeTS.selectedProperty().not());
//        animationCipherLabel.textProperty().bind(outputLabel.textProperty());
//
//        cipherPathTransition.setDuration(Duration.millis(1500));
//        cipherPathTransition.setNode(animationCipherLabel);
//        cipherPathTransition.setPath(lineCipher);
//
//        cipherFadeTransition.setFromValue(0);
//        cipherFadeTransition.setToValue(1);
//        cipherFadeTransition.setDuration(Duration.millis(500));
//        cipherFadeTransition.setDelay(Duration.millis(1500));
//        cipherFadeTransition.setNode(outputLabel);
//
//        processButtonIcon.setFitWidth(17);
//        processButtonIcon.setFitHeight(17);
//
//        processButton.setGraphic(processButtonIcon);
//
//        processButton.setContentDisplay(ContentDisplay.LEFT);
//
//    }
//
//    /**
//     * Q5 -> going up the chain to parent controller
//     *
//     * @param event key press
//     */
//    @FXML
//    void cipherCharacter(KeyEvent event) {
//        cipherOneCharacter(event.getCode().getName().toUpperCase());
//    }
//
//    private void cipherOneCharacter(String character) {
//        inputTextField.getStyleClass().removeAll("invalid-input-text-field");
//
//        if (cipherModeTS.isSelected()) {
//            DTOciphertext cipheredCharStatus = parentController.cipher(character);
//            if (!cipheredCharStatus.isSucceed()) {
//                cipheredLetter = "";
//                inputTextField.getStyleClass().add("invalid-input-text-field");
//                parentController.setStatusMessage("Could not cipher that character. " +
//                        parentController.convertProblemToMessage(cipheredCharStatus.getDetails()), MessageTone.ERROR);
//            } else {
//                outputLabel.setText(outputLabel.getText() + cipheredCharStatus.getCipheredText());
//                this.cipheredLetter = cipheredCharStatus.getCipheredText();
//                activateLightBulb(cipheredCharStatus.getCipheredText());
//            }
//        }
//    }
//
//    /**
//     * handles the Process btn action
//     *
//     * @param ignored mouseEvent ignored
//     */
//    @FXML
//    void processHandler(MouseEvent ignored) {
//        if (cipherModeTS.isSelected()) { // done btn
//            clearTextFields();
//            parentController.doneCurrentCipherProcess();
//            parentController.displayStatistics();
//        } else {
//            DTOciphertext cipheredLineStatus = parentController.cipher(inputTextField.getText().toUpperCase());
//            if (!cipheredLineStatus.isSucceed()) {
//                inputTextField.getStyleClass().add("invalid-input-text-field");
//                parentController.setStatusMessage("Could not cipher that text. " +
//                        parentController.convertProblemToMessage(cipheredLineStatus.getDetails()), MessageTone.ERROR);
//            } else {
//                outputLabel.setText(cipheredLineStatus.getCipheredText());
//                parentController.displayStatistics();
//
//                if (isAnimationProperty.getValue()) {
//                    animationCipherLabel.setOpacity(1);
//                    cipherPathTransition.play();
//                    outputLabel.setOpacity(0);
//                    cipherFadeTransition.play();
//                }
//            }
//        }
//    }
//
//    void processHandlerBruteForce(MouseEvent ignored) {
//
//        StringBuilder textBuilder = new StringBuilder();
//        String textToCipher;
//
//        // excludes all the excludedChars
//        for (Character regularCharacter : inputTextField.getText().toUpperCase().toCharArray()) {
//            if (!dictionaryExcludeCharactersProperty.getValue().contains(regularCharacter.toString())) {
//                textBuilder.append(regularCharacter);
//            }
//        }
//        textToCipher = textBuilder.toString();
//
//        // checks if all words are in the dictionary
//        if (!parentController.isAllWordsInDictionary(textToCipher)) {
//            inputTextField.getStyleClass().add("invalid-input-text-field");
//
//            parentController.setStatusMessage("All words should be from the dictionary", MessageTone.ERROR);
//            return;
//        }
//
//        // ciphers the text
//        DTOciphertext cipheredLineStatus = parentController.cipher(textToCipher);
//
//        if (!cipheredLineStatus.isSucceed()) {
//            inputTextField.getStyleClass().add("invalid-input-text-field");
//            parentController.setStatusMessage("Could not cipher that text. " +
//                    parentController.convertProblemToMessage(cipheredLineStatus.getDetails()), MessageTone.ERROR);
//        } else {
//            outputLabel.setText(cipheredLineStatus.getCipheredText());
//            parentController.displayStatistics();
//        }
//    }
//
//    /**
//     * @param ignored a mouse click on one of the 2 modes presented
//     */
//    @FXML
//    void setCipherMode(MouseEvent ignored) {
//        clearTextFields();
//        parentController.setCharByCharCipherMode(cipherModeTS.isSelected());
//    }
//
//    /**
//     * Q6 -> going up the chain to parent controller
//     *
//     * @param ignored mouse event ignored
//     */
//    @FXML
//    void ResetConfiguration(MouseEvent ignored) {
//        parentController.resetMachineConfiguration();
//    }
//
//    /**
//     * clear the textBox
//     *
//     * @param ignored mouse event ignored
//     */
//    @FXML
//    void clearCurrentCipher(MouseEvent ignored) {
//        clearTextFields();
//    }
//
//    public void clearTextFields() {
//        inputTextField.setText("");
//        outputLabel.setText("");
//    }
//
//    public void setParentController(BodyController parentController) {
//        this.parentController = parentController;
//    }
//
//    /**
//     * setting up the lightBulbs according to the Alphabet string
//     *
//     * @param alphabet string of the alphabet
//     */
//    public void initAlphabetLightBulbs(String alphabet) {
//        // clear old light bulbs
//        lightBulbs.getChildren().clear();
//
//        this.alphabet = alphabet;
//
//        for (Character letter : alphabet.toCharArray()) {
//            Button nextLightBulb = new Button();
//
//            // adds the circle shape
//            nextLightBulb.setMaxWidth(35);
//            nextLightBulb.setMaxHeight(35);
//            nextLightBulb.setMinWidth(35);
//            nextLightBulb.setMinHeight(35);
//            nextLightBulb.getStyleClass().add("light-bulb");
//
//            // adds the letter
//            nextLightBulb.setText("" + letter);
//            nextLightBulb.setWrapText(true);
//
//            nextLightBulb.setOnMousePressed(event -> {
//                Button srcLightBulb = (Button) event.getSource();
//                String srcLightBulbLetter = srcLightBulb.getText();
//                inputTextField.setText(inputTextField.getText() + srcLightBulbLetter);
//                cipherOneCharacter(srcLightBulbLetter);
//            });
//
//            nextLightBulb.setOnMouseReleased(event -> deactivateLight());
//
//            lightBulbs.getChildren().add(nextLightBulb);
//        }
//    }
//
//    public void setAllowEncryptDecrypt(boolean isAllow) {
//        inputTextField.setDisable(!isAllow);
//        processButton.setDisable(!isAllow);
//        resetButton.setDisable(!isAllow);
//        clearButton.setDisable(!isAllow);
//        inputTextField.setText("");
//        outputLabel.setText("");
//    }
//
//    /**
//     * activate animation on the matching bulb
//     *
//     * @param letter the letter to find the bulb to activate
//     */
//    public void activateLightBulb(String letter) {
//        ObservableList<?> elements = lightBulbs.getChildren();
//        Button currentPane = (Button) elements.get(alphabet.indexOf(letter.charAt(0)));
//        currentPane.getStyleClass().add("light-on");
//    }
//
//    /**
//     * deactivate the matching bulb
//     *
//     * @param ignored when user release the key presses
//     */
//    @FXML
//    public void deactivateLightBulb(KeyEvent ignored) {
//
//        if (cipheredLetter.equals("")) {
//            return;
//        }
//        ObservableList<?> elements = lightBulbs.getChildren();
//
//        elements.forEach(o -> {
//            Button currentPane = (Button) o;
//            currentPane.getStyleClass().removeAll("light-on");
//        });
//
//    }
//
//    private void deactivateLight() {
//        ObservableList<?> elements = lightBulbs.getChildren();
//        Button currentPane = (Button) elements.get(alphabet.indexOf(cipheredLetter.charAt(0)));
//
//        currentPane.getStyleClass().removeAll("light-on");
//    }
//
//    public StringProperty getOutputLabelProperty() {
//        return outputLabel.textProperty();
//    }
//
//    public void setAvailabilityOfCharByCharMode(boolean availabilityOfCharByCharMode) {
//        if (!availabilityOfCharByCharMode) {
//            cipherModeTS.setSelected(availabilityOfCharByCharMode);
//        }
//
//        cipherModeTS.setVisible(availabilityOfCharByCharMode);
//    }
//
//    public void showLightBulbs(boolean needToShow) {
//        lightBulbsScrollPane.setVisible(needToShow);
//        lightBulbsScrollPane.setHmax(0);
//    }
//
//    public void bindCipherMode(BooleanProperty isCharByCharModeProperty) {
//        processButton.textProperty().bind(Bindings.when(isCharByCharModeProperty.not()).then("Process").otherwise("Done"));
//    }
//
//    public void appendNewWordToInputCipherText(String newWord) {
//        if (inputTextField.getText().equals("")) {
//            inputTextField.setText(newWord);
//        } else {
//            inputTextField.setText(inputTextField.getText() + " " + newWord);
//        }
//    }
//
//    public void setOnActionProcessToBruteForceMode() {
//        processButton.setOnMouseClicked(this::processHandlerBruteForce);
//    }
//
//    public void setEncryptExcludeCharsValue(StringProperty dictionaryExcludeCharsProperty) {
//        this.dictionaryExcludeCharactersProperty = dictionaryExcludeCharsProperty;
//    }
//
//    public void setIsAnimationPropertyEncryptDecrypt(BooleanProperty isAnimationProperty) {
//        this.isAnimationProperty = isAnimationProperty;
//    }
//}
