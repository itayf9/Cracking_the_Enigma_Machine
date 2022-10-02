package servlets.common;

import com.google.gson.Gson;
import constants.Client;
import constants.Constants;
import dto.DTOagentConclusions;
import dto.DTOallies;
import dto.DTOstatus;
import engine.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import problem.Problem;
import utils.SessionUtils;

import java.io.IOException;

import static constants.Client.UBOAT;
import static constants.Client.getClient;
import static utils.ServletUtils.validateAuthorization;

public class FetchCandidatesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);

        resp.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(req);
        Client typeOfClient = SessionUtils.getTypeOfClient(req);

        boolean isValidSession = validateAuthorization(usernameFromSession, resp, gson);

        if (isValidSession) {
            switch (typeOfClient) {
                case UBOAT:
                    DTOagentConclusions agentConclusionsStatus = engine.fetchNextCandidates(usernameFromSession);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println(gson.toJson(agentConclusionsStatus));
                    break;
                case ALLIE:
                    String uboatName = req.getParameter("uboat-name");
                    if (uboatName == null) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.NO_UBOAT_NAME)));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().println(gson.toJson(engine.fetchCandidatesToDisplay(uboatName, usernameFromSession)));
                        break;
                    }
            }
        }
    }
}
