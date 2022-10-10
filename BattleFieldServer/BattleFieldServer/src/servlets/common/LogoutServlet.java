package servlets.common;

import com.google.gson.Gson;
import http.url.Client;
import http.url.Constants;
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

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

            DTOstatus approvalStatus = engine.removeBattlefield(usernameFromSession);
            if (!approvalStatus.isSucceed()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                req.getSession().invalidate();
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            resp.getWriter().println(gson.toJson(approvalStatus));
        }
    }
}
