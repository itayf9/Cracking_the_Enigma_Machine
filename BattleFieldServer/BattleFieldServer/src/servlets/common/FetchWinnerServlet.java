package servlets.common;

import com.google.gson.Gson;
import dto.DTOstatus;
import dto.DTOwinner;
import engine.Engine;
import http.url.Client;
import http.url.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import problem.Problem;
import utils.SessionUtils;

import java.io.IOException;

import static utils.ServletUtils.validateAuthorization;

public class FetchWinnerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

            if (!typeOfClient.equals(Client.ALLIE)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return;
            }

            uboatName = req.getParameter(Constants.UBOAT_NAME);
            if (uboatName == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.NO_UBOAT_NAME)));
                return;
            }

            DTOwinner winnerStatus = engine.getAllieWinnerInfo(uboatName);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println(gson.toJson(winnerStatus));
        }
    }
}
