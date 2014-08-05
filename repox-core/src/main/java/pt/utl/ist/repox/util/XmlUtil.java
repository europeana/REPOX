package pt.utl.ist.repox.util;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.Charset;

/**
 */
public class XmlUtil {
    private static final Logger log               = Logger.getLogger(XmlUtil.class);
    private static final String XML_FILE_ENCODING = "UTF-8";

    /**
     * @param destinationFile
     * @param document
     * @throws IOException
     */
    public static void writePrettyPrint(File destinationFile, Document document) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(destinationFile), XML_FILE_ENCODING);
        XMLWriter writer = new XMLWriter(outputStreamWriter, format);
        writer.write(document);
        writer.close();
    }

    /**
     * @param inputFile
     * @return String of the file contents
     * @throws IOException
     */
    public static String readFileAsString(File inputFile) throws IOException {
        String finalResult = "";
        String str;
        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        while ((str = in.readLine()) != null) {
            finalResult += str;
        }
        in.close();
        return finalResult;
    }

    /**
     * @param outputStream
     * @param document
     * @throws IOException
     */
    public static void writePrettyPrint(OutputStream outputStream, Document document) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, XML_FILE_ENCODING);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(outputStreamWriter, format);
        writer.write(document);
        writer.close();
    }

    /**
     * @param outputStream
     * @param element
     * @throws IOException
     */
    public static void writePrettyPrint(OutputStream outputStream, Element element) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, XML_FILE_ENCODING);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(outputStreamWriter, format);
        writer.write(element);
        writer.close();
    }

    /**
     * @param xmlString
     * @return root Element
     * @throws DocumentException
     * @throws UnsupportedEncodingException
     */
    public static Element getRootElement(String xmlString) throws DocumentException, UnsupportedEncodingException {
        SAXReader reader = new SAXReader();
        InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
        Document document = reader.read(inputStream);
        return document.getRootElement();
    }

    /**
     * @param bytes
     * @return the root element
     * @throws DocumentException
     * @throws UnsupportedEncodingException
     */
    public static Element getRootElement(byte[] bytes) throws DocumentException, UnsupportedEncodingException {
        if (bytes == null) { return null; }

        SAXReader reader = new SAXReader();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        Document document = reader.read(inputStream);
        return document.getRootElement();
    }

    /**
     * @param xmlFile
     * @return the root element
     * @throws DocumentException
     */
    public static Element getRootElement(File xmlFile) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(xmlFile);
        return document.getRootElement();
    }

    /**
     * @param firstElement
     * @param secondElement
     * @return boolean if the elements provided are equal
     */
    public static boolean elementsEqual(Element firstElement, Element secondElement) {
        try {
            OutputFormat format = OutputFormat.createCompactFormat();

            StringWriter firstElementWriter = new StringWriter();
            XMLWriter writerNew = new XMLWriter(firstElementWriter, format);
            writerNew.write(firstElement);

            StringWriter secondElementWriter = new StringWriter();
            XMLWriter writerOld = new XMLWriter(secondElementWriter, format);
            writerOld.write(secondElement);

            log.debug("XML Elements Equal: " + firstElementWriter.toString().equals(secondElementWriter.toString()));

            return firstElementWriter.toString().equals(secondElementWriter.toString());
        } catch (IOException e) {
            log.error("Error Writing XML", e);
            throw new RuntimeException("Error Writing XML", e);
        }
    }

    /**
     * @param element
     * @param destinationFile
     * @param charset
     * @param escapeText
     * @throws IOException
     */
    public static void writeXml(Element element, File destinationFile, Charset charset, boolean escapeText) throws IOException {
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        outputFormat.setEncoding(charset.toString());

        Document document = DocumentHelper.createDocument((Element)element.detach());

        XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(destinationFile), charset), outputFormat);
        writer.setEscapeText(escapeText);
        writer.write(document);
        writer.close();
    }

    /**
     * @param element
     * @param destinationFile
     * @param charset
     * @throws IOException
     */
    public static void writeXml(Element element, File destinationFile, Charset charset) throws IOException {
        writeXml(element, destinationFile, charset, true);
    }

    /**
     * This method ensures that the output String has only valid XML unicode characters as specified by the
     * XML 1.0 standard. For reference, please see the
     * standard. This method will return an empty String if the input is null or empty.
     *
     * @author Nuno Freire
     * @param  s - The String whose non-valid characters we want to replace.
     * @return The in String, where non-valid characters are replace by spaces.
     */
    public static String removeInvalidXMLCharacters(String s) {

        StringBuilder out = new StringBuilder(); // Used to hold the output.
        int codePoint; // Used to reference the current character.
        int i = 0;
        while (i < s.length()) {
            codePoint = s.codePointAt(i); // This is the unicode code of the character.
            if ((codePoint == 0x9) || // Consider testing larger ranges first to improve speed.
            (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF))) {
                out.append(Character.toChars(codePoint));
            } else {
                out.append(' ');
            }
            i += Character.charCount(codePoint); // Increment with the number of code units(java chars) needed to represent a Unicode char.
        }
        return out.toString();
    }

    /**
     * @param inputFilePath
     * @param schema
     */
    public static void validateXmlFile(String inputFilePath, String schema) {
        try {
            SAXParserFactory sf = SAXParserFactory.newInstance();
            sf.setNamespaceAware(true);
            sf.setValidating(true);
            SAXParser sp = sf.newSAXParser();
            sp.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            sp.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", schema);

            DefaultHandler handler = new XmlDefaultHandler();
            sp.parse(new InputSource(new FileReader(inputFilePath)), handler);
            log.debug("Success");
        } catch (FactoryConfigurationError e) {
            log.error(e.toString());
        } catch (ParserConfigurationException e) {
            log.error(e.toString());
        } catch (SAXException e) {
            log.error(e.toString());
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    /**
     */
    public static class XmlDefaultHandler extends DefaultHandler {
        /** 
         * @see org.xml.sax.ErrorHandler#error(SAXParseException)
         */
        @Override
        public void error(SAXParseException spe) throws SAXException {
            throw spe;
        }

        /** 
         * @see org.xml.sax.ErrorHandler#fatalError(SAXParseException)
         */
        @Override
        public void fatalError(SAXParseException spe) throws SAXException {
            throw spe;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        validateXmlFile("C:\\testeValidate\\test.xml", "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd");
        /*
        System.out.println(
                "<sfsdf>&nasdfij:);\""+((char)0x02)+"\'"
        );
        System.out.println(
                removeInvalidXMLCharacters("<sfsdf>&nasdfij:);\""+((char)0x02)+"\'")
        );*/
    }
}
