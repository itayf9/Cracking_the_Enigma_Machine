package agent.screen1.candidate.tile;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class CandidateTileController {

    @FXML
    private Label decipheredTextLabel;

    @FXML
    private Label windowsCharsAndNotchesLabel;

    @FXML
    private Label rotorsIDsLabel;

    @FXML
    private Label reflectorSymbolLabel;

    @FXML
    private Label foundByAgentNameLabel;

    @FXML
    private Label foundByAllieNameLabel;

    public void setDecipheredText(String decipheredText) {
        decipheredTextLabel.setText(decipheredText);
    }

    public void setRotorsIDs(List<Integer> rotorsIDs) {

        StringBuilder rotorBuilder = new StringBuilder();

        rotorBuilder.append("<");
        for (int i = rotorsIDs.size() - 1; i >= 0; i--) {
            rotorBuilder.append(rotorsIDs.get(i).toString());

            if (i != 0) {
                rotorBuilder.append(",");
            }
        }
        rotorBuilder.append(">");

        rotorsIDsLabel.setText(rotorBuilder.toString());

    }

    public void setWindowsCharsAndNotches(String windowChars, List<Integer> currentNotchPositions) {

        StringBuilder reversedWindows = new StringBuilder();

        for (int i = windowChars.length() - 1; i >= 0; i--) {
            reversedWindows.append(windowChars.charAt(i));

            if (currentNotchPositions.size() > 0) {
                reversedWindows.append("(")
                        .append(currentNotchPositions.get(i).toString())
                        .append(")");
            }
        }

        if (reversedWindows.toString().length() != 0) {
            windowsCharsAndNotchesLabel.setText("<" + reversedWindows.toString() + ">");
        }
        windowsCharsAndNotchesLabel.setText(reversedWindows.toString());

    }

    public void setReflectorSymbol(String reflectorSymbol) {
        reflectorSymbolLabel.setText("<" + reflectorSymbol + ">");
    }

    public void setProcessedByAgentName(String processedByAgentName) {
        foundByAgentNameLabel.setText("Agent: " + processedByAgentName);
    }

    public void setProcessedByAllieName(String processedByAgentName) {
        foundByAllieNameLabel.setText("Ally: " + processedByAgentName);
    }
}
