package tasks;

import com.google.gson.Gson;
import dto.DTOactive;
import dto.DTOstatus;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import okhttp3.*;

import java.io.IOException;
import java.util.TimerTask;

import static http.url.URLconst.BASE_URL;
import static http.url.URLconst.CONTENT_TYPE;

public class FetchSubscriptionStatusTimer extends TimerTask {

    private OkHttpClient client;
    private final BooleanProperty isSubscribed;

    public FetchSubscriptionStatusTimer(BooleanProperty isSubscribed) {
        this.isSubscribed = isSubscribed;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void run() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/fetch/subscribe/status").newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("fetch subscription task response");

                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                System.out.println("Body: " + dtoAsStr);
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
