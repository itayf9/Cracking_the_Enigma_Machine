package uboat.body.screen2.dictionary;

import java.util.HashSet;
import java.util.Set;

public class Trie {
    private TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode current = root;

        for (char currentChar : word.toCharArray()) {
            current = current.getChildren().computeIfAbsent(currentChar, c -> new TrieNode(c));
        }
        current.setEndOfWord(true);
    }

    public boolean find(String word) {
        TrieNode current = root;
        for (int i = 0; i < word.length(); i++) {
            char currentChar = word.charAt(i);
            TrieNode node = current.getChildren().get(currentChar);
            if (node == null) {
                return false;
            }
            current = node;
        }
        return current.isEndOfWord();
    }

    public Set<String> getAllWordsWithPrefix(String prefix) {
        Set<String> words = new HashSet<>();
        StringBuilder wordBuilder = new StringBuilder();
        TrieNode current = root;
        for (int i = 0; i < prefix.length(); i++) {
            char currentChar = prefix.charAt(i);
            wordBuilder.append(currentChar);
            TrieNode node = current.getChildren().get(currentChar);
            if (node == null) {
                return null; // no words with that prefix
            }
            current = node;
        }
        if (current.isEndOfWord()) {
            words.add(wordBuilder.toString());
        }
        if (current.getChildren().size() > 0) {
            getAllWordsWithPrefixUtil(current, prefix, words, new StringBuilder());
        }

        return words;
    }

    private void getAllWordsWithPrefixUtil(TrieNode startNode, String prefix, Set<String> strings, StringBuilder soFar) {

        /**
         * not sure myself what's this part of the code does.
         */
        if (startNode.isEndOfWord()) {
            StringBuilder ans = new StringBuilder();
            ans.append(prefix);
            ans.append(soFar.toString());
            strings.add(ans.toString());
            //return;
        }

        for (char i = 'A'; i <= 'Z'; i++) {
            TrieNode child = startNode.getChildren().get(i);
            if (child == null) {
                continue;
            }
            soFar.append(i);
            getAllWordsWithPrefixUtil(child, prefix, strings, soFar);
            soFar.setLength(soFar.length() - 1);
        }
    }

}