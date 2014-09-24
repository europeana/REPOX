package pt.utl.ist.repox.util;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 */
public class ZipUtil {
    /**
     * @param bytes
     * @return zipped bytes
     * @throws IOException
     */
    public static byte[] zip(byte[] bytes) throws IOException {
        int sChunk = 8192;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream zipout = new GZIPOutputStream(out);
        byte[] buffer = new byte[sChunk];

        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        int length;
        while ((length = in.read(buffer, 0, sChunk)) != -1)
            zipout.write(buffer, 0, length);
        in.close();
        zipout.close();

        return out.toByteArray();
    }

    /**
     * @param zippedString
     * @param charset
     * @return zipped bytes
     * @throws IOException
     */
    public static byte[] zip(String zippedString, Charset charset) throws IOException {
        return zip(zippedString.getBytes(charset));
    }

    /**
     * @param listFiles2Zip
     * @param output
     * @throws IOException
     */
    public static void zipFiles(File[] listFiles2Zip, File output) throws IOException {
        // These are the files to include in the ZIP file

        // Create a buffer for reading the files
        byte[] buf = new byte[1024];

        // Create the ZIP file
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(output));

        // Compress the files
        for (File actualFile : listFiles2Zip) {

            FileInputStream in = new FileInputStream(actualFile);

            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(actualFile.getName()));

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
    }

    /**
     * @param zippedBytes
     * @return unzipped bytes
     * @throws IOException
     */
    public static byte[] unzip(byte[] zippedBytes) throws IOException {
        int sChunk = 8192;

        ByteArrayInputStream in = new ByteArrayInputStream(zippedBytes);
        GZIPInputStream zipin = new GZIPInputStream(in);
        byte[] buffer = new byte[sChunk];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int length;
        while ((length = zipin.read(buffer, 0, sChunk)) != -1)
            out.write(buffer, 0, length);
        out.close();
        zipin.close();
        in.close();

        return out.toByteArray();
    }

    /**
     * @param zippedBytes
     * @param charset
     * @return unzipped bytes
     * @throws IOException
     */
    public static String unzip(byte[] zippedBytes, Charset charset) throws IOException {
        return new String(unzip(zippedBytes), charset);
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String originalString = "Quero isto descomprimido com acentos";

        byte[] zippedValue = ZipUtil.zip(originalString, Charset.forName("ISO-8859-1"));
        String unzippedValue = new String(ZipUtil.unzip(zippedValue));

        System.out.println("*   zipped: " + zippedValue);
        System.out.println("* unzipped: " + unzippedValue);
    }
}
