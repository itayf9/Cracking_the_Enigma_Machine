package tasks;

import agent.AgentTask;
import agent.AgentTaskDeserializer;
import app.MainController;
import app.MessageTone;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.DTOstatus;
import dto.DTOtasks;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import okhttp3.*;

import java.io.IOException;
import java.util.TimerTask;

import static http.url.QueryParameter.*;
import static http.url.URLconst.*;
import static http.url.URLconst.BASE_URL;
import static http.url.Constants.CONTENT_TYPE;

public class FetchTasksTimer extends TimerTask {

    private OkHttpClient client;
    private final MainController mainController;
    private final StringProperty allieName;
    private final StringProperty uboatName;

    public FetchTasksTimer(MainController mainController, StringProperty allieName, StringProperty uboatName) {
        this.mainController = mainController;
        this.allieName = allieName;
        this.uboatName = uboatName;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_NEXT_TASKS_SRC).newBuilder();
        urlBuilder.addQueryParameter(ALLIE_NAME, allieName.get());
        urlBuilder.addQueryParameter(UBOAT_NAME, uboatName.get());
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("fetch agentTasks task response");
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                System.out.println("Body: " + dtoAsStr);
                //Gson gson = new Gson();
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(AgentTask.class, new AgentTaskDeserializer())
                        .create();


                if (response.code() != 200) {
                    DTOstatus tasksStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        mainController.setStatusMessage(mainController.convertProblemToMessage(tasksStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    DTOtasks tasksStatus = gson.fromJson(dtoAsStr, DTOtasks.class);
                    Platform.runLater(() -> {
                        mainController.executeTasks(tasksStatus.getTaskList());
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }
}
