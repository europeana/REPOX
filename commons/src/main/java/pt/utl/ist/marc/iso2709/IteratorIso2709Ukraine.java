/*
 * Created on 29/Abr/2005
 *
 */
package pt.utl.ist.marc.iso2709;

import java.io.File;

/**
 */
public class IteratorIso2709Ukraine extends IteratorIso2709 {
    /**
     * Creates a new instance of this class.
     * 
     * @param isoFile
     */
    public IteratorIso2709Ukraine(File isoFile) {
        super(isoFile);
        MARCPartialReaderUkraine reader = new MARCPartialReaderUkraine("Cp1251");
        reader.setUnitDelimiter('^');
        isoNavig = new IsoNavigator(isoFile.getAbsolutePath(), reader);
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param isoFile
     * @param charset
     */
    public IteratorIso2709Ukraine(File isoFile, String charset) {
        super(isoFile, charset);
        MARCPartialReaderUkraine reader = new MARCPartialReaderUkraine(charset);
        reader.setUnitDelimiter('^');
        isoNavig = new IsoNavigator(isoFile.getAbsolutePath(), reader);
    }

}
