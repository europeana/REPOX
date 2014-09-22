package harvesterUI.server.web.servlet;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import pt.utl.ist.repox.Urn;
import pt.utl.ist.repox.dataProvider.MessageType;
import pt.utl.ist.repox.metadataSchemas.MetadataSchemaVersion;
import pt.utl.ist.repox.util.InvalidInputException;
import pt.utl.ist.rest.services.web.WebServices;
import pt.utl.ist.rest.services.web.impl.WebServicesImpl;
import pt.utl.ist.rest.services.web.rest.InvalidRequestException;
import pt.utl.ist.rest.services.web.rest.RestRequest;
import pt.utl.ist.rest.services.web.rest.RestUtils;

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
 * Time: 21:53
 * To change this template use File | Settings | File Templates.
 */
public class ResponseRestLight extends ResponseOperations implements ResponseRest {
    public void response(HttpServletRequest request, HttpServletResponse response, WebServices webServicesLight) throws InvalidRequestException, IOException, DocumentException, ParseException, ClassNotFoundException, NoSuchMethodException, InvalidInputException, SQLException {

        WebServicesImpl webServices = (WebServicesImpl)webServicesLight;

        RestRequest restRequest = RestUtils.processRequest(RestServlet.BASE_URI, request);
        response.setContentType("text/xml");
        ServletOutputStream out = response.getOutputStream();

        webServices.setRequestURI(request.getRequestURL().toString());

        // Mappings
        if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals("removeMapping")) {
            String id = restRequest.getRequestParameters().get("id");

            if(id != null && !id.isEmpty()){
                webServices.removeMapping(out, id);
            }
            else{
                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error removing mapping:" +
                        "invalid arguments. Syntax: /rest/removeMapping?id=ID" +
                        "[Mandatory fields: id]");
            }
        }else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals("listMappings")) {
            webServices.listMappings(out);
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
                webServices.createMetadataSchema(out, id, "", namespace, designation, description,
                        notes, oaiAvailable,metadataSchemaVersionList);
            }
            else{
                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the" +
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
                webServices.updateMetadataSchema(out, id, oldId == null ? id : oldId, namespace, designation, description,
                        notes, oaiAvailable,metadataSchemaVersionList);
            }
            else{
                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the" +
                        "Metadata Schema: invalid arguments." +
                        "Syntax: /rest/updateSchema?shortDesignation=SHORT_DESIGNATION&oldShortDesignation=OLD_SHORT_DESIGNATION&namespace=NAMESPACE" +
                        "&designation=DESIGNATION&description=DESCRIPTION&notes=NOTES&oaiAvailable=TRUE/FALSE " +
                        "[mandatory fields: shortDesignation]");
            }
        }else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals("removeSchema")) {
            String id = restRequest.getRequestParameters().get("id");

            if(id != null && !id.isEmpty()){
                webServices.removeMetadataSchema(out, id);
            }
            else{
                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error removing schema:" +
                        "invalid arguments. Syntax: /rest/removeSchema?id=ID" +
                        "[Mandatory fields: id]");
            }
        }else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals("listSchemas")) {
            webServices.listMetadataSchemas(out);
        }
        // data providers
        else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals(RestServlet.DPROVIDERS_URL_NAME)) {

            if(restRequest.getUriHierarchy().size() == 1) {
                //list all available data providers operations
                Element rootElement = getDataProviderOperationListLight(restRequest);
                RestUtils.writeRestResponse(out, rootElement);
            }
            else { // operation over a Data Provider
                if(restRequest.getUriHierarchy().size() == 2) {

                    if(restRequest.getUriHierarchy().get(1).equals("list")) {
                        webServices.writeDataProviders(out);
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("create")) {
                        String name = restRequest.getRequestParameters().get("name");
                        String country = restRequest.getRequestParameters().get("country");
                        String description = restRequest.getRequestParameters().get("description");

                        if(name != null && !name.isEmpty() &&
                                country != null && !country.isEmpty()){
                            webServices.createDataProvider(out, name, country, description);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the" +
                                    "Data Provider: invalid arguments." +
                                    "Syntax: /rest/dataProviders/create?name=NAME" +
                                    "&description=DESCRIPTION&country=2_LETTERS_COUNTRY [mandatory fields: name, " +
                                    "country]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("update")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String name = restRequest.getRequestParameters().get("name");
                        String country = restRequest.getRequestParameters().get("country");
                        String description = restRequest.getRequestParameters().get("description");

                        if(id != null && !id.isEmpty() &&
                                name != null && !name.isEmpty() &&
                                country != null && !country.isEmpty()){
                            webServices.updateDataProvider(out, id, name, country, description);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating Data" +
                                    "Provider: invalid arguments. Syntax: /rest/dataProviders/update?id=ID" +
                                    "&name=NAME&description=DESCRIPTION&country=2_LETTERS_COUNTRY [mandatory fields: " +
                                    "id, name, country]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("delete")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.deleteDataProvider(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting Data" +
                                    "Provider: invalid arguments. Syntax: /rest/dataProviders/delete?id=ID [mandatory " +
                                    "field: id]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("getDataProvider")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.getDataProvider(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error retrieving Data" +
                                    "Provider: invalid arguments. Syntax: /rest/dataProviders/getDataProvider?id=ID [mandatory " +
                                    "field: id]");
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
                            webServices.writeDataSources(out);
                        }
                        else{
                            String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                            if (restRequest.getRequestParameters().size() == 1 &&
                                    dataProviderId != null && !dataProviderId.isEmpty()) {
                                webServices.writeDataSources(out, dataProviderId);
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error listing the" +
                                        "Data Source: invalid arguments." +
                                        "Syntax: /rest/dataSources/list?dataProviderId=DATA_PROVIDER_ID");
                            }
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createSruRecordUpdate")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        
                        // optionals
                        String marcFormat = restRequest.getRequestParameters().get("marcFormat");
                        
                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() ){
                            
                            webServices.createDataSourceSruRecordUpdate(out, dataProviderId, id, description, schema, namespace,
                                    metadataFormat, (marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createOai?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&oaiURL=URL_OAI_SERVER&oaiSet=OAI_SET [Mandatory" +
                                    " fields: dataProviderId, id, description, schema, namespace, metadataFormat, " +
                                    "oaiURL]. If metadataFormat=MarcXchange the field marcFormat (optional) can be filled with one " +
                            "of the following values: MARC21, UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createOai")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String oaiURL = restRequest.getRequestParameters().get("oaiURL");

                        // optionals
                        String oaiSet = restRequest.getRequestParameters().get("oaiSet");
                        String marcFormat = restRequest.getRequestParameters().get("marcFormat");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                oaiURL != null && !oaiURL.isEmpty()){

                            webServices.createDataSourceOai(out, dataProviderId, id, description, schema, namespace,
                                    metadataFormat, oaiURL, !oaiSet.isEmpty() ? oaiSet : null, (marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createOai?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&oaiURL=URL_OAI_SERVER&oaiSet=OAI_SET [Mandatory" +
                                    " fields: dataProviderId, id, description, schema, namespace, metadataFormat, " +
                                    "oaiURL]. If metadataFormat=MarcXchange the field marcFormat (optional) can be filled with one " +
                                    "of the following values: MARC21, UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createZ3950Timestamp")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
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

                            webServices.createDataSourceZ3950Timestamp(out, dataProviderId, id, description, schema,
                                    namespace, address, port, database, user, password, recordSyntax, charset,
                                    earliestTimestamp, recordIdPolicy, idXpath, namespacePrefix, namespaceUri);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createZ3950Timestamp?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&earliestTimestamp=DATE(YYYYMMDD)" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, address, port, database, " +
                                    "recordSyntax, charset, earliestTimestamp, recordIdPolicy (if " +
                                    "recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)].");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createZ3950IdSequence")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
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

                            webServices.createDataSourceZ3950IdSequence(out, dataProviderId, id, description, schema,
                                    namespace, address, port, database, user, password, recordSyntax, charset,
                                    maximumId, recordIdPolicy, idXpath, namespacePrefix, namespaceUri);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
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
                                server != null && !server.isEmpty() &&
                                ftpPath != null && !ftpPath.isEmpty()){

                            webServices.createDataSourceFtp(out, dataProviderId, id, description, schema, namespace,
                                    metadataFormat, isoFormat, charset, recordIdPolicy, idXpath, namespacePrefix,
                                    namespaceUri, recordXPath, server, (user != null && !user.isEmpty()) ? user : "",
                                    (password != null && !password.isEmpty()) ? password : "", ftpPath,
                                    (marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createFtp?dataProviderId=DATA_PROVIDER_ID&" +
                                    "id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&server=SERVER&user=USER&password=PASSWORD" +
                                    "&ftpPath=FTP_PATH [Mandatory fields: dataProviderId, id, description, schema, " +
                                    "namespace, metadataFormat, recordIdPolicy, server, ftpPath (Note: if" +
                                    "recordIdPolicy=IdExtracted the fields: idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory and if metadataFormat=ISO2709 the fields isoFormat and charset " +
                                    "are mandatory. If metadataFormat=MarcXchange the field marcFormat (optional) can " +
                                    "be filled with one of the following values: MARC21, UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createHttp")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
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
                                url != null && !url.isEmpty()){

                            webServices.createDataSourceHttp(out, dataProviderId, id, description, schema, namespace,
                                    metadataFormat, isoFormat, charset, recordIdPolicy, idXpath, namespacePrefix,
                                    namespaceUri, recordXPath, url,(marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createHttp?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&url=URL [Mandatory fields: dataProviderId, id, " +
                                    "description, schema, namespace, metadataFormat, recordIdPolicy, url (Note: if" +
                                    "recordIdPolicy=IdExtracted the fields: idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory and if metadataFormat=ISO2709 the fields isoFormat and charset " +
                                    "are mandatory)]. If metadataFormat=MarcXchange the field marcFormat (optional) can " +
                                    "be filled with one of the following values: MARC21, UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createFolder")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
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
                                folder != null && !folder.isEmpty()){

                            webServices.createDataSourceFolder(out, dataProviderId, id, description, schema, namespace,
                                    metadataFormat, isoFormat, charset, recordIdPolicy, idXpath, namespacePrefix,
                                    namespaceUri, recordXPath, folder,
                                    (marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createFolder?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&folder=FOLDER_PATH [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, metadataFormat, recordIdPolicy, " +
                                    "folder(Note: if recordIdPolicy=IdExtracted the fields: idXpath, namespacePrefix, " +
                                    "and namespaceUri are also mandatory and if metadataFormat=ISO2709 the fields " +
                                    "isoFormat and charset are also mandatory]. If metadataFormat=MarcXchange the field " +
                                    "marcFormat (optional) can be filled with one of the following values: MARC21, " +
                                    "UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateSruRecordUpdate")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        
                        // optionals
                        String marcFormat = restRequest.getRequestParameters().get("marcFormat");
                        
                        
                        if(id != null && !id.isEmpty()){
                            webServices.updateDataSourceSruRecordUpdate(out, id, description, schema, namespace, metadataFormat,
                                            (marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the OAI" +
                                    "Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateOai?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&oaiURL=URL_OAI_SERVER&oaiSet=OAI_SET " +
                                    "[Mandatory fields: id]. If metadataFormat=MarcXchange the field marcFormat (optional) can " +
                            "be filled with one of the following values: MARC21, UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateOai")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String oaiURL = restRequest.getRequestParameters().get("oaiURL");
                        String oaiSet = restRequest.getRequestParameters().get("oaiSet");

                        // optionals
                        String marcFormat = restRequest.getRequestParameters().get("marcFormat");


                        if(id != null && !id.isEmpty()){
                            webServices.updateDataSourceOai(out, id, description, schema, namespace, metadataFormat,
                                    oaiURL, !oaiSet.isEmpty() ? oaiSet : null,
                                    (marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the OAI" +
                                    "Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateOai?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&oaiURL=URL_OAI_SERVER&oaiSet=OAI_SET " +
                                    "[Mandatory fields: id]. If metadataFormat=MarcXchange the field marcFormat (optional) can " +
                                    "be filled with one of the following values: MARC21, UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateZ3950Timestamp")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
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
                            webServices.updateDataSourceZ3950Timestamp(out, id, description, schema, namespace, address,
                                    port, database, user, password, recordSyntax, charset, earliestTimestamp,
                                    recordIdPolicy, idXpath, namespacePrefix, namespaceUri);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the" +
                                    "Z39.50 Data Source with Time Stamp: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateZ3950Timestamp?id=DATA_SOURCE_ID&description=DESCRIPTION" +
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
                            webServices.updateDataSourceZ3950IdSequence(out, id, description, schema, namespace,
                                    address, port, database, user, password, recordSyntax, charset, maximumId,
                                    recordIdPolicy, idXpath, namespacePrefix, namespaceUri);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the Z39.50" +
                                    "Data Source with ID Sequence: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateZ3950IdSequence?id=DATA_SOURCE_ID&description=DESCRIPTION" +
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

                        if(id != null && !id.isEmpty()){
                            webServices.updateDataSourceFtp(out, id, description, schema, namespace, metadataFormat,
                                    isoFormat, charset, recordIdPolicy, idXpath, namespacePrefix, namespaceUri,
                                    recordXPath, server, (user != null && !user.isEmpty()) ? user : "",
                                    (password != null && !password.isEmpty()) ? password : "",
                                    ftpPath,(marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the FTP Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateFtp?dataProviderId=id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&server=SERVER&user=USER&password=PASSWORD" +
                                    "&ftpPath=FTP_PATH [Mandatory field: id (Note: if recordIdPolicy=IdExtracted the fields: idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory and if metadataFormat=ISO2709 the fields isoFormat and charset " +
                                    "are mandatory). If metadataFormat=MarcXchange the field marcFormat (optional) can " +
                                    "be filled with one of the following values: MARC21, UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateHttp")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
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

                        if(id != null && !id.isEmpty()){
                            webServices.updateDataSourceHttp(out, id, description, schema, namespace, metadataFormat,
                                    isoFormat, charset, recordIdPolicy, idXpath, namespacePrefix, namespaceUri,
                                    recordXPath, url,(marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the HTTP Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateHttp?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&url=URL [Mandatory field: id (Note: if" +
                                    "recordIdPolicy=IdExtracted the fields: idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory and if metadataFormat=ISO2709 the fields isoFormat and charset " +
                                    "are mandatory)]. If metadataFormat=MarcXchange the field marcFormat (optional) can " +
                                    "be filled with one of the following values: MARC21, UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateFolder")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
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
                            webServices.updateDataSourceFolder(out, id, description, schema, namespace, metadataFormat,
                                    isoFormat, charset, recordIdPolicy, idXpath, namespacePrefix, namespaceUri, recordXPath,
                                    folder,(marcFormat != null && !marcFormat.isEmpty()) ? marcFormat : null);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the Folder Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateFolder?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&folder=FOLDER_PATH [Mandatory fields: id (Note: if" +
                                    "recordIdPolicy=IdExtracted the fields: idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory and if metadataFormat=ISO2709 the fields isoFormat and charset " +
                                    "are mandatory)]. If metadataFormat=MarcXchange the field marcFormat (optional) can " +
                                    "be filled with one of the following values: MARC21, UNIMARC, Ibermarc or danMARC2.");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("delete")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.deleteDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting Data" +
                                    "Source: invalid arguments. Syntax: /rest/dataSources/delete?id=ID [Mandatory " +
                                    "field: id]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("getDataSource")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.getDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error retrieving Data" +
                                    "Source: invalid arguments. Syntax: /rest/dataSources/getDataSource?id=ID [Mandatory " +
                                    "field: id]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("countRecords")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.countRecordsDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error counting records from" +
                                    " Data Source: invalid arguments. Syntax: /rest/dataSources/countRecords?id=ID");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("lastIngestionDate")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.lastIngestionDateDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error retrieving last" +
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
                            webServices.startIngestDataSource(out, id, Boolean.valueOf(fullIngest));
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error starting the Data" +
                                    "Source ingestion: invalid arguments. Syntax: /rest/dataSources/startIngest?id=ID&" +
                                    "fullIngest=BOOLEAN [Mandatory fields: id, fullIngest]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("stopIngest")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.stopIngestDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error stopping the Data" +
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
                            webServices.scheduleIngestDataSource(out, id, firstRunDate, firstRunHour, frequency,
                                    xmonths, fullIngest);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error scheduling the" +
                                    "Data Source ingestion: invalid arguments. Syntax: /rest/dataSources/scheduleIngest?id=ID" +
                                    "&firstRunDate=YYYY-MM-DD&firstRunHour=HH:MM&frequency=ONCE_DAILY_WEEKLY_XMONTHLY" +
                                    "&xmonths=NUMBER&fullIngest=BOOLEAN [Mandatory fields: id, firstRunDate, firstRunHour, " +
                                    "frequency, xmonths, fullIngest]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("scheduleList")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.scheduleListDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                    "Source ingestion schedule: invalid arguments. Syntax: /rest/dataSources/scheduleList?id=ID " +
                                    "[Mandatory field: id]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("harvestStatus")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.harvestStatusDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                    "Source harvest status: invalid arguments. Syntax: /rest/dataSources/harvestStatus?id=ID " +
                                    "[Mandatory field: id]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("harvesting")) {
                        if(restRequest.getRequestParameters().size() == 0){
                            webServices.harvestingDataSources(out);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the list" +
                                    "of data sources harvesting: invalid arguments. Syntax: /rest/dataSources/harvesting");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("startExport")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String recordsPerFile = restRequest.getRequestParameters().get("recordsPerFile");
                        String metadataExportFormat = restRequest.getRequestParameters().get("metadataExportFormat");
                        if(id != null && !id.isEmpty() &&
                                recordsPerFile != null && !recordsPerFile.isEmpty()){
                            webServices.startExportDataSource(out, id, recordsPerFile, metadataExportFormat);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error starting the Data" +
                                    "Source exportation: invalid arguments. Syntax: /rest/dataSources/startExport?id=ID&" +
                                    "recordsPerFile=RECORDS_NUMBER&metadataExportFormat=METADATA_EXPORT_FORMAT [Mandatory fields: id, recordsPerFile]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("log")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.logDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
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
                        if(restRequest.getRequestParameters().get("recordId") != null){
                            webServices.getRecord(out, new Urn(restRequest.getRequestParameters().get("recordId")));
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                    "Source log: invalid arguments. Syntax: /rest/records/getRecord?recordId=RECORD_ID");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("saveRecord")){
                        if(restRequest.getRequestParameters().get("recordId") != null &&
                                restRequest.getRequestParameters().get("dataSourceId") != null &&
                                restRequest.getRequestParameters().get("recordString") != null){

                            webServices.saveRecord(out, restRequest.getRequestParameters().get("recordId"),
                                    restRequest.getRequestParameters().get("dataSourceId"),
                                    restRequest.getRequestParameters().get("recordString"));
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error saving record:" +
                                    "invalid arguments. Syntax: /rest/records/saveRecord?recordId=RECORD_ID" +
                                    "&dataSourceId=DATA_SOURCE_ID&recordString=RECORD_STRING");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("deleteRecord")){
                        if(restRequest.getRequestParameters().get("recordId") != null){
                            webServices.deleteRecord(out, restRequest.getRequestParameters().get("recordId"));
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting record:" +
                                    "invalid arguments. Syntax: /rest/records/deleteRecord?recordId=RECORD_ID");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("eraseRecord")){
                        if(restRequest.getRequestParameters().get("recordId") != null){
                            webServices.eraseRecord(out, restRequest.getRequestParameters().get("recordId"));
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error erasing record:" +
                                    "invalid arguments. Syntax: /rest/records/eraseRecord?recordId=RECORD_ID");
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
                        webServices.getStatistics(out, restRequest.getRequestParameters().get("type"));
                    }
                    else{
                        webServices.getStatistics(out, "ALL");
                    }
                }
            }
        }
        else{
            // list operations
            Element rootElement = DocumentHelper.createElement("repoxOperationsList");

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

            rootElement.add(dataProviderOperations);
            rootElement.add(dataSourceOperations);
            rootElement.add(recordsOperations);
            rootElement.add(statisticsOperation);

            RestUtils.writeRestResponse(out, rootElement);
        }


    }
}
