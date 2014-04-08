/*
 * Created on 17/Mar/2006
 *
 */
package pt.utl.ist.repox.marc;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.util.RecordComparer;
import pt.utl.ist.marc.xml.MarcXChangeDom4jBuilder;
import pt.utl.ist.repox.recordPackage.RecordRepox;

import java.io.*;
import java.util.HashSet;


public class RecordRepoxMarc implements RecordRepox, Serializable{
	private static final Logger log = Logger.getLogger(RecordRepoxMarc.class);
	static final long serialVersionUID = 1;

	protected Record record;
	protected boolean isDeleted = false;
	protected String marcFormat;

	public RecordRepoxMarc() {
	}

	public RecordRepoxMarc(Element dom) {
		record = MarcXChangeDom4jBuilder.parseRecord(dom);
	}

	public RecordRepoxMarc(Element dom, boolean isDeleted) {
		this(dom);
		this.isDeleted = isDeleted;
	}

	public byte[] serialize() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream s = new ObjectOutputStream(out);
		s.writeObject(record);
		s.flush();
		return out.toByteArray();
	}

	public void deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ObjectInputStream s = new ObjectInputStream(in);
		record=(Record) s.readObject();
		s.close();
		in.close();
	}

	public RecordRepoxMarc(Record record) {
		this.record = record;
	}

	public String getId() {
		return record.getNc();
	}

	public void toDom(Element doElement) {
		MarcXChangeDom4jBuilder.record2DomElement(record, doElement, marcFormat);
	}

    public Element getDom() {
        return MarcXChangeDom4jBuilder.record2Dom(record, marcFormat).getRootElement();
    }

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

    public String getMarcFormat() {
        return marcFormat;
    }

    public void setMarcFormat(String marcFormat) {
        this.marcFormat = marcFormat;
    }

    @Override
	public boolean equals(Object arg0) {
		if(!(arg0 instanceof RecordRepoxMarc)) {
			return false;
		}
		RecordRepoxMarc other=(RecordRepoxMarc)arg0;
		RecordComparer comparer=new RecordComparer(new HashSet<Integer>(0));
		return comparer.areEqual(record, getRecord());
	}

}
