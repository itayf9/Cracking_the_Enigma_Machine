package info.allie;

public class AllieInfo {

    private String allieName;
    private int numOfAgents;
    private int taskSize;

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
