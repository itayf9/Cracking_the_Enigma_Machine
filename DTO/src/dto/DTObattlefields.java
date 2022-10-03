package dto;

import info.battlefield.BattlefieldInfo;
import problem.Problem;

import java.util.List;

public class DTObattlefields extends DTOstatus {

    private List<BattlefieldInfo> allBattlefields;

    public DTObattlefields(boolean isSucceed, Problem details, List<BattlefieldInfo> allBattlefields) {
        super(isSucceed, details);
        this.allBattlefields = allBattlefields;
    }

    public List<BattlefieldInfo> getAllBattlefields() {
        return allBattlefields;
    }
}
