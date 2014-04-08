/*
 * ConfigFiles.java
 *
 * Created on 10 de Abril de 2002, 18:35
 */

package pt.utl.ist.util;

import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author  Nuno Freire
 */
public class FileUtil {

    /** Creates a new instance of ConfigFiles */
    public FileUtil() {
    }

    /**
     * @param sourceFilename
     * @param targetFilename
     * @param regExp - Regular expression to substitute
     * @param newText - text to place on the targetFile */
    public static void substituteInFile(String sourceFilename,String targetFilename, String regExp, String newText){
        File f1=new File(sourceFilename);
        File f2=new File(targetFilename);
        try {
            Pattern pattern = Pattern.compile(regExp);
            BufferedReader file1=new BufferedReader(new FileReader(f1));
            PrintWriter file2=new PrintWriter(new FileWriter(f2));
            try {
                String line=file1.readLine();
                while(line != null) {
                    Matcher m = pattern.matcher(line);
                    String s=m.replaceAll(newText);
                    file2.println(s);
                    line=file1.readLine();
                }
            } catch(EOFException ex) {
            }
            file2.close();
            file1.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }


    public static String readFileToString(File file) throws java.io.IOException{
        StringBuffer ret=new StringBuffer();
//        char[] buf=new char[1024];
        FileReader reader=new FileReader(file);
        int r;
        while((r=reader.read()) != -1){
            ret.append((char)r);
        }
        reader.close();
        return ret.toString();
    }

    // Function to read a file
    public static String readFile(File pathRecord, int lineNumber) {
        try{
            ArrayList<String> textoList = new ArrayList<String>();
            BufferedReader br = new BufferedReader(new FileReader(pathRecord));
            String line;
            while ((line = br.readLine()) != null) {
                textoList.add(line);
            }
            br.close();

            if(textoList.size() >= lineNumber + 1){
                return textoList.get(lineNumber);
            }
            return null;
        }
        catch (Exception e){
            return null;
        }
    }


    public static ArrayList<String> readFile(File pathRecord) {
        try{
            ArrayList<String> textoList = new ArrayList<String>();
            BufferedReader br = new BufferedReader(new FileReader(pathRecord));
            String line;
            while ((line = br.readLine()) != null) {
                textoList.add(line);
            }
            br.close();

            return textoList;
        }
        catch (Exception e){
            return null;
        }
    }

    public static String readFileToString(File file, String encoding) throws java.io.IOException{
        byte[] bytes=readFileBytes(file);
        return new String(bytes, encoding);
    }


    // Returns the contents of the file in a byte array.
    public static byte[] readFileBytes(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            throw new IOException("File is too large"+file.getName());
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    public static void writeToFile(File file, String data) throws java.io.IOException{
        FileWriter f=new FileWriter(file);
        f.write(data);
        f.close();
    }

    public static void writeToFile(File file, String data, String encoding) throws java.io.IOException{
        FileOutputStream f=new FileOutputStream(file);
        f.write(data.getBytes(encoding));
        f.close();
    }


    public static void writeToFile(File file, byte[] data) throws java.io.IOException{
        FileOutputStream f=new FileOutputStream(file);
        f.write(data);
        f.close();
    }

    public static void writeToFile(File file, InputStream data) throws java.io.FileNotFoundException, java.io.IOException{
        FileOutputStream fos=new FileOutputStream(file);
        transferData(data,fos);
        data.close();
        fos.close();
    }

    public static String getSystemCharset() {
        return NUtil.getSystemCharset();
    }


    public static void copyFile(File source, File target) throws java.io.FileNotFoundException, java.io.IOException{
        FileInputStream fis = new FileInputStream(source);
        FileOutputStream fos = new FileOutputStream(target);
        transferData(fis,fos);
        fis.close();
        fos.close();
    }

    public static void copyFileToDir(File source, File targetDir) throws java.io.FileNotFoundException, java.io.IOException{
        copyFile(source,new File(targetDir,source.getName()));
    }

    public static void copyDirContents(File sourceDir, File targetDir) throws java.io.FileNotFoundException, java.io.IOException{
        File[] files=sourceDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                File subTarget = new File(targetDir, file.getName());
                if (!subTarget.exists())
                    subTarget.mkdir();
                copyDirContents(file, subTarget);
            } else {
                copyFileToDir(file, targetDir);
            }
        }
    }


    public static boolean deleteDir(File sourceDir) throws java.io.IOException{
        File[] files=sourceDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDir(file);
            } else {
                boolean succ = file.delete();
                if (!succ)
                    return false;
            }
        }
        boolean succ = sourceDir.delete();
        return succ;
    }


    public static void transferData(InputStream in, OutputStream out) throws IOException{
        byte[] buf = new byte[4096];
        int i = 0;
        do {
            i = in.read(buf);
            if (i != -1) {
                out.write(buf, 0, i);
            }
        } while (i != -1);
    }

    public static boolean createFile(String inputStr, File newFile) {
        try{

            InputStream is = null;
            BufferedReader br = null;
            String line;
            String fullText = "";

            try {
                is = FileUtil.class.getResourceAsStream(inputStr);
                br = new BufferedReader(new InputStreamReader(is));
                while (null != (line = br.readLine())) {
                    fullText += line + "\n";
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (br != null) br.close();
                    if (is != null) is.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(newFile));
            writer2.write(fullText.replace('\\', '/'));
            writer2.close();

            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Check if an URL exists
     * @param URLName
     * @return
     */
    public static boolean checkUrl(String URLName) {
        boolean exists = true;
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            if(con.getResponseCode() != HttpURLConnection.HTTP_OK  &&
                    con.getResponseCode() != HttpURLConnection.HTTP_INTERNAL_ERROR &&
                    con.getResponseCode() != HttpURLConnection.HTTP_MULT_CHOICE &&
                    con.getResponseCode() != HttpURLConnection.HTTP_MOVED_PERM &&
                    con.getResponseCode() != HttpURLConnection.HTTP_MOVED_TEMP)
                exists = false;
        } catch (IOException ex) {
            exists = false;
        }
        return exists;
    }

    /**
     * Checks if an FTP connection works
     * @param server
     * @param idType
     * @param ftpPath
     * @param user
     * @param password
     * @return if the connection works or not
     */
    public static boolean checkFtpServer(String server, String idType, String ftpPath, String user, String password){
        FTPClient ftp = new FTPClient();

        try {
            ftp.connect(server);

            if(idType.equals("Normal")){
                if(!ftp.login(user, password)){
                    return false;
                }
            }

            if(!ftp.changeWorkingDirectory(ftpPath)){
                return false;
            }

            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                ftp.disconnect();
            } catch (NullPointerException ignored) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
    }

    public static int countFilesInDirectory(File directory) {
        int count = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                count++;
            }
            if (file.isDirectory()) {
                count += countFilesInDirectory(file);
            }
        }
        return count;
    }
    
    public static void main(String arrgs[]){
        System.out.println("number of files: " + countFilesInDirectory(new File("C:\\Users\\Gilberto Pedrosa\\Desktop\\BulDML_external")));
                
    }
}
