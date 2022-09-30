package servlets.uboat;

import com.google.gson.Gson;
import constants.Constants;
import dto.DTOsecretConfig;
import dto.DTOstatus;
import engine.Engine;
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
        Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
        resp.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(req);
        boolean isValid = validateAuthorization(usernameFromSession, resp, gson);

        System.out.println(req.getParameter("rotors") + "  " + req.getParameter("windows") + "  " + req.getParameter("reflector") + "  " + req.getParameter("plugs"));

        if (isValid) {

            // extract rotors from body parameter
            String rotorsIDs = req.getParameter("rotors");
            DTOstatus rotorsStatus = engine.validateRotors(rotorsIDs, usernameFromSession);
            if (!rotorsStatus.isSucceed()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(rotorsStatus));
                return;
            }

            // extract windows from body parameter
            String windowsCharacters = req.getParameter("windows");
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
                reflectorID = Integer.parseInt(req.getParameter("reflector"));
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
            String plugs = req.getParameter("plugs");
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
