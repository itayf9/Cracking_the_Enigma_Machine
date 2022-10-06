package info.agent;

public class AgentInfo {

    private String agentName;
    private int numOfThreads;
    private int numOfTasksPerPull;
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

    public void setNumOfReceivedTasks(int numOfReceivedTasks) {
        this.numOfReceivedTasks = numOfReceivedTasks;
    }

    public void setNumOfFoundCandidates(int numOfFoundCandidates) {
        this.numOfFoundCandidates = numOfFoundCandidates;
    }

    public void updateNumOfFoundCandidate(int newFoundCandidates) {
        numOfFoundCandidates += newFoundCandidates;
    }
}
