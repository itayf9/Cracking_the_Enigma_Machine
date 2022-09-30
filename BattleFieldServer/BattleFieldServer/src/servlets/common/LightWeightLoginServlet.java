package servlets.common;

import battlefield.Battlefield;
import constants.Constants;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;
import java.io.IOException;
import java.util.Map;

import static constants.Constants.USERNAME;

public class LightWeightLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        System.out.println("got to login servlet");

        String usernameFromSession = SessionUtils.getUsername(request);
        Map<String, Battlefield> uboatName2battleField = ServletUtils.getUboatName2battleField(getServletContext());

        if (usernameFromSession == null) { //user is not logged in yet

            System.out.println("username doesn't have session");

            String usernameFromParameter = request.getParameter(USERNAME);
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                //no username in session and no username in parameter - not standard situation. it's a conflict

                // stands for conflict in server state
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } else {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();

                System.out.println("username = " + usernameFromParameter);

                synchronized (this) {
                    if (uboatName2battleField.containsKey(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";

                        // stands for unauthorized as there is already such user with this name
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getOutputStream().print(errorMessage);
                    } else {
                        //add the new user to the users list
                        uboatName2battleField.put(usernameFromParameter, new Battlefield());

                        request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);

                        //redirect the request to the chat room - in order to actually change the URL
                        System.out.println("On login, request URI is: " + request.getRequestURI());
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                }

                System.out.println("synchronized done user added successfully");
            }
        } else {
            //user is already logged in
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

}
