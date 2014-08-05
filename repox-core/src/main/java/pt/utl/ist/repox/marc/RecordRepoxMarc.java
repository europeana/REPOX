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

/**
 */
public class RecordRepoxMarc implements RecordRepox, Serializable {
    private static final Logger log              = Logger.getLogger(RecordRepoxMarc.class);
    static final long           serialVersionUID = 1;

    protected Record            record;
    protected boolean           isDeleted        = false;
    protected String            marcFormat;

    /**
     * Creates a new instance of this class.
     */
    public RecordRepoxMarc() {
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param dom
     */
    public RecordRepoxMarc(Element dom) {
        record = MarcXChangeDom4jBuilder.parseRecord(dom);
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param dom
     * @param isDeleted
     */
    public RecordRepoxMarc(Element dom, boolean isDeleted) {
        this(dom);
        this.isDeleted = isDeleted;
    }

    @Override
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream s = new ObjectOutputStream(out);
        s.writeObject(record);
        s.flush();
        return out.toByteArray();
    }

    @Override
    public void deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream s = new ObjectInputStream(in);
        record = (Record)s.readObject();
        s.close();
        in.close();
    }

    @SuppressWarnings("javadoc")
    public RecordRepoxMarc(Record record) {
        this.record = record;
    }

    @Override
    public String getId() {
        return record.getNc();
    }

    /**
     * @param doElement
     */
    public void toDom(Element doElement) {
        MarcXChangeDom4jBuilder.record2DomElement(record, doElement, marcFormat);
    }

    @Override
    public Element getDom() {
        return MarcXChangeDom4jBuilder.record2Dom(record, marcFormat).getRootElement();
    }

    @SuppressWarnings("javadoc")
    public Record getRecord() {
        return record;
    }

    @SuppressWarnings("javadoc")
    public void setRecord(Record record) {
        this.record = record;
    }

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @SuppressWarnings("javadoc")
    public String getMarcFormat() {
        return marcFormat;
    }

    @SuppressWarnings("javadoc")
    public void setMarcFormat(String marcFormat) {
        this.marcFormat = marcFormat;
    }

    @Override
    public boolean equals(Object arg0) {
        if (!(arg0 instanceof RecordRepoxMarc)) { return false; }
        RecordRepoxMarc other = (RecordRepoxMarc)arg0;
        RecordComparer comparer = new RecordComparer(new HashSet<Integer>(0));
        return comparer.areEqual(record, getRecord());
    }

}
