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
 * Convert native "item" to oai_dc. In this case, the native "item"
 * is assumed to already be formatted as an OAI <record> element,
 * with the possible exception that multiple metadataFormats may
 * be present in the <metadata> element. The "crosswalk", merely
 * involves pulling out the one that is requested.
 */
public class ExtendedJDBC2oai_dc extends Crosswalk {
    private String dcTitleLabel = null;
    private String dcCreatorLabel = null;
    private String dcSubjectLabel = null;
    private String dcDescriptionLabel = null;
    private String dcPublisherLabel = null;
    private String dcContributorLabel = null;
    private String dcDateLabel = null;
    private String dcTypeLabel = null;
    private String dcFormatLabel = null;
    private String dcIdentifierLabel = null;
    private String dcSourceLabel = null;
    private String dcLanguageLabel = null;
    private String dcRelationLabel = null;
    private String dcCoverageLabel = null;
    private String dcRightsLabel = null;
    
    /**
     * The constructor assigns the schemaLocation associated with this crosswalk. Since
     * the crosswalk is trivial in this case, no properties are utilized.
     *
     * @param properties properties that are needed to configure the crosswalk.
     */
    public ExtendedJDBC2oai_dc(Properties properties) {
	super("http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
	dcTitleLabel = (String)properties.get("ExtendedJDBC2oai_dc.dcTitleLabel");
	dcCreatorLabel = (String)properties.get("ExtendedJDBC2oai_dc.dcCreatorLabel");
	dcSubjectLabel = (String)properties.get("ExtendedJDBC2oai_dc.dcSubjectLabel");
	dcDescriptionLabel = (String)properties.get("ExtendedJDBC2oai_dc.dcDescriptionLabel");
	dcPublisherLabel = (String)properties.get("ExtendedJDBC2oai_dc.dcPublisherLabel");
	dcContributorLabel = (String)properties.get("ExtendedJDBC2oai_dc.dcContributorLabel");
	dcDateLabel = (String)properties.get("ExtendedJDBC2oai_dc.dcDateLabel");
	dcTypeLabel = (String)properties.get("ExtendedJDBC2oai_dc.dcTypeLabel");
	dcFormatLabel = (String)properties.get("ExtendedJDBC2oai_dc.dcFormatLabel");
	dcIdentifierLabel = (String)properties.get("ExtendedJDBC2oai_dc.dcIdentifierLabel");
	dcSourceLabel = (String)properties.get("ExtendedJDBC2oai_dc.dcSourceLabel");
	dcLanguageLabel = (String)properties.get("ExtendedJDBC2oai_dc.dcLanguageLabel");
	dcRelationLabel = (String)properties.get("ExtendedJDBC2oai_dc.dcRelationLabel");
	dcCoverageLabel = (String)properties.get("ExtendedJDBC2oai_dc.dcCoverageLabel");
	dcRightsLabel = (String)properties.get("ExtendedJDBC2oai_dc.dcRightsLabel");
    }

    /**
     * Can this nativeItem be represented in DC format?
     * @param nativeItem a record in native format
     * @return true if DC format is possible, false otherwise.
     */
    public boolean isAvailableFor(Object nativeItem) {
	return true; // all records must support oai_dc according to the OAI spec.
    }

    /**
     * Perform the actual crosswalk.
     *
     * @param nativeItem the native "item". In this case, it is
     * already formatted as an OAI <record> element, with the
     * possible exception that multiple metadataFormats are
     * present in the <metadata> element.
     * @return a String containing the XML to be stored within the <metadata> element.
     */
    public String createMetadata(Object nativeItem) {
	Object dcTitle = null;
	Object dcCreator = null;
	Object dcSubject = null;
	Object dcDescription = null;
	Object dcPublisher = null;
	Object dcContributor = null;
	Object dcDate = null;
	Object dcType = null;
	Object dcFormat = null;
	Object dcIdentifier = null;
	Object dcSource = null;
	Object dcLanguage = null;
	Object dcRelation = null;
	Object dcCoverage = null;
	Object dcRights = null;
	HashMap coreTable = (HashMap)((HashMap)nativeItem).get("coreResult");
	StringBuffer sb = new StringBuffer();
	sb.append("<oai_dc:dc xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\""
		  + getSchemaLocation()
		  + "\">");


	if (dcTitleLabel != null
	    && (dcTitle = coreTable.get(dcTitleLabel)) != null) {
	    sb.append("<dc:title>");
	    sb.append(OAIUtil.xmlEncode(dcTitle.toString()));
	    sb.append("</dc:title>");
	}
	
	if (dcCreatorLabel != null
	    && (dcCreator = coreTable.get(dcCreatorLabel)) != null) {
	    sb.append("<dc:creator>");
	    sb.append(OAIUtil.xmlEncode(dcCreator.toString()));
	    sb.append("</dc:creator>");
	}
	
	if (dcSubjectLabel != null
	    && (dcSubject = coreTable.get(dcSubjectLabel)) != null) {
	    sb.append("<dc:subject>");
	    sb.append(OAIUtil.xmlEncode(dcSubject.toString()));
	    sb.append("</dc:subject>");
	}
	
	if (dcDescriptionLabel != null
	    && (dcDescription = coreTable.get(dcDescriptionLabel)) != null) {
	    sb.append("<dc:description>");
	    sb.append(OAIUtil.xmlEncode(dcDescription.toString()));
	    sb.append("</dc:description>");
	}
	
	if (dcPublisherLabel != null
	    && (dcPublisher = coreTable.get(dcPublisherLabel)) != null) {
	    sb.append("<dc:publisher>");
	    sb.append(OAIUtil.xmlEncode(dcPublisher.toString()));
	    sb.append("</dc:publisher>");
	}
	
	if (dcContributorLabel != null
	    && (dcContributor = coreTable.get(dcContributorLabel)) != null) {
	    sb.append("<dc:contributor>");
	    sb.append(OAIUtil.xmlEncode(dcContributor.toString()));
	    sb.append("</dc:contributor>");
	}
	
	if (dcDateLabel != null
	    && (dcDate = coreTable.get(dcDateLabel)) != null) {
	    sb.append("<dc:date>");
	    sb.append(OAIUtil.xmlEncode(dcDate.toString()));
	    sb.append("</dc:date>");
	}
	
	if (dcTypeLabel != null
	    && (dcType = coreTable.get(dcTypeLabel)) != null) {
	    sb.append("<dc:type>");
	    sb.append(OAIUtil.xmlEncode(dcType.toString()));
	    sb.append("</dc:type>");
	}
	
	if (dcFormatLabel != null
	    && (dcFormat = coreTable.get(dcFormatLabel)) != null) {
	    sb.append("<dc:format>");
	    sb.append(OAIUtil.xmlEncode(dcFormat.toString()));
	    sb.append("</dc:format>");
	}
	
	if (dcIdentifierLabel != null
	    && (dcIdentifier = coreTable.get(dcIdentifierLabel)) != null) {
	    sb.append("<dc:identifier>");
	    sb.append(OAIUtil.xmlEncode(dcIdentifier.toString()));
	    sb.append("</dc:identifier>");
	}
	
	if (dcSourceLabel != null
	    && (dcSource = coreTable.get(dcSourceLabel)) != null) {
	    sb.append("<dc:source>");
	    sb.append(OAIUtil.xmlEncode(dcSource.toString()));
	    sb.append("</dc:source>");
	}
	
	if (dcLanguageLabel != null
	    && (dcLanguage = coreTable.get(dcLanguageLabel)) != null) {
	    sb.append("<dc:language>");
	    sb.append(OAIUtil.xmlEncode(dcLanguage.toString()));
	    sb.append("</dc:language>");
	}
	
	if (dcRelationLabel != null
	    && (dcRelation = coreTable.get(dcRelationLabel)) != null) {
	    sb.append("<dc:relation>");
	    sb.append(OAIUtil.xmlEncode(dcRelation.toString()));
	    sb.append("</dc:relation>");
	}
	
	if (dcCoverageLabel != null
	    && (dcCoverage = coreTable.get(dcCoverageLabel)) != null) {
	    sb.append("<dc:coverage>");
	    sb.append(OAIUtil.xmlEncode(dcCoverage.toString()));
	    sb.append("</dc:coverage>");
	}
	
	if (dcRightsLabel != null
	    && (dcRights = coreTable.get(dcRightsLabel)) != null) {
	    sb.append("<dc:rights>");
	    sb.append(OAIUtil.xmlEncode(dcRights.toString()));
	    sb.append("</dc:rights>");
	}
	
	sb.append("</oai_dc:dc>");
	return sb.toString();
    }
}
