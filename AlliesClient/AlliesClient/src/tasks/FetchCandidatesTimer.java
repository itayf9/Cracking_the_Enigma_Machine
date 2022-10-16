package tasks;

import app.MainController;
import app.MessageTone;
import com.google.gson.Gson;
import dto.DTOagentConclusions;
import dto.DTOstatus;
import http.url.QueryParameter;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import okhttp3.*;

import java.io.IOException;
import java.util.TimerTask;

import static http.url.URLconst.BASE_URL;
import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.FETCH_CANDIDATES_SRC;

public class FetchCandidatesTimer extends TimerTask {

    private OkHttpClient client;
    private MainController mainController;
    private StringProperty uboatName;

    public FetchCandidatesTimer(MainController mainController, StringProperty uboatName) {
        this.mainController = mainController;
        this.uboatName = uboatName;

    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_CANDIDATES_SRC).newBuilder();
        urlBuilder.addQueryParameter(QueryParameter.UBOAT_NAME, uboatName.get());
        
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("fetch candidates task response");
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
                        mainController.displayAllCandidates(candidatesStatus.getAgentConclusions());
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }
}
