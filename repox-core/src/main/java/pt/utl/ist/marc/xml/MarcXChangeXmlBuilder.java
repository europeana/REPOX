/*
 * DOMBuilder.java
 *
 * Created on 4 de Janeiro de 2002, 10:44
 */

package pt.utl.ist.marc.xml;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pt.utl.ist.marc.Field;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.RecordType;
import pt.utl.ist.marc.Subfield;
import pt.utl.ist.util.DomUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/** Utility class to code Marc records in XML
 * @author Nuno Freire
 */
public class MarcXChangeXmlBuilder {
    private static Logger log = Logger.getLogger(MarcXChangeXmlBuilder.class);
    
    boolean attributesWithExplicitNS=false;
    
    /** Creates a new instance of DOMBuilder */
    public MarcXChangeXmlBuilder( boolean attributesWithExplicitNS) {
    	this.attributesWithExplicitNS = attributesWithExplicitNS;
    }

    
    public Document record2Dom(List recs){ 
    	return record2Dom(recs, null);
    }
    
    public Document record2Dom(List recs, String marcType){ 
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument(); // Create from whole cloth
            Element collection =  document.createElementNS("info:lc/xmlns/marcxchange-v1","collection");
            collection.setAttribute("xmlns","info:lc/xmlns/marcxchange-v1");
            collection.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
//            collection.setAttribute("xsi:schemaLocation","info:lc/xmlns/marcxchange-v1 info:lc/xmlns/marcxchange-v1.xsd");
            document.appendChild(collection);
            for (Object rec1 : recs) {
                Record rec = (Record) rec1;
                if (rec != null)
                    collection.appendChild(createRecordDom(document, rec, marcType));
            }
            return document;
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException pce) {
            throw new RuntimeException(pce);
        }
    }
    
    
    
    
    /** Creates an XML representation of a marc record
     * @param rec a marc record
     * @return the record in xml
     */    
    public String record2XMLString(Record rec){ 
        return record2XMLString(rec, true);        
    }
    /** Creates an XML representation of a marc record
     * @param rec a marc record
     * @param withXmlDeclaration include the xml declaration
     * @return the record in xml
     */    
    public String record2XMLString(Record rec, boolean withXmlDeclaration){ 
        return record2XMLString(rec, withXmlDeclaration, null);
    }   
    public String record2XMLString(Record rec, boolean withXmlDeclaration, String marcType){ 
    	Document doc=record2Dom(rec,false,marcType);
    	return DomUtil.domToString(doc,withXmlDeclaration);
    }   

    /** Creates an XML representation of a marc record
     * @param recs a list of marc records
     * @return the records in xml
     */  
    public String record2XMLString(List recs){ 
        return record2XMLString(recs,true);        
    }   
    

    /** Creates an XML representation of a marc record
     * @param recs a list of marc records
     * @param withXmlDeclaration include the xml declaration
     * @return the records in xml
     */  
    public String record2XMLString(List recs, boolean withXmlDeclaration){ 
        Document doc=record2Dom(recs);
        return DomUtil.domToString(doc,withXmlDeclaration);
    }
    
    
    
    
    
    /** Creates an XML representation of a marc record
     * @param rec a marc record
     * @return the record in xml
     */    
    public byte[] record2XMLBytes(Record rec){ 
        return record2XMLBytes(rec, true);        
    }
    /** Creates an XML representation of a marc record
     * @param rec a marc record
     * @param withXmlDeclaration include the xml declaration
     * @return the record in xml
     */    
    public byte[] record2XMLBytes(Record rec, boolean withXmlDeclaration){ 
        return record2XMLBytes( rec, withXmlDeclaration, null);
    }   
    public byte[] record2XMLBytes(Record rec, boolean withXmlDeclaration, String marcType){ 
    	Document doc=record2Dom(rec, true, marcType);
    	return DomUtil.domToBytes(doc,withXmlDeclaration);
    }   

    /** Creates an XML representation of a marc record
     * @param recs a list of marc records
     * @return the record in xml
     */    
    public byte[] record2XMLBytes(List recs){ 
        return record2XMLBytes(recs,true);        
    }   

    /** Creates an XML representation of several marc records
     * @param recs marc records
     * @param withXmlDeclaration include the xml declaration
     * @return the record in xml
     */    
    public byte[] record2XMLBytes(List recs, boolean withXmlDeclaration){ 
        Document doc=record2Dom(recs);
        return DomUtil.domToBytes(doc,withXmlDeclaration);
    }    
    
    

     
    
    
        
    /** Creates an XML representation of a marc record
     * @param rec a marc record
     *
     * @return Dom Document representing the record
     *
     */    
    public Document record2Dom(Record rec){ 
        return record2Dom(rec,true,null);        
    } 
    
    /** Creates an XML representation of a marc record
     * @param rec a marc record
     *
     * @return Dom Document representing the record
     *
     */    
    public Document record2Dom(Record rec, boolean withCollectionElement, String marcType){ 
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument(); // Create from whole cloth
            Element root=null;
            if (withCollectionElement){
	            root= document.createElementNS("info:lc/xmlns/marcxchange-v1","collection");
	            root.appendChild(createRecordDom(document,rec, marcType));
            }else {
                root =createRecordDom(document,rec, marcType);
            }
            root.setAttribute("xmlns","info:lc/xmlns/marcxchange-v1");
            root.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
            root.setAttribute("xsi:schemaLocation","info:lc/xmlns/marcxchange-v1 info:lc/xmlns/marcxchange-v1.xsd");
            document.appendChild(root);
            return document;
        } catch (UnsupportedEncodingException e) { 
            throw new RuntimeException(e);
        } catch (ParserConfigurationException pce) {
            throw new RuntimeException(pce);
        }        
    } 
    
    /** Creates an XML representation of a marc record
     * @param rec a marc record
     *
     * @return Dom Document representing the record
     *
     */    
    public Element record2DomElement(Record rec, Document document, String marcType){ 
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            if (document==null)
            	document = builder.newDocument(); // Create from whole cloth
//            Element collection =  document.createElementNS("http://www.bn.pt/standards/metadata/marcxml/1.0/","collection");
            Element recElement=createRecordDom(document,rec, marcType);
//            recElement.setAttribute("xmlns","http://www.bn.pt/standards/metadata/marcxml/1.0/");
//            recElement.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
//            recElement.setAttribute("xsi:schemaLocation","http://www.bn.pt/standards/metadata/marcxml/1.0/ http://xml.bn.pt/schemas/Unimarc-1.0.xsd");
            return recElement;
        } catch (UnsupportedEncodingException e) { 
            throw new RuntimeException(e);
        } catch (ParserConfigurationException pce) {
            throw new RuntimeException(pce);
        }        
    }     
    
    private Element createRecordDom(Document document, Record rec, String marcType) throws java.io.UnsupportedEncodingException{
        Element root =  document.createElementNS("info:lc/xmlns/marcxchange-v1","record"); 
        
        //createAttribute(root, "format",(marcType==null ? "Unimarc" : marcType));
        if(rec.getRecordType()!=null) {
	        if(rec.getRecordType()==RecordType.BIBLIOGRAPHIC)
	        	createAttribute(root, "type","bibliographic");
	        else
	        	createAttribute(root, "type","authority");
        }
        List fields=rec.getFields();

        Element leadElem =  document.createElementNS("info:lc/xmlns/marcxchange-v1","leader");          
        if(rec.getLeader()==null)
        	leadElem.appendChild(document.createTextNode(Record.DEFAULT_LEADER));
        else        
        	leadElem.appendChild(document.createTextNode(rec.getLeader()));
        root.appendChild(leadElem);

        // append control fields to directory and data
        boolean inDataFields=false;
        for (Object field : fields) {
            Field f = (Field) field;
            if (f.isControlField()) {
                if (inDataFields) {
                    log.warn("Datafields and controlfields not sorted: " + rec);
                    Record sortedRec = new Record();
                    sortedRec.setLeader(rec.getLeader());
                    sortedRec.setNc(rec.getNc());
                    sortedRec.setRecordType(rec.getRecordType());
                    for (Field fld : rec.getFields()) {
                        sortedRec.addField(fld);
                    }
                    return createRecordDom(document, sortedRec, marcType);
                }
                Element el = (Element) document.createElementNS("info:lc/xmlns/marcxchange-v1", "controlfield");
                createAttribute(el, "tag", f.getTagAsString());
                byte[] bytes = f.getValue().getBytes();
                String data = new String(bytes, System.getProperty("file.encoding"));
                el.appendChild(document.createTextNode(data));
                root.appendChild(el);
            } else {
                inDataFields = true;
                Element el = (Element) document.createElementNS("info:lc/xmlns/marcxchange-v1", "datafield");
                createAttribute(el, "tag", f.getTagAsString());
                createAttribute(el, "ind1", String.valueOf(f.getInd1()));
                createAttribute(el, "ind2", String.valueOf(f.getInd2()));
                for (Object o : f.getSubfields()) {
                    Subfield sf = (Subfield) o;
                    Element elsf = (Element) document.createElementNS("info:lc/xmlns/marcxchange-v1", "subfield");
                    createAttribute(elsf, "code", String.valueOf(sf.getCode()));
                    elsf.appendChild(document.createTextNode(String.valueOf(sf.getValue())));
                    el.appendChild(elsf);
                }
                root.appendChild(el);
            }
        }
        return root;
    }
    
    
    
    private void createAttribute(Element el, String name, String value) {
    	if(attributesWithExplicitNS)
    		el.setAttributeNS("info:lc/xmlns/marcxchange-v1", name, value);
    	else
    		el.setAttribute(name, value);
    }
}
