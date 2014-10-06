/*
 * Created on 2006/08/17
 *
 */
package pt.utl.ist.marc.xml;

import pt.utl.ist.marc.MarcRecord;

/**
 */
public abstract class MarcSaxParserClient {
    boolean parseFinished = false;
    int     counter       = 0;

    /**
     * @param rec
     * @throws Exception
     */
    public void nextRecord(MarcRecord rec) throws Exception {
        processRecord(rec);
        counter++;
    }

    public int getRecordCount() {
        return counter;
    }

    /**
	 */
    public void signalParseFinished() {
        parseFinished = true;
    }

    /**
     * @param rec
     * @throws Exception
     */
    protected abstract void processRecord(MarcRecord rec) throws Exception;
}
