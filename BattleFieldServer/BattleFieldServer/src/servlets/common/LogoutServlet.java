package servlets.common;

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
            if (!typeOfClient.equals(Client.UBOAT) && !typeOfClient.equals(Client.ALLIE) && !typeOfClient.equals(Client.AGENT)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return;
            }
            switch(typeOfClient){
                case UBOAT:
                    DTOstatus uboatRemoveStatus = engine.removeBattlefield(usernameFromSession);
                    if (!uboatRemoveStatus.isSucceed()) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    } else {
                        req.getSession().invalidate();
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }
                    resp.getWriter().println(gson.toJson(uboatRemoveStatus));
                    return;
                case ALLIE:
                    String uboatName = req.getParameter(QueryParameter.UBOAT_NAME);
                    if (uboatName == null) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UBOAT_NAME_DOESNT_EXIST)));
                        return;
                    }

                    DTOstatus allieRemoveStatus = engine.removeAllie(uboatName, usernameFromSession);
                    if (!allieRemoveStatus.isSucceed()) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    } else {
                        req.getSession().invalidate();
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }
                    resp.getWriter().println(gson.toJson(allieRemoveStatus));
                    return;
                case AGENT:
                    String allieName = req.getParameter(QueryParameter.ALLIE_NAME);
                    DTOstatus agentRemoveStatus = engine.removeAgent(usernameFromSession, allieName);
                    if (!agentRemoveStatus.isSucceed()) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    } else {
                        req.getSession().invalidate();
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }
                    resp.getWriter().println(gson.toJson(agentRemoveStatus));
            }
        }
    }
}
