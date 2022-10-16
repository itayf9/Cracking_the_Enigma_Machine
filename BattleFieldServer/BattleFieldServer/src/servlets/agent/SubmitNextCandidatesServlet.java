package servlets.agent;

import candidate.AgentConclusion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.DTOagentConclusions;
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
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static utils.ServletUtils.validateAuthorization;

public class SubmitNextCandidatesServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        resp.setContentType("application/json");
        String usernameFromSession = SessionUtils.getUsername(req);
        Client typeOfClient = SessionUtils.getTypeOfClient(req);
        boolean isValidSession = validateAuthorization(usernameFromSession, resp, gson);

        if (isValidSession) {
            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
            if (!typeOfClient.equals(Client.AGENT)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return;
            }

            String uboatName = req.getParameter(QueryParameter.UBOAT_NAME);
            String allieName = req.getParameter(QueryParameter.ALLIE_NAME);
            if (uboatName == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.NO_UBOAT_NAME)));
            } else if (allieName == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.NO_UBOAT_NAME)));
            } else {

                // get the conclusions
                String conclusionsAsStr = readFromInputStream(req.getInputStream());
                DTOagentConclusions conclusionsDto = gson.fromJson(conclusionsAsStr, DTOagentConclusions.class);
                DTOstatus submitStatus = engine.submitConclusions(conclusionsDto.getAgentConclusions(), allieName, uboatName);
                if (!submitStatus.isSucceed()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
                resp.getWriter().println(gson.toJson(submitStatus));
            }
        }
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}
