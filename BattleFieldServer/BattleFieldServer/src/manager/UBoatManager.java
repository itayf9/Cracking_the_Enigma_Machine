package manager;

import java.util.HashSet;
import java.util.Set;

public class UBoatManager {

    private Set<String> uboatNames;

    public UBoatManager() {
        this.uboatNames = new HashSet<>();
    }

    public Set<String> getUboatNames() {
        return uboatNames;
    }

    public void addUboatName(String username) {
        uboatNames.add(username);
    }

    public boolean isUboatExists(String username) {
        return uboatNames.contains(username);
    }


}
