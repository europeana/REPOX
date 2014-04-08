/*
 * Utility.java
 *
 * Created on 23 de Novembro de 2001, 18:50
 */

package pt.utl.ist.marc.util;

import pt.utl.ist.marc.Field;
import pt.utl.ist.marc.MarcObjectFactory;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.Subfield;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author  Nuno Freire
 */
public class MarcUtil {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger( MarcUtil.class);
    
    /** Creates a new instance of Utility */
    public MarcUtil() {
    }

    /**
     * @deprecated Use the methods in Record and Field classes
     */
    public static String tagToString(int tag) {
        String st=String.valueOf(tag);
        if (tag<10)
            return "00"+st;
        if (tag<100)
            return "0"+st;
        return st;
    }

    /**
     * @deprecated  Use the methods in Record and Field classes
     */
    public static String getSingleFieldValue(String tag, char subfield, Record marc) {
        Field f=getSingleField(tag,marc);
        if (f==null) 
			return null;        
        if (Integer.parseInt(tag)<10)
            return f.getValue();
        return getSingleSubfieldValue(subfield, f);
    }

    
    /**
     * @deprecated  Use the methods in Record and Field classes
     */
    public static List getFieldValues(String tag, char subfield, Record marc) {
        List ret=new ArrayList();
        List f=getField(tag,marc);
        if (f.size()==0) 
			return ret;        
        if (Integer.parseInt(tag)<10){
            for (Object aF : f) {
                Field el = (Field) aF;
                ret.add(el.getValue());
            }
        }else{
            for (Object aF : f) {
                Field el = (Field) aF;
                List sfs = getSubfieldValues(subfield, el);
                for (Object sf : sfs) {
                    ret.add((String) sf);
                }
            }
        }
    	return ret;        
    }
    
    
    /**
     * @deprecated  Use the methods in Record and Field classes
     */
    public static Field getSingleField(String tag, Record marc) {
        List flds=marc.getFields();
//System.err.print(flds);            
        int sz = flds.size() - 1;
		for (int idx = 0 ; idx <= sz ; idx++) {
//System.err.print(idx);            
		    Field f=(Field)flds.get( idx);
//System.err.println(f);            
            if (f.getTagAsString().equals(tag))
                return f;
		}        
        return null;
    }

    /**
     * @deprecated  Use the methods in Record and Field classes
     */
    public static List<Field> getField(String tag, Record marc) {
        List<Field> ret=new ArrayList<Field>();
        List flds=marc.getFields();
        int sz = flds.size() - 1;
		for (int idx = 0 ; idx <= sz ; idx++) {
		    Field f=(Field)flds.get( idx);
            if (f.getTagAsString().equals(tag))
                ret.add(f);
		}
        return ret;
    }    
    
    
    /**
     * @deprecated  Use the methods in Record and Field classes
     */
    public static String getSingleSubfieldValue(char sf, Field fld) {
        List sflds=fld.getSubfields();
        int sz = sflds.size() - 1;
		for (int idx = 0 ; idx <= sz ; idx++) {
		    Subfield f=(Subfield)sflds.get( idx);
            if (f.getCode()==sf)
                return f.getValue();
		}        
        return null;
    }

    /**
     * @deprecated  Use the methods in Record and Field classes
     */
    public static List<String> getSubfieldValues(char sf, Field fld) {
        List<String> ret=new ArrayList<String>();
        List sflds=fld.getSubfields();
        int sz = sflds.size() - 1;
		for (int idx = 0 ; idx <= sz ; idx++) {
		    Subfield f=(Subfield)sflds.get( idx);
            if (f.getCode()==sf){
                ret.add(f.getValue());
            }
		}        
        return ret;
    }
    

    /**
     * @deprecated  Use the methods in Record and Field classes
     */    
    public static String getTitleOfBibliographicRecord(Record rec){
        String tit="";
        List tits=MarcUtil.getFieldValues("200",'a',rec);
        for (Object tit1 : tits) {
            tit += (String) tit1;
        }
        String loc=MarcUtil.getSingleFieldValue("210",'a',rec);
        String dat=MarcUtil.getSingleFieldValue("210",'d',rec);
        if (tit==null) tit = "";
        if (loc==null || loc.toLowerCase().indexOf("s.l.") != -1) loc = ""; else loc=", "+loc;
        if (dat==null) dat = ""; else dat=", "+dat;
        tit=tit.replaceFirst("^\\s*<([^>]+)>","$1");
        return tit+loc+dat;    
    }  

    /**
     * @deprecated  Use the methods in Record and Field classes
     */
    public static String getHeadingString(Field fld, char[] useSubfields, String separator, boolean removeBicos){        
        StringBuffer value=new StringBuffer();
        boolean first=true;
        for (Object o : fld.getSubfields()) {
            Subfield sf = (Subfield) o;
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
        String ret=value.toString().trim();
        if (ret.length()>0){
            while (ret.endsWith(" "))
                ret=ret.substring(0, ret.length()-1);
            char lastChar=ret.charAt(ret.length()-1);
            if (lastChar==',' || lastChar==';' || lastChar==':')
                ret=ret.substring(0, ret.length()-1);
        }
        return ret;    
    }
    
      
    
   
    
    /**
     * @deprecated  Use the methods in Record and Field classes
     */
    public static List<Field> getFields(Record rec, String[] fields){
        List<Field> ret=new ArrayList<Field>(fields.length);
        for (String field : fields) {
            ret.addAll(getField(field, rec));
        }
        return ret;
    }
    
    public static Record createTestAuthorityRecord(){
        Record ret=new Record();
        ret.setNc("1");
        Field fld=ret.addField(1);
        fld.setValue("1");
        fld=ret.addField(200);
        fld.addSubfield('a',"Freire");
        fld.addSubfield('b',"Nuno");
        fld=ret.addField(500);
        fld.addSubfield('a',"Freire");
        fld.addSubfield('b',"Nuno");
        fld.addSubfield('3',"123");
        fld=ret.addField(500);
        fld.addSubfield('a',"Freire");
        fld.addSubfield('b',"Nuno");
        fld.addSubfield('3',"1234");
        return ret;        
    }

    public static Record createTestBibliographicRecord(){
        Record ret=new Record();
        ret.setNc("1234");
        ret.setLeader("000000000000000000000000");
//        Field fld=ret.addField(1);
//        fld.setValue("1");
        Field fld=ret.addField(100);
        fld.addSubfield('a',"19790301d1978    m  y0pora0103    ba");   
        fld=ret.addField(200);
        fld.addSubfield('a',"Uma Biblioteca");
        fld.addSubfield('a',"Teses");
        fld=ret.addField(210);
        fld.addSubfield('a',"Lisboa");
        fld.addSubfield('d',"2000");        
        fld=ret.addField(700);
        fld.addSubfield('a',"Pessoa");        
        fld.addSubfield('b',"Fernando");        
        fld.addSubfield('f',"1999-2000");        
        fld=ret.addField(701);
        fld.addSubfield('a',"Nuno");        
        fld.addSubfield('b',"Freire");        
        fld.addSubfield('f',"1975-");        
        fld.addSubfield('4',"660");
        fld=ret.addField(702);
        fld.addSubfield('a',"Rita");        
        fld.addSubfield('b',"Martins");        
        fld.addSubfield('f',"1973-");        
        fld.addSubfield('4',"660");
        return ret;        
    }
    
    /**
     * @deprecated 
     */
    public static Record convertRecordToOtherFactory(Record source, MarcObjectFactory factory){
        if (factory.isFromThisFactory(source))
            return source;
        Record rec=factory.newRecord();
        rec.setLeader(source.getLeader());
        rec.setNc(source.getNc());
        for (Object o1 : source.getFields()) {
            Field srcField = (Field) o1;
            Field newField = rec.addField(Integer.parseInt(srcField.getTagAsString()));
            newField.setInd1(srcField.getInd1());
            newField.setInd2(srcField.getInd2());
            if (srcField.isControlField())
                newField.setValue(srcField.getValue());
            else {
                for (Object o : srcField.getSubfields()) {
                    Subfield srcSf = (Subfield) o;
                    newField.addSubfield(srcSf.getCode(), srcSf.getValue());
                }
            }
        }
        return rec;
    }
    

    /**
     * @deprecated  Use the methods in Record and Field classes
     */
    public static Field getMainHeadingField(Record rec){
        for (Object o : rec.getFields()) {
            Field fld = (Field) o;
            if (fld.getTagAsString().startsWith("2")) {
                return fld;
            }
        }        
        return null;
    }        
    
}
