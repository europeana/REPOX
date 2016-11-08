package pt.utl.ist.oai;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.SAXReader;
import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.recordPackage.RecordRepox;
import pt.utl.ist.reports.LogUtil;
import pt.utl.ist.util.FileUtil;
import pt.utl.ist.util.StringUtil;
import pt.utl.ist.util.XmlUtil;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class ResponseTransformer {
  private static final Logger log = Logger.getLogger(ResponseTransformer.class);
  private static final String XSLT_OAI_REQ2RECORDS_FILENAME = "oairesponse2records.xsl";
  private static final String RECORD_ELEMENT_NAME = "record";

  private Transformer transformer;

  public Transformer getTransformer() {
    return transformer;
  }

  public void setTransformer(Transformer transformer) {
    this.transformer = transformer;
  }

  /**
   * Creates a new instance of this class.
   * 
   * @throws TransformerConfigurationException
   */
  public ResponseTransformer() throws TransformerConfigurationException {
    super();
    // Loads the resource from [REPOX_CLASSES]/[XSLT_OAI_REQ2RECORDS_FILENAME]
    InputStream inputXsltStream =
        ResponseTransformer.class.getClassLoader().getResourceAsStream(
            XSLT_OAI_REQ2RECORDS_FILENAME);
    Source xsltSource = new StreamSource(inputXsltStream);

    System.setProperty("javax.xml.transform.TransformerFactory",
        "org.apache.xalan.processor.TransformerFactoryImpl");

    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    this.transformer = transformerFactory.newTransformer(xsltSource);

  }

  private Iterator<Element> getIteratorSplitResponse(File xmlFile, File logFile)
      throws IOException, DocumentException, TransformerException {
    if (logFile == null || (!logFile.exists() && !logFile.createNewFile())) {
      throw new IOException("Unable to create log file: " + logFile.getAbsolutePath());
    }

    SAXReader reader = new SAXReader();
    // Document xmlSource = reader.read(xmlFile);
    // CHANGE BY NUNO
    FileInputStream fileInputStream = new FileInputStream(xmlFile);
    String sourceXmlString = IOUtils.toString(fileInputStream, "UTF-8");// we know it is utf8,
                                                                        // because the harvester has
                                                                        // written it as such
                                                                        // previously
    Document xmlSource = null;
    int atempts = 0;
    while (xmlSource == null && atempts < 100) {
      try {
        xmlSource = reader.read(new StringReader(sourceXmlString));
      } catch (DocumentException exception) {
        Pattern invalidCharPattern =
            Pattern.compile("Character reference \"&#x?([abcdef0-9]{1,8});?\"",
                Pattern.CASE_INSENSITIVE);
        Matcher m = invalidCharPattern.matcher(exception.getMessage());
        if (m.find()) {
          // Error on line 1203 of document
          // file:///data/repox/[temp]OAI-PMH_Requests/oai.driver.research-infrastructures.eu-ALL/recordsRequest-1130.xml
          // : Character reference "&#dbc0" is an invalid XML character. Nested exception:
          // Character reference "&#dbc0" is an invalid XML character.
          String charPattern = "\\&\\#" + m.group(1) + "\\;";
          Matcher replaceCharMatcher = Pattern.compile(charPattern).matcher(sourceXmlString);
          if (replaceCharMatcher.matches())
            sourceXmlString = replaceCharMatcher.replaceAll(" ");
          else {
            charPattern = "\\&\\#" + Integer.parseInt(m.group(1), 16) + "\\;";
            sourceXmlString = sourceXmlString.replaceAll(charPattern, " ");
          }
          atempts++;
        } else {
          // other kind of error throw the exception
          throw exception;
        }
      }
    }
    fileInputStream.close();
    // END CHANGE BY NUNO

    DocumentSource source = new DocumentSource(xmlSource);
    DocumentResult result = new DocumentResult();
    StringUtil.simpleLog("Starting to split OAI-PMH request to Record Files", this.getClass(),
        logFile);
    transformer.transform(source, result);
    Document transformedDoc = result.getDocument();
    Element rootElement = transformedDoc.getRootElement();

    return rootElement.elementIterator(RECORD_ELEMENT_NAME);
  }

  /**
   * Extracts each record from the xmlFile and returns a list of records.
   * 
   * @param xmlFile a file with OAI-PMH requests in XML
   * @param dataSource
   * @param logFile
   * @return a list of RecordRepox
   * @throws Exception
   */
  public List<RecordRepox> splitResponseToRecords(File xmlFile, DataSource dataSource, File logFile)
      throws Exception {
    List<RecordRepox> splitRecords = new ArrayList<RecordRepox>();
    Iterator<Element> iterator = getIteratorSplitResponse(xmlFile, logFile);

    // iterate through child elements of root
    while (iterator.hasNext()) {
      Element currentElement = iterator.next();

      String recordId = currentElement.element("identifier").getText();

      // if(currentElement.element("metadata").elements().size() > 0){
      boolean deleted = false;
      boolean empty = false;
      Element recordElement = null;
      if (currentElement.attribute("status") != null
          && currentElement.attributeValue("status").equals("deleted")) {
        deleted = true;
      } else {
        try {
          if (currentElement.element("metadata").content().size() != 0) {
            recordElement = (Element) currentElement.element("metadata").elements().get(0);
            String xmlContent = recordElement.asXML();
            xmlContent =
                xmlContent.replaceAll("xmlns=\"http://www.openarchives.org/OAI/2.0/\"", "");

            SAXReader reader = new SAXReader();
            Document document = reader.read(new StringReader(xmlContent));
            recordElement = document.getRootElement();
            // recordElement.remove(Namespace.get("http://www.openarchives.org/OAI/2.0/"));
            // remove namespace...
            // ((DefaultElement) recordElement).setDestNamespace(Namespace.NO_NAMESPACE);
          }
          else {
            LogUtil.addEmptyRecordCount(recordId, logFile);
            continue;
          }
        } catch (Exception e) {
          log.error("Error getting metadata from dataSource " + dataSource.getId() + " in xmlFile "
              + xmlFile + " record identifier " + recordId, e);
          LogUtil.addEmptyRecordCount(recordId, logFile);
          continue;
        }
      }
      RecordRepox record =
          dataSource.getRecordIdPolicy().createRecordRepox(recordElement, recordId, true, deleted);
      splitRecords.add(record);
      // }
    }

    StringUtil.simpleLog("Finished splitting OAI-PMH request to List", this.getClass(), logFile);

    return splitRecords;
  }

  /**
   * Extracts each record from the xmlFile to the directory recordsOutputDirname.
   * 
   * @param xmlFile a file with OAI-PMH requests in XML
   * @param recordsOutputDirname the output String where the records will be saved
   * @param logFile
   * @throws TransformerException
   * @throws IOException
   * @throws DocumentException
   */
  public void splitResponseToFiles(File xmlFile, String recordsOutputDirname, File logFile)
      throws TransformerException, IOException, DocumentException {
    File recordsOutputDir = new File(recordsOutputDirname);
    if (!recordsOutputDir.exists() && !recordsOutputDir.mkdir()) {
      throw new RuntimeException("Unable to create dir: " + recordsOutputDir.getAbsolutePath());
    }

    Iterator<Element> iterator = getIteratorSplitResponse(xmlFile, logFile);

    // iterate through child elements of root
    while (iterator.hasNext()) {
      Element currentElement = iterator.next();
      String identifier = sanitizeRecordElement(currentElement);

      String recordPath =
          recordsOutputDir.getAbsolutePath() + File.separator
              + FileUtil.sanitizeToValidFilename(identifier) + ".xml";
      log.debug("recordPath " + recordPath);

      OutputStream outputStream = new FileOutputStream(recordPath);
      XmlUtil.writePrettyPrint(outputStream, currentElement);
    }

    StringUtil.simpleLog("Finished splitting OAI-PMH request to Record Files", this.getClass(),
        logFile);
  }

  /**
   * Extract the first record from an XML Request as a String to the transformed version.
   * 
   * @param xmlRequest a String of an OAI-PMH response with records in XML
   * @return the transformed Record
   * @throws DocumentException
   * @throws TransformerException
   */
  public String splitResponseToRecord(String xmlRequest) throws DocumentException,
      TransformerException {
    Document xmlSource = DocumentHelper.parseText(xmlRequest);
    DocumentResult result = new DocumentResult();
    transformer.transform(new DocumentSource(xmlSource), result);
    Document transformedDoc = result.getDocument();
    Element rootElement = transformedDoc.getRootElement();

    Element recordElement = (Element) rootElement.elementIterator(RECORD_ELEMENT_NAME).next();
    sanitizeRecordElement(recordElement);

    return recordElement.asXML();
  }

  /**
   * Change the internal properties of the Record DOM representation to allow its usage in the file
   * system.
   */
  private String sanitizeRecordElement(Element recordElement) {
    Element identifierElement = getFirstInternalElement(recordElement, "identifier");
    // Here we remove all the ":" because it's the char used for URN separation
    identifierElement.setText(identifierElement.getText().replaceAll(":", "_"));
    String identifier = identifierElement.getText();
    // String datestamp = getInternalNode(currentNode, "datestamp").getTextContent();
    // String setSpec = getInternalNode(currentNode, "setSpec").getTextContent();

    return identifier;
  }

  private Element getFirstInternalElement(Element currentElement, String nodeName) {
    Iterator<Element> iterator = currentElement.elementIterator(nodeName);
    if (iterator.hasNext()) {
      return iterator.next();
    }

    return null;
  }

  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());

    Date fromDate = null;
    Date untilDate = null;
    String sourceUrl = "http://projecteuclid.org/DPubS";
    String sourceSet = "";

    File logFile = new File("c:\\log.txt");

    OaiHarvester harvester =
        new OaiHarvester(sourceUrl, sourceSet, fromDate, untilDate, "oai_dc", logFile, -1);

    harvester.run();

    File file = new File("c:\\teste111.xml");
    String lines = FileUtils.readFileToString(file, "UTF-8");

    Document document = null;
    SAXReader reader = new SAXReader();
    document = reader.read(new StringReader(lines));

    XmlUtil.writePrettyPrint(new File("c:\\teste222.xml"), document);

  }

}
