package servlets.uboat;

import battlefield.Battlefield;
import com.google.gson.Gson;
import http.url.Client;
import http.url.Constants;
import dto.DTOspecs;
import dto.DTOstatus;
import engine.Engine;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import problem.Problem;
import utils.SessionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

import static utils.ServletUtils.validateAuthorization;

@WebServlet(name = "Load XML", urlPatterns = "/load")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class LoadXMLServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        String usernameFromSession = SessionUtils.getUsername(req);
        boolean isValidSession = validateAuthorization(usernameFromSession, resp, new Gson());
        Client typeOFClient = SessionUtils.getTypeOfClient(req);

        if (isValidSession) {

            if (!typeOFClient.equals(Client.UBOAT)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
                return;
            }

            // check if username is valid/existing Uboat client
            ServletContext servletContext = getServletContext();
            Engine engine = (Engine) servletContext.getAttribute(Constants.ENGINE);
            Map<String, Battlefield> userName2battlefield = engine.getBattleFieldManager();
            if (userName2battlefield.containsKey(usernameFromSession) && req.getParts().size() == 1) { // username exists and exactly 1 file was sent

                Collection<Part> parts = req.getParts();
                String fileContent = "";

                for (Part file : parts) {
                    //to write the content of the file to a string
                    fileContent = readFromInputStream(file.getInputStream());
                }
                DTOspecs loadStatus = engine.buildMachineFromXmlFile(fileContent, usernameFromSession);
                if (!loadStatus.isSucceed()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } else { // all ok
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
                resp.getWriter().println(gson.toJson(loadStatus));
            } else if (userName2battlefield.containsKey(usernameFromSession)) { // more parts than 1
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.MORE_THAN_ONE_FILE)));
            } else { // if no key in map
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.UNAUTHORIZED_CLIENT_ACCESS)));
            }
        }
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}
