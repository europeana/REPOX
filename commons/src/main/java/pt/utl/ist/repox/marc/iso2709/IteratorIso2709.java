/*
 * Created on 29/Abr/2005
 *
 */
package pt.utl.ist.repox.marc.iso2709;

import pt.utl.ist.repox.characters.CharacterConverterI;
import pt.utl.ist.repox.characters.CharacterConverters;
import pt.utl.ist.repox.marc.Record;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

/**
 */
public class IteratorIso2709 implements Iterator<Record>, Iterable<Record> {
    File         isoFile;
    IsoNavigator isoNavig;
    List<Record> records = null;
    int          currentIndex;

    /**
     * Creates a new instance of this class.
     * 
     * @param isoFile
     */
    public IteratorIso2709(File isoFile) {
        this.isoFile = isoFile;
        isoNavig = new IsoNavigator(isoFile.getAbsolutePath());
        currentIndex = 0;
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param isoFile
     * @param charset
     */
    public IteratorIso2709(File isoFile, String charset) {
        this.isoFile = isoFile;
        CharacterConverterI converter = null;
        if (charset != null) converter = CharacterConverters.getInstance(charset);
        if (converter == null)
            isoNavig = new IsoNavigator(isoFile.getAbsolutePath(), new MARCPartialReader(charset));
        else
            isoNavig = new IsoNavigator(isoFile.getAbsolutePath(), converter);
        currentIndex = 0;
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param fileInputStream
     */
    public IteratorIso2709(FileInputStream fileInputStream) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean hasNext() {
        if (records == null || currentIndex == records.size()) {
            records = isoNavig.getNextRecords();
            currentIndex = 0;
        }
        if (records != null && currentIndex < records.size()) return true;

        isoNavig.close();
        return false;
    }

    @Override
    public void remove() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Record next() {
        Record ret = null;
        if (hasNext()) {
            ret = records.get(currentIndex);
            currentIndex++;
            processRecord(ret);
        }
        return ret;
    }

    @Override
    public Iterator<Record> iterator() {
        return this;
    }

    /**
     * @param rec
     */
    protected void processRecord(Record rec) {
    }

}
