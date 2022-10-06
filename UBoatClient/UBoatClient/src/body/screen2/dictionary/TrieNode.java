package body.screen2.dictionary;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    private HashMap<Character, TrieNode> children;
    private Character keyCharacter;
    private boolean isEndOfWord;

    public TrieNode() {
        this.children = new HashMap<>();
        this.keyCharacter = (char) -1;
        this.isEndOfWord = false;
    }

    public TrieNode(Character keyCharacter) {
        this.children = new HashMap<>();
        this.keyCharacter = keyCharacter;
        this.isEndOfWord = false;
    }

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    // setter
    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

    // getter
    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public Character getKeyCharacter() {
        return keyCharacter;
    }
}