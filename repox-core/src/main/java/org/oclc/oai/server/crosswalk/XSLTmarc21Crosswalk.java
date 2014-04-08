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
import java.util.Properties;

/**
 * Convert native "item" to marc21. In this case, the native "item"
 * is assumed to already be formatted as an OAI <record> element,
 * with the possible exception that multiple metadataFormats may
 * be present in the <metadata> element. The "crosswalk", merely
 * involves pulling out the one that is requested.
 */
public class XSLTmarc21Crosswalk extends XSLTCrosswalk {
    private boolean debug = false;
//     private Transformer transformer = null;
    
    /**
     * The constructor assigns the schemaLocation associated with this crosswalk. Since
     * the crosswalk is trivial in this case, no properties are utilized.
     *
     * @param properties properties that are needed to configure the crosswalk.
     */
    public XSLTmarc21Crosswalk(Properties properties)
        throws OAIInternalServerError {
 	super(properties, "http://www.loc.gov/MARC21/slim http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd", null);
        String temp = properties.getProperty("XSLTmarc21Crosswalk.debug");
        if ("true".equals(temp)) debug = true;
        try {
            String xsltName = properties.getProperty("XSLTmarc21Crosswalk.xsltName");
            if (debug) System.out.println("XSLTmarc21Crosswalk.xsltName=" + xsltName);
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
}
