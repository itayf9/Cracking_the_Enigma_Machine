package allies.tasks;

import allies.app.MessageTone;
import allies.app.MainController;
import candidate.AgentConclusion;
import candidate.Candidate;
import com.google.gson.Gson;
import dto.DTOagentConclusions;
import dto.DTOstatus;
import http.url.QueryParameter;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

import static http.url.URLconst.BASE_URL;
import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.FETCH_CANDIDATES_SRC;

public class FetchCandidatesTimer extends TimerTask {

    private OkHttpClient client;
    private MainController mainController;
    private StringProperty uboatName;

    public FetchCandidatesTimer(MainController mainController, StringProperty uboatName) {
        this.mainController = mainController;
        this.uboatName = uboatName;

    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_CANDIDATES_SRC).newBuilder();
        urlBuilder.addQueryParameter(QueryParameter.UBOAT_NAME, uboatName.get());
        
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("fetch candidates task response");
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();


                if (response.code() != 200) {
                    DTOstatus candidatesStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        mainController.setStatusMessage(mainController.convertProblemToMessage(candidatesStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    // start scanning candidates
                    DTOagentConclusions candidatesStatus = gson.fromJson(dtoAsStr, DTOagentConclusions.class);
                    displayAllCandidates(candidatesStatus.getAgentConclusions());
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }

    /**
     * display all candidates from server
     *
     * @param conclusions
     */
    public void displayAllCandidates(List<AgentConclusion> conclusions) {

        //Platform.runLater(() -> mainController.clearOldCandidates());

        // goes through all the conclusions
        for (AgentConclusion conclusion : conclusions) {
            String currentAllieName = conclusion.getAllieName();
            String currentAgentName = conclusion.getAgentName();

            // goes through all the candidates of each conclusion
            for (Candidate candidate : conclusion.getCandidates()) {

                // adds a new tile to the candidates area

                //Platform.runLater(() -> mainController.createCandidateTile(candidate, currentAllieName, currentAgentName));

            }
        }
    }
}
