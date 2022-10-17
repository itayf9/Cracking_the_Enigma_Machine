package uboat.bindings;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ListProperty;

public class InUseRotorsIDsBinding extends StringBinding {

    private ListProperty<Integer> inUseRotorsIDsProperty;

    public InUseRotorsIDsBinding(ListProperty<Integer> inUseRotorsIDsProperty) {
        this.inUseRotorsIDsProperty = inUseRotorsIDsProperty;
        bind(inUseRotorsIDsProperty);
    }

    @Override
    protected String computeValue() {

        StringBuilder rotorBuilder = new StringBuilder();

        if (inUseRotorsIDsProperty.size() > 0) {
            rotorBuilder.append("<");
            for (int i = inUseRotorsIDsProperty.size() - 1; i >= 0; i--) {
                rotorBuilder.append(inUseRotorsIDsProperty.get(i).toString());
                if (i != 0) {
                    rotorBuilder.append(",");
                }
            }
            rotorBuilder.append(">");

        }
        return rotorBuilder.toString();
    }
}
