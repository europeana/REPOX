/*
 * ConfigFiles.java
 * 
 * Created on 10 de Abril de 2002, 18:35
 */

package pt.utl.ist.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author Nuno Freire
 */
public class FileUtil {

  /** Creates a new instance of ConfigFiles */
  public FileUtil() {}

  /**
   * @param sourceFilename
   * @param targetFilename
   * @param regExp - Regular expression to substitute
   * @param newText - text to place on the targetFile
   */
  public static void substituteInFile(String sourceFilename, String targetFilename, String regExp,
      String newText) {
    File f1 = new File(sourceFilename);
    File f2 = new File(targetFilename);
    try {
      Pattern pattern = Pattern.compile(regExp);
      BufferedReader file1 = new BufferedReader(new FileReader(f1));
      PrintWriter file2 = new PrintWriter(new FileWriter(f2));
      try {
        String line = file1.readLine();
        while (line != null) {
          Matcher m = pattern.matcher(line);
          String s = m.replaceAll(newText);
          file2.println(s);
          line = file1.readLine();
        }
      } catch (EOFException ex) {
      }
      file2.close();
      file1.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * @param file
   * @return String of the contents of the File
   * @throws IOException
   */
  public static String readFileToString(File file) throws java.io.IOException {
    StringBuffer ret = new StringBuffer();
    // char[] buf=new char[1024];
    FileReader reader = new FileReader(file);
    int r;
    while ((r = reader.read()) != -1) {
      ret.append((char) r);
    }
    reader.close();
    return ret.toString();
  }

  /**
   * @param pathRecord
   * @param lineNumber
   * @return String of the line number in File
   */
  // Function to read a file
  public static String readFile(File pathRecord, int lineNumber) {
    try {
      ArrayList<String> textoList = new ArrayList<String>();
      BufferedReader br = new BufferedReader(new FileReader(pathRecord));
      String line;
      while ((line = br.readLine()) != null) {
        textoList.add(line);
      }
      br.close();

      if (textoList.size() >= lineNumber + 1) {
        return textoList.get(lineNumber);
      }
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * @param pathRecord
   * @return List of the String lines in File
   */
  public static ArrayList<String> readFile(File pathRecord) {
    try {
      ArrayList<String> textoList = new ArrayList<String>();
      BufferedReader br = new BufferedReader(new FileReader(pathRecord));
      String line;
      while ((line = br.readLine()) != null) {
        textoList.add(line);
      }
      br.close();

      return textoList;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * @param file
   * @param encoding
   * @return String of the contents of File encoded
   * @throws IOException
   */
  public static String readFileToString(File file, String encoding) throws java.io.IOException {
    byte[] bytes = readFileBytes(file);
    return new String(bytes, encoding);
  }

  /**
   * @param file
   * @return bytes of File
   * @throws IOException
   */
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
      is.close();
      throw new IOException("File is too large" + file.getName());
    }

    // Create the byte array to hold the data
    byte[] bytes = new byte[(int) length];

    // Read in the bytes
    int offset = 0;
    int numRead = 0;
    while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
      offset += numRead;
    }

    // Ensure all the bytes have been read in
    if (offset < bytes.length) {
      is.close();
      throw new IOException("Could not completely read file " + file.getName());
    }

    // Close the input stream and return bytes
    is.close();
    return bytes;
  }

  /**
   * @param file
   * @param data
   * @throws IOException
   */
  public static void writeToFile(File file, String data) throws java.io.IOException {
    FileWriter f = new FileWriter(file);
    f.write(data);
    f.close();
  }

  /**
   * @param file
   * @param data
   * @param encoding
   * @throws IOException
   */
  public static void writeToFile(File file, String data, String encoding)
      throws java.io.IOException {
    FileOutputStream f = new FileOutputStream(file);
    f.write(data.getBytes(encoding));
    f.close();
  }

  /**
   * @param file
   * @param data
   * @throws IOException
   */
  public static void writeToFile(File file, byte[] data) throws java.io.IOException {
    FileOutputStream f = new FileOutputStream(file);
    f.write(data);
    f.close();
  }

  /**
   * @param file
   * @param data
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void writeToFile(File file, InputStream data) throws java.io.FileNotFoundException,
      java.io.IOException {
    FileOutputStream fos = new FileOutputStream(file);
    transferData(data, fos);
    data.close();
    fos.close();
  }

  public static String getSystemCharset() {
    return NUtil.getSystemCharset();
  }

  /**
   * @param source
   * @param target
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void copyFile(File source, File target) throws java.io.FileNotFoundException,
      java.io.IOException {
    FileInputStream fis = new FileInputStream(source);
    FileOutputStream fos = new FileOutputStream(target);
    transferData(fis, fos);
    fis.close();
    fos.close();
  }

  /**
   * @param source
   * @param targetDir
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void copyFileToDir(File source, File targetDir)
      throws java.io.FileNotFoundException, java.io.IOException {
    copyFile(source, new File(targetDir, source.getName()));
  }

  /**
   * @param sourceDir
   * @param targetDir
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void copyDirContents(File sourceDir, File targetDir)
      throws java.io.FileNotFoundException, java.io.IOException {
    File[] files = sourceDir.listFiles();
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

  /**
   * @param sourceDir
   * @return boolean indicating if the deletion was successful
   * @throws IOException
   */
  public static boolean deleteDir(File sourceDir) throws java.io.IOException {
    File[] files = sourceDir.listFiles();
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

  /**
   * @param in
   * @param out
   * @throws IOException
   */
  public static void transferData(InputStream in, OutputStream out) throws IOException {
    byte[] buf = new byte[4096];
    int i = 0;
    while ((i = in.read(buf)) > 0) {
      out.write(buf, 0, i);
    }
  }

  /**
   * @param inputStr
   * @param newFile
   * @return boolean indicating if successful
   */
  public static boolean createFile(String inputStr, File newFile) {
    try {

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
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          if (br != null)
            br.close();
          if (is != null)
            is.close();
        } catch (IOException e) {
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
   * 
   * @param URLName
   * @return boolean indicating if the url exists
   */
  public static boolean checkUrl(String URLName) {
    boolean exists = true;
    try {
      HttpURLConnection.setFollowRedirects(false);
      HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
      int responseCode = con.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_OK
          && responseCode != HttpURLConnection.HTTP_INTERNAL_ERROR
          && responseCode != HttpURLConnection.HTTP_MULT_CHOICE
          && responseCode != HttpURLConnection.HTTP_MOVED_PERM
          && responseCode != HttpURLConnection.HTTP_MOVED_TEMP
          && responseCode != HttpURLConnection.HTTP_BAD_REQUEST)
        exists = false;
    } catch (IOException ex) {
      exists = false;
    }
    return exists;
  }

  /**
   * Checks if an FTP connection works
   * 
   * @param server
   * @param idType
   * @param ftpPath
   * @param user
   * @param password
   * @return if the connection works or not
   */
  public static boolean checkFtpServer(String server, String idType, String ftpPath, String user,
      String password) {
    FTPClient ftp = new FTPClient();

    try {
      ftp.connect(server);

      if (idType.equals("Normal")) {
        if (!ftp.login(user, password)) {
          return false;
        }
      } else if (idType.equals("Anonymous")) {
        if (!ftp.login("anonymous", "")) {
          return false;
        }
      }

      if (!ftp.changeWorkingDirectory(ftpPath)) {
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

  /**
   * @param directory
   * @return number of files in a directory
   */
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

  /**
   * @param name
   * @return String of the sanitized name
   */
  public static String sanitizeToValidFilename(String name) {
    String[] invalidSymbols = new String[] {"\\", "/", ":", "*", "?", "\"", "<", ">", "|"};
    String sanitizedName = name;

    for (String currentSymbol : invalidSymbols) {
      sanitizedName = sanitizedName.replaceAll("[\\" + currentSymbol + "]", "_");
    }
    return sanitizedName;
  }

  /**
   * @param fromDate
   * @param files
   * @return Array of Files
   */
  public static File[] getChangedFiles(Date fromDate, File[] files) {
    List<File> changedFilesList = getChangedFilesList(fromDate, files);
    File[] changedFiles = new File[changedFilesList.size()];
    changedFilesList.toArray(changedFiles);
    // Arrays.sort(changedFiles, NameFileComparator.NAME_INSENSITIVE_COMPARATOR);
    Arrays.sort(changedFiles, new Comparator<File>() {
      @Override
      public int compare(File file1, File file2) {
        if (file1.getName().toLowerCase().compareTo(file2.getName().toLowerCase()) < 0)
          return -1;
        else if (file1.getName().toLowerCase().compareTo(file2.getName().toLowerCase()) > 0)
          return 1;
        return 0;
      }
    });

    return changedFiles;
  }

  /**
   * @param fromDate
   * @param files
   * @return List of Files
   */
  public static List<File> getChangedFilesList(Date fromDate, File[] files) {
    List<File> changedFiles = new ArrayList<File>();

    if (files != null) {
      for (File file : files) {
        if (file.isDirectory()) {
          changedFiles.addAll(getChangedFilesList(fromDate, file.listFiles()));
        } else if (isFileChanged(fromDate, file)) {
          changedFiles.add(file);
        }
      }
    }

    return changedFiles;
  }

  public static boolean isFileChanged(Date fromDate, File file) {
    return fromDate == null || file.lastModified() > fromDate.getTime();
  }

  /**
   * @param arrgs
   */
  public static void main(String arrgs[]) {
    System.out.println("number of files: "
        + countFilesInDirectory(new File("C:\\Users\\Gilberto Pedrosa\\Desktop\\BulDML_external")));

  }
}
