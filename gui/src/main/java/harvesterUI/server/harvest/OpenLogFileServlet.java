package harvesterUI.server.harvest;

import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.shared.ServerSideException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.util.XmlUtil;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 14-03-2012
 * Time: 10:46
 */
public class OpenLogFileServlet extends HttpServlet {

    private int MAXIMUM_FILE_SIZE_IN_MEGABYTES = 2;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        super.doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        String dataSetId = request.getParameter("dataSetId");
        String logName = request.getParameter("logName");

        DataSource dataSource = null;
        try {
            dataSource = RepoxServiceImpl.getRepoxManager().getDataManager().getDataSourceContainer(dataSetId).getDataSource();
        } catch (DocumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ServerSideException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        String logPath = "";
        if(logName == null){
            String delimFolder = "\\\\";
            assert dataSource != null;
            String[] tokensFolder = dataSource.getLogFilenames().get(0).split(delimFolder);
            String correctFilename;
            if(tokensFolder.length > 1)
                correctFilename = tokensFolder[0] + "/" + tokensFolder[1];
            else
                correctFilename = dataSource.getLogFilenames().get(0);
            logPath = ConfigSingleton.getRepoxContextUtil().getRepoxManager()
                    .getConfiguration().getRepositoryPath()+ "/"+dataSource.getId()+ "/logs/" + correctFilename;
        }
        else{
            assert dataSource != null;
            for(String currentLogName : dataSource.getLogFilenames()) {
                if(currentLogName.equals(logName)) {
                    logPath = ConfigSingleton.getRepoxContextUtil().getRepoxManager()
                            .getConfiguration().getRepositoryPath()+ "/"+dataSource.getId()+ "/logs/" + logName;
                    break;
                }
            }
        }

        ServletOutputStream out = response.getOutputStream();
        File logFile = new File(logPath);
        if(isFileTooBig(logFile)){
            returnZipFile(out,response,logFile);
        }else {
            String fileAsString = XmlUtil.readFileAsString(logFile);
            checkContentType(logPath,fileAsString,out,response);
        }
    }

    private void returnZipFile(ServletOutputStream op,HttpServletResponse response, File logFile){
        File zipFile = createZipFile(logFile.getName(),logFile);
        try{
            int length;
            response.setContentType("application/zip");
            response.setContentLength((int) zipFile.length());
            response.setHeader("Content-Disposition", "attachment; filename="+ zipFile.getName());

            byte[] bbuf = new byte[1024];
            DataInputStream in = new DataInputStream(new FileInputStream(zipFile));

            while ((in != null) && ((length = in.read(bbuf)) != -1)) {
                op.write(bbuf, 0, length);
            }

            in.close();
            op.flush();
            op.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        zipFile.delete();
    }

    private void checkContentType(String logPath,String result, ServletOutputStream out,
                                  HttpServletResponse response) throws IOException {
        try {
            response.setContentType("text/xml");
            SAXReader reader = new SAXReader();
            Document newNodeDocument = reader.read(new StringReader(result));

            XmlUtil.writePrettyPrint(out, newNodeDocument.getRootElement());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (DocumentException e) {
            response.setContentType("text/plain");
            writePlainTextOutput(logPath,out);
        }
    }

    private void writePlainTextOutput(String logPath, ServletOutputStream out) throws IOException {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(logPath));
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(isr);
            String text = "";

            while ((text = reader.readLine()) != null) {
                out.print(text + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if (in != null) in.close();  // very important
        }
    }

    private boolean isFileTooBig(File logFile){
        double bytes = logFile.length();
        double kilobytes = (bytes / 1024);
        double megabytes = (kilobytes / 1024);
        return megabytes > MAXIMUM_FILE_SIZE_IN_MEGABYTES;
    }

    private File createZipFile(String zipFilename,File logFile1){
        // These are the files to include in the ZIP file
        String[] files = new String[]{logFile1.getAbsolutePath()};
        String[] filenames = new String[]{logFile1.getName()};

        // Create a buffer for reading the files
        byte[] buf = new byte[1024];

        File zippedFile = new File(logFile1.getParentFile().getAbsolutePath()+ File.separator + zipFilename+".zip");
        try {
            // Create the ZIP file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zippedFile));

            // Compress the files
            for (int i=0; i<filenames.length; i++) {
                FileInputStream in = new FileInputStream(files[i]);

                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(filenames[i]));

                // Transfer bytes from the file to the ZIP file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                // Complete the entry
                out.closeEntry();
                in.close();
            }

            // Complete the ZIP file
            out.close();
        } catch (IOException e) {
        }
        return zippedFile;
    }
}
