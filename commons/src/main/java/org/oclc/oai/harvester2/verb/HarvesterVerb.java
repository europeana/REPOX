/**
 Copyright 2006 OCLC, Online Computer Library Center
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package org.oclc.oai.harvester2.verb;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import pt.utl.ist.util.XmlUtil;

/**
 * HarvesterVerb is the parent class for each of the OAI verbs.
 * 
 * @author Jefffrey A. Young, OCLC Online Computer Library Center
 * @author Nuno Freire
 */
public abstract class HarvesterVerb {
    private static Logger                    logger                                     = Logger.getLogger(HarvesterVerb.class);

    /* Gilberto */
    /** HarvesterVerb NAMESPACE_V2_0 */
    public static final String               NAMESPACE_V2_0                             = "http://www.openarchives.org/OAI/2.0/";
    /** HarvesterVerb NAMESPACE_V1_1 */
    public static final String               NAMESPACE_V1_1                             = "http://www.openarchives.org/OAI/1.1/";

    /* Primary OAI namespaces */
    /** HarvesterVerb SCHEMA_LOCATION_V2_0 */
    public static final String               SCHEMA_LOCATION_V2_0                       = "http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd";
    /** HarvesterVerb SCHEMA_LOCATION_V1_1_GET_RECORD */
    public static final String               SCHEMA_LOCATION_V1_1_GET_RECORD            = "http://www.openarchives.org/OAI/1.1/OAI_GetRecord http://www.openarchives.org/OAI/1.1/OAI_GetRecord.xsd";
    /** HarvesterVerb SCHEMA_LOCATION_V1_1_IDENTIFY */
    public static final String               SCHEMA_LOCATION_V1_1_IDENTIFY              = "http://www.openarchives.org/OAI/1.1/OAI_Identify http://www.openarchives.org/OAI/1.1/OAI_Identify.xsd";
    /** HarvesterVerb SCHEMA_LOCATION_V1_1_LIST_IDENTIFIERS */
    public static final String               SCHEMA_LOCATION_V1_1_LIST_IDENTIFIERS      = "http://www.openarchives.org/OAI/1.1/OAI_ListIdentifiers http://www.openarchives.org/OAI/1.1/OAI_ListIdentifiers.xsd";
    /** HarvesterVerb SCHEMA_LOCATION_V1_1_LIST_METADATA_FORMATS */
    public static final String               SCHEMA_LOCATION_V1_1_LIST_METADATA_FORMATS = "http://www.openarchives.org/OAI/1.1/OAI_ListMetadataFormats http://www.openarchives.org/OAI/1.1/OAI_ListMetadataFormats.xsd";
    /** HarvesterVerb SCHEMA_LOCATION_V1_1_LIST_RECORDS */
    public static final String               SCHEMA_LOCATION_V1_1_LIST_RECORDS          = "http://www.openarchives.org/OAI/1.1/OAI_ListRecords http://www.openarchives.org/OAI/1.1/OAI_ListRecords.xsd";
    /** HarvesterVerb SCHEMA_LOCATION_V1_1_LIST_SETS */
    public static final String               SCHEMA_LOCATION_V1_1_LIST_SETS             = "http://www.openarchives.org/OAI/1.1/OAI_ListSets http://www.openarchives.org/OAI/1.1/OAI_ListSets.xsd";

    private HashMap<Thread, DocumentBuilder> builderMap                                 = new HashMap<Thread, DocumentBuilder>();
    private Element                          namespaceElement                           = null;
    private DocumentBuilderFactory           factory                                    = null;
    //    private TransformerFactory xformFactory = TransformerFactory.newInstance();
    private Document                         doc                                        = null;
    private String                           schemaLocation                             = null;
    private String                           requestURL                                 = null;

    //gilberto
    private String                           defaultNamespace                           = null;

    /**
     * Get the OAI response as a DOM object
     * 
     * @return the DOM for the OAI response
     */
    public Document getDocument() {
        return doc;
    }

    /**
     * Get the xsi:schemaLocation for the OAI response
     * 
     * @return the xsi:schemaLocation value
     */
    public String getSchemaLocation() {
        return schemaLocation;
    }

    /**
     * @return default namespace String
     */
    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    /**
     * Get the OAI errors
     * 
     * @return a NodeList of /oai:OAI-PMH/oai:error elements
     * @throws TransformerException
     */
    public NodeList getErrors() throws TransformerException {
        if (SCHEMA_LOCATION_V2_0.equals(getSchemaLocation())) {
            return getNodeList("/oai20:OAI-PMH/oai20:error");
        } else {
            return null;
        }
    }

    /**
     * 
     * @return true if the List verb returned no valid records, false otherwise
     * @throws TransformerException
     */
    public boolean isResultEmpty() throws TransformerException {
        if (SCHEMA_LOCATION_V2_0.equals(getSchemaLocation())) {
            NodeList errorList = getNodeList("/oai20:OAI-PMH/oai20:error[@code = 'noRecordsMatch']");
            return (errorList != null && errorList.getLength() > 0); //noRecordsMatch is not an error, simply an empty list!!!
        }

        return false;
    }

    /**
     * Get the OAI request URL for this response
     * 
     * @return the OAI request URL as a String
     */
    public String getRequestURL() {
        return requestURL;
    }

    /**
     * Mock object creator (for unit testing purposes)
     */
    public HarvesterVerb() {
        try {
            /* Load DOM Document */
            factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Thread t = Thread.currentThread();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builderMap.put(t, builder);

            DOMImplementation impl = builder.getDOMImplementation();
            Document namespaceHolder = impl.createDocument("http://www.oclc.org/research/software/oai/harvester", "harvester:namespaceHolder", null);
            namespaceElement = namespaceHolder.getDocumentElement();
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:harvester", "http://www.oclc.org/research/software/oai/harvester");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:oai20", "http://www.openarchives.org/OAI/2.0/");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:oai11_GetRecord", "http://www.openarchives.org/OAI/1.1/OAI_GetRecord");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:oai11_Identify", "http://www.openarchives.org/OAI/1.1/OAI_Identify");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:oai11_ListIdentifiers", "http://www.openarchives.org/OAI/1.1/OAI_ListIdentifiers");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:oai11_ListMetadataFormats", "http://www.openarchives.org/OAI/1.1/OAI_ListMetadataFormats");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:oai11_ListRecords", "http://www.openarchives.org/OAI/1.1/OAI_ListRecords");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:oai11_ListSets", "http://www.openarchives.org/OAI/1.1/OAI_ListSets");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs the OAI request
     * 
     * @param requestURL
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     */
    public HarvesterVerb(String requestURL) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        this();
        harvest(requestURL);
    }

    /**
     * Performs the OAI request
     * 
     * @param requestURL
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     */
    public void harvestOldOclcImplementation(String requestURL) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        this.requestURL = requestURL;
        logger.debug("requestURL=" + requestURL);
        InputStream in;
        URL url = new URL(requestURL);
        HttpURLConnection con;
        int responseCode;
        do {
            con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("User-Agent", "OAIHarvester/2.0");
            con.setRequestProperty("Accept-Encoding", "compress, gzip, identify");
            try {
                responseCode = con.getResponseCode();
                logger.debug("responseCode=" + responseCode);
            } catch (FileNotFoundException e) {
                // assume it's a 503 response
                logger.error(requestURL, e);
                responseCode = HttpURLConnection.HTTP_UNAVAILABLE;
            }

            if (responseCode == HttpURLConnection.HTTP_UNAVAILABLE) {
                long retrySeconds = con.getHeaderFieldInt("Retry-After", -1);
                if (retrySeconds == -1) {
                    long now = (new Date()).getTime();
                    long retryDate = con.getHeaderFieldDate("Retry-After", now);
                    retrySeconds = retryDate - now;
                }
                if (retrySeconds == 0) { // Apparently, it's a bad URL
                    throw new FileNotFoundException("Bad URL?");
                }
                logger.warn("Server response: Retry-After=" + retrySeconds);
                if (retrySeconds > 0) {
                    try {
                        Thread.sleep(retrySeconds * 1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } while (responseCode == HttpURLConnection.HTTP_UNAVAILABLE);
        String contentEncoding = con.getHeaderField("Content-Encoding");
        logger.debug("contentEncoding=" + contentEncoding);
        if ("compress".equals(contentEncoding)) {
            ZipInputStream zis = new ZipInputStream(con.getInputStream());
            zis.getNextEntry();
            in = zis;
        } else if ("gzip".equals(contentEncoding)) {
            in = new GZIPInputStream(con.getInputStream());
        } else if ("deflate".equals(contentEncoding)) {
            in = new InflaterInputStream(con.getInputStream());
        } else {
            in = con.getInputStream();
        }

        InputSource data = new InputSource(in);

        Thread t = Thread.currentThread();
        DocumentBuilder builder = builderMap.get(t);
        if (builder == null) {
            builder = factory.newDocumentBuilder();
            builderMap.put(t, builder);
        }
        doc = builder.parse(data);

        StringTokenizer tokenizer = new StringTokenizer(getSingleString("/*/@xsi:schemaLocation"), " ");
        StringBuffer sb = new StringBuffer();
        while (tokenizer.hasMoreTokens()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(tokenizer.nextToken());
        }
        this.schemaLocation = sb.toString();
    }

    /**
     * Performs the OAI request, recovering from typical XML error
     * 
     * @author nfreire Nuno Freire / Gilberto Pedrosa
     * @param requestURL
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     */
    private void harvest(String requestURL) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        this.requestURL = requestURL;
        logger.debug("requestURL=" + requestURL);
        InputStream in;
        URL url = new URL(requestURL);
        HttpURLConnection con;
        int responseCode;
        do {
            con = (HttpURLConnection)url.openConnection();
            con.setConnectTimeout(30000);
            con.setReadTimeout(600000);

            if (con.getAllowUserInteraction()) {
                con.setRequestProperty("User-Agent", "OAIHarvester/2.0");
                con.setRequestProperty("Accept-Encoding", "compress, gzip, identify");
            }
            try {
                responseCode = con.getResponseCode();
                logger.debug("responseCode=" + responseCode);
            } catch (FileNotFoundException e) {
                // assume it's a 503 response
                logger.error(requestURL, e);
                responseCode = HttpURLConnection.HTTP_UNAVAILABLE;
            }

            if (responseCode == HttpURLConnection.HTTP_UNAVAILABLE) {
                long retrySeconds = con.getHeaderFieldInt("Retry-After", -1);
                if (retrySeconds == -1) {
                    long now = (new Date()).getTime();
                    long retryDate = con.getHeaderFieldDate("Retry-After", now);
                    retrySeconds = retryDate - now;
                }
                if (retrySeconds == 0) { // Apparently, it's a bad URL
                    throw new FileNotFoundException("Bad URL?");
                }
                logger.warn("Server response: Retry-After=" + retrySeconds);
                if (retrySeconds > 0) {
                    try {
                        Thread.sleep(retrySeconds * 1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } while (responseCode == HttpURLConnection.HTTP_UNAVAILABLE);
        String contentEncoding = con.getHeaderField("Content-Encoding");
        logger.debug("contentEncoding=" + contentEncoding);
        if ("compress".equals(contentEncoding)) {
            ZipInputStream zis = new ZipInputStream(con.getInputStream());
            zis.getNextEntry();
            in = zis;
        } else if ("gzip".equals(contentEncoding)) {
            in = new GZIPInputStream(con.getInputStream());
        } else if ("deflate".equals(contentEncoding)) {
            in = new InflaterInputStream(con.getInputStream());
        } else {
            in = con.getInputStream();
        }

        byte[] inputBytes = IOUtils.toByteArray(in);
        InputSource data = new InputSource(new ByteArrayInputStream(inputBytes));

        Thread t = Thread.currentThread();
        DocumentBuilder builder = builderMap.get(t);
        if (builder == null) {
            builder = factory.newDocumentBuilder();
            builderMap.put(t, builder);
        }
        try {
            doc = builder.parse(data);
        } catch (SAXException e) {
            try {
                //Here we can try to recover the xml from known typical problems

                //Recover from invalid characters
                //we assume this is UTF-8...
                String xmlString = new String(inputBytes, "UTF-8");
                xmlString = XmlUtil.removeInvalidXMLCharacters(xmlString);

                data = new InputSource(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
                doc = builder.parse(data);
            } catch (Exception e2) {
                //the recovered version did not work either. Throw the original exception
                throw e;
            }
        } catch (IOException e3) {
            System.out.println("e = " + e3.getMessage());
        } catch (Exception e4) {
            System.out.println("e = " + e4.getMessage());
        }

        StringTokenizer tokenizer = new StringTokenizer(getSingleString("/*/@xsi:schemaLocation"), " ");
        StringBuffer sb = new StringBuffer();
        while (tokenizer.hasMoreTokens()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(tokenizer.nextToken());
        }
        this.schemaLocation = sb.toString();
        this.defaultNamespace = getDocument().getDocumentElement().getNamespaceURI();
    }

    /**
     * Get the String value for the given XPath location in the response DOM
     * 
     * @param xpath
     * @return a String containing the value of the XPath location.
     * @throws TransformerException
     */
    public String getSingleString(String xpath) throws TransformerException {
        return getSingleString(getDocument(), xpath);
        // return XPathAPI.eval(getDocument(), xpath, namespaceElement).str();
        // String str = null;
        // Node node = XPathAPI.selectSingleNode(getDocument(), xpath,
        // namespaceElement);
        // if (node != null) {
        // XObject xObject = XPathAPI.eval(node, "string()");
        // str = xObject.str();
        // }
        // return str;
    }

    /**
     * Get the String value for the given XPath
     * 
     * @param node
     * @param xpath
     * @return XPath value
     * @throws TransformerException
     */
    public String getSingleString(Node node, String xpath) throws TransformerException {
        return XPathAPI.eval(node, xpath, namespaceElement).str();
    }

    /**
     * Get a NodeList containing the nodes in the response DOM for the specified
     * XPath
     * 
     * @param xpath
     * @return the NodeList for the XPath into the response DOM
     * @throws TransformerException
     */
    public NodeList getNodeList(String xpath) throws TransformerException {
        return XPathAPI.selectNodeList(getDocument(), xpath, namespaceElement);
    }

    @Override
    public String toString() {
        // Element docEl = getDocument().getDocumentElement();
        // return docEl.toString();
        /*
         * Source input = new DOMSource(getDocument()); StringWriter sw = new
         * StringWriter(); Result output = new StreamResult(sw); try {
         * Transformer idTransformer = xformFactory.newTransformer();
         * idTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
         * "yes"); idTransformer.transform(input, output); return sw.toString();
         * } catch (TransformerException e) { return e.getMessage(); }
         */

        //Serialize DOM
        OutputFormat format = new OutputFormat(doc);
        // as a String
        StringWriter stringOut = new StringWriter();
        XMLSerializer serial = new XMLSerializer(stringOut, format);
        try {
            serial.serialize(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Display the XML
        return stringOut.toString();

    }

    /**
     * @param args
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String requestURL = "https://databank.ora.ox.ac.uk/oaipmh?verb=ListRecords&resumptionToken=20121206_TKMGW4A_SWT3NHV";

        //String requestURL = "http://bd2.inesc-id.pt:8080/repox2Eudml/OAIHandler?verb=ListRecords&resumptionToken=1354116062009:ELibM_external:eudml-article2:33753:37054::";
        //String requestURL = "http://bd2.inesc-id.pt:8080/repox2Eudml/OAIHandler?verb=GetRecord&identifier=urn:eudml.eu:ELibM_external:05152756&metadataPrefix=eudml-article2";
        //String requestURL = "C:/Users/Gilberto Pedrosa/Desktop/OAIHandler.xml";
        //FileInputStream fis = new FileInputStream(requestURL);
        //InputStream in = fis;
        logger.debug("requestURL=" + requestURL);
        DocumentBuilderFactory factory;
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Thread t = Thread.currentThread();
        DocumentBuilder builder = factory.newDocumentBuilder();
        HashMap<Thread, DocumentBuilder> builderMap = new HashMap<Thread, DocumentBuilder>();
        builderMap.put(t, builder);

        InputStream in;

        URL url = new URL(requestURL);
        HttpURLConnection con;
        int responseCode;
        do {
            con = (HttpURLConnection)url.openConnection();
            con.setConnectTimeout(30000);
            con.setReadTimeout(600000);

            if (con.getAllowUserInteraction()) {
                con.setRequestProperty("User-Agent", "OAIHarvester/2.0");
                con.setRequestProperty("Accept-Encoding", "compress, gzip, identify");
            }
            try {
                responseCode = con.getResponseCode();
                logger.debug("responseCode=" + responseCode);
            } catch (FileNotFoundException e) {
                // assume it's a 503 response
                logger.error(requestURL, e);
                responseCode = HttpURLConnection.HTTP_UNAVAILABLE;
            }

            if (responseCode == HttpURLConnection.HTTP_UNAVAILABLE) {
                long retrySeconds = con.getHeaderFieldInt("Retry-After", -1);
                if (retrySeconds == -1) {
                    long now = (new Date()).getTime();
                    long retryDate = con.getHeaderFieldDate("Retry-After", now);
                    retrySeconds = retryDate - now;
                }
                if (retrySeconds == 0) { // Apparently, it's a bad URL
                    throw new FileNotFoundException("Bad URL?");
                }
                logger.warn("Server response: Retry-After=" + retrySeconds);
                if (retrySeconds > 0) {
                    try {
                        Thread.sleep(retrySeconds * 1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        while (responseCode == HttpURLConnection.HTTP_UNAVAILABLE);
        String contentEncoding = con.getHeaderField("Content-Encoding");
        logger.debug("contentEncoding=" + contentEncoding);
        if ("compress".equals(contentEncoding)) {
            ZipInputStream zis = new ZipInputStream(con.getInputStream());
            zis.getNextEntry();
            in = zis;
        } else if ("gzip".equals(contentEncoding)) {
            in = new GZIPInputStream(con.getInputStream());
        } else if ("deflate".equals(contentEncoding)) {
            in = new InflaterInputStream(con.getInputStream());
        } else {
            in = con.getInputStream();
        }

        byte[] inputBytes = IOUtils.toByteArray(in);
        InputSource data = new InputSource(new ByteArrayInputStream(inputBytes));

        String xmlString = new String(inputBytes, "UTF-8");
        xmlString = XmlUtil.removeInvalidXMLCharacters(xmlString);

        builder.parse(data);

        System.out.println("data = " + data);
    }
}
