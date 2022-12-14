package dto;

import dictionary.Dictionary;
import problem.Problem;

import java.util.List;

public class DTOspecs extends DTOstatus {
    private int availableRotorsCount;
    private int inUseRotorsCount;
    private List<Integer> notchDistancesToWindow;
    private List<Integer> originalNotchPositions;

    private int availableReflectorsCount;
    private int cipheredTextsCount;

    private List<Integer> inUseRotorsIDs;
    private String originalWindowsCharacters;
    private String currentWindowsCharacters;
    private String inUseReflectorSymbol;
    private String inUsePlugs;
    private String dictionaryExcludeCharacters;
    private String machineAlphabet;
    private Dictionary dictionary;

    public DTOspecs(boolean isSucceeded, Problem details, int availableRotorsCount, int inUseRotorsCount,
                    List<Integer> notchDistancesToWindow, List<Integer> originalNotchPositions,
                    int availableReflectorsCount, int cipheredTextsCount, List<Integer> inUseRotorsIDs,
                    String originalWindowsCharacters, String currentWindowsCharacters,
                    String inUseReflectorSymbol, String dictionaryExcludeCharacters,
                    String inUsePlugs, String machineAlphabet, Dictionary dictionary) {
        super(isSucceeded, details);
        this.availableRotorsCount = availableRotorsCount;
        this.inUseRotorsCount = inUseRotorsCount;
        this.notchDistancesToWindow = notchDistancesToWindow;
        this.originalNotchPositions = originalNotchPositions;
        this.availableReflectorsCount = availableReflectorsCount;
        this.cipheredTextsCount = cipheredTextsCount;
        this.inUseRotorsIDs = inUseRotorsIDs;
        this.originalWindowsCharacters = originalWindowsCharacters;
        this.currentWindowsCharacters = currentWindowsCharacters;
        this.inUseReflectorSymbol = inUseReflectorSymbol;
        this.inUsePlugs = inUsePlugs;
        this.dictionaryExcludeCharacters = dictionaryExcludeCharacters;
        this.machineAlphabet = machineAlphabet;
        this.dictionary = dictionary;
    }

    public int getAvailableRotorsCount() {
        return availableRotorsCount;
    }

    public int getInUseRotorsCount() {
        return inUseRotorsCount;
    }

    public List<Integer> getNotchDistancesToWindow() {
        return notchDistancesToWindow;
    }

    public List<Integer> getOriginalNotchPositions() {
        return originalNotchPositions;
    }

    public int getAvailableReflectorsCount() {
        return availableReflectorsCount;
    }

    public int getCipheredTextsCount() {
        return cipheredTextsCount;
    }

    public List<Integer> getInUseRotorsIDs() {
        return inUseRotorsIDs;
    }

    public String getOriginalWindowsCharacters() {
        return originalWindowsCharacters;
    }

    public String getCurrentWindowsCharacters() {
        return currentWindowsCharacters;
    }

    public String getInUseReflectorSymbol() {
        return inUseReflectorSymbol;
    }

    public String getInUsePlugs() {
        return inUsePlugs;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }


    public String getDictionaryExcludeCharacters() {
        return dictionaryExcludeCharacters;
    }

    public String getMachineAlphabet() {
        return machineAlphabet;
    }

    @Override
    public String toString() {
        return "DTOspecs{" +
                "availableRotorsCount=" + availableRotorsCount +
                ", inUseRotorsCount=" + inUseRotorsCount +
                ", notchDistancesToWindow=" + notchDistancesToWindow +
                ", originalNotchPositions=" + originalNotchPositions +
                ", availableReflectorsCount=" + availableReflectorsCount +
                ", cipheredTextsCount=" + cipheredTextsCount +
                ", inUseRotorsIDs=" + inUseRotorsIDs +
                ", originalWindowsCharacters='" + originalWindowsCharacters + '\'' +
                ", currentWindowsCharacters='" + currentWindowsCharacters + '\'' +
                ", inUseReflectorSymbol='" + inUseReflectorSymbol + '\'' +
                ", inUsePlugs='" + inUsePlugs + '\'' +
                ", dictionaryExcludeCharacters='" + dictionaryExcludeCharacters + '\'' +
                ", machineAlphabet='" + machineAlphabet + '\'' +
                '}';
    }
}
