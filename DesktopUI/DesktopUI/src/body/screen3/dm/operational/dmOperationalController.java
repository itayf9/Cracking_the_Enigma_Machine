//package body.screen3.dm.operational;
//
//import app.statusbar.MessageTone;
//import body.BodyController;
//import dm.difficultylevel.DifficultyLevel;
//import dto.DTOspecs;
//import javafx.beans.binding.Bindings;
//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.LongProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.StringProperty;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.MouseEvent;
//
//import static utill.Utility.factorial;
//import static utill.Utility.nCk;
//
//public class dmOperationalController {
//
//    private BodyController parentController;
//
//    @FXML
//    private Slider numOfAgentsSlider;
//
//    @FXML
//    private ComboBox<DifficultyLevel> difficultyLevelComboBox;
//
//    @FXML
//    private Spinner<Integer> taskSizeSpinner;
//
//    @FXML
//    private Button startButton;
//
//    @FXML
//    private Button pauseButton;
//
//    @FXML
//    private Label numOfAgentLabel;
//
//    @FXML
//    private Label tasksAmountLabel;
//
//    private LongProperty totalPossibleWindowsPositions;
//    private StringProperty textToDecipherProperty;
//    private BooleanProperty isBruteForceTaskActive;
//    private BooleanProperty isBruteForceTaskPaused;
//
//    private DTOspecs specStatus;
//
//    private ImageView startButtonIcon = new ImageView("/resource/buttonicons/play-solid.png");
//    private ImageView pauseButtonIcon = new ImageView("/resource/buttonicons/pause-solid.png");
//    private ImageView stopButtonIcon = new ImageView("/resource/buttonicons/stop-solid.png");
//    private ImageView resumeButtonIcon = new ImageView("/resource/buttonicons/resume-play.png");
//
//    @FXML
//    public void initialize() {
//        difficultyLevelComboBox.getItems().add(DifficultyLevel.EASY);
//        difficultyLevelComboBox.getItems().add(DifficultyLevel.MEDIUM);
//        difficultyLevelComboBox.getItems().add(DifficultyLevel.HARD);
//        difficultyLevelComboBox.getItems().add(DifficultyLevel.IMPOSSIBLE);
//        difficultyLevelComboBox.setPromptText("Please Select");
//        this.textToDecipherProperty = new SimpleStringProperty();
//        numOfAgentLabel.textProperty().bind(Bindings.concat("Number Of Agents: ", Bindings.format("%.0f", numOfAgentsSlider.valueProperty())));
//
//        taskSizeSpinner.valueProperty().addListener((observable, oldValue, newValue) -> displayTasksAmount(newValue, difficultyLevelComboBox.getValue()));
//        difficultyLevelComboBox.valueProperty().addListener((observable, oldValue, newValue) -> displayTasksAmount(taskSizeSpinner.getValue(), newValue));
//
//        startButtonIcon.setFitWidth(12);
//        startButtonIcon.setFitHeight(12);
//        pauseButtonIcon.setFitWidth(12);
//        pauseButtonIcon.setFitHeight(12);
//        stopButtonIcon.setFitWidth(12);
//        stopButtonIcon.setFitHeight(12);
//        resumeButtonIcon.setFitWidth(12);
//        resumeButtonIcon.setFitHeight(12);
//
//        startButton.setContentDisplay(ContentDisplay.BOTTOM);
//        pauseButton.setContentDisplay(ContentDisplay.BOTTOM);
//    }
//
//    @FXML
//    void handleBruteForceStartStopOperationAction(MouseEvent event) {
//
//        if (isBruteForceTaskActive.getValue()) {
//            stopBruteForceAction();
//        } else {
//            startBruteForceAction();
//        }
//    }
//
//    @FXML
//    void handleBruteForcePauseResumeOperationAction(MouseEvent event) {
//
//        if (isBruteForceTaskPaused.getValue()) {
//            resumeBruteForceAction();
//        } else {
//            pauseBruteForceAction();
//        }
//    }
//
//    void pauseBruteForceAction() {
//        parentController.pauseBruteForce();
//    }
//
//    void resumeBruteForceAction() {
//        parentController.resumeBruteForce();
//    }
//
//    void stopBruteForceAction() {
//        parentController.stopBruteForce();
//    }
//
//    void startBruteForceAction() {
//        DifficultyLevel difficultyLevel = difficultyLevelComboBox.getValue();
//        int taskSize = taskSizeSpinner.getValue(); // need to fix text value not updating if no button pressed
//        int numOfAgentSelected = (int) numOfAgentsSlider.getValue();
//        String textToDecipher = textToDecipherProperty.getValue();
//
//        // clear text from exclude characters
//
//        if (difficultyLevel == null) {
//            parentController.setStatusMessage("Please enter a difficulty level", MessageTone.ERROR);
//            return;
//        }
//
//        if (textToDecipher.equals("")) {
//            parentController.setStatusMessage("Please cipher some text", MessageTone.ERROR);
//            return;
//        }
//
//        parentController.startBruteForce(textToDecipher, difficultyLevel, taskSize, numOfAgentSelected);
//
//    }
//
//    public void setSettings(LongProperty totalPossibleWindowsPositions, int maxNumOfAgents, DTOspecs specStatus) {
//        this.specStatus = specStatus;
//        taskSizeSpinner.valueFactoryProperty().set(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, (totalPossibleWindowsPositions.getValue().intValue())));
//        numOfAgentsSlider.setMax(maxNumOfAgents);
//        this.totalPossibleWindowsPositions = totalPossibleWindowsPositions;
//
//        taskSizeSpinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
//            try {
//                int newValueAsInt = Integer.parseInt(newValue);
//                if (newValueAsInt > 0 && newValueAsInt <= totalPossibleWindowsPositions.getValue()) {
//                    taskSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, totalPossibleWindowsPositions.getValue().intValue(), newValueAsInt));
//                } else {
//                    taskSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, totalPossibleWindowsPositions.getValue().intValue(), Integer.parseInt(oldValue)));
//                    taskSizeSpinner.getEditor().setText(oldValue);
//                }
//            } catch (NumberFormatException e) {
//                taskSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, totalPossibleWindowsPositions.getValue().intValue(), Integer.parseInt(oldValue)));
//                taskSizeSpinner.getEditor().setText(oldValue);
//            }
//        });
//    }
//
//    public void bindTextToDecipherPropertyToOutputCipher(StringProperty cipheredText) {
//        textToDecipherProperty.bind(cipheredText);
//    }
//
//    public void setParentController(BodyController parentController) {
//        this.parentController = parentController;
//    }
//
//    public void bindComponents(BooleanProperty isBruteForceTaskActive, BooleanProperty isBruteForceTaskPaused) {
//        this.isBruteForceTaskActive = isBruteForceTaskActive;
//        this.isBruteForceTaskPaused = isBruteForceTaskPaused;
//        taskSizeSpinner.disableProperty().bind(isBruteForceTaskActive);
//        numOfAgentsSlider.disableProperty().bind(isBruteForceTaskActive);
//        difficultyLevelComboBox.disableProperty().bind(isBruteForceTaskActive);
//        startButton.textProperty().bind(Bindings.when(isBruteForceTaskActive.not()).then("Start").otherwise("Stop"));
//        startButton.graphicProperty().bind(Bindings.when(isBruteForceTaskActive.not()).then(startButtonIcon).otherwise(stopButtonIcon));
//        pauseButton.disableProperty().bind(Bindings.when(isBruteForceTaskActive.not()).then(true).otherwise(false));
//        pauseButton.textProperty().bind(Bindings.when(isBruteForceTaskPaused.not().or(isBruteForceTaskActive.not())).then("Pause").otherwise("Resume"));
//        pauseButton.graphicProperty().bind(Bindings.when(isBruteForceTaskPaused.not().or(isBruteForceTaskActive.not())).then(pauseButtonIcon).otherwise(resumeButtonIcon));
//    }
//
//    public void displayTasksAmount(int taskSize, DifficultyLevel difficultyLevel) {
//
//        long totalPossibleConfigurations = 0;
//
//        if (difficultyLevel == null) {
//            tasksAmountLabel.setText("");
//            return;
//        }
//
//        switch (difficultyLevel) {
//            case EASY:
//                totalPossibleConfigurations = (totalPossibleWindowsPositions.getValue());
//                break;
//            case MEDIUM:
//                totalPossibleConfigurations = (totalPossibleWindowsPositions.getValue() * specStatus.getAvailableReflectorsCount());
//                break;
//            case HARD:
//                totalPossibleConfigurations = (totalPossibleWindowsPositions.getValue() *
//                        specStatus.getAvailableReflectorsCount() *
//                        factorial(specStatus.getInUseRotorsCount()));
//                break;
//            case IMPOSSIBLE:
//                totalPossibleConfigurations = (totalPossibleWindowsPositions.getValue() *
//                        specStatus.getAvailableReflectorsCount() *
//                        factorial(specStatus.getInUseRotorsCount()) *
//                        (nCk(specStatus.getAvailableRotorsCount(), specStatus.getInUseRotorsCount()))
//                );
//                break;
//        }
//
//        long numOfTasks;
//        if (totalPossibleConfigurations % taskSize != 0) {
//            numOfTasks = (totalPossibleConfigurations / taskSize) + 1;
//        } else {
//            numOfTasks = totalPossibleConfigurations / taskSize;
//        }
//
//        tasksAmountLabel.setText(String.valueOf(numOfTasks));
//
//    }
//}
