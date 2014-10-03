/*
 * MarkRemover.java
 *
 * Created on 14 de Janeiro de 2003, 11:40
 */

package pt.utl.ist.util.marc;

import org.w3c.dom.Document;

import pt.utl.ist.marc.MarcField;
import pt.utl.ist.marc.MarcRecord;
import pt.utl.ist.marc.MarcSubfield;
import pt.utl.ist.marc.xml.DomBuilder;
import pt.utl.ist.marc.xml.RecordBuilderFromMarcXml;
import pt.utl.ist.util.structure.MapOfLists;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author  Nuno Freire
 */
public class RemovePontuationVisitor {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger( RemovePontuationVisitor.class);
    
    private static Pattern bicosPattern=Pattern.compile("^\\s*<([^>]+)>");

    private static Pattern pontuationAtStartPattern=Pattern.compile("^[\\)\\]\\} ,;:/]+");
    private static Pattern pontuationAtEndPattern=Pattern.compile("[ ,;:/\\[\\{\\(]+$");
    private static Pattern pontuationSoleParentesisAtStartPattern=Pattern.compile("[ \\(\\{\\[]+[^\\]\\)\\}]+");
    private static Pattern pontuationSoleParentesisAtEndPattern=Pattern.compile("[^\\[\\(\\{]+[ \\)\\]\\}]+");
    private static Pattern pontuationSoleParentesisAtStartCleanPattern=Pattern.compile("^[ \\(\\[\\{]+");
    private static Pattern pontuationSoleParentesisAtEndCleanPattern=Pattern.compile("[ \\)\\]\\}]+$");
    
    
    /** RemovePontuationVisitor fields */
    protected static HashMap fields;
    static{
        fields=new HashMap(10);
        RemoverDefinition d=new RemoverDefinition("200");
        d.addIn("d","^\\s*=\\s*");
        fields.put(d.field,d);
        d=new RemoverDefinition("205");
        d.addIn("a","\\s*\\,\\s*$");
        fields.put(d.field,d);
        d=new RemoverDefinition("210");
//        d.addIn("d","\\s*\\]\\s*$");
        d.addIn("g","\\s*\\)\\s*$");
//        d.addIn("c","(\\s*,\\s*$|^\\s*\\[\\s*)");
        d.addIn("c","\\s*,\\s*$");
        d.addBefore("g","\\s*:\\s*$");
        fields.put(d.field,d);
        d=new RemoverDefinition("215");
        d.addIn("a","^\\s*=\\s*");
        d.addIn("c","^\\s*;\\s*");
        d.addBefore("d","\\s*;\\s*$");
        fields.put(d.field,d);
        d=new RemoverDefinition("225");
        d.addIn("d","^\\s*=\\s*");
        d.addIn("last","\\s*\\)\\s*$");
        fields.put(d.field,d);
        d=new RemoverDefinition("600");
        d.addBefore("b","\\s*,\\s*$");
        d.addBefore("c","\\s*,\\s*$");
        d.addBefore("f","\\s*,\\s*$");
        fields.put(d.field,d);
        d=new RemoverDefinition("500");
        d.addBefore("h","\\s*,\\s*$");
        d.addBefore("n","\\s*,\\s*$");
        d.addBefore("k","\\s*,\\s*$");
        d.addIn("k","^\\s*,\\s*");
        fields.put(d.field,d);        
        d=new RemoverDefinition("601");
        d.addBefore("b","\\s*\\.\\s*$");
        d.addBefore("d","\\s*,\\s*$");
        d.addBefore("e","\\s*,\\s*$");
        fields.put(d.field,d);
        d=new RemoverDefinition("601");
        d.addBefore("b","\\s*\\.\\s*$");
        d.addBefore("d","\\s*,\\s*$");
        d.addBefore("e","\\s*,\\s*$");
        fields.put(d.field,d);
        d=new RemoverDefinition("700");
        d.addIn("a","\\s*,\\s*$");
        d.addIn("b","\\s*,\\s*$");
        d.addIn("b","^\\s*,\\s*");
        d.addIn("c","\\s*,\\s*$");
        d.addIn("d","\\s*,\\s*$");
        fields.put(d.field,d);
        d=new RemoverDefinition("701");
        d.addIn("a","\\s*,\\s*$");
        d.addIn("b","\\s*,\\s*$");
        d.addIn("c","\\s*,\\s*$");
        d.addIn("d","\\s*,\\s*$");
        fields.put(d.field,d);
        d=new RemoverDefinition("702");
        d.addIn("a","\\s*,\\s*$");
        d.addIn("b","\\s*,\\s*$");
        d.addIn("c","\\s*,\\s*$");
        d.addIn("d","\\s*,\\s*$");
        fields.put(d.field,d);
        d=new RemoverDefinition("710");
        d.addBefore("b","\\s*,\\s*$");
        d.addBefore("c","\\s*,\\s*$");
        d.addBefore("d","\\s*,\\s*$");
        d.addBefore("e","\\s*,\\s*$");
        d.addBefore("f","\\s*,\\s*$");
        d.addIn("b","\\s*,\\s*$");
        d.addIn("c","\\s*,\\s*$");
        d.addIn("d","\\s*,\\s*$");
        d.addIn("e","\\s*,\\s*$");
        d.addIn("a","\\s*\\.\\s*$");
        d.addIn("b","\\s*\\.\\s*$");
        fields.put(d.field,d);
        d=new RemoverDefinition("711");
        d.addBefore("b","\\s*,\\s*$");
        d.addBefore("c","\\s*,\\s*$");
        d.addBefore("d","\\s*,\\s*$");
        d.addBefore("e","\\s*,\\s*$");
        d.addBefore("f","\\s*,\\s*$");
        d.addIn("b","\\s*,\\s*$");
        d.addIn("c","\\s*,\\s*$");
        d.addIn("d","\\s*,\\s*$");
        d.addIn("e","\\s*,\\s*$");
        d.addIn("a","\\s*\\.\\s*$");
        d.addIn("b","\\s*\\.\\s*$");
        fields.put(d.field,d);
        d=new RemoverDefinition("712");
        d.addBefore("b","\\s*,\\s*$");
        d.addBefore("c","\\s*,\\s*$");
        d.addBefore("d","\\s*,\\s*$");
        d.addBefore("e","\\s*,\\s*$");
        d.addBefore("f","\\s*,\\s*$");
        d.addIn("b","\\s*,\\s*$");
        d.addIn("c","\\s*,\\s*$");
        d.addIn("d","\\s*,\\s*$");
        d.addIn("e","\\s*,\\s*$");
        d.addIn("a","\\s*\\.\\s*$");
        d.addIn("b","\\s*\\.\\s*$");
        fields.put(d.field,d);        
    }
    
    
    /**
     * @param rec
     */
    public static void removePontuation(MarcRecord rec){
        for (Object o : rec.getFields()) {
            MarcField fld = (MarcField) o;
            RemoverDefinition rd = (RemoverDefinition) fields.get(fld.getTagAsString());
            if (rd != null) {
                removePontuation(fld, rd);
            }
        }        
    }    

    /**
     * @param rec
     */
    public static void removePontuationAndBicos(MarcRecord rec){
        for (Object o : rec.getFields()) {
            MarcField fld = (MarcField) o;
            RemoverDefinition rd = (RemoverDefinition) fields.get(fld.getTagAsString());
            if (rd != null) {
                removePontuation(fld, rd);
            }

            if (fld.getTag() == 200) {
                MarcSubfield sfa = fld.getSingleSubfield('a');
                if (sfa != null) {
                    Matcher m = bicosPattern.matcher(sfa.getValue());
                    if (m.find()) {
                        sfa.setValue(m.replaceFirst(m.group(1)));
                    }
                }
                sfa = fld.getSingleSubfield('e');
                if (sfa != null) {
                    Matcher m = bicosPattern.matcher(sfa.getValue());
                    if (m.find()) {
                        sfa.setValue(m.replaceFirst(m.group(1)));
                    }
                }
            }
        }        
    }
    
    /**
     * @param doc
     * @return Document
     */
    public static Document removePontuation(Document doc){
        MarcRecord rec=new RecordBuilderFromMarcXml().parseDom(doc);
        removePontuation(rec);
        return DomBuilder.record2Dom(rec);
    }       
    
    /**
     * @param fld
     * @param rd
     */
    protected static void removePontuation(MarcField fld, RemoverDefinition rd){
        MarcSubfield before=null;
        MarcSubfield now=null;
        for (Object o : fld.getSubfields()) {
            before = now;
            now = (MarcSubfield) o;
            List<String> regExps = rd.in.get(String.valueOf(now.getCode()));
            if (regExps != null) {
                for (String regExp : regExps)
                    removePontuation(now, regExp);
            }
            regExps = rd.before.get(String.valueOf(now.getCode()));
            if (before != null && regExps != null) {
                for (String regExp : regExps)
                    removePontuation(before, regExp);
            }
        }        
        List<String> regExps=rd.in.get("last");
        if (regExps!=null && now!=null){
        	for(String regExp: regExps)
        		removePontuation(now,regExp);
        }
    }

    
    /**
     * @param sf
     * @param regExp
     */
    protected static void removePontuation(MarcSubfield sf, String regExp){
        String value=sf.getValue();
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(value);
        String newValue= m.replaceAll("");
        sf.setValue(newValue);
    }   
    
    
    /**
     */
    protected static class RemoverDefinition{
        public String field;
        public MapOfLists<String, String> before;
        public MapOfLists<String, String> in;
        
        /**
         * Creates a new instance of this class.
         * @param field
         */
        public RemoverDefinition(String field){
            this.field=field;
            before=new MapOfLists<String, String>();
            in=new MapOfLists<String, String>();
        }
        
        /**
         * @param subfield
         * @param regExp
         */
        public void addIn(String subfield, String regExp){
            in.put(subfield,regExp);
        }
        /**
         * @param subfield
         * @param regExp
         */
        public void addBefore(String subfield, String regExp){
            before.put(subfield,regExp);
        }
    }
    
    /**
     * @param rec
     */
    public static void removeAllPontuation(MarcRecord rec){
        for (Object o : rec.getFields()) {
            MarcField fld = (MarcField) o;
            removeAllPontuation(fld);
        }        
    }    
    
    /**
     * @param fld
     */
    public static void removeAllPontuation(MarcField fld){
        for(MarcSubfield sf: fld.getSubfields()) {
        	Matcher m=pontuationAtStartPattern.matcher(sf.getValue());
        	if(m.find())
        		sf.setValue(m.replaceFirst(""));
        	m=pontuationAtEndPattern.matcher(sf.getValue());
        	if(m.find())
        		sf.setValue(m.replaceFirst(""));
        	
        	m=pontuationSoleParentesisAtStartPattern.matcher(sf.getValue());
        	if(m.matches()) {
        		m=pontuationSoleParentesisAtStartCleanPattern.matcher(sf.getValue());
        		sf.setValue(m.replaceFirst(""));
        	}
        	m=pontuationSoleParentesisAtEndPattern.matcher(sf.getValue());
        	if(m.matches()) {
        		m=pontuationSoleParentesisAtEndCleanPattern.matcher(sf.getValue());
        		sf.setValue(m.replaceFirst(""));
        	}
        }
    }     
    
    /**
     * @param args
     */
    public static void main(String[] args){
        MarcRecord rec=new MarcRecord();
        MarcField f=rec.addField(225);
        f.addSubfield('f',"sdfsdfdsf)");
        System.err.println(rec);
        RemovePontuationVisitor.removePontuation(rec);
        System.err.println(rec);
        
    }
}
