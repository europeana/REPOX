package pt.utl.ist.rest;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import pt.utl.ist.dataProvider.MessageType;
import pt.utl.ist.dataProvider.dataSource.IdExtractedRecordIdPolicy;
import pt.utl.ist.metadataSchemas.MetadataSchemaVersion;
import pt.utl.ist.rest.services.web.DefaultWebServices;
import pt.utl.ist.rest.services.web.WebServices;
import pt.utl.ist.rest.services.web.rest.RestRequest;
import pt.utl.ist.rest.services.web.rest.RestUtils;
import pt.utl.ist.util.InvalidInputException;
import pt.utl.ist.util.Urn;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.InvalidRequestException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Gilberto Pedrosa
 * Date: 14-12-2012
 * Time: 22:01
 * To change this template use File | Settings | File Templates.
 */
public class DefaultResponseRest extends ResponseOperations implements ResponseRest {
    @Override
    public void response(HttpServletRequest request, HttpServletResponse response, WebServices webServices) throws InvalidRequestException, IOException, DocumentException, ParseException, ClassNotFoundException, NoSuchMethodException, InvalidInputException, SQLException {

        DefaultWebServices defaultWebServices = (DefaultWebServices)webServices;

        RestRequest restRequest = RestUtils.processRequest(RestServlet.BASE_URI, request);
        response.setContentType("text/xml");
        ServletOutputStream out = response.getOutputStream();

        defaultWebServices.setRequestURI(request.getRequestURL().toString());

        if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals("removeMapping")) {
            String id = restRequest.getRequestParameters().get("id");

            if(id != null && !id.isEmpty()){
                defaultWebServices.removeMapping(out, id);
            }
            else{
                defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error removing mapping:" +
                        "invalid arguments. Syntax: /rest/removeMapping?id=ID" +
                        "[Mandatory fields: id]");
            }
        }else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals("listMappings")) {
            defaultWebServices.listMappings(out);
        }
        // Schemas
        else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals("createSchema")) {
            String id = restRequest.getRequestParameters().get("shortDesignation");
            String namespace = restRequest.getRequestParameters().get("namespace");
            String designation = restRequest.getRequestParameters().get("designation");
            String description = restRequest.getRequestParameters().get("description");
            String notes = restRequest.getRequestParameters().get("notes");
            Boolean oaiAvailable = Boolean.valueOf(restRequest.getRequestParameters().get("oaiAvailable"));
            String versions = restRequest.getRequestParameters().get("versions");

            List<MetadataSchemaVersion> metadataSchemaVersionList = new ArrayList<MetadataSchemaVersion>();
            if(versions != null){
                String[] versionsTokens = versions.split(";");
                for (String versionsToken : versionsTokens) {
                    String[] versionToken = versionsToken.split("--");
                    metadataSchemaVersionList.add(new MetadataSchemaVersion(Double.valueOf(versionToken[0]),versionToken[1]));
                }
            }

            if(id != null && !id.isEmpty() ||
                    namespace != null && !namespace.isEmpty() ||
                    versions != null && !versions.isEmpty()){
                defaultWebServices.createMetadataSchema(out, id, "", namespace, designation, description,
                        notes, oaiAvailable,metadataSchemaVersionList);
            }
            else{
                defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the" +
                        "Metadata Schema: invalid arguments." +
                        "Syntax: /rest/createSchema?shortDesignation=SHORT_DESIGNATION&namespace=NAMESPACE" +
                        "&designation=DESIGNATION&description=DESCRIPTION&notes=NOTES&oaiAvailable=TRUE/FALSE " +
                        "[mandatory fields: shortDesignation, namespace, at least one version]");
            }
        }else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals("updateSchema")) {
            String id = restRequest.getRequestParameters().get("shortDesignation");
            String oldId = restRequest.getRequestParameters().get("oldShortDesignation");
            String namespace = restRequest.getRequestParameters().get("namespace");
            String designation = restRequest.getRequestParameters().get("designation");
            String description = restRequest.getRequestParameters().get("description");
            String notes = restRequest.getRequestParameters().get("notes");
            Boolean oaiAvailable = Boolean.valueOf(restRequest.getRequestParameters().get("oaiAvailable"));
            String versions = restRequest.getRequestParameters().get("versions");

            List<MetadataSchemaVersion> metadataSchemaVersionList = new ArrayList<MetadataSchemaVersion>();
            if(versions != null){
                String[] versionsTokens = versions.split(";");
                for (String versionsToken : versionsTokens) {
                    String[] versionToken = versionsToken.split("--");
                    metadataSchemaVersionList.add(new MetadataSchemaVersion(Double.valueOf(versionToken[0]),versionToken[1]));
                }
            }

            if(id != null && !id.isEmpty()){
                defaultWebServices.updateMetadataSchema(out, id, oldId == null ? id : oldId, namespace, designation, description,
                        notes, oaiAvailable,metadataSchemaVersionList);
            }
            else{
                defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the" +
                        "Metadata Schema: invalid arguments." +
                        "Syntax: /rest/updateSchema?shortDesignation=SHORT_DESIGNATION&oldShortDesignation=OLD_SHORT_DESIGNATION&namespace=NAMESPACE" +
                        "&designation=DESIGNATION&description=DESCRIPTION&notes=NOTES&oaiAvailable=TRUE/FALSE " +
                        "[mandatory fields: shortDesignation]");
            }
        }else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals("removeSchema")) {
            String id = restRequest.getRequestParameters().get("id");

            if(id != null && !id.isEmpty()){
                defaultWebServices.removeMetadataSchema(out, id);
            }
            else{
                defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error removing schema:" +
                        "invalid arguments. Syntax: /rest/removeSchema?id=ID" +
                        "[Mandatory fields: id]");
            }
        }else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals("listSchemas")) {
            defaultWebServices.listMetadataSchemas(out);
        }
        // aggregators
        else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals(RestServlet.AGGREGATORS_URL_NAME)) {
            if(restRequest.getUriHierarchy().size() == 1) {
                //list all available data providers operations
                Element rootElement = getAggregatorOperationList(restRequest);
                RestUtils.writeRestResponse(out, rootElement);
            }
            else { // operation over an Aggregator
                if(restRequest.getUriHierarchy().size() == 2) {

                    if(restRequest.getUriHierarchy().get(1).equals("list")) {
                        defaultWebServices.writeAggregators(out);
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("create")) {
                        String name = restRequest.getRequestParameters().get("name");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String homepage = restRequest.getRequestParameters().get("homepage");

                        if(name != null && !name.isEmpty()){
                            defaultWebServices.createAggregator(out, name, nameCode, homepage);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the" +
                                    "Aggregator: invalid arguments. Syntax: /rest/aggregators/create?name=NAME" +
                                    "&nameCode=NAME_CODE&homepage=HOMEPAGE [mandatory fields: name]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("update")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String name = restRequest.getRequestParameters().get("name");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String homepage = restRequest.getRequestParameters().get("homepage");

                        if(id != null && !id.isEmpty()){
                            defaultWebServices.updateAggregator(out, id, name, nameCode, homepage);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating" +
                                    "Aggregator: invalid arguments. Syntax: /rest/aggregators/update?id=ID" +
                                    "&name=NAME&nameCode=NAME_CODE&homepage=HOMEPAGE [mandatory field: id]");
                        }

                        /*if(id != null && !id.isEmpty() &&
                                name != null && !name.isEmpty()){
                            webServices.updateAggregator(out, id, name, nameCode, homepage);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating" +
                                    "Aggregator: invalid arguments. Syntax: /rest/aggregators/update?id=ID" +
                                    "&name=NAME&nameCode=NAME_CODE&homepage=HOMEPAGE [mandatory fields: id, name]");
                        }*/
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("delete")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            defaultWebServices.deleteAggregator(out, id);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting " +
                                    "Aggregator: invalid arguments. Syntax: /rest/aggregators/delete?id=ID" +
                                    " [mandatory field: id]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("getAggregator")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            defaultWebServices.getAggregator(out, id);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error retrieving " +
                                    "Aggregator: invalid arguments. Syntax: /rest/aggregators/getAggregator?id=ID" +
                                    " [mandatory field: id]");
                        }
                    }
                }
                else {
                    RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), out);
                }
            }

        }
        // data providers
        else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals(RestServlet.DPROVIDERS_URL_NAME)) {

            if(restRequest.getUriHierarchy().size() == 1) {
                //list all available data providers operations
                Element rootElement = getDataProviderOperationListEuropeana(restRequest);
                RestUtils.writeRestResponse(out, rootElement);
            }
            else { // operation over a Data Provider
                if(restRequest.getUriHierarchy().size() == 2) {
                    if(restRequest.getUriHierarchy().get(1).equals("list")) {
                        if(restRequest.getRequestParameters().size() == 0) {
                            defaultWebServices.writeDataProviders(out);
                        }
                        else{
                            String aggregatorId = restRequest.getRequestParameters().get("aggregatorId");
                            if (restRequest.getRequestParameters().size() == 1 &&
                                    aggregatorId != null && !aggregatorId.isEmpty()) {
                                defaultWebServices.writeDataProviders(out, aggregatorId);
                            }
                            else{
                                defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error listing" +
                                        " Data Provider: invalid arguments." +
                                        "Syntax: /rest/dataProviders/list?aggregatorId=AGGREGATOR_ID");
                            }
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("create")) {
                        String aggregatorId = restRequest.getRequestParameters().get("aggregatorId");
                        String name = restRequest.getRequestParameters().get("name");
                        String country = restRequest.getRequestParameters().get("country");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String url = restRequest.getRequestParameters().get("url");
                        String dataSetType = restRequest.getRequestParameters().get("dataSetType");
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");

                        if(aggregatorId != null && !aggregatorId.isEmpty() &&
                                name != null && !name.isEmpty() &&
                                country != null && !country.isEmpty() &&
                                dataSetType != null && !dataSetType.isEmpty()){

                            if(dataProviderId == null || dataProviderId.isEmpty()){
                                defaultWebServices.createDataProvider(out, aggregatorId, name, country, description, nameCode,
                                        url, dataSetType);
                            }
                            else{
                                defaultWebServices.createDataProvider(out, aggregatorId, dataProviderId, name, country,
                                        description, nameCode, url, dataSetType);
                            }
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the" +
                                    "Data Provider: invalid arguments." +
                                    "Syntax: /rest/dataProviders/create?aggregatorId=AGGREGATOR_ID" +
                                    "&dataProviderId=DATA_PROVIDER_ID&name=NAME" +
                                    "&description=DESCRIPTION&country=2_LETTERS_COUNTRY&nameCode=NAME_CODE" +
                                    "&url=URL&dataSetType=DATA_SET_TYPE [mandatory fields: aggregatorId, name, " +
                                    "country, dataSetType]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("update")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String name = restRequest.getRequestParameters().get("name");
                        String country = restRequest.getRequestParameters().get("country");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String url = restRequest.getRequestParameters().get("url");
                        String dataSetType = restRequest.getRequestParameters().get("dataSetType");

                        if(id != null && !id.isEmpty()){
                            try {
                                defaultWebServices.updateDataProvider(out, id, name, country, description, nameCode, url,
                                        dataSetType);
                            } catch (AlreadyExistsException e) {
                                throw new RuntimeException("Caused by AlreadyExistsException", e);
                            }
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating Data" +
                                    "Provider: invalid arguments. Syntax: /rest/dataProviders/update?id=ID" +
                                    "&name=NAME&description=DESCRIPTION&country=2_LETTERS_COUNTRY&nameCode=NAME_CODE" +
                                    "&url=URL&dataSetType=DATA_SET_TYPE [mandatory field: id]");
                        }

                        /*if(id != null && !id.isEmpty() &&
                                name != null && !name.isEmpty() &&
                                country != null && !country.isEmpty() &&
                                dataSetType != null && !dataSetType.isEmpty()){
                            webServices.updateDataProvider(out, id, name, country, description, nameCode, url,
                                    dataSetType);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating Data" +
                                    "Provider: invalid arguments. Syntax: /rest/dataProviders/update?id=ID" +
                                    "&name=NAME&description=DESCRIPTION&country=2_LETTERS_COUNTRY&nameCode=NAME_CODE" +
                                    "&url=URL&dataSetType=DATA_SET_TYPE [mandatory fields: " +
                                    "id, name, country, dataSetType]");
                        }*/
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("delete")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            defaultWebServices.deleteDataProvider(out, id);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting Data" +
                                    "Provider: invalid arguments. Syntax: /rest/dataProviders/delete?id=ID [mandatory " +
                                    "field: id");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("getDataProvider")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            defaultWebServices.getDataProvider(out, id);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error retrieving Data" +
                                    "Provider: invalid arguments. Syntax: /rest/dataProviders/getDataProvider?id=ID [mandatory " +
                                    "field: id");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("move")) {
                        String idDataProvider = restRequest.getRequestParameters().get("idDataProvider");
                        String idNewAggr = restRequest.getRequestParameters().get("idNewAggr");

                        if(idDataProvider != null && !idDataProvider.isEmpty() &&
                                idNewAggr != null && !idNewAggr.isEmpty()){
                            defaultWebServices.moveDataProvider(out, idDataProvider, idNewAggr);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error moving Data" +
                                    "Provider: invalid arguments. Syntax: /rest/dataProviders/move?idDataProvider=" +
                                    "ID_DATA_PROVIVER&idNewAggr=ID_NEW_AGGREGATOR [mandatory fields: " +
                                    "idDataProvider, idNewAggr]");
                        }
                    }
                }
                else {
                    RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), out);
                }
            }
        }
        // data sources
        else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals(RestServlet.DSOURCES_URL_NAME)) {

            if(restRequest.getUriHierarchy().size() == 1) {
                //list all available data providers operations
                Element rootElement = getDataSourceOperationList(restRequest);
                RestUtils.writeRestResponse(out, rootElement);
            }
            else { // operation over a Data Source
                if(restRequest.getUriHierarchy().size() == 2) {
                    if(restRequest.getUriHierarchy().get(1).equals("list")){
                        if(restRequest.getRequestParameters().size() == 0) {
                            defaultWebServices.writeDataSources(out);
                        }
                        else{
                            String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                            if (restRequest.getRequestParameters().size() == 1 &&
                                    dataProviderId != null && !dataProviderId.isEmpty()) {
                                defaultWebServices.writeDataSources(out, dataProviderId);
                            }
                            else{
                                defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error listing the" +
                                        "Data Sources: invalid arguments." +
                                        "Syntax: /rest/dataSources/list?dataProviderId=DATA_PROVIDER_ID");
                            }
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createOai")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String oaiURL = restRequest.getRequestParameters().get("oaiURL");
                        String oaiSet = restRequest.getRequestParameters().get("oaiSet");

                        // optionals
                        String marcFormat = restRequest.getRequestParameters().get("marcFormat");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                oaiURL != null && !oaiURL.isEmpty()){

                            defaultWebServices.createDataSourceOai(out, dataProviderId, id, description, nameCode, name,
                                    exportPath, schema, namespace, metadataFormat, oaiURL,
                                    !oaiSet.isEmpty() ? oaiSet : null, (marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createOai?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&metadataFormat=METADATA_FORMAT&oaiURL=URL_OAI_SERVER&oaiSet=OAI_SET" +
                                    " [Mandatory fields: dataProviderId, id, description, schema, namespace, metadataFormat, " +
                                    "oaiURL]. If metadataFormat=MarcXchange the field marcFormat (optional) can " +
                                    "be filled with one of the following values: MARC21, UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createZ3950Timestamp")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String address = restRequest.getRequestParameters().get("address");
                        String port = restRequest.getRequestParameters().get("port");
                        String database = restRequest.getRequestParameters().get("database");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String recordSyntax = restRequest.getRequestParameters().get("recordSyntax");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String earliestTimestamp = restRequest.getRequestParameters().get("earliestTimestamp");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                address != null && !address.isEmpty() &&
                                port != null && !port.isEmpty() &&
                                database != null && !database.isEmpty() &&
                                recordSyntax != null && !recordSyntax.isEmpty() &&
                                charset != null && !charset.isEmpty() &&
                                earliestTimestamp != null && !earliestTimestamp.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty()){

                            defaultWebServices.createDataSourceZ3950Timestamp(out, dataProviderId, id, description, nameCode,
                                    name, exportPath, schema, namespace, address, port, database, user, password,
                                    recordSyntax, charset, earliestTimestamp, recordIdPolicy, idXpath, namespacePrefix,
                                    namespaceUri);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createZ3950Timestamp?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&earliestTimestamp=DATE(YYYYMMDD)" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, address, port, database, " +
                                    "recordSyntax, charset, earliestTimestamp, recordIdPolicy (if " +
                                    "recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createZ3950IdSequence")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String address = restRequest.getRequestParameters().get("address");
                        String port = restRequest.getRequestParameters().get("port");
                        String database = restRequest.getRequestParameters().get("database");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String recordSyntax = restRequest.getRequestParameters().get("recordSyntax");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String maximumId = restRequest.getRequestParameters().get("maximumId");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                address != null && !address.isEmpty() &&
                                port != null && !port.isEmpty() &&
                                database != null && !database.isEmpty() &&
                                recordSyntax != null && !recordSyntax.isEmpty() &&
                                charset != null && !charset.isEmpty() &&
                                maximumId != null && !maximumId.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty()){

                            defaultWebServices.createDataSourceZ3950IdSequence(out, dataProviderId, id, description, nameCode,
                                    name, exportPath, schema, namespace, address, port, database, user, password,
                                    recordSyntax, charset, maximumId, recordIdPolicy, idXpath, namespacePrefix,
                                    namespaceUri);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createZ3950IdSequence?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&maximumId=MAXIMUM_ID" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, address, port, database, " +
                                    "recordSyntax, charset, maximumId, recordIdPolicy (if " +
                                    "recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createFtp")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String server = restRequest.getRequestParameters().get("server");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String ftpPath = restRequest.getRequestParameters().get("ftpPath");

                        // optionals
                        String marcFormat = restRequest.getRequestParameters().get("marcFormat");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty() &&
                                (!recordIdPolicy.equals(IdExtractedRecordIdPolicy.IDEXTRACTED) ||
                                        (idXpath != null && !idXpath.isEmpty() &&
                                                namespacePrefix != null && !namespacePrefix.isEmpty() &&
                                                namespaceUri != null && !namespaceUri.isEmpty())) &&
                                server != null && !server.isEmpty() &&
                                ftpPath != null && !ftpPath.isEmpty()){

                            defaultWebServices.createDataSourceFtp(out, dataProviderId, id, description, nameCode, name,
                                    exportPath, schema, namespace, metadataFormat, isoFormat, charset, recordIdPolicy,
                                    idXpath, namespacePrefix, namespaceUri, recordXPath, server, (user != null && !user.isEmpty()) ? user : "",
                                    (password != null && !password.isEmpty()) ? password : "",
                                    ftpPath,(marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createFtp?dataProviderId=DATA_PROVIDER_ID&" +
                                    "id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&server=SERVER&user=USER&password=PASSWORD" +
                                    "&ftpPath=FTP_PATH [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, metadataFormat, recordIdPolicy, " +
                                    "server, ftpPath (Note: if recordIdPolicy=IdExtracted the fields: idXpath," +
                                    "namespacePrefix and namespaceUri are mandatory and if metadataFormat=ISO2709 " +
                                    "the fields isoFormat and charset are mandatory)]. If metadataFormat=MarcXchange the " +
                                    "field marcFormat (optional) can be filled with one of the following values: MARC21, " +
                                    "UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createHttp")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String url = restRequest.getRequestParameters().get("url");

                        // optionals
                        String marcFormat = restRequest.getRequestParameters().get("marcFormat");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty() &&
                                (!recordIdPolicy.equals(IdExtractedRecordIdPolicy.IDEXTRACTED) ||
                                        (idXpath != null && !idXpath.isEmpty() &&
                                                namespacePrefix != null && !namespacePrefix.isEmpty() &&
                                                namespaceUri != null && !namespaceUri.isEmpty())) &&
                                url != null && !url.isEmpty()){

                            defaultWebServices.createDataSourceHttp(out, dataProviderId, id, description, nameCode, name,
                                    exportPath, schema, namespace, metadataFormat, isoFormat, charset, recordIdPolicy,
                                    idXpath, namespacePrefix, namespaceUri, recordXPath, url,
                                    (marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createHttp?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&url=URL [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, metadataFormat, recordIdPolicy, " +
                                    "url (Note: if recordIdPolicy=IdExtracted the fields: idXpath," +
                                    "namespacePrefix and namespaceUri are mandatory and if metadataFormat=ISO2709 " +
                                    "the fields isoFormat and charset are mandatory)].  If metadataFormat=MarcXchange " +
                                    "the field marcFormat (optional) can be filled with one of the following values: " +
                                    "MARC21, UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createFolder")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String folder = restRequest.getRequestParameters().get("folder");

                        // optionals
                        String marcFormat = restRequest.getRequestParameters().get("marcFormat");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty() &&
                                (!recordIdPolicy.equals(IdExtractedRecordIdPolicy.IDEXTRACTED) ||
                                        (idXpath != null && !idXpath.isEmpty() &&
                                                namespacePrefix != null && !namespacePrefix.isEmpty() &&
                                                namespaceUri != null && !namespaceUri.isEmpty())) &&
                                folder != null && !folder.isEmpty()){

                            defaultWebServices.createDataSourceFolder(out, dataProviderId, id, description, nameCode,
                                    name, exportPath, schema, namespace, metadataFormat, isoFormat, charset,
                                    recordIdPolicy, idXpath, namespacePrefix, namespaceUri, recordXPath, folder,
                                    (marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createFolder?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&folder=FOLDER_PATH [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, metadataFormat, recordIdPolicy, " +
                                    "folder (Note: if recordIdPolicy=IdExtracted the fields: idXpath," +
                                    "namespacePrefix and namespaceUri are mandatory and if metadataFormat=ISO2709 " +
                                    "the fields isoFormat and charset are mandatory)].  If metadataFormat=MarcXchange " +
                                    "the field marcFormat (optional) can be filled with one of the following values: " +
                                    "MARC21, UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateOai")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String oaiURL = restRequest.getRequestParameters().get("oaiURL");
                        String oaiSet = restRequest.getRequestParameters().get("oaiSet");
                        String marcFormat = restRequest.getRequestParameters().get("marcFormat");

                        if(id != null && !id.isEmpty()){
                            defaultWebServices.updateDataSourceOai(out, id, description, nameCode, name,
                                    exportPath, schema, namespace, metadataFormat, oaiURL,
                                    (oaiSet != null &&!oaiSet.isEmpty()) ? oaiSet : null,
                                    (marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the OAI" +
                                    "Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateOai?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&oaiURL=URL_OAI_SERVER&oaiSet=OAI_SET " +
                                    "[Mandatory field: id]. If metadataFormat=MarcXchange the field marcFormat (optional) can " +
                                    "be filled with one of the following values: MARC21, UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateZ3950Timestamp")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String address = restRequest.getRequestParameters().get("address");
                        String port = restRequest.getRequestParameters().get("port");
                        String database = restRequest.getRequestParameters().get("database");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String recordSyntax = restRequest.getRequestParameters().get("recordSyntax");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String earliestTimestamp = restRequest.getRequestParameters().get("earliestTimestamp");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                        if(id != null && !id.isEmpty()){
                            defaultWebServices.updateDataSourceZ3950Timestamp(out, id, description, nameCode,
                                    name, exportPath, schema, namespace, address, port, database, user, password,
                                    recordSyntax, charset, earliestTimestamp, recordIdPolicy, idXpath, namespacePrefix,
                                    namespaceUri);
                        }

                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the" +
                                    "Z39.50 Data Source with Time Stamp: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateZ3950Timestamp?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&earliestTimestamp=DATE(YYYYMMDD)" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory field: " +
                                    "id (if recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateZ3950IdSequence")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String address = restRequest.getRequestParameters().get("address");
                        String port = restRequest.getRequestParameters().get("port");
                        String database = restRequest.getRequestParameters().get("database");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String recordSyntax = restRequest.getRequestParameters().get("recordSyntax");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String maximumId = restRequest.getRequestParameters().get("maximumId");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                        if(id != null && !id.isEmpty()){
                            defaultWebServices.updateDataSourceZ3950IdSequence(out, id, description, nameCode,
                                    name, exportPath, schema, namespace, address, port, database, user, password,
                                    recordSyntax, charset, maximumId, recordIdPolicy, idXpath, namespacePrefix,
                                    namespaceUri);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the Z39.50" +
                                    "Data Source with ID Sequence: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateZ3950IdSequence?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&maximumId=MAXIMUM_ID" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory field: " +
                                    "id (if recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateFtp")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String server = restRequest.getRequestParameters().get("server");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String ftpPath = restRequest.getRequestParameters().get("ftpPath");
                        String marcFormat = restRequest.getRequestParameters().get("marcFormat");

                        if(id != null && !id.isEmpty()){
                            defaultWebServices.updateDataSourceFtp(out, id, description, nameCode, name,
                                    exportPath, schema, namespace, metadataFormat, isoFormat, charset, recordIdPolicy,
                                    idXpath, namespacePrefix, namespaceUri, recordXPath, server, user, password,
                                    /*(user != null && !user.isEmpty()) ? user : "",
                                    (password != null && !password.isEmpty()) ? password : "",*/
                                    ftpPath,(marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the FTP Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateFtp?dataProviderId=id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&server=SERVER&user=USER&password=PASSWORD" +
                                    "&ftpPath=FTP_PATH [Mandatory field: " +
                                    "id (Note: if recordIdPolicy=IdExtracted the fields: idXpath," +
                                    "namespacePrefix and namespaceUri are mandatory and if metadataFormat=ISO2709 " +
                                    "the fields isoFormat and charset are mandatory)].  If metadataFormat=MarcXchange " +
                                    "the field marcFormat (optional) can be filled with one of the following values: " +
                                    "MARC21, UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateHttp")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String url = restRequest.getRequestParameters().get("url");
                        String marcFormat = restRequest.getRequestParameters().get("marcFormat");

                        if(id != null && !id.isEmpty()){
                            defaultWebServices.updateDataSourceHttp(out, id, description, nameCode, name,
                                    exportPath, schema, namespace, metadataFormat, isoFormat, charset, recordIdPolicy,
                                    idXpath, namespacePrefix, namespaceUri, recordXPath, url,
                                    (marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the HTTP Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateHttp?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&url=URL [Mandatory field: (Note: if " +
                                    "recordIdPolicy=IdExtracted the fields: idXpath," +
                                    "namespacePrefix and namespaceUri are mandatory and if metadataFormat=ISO2709 " +
                                    "the fields isoFormat and charset are mandatory)]. If metadataFormat=MarcXchange " +
                                    "the field marcFormat (optional) can be filled with one of the following values: " +
                                    "MARC21, UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateFolder")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String folder = restRequest.getRequestParameters().get("folder");

                        // optionals
                        String marcFormat = restRequest.getRequestParameters().get("marcFormat");

                        if(id != null && !id.isEmpty()){
                            defaultWebServices.updateDataSourceFolder(out, id, description, nameCode,
                                    name, exportPath, schema, namespace, metadataFormat, isoFormat, charset,
                                    recordIdPolicy, idXpath, namespacePrefix, namespaceUri, recordXPath, folder,
                                    (marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the Folder Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateFolder?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&folder=FOLDER_PATH [Mandatory field: " +
                                    "id (Note: if recordIdPolicy=IdExtracted the fields: idXpath," +
                                    "namespacePrefix and namespaceUri are mandatory and if metadataFormat=ISO2709 " +
                                    "the fields isoFormat and charset are mandatory)]. If metadataFormat=MarcXchange " +
                                    "the field marcFormat (optional) can be filled with one of the following values: " +
                                    "MARC21, UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("delete")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            defaultWebServices.deleteDataSource(out, id);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting Data" +
                                    "Source: invalid arguments. Syntax: /rest/dataSources/delete?id=ID [Mandatory " +
                                    "field: id]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("getDataSource")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            defaultWebServices.getDataSource(out, id);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error retrieving Data" +
                                    "Source: invalid arguments. Syntax: /rest/dataSources/getDataSource?id=ID [Mandatory " +
                                    "field: id]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("countRecords")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            defaultWebServices.countRecordsDataSource(out, id);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error counting records from" +
                                    " Data Source: invalid arguments. Syntax: /rest/dataSources/countRecords?id=ID " +
                                    "[Mandatory field: id]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("lastIngestionDate")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            defaultWebServices.lastIngestionDateDataSource(out, id);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error retrieving last" +
                                    "ingestion date from Data Source: invalid arguments. Syntax: " +
                                    "/rest/dataSources/lastIngestionDate?id=ID [Mandatory field: id]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("startIngest")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String fullIngest = restRequest.getRequestParameters().get("fullIngest");
                        if(id != null && !id.isEmpty() &&
                                fullIngest != null && !fullIngest.isEmpty() &&
                                (fullIngest.equalsIgnoreCase("true") || fullIngest.equalsIgnoreCase("false"))){
                            defaultWebServices.startIngestDataSource(out, id, Boolean.valueOf(fullIngest));
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error starting the Data" +
                                    "Source ingestion: invalid arguments. Syntax: /rest/dataSources/startIngest?id=ID&" +
                                    "fullIngest=BOOLEAN [Mandatory fields: id, fullIngest]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("stopIngest")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            defaultWebServices.stopIngestDataSource(out, id);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error stopping the Data" +
                                    "Source ingestion: invalid arguments. Syntax: /rest/dataSources/stopIngest?id=ID " +
                                    "[Mandatory field: id]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("scheduleIngest")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String firstRunDate = restRequest.getRequestParameters().get("firstRunDate");
                        String firstRunHour = restRequest.getRequestParameters().get("firstRunHour");
                        String frequency = restRequest.getRequestParameters().get("frequency");
                        String xmonths = restRequest.getRequestParameters().get("xmonths");
                        String fullIngest = restRequest.getRequestParameters().get("fullIngest");

                        if(id != null && !id.isEmpty() &&
                                firstRunDate != null && !firstRunDate.isEmpty() &&
                                firstRunHour != null && !firstRunHour.isEmpty() &&
                                frequency != null && !frequency.isEmpty() &&
                                xmonths != null && !xmonths.isEmpty() &&
                                fullIngest != null && !fullIngest.isEmpty() &&
                                (fullIngest.equalsIgnoreCase("true") || fullIngest.equalsIgnoreCase("false"))){
                            defaultWebServices.scheduleIngestDataSource(out, id, firstRunDate, firstRunHour, frequency,
                                    xmonths, fullIngest);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error scheduling the" +
                                    "Data Source ingestion: invalid arguments. Syntax: /rest/dataSources/scheduleIngest?id=ID" +
                                    "&firstRunDate=YYYY-MM-DD&firstRunHour=HH:MM&frequency=ONCE_DAILY_WEEKLY_XMONTHLY" +
                                    "&xmonths=NUMBER&fullIngest=BOOLEAN [Mandatory fields: id, firstRunDate, firstRunHour, " +
                                    "frequency, xmonths, fullIngest]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("scheduleList")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            defaultWebServices.scheduleListDataSource(out, id);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                    "Source ingestion schedule: invalid arguments. Syntax: /rest/dataSources/" +
                                    "scheduleList?id=ID [Mandatory field: id]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("harvestStatus")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            defaultWebServices.harvestStatusDataSource(out, id);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                    "Source harvest status: invalid arguments. Syntax: /rest/dataSources/harvestStatus?id=ID " +
                                    "[Mandatory field: id]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("harvesting")) {
                        if(restRequest.getRequestParameters().size() == 0){
                            defaultWebServices.harvestingDataSources(out);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the list" +
                                    "of data sources harvesting: invalid arguments. Syntax: /rest/dataSources/harvesting");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("startExport")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String recordsPerFile = restRequest.getRequestParameters().get("recordsPerFile");
                        String metadataExportFormat = restRequest.getRequestParameters().get("metadataExportFormat");
                        if(id != null && !id.isEmpty() &&
                                recordsPerFile != null && !recordsPerFile.isEmpty()){
                            defaultWebServices.startExportDataSource(out, id, recordsPerFile, metadataExportFormat);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error starting the Data" +
                                    "Source exportation: invalid arguments. Syntax: /rest/dataSources/startExport?id=ID&" +
                                    "recordsPerFile=RECORDS_NUMBER&metadataExportFormat=METADATA_EXPORT_FORMAT [Mandatory fields: id, recordsPerFile]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("log")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            defaultWebServices.logDataSource(out, id);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                    "Source log: invalid arguments. Syntax: /rest/dataSources/log?id=ID [Mandatory " +
                                    "field: id]");
                        }
                    }
                }
                else {
                    RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), out);
                }
            }
        }
        else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals(RestServlet.RECORDS_URL_NAME)) {
            // records
            if(restRequest.getUriHierarchy().size() == 1) {
                //list all available records operations
                Element rootElement = getRecordsOperationList(restRequest);
                RestUtils.writeRestResponse(out, rootElement);
            }
            else { // operation over a Data Source
                if(restRequest.getUriHierarchy().size() == 2) {
                    if(restRequest.getUriHierarchy().get(1).equals("getRecord")){
                        String recordId = restRequest.getRequestParameters().get("recordId");
                        if(recordId != null && !recordId.isEmpty()){
                            defaultWebServices.getRecord(out, new Urn(recordId));
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                    "Source log: invalid arguments. Syntax: /rest/records/getRecord?recordId=RECORD_ID" +
                                    " [Mandatory field: recordId]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("saveRecord")){
                        String recordId = restRequest.getRequestParameters().get("recordId");
                        String dataSourceId = restRequest.getRequestParameters().get("dataSourceId");
                        String recordString = restRequest.getRequestParameters().get("recordString");
                        if(recordId != null && !recordId.isEmpty() &&
                                dataSourceId != null && !dataSourceId.isEmpty() &&
                                recordString != null && !recordString.isEmpty()){

                            defaultWebServices.saveRecord(out, recordId, dataSourceId, recordString);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error saving record:" +
                                    "invalid arguments. Syntax: /rest/records/saveRecord?recordId=RECORD_ID" +
                                    "&dataSourceId=DATA_SOURCE_ID&recordString=RECORD_STRING [Mandatory fields: " +
                                    "recordId, dataSourceId, recordString]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("deleteRecord")){
                        String recordId = restRequest.getRequestParameters().get("recordId");
                        if(recordId != null && !recordId.isEmpty()){
                            defaultWebServices.deleteRecord(out, recordId);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting record:" +
                                    "invalid arguments. Syntax: /rest/records/deleteRecord?recordId=RECORD_ID " +
                                    "[Mandatory field: recordId]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("eraseRecord")){
                        String recordId = restRequest.getRequestParameters().get("recordId");
                        if(recordId != null && !recordId.isEmpty()){
                            defaultWebServices.eraseRecord(out, recordId);
                        }
                        else{
                            defaultWebServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error erasing record:" +
                                    "invalid arguments. Syntax: /rest/records/eraseRecord?recordId=RECORD_ID " +
                                    "[Mandatory field: recordId]");
                        }
                    }
                }
            }

        }
        else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals(RestServlet.STATISTICS_URL_NAME)){
            if(restRequest.getUriHierarchy().size() == 1) {
                //list all available statistics operations
                Element rootElement = getStatisticsOperationList(restRequest);
                RestUtils.writeRestResponse(out, rootElement);
            }
            else {
                if(restRequest.getUriHierarchy().get(1).equals("getStatistics")){
                    if(restRequest.getRequestParameters().get("type") != null){
                        defaultWebServices.getStatistics(out, restRequest.getRequestParameters().get("type"));
                    }
                    else{
                        defaultWebServices.getStatistics(out, "ALL");
                    }
                }
            }
        }
        else{
            // list operations
            Element rootElement = DocumentHelper.createElement("repoxOperationsList");

            Element aggregatorOperations = getOperationElement("aggregatorOperationsList",
                    "Retrieve the list of the available operations over Aggregators",
                    restRequest,
                    "/rest/aggregators");

            Element dataProviderOperations = getOperationElement("dataProviderOperationsList",
                    "Retrieve the list of the available operations over Data Providers",
                    restRequest,
                    "/rest/dataProviders");

            Element dataSourceOperations = getOperationElement("dataSourceOperationsList",
                    "Retrieve the list of the available operations over Data Sources",
                    restRequest,
                    "/rest/dataSources");

            Element recordsOperations = getOperationElement("recordOperationsList",
                    "Retrieve the list of the available operations over Records",
                    restRequest,
                    "/rest/records");

            Element statisticsOperation = getOperationElement("statistics",
                    "Retrieve the REPOX statistics",
                    restRequest,
                    "/rest/statistics");

            rootElement.add(aggregatorOperations);
            rootElement.add(dataProviderOperations);
            rootElement.add(dataSourceOperations);
            rootElement.add(recordsOperations);
            rootElement.add(statisticsOperation);

            RestUtils.writeRestResponse(out, rootElement);
        }
    }
}
