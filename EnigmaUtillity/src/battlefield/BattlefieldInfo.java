package battlefield;

import difficultylevel.DifficultyLevel;

import static difficultylevel.DifficultyLevel.UNDEFINED;

public class BattlefieldInfo {
    private String battleName;
    private String uboatName;
    private boolean isActive;
    private DifficultyLevel difficultyLevel;
    private int numOfRequiredAllies;
    private int numOfLoggedAllies;
    private String textToDecipher;

    public BattlefieldInfo() {
        this.battleName = "";
        this.uboatName = "";
        this.isActive = false;
        this.difficultyLevel = UNDEFINED;
        this.numOfRequiredAllies = -1;
        this.numOfLoggedAllies = -1;
        this.textToDecipher = "";
    }

    public BattlefieldInfo(String battleName, String uboatName, boolean isActive, DifficultyLevel difficultyLevel, int numOfRequiredAllies,
                           int numOfLoggedAllies, String textToDecipher) {
        this.battleName = battleName;
        this.uboatName = uboatName;
        this.isActive = isActive;
        this.difficultyLevel = difficultyLevel;
        this.numOfRequiredAllies = numOfRequiredAllies;
        this.numOfLoggedAllies = numOfLoggedAllies;
        this.textToDecipher = textToDecipher;
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

    public String getTextToDecipher() {
        return textToDecipher;
    }
}
