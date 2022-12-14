package info.agent;

public class AgentInfo {

    private String agentName;
    private final int numOfThreads;
    private final int numOfTasksPerPull;
    private int numOfReceivedTasks;
    private int numOfFoundCandidates;


    public AgentInfo(String agentName, int numOfThreads, int numOfTasksPerPull) {
        this.agentName = agentName;
        this.numOfThreads = numOfThreads;
        this.numOfTasksPerPull = numOfTasksPerPull;
        this.numOfReceivedTasks = 0;
        this.numOfFoundCandidates = 0;
    }

    public String getAgentName() {
        return agentName;
    }

    public int getNumOfThreads() {
        return numOfThreads;
    }

    public int getNumOfTasksPerPull() {
        return numOfTasksPerPull;
    }

    public int getNumOfReceivedTasks() {
        return numOfReceivedTasks;
    }

    public int getNumOfFoundCandidates() {
        return numOfFoundCandidates;
    }

    public void updateNumOfFoundCandidate(int newFoundCandidates) {
        numOfFoundCandidates += newFoundCandidates;
    }

    public void updateNumOfReceivedTasks(int newReceivedTasks) {
        numOfReceivedTasks += newReceivedTasks;
    }

    public void resetDynamicInfo() {
        numOfReceivedTasks = 0;
        numOfFoundCandidates = 0;
    }
}
