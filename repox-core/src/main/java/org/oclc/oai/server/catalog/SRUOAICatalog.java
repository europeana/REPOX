/**
 * Copyright 2006 OCLC Online Computer Library Center Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or
 * agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oclc.oai.server.catalog;

import org.apache.xpath.XPathAPI;
import org.oclc.oai.server.verb.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class SRUOAICatalog extends AbstractCatalog {
    private static final boolean debug = false;
    private String sruURL;
    private String sortKeys = "";
    protected int maxListSize;
    private TreeMap sets = null;
    private static TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private static Transformer transformer = null;
    private static HashMap builderMap = new HashMap();
    private static Element xmlnsEl = null;
    private static DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
    
    static {
        try {
            transformer = transformerFactory.newTransformer();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = getBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            Document xmlnsDoc
            = impl.createDocument("http://www.oclc.org/research/software/oai/harvester",
                    "harvester:xmlnsDoc", null);
            xmlnsEl = xmlnsDoc.getDocumentElement();
            xmlnsEl.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:srw",
            "http://www.loc.gov/zing/srw/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public SRUOAICatalog(Properties properties) {
        this(properties, properties.getProperty("SRUOAICatalog.sruURL"));
    }
    
    public SRUOAICatalog(Properties properties, String sruURL) {
        sortKeys = properties.getProperty("SRUOAICatalog.sortKeys");
        if (sortKeys == null) sortKeys = "";
        
        String maxListSize =
            properties.getProperty("SRUOAICatalog.maxListSize");
        
        if (maxListSize == null) {
            throw new IllegalArgumentException("SRUOAICatalog.maxListSize is missing from the properties file");
        } else {
            this.maxListSize = Integer.parseInt(maxListSize);
        }
        
        this.sruURL = sruURL;
        sets = getSets(properties);
    }
    
    private static TreeMap getSets(Properties properties) {
        TreeMap treeMap = new TreeMap();
        return treeMap;
    }
    
    private static DocumentBuilder getBuilder() throws ParserConfigurationException {
        Thread currentThread = Thread.currentThread();
        DocumentBuilder builder = (DocumentBuilder) builderMap.get(currentThread);
        if (builder == null) {
            builder = factory.newDocumentBuilder();
            builderMap.put(currentThread, builder);
        }
        return builder;
    }
    
    private String normalizeTerm(String term) {
        return term;
    }
    
    public Map listSets() throws NoSetHierarchyException {
        if (sets.size() == 0)
            throw new NoSetHierarchyException();
        Map listSetsMap = new LinkedHashMap();
        try {
            Object[] keys = sets.keySet().toArray();
            Object[] values = sets.values().toArray();
            ArrayList newList = new ArrayList();
            for (int i = 0; i < keys.length; ++i) {
                newList.add(values[i]);
            }
            listSetsMap.put("sets", newList.iterator());
        } catch (Throwable e) {
            System.err.println("SRUOAICatalog.listSets: browse failed");
            e.printStackTrace();
        }
        
        return listSetsMap;
    }
    
    public Map listSets(String resumptionToken)
    throws BadResumptionTokenException {
        throw new BadResumptionTokenException();
    }
    
    /**
     * get a map containing a key="headers" value=Iterator and
     * a key="resumptionToken" value=String. The "headers" Map
     * contains Map.Entrys where key="header" value="deleted" or null.
     */
    public Map listIdentifiers(String from,
            String until,
            String set,
            String metadataPrefix)
    throws
    BadArgumentException,
    NoItemsMatchException,
    OAIInternalServerError {
        if (set != null
                && set.length() > 0
                && from.equals(toFinestFrom("0000-00-00"))
                && until.equals(toFinestUntil("9999-99-99"))) {
            from = null;
            until = null;
        }
        Map listIdentifiersMap = new HashMap();
        ArrayList headers = new ArrayList();
        ArrayList identifiers = new ArrayList();
        Document srResponse;
        try {
            srResponse =
                getSearchRetrieveResponse(sruURL,
                        from,
                        until,
                        set,
                        "http://www.openarchives.org/OAI/2.0/#header",
                        1,
                        maxListSize,
                "xml");
        } catch (IOException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        } catch (SAXException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        }
        try {
            NodeList nodeList = getRecords(srResponse);
            if (nodeList != null) {
                RecordFactory recordFactory = getRecordFactory();
                for (int i = 0; i < nodeList.getLength(); ++i) {
                    Object rec = getRecordData(nodeList.item(i));
                    HashMap hashMap = new HashMap();
                    hashMap.put("header", rec);
                    String localIdentifier = getRecordFactory().getLocalIdentifier(hashMap);
                    try {
                        hashMap.put("metadata", getNativeMetadata(localIdentifier,
                                metadataPrefix));
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new OAIInternalServerError(e.getMessage());
                    } catch (TransformerException e) {
                        e.printStackTrace();
                        throw new OAIInternalServerError(e.getMessage());
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                        throw new OAIInternalServerError(e.getMessage());
                    } catch (SAXException e) {
                        e.printStackTrace();
                        throw new OAIInternalServerError(e.getMessage());
                    }
                    String[] header = recordFactory.createHeader(hashMap);
                    headers.add(header[0]);
                    identifiers.add(header[1]);
                }
                try {
                    String resultSetId = XPathAPI.eval(srResponse, "/srw:searchRetrieveResponse/srw:resultSetId", xmlnsEl).str();
                    String nextRecordPosition = XPathAPI.eval(srResponse, "/srw:searchRetrieveResponse/srw:nextRecordPosition", xmlnsEl).str();
                    if (nextRecordPosition != null && nextRecordPosition.length() > 0) {
                        StringBuffer resumptionToken = new StringBuffer();
                        resumptionToken.append(resultSetId);
                        resumptionToken.append(":");
                        resumptionToken.append(nextRecordPosition);
                        resumptionToken.append(":");
                        resumptionToken.append(metadataPrefix);
                        listIdentifiersMap.put(
                                "resumptionMap",
                                getResumptionMap(resumptionToken.toString()));
                    }
                } catch (TransformerException e) {
                    e.printStackTrace();
                    throw new OAIInternalServerError(e.getMessage());
                }
            } else {
                throw new NoItemsMatchException();
            }
            listIdentifiersMap.put("headers", headers.iterator());
            listIdentifiersMap.put("identifiers", identifiers.iterator());
            return listIdentifiersMap;
        } catch (TransformerException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        } catch (SAXException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        }
    }
    
    /**
     * get a map containing a key="headers" value=Iterator and
     * a key="resumptionToken" value=String. The "headers" Iterator
     * contains Map.Entrys where key="header" value="deleted" or null.
     */
    public Map listIdentifiers(String resumptionToken)
    throws BadResumptionTokenException, OAIInternalServerError {
        StringTokenizer tokenizer = new StringTokenizer(resumptionToken, ":");
        String resultSetId;
        String nextRecordPosition;
        String metadataPrefix;
        try {
            resultSetId = tokenizer.nextToken();
            nextRecordPosition = tokenizer.nextToken();
            metadataPrefix = tokenizer.nextToken();
            if (metadataPrefix.equals("null"))
                metadataPrefix = null;
        } catch (NoSuchElementException e) {
            throw new BadResumptionTokenException();
        }
        Map listIdentifiersMap = new HashMap();
        ArrayList headers = new ArrayList();
        ArrayList identifiers = new ArrayList();
        try {
            Document srResponse;
            try {
                srResponse =
                    getSearchRetrieveResponse(sruURL,
                            resultSetId,
                            nextRecordPosition,
                            "http://www.openarchives.org/OAI/2.0/#header",
                            maxListSize,
                    "xml");
            } catch (IOException e) {
                e.printStackTrace();
                throw new OAIInternalServerError(e.getMessage());
            } catch (SAXException e) {
                e.printStackTrace();
                throw new OAIInternalServerError(e.getMessage());
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                throw new OAIInternalServerError(e.getMessage());
            }
            NodeList nodeList = getRecords(srResponse);
            if (nodeList != null) {
                RecordFactory recordFactory = getRecordFactory();
                for (int i = 0; i < nodeList.getLength(); ++i) {
                    
                    Object rec = getRecordData(nodeList.item(i));
                    HashMap hashMap = new HashMap();
                    hashMap.put("header", rec);
                    String localIdentifier = getRecordFactory().getLocalIdentifier(hashMap);
                    hashMap.put("metadata", getNativeMetadata(localIdentifier,
                            metadataPrefix));
                    String[] header = recordFactory.createHeader(hashMap);
                    headers.add(header[0]);
                    identifiers.add(header[1]);
                }
                nextRecordPosition = XPathAPI.eval(srResponse,
                        "/srw:searchRetrieveResponse/srw:nextRecordPosition",
                        xmlnsEl).str();
                if (nextRecordPosition != null && nextRecordPosition.length() > 0) {
                    StringBuffer newResumptionToken = new StringBuffer();
                    newResumptionToken.append(resultSetId);
                    newResumptionToken.append(":");
                    newResumptionToken.append(nextRecordPosition);
                    newResumptionToken.append(":");
                    newResumptionToken.append(metadataPrefix);
                    listIdentifiersMap.put(
                            "resumptionMap",
                            getResumptionMap(newResumptionToken.toString()));
                }
            }
            listIdentifiersMap.put("headers", headers.iterator());
            listIdentifiersMap.put("identifiers", identifiers.iterator());
        } catch (Throwable e) {
            e.printStackTrace();
            throw new OAIInternalServerError("Database Failure");
        }
        return listIdentifiersMap;
    }
    
    /**
     * get an Iterator containing Map.Entrys where key=metadataPrefix
     * and value=schema.
     */
    public Vector getSchemaLocations(String oaiIdentifier)
    throws
    IdDoesNotExistException,
    NoMetadataFormatsException,
    OAIInternalServerError {
        Vector result = new Vector();
        NodeList nodeList = null;
        try {
            nodeList = getIdentifierRecords(sruURL, oaiIdentifier, (String) null);
        } catch (Throwable e) {
            throw new OAIInternalServerError("Database failure");
        }
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); ++i) {
                try {
                    Object rec = getRecordData(nodeList.item(i));
                    if (rec != null) {
                        Vector schemaLocations =
                            getRecordFactory().getSchemaLocations(rec);
                        for (Object schemaLocation : schemaLocations) {
                            result.add(schemaLocation);
                        }
                    } else {
                        throw new OAIInternalServerError("Null Record");
                    }
                } catch (TransformerException e) {
                    e.printStackTrace();
                    throw new OAIInternalServerError(e.getMessage());
                } catch (SAXException e) {
                    e.printStackTrace();
                    throw new OAIInternalServerError(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new OAIInternalServerError(e.getMessage());
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                    throw new OAIInternalServerError(e.getMessage());
                }
            }
        } else {
            throw new IdDoesNotExistException(oaiIdentifier);
        }
        return result;
    }
    
    /**
     * get a DocumentFragment containing the specified record
     */
    public String getRecord(String oaiIdentifier, String metadataPrefix)
    throws
    IdDoesNotExistException,
    IdDoesNotExistException,
    CannotDisseminateFormatException,
    OAIInternalServerError {
        Object nativeObject;
        try {
            nativeObject = getFullRecord(sruURL, oaiIdentifier, metadataPrefix);
        } catch (IOException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        } catch (TransformerException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        } catch (SAXException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        }
        
        
        if (nativeObject != null) {
            String schemaURL = null;
            
            if (metadataPrefix != null) {
                if ((schemaURL = getCrosswalks().getSchemaURL(metadataPrefix))
                        == null) {
                    throw new CannotDisseminateFormatException(metadataPrefix);
                }
            }
            try {
                String s =
                    getRecordFactory().create(
                            nativeObject,
                            schemaURL,
                            metadataPrefix);
                return s;
            } catch (CannotDisseminateFormatException e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            throw new IdDoesNotExistException(oaiIdentifier);
        }
    }
    
    /**
     * get a DocumentFragment containing the specified record
     */
    public String getMetadata(String oaiIdentifier, String metadataPrefix)
    throws
    IdDoesNotExistException,
    IdDoesNotExistException,
    CannotDisseminateFormatException,
    OAIInternalServerError {
        Object nativeObject;
        try {
            nativeObject = getFullRecord(sruURL, oaiIdentifier, metadataPrefix);
        } catch (IOException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        } catch (TransformerException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        } catch (SAXException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        }
        
        if (debug) {
            System.out.println(nativeObject);
        }
        if (nativeObject != null) {
            String schemaURL = null;
            
            if (metadataPrefix != null) {
                if ((schemaURL = getCrosswalks().getSchemaURL(metadataPrefix))
                        == null) {
                    if (debug) {
                        System.out.println(
                        "SRUOAICatalog.getRecord: metadataPrefix not found");
                    }
                    throw new CannotDisseminateFormatException(metadataPrefix);
                }
            }
            try {
                return getRecordFactory().createMetadata(
                        nativeObject,
                        schemaURL,
                        metadataPrefix);
            } catch (CannotDisseminateFormatException e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            throw new IdDoesNotExistException(oaiIdentifier);
        }
    }
    
    private NodeList getIdentifierRecords(String sruURL,
            String oaiIdentifier,
            String metadataPrefix)
    throws TransformerException, SAXException, IOException,
    ParserConfigurationException {
        Document srResponse;
        String localIdentifier = getRecordFactory().fromOAIIdentifier(oaiIdentifier);
        srResponse =
            getSearchRetrieveResponse(sruURL,
                    localIdentifier,
                    "http://www.openarchives.org/OAI/2.0/#header",
            "xml");
        return getRecords(srResponse);
    }
    
    private Object getFullRecord(String sruURL,
            String oaiIdentifier,
            String metadataPrefix)
    throws SAXException, TransformerException, IOException,
    ParserConfigurationException {
        NodeList nodeList = getIdentifierRecords(sruURL, oaiIdentifier, metadataPrefix);
        if (nodeList != null) {
            HashMap hashMap = new HashMap();
            hashMap.put("header", getRecordData(nodeList.item(0)));
            String nativeRecordSchema = getRecordFactory().getCrosswalks().getNativeRecordSchema(metadataPrefix);
            String localIdentifier = getRecordFactory().fromOAIIdentifier(oaiIdentifier);
            Document srResponse =
                getSearchRetrieveResponse(sruURL,
                        localIdentifier,
                        nativeRecordSchema,
                "xml");
            nodeList = getRecords(srResponse);
            hashMap.put("metadata", getRecordData(nodeList.item(0)));
            return hashMap;
        }
        return null;
    }
    
    private Object getNativeMetadata(String localIdentifier, String metadataPrefix)
    throws TransformerException, SAXException, IOException,
    ParserConfigurationException {
        String nativeRecordSchema = getRecordFactory().getCrosswalks().getNativeRecordSchema(metadataPrefix);
        Document srResponse;
        srResponse =
            getSearchRetrieveResponse(sruURL,
                    localIdentifier,
                    nativeRecordSchema,
            "xml");
        NodeList nodeList = getRecords(srResponse);
        return getRecordData(nodeList.item(0));
    }
    
    public Map listRecords(String from,
            String until,
            String set,
            String metadataPrefix)
    throws
    BadArgumentException,
    CannotDisseminateFormatException,
    NoItemsMatchException,
    OAIInternalServerError {
        if (set != null
                && set.length() > 0
                && from.equals(toFinestFrom("0000-00-00"))
                && until.equals(toFinestUntil("9999-99-99"))) {
            from = null;
            until = null;
        }
        if (toFinestFrom("0000-00-00").equals(from)) {
            from = null;
        }
        if (toFinestUntil("9999-12-31").compareTo(until) <= 0) {
            until = null;
        }
        Map listRecordsMap = new HashMap();
        ArrayList recordsList = new ArrayList();
        Document srResponse;
        try {
            srResponse =
                getSearchRetrieveResponse(sruURL,
                        from,
                        until,
                        set,
                        "http://www.openarchives.org/OAI/2.0/#header",
                        1,
                        maxListSize,
                "xml");
        } catch (IOException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        } catch (SAXException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        }
        try {
            NodeList nodeList = getRecords(srResponse);
            if (nodeList != null) {
                RecordFactory recordFactory = getRecordFactory();
                String schemaURL = null;
                if (metadataPrefix != null) {
                    if ((schemaURL = getCrosswalks().getSchemaURL(metadataPrefix))
                            == null)
                        throw new CannotDisseminateFormatException(metadataPrefix);
                }
                for (int i = 0; i < nodeList.getLength(); ++i) {
                    Object rec = getRecordData(nodeList.item(i));
                    HashMap hashMap = new HashMap();
                    hashMap.put("header", rec);
                    String localIdentifier = getRecordFactory().getLocalIdentifier(hashMap);
                    try {
                        hashMap.put("metadata", getNativeMetadata(localIdentifier,
                                metadataPrefix));
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new OAIInternalServerError(e.getMessage());
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                        throw new OAIInternalServerError(e.getMessage());
                    } catch (SAXException e) {
                        e.printStackTrace();
                        throw new OAIInternalServerError(e.getMessage());
                    }
                    recordsList.add(
                            recordFactory.create(hashMap,
                                    schemaURL,
                                    metadataPrefix));
                }
                String nextRecordPosition = XPathAPI.eval(srResponse, "/srw:searchRetrieveResponse/srw:nextRecordPosition", xmlnsEl).str();
                if (nextRecordPosition != null && nextRecordPosition.length() > 0) {
                    String resultSetId = XPathAPI.eval(srResponse, "/srw:searchRetrieveResponse/srw:resultSetId", xmlnsEl).str();
                    StringBuffer resumptionToken = new StringBuffer();
                    resumptionToken.append(resultSetId);
                    resumptionToken.append(":");
                    resumptionToken.append(nextRecordPosition);
                    resumptionToken.append(":");
                    resumptionToken.append(metadataPrefix);
                    listRecordsMap.put(
                            "resumptionMap",
                            getResumptionMap(resumptionToken.toString()));
                }
            } else {
                throw new NoItemsMatchException();
            }
            listRecordsMap.put("records", recordsList.iterator());
            return listRecordsMap;
        } catch (TransformerException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        } catch (SAXException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        }
    }
    
    public Map listRecords(String resumptionToken)
    throws BadResumptionTokenException, OAIInternalServerError {
        StringTokenizer tokenizer = new StringTokenizer(resumptionToken, ":");
        String resultSetId;
        String nextRecordPosition;
        String metadataPrefix;
        
        try {
            resultSetId = tokenizer.nextToken();
            nextRecordPosition = tokenizer.nextToken();
            metadataPrefix = tokenizer.nextToken();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new BadResumptionTokenException();
        }
        Map listRecordsMap = new HashMap();
        ArrayList recordsList = new ArrayList();
        try {
            Document srResponse =
                getSearchRetrieveResponse(sruURL,
                        resultSetId,
                        nextRecordPosition,
                        "http://www.openarchives.org/OAI/2.0/#header",
                        maxListSize,
                "xml");
            NodeList nodeList = getRecords(srResponse);
            RecordFactory recordFactory = getRecordFactory();
            String schemaURL = null;
            if (metadataPrefix != null) {
                if ((schemaURL = getCrosswalks().getSchemaURL(metadataPrefix))
                        == null)
                    throw new CannotDisseminateFormatException(metadataPrefix);
            }
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Object rec = getRecordData(nodeList.item(i));
                HashMap hashMap = new HashMap();
                hashMap.put("header", rec);
                String localIdentifier = getRecordFactory().getLocalIdentifier(hashMap);
                hashMap.put("metadata", getNativeMetadata(localIdentifier,
                        metadataPrefix));
                recordsList.add(
                        recordFactory.create(hashMap,
                                schemaURL,
                                metadataPrefix));
            }
            nextRecordPosition = XPathAPI.eval(srResponse, "/srw:searchRetrieveResponse/srw:nextRecordPosition", xmlnsEl).str();
            if (nextRecordPosition != null && nextRecordPosition.length() > 0) {
                resultSetId = XPathAPI.eval(srResponse, "/srw:searchRetrieveResponse/srw:resultSetId", xmlnsEl).str();
                StringBuffer newResumptionToken = new StringBuffer();
                newResumptionToken.append(resultSetId);
                newResumptionToken.append(":");
                newResumptionToken.append(nextRecordPosition);
                newResumptionToken.append(":");
                newResumptionToken.append(metadataPrefix);
                listRecordsMap.put(
                        "resumptionMap",
                        getResumptionMap(newResumptionToken.toString()));
            }
            listRecordsMap.put("records", recordsList.iterator());
        } catch (Throwable e) {
            e.printStackTrace();
            throw new OAIInternalServerError("Database Failure");
        }
        return listRecordsMap;
    }
    
    private Document
    getSearchRetrieveResponse(String sruURL,
            String from,
            String until,
            String set,
            String recordSchema,
            int startRecord,
            int maximumRecords,
            String recordPacking)
    throws SAXException, ParserConfigurationException,
    UnsupportedEncodingException, IOException {
        StringBuffer query = new StringBuffer();
        if ((from != null && from.length() > 0)
                || (until != null && until.length() > 0)) {
            query.append("(");
            if (from != null && from.length() > 0) {
                query.append("oai.datestamp>=\"");
                query.append(normalizeTerm(from));
                query.append("\"");
            }
            if (until != null && from.length() > 0) {
                if (query.length() > 0)
                    query.append(" and ");
                query.append("oai.datestamp<=\"");
                query.append(normalizeTerm(until));
                query.append("\"");
            }
            query.append(")");
        }
        if (set != null && set.length() > 0) {
            if (query.length() > 0)
                query.append(" and ");
            query.append("oai.set=\"");
            query.append(normalizeTerm(set));
            query.append("\"");
        }
        StringBuffer request = new StringBuffer(sruURL);
        request.append("?operation=searchRetrieve&version=1.1&resultSetTTL=600&query=");
        request.append(URLEncoder.encode(query.toString(), "UTF-8"));
        request.append("&recordSchema=").append(URLEncoder.encode(recordSchema, "UTF-8"));
        request.append("&startRecord=").append(Integer.toString(startRecord));
        request.append("&maximumRecords=").append(Integer.toString(maximumRecords));
        request.append("&recordPacking=").append(recordPacking);
        request.append("&sortKeys=").append(URLEncoder.encode(sortKeys, "UTF-8"));
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(request.toString());
    }
    
    private Document
    getSearchRetrieveResponse(String sruURL,
            String resultSetId,
            String nextRecordPosition,
            String recordSchema,
            int maximumRecords,
            String recordPacking)
    throws SAXException, ParserConfigurationException,
    UnsupportedEncodingException, IOException {
        StringBuffer query = new StringBuffer();
        query.append("cql.resultSetId=");
        query.append(resultSetId);
        StringBuffer request = new StringBuffer(sruURL);
        request.append("?operation=searchRetrieve&resultSetTTL=600&version=1.1&query=");
        request.append(URLEncoder.encode(query.toString(), "UTF-8"));
        request.append("&recordSchema=").append(URLEncoder.encode(recordSchema, "UTF-8"));
        request.append("&startRecord=").append(nextRecordPosition);
        request.append("&maximumRecords=").append(Integer.toString(maximumRecords));
        request.append("&recordPacking=").append(recordPacking);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(request.toString());
    }
    
    private Document
    getSearchRetrieveResponse(String sruURL,
            String localIdentifier,
            String recordSchema,
            String recordPacking)
    throws SAXException, IOException, ParserConfigurationException,
    UnsupportedEncodingException {
        StringBuffer query = new StringBuffer();
        query.append("oai.identifier exact \"");
        query.append(localIdentifier);
        query.append("\"");
        
        StringBuffer request = new StringBuffer(sruURL);
        request.append("?operation=searchRetrieve&resultSetTTL=0&version=1.1&query=");
        request.append(URLEncoder.encode(query.toString(), "UTF-8"));
        request.append("&recordSchema=").append(URLEncoder.encode(recordSchema, "UTF-8"));
        request.append("&startRecord=1&maximumRecords=1");
        request.append("&recordPacking=").append(recordPacking);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(request.toString());
    }
    
    private Element getRecordData(Node record)
    throws TransformerException, SAXException, IOException, ParserConfigurationException {
        Node result = XPathAPI.selectSingleNode(record,
                "srw:recordData/*[1]",
                xmlnsEl);
        return toDocument((Element)result).getDocumentElement();
    }
    
    private Document toDocument(Element el)
    throws TransformerException, SAXException, IOException, ParserConfigurationException {
        DOMSource source = new DOMSource(el);
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        synchronized (transformer) {
            transformer.transform(source, result);
        }
        DocumentBuilder builder = getBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(sw.toString())));
        Element docEl = doc.getDocumentElement();
        if (docEl.getNamespaceURI() == null) {
            docEl.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "");
        }
        return doc;
    }
    
    private NodeList getRecords(Document srResponse)
    throws TransformerException {
        return XPathAPI.selectNodeList(srResponse, "/srw:searchRetrieveResponse/srw:records/srw:record", xmlnsEl);
    }

    public void close() {
    }
}

