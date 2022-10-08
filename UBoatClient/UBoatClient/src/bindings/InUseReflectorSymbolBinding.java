package bindings;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.StringProperty;

public class InUseReflectorSymbolBinding extends StringBinding {

    private StringProperty inUseReflectorSymbolProperty;

    public InUseReflectorSymbolBinding(StringProperty inUseReflectorSymbolProperty) {
        this.inUseReflectorSymbolProperty = inUseReflectorSymbolProperty;
        bind(inUseReflectorSymbolProperty);
    }

    @Override
    protected String computeValue() {

        String reflectorSymbolStr = "";

        if (inUseReflectorSymbolProperty.getValue().length() > 0) {
            reflectorSymbolStr = "<" + inUseReflectorSymbolProperty.getValue() + ">";
        }
        return reflectorSymbolStr;
    }
}
