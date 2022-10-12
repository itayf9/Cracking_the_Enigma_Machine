public class Main {
    public static void main(String[] args) {
        Application.launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Allie");

        Parent load1 = FXMLLoader.load(getClass().getResource("login/login.fxml"));
        Scene loginScene = new Scene(load1, 300, 300);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }
}