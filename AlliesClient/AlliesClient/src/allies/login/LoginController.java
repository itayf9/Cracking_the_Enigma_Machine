package allies.login;

import allies.app.MainController;
import com.google.gson.Gson;
import dto.DTOstatus;
import http.cookie.SimpleCookieManager;
import http.url.Constants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import okhttp3.*;

import java.io.IOException;
import java.net.URL;

import static http.url.Client.ALLIE;
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
    public void initialize() {
        errorLabel.setVisible(false); // hide error label
    }


    @FXML
    void sendLogIn(MouseEvent event) {

        String body = "";
        OkHttpClient client = new OkHttpClient().newBuilder().cookieJar(new SimpleCookieManager()).build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + LOGIN_SRC).newBuilder();
        urlBuilder.addQueryParameter(Constants.USERNAME, userNameTextField.getText());
        urlBuilder.addQueryParameter(Constants.CLIENT_TYPE, ALLIE.getClientTypeAsString());
        System.out.println(urlBuilder.build().toString());
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
                        errorLabel.setText(loginStatus.getDetails().problemToGeneralMessage());
                    });
                    return;
                }


                Platform.runLater(() -> {
                    FXMLLoader loader = null;
                    try {
                        loader = new FXMLLoader();
                        URL appFxml = getClass().getResource("/allies/app/app.fxml");
                        loader.setLocation(appFxml);
                        app = loader.load();
                        appController = loader.getController();
                        appController.setOkHttpClient(client);
                        appController.setUserName(userNameTextField.getText());

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
