package servlets.allies;

import battlefield.Battlefield;
import com.google.gson.Gson;
import constants.Client;
import constants.Constants;
import dm.decryptmanager.DecryptManager;
import dto.DTOagentConclusions;
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

public class SubscribeToBattlefieldServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);

        resp.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(req);
        Client typeOfClient = SessionUtils.getTypeOfClient(req);

        boolean isValid = validateAuthorization(usernameFromSession, resp, gson);

        if (isValid) {
            String uboatNameToRegister = req.getParameter(Constants.UBOAT_NAME);
            if (uboatNameToRegister == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.NO_UBOAT_NAME)));
                return;
            }

            Battlefield wantedBattlefield = engine.getBattleFieldManager().get(uboatNameToRegister);
            if (wantedBattlefield == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UBOAT_NAME_DOESNT_EXIST)));
                return;
            }
            wantedBattlefield.getAllies().add(new DecryptManager(usernameFromSession, wantedBattlefield));
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println(gson.toJson(new DTOstatus(true, Problem.NO_PROBLEM)));
        }
    }
}
