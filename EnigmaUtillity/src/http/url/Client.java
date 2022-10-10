package http.url;

public enum Client {
    UBOAT, ALLIE, AGENT, UNAUTHORIZED;


    public static Client getClientTypeFromString(String clientName) {
        if (clientName == null) {
            return UNAUTHORIZED;
        }

        switch (clientName) {
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



