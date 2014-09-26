package pt.utl.ist.rest;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import pt.utl.ist.rest.services.web.rest.RestRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Gilberto Pedrosa
 * Date: 14-12-2012
 * Time: 22:06
 * To change this template use File | Settings | File Templates.
 */
public abstract class ResponseOperations {
    /**
     * Retrieve the list of the available operations over Aggregators
     * @param restRequest
     * @return
     */
    public Element getAggregatorOperationList(RestRequest restRequest) {
        Element aggregatorOperations = getOperationElement("dataProviderOperationsList",
                "Retrieve the list of the available operations over Aggregators",
                restRequest,
                "/rest/aggregators");

        Element aggregatorList = getOperationElement("aggregators",
                "Retrieve all available Aggregators",
                restRequest,
                "/rest/aggregators/list");

        Element aggregatorCreate = getOperationElement("aggregators",
                "Creates an Aggregator",
                restRequest,
                "/rest/aggregators/create");

        Element aggregatorUpdate = getOperationElement("aggregators",
                "Updates an Aggregator",
                restRequest,
                "/rest/aggregators/update");

        Element aggregatorDelete = getOperationElement("aggregators",
                "Deletes an Aggregator",
                restRequest,
                "/rest/aggregators/delete");

        Element aggregatorGet = getOperationElement("aggregators",
                "Gets an Aggregator",
                restRequest,
                "/rest/aggregators/getAggregator");

        Element rootElement = DocumentHelper.createElement("aggregatorOperationsList");
        rootElement.add(aggregatorOperations);
        rootElement.add(aggregatorList);
        rootElement.add(aggregatorCreate);
        rootElement.add(aggregatorUpdate);
        rootElement.add(aggregatorDelete);
        rootElement.add(aggregatorGet);
        return rootElement;
    }

    /**
     * Retrieve the list of the available operations over Data Providers
     * @param restRequest
     * @return
     */
    public Element getDataProviderOperationListLight(RestRequest restRequest) {
        Element dataProviderOperations = getOperationElement("dataProviderOperationsList",
                "Retrieve the list of the available operations over Data Providers",
                restRequest,
                "/rest/dataProviders");

        Element dataProviderList = getOperationElement("dataProviders",
                "Retrieve all available Data Providers",
                restRequest,
                "/rest/dataProviders/list");

        Element dataProviderCreate = getOperationElement("dataProviders",
                "Creates a Data Provider",
                restRequest,
                "/rest/dataProviders/create");

        Element dataProviderUpdate = getOperationElement("dataProviders",
                "Updates a Data Provider",
                restRequest,
                "/rest/dataProviders/update");

        Element dataProviderDelete = getOperationElement("dataProviders",
                "Deletes a Data Provider",
                restRequest,
                "/rest/dataProviders/delete");
        Element dataProviderGet = getOperationElement("dataProviders",
                "Gets a Data Provider",
                restRequest,
                "/rest/dataProviders/getDataProvider");

        Element rootElement = DocumentHelper.createElement("dataProviderOperationsList");
        rootElement.add(dataProviderOperations);
        rootElement.add(dataProviderList);
        rootElement.add(dataProviderCreate);
        rootElement.add(dataProviderUpdate);
        rootElement.add(dataProviderDelete);
        rootElement.add(dataProviderGet);
        return rootElement;
    }


    public Element getDataProviderOperationListEuropeana(RestRequest restRequest) {
        Element dataProviderOperations = getOperationElement("dataProviderOperationsList",
                "Retrieve the list of the available operations over Data Providers",
                restRequest,
                "/rest/dataProviders");

        Element dataProviderList = getOperationElement("dataProviders",
                "Retrieve all available Data Providers",
                restRequest,
                "/rest/dataProviders/list");

        Element dataSourceFromDataProviderList = getOperationElement("dataSources",
                "Retrieve all available Data Providers from a specific Aggregator",
                restRequest,
                "/rest/dataProviders/list?aggregatorId=AGGREGATOR_ID");

        Element dataProviderCreate = getOperationElement("dataProviders",
                "Creates a Data Provider",
                restRequest,
                "/rest/dataProviders/create");

        Element dataProviderUpdate = getOperationElement("dataProviders",
                "Updates a Data Provider",
                restRequest,
                "/rest/dataProviders/update");

        Element dataProviderDelete = getOperationElement("dataProviders",
                "Deletes a Data Provider",
                restRequest,
                "/rest/dataProviders/delete");

        Element dataProviderGet = getOperationElement("dataProviders",
                "Gets a Data Provider",
                restRequest,
                "/rest/dataProviders/getDataProvider");

        Element dataProviderMove = getOperationElement("dataProviders",
                "Moves a Data Provider from aggregator",
                restRequest,
                "/rest/dataProviders/move");

        Element rootElement = DocumentHelper.createElement("dataProviderOperationsList");
        rootElement.add(dataProviderOperations);
        rootElement.add(dataProviderList);
        rootElement.add(dataSourceFromDataProviderList);
        rootElement.add(dataProviderCreate);
        rootElement.add(dataProviderUpdate);
        rootElement.add(dataProviderDelete);
        rootElement.add(dataProviderMove);
        rootElement.add(dataProviderGet);
        return rootElement;
    }


    /**
     * Retrieve the list of the available operations over Data Providers
     * @param restRequest
     * @return
     */
    public Element getDataSourceOperationList(RestRequest restRequest) {
        Element dataSourceOperations = getOperationElement("dataSourceOperationsList",
                "Retrieve the list of the available operations over Data Sources",
                restRequest,
                "/rest/dataSources");

        Element dataSourceList = getOperationElement("dataSources",
                "Retrieves all available Data Sources",
                restRequest,
                "/rest/dataSources/list");

        Element dataSourceFromDataProviderList = getOperationElement("dataSources",
                "Retrieve all available Data Sources from a specific Data Provider",
                restRequest,
                "/rest/dataSources/list?dataProviderId=DATA_PROVIDER_ID");

        Element dataSourceCreateOai = getOperationElement("dataSources",
                "Creates a Data Source from OAI-PMH",
                restRequest,
                "/rest/dataSources/createOai?dataProviderId=DATA_PROVIDER_ID&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                        "&schema=SCHEMA&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT&oaiURL=URL_OAI_SERVER" +
                        "&oaiSet=OAI_SET");

        Element dataSourceCreateZ3950IdList = getOperationElement("dataSources",
                "Creates a Data Source from Z39.50 with Id List",
                restRequest,
                "/rest/dataSources/createZ3950IdList?dataProviderId=DATA_PROVIDER_ID&id=DATA_SOURCE_ID" +
                        "&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE&address=ADDRESS&port=PORT" +
                        "&database=DATABASE&user=USER&password=PASSWORD&recordSyntax=RECORDS_SYNTAX&charset=CHARSET" +
                        "&filePath=FILE_PATH");

        Element dataSourceCreateZ3950Timestamp = getOperationElement("dataSources",
                "Creates a Data Source from Z39.50 with Timestamp",
                restRequest,
                "/rest/dataSources/createZ3950Timestamp?dataProviderId=DATA_PROVIDER_ID&id=DATA_SOURCE_ID" +
                        "&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE&address=ADDRESS&port=PORT" +
                        "&database=DATABASE&user=USER&password=PASSWORD&recordSyntax=RECORDS_SYNTAX&charset=CHARSET" +
                        "&earliestTimestamp=DATE(YYYYMMDD)");

        Element dataSourceCreateZ3950IdSequence = getOperationElement("dataSources",
                "Creates a Data Source from Z39.50 with Id Sequence",
                restRequest,
                "/rest/dataSources/createZ3950IdSequence?dataProviderId=DATA_PROVIDER_ID&id=DATA_SOURCE_ID" +
                        "&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE&address=ADDRESS&port=PORT" +
                        "&database=DATABASE&user=USER&password=PASSWORD&recordSyntax=RECORDS_SYNTAX&charset=CHARSET" +
                        "&maximumId=MAXIMUM_ID");

        Element dataSourceCreateFtp = getOperationElement("dataSources",
                "Creates a Data Source from FTP server",
                restRequest,
                "/rest/dataSources/createFtp?dataProviderId=DATA_PROVIDER_ID&id=DATA_SOURCE_ID" +
                        "&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT" +
                        "&isoFormat=ISO_FORMAT&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY" +
                        "&idXpath=ID_XPATH&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI&" +
                        "recordXPath=RECORDS_XPATH&server=SERVER&user=USER&password=PASSWORD&ftpPath=FTP_PATH");

        Element dataSourceCreateHttp = getOperationElement("dataSources",
                "Creates a Data Source from HTTP server",
                restRequest,
                "/rest/dataSources/createHttp?dataProviderId=DATA_PROVIDER_ID&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                        "&schema=SCHEMA&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                        "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                        "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI&recordXPath=RECORDS_XPATH&url=URL");

        Element dataSourceCreateFolder = getOperationElement("dataSources",
                "Creates a Data Source from Folder",
                restRequest,
                "/rest/dataSources/createFolder?dataProviderId=DATA_PROVIDER_ID&id=DATA_SOURCE_ID" +
                        "&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT" +
                        "&isoFormat=ISO_FORMAT&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                        "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI&recordXPath=RECORDS_XPATH" +
                        "&folder=FOLDER_PATH");

        Element dataSourceUpdateOai = getOperationElement("dataSources",
                "Updates an OAI-PMH Data Source",
                restRequest,
                "/rest/dataSources/updateOai?id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA" +
                        "&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT&oaiURL=URL_OAI_SERVER&oaiSet=OAI_SET");

        Element dataSourceUpdateZ3950IdList = getOperationElement("dataSources",
                "Updates a Z39.50 Data Source with Id List",
                restRequest,
                "/rest/dataSources/updateZ3950IdList?id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA" +
                        "&namespace=NAMESPACE&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                        "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&filePath=FILE_PATH");

        Element dataSourceUpdateZ3950Timestamp = getOperationElement("dataSources",
                "Updates a Z39.50 Data Source with Timestamp",
                restRequest,
                "/rest/dataSources/updateZ3950Timestamp?id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA" +
                        "&namespace=NAMESPACE&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                        "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&earliestTimestamp=DATE(YYYYMMDD)");

        Element dataSourceUpdateZ3950IdSequence = getOperationElement("dataSources",
                "Updates a Z39.50 Data Source with Id Sequence",
                restRequest,
                "/rest/dataSources/updateZ3950IdSequence?id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA" +
                        "&namespace=NAMESPACE&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                        "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&maximumId=MAXIMUM_ID");

        Element dataSourceUpdateFtp = getOperationElement("dataSources",
                "Updates a FTP Data Source",
                restRequest,
                "/rest/dataSources/updateDataSourceFtp?id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA" +
                        "&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT&charset=CHAR_SET" +
                        "&password=PASSWORD&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&recordIdPolicy=RECORD_ID_POLICY" +
                        "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI&recordXPath=RECORDS_XPATH" +
                        "&server=SERVER&user=USER&password=PASSWORD&ftpPath=FTP_PATH");

        Element dataSourceUpdateHttp = getOperationElement("dataSources",
                "Updates a HTTP Data Source",
                restRequest,
                "/rest/dataSources/updateHttp?id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA" +
                        "&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                        "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                        "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                        "&recordXPath=RECORDS_XPATH&url=URL");

        Element dataSourceUpdateFolder = getOperationElement("dataSources",
                "Updates a Folder Data Source",
                restRequest,
                "/rest/dataSources/updateFolder?id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA" +
                        "&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT&charset=CHAR_SET" +
                        "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH&namespacePrefix=NAMESPACE_PREFIX" +
                        "&namespaceUri=NAMESPACE_URI&recordXPath=RECORDS_XPATH&folder=FOLDER_PATH");


        Element dataSourceDelete = getOperationElement("dataSources",
                "Deletes a Data Source",
                restRequest,
                "/rest/dataSources/delete");

        Element dataSourceGet = getOperationElement("dataSources",
                "Gets a Data Source",
                restRequest,
                "/rest/dataSources/getDataSource");

        Element dataSourceStartIngest = getOperationElement("dataSources",
                "Starts the Data Source ingest",
                restRequest,
                "/rest/dataSources/startIngest");

        Element dataSourceCountRecords = getOperationElement("dataSources",
                "Counts the number of records",
                restRequest,
                "/rest/dataSources/countRecords");

        Element dataSourceLastIngestionDate = getOperationElement("dataSources",
                "Returns the last ingestion date",
                restRequest,
                "/rest/dataSources/lastIngestionDate");

        Element dataSourceStopIngest = getOperationElement("dataSources",
                "Stops the Data Source ingest",
                restRequest,
                "/rest/dataSources/stopIngest");

        Element dataSourceScheduleIngest = getOperationElement("dataSources",
                "Schedules a Data Source ingest",
                restRequest,
                "/rest/dataSources/scheduleIngest");

        Element dataSourceScheduleList = getOperationElement("dataSources",
                "Retrieves the list of all Data Source schedules",
                restRequest,
                "/rest/dataSources/scheduleList");

        Element dataSourceHarvesting = getOperationElement("dataSources",
                "Retrieves the list of all harvesting Data Sources",
                restRequest,
                "/rest/dataSources/harvesting");

        Element dataSourceStartExport = getOperationElement("dataSources",
                "Starts the Data Source export",
                restRequest,
                "/rest/dataSources/startExport");

        Element dataSourcesHarvestStatus = getOperationElement("dataSources",
                "Retrieves the Data Source status",
                restRequest,
                "/rest/dataSources/harvestStatus");

        Element dataSourceLog = getOperationElement("dataSources",
                "Retrieves the last Data Source ingest log",
                restRequest,
                "/rest/dataSources/log");


        Element rootElement = DocumentHelper.createElement("dataSourceOperationsList");
        rootElement.add(dataSourceOperations);
        rootElement.add(dataSourceList);
        rootElement.add(dataSourceFromDataProviderList);
        rootElement.add(dataSourceCreateOai);
        rootElement.add(dataSourceCreateZ3950Timestamp);
        rootElement.add(dataSourceCreateZ3950IdList);
        rootElement.add(dataSourceCreateZ3950IdSequence);
        rootElement.add(dataSourceCreateFtp);
        rootElement.add(dataSourceCreateHttp);
        rootElement.add(dataSourceCreateFolder);
        rootElement.add(dataSourceUpdateOai);
        rootElement.add(dataSourceUpdateZ3950Timestamp);
        rootElement.add(dataSourceUpdateZ3950IdList);
        rootElement.add(dataSourceUpdateZ3950IdSequence);
        rootElement.add(dataSourceUpdateFtp);
        rootElement.add(dataSourceUpdateHttp);
        rootElement.add(dataSourceUpdateFolder);
        rootElement.add(dataSourceDelete);
        rootElement.add(dataSourceGet);
        rootElement.add(dataSourceCountRecords);
        rootElement.add(dataSourceLastIngestionDate);
        rootElement.add(dataSourceStartIngest);
        rootElement.add(dataSourceStopIngest);
        rootElement.add(dataSourceScheduleIngest);
        rootElement.add(dataSourceScheduleList);
        rootElement.add(dataSourceHarvesting);
        rootElement.add(dataSourceStartExport);
        rootElement.add(dataSourcesHarvestStatus);
        rootElement.add(dataSourceLog);
        return rootElement;
    }

    public Element getRecordsOperationList(RestRequest restRequest) {
        Element operationsList = getOperationElement("recordOperationsList",
                "Retrieve the list of the available operations over records",
                restRequest,
                "/rest/records");

        Element recordGet = getOperationElement("getRecord",
                "Retrieves a specific record",
                restRequest,
                "/rest/records/getRecord");

        Element recordSave = getOperationElement("saveRecord",
                "Save record",
                restRequest,
                "/rest/records/saveRecord");

        Element recordDelete = getOperationElement("deleteRecord",
                "Delete record (mark as deleted)",
                restRequest,
                "/rest/records/deleteRecord");

        Element recordErase = getOperationElement("eraseRecord",
                "Erase Record (remove permanent)",
                restRequest,
                "/rest/records/eraseRecord");


        Element rootElement = DocumentHelper.createElement("operationsList");
        rootElement.add(operationsList);
        rootElement.add(recordGet);
        rootElement.add(recordSave);
        rootElement.add(recordDelete);
        rootElement.add(recordErase);
        return rootElement;
    }

    /**
     * Builds the Element from the given operation parameters.
     */
    public Element getOperationElement(String operationName, String operationDescription,
                                        RestRequest restRequest, String operationSyntax) {
        Element operationElement = DocumentHelper.createElement(operationName);

        Element operationDescriptionElement = DocumentHelper.createElement("description");
        operationDescriptionElement.setText(operationDescription);
        Element operationExampleElement = DocumentHelper.createElement("syntax");
        operationSyntax = restRequest.getContextURL() + operationSyntax;
        operationExampleElement.setText(operationSyntax);

        operationElement.add(operationDescriptionElement);
        operationElement.add(operationExampleElement);
        return operationElement;
    }

    /**
     * Retrieve the list of the available operations over Data Providers
     * @param restRequest
     * @return
     */
    public Element getStatisticsOperationList(RestRequest restRequest) {
        Element statisticsOperations = getOperationElement("statisticsOperationsList",
                "Retrieve the list of the available operations over Statistics",
                restRequest,
                "/rest/statistics");

        Element getStatistics = getOperationElement("getStatistics",
                "Retrieve statistics of the REPOX Instance",
                restRequest,
                "/rest/statistics/getStatistics?type=ALL/INTERNAL/EXTERNAL/YADDA");

        Element rootElement = DocumentHelper.createElement("statisticsOperationsList");
        rootElement.add(statisticsOperations);
        rootElement.add(getStatistics);
        return rootElement;
    }
}
