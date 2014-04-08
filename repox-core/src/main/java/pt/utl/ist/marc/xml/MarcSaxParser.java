/*
 * UnimarcSaxParser.java
 *
 * Created on 28 de Abril de 2004, 17:32
 */

package pt.utl.ist.marc.xml;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import pt.utl.ist.marc.Field;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.Subfield;
import pt.utl.ist.util.NUtil;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;


/** Parses marc records contained in an xml ImputStream 
 * 
 * @author  Nuno Freire
 */
public class MarcSaxParser extends DefaultHandler{
	ArrayList<Record> records;
    protected Record currentRecord;
    protected MarcSaxParserClient client;

    String currentCharacters="";
    
    protected Field currentField=null;
    protected Subfield currentSubfield=null;

    /** Creates a new instance of UnimarcSaxParser */
    public MarcSaxParser() {
    }
    
    public MarcSaxParser(MarcSaxParserClient client) {
    	this.client = client;
    }
    
    /**
     * @return the marc records parsed from the input stream
     */
    public ArrayList<Record> getRecords(){
        return records;
    }
    
    
    public void startDocument() throws SAXException {
    	if (client==null)
    		records=new ArrayList<Record>();
    }
    
    public void endDocument() throws SAXException {
    	if (client!=null)
    		client.signalParseFinished();
    }
  
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException  {       
        try{
            if (qName.equals("record") || qName.endsWith(":record") ){
                currentRecord=new Record();
            }else if (qName.equals("leader") || qName.endsWith(":leader")){
                currentCharacters="";
            }else if (qName.equals("controlfield") || qName.endsWith(":controlfield")){
                currentCharacters="";
                currentField=currentRecord.addField(Short.parseShort(attributes.getValue(0)));
            }else if (qName.equals("datafield") || qName.endsWith(":datafield")){
            	for(int i=0; i<attributes.getLength() ; i++) {
            		if(attributes.getQName(i).equals("tag") || attributes.getQName(i).endsWith(":tag") ) {
                        currentField=currentRecord.addField(Short.parseShort(attributes.getValue(i)));
                        break;
            		}
            	}
            	for(int i=0; i<attributes.getLength() ; i++) {
            		if(attributes.getQName(i).equals("ind1") || attributes.getQName(i).endsWith(":ind1") ) {
            			String val=attributes.getValue(i);
            			if (val.length()>0)
            				currentField.setInd1(val.charAt(0));
            			else
            				currentField.setInd1(' ');
            		}else if(attributes.getQName(i).equals("ind2") || attributes.getQName(i).endsWith(":ind2") ) {
            			String val=attributes.getValue(i);
            			if (val.length()>0)
            				currentField.setInd2(val.charAt(0));
            			else
            				currentField.setInd2(' ');
            		}
            	}
            }else if (qName.equals("subfield") || qName.endsWith(":subfield")){
                currentCharacters="";
                currentSubfield=currentField.addSubfield(attributes.getValue(0).charAt(0));
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new SAXException(e);
        }
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException  {
        try{            
            if (qName.equals("record") || qName.endsWith(":record") ){
            	if (client==null)
            		records.add(currentRecord);
            	else
            		client.nextRecord(currentRecord);
            }else if (qName.equals("leader") || qName.endsWith(":leader")){
                currentRecord.setLeader(currentCharacters);
                currentCharacters="";
            }else if (qName.equals("controlfield") || qName.endsWith(":controlfield")){
                currentField.setValue(currentCharacters);
                currentCharacters="";
            //else if (qName.equals("datafield")){
            }else if (qName.equals("subfield") || qName.endsWith(":subfield")){
                currentSubfield.setValue(currentCharacters);
                currentCharacters="";
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new SAXException(e);
        }
    }
    
    public void characters(char buf[], int offset, int len) throws SAXException {
        currentCharacters+=new String(buf,offset,len);
    }
    
   /**
	 * @param in The InputStream with the records in xml
	 * @return the parsed marc records
	 * @throws SAXException
	 */
	public static ArrayList<Record> parse(InputStream in) throws SAXException{
       try{
           MarcSaxParser handler = new MarcSaxParser();
           SAXParserFactory factory = SAXParserFactory.newInstance();
           SAXParser saxParser = factory.newSAXParser();
           XMLReader parser = saxParser.getXMLReader();
           parser.setContentHandler(handler);
           InputSource inSource=new InputSource(in);
           parser.parse(inSource);
                  
           return handler.getRecords();
       }catch(Exception e){
           e.printStackTrace();
           throw new SAXException(e);
       }
    }
   


   /**
	 * @param in The InputStream with the records in xml
	 * @param client The class that will process the parsed records
	 * @throws SAXException
	 */
	public static void parse(InputStream in, MarcSaxParserClient client) throws SAXException{
       try{
           MarcSaxParser handler = new MarcSaxParser(client);
           SAXParserFactory factory = SAXParserFactory.newInstance();
           SAXParser saxParser = factory.newSAXParser();
           XMLReader parser = saxParser.getXMLReader();
           parser.setContentHandler(handler);
           InputSource inSource=new InputSource(in);
           parser.parse(inSource);
       }catch(Exception e){
           e.printStackTrace();
           throw new SAXException(e);
       }
    }
	
   public static void main( final String [] args ) {
       try{
           
    	   ArrayList<Record> records=MarcSaxParser.parse(new FileInputStream("C:\\desktop\\example.xml"));
            System.out.println(NUtil.listToString(records, "", "", "\n"));
            
           
           
//            Record mets=UnimarcSaxParser.parse(new FileInputStream("C:\\progs\\tomcat5\\webapps\\testebndpub\\obra1\\record\\1.xml"));
//            System.err.println(mets);
//            
//            System.err.println();
//            System.err.println();
//            Document dom=pt.utl.ist.urn.client.UrnClient.getRecordAsXml("323613",pt.utl.ist.urn.client.UrnClient.NCB);
//            mets=RecordBuilder.domToRecord(dom);
//            System.err.println(mets);
//            
//            
//            pt.utl.ist.util.DomUtil.saveDomToFile(dom, new File("c:\\desktop\\teste.xml"));
//            
//            System.err.println();
//            System.err.println();
//            
//            Document d=pt.utl.ist.util.DomUtil.parseDomFromFile(new File("C:\\progs\\tomcat5\\webapps\\testebndpub\\obra1\\record\\1.xml"));
//            mets=RecordBuilder.domToRecord(d);
//            System.err.println(mets);
       }catch(Exception e){
           e.printStackTrace();
       }
   }
   
}
