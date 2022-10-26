package dm.candidatecollector;

import info.agent.AgentInfo;
import candidate.AgentConclusion;
import dm.decryptmanager.DecryptManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import jdk.nashorn.internal.ir.Block;
import jobprogress.JobProgressInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class CandidatesCollector implements Runnable {

    private final BlockingQueue<AgentConclusion> agentReportsOfCandidateQueue;
    private final BlockingQueue<AgentConclusion> uboatCandidateQueue;
    private final long totalPossibleConfigurations;
    private final List<AgentConclusion> allConclusions;
    private final BooleanProperty isContestActive;
    private final JobProgressInfo jobProgressInfo;
    private final Map<String, AgentInfo> agentName2agentInfo;
    private final BlockingQueue<AgentConclusion> allBlockingQueueConclusions;

    public CandidatesCollector(DecryptManager dm) {
        this.agentReportsOfCandidateQueue = dm.getCandidatesQueue();
        this.uboatCandidateQueue = dm.getUboatCandidateQueue();
        this.totalPossibleConfigurations = dm.getTotalPossibleConfigurations();
        this.isContestActive = dm.getIsContestActive();
        this.jobProgressInfo = dm.getJobProgressInfo();
        this.agentName2agentInfo = dm.getAgentName2agentInfo();
        this.allConclusions = dm.getAllConclusions();
        this.allBlockingQueueConclusions = dm.getAllBlockingQueueConclusions();
    }

    @Override
    public void run() {

        long scannedConfigsCount = 0;
        long tasksCounter = 0;

        while (scannedConfigsCount < totalPossibleConfigurations && isContestActive.get()) {
            AgentConclusion currentConclusion;
            try {
                currentConclusion = agentReportsOfCandidateQueue.take();
                tasksCounter++;
                jobProgressInfo.setNumberOfTasksDone(tasksCounter);
                scannedConfigsCount += currentConclusion.getNumOfScannedConfigurations();

            } catch (InterruptedException e) {
                return;
            }

            if (currentConclusion.getCandidates().size() != 0) {
                String agentName = currentConclusion.getAgentName();
                agentName2agentInfo.get(agentName).updateNumOfFoundCandidate(currentConclusion.getCandidates().size());

                // pushing the conclusion back to the dm queue
                try {
                    allBlockingQueueConclusions.put(currentConclusion);
                } catch (InterruptedException ignored) {

                }

                // pushing the conclusion back to the uboat queue
                try {
                    uboatCandidateQueue.put(currentConclusion);
                } catch (InterruptedException ignored) {

                }
            }
        }
    }
}
