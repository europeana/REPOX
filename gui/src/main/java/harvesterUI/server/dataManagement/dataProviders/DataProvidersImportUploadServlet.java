package harvesterUI.server.dataManagement.dataProviders;

import harvesterUI.server.util.Util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import pt.utl.ist.repox.configuration.ConfigSingleton;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 27-05-2011
 * Time: 11:43
 */
public class DataProvidersImportUploadServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(DataProvidersImportUploadServlet.class);

    public void doPost(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());

        try{
            List<FileItem> fileItems = upload.parseRequest(request);

            for(FileItem item: fileItems) {
                File tempDir = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getTempDir();
                FileUtils.forceMkdir(tempDir);
                File tempFile = new File(tempDir, "temp" + String.valueOf(new Date().getTime()) + ".xml");
                item.write(tempFile);

                File repPath = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getRepositoryPath());

                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().loadDataProvidersFromFile(tempFile, repPath);
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().initialize(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().loadDataSourceContainers());

                FileUtils.forceDelete(tempFile);
                Util.addLogEntry("Data Providers imported successfully import",logger);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

