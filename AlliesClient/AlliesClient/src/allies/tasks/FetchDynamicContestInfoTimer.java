package allies.tasks;

import allies.app.MessageTone;
import allies.app.MainController;
import candidate.AgentConclusion;
import candidate.Candidate;
import com.google.gson.Gson;
import dto.DTOdynamicContestInfo;
import dto.DTOstatus;
import info.agent.AgentInfo;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import jobprogress.JobProgressInfo;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;

import static http.url.QueryParameter.*;
import static http.url.URLconst.BASE_URL;
import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.FETCH_DYNAMIC_CONTEST_INFO_SRC;

public class FetchDynamicContestInfoTimer extends TimerTask {

    private OkHttpClient client;
    private final MainController mainController;
    private StringProperty uboatName;

    public FetchDynamicContestInfoTimer(MainController mainController, StringProperty uboatName, OkHttpClient client) {
        this.mainController = mainController;
        this.uboatName = uboatName;
        this.client = client;
    }
    
    @Override
    public void run() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_DYNAMIC_CONTEST_INFO_SRC).newBuilder();
        urlBuilder.addQueryParameter(UBOAT_NAME, uboatName.get());
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("fetch dynamic info timer response");
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                Gson gson = new Gson();
                
                if (response.code() != 200) {
                    DTOstatus contestInfoStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        mainController.setStatusMessage(mainController.convertProblemToMessage(contestInfoStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    // start scanning candidates
                    DTOdynamicContestInfo contestInfoStatus = gson.fromJson(dtoAsStr, DTOdynamicContestInfo.class);
                    System.out.println("num of conclusions found " + contestInfoStatus.getAllCandidates().size());
                    displayDynamicContestInfo(contestInfoStatus.getAgentsInfo(), contestInfoStatus.getJobStatus(), contestInfoStatus.getAllCandidates());
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }

    /**
     * disaply all dynamic info like agents & progress of the contest
     *
     * @param agentsInfo    agent info
     * @param jobStatus     progress
     * @param allCandidates candidates found
     */
    public void displayDynamicContestInfo(Set<AgentInfo> agentsInfo, JobProgressInfo jobStatus, List<AgentConclusion> allCandidates) {
        Platform.runLater(() -> mainController.displayDynamicContestInfo(agentsInfo, jobStatus));

        for (AgentConclusion agentConclusion : allCandidates) {
            String allieName = agentConclusion.getAllieName();
            String agentName = agentConclusion.getAgentName();
            for (Candidate candidate : agentConclusion.getCandidates()) {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> mainController.createCandidateTile(candidate, allieName, agentName));
            }
        }
    }

}
