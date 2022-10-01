package constants;

public enum Client {
    UBOAT, ALLIE, AGENT, UNAUTHORIZED;


    public static Client getClient(String client) {
        switch (client) {
            case "uboat":
                return UBOAT;
            case "allie":
                return ALLIE;
            case "agent":
                return AGENT;
            default:
                return UNAUTHORIZED;
        }
    }

}



