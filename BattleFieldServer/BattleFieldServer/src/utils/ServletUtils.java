package utils;

import battlefield.Battlefield;
import constants.Constants;
import engine.Engine;
import engine.EnigmaEngine;
import jakarta.servlet.ServletContext;

import java.util.Map;

public class ServletUtils {

    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";

    /*
    Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
    the actual fetch of them is remained un-synchronized for performance POV
     */
    private static final Object userManagerLock = new Object();
    private static final Object chatManagerLock = new Object();

    public static Map<String, Battlefield> getUboatName2battleField(ServletContext servletContext) {

        synchronized (userManagerLock) {
            if (servletContext.getAttribute(Constants.ENGINE) == null) {
                servletContext.setAttribute(Constants.ENGINE, new EnigmaEngine());
            }
        }
        Engine engine = (Engine) servletContext.getAttribute(Constants.ENGINE);

        return engine.getBattleFieldManager();
    }

    /*public static ChatManager getChatManager(ServletContext servletContext) {
        synchronized (chatManagerLock) {
            if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
            }
        }
        return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
    }*/

    /*public static int getIntParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        return INT_PARAMETER_ERROR;
    }*/
}
