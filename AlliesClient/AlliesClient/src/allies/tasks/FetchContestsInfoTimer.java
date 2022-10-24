package allies.tasks;

import allies.app.MessageTone;
import allies.app.MainController;
import com.google.gson.Gson;
import dto.DTObattlefields;
import dto.DTOstatus;
import javafx.application.Platform;
import okhttp3.*;

import java.io.IOException;
import java.util.TimerTask;

import static http.url.URLconst.BASE_URL;
import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.FETCH_BATTLEFIELDS_INFO_SRC;

public class FetchContestsInfoTimer extends TimerTask {

    private OkHttpClient client;
    private final MainController mainController;


    public FetchContestsInfoTimer(MainController mainController) {
        this.mainController = mainController;
    }

    public FetchContestsInfoTimer(MainController mainController, OkHttpClient client) {
        this.mainController = mainController;
        this.client = client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void run() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_BATTLEFIELDS_INFO_SRC).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("fetch contests timer response" + "Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();


                if (response.code() != 200) {
                    DTOstatus contestsStatus = gson.fromJson(dtoAsStr, DTOstatus.class);

                    Platform.runLater(() -> {
                        mainController.setStatusMessage(mainController.convertProblemToMessage(contestsStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    DTObattlefields contestsStatus = gson.fromJson(dtoAsStr, DTObattlefields.class);

                    Platform.runLater(() -> {
                        mainController.displayContestsInfo(contestsStatus.getAllBattlefields());
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });

    }
}
