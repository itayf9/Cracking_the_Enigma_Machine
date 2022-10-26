package agent.tasks;

import agent.AgentTask;
import agent.AgentTaskDeserializer;
import agent.app.MainController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.DTOstatus;
import dto.DTOtasks;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import okhttp3.*;
import java.io.IOException;
import problem.Problem;
import java.util.concurrent.CountDownLatch;
import static http.url.QueryParameter.*;
import static http.url.URLconst.*;
import static http.url.URLconst.BASE_URL;
import static http.url.Constants.CONTENT_TYPE;

public class FetchTasksThread implements Runnable {

    private OkHttpClient client;
    private final MainController mainController;
    private final StringProperty allieName;
    private final StringProperty uboatName;
    private final BooleanProperty agentLoggedOut;
    private CountDownLatch cdl;


    public FetchTasksThread(MainController mainController, StringProperty allieName, StringProperty uboatName, BooleanProperty agentLoggedOut) {
        this.mainController = mainController;
        this.allieName = allieName;
        this.uboatName = uboatName;
        this.agentLoggedOut = agentLoggedOut;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }


    @Override
    public void run() {

        this.cdl = mainController.getCountDownLatch();

        try {
            cdl.await();
        } catch (InterruptedException ignored) {
            // if we here then someone interrupted us, hopefully that means contest is Over...
            return;
        }


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
                String dtoAsStr = response.body().string();
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(AgentTask.class, new AgentTaskDeserializer())
                        .create();

                if (response.code() != 200) {
                    DTOstatus tasksStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    System.out.println("fetch agentTasks task response " + "Code: " + response.code() + " " + tasksStatus.getDetails());
                    Platform.runLater(() -> {
                        if ((tasksStatus.getDetails().equals(Problem.ALLIE_NOT_SUBSCRIBED) || tasksStatus.getDetails().equals(Problem.UBOAT_LOGGED_OUT)) && !agentLoggedOut.get()) {
                            mainController.allieUnsubscribedFromCurrentContest();
                        } else if (tasksStatus.getDetails().equals(Problem.ALLIE_LOGGED_OUT) && !agentLoggedOut.get()) {
                            mainController.logoutAgent();
                        }
                    });

                } else {
                    System.out.println("fetch agentTasks task response " + "Code: " + response.code());

                    DTOtasks tasksStatus = gson.fromJson(dtoAsStr, DTOtasks.class);
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException ignored) {
                    }
                    Platform.runLater(() -> mainController.executeTasks(tasksStatus.getTaskList()));
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }
}
