package servlets.uboat;

import battlefield.Battlefield;
import com.google.gson.Gson;
import constants.Constants;
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

        String usernameFromSession = SessionUtils.getUsername(req);

        boolean isValid = validateAuthorization(usernameFromSession, resp, new Gson());

        if (isValid) {
            // check if username is valid/existing Uboat client
            ServletContext servletContext = getServletContext();
            Engine engine = (Engine) servletContext.getAttribute(Constants.ENGINE);
            Map<String, Battlefield> userName2battlefield = engine.getBattleFieldManager();

            if (userName2battlefield.containsKey(usernameFromSession)) {
                if (req.getParts().size() == 1) {
                    Collection<Part> parts = req.getParts();

                    String fileContent = "";
                    for (Part file : parts) {
                        //to write the content of the file to a string
                        fileContent = readFromInputStream(file.getInputStream());
                    }

                    DTOstatus loadXMLstatus = engine.buildMachineFromXmlFile(fileContent, usernameFromSession);

                    if (!loadXMLstatus.isSucceed()) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().println(loadXMLstatus.getDetails().name());
                    } else { // all ok
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().println("build successfully.");
                    }

                } else { // more parts than 1
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println("please upload only 1 file");
                }
            } else { // if no key in map
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println("no UBoat Client exist with that name specified");
            }
        }
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}
