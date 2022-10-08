package info.allie;

public class AllieInfo {

    private final String allieName;
    private final int numOfAgents;
    private final int taskSize;

    public AllieInfo(String allieName, int numOfAgents, int taskSize) {
        this.allieName = allieName;
        this.numOfAgents = numOfAgents;
        this.taskSize = taskSize;
    }

    public String getAllieName() {
        return allieName;
    }

    public int getNumOfAgents() {
        return numOfAgents;
    }

    public int getTaskSize() {
        return taskSize;
    }
}
