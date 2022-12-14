package agent.tasks;


import agent.app.MainController;
import agent.app.MessageTone;
import com.google.gson.Gson;
import dto.DTOactive;
import dto.DTOstatus;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import okhttp3.*;
import problem.Problem;

import java.io.IOException;
import java.util.TimerTask;

import static http.url.QueryParameter.*;
import static http.url.URLconst.BASE_URL;
import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.*;

public class FetchContestStatusTimer extends TimerTask {

    private final OkHttpClient client;
    private final BooleanProperty isContestActive;
    private final StringProperty allieName;
    private final MainController mainController;

    private final BooleanProperty agentLoggedOut;

    public FetchContestStatusTimer(BooleanProperty isContestActive, StringProperty allieName, OkHttpClient client, MainController mainController, BooleanProperty agentLoggedOut) {
        this.isContestActive = isContestActive;
        this.allieName = allieName;
        this.client = client;
        this.mainController = mainController;
        this.agentLoggedOut = agentLoggedOut;
    }

    @Override
    public void run() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_CONTEST_STATUS_SRC).newBuilder();
        urlBuilder.addQueryParameter(ALLIE_NAME, allieName.get());
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                String dtoAsStr = response.body().string();
                System.out.println("fetch contest status timer response " + "Code: " + response.code() + " " + dtoAsStr);
                Gson gson = new Gson();

                if (response.code() != 200) {
                    DTOstatus activeStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        if ((activeStatus.getDetails().equals(Problem.ALLIE_NOT_SUBSCRIBED) || activeStatus.getDetails().equals(Problem.UBOAT_LOGGED_OUT)) && !agentLoggedOut.get()) {
                            mainController.allieUnsubscribedFromCurrentContest();
                        } else if (activeStatus.getDetails().equals(Problem.ALLIE_LOGGED_OUT) && !agentLoggedOut.get()) {
                            mainController.logoutAgent();
                        }
                    });

                } else {
                    DTOactive activeStatus = gson.fromJson(dtoAsStr, DTOactive.class);
                    Platform.runLater(() -> {
                        if (activeStatus.isActive()) {
                            isContestActive.set(Boolean.TRUE);
                        } else {
                            isContestActive.set(Boolean.FALSE);
                        }
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }
}
