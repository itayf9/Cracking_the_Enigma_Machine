package servlets;

import constants.Constants;
import engine.Engine;
import engine.EnigmaEngine;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.UBoatManager;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static constants.Constants.USERNAME;

public class LightWeightLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        System.out.println("got to login servlet");

        String usernameFromSession = SessionUtils.getUsername(request);
        UBoatManager uBoatManager = ServletUtils.getUBoatManager(getServletContext());

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
                    if (uBoatManager.isUboatExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";

                        // stands for unauthorized as there is already such user with this name
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getOutputStream().print(errorMessage);
                    } else {
                        //add the new user to the users list
                        uBoatManager.addUboatName(usernameFromParameter);
                        ServletContext servletContext = getServletContext();
                        Map<String, Engine> userName2engine;
                        if (servletContext.getAttribute(Constants.MAP_OF_ENGINES_ATTRIBUTE_NAME) == null) {
                            userName2engine = new HashMap<>();
                            servletContext.setAttribute(Constants.MAP_OF_ENGINES_ATTRIBUTE_NAME, userName2engine);
                        }

                        System.out.println(EnigmaEngine.class);

                        // engine.EnigmaEngine

                        userName2engine = (Map<String, Engine>) servletContext.getAttribute(Constants.MAP_OF_ENGINES_ATTRIBUTE_NAME);
                        request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);
                        userName2engine.put(usernameFromParameter, new EnigmaEngine());

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
