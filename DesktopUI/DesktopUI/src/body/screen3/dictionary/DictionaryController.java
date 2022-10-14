//package body.screen3.dictionary;
//
//import body.BodyController;
//import javafx.fxml.FXML;
//import javafx.geometry.Insets;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.FlowPane;
//
//import java.util.Set;
//
//public class DictionaryController {
//
//    BodyController parentController;
//    private Trie dictionary;
//
//    @FXML
//    private FlowPane wordsAreaFlowPane;
//
//    @FXML
//    private TextField searchTextField;
//    private String alphabet;
//
//    @FXML
//    public void initialize() {
//        this.searchTextField.textProperty().addListener((observable, oldValue, newValue) -> displayDictionaryWordsThatStartWith(newValue));
//    }
//
//    public void setParentController(BodyController parentController) {
//        this.parentController = parentController;
//    }
//
//    /**
//     * initialize the dictionary Controller to have all dictionary words.
//     *
//     * @param dictionaryWords a Set of String(the words) from the dictionary of the DM
//     */
//    public void setDictionaryWords(Set<String> dictionaryWords, String alphabet) {
//        this.alphabet = alphabet;
//
//        // clear old words
//        wordsAreaFlowPane.getChildren().clear();
//        this.dictionary = new Trie();
//        // insert words to dictionary
//        for (String word : dictionaryWords) {
//            dictionary.insert(word);
//        }
//
//        displayDictionaryWordsThatStartWith("");
//    }
//
//    public void displayDictionaryWordsThatStartWith(String newValue) {
//        wordsAreaFlowPane.getChildren().clear();
//        Set<String> allWordsWithSearchedPrefix = dictionary.getAllWordsWithPrefix(newValue.toUpperCase());
//        if (allWordsWithSearchedPrefix != null) {
//            for (String word : allWordsWithSearchedPrefix) {
//                boolean validWord = true;
//                Label wordLabel = new Label(word);
//                wordLabel.setPadding(new Insets(5));
//
//                for (Character letter : word.toCharArray()) {
//                    if (!alphabet.contains("" + letter)) {
//                        wordLabel.getStyleClass().add("invalid-word-label");
//                        validWord = false;
//                    }
//                }
//
//                if (validWord) {
//                    wordLabel.setOnMouseClicked(event -> parentController.appendNewWordToInputCipherText(wordLabel.getText()));
//                    wordLabel.getStyleClass().add("word-label");
//                }
//                wordsAreaFlowPane.getChildren().add(wordLabel);
//            }
//        }
//    }
//
//}
