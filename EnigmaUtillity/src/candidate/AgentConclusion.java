package candidate;

import java.util.List;

public class AgentConclusion {

    private final List<Candidate> candidates;
    private final int numOfScannedConfigurations;
    private final String agentName;
    private final String allieName;

    public AgentConclusion(List<Candidate> candidates, int numOfScannedConfigurations, String agentName, String allieName) {
        this.candidates = candidates;
        this.numOfScannedConfigurations = numOfScannedConfigurations;
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
}
