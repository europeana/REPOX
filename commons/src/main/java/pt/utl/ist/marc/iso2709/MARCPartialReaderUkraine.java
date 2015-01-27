package pt.utl.ist.marc.iso2709;

import org.apache.log4j.Logger;

import pt.utl.ist.marc.MarcRecord;
import pt.utl.ist.marc.xml.MarcWriterInXml;

import java.io.File;

/**
 */
public class MARCPartialReaderUkraine extends MARCPartialReader {
    /**
     * Logger for this class
     */
    private static final Logger log = Logger.getLogger(MARCPartialReaderUkraine.class);

    /**
     * Creates a new instance of this class.
     */
    public MARCPartialReaderUkraine() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param charset
     */
    public MARCPartialReaderUkraine(String charset) {
        super(charset);
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        MarcWriterInXml w = new MarcWriterInXml(new File("C:\\Desktop\\t.xml"));
        for (MarcRecord r : new IteratorIso2709Ukraine(new File("C:\\Desktop\\Projectos\\TELplus\\Repox\\ukraine.iso"))) {
            System.out.println(r.toMarcXChangeXmlString());

            w.write(r);
        }
        w.close();

    }
}
// End of MARCREader.java
