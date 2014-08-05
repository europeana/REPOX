package pt.utl.ist.repox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

//TODO: Delete this file if you see this. Used to shorten sample ISO2709 file (can't open large files in windows, not even with emacs... =/ ) 
/**
 */
public class Splitter {
    /**
     * @throws IOException
     */
    public static void split() throws IOException {
        long maxSize = 1 * 1024 * 1024;

        File file = new File("C:/LavoriMiei/Europeana/Sources/Contrib/Repox/work/1.testdeploy/testrecords/idextracted update/repox1.out");
        FileInputStream inStream = new FileInputStream(file);

        File fileOut = new File("C:/LavoriMiei/Europeana/Sources/Contrib/Repox/work/1.testdeploy/testrecords/idextracted update/repox2.out");
        FileOutputStream outStream = new FileOutputStream(fileOut);

        byte[] buffer = new byte[1024];
        for (int i = 0; i < maxSize; i += buffer.length) {
            inStream.read(buffer);
            outStream.write(buffer);
        }

        inStream.close();
        outStream.close();
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        split();
    }
}
