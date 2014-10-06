package pt.utl.ist.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.configuration.RepoxManager;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.MessageType;
import pt.utl.ist.dataProvider.dataSource.IdProvidedRecordIdPolicy;
import pt.utl.ist.rest.services.web.LightWebServices;
import pt.utl.ist.rest.services.web.DefaultWebServices;
import pt.utl.ist.rest.services.web.WebServices;
import pt.utl.ist.rest.services.web.rest.RestRequest;
import pt.utl.ist.rest.services.web.rest.RestUtils;
import pt.utl.ist.util.ProjectType;
import pt.utl.ist.util.PropertyUtil;
import pt.utl.ist.util.Urn;

public class RestServlet extends HttpServlet {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RestServlet.class);
    public static final String BASE_URI = "/rest";
    public static final String RECORDS_URL_NAME = "records";
    public static final String AGGREGATORS_URL_NAME = "aggregators";
    public static final String DPROVIDERS_URL_NAME = "dataProviders";
    public static final String DSOURCES_URL_NAME = "dataSources";
    public static final String STATISTICS_URL_NAME = "statistics";

    private RepoxManager repoxManager;
    public static ProjectType projectType;
    public ResponseRest responseRest;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        Properties properties = PropertyUtil.loadGuiConfiguration("gui.properties");
        projectType = ProjectType.valueOf(properties.getProperty("project.type"));

        if(projectType == ProjectType.LIGHT){
            ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
            this.repoxManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager();
        } else if(projectType == ProjectType.DEFAULT){
            ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
            this.repoxManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager();
        }
    }

    @Override
    /**
     * Processes a request related with registers and writes to a given OutputStream
     * the response.
     * Processed requests:
     *
     * .records operations list (this list)
     *  http://[server]/rest/records
     * .View record with urn "1" in default schema
     *  http://[server]/rest/records/1/
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if(projectType == ProjectType.LIGHT){
                LightWebServices webServicesLight = new LightWebServices();
                responseRest = new LightResponseRest();
                responseRest.response(request, response, webServicesLight);
            }else if(projectType == ProjectType.DEFAULT){
                DefaultWebServices webServicesEuropeana = new DefaultWebServices();
                responseRest = new DefaultResponseRest();
                responseRest.response(request, response, webServicesEuropeana);
            }

        } catch (Exception e) {
            log.error("Error in Rest GET request", e);
        }
    }



    @Override
    /**
     * Processes a post related with registers and writes to a given OutputStream
     * the response.
     * Processed requests:
     *
     * .Save new record to dataSource
     *  http://[server]/rest/records/
     *  Parameters: operation - save; dataSourceId - the DataSource Id; record - the record String
     *
     * .Mark record as deleted from dataSource
     *  http://[server]/rest/records/
     *  Parameters: operation - delete; dataSourceId - the DataSource Id; recordId - the record Id
     *
     * .Permanently remove record from dataSource
     *  http://[server]/rest/records/
     *  Parameters: operation - erase; dataSourceId - the DataSource Id; recordId - the record Id
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            WebServices webServices = null;
            if(projectType == ProjectType.LIGHT){
                webServices = new LightWebServices();
            } else if(projectType == ProjectType.DEFAULT){
                webServices = new DefaultWebServices();
            }

            RestRequest restRequest = RestUtils.processRequest(BASE_URI, request);
            response.setContentType("text/xml");
            ServletOutputStream out = response.getOutputStream();

            if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                    && restRequest.getUriHierarchy().get(0).equals("createMapping")) {
                String id = restRequest.getRequestParameters().get("id");
                String description = restRequest.getRequestParameters().get("description");
                String srcSchemaId = restRequest.getRequestParameters().get("srcSchemaId");
                String srcSchemaVersion = restRequest.getRequestParameters().get("srcSchemaVersion");
                String destSchemaId = restRequest.getRequestParameters().get("destSchemaId");
                String destSchemaVersion = restRequest.getRequestParameters().get("destSchemaVersion");
                String isXslVersion2 = restRequest.getRequestParameters().get("isXslVersion2");
                String xslFilename = restRequest.getRequestParameters().get("xslFilename");

                InputStream xsdFile = request.getInputStream();

                if(id != null && !id.isEmpty() &&
                        xslFilename != null && !xslFilename.isEmpty() &&
                        description != null && !description.isEmpty() &&
                        srcSchemaId != null && !srcSchemaId.isEmpty() &&
                        srcSchemaVersion != null && !srcSchemaVersion.isEmpty() &&
                        destSchemaId != null && !destSchemaId.isEmpty() &&
                        destSchemaVersion != null && !destSchemaVersion.isEmpty() &&
                        isXslVersion2 != null && !isXslVersion2.isEmpty() &&
                        xsdFile != null && xsdFile.available() != 0){
                    webServices.createMapping(out, id,description,srcSchemaId,srcSchemaVersion,destSchemaId,destSchemaVersion,isXslVersion2,xslFilename,xsdFile);
                }
                else{
                    webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating mapping:" +
                            "invalid arguments. Syntax: /rest/createMapping?id=ID" +
                            "&description=DESCRIPTION&srcSchemaId=SOURCE_SCHEMA_ID&srcSchemaVersion=SOURCE_SCHEMA_VERSION" +
                            "&destSchemaId=DESTINATION_SCHEMA_ID&destSchemaVersion=DESTINATION_SCHEMA_VERSION " +
                            "&isXslVersion2=TRUE/FALSE&xslFilename=XSL_FILE_NAME" +
                            "[Mandatory fields: all the above]" +" NOTE: XSL FILE MUST BE UPLOADED THROUGH HTTP POST");
                }
            }
            else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                    && restRequest.getUriHierarchy().get(0).equals("updateMapping")) {
                String id = restRequest.getRequestParameters().get("id");
                String oldMappingId = restRequest.getRequestParameters().get("oldId");
                String description = restRequest.getRequestParameters().get("description");
                String srcSchemaId = restRequest.getRequestParameters().get("srcSchemaId");
                String srcSchemaVersion = restRequest.getRequestParameters().get("srcSchemaVersion");
                String destSchemaId = restRequest.getRequestParameters().get("destSchemaId");
                String destSchemaVersion = restRequest.getRequestParameters().get("destSchemaVersion");
                String isXslVersion2 = restRequest.getRequestParameters().get("isXslVersion2");
                String xslFilename = restRequest.getRequestParameters().get("xslFilename");

                InputStream xsdFile = request.getInputStream();

                if(id != null && !id.isEmpty()){
                    webServices.updateMapping(out, id,description,srcSchemaId,srcSchemaVersion,destSchemaId,destSchemaVersion,isXslVersion2,xslFilename,xsdFile,oldMappingId == null? id : oldMappingId);
                }
                else{
                    webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating mapping:" +
                            "invalid arguments. Syntax: /rest/updateMapping?id=ID&oldId=OLD_ID" +
                            "&description=DESCRIPTION&srcSchemaId=SOURCE_SCHEMA_ID&srcSchemaVersion=SOURCE_SCHEMA_VERSION" +
                            "&destSchemaId=DESTINATION_SCHEMA_ID&destSchemaVersion=DESTINATION_SCHEMA_VERSION " +
                            "&isXslVersion2=TRUE/FALSE&xslFilename=XSL_FILE_NAME" +
                            "[Mandatory fields: all the above]" +" NOTE: XSL FILE MUST BE UPLOADED THROUGH HTTP POST");
                }
            }else if((restRequest.getUriHierarchy() != null) && restRequest.getUriHierarchy().size() == 1
                    && restRequest.getUriHierarchy().get(0).equals(RECORDS_URL_NAME)) {
                String operationParameter = restRequest.getRequestParameters().get("operation");
                String recordParameter = restRequest.getRequestParameters().get("record");
                String recordIdParameter = restRequest.getRequestParameters().get("recordId");

                if(operationParameter != null && operationParameter.equals("save")
                        && recordParameter != null && !recordParameter.trim().isEmpty()) { // Save Record
                    String dataSourceId = restRequest.getRequestParameters().get("dataSourceId");

                    boolean validParameters = validateSaveParameters(restRequest, out, dataSourceId, operationParameter, recordIdParameter);

                    if(!validParameters) {
                        return;
                    }

                    webServices.saveRecord(out, dataSourceId, recordParameter, recordIdParameter);
                }
                else if(operationParameter != null && operationParameter.equals("delete")
                        && validateRecordId(restRequest, out, recordIdParameter)) { // Mark record as deleted
                    webServices.deleteRecord(out, recordIdParameter);
                }
                else if(operationParameter != null && operationParameter.equals("erase")
                        && validateRecordId(restRequest, out, recordIdParameter)) { // Permanently remove record
                    webServices.eraseRecord(out, recordIdParameter);
                }
                else {
                    RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), out);
                }

            }
            else if(webServices instanceof DefaultWebServices){
                if(restRequest.getUriHierarchy().get(1).equals("createZ3950IdList")) {
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
                    String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                    String idXpath = restRequest.getRequestParameters().get("idXpath");
                    String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                    String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                    InputStream xsdFile = request.getInputStream();

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
                            xsdFile != null && xsdFile.available() != 0 &&
                            recordIdPolicy != null && !recordIdPolicy.isEmpty()){
                        ((DefaultWebServices)webServices).createDataSourceZ3950IdList(out, dataProviderId, id, description, nameCode,
                                name, exportPath, schema, namespace, address, port, database, user, password,
                                recordSyntax, charset, xsdFile, recordIdPolicy, idXpath, namespacePrefix,
                                namespaceUri);
                    }
                    else{
                        webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                "Source: invalid arguments." +
                                "Syntax: /rest/dataSources/createZ3950IdList?dataProviderId=DATA_PROVIDER_ID" +
                                "&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                "&schema=SCHEMA&namespace=NAMESPACE" +
                                "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&filePath=FILE_PATH" +
                                "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory fields: " +
                                "dataProviderId, id, description, schema, namespace, address, port, database, " +
                                "recordSyntax, charset, filePath, recordIdPolicy (if " +
                                "recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                "are mandatory)]");
                    }
                }else if(restRequest.getUriHierarchy().get(1).equals("updateZ3950IdList")) {
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
                    String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                    String idXpath = restRequest.getRequestParameters().get("idXpath");
                    String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                    String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                    InputStream xsdFile = request.getInputStream();

                    if(id != null && !id.isEmpty()){
                        ((DefaultWebServices)webServices).updateDataSourceZ3950IdList(out, id, description, nameCode,
                                name, exportPath, schema, namespace, address, port, database, user, password,
                                recordSyntax, charset, xsdFile, recordIdPolicy, idXpath, namespacePrefix,
                                namespaceUri);
                    }
                    else{
                        webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the" +
                                "Z39.50 Data Source with ID list: invalid arguments." +
                                "Syntax: /rest/dataSources/updateZ3950IdList?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                "&schema=SCHEMA&namespace=NAMESPACE" +
                                "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&filePath=FILE_PATH" +
                                "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory field: " +
                                "id (if recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                "are mandatory)]");
                    }
                }
            }
            else {
                RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), response.getOutputStream());
            }

        } catch (Exception e) {
            log.error("Error in Rest POST request", e);
        }
    }

    /**
     * @param restRequest
     * @return a simple xml report of the REST request
     */
    private String xmlDebugRequest(RestRequest restRequest) {
        StringBuilder xmlResponse = new StringBuilder();
        xmlResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        xmlResponse.append("<xml><response>");

        xmlResponse.append("<parsedRequestURI>" + restRequest.getParsedRequestURI() + "</parsedRequestURI>");

        for (String currentURIDir : restRequest.getUriHierarchy()) {
            xmlResponse.append("<uriDir>" + currentURIDir + "</uriDir>");
        }

        if(restRequest.getRequestParameters() != null) {
            for (Entry<String, String> stringStringEntry : restRequest.getRequestParameters().entrySet()) {
                Entry<String, String> elem = stringStringEntry;
                xmlResponse.append("<requestParameter>");
                xmlResponse.append("<currentKey>" + elem.getKey() + "</currentKey>");
                xmlResponse.append("<currentValue>" + elem.getValue() + "</currentValue>");
                xmlResponse.append("</requestParameter>");
            }
        }

        xmlResponse.append("</response></xml>");

        return xmlResponse.toString();
    }

    private boolean validateOperationParameter(RestRequest restRequest, OutputStream out, String operationParameter)
            throws IOException, DocumentException {
        if(operationParameter == null || operationParameter.trim().isEmpty()) {
            RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), "operationParameter is mandatory ", out);
            return false;
        }

        return true;
    }

    private boolean validateDataSource(RestRequest restRequest, OutputStream out, String dataSourceId)
            throws IOException, DocumentException {
        DataSource dataSource = repoxManager.getDataManager().getDataSourceContainer(dataSourceId).getDataSource();
        if(dataSource == null) {
            RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), "Data Source is invalid", out);
            return false;
        }

        return true;
    }

    private boolean validateRecordId(RestRequest restRequest, OutputStream out, String recordIdParameter)
            throws IOException, DocumentException {
        if(recordIdParameter == null || recordIdParameter.trim().isEmpty()) {
            RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), "recordId Parameter is mandatory ", out);
            return false;
        }

        return true;
    }

    private boolean validateRecordUrn(RestRequest restRequest, ServletOutputStream out, String recordUrnString)
            throws IOException, DocumentException, SQLException {
        Urn recordUrn;

        try {
            recordUrn = new Urn(recordUrnString);
        }
        catch (Exception e) {
            return false;
        }

        return !(!validateDataSource(restRequest, out, recordUrn.getDataSourceId())
                || !validateRecordId(restRequest, out, recordUrnString)
                || ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().getRecord(recordUrn) == null);

    }

    private boolean validateSaveParameters(RestRequest restRequest, OutputStream out, String dataSourceId,
                                           String operationParameter, String recordIdParameter) throws IOException, DocumentException {
        if(!validateOperationParameter(restRequest, out, operationParameter)) {
            RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(),
                    "operationParameter is mandatory for this operation: " + operationParameter,
                    out);
            return false;
        }
        else if(!validateDataSource(restRequest, out, dataSourceId)) {
            RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(),
                    "dataSourceId parameter is mandatory for this operation: " + operationParameter,
                    out);
            return false;
        }
        else {
            DataSource dataSource = repoxManager.getDataManager().getDataSourceContainer(dataSourceId).getDataSource();

            if(dataSource.getRecordIdPolicy() instanceof IdProvidedRecordIdPolicy
                    && !validateRecordId(restRequest, out, recordIdParameter)) {
                RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(),
                        "recordId parameter is mandatory for this operation: " + operationParameter,
                        out);
                return false;
            }
        }

        return true;
    }
}
