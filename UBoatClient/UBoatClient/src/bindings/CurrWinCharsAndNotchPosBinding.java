package bindings;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;

public class CurrWinCharsAndNotchPosBinding extends StringBinding {

    private StringProperty currentWindowsCharactersProperty;
    private ListProperty<Integer> currentNotchPositionsProperty;

    // ctor!!
    public CurrWinCharsAndNotchPosBinding(StringProperty currentWindowsCharactersProperty, ListProperty<Integer> currentNotchPositionsProperty) {
        this.currentWindowsCharactersProperty = currentWindowsCharactersProperty;
        this.currentNotchPositionsProperty = currentNotchPositionsProperty;
        bind(currentWindowsCharactersProperty, currentNotchPositionsProperty);
    }

    @Override
    protected String computeValue() {

        StringBuilder reversedWindows = new StringBuilder();

        for (int i = currentWindowsCharactersProperty.getValue().length() - 1; i >= 0; i--) {
            reversedWindows.append(currentWindowsCharactersProperty.getValue().charAt(i));

            if (currentNotchPositionsProperty.size() > 0) {
                reversedWindows.append("(")
                        .append(currentNotchPositionsProperty.get(i).toString())
                        .append(")");
            }
        }

        if (reversedWindows.toString().length() != 0) {
            return "<" + reversedWindows.toString() + ">";
        }
        return reversedWindows.toString();
    }
}

