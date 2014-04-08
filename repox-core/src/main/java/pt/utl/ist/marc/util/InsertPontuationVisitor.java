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

import java.util.HashMap;
/**
 *
 * @author  Nuno Freire
 */
public class InsertPontuationVisitor {
    protected static HashMap fields;
    static{
        fields=new HashMap(10);
        PontuationDefinition d=new PontuationDefinition("200");
        d.addBefore("d","=");
        fields.put(d.field,d);
        d=new PontuationDefinition("210");
        d.addBefore("g",") ");
        fields.put(d.field,d);
        d=new PontuationDefinition("215");
        d.addIn("a","; ");
        d.addIn("c","; ");
        d.addIn("d"," ; ");
        fields.put(d.field,d);
        d=new PontuationDefinition("225");
        d.addBefore("d","=");
        fields.put(d.field,d);
        d=new PontuationDefinition("500");
        d.addIn("h",", ");
        d.addIn("n",", ");
        d.addIn("k",", ");
        d.addBefore("k",", ");
        fields.put(d.field,d);
        d=new PontuationDefinition("700");
        d.addIn("a",", ");
        d.addIn("b",", ");
        d.addIn("c",", ");
        d.addIn("d",", ");
        fields.put(d.field,d);
        d=new PontuationDefinition("701");
        d.addIn("a",", ");
        d.addIn("b",", ");
        d.addIn("c",", ");
        d.addIn("d",", ");
        fields.put(d.field,d);
        d=new PontuationDefinition("702");
        d.addIn("a",", ");
        d.addIn("b",", ");
        d.addIn("c",", ");
        d.addIn("d",", ");
        fields.put(d.field,d);
        d=new PontuationDefinition("710");
        d.addIn("a",". ");
        d.addIn("b",", ");
        d.addIn("c",", ");
        d.addIn("d",", ");
        d.addIn("e",", ");
        fields.put(d.field,d);
        d=new PontuationDefinition("711");
        d.addIn("a",". ");
        d.addIn("b",", ");
        d.addIn("c",", ");
        d.addIn("d",", ");
        d.addIn("e",", ");
        fields.put(d.field,d);
        d=new PontuationDefinition("712");
        d.addIn("a",". ");
        d.addIn("b",", ");
        d.addIn("c",", ");
        d.addIn("d",", ");
        d.addIn("e",", ");
        fields.put(d.field,d);
    }


    public static void insertPontuation(Record rec){
        for (Object o : rec.getFields()) {
            Field fld = (Field) o;
            PontuationDefinition rd = (PontuationDefinition) fields.get(fld.getTagAsString());
            insertPontuation(fld, rd);
        }
    }

    public static Document insertPontuation(Document doc){
        Record rec=new RecordBuilderFromMarcXml().parseDom(doc);
        insertPontuation(rec);
        return DomBuilder.record2Dom(rec);
    }

    protected static void insertPontuation(Field fld, PontuationDefinition rd){
        Subfield before=null;
        Subfield now=null;
        for (Object o : fld.getSubfields()) {
            before = now;
            now = (Subfield) o;
            if (before != null) {
                String sep = "; ";
                if (rd != null) {
                    if (rd.in.get(String.valueOf(before.getCode())) != null) {
                        sep = (String) rd.in.get(String.valueOf(before.getCode()));
                    } else if (rd.in.get(String.valueOf(before.getCode())) != null) {
                        sep = (String) rd.before.get(String.valueOf(now.getCode()));
                    }
                }

                String sepaux = sep;
                if (sepaux.endsWith(" "))
                    sepaux = sepaux.substring(0, sepaux.length());
                if (sepaux.startsWith(" "))
                    sepaux = sepaux.substring(1);

                if (!(before.getValue().endsWith(sep) || before.getValue().endsWith(sepaux) || before.getValue().endsWith(",") || before.getValue().endsWith(";") || before.getValue().endsWith(":"))) {
                    before.setValue(before.getValue() + sep);
                }
            }
        }
    }


    protected static class PontuationDefinition{
        public String field;
        public HashMap before;
        public HashMap in;
        public HashMap allways; //não está a ser utilizado

        public PontuationDefinition(String field){
            this.field=field;
            before=new HashMap();
            in=new HashMap();
            allways=new HashMap();
        }

        public void addIn(String subfield, String separator){
            allways.put(subfield,separator);
        }
        public void addAllways(String subfield, String separator){
            in.put(subfield,separator);
        }
        public void addBefore(String subfield, String separator){
            before.put(subfield,separator);
        }
    }
}
