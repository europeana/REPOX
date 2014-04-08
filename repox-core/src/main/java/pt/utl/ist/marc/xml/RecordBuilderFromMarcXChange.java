/*
 * RecordBuilder.java
 *
 * Created on 8 de Janeiro de 2003, 1:02
 */

package pt.utl.ist.marc.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.RecordType;

import java.util.List;

/** Utility class that build a Marc Record Class from a xml DOM.
 * Use Record.fromDom(Document doc) instead of this class. 
 *	
 * @author  Nuno Freire
 */
public class RecordBuilderFromMarcXChange  extends RecordBuilderFromMarcXml{
    protected void parseRecord(Node n){
        rec=new Record();
        if (n.getAttributes().getNamedItem("type")!=null) {
        	rec.setRecordType(RecordType.valueOf(n.getAttributes().getNamedItem("type").getNodeValue().toUpperCase()));
        }
        
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
    
//    public static Record domToRecord(Document dom){
//        RecordBuilder bld=new RecordBuilder();
//        return bld.parseDom(dom);
//    }

    /**
     * @param dom
     * @return
     */
    public static Record domToRecord(Node dom){
        RecordBuilderFromMarcXChange bld=new RecordBuilderFromMarcXChange();
        return bld.parseDom(dom);
    }

    public static List<Record> domToRecords(Document dom){
        RecordBuilderFromMarcXChange bld=new RecordBuilderFromMarcXChange();
        return bld.parseDomGetRecords(dom);
    }
}
