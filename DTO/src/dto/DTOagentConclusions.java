package dto;

import candidate.AgentConclusion;
import problem.Problem;

import java.util.List;

public class DTOagentConclusions extends DTOstatus {

    private List<AgentConclusion> agentConclusions;

    public DTOagentConclusions(boolean isSucceed, Problem details, List<AgentConclusion> agentConclusions) {
        super(isSucceed, details);
        this.agentConclusions = agentConclusions;
    }

    public List<AgentConclusion> getAgentConclusions() {
        return agentConclusions;
    }
}
