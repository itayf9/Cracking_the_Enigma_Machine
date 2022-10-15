package servlets.agent;

import com.google.gson.Gson;
import dto.DTOactive;
import dto.DTOstatus;
import engine.Engine;
import http.url.Client;
import http.url.Constants;
import http.url.QueryParameter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import problem.Problem;
import utils.SessionUtils;

import java.io.IOException;

import static utils.ServletUtils.validateAuthorization;

public class FetchSubscriptionStatusServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        resp.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(req);
        boolean isValidSession = validateAuthorization(usernameFromSession, resp, gson);
        Client typeOfClient = SessionUtils.getTypeOfClient(req);
        if (isValidSession) {
            if (!typeOfClient.equals(Client.AGENT)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return;
            }
            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
            String allieName;

            allieName = req.getParameter(QueryParameter.ALLIE_NAME);
            if (allieName == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.NO_ALLIE_NAME)));
            } else {
                DTOactive activeStatus = engine.checkIfAllieIsSubscribedToContest(allieName);
                if (!activeStatus.isSucceed()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
                resp.getWriter().println(gson.toJson(activeStatus));
            }
        }
    }
}
