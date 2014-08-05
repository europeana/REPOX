package pt.utl.ist.repox.recordPackage;

import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.jaxen.SimpleNamespaceContext;
import pt.utl.ist.repox.util.XmlUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 1/Mar/2010
 * Time: 14:13:37
 * To change this template use File | Settings | File Templates.
 */

public class XmlRecordSplitter {
    private Charset charset;

    /**
     * Creates a new instance of this class.
     * @param charset
     */
    public XmlRecordSplitter(Charset charset) {
        this.charset = charset;
    }

    /**
     * @param inputFile
     * @param destinationDir
     * @param xpathToRecord
     * @param map
     * @throws DocumentException
     * @throws IOException
     */
    public void splitRecords(File inputFile, File destinationDir, String xpathToRecord, Map map) throws DocumentException, IOException {
        if (destinationDir.exists()) {
            File[] listFiles = destinationDir.listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    file.delete();
                }
            }
        } else {
            destinationDir.mkdir();
        }
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputFile);

        List<Element> list;
        if (map != null) {
            XPath xpath2 = DocumentHelper.createXPath(xpathToRecord);
            xpath2.setNamespaceContext(new SimpleNamespaceContext(map));

            list = xpath2.selectNodes(document);
        } else
            list = document.selectNodes(xpathToRecord);

        int count = 1;
        if (list != null) {
            for (Element element : list) {
                File destinationFile = new File(destinationDir, count + ".xml");

                XmlUtil.writeXml(element, destinationFile, charset);

                count++;
            }
        }
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        SAXReader reader = new SAXReader();
        Element recordElement = reader.read(new FileInputStream("C:\\Users\\GPedrosa\\Desktop\\teste\\09428_Ag_DE_ELocal.xml")).getRootElement();
        System.out.println(recordElement.asXML());

        //String xPathString = "//harvest/OAI-PMH/ListRecords/record/metadata/europeana:record";
        String xPathString = "/harvest/oai:OAI-PMH/oai:ListRecords/oai:record/oai:metadata/europeana:record";

        TreeMap<String, String> namespaces = new TreeMap<String, String>();
        namespaces.put("oai", "http://www.openarchives.org/OAI/2.0/");
        namespaces.put("europeana", "http://www.europeana.eu/schemas/ese/");
        /*namespaces.put("dc", "http://purl.org/dc/elements/1.1/");
        namespaces.put("dcterms", "http://purl.org/dc/terms/");
        */

        XmlRecordSplitter xmlRecordSplitter = new XmlRecordSplitter(Charset.forName("UTF-8"));
        xmlRecordSplitter.splitRecords(new File("C:\\Users\\GPedrosa\\Desktop\\teste\\09428_Ag_DE_ELocal.xml"), new File("C:\\Users\\GPedrosa\\Desktop\\teste\\result"), xPathString, namespaces);

    }

}
