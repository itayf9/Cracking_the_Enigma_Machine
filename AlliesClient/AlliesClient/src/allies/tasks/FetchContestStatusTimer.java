package allies.tasks;


import allies.app.MainController;
import allies.app.MessageTone;
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

import static http.url.QueryParameter.UBOAT_NAME;
import static http.url.URLconst.BASE_URL;
import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.FETCH_CONTEST_STATUS_SRC;

public class FetchContestStatusTimer extends TimerTask {

    private final OkHttpClient client;
    private final BooleanProperty isContestActive;
    private final StringProperty uboatName;
    private MainController mainController;

    public FetchContestStatusTimer(BooleanProperty isContestActive, StringProperty uboatName, OkHttpClient client, MainController mainController) {
        this.isContestActive = isContestActive;
        this.uboatName = uboatName;
        this.client = client;
        this.mainController = mainController;
    }

    @Override
    public void run() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_CONTEST_STATUS_SRC).newBuilder();
        urlBuilder.addQueryParameter(UBOAT_NAME, uboatName.get());
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
                        if (activeStatus.getDetails().equals(Problem.UBOAT_LOGGED_OUT)) {
                            mainController.unsubscribeFromCurrentContestAllieLoggedOut();
                            mainController.cancelContestStatusTimer();
                        }
                        mainController.setStatusMessage(mainController.convertProblemToMessage(activeStatus.getDetails()), MessageTone.ERROR);
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
