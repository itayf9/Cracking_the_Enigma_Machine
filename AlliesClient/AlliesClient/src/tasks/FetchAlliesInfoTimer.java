package tasks;

import app.MainController;
import app.MessageTone;
import com.google.gson.Gson;
import dto.DTOallies;
import dto.DTOstatus;
import http.url.Constants;
import javafx.application.Platform;
import okhttp3.*;

import java.io.IOException;
import java.util.TimerTask;

import static http.url.URLconst.BASE_URL;
import static http.url.URLconst.CONTENT_TYPE;

public class FetchAlliesInfoTimer extends TimerTask {

    private OkHttpClient client;
    private MainController mainController;
    private String uboatName;


    public FetchAlliesInfoTimer(MainController mainController) {
        this.mainController = mainController;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public void setUboatName(String uboatName) {
        this.uboatName = uboatName;
    }


    @Override
    public void run() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/fetch/allies-info").newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("allies info task response");
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                System.out.println("Body: " + dtoAsStr);
                Gson gson = new Gson();


                if (response.code() != 200) {
                    DTOstatus alliesStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        mainController.setStatusMessage("Could not fetch team's information." + mainController.convertProblemToMessage(alliesStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    DTOallies alliesStatus = gson.fromJson(dtoAsStr, DTOallies.class);

                    Platform.runLater(() -> {
                        mainController.updateActiveTeamsInfo(alliesStatus.getAllies());
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }
}