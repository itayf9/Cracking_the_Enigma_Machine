package servlets.common;

import com.google.gson.Gson;
import http.url.Client;
import http.url.Constants;
import dto.DTObattlefields;
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

public class FetchBattlefieldsInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        resp.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(req);
        boolean isValidSession = validateAuthorization(usernameFromSession, resp, gson);
        Client typeOfClient = SessionUtils.getTypeOfClient(req);
        if (isValidSession) {
            if (!typeOfClient.equals(Client.ALLIE)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return;
            }

            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);

            String onlyMyBattlefield = req.getParameter(QueryParameter.ONLY_MY);
            String uboatName = req.getParameter(QueryParameter.UBOAT_NAME);
            boolean isOnlyMyBattlefield = Boolean.parseBoolean(onlyMyBattlefield);

            if (!isOnlyMyBattlefield || uboatName == null) {
                isOnlyMyBattlefield = false;
            }

            DTObattlefields battlefieldsStatus = engine.getBattleFieldsInfo(uboatName, isOnlyMyBattlefield);
            if (!battlefieldsStatus.isSucceed()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            resp.getWriter().println(gson.toJson(battlefieldsStatus));
        }
    }
}