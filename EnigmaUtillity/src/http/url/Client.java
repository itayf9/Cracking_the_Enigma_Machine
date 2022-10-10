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
            case "allie":
                return ALLIE;
            case "agent":
                return AGENT;
            default:
                return UNAUTHORIZED;
        }
    }

    public String getClientTypeAsString() {

        switch (this) {
            case UBOAT:
                return "uboat";
            case ALLIE:
                return "allie";
            case AGENT:
                return "agent";
            default:
                return "";
        }
    }

}



