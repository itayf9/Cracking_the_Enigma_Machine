package dm.agent;

import candidate.AgentConclusion;
import candidate.Candidate;
import dm.decryptmanager.DecryptManager;
import dm.dictionary.Dictionary;
import javafx.beans.property.BooleanProperty;
import machine.Machine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static utill.Utility.decimalToRoman;

public class AgentTask implements Runnable {

    private final Machine machine;
    private final int taskSize;
    private Dictionary dictionary;
    private final String textToDecipher;
    private final List<Integer> rotorsIDs;
    private final List<Integer> windowOffsets;
    private final int inUseReflectorID;
    private BlockingQueue<AgentConclusion> candidatesQueue;

    private BooleanProperty isIsBruteForceActionCancelledProperty;
    private String agentName;

    private String allieName;

    public AgentTask(List<Integer> rotorsIDs, List<Integer> windowOffsets, int inUseReflectorID, Machine copyOfMachine, DecryptManager dm) {
        this.machine = copyOfMachine;
        this.taskSize = dm.getTaskSize();
        this.textToDecipher = dm.getTextToDecipher();
        this.windowOffsets = windowOffsets;
        this.rotorsIDs = rotorsIDs;
        this.inUseReflectorID = inUseReflectorID;
        this.agentName = "";
        this.allieName = dm.getAllieName();

    }

    private String decipherLine(String LineToDecipher) {
        StringBuilder cipheredText = new StringBuilder();

        // goes through the character in the string
        for (Character currentChar : LineToDecipher.toCharArray()) {
            cipheredText.append(machine.cipher(currentChar));
        }

        return cipheredText.toString();
    }

    private void resetConfig() {
        for (int i = 0; i < machine.getRotorsCount(); i++) {

            int currentOffset = machine.getInUseWindowsOffsets().get(i);
            machine.getInUseRotors().get(i).rotateToOffset(currentOffset);
        }
    }

    private void advanceWindow() {

        for (int i = 0; i < windowOffsets.size(); i++) {
            windowOffsets.set(i, (windowOffsets.get(i) + 1 + machine.getAlphabet().length()) % machine.getAlphabet().length());

            // check if it is needed to rotate next rotor
            if (windowOffsets.get(i) != 0) {
                break;
            }
        }
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void setIsIsBruteForceActionCancelledProperty(boolean isIsBruteForceActionCancelledProperty) {
        this.isIsBruteForceActionCancelledProperty.set(isIsBruteForceActionCancelledProperty);
    }

    public void setCandidatesQueue(BlockingQueue<AgentConclusion> candidatesQueue) {
        this.candidatesQueue = candidatesQueue;
    }


    private boolean AllWindowsOffsetsAtBeginning() {
        for (Integer offset : windowOffsets) {
            if (offset != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void run() {
        long startMeasureTime = System.nanoTime();

        List<Candidate> candidates = new ArrayList<>();
        int numOfConfigScanned = 0;

        for (int i = 0; i < taskSize && !isIsBruteForceActionCancelledProperty.get(); i++) {
            numOfConfigScanned++;

            // sets machine to the next configuration
            // changes only the window offsets
            machine.setMachineConfiguration(rotorsIDs, windowOffsets, inUseReflectorID, "");

            // ciphers the text
            String decipherResult = decipherLine(textToDecipher);

            // check dictionary
            if (dictionary.isAllWordsInDictionary(decipherResult)) {
                // convert windows offsets to characters.
                String windowCharacters = machine.getOriginalWindowsCharacters(); // I trust this !

                // convert reflector ID to Roman number.
                String nextCandidateReflectorSymbol = decimalToRoman(inUseReflectorID);

                // fetch the notch positions
                List<Integer> notchPositions = machine.getOriginalNotchPositions(); // I trust this !

                // fetch the current thread's name
                String processedByAgentName = Thread.currentThread().getName();

                Candidate nextCandidate = new Candidate(decipherResult, rotorsIDs, windowCharacters, nextCandidateReflectorSymbol, notchPositions);
                candidates.add(nextCandidate);
            }

            resetConfig();

            // moves to the next configuration
            advanceWindow();

            // if we reached the end of the offset then we do not go any further
            if (AllWindowsOffsetsAtBeginning()) {
                break;
            }
        }
        // send conclusion to DM
        try {
            long timeElapsed = System.nanoTime() - startMeasureTime;
            candidatesQueue.put(new AgentConclusion(candidates, numOfConfigScanned, timeElapsed, agentName, allieName));
        } catch (InterruptedException ignored) {

        }
    }
}
