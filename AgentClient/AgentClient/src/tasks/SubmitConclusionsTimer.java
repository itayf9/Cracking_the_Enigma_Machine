package tasks;

import app.MainController;
import com.google.gson.Gson;
import dto.DTOstatus;
import javafx.application.Platform;
import okhttp3.*;

import java.io.IOException;
import java.util.TimerTask;
import static http.url.URLconst.*;
import static http.url.URLconst.BASE_URL;
import static http.url.Constants.CONTENT_TYPE;

public class SubmitConclusionsTimer extends TimerTask {

    private OkHttpClient client;
    private MainController mainController;
    private Gson gson = new Gson();

    public SubmitConclusionsTimer(MainController mainController) {
        this.mainController = mainController;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void run() {

        String conclusions = gson.toJson(mainController.getConclusions());

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + SUBMIT_NEXT_CANDIDATES_SRC).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .post(RequestBody.create(conclusions.getBytes()))
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
                    DTOstatus agentsInfoStatus = gson.fromJson(dtoAsStr, DTOstatus.class);

                    Platform.runLater(() -> {
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }
}
