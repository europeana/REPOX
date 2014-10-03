/*
 * Utility.java
 *
 * Created on 23 de Novembro de 2001, 18:50
 */

package pt.utl.ist.util.marc;

import java.util.ArrayList;
import java.util.List;

import pt.utl.ist.marc.MarcField;
import pt.utl.ist.marc.MarcObjectFactory;
import pt.utl.ist.marc.MarcRecord;
import pt.utl.ist.marc.MarcSubfield;

/**
 * 
 * @author Nuno Freire
 */
public class MarcUtil {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MarcUtil.class);

    /** Creates a new instance of Utility */
    public MarcUtil() {
    }

    /**
     * @param tag
     * @return String
     * @deprecated Use the methods in Record and Field classes
     */
    @Deprecated
    public static String tagToString(int tag) {
        String st = String.valueOf(tag);
        if (tag < 10) return "00" + st;
        if (tag < 100) return "0" + st;
        return st;
    }

    /**
     * @param tag
     * @param subfield
     * @param marc
     * @return String of the Field value
     * @deprecated Use the methods in Record and Field classes
     */
    @Deprecated
    public static String getSingleFieldValue(String tag, char subfield, MarcRecord marc) {
        MarcField f = getSingleField(tag, marc);
        if (f == null) return null;
        if (Integer.parseInt(tag) < 10) return f.getValue();
        return getSingleSubfieldValue(subfield, f);
    }

    /**
     * @param tag
     * @param subfield
     * @param marc
     * @return List of Field values
     * @deprecated Use the methods in Record and Field classes
     */
    @Deprecated
    public static List getFieldValues(String tag, char subfield, MarcRecord marc) {
        List ret = new ArrayList();
        List f = getField(tag, marc);
        if (f.size() == 0) return ret;
        if (Integer.parseInt(tag) < 10) {
            for (Object aF : f) {
                MarcField el = (MarcField)aF;
                ret.add(el.getValue());
            }
        } else {
            for (Object aF : f) {
                MarcField el = (MarcField)aF;
                List sfs = getSubfieldValues(subfield, el);
                for (Object sf : sfs) {
                    ret.add((String)sf);
                }
            }
        }
        return ret;
    }

    /**
     * @param tag
     * @param marc
     * @return Field
     * @deprecated Use the methods in Record and Field classes
     */
    @Deprecated
    public static MarcField getSingleField(String tag, MarcRecord marc) {
        List flds = marc.getFields();
        //System.err.print(flds);            
        int sz = flds.size() - 1;
        for (int idx = 0; idx <= sz; idx++) {
            //System.err.print(idx);            
            MarcField f = (MarcField)flds.get(idx);
            //System.err.println(f);            
            if (f.getTagAsString().equals(tag)) return f;
        }
        return null;
    }

    /**
     * @param tag
     * @param marc
     * @return List of Fields
     * @deprecated Use the methods in Record and Field classes
     */
    @Deprecated
    public static List<MarcField> getField(String tag, MarcRecord marc) {
        List<MarcField> ret = new ArrayList<MarcField>();
        List flds = marc.getFields();
        int sz = flds.size() - 1;
        for (int idx = 0; idx <= sz; idx++) {
            MarcField f = (MarcField)flds.get(idx);
            if (f.getTagAsString().equals(tag)) ret.add(f);
        }
        return ret;
    }

    /**
     * @param sf
     * @param fld
     * @return String og the subfield value
     * @deprecated Use the methods in Record and Field classes
     */
    @Deprecated
    public static String getSingleSubfieldValue(char sf, MarcField fld) {
        List sflds = fld.getSubfields();
        int sz = sflds.size() - 1;
        for (int idx = 0; idx <= sz; idx++) {
            MarcSubfield f = (MarcSubfield)sflds.get(idx);
            if (f.getCode() == sf) return f.getValue();
        }
        return null;
    }

    /**
     * @param sf
     * @param fld
     * @return List of subfield values
     * @deprecated Use the methods in Record and Field classes
     */
    @Deprecated
    public static List<String> getSubfieldValues(char sf, MarcField fld) {
        List<String> ret = new ArrayList<String>();
        List sflds = fld.getSubfields();
        int sz = sflds.size() - 1;
        for (int idx = 0; idx <= sz; idx++) {
            MarcSubfield f = (MarcSubfield)sflds.get(idx);
            if (f.getCode() == sf) {
                ret.add(f.getValue());
            }
        }
        return ret;
    }

    /**
     * @param rec
     * @return String of the title of the bibliographic record
     * @deprecated Use the methods in Record and Field classes
     */
    @Deprecated
    public static String getTitleOfBibliographicRecord(MarcRecord rec) {
        String tit = "";
        List tits = MarcUtil.getFieldValues("200", 'a', rec);
        for (Object tit1 : tits) {
            tit += (String)tit1;
        }
        String loc = MarcUtil.getSingleFieldValue("210", 'a', rec);
        String dat = MarcUtil.getSingleFieldValue("210", 'd', rec);
        if (tit == null) tit = "";
        if (loc == null || loc.toLowerCase().indexOf("s.l.") != -1)
            loc = "";
        else
            loc = ", " + loc;
        if (dat == null)
            dat = "";
        else
            dat = ", " + dat;
        tit = tit.replaceFirst("^\\s*<([^>]+)>", "$1");
        return tit + loc + dat;
    }

    /**
     * @deprecated Use the methods in Record and Field classes
     */
    @Deprecated
    public static String getHeadingString(MarcField fld, char[] useSubfields, String separator, boolean removeBicos) {
        StringBuffer value = new StringBuffer();
        boolean first = true;
        for (Object o : fld.getSubfields()) {
            MarcSubfield sf = (MarcSubfield)o;
            boolean append = false;
            int sz = useSubfields.length;
            for (int idx = sz - 1; idx >= 0; idx--) {
                if (useSubfields[idx] == sf.getCode()) {
                    append = true;
                    break;
                }
            }
            if (append) {
                if (!first) {
                    value.append(separator);
                } else
                    first = false;
                String sfv = sf.getValue();
                if (removeBicos) {
                    sfv = sfv.replaceFirst("^\\s*<([^>]+)>", "$1");
                }
                value.append(sfv);
            }
        }
        String ret = value.toString().trim();
        if (ret.length() > 0) {
            while (ret.endsWith(" "))
                ret = ret.substring(0, ret.length() - 1);
            char lastChar = ret.charAt(ret.length() - 1);
            if (lastChar == ',' || lastChar == ';' || lastChar == ':') ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }

    /**
     * @param rec
     * @param fields
     * @return List of Fields
     * @deprecated Use the methods in Record and Field classes
     */
    @Deprecated
    public static List<MarcField> getFields(MarcRecord rec, String[] fields) {
        List<MarcField> ret = new ArrayList<MarcField>(fields.length);
        for (String field : fields) {
            ret.addAll(getField(field, rec));
        }
        return ret;
    }

    /**
     * @return Record of test Authority
     */
    public static MarcRecord createTestAuthorityRecord() {
        MarcRecord ret = new MarcRecord();
        ret.setNc("1");
        MarcField fld = ret.addField(1);
        fld.setValue("1");
        fld = ret.addField(200);
        fld.addSubfield('a', "Freire");
        fld.addSubfield('b', "Nuno");
        fld = ret.addField(500);
        fld.addSubfield('a', "Freire");
        fld.addSubfield('b', "Nuno");
        fld.addSubfield('3', "123");
        fld = ret.addField(500);
        fld.addSubfield('a', "Freire");
        fld.addSubfield('b', "Nuno");
        fld.addSubfield('3', "1234");
        return ret;
    }

    /**
     * @return Record of test Authority
     */
    public static MarcRecord createTestBibliographicRecord() {
        MarcRecord ret = new MarcRecord();
        ret.setNc("1234");
        ret.setLeader("000000000000000000000000");
        //        Field fld=ret.addField(1);
        //        fld.setValue("1");
        MarcField fld = ret.addField(100);
        fld.addSubfield('a', "19790301d1978    m  y0pora0103    ba");
        fld = ret.addField(200);
        fld.addSubfield('a', "Uma Biblioteca");
        fld.addSubfield('a', "Teses");
        fld = ret.addField(210);
        fld.addSubfield('a', "Lisboa");
        fld.addSubfield('d', "2000");
        fld = ret.addField(700);
        fld.addSubfield('a', "Pessoa");
        fld.addSubfield('b', "Fernando");
        fld.addSubfield('f', "1999-2000");
        fld = ret.addField(701);
        fld.addSubfield('a', "Nuno");
        fld.addSubfield('b', "Freire");
        fld.addSubfield('f', "1975-");
        fld.addSubfield('4', "660");
        fld = ret.addField(702);
        fld.addSubfield('a', "Rita");
        fld.addSubfield('b', "Martins");
        fld.addSubfield('f', "1973-");
        fld.addSubfield('4', "660");
        return ret;
    }

    /**
     * @param source
     * @param factory
     * @return Converted Record
     * @deprecated
     */
    @Deprecated
    public static MarcRecord convertRecordToOtherFactory(MarcRecord source, MarcObjectFactory factory) {
        if (factory.isFromThisFactory(source)) return source;
        MarcRecord rec = factory.newRecord();
        rec.setLeader(source.getLeader());
        rec.setNc(source.getNc());
        for (Object o1 : source.getFields()) {
            MarcField srcField = (MarcField)o1;
            MarcField newField = rec.addField(Integer.parseInt(srcField.getTagAsString()));
            newField.setInd1(srcField.getInd1());
            newField.setInd2(srcField.getInd2());
            if (srcField.isControlField())
                newField.setValue(srcField.getValue());
            else {
                for (Object o : srcField.getSubfields()) {
                    MarcSubfield srcSf = (MarcSubfield)o;
                    newField.addSubfield(srcSf.getCode(), srcSf.getValue());
                }
            }
        }
        return rec;
    }

    /**
     * @param rec
     * @return main heading Field
     * @deprecated Use the methods in Record and Field classes
     */
    @Deprecated
    public static MarcField getMainHeadingField(MarcRecord rec) {
        for (Object o : rec.getFields()) {
            MarcField fld = (MarcField)o;
            if (fld.getTagAsString().startsWith("2")) { return fld; }
        }
        return null;
    }

}
