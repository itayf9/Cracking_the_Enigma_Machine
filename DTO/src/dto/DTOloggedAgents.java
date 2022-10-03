package dto;

import info.agent.AgentInfo;
import problem.Problem;

import java.util.Set;

public class DTOloggedAgents extends DTOstatus {

    private Set<AgentInfo> loggedAgents;

    public DTOloggedAgents(boolean isSucceed, Problem details, Set<AgentInfo> loggedAgents) {
        super(isSucceed, details);
        this.loggedAgents = loggedAgents;
    }

    public Set<AgentInfo> getLoggedAgents() {
        return loggedAgents;
    }
}
