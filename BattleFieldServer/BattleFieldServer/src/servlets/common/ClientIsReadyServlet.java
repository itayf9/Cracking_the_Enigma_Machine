package servlets.common;

import com.google.gson.Gson;
import constants.Client;
import constants.Constants;

import dto.DTOstatus;
import engine.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import problem.Problem;
import utils.SessionUtils;

import java.io.IOException;

import static constants.Client.*;
import static utils.ServletUtils.validateAuthorization;

public class ClientIsReadyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();

        // get userName of uboat
        String userNameFromSession = SessionUtils.getUsername(req);
        Client typeOfClient = SessionUtils.getTypeOfClient(req);

        String uboatName = "";

        resp.setContentType("application/json");
        boolean isValid = validateAuthorization(userNameFromSession, resp, gson);
        if (isValid) {
            // get engine from context
            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);

            switch (typeOfClient) {
                case UBOAT:
                    uboatName = userNameFromSession;
                    engine.setUboatReady(userNameFromSession, true);
                    break;
                case ALLIE:
                    uboatName = req.getParameter("uboat-name");
                    if (uboatName == null) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.NO_UBOAT_NAME)));
                        return;
                    }
                    engine.setAllieReady(userNameFromSession, uboatName, true);
                    break;
                case AGENT:
                case UNAUTHORIZED:
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                    return;
            }
            if (engine.allClientsReady(uboatName)) {
                // redirect to Start contest Servlet..
            }


            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println(gson.toJson(new DTOstatus(true, Problem.NO_PROBLEM)));
        }
    }
}