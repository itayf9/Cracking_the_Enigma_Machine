package agent;

public class AgentInfo {

    private String agentName;
    private int numOfThreads;
    private int numOfTasksPerPull;

    public AgentInfo(String agentName, int numOfThreads, int numOfTasksPerPull) {
        this.agentName = agentName;
        this.numOfThreads = numOfThreads;
        this.numOfTasksPerPull = numOfTasksPerPull;
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
}
