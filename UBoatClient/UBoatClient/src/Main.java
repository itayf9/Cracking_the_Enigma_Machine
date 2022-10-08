import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("UBoat");

        Parent load = FXMLLoader.load(getClass().getResource("app/app.fxml"));
        Scene scene = new Scene(load, 900, 625);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}