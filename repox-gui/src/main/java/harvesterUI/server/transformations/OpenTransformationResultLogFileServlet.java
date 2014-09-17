package harvesterUI.server.transformations;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.util.XmlUtil;
import pt.utl.ist.util.TransformationResultLogger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 14-03-2012
 * Time: 10:46
 */
public class OpenTransformationResultLogFileServlet extends HttpServlet {

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
        String transformationId = request.getParameter("transformationId");
        ServletOutputStream out = response.getOutputStream();

        File logFile = TransformationResultLogger.getTransformationResultFile(dataSetId,transformationId);
        if(!logFile.exists()){
            createFileNotFoundResponse(out,response);
            return;
        }

        if(isFileTooBig(logFile)){
            returnZipFile(out,response,logFile);
        }else {
            returnXmlResponse(logFile, out, response);
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

    private void returnXmlResponse(File logXMLFile, ServletOutputStream out,
                                   HttpServletResponse response) throws IOException {
        try {
            response.setContentType("text/xml");
            SAXReader reader = new SAXReader();
            Document newNodeDocument = reader.read(logXMLFile);

            XmlUtil.writePrettyPrint(out, newNodeDocument.getRootElement());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void createFileNotFoundResponse(ServletOutputStream out,
                                   HttpServletResponse response) throws IOException {
        try {
            response.setContentType("text/xml");
            Document newNodeDocument = DocumentHelper.createDocument();
            Element root = newNodeDocument.addElement("response");
            root.addElement("error").addText("File Not found");
            root.addElement("cause").addText("Data Set ID or Transformation ID might be wrong");

            XmlUtil.writePrettyPrint(out, newNodeDocument.getRootElement());
        } catch (IOException e) {
            e.printStackTrace();
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
