package tasks;

import app.MainController;
import app.MessageTone;
import com.google.gson.Gson;
import dto.DTOagentConclusions;
import dto.DTOstatus;
import javafx.application.Platform;
import okhttp3.*;

import java.io.IOException;
import java.util.TimerTask;

import static http.url.URLconst.BASE_URL;
import static http.url.URLconst.CONTENT_TYPE;

public class FetchCandidatesTimer extends TimerTask {

    OkHttpClient client;
    MainController mainController;

    public FetchCandidatesTimer(MainController mainController) {
        this.mainController = mainController;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/fetch/candidates").newBuilder();
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
                    DTOstatus candidatesStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        mainController.setStatusMessage(mainController.convertProblemToMessage(candidatesStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    // start scanning candidates
                    DTOagentConclusions candidatesStatus = gson.fromJson(dtoAsStr, DTOagentConclusions.class);
                    Platform.runLater(() -> {
                        mainController.scanCandidates(candidatesStatus.getAgentConclusions());
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }
}
