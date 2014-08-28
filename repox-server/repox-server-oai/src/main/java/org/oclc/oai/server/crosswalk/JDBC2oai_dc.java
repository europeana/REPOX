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

import org.oclc.oai.util.OAIUtil;

import java.util.HashMap;
import java.util.Properties;

/**
 * Convert native "item" to oai_dc. In this case, the native "item" is assumed
 * to already be formatted as an OAI <record> element, with the possible
 * exception that multiple metadataFormats may be present in the <metadata>
 * element. The "crosswalk", merely involves pulling out the one that is
 * requested.
 */
public class JDBC2oai_dc extends Crosswalk {
    private String separator          = null;
    private String dcTitleLabel       = null;
    private String dcCreatorLabel     = null;
    private String dcSubjectLabel     = null;
    private String dcDescriptionLabel = null;
    private String dcPublisherLabel   = null;
    private String dcContributorLabel = null;
    private String dcDateLabel        = null;
    private String dcTypeLabel        = null;
    private String dcFormatLabel      = null;
    private String dcIdentifierLabel  = null;
    private String dcSourceLabel      = null;
    private String dcLanguageLabel    = null;
    private String dcRelationLabel    = null;
    private String dcCoverageLabel    = null;
    private String dcRightsLabel      = null;

    /**
     * The constructor assigns the schemaLocation associated with this
     * crosswalk. Since the crosswalk is trivial in this case, no properties are
     * utilized.
     * 
     * @param properties
     *            properties that are needed to configure the crosswalk.
     */
    public JDBC2oai_dc(Properties properties) {
        super("http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
        dcTitleLabel = (String)properties.get("JDBC2oai_dc.dcTitleLabel");
        dcCreatorLabel = (String)properties.get("JDBC2oai_dc.dcCreatorLabel");
        dcSubjectLabel = (String)properties.get("JDBC2oai_dc.dcSubjectLabel");
        dcDescriptionLabel = (String)properties.get("JDBC2oai_dc.dcDescriptionLabel");
        dcPublisherLabel = (String)properties.get("JDBC2oai_dc.dcPublisherLabel");
        dcContributorLabel = (String)properties.get("JDBC2oai_dc.dcContributorLabel");
        dcDateLabel = (String)properties.get("JDBC2oai_dc.dcDateLabel");
        dcTypeLabel = (String)properties.get("JDBC2oai_dc.dcTypeLabel");
        dcFormatLabel = (String)properties.get("JDBC2oai_dc.dcFormatLabel");
        dcIdentifierLabel = (String)properties.get("JDBC2oai_dc.dcIdentifierLabel");
        dcSourceLabel = (String)properties.get("JDBC2oai_dc.dcSourceLabel");
        dcLanguageLabel = (String)properties.get("JDBC2oai_dc.dcLanguageLabel");
        dcRelationLabel = (String)properties.get("JDBC2oai_dc.dcRelationLabel");
        dcCoverageLabel = (String)properties.get("JDBC2oai_dc.dcCoverageLabel");
        dcRightsLabel = (String)properties.get("JDBC2oai_dc.dcRightsLabel");
        separator = (String)properties.get("JDBC2oai_dc.separator");
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
        return true; // all records must support oai_dc according to the OAI spec.
    }

    /**
     * Perform the actual crosswalk.
     * 
     * @param nativeItem
     *            the native "item". In this case, it is already formatted as an
     *            OAI <record> element, with the possible exception that
     *            multiple metadataFormats are present in the <metadata>
     *            element.
     * @return a String containing the XML to be stored within the <metadata>
     *         element.
     */
    @Override
    public String createMetadata(Object nativeItem) {
        HashMap table = (HashMap)nativeItem;
        StringBuffer sb = new StringBuffer();
        sb.append("<oai_dc:dc xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"" + getSchemaLocation() + "\">\n");

        sb.append(getElements(table, dcTitleLabel, "dc:title"));
        sb.append(getElements(table, dcCreatorLabel, "dc:creator"));
        sb.append(getElements(table, dcSubjectLabel, "dc:subject"));
        sb.append(getElements(table, dcDescriptionLabel, "dc:description"));
        sb.append(getElements(table, dcPublisherLabel, "dc:publisher"));
        sb.append(getElements(table, dcContributorLabel, "dc:contributor"));
        sb.append(getElements(table, dcDateLabel, "dc:date"));
        sb.append(getElements(table, dcTypeLabel, "dc:type"));
        sb.append(getElements(table, dcFormatLabel, "dc:format"));
        sb.append(getElements(table, dcIdentifierLabel, "dc:identifier"));
        sb.append(getElements(table, dcSourceLabel, "dc:source"));
        sb.append(getElements(table, dcLanguageLabel, "dc:language"));
        sb.append(getElements(table, dcRelationLabel, "dc:relation"));
        sb.append(getElements(table, dcCoverageLabel, "dc:coverage"));
        sb.append(getElements(table, dcRightsLabel, "dc:rights"));

        sb.append("</oai_dc:dc>\n");
        return sb.toString();
    }

    private String getElements(HashMap table, String jdbcLabel, String elementLabel) {
        StringBuffer sb = new StringBuffer();
        Object jdbcObject;
        if (jdbcLabel != null && (jdbcObject = table.get(jdbcLabel)) != null && jdbcObject.toString().length() > 0) {
            if (separator != null && separator.length() > 0) {
                String[] values = jdbcObject.toString().split(separator);
                for (String value : values) {
                    sb.append("<").append(elementLabel).append(">");
                    sb.append(OAIUtil.xmlEncode(value));
                    sb.append("</").append(elementLabel).append(">\n");
                }
            } else {
                sb.append("<").append(elementLabel).append(">");
                sb.append(OAIUtil.xmlEncode(jdbcObject.toString()));
                sb.append("</").append(elementLabel).append(">\n");
            }
        }
        return sb.toString();
    }
}
