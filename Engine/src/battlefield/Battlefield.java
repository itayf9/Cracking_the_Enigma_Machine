package battlefield;

import candidate.AgentConclusion;
import dm.decryptmanager.DecryptManager;
import dm.dictionary.Dictionary;
import dm.difficultylevel.DifficultyLevel;
import machine.Machine;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class Battlefield {

    // updated stuff
    private Machine machine;
    private Set<DecryptManager> allies;
    private Dictionary dictionary;
    private String battlefieldName;
    private int numOfAllies;
    private DifficultyLevel difficultyLevel;
    private BlockingQueue<AgentConclusion> uboatCandidatesQueue;
    private boolean isUboatReady;

    public Battlefield() {
        this.allies = new HashSet<>();
        this.battlefieldName = "";
        this.isUboatReady = false;
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

    public int getNumOfAllies() {
        return numOfAllies;
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

    public void setNumOfAllies(int numOfAllies) {
        this.numOfAllies = numOfAllies;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public void addDecryptManager(String allieName) {
        allies.add(new DecryptManager(allieName, this));
    }
}
