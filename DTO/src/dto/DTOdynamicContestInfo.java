package dto;

import info.agent.AgentInfo;
import candidate.AgentConclusion;
import jobprogress.JobProgressInfo;
import problem.Problem;

import java.util.List;
import java.util.Set;

public class DTOdynamicContestInfo extends DTOstatus {
    private Set<AgentInfo> agentsInfo;
    private List<AgentConclusion> allCandidates;
    private JobProgressInfo jobProgressInfo;

    public DTOdynamicContestInfo(boolean isSucceed, Problem details, Set<AgentInfo> agentsInfo, List<AgentConclusion> allCandidates, JobProgressInfo jobProgressInfo) {
        super(isSucceed, details);
        this.agentsInfo = agentsInfo;
        this.allCandidates = allCandidates;
        this.jobProgressInfo = jobProgressInfo;
    }

    public Set<AgentInfo> getAgentsInfo() {
        return agentsInfo;
    }

    public List<AgentConclusion> getAllCandidates() {
        return allCandidates;
    }

    public JobProgressInfo getJobStatus() {
        return jobProgressInfo;
    }
}
