package pt.utl.ist.rest.services.web;

import org.apache.log4j.Logger;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Gilberto Pedrosa
 * Date: 09-07-2011
 * Time: 2:01
 * To change this template use File | Settings | File Templates.
 */
public class ValidateXml {

    private static final Logger log = Logger.getLogger(ValidateXml.class);

    public void validateXML(String inputFilePath, String schema){
        try {
            SAXParserFactory sf = SAXParserFactory.newInstance();
            sf.setNamespaceAware(true);
            sf.setValidating(true);
            SAXParser sp = sf.newSAXParser();
            sp.setProperty(
                    "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                    "http://www.w3.org/2001/XMLSchema");
            sp.setProperty(
                    "http://java.sun.com/xml/jaxp/properties/schemaSource",
                    schema);

            DefaultHandler handler = new XmlDefaultHandler();
            sp.parse(new InputSource(new FileReader(inputFilePath)), handler);
            log.debug("Success");
        }
        catch (FactoryConfigurationError e) { log.error(e.toString()); }
        catch (ParserConfigurationException e) { log.error(e.toString()); }
        catch (SAXException e) { log.error(e.toString()); }
        catch (IOException e) { log.error(e.toString()); }
    }

    public static class XmlDefaultHandler extends DefaultHandler{
        /** @see org.xml.sax.ErrorHandler#error(SAXParseException)
         */
        public void error(SAXParseException spe) throws SAXException{
            throw spe;
        }

        /** @see org.xml.sax.ErrorHandler#fatalError(SAXParseException)
         */
        public void fatalError(SAXParseException spe) throws SAXException{
            throw spe;
        }
    }

    public static void main(String[] args){
        ValidateXml validateXml = new ValidateXml();
        validateXml.validateXML(/*"C:\\testeValidate\\test.xml"*/"C:\\Users\\Gilberto Pedrosa\\Desktop\\recordsTest\\outros1\\dil.xml",
                "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd");
    }
}
