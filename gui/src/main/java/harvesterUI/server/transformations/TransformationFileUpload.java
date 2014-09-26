package harvesterUI.server.transformations;

import harvesterUI.server.util.Util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.metadataTransformation.TransformationsFileManager;

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
public class TransformationFileUpload extends HttpServlet {

    private static Logger logger = Logger.getLogger(TransformationFileUpload.class);

    private static boolean copySuccessful;

    private static boolean bFileUploaded;

    public void doPost(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        copySuccessful = false;
        bFileUploaded = false;
        try{
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
            List<FileItem> fileItems = upload.parseRequest(request);

            for(FileItem item: fileItems) {
                String name = item.getName();

                if(name == null || name.equals("null") || name.isEmpty())
                    continue;
                bFileUploaded = true;

                InputStream stream = item.getInputStream();
                File xsltDir = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().getXsltDir();
                int maxFileSize = 10485760; //10 megs max
                TransformationsFileManager.Response result = TransformationsFileManager.writeXslFile(name, xsltDir, stream);

                switch(result) {
                    case ERROR:
                        copySuccessful = false;
                    break;
                    case FILE_TOO_BIG:
                        Util.addLogEntry("File is > than " + maxFileSize,logger);
                        copySuccessful = false;
                    break;
                    case SUCCESS:
                        copySuccessful = true;
                    break;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            copySuccessful = false;
        }
        copySuccessful = true;
    }

    public static boolean isCopySuccessful() {
        return copySuccessful;
    }

    public static boolean isFileUploaded() {
        return bFileUploaded;
    }
}

