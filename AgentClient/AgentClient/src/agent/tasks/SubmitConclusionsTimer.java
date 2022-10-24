package agent.tasks;

import agent.app.MainController;
import com.google.gson.Gson;
import dto.DTOagentConclusions;
import dto.DTOstatus;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import okhttp3.*;
import problem.Problem;

import java.io.IOException;
import java.util.TimerTask;

import static http.url.QueryParameter.ALLIE_NAME;
import static http.url.QueryParameter.UBOAT_NAME;
import static http.url.URLconst.*;
import static http.url.URLconst.BASE_URL;
import static http.url.Constants.CONTENT_TYPE;

public class SubmitConclusionsTimer extends TimerTask {

    private final OkHttpClient client;
    private final MainController mainController;
    private final StringProperty uboatName;
    private final StringProperty allieName;
    private final BooleanProperty agentLoggedOut;
    private final Gson gson = new Gson();

    public SubmitConclusionsTimer(MainController mainController, StringProperty allieName, StringProperty uboatName, OkHttpClient client, BooleanProperty agentLoggedOut) {
        this.mainController = mainController;
        this.allieName = allieName;
        this.uboatName = uboatName;
        this.client = client;
        this.agentLoggedOut = agentLoggedOut;
    }

    @Override
    public void run() {

        DTOagentConclusions conclusionsDto = mainController.getConclusions();
        String conclusionsDtoAsJsonStr = gson.toJson(conclusionsDto);
        Platform.runLater(() -> mainController.displayAllCandidates(conclusionsDto.getAgentConclusions()));

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + SUBMIT_NEXT_CANDIDATES_SRC).newBuilder();
        urlBuilder.addQueryParameter(ALLIE_NAME, allieName.get());
        urlBuilder.addQueryParameter(UBOAT_NAME, uboatName.get());
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(conclusionsDtoAsJsonStr.getBytes()))
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("submitConclusion timer response " + "Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();

                if (response.code() != 200) {
                    DTOstatus submitStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        if ((submitStatus.getDetails().equals(Problem.ALLIE_NOT_SUBSCRIBED) || submitStatus.getDetails().equals(Problem.UBOAT_LOGGED_OUT)) && !agentLoggedOut.get()) {
                            mainController.allieUnsubscribedFromCurrentContest();
                        } else if (submitStatus.getDetails().equals(Problem.ALLIE_LOGGED_OUT) && !agentLoggedOut.get()) {
                            mainController.logoutAgent();
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
