package constants;

public enum Client {
    UBOAT, ALLIE, AGENT, UNAUTHORIZED;


    public static Client getClient(String client) {
        if (client == null) {
            return UNAUTHORIZED;
        }

        switch (client) {
            case "uboat":
                return UBOAT;
            case "info/allie":
                return ALLIE;
            case "info/agent":
                return AGENT;
            default:
                return UNAUTHORIZED;
        }
    }

}



