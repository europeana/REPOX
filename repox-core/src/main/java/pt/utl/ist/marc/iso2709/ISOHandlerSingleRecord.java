package pt.utl.ist.marc.iso2709;

import org.apache.log4j.Logger;
import pt.utl.ist.characters.CharacterConverterI;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.util.Leader;

import java.util.ArrayList;

public class ISOHandlerSingleRecord extends ISOHandler {
	private static final Logger log = Logger.getLogger(ISOHandlerSingleRecord.class);

    
	public ISOHandlerSingleRecord(Record rec) {
		this.rec=rec;
	}
    public ISOHandlerSingleRecord(Record rec, CharacterConverterI charConverter) {
    	this.rec=rec;
    	this.charConverter = charConverter;
    }

    public void startTape() {
        records=new ArrayList<Record>();
    }

    public void endTape() {
    }

    public void startRecord(Leader leader) {
        rec.setLeader(getString(leader.getSerializedForm()));
    }

    public void endRecord() {
    }
}