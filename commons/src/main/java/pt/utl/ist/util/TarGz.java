package pt.utl.ist.util;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Gilberto Pedrosa
 * Date: 06-07-2012
 * Time: 13:03
 * To change this template use File | Settings | File Templates.
 */
public class TarGz {
    private static final Logger log = Logger.getLogger(TarGz.class);

    /** Untar an input file into an output file.

     * The output file is created in the output folder, having the same name
     * as the input file, minus the '.tar' extension.
     *
     * @param inputFile     the input .tar file
     * @param outputDir     the output directory file.
     * @throws IOException
     * @throws FileNotFoundException
     *
     * @return  The {@link List} of {@link File}s with the untared content.
     * @throws ArchiveException
     */
    private static List<File> unTar(final File inputFile, final File outputDir) throws FileNotFoundException, IOException, ArchiveException {

        log.debug(String.format("Untaring %s to dir %s.", inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));

        final List<File> untaredFiles = new LinkedList<File>();
        final InputStream is = new FileInputStream(inputFile);
        final TarArchiveInputStream debInputStream = (TarArchiveInputStream)new ArchiveStreamFactory().createArchiveInputStream("tar", is);
        TarArchiveEntry entry = null;
        while ((entry = (TarArchiveEntry)debInputStream.getNextEntry()) != null) {
            final File outputFile = new File(outputDir, entry.getName());
            if (entry.isDirectory()) {
                log.debug(String.format("Attempting to write output directory %s.", outputFile.getAbsolutePath()));
                if (!outputFile.exists()) {
                    log.debug(String.format("Attempting to create output directory %s.", outputFile.getAbsolutePath()));
                    if (!outputFile.mkdirs()) { throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath())); }
                }
            } else {
                log.debug(String.format("Creating output file %s.", outputFile.getAbsolutePath()));
                final OutputStream outputFileStream = new FileOutputStream(outputFile);
                IOUtils.copy(debInputStream, outputFileStream);
                outputFileStream.close();
            }
            untaredFiles.add(outputFile);
        }
        debInputStream.close();

        return untaredFiles;
    }

    /**
     * Ungzip an input file into an output file.
     * <p>
     * The output file is created in the output folder, having the same name
     * as the input file, minus the '.gz' extension.
     *
     * @param inputFile     the input .gz file
     * @param outputDir     the output directory file.
     * @throws IOException
     * @throws FileNotFoundException
     *
     * @return  The {@File} with the ungzipped content.
     */
    private static File unGzip(final File inputFile, final File outputDir) throws FileNotFoundException, IOException {

        log.debug(String.format("Ungzipping %s to dir %s.", inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));

        final File outputFile = new File(outputDir, inputFile.getName().substring(0, inputFile.getName().length() - 3));

        final GZIPInputStream in = new GZIPInputStream(new FileInputStream(inputFile));
        final FileOutputStream out = new FileOutputStream(outputFile);

        for (int c = in.read(); c != -1; c = in.read()) {
            out.write(c);
        }

        in.close();
        out.close();

        return outputFile;
    }

    /**
     * @param tarGzFile
     * @param tempDir
     * @return List of Files of the untared File 
     * @throws IOException
     * @throws ArchiveException
     */
    public static List<File> unTarGz(File tarGzFile, File tempDir) throws IOException, ArchiveException {
        File tarFile = TarGz.unGzip(tarGzFile, tempDir);
        List<File> unTarFiles = TarGz.unTar(tarFile, tempDir);
        if(tarFile.exists())
          tarFile.delete();
        return unTarFiles;
    }

    /**
     * @param args
     */
    public static void main(String args[]) {
        try {
            File tarGzFile = new File("C:\\Users\\Gilberto Pedrosa\\Desktop\\teste\\GDZ_Band.tar.gz");
            File tempDir = new File("C:\\Users\\Gilberto Pedrosa\\Desktop\\teste\\temp");
            List<File> listFiles = unTarGz(tarGzFile, tempDir);

            System.out.println("listFiles = " + listFiles);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
