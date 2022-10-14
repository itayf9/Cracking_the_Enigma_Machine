package servlets.agent;

import com.google.gson.Gson;
import http.url.Constants;
import dto.DTOloggedAllies;
import dto.DTOstatus;
import engine.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import problem.Problem;

import java.io.IOException;
import java.util.HashSet;

public class FetchAllLoggedAlliesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        Gson gson = new Gson();
        String clientTypeStr = req.getParameter(Constants.CLIENT_TYPE);

        if (clientTypeStr != null && clientTypeStr.equals("agent")) {

            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
            if (engine != null) { // if engine exist we send all logged allies info
                resp.getWriter().println(gson.toJson(engine.fetchAllLoggedAllies()));
            } else { // when there is no engine it means there are no logged allies and the returned value is an empty set
                resp.getWriter().println(gson.toJson(new DTOloggedAllies(true, Problem.NO_PROBLEM, new HashSet<>())));
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
        }
    }
}
