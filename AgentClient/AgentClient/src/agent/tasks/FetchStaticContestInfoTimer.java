package agent.tasks;

import agent.app.MainController;
import agent.app.MessageTone;
import com.google.gson.Gson;
import dto.DTOstaticContestInfo;
import dto.DTOstatus;
import http.url.QueryParameter;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import okhttp3.*;

import java.io.IOException;
import java.util.TimerTask;

import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.BASE_URL;
import static http.url.URLconst.FETCH_STATIC_CONTEST_INFO_SRC;

public class FetchStaticContestInfoTimer extends TimerTask {

    private MainController mainController;

    private OkHttpClient client;
    private final StringProperty allieName;

    public FetchStaticContestInfoTimer(MainController mainController, StringProperty allieName, OkHttpClient client) {
        this.mainController = mainController;
        this.allieName = allieName;
        this.client = client;
    }

    @Override
    public void run() {


        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_STATIC_CONTEST_INFO_SRC).newBuilder();
        urlBuilder.addQueryParameter(QueryParameter.ALLIE_NAME, allieName.get());
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("fetch staticContestInfo timer resp");
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                System.out.println(dtoAsStr);
                Gson gson = new Gson();

                if (response.code() != 200) {
                    DTOstatus staticInfoStatus = gson.fromJson(dtoAsStr, DTOstatus.class);

                    Platform.runLater(() -> mainController.setStatusMessage(mainController.convertProblemToMessage(staticInfoStatus.getDetails()), MessageTone.ERROR));

                } else {
                    DTOstaticContestInfo staticInfoStatus = gson.fromJson(dtoAsStr, DTOstaticContestInfo.class);
                    Platform.runLater(() -> {
                        mainController.updateStaticContestInfo(staticInfoStatus);
                    });

                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }

}
