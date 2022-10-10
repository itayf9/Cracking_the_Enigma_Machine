package servlets.uboat;

import battlefield.Battlefield;
import com.google.gson.Gson;

import constants.Client;
import http.url.Constants;
import dm.decryptmanager.DecryptManager;
import dto.DTOstatus;
import engine.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import problem.Problem;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static utils.ServletUtils.validateAuthorization;

public class WinnerFoundServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        String usernameFromSession = SessionUtils.getUsername(req);
        boolean isValidSession = validateAuthorization(usernameFromSession, resp, new Gson());
        Client typeOFClient = SessionUtils.getTypeOfClient(req);

        if (isValidSession) {
            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
            if (!typeOFClient.equals(Client.UBOAT)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return;
            }

            String allieName = req.getParameter(Constants.ALLIE_NAME);
            if (allieName == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.MISSING_QUERY_PARAMETER)));
            }
            // check if battlefield isActive
            Map<String, Battlefield> battlefields = ServletUtils.getUboatName2battleField(getServletContext());
            AtomicBoolean isContestActive = battlefields.get(usernameFromSession).isActive();
            if (isContestActive.get()) {

                // check if winner allie name is exist
                Set<DecryptManager> allies = battlefields.get(usernameFromSession).getAllies();
                Optional<DecryptManager> allieMaybe = allies.stream().filter(DecryptManager -> DecryptManager.getAllieName().equals(allieName)).findFirst();
                if (!allieMaybe.isPresent()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.ALLIE_NAME_NOT_FOUND)));
                    return;
                }

                // now take care of all the collectors and producers of all dms participating
                DTOstatus stopStatus = engine.stopBruteForceProcess(usernameFromSession);
                DTOstatus winnerStatus = engine.setAllieWinnerInfo(usernameFromSession, allieName);

                if (!stopStatus.isSucceed()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println(gson.toJson(stopStatus));
                } else if (!winnerStatus.isSucceed()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println(gson.toJson(winnerStatus));
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    // since both status succeeded we can send one arbitrarily
                    resp.getWriter().println(gson.toJson(winnerStatus));
                }
            }
        }
    }
}
