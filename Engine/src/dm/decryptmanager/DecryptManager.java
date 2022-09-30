package dm.decryptmanager;

import battlefield.Battlefield;
import candidate.Candidate;
import dm.agent.AgentConclusion;
import dm.candidatecollector.CandidatesCollector;
import dm.dictionary.Dictionary;
import dm.difficultylevel.DifficultyLevel;
import dm.taskproducer.TaskProducer;
import engine.Engine;
import engine.EnigmaEngine;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import machine.Machine;
import ui.adapter.UIAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static utill.Utility.factorial;
import static utill.Utility.nCk;

public class DecryptManager {


    private BlockingQueue<AgentConclusion> candidatesQueue;
    private ThreadPoolExecutor threadExecutor;
    private Thread collector;
    private final List<Candidate> allCandidates = new ArrayList<>();


    private Thread taskProducer;

    private final Machine enigmaMachine;
    private final Dictionary dictionary;
    private DifficultyLevel difficultyLevel;


    private long totalPossibleConfigurations;
    private final long totalPossibleWindowsPositions;
    private LongProperty totalTimeDecryptProperty;


    private final BlockingQueue<Runnable> waitingTasksBlockingQueue;
    private int taskSize;
    private String allieName;
    private BlockingQueue<AgentConclusion> uboatCandidateQueue;
    private final BooleanProperty isBruteForceActionCancelled;
    private final BooleanProperty isBruteForceActionPaused;

    public DecryptManager(String name, Dictionary dictionary, Machine machine) {
        int LIMIT_NUMBER_OF_TASK = 1000;
        this.waitingTasksBlockingQueue = new LinkedBlockingQueue<>(LIMIT_NUMBER_OF_TASK);
        this.dictionary = battlefield.getDictionary();
        this.enigmaMachine = battlefield.getMachine();
        this.difficultyLevel = battlefield.getDifficultyLevel();
        this.allieName = allieName;
        this.uboatCandidateQueue = battlefield.getUboatCandidatesQueue();
        this.totalPossibleWindowsPositions = (long) Math.pow(enigmaMachine.getAlphabet().length(), enigmaMachine.getRotorsCount());
        this.totalTimeDecryptProperty = new SimpleLongProperty();
        this.isBruteForceActionCancelled = new SimpleBooleanProperty(false);
        this.isBruteForceActionPaused = new SimpleBooleanProperty(false);
    }

    /**
     * pauses execution
     */
    public void pauseDecrypt() {
        synchronized (isBruteForceActionPaused) {
            isBruteForceActionPaused.setValue(true);
        }
    }

    /**
     * resume the execution after being paused
     */
    public void resumeDecrypt() {
        synchronized (isBruteForceActionPaused) {
            isBruteForceActionPaused.setValue(false);
            isBruteForceActionPaused.notifyAll();
        }
    }

    /**
     * cancel the bruteForce execution
     */
    public void stopDecrypt() {
        isBruteForceActionPaused.setValue(false);

        // stopping the thread pool
        isBruteForceActionCancelled.set(true);
        threadExecutor.shutdownNow();

        //  stopping the collector Task / Thread
        collector.interrupt();

        // stopping the producer Thread;
        taskProducer.interrupt();
    }

    /**
     * initiates the thread needed to start the brute force process
     *
     * @param taskSize            size of task per thread to execute
     * @param numOfSelectedAgents num of threads in pool
     * @param textToDecipher      the text that agents trying to cipher
     * @param difficultyLevel     difficulty label
     * @param uiAdapter           ui adapter to update the ui
     */
    public void startDecrypt(int taskSize, int numOfSelectedAgents, String textToDecipher, DifficultyLevel difficultyLevel
            , UIAdapter uiAdapter) {

        this.candidatesQueue = new LinkedBlockingQueue<>();
        isBruteForceActionCancelled.set(false);

        totalTimeDecryptProperty.setValue(System.nanoTime());

        // updates the total configs property
        setTotalConfigs(difficultyLevel);

        // setting up the collector of the candidates
        collector = new Thread(new CandidatesCollector(candidatesQueue, totalPossibleConfigurations, uiAdapter,
                totalTimeDecryptProperty, isBruteForceActionCancelledProperty(), isBruteForceActionPaused));
        collector.setName("THE_COLLECTOR");

        // starting the thread pool
        threadExecutor = new ThreadPoolExecutor(numOfSelectedAgents, numOfSelectedAgents,
                0L, TimeUnit.MILLISECONDS, threadPoolBlockingQueue);

        // setting up thr thread factory for the thread pool
        threadExecutor.setThreadFactory(new ThreadFactory() {

            private int nameCounter = 0;

            @Override
            public Thread newThread(Runnable r) {
                nameCounter++;
                return new Thread(r, String.valueOf(nameCounter));
            }
        });

        // setting a thread that produces tasks
        taskProducer = new Thread(new TaskProducer(this, taskSize, difficultyLevel, textToDecipher));
        taskProducer.setName("TASK_PRODUCER");

        // trigger the threads
        threadExecutor.prestartAllCoreThreads();

        taskProducer.start(); // thread is in the air starting the missions spread.
        collector.start();
        // main thread ends here
    }

    private void setTotalConfigs(DifficultyLevel difficultyLevel) {

        switch (difficultyLevel) {
            case EASY:
                totalPossibleConfigurations = (totalPossibleWindowsPositions);
                break;
            case MEDIUM:
                totalPossibleConfigurations = (totalPossibleWindowsPositions * enigmaMachine.getAvailableReflectorsLen());
                break;
            case HARD:
                totalPossibleConfigurations = (totalPossibleWindowsPositions *
                        enigmaMachine.getAvailableReflectorsLen() *
                        factorial(enigmaMachine.getRotorsCount()));
                break;
            case IMPOSSIBLE:
                totalPossibleConfigurations = (totalPossibleWindowsPositions *
                        enigmaMachine.getAvailableReflectorsLen() *
                        factorial(enigmaMachine.getRotorsCount()) *
                        nCk(enigmaMachine.getAvailableRotorsLen(), enigmaMachine.getRotorsCount())
                );
                break;
        }
    }

    public List<Candidate> getDecipherCandidates() {
        return allCandidates;
    }

    public Set<String> getDictionaryWords() {
        return dictionary.getWords();
    }

    public Machine getEnigmaMachine() {
        return enigmaMachine;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public BlockingQueue<AgentConclusion> getCandidatesQueue() {
        return candidatesQueue;
    }

    public BlockingQueue<Runnable> getThreadPoolBlockingQueue() {
        return threadPoolBlockingQueue;
    }

    public boolean isIsBruteForceActionCancelled() {
        return isBruteForceActionCancelled.get();
    }

    public BooleanProperty isBruteForceActionCancelledProperty() {
        return isBruteForceActionCancelled;
    }

    public boolean getIsBruteForceActionPaused() {
        return isBruteForceActionPaused.get();
    }

    public BooleanProperty isBruteForceActionPausedProperty() {
        return isBruteForceActionPaused;
    }

    public String getDictionaryExcludeCharacters() {
        return dictionary.getExcludeChars();
    }

    public boolean isAllWordsInDictionary(String text) {
        return dictionary.isAllWordsInDictionary(text);
    }

    public void setTaskSize(int taskSize) {
        this.taskSize = taskSize;
    }
}
