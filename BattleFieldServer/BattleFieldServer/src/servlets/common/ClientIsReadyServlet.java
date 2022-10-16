package servlets.common;

import com.google.gson.Gson;
import http.url.Client;
import http.url.Constants;

import dto.DTOstatus;
import engine.Engine;
import http.url.QueryParameter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import problem.Problem;
import utils.SessionUtils;

import java.io.IOException;

import static utils.ServletUtils.validateAuthorization;

public class ClientIsReadyServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();

        // get userName of uboat
        String userNameFromSession = SessionUtils.getUsername(req);
        Client typeOfClient = SessionUtils.getTypeOfClient(req);

        String uboatName = "";

        resp.setContentType("application/json");
        boolean isValidSession = validateAuthorization(userNameFromSession, resp, gson);
        if (isValidSession) {


            // get engine from context
            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);

            if (!typeOfClient.equals(Client.UBOAT) && !typeOfClient.equals(Client.ALLIE)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return;
            }

            switch (typeOfClient) {
                case UBOAT:
                    uboatName = userNameFromSession;
                    engine.setUboatReady(userNameFromSession, true);
                    break;
                case ALLIE:
                    uboatName = req.getParameter(QueryParameter.UBOAT_NAME);
                    if (uboatName == null) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.NO_UBOAT_NAME)));
                        return;
                    }
                    int taskSize;
                    try {
                        taskSize = Integer.parseInt(req.getParameter(QueryParameter.TASK_SIZE));
                    } catch (NumberFormatException e) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.MISSING_QUERY_PARAMETER)));
                        return;
                    }
                    engine.setAllieReady(userNameFromSession, uboatName, true, taskSize);
                    break;
            }
            if (engine.allClientsReady(uboatName)) {
                engine.startBruteForceProcess(uboatName);
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println(gson.toJson(new DTOstatus(true, Problem.NO_PROBLEM)));
        }
    }
}