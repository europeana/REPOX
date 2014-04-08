package harvesterUI.server.transformations;

import pt.utl.ist.repox.util.ConfigSingleton;

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
 * Date: 27-05-2011
 * Time: 11:43
 */
public class XslFileDownloadServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        try{
            String filename = (String)request.getParameter("fileName");
            String filePath = filename + ".xsl";
            File xsltFile = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    getXsltFile(filename);

            int length   = 0;
            ServletOutputStream op = response.getOutputStream();
            ServletContext context = getServletConfig().getServletContext();
            String mimetype = context.getMimeType( filePath );

            response.setContentType( (mimetype != null) ? mimetype : "application/octet-stream" );
            response.setContentLength( (int)xsltFile.length() );
            response.setHeader( "Content-Disposition", "attachment; filename=\"" + filePath + "\"" );


            byte[] bbuf = new byte[4096];
            DataInputStream in = new DataInputStream(new FileInputStream(xsltFile));

            while ((in != null) && ((length = in.read(bbuf)) != -1)) {
                op.write(bbuf,0,length);
            }

            in.close();
            op.flush();
            op.close();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}


