package dm;


public class DecryptManager {

    private Dictionary dictionary;
    private int numberOfAgents;

    private DifficultyLevel difficultyLevel;

    public DecryptManager(Dictionary dictionary, int numberOfAgents) {
        this.dictionary = dictionary;
        this.numberOfAgents = numberOfAgents;
    }
}
