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

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * NewFileRecordFactory converts native XML "items" to "record" Strings. This
 * factory assumes the native XML item looks exactly like the <record> element
 * of an OAI GetRecord response, with the possible exception that the <metadata>
 * element contains multiple metadataFormats from which to choose.
 */
public class FolderRecordFactory extends RecordFactory {
    private String repositoryIdentifier = null;

    /**
     * Construct an NewFileRecordFactory capable of producing the Crosswalk(s)
     * specified in the properties file.
     * 
     * @param properties
     *            Contains information to configure the factory: specifically,
     *            the names of the crosswalk(s) supported
     * @exception IllegalArgumentException
     *                Something is wrong with the argument.
     */
    public FolderRecordFactory(Properties properties) throws IllegalArgumentException {
        super(properties);
        repositoryIdentifier = properties.getProperty("NewFileRecordFactory.repositoryIdentifier");
        if (repositoryIdentifier == null) { throw new IllegalArgumentException("NewFileRecordFactory.repositoryIdentifier is missing from the properties file"); }
    }

    /**
     * Utility method to parse the 'local identifier' from the OAI identifier
     * 
     * @param identifier
     *            OAI identifier (e.g. oai:oaicat.oclc.org:ID/12345)
     * @return local identifier (e.g. ID/12345).
     */
    @Override
    public String fromOAIIdentifier(String identifier) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(identifier, ":");
            tokenizer.nextToken();
            tokenizer.nextToken();
            return tokenizer.nextToken();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Construct an OAI identifier from the native item
     * 
     * @param nativeItem
     *            native Item object
     * @return OAI identifier
     */
    @Override
    public String getOAIIdentifier(Object nativeItem) {
        Document doc = (Document)nativeItem;
        try {
            return XPathAPI.eval(doc, "record/header/identifier").str();
        } catch (TransformerException e) {
            return e.getMessage();
        }
        //	StringBuffer sb = new StringBuffer();
        //	sb.append("oai:");
        //	sb.append(repositoryIdentifier);
        //	sb.append(":");
        //	sb.append(getLocalIdentifier(nativeItem));
        //	return sb.toString();
    }

    /**
     * Extract the local identifier from the native item
     * 
     * @param nativeItem
     *            native Item object
     * @return local identifier
     */
    @Override
    public String getLocalIdentifier(Object nativeItem) {
        return "foo";
        //        return (String)((Document)nativeItem).get("localIdentifier");
    }

    /**
     * get the datestamp from the item
     * 
     * @param nativeItem
     *            a native item presumably containing a datestamp somewhere
     * @return a String containing the datestamp for the item
     * @exception IllegalArgumentException
     *                Something is wrong with the argument.
     */
    @Override
    public String getDatestamp(Object nativeItem) throws IllegalArgumentException {
        Document doc = (Document)nativeItem;
        try {
            return XPathAPI.eval(doc, "/record/header/datestamp").str();
        } catch (TransformerException e) {
            return e.getMessage();
        }
        //        return (String)((HashMap)nativeItem).get("lastModified");
    }

    /**
     * get the setspec from the item
     * 
     * @param nativeItem
     *            a native item presumably containing a setspec somewhere
     * @return a String containing the setspec for the item
     * @exception IllegalArgumentException
     *                Something is wrong with the argument.
     */
    @Override
    public Iterator getSetSpecs(Object nativeItem) throws IllegalArgumentException {
        ArrayList list = new ArrayList();
        Document doc = (Document)nativeItem;
        try {
            NodeIterator iter = XPathAPI.selectNodeIterator(doc, "/record/header/setSpec");
            Node node = null;
            while ((node = iter.nextNode()) != null) {
                list.add(XPathAPI.eval(node, ".").str());
            }
        } catch (TransformerException e) {
            e.printStackTrace();
            return null;
        }
        return list.iterator();
        //        return (Iterator)((HashMap)nativeItem).get("setSpecs");
    }

    /**
     * Get the about elements from the item
     * 
     * @param nativeItem
     *            a native item presumably containing about information
     *            somewhere
     * @return a Iterator of Strings containing &lt;about&gt;s for the item
     * @exception IllegalArgumentException
     *                Something is wrong with the argument.
     */
    public Iterator getAbouts(Object nativeItem) throws IllegalArgumentException {
        return null;
    }

    /**
     * Is the record deleted?
     * 
     * @param nativeItem
     *            a native item presumably containing a possible delete
     *            indicator
     * @return true if record is deleted, false if not
     * @exception IllegalArgumentException
     *                Something is wrong with the argument.
     */
    @Override
    public boolean isDeleted(Object nativeItem) throws IllegalArgumentException {
        return false;
    }

    /**
     * Allows classes that implement RecordFactory to override the default
     * create() method. This is useful, for example, if the entire
     * &lt;record&gt; is already packaged as the native record. Return null if
     * you want the default handler to create it by calling the methods above
     * individually.
     * 
     * @param nativeItem
     *            the native record
     * @param schemaURL
     *            the schemaURL desired for the response
     * @param the
     *            metadataPrefix from the request
     * @return a String containing the OAI &lt;record&gt; or null if the default
     *         method should be used.
     */
    @Override
    public String quickCreate(Object nativeItem, String schemaLocation, String metadataPrefix) {
        return null;
        //	Document doc = (Document)nativeItem;
        //            String result = new String((byte[])((HashMap)nativeItem).get("recordBytes"), "UTF-8");
        //            if (result.startsWith("<?")) {
        //                int offset = result.indexOf("?>");
        //                result = result.substring(offset+2);
        //            }
        //            return result;
        //        try {
        //            return OAIUtil.toString(doc);
        //        } catch (TransformerException e) {
        //            e.printStackTrace();
        //            return null;
        //        }
    }
}
