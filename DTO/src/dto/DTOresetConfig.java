package dto;

import problem.Problem;

import java.util.List;

public class DTOresetConfig extends DTOstatus {

    private String currentWindowsCharacters;
    private List<Integer> currentNotchDistances;

    public DTOresetConfig(boolean isSucceed, Problem details, String currentWindowsCharacters, List<Integer> currentNotchDistances) {
        super(isSucceed, details);
        this.currentWindowsCharacters = currentWindowsCharacters;
        this.currentNotchDistances = currentNotchDistances;
    }

    public String getCurrentWindowsCharacters() {
        return currentWindowsCharacters;
    }

    public List<Integer> getCurrentNotchDistances() {
        return currentNotchDistances;
    }
}
