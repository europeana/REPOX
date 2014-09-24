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

import org.oclc.oai.server.catalog.helpers.RecordStringHandler;
import org.oclc.oai.server.verb.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.*;

/**
 * XMLFileOAICatalog is an implementation of AbstractCatalog interface with the
 * data sitting in a directory on a filesystem.
 * 
 * @author Jeff Young, OCLC Online Computer Library Center
 */

public class XMLFileOAICatalog extends AbstractCatalog {
    private static boolean debug                  = false;

    private SortedMap      nativeMap              = null;
    private HashMap        resumptionResults      = new HashMap();
    private int            maxListSize;
    private ArrayList      sets                   = null;
    private Transformer    getMetadataTransformer = null;
    private boolean        schemaLocationIndexed  = false;

    /**
     * Creates a new instance of this class.
     * @param properties
     * @throws IOException
     */
    public XMLFileOAICatalog(Properties properties) throws IOException {
        try {
            String temp;

            temp = properties.getProperty("XMLFileOAICatalog.debug");
            if ("true".equals(temp)) debug = true;
            temp = properties.getProperty("XMLFileOAICatalog.schemaLocationIndexed");
            if ("true".equals(temp)) schemaLocationIndexed = true;
            temp = properties.getProperty("XMLFileOAICatalog.maxListSize");
            if (temp == null) throw new IllegalArgumentException("XMLFileOAICatalog." + "maxListSize is missing from the properties file");
            maxListSize = Integer.parseInt(temp);
            if (debug) System.out.println("in XMLFileOAICatalog(): maxListSize=" + maxListSize);

            String sourceFile = properties.getProperty("XMLFileOAICatalog.sourceFile");
            if (sourceFile == null) throw new IllegalArgumentException("XMLFileOAICatalog." + "sourceFile is missing from the properties file");
            if (debug) System.out.println("in XMLFileOAICatalog(): sourceFile=" + sourceFile);
            String getMetadataXSLTName = properties.getProperty("XMLFileOAICatalog.getMetadataXSLTName");
            if (getMetadataXSLTName != null) {
                try {
                    InputStream is;
                    try {
                        is = new FileInputStream(getMetadataXSLTName);
                    } catch (FileNotFoundException e) {
                        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(getMetadataXSLTName);
                    }
                    StreamSource xslSource = new StreamSource(is);
                    TransformerFactory tFactory = TransformerFactory.newInstance();
                    getMetadataTransformer = tFactory.newTransformer(xslSource);
                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                    throw new IOException(e.getMessage());
                }
            }

            RecordStringHandler rsh = new RecordStringHandler();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            SAXParser saxParser = factory.newSAXParser();
            InputStream in;
            try {
                in = new FileInputStream(sourceFile);
            } catch (FileNotFoundException e) {
                in = Thread.currentThread().getContextClassLoader().getResourceAsStream(sourceFile);
            }
            saxParser.parse(in, rsh);
            //            saxParser.parse(new File(sourceFile), rsh);

            // build the indexes
            nativeMap = rsh.getNativeRecords();
        } catch (SAXException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
        sets = getSets(properties);
    }

    private static ArrayList getSets(Properties properties) {
        TreeMap treeMap = new TreeMap();
        String propertyPrefix = "Sets.";
        Enumeration propNames = properties.propertyNames();
        while (propNames.hasMoreElements()) {
            String propertyName = (String)propNames.nextElement();
            if (propertyName.startsWith(propertyPrefix)) {
                treeMap.put(propertyName, properties.get(propertyName));
            }
        }
        return new ArrayList(treeMap.values());
    }

    /**
     * Retrieve the specified metadata for the specified oaiIdentifier
     * 
     * @param oaiIdentifier
     *            the OAI identifier
     * @param metadataPrefix
     *            the OAI metadataPrefix
     * @return the Record object containing the result.
     * @exception CannotDisseminateFormatException
     *                signals an HTTP status code 400 problem
     * @exception IdDoesNotExistException
     *                signals an HTTP status code 404 problem
     * @exception OAIInternalServerError
     *                signals an HTTP status code 500 problem
     */
    @Override
    public String getRecord(String oaiIdentifier, String metadataPrefix) throws IdDoesNotExistException, CannotDisseminateFormatException, OAIInternalServerError {
        String localIdentifier = getRecordFactory().fromOAIIdentifier(oaiIdentifier);
        String recordid = localIdentifier;
        if (schemaLocationIndexed) {
            recordid = recordid + "/" + metadataPrefix;
        }
        if (debug) System.out.println("XMLFileOAICatalog.getRecord: recordid=" + recordid);
        Object nativeRecord = nativeMap.get(recordid.toLowerCase());
        if (nativeRecord == null) throw new IdDoesNotExistException(oaiIdentifier);
        return constructRecord(nativeRecord, metadataPrefix);
    }

    /**
     * get a DocumentFragment containing the specified record
     */
    @Override
    public String getMetadata(String oaiIdentifier, String metadataPrefix) throws IdDoesNotExistException, IdDoesNotExistException, OAIInternalServerError {
        String localIdentifier = getRecordFactory().fromOAIIdentifier(oaiIdentifier);
        String recordid = localIdentifier;
        if (schemaLocationIndexed) {
            recordid = recordid + "/" + metadataPrefix;
        }
        if (debug) System.out.println("XMLFileOAICatalog.getRecord: recordid=" + recordid);
        HashMap nativeRecord = (HashMap)nativeMap.get(recordid.toLowerCase());
        if (nativeRecord == null) throw new IdDoesNotExistException(oaiIdentifier);
        if (debug) {
            for (Object o : nativeRecord.keySet())
                System.out.println(o);
        }
        String result = (String)nativeRecord.get("recordString");
        if (getMetadataTransformer != null) {
            StringReader stringReader = new StringReader(result);
            StreamSource streamSource = new StreamSource(stringReader);
            StringWriter stringWriter = new StringWriter();
            try {
                synchronized (getMetadataTransformer) {
                    getMetadataTransformer.transform(streamSource, new StreamResult(stringWriter));
                }
            } catch (TransformerException e) {
                e.printStackTrace();
                throw new OAIInternalServerError(e.getMessage());
            }
            result = stringWriter.toString();
        }
        return result;
    }

    /**
     * Retrieve a list of schemaLocation values associated with the specified
     * oaiIdentifier.
     * 
     * We get passed the ID for a record and are supposed to return a list of
     * the formats that we can deliver the record in. Since we are assuming that
     * all the records in the directory have the same format, the response to
     * this is static;
     * 
     * @param oaiIdentifier
     *            the OAI identifier
     * @return a Vector containing schemaLocation Strings
     * @exception OAIBadRequestException
     *                signals an HTTP status code 400 problem
     * @exception OAINotFoundException
     *                signals an HTTP status code 404 problem
     */
    @Override
    public Vector getSchemaLocations(String oaiIdentifier) throws IdDoesNotExistException, NoMetadataFormatsException {
        Vector v = new Vector();
        //        String localIdentifier
        //            = getRecordFactory().fromOAIIdentifier(oaiIdentifier);
        Iterator iterator = nativeMap.entrySet().iterator();
        int numRows = nativeMap.entrySet().size();
        for (int i = 0; i < numRows; ++i) {
            Map.Entry entryNativeMap = (Map.Entry)iterator.next();
            HashMap nativeRecord = (HashMap)entryNativeMap.getValue();
            if (getRecordFactory().getOAIIdentifier(nativeRecord).equals(oaiIdentifier)) {
                Vector schemaLocations = getRecordFactory().getSchemaLocations(nativeRecord);
                for (Object schemaLocation : schemaLocations)
                    v.add(schemaLocation);
            }
        }
        if (v.size() > 0) {
            return v;
        } else {
            throw new IdDoesNotExistException(oaiIdentifier);
        }
    }

    /**
     * Retrieve a list of Identifiers that satisfy the criteria parameters
     * 
     * @param from
     *            beginning date in the form of YYYY-MM-DD or null if earliest
     *            date is desired
     * @param until
     *            ending date in the form of YYYY-MM-DD or null if latest date
     *            is desired
     * @param set
     *            set name or null if no set is desired
     * @return a Map object containing an optional "resumptionToken" key/value
     *         pair and an "identifiers" Map object. The "identifiers" Map
     *         contains OAI identifier keys with corresponding values of "true"
     *         or null depending on whether the identifier is deleted or not.
     * @exception OAIBadRequestException
     *                signals an HTTP status code 400 problem
     */
    @Override
    public Map listIdentifiers(String from, String until, String set, String metadataPrefix) throws NoItemsMatchException {
        purge(); // clean out old resumptionTokens
        Map listIdentifiersMap = new HashMap();
        ArrayList headers = new ArrayList();
        ArrayList identifiers = new ArrayList();
        Iterator iterator = nativeMap.entrySet().iterator();
        int numRows = nativeMap.entrySet().size();
        int count = 0;
        while (count < maxListSize && iterator.hasNext()) {
            Map.Entry entryNativeMap = (Map.Entry)iterator.next();
            HashMap nativeRecord = (HashMap)entryNativeMap.getValue();
            String recordDate = getRecordFactory().getDatestamp(nativeRecord);
            String schemaLocation = (String)nativeRecord.get("schemaLocation");
            List setSpecs = (List)nativeRecord.get("setSpecs");
            if (recordDate.compareTo(from) >= 0 && recordDate.compareTo(until) <= 0 && (!schemaLocationIndexed || schemaLocation.equals(getCrosswalks().getSchemaLocation(metadataPrefix))) && (set == null || setSpecs.contains(set))) {
                String[] header = getRecordFactory().createHeader(nativeRecord);
                headers.add(header[0]);
                identifiers.add(header[1]);
                count++;
            }
        }

        if (count == 0) throw new NoItemsMatchException();

        /* decide if you're done */
        if (iterator.hasNext()) {
            String resumptionId = getRSName();
            resumptionResults.put(resumptionId, iterator);

            /*****************************************************************
             * Construct the resumptionToken String however you see fit.
             *****************************************************************/
            StringBuffer resumptionTokenSb = new StringBuffer();
            resumptionTokenSb.append(resumptionId);
            resumptionTokenSb.append(":");
            resumptionTokenSb.append(Integer.toString(count));
            resumptionTokenSb.append(":");
            resumptionTokenSb.append(Integer.toString(numRows));
            resumptionTokenSb.append(":");
            resumptionTokenSb.append(metadataPrefix);

            /*****************************************************************
             * Use the following line if you wish to include the optional
             * resumptionToken attributes in the response. Otherwise, use the
             * line after it that I've commented out.
             *****************************************************************/
            listIdentifiersMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(), numRows, 0));
            // 	    listIdentifiersMap.put("resumptionMap",
            // 				   getResumptionMap(resumptionTokenSb.toString()));
        }
        listIdentifiersMap.put("headers", headers.iterator());
        listIdentifiersMap.put("identifiers", identifiers.iterator());
        return listIdentifiersMap;
    }

    /**
     * Retrieve the next set of Identifiers associated with the resumptionToken
     * 
     * @param resumptionToken
     *            implementation-dependent format taken from the previous
     *            listIdentifiers() Map result.
     * @return a Map object containing an optional "resumptionToken" key/value
     *         pair and an "identifiers" Map object. The "identifiers" Map
     *         contains OAI identifier keys with corresponding values of "true"
     *         or null depending on whether the identifier is deleted or not.
     * @throws OAIBadRequestException
     *                signals an http status code 400 problem
     */
    @Override
    public Map listIdentifiers(String resumptionToken) throws BadResumptionTokenException {
        purge(); // clean out old resumptionTokens
        Map listIdentifiersMap = new HashMap();
        ArrayList headers = new ArrayList();
        ArrayList identifiers = new ArrayList();

        /**********************************************************************
         * parse your resumptionToken and look it up in the resumptionResults,
         * if necessary
         **********************************************************************/
        StringTokenizer tokenizer = new StringTokenizer(resumptionToken, ":");
        String resumptionId;
        int oldCount;
        String metadataPrefix;
        int numRows;
        try {
            resumptionId = tokenizer.nextToken();
            oldCount = Integer.parseInt(tokenizer.nextToken());
            numRows = Integer.parseInt(tokenizer.nextToken());
            metadataPrefix = tokenizer.nextToken();
        } catch (NoSuchElementException e) {
            throw new BadResumptionTokenException();
        }

        /* Get some more records from your database */
        Iterator iterator = (Iterator)resumptionResults.remove(resumptionId);
        if (iterator == null) {
            System.out.println("XMLFileOAICatalog.listIdentifiers: reuse of old resumptionToken?");
            iterator = nativeMap.entrySet().iterator();
            for (int i = 0; i < oldCount; ++i)
                iterator.next();
        }

        /* load the headers and identifiers ArrayLists. */
        int count = 0;
        while (count < maxListSize && iterator.hasNext()) {
            Map.Entry entryNativeMap = (Map.Entry)iterator.next();
            String key = (String)entryNativeMap.getKey();
            Object nativeRecord = nativeMap.get(key);
            String[] header = getRecordFactory().createHeader(nativeRecord);
            headers.add(header[0]);
            identifiers.add(header[1]);
            count++;
        }

        /* decide if you're done. */
        if (iterator.hasNext()) {
            resumptionId = getRSName();
            resumptionResults.put(resumptionId, iterator);

            /*****************************************************************
             * Construct the resumptionToken String however you see fit.
             *****************************************************************/
            StringBuffer resumptionTokenSb = new StringBuffer();
            resumptionTokenSb.append(resumptionId);
            resumptionTokenSb.append(":");
            resumptionTokenSb.append(Integer.toString(oldCount + count));
            resumptionTokenSb.append(":");
            resumptionTokenSb.append(Integer.toString(numRows));
            resumptionTokenSb.append(":");
            resumptionTokenSb.append(metadataPrefix);

            /*****************************************************************
             * Use the following line if you wish to include the optional
             * resumptionToken attributes in the response. Otherwise, use the
             * line after it that I've commented out.
             *****************************************************************/
            listIdentifiersMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(), numRows, oldCount));
            //          listIdentifiersMap.put("resumptionMap",
            //                                 getResumptionMap(resumptionTokenSb.toString()));
        }

        listIdentifiersMap.put("headers", headers.iterator());
        listIdentifiersMap.put("identifiers", identifiers.iterator());
        return listIdentifiersMap;
    }

    /**
     * Utility method to construct a Record object for a specified
     * metadataFormat from a native record
     * 
     * @param nativeRecord
     *            native item from the dataase
     * @param metadataPrefix
     *            the desired metadataPrefix for performing the crosswalk
     * @return the <record/> String
     * @throws CannotDisseminateFormatException
     *                the record is not available for the specified
     *                metadataPrefix.
     */
    private String constructRecord(Object nativeRecord, String metadataPrefix) throws CannotDisseminateFormatException, OAIInternalServerError {
        String schemaURL = null;
        Iterator setSpecs = getSetSpecs(nativeRecord);
        Iterator abouts = getAbouts(nativeRecord);

        if (metadataPrefix != null) {
            if (debug) {
                System.out.println(getCrosswalks());
            }
            if ((schemaURL = getCrosswalks().getSchemaURL(metadataPrefix)) == null) throw new CannotDisseminateFormatException(metadataPrefix);
        }
        return getRecordFactory().create(nativeRecord, schemaURL, metadataPrefix, setSpecs, abouts);
    }

    /**
     * get an Iterator containing the setSpecs for the nativeRecord
     * 
     * @param rs
     *            ResultSet containing the nativeRecord
     * @return an Iterator containing the list of setSpec values for this
     *         nativeRecord
     */
    private Iterator getSetSpecs(Object nativeRecord) throws OAIInternalServerError {
        try {
            return getRecordFactory().getSetSpecs(nativeRecord);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        }
    }

    /**
     * get an Iterator containing the abouts for the nativeRecord
     * 
     * @param rs
     *            ResultSet containing the nativeRecord
     * @return an Iterator containing the list of about values for this
     *         nativeRecord
     */
    private Iterator getAbouts(Object nativeRecord) {
        return null;
    }

    /**
     * Retrieve a list of records that satisfy the specified criteria
     * 
     * @param from
     *            beginning date in the form of YYYY-MM-DD or null if earliest
     *            date is desired
     * @param until
     *            ending date in the form of YYYY-MM-DD or null if latest date
     *            is desired
     * @param set
     *            set name or null if no set is desired
     * @param metadataPrefix
     *            the OAI metadataPrefix
     * @return a Map object containing an optional "resumptionToken" key/value
     *         pair and a "records" Iterator object. The "records" Iterator
     *         contains a set of Records objects.
     * @throws OAIBadRequestException
     *                signals an HTTP status code 400 problem
     * @throws OAIInternalServerError
     *                signals an HTTP status code 500 problem
     */
    @Override
    public Map listRecords(String from, String until, String set, String metadataPrefix) throws CannotDisseminateFormatException, OAIInternalServerError, NoItemsMatchException {
        String requestedSchemaLocation = getCrosswalks().getSchemaLocation(metadataPrefix);
        purge(); // clean out old resumptionTokens
        Map listRecordsMap = new HashMap();
        LinkedList records = new LinkedList();
        Iterator iterator = nativeMap.entrySet().iterator();
        int numRows = nativeMap.entrySet().size();
        if (debug) {
            System.out.println("XMLFileOAICatalog.listRecords: numRows=" + numRows);
        }
        int count = 0;
        while (count < maxListSize && iterator.hasNext()) {
            Map.Entry entryNativeMap = (Map.Entry)iterator.next();
            HashMap nativeRecord = (HashMap)entryNativeMap.getValue();
            String recordDate = getRecordFactory().getDatestamp(nativeRecord);
            String schemaLocation = (String)nativeRecord.get("schemaLocation");
            List setSpecs = (List)nativeRecord.get("setSpecs");
            if (debug) {
                System.out.println("XMLFileOAICatalog.listRecord: recordDate=" + recordDate);
                System.out.println("XMLFileOAICatalog.listRecord: requestedSchemaLocation=" + requestedSchemaLocation);
                System.out.println("XMLFileOAICatalog.listRecord: schemaLocation=" + schemaLocation);
            }
            if (recordDate.compareTo(from) >= 0 && recordDate.compareTo(until) <= 0 && (!schemaLocationIndexed || requestedSchemaLocation.equals(schemaLocation)) && (set == null || setSpecs.contains(set))) {
                String record = constructRecord(nativeRecord, metadataPrefix);
                if (debug) {
                    System.out.println("XMLFileOAICatalog.listRecords: record=" + record);
                }
                records.add(record);
                count++;
            }
        }

        if (count == 0) throw new NoItemsMatchException();

        /* decide if you're done */
        if (iterator.hasNext()) {
            String resumptionId = getRSName();
            resumptionResults.put(resumptionId, iterator);

            /*****************************************************************
             * Construct the resumptionToken String however you see fit.
             *****************************************************************/
            StringBuffer resumptionTokenSb = new StringBuffer();
            resumptionTokenSb.append(resumptionId);
            resumptionTokenSb.append(":");
            resumptionTokenSb.append(Integer.toString(count));
            resumptionTokenSb.append(":");
            resumptionTokenSb.append(Integer.toString(numRows));
            resumptionTokenSb.append(":");
            resumptionTokenSb.append(metadataPrefix);

            /*****************************************************************
             * Use the following line if you wish to include the optional
             * resumptionToken attributes in the response. Otherwise, use the
             * line after it that I've commented out.
             *****************************************************************/
            listRecordsMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(), numRows, 0));
            // 	    listRecordsMap.put("resumptionMap",
            // 				   getResumptionMap(resumptionTokenSb.toString()));
        }
        listRecordsMap.put("records", records.iterator());
        return listRecordsMap;
    }

    /**
     * Retrieve the next set of records associated with the resumptionToken
     * 
     * @param resumptionToken
     *            implementation-dependent format taken from the previous
     *            listRecords() Map result.
     * @return a Map object containing an optional "resumptionToken" key/value
     *         pair and a "records" Iterator object. The "records" Iterator
     *         contains a set of Records objects.
     * @throws OAIBadRequestException
     *                signals an http status code 400 problem
     * @throws OAIInternalServerError
     *                signals an http status code 500 problem
     */
    @Override
    public Map listRecords(String resumptionToken) throws BadResumptionTokenException, OAIInternalServerError {
        purge(); // clean out old resumptionTokens
        Map listRecordsMap = new HashMap();
        LinkedList records = new LinkedList();

        /**********************************************************************
         * parse your resumptionToken and look it up in the resumptionResults,
         * if necessary
         **********************************************************************/
        StringTokenizer tokenizer = new StringTokenizer(resumptionToken, ":");
        String resumptionId;
        int oldCount;
        String metadataPrefix;
        int numRows;
        try {
            resumptionId = tokenizer.nextToken();
            oldCount = Integer.parseInt(tokenizer.nextToken());
            numRows = Integer.parseInt(tokenizer.nextToken());
            metadataPrefix = tokenizer.nextToken();
        } catch (NoSuchElementException e) {
            throw new BadResumptionTokenException();
        }

        /* Get some more records from your database */
        Iterator iterator = (Iterator)resumptionResults.remove(resumptionId);
        if (iterator == null) {
            System.out.println("XMLFileOAICatalog.listRecords: reuse of old resumptionToken?");
            iterator = nativeMap.entrySet().iterator();
            for (int i = 0; i < oldCount; ++i)
                iterator.next();
        }

        /* load the records ArrayLists. */
        int count = 0;
        while (count < maxListSize && iterator.hasNext()) {
            Map.Entry entryNativeMap = (Map.Entry)iterator.next();
            try {
                String key = (String)entryNativeMap.getKey();
                Object nativeRecord = nativeMap.get(key);
                String record = constructRecord(nativeRecord, metadataPrefix);
                records.add(record);
                count++;
            } catch (CannotDisseminateFormatException e) {
                /* the client hacked the resumptionToken beyond repair */
                throw new BadResumptionTokenException();
            }
        }

        /* decide if you're done. */
        if (iterator.hasNext()) {
            resumptionId = getRSName();
            resumptionResults.put(resumptionId, iterator);

            /*****************************************************************
             * Construct the resumptionToken String however you see fit.
             *****************************************************************/
            StringBuffer resumptionTokenSb = new StringBuffer();
            resumptionTokenSb.append(resumptionId);
            resumptionTokenSb.append(":");
            resumptionTokenSb.append(Integer.toString(oldCount + count));
            resumptionTokenSb.append(":");
            resumptionTokenSb.append(Integer.toString(numRows));
            resumptionTokenSb.append(":");
            resumptionTokenSb.append(metadataPrefix);

            /*****************************************************************
             * Use the following line if you wish to include the optional
             * resumptionToken attributes in the response. Otherwise, use the
             * line after it that I've commented out.
             *****************************************************************/
            listRecordsMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(), numRows, oldCount));
            //          listRecordsMap.put("resumptionMap",
            //                                 getResumptionMap(resumptionTokenSb.toString()));
        }

        listRecordsMap.put("records", records.iterator());
        return listRecordsMap;
    }

    public Map listSets() throws NoSetHierarchyException {
        if (sets.size() == 0) throw new NoSetHierarchyException();
        Map listSetsMap = new LinkedHashMap();
        listSetsMap.put("sets", sets.iterator());
        return listSetsMap;
    }

    public Map listSets(String resumptionToken) throws BadResumptionTokenException {
        throw new BadResumptionTokenException();
    }

    /**
     * close the repository
     */
    public void close() {
    }

    /**
     * Purge tokens that are older than the time-to-live.
     */
    private void purge() {
        ArrayList old = new ArrayList();
        Date then, now = new Date();
        Iterator keySet = resumptionResults.keySet().iterator();
        String key;

        while (keySet.hasNext()) {
            key = (String)keySet.next();
            then = new Date(Long.parseLong(key) + getMillisecondsToLive());
            if (now.after(then)) {
                old.add(key);
            }
        }
        for (Object anOld : old) {
            key = (String)anOld;
            resumptionResults.remove(key);
        }
    }

    /**
     * Use the current date as the basis for the resumptiontoken
     * 
     * @return a long integer version of the current time
     */
    private synchronized static String getRSName() {
        Date now = new Date();
        return Long.toString(now.getTime());
    }
}
