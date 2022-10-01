package dm.taskproducer;

import candidate.AgentConclusion;
import dm.agent.AgentTask;
import dm.decryptmanager.DecryptManager;
import dm.dictionary.Dictionary;
import dm.difficultylevel.DifficultyLevel;
import machine.EnigmaMachine;
import machine.Machine;

import java.util.*;
import java.util.concurrent.BlockingQueue;


public class TaskProducer implements Runnable {

    protected BlockingQueue<Runnable> agentTaskQueue;
    private final Machine machine;
    private final String alphabet;
    private final int taskSize;
    private final DifficultyLevel difficulty;
    private final String textToDecipher;
    private final Dictionary dictionary;
    private final BlockingQueue<AgentConclusion> candidatesQueue;
    private final DecryptManager dm;


    public TaskProducer(DecryptManager dm, int taskSize, DifficultyLevel difficultyLevel, String textToDecipher) {
        this.dm = dm;
        this.agentTaskQueue = dm.getWaitingTasksBlockingQueue();
        this.machine = dm.getEnigmaMachine();
        this.alphabet = machine.getAlphabet();
        this.taskSize = taskSize;
        this.difficulty = difficultyLevel;
        this.textToDecipher = textToDecipher;
        this.dictionary = dm.getDictionary();
        this.candidatesQueue = dm.getCandidatesQueue();
    }

    public void run() {
        // set the tasks and send them to the pool
        switch (difficulty) {
            case EASY:
                produceEasyTasks(machine.getInUseReflector().getId(), machine.getInUseRotorsIDs());
                break;
            case MEDIUM:
                produceMediumTasks(machine.getInUseRotorsIDs());
                break;
            case HARD:
                produceHardTasks(machine.getInUseRotorsIDs());
                break;
            case IMPOSSIBLE:
                produceImpossibleTasks();
                break;
        }
    }

    private void produceImpossibleTasks() {
        List<List<Integer>> listOfAllCombinationsRotorsIDsFixed = generateCombinations(machine.getAvailableRotorsLen(), machine.getRotorsCount());
        for (List<Integer> combination : listOfAllCombinationsRotorsIDsFixed) {
            produceHardTasks(combination);
        }
    }

    private void produceHardTasks(List<Integer> rotorsIDs) {

        List<List<Integer>> listOfAllPermutationsRotorsIDs = permute(new ArrayList<>(rotorsIDs));

        for (List<Integer> permutation : listOfAllPermutationsRotorsIDs) {
            produceMediumTasks(permutation);
        }
    }

    private void produceMediumTasks(List<Integer> rotorsIDs) {

        int numOfReflectors = machine.getAvailableReflectorsLen();

        for (int i = 1; i <= numOfReflectors; i++) {
            produceEasyTasks(i, rotorsIDs);
        }
    }

    private void produceEasyTasks(int reflectorID, List<Integer> rotorsIDs) {

        List<Integer> currentWindowsOffsets = new ArrayList<>(Collections.nCopies(machine.getRotorsCount(), 0));
        boolean finishedAllTasks = false;
        // easy mode so rotors & reflector doesn't change.

        List<Integer> nextWindowsOffsets;
        Machine copyOfMachine = new EnigmaMachine((EnigmaMachine) machine); // Clone!

        // set up first agentTask
        try {
            agentTaskQueue.put(new AgentTask(rotorsIDs, new ArrayList<>(currentWindowsOffsets), reflectorID,
                    copyOfMachine, dm, taskSize, textToDecipher, dictionary, candidatesQueue));
        } catch (InterruptedException ignored) {
            //throw new RuntimeException(e);
        }

        while (!finishedAllTasks && !dm.isIsBruteForceActionCancelled()) {

            // first clone a machine to send to the agent
            copyOfMachine = new EnigmaMachine((EnigmaMachine) machine);

            // the next window characters to set for the agent, based on last window characters
            nextWindowsOffsets = getNextWindowsOffsets(taskSize, currentWindowsOffsets);
            if (AllWindowsOffsetsAtBeginning(nextWindowsOffsets)) {
                finishedAllTasks = true;
                continue;
            }

            // replace current list with next list
            currentWindowsOffsets.clear();
            currentWindowsOffsets.addAll(nextWindowsOffsets);

            try {
                agentTaskQueue.put(new AgentTask(rotorsIDs, nextWindowsOffsets, reflectorID,
                        copyOfMachine, dm, taskSize, textToDecipher, dictionary, candidatesQueue));
            } catch (InterruptedException e) {
                // producer Stopped so need to die
                return;
            }
        }
    }

    public List<List<Integer>> generateCombinations(int n, int k) {
        ArrayList<Integer> combination = new ArrayList<>();

        // initialize with the lowest lexicographic combination
        for (int i = 0; i < k; i++) {
            combination.add(i);
        }

        List<List<Integer>> combinations = new ArrayList<>();
        while (combination.get(k - 1) < n) {
            combinations.add((List<Integer>) combination.clone());

            // generate next combination in lexicographic order
            int t = k - 1;
            while (t != 0 && combination.get(t) == n - k + t) {
                t--;
            }
            combination.set(t, combination.get(t) + 1);
            for (int i = t + 1; i < k; i++) {
                combination.set(i, combination.get(i - 1) + 1);
            }
        }

        for (List<Integer> updatedCombination : combinations) {
            updatedCombination.replaceAll(integer -> integer + 1);
        }

        return combinations;
    }

    public List<List<Integer>> permute(List<Integer> nums) {
        List<List<Integer>> results = new ArrayList<>();
        if (nums == null || nums.size() == 0) {
            return results;
        }
        List<Integer> result = new ArrayList<>();
        dfs(nums, results, result);
        return results;
    }

    public void dfs(List<Integer> nums, List<List<Integer>> results, List<Integer> result) {
        if (nums.size() == result.size()) {
            List<Integer> temp = new ArrayList<>(result);
            results.add(temp);
        }
        for (int i = 0; i < nums.size(); i++) {
            if (!result.contains(nums.get(i))) {
                result.add(nums.get(i));
                dfs(nums, results, result);
                result.remove(result.size() - 1);
            }
        }
    }

    private List<Integer> getNextWindowsOffsets(int taskSize, List<Integer> currentWindowsOffsets) {
        List<Integer> nextWindowsOffsets = new ArrayList<>(currentWindowsOffsets);

        for (int i = 0; i < taskSize; i++) {

            for (int j = 0; j < nextWindowsOffsets.size(); j++) {
                nextWindowsOffsets.set(j, rotateWindow(nextWindowsOffsets.get(j)));

                // check if it is needed to rotate next rotor
                if (nextWindowsOffsets.get(j) != 0) {
                    break;
                }
            }

            if (AllWindowsOffsetsAtBeginning(nextWindowsOffsets)) {
                return nextWindowsOffsets;
            }
        }
        return nextWindowsOffsets;
    }

    private int rotateWindow(Integer windowOffset) {
        return (windowOffset + 1 + alphabet.length()) % alphabet.length();
    }

    private boolean AllWindowsOffsetsAtBeginning(List<Integer> nextWindowsOffsets) {
        return nextWindowsOffsets.stream().allMatch(offset -> offset == 0);
    }
}