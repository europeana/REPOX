/*
 * MarcRecord.java
 *
 * Created on 20 de Julho de 2002, 18:22
 */

package pt.utl.ist.marc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import pt.utl.ist.characters.CharacterConverterI;
import pt.utl.ist.characters.CharacterConverters;
import pt.utl.ist.marc.iso2709.ISOHandlerSingleRecord;
import pt.utl.ist.marc.iso2709.MARCPartialReader;
import pt.utl.ist.marc.util.Directory;
import pt.utl.ist.marc.util.Leader;
import pt.utl.ist.marc.xml.DomBuilder;
import pt.utl.ist.marc.xml.MarcXChangeBuilder;
import pt.utl.ist.marc.xml.RecordBuilderFromMarcXml;
import pt.utl.ist.repox.util.DomUtil;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a marc record
 * 
 * @author Nuno Freire
 */
public class Record implements Serializable, Iterable<Field> {
    static final long          serialVersionUID = 2;
    /**
     * Record terminator character (ASCII octal 035) 29.
     */
    public static final char   RT               = '\035';
    /**
     * Field terminator character (ASCII octal 036). 30
     */
    public static final char   FT               = '\036';
    /**
     * Delimiter (Unit Separator ASCII octal 037). 31
     */
    public static final char   US               = '\037';
    /** Record DEFAULT_LEADER */
    public static final String DEFAULT_LEADER   = "00000     2200000   450 ";
    /** Record nc */
    protected String           nc;
    /** Record recordType */
    protected RecordType       recordType;
    /** Record leader */
    protected String           leader;
    /** Record fields */
    protected List<Field>      fields           = new ArrayList<Field>(5);

    /**************************************************************************
     ************ Constructors ******************
     *************************************************************************/
    /**
     * Creates an empty Record
     * 
     */
    public Record() {
    }

    /**
     * Creates an empty Record
     * 
     * @param recordType
     *            the format of the record
     */
    public Record(RecordType recordType) {
        this.recordType = recordType;
    }

    /**
     * Creates a record from a String representation of the record in ISO2709
     * format
     * 
     * @param iso2709
     *            the String representation of the record in ISO2709 format
     */
    public Record(String iso2709) {
        MARCPartialReader mr = new MARCPartialReader();
        ISOHandlerSingleRecord handler = new ISOHandlerSingleRecord(this);
        mr.setMARCHandler(handler);
        mr.setMARCHandler(handler);
        mr.parse(new ByteArrayInputStream(iso2709.getBytes()), 1);
    }

    /**
     * Creates a record from a String representation of the record in ISO2709
     * format
     * 
     * @param iso2709
     *            the String representation of the record in ISO2709 format
     * @param recordType
     *            the format of the record
     */
    public Record(String iso2709, RecordType recordType) {
        this(iso2709);
        this.recordType = recordType;
    }

    /**
     * Creates a record from a byte[] representation of the record in ISO2709
     * format
     * 
     * @param originalObject
     *            iso2709 the byte[] representation of the record in ISO2709
     *            format
     * @param charset
     *            the charset of the record
     */
    public Record(byte[] originalObject, String charset) {
        CharacterConverterI converter = null;
        if (charset != null && !charset.equals("")) converter = CharacterConverters.getInstance(charset);

        MARCPartialReader mr;
        if (converter == null && charset != null && !charset.equals(""))
            mr = new MARCPartialReader(charset);
        else
            mr = new MARCPartialReader();
        ISOHandlerSingleRecord handler = new ISOHandlerSingleRecord(this, converter);
        mr.setMARCHandler(handler);
        mr.parse(new ByteArrayInputStream(originalObject), 1);
    }

    /**
     * Creates a record from a byte[] representation of the record in ISO2709
     * format
     * 
     * @param originalObject
     *            iso2709 the byte[] representation of the record in ISO2709
     *            format
     * @param charset
     *            the charset of the record
     * @param recordType
     *            the format of the record
     */
    public Record(byte[] originalObject, String charset, RecordType recordType) {
        this(originalObject, charset);
        this.recordType = recordType;
    }

    /**************************************************************************
     ************ Public Methods ******************
     *************************************************************************/
    @Override
    public java.util.Iterator<Field> iterator() {
        return fields.iterator();
    }

    /**
     * @param tag
     * @param subfield
     * @param value
     * @return boolean
     */
    public boolean contains(int tag, Character subfield, String value) {
        for (Field ff : getField(tag)) {
            for (String val : ff.getSubfieldValues(subfield)) {
                if (val.equals(value)) { return true; }
            }
        }
        return false;
    }

    /**
     * 
     */
    public void sortFields() {
        Collections.sort(fields, new Comparator<Field>() {
            public int compare(Field o1, Field o2) {
                return o1.getTag() - o2.getTag();
            }
        });
    }

    /**
     * @return boolean indicating if it is deleted
     */
    public boolean isDeleted() {
        return leader != null && leader.length() >= 6 && leader.charAt(5) == 'd';
    }

    /**
     * Set as deleted
     */
    public void setAsDeleted() {
        if (leader == null) leader = DEFAULT_LEADER;
        leader = leader.substring(0, 5) + "d" + leader.substring(6);
    }

    /**
     * Creates an empry field in the record
     * 
     * @param tag
     *            the tag of the field
     * @return the added field
     * 
     * @deprecated use addField(int)
     */
    public pt.utl.ist.marc.Field addField(String tag) {
        short tagInt = Short.parseShort(tag);
        return addField(tagInt);
    }

    /**
     * Creates an empry field in the record
     * 
     * @param tag
     *            the tag of the field
     * @return the added field
     */
    public pt.utl.ist.marc.Field addField(int tag) {
        return addField((short)tag);
    }

    /**
     * Creates an empry field in the record
     * 
     * @param tag
     *            the tag of the field
     * @return the added field
     */
    public pt.utl.ist.marc.Field addField(short tag) {
        Field ret = new Field(tag, ' ', ' ');
        int sz = fields.size();
        for (int idx = 0; idx < sz; idx++) {
            Field f = (Field)fields.get(idx);
            if (f.getTag() > tag) {
                fields.add(idx, ret);
                return ret;
            }
        }
        fields.add(ret);
        return ret;
    }

    /**
     * adds the field to the record
     * 
     * @param field
     *            the Field to add
     */
    public void addField(Field field) {
        if (field.getTag() == 1 && (fields.size() == 0 || fields.get(0).getTag() != 1)) nc = field.getValue();

        int sz = fields.size();
        for (int idx = 0; idx < sz; idx++) {
            Field f = (Field)fields.get(idx);
            if (f.getTag() > field.getTag()) {
                fields.add(idx, field);
                return;
            }
        }
        fields.add(field);
    }

    /**
     * Removes all fields with the tag
     * 
     * @param tag
     */
    public void removeField(int tag) {
        for (int idx = 0; idx < fields.size(); idx++) {
            Field f = (Field)fields.get(idx);
            if (f.getTag() == tag) {
                fields.remove(idx);
                idx--;
            } else if (f.getTag() > tag) break;
        }
    }

    /**
     * gets a value of a field in the record. To be used only for non repeatable
     * fields/subfields, as it returns the value of first field/subfield that
     * matches. If the tag specifies a control field, the subfield is ignored
     * 
     * @param tag
     *            the field tag
     * @param subfield
     *            the subfield code
     * @return the value of first field/subfield that matches the parameters, or
     *         null if not found
     */
    public String getSingleFieldValue(int tag, char subfield) {
        Field f = getSingleField(tag);
        if (f == null) return null;
        if (tag < 10) return f.getValue();
        return f.getSingleSubfieldValue(subfield);
    }

    /**
     * gets a value of a field in the record. To be used only for non repeatable
     * fields/subfields, as it returns the value of first field/subfield that
     * matches. If the tag specifies a control field, the subfield is ignored
     * 
     * @param tag
     *            the field tag
     * @param subfield
     *            the subfield code
     * @return the value of first field/subfield that matches the parameters, or
     *         null if not found
     */
    public Subfield getSingleSubfield(int tag, char subfield) {
        Field f = getSingleField(tag);
        if (f == null) return null;
        if (tag < 10) return null;
        return f.getSingleSubfield(subfield);
    }

    /**
     * gets all values of a field/subfield in the record. If the tag specifies a
     * control field, the subfield is ignored
     * 
     * @param tag
     *            the field tag
     * @param subfield
     *            the subfield code
     * @return all values of a field/subfield in the record, or an empty List
     */
    public List<String> getFieldValues(int tag, char subfield) {
        List<String> ret = new ArrayList<String>();
        List<Field> f = getField(tag);
        if (f.size() == 0) return ret;
        if (tag < 10) {
            for (Object aF : f) {
                Field el = (Field)aF;
                ret.add(el.getValue());
            }
        } else {
            for (Object aF : f) {
                Field el = (Field)aF;
                List<String> sfs = el.getSubfieldValues(subfield);
                for (Object sf : sfs) {
                    ret.add((String)sf);
                }
            }
        }
        return ret;
    }

    /**
     * gets a field in the record. To be used only for non repeatable
     * fields/subfields, as it returns the first field that matches.
     * 
     * @param tag
     *            the field tag
     * @return the first field that matches the parameters, or null if not found
     */
    public Field getSingleField(int tag) {
        int sz = fields.size() - 1;
        for (int idx = 0; idx <= sz; idx++) {
            Field f = fields.get(idx);
            if (f.getTag() == tag) return f;
        }
        return null;
    }

    /**
     * gets all occurrences of a field in the record.
     * 
     * @param tag
     *            the field tag
     * @return all occurrences of a field in the record, or an empty List
     */
    public List<Field> getField(int tag) {
        List<Field> ret = new ArrayList<Field>();
        int sz = fields.size() - 1;
        for (int idx = 0; idx <= sz; idx++) {
            Field f = fields.get(idx);
            if (f.getTag() == tag) ret.add(f);
        }
        return ret;
    }

    /**
     * gets all occurrences of a field in the record that has a value in a
     * subfield.
     * 
     * @param tag
     *            the field tag
     * @param subfield
     *            the subfield code
     * @param equalsThis
     *            the value to compare to
     * @return all occurrences of a field in the record, or an empty List
     */
    public List<Field> getFieldWhere(int tag, char subfield, String equalsThis) {
        List<Field> ret = new ArrayList<Field>();
        int sz = fields.size() - 1;
        for (int idx = 0; idx <= sz; idx++) {
            Field f = fields.get(idx);
            if (f.getTag() == tag) {
                for (String value : f.getSubfieldValues(subfield)) {
                    if (value.equals(equalsThis)) {
                        ret.add(f);
                        break;
                    }
                }
            }
        }
        return ret;
    }

    /**
     * gets all occurrences of several fields in the record.
     * 
     * @param tags
     *            the field tags
     * @return all occurrences of several fields in the record, or an empty List
     */
    public List<Field> getFields(int... tags) {
        List<Field> ret = new ArrayList<Field>(tags.length);
        for (int tag : tags) {
            ret.addAll(getField(tag));
        }
        return ret;
    }

    /**
     * gets the 2xx field of the record.
     * 
     * @deprecated use UnimarcUtil.getMainHeadingField
     * @return the 2xx field of the record, or null if not found
     */
    public Field getMainHeadingField() {
        for (Object o : getFields()) {
            Field fld = (Field)o;
            if (fld.getTag() >= 200 && fld.getTag() < 300) { return fld; }
        }
        return null;
    }

    /**
     * Gets a String representation of the publication year
     * 
     * @deprecated use UnimarcUtil.getYearOfPublication
     * @return the 210$d the record, null if the record contains no 210$d
     */
    public String getYearOfPublicationOfBibliographicRecord() {
        String dat = getSingleFieldValue(210, 'd');
        if (dat != null) dat = dat.trim();
        return dat;
    }

    /**
     * Gets a String representation of the title of the record. It contains the
     * title, the publication location and the publication date. To be used only
     * with bibliographic records
     * 
     * @deprecated use UnimarcUtil.getTitle
     * @return the title of the record, an empty string if the record contains
     *         no title
     */
    public String getTitleOfBibliographicRecord() {
        String tit = getTitleOfBibliographicWithoutPlaceAndDate();
        if (tit.equals("")) return "";
        String loc = getSingleFieldValue(210, 'a');
        String dat = getSingleFieldValue(210, 'd');
        if (tit == null) tit = "";
        if (loc == null || loc.toLowerCase().indexOf("s.l.") != -1)
            loc = "";
        else
            loc = ", " + loc;
        if (dat == null)
            dat = "";
        else
            dat = ", " + dat;
        return tit + loc + dat;
    }

    /**
     * Gets a String representation of the title of the record. It contains the
     * only the title, not the publication location and the publication date. To
     * be used only with bibliographic records
     * 
     * @deprecated use UnimarcUtil.getTitleWithoutPlaceAndDate
     * @return the title of the record, an empty string if the record contains
     *         no title
     */
    public String getTitleOfBibliographicWithoutPlaceAndDate() {
        List<Field> fields200 = getField(200);
        if (fields200.size() == 0) return "";
        Field f200 = getField(200).get(0);
        String tit = f200.getSingleSubfieldValue('a');
        if (tit == null) return "";
        for (String sc : f200.getSubfieldValues('c'))
            tit += ". " + sc;
        for (String sc : f200.getSubfieldValues('d'))
            tit += " = " + sc;
        for (String sc : f200.getSubfieldValues('e'))
            tit += ": " + sc;
        for (String sc : f200.getSubfieldValues('i'))
            tit += ". " + sc;

        tit = tit.replaceFirst("^\\s*<([^>]+)>", "$1");
        return tit;
    }

    /**
     * Gets a String representation of the title of the record. It contains the
     * title, the publication location and the publication date. To be used only
     * with bibliographic records
     * 
     * @deprecated use UnimarcUtil.getAuthorLists
     * @return the title of the record
     */
    public String getAuthorListOfBibliographicRecord() {
        String ret = "";
        int[][] fields = new int[3][];
        fields[0] = new int[] { 700, 710, 720 };
        fields[1] = new int[] { 701, 711, 721 };
        fields[2] = new int[] { 702, 712, 722 };
        for (int i = 0; i < 3; i++) {
            for (Field fld : getFields(fields[i])) {
                String sfa = fld.getSingleSubfieldValue('a');
                String sfb = fld.getSingleSubfieldValue('b');
                if (sfa != null) {
                    sfa = sfa.trim();
                    if (sfa.endsWith(",")) sfa = sfa.substring(0, sfa.length() - 1).trim();
                } else {
                    continue;
                }
                if (sfb != null) {
                    sfb = sfb.trim();
                    if (sfb.endsWith(",")) sfb = sfb.substring(0, sfb.length() - 1).trim();
                }
                if (!ret.equals("")) ret += "; ";
                if (sfb == null) {
                    ret += sfa;
                } else {
                    ret += sfa + ", " + sfb;
                }
            }
        }
        return ret;
    }

    /**
     * Codes the record in Iso2709 format
     * 
     * @return a String representation of the record in ISO 2709 format
     */
    public String toIso2709() {
        StringBuffer data = new StringBuffer();
        Directory directory = new Directory();
        Leader leader = new Leader(getLeader());

        // append fields to directory and data
        for (Object o : getFields()) {
            Field fld = (Field)o;
            String fldStr = fld.toIso2709();
            directory.add(fld.getTagAsString(), fldStr.length());
            data.append(fldStr);
        }

        // add base address of data and logical record length tp the leader
        int baseAddress = 24 + directory.getLength();
        int recordLength = baseAddress + data.length() + 1;
        leader.setRecordLength(recordLength);
        leader.setBaseAddressOfData(baseAddress);

        // return record in tape format
        return leader.getSerializedForm() + directory.getSerializedForm() + data + Record.RT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer b = new StringBuffer().append("\nNC: " + nc).append("\n000: " + leader);
        for (Field el : fields) {
            b.append("\n");
            b.append(el);
        }
        b.append("\n");
        return b.toString();
    }

    public Record clone() {
        Record rec = new Record();
        rec.setLeader(getLeader());
        rec.setRecordType(getRecordType());
        for (Object o : getFields()) {
            Field srcField = (Field)o;
            if (srcField.getTag() == 1) {
                rec.setNc(srcField.getValue());
            } else {
                Field newField = srcField.clone();
                rec.addField(newField);
            }
        }
        return rec;
    }

    /**
     * codes the record in DOM
     * 
     * @return a DOM Document representing the record
     */
    public Document toMarcXChangeDom(boolean withCollectionElement) {
        return MarcXChangeBuilder.record2Dom(this, withCollectionElement);
    }

    public Document toMarcXChangeDom(boolean withCollectionElement, String marcType) {
        return MarcXChangeBuilder.record2Dom(this, withCollectionElement, marcType);
    }

    /**
     * codes the record in DOM
     * 
     * @return a DOM Element representing the record
     */
    public Element toMarcXChangeDomElement() {
        return MarcXChangeBuilder.record2DomElement(this, null);
    }

    public Element toMarcXChangeDomElement(String marcType) {
        return MarcXChangeBuilder.record2DomElement(this, null, marcType);
    }

    /**
     * codes the record in DOM
     * 
     * @param documentForFactory
     *            the document to use as factory for the DOM elements
     * @return a DOM Element representing the record
     */
    public Element toMarcXChangeDomElement(Document documentForFactory) {
        return MarcXChangeBuilder.record2DomElement(this, documentForFactory);
    }

    public Element toMarcXChangeDomElement(Document documentForFactory, String marcType) {
        return MarcXChangeBuilder.record2DomElement(this, documentForFactory, marcType);
    }

    /**
     * the record coded in xml
     * 
     * @return the record coded in xml
     */
    public String toMarcXChangeXmlString() {
        return toMarcXChangeXmlString(null);
    }

    public String toMarcXChangeXmlString(String marcType) {
        return MarcXChangeBuilder.record2XMLString(this, marcType);
    }

    /**
     * the record coded in xml
     * 
     * @return the record coded in xml
     */
    public byte[] toMarcXChangeXmlbytes() {
        return toMarcXChangeXmlbytes(null);
    }

    public byte[] toMarcXChangeXmlbytes(String marcType) {
        return MarcXChangeBuilder.record2XMLBytes(this, marcType);
    }

    public String toHtml() {
        StringBuffer ret = new StringBuffer().append("<p style=\'white-space:-moz-pre-wrap; font-family: \"Courier New\", Courier, mono; font-size: 12px;\'>NC: ").append(nc).append("<br><b>000</b>: ").append(leader);
        for (Field el : fields) {
            ret.append("<br>");
            ret.append(el.toHtml());
        }
        ret.append("</p>");
        return ret.toString();

    }

    /**
     * codes the record in DOM
     * 
     * @return a DOM Document representing the record
     */
    public Document toDom() {
        return DomBuilder.record2Dom(this);
    }

    /**
     * codes the record in DOM
     * 
     * @return a DOM Element representing the record
     */
    public Element toDomElement() {
        return DomBuilder.record2DomElement(this, null);
    }

    /**
     * codes the record in DOM
     * 
     * @param documentForFactory
     *            the document to use as factory for the DOM elements
     * @return a DOM Element representing the record
     */
    public Element toDomElement(Document documentForFactory) {
        return DomBuilder.record2DomElement(this, documentForFactory);
    }

    /**
     * the record coded in xml
     * 
     * @return the record coded in xml
     */
    public String toXmlString() {
        return DomBuilder.record2XMLString(this);
    }

    /**
     * the record coded in xml
     * 
     * @return the record coded in xml
     */
    public byte[] toXmlbytes() {
        return DomBuilder.record2XMLBytes(this);
    }

    /**
     * Creates a Record object by parsing a xml string
     * 
     * @param xmlString
     *            a xml representation of the record
     * @return record parsed from the string
     * @throws SAXException
     */
    public static Record fromXmlString(String xmlString) throws SAXException {
        return RecordBuilderFromMarcXml.domToRecord(DomUtil.parseDomFromString(xmlString));
    }

    /**
     * Creates a Record object by parsing a xml Dom
     * 
     * @param dom
     *            a xml representation of the record
     * @return record parsed from the dom
     * @throws SAXException
     */
    public static Record fromDom(Document dom) {
        return RecordBuilderFromMarcXml.domToRecord(dom);
    }

    /**
     * Creates a Record object by parsing a xml string
     * 
     * @param xmlString
     *            a xml representation of the record
     * @return record parsed from the string
     * @throws SAXException
     */
    public static Record fromMarcXChangeString(String xmlString) throws SAXException {
        return RecordBuilderFromMarcXml.domToRecord(DomUtil.parseDomFromString(xmlString));
    }

    /**
     * Creates a Record object by parsing a xml Dom
     * 
     * @param dom
     *            a xml representation of the record
     * @return record parsed from the dom
     * @throws SAXException
     */
    public static Record fromMarcXChangeDom(Node dom) {
        return RecordBuilderFromMarcXml.domToRecord(dom);
    }

    /**************************************************************************
     ************ Private Methods ******************
     *************************************************************************/

    /**************************************************************************
     ************ Properties Methods ******************
     *************************************************************************/
    /**
     * Gets the control number (identifier) of the record
     * 
     * @return the control number of the record. May be null if the record does
     *         not contain one
     */
    public String getNc() {
        if (nc == null || nc.equals("")) {
            Field f = getSingleField(1);
            if (f != null) {
                try {
                    nc = f.getValue();
                } catch (NumberFormatException e) {
                }
            }
        }
        return nc;
    }

    /**
     * Sets the control number (identifier) of the record
     * 
     * @param nc
     *            the control number
     */
    public void setNc(String nc) {
        this.nc = nc;
        Field f001 = getSingleField(1);
        if (f001 == null) {
            f001 = new Field(1, nc);
            fields.add(0, f001);
        } else {
            f001.setValue(nc);
        }
    }

    /**
     * @return the record LEADER (pt: Etiqueta de registo)
     */
    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    /**
     * @return the fields of the document
     */
    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

}
