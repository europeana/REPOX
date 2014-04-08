package harvesterUI.server.dataManagement.dataSets;

import harvesterUI.server.util.Util;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import pt.utl.ist.repox.z3950.IdListHarvester;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 27-05-2011
 * Time: 11:43
 */
public class Z39FileUpload extends HttpServlet {

    private static Logger logger = Logger.getLogger(Z39FileUpload.class);

    static File tempFile = null;
    static boolean ignoreUploadFile = false;

    public void doPost(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());

        try{
            List<FileItem> fileItems = upload.parseRequest(request);

            for(FileItem item: fileItems) {

                if(item.getName().equals("")){
                    ignoreUploadFile = true;
                }
                else{
                    File temporaryFile = IdListHarvester.getIdListFilePermanent();
                    item.write(temporaryFile);
                    tempFile = temporaryFile;
                    ignoreUploadFile = false;
                    Util.addLogEntry("Z39.50 uploaded successfully", logger);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public static File getZ39TempFile() {
        return tempFile;
    }

    public static void deleteTempFile() {
        tempFile = null;
        ignoreUploadFile = false;
    }

    public static boolean ignoreUploadFile() {
        return ignoreUploadFile;
    }
}

