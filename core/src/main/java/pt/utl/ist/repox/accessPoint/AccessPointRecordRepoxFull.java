package pt.utl.ist.repox.accessPoint;

import org.apache.log4j.Logger;
import pt.utl.ist.repox.recordPackage.RecordRepox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An accessPoint implementation that serializes a RecordPackage
 * 
 * @author Nuno Freire
 */
public class AccessPointRecordRepoxFull extends AccessPoint {
    private static final Logger log = Logger.getLogger(AccessPointRecordRepoxFull.class);

    /**
     * Creates a new instance of this class.
     * 
     * @param id
     */
    public AccessPointRecordRepoxFull(String id) {
        super(id);
    }

    @Override
    public Collection<byte[]> index(RecordRepox record) {
        try {
            Collection<byte[]> ret = new ArrayList<byte[]>(1);
            ret.add(record.serialize());
            return ret;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<byte[]> index(List<RecordRepox> records) {
        try {
            List<byte[]> ret = new ArrayList<byte[]>(1);

            for (RecordRepox recordRepox : records) {
                ret.add(recordRepox.serialize());
            }

            return ret;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Class typeOfIndex() {
        return byte[].class;
    }

}
