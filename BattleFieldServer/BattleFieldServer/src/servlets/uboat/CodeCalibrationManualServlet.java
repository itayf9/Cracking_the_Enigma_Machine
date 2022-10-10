package servlets.uboat;

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


public class CodeCalibrationManualServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        resp.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(req);
        boolean isValidSession = validateAuthorization(usernameFromSession, resp, gson);
        Client typeOfClient = SessionUtils.getTypeOfClient(req);

        if (isValidSession) {
            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);

            if (!typeOfClient.equals(Client.UBOAT)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return;
            }

            // extract rotors from body parameter
            String rotorsIDs = req.getParameter(QueryParameter.ROTORS_IDS);
            DTOstatus rotorsStatus = engine.validateRotors(rotorsIDs, usernameFromSession);
            if (!rotorsStatus.isSucceed()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(rotorsStatus));
                return;
            }

            // extract windows from body parameter
            String windowsCharacters = req.getParameter(QueryParameter.WINDOWS_CHARS);
            DTOstatus windowsStatus = engine.validateWindowCharacters(windowsCharacters, usernameFromSession);
            if (!windowsStatus.isSucceed()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(windowsStatus));
                return;
            }

            // extract reflector from body parameter
            int reflectorID = -1;
            DTOstatus reflectorStatus = new DTOstatus(false, Problem.REFLECTOR_INPUT_NOT_A_NUMBER);
            try {
                reflectorID = Integer.parseInt(req.getParameter(QueryParameter.REFLECTOR_ID));
                reflectorStatus = engine.validateReflector(reflectorID, usernameFromSession);
                if (!reflectorStatus.isSucceed()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println(gson.toJson(reflectorStatus));
                    return;
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                // decide later what to do here
            }

            // extract plugs from body parameter
            String plugs = req.getParameter(QueryParameter.PLUGS);
            DTOstatus plugsStatus = engine.validatePlugs(plugs, usernameFromSession);
            if (!plugsStatus.isSucceed()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(plugsStatus));
                return;
            }

            // assuming all is valid
            resp.getWriter().println(gson.toJson(engine.selectConfigurationManual(rotorsIDs, windowsCharacters, reflectorID, plugs, usernameFromSession)));
        }
    }
}
