package utils;

import dm.decryptmanager.DecryptManager;
import info.agent.AgentInfo;
import battlefield.Battlefield;
import com.google.gson.Gson;
import http.url.Constants;
import dto.DTOstatus;
import engine.Engine;
import engine.EnigmaEngine;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import problem.Problem;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ServletUtils {

    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";

    /*
    Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
    the actual fetch of them is remained un-synchronized for performance POV
     */
    private static final Object uboatManagerLock = new Object();
    private static final Object allieManagerLock = new Object();
    private static final Object agentManagerLock = new Object();
    private static final Object engineLock = new Object();


    public static Map<String, Battlefield> getUboatName2battleField(ServletContext servletContext) {

        synchronized (uboatManagerLock) {
            if (servletContext.getAttribute(Constants.ENGINE) == null) {
                servletContext.setAttribute(Constants.ENGINE, new EnigmaEngine());
            }
        }
        Engine engine = (Engine) servletContext.getAttribute(Constants.ENGINE);

        return engine.getBattleFieldManager();
    }

    public static boolean validateAuthorization(String usernameFromSession, HttpServletResponse resp, Gson gson) {

        if (usernameFromSession == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try {
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    public static Map<String, Set<AgentInfo>> getLoggedAlliesNames(ServletContext servletContext) {
        synchronized (allieManagerLock) {
            if (servletContext.getAttribute(Constants.ENGINE) == null) {
                servletContext.setAttribute(Constants.ENGINE, new EnigmaEngine());
            }
        }
        Engine engine = (Engine) servletContext.getAttribute(Constants.ENGINE);

        return engine.getLoggedAlliesNamesManager();
    }

    public static Map<String, Boolean> getLoggedAlliesMap(ServletContext servletContext) {
        synchronized (allieManagerLock) {
            if (servletContext.getAttribute(Constants.ENGINE) == null) {
                servletContext.setAttribute(Constants.ENGINE, new EnigmaEngine());
            }
        }
        Engine engine = (Engine) servletContext.getAttribute(Constants.ENGINE);

        return engine.getLoggedAlliesMap();
    }

    public static Map<String, AgentInfo> getLoggedAgentNames(ServletContext servletContext) {
        synchronized (agentManagerLock) {
            if (servletContext.getAttribute(Constants.ENGINE) == null) {
                servletContext.setAttribute(Constants.ENGINE, new EnigmaEngine());
            }
        }
        Engine engine = (Engine) servletContext.getAttribute(Constants.ENGINE);

        return engine.getLoggedAgentNamesManager();
    }

    public static boolean checkNameValidity(ServletContext servletContext, String usernameFromSession) {
        synchronized (engineLock) {
            if (servletContext.getAttribute(Constants.ENGINE) == null) {
                servletContext.setAttribute(Constants.ENGINE, new EnigmaEngine());
            }
        }
        Engine engine = (Engine) servletContext.getAttribute(Constants.ENGINE);
        return engine.checkNameValidity(usernameFromSession);
    }
}
