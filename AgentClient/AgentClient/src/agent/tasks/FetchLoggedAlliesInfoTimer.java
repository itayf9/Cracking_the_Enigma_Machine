package agent.tasks;

import com.google.gson.Gson;
import dto.DTOloggedAllies;
import dto.DTOstatus;
import http.cookie.SimpleCookieManager;
import http.url.Constants;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import okhttp3.*;

import java.io.IOException;
import java.util.Optional;
import java.util.TimerTask;

import static http.url.Client.AGENT;
import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.BASE_URL;
import static http.url.URLconst.FETCH_LOGGED_ALLIES_SRC;

public class FetchLoggedAlliesInfoTimer extends TimerTask {
    private final Label errorLabel;
    private final ComboBox<String> teamComboBox;
    private StringProperty chosenAllie;

    public FetchLoggedAlliesInfoTimer(Label errorLabel, ComboBox<String> teamComboBox) {
        this.errorLabel = errorLabel;
        this.teamComboBox = teamComboBox;
    }


    @Override
    public void run() {
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
                    String currentTeamComboBoxValue = teamComboBox.getValue();
                    teamComboBox.getItems().clear();
                    for (String allieName : alliesStatus.getLoggedAllies()) {
                        teamComboBox.getItems().add(allieName);
                    }
                    Optional<String> maybeAllieName = teamComboBox.getItems().stream().filter(team -> team.equals(currentTeamComboBoxValue)).findFirst();
                    if (maybeAllieName.isPresent()) {
                        teamComboBox.setValue(currentTeamComboBoxValue);
                    } else {
                        errorLabel.setText("The selected team has logged out");
                    }
                });
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });}
}
