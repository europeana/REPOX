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
public class FileMap2oai_dc extends Crosswalk {
    private Transformer transformer = null;

    /**
     * The constructor assigns the schemaLocation associated with this
     * crosswalk. Since the crosswalk is trivial in this case, no properties are
     * utilized.
     * 
     * @param properties
     *            properties that are needed to configure the crosswalk.
     * @throws OAIInternalServerError 
     */
    public FileMap2oai_dc(Properties properties) throws OAIInternalServerError {
        super("http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
        try {
            String xsltName = properties.getProperty("FileMap2oai_dc.xsltName");
            TransformerFactory tFactory = TransformerFactory.newInstance();
            if (xsltName != null) {
                StreamSource xslSource = new StreamSource(new FileInputStream(xsltName));
                this.transformer = tFactory.newTransformer(xslSource);
            } else {
                this.transformer = tFactory.newTransformer();
                this.transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
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
     *                nativeItem doesn't support this format.
     */
    @Override
    public String createMetadata(Object nativeItem) throws CannotDisseminateFormatException {
        HashMap recordMap = (HashMap)nativeItem;
        try {
            String xmlRec = (new String((byte[])recordMap.get("recordBytes"), "UTF-8")).trim();
            if (xmlRec.startsWith("<?")) {
                int offset = xmlRec.indexOf("?>");
                xmlRec = xmlRec.substring(offset + 2);
            }
            StringReader stringReader = new StringReader(xmlRec);
            StreamSource streamSource = new StreamSource(stringReader);
            StringWriter stringWriter = new StringWriter();
            synchronized (transformer) {
                transformer.transform(streamSource, new StreamResult(stringWriter));
            }
            return stringWriter.toString();
        } catch (Exception e) {
            throw new CannotDisseminateFormatException(e.getMessage());
        }
    }
}
