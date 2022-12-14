package uboat.tasks;


import com.google.gson.Gson;
import dto.DTOactive;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import okhttp3.*;

import java.io.IOException;
import java.util.TimerTask;

import static http.url.URLconst.BASE_URL;
import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.FETCH_CONTEST_STATUS_SRC;

public class FetchContestStatusTimer extends TimerTask {

    private OkHttpClient client;
    private BooleanProperty isContestActive;

    public FetchContestStatusTimer(BooleanProperty isContestActive, OkHttpClient client) {
        this.isContestActive = isContestActive;
        this.client = client;
    }

    @Override
    public void run() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_CONTEST_STATUS_SRC).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                String dtoAsStr = response.body().string();
                System.out.println("fetching contest status resp " + "Code: " + response.code() + " " + dtoAsStr);
                Gson gson = new Gson();

                DTOactive activeStatus = gson.fromJson(dtoAsStr, DTOactive.class);

                if (response.code() != 200) {
                    Platform.runLater(() -> {
                    });

                } else {
                    Platform.runLater(() -> {
                        if (activeStatus.isActive()) {
                            isContestActive.set(Boolean.TRUE);
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
