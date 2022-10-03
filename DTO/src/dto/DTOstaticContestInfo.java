package dto;

import allie.AllieInfo;
import battlefield.BattlefieldInfo;
import problem.Problem;

import java.util.List;

public class DTOstaticContestInfo extends DTOstatus {

    private List<AllieInfo> alliesInfo;
    private BattlefieldInfo battlefieldInfo;

    public DTOstaticContestInfo(boolean isSucceed, Problem details, List<AllieInfo> alliesInfo, BattlefieldInfo battlefieldInfo) {
        super(isSucceed, details);
        this.alliesInfo = alliesInfo;
        this.battlefieldInfo = battlefieldInfo;
    }

    public List<AllieInfo> getAlliesInfo() {
        return alliesInfo;
    }

    public BattlefieldInfo getBattlefieldInfo() {
        return battlefieldInfo;
    }
}
