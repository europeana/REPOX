package pt.utl.ist.repox.marc.iso2709;

import org.apache.log4j.Logger;

import pt.utl.ist.repox.characters.CharacterConverterI;
import pt.utl.ist.repox.marc.Record;
import pt.utl.ist.repox.marc.util.Leader;

import java.util.ArrayList;

/**
 */
public class ISOHandlerSingleRecord extends ISOHandler {
    private static final Logger log = Logger.getLogger(ISOHandlerSingleRecord.class);

    /**
     * Creates a new instance of this class.
     * 
     * @param rec
     */
    public ISOHandlerSingleRecord(Record rec) {
        this.rec = rec;
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param rec
     * @param charConverter
     */
    public ISOHandlerSingleRecord(Record rec, CharacterConverterI charConverter) {
        this.rec = rec;
        this.charConverter = charConverter;
    }

    @Override
    public void startTape() {
        records = new ArrayList<Record>();
    }

    @Override
    public void endTape() {
    }

    @Override
    public void startRecord(Leader leader) {
        rec.setLeader(getString(leader.getSerializedForm()));
    }

    @Override
    public void endRecord() {
    }
}