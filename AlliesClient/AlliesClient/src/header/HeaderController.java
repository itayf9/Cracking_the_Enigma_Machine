package header;

import app.MainController;

public class HeaderController {

    private MainController mainController;

    // will need to add logout button if we want to implement bonus
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
