/*
 * Created on 2006/08/17
 *
 */
package pt.utl.ist.marc.xml;

import pt.utl.ist.marc.Record;

public abstract class MarcSaxParserClient {
	boolean parseFinished=false;
	int counter=0;
	public void nextRecord(Record rec) throws Exception {
		processRecord(rec);
		counter++;
	};
	public int getRecordCount() {
		return counter;
	}
	
	public void signalParseFinished() {
		
		parseFinished=true;
	}
	
	protected abstract void processRecord(Record rec) throws Exception;
}
