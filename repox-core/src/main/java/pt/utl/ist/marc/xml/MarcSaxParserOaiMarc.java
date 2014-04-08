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

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;


/** Parses marc records contained in an xml ImputStream 
 * 
 * @author  Nuno Freire
 */
public class MarcSaxParserOaiMarc extends DefaultHandler{
	protected String tagNameRecord="oai_marc";
	protected String tagNameDataField="varfield";
	protected String tagNameControlField="fixfield";
	protected String tagNameSubfield="subfield";
//	protected String tagNameLeader="lab";

	protected String attributeNameTag="id";
	protected String attributeNameCode="label";
	protected String attributeNameI1="i1";
	protected String attributeNameI2="i2";
	
	
	
	
	ArrayList<Record> records;
    protected Record currentRecord;
    protected MarcSaxParserClient client;

    boolean inLeader=false;
    boolean inFmt=false;
    String currentCharacters="";
    
    protected Field currentField=null;
    protected Subfield currentSubfield=null;

    /** Creates a new instance of UnimarcSaxParser */
    public MarcSaxParserOaiMarc() {
    }
    
    public MarcSaxParserOaiMarc(MarcSaxParserClient client) {
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
            if (qName.equals(tagNameRecord) || qName.endsWith(":"+tagNameRecord) ){
                currentRecord=new Record();
//            }else if (qName.equals(tagNameLeader) || qName.endsWith(":"+tagNameLeader)){
//                currentCharacters="";
            }else if (qName.equals(tagNameControlField) || qName.endsWith(":"+tagNameControlField)){
                currentCharacters="";
                String tag=attributes.getValue(0);
                if(tag.equals("LDR"))
                	inLeader=true;
                else if(tag.equals("FMT") || tag.equals("CAT"))
                	inFmt=true;
                else
                	currentField=currentRecord.addField(Short.parseShort(tag));
            }else if (qName.equals(tagNameDataField) || qName.endsWith(":"+tagNameDataField)){
            	for(int i=0; i<attributes.getLength() ; i++) {
            		if(attributes.getQName(i).equals(attributeNameTag) || attributes.getQName(i).endsWith(":"+attributeNameTag) ) {
                        currentField=currentRecord.addField(Short.parseShort(attributes.getValue(i)));
                        break;
            		}
            	}
            	for(int i=0; i<attributes.getLength() ; i++) {
            		if(attributes.getQName(i).equals(attributeNameI1) || attributes.getQName(i).endsWith(":"+attributeNameI1) ) {
                        currentField.setInd1(attributes.getValue(i).charAt(0));
            		}else if(attributes.getQName(i).equals(attributeNameI2) || attributes.getQName(i).endsWith(":"+attributeNameI2) ) {
                        currentField.setInd2(attributes.getValue(i).charAt(0));
            		}
            	}
            }else if (qName.equals(tagNameSubfield) || qName.endsWith(":"+tagNameSubfield)){
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
            if (qName.equals(tagNameRecord) || qName.endsWith(":"+tagNameRecord) ){
            	if (client==null)
            		records.add(currentRecord);
            	else
            		client.nextRecord(currentRecord);
//            }else if (qName.equals(tagNameLeader) || qName.endsWith(":"+tagNameLeader)){
//                currentRecord.setLeader(currentCharacters);
//                currentCharacters="";
            }else if (qName.equals(tagNameControlField) || qName.endsWith(":"+tagNameControlField)){
                if(!inLeader && !inFmt)
                	currentField.setValue(currentCharacters);
                else if(inLeader){
                    currentRecord.setLeader(currentCharacters);
                    inLeader=false;
                }
                	
                currentCharacters="";
            //else if (qName.equals("datafield")){
            }else if (qName.equals(tagNameSubfield) || qName.endsWith(":"+tagNameSubfield)){
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
           MarcSaxParserOaiMarc handler = new MarcSaxParserOaiMarc();
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
           MarcSaxParserOaiMarc handler = new MarcSaxParserOaiMarc(client);
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
           
    	   MarcSaxParserOaiMarc.parse(new FileInputStream("C:\\desktop\\000000001-817185_xserver.xml"),
    		new MarcSaxParserClient() {
    		   @Override
    		protected void processRecord(Record rec)  {
    			System.out.println(rec);
    			
    		}
    	   }
    	   
    );
           
            
           
           
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
