package servlets.common;

import battlefield.Battlefield;
import com.google.gson.Gson;
import engine.Engine;
import http.url.Client;
import http.url.Constants;
import dto.DTOactive;
import dto.DTOstatus;
import http.url.QueryParameter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import problem.Problem;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static utils.ServletUtils.validateAuthorization;

public class FetchContestStatusServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        resp.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(req);
        boolean isValidSession = validateAuthorization(usernameFromSession, resp, gson);
        Client typeOfClient = SessionUtils.getTypeOfClient(req);
        if (isValidSession) {
            if (!typeOfClient.equals(Client.AGENT) && !typeOfClient.equals(Client.ALLIE) && !typeOfClient.equals(Client.UBOAT)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return;
            }
            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
            String uboatName;
            String allieName;
            if (typeOfClient.equals(Client.UBOAT)) {
                uboatName = usernameFromSession;
            } else if (typeOfClient.equals(Client.ALLIE)) {
                uboatName = req.getParameter(QueryParameter.UBOAT_NAME);
            } else {

                // this is case of agent only
                allieName = req.getParameter(QueryParameter.ALLIE_NAME);
                if (allieName == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.NO_ALLIE_NAME)));
                } else {
                    DTOactive activeStatus = engine.checkIfAllieIsSubscribedToContestHasStarted(allieName);
                    if (!activeStatus.isSucceed()) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }
                    resp.getWriter().println(gson.toJson(activeStatus));
                }
                return;
                // end case of agent
            }
            // start case of uboat & allie
            if (uboatName == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.NO_UBOAT_NAME)));
                return;
            }

            Map<String, Battlefield> battlefields = ServletUtils.getUboatName2battleField(getServletContext());
            Set<String> loggedOutClients = ServletUtils.getLoggedOutClients(getServletContext());
            Battlefield battlefield = battlefields.get(uboatName);

            if (battlefield == null && loggedOutClients.contains(uboatName)) { // uboat has logged out
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(new DTOactive(false, Problem.UBOAT_LOGGED_OUT, false)));
            } else if (battlefield == null) { // uboat doesn't exist
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(new DTOactive(false, Problem.UBOAT_NAME_DOESNT_EXIST, false)));
            } else { // all good
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().println(gson.toJson(new DTOactive(true, Problem.NO_PROBLEM, battlefield.isActive().get())));
            }
        }
    }
}
