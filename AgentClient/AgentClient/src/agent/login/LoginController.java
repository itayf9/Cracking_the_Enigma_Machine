package agent.login;

import agent.app.MainController;
import agent.tasks.FetchLoggedAlliesInfoTimer;
import com.google.gson.Gson;
import dto.DTOloggedAllies;
import dto.DTOstatus;
import http.cookie.SimpleCookieManager;
import http.url.Constants;
import http.url.QueryParameter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import okhttp3.*;

import java.io.IOException;
import java.net.URL;
import java.util.Timer;

import static http.url.Client.AGENT;
import static http.url.URLconst.*;
import static http.url.URLconst.BASE_URL;
import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.LOGIN_SRC;

public class LoginController {

    GridPane app;

    private MainController appController;

    @FXML
    private TextField userNameTextField;

    @FXML
    private Button logInButton;

    @FXML
    private Label errorLabel;

    @FXML
    private ComboBox<String> teamComboBox;

    @FXML
    private Slider threadsSlider;

    @FXML
    private Label numOfThreadsLabel;

    @FXML
    private Spinner<Integer> tasksPerPullSpinner;

    private Timer fetchLoggedAlliesInfoTimer;
    private FetchLoggedAlliesInfoTimer fetchLoggedAlliesInfoTimerTask;
    private StringProperty chosenAllie;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false); // hide error label
        this.chosenAllie = new SimpleStringProperty("");
        numOfThreadsLabel.textProperty().bind(Bindings.concat("Threads: ", Bindings.format("%.0f", threadsSlider.valueProperty())));

        // get all allies
        OkHttpClient client = new OkHttpClient().newBuilder().cookieJar(new SimpleCookieManager()).build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_LOGGED_ALLIES_SRC).newBuilder();
        urlBuilder.addQueryParameter(Constants.CLIENT_TYPE, AGENT.getClientTypeAsString());
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Code: " + response.code());

                String dtoAsStr = response.body().string();
                System.out.println("Body: " + dtoAsStr);
                Gson gson = new Gson();
                if (response.code() != 200) {

                    DTOstatus loginStatus = gson.fromJson(dtoAsStr, DTOstatus.class);

                    Platform.runLater(() -> {
                        errorLabel.setVisible(true);
                        errorLabel.setText(loginStatus.getDetails().name());
                    });
                    return;
                }

                DTOloggedAllies alliesStatus = gson.fromJson(dtoAsStr, DTOloggedAllies.class);

                Platform.runLater(() -> {
                    teamComboBox.getItems().clear();
                    for (String allieName : alliesStatus.getLoggedAllies()) {
                        teamComboBox.getItems().add(allieName);
                    }
                });
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
//        fetchLoggedAlliesInfoTimer = new Timer();
//        fetchLoggedAlliesInfoTimerTask = new FetchLoggedAlliesInfoTimer(errorLabel, teamComboBox);
//        fetchLoggedAlliesInfoTimer.schedule(fetchLoggedAlliesInfoTimerTask, 2000,2000);

        tasksPerPullSpinner.valueFactoryProperty().set(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE));


        tasksPerPullSpinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int newValueAsInt = Integer.parseInt(newValue);
                if (newValueAsInt > 0) {
                    tasksPerPullSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, newValueAsInt));
                } else {
                    tasksPerPullSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, Integer.parseInt(oldValue)));
                    tasksPerPullSpinner.getEditor().setText(oldValue);
                }
            } catch (NumberFormatException e) {
                tasksPerPullSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, Integer.parseInt(oldValue)));
                tasksPerPullSpinner.getEditor().setText(oldValue);
            }
        });
    }


    @FXML
    void sendLogIn(MouseEvent event) {

        String body = "";
        OkHttpClient client = new OkHttpClient().newBuilder().cookieJar(new SimpleCookieManager()).build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + LOGIN_SRC).newBuilder();
        urlBuilder.addQueryParameter(Constants.USERNAME, userNameTextField.getText());
        urlBuilder.addQueryParameter(QueryParameter.ALLIE_NAME, chosenAllie.get());
        urlBuilder.addQueryParameter(QueryParameter.NUM_OF_THREADS, String.valueOf((int) threadsSlider.getValue()));
        urlBuilder.addQueryParameter(QueryParameter.MISSION_COUNT, tasksPerPullSpinner.getEditor().getText());
        urlBuilder.addQueryParameter(Constants.CLIENT_TYPE, AGENT.getClientTypeAsString());
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(body.getBytes()))
                .build();
        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Code: " + response.code());

                String dtoAsStr = response.body().string();
                System.out.println("Body: " + dtoAsStr);

                if (response.code() != 200) {
                    Gson gson = new Gson();
                    DTOstatus loginStatus = gson.fromJson(dtoAsStr, DTOstatus.class);

                    Platform.runLater(() -> {
                        errorLabel.setVisible(true);
                        errorLabel.setText(loginStatus.getDetails().name());
                    });
                    return;
                }


                Platform.runLater(() -> {
                    FXMLLoader loader = null;
                    try {
                        loader = new FXMLLoader();
                        URL appFxml = getClass().getResource("/agent/app/app.fxml");
                        loader.setLocation(appFxml);
                        app = loader.load();
                        appController = loader.getController();
                        appController.setOkHttpClient(client);
                        appController.setInitialSettings(teamComboBox.getValue(), (int) threadsSlider.getValue(), tasksPerPullSpinner.getValue(), userNameTextField.getText());

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Scene appScene = new Scene(app, 900, 625);
                    Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//                    fetchLoggedAlliesInfoTimer.cancel();
//                    fetchLoggedAlliesInfoTimerTask.cancel();
                    primaryStage.setScene(appScene);
                    primaryStage.show();
                });
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });

    }
}
