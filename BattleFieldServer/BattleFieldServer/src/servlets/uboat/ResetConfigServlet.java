package servlets.uboat;

import com.google.gson.Gson;
import dto.DTOciphertext;
import dto.DTOresetConfig;
import dto.DTOspecs;
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

public class ResetConfigServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        resp.setContentType("application/json");

        // get userName of uboat
        String userNameFromSession = SessionUtils.getUsername(req);
        boolean isValidSession = validateAuthorization(userNameFromSession, resp, gson);
        Client typeOfClient = SessionUtils.getTypeOfClient(req);

        if (isValidSession) {

            // get engine from context
            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
            if (!typeOfClient.equals(Client.UBOAT)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return;
            }

            DTOresetConfig resetStatus = engine.resetConfiguration(userNameFromSession);
            if (!resetStatus.isSucceed()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            resp.getWriter().println(gson.toJson(resetStatus));
        }
    }
}
