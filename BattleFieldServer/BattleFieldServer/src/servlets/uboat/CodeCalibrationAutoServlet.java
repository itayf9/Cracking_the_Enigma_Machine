package servlets.uboat;

import com.google.gson.Gson;
import constants.Constants;
import engine.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.SessionUtils;

import java.io.IOException;

import static utils.ServletUtils.validateAuthorization;

public class CodeCalibrationAutoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);

        resp.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(req);
        boolean isValidSession = validateAuthorization(usernameFromSession, resp, gson);
        Client typeOfClient = SessionUtils.getTypeOfClient(req);

        if (isValidSession) {

            if (!typeOfClient.equals(Client.UBOAT)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return;
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println(gson.toJson(engine.selectConfigurationAuto(usernameFromSession)));
        }

    }
}
