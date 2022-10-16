package dm.candidatecollector;

import info.agent.AgentInfo;
import candidate.AgentConclusion;
import dm.decryptmanager.DecryptManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import jobprogress.JobProgressInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class CandidatesCollector implements Runnable {

    private final BlockingQueue<AgentConclusion> agentReportsOfCandidateQueue;
    private BlockingQueue<AgentConclusion> uboatCandidateQueue;
    private final long totalPossibleConfigurations;
    private final List<AgentConclusion> allConclusions;
    private final BooleanProperty isContestActive;
    private JobProgressInfo jobProgressInfo;
    private Map<String, AgentInfo> agentName2agentInfo;

    public CandidatesCollector(DecryptManager dm) {
        this.agentReportsOfCandidateQueue = dm.getCandidatesQueue();
        this.uboatCandidateQueue = dm.getUboatCandidateQueue();
        this.totalPossibleConfigurations = dm.getTotalPossibleConfigurations();
        this.isContestActive = dm.getIsContestActive();
        this.jobProgressInfo = dm.getJobProgressInfo();
        this.agentName2agentInfo = dm.getAgentName2agentInfo();
        this.allConclusions = dm.getAllConclusions();
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
