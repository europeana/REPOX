package harvesterUI.server.harvest;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-09-2011
 * Time: 15:15
 */
public class DownloadExportZipFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        super.doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String fileName = req.getParameter("fileName");

        File f = new File(fileName);

        String filename = f.getName();

        int length = 0;

        try {
            ServletOutputStream op = resp.getOutputStream();
            ServletContext context = getServletConfig().getServletContext();
            resp.setContentType("application/zip");
            resp.setContentLength((int) f.length());
            resp.setHeader("Content-Disposition", "attachment; filename="+ filename);

            byte[] bbuf = new byte[1024];
            DataInputStream in = new DataInputStream(new FileInputStream(f));

            while ((in != null) && ((length = in.read(bbuf)) != -1)) {
                op.write(bbuf, 0, length);
            }

            in.close();
            op.flush();
            op.close();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
