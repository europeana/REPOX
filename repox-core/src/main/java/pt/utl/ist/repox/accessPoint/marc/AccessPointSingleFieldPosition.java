package pt.utl.ist.repox.accessPoint.marc;

import org.apache.log4j.Logger;
import pt.utl.ist.marc.Field;
import pt.utl.ist.marc.Record;
import pt.utl.ist.repox.marc.RecordRepoxMarc;
import pt.utl.ist.repox.recordPackage.RecordRepox;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 */
public class AccessPointSingleFieldPosition extends AccessPointSingleField {
    private static final Logger log = Logger.getLogger(AccessPointSingleFieldPosition.class);

    int                         startPosition;
    int                         endPosition;

    /**
     * Creates a new instance of this class.
     * 
     * @param id
     * @param tag
     * @param subfield
     * @param startPosition
     * @param endPosition
     */
    public AccessPointSingleFieldPosition(String id, int tag, Character subfield, int startPosition, int endPosition) {
        super(id, tag, subfield);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        tokenizable = false;
    }

    @Override
    public Collection<String> index(RecordRepox record) {
        RecordRepoxMarc repoxRecord = (RecordRepoxMarc)record;
        Record marcRecord = repoxRecord.getRecord();
        Set<String> idxVals = new HashSet<String>();

        if (tag == 0) {
            try {
                idxVals.add(indexValue(marcRecord.getLeader().substring(startPosition, endPosition + 1)));
            } catch (Exception e) {
                log.debug(e.getMessage(), e);
            }
        } else {
            List<Field> fields = marcRecord.getField(tag);
            for (Field f : fields) {
                try {
                    if (subfield == null) {
                        String val = f.getValue();
                        if (val != null) {
                            idxVals.add(indexValue(val.substring(startPosition, endPosition + 1)));
                        }
                    } else {
                        for (String val : f.getSubfieldValues(subfield)) {
                            idxVals.add(indexValue(val.substring(startPosition, endPosition + 1)));
                        }
                    }
                } catch (Exception e) {
                    log.debug(e.getMessage(), e);
                }
            }
        }
        return idxVals;
    }

    @Override
    public String indexValue(String value) {
        return value;
    }

    @Override
    public String getDescription() {
        return "Campo " + tag + " $" + subfield + " posi��es " + startPosition + " a " + endPosition;
    }

}
