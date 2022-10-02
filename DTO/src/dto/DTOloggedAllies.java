package dto;

import problem.Problem;

import java.util.Set;

public class DTOloggedAllies extends DTOstatus {

    private Set<String> loggedAllies;

    public DTOloggedAllies(boolean isSucceed, Problem details, Set<String> loggedAllies) {
        super(isSucceed, details);
        this.loggedAllies = loggedAllies;
    }

    public Set<String> getLoggedAllies() {
        return loggedAllies;
    }
}
