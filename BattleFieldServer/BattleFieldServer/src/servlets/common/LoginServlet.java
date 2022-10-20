package servlets.common;

import http.url.QueryParameter;
import info.agent.AgentInfo;
import battlefield.Battlefield;
import com.google.gson.Gson;
import http.url.Client;
import http.url.Constants;
import dto.DTOstatus;
import engine.Engine;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import problem.Problem;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();
        String usernameFromSession = SessionUtils.getUsername(request);

        Client typeOfClient = Client.getClientTypeFromString(request.getParameter(Constants.CLIENT_TYPE));

        if (typeOfClient.equals(Client.UNAUTHORIZED)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
            return;
        }

        if (usernameFromSession == null) { //user is not logged in yet
            String usernameFromParameter = request.getParameter(Constants.USERNAME);
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                // create response for bad request
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println(gson.toJson(new DTOstatus(false, Problem.MISSING_QUERY_USERNAME)));
            } else {
                switch (typeOfClient) {
                    case UBOAT:
                        Map<String, Battlefield> uboatName2battleField = ServletUtils.getUboatName2battleField(getServletContext());

                        //normalize the username value
                        usernameFromParameter = usernameFromParameter.trim();
                        synchronized (this) {
                            if (!ServletUtils.checkNameValidity(getServletContext(), usernameFromParameter)) {
                                // the username already exists
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.getWriter().println(gson.toJson(new DTOstatus(false, Problem.USERNAME_ALREADY_EXIST)));
                                return;
                            }

                            //add the new user to the users list
                            uboatName2battleField.put(usernameFromParameter, new Battlefield(usernameFromParameter));
                        }
                        break;
                    case ALLIE: //
                        Map<String, Set<AgentInfo>> loggedAlliesNames = ServletUtils.getLoggedAlliesNames(getServletContext());
                        Map<String, Boolean> loggedAlliesMap = ServletUtils.getLoggedAlliesMap(getServletContext());

                        synchronized (this) {
                            if (!ServletUtils.checkNameValidity(getServletContext(), usernameFromParameter)) {
                                // the username already exists
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.getWriter().println(gson.toJson(new DTOstatus(false, Problem.USERNAME_ALREADY_EXIST)));
                                return;
                            }

                            //add the new user to the allies list
                            loggedAlliesNames.put(usernameFromParameter, new HashSet<>());
                            loggedAlliesMap.put(usernameFromParameter, false);
                        }
                        break;
                    case AGENT:
                        String agentName = request.getParameter(Constants.USERNAME);
                        String allieNameToJoin = request.getParameter(QueryParameter.ALLIE_NAME);
                        if (allieNameToJoin == null) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            response.getWriter().println(gson.toJson(new DTOstatus(false, Problem.MISSING_ALLIE_NAME)));
                        }
                        int numOfThreads;
                        try {
                            numOfThreads = Integer.parseInt(request.getParameter(QueryParameter.NUM_OF_THREADS));
                        } catch (NullPointerException | NumberFormatException e) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            response.getWriter().println(gson.toJson(new DTOstatus(false, Problem.MISSING_NUM_OF_THREADS)));
                            return;
                        }
                        int numOfMissionsToPull;
                        try {
                            numOfMissionsToPull = Integer.parseInt(request.getParameter(QueryParameter.MISSION_COUNT));
                        } catch (NullPointerException | NumberFormatException e) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            response.getWriter().println(gson.toJson(new DTOstatus(false, Problem.MISSING_MISSION_COUNT)));
                            return;
                        }

                        if (agentName != null && allieNameToJoin != null && numOfThreads > 0 && numOfThreads < 5 && numOfMissionsToPull > 0) {
                            synchronized (this) {
                                if (!ServletUtils.checkNameValidity(getServletContext(), usernameFromParameter)) {
                                    // the username already exists
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.getWriter().println(gson.toJson(new DTOstatus(false, Problem.USERNAME_ALREADY_EXIST)));
                                    return;
                                }

                                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
                                DTOstatus assignStatus = engine.assignAgentToAllie(agentName, allieNameToJoin, numOfThreads, numOfMissionsToPull);
                                if (!assignStatus.isSucceed()) {
                                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                    response.getWriter().println(gson.toJson(assignStatus));
                                    return;
                                }
                            }

                        }
                        break;
                }
                System.out.println("client " + typeOfClient + " with name of " + usernameFromParameter + " logged successfully to the server");
                // assuming we got here then all ok
                request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);
                request.getSession(true).setAttribute(Constants.CLIENT_TYPE, typeOfClient);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(gson.toJson(new DTOstatus(true, Problem.NO_PROBLEM)));

            }
        } else {
            //user is already logged in
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
