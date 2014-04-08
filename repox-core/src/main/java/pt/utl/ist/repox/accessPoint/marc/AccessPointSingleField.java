package pt.utl.ist.repox.accessPoint.marc;

import pt.utl.ist.marc.Field;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.xml.MarcXChangeDom4jBuilder;
import pt.utl.ist.repox.accessPoint.AccessPoint;
import pt.utl.ist.repox.marc.RecordRepoxMarc;
import pt.utl.ist.repox.recordPackage.RecordRepox;

import java.util.*;


public class AccessPointSingleField extends AccessPoint {
	protected int tag;
	protected Character subfield;


	public AccessPointSingleField(String id, int tag, Character subfield) {
		super(id);
		this.tag=tag;
		this.subfield=subfield;
	}

	public AccessPointSingleField(String id, int tag, Character subfield, boolean tokenizable) {
		super(id,tokenizable);
		this.tag=tag;
		this.subfield=subfield;
	}

	@Override
	public Collection<String> index(RecordRepox record) {
		RecordRepoxMarc repoxRecord = (RecordRepoxMarc) record;
		return indexRecordRepoxMarc(repoxRecord);
	}

	@Override
	public List index(List<RecordRepox> records) {
		List idxVals = new ArrayList();

		for (RecordRepox recordRepox : records) {
			idxVals.add(indexRecordRepoxMarc(recordRepox));
		}

		return idxVals;
	}

	private Collection<String> indexRecordRepoxMarc(RecordRepox repoxRecord) {
		Record record = (repoxRecord instanceof RecordRepoxMarc ?
				((RecordRepoxMarc)repoxRecord).getRecord() : MarcXChangeDom4jBuilder.parseRecord(repoxRecord.getDom()));
		Set<String> idxVals=new HashSet<String>();
		List<Field> fields=record.getField(tag);
		for (Field f:fields) {
			if (subfield==null) {
				String  val=f.getValue();
				if (val!=null) {
					idxVals.add(indexValue(val));
				}
			}else {
				for (String val: f.getSubfieldValues(subfield)) {
					idxVals.add(indexValue(val));
				}
			}
		}
		return idxVals;
	}

	@Override
	public Class typeOfIndex() {
		return String.class;
	}

	public String getDescription() {
		return "Campo "+tag+" $"+subfield;
	}

}
