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
        primaryStage.setTitle("Allie");

        Parent load1 = FXMLLoader.load(getClass().getResource("allies/login/login.fxml"));
        Scene loginScene = new Scene(load1, 300, 300);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }
}