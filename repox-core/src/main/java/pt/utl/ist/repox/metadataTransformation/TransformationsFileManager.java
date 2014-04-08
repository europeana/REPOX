package pt.utl.ist.repox.metadataTransformation;

import java.io.*;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 01-02-2013
 * Time: 11:41
 */
public class TransformationsFileManager {
    public enum Response {
        SUCCESS,
        XSL_ALREADY_EXISTS,
        FILE_TOO_BIG,
        ERROR
    }

    //WRITING
    public static Response writeXslFile(String filename, File xsltDir, InputStream stream) {
        Response result = Response.SUCCESS;

        if(!xsltDir.exists())
            if(xsltDir.mkdirs())
                System.out.println("[INFO] XSLT dir was created.");
            else
                System.out.println("[INFO] XSLT dir couldn't be created.");

        File tmpFile = new File(xsltDir, filename.toLowerCase());

        try {
            // Process the input stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[8192];
            while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
            }

            int maxFileSize = 10485760; //10 megs max
            if (out.size() > maxFileSize) {
                //Util.addLogEntry("File is > than " + maxFileSize,logger);
                result = Response.FILE_TOO_BIG;
                System.out.println("[INFO] Error writing XSL file: the file is too big (10MB max)");
                return result;
            }

            FileWriter fstream = new FileWriter(tmpFile);
            BufferedWriter outFile = new BufferedWriter(fstream);
            outFile.write(out.toString());
            outFile.close();
            System.out.println("[INFO] "+ filename +" was created.");
        }
        catch(IOException e) {
            e.printStackTrace();
            System.out.println("[INFO] Error writing XSL file.");
            result = Response.ERROR;
        }
        return result;
    }

    /*public static void writeXmapFile(String filename, File xsltDir) { TODO needs IO libs

    }*/

    //DELETING
    public static boolean deleteTransformationFiles(String filename, File xsltDir, File xmapDir) {
        return deleteXslFile(filename, xsltDir) && deleteXmapFile(filename, xmapDir);
    }

    public static boolean deleteXslFile(String filename, File xsltDir) {
        File tmpFile = new File(xsltDir, filename + ".xsl");
        Boolean result = tmpFile.delete();
        if(result){
            System.out.println("[INFO] "+tmpFile.getName() + " was deleted.");
        }else{
            System.out.println("[INFO] "+"Delete operation is failed. File: " + tmpFile.getName());
        }
        return result;
    }

    public static boolean deleteXmapFile(String filename, File xmapDir) {
        File tmpFile = new File(xmapDir, filename + ".xmap");
        Boolean result = tmpFile.delete();
        if(result){
            System.out.println("[INFO] "+tmpFile.getName() + " was deleted.");
        }else{
            System.out.println("[INFO] "+"Delete operation is failed. File: " + tmpFile.getPath());
        }
        return result;
    }

}
