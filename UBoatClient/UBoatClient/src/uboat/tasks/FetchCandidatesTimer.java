package uboat.tasks;

import uboat.app.MainController;
import uboat.app.MessageTone;
import candidate.AgentConclusion;
import candidate.Candidate;
import com.google.gson.Gson;
import dto.DTOagentConclusions;
import dto.DTOstatus;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
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

    private ListProperty<Integer> inUseRotorsIDsProperty;

    private StringProperty originalWindowsPositionsProperty;

    private StringProperty inUseReflectorSymbolProperty;

    private StringProperty originalText;

    public FetchCandidatesTimer(MainController mainController,
                                ListProperty<Integer> inUseRotorsIDsProperty,
                                StringProperty originalWindowsPositionsProperty,
                                StringProperty inUseReflectorSymbolProperty,
                                StringProperty originalText) {
        this.mainController = mainController;
        this.inUseRotorsIDsProperty = inUseRotorsIDsProperty;
        this.originalWindowsPositionsProperty = originalWindowsPositionsProperty;
        this.inUseReflectorSymbolProperty = inUseReflectorSymbolProperty;
        this.originalText = originalText;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_CANDIDATES_SRC).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("fetching candidates timer resp");
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
                    scanCandidates(candidatesStatus.getAgentConclusions());
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }

    public void scanCandidates(List<AgentConclusion> conclusions) {

        // goes through all the conclusions
        for (AgentConclusion conclusion : conclusions) {
            String currentAllieName = conclusion.getAllieName();
            String currentAgentName = conclusion.getAgentName();

            // goes through all the candidates of each conclusion
            for (Candidate candidate : conclusion.getCandidates()) {

                // adds a new tile to the candidates area
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> mainController.createCandidateTile(candidate, currentAllieName, currentAgentName));

                // checks for a winner
                if (candidate.getRotorsIDs().equals(inUseRotorsIDsProperty.getValue())
                        && candidate.getWindowChars().equals(originalWindowsPositionsProperty.get())
                        && candidate.getReflectorSymbol().equals(inUseReflectorSymbolProperty.get())
                        && candidate.getDecipheredText().equals(originalText.get())) {

                    // winner has been found...
                    mainController.announceTheWinnerOfTheContest(conclusion.getAllieName());
                    return;
                }


            }

        }
    }

}
