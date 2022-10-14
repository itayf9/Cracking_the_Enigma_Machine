package dto;

import info.allie.AllieInfo;
import problem.Problem;

public class DTOwinner extends DTOstatus {

    private AllieInfo allieWinner;

    public DTOwinner(boolean isSucceed, Problem details, AllieInfo allieWinner) {
        super(isSucceed, details);
        this.allieWinner = allieWinner;
    }

    public AllieInfo getAllieWinner() {
        return allieWinner;
    }
}
