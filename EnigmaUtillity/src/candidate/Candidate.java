package candidate;

import java.util.List;

public class Candidate {

    private String decipheredText;
    private List<Integer> rotorsIDs;
    private String windowChars;
    private String reflectorSymbol;

    private List<Integer> notchPositions;


    public Candidate(String decipheredText, List<Integer> rotorsIDs, String windowChars, String reflectorSymbol, List<Integer> notchPositions) {
        this.decipheredText = decipheredText;
        this.rotorsIDs = rotorsIDs;
        this.reflectorSymbol = reflectorSymbol;
        this.windowChars = windowChars;
        this.notchPositions = notchPositions;
    }

    public String getDecipheredText() {
        return decipheredText;
    }

    public List<Integer> getRotorsIDs() {
        return rotorsIDs;
    }

    public String getWindowChars() {
        return windowChars;
    }

    public String getReflectorSymbol() {
        return reflectorSymbol;
    }

    public List<Integer> getNotchPositions() {
        return notchPositions;
    }
}
