package battlefield;

import difficultylevel.DifficultyLevel;

public class BattlefieldInfo {
    private String battleName;
    private String uboatName;
    private boolean isActive;
    private DifficultyLevel difficultyLevel;
    private int numOfRequiredAllies;
    private int numOfLoggedAllies;

    public BattlefieldInfo(String battleName, String uboatName, boolean isActive, DifficultyLevel difficultyLevel, int numOfRequiredAllies, int numOfLoggedAllies) {
        this.battleName = battleName;
        this.uboatName = uboatName;
        this.isActive = isActive;
        this.difficultyLevel = difficultyLevel;
        this.numOfRequiredAllies = numOfRequiredAllies;
        this.numOfLoggedAllies = numOfLoggedAllies;
    }

    public String getBattleName() {
        return battleName;
    }

    public String getUboatName() {
        return uboatName;
    }

    public boolean isActive() {
        return isActive;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public int getNumOfRequiredAllies() {
        return numOfRequiredAllies;
    }

    public int getNumOfLoggedAllies() {
        return numOfLoggedAllies;
    }
}
