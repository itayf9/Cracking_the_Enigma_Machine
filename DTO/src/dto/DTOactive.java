package dto;

import problem.Problem;

public class DTOactive extends DTOstatus {

    private boolean isActive;

    public DTOactive(boolean isSucceed, Problem details, boolean isActive) {
        super(isSucceed, details);
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }
}
