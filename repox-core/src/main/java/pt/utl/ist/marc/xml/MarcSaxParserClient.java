/*
 * Created on 2006/08/17
 *
 */
package pt.utl.ist.marc.xml;

import pt.utl.ist.marc.Record;

/**
 */
public abstract class MarcSaxParserClient {
    boolean parseFinished = false;
    int     counter       = 0;

    /**
     * @param rec
     * @throws Exception
     */
    public void nextRecord(Record rec) throws Exception {
        processRecord(rec);
        counter++;
    }

    @SuppressWarnings("javadoc")
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
    protected abstract void processRecord(Record rec) throws Exception;
}
