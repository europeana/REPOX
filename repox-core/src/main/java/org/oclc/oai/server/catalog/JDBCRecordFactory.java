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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * JDBCRecordFactory converts JDBC "items" to "record" Strings.
 */
public class JDBCRecordFactory extends RecordFactory {
    private String   repositoryIdentifier = null;
    /** JDBCRecordFactory identifierLabel */
    protected String identifierLabel      = null;
    /** JDBCRecordFactory datestampLabel */
    protected String datestampLabel       = null;

    /**
     * Construct an JDBCRecordFactory capable of producing the Crosswalk(s)
     * specified in the properties file.
     * 
     * @param properties
     *            Contains information to configure the factory: specifically,
     *            the names of the crosswalk(s) supported
     * @exception IllegalArgumentException
     *                Something is wrong with the argument.
     */
    public JDBCRecordFactory(Properties properties) throws IllegalArgumentException {
        super(properties);
        repositoryIdentifier = properties.getProperty("JDBCRecordFactory.repositoryIdentifier");
        if (repositoryIdentifier == null) { throw new IllegalArgumentException("JDBCRecordFactory.repositoryIdentifier is missing from the properties file"); }
        identifierLabel = properties.getProperty("JDBCRecordFactory.identifierLabel");
        if (identifierLabel == null) { throw new IllegalArgumentException("JDBCRecordFactory.identifierLabel is missing from the properties file"); }
        datestampLabel = properties.getProperty("JDBCRecordFactory.datestampLabel");
        if (datestampLabel == null) { throw new IllegalArgumentException("JDBCRecordFactory.datestampLabel is missing from the properties file"); }
    }

    /**
     * Utility method to parse the 'local identifier' from the OAI identifier
     * 
     * @param oaiIdentifier
     *            OAI identifier (e.g. oai:oaicat.oclc.org:ID/12345)
     * @return local identifier (e.g. ID/12345).
     */
    @Override
    public String fromOAIIdentifier(String oaiIdentifier) {
        StringTokenizer tokenizer = new StringTokenizer(oaiIdentifier, ":");
        try {
            tokenizer.nextToken();
            tokenizer.nextToken();
            return tokenizer.nextToken();
        } catch (java.util.NoSuchElementException e) {
            return null;
        }
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
        // 	throws IllegalArgumentException {
        // 	try {
        HashMap table = (HashMap)nativeItem;
        Object o = table.get(identifierLabel);
        return o.toString();
        // 	} catch (SQLException e) {
        // 	    e.printStackTrace();
        // 	    throw new IllegalArgumentException(e.getMessage());
        // 	}
    }

    /**
     * Construct an OAI identifier from the native item
     * 
     * @param nativeItem
     *            native Item object
     * @return OAI identifier
     */
    @Override
    public String getOAIIdentifier(Object nativeItem) throws IllegalArgumentException {
        StringBuffer sb = new StringBuffer();
        sb.append("oai:");
        sb.append(repositoryIdentifier);
        sb.append(":");
        sb.append(getLocalIdentifier(nativeItem));
        return sb.toString();
    }

    /**
     * get the datestamp from the item
     * 
     * @param nativeItem
     *            a native item presumably containing a datestamp somewhere
     * @return a String containing the datestamp for the item
     * @throws IllegalArgumentException
     *             Something is wrong with the argument.
     */
    @Override
    public String getDatestamp(Object nativeItem) {
        // 	throws IllegalArgumentException  {
        // 	try {
        HashMap table = (HashMap)nativeItem;
        Object datestamp = table.get(datestampLabel);
        if (datestamp instanceof Timestamp) {
            return ((Timestamp)datestamp).toString().substring(0, 10);
        } else if (datestamp instanceof Date) {
            return ((Date)datestamp).toString();
        } else {
            throw new IllegalArgumentException("Unrecognized datestamp format: " + datestamp.getClass().getName());
        }

        // 	} catch (SQLException e) {
        // 	    e.printStackTrace();
        // 	    throw new IllegalArgumentException(e.getMessage());
        // 	}
    }

    /**
     * get the setspec from the item
     * 
     * @param nativeItem
     *            a native item presumably containing a setspec somewhere
     * @return a String containing the setspec for the item. Null if setSpecs
     *         aren't derived from the nativeItem.
     * @throws IllegalArgumentException
     *             Something is wrong with the argument.
     */
    @Override
    public Iterator getSetSpecs(Object nativeItem) throws IllegalArgumentException {
        return null;
    }

    /**
     * Get the about elements from the item
     * 
     * @param nativeItem
     *            a native item presumably containing about information
     *            somewhere
     * @return a Iterator of Strings containing &lt;about&gt;s for the item.
     *         Null if abouts aren't derived from the nativeItem
     * @throws IllegalArgumentException
     *             Something is wrong with the argument.
     */
    @Override
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
     * @throws IllegalArgumentException
     *             Something is wrong with the argument.
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
     * @param schemaLocation
     *            the schemaURL desired for the response
     * @param metadataPrefix
     *            from the request
     * @return a String containing the OAI &lt;record&gt; or null if the default
     *         method should be used.
     */
    @Override
    public String quickCreate(Object nativeItem, String schemaLocation, String metadataPrefix) {
        // Don't perform quick creates
        return null;
    }
}
