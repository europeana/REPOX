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
package org.oclc.oai.server.crosswalk;

import org.oclc.oai.server.verb.CannotDisseminateFormatException;
import org.oclc.oai.server.verb.OAIInternalServerError;
import org.oclc.oai.util.OAIUtil;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Properties;

/**
 * Convert native "item" to oai_dc. In this case, the native "item" is assumed
 * to already be formatted as an OAI <record> element, with the possible
 * exception that multiple metadataFormats may be present in the <metadata>
 * element. The "crosswalk", merely involves pulling out the one that is
 * requested.
 */
public class XSLTCrosswalk extends Crosswalk {
    private boolean       debug       = false;
    protected Transformer transformer = null;

    /**
     * Creates a new instance of this class.
     * 
     * @param properties
     * @throws OAIInternalServerError
     */
    public XSLTCrosswalk(Properties properties) throws OAIInternalServerError {
        this(properties, properties.getProperty("XSLTCrosswalk.schemaLocation"), (String)null);
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param properties
     * @param schemaLocation
     * @param contentType
     * @throws OAIInternalServerError
     */
    public XSLTCrosswalk(Properties properties, String schemaLocation, String contentType) throws OAIInternalServerError {
        this(properties, schemaLocation, contentType, (String)null);
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param properties
     * @param schemaLocation
     * @param contentType
     * @param docType
     * @throws OAIInternalServerError
     */
    public XSLTCrosswalk(Properties properties, String schemaLocation, String contentType, String docType) throws OAIInternalServerError {
        this(properties, schemaLocation, contentType, docType, (String)null);
    }

    /**
     * The constructor assigns the schemaLocation associated with this
     * crosswalk. Since the crosswalk is trivial in this case, no properties are
     * utilized.
     * 
     * @param properties
     *            properties that are needed to configure the crosswalk.
     * @param schemaLocation
     * @param contentType
     * @param docType
     * @param encoding
     * @throws OAIInternalServerError
     */
    public XSLTCrosswalk(Properties properties, String schemaLocation, String contentType, String docType, String encoding) throws OAIInternalServerError {
        // 	super("http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
        super(schemaLocation, contentType, docType, encoding);
        if ("true".equals(properties.getProperty("XSLTCrosswalk.debug"))) debug = true;
        try {
            String xsltName = properties.getProperty("XSLTCrosswalk.xsltName");
            if (xsltName != null) {
                StreamSource xslSource = new StreamSource(new FileInputStream(xsltName));
                TransformerFactory tFactory = TransformerFactory.newInstance();
                this.transformer = tFactory.newTransformer(xslSource);
                this.transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                this.transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
                this.transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        }
    }

    /**
     * Can this nativeItem be represented in DC format?
     * 
     * @param nativeItem
     *            a record in native format
     * @return true if DC format is possible, false otherwise.
     */
    @Override
    public boolean isAvailableFor(Object nativeItem) {
        return true;
    }

    /**
     * Perform the actual crosswalk.
     * 
     * @param nativeItem
     *            the native "item". In this case, it is already formatted as an
     *            OAI <record> element, with the possible exception that
     *            multiple metadataFormats are present in the <metadata>
     *            element.
     * @return a String containing the FileMap to be stored within the
     *         <metadata> element.
     * @throws CannotDisseminateFormatException
     *             nativeItem doesn't support this format.
     */
    @Override
    public String createMetadata(Object nativeItem) throws CannotDisseminateFormatException {
        try {
            String xmlRec = null;
            if (nativeItem instanceof HashMap) {
                HashMap recordMap = (HashMap)nativeItem;
                xmlRec = (String)recordMap.get("recordString");
                if (xmlRec == null) {
                    xmlRec = new String((byte[])recordMap.get("recordBytes"), "UTF-8");
                }
                xmlRec = xmlRec.trim();
            } else if (nativeItem instanceof String) {
                xmlRec = (String)nativeItem;
            } else if (nativeItem instanceof Document) {
                xmlRec = OAIUtil.toString((Document)nativeItem);
            } else {
                throw new Exception("Unrecognized nativeItem");
            }

            if (debug) {
                System.out.println("XSLTCrosswalk.createMetadata: xmlRec=" + xmlRec);
            }
            if (xmlRec.startsWith("<?")) {
                int offset = xmlRec.indexOf("?>");
                xmlRec = xmlRec.substring(offset + 2);
            }
            if (debug) {
                System.out.println("XSLTCrosswalk.createMetadata: transformer=" + transformer);
            }
            if (transformer != null) {
                StringReader stringReader = new StringReader(xmlRec);
                StreamSource streamSource = new StreamSource(stringReader);
                StringWriter stringWriter = new StringWriter();
                synchronized (this) {
                    transformer.transform(streamSource, new StreamResult(stringWriter));
                }
                if (debug) {
                    System.out.println("XSLTCrosswalk.createMetadata: return=" + stringWriter.toString());
                }
                return stringWriter.toString();
            } else {
                return xmlRec;
            }
        } catch (Exception e) {
            if (debug) {
                e.printStackTrace();
            }
            throw new CannotDisseminateFormatException(e.getMessage());
        }
    }
}
