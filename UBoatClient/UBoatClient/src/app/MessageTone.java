package app;

import javafx.collections.ObservableList;

public enum MessageTone {

    SUCCESS, ERROR, INFO;

    public String colorClassOfMessage() {
        switch (this) {
            case SUCCESS:
                return "success-message-area";
            case ERROR:
                return "error-message-area";
            case INFO:
                return "info-message-area";
            default:
                return "info-message-area";
        }
    }

    public void removeAllStyleClassExcept(ObservableList<String> styleClass) {
        switch (this) {
            case SUCCESS:
                styleClass.removeAll(ERROR.colorClassOfMessage(), INFO.colorClassOfMessage());
                break;
            case ERROR:
                styleClass.removeAll(SUCCESS.colorClassOfMessage(), INFO.colorClassOfMessage());
                break;
            case INFO:
                styleClass.removeAll(SUCCESS.colorClassOfMessage(), ERROR.colorClassOfMessage());
                break;
        }
    }
}
