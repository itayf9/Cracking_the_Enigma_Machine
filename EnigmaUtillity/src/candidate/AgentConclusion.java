package candidate;

import candidate.Candidate;

import java.util.List;

public class AgentConclusion {

    private List<Candidate> candidates;
    private int numOfScannedConfigurations;
    private long timeTakenToDoTask;

    public AgentConclusion(List<Candidate> candidates, int numOfScannedConfigurations, long timeTakenToDoTask) {
        this.candidates = candidates;
        this.numOfScannedConfigurations = numOfScannedConfigurations;
        this.timeTakenToDoTask = timeTakenToDoTask;
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
