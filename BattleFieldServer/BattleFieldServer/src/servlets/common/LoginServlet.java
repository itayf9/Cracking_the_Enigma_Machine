package servlets.common;

import battlefield.Battlefield;
import constants.Constants;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static constants.Constants.USERNAME;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        System.out.println("got to login servlet");

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
                //no username in session and no username in parameter - not standard situation. it's a conflict

                // stands for conflict in server state
                response.setStatus(HttpServletResponse.SC_CONFLICT);
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
                        Set<String> loggedAlliesNames = ServletUtils.getLoggedAlliesNames(getServletContext());

                        synchronized (this) {
                            if (loggedAlliesNames.contains(usernameFromParameter)) {
                                // the username already exists
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.getWriter().println(gson.toJson(new DTOstatus(false, Problem.USERNAME_ALREADY_EXIST)));
                                return;
                            }

                            //add the new user to the allies list
                            loggedAlliesNames.add(usernameFromParameter);
                        }
                        break;
                    case AGENT:
                        break;
                }
                System.out.println("client " + typeOfClient + "with name of " + usernameFromParameter + " logged successfully to the server");
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
