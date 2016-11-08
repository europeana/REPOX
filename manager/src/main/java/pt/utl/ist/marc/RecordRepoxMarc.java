/*
 * Created on 17/Mar/2006
 *
 */
package pt.utl.ist.marc;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import pt.utl.ist.marc.xml.MarcXChangeDom4jBuilder;
import pt.utl.ist.recordPackage.RecordRepox;
import pt.utl.ist.util.marc.RecordComparer;

import java.io.*;
import java.util.HashSet;

/**
 */
public class RecordRepoxMarc implements RecordRepox, Serializable {
    private static final Logger log              = Logger.getLogger(RecordRepoxMarc.class);
    static final long           serialVersionUID = 1;

    protected MarcRecord            record;
    protected String            marcFormat;
    protected boolean           isDeleted        = false;
    protected boolean           isEmpty        = false;

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
        record = (MarcRecord)s.readObject();
        s.close();
        in.close();
    }

    public RecordRepoxMarc(MarcRecord record) {
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

    public MarcRecord getRecord() {
        return record;
    }

    public void setRecord(MarcRecord record) {
        this.record = record;
    }

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    @Override
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
        if (!(arg0 instanceof RecordRepoxMarc)) { return false; }
        RecordRepoxMarc other = (RecordRepoxMarc)arg0;
        RecordComparer comparer = new RecordComparer(new HashSet<Integer>(0));
        return comparer.areEqual(record, getRecord());
    }

}
