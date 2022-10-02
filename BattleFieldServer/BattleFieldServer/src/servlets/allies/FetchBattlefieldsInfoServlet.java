package servlets.allies;

import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Bool;
import constants.Constants;
import dto.DTObattlefields;
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

public class FetchBattlefieldsInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
        resp.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(req);
        boolean isValidSession = validateAuthorization(usernameFromSession, resp, gson);

        if (isValidSession) {
            String onlyMyBattlefield = req.getParameter(Constants.ONLY_MY);
            String uboatName = req.getParameter(Constants.UBOAT_NAME);

            boolean isOnlyMyBattlefield = Boolean.parseBoolean(onlyMyBattlefield);

            if (!isOnlyMyBattlefield || uboatName == null) {
                isOnlyMyBattlefield = false;
            }

            DTObattlefields battlefieldsStatus = engine.getBattleFieldsInfo(uboatName, isOnlyMyBattlefield);
            if (!battlefieldsStatus.isSucceed()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(battlefieldsStatus));
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().println(gson.toJson(battlefieldsStatus));
            }
        }
    }
}
