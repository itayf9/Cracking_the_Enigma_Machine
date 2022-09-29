package dm.dictionary;

import java.util.HashSet;
import java.util.Set;

public class Dictionary {

    private Set<String> words;
    private String excludeChars;

    public Dictionary(String wordsAsString, String excludeChars) {
        this.excludeChars = excludeChars;
        this.words = new HashSet<>();
        initializeDictionary(wordsAsString);
    }

    public void initializeDictionary(String wordsAsString) {
        
        // clear old words at the engine
        words.clear();

        StringBuilder wordsBuilder = new StringBuilder();

        // excludes all the excludedChars
        for (Character regularCharacter : wordsAsString.toCharArray()) {
            if (!excludeChars.contains(regularCharacter.toString())) {
                wordsBuilder.append(regularCharacter);
            }
        }

        String wordsWithoutExcludeChars = wordsBuilder.toString();

        // assuming no exclude characters here !!
        String[] dictionaryWords = wordsWithoutExcludeChars.split(" ");

        // inserts the words into a set
        // deletes duplications
        for (String word : dictionaryWords) {
            this.words.add(word);
        }
    }

    public boolean isAllWordsInDictionary(String text) {
        boolean res = true;
        String[] textWords = text.trim().split(" ");

        for (String word : textWords) {
            if (!word.equals("")) {
                if (!words.contains(word)) {
                    res = false;
                    break;
                }
            }
        }
        return res;
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "words=" + words +
                ", excludeChars='" + excludeChars + '\'' +
                '}';
    }

    public Set<String> getWords() {
        return words;
    }

    public String getExcludeChars() {
        return excludeChars;
    }

    public Set<String> getDictionaryWords() {
        return words;
    }
}
