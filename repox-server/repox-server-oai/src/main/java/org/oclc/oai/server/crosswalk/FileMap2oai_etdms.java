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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Convert native "item" to oai_etdms. In this case, the native "item" is
 * assumed to already be formatted as an OAI <record> element, with the possible
 * exception that multiple metadataFormats may be present in the <metadata>
 * element. The "crosswalk", merely involves pulling out the one that is
 * requested.
 */
public class FileMap2oai_etdms extends Crosswalk {
    /**
     * The constructor assigns the schemaLocation associated with this
     * crosswalk. Since the crosswalk is trivial in this case, no properties are
     * utilized.
     * 
     * @param properties
     *            properties that are needed to configure the crosswalk.
     */
    public FileMap2oai_etdms(Properties properties) {
        super("http://www.ndltd.org/standards/metadata/etdms/1.0/ http://www.ndltd.org/standards/metadata/etdms/1.0/etdms.xsd");
    }

    /**
     * Can this nativeItem be represented in ETDMS format?
     * 
     * @param nativeItem
     *            a record in native format
     * @return true if ETDMS format is possible, false otherwise.
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
        HashMap recordMap = (HashMap)nativeItem;
        try {
            String xmlRec = (new String((byte[])recordMap.get("recordBytes"), "UTF-8")).trim();
            if (xmlRec.startsWith("<?")) {
                int offset = xmlRec.indexOf("?>");
                xmlRec = xmlRec.substring(offset + 2);
            }
            return xmlRec;
        } catch (UnsupportedEncodingException e) {
            throw new CannotDisseminateFormatException("An error occurred");
        }
    }
}
