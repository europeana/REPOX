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

import org.oclc.oai.util.OAIUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * XMLRecordFactory converts native XML "items" to "record" Strings. This factory assumes the native XML item looks exactly
 * like the <record> element of an OAI GetRecord response, with the possible exception that the <metadata> element contains
 * multiple metadataFormats from which to choose.
 */
public class XMLRecordFactory extends RecordFactory {
	private String identifierStart = "<" + OAIUtil.getTag("identifier") + ">";
	private String identifierEnd = "</" + OAIUtil.getTag("identifier") + ">";
	private String datestampStart = "<" + OAIUtil.getTag("datestamp") + ">";
	private String datestampEnd = "</" + OAIUtil.getTag("datestamp") + ">";
	private String setSpecStart = "<" + OAIUtil.getTag("setSpec") + ">";
	private String setSpecEnd = "</" + OAIUtil.getTag("setSpec") + ">";
	private String aboutStart = "<" + OAIUtil.getTag("about") + ">";
	private String aboutEnd = "</" + OAIUtil.getTag("about") + ">";


    /**
	 * Construct an XMLRecordFactory capable of producing the Crosswalk(s) specified in the properties file.
	 * @param properties Contains information to configure the factory: specifically, the names of the crosswalk(s) supported
	 * @exception IllegalArgumentException Something is wrong with the argument.
	 */
	public XMLRecordFactory(Properties properties) throws IllegalArgumentException {
		super(properties);
	}

	/**
	 * Utility method to parse the 'local identifier' from the OAI identifier
	 * 
	 * @param identifier OAI identifier (e.g. oai:oaicat.oclc.org:ID/12345)
	 * @return local identifier (e.g. ID/12345).
	 */
	@Override
    public String fromOAIIdentifier(String identifier) {
		return identifier;
	}

	/**
	 * Construct an OAI identifier from the native item
	 * 
	 * @param nativeItem native Item object
	 * @return OAI identifier
	 */
	@Override
    public String getOAIIdentifier(Object nativeItem) {
		String xmlRec = (String) nativeItem;
		int startOffset = xmlRec.indexOf(identifierStart);
		int endOffset = xmlRec.indexOf(identifierEnd);
		return xmlRec.substring(startOffset + identifierStart.length(), endOffset);
	}

	/**
	 * get the datestamp from the item
	 * 
	 * @param nativeItem a native item presumably containing a datestamp somewhere
	 * @return a String containing the datestamp for the item
	 * @throws IllegalArgumentException Something is wrong with the argument.
	 */
	@Override
    public String getDatestamp(Object nativeItem) throws IllegalArgumentException {
		String xmlRec = (String) nativeItem;
		int startOffset = xmlRec.indexOf(datestampStart);
		int endOffset = xmlRec.indexOf(datestampEnd);
		return xmlRec.substring(startOffset + datestampStart.length(), endOffset);
	}

	/**
	 * get the setspec from the item
	 * 
	 * @param nativeItem a native item presumably containing a setspec somewhere
	 * @return a String containing the setspec for the item
	 * @throws IllegalArgumentException Something is wrong with the argument.
	 */
	@Override
    public Iterator getSetSpecs(Object nativeItem) throws IllegalArgumentException {
		ArrayList list = new ArrayList();
		String xmlRec = (String) nativeItem;
		for (int startOffset = xmlRec.indexOf(setSpecStart); startOffset >= 0; startOffset = xmlRec.indexOf(setSpecStart,
				startOffset + 1)) {
			int endOffset = xmlRec.indexOf(setSpecEnd, startOffset + 1);
			list.add(xmlRec.substring(startOffset + setSpecStart.length(), endOffset));
		}
		return list.iterator();
	}

	/**
	 * Get the about elements from the item
	 * 
	 * @param nativeItem a native item presumably containing about information somewhere
	 * @return a Iterator of Strings containing &lt;about&gt;s for the item
	 * @throws IllegalArgumentException Something is wrong with the argument.
	 */
	@Override
    public Iterator getAbouts(Object nativeItem) throws IllegalArgumentException {
		ArrayList list = new ArrayList();
		String xmlRec = (String) nativeItem;
		for (int startOffset = xmlRec.indexOf(aboutStart); startOffset >= 0; startOffset = xmlRec.indexOf(aboutStart,
				startOffset + 1)) {
			int endOffset = xmlRec.indexOf(aboutEnd, startOffset + 1);
			list.add(xmlRec.substring(startOffset + aboutStart.length(), endOffset));
		}
		return list.iterator();
	}

	/**
	 * Is the record deleted?
	 * 
	 * @param nativeItem a native item presumably containing a possible delete indicator
	 * @return true if record is deleted, false if not
	 * @throws IllegalArgumentException Something is wrong with the argument.
	 */
	@Override
    public boolean isDeleted(Object nativeItem) throws IllegalArgumentException {
		String xmlRec = (String) nativeItem;
		return xmlRec.indexOf("<header status=\"deleted\"") != -1;
	}

	/**
	 * Allows classes that implement RecordFactory to override the default create() method. This is useful, for example, if
	 * the entire &lt;record&gt; is already packaged as the native record. Return null if you want the default handler to
	 * create it by calling the methods above individually.
	 * 
	 * @param nativeItem the native record
	 * @param schemaLocation the schemaURL desired for the response
	 * @param metadataPrefix from the request
	 * @return a String containing the OAI &lt;record&gt; or null if the default method should be used.
	 */
	@Override
    public String quickCreate(Object nativeItem, String schemaLocation, String metadataPrefix) {
		// Don't perform quick creates
		return null;
	}
}
