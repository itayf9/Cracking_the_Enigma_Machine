package login;

import app.MainController;
import com.google.gson.Gson;
import dto.DTOallies;
import dto.DTOstatus;
import http.cookie.SimpleCookieManager;
import http.url.Constants;
import info.allie.AllieInfo;
import javafx.application.Platform;
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

import static http.url.Client.AGENT;
import static http.url.Client.UBOAT;
import static http.url.URLconst.BASE_URL;
import static http.url.URLconst.CONTENT_TYPE;

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
    private Spinner<Integer> tasksPerPullSpinner;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false); // hide error label

        // get all allies
        OkHttpClient client = new OkHttpClient().newBuilder().cookieJar(new SimpleCookieManager()).build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/fetch/allies-info").newBuilder();
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

                DTOallies alliesStatus = gson.fromJson(dtoAsStr, DTOallies.class);

                Platform.runLater(() -> {
                    for (AllieInfo allie : alliesStatus.getAllies()) {
                        teamComboBox.getItems().add(allie.getAllieName());
                    }
                });
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });

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
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/login").newBuilder();
        urlBuilder.addQueryParameter(Constants.USERNAME, userNameTextField.getText());
        urlBuilder.addQueryParameter(Constants.ALLIE_NAME, teamComboBox.getEditor().getText());
        urlBuilder.addQueryParameter(Constants.NUM_OF_THREADS, String.valueOf(threadsSlider.getValue()));
        urlBuilder.addQueryParameter(Constants.MISSION_COUNT, tasksPerPullSpinner.getEditor().getText());
        urlBuilder.addQueryParameter(Constants.CLIENT_TYPE, UBOAT.getClientTypeAsString());
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
                        URL appFxml = getClass().getResource("/app/app.fxml");
                        loader.setLocation(appFxml);
                        app = loader.load();
                        appController = loader.getController();
                        appController.setOkHttpClient(client);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Scene appScene = new Scene(app, 900, 625);
                    Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
