/*
 * Created on 17/Mar/2006
 *
 */
package pt.utl.ist.recordPackage;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import pt.utl.ist.util.XmlUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.TreeMap;

public class RecordRepoxXpathId implements RecordRepox {
    private static final Logger log       = Logger.getLogger(RecordRepoxXpathId.class);

    protected Element           dom;
    protected XPath             idXpath;
    protected boolean           isDeleted = false;
    protected boolean           isEmpty = false;

    @Override
    public Object getId() {
        return idXpath.valueOf(dom);
    }

    @Override
    public Element getDom() {
        return dom;
    }

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    @Override
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * Creates a new instance of this class.
     */
    public RecordRepoxXpathId() {
    }

    /**
     * Creates a new instance of this class.
     *
     * @param dom
     * @param idXpath
     */
    public RecordRepoxXpathId(Element dom, XPath idXpath) {
        this.dom = dom;
        this.idXpath = idXpath;
    }

    /**
     * Creates a new instance of this class.
     *
     * @param dom
     * @param idXpath
     * @param isDeleted
     */
    public RecordRepoxXpathId(Element dom, XPath idXpath, boolean isDeleted) {
        this(dom, idXpath);
        this.isDeleted = isDeleted;
    }

    @Override
    public byte[] serialize() {
        try {
            if (dom == null) {
                return null;
            } else {
                byte[] domToBytes = dom.asXML().getBytes("UTF-8");
                return domToBytes;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deserialize(byte[] bytes) throws DocumentException, UnsupportedEncodingException {
        dom = XmlUtil.getRootElement(bytes);
    }

    /**
     * @param args
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    public static void main(String[] args) throws FileNotFoundException, DocumentException {
        SAXReader reader = new SAXReader();
        Element recordElement = reader.read(new FileInputStream("C:\\Users\\Gilberto Pedrosa\\Desktop\\teste.xml")).getRootElement();
        System.out.println(recordElement.asXML());
        String xPathString = "/europeana:record/dc:identifier[@identifier_type=\"URN\"] | /europeana:record/dc:identifier[@identifier_type=\"SIGN\"]";
        XPath xPath = DocumentHelper.createXPath(xPathString);
        TreeMap<String, String> namespaces = new TreeMap<String, String>();
        namespaces.put("dc", "http://purl.org/dc/elements/1.1/");
        namespaces.put("europeana", "http://www.europeana.eu/schemas/ese/");
        xPath.setNamespaceURIs(namespaces);
        RecordRepoxXpathId record = new RecordRepoxXpathId(recordElement, xPath);

        log.info("record id = " + record.getId());
        log.info("record1 nodes size = " + xPath.selectNodes(record.getDom()).size());
    }

}
