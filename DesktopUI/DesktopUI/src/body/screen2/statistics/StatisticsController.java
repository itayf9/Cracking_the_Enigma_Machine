//package body.screen2.statistics;
//
//import body.BodyController;
//import dto.DTOstatistics;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.layout.Region;
//import javafx.scene.layout.VBox;
//import javafx.util.Pair;
//import statistics.StatisticRecord;
//import utill.Utility;
//
//import java.util.List;
//
//
//public class StatisticsController {
//
//    private BodyController parentController;
//
//    @FXML
//    private VBox vBoxHistory;
//
//    public void setParentController(BodyController parentController) {
//        this.parentController = parentController;
//    }
//
//    public void displayStats(DTOstatistics stats) {
//
//        vBoxHistory.getChildren().clear();
//        if (stats.getStats().size() == 0) {
//            Label nextSecret = new Label("No statistics yet.");
//            vBoxHistory.getChildren().add(nextSecret);
//        }
//
//        for (StatisticRecord record : stats.getStats()) {
//            VBox nextRecord = new VBox();
//            nextRecord.getStyleClass().add("record-vbox");
//            nextRecord.setMaxWidth(Integer.MAX_VALUE);
//            nextRecord.setFillWidth(true);
//
//            Label configTitle = new Label("Configuration:");
//            configTitle.getStyleClass().add("sub-title");
//
//            ScrollPane nextSecretPane = new ScrollPane();
//
//            Label nextSecret = new Label(assembleConfiguration(record.getInUseRotors(), record.getWindowCharacters(),
//                    Utility.decimalToRoman(record.getReflectorID()), record.getPlugs(), record.getOriginalNotchPositions()));
//
//            nextSecretPane.setContent(nextSecret);
//            nextSecretPane.setMaxSize(250, 30);
//
//            Label textTitle = new Label("Ciphered Texts:");
//            textTitle.getStyleClass().add("sub-title");
//
//            nextRecord.getChildren().add(configTitle);
//            nextRecord.getChildren().add(nextSecretPane);
//            nextRecord.getChildren().add(textTitle);
//            TableView<TableViewStatistic> cipherRecordTable;
//
//            // create new table of history only if the list of history has elements
//            if (record.getCipherHistory().size() != 0) {
//                cipherRecordTable = new TableView<>();
//                cipherRecordTable.setPrefHeight(Region.USE_COMPUTED_SIZE);
//                cipherRecordTable.setMinWidth(250);
//                cipherRecordTable.setMaxWidth(250);
//
//                TableColumn<TableViewStatistic, String> inputColumn = new TableColumn<>("Input");
//                inputColumn.setCellValueFactory(new PropertyValueFactory<>("input"));
//                inputColumn.setSortable(false);
//                inputColumn.setMinWidth(50);
//                TableColumn<TableViewStatistic, String> outputColumn = new TableColumn<>("Output");
//                outputColumn.setCellValueFactory(new PropertyValueFactory<>("output"));
//                outputColumn.setSortable(false);
//                outputColumn.setMinWidth(50);
//                TableColumn<TableViewStatistic, String> timeTakenColumn = new TableColumn<>("Time Taken (ns)");
//                timeTakenColumn.setCellValueFactory(new PropertyValueFactory<>("timeTaken"));
//                timeTakenColumn.setSortable(false);
//                timeTakenColumn.setMinWidth(50);
//
//                cipherRecordTable.getColumns().addAll(inputColumn, outputColumn, timeTakenColumn);
//
//                for (Pair<Pair<String, String>, Long> currentCipherRecord : record.getCipherHistory()) {
//                    Pair<String, String> pairCipher = currentCipherRecord.getKey();
//                    long time = currentCipherRecord.getValue();
//                    cipherRecordTable.getItems().add(new TableViewStatistic(pairCipher, time));
//                }
//                nextRecord.getChildren().add(cipherRecordTable);
//            } else {
//                Label noHistoryLabel = new Label("None.");
//                nextRecord.getChildren().add(noHistoryLabel);
//            }
//
//            vBoxHistory.getChildren().add(nextRecord);
//
//            //for (Pair<Pair<String, String>, Long> currentCipherRecord : record.getCipherHistory()) {
//
//            //   cipherRecordTable.getItems().add(currentCipherRecord);
//
//
//                /*StringBuilder cipherRecordStr = new StringBuilder();
//
//                cipherRecordStr.append("#. ")
//                        .append("<")
//                        .append(currentCipherRecord.getKey().getKey())
//                        .append(">").append(" --> ").append("<")
//                        .append(currentCipherRecord.getKey().getValue()).append(">")
//                        .append(" (").append(currentCipherRecord.getValue()).append(" nano-seconds)");
//
//                Label newCipherHistory = new Label(cipherRecordStr.toString());
//                vBoxHistory.getChildren().add(newCipherHistory);*/
//
//            // }
//        }
//
//    }
//
//    private String assembleConfiguration(List<Integer> inUseRotorsIDs, String windowsCharacters, String inUseReflectorSymbol, String inUsePlugs, List<Integer> notchDistancesToWindow) {
//        StringBuilder strConfig = new StringBuilder();
//
//        // prints rotors' configuration
//        // including the in use rotors' IDs, their order, and the current notch distances from the windows
//        strConfig.append("<");
//        for (int i = inUseRotorsIDs.size() - 1; i >= 0; i--) {
//            strConfig.append(inUseRotorsIDs.get(i).toString());
//            if (i != 0) {
//                strConfig.append(",");
//            }
//        }
//        strConfig.append(">");
//
//        // prints windows characters configuration
//        strConfig.append("<");
//        for (int i = windowsCharacters.length() - 1; i >= 0; i--) {
//            strConfig.append(windowsCharacters.charAt(i));
//            if (notchDistancesToWindow.size() > 0) {
//                strConfig.append("(")
//                        .append(notchDistancesToWindow.get(i).toString())
//                        .append(")");
//            }
//        }
//        strConfig.append(">");
//
//        // prints reflector configuration
//        strConfig.append("<")
//                .append(inUseReflectorSymbol)
//                .append(">");
//
//        // prints plugs configuration
//        if (inUsePlugs.length() > 0) {
//            strConfig.append("<");
//            for (int i = 0; i < inUsePlugs.length(); i += 2) {
//                strConfig.append(inUsePlugs.charAt(i))
//                        .append("|")
//                        .append(inUsePlugs.charAt(i + 1));
//                if (i != inUsePlugs.length() - 2) {
//                    strConfig.append(",");
//                }
//            }
//            strConfig.append(">");
//        }
//
//        return strConfig.toString();
//    }
//}
