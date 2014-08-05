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

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Converts a native "item" to an OAI metadataFormat.
 */
public abstract class Crosswalk {
    /**
     * The schemaLocation supported by this crosswalk
     */
    private String schemaLocation;
    private String contentType;
    private String docType;
    private String encoding;

    /**
     * Constructor
     *
     * @param schemaLocation the schemaLocation supported by this crosswalk
     */
    public Crosswalk(String schemaLocation) {
        this(schemaLocation, (String)null);
    }

    /**
     * Constructor
     *
     * @param schemaLocation the schemaLocation supported by this crosswalk
     * @param contentType 
     */
    public Crosswalk(String schemaLocation, String contentType) {
        this(schemaLocation, contentType, (String)null);
    }

    /**
     * Creates a new instance of this class.
     * @param schemaLocation
     * @param contentType
     * @param docType
     */
    public Crosswalk(String schemaLocation, String contentType, String docType) {
        this(schemaLocation, contentType, docType, (String)null);
    }
    /**
     * Constructor
     *
     * @param schemaLocation the schemaLocation supported by this crosswalk
     * @param contentType 
     * @param docType 
     * @param encoding 
     */
    public Crosswalk(String schemaLocation, String contentType, String docType, String encoding) {
        this.schemaLocation = schemaLocation;
        if (contentType == null)
            contentType = "text/xml; charset=UTF-8";
        this.contentType = contentType;
        this.docType = docType;
        this.encoding = encoding;
    }

    /**
     * returns the schemaLocation
     *
     * @return the schemaLocation
     */
    public String getSchemaLocation() { return schemaLocation; }

    /**
     * @return contentType
     */
    public String getContentType() { return contentType; }

    /**
     * @return docType
     */
    public String getDocType() { return docType; }

    /**
     * @return encoding
     */
    public String getEncoding() { return encoding; }

    /**
     * parse the schemaURL from the schemaLocation
     *
     * @return the schemaURL portion of the schemaLocation
     */
    public String getSchemaURL() {
	StringTokenizer tokenizer = new StringTokenizer(schemaLocation, " ");
	String temp = tokenizer.nextToken();
	try {
	    return tokenizer.nextToken();
	} catch (NoSuchElementException e) {
	    // no namespace for the format
	    return temp;
	}
    }

    /**
     * Parse the namespaceURL from the schemaLocation
     *
     * @return the namespaceURL portion of the schemaLocation
     */
    public String getNamespaceURL() {
	StringTokenizer tokenizer = new StringTokenizer(schemaLocation, " ");
	return tokenizer.nextToken();
    }

    /**
     * Can this nativeItem be represented in ETDMS format?
     * @param nativeItem a record in native format
     * @return true if ETDMS format is possible, false otherwise.
     */
    public abstract boolean isAvailableFor(Object nativeItem);
    
    /**
     * Perform the actual crosswalk.
     *
     * @param nativeItem the native "item". In this case, it is
     * already formatted as an OAI <record> element, with the
     * possible exception that multiple metadataFormats are
     * present in the <metadata> element.
     * @return a String containing the XML to be stored within the <metadata> element.
     * @exception CannotDisseminateFormatException nativeItem doesn't support this format.
     */
    public abstract String createMetadata(Object nativeItem)
	throws CannotDisseminateFormatException;

    /**
     * returns the schemaLocation for this crosswalk.
     * @return a String containing the schemaLocation.
     */
    @Override
    public String toString() {
	return schemaLocation;
    }
}
