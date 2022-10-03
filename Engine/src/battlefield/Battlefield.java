package battlefield;

import agent.AgentInfo;
import candidate.AgentConclusion;
import dm.decryptmanager.DecryptManager;
import dictionary.Dictionary;
import difficultylevel.DifficultyLevel;
import machine.Machine;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class Battlefield {

    // updated stuff
    private Machine machine;
    private Set<DecryptManager> allies;
    private Dictionary dictionary;
    private String battlefieldName;
    private int numOfRequiredAllies;
    private DifficultyLevel difficultyLevel;
    private BlockingQueue<AgentConclusion> uboatCandidatesQueue;
    private boolean isUboatReady;
    private boolean isActive;
    private String textToDecipher;
    private boolean isBattlefieldConfigured;

    public Battlefield() {
        this.allies = new HashSet<>();
        this.battlefieldName = "";
        this.isUboatReady = false;
        this.isActive = false;
        this.difficultyLevel = DifficultyLevel.UNDEFINED;
        this.isBattlefieldConfigured = false;
        this.textToDecipher = "";
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

    public void setUboatCandidatesQueue(BlockingQueue<AgentConclusion> uboatCandidatesQueue) {
        this.uboatCandidatesQueue = uboatCandidatesQueue;
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

    public void addDecryptManager(String allieName, Map<String, AgentInfo> agentName2agentInfo) {
        allies.add(new DecryptManager(allieName, this, agentName2agentInfo));
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isBattlefieldConfigured() {
        return isBattlefieldConfigured;
    }

    public void setBattlefieldConfigured(boolean battlefieldConfigured) {
        isBattlefieldConfigured = battlefieldConfigured;
    }

    public String getTextToDecipher() {
        return textToDecipher;
    }

    public void setTextToDecipher(String textToDecipher) {
        this.textToDecipher = textToDecipher;
    }
}
