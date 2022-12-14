package servlets.common;

import com.google.gson.Gson;
import http.url.Client;
import http.url.Constants;
import dto.DTOagentConclusions;
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

public class FetchCandidatesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();

        resp.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(req);
        Client typeOfClient = SessionUtils.getTypeOfClient(req);

        boolean isValidSession = validateAuthorization(usernameFromSession, resp, gson);

        if (isValidSession) {
            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);

            if (!typeOfClient.equals(Client.UBOAT)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return;
            }

            switch (typeOfClient) {
                case UBOAT:
                    DTOagentConclusions agentConclusionsStatus = engine.fetchNextCandidates(usernameFromSession);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println(gson.toJson(agentConclusionsStatus));
                    break;
            }
        }
    }
}
