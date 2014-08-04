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

import org.oclc.oai.server.verb.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
// import org.oclc.oai.util.*;
// import org.xml.sax.*;


/**
 * FileSystemOAICatalog is an implementation of AbstractCatalog interface
 * with the data sitting in a directory on a filesystem.
 *
 * @author Ralph LeVan, OCLC Online Computer Library Center
 */

public class FileSystemOAICatalog extends AbstractCatalog {
    static final boolean debug=false;

    private SimpleDateFormat dateFormatter = new SimpleDateFormat();
    protected String           homeDir;
    private HashMap          fileDateMap = new HashMap();
    private HashMap          resumptionResults=new HashMap();
    private int              maxListSize;
    private boolean hideExtension = false;

    /**
     * Creates a new instance of this class.
     * @param properties
     */
    public FileSystemOAICatalog(Properties properties) {
        String          temp;
        dateFormatter.applyPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        temp=properties.getProperty("FileSystemOAICatalog.maxListSize");
        if (temp==null)
            throw new IllegalArgumentException("FileSystemOAICatalog."+
                "maxListSize is missing from the properties file");
        maxListSize = Integer.parseInt(temp);
        if(debug)
            System.out.println("in FileSystemOAICatalog(): maxListSize="+
                maxListSize);

	hideExtension = "true".equalsIgnoreCase(properties.getProperty("FileSystemOAICatalog.hideExtension"));

        homeDir=properties.getProperty("FileSystemOAICatalog.homeDir");
        if (homeDir==null)
            throw new IllegalArgumentException("FileSystemOAICatalog."+
                "homeDir is missing from the properties file");
        if(debug)
            System.out.println("in FileSystemOAICatalog(): homeDir="+
                homeDir);


	File homeFile = new File(homeDir);
	int homeDirLen = homeFile.getPath().length()+1;
	loadFileMap(homeDirLen, homeFile);
// 	Iterator iterator = fileDateMap.entrySet().iterator();
// 	while (iterator.hasNext()) {
// 	    Map.Entry entry = (Map.Entry)iterator.next();
// 	    System.out.println(entry.getKey() + ":" + entry.getValue());
// 	}
    }

    private void loadFileMap(int homeDirLen, File currentDir) {
	String[] list = currentDir.list();
        for (String aList : list) {
            File child = new File(currentDir, aList);
            if (child.isDirectory()) {
                loadFileMap(homeDirLen, child);
            } else if (isMetadataFile(child)) {
                String localIdentifier = file2LocalIdentifier(homeDirLen, child);
                String datestamp = date2OAIDatestamp(new Date(child.lastModified()));
                fileDateMap.put(localIdentifier, datestamp);
            }
        }
    }

    /**
     * Override this method if some files exist in the
     * filesystem that aren't metadata records.
     *
     * @param child the File to be investigated
     * @return true if it contains metadata, false otherwise
     */
    protected boolean isMetadataFile(File child) {
        return true;
    }

    /**
     * Override this method if you don't like the default localIdentifiers.
     * @param homeDirLen the length of the home directory path
     * @param file the File object containing the native record
     * @return localIdentifier
     */
    protected String file2LocalIdentifier(int homeDirLen, File file) {
	String fileName = file.getPath().substring(homeDirLen).replace(File.separatorChar, '/');
	if (hideExtension && fileName.endsWith(".xml")) {
	    fileName = fileName.substring(0, fileName.lastIndexOf(".xml"));
	}
	return fileName;
    }

    /**
     * Override this method if you don't like the default localIdentifiers.
     * @param localIdentifier the localIdentifier as parsed from the OAI identifier
     * @return the File object containing the native record
     */
    protected File localIdentifier2File(String localIdentifier) {
        String fileName = localIdentifier.replace('/', File.separatorChar);
	if (hideExtension) {
	    fileName = fileName + ".xml";
	}
	return new File(homeDir, fileName);
    }

    private String date2OAIDatestamp(Date date) {
        return dateFormatter.format(date);
    }

    private HashMap getNativeHeader(String localIdentifier) {
        HashMap recordMap = null;
        if (fileDateMap.containsKey(localIdentifier)) {
            recordMap = new HashMap();
            recordMap.put("localIdentifier", localIdentifier);
            recordMap.put("lastModified", fileDateMap.get(localIdentifier));
            return recordMap;
        }
        return recordMap;
    }
    
    private HashMap getNativeRecord(String localIdentifier)
        throws IOException {
        HashMap recordMap = getNativeHeader(localIdentifier);
        if (recordMap == null) {
            return null;
        } else {
            File file = localIdentifier2File(localIdentifier);
            try {
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                byte[] buffer = new byte[(int)file.length()];
                bis.read(buffer, 0, (int)file.length());
                recordMap.put("recordBytes", buffer);
                return recordMap;
            } catch (FileNotFoundException e) {
                return null;
            }
        }
    }
    
    /**
     * Retrieve the specified metadata for the specified oaiIdentifier
     *
     * @param     oaiIdentifier the OAI identifier
     * @param     metadataPrefix the OAI metadataPrefix
     * @return    the Record object containing the result.
     * @throws CannotDisseminateFormatException signals an http status
     *                code 400 problem
     * @throws IdDoesNotExistException signals an http status code 404
     *                problem
     * @throws OAIInternalServerError signals an http status code 500
     *                problem
     */
    @Override
    public String getRecord(String oaiIdentifier, String metadataPrefix) throws IdDoesNotExistException, CannotDisseminateFormatException, OAIInternalServerError {
        HashMap nativeItem = null;
        try {
	    String localIdentifier = getRecordFactory().fromOAIIdentifier(oaiIdentifier);

            nativeItem = getNativeRecord(localIdentifier);
            if (nativeItem == null)
                throw new IdDoesNotExistException(oaiIdentifier);
            return constructRecord(nativeItem, metadataPrefix);
        } catch (IOException e) {
            e.printStackTrace();
            throw new OAIInternalServerError("Database Failure");
        }
    }


    /**
     * Retrieve a list of schemaLocation values associated with the specified
     * oaiIdentifier.
     *
     * We get passed the ID for a record and are supposed to return a list
     * of the formats that we can deliver the record in.  Since we are assuming
     * that all the records in the directory have the same format, the
     * response to this is static;
     *
     * @param oaiIdentifier the OAI identifier
     * @return a Vector containing schemaLocation Strings
     * @throws OAIInternalServerError signals an http status code 500
     *            problem
     */
    @Override
    public Vector getSchemaLocations(String oaiIdentifier) throws IdDoesNotExistException, OAIInternalServerError, NoMetadataFormatsException {
        HashMap nativeItem = null;
        try {
	    String localIdentifier
		= getRecordFactory().fromOAIIdentifier(oaiIdentifier);
            nativeItem = getNativeRecord(localIdentifier);
        } catch (IOException e) {
            e.printStackTrace();
            throw new OAIInternalServerError("Database Failure");
        }
        
        if (nativeItem != null) {
            return getRecordFactory().getSchemaLocations(nativeItem);
        } else {
            throw new IdDoesNotExistException(oaiIdentifier);
        }
    }


    /**
     * Retrieve a list of Identifiers that satisfy the criteria parameters
     *
     * @param from beginning date in the form of YYYY-MM-DD or null if earliest
     * date is desired
     * @param until ending date in the form of YYYY-MM-DD or null if latest
     * date is desired
     * @param set set name or null if no set is desired
     * @return a Map object containing an optional "resumptionToken" key/value
     * pair and an "identifiers" Map object. The "identifiers" Map contains OAI
     * identifier keys with corresponding values of "true" or null depending on
     * whether the identifier is deleted or not.
     */
    @Override
    public Map listIdentifiers(String from, String until, String set, String metadataPrefix) throws NoItemsMatchException {
        purge(); // clean out old resumptionTokens
        Map listIdentifiersMap = new HashMap();
        ArrayList headers = new ArrayList();
        ArrayList identifiers = new ArrayList();
	Iterator iterator = fileDateMap.entrySet().iterator();
	int numRows = fileDateMap.entrySet().size();
	int count = 0;
	while (count < maxListSize && iterator.hasNext()) {
	    Map.Entry entryDateMap = (Map.Entry)iterator.next();
            String fileDate = (String)entryDateMap.getValue();
            if (fileDate.compareTo(from) >= 0
                && fileDate.compareTo(until) <= 0) {
                HashMap nativeHeader = getNativeHeader((String)entryDateMap.getKey());
                String[] header = getRecordFactory().createHeader(nativeHeader);
                headers.add(header[0]);
                identifiers.add(header[1]);
                count++;
            }
	}

        if (count == 0)
            throw new NoItemsMatchException();
        
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
     * @param resumptionToken implementation-dependent format taken from the
     * previous listIdentifiers() Map result.
     * @return a Map object containing an optional "resumptionToken" key/value
     * pair and an "identifiers" Map object. The "identifiers" Map contains OAI
     * identifier keys with corresponding values of "true" or null depending on
     * whether the identifier is deleted or not.
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
	    System.out.println("FileSystemOAICatalog.listIdentifiers: reuse of old resumptionToken?");
	    iterator = fileDateMap.entrySet().iterator();
	    for (int i = 0; i<oldCount; ++i)
		iterator.next();
	}
        
	/* load the headers and identifiers ArrayLists. */
	int count = 0;
	while (count < maxListSize && iterator.hasNext()) {
	    Map.Entry entryDateMap = (Map.Entry)iterator.next();
	    HashMap nativeHeader = getNativeHeader((String)entryDateMap.getKey());
		String[] header = getRecordFactory().createHeader(nativeHeader);
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
     * @param nativeItem native item from the dataase
     * @param metadataPrefix the desired metadataPrefix for performing the crosswalk
     * @return the <record/> String
     * @exception CannotDisseminateFormatException the record is not available
     * for the specified metadataPrefix.
     */
    private String constructRecord(HashMap nativeItem, String metadataPrefix)
        throws CannotDisseminateFormatException {
        String schemaURL = null;
	Iterator setSpecs = getSetSpecs(nativeItem);
	Iterator abouts = getAbouts(nativeItem);

        if (metadataPrefix != null) {
            if ((schemaURL = getCrosswalks().getSchemaURL(metadataPrefix)) == null)
                throw new CannotDisseminateFormatException(metadataPrefix);
        }
        return getRecordFactory().create(nativeItem, schemaURL, metadataPrefix, setSpecs, abouts);
    }

    /**
     * Get an Iterator containing the setSpecs for the nativeItem
     *
     * @param rs ResultSet containing the nativeItem
     * @return an Iterator containing the list of setSpec values for this nativeItem
     */
    private Iterator getSetSpecs(HashMap nativeItem) {
        return null;
    }

    /**
     * Get an Iterator containing the abouts for the nativeItem
     *
     * @param rs ResultSet containing the nativeItem
     * @return an Iterator containing the list of about values for this nativeItem
     */
    private Iterator getAbouts(HashMap nativeItem) {
        return null;
    }

    /**
     * Retrieve a list of records that satisfy the specified criteria
     *
     * @param from beginning date in the form of YYYY-MM-DD or null if earliest
     * date is desired
     * @param until ending date in the form of YYYY-MM-DD or null if latest
     * date is desired
     * @param set set name or null if no set is desired
     * @param metadataPrefix the OAI metadataPrefix
     * @return a Map object containing an optional "resumptionToken" key/value
     * pair and a "records" Iterator object. The "records" Iterator contains a
     * set of Records objects.
     *            problem
     * @throws OAIInternalServerError signals an http status code 500
     *            problem
     */
    @Override
    public Map listRecords(String from, String until, String set, String metadataPrefix) throws CannotDisseminateFormatException, OAIInternalServerError, NoItemsMatchException {
        purge(); // clean out old resumptionTokens
        Map listRecordsMap = new HashMap();
        ArrayList records = new ArrayList();
	Iterator iterator = fileDateMap.entrySet().iterator();
	int numRows = fileDateMap.entrySet().size();
	int count = 0;
	while (count < maxListSize && iterator.hasNext()) {
	    Map.Entry entryDateMap = (Map.Entry)iterator.next();
            String fileDate = (String)entryDateMap.getValue();
            if (fileDate.compareTo(from) >= 0
                && fileDate.compareTo(until) <= 0) {
                try {
                    HashMap nativeItem = getNativeRecord((String)entryDateMap.getKey());
                    String record = constructRecord(nativeItem, metadataPrefix);
                    records.add(record);
                    count++;
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new OAIInternalServerError(e.getMessage());
                }
            }
	}
        
        if (count == 0)
            throw new NoItemsMatchException();
        
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
     * @param resumptionToken implementation-dependent format taken from the
     * previous listRecords() Map result.
     * @return a Map object containing an optional "resumptionToken" key/value
     * pair and a "records" Iterator object. The "records" Iterator contains a
     * set of Records objects.
     */
    @Override
    public Map listRecords(String resumptionToken) throws BadResumptionTokenException {
        purge(); // clean out old resumptionTokens
        Map listRecordsMap = new HashMap();
        ArrayList records = new ArrayList();
        
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
	    System.out.println("FileSystemOAICatalog.listRecords: reuse of old resumptionToken?");
	    iterator = fileDateMap.entrySet().iterator();
	    for (int i = 0; i<oldCount; ++i)
		iterator.next();
	}
        
	/* load the records ArrayLists. */
	int count = 0;
	while (count < maxListSize && iterator.hasNext()) {
	    Map.Entry entryDateMap = (Map.Entry)iterator.next();
	    try {
                HashMap nativeItem = getNativeRecord((String)entryDateMap.getKey());
                String record = constructRecord(nativeItem, metadataPrefix);
                records.add(record);
                count++;
            } catch (CannotDisseminateFormatException e) {
                /* the client hacked the resumptionToken beyond repair */
                throw new BadResumptionTokenException();
            } catch (IOException e) {
                /* the file is probably missing */
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


    @Override
    public Map listSets() throws NoSetHierarchyException {
	throw new NoSetHierarchyException();
//         Map listSetsMap = new HashMap();
//         listSetsMap.put("sets", setsList.iterator());
//         return listSetsMap;
    }


    @Override
    public Map listSets(String resumptionToken) throws BadResumptionTokenException {
	throw new BadResumptionTokenException();
    }


    /**
     * close the repository
     */
    @Override
    public void close() { }


    /**
     * Purge tokens that are older than the time-to-live.
     */
    private void purge() {
        ArrayList old = new ArrayList();
        Date      then, now = new Date();
        Iterator  keySet = resumptionResults.keySet().iterator();
        String    key;

        while (keySet.hasNext()) {
            key=(String)keySet.next();
            then=new Date(Long.parseLong(key)+getMillisecondsToLive());
            if (now.after(then)) {
                old.add(key);
            }
        }
        for (Object anOld : old) {
            key = (String) anOld;
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
