package battlefield;

import info.agent.AgentInfo;
import info.allie.AllieInfo;
import candidate.AgentConclusion;
import dm.decryptmanager.DecryptManager;
import dictionary.Dictionary;
import difficultylevel.DifficultyLevel;
import machine.Machine;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private AtomicBoolean isActive;

    private AllieInfo winnerAllieInfo;
    private String textToDecipher;
    private boolean isBattlefieldConfigured;

    public Battlefield() {
        this.uboatCandidatesQueue = new LinkedBlockingQueue<>();
        this.allies = new HashSet<>();
        this.battlefieldName = "";
        this.isUboatReady = false;
        this.isActive = new AtomicBoolean(false);
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
        System.out.println(allies);
        allies.add(new DecryptManager(allieName, this, agentName2agentInfo));
        System.out.println(allies);
    }

    public AtomicBoolean isActive() {
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

    public String getTextToDecipher() {
        return textToDecipher;
    }

    public void setTextToDecipher(String textToDecipher) {
        this.textToDecipher = textToDecipher;
    }
}
