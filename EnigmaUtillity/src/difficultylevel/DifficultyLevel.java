package difficultylevel;

public enum DifficultyLevel {
    EASY, MEDIUM, HARD, IMPOSSIBLE, UNDEFINED;

    public static DifficultyLevel getDifficultyLevelFromString(String difficultyString) {
        switch (difficultyString) {
            case "Easy":
                return EASY;
            case "Medium":
                return MEDIUM;
            case "Hard":
                return HARD;
            case "Insane":
                return IMPOSSIBLE;
            default:
                return UNDEFINED;
        }
    }
}
