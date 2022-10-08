package dto;

import problem.Problem;

import java.util.List;

public class DTOciphertext extends DTOstatus {

    private String cipheredText;
    private String currentWindowsCharacters;
    private List<Integer> currentNotchDistances;
    private int cipherCounter;

    public DTOciphertext(boolean isSucceed, Problem details, String cipheredText, String currentWindowsCharacters, List<Integer> currentNotchDistances, int cipherCounter) {
        super(isSucceed, details);
        this.cipheredText = cipheredText;
        this.currentWindowsCharacters = currentWindowsCharacters;
        this.currentNotchDistances = currentNotchDistances;
        this.cipherCounter = cipherCounter;
    }

    public String getCipheredText() {
        return cipheredText;
    }

    public String getCurrentWindowsCharacters() {
        return currentWindowsCharacters;
    }

    public List<Integer> getCurrentNotchDistances() {
        return currentNotchDistances;
    }

    public int getCipherCounter() {
        return cipherCounter;
    }
}
