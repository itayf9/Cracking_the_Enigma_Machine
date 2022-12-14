package dm.decryptmanager;

import info.agent.AgentInfo;
import battlefield.Battlefield;
import candidate.AgentConclusion;
import dm.candidatecollector.CandidatesCollector;
import dictionary.Dictionary;
import difficultylevel.DifficultyLevel;
import dm.taskproducer.TaskProducer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import jobprogress.JobProgressInfo;
import machine.Machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static utill.Utility.factorial;
import static utill.Utility.nCk;

public class DecryptManager {

    private BlockingQueue<AgentConclusion> agentReportsOfCandidatesQueue;
    private Thread collector;
    private final List<AgentConclusion> allConclusions;
    private final BlockingQueue<AgentConclusion> allBlockingQueueConclusions;
    private Thread taskProducer;
    private final Machine enigmaMachine;
    private final Dictionary dictionary;
    private final DifficultyLevel difficultyLevel;
    private long totalPossibleConfigurations;
    private final long totalPossibleWindowsPositions;
    private final BlockingQueue<Runnable> waitingTasksBlockingQueue;
    private int taskSize;
    private final String allieName;
    private boolean isDMReady;
    private final JobProgressInfo jobProgressInfo;
    private final BlockingQueue<AgentConclusion> uboatCandidateQueue;
    private final Map<String, AgentInfo> agentName2agentInfo;
    private final StringProperty textToDecipher;
    private final BooleanProperty isContestActive;
    private final String uboatName;

    public DecryptManager(String allieName, Battlefield battlefield, Map<String, AgentInfo> agentName2agentInfo) {
        this.agentName2agentInfo = agentName2agentInfo;
        final int LIMIT_NUMBER_OF_TASK = 1000;
        this.waitingTasksBlockingQueue = new LinkedBlockingQueue<>(LIMIT_NUMBER_OF_TASK);
        this.dictionary = battlefield.getDictionary();
        this.enigmaMachine = battlefield.getMachine();
        this.difficultyLevel = battlefield.getDifficultyLevel();
        this.allieName = allieName;
        this.uboatCandidateQueue = battlefield.getUboatCandidatesQueue();
        this.totalPossibleWindowsPositions = (long) Math.pow(enigmaMachine.getAlphabet().length(), enigmaMachine.getRotorsCount());
        this.isDMReady = false;
        int UNDEFINED = 0;
        this.taskSize = UNDEFINED;
        this.jobProgressInfo = new JobProgressInfo();
        this.textToDecipher = battlefield.getTextToDecipherProperty();
        this.isContestActive = battlefield.isActive();
        this.uboatName = battlefield.getUboatName();
        setTotalConfigs(difficultyLevel);
        this.allConclusions = new ArrayList<>();
        this.allBlockingQueueConclusions = new LinkedBlockingQueue<>();
    }

    public BooleanProperty getIsContestActive() {
        return isContestActive;
    }

    /**
     * cancel the bruteForce execution
     */
    public void stopDecrypt() {

        // makes the dm unready, for the next contest.
        setDMReady(false);

        //  stopping the collector Task / Thread
        collector.interrupt();

        // stopping the producer Thread;
        taskProducer.interrupt();
    }

    /**
     * initiates the thread needed to start the brute force process
     */
    public void startDecrypt() {
        this.agentReportsOfCandidatesQueue = new LinkedBlockingQueue<>();

        // setting up the collector of the candidates
        collector = new Thread(new CandidatesCollector(this));
        collector.setName("THE_COLLECTOR");

        // setting a thread that produces tasks
        taskProducer = new Thread(new TaskProducer(this));
        taskProducer.setName("TASK_PRODUCER");

        taskProducer.start();
        collector.start();
    }

    private void setTotalConfigs(DifficultyLevel difficultyLevel) {

        switch (difficultyLevel) {
            case EASY:
                totalPossibleConfigurations = (totalPossibleWindowsPositions);
                break;
            case MEDIUM:
                totalPossibleConfigurations = (totalPossibleWindowsPositions * enigmaMachine.getAvailableReflectorsLen());
                break;
            case HARD:
                totalPossibleConfigurations = (totalPossibleWindowsPositions *
                        enigmaMachine.getAvailableReflectorsLen() *
                        factorial(enigmaMachine.getRotorsCount()));
                break;
            case IMPOSSIBLE:
                totalPossibleConfigurations = (totalPossibleWindowsPositions *
                        enigmaMachine.getAvailableReflectorsLen() *
                        factorial(enigmaMachine.getRotorsCount()) *
                        nCk(enigmaMachine.getAvailableRotorsLen(), enigmaMachine.getRotorsCount())
                );
                break;
        }
    }

    public Machine getEnigmaMachine() {
        return enigmaMachine;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public BlockingQueue<AgentConclusion> getCandidatesQueue() {
        return agentReportsOfCandidatesQueue;
    }

    public BlockingQueue<Runnable> getWaitingTasksBlockingQueue() {
        return waitingTasksBlockingQueue;
    }

    public String getAllieName() {
        return allieName;
    }

    public int getTaskSize() {
        return taskSize;
    }

    public long getTotalPossibleWindowsPositions() {
        return totalPossibleWindowsPositions;
    }

    public void setTaskSize(int taskSize) {
        this.taskSize = taskSize;
        jobProgressInfo.setTotalAmountOfTasks(totalPossibleConfigurations / taskSize);
    }

    public void setDMReady(boolean isReady) {
        this.isDMReady = isReady;
    }

    public boolean getDMReady() {
        return isDMReady;
    }

    public JobProgressInfo getJobProgressInfo() {
        return jobProgressInfo;
    }

    public List<AgentConclusion> getAllConclusions() {
        return allConclusions;
    }

    public BlockingQueue<AgentConclusion> getAllBlockingQueueConclusions() {
        return allBlockingQueueConclusions;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public StringProperty getTextToDecipherProperty() {
        return textToDecipher;
    }

    public long getTotalPossibleConfigurations() {
        return totalPossibleConfigurations;
    }

    public BlockingQueue<AgentConclusion> getUboatCandidateQueue() {
        return uboatCandidateQueue;
    }

    public Map<String, AgentInfo> getAgentName2agentInfo() {
        return agentName2agentInfo;
    }

    public void resetJobStatusInfo() {
        jobProgressInfo.setTotalAmountOfTasks(0);
        jobProgressInfo.setNumberOfTasksProduced(0);
        jobProgressInfo.setNumberOfTasksDone(0);
    }

    public String getUboatName() {
        return uboatName;
    }

    public List<AgentConclusion> getNextConclusions() {
        List<AgentConclusion> nextConclusions = new ArrayList<>();

        while (!allBlockingQueueConclusions.isEmpty()) {

            AgentConclusion conclusion = allBlockingQueueConclusions.poll();
            if (conclusion != null) {
                nextConclusions.add(conclusion);
            } else {
                break;
            }
        }
        return nextConclusions;
    }
}
