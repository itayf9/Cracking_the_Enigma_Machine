package dto;

import info.allie.AllieInfo;
import problem.Problem;

import java.util.List;

public class DTOallies extends DTOstatus {

    private List<AllieInfo> allies;

    public DTOallies(boolean isSucceed, Problem details, List<AllieInfo> allies) {
        super(isSucceed, details);
        this.allies = allies;
    }

    public List<AllieInfo> getAllies() {
        return allies;
    }
}
