package agent.tasks;

import agent.app.MainController;
import agent.app.MessageTone;
import com.google.gson.Gson;
import dto.DTOactive;
import dto.DTOstatus;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import okhttp3.*;
import problem.Problem;

import java.io.IOException;
import java.util.TimerTask;

import static http.url.Constants.CONTENT_TYPE;
import static http.url.QueryParameter.ALLIE_NAME;
import static http.url.URLconst.BASE_URL;
import static http.url.URLconst.FETCH_CONTEST_STATUS_SRC;

public class FetchIsAgentCanGetOutOfWaitingModeTimer extends TimerTask {
    private final OkHttpClient client;
    private final String allieName;
    private final MainController mainController;


    public FetchIsAgentCanGetOutOfWaitingModeTimer(String allieName, OkHttpClient client, MainController mainController) {
        this.allieName = allieName;
        this.client = client;
        this.mainController = mainController;
    }

    @Override
    public void run() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_CONTEST_STATUS_SRC).newBuilder();
        urlBuilder.addQueryParameter(ALLIE_NAME, allieName);
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("fetch isAgentCanGetOutOFWaitingMode task response " + "Code: " + response.code());
                String dtoAsStr = response.body().string();
                System.out.println(dtoAsStr);
                Gson gson = new Gson();

                if (response.code() != 200) {
                    DTOstatus activeStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        if (activeStatus.getDetails().equals(Problem.ALLIE_NOT_SUBSCRIBED)) { // uboat logged out
                            mainController.allieUnsubscribedFromCurrentContest();
                        } else if (activeStatus.getDetails().equals(Problem.ALLIE_LOGGED_OUT)) { // allie logged out
                            mainController.logoutAgent();
                        }
                        mainController.setStatusMessage(mainController.convertProblemToMessage(activeStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    DTOactive activeStatus = gson.fromJson(dtoAsStr, DTOactive.class);
                    Platform.runLater(() -> {
                        if (!activeStatus.isActive()) {
                            mainController.setAgentIsOutOfWaitingMode();
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