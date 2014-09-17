package harvesterUI.server.util;

/**
 * Created to project REPOX.
 * User: Edmundo
 * Date: 15/03/13
 * Time: 14:09
 */
import org.apache.log4j.PropertyConfigurator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Log4jInitServlet extends HttpServlet {

    public
    void init() {
        String log4jConfigurationFile = System.getProperty("repox.log4j.configuration");
        if(log4jConfigurationFile != null) {
            PropertyConfigurator.configure(log4jConfigurationFile);
            System.out.println("Load Config files from Java Variables -- Pre-Configured Log4j File found for REPOX");
        }else
            System.out.println("Load Config files from Java Variables -- No Pre-Configured Log4j File found for REPOX");
    }

    public
    void doGet(HttpServletRequest req, HttpServletResponse res) {
    }
}
