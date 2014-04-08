package pt.utl.ist.repox.accessPoint.marc;

import pt.utl.ist.marc.Field;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.Subfield;
import pt.utl.ist.marc.xml.MarcXChangeDom4jBuilder;
import pt.utl.ist.repox.accessPoint.AccessPoint;
import pt.utl.ist.repox.marc.RecordRepoxMarc;
import pt.utl.ist.repox.recordPackage.RecordRepox;

import java.util.*;


public class AccessPointFieldsWithMultipleSubfieldsConcatenated extends AccessPoint {
	private int[] tags;
	private Set<Character> subfields;

	private String separator = " ";

	public AccessPointFieldsWithMultipleSubfieldsConcatenated(String id, List<Integer> tags, List<Character> subfields) {
		super(id);
		this.tags = new int[tags.size()];
		int idx = 0;
		for (int i : tags) {
			this.tags[idx] = i;
			idx++;
		}
		this.subfields = new HashSet<Character>(subfields.size());
		for (char c : subfields) {
			this.subfields.add(c);
		}
	}

	public AccessPointFieldsWithMultipleSubfieldsConcatenated(String id, List<Integer> tags, List<Character> subfields, String separator) {
		this(id, tags, subfields);
		this.separator = separator;
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
		Set<String> idxVals = new HashSet<String>();
		List<Field> fields = record.getFields(tags);
		for (Field f : fields) {
			String val = "";
			for (Subfield sf : f.getSubfields()) {
				if(subfields.contains(sf.getCode())) {
					val = (val.equals("") ? indexValue(sf.getValue()) : val + separator + indexValue(sf.getValue()));
				}
			}
			if(!val.equals("")) {
				idxVals.add(val);
			}
		}
		return idxVals;
	}

	@Override
	public Class typeOfIndex() {
		return String.class;
	}

	public String getDescription() {
		return "Campos " + tags + " com subcampos " + subfields + " separados por \"" + separator + "\"";
	}

}
