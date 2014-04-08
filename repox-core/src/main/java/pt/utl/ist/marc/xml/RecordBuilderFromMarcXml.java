/*
 * RecordBuilder.java
 *
 * Created on 8 de Janeiro de 2003, 1:02
 */

package pt.utl.ist.marc.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.helpers.DefaultHandler;
import pt.utl.ist.marc.Field;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.Subfield;

import java.util.ArrayList;
import java.util.List;

/** Utility class that build a Marc Record Class from a xml DOM.
 * Use Record.fromDom(Document doc) instead of this class. 
 *	
 * @author  Nuno Freire
 */
public class RecordBuilderFromMarcXml  extends DefaultHandler{
    protected Record rec=null;
    protected List<Record>  recs;
    
    public RecordBuilderFromMarcXml (){
    }
    
    public Record parseDom(Node dom){ 
        recs=new ArrayList<Record>();
        if (dom instanceof Document){
        	dom=dom.getFirstChild();
        }
        if (dom.getNodeName().equals("collection"))
            parseCollection(dom);
        else if (dom.getNodeName().equals("record"))
        	parseRecord(dom);
        return rec;
    }
    
    
    
    protected void parseCollection(Node n){
        int sz=n.getChildNodes().getLength();
        for (int idx=0 ; idx<sz ; idx++) {
            Node node = n.getChildNodes().item(idx);
            if (node.getNodeName().equals("record"))
                parseRecord(node);
        }
    }
    
    protected void parseRecord(Node n){
        rec=new Record();
        int sz=n.getChildNodes().getLength();
        for (int idx=0 ; idx<sz ; idx++) {
            Node node = n.getChildNodes().item(idx);
            if (node.getNodeName().equals("leader"))
                rec.setLeader(node.getFirstChild().getNodeValue());
            else if (node.getNodeName().equals("controlfield"))
                parseControlField(node);
            else if (node.getNodeName().equals("datafield"))
                parseDataField(node);
        }
        recs.add(rec);
    }

    protected void parseControlField(Node n){
        Field f=rec.addField(Integer.parseInt(n.getAttributes().getNamedItem("tag").getNodeValue()));
        if(n.getFirstChild()==null)
        	f.setValue("");
        else
        	f.setValue(n.getFirstChild().getNodeValue());
        if (f.getTag()==001) {
        	rec.setNc(f.getValue());
        }
    }

    protected void parseDataField(Node n){
        Field f=rec.addField(Integer.parseInt(n.getAttributes().getNamedItem("tag").getNodeValue()));

        if (n.getAttributes().getNamedItem("ind1")!=null && n.getAttributes().getNamedItem("ind1").getNodeValue().length()>0)
            f.setInd1(n.getAttributes().getNamedItem("ind1").getNodeValue().charAt(0));
        else
            f.setInd1(' ');
        if (n.getAttributes().getNamedItem("ind2")!=null && n.getAttributes().getNamedItem("ind2").getNodeValue().length()>0)
            f.setInd2(n.getAttributes().getNamedItem("ind2").getNodeValue().charAt(0));
        else
            f.setInd2(' ');

        int sz=n.getChildNodes().getLength();
        for (int idx=0 ; idx<sz ; idx++) {
            Node node = n.getChildNodes().item(idx);
            if (node.getNodeName().equals("subfield"))
                parseSubfield(node,f);
        }
    }

    protected void parseSubfield(Node n, Field f){
        Subfield sf=f.addSubfield(n.getAttributes().getNamedItem("code").getNodeValue().charAt(0));
        if (n.getFirstChild()!=null)
            sf.setValue(n.getFirstChild().getNodeValue());
        else
            sf.setValue("");
    }


    public List<Record> parseDomGetRecords(Document dom){
       recs=new ArrayList<Record> ();
       int sz=dom.getChildNodes().getLength();
       for (int idx=0 ; idx<sz ; idx++) {
           Node node = dom.getChildNodes().item(idx);
           if (node.getNodeName().equals("collection"))
               parseCollection(node);
       }
       return recs;
   }
    
    
//    public static Record domToRecord(Document dom){
//        RecordBuilder bld=new RecordBuilder();
//        return bld.parseDom(dom);
//    }

    /**
     * @param dom
     * @return
     */
    public static Record domToRecord(Node dom){
        RecordBuilderFromMarcXml bld=new RecordBuilderFromMarcXml();
        if(dom instanceof Document)
        	return bld.parseDom(dom.getFirstChild());
    	return bld.parseDom(dom);
    }

    public static List<Record> domToRecords(Document dom){
        RecordBuilderFromMarcXml bld=new RecordBuilderFromMarcXml();
        return bld.parseDomGetRecords(dom);
    }
}
