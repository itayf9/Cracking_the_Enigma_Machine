package servlets.common;

import battlefield.Battlefield;
import com.google.gson.Gson;
import constants.Client;
import http.url.Constants;
import dto.DTOactive;
import dto.DTOstatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import problem.Problem;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.Map;

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
            if (!typeOfClient.equals(Client.AGENT) && !typeOfClient.equals(Client.ALLIE)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return;
            }

            String uboatName = req.getParameter(Constants.UBOAT_NAME);
            if (uboatName == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.NO_UBOAT_NAME)));
                return;
            }

            Map<String, Battlefield> battlefields = ServletUtils.getUboatName2battleField(getServletContext());
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println(gson.toJson(new DTOactive(true, Problem.NO_PROBLEM, battlefields.get(usernameFromSession).isActive().get())));
        }
    }
}
