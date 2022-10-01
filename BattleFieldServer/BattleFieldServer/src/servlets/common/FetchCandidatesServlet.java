package servlets.common;

import com.google.gson.Gson;
import constants.Client;
import constants.Constants;
import dto.DTOagentConclusions;
import dto.DTOallies;
import engine.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        String typeOfClient = SessionUtils.getTypeOfClient(req);
        Client client = getClient(typeOfClient);

        boolean isValid = validateAuthorization(usernameFromSession, resp, gson);

        if (isValid) {
            switch (client) {
                case UBOAT:
                    DTOagentConclusions agentConclusionsStatus = engine.fetchNextCandidates(usernameFromSession);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println(gson.toJson(agentConclusionsStatus));
                    break;
                case ALLIE:
                    //engine.fetchAllCandidates()
                    break;

            }

        }
    }
}
