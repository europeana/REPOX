
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

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * This class represents an ListRecords response on either the server or
 * on the client
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class ListRecords extends HarvesterVerb {
    /**
     * Mock object constructor (for unit testing purposes)
     */
    public ListRecords() {
        super();
    }
    
    /**
     * Client-side ListRecords verb constructor
     *
     * @param baseURL the baseURL of the server to be queried
     * @exception MalformedURLException the baseURL is bad
     * @exception SAXException the xml response is bad
     * @exception IOException an I/O error occurred
     */
    public ListRecords(String baseURL, String from, String until, String set, String metadataPrefix)
    		throws IOException, ParserConfigurationException, SAXException, TransformerException {
    	super(baseURL + "?verb=ListRecords" + (from != null ? "&from=" + from : "") + (until != null ? "&until=" + until : "")
        		+ (set != null ? "&set=" + URLEncoder.encode(set, "UTF-8") : "") + "&metadataPrefix=" + metadataPrefix);
    }
    
    /**
     * Client-side ListRecords verb constructor (resumptionToken version)
     * @param baseURL
     * @param resumptionToken
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     */
    public ListRecords(String baseURL, String resumptionToken) throws IOException, ParserConfigurationException, SAXException,
    		TransformerException {
        super(baseURL + "?verb=ListRecords" + "&resumptionToken=" + URLEncoder.encode(resumptionToken, "UTF-8"));
    }

    /**
     * Get the oai:resumptionToken from the response
     *
     * @return the oai:resumptionToken value
     * @throws TransformerException
     * @throws NoSuchFieldException
     */
    public String getResumptionToken() throws TransformerException, NoSuchFieldException {
        String namespace = getDefaultNamespace();
        if (namespace.contains(NAMESPACE_V2_0)) {
            return getSingleString("/oai20:OAI-PMH/oai20:ListRecords/oai20:resumptionToken");
        } else if (namespace.contains(NAMESPACE_V1_1)) {
            return getSingleString("/oai11_ListRecords:ListRecords/oai11_ListRecords:resumptionToken");
        } else {
            throw new NoSuchFieldException(namespace);
        }
    }
    
    /**
     * Get the oai:resumptionToken from the response
     * 
     * @return the oai:resumptionToken value
     * @throws TransformerException
     * @throws NoSuchFieldException
     */
    public String getResumptionTokenOld() throws TransformerException, NoSuchFieldException {
        String schemaLocation = getSchemaLocation();
        if (schemaLocation.indexOf(SCHEMA_LOCATION_V2_0) != -1) {
            return getSingleString("/oai20:OAI-PMH/oai20:ListRecords/oai20:resumptionToken");
        } else if (schemaLocation.indexOf(SCHEMA_LOCATION_V1_1_LIST_RECORDS) != -1) {
            return getSingleString("/oai11_ListRecords:ListRecords/oai11_ListRecords:resumptionToken");
        } else {
            throw new NoSuchFieldException(schemaLocation);
        }
    }

    /**
     * Get the total of number records from a specific set
     *
     * @return the oai:resumptionToken value
     * @throws TransformerException
     * @throws NoSuchFieldException
     */
    public int getRecordsNumber() throws TransformerException, NoSuchFieldException {
        String value2Return;
        String namespace = getDefaultNamespace();
        if (namespace.contains(NAMESPACE_V2_0)) {
            value2Return = getSingleString("/oai20:OAI-PMH/oai20:ListRecords/oai20:resumptionToken/@completeListSize");
        } else if (namespace.contains(NAMESPACE_V1_1)) {
            value2Return = getSingleString("/oai11_ListRecords:ListRecords/oai11_ListRecords:resumptionToken/@completeListSize");
        } else {
            throw new NoSuchFieldException(namespace);
        }

        if(!value2Return.isEmpty()){
            try{
                return Integer.valueOf(value2Return);
            }
            catch (NumberFormatException e){
                return 0;
            }
        }
        else{
            if(namespace.contains(NAMESPACE_V2_0)) {
                if(!getSingleString("/oai20:OAI-PMH/oai20:ListRecords/oai20:resumptionToken").equals("")){
                    return 0;
                }
                return getNodeList("//oai20:header").getLength();
            } else if (namespace.contains(NAMESPACE_V1_1)) {
                if(!getSingleString("/oai11_ListRecords:ListRecords/oai11_ListRecords:resumptionToken").equals("")){
                    return 0;
                }
                return getNodeList("//oai11_ListRecords:header").getLength();
            } else {
                throw new NoSuchFieldException(namespace);
            }
        }
    }

    /**
     * Get the total of number records from a specific set
     *
     * @return the oai:resumptionToken value
     * @throws TransformerException
     * @throws NoSuchFieldException
     */
    public int getRecordsNumberPerResponse() throws TransformerException, NoSuchFieldException {
        String namespace = getDefaultNamespace();
        if(namespace.contains(NAMESPACE_V2_0)) {
            return getNodeList("//oai20:header").getLength();
        } else if (namespace.contains(NAMESPACE_V1_1)) {
            return getNodeList("//oai11_ListRecords:header").getLength();
        } else {
            throw new NoSuchFieldException(namespace);
        }
    }
    
}
