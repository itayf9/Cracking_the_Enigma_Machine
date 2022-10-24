package agent.tasks;

import agent.app.MainController;
import agent.app.MessageTone;
import com.google.gson.Gson;
import dto.DTOstaticContestInfo;
import dto.DTOstatus;
import http.url.QueryParameter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import okhttp3.*;
import problem.Problem;

import java.io.IOException;
import java.util.TimerTask;

import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.BASE_URL;
import static http.url.URLconst.FETCH_STATIC_CONTEST_INFO_SRC;

public class FetchStaticContestInfoTimer extends TimerTask {

    private final MainController mainController;
    private final OkHttpClient client;
    private final StringProperty allieName;
    private final BooleanProperty agentLoggedOut;

    public FetchStaticContestInfoTimer(MainController mainController, StringProperty allieName, OkHttpClient client, BooleanProperty agentLoggedOut) {
        this.mainController = mainController;
        this.allieName = allieName;
        this.client = client;
        this.agentLoggedOut = agentLoggedOut;
    }

    @Override
    public void run() {


        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_STATIC_CONTEST_INFO_SRC).newBuilder();
        urlBuilder.addQueryParameter(QueryParameter.ALLIE_NAME, allieName.get());
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("fetch staticContestInfo timer resp" + "Code: " + response.code());
                String dtoAsStr = response.body().string();
                System.out.println(dtoAsStr);
                Gson gson = new Gson();

                if (response.code() != 200) {
                    DTOstatus staticInfoStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        if ((staticInfoStatus.getDetails().equals(Problem.ALLIE_NOT_SUBSCRIBED) || staticInfoStatus.getDetails().equals(Problem.UBOAT_LOGGED_OUT)) && !agentLoggedOut.get()) {
                            mainController.cancelStaticInfoTimer();
                            mainController.allieUnsubscribedFromCurrentContest();
                        } else if (staticInfoStatus.getDetails().equals(Problem.UBOAT_LOGGED_OUT) && !agentLoggedOut.get()) {
                            mainController.logoutAgent();
                        }
                        mainController.setStatusMessage(mainController.convertProblemToMessage(staticInfoStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    DTOstaticContestInfo staticInfoStatus = gson.fromJson(dtoAsStr, DTOstaticContestInfo.class);
                    Platform.runLater(() -> mainController.updateStaticContestInfo(staticInfoStatus));
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }

}
