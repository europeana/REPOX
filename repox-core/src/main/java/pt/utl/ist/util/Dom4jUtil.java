/*
 * DOMBuilder.java
 *
 * Created on 4 de Janeiro de 2002, 10:44
 */

package pt.utl.ist.util;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/** Utility class to manipulate Marc records in XML
 * @author Nuno Freire
 */
public class Dom4jUtil {
    private static Logger log = Logger.getLogger( Dom4jUtil.class);  
    

    /** Transforms a DOM into a String
    * @deprecated 
    */    
    public static String domToString(Document doc, boolean withXmlDeclaration){    	
        return withXmlDeclaration ?  doc.asXML() : doc.getRootElement().asXML();
    }
    
    /** Transforms a DOM into a String
     * @param doc a DOM Document
     * @param withXmlDeclaration include the xml declaration?
     * @return String representation of the DOM
     * @deprecated 
     */    
    public static String domToString(Element doc){    
    	return doc.asXML();
    }    


    /** Transforms a DOM into the bytes of a utf-8 string
     * @param doc a DOM Document
     * @param withXmlDeclaration include the xml declaration?
     * @return byte[] representation of the DOM
     */    
    public static byte[] domToBytes(Document doc) throws IOException{
    	ByteArrayOutputStream out=new ByteArrayOutputStream();
    	XMLWriter writer = new XMLWriter(out, OutputFormat.createCompactFormat());
        writer.write( doc );
        writer.close();
        byte[] ret=out.toByteArray();
        out.close();
        return ret;
    }    

    /** Transforms a DOM into the bytes of a utf-8 string
     * @param doc a DOM Document
     * @param withXmlDeclaration include the xml declaration?
     * @return byte[] representation of the DOM
     */    
    public static byte[] domToBytes(Element el) throws IOException{
    	ByteArrayOutputStream out=new ByteArrayOutputStream();
    	XMLWriter writer = new XMLWriter(out, OutputFormat.createCompactFormat());
        writer.write( el );
        writer.close();
        byte[] ret=out.toByteArray();
        out.close();
        return ret;
    }  

    
    public static void domToFile(Element el, File file) throws IOException{
    	FileOutputStream out=new FileOutputStream(file);
    	XMLWriter writer = new XMLWriter(out, OutputFormat.createCompactFormat());
    	writer.write( el );
    	writer.close();
    	out.close();
    }  
   
    public static void domToFile(Document doc, File file) throws IOException{
    	FileOutputStream out=new FileOutputStream(file);
    	XMLWriter writer = new XMLWriter(out, OutputFormat.createCompactFormat());
    	writer.write( doc );
    	writer.close();
    	out.close();
    }  
    
    
//    public static void saveDomToFile(Document doc, File file) throws IOException{
//    	XMLWriter writer = new XMLWriter(new FileWriter( file) , OutputFormat.createCompactFormat() );
//        writer.write( doc );
//        writer.close();
//    }    

    
    

    /** Transforms a DOM via a stylesheet, and returns the result in a String
     * @param doc a DOM Document
     * @param stylesheet complete path to the stylesheet
     * @return transformed DOM as a String
     */    
    public static String transformDom(Document doc, String stylesheet, boolean withXmlDeclaration){        
        return transformDom(doc, stylesheet, withXmlDeclaration,null);
    }
    
    /** Transforms a DOM via a stylesheet, and returns the result in a String
     * @param doc a DOM Document
     * @param stylesheet complete path to the stylesheet
     * @return transformed DOM as a String
     */    
    public static String transformDom(Document doc, String stylesheet){        
        return transformDom(doc, stylesheet, true,null);
    }
    /** Transforms a DOM via a stylesheet, and returns the result in a String
     * @param doc a DOM Document
     * @param stylesheet complete path to the stylesheet
     * @param withXmlDeclaration include the XML declaration?
     * @param parameters parameters to pass to the stylesheet
     * @return transformed DOM as a String
     */    
    public static String transformDom(Document doc, String stylesheet, boolean withXmlDeclaration, Map parameters){
        try {
            Transformer transformer = getTransformer(stylesheet);
            transformer.clearParameters();
            if (parameters!=null){
                for (Object o : parameters.keySet()) {
                    String k = (String) o;
                    transformer.setParameter(k, parameters.get(k));
                }
            }            
            if (! withXmlDeclaration)
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
            
            
	        // now lets style the given document
	        DocumentSource source = new DocumentSource( doc );
	        
            ByteArrayOutputStream output=new ByteArrayOutputStream();
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
            return output.toString();
        } catch (TransformerConfigurationException tce) {
            Throwable x = tce;
            if (tce.getException() != null)
                x = tce.getException();
            throw new RuntimeException(x);
        } catch (TransformerException te) {
            Throwable x = te;
            if (te.getException() != null)
                x = te.getException();
            throw new RuntimeException(x);
        }
    }    

    
    public static byte[] transformDomToBytes(Document doc, String stylesheet, boolean withXmlDeclaration, Map parameters){        
        try {
            Transformer transformer = getTransformer(stylesheet);
            transformer.clearParameters();
            if (parameters!=null){
                for (Object o : parameters.keySet()) {
                    String k = (String) o;
                    transformer.setParameter(k, parameters.get(k));
                }
            }            
            if (! withXmlDeclaration)
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
            
            
	        // now lets style the given document
	        DocumentSource source = new DocumentSource( doc );
	        
            ByteArrayOutputStream output=new ByteArrayOutputStream();
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
            return output.toByteArray();
        } catch (TransformerConfigurationException tce) {
            Throwable x = tce;
            if (tce.getException() != null)
                x = tce.getException();
            throw new RuntimeException(x);
        } catch (TransformerException te) {
            Throwable x = te;
            if (te.getException() != null)
                x = te.getException();
            throw new RuntimeException(x);
        }
    }    
    
    
    /** Transforms a DOM via a stylesheet, and returns the result in another DOM
     * Document
     * @param doc a DOM Document
     * @param stylesheet complete path to the stylesheet
     * @return transformed DOM
     */    
    public static Document transformDomIntoDom(Document doc, String stylesheet){        
        return transformDomIntoDom(doc, stylesheet, null);
    }    
    /** Transforms a DOM via a stylesheet, and returns the result in another DOM
     * Document
     * @param doc a DOM Document
     * @param stylesheet complete path to the stylesheet
     * @return transformed DOM
     */    
    public static Document transformDomIntoDom(Document doc, String stylesheet, Map parameters){        
        try {
            Transformer transformer = getTransformer(stylesheet);
            transformer.clearParameters();
            if (parameters!=null){
                for (Object o : parameters.keySet()) {
                    String k = (String) o;
                    transformer.setParameter(k, parameters.get(k));
                }
            }            
	        // now lets style the given document
	        DocumentSource source = new DocumentSource( doc );
	        
            ByteArrayOutputStream output=new ByteArrayOutputStream();
            DocumentResult result = new DocumentResult();
            transformer.transform(source, result);
            Document transformedDoc = result.getDocument();
            return transformedDoc;
        } catch (TransformerConfigurationException tce) {
            Throwable x = tce;
            if (tce.getException() != null)
                x = tce.getException();
            throw new RuntimeException(x);
        } catch (TransformerException te) {
            Throwable x = te;
            if (te.getException() != null)
                x = te.getException();
            throw new RuntimeException(x);
        }
    }    

    /** Creates a DOM from a string representation of an xml record
     * @param doc the xml string
     * @return the DOM document
     * @deprecated
     */    
    public static Document parseDomFromString(String doc) throws org.xml.sax.SAXException, DocumentException{        
        return DocumentHelper.parseText(doc);
    }
     
/** Creates a DOM from a file representation of an xml record
     * @param doc the xml file
     * @return the DOM document
     */    
    public static Document parseDomFromFile(File doc) throws org.xml.sax.SAXException, DocumentException{     
    	SAXReader reader = new SAXReader();
        Document document = reader.read(doc);
        return document;
    }  
    
    /** Creates a DOM from a file representation of an xml record
     * @param doc the xml file
     * @return the DOM document
     */    
    public static Document parseDomFromFile(File doc, String encoding) throws IOException, org.xml.sax.SAXException, DocumentException{
    	FileInputStream fis=new FileInputStream(doc);
    	InputStreamReader fileReader=new InputStreamReader(fis,encoding);
    	SAXReader reader = new SAXReader();
        Document document = reader.read(fileReader);
        fileReader.close();
        fis.close();
        return document;
    	
    	
    }  
/**************************************************************************
 ************                  Private Methods           ******************
 *************************************************************************/
//    private static TransformerFactory transFact = TransformerFactory.newInstance();
    private static TransformerFactory transFact = new org.apache.xalan.processor.TransformerFactoryImpl();
	
    private static Map<String,Templates> templates=new HashMap<String,Templates>();
    
    private static Transformer getTransformer(String xsltFile) throws TransformerConfigurationException{
        Templates tpl=(Templates) templates.get(xsltFile);
        if (tpl==null){
            Source xsltSource = new StreamSource(xsltFile);        
            tpl = transFact.newTemplates(xsltSource);
            if(tpl==null) {
            	throw new RuntimeException("Stylesheet does not exist: "+xsltFile);
            }
            templates.put(xsltFile,tpl);
        }
        return tpl.newTransformer();
    }
    
    
    
}
