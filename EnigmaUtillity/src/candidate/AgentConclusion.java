package candidate;

import java.util.List;

public class AgentConclusion {

    private List<Candidate> candidates;
    private int numOfScannedConfigurations;
    private long timeTakenToDoTask;

    public AgentConclusion(List<Candidate> candidates, int numOfScannedConfigurations, long timeTakenToDoTask, String agentName, String allieName) {
        this.candidates = candidates;
        this.numOfScannedConfigurations = numOfScannedConfigurations;
        this.timeTakenToDoTask = timeTakenToDoTask;
        this.agentName = agentName;
        this.allieName = allieName;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public int getNumOfScannedConfigurations() {
        return numOfScannedConfigurations;
    }

    public long getTimeTakenToDoTask() {
        return timeTakenToDoTask;
    }
}
