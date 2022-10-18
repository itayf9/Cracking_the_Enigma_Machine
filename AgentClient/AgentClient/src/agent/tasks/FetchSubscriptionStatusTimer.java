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

import static http.url.Constants.CONTENT_TYPE;
import static http.url.QueryParameter.ALLIE_NAME;
import static http.url.URLconst.*;
import static http.url.URLconst.BASE_URL;


public class FetchSubscriptionStatusTimer extends TimerTask {

    private OkHttpClient client;
    private final BooleanProperty isSubscribed;
    private final StringProperty allieName;

    public FetchSubscriptionStatusTimer(BooleanProperty isSubscribed, StringProperty allieName) {
        this.isSubscribed = isSubscribed;
        this.allieName = allieName;
    }

    public FetchSubscriptionStatusTimer(BooleanProperty isSubscribed, StringProperty allieName, OkHttpClient client) {
        this.isSubscribed = isSubscribed;
        this.allieName = allieName;
        this.client = client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void run() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_SUBSCRIPTION_STATUS_SRC).newBuilder();
        urlBuilder.addQueryParameter(ALLIE_NAME, allieName.get());
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("fetch subscription status timer response");

                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();


                if (response.code() != 200) {
                    DTOstatus subscribeStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {

                    });

                } else {
                    DTOactive activeStatus = gson.fromJson(dtoAsStr, DTOactive.class);

                    Platform.runLater(() -> {
                        if (activeStatus.isActive()) {
                            isSubscribed.set(Boolean.TRUE);
                        } else {
                            isSubscribed.set(Boolean.FALSE);
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
