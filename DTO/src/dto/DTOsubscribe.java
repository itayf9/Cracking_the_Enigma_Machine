package dto;

import problem.Problem;

public class DTOsubscribe extends DTOstatus {

    private final long totalPossibleWindowsPositions;

    public DTOsubscribe(boolean isSucceed, Problem details, long totalPossibleWindowsPositions) {
        super(isSucceed, details);
        this.totalPossibleWindowsPositions = totalPossibleWindowsPositions;
    }

    public long getTotalPossibleWindowsPositions() {
        return totalPossibleWindowsPositions;
    }
}
