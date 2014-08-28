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
import org.oclc.oai.server.crosswalk.CrosswalkItem;
import org.oclc.oai.server.crosswalk.NodePassThruCrosswalk;
import org.oclc.oai.server.verb.OAIInternalServerError;
import org.w3c.dom.*;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

/**
 * NodeRecordFactory converts native XML "items" to "record" Strings.
 */
public class NodeRecordFactory extends RecordFactory {
    //     private static Logger logger = Logger.getLogger(NodeRecordFactory.class);
    //     static {
    // 	BasicConfigurator.configure();
    //     }
    private static Element                xmlnsEl = null;
    private static DocumentBuilderFactory factory = null;
    static {
        try {
            factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            Document xmlnsDoc = impl.createDocument("http://www.oclc.org/research/software/oai/harvester", "harvester:xmlnsDoc", null);
            xmlnsEl = xmlnsDoc.getDocumentElement();
            // 	    xmlnsEl.setAttributeNS("http://www.w3.org/2000/xmlns/",
            // 				   "xmlns:mx",
            // 				   "http://www.loc.gov/MARC21/slim");
            xmlnsEl.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            xmlnsEl.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:srw", "http://www.loc.gov/zing/srw/");
            xmlnsEl.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:oai", "http://www.openarchives.org/OAI/2.0/");
            xmlnsEl.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:explain", "http://explain.z3950.org/dtd/2.0/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param properties
     * @throws IllegalArgumentException
     */
    public NodeRecordFactory(Properties properties) throws IllegalArgumentException {
        this(properties, getCrosswalkMap(properties.getProperty("SRUOAICatalog.sruURL"), false));
    }

    /**
     * Construct an NodeRecordFactory capable of producing the Crosswalk(s)
     * specified in the properties file.
     * 
     * @param properties
     *            Contains information to configure the factory: specifically,
     *            the names of the crosswalk(s) supported
     * @exception IllegalArgumentException
     *                Something is wrong with the argument.
     */
    public NodeRecordFactory(Properties properties, HashMap crosswalkMap) throws IllegalArgumentException {
        super(crosswalkMap);
    }

    private static HashMap getCrosswalkMap(String sruURL, boolean enrich) throws IllegalArgumentException {
        try {
            DocumentBuilder parser = factory.newDocumentBuilder();
            System.out.println("sruURL=" + sruURL);
            Document explainDoc = parser.parse(sruURL);
            System.out.println("explainDoc=" + explainDoc);
            NodeList schemas = XPathAPI.selectNodeList(explainDoc, "/srw:explainResponse/srw:record/srw:recordData/explain:explain/explain:schemaInfo/explain:schema", xmlnsEl);

            // Load the formats the repository supports directly
            HashMap crosswalkMap = new HashMap();
            for (int i = 0; i < schemas.getLength(); ++i) {
                Object[] crosswalkItem = crosswalkItemFactory(schemas.item(i));
                for (Object aCrosswalkItem : crosswalkItem) {
                    CrosswalkItem currentItem = (CrosswalkItem)aCrosswalkItem;
                    // 		logger.debug(currentItem.toString());
                    Object key = currentItem.getMetadataPrefix();
                    CrosswalkItem storedItem = (CrosswalkItem)crosswalkMap.get(key);
                    if (storedItem == null || (currentItem.getRank() < storedItem.getRank())) {
                        crosswalkMap.put(key, currentItem);
                    }
                }
            }

            HashMap moreCrosswalkMap = new HashMap();
            // 	    if (enrich) {
            // 		System.out.println("enriching...");
            // 		// Enrich the crosswalkMap with formats we can create w/crosswalks
            // 		Iterator oldCrosswalkItems = crosswalkMap.values().iterator();
            // 		while (oldCrosswalkItems.hasNext()) {
            // 		    CrosswalkItem oldCrosswalkItem = (CrosswalkItem)oldCrosswalkItems.next();
            // 		    String oldSchema = oldCrosswalkItem.getDestSchema();
            // 		    String nativeRecordSchema = oldCrosswalkItem.getNativeRecordSchema();
            // 		    moreCrosswalkMap.putAll(getCrosswalkMap(oldSchema,
            // 							    nativeRecordSchema,
            // 							    crosswalkMap,
            // 							    moreCrosswalkMap));
            // 		}
            // 	    }
            // Now, combine the lists with the originals taking precidence
            moreCrosswalkMap.putAll(crosswalkMap);
            return moreCrosswalkMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(sruURL);
        }
    }

    //    private static HashMap getCrosswalkMap(String oldSchema,
    //				    String nativeRecordSchema,
    //				    HashMap crosswalkMap,
    //				    HashMap moreCrosswalkMap)
    //	throws OAIInternalServerError, SAXException, IOException,
    //	       TransformerException {
    //	HashMap hashMap = new HashMap();
    //	return hashMap;
    //    }

    //     public ArrayList querySourceSchema(String nativeRecordSchema,
    // 				       String sourceSchema)
    // 	throws OAIInternalServerError, SAXException, IOException,
    // 	       TransformerException {
    // 	try {
    // 	    ArrayList list = new ArrayList();
    // 	    StringBuffer sb = new StringBuffer(getCentralERRoLURL());
    // 	    sb.append("/schemaTrans.oclc.org.sru?query=schemaTrans.source+%3D+%22");
    // 	    sb.append(URLEncoder.encode(sourceSchema, "UTF-8"));
    // 	    sb.append("%22&version=1.1&operation=searchRetrieve&maximumRecords=10&startRecord=1&resultSetTTL=0&recordPacking=xml&recordXPath=&sortKeys=");
    // 	    Document sruDoc = parser.parse(sb.toString());
    // 	    NodeList nodeList = XPathAPI.selectNodeList(sruDoc, "/srw:searchRetrieveResponse/srw:records/srw:record/srw:recordData/mets:mets[mets:structMap/mets:div/mets:div[@LABEL='target']/mets:div[@LABEL='Application']/mets:div[@LABEL='infoURI']]", xmlnsEl);

    // 	    for (int i=0; i<nodeList.getLength(); ++i) {
    // 		Node recordNode = nodeList.item(i);
    // 		String infoURIFID = XPathAPI.eval(recordNode, "mets:structMap/mets:div/mets:div[@LABEL='target']/mets:div[@LABEL='Application']/mets:div[@LABEL='infoURI']/mets:fptr/@FILEID", xmlnsEl).str();
    //  		String infoURI = XPathAPI.eval(recordNode, "mets:fileSec/mets:fileGrp[@USE='Application']/mets:file[@ID='"+infoURIFID+"']/mets:FLocat/@xlink:href", xmlnsEl).str();
    // 		String namespaceFID = XPathAPI.eval(recordNode, "mets:structMap/mets:div/mets:div[@LABEL='target']/mets:div[@LABEL='Application']/mets:div[@LABEL='namespace']/mets:fptr/@FILEID", xmlnsEl).str();
    // 		String namespace = XPathAPI.eval(recordNode, "mets:fileSec/mets:fileGrp[@USE='Application']/mets:file[@ID='"+namespaceFID+"']/mets:FLocat/@xlink:href", xmlnsEl).str();
    // 		String schemaFID = XPathAPI.eval(recordNode, "mets:structMap/mets:div/mets:div[@LABEL='target']/mets:div[@LABEL='Application']/mets:fptr/@FILEID", xmlnsEl).str();
    // 		String schema = XPathAPI.eval(recordNode, "mets:fileSec/mets:fileGrp[@USE='Application']/mets:file[@ID='"+schemaFID+"']/mets:FLocat/@xlink:href", xmlnsEl).str();
    // 		String crosswalkFID = XPathAPI.eval(recordNode, "mets:structMap/mets:div/mets:div[@LABEL='crosswalk']/mets:div[@LABEL='Application']/mets:fptr/@FILEID", xmlnsEl).str();
    // 		String crosswalk = XPathAPI.eval(recordNode, "mets:fileSec/mets:fileGrp[@USE='Application']/mets:file[@ID='"+crosswalkFID+"']/mets:FLocat/@xlink:href", xmlnsEl).str();

    // 		String metadataPrefix = infoURI.substring("info:ofi/fmt:xml:xsd:".length());
    // 		list.add(new CrosswalkItem(nativeRecordSchema,
    // 					   metadataPrefix,
    // 					   schema,
    // 					   namespace,
    // 					   XSLCrosswalk.class,
    // 					   crosswalk
    // 					   ));
    // 	    }
    // 	    return list;
    // 	} catch (UnsupportedEncodingException e) {
    // 	    e.printStackTrace();
    // 	    throw new OAIInternalServerError(e.getMessage());
    // 	}
    //     }

    /**
     * @param explainSchemaNode
     * @return
     * @throws TransformerException
     * @throws OAIInternalServerError
     */
    public static Object[] crosswalkItemFactory(Node explainSchemaNode) throws TransformerException, OAIInternalServerError {
        ArrayList crosswalkItemList = new ArrayList();
        String nativeRecordSchema = XPathAPI.eval(explainSchemaNode, "@identifier", xmlnsEl).str();
        String metadataPrefix = XPathAPI.eval(explainSchemaNode, "@name", xmlnsEl).str();
        String schema = XPathAPI.eval(explainSchemaNode, "@location", xmlnsEl).str();
        String metadataNamespace = null;
        try {
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document schemaDoc = parser.parse(schema);
            metadataNamespace = XPathAPI.eval(schemaDoc, "/xsd:schema/@targetNamespace", xmlnsEl).str();
        } catch (SAXParseException e) {
            // 	    logger.debug("assume unreadable schema is for empty namespace", e);
            metadataNamespace = "";
        } catch (Exception e) {
            // 	    logger.warn("Problem obtaining namespace", e);
            System.out.println("Failed to get schema: " + schema);
            e.printStackTrace();
            metadataNamespace = "";
        }
        CrosswalkItem crosswalkItem = new CrosswalkItem(nativeRecordSchema, metadataPrefix, schema, metadataNamespace, NodePassThruCrosswalk.class);
        crosswalkItemList.add(crosswalkItem);
        // 	logger.debug("Service_srw2oai.crosswalkItemFactory: metadataPrefix=" + metadataPrefix);
        // 	logger.debug("Service_srw2oai.crosswalkItemFactory: nativeRecordSchema=" + crosswalkItem.getNativeRecordSchema());
        // 	if (nativeRecordSchema.equals("info:srw/schema/1/marcxml-v1.1")) {
        // 	}
        return crosswalkItemList.toArray();
    }

    /**
     * Utility method to parse the 'local identifier' from the OAI identifier
     * 
     * @param identifier
     *            OAI identifier (e.g. oai:oaicat.oclc.org:ID/12345)
     * @return local identifier (e.g. ID/12345).
     */
    @Override
    public String fromOAIIdentifier(String identifier) {
        return identifier;
    }

    /**
     * Construct an OAI identifier from the native item
     * 
     * @param nativeItem
     *            native Item object
     * @return OAI identifier
     */
    @Override
    public String getOAIIdentifier(Object nativeItem) {
        return getLocalIdentifier(nativeItem);
    }

    /**
     * Extract the local identifier from the native item
     * 
     * @param nativeItem
     *            native Item object
     * @return local identifier
     */
    @Override
    public String getLocalIdentifier(Object nativeItem) {
        //  	logger.debug("getLocalIdentifier: nativeItem=" + nativeItem);
        HashMap hashMap = (HashMap)nativeItem;
        Node dataNode = (Node)hashMap.get("header");
        // 	MessageElement messageElement = dataNode.get_any()[0];
        //	StringBuffer sb = new StringBuffer();
        try {
            // 	    Element recordEl = messageElement.getAsDOM();
            Element recordEl = (Element)dataNode;
            // 	    logger.debug("NodeRecordFactory.getLocalIdentifier: recordEl=" + recordEl);
            Node identifierNode = XPathAPI.selectSingleNode(recordEl, "/oai:header/oai:identifier", xmlnsEl);
            // 	    logger.debug("NodeRecordFactory.getLocalIdentifier: identifierNode=" + identifierNode);
            if (identifierNode != null) { return XPathAPI.eval(identifierNode, "string()").str(); }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get the datestamp from the item
     * 
     * @param nativeItem
     *            a native item presumably containing a datestamp somewhere
     * @return a String containing the datestamp for the item
     * @throws IllegalArgumentException
     *             Something is wrong with the argument.
     */
    @Override
    public String getDatestamp(Object nativeItem) throws IllegalArgumentException {
        HashMap hashMap = (HashMap)nativeItem;
        Node dataNode = (Node)hashMap.get("header");
        // 	MessageElement messageElement = dataNode.get_any()[0];
        try {
            // 	    Element recordEl = messageElement.getAsDOM();
            Element recordEl = (Element)dataNode;
            Node datestampNode = XPathAPI.selectSingleNode(recordEl, "/oai:header/oai:datestamp", xmlnsEl);
            if (datestampNode != null) { return XPathAPI.eval(datestampNode, "string()").str(); }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get the setspec from the item
     * 
     * @param nativeItem
     *            a native item presumably containing a setspec somewhere
     * @return a String containing the setspec for the item
     * @throws IllegalArgumentException
     *             Something is wrong with the argument.
     */
    @Override
    public Iterator getSetSpecs(Object nativeItem) throws IllegalArgumentException {
        //         List setSpecs = (List)((HashMap)nativeItem).get("setSpecs");
        //         if (setSpecs != null) 
        //             return setSpecs.iterator();
        //         else
        return null;
    }

    /**
     * Get the about elements from the item
     * 
     * @param nativeItem
     *            a native item presumably containing about information
     *            somewhere
     * @return a Iterator of Strings containing &lt;about&gt;s for the item
     * @throws IllegalArgumentException
     *             Something is wrong with the argument.
     */
    @Override
    public Iterator getAbouts(Object nativeItem) throws IllegalArgumentException {
        return null;
    }

    /**
     * Is the record deleted?
     * 
     * @param nativeItem
     *            a native item presumably containing a possible delete
     *            indicator
     * @return true if record is deleted, false if not
     * @throws IllegalArgumentException
     *             Something is wrong with the argument.
     */
    @Override
    public boolean isDeleted(Object nativeItem) throws IllegalArgumentException {
        return false;
    }

    /**
     * Allows classes that implement RecordFactory to override the default
     * create() method. This is useful, for example, if the entire
     * &lt;record&gt; is already packaged as the native record. Return null if
     * you want the default handler to create it by calling the methods above
     * individually.
     * 
     * @param nativeItem
     *            the native record
     * @param schemaLocation
     *            the schemaURL desired for the response
     * @param metadataPrefix
     *            from the request
     * @return a String containing the OAI &lt;record&gt; or null if the default
     *         method should be used.
     */
    @Override
    public String quickCreate(Object nativeItem, String schemaLocation, String metadataPrefix) {
        return null;
    }
}
