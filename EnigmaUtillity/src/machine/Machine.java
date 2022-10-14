package machine;

import machine.component.Reflector;
import machine.component.Rotor;

import java.util.List;

public interface Machine {

    /**
     * finds a rotor by a specific id
     *
     * @param id an unique id of a rotor
     * @return the rotor with that id
     */
    Rotor getRotorByID(Integer id);

    /**
     * updating the current config of the machine.
     * by sending the updated list of rotors, reflectors and plugs.
     *
     * @param rotorsIDs     a list of rotors id's
     * @param windowOfssets a list of thw current offsets of the rotors
     * @param reflectorID   the reflector id as an integer
     * @param plugs         a String of plugs, without spaces or separators
     */
    void setMachineConfiguration(List<Integer> rotorsIDs, List<Integer> windowOfssets, int reflectorID, String plugs);

    /**
     * cipher one character in the machine.
     * based on: input->plugs->rotors-reflector-rotors-plugs->output
     *
     * @param srcChar the character to cipher
     * @return the ciphered character
     */
    char cipher(char srcChar);

    /**
     * @return true is the machine is configured, false otherwise
     */
    boolean isConfigured();

    /**
     * @return a string that represents the alphabet
     */
    String getAlphabet();

    /**
     * @return the size of the rotors that is currently allowed to be in machine
     */
    int getRotorsCount();

    /**
     * @return the size of the list of Available Reflectors in the machine
     */
    int getAvailableReflectorsLen();

    /**
     * @return the size of the list of Available Rotors in the machine
     */
    int getAvailableRotorsLen();

    /**
     * @return a list of the ids of the in-use rotors, maintaining their order.
     */
    List<Integer> getInUseRotorsIDs();

    /**
     * getting the original notch distances from the window, of the in use rotors in machine
     *
     * @return a list of original notch distances to the windows
     */
    List<Integer> getOriginalNotchPositions();

    /**
     * @return a list of the alphabet letters, for each rotor, that are currently at the 'window' (meaning that the absolute position is 0)
     */
    String getOriginalWindowsCharacters();

    /**
     * getting the current notch distances from the window, of the in use rotors in machine
     *
     * @return a list of notch distances to the windows
     */
    List<Integer> getInUseNotchDistanceToWindow();

    /**
     * @return the reflector that is currently used in the machine.
     */
    Reflector getInUseReflector();

    /**
     * @return a list of pairs of characters that are plugged in the plug board.
     */
    String getAllPlugPairs();

    /**
     * @return a list of window offsets of the in used rotors
     */
    List<Integer> getInUseWindowsOffsets();

    /**
     * @return a list of all rotors that are currently used in the machine, preserving their order.
     * (right most rotor is the first rotor in the list).
     */
    List<Rotor> getInUseRotors();

    /**
     * gets the current window characters.
     *
     * @return a String representing the window characters in the machine
     */
    String getCurrentWindowsCharacters();

    /**
     * increases the amount of ciphered texts that the machine has ciphered so far, by one.
     */
    void advanceCipherCounter();

    /**
     * @return how many ciphered texts the machine has been ciphering so far
     */
    int getCipherCounter();

    void saveCopyOfMachine(String fileName);

    Machine getCopyOfMachine(String fileName);
}
