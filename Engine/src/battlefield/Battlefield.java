package battlefield;

import info.allie.AllieInfo;
import candidate.AgentConclusion;
import dm.decryptmanager.DecryptManager;
import dictionary.Dictionary;
import difficultylevel.DifficultyLevel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import machine.Machine;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Battlefield {

    // updated stuff
    private String uboatName;

    private Machine machine;
    private Set<DecryptManager> allies;
    private Dictionary dictionary;
    private String battlefieldName;
    private int numOfRequiredAllies;
    private DifficultyLevel difficultyLevel;
    private BlockingQueue<AgentConclusion> uboatCandidatesQueue;
    private boolean isUboatReady;
    private BooleanProperty isActive;

    private AllieInfo winnerAllieInfo;
    private StringProperty textToDecipher;
    private boolean isBattlefieldConfigured;

    public Battlefield(String uboatName) {
        this.uboatName = uboatName;
        this.uboatCandidatesQueue = new LinkedBlockingQueue<>();
        this.allies = new HashSet<>();
        this.battlefieldName = "";
        this.isUboatReady = false;
        this.isActive = new SimpleBooleanProperty(false);
        this.difficultyLevel = DifficultyLevel.UNDEFINED;
        this.isBattlefieldConfigured = false;
        this.textToDecipher = new SimpleStringProperty("");
    }

    public String getUboatName() {
        return uboatName;
    }

    public Machine getMachine() {
        return machine;
    }

    public Set<DecryptManager> getAllies() {
        return allies;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public String getBattlefieldName() {
        return battlefieldName;
    }

    public int getNumOfRequiredAllies() {
        return numOfRequiredAllies;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public boolean getIsUboatReady() {
        return isUboatReady;
    }

    public void setIsUboatReady(boolean isUboatReady) {
        this.isUboatReady = isUboatReady;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public void setAllies(Set<DecryptManager> allies) {
        this.allies = allies;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public BlockingQueue<AgentConclusion> getUboatCandidatesQueue() {
        return uboatCandidatesQueue;
    }

    public void setBattlefieldName(String battlefieldName) {
        this.battlefieldName = battlefieldName;
    }

    public void setNumOfRequiredAllies(int numOfRequiredAllies) {
        this.numOfRequiredAllies = numOfRequiredAllies;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public void addDecryptManager(DecryptManager allie) {
        System.out.println(allies);
        allies.add(allie);
        System.out.println(allies);
    }

    public BooleanProperty isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive.set(isActive);
    }

    public AllieInfo getWinnerAllieInfo() {
        return winnerAllieInfo;
    }

    public void setWinnerAllieInfo(AllieInfo winnerAllieInfo) {
        this.winnerAllieInfo = winnerAllieInfo;
    }

    public boolean isBattlefieldConfigured() {
        return isBattlefieldConfigured;
    }

    public void setBattlefieldConfigured(boolean battlefieldConfigured) {
        isBattlefieldConfigured = battlefieldConfigured;
    }

    public StringProperty getTextToDecipherProperty() {
        return textToDecipher;
    }

    public void setTextToDecipherValue(String textToDecipher) {
        this.textToDecipher.set(textToDecipher);
    }
}
