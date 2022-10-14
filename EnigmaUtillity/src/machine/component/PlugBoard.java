package machine.component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PlugBoard implements Serializable, Cloneable {

    // key-value pairs representing the plugs.
    private Map<Integer, Integer> plugMap;

    /**
     * constructor for PlugBoard.
     * creates an empty Map<Integer, Integer> for plugMap.
     */
    public PlugBoard() {
        this.plugMap = new HashMap<>();
    }

    /**
     * copy constructor for PlugBoard
     *
     * @param otherPlugBoard the plug board to copy from
     */
    public PlugBoard(PlugBoard otherPlugBoard) {
        this.plugMap = new HashMap<>(otherPlugBoard.plugMap);
    }

    /**
     * @return a map of indices that represents the pairs of letters that are currently plugged.
     */
    public Map<Integer, Integer> getPlugMap() {
        return plugMap;
    }

    /**
     * finds out if a letter has a plug in the plug board.
     *
     * @param keyIndex index that represents a letter to be checked
     * @return 'true' if the letter is currently plugged, 'false' otherwise
     */
    public boolean isPlugged(int keyIndex) {
        return plugMap.containsKey(keyIndex);
    }


    /**
     * finds out the value of the matching letter, which is plugged to the given letter
     * assuming the pair exists in the plug board
     *
     * @param keyIndex index that represents a letter to be checked
     * @return index that represents the matching pair
     */
    public int getMatch(int keyIndex) {
        return plugMap.get(keyIndex);
    }


    /**
     * initializes the plug board mapping.
     *
     * @param character2index a map that translates each letter in the alphabet, to an index.
     * @param plugs           a string that represents the plug pairs.
     *                        There is no separation between pairs.
     *                        There is no division in a pair.
     *                        For example, "AZDE" means that there are two pairs.
     *                        A,Z and D,E.
     */
    public void initPlugBoardMap(Map<Character, Integer> character2index, String plugs) {

        plugMap.clear();
        // run through all plugs
        for (int i = 0; i < plugs.length(); i += 2) {
            // convert char to index
            int firstInPlugIndex = character2index.get(plugs.charAt(i));
            int secondInPlugIndex = character2index.get(plugs.charAt(i + 1));

            // build  plug map
            plugMap.put(secondInPlugIndex, firstInPlugIndex);
            plugMap.put(firstInPlugIndex, secondInPlugIndex);
        }
    }

    @Override
    public PlugBoard clone() throws CloneNotSupportedException {
        return new PlugBoard(this);
    }

    @Override
    public String toString() {
        return "PlugBoard{" +
                "plugMap=" + plugMap +
                '}';
    }

}
