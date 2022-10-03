package jobprogress;

public class JobProgressInfo {
    private long totalAmountOfTasks;
    private long numberOfTasksProduced;
    private long numberOfTasksDone;

    public JobProgressInfo() {
        this.totalAmountOfTasks = 0;
        this.numberOfTasksProduced = 0;
        this.numberOfTasksDone = 0;
    }

    public long getTotalAmountOfTasks() {
        return totalAmountOfTasks;
    }

    public void setTotalAmountOfTasks(long totalAmountOfTasks) {
        this.totalAmountOfTasks = totalAmountOfTasks;
    }

    public long getNumberOfTasksProduced() {
        return numberOfTasksProduced;
    }

    public void setNumberOfTasksProduced(long numberOfTasksProduced) {
        this.numberOfTasksProduced = numberOfTasksProduced;
    }

    public long getNumberOfTasksDone() {
        return numberOfTasksDone;
    }

    public void setNumberOfTasksDone(long numberOfTasksDone) {
        this.numberOfTasksDone = numberOfTasksDone;
    }
}
