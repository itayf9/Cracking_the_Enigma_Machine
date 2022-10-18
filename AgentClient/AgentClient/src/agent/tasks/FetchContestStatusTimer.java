package agent.tasks;


import com.google.gson.Gson;
import dto.DTOactive;
import dto.DTOstatus;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import okhttp3.*;

import java.io.IOException;
import java.util.TimerTask;

import static http.url.QueryParameter.*;
import static http.url.URLconst.BASE_URL;
import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.*;
public class FetchContestStatusTimer extends TimerTask {

    private OkHttpClient client;
    private BooleanProperty isContestActive;
    private StringProperty allieName;

    public FetchContestStatusTimer(BooleanProperty isContestActive, StringProperty allieName, OkHttpClient client) {
        this.isContestActive = isContestActive;
        this.allieName = allieName;
        this.client = client;
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
                System.out.println("fetch contest status task response");

                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();


                if (response.code() != 200) {
                    DTOstatus activeStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {

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
