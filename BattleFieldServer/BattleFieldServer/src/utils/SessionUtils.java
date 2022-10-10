package utils;

import http.url.Client;
import http.url.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionUtils {

    public static String getUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(Constants.USERNAME) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static void clearSession(HttpServletRequest request) {
        request.getSession().invalidate();
    }

    public static Client getTypeOfClient(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null ? (Client) session.getAttribute(Constants.CLIENT_TYPE) : Client.UNAUTHORIZED;
    }
}