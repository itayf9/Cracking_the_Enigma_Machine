package servlets.common;

import com.google.gson.Gson;
import http.url.Client;
import http.url.Constants;
import dto.DTOallies;
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

public class FetchAlliesInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        resp.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(req);
        boolean isValidSession = validateAuthorization(usernameFromSession, resp, gson);
        Client typeOfClient = SessionUtils.getTypeOfClient(req);

        if (isValidSession) {
            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);

            if (!typeOfClient.equals(Client.UBOAT) && !typeOfClient.equals(Client.ALLIE)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return;
            }

            String uboatName = "";

            switch (typeOfClient) {
                case UBOAT:
                    uboatName = usernameFromSession;
                    break;
                case ALLIE:
                    uboatName = req.getParameter(Constants.UBOAT_NAME);
                    if (uboatName == null) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.NO_UBOAT_NAME)));
                        return;
                    }
                    break;
            }

            DTOallies alliesInfoStatus = engine.getAlliesInfo(uboatName);

            if (!alliesInfoStatus.isSucceed()) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            resp.getWriter().println(gson.toJson(alliesInfoStatus));
        }
    }
}
