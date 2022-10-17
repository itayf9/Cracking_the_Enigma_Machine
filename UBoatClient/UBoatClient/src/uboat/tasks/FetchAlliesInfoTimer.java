package uboat.tasks;

import uboat.app.MainController;
import uboat.app.MessageTone;
import com.google.gson.Gson;
import dto.DTOallies;
import dto.DTOstatus;
import javafx.application.Platform;
import okhttp3.*;

import java.io.IOException;
import java.util.TimerTask;

import static http.url.URLconst.BASE_URL;
import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.FETCH_ALLIES_INFO_SRC;

public class FetchAlliesInfoTimer extends TimerTask {

    OkHttpClient client;
    MainController mainController;

    public FetchAlliesInfoTimer(MainController mainController) {
        this.mainController = mainController;
    }

    public FetchAlliesInfoTimer(OkHttpClient client, MainController mainController) {
        this.client = client;
        this.mainController = mainController;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_ALLIES_INFO_SRC).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();


                if (response.code() != 200) {
                    DTOstatus alliesStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        mainController.setStatusMessage("Could not fetch team's information." + mainController.convertProblemToMessage(alliesStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    DTOallies alliesStatus = gson.fromJson(dtoAsStr, DTOallies.class);

                    Platform.runLater(() -> {
                        mainController.updateAlliesInfo(alliesStatus.getAllies());
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }
}
