package tasks;

import app.MainController;
import com.google.gson.Gson;
import dto.DTOloggedAgents;
import dto.DTOstatus;
import javafx.application.Platform;
import okhttp3.*;

import java.io.IOException;
import java.util.TimerTask;

import static http.url.URLconst.BASE_URL;
import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.FETCH_LOGGED_AGENTS_INFO_SRC;

public class FetchLoggedAgentsInfoTimer extends TimerTask {

    private OkHttpClient client;
    private MainController mainController;

    public FetchLoggedAgentsInfoTimer(MainController mainController) {
        this.mainController = mainController;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void run() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_LOGGED_AGENTS_INFO_SRC).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("fetch logged agents task response");
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                System.out.println("Body: " + dtoAsStr);
                Gson gson = new Gson();

                if (response.code() != 200) {
                    DTOstatus agentsInfoStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                    });

                } else {
                    DTOloggedAgents agentsInfoStatus = gson.fromJson(dtoAsStr, DTOloggedAgents.class);

                    Platform.runLater(() -> {
                        mainController.updateLoggedAgentsInfo(agentsInfoStatus.getLoggedAgents());
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }
}
