package candidate;

import java.util.List;

public class AgentConclusion {

    private List<Candidate> candidates;
    private int numOfScannedConfigurations;
    private long timeTakenToDoTask;
    private String agentName;
    private String allieName;

    public AgentConclusion(List<Candidate> candidates, int numOfScannedConfigurations, long timeTakenToDoTask, String agentName, String allieName) {
        this.candidates = candidates;
        this.numOfScannedConfigurations = numOfScannedConfigurations;
        this.timeTakenToDoTask = timeTakenToDoTask;
        this.agentName = agentName;
        this.allieName = allieName;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getAllieName() {
        return allieName;
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
