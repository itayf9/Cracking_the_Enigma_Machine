package servlets.common;

import agent.AgentInfo;
import battlefield.Battlefield;
import com.google.gson.Gson;
import constants.Client;
import constants.Constants;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();
        String usernameFromSession = SessionUtils.getUsername(request);

        Client typeOfClient = Client.getClient(request.getParameter(Constants.CLIENT_TYPE));

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
                            if (uboatName2battleField.containsKey(usernameFromParameter)) {
                                // the username already exists
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.getWriter().println(gson.toJson(new DTOstatus(false, Problem.USERNAME_ALREADY_EXIST)));
                                return;
                            }

                            //add the new user to the users list
                            uboatName2battleField.put(usernameFromParameter, new Battlefield());
                        }
                        break;
                    case ALLIE: //
                        Map<String, Set<AgentInfo>> loggedAlliesNames = ServletUtils.getLoggedAlliesNames(getServletContext());

                        synchronized (this) {
                            if (loggedAlliesNames.containsKey(usernameFromParameter)) {
                                // the username already exists
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.getWriter().println(gson.toJson(new DTOstatus(false, Problem.USERNAME_ALREADY_EXIST)));
                                return;
                            }

                            //add the new user to the allies list
                            loggedAlliesNames.put(usernameFromParameter, new HashSet<>());
                        }
                        break;
                    case AGENT:
                        String agentName = request.getParameter(Constants.USERNAME);
                        String allieNameToJoin = request.getParameter(Constants.ALLIE_NAME);
                        int numOfThreads;
                        try {
                            numOfThreads = Integer.parseInt(request.getParameter(Constants.NUM_OF_THREADS));
                        } catch (NullPointerException | NumberFormatException e) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            response.getWriter().println(gson.toJson(new DTOstatus(false, Problem.MISSING_QUERY_PARAMETER)));
                            return;
                        }
                        int numOfMissionsToPull;
                        try {
                            numOfMissionsToPull = Integer.parseInt(request.getParameter(Constants.MISSION_COUNT));
                        } catch (NullPointerException | NumberFormatException e) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            response.getWriter().println(gson.toJson(new DTOstatus(false, Problem.MISSING_QUERY_PARAMETER)));
                            return;
                        }

                        if (agentName != null && allieNameToJoin != null && numOfThreads > 0 && numOfThreads < 5 && numOfMissionsToPull > 0) {

                            Map<String, AgentInfo> agentInfoMap = ServletUtils.getLoggedAgentNames(getServletContext());

                            synchronized (this) {
                                if (agentInfoMap.containsKey(usernameFromParameter)) {
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
