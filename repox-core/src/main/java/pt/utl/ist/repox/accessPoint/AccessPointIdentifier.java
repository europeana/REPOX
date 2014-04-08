package pt.utl.ist.repox.accessPoint;

import pt.utl.ist.repox.recordPackage.RecordRepox;

import java.util.*;


/**An accessPoint implementation that index the timestamp of a record
 * @author Nuno Freire
 *
 */
public class AccessPointIdentifier extends AccessPoint{

	public AccessPointIdentifier(String id) {
		super(id);
		tokenizable=false;
	}

	@Override
	public Collection index(RecordRepox record) {
		Set idxVals=new HashSet(1);
		idxVals.add(record.getId());
		return idxVals;
	}

	@Override
	public List index(List<RecordRepox> records) {
		List idxVals = new ArrayList();
		for (RecordRepox recordRepox : records) {
			idxVals.add(recordRepox.getId());
		}
		return idxVals;
	}

	@Override
	public Class typeOfIndex() {
		return Object.class;
	}

	public String getDescription() {
		return "Timestamp";
	}

}
