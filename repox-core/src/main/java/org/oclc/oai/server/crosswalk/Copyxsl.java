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

import org.oclc.oai.server.verb.OAIInternalServerError;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Convert native "item" to xsl. In this case, the native "item" is assumed to
 * already be formatted as an OAI <record> element, with the possible exception
 * that multiple metadataFormats may be present in the <metadata> element. The
 * "crosswalk", merely involves pulling out the one that is requested.
 */
public class Copyxsl extends XSLTCrosswalk {
    //    private Transformer transformer = null;

    /**
     * The constructor assigns the schemaLocation associated with this
     * crosswalk. Since the crosswalk is trivial in this case, no properties are
     * utilized.
     * 
     * @param properties
     *            properties that are needed to configure the crosswalk.
     * @throws OAIInternalServerError 
     */
    public Copyxsl(Properties properties) throws OAIInternalServerError {
        super(properties, "http://www.w3.org/1999/XSL/Transform http://www.w3.org/1999/XSL/Transform.xsd", null);
        try {
            String xsltName = properties.getProperty("Copyxsl.xsltName");
            if (xsltName != null) {
                StreamSource xslSource = new StreamSource(new FileInputStream(xsltName));
                TransformerFactory tFactory = TransformerFactory.newInstance();
                this.transformer = tFactory.newTransformer(xslSource);
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
        ArrayList list = (ArrayList)nativeItem;
        return list.contains("xsl");
    }
}
