
/**
 *Copyright (c) 2000-2002 OCLC Online Computer Library Center,
 *Inc. and other contributors. All rights reserved.  The contents of this file, as updated
 *from time to time by the OCLC Office of Research, are subject to OCLC Research
 *Public License Version 2.0 (the "License"); you may not use this file except in
 *compliance with the License. You may obtain a current copy of the License at
 *http://purl.oclc.org/oclc/research/ORPL/.  Software distributed under the License is
 *distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 *or implied. See the License for the specific language governing rights and limitations
 *under the License.  This software consists of voluntary contributions made by many
 *individuals on behalf of OCLC Research. For more information on OCLC Research,
 *please see http://www.oclc.org/oclc/research/.
 *
 *The Original Code is DummyOAICatalog.java.
 *The Initial Developer of the Original Code is Jeff Young.
 *Portions created by Diogo Mena Reis
 */


package pt.utl.ist.repox.oai.server.catalog;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.oclc.oai.server.catalog.AbstractCatalog;
import org.oclc.oai.server.verb.*;
import org.oclc.oai.util.OAIUtil;
import pt.utl.ist.repox.Urn;
import pt.utl.ist.repox.accessPoint.AccessPointsManager;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.metadataSchemas.MetadataSchemaManager;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.oai.DataSourceOai;
import pt.utl.ist.repox.oai.OaiListResponse;
import pt.utl.ist.repox.oai.OaiListResponse.OaiItem;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.XmlUtil;
import pt.utl.ist.util.InvalidInputException;
import pt.utl.ist.util.TransformationResultLogger;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class DataSourceOaiCatalog extends AbstractCatalog {
    private static final Logger log = Logger.getLogger(DataSourceOaiCatalog.class);
    /**
     * maximum number of entries to return for ListRecords and ListIdentifiers
     */
    private static int maxListSize;

    /**
     * pending resumption tokens
     */
    private HashMap resumptionResults = new HashMap();

    /**
     * Construct a DummyOAICatalog object
     *
     * @param properties a properties object containing initialization parameters
     */
    public DataSourceOaiCatalog(Properties properties) {
        String maxListSize = properties.getProperty("DataSourceOAICatalog.maxListSize");
        if (maxListSize == null) {
            throw new IllegalArgumentException("DataSourceOAICatalog.maxListSize is missing from the properties file");
        } else {
            DataSourceOaiCatalog.maxListSize = Integer.parseInt(maxListSize);
        }
    }

    @Override
    public Vector getSchemaLocations(String identifier) throws IdDoesNotExistException, NoMetadataFormatsException, OAIInternalServerError {
        return null;
    }

    /**
     * Retrieve a list of schemaLocation values associated with the specified
     * identifier.
     *
     * @param identifier the OAI identifier
     * @return a Vector containing schemaLocation Strings
     * @exception IdDoesNotExistException the specified identifier can't be found
     * @exception NoMetadataFormatsException the specified identifier was found
     * but the item is flagged as deleted and thus no schemaLocations (i.e.
     * metadataFormats) can be produced.
     */
    public Map<String, String> getRepoxSchemaLocations(String identifier) throws IdDoesNotExistException, NoMetadataFormatsException {
        Map<String, String> schemaLocationsMap = new HashMap<String, String>();
        MetadataSchemaManager metadataSchemaManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager();

        try {
            if(identifier == null || identifier.isEmpty()) {
                HashMap<String, DataSourceContainer> dataSourceContainers = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().loadDataSourceContainers();
                for (DataSourceContainer dataSourceContainer : dataSourceContainers.values()) {

                    for (MetadataTransformation metadataTransformation : dataSourceContainer.getDataSource().getMetadataTransformations().values()) {
                        if(metadataSchemaManager.isMetadataSchemaOaiAvailable(metadataTransformation.getDestinationFormat()))
                            schemaLocationsMap.put(metadataTransformation.getDestinationFormat(),
                                    metadataTransformation.getDestNamespace() + " " + metadataTransformation.getDestSchema());
                    }

                    if(metadataSchemaManager.isMetadataSchemaOaiAvailable(dataSourceContainer.getDataSource().getMetadataFormat()))
                        schemaLocationsMap.put(dataSourceContainer.getDataSource().getMetadataFormat(),
                                dataSourceContainer.getDataSource().getNamespace() + " " + dataSourceContainer.getDataSource().getSchema());
                }
            }
            else {
                try{
                    Urn urn = new Urn(identifier);
                    if(urn.getRecordId() == null || ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().getRecord(urn) == null) {
                        throw new IdDoesNotExistException("Record with identifier: " + identifier + " does not exist.");
                    }
                    DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(urn.getDataSourceId()).getDataSource();
                    if(metadataSchemaManager.isMetadataSchemaOaiAvailable(dataSource.getMetadataFormat()))
                        schemaLocationsMap.put(dataSource.getMetadataFormat(),
                                dataSource.getNamespace() + " " + dataSource.getSchema());
                }
                catch (InvalidInputException e){
                    throw new IdDoesNotExistException("Record with identifier: " + identifier + " does not exist.");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (DocumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        /*catch (Exception e) {
            log.error("Error retrieving schema locations for identifier: " + identifier, e);
            return null;
        }*/

        return schemaLocationsMap;
    }



    /*private String getSchemaLocation(String metadataPrefix) {
        String schemaURL = null;

        if (metadataPrefix != null) {
            if(metadataPrefix.equals("MarcXchange")) {
                schemaURL = "info:lc/xmlns/marcxchange-v1 info:lc/xmlns/marcxchange-v1.xsd";
            }
            else if(metadataPrefix.equals("oai_dc")){
                schemaURL = "http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd";
            }
            else if(metadataPrefix.equals("ese")){
                schemaURL = "http://www.europeana.eu/schemas/ese/ http://www.europeana.eu/schemdaas/ese/ESE-V3.3.xsd";
            }
            else if(metadataPrefix.equals("tel")){
                schemaURL = "http://krait.kb.nl/coop/tel/handbook/telterms.html ";
            }
            else {
                schemaURL = " ";
            }
        }
        return schemaURL;
    }*/

    /**
     * Retrieve a list of identifiers that satisfy the specified criteria
     *
     * @param from beginning date using the proper granularity
     * @param until ending date using the proper granularity
     * @param set the set name or null if no such limit is requested
     * @param metadataPrefix the OAI metadataPrefix or null if no such limit is requested
     * @return a Map object containing entries for "headers" and "identifiers" Iterators
     * (both containing Strings) as well as an optional "resumptionMap" Map.
     * It may seem strange for the map to include both "headers" and "identifiers"
     * since the identifiers can be obtained from the headers. This may be true, but
     * AbstractCatalog.listRecords() can operate quicker if it doesn't
     * need to parse identifiers from the XML headers itself. Better
     * still, do like I do below and override AbstractCatalog.listRecords().
     * AbstractCatalog.listRecords() is relatively inefficient because given the list
     * of identifiers, it must call getRecord() individually for each as it constructs
     * its response. It's much more efficient to construct the entire response in one fell
     * swoop by overriding listRecords() as I've done here.
     * @throws BadArgumentException
     */
    @Override
    public Map listIdentifiers(String from, String until, String set, String metadataPrefix) throws BadArgumentException, NoItemsMatchException, CannotDisseminateFormatException {
        purge(); // clean out old resumptionTokens
        return getListRecords(from, until, set, metadataPrefix, 0, -1, false);
    }

    /**
     * Retrieve the next set of identifiers associated with the resumptionToken
     *
     * @param resumptionToken implementation-dependent format taken from the
     * previous listIdentifiers() Map result.
     * @return a Map object containing entries for "headers" and "identifiers" Iterators
     * (both containing Strings) as well as an optional "resumptionMap" Map.
     * @exception BadResumptionTokenException the value of the resumptionToken
     * is invalid or expired.
     */
    @Override
    public Map listIdentifiers(String resumptionToken) throws BadResumptionTokenException, NoItemsMatchException {
        purge(); // clean out old resumptionTokens

        try {
            return getListRecords(resumptionToken, false);
        } catch (BadArgumentException e) {
            return null;
        }
        catch (CannotDisseminateFormatException e) {
            return null;
        } catch (RuntimeException e) {
            log.error("Error retrieving record ids with resumptionToken: " + resumptionToken, e);
            throw new BadResumptionTokenException();
        }
    }


    /**
     * Retrieve a list of records that satisfy the specified criteria. Note, though,
     * that unlike the other OAI verb type methods implemented here, both of the
     * listRecords methods are already implemented in AbstractCatalog rather than
     * abstracted. This is because it is possible to implement ListRecords as a
     * combination of ListIdentifiers and GetRecord combinations. Nevertheless,
     * I suggest that you override both the AbstractCatalog.listRecords methods
     * here since it will probably improve the performance if you create the
     * response in one fell swoop rather than construct it one GetRecord at a time.
     *
     * @param from beginning date using the proper granularity
     * @param until ending date using the proper granularity
     * @param set the set name or null if no such limit is requested
     * @param metadataPrefix the OAI metadataPrefix or null if no such limit is requested
     * @return a Map object containing entries for a "records" Iterator object
     * (containing XML <record/> Strings) and an optional "resumptionMap" Map.
     * @exception CannotDisseminateFormatException the metadataPrefix isn't
     * supported by the item.
     * @throws BadArgumentException
     */
    @Override
    public Map listRecords(String from, String until, String set, String metadataPrefix) throws CannotDisseminateFormatException, BadArgumentException, NoItemsMatchException {
        purge(); // clean out old resumptionTokens

        return getListRecords(from, until, set, metadataPrefix, 0, -1, true);
    }

    /**
     * Retrieve the next set of records associated with the resumptionToken
     *
     * @param resumptionToken implementation-dependent format taken from the
     * previous listRecords() Map result.
     * @return a Map object containing entries for "headers" and "identifiers" Iterators
     * (both containing Strings) as well as an optional "resumptionMap" Map.
     * @exception BadResumptionTokenException the value of the resumptionToken argument
     * is invalid or expired.
     * @throws CannotDisseminateFormatException
     */
    @Override
    public Map listRecords(String resumptionToken) throws BadResumptionTokenException, NoItemsMatchException {
        purge(); // clean out old resumptionTokens

        try {
            return getListRecords(resumptionToken, true);
        } catch (BadArgumentException e) {
            return null;
        } catch (CannotDisseminateFormatException e) {
            return null;
        } catch (RuntimeException e) {
            log.error("Error retrieving records with resumptionToken: " + resumptionToken, e);
            throw new BadResumptionTokenException();
        }
    }

    private Map getListRecords(String resumptionToken, boolean fullRecord) throws CannotDisseminateFormatException, NoItemsMatchException, BadArgumentException {
        log.debug("resumptionToken: " + resumptionToken);

        // parse resumptionToken
        String[] resumptionParameters = resumptionToken.split(":");

        if(resumptionParameters.length < 5 || resumptionParameters.length > 7) {
            throw new IllegalArgumentException("Invalid resumptionToken");
        }

        String resumptionId = resumptionParameters[0];
        if(log.isDebugEnabled())
            log.debug("resumptionId: " + resumptionId);
        String set = resumptionParameters[1];
        if(log.isDebugEnabled())
            log.debug("set: " + set);
        String metadataPrefix = resumptionParameters[2];
        if(log.isDebugEnabled())
            log.debug("metadataPrefix: " + metadataPrefix);
        int offset = Integer.parseInt(resumptionParameters[3]);
        if(log.isDebugEnabled())
            log.debug("offset: " + offset);

        int collectionSize = Integer.parseInt(resumptionParameters[4]);
        if(log.isDebugEnabled())
            log.debug("collectionSize: " + collectionSize);

        String from= null;
        String until= null;
        if(resumptionParameters.length >= 6) {
            String fromToken = resumptionParameters[5];
            if(log.isDebugEnabled())
                log.debug("fromToken: " + fromToken);
            from = (fromToken.isEmpty() ? null : fromToken);
        }
        if(resumptionParameters.length >= 7) {
            String untilToken = resumptionParameters[6];
            if(log.isDebugEnabled())
                log.debug("untilToken: " + untilToken);
            until = (untilToken.isEmpty() ? null : untilToken);
        }

        return getListRecords(from, until, set, metadataPrefix, offset, collectionSize, fullRecord);
    }

    private Map getListRecords(String from, String until, String set, String metadataPrefix, int offset,
                               int collectionSize, boolean fullRecord)
            throws CannotDisseminateFormatException, NoItemsMatchException, BadArgumentException {
        DataSource dataSource = null;
        Map listObjectsMap = new HashMap();
        List<String> finalHeaders = new ArrayList<String>();
        List<String> finalItems = new ArrayList<String>();

        MetadataSchemaManager metadataSchemaManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager();

        try {



            // test if set is empty
            if(set == null || set.isEmpty()){
                set = "";
                List<OaiSet> dataSetIds = new ArrayList<OaiSet>();
                // read all data sets
                int totalRecordsSize = 0;
                for (DataProvider provider : ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProviders()){
                    for(DataSourceContainer dataSourceContainer : provider.getDataSourceContainers().values()){
                        int setTotalRecord = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager()
                                .getRecordCount(dataSourceContainer.getDataSource().getId()).getCount();

                        if(dataSourceContainer.getDataSource().getMetadataFormat().equals(metadataPrefix) &&
                                metadataSchemaManager.isMetadataSchemaOaiAvailable(metadataPrefix)){
                            dataSetIds.add(new OaiSet(dataSourceContainer.getDataSource().getId(),String.valueOf(setTotalRecord),
                                    dataSourceContainer.getDataSource().getMetadataFormat()));
                            totalRecordsSize+=setTotalRecord;
                        }else if(dataSourceContainer.getDataSource().hasTransformation(metadataPrefix)&&
                                metadataSchemaManager.isMetadataSchemaOaiAvailable(metadataPrefix)){

                            dataSetIds.add(new OaiSet(dataSourceContainer.getDataSource().getId(),String.valueOf(setTotalRecord),
                                    dataSourceContainer.getDataSource().getMetadataFormat()));
                            totalRecordsSize+=setTotalRecord;
                        }
                    }
                }

                //todo check if it make works properly
                if(totalRecordsSize == 0){
                    // it means that does not exist sets using this metadata format
                    throw new CannotDisseminateFormatException(metadataPrefix);
                }

                Collections.sort(dataSetIds, OaiSet.getComparator());

                int difference;
                int accumulated = 0;
                int numberRecords = 0;
                for(OaiSet oaiSet : dataSetIds){
                    dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().
                            getDataManager().getDataSourceContainer(oaiSet.getDataSetId()).getDataSource();
                    if(dataSource == null) {
                        throw new BadArgumentException();
                    }

                    if(!isMetadataPrefixValid(metadataPrefix, dataSource)) {
                        throw new CannotDisseminateFormatException(metadataPrefix);
                    }

                    difference = Integer.valueOf(oaiSet.getTotalRecordNumber()) - offset + accumulated;
                    if(numberRecords <= maxListSize && difference > 0 && Integer.valueOf(oaiSet.getTotalRecordNumber()) > 0){

                        TransformationResultLogger transformationResultLogger;
                        DataSource currentDataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(oaiSet.getDataSetId()).getDataSource();
                        if(currentDataSource.hasTransformation(metadataPrefix)){
                            String transId = "NO_ID";
                            for(MetadataTransformation metadataTransformation : currentDataSource.getMetadataTransformations().values()){
                                if(metadataTransformation.getDestinationFormat().equals(metadataPrefix)){
                                    transId = metadataTransformation.getId();
                                }
                            }
                            transformationResultLogger = new TransformationResultLogger(set,transId);
                        }else
                            transformationResultLogger = new TransformationResultLogger();

                        Map dataSetMap = getRecordsFromDataSet(from, until, oaiSet.getDataSetId(), metadataPrefix,
                                (offset - accumulated) > 0 ? offset - accumulated : 0, fullRecord, dataSource, set, totalRecordsSize,
                                numberRecords == 0 ? -1 : Math.abs(numberRecords - maxListSize), offset,
                                /*collectionSize*/Integer.valueOf(oaiSet.getTotalRecordNumber()), transformationResultLogger);
                        listObjectsMap.put("resumptionMap", dataSetMap.get("resumptionMap"));

                        if(fullRecord) {
                            finalItems.addAll((List<String>)dataSetMap.get("records"));
                            numberRecords += ((List<String>)dataSetMap.get("records")).size();
                        }
                        else {
                            finalHeaders.addAll((List<String>)dataSetMap.get("headers"));
                            finalItems.addAll((List<String>)dataSetMap.get("identifiers"));
                            numberRecords += ((List<String>)dataSetMap.get("identifiers")).size();
                        }
                        if(listObjectsMap.get("resumptionMap") != null){
                            break;
                        }
                    }

                    accumulated += Integer.valueOf(oaiSet.getTotalRecordNumber());
                }
            }
            else{
                DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(set);
                if(dataSourceContainer == null) {
                    throw new BadArgumentException();
                }
                dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(set).getDataSource();

                if(!isMetadataPrefixValid(metadataPrefix, dataSource) ||
                        !metadataSchemaManager.isMetadataSchemaOaiAvailable(metadataPrefix)) {
                    throw new CannotDisseminateFormatException(metadataPrefix);
                }

                TransformationResultLogger transformationResultLogger;
                DataSource currentDataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(set).getDataSource();
                if(currentDataSource.hasTransformation(metadataPrefix)){
                    String transId = "NO_ID";
                    for(MetadataTransformation metadataTransformation : currentDataSource.getMetadataTransformations().values()){
                        if(metadataTransformation.getDestinationFormat().equals(metadataPrefix)){
                            transId = metadataTransformation.getId();
                        }
                    }
                    transformationResultLogger = new TransformationResultLogger(set,transId);
                }else
                    transformationResultLogger = new TransformationResultLogger();

                Map dataSetMap = getRecordsFromDataSet(from, until, set, metadataPrefix, offset, fullRecord, dataSource,
                        set, -1, -1, -1, collectionSize, transformationResultLogger);
                listObjectsMap.put("resumptionMap",dataSetMap.get("resumptionMap"));
                if(fullRecord) {
                    finalItems.addAll((List<String>)dataSetMap.get("records"));
                }
                else {
                    finalHeaders.addAll((List<String>)dataSetMap.get("headers"));
                    finalItems.addAll((List<String>)dataSetMap.get("identifiers"));
                }
            }
        } catch (DocumentException e) {
            log.error(e.getMessage(),e);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(fullRecord) {
            listObjectsMap.put("records", finalItems.iterator());
        }
        else {
            listObjectsMap.put("headers", finalHeaders.iterator());
            listObjectsMap.put("identifiers", finalItems.iterator());
        }
        return listObjectsMap;
    }

    private Map getRecordsFromDataSet(String from, String until, String set, String metadataPrefix, int offset,
                                      boolean fullRecord, DataSource dataSource, String offsetSet, int totalRecordsSize,
                                      int maxNumRecords2Return, int originalOffset, int totalSetRecords,
                                      TransformationResultLogger transformationResultLogger) throws NoItemsMatchException{
        from = (from == null || from.startsWith("0001-01-01") ? null : from.substring(0, 10));
        until = (until == null || until.startsWith("9999-12-31") ? null : until.substring(0, 10));

        List<String> headers = new ArrayList<String>();
        List<String> items = new ArrayList<String>();
        Map dataSetMap = new HashMap();

        //int totalSetRecords;
        OaiListResponse oaiListResponse;
        try {
            AccessPointsManager accessPointsManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager();


            if((from != null || until != null) && totalSetRecords == -1){
                // used to fill the completeListSize field
                totalSetRecords = accessPointsManager.getRecordCountLastrowPair(dataSource, 0, from, until)[0];
                //totalSetRecords = accessPointsManager.getOaiRecordsFromDataSource(dataSource, from, until, offset, -1, !fullRecord).getOaiItems().size();
            }
            else if(totalSetRecords == -1){
                totalSetRecords = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(dataSource.getId()).getCount();
            }

            log.debug("Total number of Records from " + dataSource.getId() + " :" + totalSetRecords);

            //NOTE: we get 1 more record than maxlist for the resumptionToken and to avoid a last request with no results
            oaiListResponse = accessPointsManager.getOaiRecordsFromDataSource(dataSource, from, until, offset, maxNumRecords2Return != -1 ? maxNumRecords2Return + 1 : maxListSize + 1, !fullRecord);
            log.debug("Total number of Records from specific query" + oaiListResponse.getOaiItems().size());
        }
        catch(Exception e) {
            log.error("Error getting Records", e);
            return null;
        }

        int counter = 0;

        for (OaiItem currentItem : oaiListResponse.getOaiItems()) { // load the headers and identifiers ArrayLists.
            try {
                if(counter >= maxListSize || (maxNumRecords2Return != -1 && counter >= maxNumRecords2Return)) {
                    break;
                }
                counter++;
                String identifier = new Urn(currentItem.getSetSpec(), currentItem.getIdentifier()).toString();
                String encodedIdentifier = OAIUtil.xmlEncode(identifier);

                StringBuffer oaiRecordHeader = new StringBuffer("<" + OAIUtil.getTag("header"));
                if(currentItem.isDeleted()) {
                    oaiRecordHeader.append(" status=\"deleted\"");
                }
                oaiRecordHeader.append("><" + OAIUtil.getTag("identifier") + ">").append(encodedIdentifier).append("</" +  OAIUtil.getTag("identifier") + ">").append("<" +  OAIUtil.getTag("datestamp") + ">")
                        .append(currentItem.getDatestamp()).append("</" + OAIUtil.getTag("datestamp") + ">").append("<" + OAIUtil.getTag("setSpec") + ">").append(currentItem.getSetSpec())
                        .append("</" + OAIUtil.getTag("setSpec") + "></" + OAIUtil.getTag("header") + ">");

                if(!fullRecord) {
                    headers.add(oaiRecordHeader.toString());
                    items.add(encodedIdentifier);
                } else {
                    StringBuffer oaiMetadata = new StringBuffer();
                    if(!currentItem.isDeleted()) {
                        String xmlRecordString = (currentItem.getMetadata() != null ? new String(currentItem.getMetadata(),
                                "UTF-8") : "");
                        xmlRecordString = getTransformedRecord(encodedIdentifier, metadataPrefix, dataSource,
                                xmlRecordString);
                        if(xmlRecordString.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) {
                            xmlRecordString = xmlRecordString.substring(
                                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".length());
                        }
                        oaiMetadata.append("<" + OAIUtil.getTag("metadata") + ">").append(xmlRecordString).append("</" + OAIUtil.getTag("metadata") + ">");
                    }
                    String oaiProvenance = "";
                    if(dataSource instanceof DataSourceOai) {
                        oaiProvenance = getOaiProvenance((DataSourceOai)dataSource, currentItem);
                    }
                    StringBuffer oaiRecord = new StringBuffer("<" + OAIUtil.getTag("record") + ">").append(oaiRecordHeader.toString()).append(oaiMetadata.toString()).append(oaiProvenance).append("</" + OAIUtil.getTag("record") + ">");

                    //Nuno: I removed this extra xml parsing, since it seamed not necessary
                    items.add(oaiRecord.toString());
//	                try {
//	                	String record = DocumentHelper.parseText(oaiRecord).getRootElement().asXML();
//	                	items.add(record);
//	                }
//	                catch (Exception e) {
//	                	log.error("Error parsing Record", e);
//	                }
                }
            } catch (NullPointerException e) {
                log.error("Error - XSL is version 2 and is currently configured as version 1", e);
                transformationResultLogger.addRecordErrorEntry(currentItem.getIdentifier(),"Error - XSL is version 2 and is currently configured as version 1");
            } catch (Exception e) {
                log.error("Error getting Record in the correct format -" +
                        " Set: " + currentItem.getSetSpec() + " ID: " + currentItem.getIdentifier(), e);
                transformationResultLogger.addRecordErrorEntry(currentItem.getIdentifier(),e.getMessage());
            }
        }
        if (counter == 0)
            throw new NoItemsMatchException();
        if(log.isDebugEnabled()) {
            log.debug("offset + recordObjects.size(): " + (offset + oaiListResponse.getOaiItems().size()));
            log.debug("numberRecords: " + oaiListResponse.getOaiItems().size());
        }

        // we got one more record than required in the request, if it exists, a resumptionToken is required
        if(maxNumRecords2Return == -1){
            if(oaiListResponse.getOaiItems().size() > maxListSize && totalRecordsSize == -1) {
                String resumptionToken = getResumptionToken(offsetSet, metadataPrefix, oaiListResponse.getLastRequestedIdentifier(),
                        offsetSet.isEmpty() ? String.valueOf(totalRecordsSize) : String.valueOf(totalSetRecords), from, until);
                //String resumptionToken = getResumptionToken(offsetSet, metadataPrefix, oaiListResponse.getLastRequestedIdentifier(), String.valueOf(totalSetRecords), from, until);
                dataSetMap.put("resumptionMap", getResumptionMap(resumptionToken, /*offsetSet.isEmpty() ? totalRecordsSize : totalSetRecords*/totalSetRecords, offset));
            }
            else if(oaiListResponse.getOaiItems().size() > maxListSize &&
                    totalRecordsSize > (originalOffset + maxListSize)){
                String resumptionToken = getResumptionToken(offsetSet, metadataPrefix, originalOffset + maxListSize,
                        offsetSet.isEmpty() ? String.valueOf(totalRecordsSize) : String.valueOf(totalSetRecords), from, until);
                //String resumptionToken = getResumptionToken(offsetSet, metadataPrefix, originalOffset + maxListSize, String.valueOf(totalSetRecords), from, until);
                dataSetMap.put("resumptionMap", getResumptionMap(resumptionToken, offsetSet.isEmpty() ? totalRecordsSize : totalSetRecords, originalOffset));
            }
        }
        else{
            if(oaiListResponse.getOaiItems().size() >= maxNumRecords2Return) {
                String resumptionToken = getResumptionToken(offsetSet, metadataPrefix, originalOffset + maxListSize,
                        offsetSet.isEmpty() ? String.valueOf(totalRecordsSize) : String.valueOf(totalSetRecords), from, until);
                //String resumptionToken = getResumptionToken(offsetSet, metadataPrefix, originalOffset + maxListSize, String.valueOf(totalSetRecords), from, until);
                dataSetMap.put("resumptionMap", getResumptionMap(resumptionToken, offsetSet.isEmpty() ? totalRecordsSize : totalSetRecords, originalOffset));
            }
        }

        if(fullRecord) {
            dataSetMap.put("records", items);
        }
        else {
            dataSetMap.put("headers", headers);
            dataSetMap.put("identifiers", items);
        }

        transformationResultLogger.persistData();
        return dataSetMap;
    }


    private String getOaiProvenance(DataSourceOai dataSource, OaiItem oaiItem) {
        StringBuffer oaiProvenance=new StringBuffer();
        String metadataNamespace = "";
        if (dataSource.getNamespace()!=null)
            metadataNamespace=dataSource.getNamespace();
//
//        try {
//            Namespace namespace = DocumentHelper.parseText(xmlRecordString).getRootElement().getDestNamespace();
//            if(!namespace.equals(Namespace.NO_NAMESPACE)) {
//                metadataNamespace = namespace.getURI();
//            }
//        }
//        catch (Exception e) {
//        }


        StringBuffer originDescription =new StringBuffer("<oaiProvenance:originDescription harvestDate=\"")
                .append(oaiItem.getDatestamp()).append("\" altered=\"true\">")
                .append("<oaiProvenance:baseURL>").append(dataSource.getOaiSourceURL()).append("</oaiProvenance:baseURL>")
                .append("<oaiProvenance:identifier>").append(oaiItem.getIdentifier()).append("</oaiProvenance:identifier>")
                .append("<oaiProvenance:datestamp>").append(oaiItem.getDatestamp()).append("</oaiProvenance:datestamp>")
                .append("<oaiProvenance:metadataNamespace>").append(metadataNamespace).append("</oaiProvenance:metadataNamespace>")
                .append("</oaiProvenance:originDescription>");

        oaiProvenance.append("<" + OAIUtil.getTag("about") + "><oaiProvenance:provenance xmlns:oaiProvenance=\"http://www.openarchives.org/OAI/2.0/provenance\"")
                .append( " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"")
                .append( "  xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/provenance ")
                .append( "http://www.openarchives.org/OAI/2.0/provenance.xsd\">")
                .append( originDescription.toString())
                .append( "</oaiProvenance:provenance></" + OAIUtil.getTag("about") + ">");
        return oaiProvenance.toString();
    }

    /*****************************************************************
     * Construct the resumptionToken String however you see fit.
     * [id]:[set]:[metadataPrefix]:[offset]:[collectionSize]:[from]:[until]
     *****************************************************************/
    private String getResumptionToken(String set, String metadataPrefix, int offset, String collectionSize, String from, String until) {
        StringBuffer resumptionTokenSb = new StringBuffer();
        resumptionTokenSb.append(getResumptionId());
        resumptionTokenSb.append(":");
        resumptionTokenSb.append(set);
        resumptionTokenSb.append(":");
        resumptionTokenSb.append(metadataPrefix);
        resumptionTokenSb.append(":");
        resumptionTokenSb.append(offset);
        resumptionTokenSb.append(":");
        resumptionTokenSb.append(collectionSize == null ? "" : collectionSize);
        resumptionTokenSb.append(":");
        resumptionTokenSb.append(from == null ? "" : from);
        resumptionTokenSb.append(":");
        resumptionTokenSb.append(until == null ? "" : until);
        return resumptionTokenSb.toString();
    }


    /**
     * Retrieve the specified metadata for the specified identifier
     *
     * @param identifier the OAI identifier
     * @param metadataPrefix the OAI metadataPrefix
     * @return the <record/> portion of the XML response.
     * @exception CannotDisseminateFormatException the metadataPrefix is not
     * supported by the item.
     * @exception IdDoesNotExistException the identifier wasn't found
     */
    @Override
    public String getRecord(String identifier, String metadataPrefix) throws CannotDisseminateFormatException, IdDoesNotExistException {
        Urn urn = null;
        DataSource dataSource = null;

        try {
            urn = new Urn(identifier);

            try{
                if(urn == null
                        || urn.getRecordId() == null
                        || ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().getRecord(urn) == null) {
                    throw new IdDoesNotExistException("Record with identifier: " + identifier + " does not exist.");
                }
                dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(urn.getDataSourceId()).getDataSource();
            }
            catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }

            if(!isMetadataPrefixValid(metadataPrefix, dataSource)) {
                throw new CannotDisseminateFormatException(metadataPrefix);
            }

        } catch (InvalidInputException e){
            ArrayList<DataSource> dataSources = new ArrayList<DataSource>();
            // check if it exist as partial ID
            try {
                for (String dataSourceId : ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().loadDataSourceContainers().keySet()) {
                    DataSource dataSourceTemp = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().loadDataSourceContainers().get(dataSourceId).getDataSource();
                    if(isMetadataPrefixValid(metadataPrefix, dataSourceTemp)) {
                        dataSources.add(dataSourceTemp);
                    }
                }
                HashMap<String, DataSource> element = getIdBySuffix(dataSources, metadataPrefix, identifier);
                if(element == null)
                    return null;

                for (String completeId : element.keySet()) {
                    urn = new Urn(completeId);
                    dataSource = element.get(completeId);
                    identifier = completeId;
                }

            } catch (Exception e1) {
                return null;
            }
        }



        try {
            OaiItem oaiItem = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().getRecord(urn);
            Element rootElement = XmlUtil.getRootElement(oaiItem.getMetadata());
            RecordRepox recordRepox = dataSource.getRecordIdPolicy().createRecordRepox(rootElement, urn.getRecordId().toString(), false, oaiItem.isDeleted());

            String oaiRecordHeader = "<" + OAIUtil.getTag("header");

            if(recordRepox.isDeleted()) {
                oaiRecordHeader += " status=\"deleted\"";
            }

            String encodedIdentifier = OAIUtil.xmlEncode(identifier);
            oaiRecordHeader = oaiRecordHeader + "><" + OAIUtil.getTag("identifier") + ">" + encodedIdentifier + "</" + OAIUtil.getTag("identifier") + ">"
                    + "<" +  OAIUtil.getTag("datestamp") + ">" + oaiItem.getDatestamp() + "</" + OAIUtil.getTag("datestamp") + ">"
                    + "<" +  OAIUtil.getTag("setSpec") + ">" + urn.getDataSourceId() + "</" +  OAIUtil.getTag("setSpec") + "></" + OAIUtil.getTag("header") + ">";

            String xmlRecordString = "";

            if(!recordRepox.isDeleted()) {
                xmlRecordString = getTransformedRecord(encodedIdentifier, metadataPrefix, dataSource, recordRepox.getDom().asXML());
                if(xmlRecordString.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) {
                    xmlRecordString = xmlRecordString.substring(new String("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").length());
                }
            }

            String oaiMetadata = (recordRepox.isDeleted() ? "" : "<" + OAIUtil.getTag("metadata") + ">" + xmlRecordString + "</" + OAIUtil.getTag("metadata") + ">");

            String oaiProvenance = "";
            if(dataSource instanceof DataSourceOai) {
                oaiProvenance = getOaiProvenance((DataSourceOai)dataSource, oaiItem);
            }

//            String oaiRecord = "<" + OAIUtil.getTag("record") + " xmlns:oai=\"http://www.openarchives.org/OAI/2.0/\">" + oaiRecordHeader + oaiMetadata + oaiProvenance + "</" + OAIUtil.getTag("record") + ">";
            String oaiRecord = "<" + OAIUtil.getTag("record") + " xmlns" + (ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().isUseOAINamespace() ? ":oai" : "") + "=\"http://www.openarchives.org/OAI/2.0/\">" + oaiRecordHeader + oaiMetadata + oaiProvenance + "</" + OAIUtil.getTag("record") + ">";

            String record = DocumentHelper.parseText(oaiRecord).getRootElement().asXML();
            return record;
        }
        catch(Exception e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }

    public static String getTransformedRecord(String encodedIdentifier, String metadataPrefix, DataSource dataSource, String xmlRecordString)
            throws DocumentException, TransformerException, NullPointerException {
        try{
            if(metadataPrefix.equals("MarcXchange") && dataSource.getMetadataFormat().equals("ISO2709")) {
                return xmlRecordString;
            }
            else if(!dataSource.getMetadataFormat().equals(metadataPrefix) && !xmlRecordString.isEmpty()) {

                for(MetadataTransformation metadataTransformation : dataSource.getMetadataTransformations().values()){
                    if(metadataTransformation.getDestinationFormat().equals(metadataPrefix)){
                        DataProvider dataProviderParent = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProviderParent(dataSource.getId());
                        xmlRecordString = metadataTransformation.transform(encodedIdentifier, xmlRecordString, dataProviderParent.getName());
                        return xmlRecordString;
                    }
                }
            }
            return xmlRecordString;
        }catch (NullPointerException e){
            throw new NullPointerException();
        }
    }

    private boolean isMetadataPrefixValid(String metadataPrefix, DataSource dataSource) {
        boolean isValid = true;

        if(metadataPrefix.equals("MarcXchange") && dataSource.getMetadataFormat().equals("ISO2709")) {
            isValid = true;
        }
        else if(dataSource == null ||
                (!dataSource.getMetadataFormat().equals(metadataPrefix)
                        && !dataSource.hasTransformation(metadataPrefix))) {
            isValid = false;
        }

        return isValid;
    }


    /**
     * Retrieve a list of sets that satisfy the specified criteria
     *
     * @return a Map object containing "sets" Iterator object (contains
     * <setSpec/> XML Strings) as well as an optional resumptionMap Map.
     */
    @Override
    public Map listSets() {
        purge(); // clean out old resumptionTokens

        Map listSetsMap = new HashMap();
        List sets = new ArrayList();

        try {
            HashMap<String, DataSourceContainer> dataSourceContainers = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().loadDataSourceContainers();

            for (DataSourceContainer dataSourceContainer : dataSourceContainers.values()) {
                sets.add("<" + OAIUtil.getTag("set") + "><" + OAIUtil.getTag("setSpec") + ">" + dataSourceContainer.getDataSource().getId() + "</" + OAIUtil.getTag("setSpec") + "><" + OAIUtil.getTag("setName") + ">"
                        + dataSourceContainer.getDataSource().getDescription() + "</" + OAIUtil.getTag("setName") + "></" + OAIUtil.getTag("set") + ">");
            }
        }
        catch(Exception e) {
            log.error(e.getMessage(),e);
            return null;
        }

        listSetsMap.put("sets", sets.iterator());
        return listSetsMap;
    }

    /**
     * Retrieve the next set of sets associated with the resumptionToken
     *
     * @param resumptionToken implementation-dependent format taken from the
     * previous listSets() Map result.
     * @return a Map object containing "sets" Iterator object (contains
     * <setSpec/> XML Strings) as well as an optional resumptionMap Map.
     * @exception BadResumptionTokenException the value of the resumptionToken
     * is invalid or expired.
     */
    @Override
    public Map listSets(String resumptionToken)
            throws BadResumptionTokenException {
        Map listSetsMap = new HashMap();
        ArrayList sets = new ArrayList();
        purge(); // clean out old resumptionTokens

        /**********************************************************************
         * YOUR CODE GOES HERE
         **********************************************************************/
        /**********************************************************************
         * parse your resumptionToken and look it up in the resumptionResults,
         * if necessary
         **********************************************************************/
        StringTokenizer tokenizer = new StringTokenizer(resumptionToken, ":");
        String resumptionId;
        int oldCount;
        try {
            resumptionId = tokenizer.nextToken();
            oldCount = Integer.parseInt(tokenizer.nextToken());
        } catch (NoSuchElementException e) {
            throw new BadResumptionTokenException();
        }

        /* Get some more sets */
        String[] dbSets = (String[])resumptionResults.remove(resumptionId);
        if (dbSets == null) {
            throw new BadResumptionTokenException();
        }
        int count;

        /* load the sets ArrayList */
        for (count = 0; count < maxListSize && count+oldCount < dbSets.length; ++count) {
            sets.add(dbSets[count+oldCount]);
        }

        /* decide if we're done */
        if (count+oldCount < dbSets.length) {
            resumptionId = getResumptionId();

            /*****************************************************************
             * Store an object appropriate for your database API in the
             * resumptionResults Map in place of nativeItems. This object
             * should probably encapsulate the information necessary to
             * perform the next resumption of ListIdentifiers. It might even
             * be possible to encode everything you need in the
             * resumptionToken, in which case you won't need the
             * resumptionResults Map. Here, I've done a silly combination
             * of the two. Stateless resumptionTokens have some advantages.
             *****************************************************************/
            resumptionResults.put(resumptionId, dbSets);

            /*****************************************************************
             * Construct the resumptionToken String however you see fit.
             *****************************************************************/
            StringBuffer resumptionTokenSb = new StringBuffer();
            resumptionTokenSb.append(resumptionId);
            resumptionTokenSb.append(":");
            resumptionTokenSb.append(Integer.toString(oldCount + count));

            /*****************************************************************
             * Use the following line if you wish to include the optional
             * resumptionToken attributes in the response. Otherwise, use the
             * line after it that I've commented out.
             *****************************************************************/
            listSetsMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(),
                    dbSets.length,
                    oldCount));
            //          listSetsMap.put("resumptionMap",
            //                                 getResumptionMap(resumptionTokenSb.toString()));
        }
        /***********************************************************************
         * END OF CUSTOM CODE SECTION
         ***********************************************************************/
        listSetsMap.put("sets", sets.iterator());
        return listSetsMap;
    }

    /**
     * close the repository
     */
    @Override
    public void close() { }

    /**
     * Purge tokens that are older than the configured time-to-live.
     */
    private void purge() {
        ArrayList old = new ArrayList();
        Date now = new Date();
        for (Object o : resumptionResults.keySet()) {
            String key = (String) o;
            Date then = new Date(Long.parseLong(key) + getMillisecondsToLive());
            if (now.after(then)) {
                old.add(key);
            }
        }
        for (Object anOld : old) {
            String key = (String) anOld;
            resumptionResults.remove(key);
        }
    }

    /**
     * Use the current date as the basis for the resumptiontoken
     *
     * @return a String version of the current time
     */
    private synchronized static String getResumptionId() {
        Date now = new Date();
        return Long.toString(now.getTime());
    }

    private HashMap<String, DataSource> getIdBySuffix(List<DataSource> dataSources, String metadataPrefix, String id){

        HashMap<String, DataSource> list = new HashMap<String, DataSource>();
        HashMap<String, DataSource> list2Return = new HashMap<String, DataSource>();
        try {

            for (DataSource dataSource : dataSources) {
                int offset = 0;
                int dataSetSize = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(dataSource.getId()).getCount();
                while(offset < dataSetSize){
                    Map listIdentifiersMap = getRecordsFromDataSet(null, null, dataSource.getId(), metadataPrefix,
                            offset,
                            false,
                            dataSource,
                            dataSource.getId(),
                            -1,
                            -1,
                            offset,
                            dataSetSize,
                            new TransformationResultLogger());

                    ArrayList<String> listTemp = new ArrayList<String>();
                    listTemp.addAll((List<String>) listIdentifiersMap.get("identifiers"));
                    for (String identifier : listTemp) {
                        if(identifier.endsWith(id)){
                            list.put(identifier, dataSource);
                        }
                    }
                    offset += maxListSize;
                }
            }

            // try to find the "best" result
            for (String internalId : list.keySet()) {
                if(internalId.endsWith(Urn.URN_SEPARATOR + id)){
                    list2Return.put(internalId, list.get(internalId));
                    return list2Return;
                }
            }
            if(list.size() > 0){
                // returns the first element of the list
                for (String id2Return : list.keySet()) {
                    list2Return.put(id2Return, list.get(id2Return));
                    return list2Return;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;

    }
}
