package bindings;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.StringProperty;

public class InUsePlugsBinding extends StringBinding {

    private StringProperty inUsePlugsProperty;

    public InUsePlugsBinding(StringProperty inUsePlugsProperty) {
        this.inUsePlugsProperty = inUsePlugsProperty;
        bind(inUsePlugsProperty);
    }

    @Override
    protected String computeValue() {

        StringBuilder plugsBuilder = new StringBuilder();

        if (inUsePlugsProperty.getValue().length() > 0) {
            plugsBuilder.append("<");
            for (int i = 0; i < inUsePlugsProperty.getValue().length(); i += 2) {
                plugsBuilder.append(inUsePlugsProperty.getValue().charAt(i))
                        .append("|")
                        .append(inUsePlugsProperty.getValue().charAt(i + 1));
                if (i != inUsePlugsProperty.getValue().length() - 2) {
                    plugsBuilder.append(",");
                }
            }
            plugsBuilder.append(">");
        }

        return plugsBuilder.toString();
    }
}
