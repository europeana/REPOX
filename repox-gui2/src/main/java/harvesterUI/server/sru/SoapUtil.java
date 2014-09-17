/* SoapUtil.java - created on 19 de Abr de 2013, Copyright (c) 2011 The European Library, all rights reserved */
package harvesterUI.server.sru;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;

/**
 * 
 * 
 * @author Nuno Freire (nfreire@gmail.com)
 * @since 19 de Abr de 2013
 */
public class SoapUtil {
//    public static Document toDocument(SOAPMessage soapMsg) throws
//                     ParserConfigurationException, SAXException, SOAPException, IOException {
//         ByteArrayOutputStream baos = new ByteArrayOutputStream();
//         soapMsg.writeTo(baos);
//         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
// 
//         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//         dbf.setNamespaceAware(true);
//         DocumentBuilder db = dbf.newDocumentBuilder();
//         Document doc = db.parse(bais);
//         return doc;
//     }
    
    
    public static org.dom4j.Document toDom4jDocument(SOAPMessage soapMsg) throws
     SOAPException, IOException, DocumentException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      soapMsg.writeTo(baos);
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

      SAXReader reader = new SAXReader();
      org.dom4j.Document document = reader.read(bais);
      return document;
    }
    
    public static Document toDocument(SOAPMessage soapMsg) throws
                     TransformerConfigurationException,
     TransformerException, SOAPException, IOException {
             Source src = soapMsg.getSOAPPart().getContent();
             TransformerFactory tf = TransformerFactory.newInstance();
             Transformer transformer = tf.newTransformer();
             DOMResult result = new DOMResult();
             transformer.transform(src, result);
             return (Document)result.getNode();
         }
     
//         public static SOAPMessage toSOAPMessage(Document doc) throws
//                 TransformerConfigurationException, TransformerException,  SOAPException, IOException {
//             TransformerFactory tf = TransformerFactory.newInstance();
//             Transformer transformer = tf.newTransformer();
//             ByteArrayOutputStream baos = new ByteArrayOutputStream();
//             transformer.transform(new DOMSource(doc), new StreamResult(baos));
//             ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
//     
//             MessageFactory mf = MessageFactory.newInstance();
//             SOAPMessage soapMsg = mf.createMessage(new MimeHeaders(), bais);
//             return soapMsg;
//         }
     
         public static SOAPMessage toSOAPMessage(Document doc) throws
                 TransformerConfigurationException,
     TransformerException,  SOAPException, IOException {
             DOMSource src = new DOMSource(doc);
     
             MessageFactory mf = MessageFactory.newInstance();
             SOAPMessage soapMsg = mf.createMessage();
             soapMsg.getSOAPPart().setContent(src);
             return soapMsg;
         }
         
         public static SOAPElement getFirstChild(SOAPElement body, QName elementName) {
             Iterator updateRequest = body.getChildElements(elementName);
             if(!updateRequest.hasNext()) 
                 return null;
             return (SOAPElement)updateRequest.next();
         }
     
         public static void main(String[] args) throws Exception {
             
         }

        /**
         * @param recEl
         * @return
         */
        public static SOAPElement getFirstElement(SOAPElement body) {
            Iterator updateRequest = body.getChildElements();
            SOAPElement el=null;
            while (updateRequest.hasNext()) {
                Object next = updateRequest.next();
                if(next instanceof SOAPElement) {
                    el=(SOAPElement)next;
                    break;
                }
            }
            return el;
        }
     
}
