/*
 * DOMBuilder.java
 *
 * Created on 4 de Janeiro de 2002, 10:44
 */

package pt.utl.ist.util;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/** Utility class to manipulate Marc records in XML
 * @author Nuno Freire
 */
public class DomUtil {
    private static Logger log = Logger.getLogger( DomUtil.class);  
    
    /** Creates a new instance of DOMBuilder */
    public DomUtil() {
    }


    public static String domToString(Document doc, boolean withXmlDeclaration){
        return domToString(doc.getDocumentElement(),withXmlDeclaration);
    }
    
    
    
    /** Transforms a DOM into a String
     * @param doc a DOM Document
     * @param withXmlDeclaration include the xml declaration?
     * @return String representation of the DOM
     */    
    public static String domToString(Node doc, boolean withXmlDeclaration){        
        try {            
//            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = transFact.newTransformer();
            if (! withXmlDeclaration)
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
            DOMSource source = new DOMSource(doc);
            StringWriter output=new StringWriter();
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
            return output.toString();
        } catch (TransformerConfigurationException tce) {
            // Use the contained exception, if any
            Throwable x = tce;
            if (tce.getException() != null)
                x = tce.getException();
            x.printStackTrace();
            log.error(x);
        } catch (TransformerException te) {
            // Use the contained exception, if any
            Throwable x = te;
            if (te.getException() != null)
                x = te.getException();
            x.printStackTrace();
            log.error(x);            
        }
        return null;
    }    


    /** Transforms a DOM into the bytes of a utf-8 string
     * @param doc a DOM Document
     * @param withXmlDeclaration include the xml declaration?
     * @return byte[] representation of the DOM
     */    
    public static byte[] domToBytes(Node doc, boolean withXmlDeclaration){        
        try {            
//            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = transFact.newTransformer();
            if (! withXmlDeclaration)
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
            DOMSource source = new DOMSource(doc);
            ByteArrayOutputStream output=new ByteArrayOutputStream();
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
            return output.toByteArray();
        } catch (TransformerConfigurationException tce) {
            // Use the contained exception, if any
            Throwable x = tce;
            if (tce.getException() != null)
                x = tce.getException();
            x.printStackTrace();
            log.error(x);
        } catch (TransformerException te) {
            // Use the contained exception, if any
            Throwable x = te;
            if (te.getException() != null)
                x = te.getException();
            x.printStackTrace();
            log.error(x);            
        }
        return null;
    }    
    
    
    public static void saveDomToFile(Node doc, File file) throws IOException{
    	String b=domToString(doc, true);
    	FileUtil.writeToFile(file, b, "UTF-8");
    }    

    
    

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
//            TransformerFactory tFactory = TransformerFactory.newInstance();
//            StreamSource stylesource = new StreamSource(stylesheet);            
//            Transformer transformer = tFactory.newTransformer(stylesource);
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
            DOMSource source = new DOMSource(doc);
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
//            TransformerFactory tFactory = TransformerFactory.newInstance();
//            StreamSource stylesource = new StreamSource(stylesheet);            
//            Transformer transformer = tFactory.newTransformer(stylesource);
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
            DOMSource source = new DOMSource(doc);
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
//            TransformerFactory tFactory = TransformerFactory.newInstance();
//            StreamSource stylesource = new StreamSource(stylesheet);            
//            Transformer transformer = tFactory.newTransformer(stylesource);
            Transformer transformer = getTransformer(stylesheet);
            if (parameters!=null){
                for (Object o : parameters.keySet()) {
                    String k = (String) o;
                    transformer.setParameter(k, parameters.get(k));
                }
            }        
            DOMSource source = new DOMSource(doc);
            DOMResult result = new DOMResult();
            transformer.transform(source, result);
            return (Document) result.getNode();
        } catch (TransformerConfigurationException tce) {
            Throwable x = tce;
            if (tce.getException() != null)
                x = tce.getException();
            throw new RuntimeException(stylesheet,x);
        } catch (TransformerException te) {
            Throwable x = te;
            if (te.getException() != null)
                x = te.getException();
            throw new RuntimeException(stylesheet,x);
        }
    }    

    /** Creates a DOM from a string representation of an xml record
     * @param doc the xml string
     * @return the DOM document
     */    
    public static Document parseDomFromString(String doc) throws org.xml.sax.SAXException{        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder(); 
            return builder.parse(new org.xml.sax.InputSource(new StringReader(doc)));  
        }catch(javax.xml.parsers.ParserConfigurationException e){
            throw new RuntimeException(e);
        }catch(java.io.IOException e){
            throw new RuntimeException(e);
		}
    }
     
/** Creates a DOM from a file representation of an xml record
     * @param doc the xml file
     * @return the DOM document
     */    
    public static Document parseDomFromFile(File doc) throws org.xml.sax.SAXException{        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder(); 
            return builder.parse(new org.xml.sax.InputSource(new FileReader(doc)));  
        }catch(javax.xml.parsers.ParserConfigurationException e){
            throw new RuntimeException(e);
        }catch(java.io.IOException e){
            throw new RuntimeException(e);
		}
    }  
    
    /** Creates a DOM from a file representation of an xml record
     * @param doc the xml file
     * @return the DOM document
     */    
    public static Document parseDomFromFile(File doc, String encoding) throws org.xml.sax.SAXException{
    	return parseDomFromFile(doc, encoding, false);
    }
    
    /** Creates a DOM from a file representation of an xml record
     * @param doc the xml file
     * @return the DOM document
     */    
    public static Document parseDomFromFile(File doc, String encoding, boolean namespaceAware) throws org.xml.sax.SAXException{        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(namespaceAware);
        try {
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder(); 
            return builder.parse(new org.xml.sax.InputSource(new StringReader(FileUtil.readFileToString(doc, encoding))));  
        }catch(javax.xml.parsers.ParserConfigurationException e){
            throw new RuntimeException(e);
        }catch(java.io.IOException e){
            throw new RuntimeException(e);
		}
    }  
/**************************************************************************
 ************                  Private Methods           ******************
 *************************************************************************/
//    private static TransformerFactory transFact = TransformerFactory.newInstance();
    private static TransformerFactory transFact = TransformerFactory.newInstance();
//    	new org.apache.xalan.processor.TransformerFactoryImpl();
	
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
    
    
    /**
     * This will take the pre-defined entities in XML 1.0 and convert their
     * character representation to the appropriate entity reference, suitable
     * for XML attributes.
     * 
     * @param text
     *            text
     * 
     * @return text with entities escaped
     */
    public static String escapeElementEntities(String text) {
        StringBuffer buffer = new StringBuffer();
        char[] block = null;
        int i;
        int last = 0;
        int size = text.length();

        for (i = 0; i < size; i++) {
            String entity = null;
            char c = text.charAt(i);

            switch (c) {
                case '<':
                    entity = "&lt;";

                    break;

                case '>':
                    entity = "&gt;";

                    break;

                case '&':
                    entity = "&amp;";

                    break;

                case '\t':
                case '\n':
                case '\r':

//                    // don't encode standard whitespace characters
//                    if (preserve) {
//                        entity = String.valueOf(c);
//                    }

                    break;

                default:

                    if ((c < 32)) {
                        entity = "&#" + (int) c + ";";
                    }
//                    if ((c < 32) || shouldEncodeChar(c)) {
//                    	entity = "&#" + (int) c + ";";
//                    }

                    break;
            }

            if (entity != null) {
                if (block == null) {
                    block = text.toCharArray();
                }

                buffer.append(block, last, i - last);
                buffer.append(entity);
                last = i + 1;
            }
        }

        if (last == 0) {
            return text;
        }

        if (last < size) {
            if (block == null) {
                block = text.toCharArray();
            }

            buffer.append(block, last, i - last);
        }

        String answer = buffer.toString();
        buffer.setLength(0);

        return answer;
    }
    
    /**
     * This will take the pre-defined entities in XML 1.0 and convert their
     * character representation to the appropriate entity reference, suitable
     * for XML attributes.
     * 
     * @param text
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected String escapeAttributeEntities(String text) {
        StringBuffer buffer = new StringBuffer();
//        char quote = format.getAttributeQuoteCharacter();
        char quote = '"';

        char[] block = null;
        int i;
        int last = 0;
        int size = text.length();

        for (i = 0; i < size; i++) {
            String entity = null;
            char c = text.charAt(i);

            switch (c) {
                case '<':
                    entity = "&lt;";

                    break;

                case '>':
                    entity = "&gt;";

                    break;

                case '\'':

                    if (quote == '\'') {
                        entity = "&apos;";
                    }

                    break;

                case '\"':

                    if (quote == '\"') {
                        entity = "&quot;";
                    }

                    break;

                case '&':
                    entity = "&amp;";

                    break;

                case '\t':
                case '\n':
                case '\r':

                    // don't encode standard whitespace characters
                    break;

                default:

                    if ((c < 32) ) {
                        entity = "&#" + (int) c + ";";
                    }
//                    if ((c < 32) || shouldEncodeChar(c)) {
//                    	entity = "&#" + (int) c + ";";
//                    }

                    break;
            }

            if (entity != null) {
                if (block == null) {
                    block = text.toCharArray();
                }

                buffer.append(block, last, i - last);
                buffer.append(entity);
                last = i + 1;
            }
        }

        if (last == 0) {
            return text;
        }

        if (last < size) {
            if (block == null) {
                block = text.toCharArray();
            }

            buffer.append(block, last, i - last);
        }

        String answer = buffer.toString();
        buffer.setLength(0);

        return answer;
    }

}
