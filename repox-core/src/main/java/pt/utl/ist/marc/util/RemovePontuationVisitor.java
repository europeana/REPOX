/*
 * MarkRemover.java
 *
 * Created on 14 de Janeiro de 2003, 11:40
 */

package pt.utl.ist.marc.util;

import org.w3c.dom.Document;
import pt.utl.ist.marc.Field;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.Subfield;
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
    
    
    public static void removePontuation(Record rec){
        for (Object o : rec.getFields()) {
            Field fld = (Field) o;
            RemoverDefinition rd = (RemoverDefinition) fields.get(fld.getTagAsString());
            if (rd != null) {
                removePontuation(fld, rd);
            }
        }        
    }    

    public static void removePontuationAndBicos(Record rec){
        for (Object o : rec.getFields()) {
            Field fld = (Field) o;
            RemoverDefinition rd = (RemoverDefinition) fields.get(fld.getTagAsString());
            if (rd != null) {
                removePontuation(fld, rd);
            }

            if (fld.getTag() == 200) {
                Subfield sfa = fld.getSingleSubfield('a');
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
    
    public static Document removePontuation(Document doc){
        Record rec=new RecordBuilderFromMarcXml().parseDom(doc);
        removePontuation(rec);
        return DomBuilder.record2Dom(rec);
    }       
    
    protected static void removePontuation(Field fld, RemoverDefinition rd){
        Subfield before=null;
        Subfield now=null;
        for (Object o : fld.getSubfields()) {
            before = now;
            now = (Subfield) o;
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

    
    protected static void removePontuation(Subfield sf, String regExp){
        String value=sf.getValue();
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(value);
        String newValue= m.replaceAll("");
        sf.setValue(newValue);
    }   
    
    
    protected static class RemoverDefinition{
        public String field;
        public MapOfLists<String, String> before;
        public MapOfLists<String, String> in;
        
        public RemoverDefinition(String field){
            this.field=field;
            before=new MapOfLists<String, String>();
            in=new MapOfLists<String, String>();
        }
        
        public void addIn(String subfield, String regExp){
            in.put(subfield,regExp);
        }
        public void addBefore(String subfield, String regExp){
            before.put(subfield,regExp);
        }
    }
    
    
    
    
    public static void removeAllPontuation(Record rec){
        for (Object o : rec.getFields()) {
            Field fld = (Field) o;
            removeAllPontuation(fld);
        }        
    }    
    
    public static void removeAllPontuation(Field fld){
        for(Subfield sf: fld.getSubfields()) {
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
    
    
    
    
    
    
    
    
    public static void main(String[] args){
        Record rec=new Record();
        Field f=rec.addField(225);
        f.addSubfield('f',"sdfsdfdsf)");
        System.err.println(rec);
        RemovePontuationVisitor.removePontuation(rec);
        System.err.println(rec);
        
    }
}
