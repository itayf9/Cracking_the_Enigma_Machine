package servlets.allies;

import com.google.gson.Gson;
import constants.Client;
import constants.Constants;
import dto.DTOstaticContestInfo;
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

public class FetchStaticInfoGameServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
        resp.setContentType("application/json");
        String usernameFromSession = SessionUtils.getUsername(req);
        Client typeOfClient = SessionUtils.getTypeOfClient(req);
        boolean isValidSession = validateAuthorization(usernameFromSession, resp, gson);

        if (isValidSession) {
            if (!typeOfClient.equals(Client.ALLIE)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return;
            }

            String uboatName = req.getParameter(Constants.UBOAT_NAME);
            if (uboatName == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.NO_UBOAT_NAME)));
                return;
            }
            DTOstaticContestInfo staticContestInfo = engine.getStaticContestInfo(uboatName);
            if (!staticContestInfo.isSucceed()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            resp.getWriter().println(gson.toJson(staticContestInfo));
        }
    }
}
