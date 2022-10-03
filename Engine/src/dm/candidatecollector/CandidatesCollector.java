package dm.candidatecollector;

import agent.AgentInfo;
import candidate.AgentConclusion;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import jobprogress.JobProgressInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class CandidatesCollector implements Runnable {

    private final BlockingQueue<AgentConclusion> agentReportsOfCandidateQueue;
    BlockingQueue<AgentConclusion> uboatCandidateQueue;
    private final long totalPossibleConfigurations;
    private final List<AgentConclusion> allConclusions;
    private LongProperty totalTimeDecryptProperty;
    private final BooleanProperty isBruteForceActionPaused;
    private final BooleanProperty isBruteForceActionCancelled;
    private JobProgressInfo jobProgressInfo;
    private Map<String, AgentInfo> agentName2agentInfo;

    private long pauseMeasuring;

    public CandidatesCollector(BlockingQueue<AgentConclusion> agentReportsOfCandidateQueue, long totalPossibleConfigurations,
                               LongProperty totalTimeDecryptProperty, BooleanProperty isBruteForceActionCancelled,
                               BooleanProperty isBruteForceActionPaused, BlockingQueue<AgentConclusion> uboatCandidateQueue,
                               List<AgentConclusion> allConclusions, JobProgressInfo jobProgressInfo, Map<String, AgentInfo> agentName2agentInfo) {
        this.agentReportsOfCandidateQueue = agentReportsOfCandidateQueue;
        this.uboatCandidateQueue = uboatCandidateQueue;
        this.totalPossibleConfigurations = totalPossibleConfigurations;
        this.totalTimeDecryptProperty = totalTimeDecryptProperty;
        this.isBruteForceActionPaused = isBruteForceActionPaused;
        this.isBruteForceActionCancelled = isBruteForceActionCancelled;
        this.jobProgressInfo = jobProgressInfo;
        this.agentName2agentInfo = agentName2agentInfo;
        this.pauseMeasuring = 0;
        this.allConclusions = allConclusions;
    }

    @Override
    public void run() {

        long totalTasksProcessTime = 0;
        long scannedConfigsCount = 0;
        long tasksCounter = 0;
        double averageTasksProcessTime;

        while (scannedConfigsCount < totalPossibleConfigurations && !isBruteForceActionCancelled.get()) {
            AgentConclusion currentConclusion;
            try {
                currentConclusion = agentReportsOfCandidateQueue.take();
                tasksCounter++;
                jobProgressInfo.setNumberOfTasksDone(tasksCounter);
                totalTasksProcessTime += currentConclusion.getTimeTakenToDoTask();
                averageTasksProcessTime = (double) totalTasksProcessTime / (double) tasksCounter;
                scannedConfigsCount += currentConclusion.getNumOfScannedConfigurations();

            } catch (InterruptedException e) {
                return;
            }

            if (currentConclusion.getCandidates().size() != 0) {
                String agentName = currentConclusion.getAgentName();
                agentName2agentInfo.get(agentName).updateNumOfFoundCandidate(currentConclusion.getCandidates().size());
                allConclusions.add(currentConclusion);

                // pushing the conclusion back to the uboat queue
                try {
                    uboatCandidateQueue.put(currentConclusion);
                } catch (InterruptedException ignored) {

                }
            }
        }
    }
}
