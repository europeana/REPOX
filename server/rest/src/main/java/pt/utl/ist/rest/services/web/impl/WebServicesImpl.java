package pt.utl.ist.rest.services.web.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.dataProvider.DefaultDataSourceContainer;
import pt.utl.ist.dataProvider.MessageType;
import pt.utl.ist.dataProvider.dataSource.DataSourceUtil;
import pt.utl.ist.dataProvider.dataSource.FileExtractStrategy;
import pt.utl.ist.dataProvider.dataSource.FileRetrieveStrategy;
import pt.utl.ist.dataProvider.dataSource.IdExtractedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.IdGeneratedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.IdProvidedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.RecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.SimpleFileExtractStrategy;
import pt.utl.ist.externalServices.ExternalRestService;
import pt.utl.ist.ftp.FtpFileRetrieveStrategy;
import pt.utl.ist.http.HttpFileRetrieveStrategy;
import pt.utl.ist.marc.DirectoryImporterDataSource;
import pt.utl.ist.marc.FolderFileRetrieveStrategy;
import pt.utl.ist.marc.Iso2709FileExtractStrategy;
import pt.utl.ist.marc.MarcXchangeFileExtractStrategy;
import pt.utl.ist.metadataSchemas.MetadataSchema;
import pt.utl.ist.metadataSchemas.MetadataSchemaVersion;
import pt.utl.ist.metadataTransformation.MetadataFormat;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.metadataTransformation.TransformationsFileManager;
import pt.utl.ist.oai.OaiDataSource;
import pt.utl.ist.repox.marc.CharacterEncoding;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.reports.LogUtil;
import pt.utl.ist.repox.util.FileUtilSecond;
import pt.utl.ist.repox.util.StringUtil;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.repox.util.date.DateUtil;
import pt.utl.ist.repox.util.exceptions.AlreadyExistsException;
import pt.utl.ist.repox.util.exceptions.IncompatibleInstanceException;
import pt.utl.ist.repox.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.repox.util.exceptions.ObjectNotFoundException;
import pt.utl.ist.repox.util.exceptions.SameStylesheetTransformationException;
import pt.utl.ist.rest.services.web.WebServices;
import pt.utl.ist.rest.services.web.rest.RestUtils;
import pt.utl.ist.sru.SruRecordUpdateDataSource;
import pt.utl.ist.statistics.RecordCount;
import pt.utl.ist.statistics.RepoxStatistics;
import pt.utl.ist.statistics.StatisticsManager;
import pt.utl.ist.task.IngestDataSource;
import pt.utl.ist.task.ScheduledTask;
import pt.utl.ist.task.Task;
import pt.utl.ist.util.Urn;
import pt.utl.ist.z3950.DataSourceZ3950;
import pt.utl.ist.z3950.Harvester;
import pt.utl.ist.z3950.IdListHarvester;
import pt.utl.ist.z3950.IdSequenceHarvester;
import pt.utl.ist.z3950.Target;
import pt.utl.ist.z3950.TimestampHarvester;

/**
 */
public class WebServicesImpl implements WebServices {
    private static final Logger log = Logger.getLogger(WebServicesImpl.class);
    private String requestURI;

    @SuppressWarnings("javadoc")
    public String getRequestURI() {
        return requestURI;
    }

    @SuppressWarnings("javadoc")
    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    /**
     * Creates a new instance of this class.
     */
    public WebServicesImpl() {
        super();
    }

    public void writeDataProviders(OutputStream out) throws DocumentException, IOException {
        List<DataProvider> dataProviders = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProviders();

        Element dataProvidersElement = DocumentHelper.createElement("dataProviders");

        for (DataProvider dataProvider : dataProviders) {
            Element currentDataProviderElement = dataProvider.createElement(false);
            dataProvidersElement.add(currentDataProviderElement);
        }

        RestUtils.writeRestResponse(out, dataProvidersElement);
    }

    public void createDataProvider(OutputStream out, String name, String country, String description) throws DocumentException {
        try {
            DataProvider dataProvider = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().createDataProvider(name,
                    country, description);
            RestUtils.writeRestResponse(out, dataProvider.createElement(false));
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Data Provider.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Data Provider already exists.");
        }
    }

    public void updateDataProvider(OutputStream out, String id, String name, String country, String description) throws DocumentException {
        try {
            DataProvider dataProvider = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().updateDataProvider(id,
                    name, country, description);
            RestUtils.writeRestResponse(out, dataProvider.createElement(false));
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error updating Data Provider: id \"" + id + "\" was not found.");
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error updating Data Provider with id \"" + id + "\".");
        }
    }


    public void deleteDataProvider(OutputStream out, String dataProviderId) throws DocumentException {
        try {
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().deleteDataProvider(dataProviderId);
            Element currentDataProviderElement = DocumentHelper.createElement("success");
            currentDataProviderElement.setText("Data Provider with id \"" + dataProviderId + "\" was successfully deleted.");
            RestUtils.writeRestResponse(out, currentDataProviderElement);
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error deleting Data Provider with id \"" + dataProviderId + "\".");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error deleting Data Provider. Data provider with id \"" + dataProviderId + "\" was not found.");
        }
    }

    public void getDataProvider(OutputStream out, String dataProviderId) throws DocumentException {
        try {
            DataProvider dataProvider = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProvider(dataProviderId);

            if(dataProvider != null){
                Element dataProviderElement = dataProvider.createElement(false);
                RestUtils.writeRestResponse(out, dataProviderElement);
            }
            else{
                createErrorMessage(out, MessageType.NOT_FOUND, "Error retrieving Data Provider. Data Provider with id \"" + dataProviderId + "\" was not found.");
            }
        }
        catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error retrieving Data Provider with id \"" + dataProviderId + "\".");
        }
    }


    public void writeDataSources(OutputStream out) throws DocumentException, IOException {
        HashMap<String, DataSourceContainer> dataSourceContainers = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().loadDataSourceContainers();

        Element dataSourcesElement = DocumentHelper.createElement("dataSources");

        for (DataSourceContainer dataSourceContainer : dataSourceContainers.values()) {
            Element currentDatasourceElement = dataSourceContainer.getDataSource().createElement();
            dataSourcesElement.add(currentDatasourceElement);
        }

        RestUtils.writeRestResponse(out, dataSourcesElement);
    }

    public void writeDataSources(OutputStream out, String dataProviderId) throws DocumentException, IOException {
        DataProvider dataProvider = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProvider(dataProviderId);

        if(dataProvider != null){
            Element dataSourcesElement = DocumentHelper.createElement("dataSources");

            for (DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers().values()) {
                Element currentDatasourceElement = dataSourceContainer.getDataSource().createElement();
                dataSourcesElement.add(currentDatasourceElement);
            }

            RestUtils.writeRestResponse(out, dataSourcesElement);
        }
        else{
            createErrorMessage(out, MessageType.NOT_FOUND, "Error retrieving Data Sources. Data provider with id \"" + dataProviderId + "\" was not found.");
        }
    }

    
    public void createDataSourceOai(OutputStream out, String dataProviderId, String id, String description,
            String schema, String namespace, String metadataFormat, String oaiSourceURL,
            String oaiSet, String marcFormat) throws DocumentException {
        saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        
        try {
            DataSource dataSource = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataSourceOai(dataProviderId,
                    id, description, schema, namespace, metadataFormat, oaiSourceURL, oaiSet, new HashMap<String, MetadataTransformation>(),
                    new ArrayList<ExternalRestService>(),marcFormat);
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (SQLException e) {
            createErrorMessage(out, MessageType.ERROR_DATABASE, "Error creating Data Base.");
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Data Source.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error creating a Data Source OAI. Data provider with id \"" + dataProviderId + "\" was not found.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating a Data Source OAI. Data source with id \"" + id + "\" already exists.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating a Data Source OAI. Invalid argument " + e.getMessage() + ".");
        }
    }
    

    public void createDataSourceSruRecordUpdate(OutputStream out, String dataProviderId, String id, String description,
                                    String schema, String namespace, String metadataFormat, 
                                    String marcFormat) throws DocumentException {
        saveNewMetadataSchema(metadataFormat, schema, namespace, out);

        try {
            DataSource dataSource = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataSourceSruRecordUpdate(dataProviderId,
                    id, description, schema, namespace, metadataFormat, new HashMap<String, MetadataTransformation>(),
                    new ArrayList<ExternalRestService>(),marcFormat);
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (SQLException e) {
            createErrorMessage(out, MessageType.ERROR_DATABASE, "Error creating Data Base.");
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Data Source.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error creating a Data Source SRU. Data provider with id \"" + dataProviderId + "\" was not found.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating a Data Source SRU. Data source with id \"" + id + "\" already exists.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating a Data Source SRU. Invalid argument " + e.getMessage() + ".");
        }
    }


    public void createDataSourceZ3950Timestamp(OutputStream out, String dataProviderId, String id, String description,
                                               String schema, String namespace, String address, String port, String database,
                                               String user, String password, String recordSyntax, String charset, String earliestTimestampString,
                                               String recordIdPolicyClass, String idXpath, String namespacePrefix, String namespaceUri) throws DocumentException, IOException, ParseException {
        saveNewMetadataSchema(MetadataFormat.MarcXchange.toString(), schema, namespace, out);
        try {
            Map<String, String> namespaces = new TreeMap<String, String>();
            namespaces.put(namespacePrefix, namespaceUri);

            DataSource dataSource = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataSourceZ3950Timestamp(dataProviderId,
                    id, description, schema, namespace, address, port, database, user, password,
                    recordSyntax, charset, earliestTimestampString, recordIdPolicyClass, idXpath, namespaces,
                    new HashMap<String, MetadataTransformation>(),new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating a Data Source Z39.50 Timestamp. Data source with id \"" + id + "\" already exists.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error creating a Data Source Z39.50 Timestamp. Data provider with id \"" + dataProviderId + "\" was not found.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating a Data Source Z39.50 Timestamp. Invalid argument " + e.getMessage() + ".");
        } catch (SQLException e) {
            createErrorMessage(out, MessageType.ERROR_DATABASE, "Error creating Data Source Z39.50 Timestamp.");
        }
    }

    public void createDataSourceZ3950IdList(OutputStream out, String dataProviderId, String id, String description,
                                            String schema, String namespace, String address, String port, String database,
                                            String user, String password, String recordSyntax, String charset, InputStream xslFile,
                                            String recordIdPolicyClass, String idXpath, String namespacePrefix, String namespaceUri) throws DocumentException, IOException, ParseException {
        saveNewMetadataSchema(MetadataFormat.MarcXchange.toString(), schema, namespace, out);
        try {
            Map<String, String> namespaces = new TreeMap<String, String>();
            namespaces.put(namespacePrefix, namespaceUri);

            File temporaryFile = IdListHarvester.getIdListFilePermanent();
            byte[] buffer = new byte[8 * 1024];
            try {
                OutputStream output = new FileOutputStream(temporaryFile);
                try {
                    int bytesRead;
                    while ((bytesRead = xslFile.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                } finally {
                    output.close();
                }
            } finally {
                xslFile.close();
            }

            DataSource dataSource = ((DefaultDataManager) ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataSourceZ3950IdList(dataProviderId,
                    id, description, schema, namespace, address, port, database, user, password,
                    recordSyntax, charset, temporaryFile.getAbsolutePath(), recordIdPolicyClass, idXpath, namespaces,
                    new HashMap<String, MetadataTransformation>(),new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error creating a Data Source Z39.50 Id List. Data provider with id \"" + dataProviderId + "\" was not found.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating a Data Source Z39.50 Id List. Data source with id \"" + id + "\" already exists.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating a Data Source Z39.50 Id List. Invalid argument " + e.getMessage() + ".");
        } catch (SQLException e) {
            createErrorMessage(out, MessageType.ERROR_DATABASE, "Error creating Data Source Z39.50 Id List.");
        }
    }

    public void createDataSourceZ3950IdSequence(OutputStream out, String dataProviderId, String id, String description,
                                                String schema, String namespace, String address, String port,
                                                String database, String user, String password, String recordSyntax,
                                                String charset, String maximumIdString, String recordIdPolicyClass,
                                                String idXpath, String namespacePrefix, String namespaceUri) throws DocumentException, IOException, ParseException {
        saveNewMetadataSchema(MetadataFormat.MarcXchange.toString(), schema, namespace, out);
        try {
            Map<String, String> namespaces = new TreeMap<String, String>();
            namespaces.put(namespacePrefix, namespaceUri);

            DataSource dataSource = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataSourceZ3950IdSequence(dataProviderId,
                    id, description, schema, namespace, address, port, database, user, password,
                    recordSyntax, charset, maximumIdString, recordIdPolicyClass, idXpath, namespaces,
                    new HashMap<String, MetadataTransformation>(),new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating a Data Source Z39.50 Id Sequence. Data source with id \"" + id + "\" already exists.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error creating a Data Source Z39.50 Id Sequence. Data provider with id \"" + dataProviderId + "\" was not found.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating a Data Source Z39.50 Id Sequence. Invalid argument " + e.getMessage() + ".");
        } catch (SQLException e) {
            createErrorMessage(out, MessageType.ERROR_DATABASE, "Error creating Data Source Z39.50 Id Sequence.");
        }
    }


    public void createDataSourceFtp(OutputStream out, String dataProviderId, String id, String description, String schema, String namespace,
                                    String metadataFormat, String isoFormat, String charset,
                                    String recordIdPolicyClass, String idXpath, String namespacePrefix, String namespaceUri,
                                    String recordXPath, String server, String user, String password, String ftpPath, String marcFormat) throws DocumentException {
        saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        try {
            Map<String, String> namespaces = new TreeMap<String, String>();
            namespaces.put(namespacePrefix, namespaceUri);

            DataSource dataSource = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataSourceFtp(dataProviderId,
                    id, description, schema, namespace, metadataFormat, isoFormat, charset,
                    recordIdPolicyClass, idXpath, namespaces, recordXPath, server, user, password,
                    ftpPath, new HashMap<String, MetadataTransformation>(),new ArrayList<ExternalRestService>(),marcFormat);
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Data Source FTP.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating a Data Source FTP. Data source with id \"" + id + "\" already exists.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error creating a Data Source FTP. Data provider with id \"" + dataProviderId + "\" was not found.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating Data Source. Invalid argument " + e.getMessage() + ".");
        } catch (SQLException e) {
            createErrorMessage(out, MessageType.ERROR_DATABASE, "Error creating Data Source FTP.");
        }

    }

    public void createDataSourceHttp(OutputStream out, String dataProviderId, String id, String description, String schema, String namespace,
                                     String metadataFormat, String isoFormat, String charset,
                                     String recordIdPolicyClass, String idXpath, String namespacePrefix, String namespaceUri,
                                     String recordXPath, String url, String marcFormat) throws DocumentException {
        saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        try {
            Map<String, String> namespaces = new TreeMap<String, String>();
            namespaces.put(namespacePrefix, namespaceUri);

            DataSource dataSource = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataSourceHttp(dataProviderId,
                    id, description, schema, namespace, metadataFormat, isoFormat, charset,
                    recordIdPolicyClass, idXpath, namespaces, recordXPath, url,
                    new HashMap<String, MetadataTransformation>(),new ArrayList<ExternalRestService>(),marcFormat);
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Data Source HTTP.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating a Data Source HTTP. Data source with id \"" + id + "\" already exists.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error creating a Data Source HTTP. Data provider with id \"" + dataProviderId + "\" was not found.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating Data Source HTTP. Invalid argument " + e.getMessage() + ".");
        } catch (SQLException e) {
            createErrorMessage(out, MessageType.ERROR_DATABASE, "Error creating Data Source HTTP.");
        }
    }


    public void createDataSourceFolder(OutputStream out, String dataProviderId, String id, String description, String schema, String namespace,
                                       String metadataFormat, String isoFormat, String charset,
                                       String recordIdPolicyClass, String idXpath, String namespacePrefix, String namespaceUri,
                                       String recordXPath, String sourcesDirPath, String marcFormat) throws DocumentException {
        saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        try {
            Map<String, String> namespaces = new TreeMap<String, String>();
            namespaces.put(namespacePrefix, namespaceUri);

            DataSource dataSource = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataSourceFolder(dataProviderId,
                    id, description, schema, namespace, metadataFormat, isoFormat, charset,
                    recordIdPolicyClass, idXpath, namespaces, recordXPath, sourcesDirPath,
                    new HashMap<String, MetadataTransformation>(),new ArrayList<ExternalRestService>(),marcFormat);
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Data Source.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error creating a Data Source Folder. Data provider with id \"" + dataProviderId + "\" was not found.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating a Data Source Folder. Data source with id \"" + id + "\" already exists.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating Data Source Folder. Invalid " + e.getMessage() + ".");
        } catch (SQLException e) {
            createErrorMessage(out, MessageType.ERROR_DATABASE, "Error creating Data Source.");
        }
    }

    public void updateDataSourceSruRecordUpdate(OutputStream out, String id, String description, String schema, String namespace,
            String metadataFormat, String marcFormat) throws DocumentException {
        if(metadataFormat != null && schema != null && namespace != null)
            saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        try {
            Map<String, MetadataTransformation> transformations = new HashMap<String, MetadataTransformation>();
            List<ExternalRestService> externalServices = new ArrayList<ExternalRestService>();
            
            DefaultDataSourceContainer dataSourceContainerOld = (DefaultDataSourceContainer)(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(id));
            if(dataSourceContainerOld != null){
                DataSource dataSourceOld = dataSourceContainerOld.getDataSource();
                DataProvider dataProviderParent = getDataProviderParent(dataSourceOld.getId());
                
                if(!(dataSourceOld instanceof SruRecordUpdateDataSource)){
                    DataSource newDataSource = new SruRecordUpdateDataSource(dataProviderParent, id, description, schema, namespace, metadataFormat,
                            new IdGeneratedRecordIdPolicy(), new TreeMap<String, MetadataTransformation>());
                    newDataSource.setAccessPoints(dataSourceOld.getAccessPoints());
                    newDataSource.setStatus(dataSourceOld.getStatus());
                    
                    setLastIngestDate(true, dataSourceOld, newDataSource);
                    newDataSource.setOldTasksList(dataSourceOld.getOldTasksList());
                    newDataSource.setTags(dataSourceOld.getTags());
                    
                    dataProviderParent.getDataSourceContainers().remove(dataSourceOld.getId());
                    dataProviderParent.getDataSourceContainers().put(newDataSource.getId(), new DefaultDataSourceContainer(newDataSource));
                    dataSourceOld = newDataSource;
                }
                
                if(description == null)
                    description = dataSourceOld.getDescription();
                if(schema == null)
                    schema = dataSourceOld.getSchema();
                if(namespace == null)
                    namespace = dataSourceOld.getNamespace();
                if(metadataFormat == null)
                    metadataFormat = dataSourceOld.getMetadataFormat();
                if(marcFormat == null)
                    marcFormat = dataSourceOld.getMarcFormat();
                if(dataSourceOld.getMetadataTransformations().size() > 0)
                    transformations = dataSourceOld.getMetadataTransformations();
                if(dataSourceOld.getExternalRestServices().size() > 0)
                    externalServices = dataSourceOld.getExternalRestServices();
            }
            DataSource dataSource = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateDataSourceSruRecordUpdate(id, id,
                    description, schema, namespace, metadataFormat, 
                    transformations, externalServices, marcFormat, false);
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error updating an OAI Data Source. Data Source with id \"" + id + "\" was not an OAI-PMH data source.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error updating an OAI Data Source. Data Provider was not found or the Data Source with id \"" + id + "\" was not found.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error updating an OAI Data Source. Invalid argument " + e.getMessage() + ".");
        } catch (IncompatibleInstanceException e) {
            createErrorMessage(out, MessageType.INCOMPATIBLE_TYPE, "Error updating an OAI Data Source. Data Source with id \"" + id + "\" was not an OAI-PMH data source.");
        }
    }
    public void updateDataSourceOai(OutputStream out, String id, String description, String schema, String namespace,
                                    String metadataFormat, String oaiSourceURL, String oaiSet, String marcFormat) throws DocumentException {
        if(metadataFormat != null && schema != null && namespace != null)
            saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        try {
            Map<String, MetadataTransformation> transformations = new HashMap<String, MetadataTransformation>();
            List<ExternalRestService> externalServices = new ArrayList<ExternalRestService>();

            DefaultDataSourceContainer dataSourceContainerOld = (DefaultDataSourceContainer)(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(id));
            if(dataSourceContainerOld != null){
                DataSource dataSourceOld = dataSourceContainerOld.getDataSource();
                DataProvider dataProviderParent = getDataProviderParent(dataSourceOld.getId());

                if(!(dataSourceOld instanceof OaiDataSource)){
                    DataSource newDataSource = new OaiDataSource(dataProviderParent, id, description, schema, namespace, metadataFormat,
                            oaiSourceURL, oaiSet, new IdProvidedRecordIdPolicy(), new TreeMap<String, MetadataTransformation>());
                    newDataSource.setAccessPoints(dataSourceOld.getAccessPoints());
                    newDataSource.setStatus(dataSourceOld.getStatus());

                    setLastIngestDate(true, dataSourceOld, newDataSource);
                    newDataSource.setOldTasksList(dataSourceOld.getOldTasksList());
                    newDataSource.setTags(dataSourceOld.getTags());

                    dataProviderParent.getDataSourceContainers().remove(dataSourceOld.getId());
                    dataProviderParent.getDataSourceContainers().put(newDataSource.getId(), new DefaultDataSourceContainer(newDataSource));
                    dataSourceOld = newDataSource;
                }

                if(description == null)
                    description = dataSourceOld.getDescription();
                if(schema == null)
                    schema = dataSourceOld.getSchema();
                if(namespace == null)
                    namespace = dataSourceOld.getNamespace();
                if(metadataFormat == null)
                    metadataFormat = dataSourceOld.getMetadataFormat();
                if(oaiSourceURL == null)
                    oaiSourceURL = ((OaiDataSource)dataSourceOld).getOaiSourceURL();
                if(oaiSet == null)
                    oaiSet = ((OaiDataSource)dataSourceOld).getOaiSet();
                if(marcFormat == null)
                    marcFormat = dataSourceOld.getMarcFormat();
                if(dataSourceOld.getMetadataTransformations().size() > 0)
                    transformations = dataSourceOld.getMetadataTransformations();
                if(dataSourceOld.getExternalRestServices().size() > 0)
                    externalServices = dataSourceOld.getExternalRestServices();
            }
            DataSource dataSource = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateDataSourceOai(id, id,
                    description, schema, namespace, metadataFormat, oaiSourceURL, oaiSet,
                    transformations, externalServices, marcFormat, false);
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error updating an OAI Data Source. Data Source with id \"" + id + "\" was not an OAI-PMH data source.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error updating an OAI Data Source. Data Provider was not found or the Data Source with id \"" + id + "\" was not found.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error updating an OAI Data Source. Invalid argument " + e.getMessage() + ".");
        } catch (IncompatibleInstanceException e) {
            createErrorMessage(out, MessageType.INCOMPATIBLE_TYPE, "Error updating an OAI Data Source. Data Source with id \"" + id + "\" was not an OAI-PMH data source.");
        }
    }

    public void updateDataSourceZ3950Timestamp(OutputStream out, String id, String description, String schema, String namespace,
                                               String address, String port, String database, String user, String password,
                                               String recordSyntax, String charset, String earliestTimestampString,
                                               String recordIdPolicyClass, String idXpath, String namespacePrefix,
                                               String namespaceUri) throws DocumentException, IOException, ParseException {
        if(schema != null && namespace != null)
            saveNewMetadataSchema(MetadataFormat.MarcXchange.toString(), schema, namespace, out);
        try {
            Map<String, String> namespaces = new TreeMap<String, String>();
            Map<String, MetadataTransformation> transformations = new HashMap<String, MetadataTransformation>();
            List<ExternalRestService> externalServices = new ArrayList<ExternalRestService>();

            DefaultDataSourceContainer dataSourceContainerOld = (DefaultDataSourceContainer)(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(id));if(dataSourceContainerOld != null){
                DataSource dataSourceOld = dataSourceContainerOld.getDataSource();
                DataProvider dataProviderParent = getDataProviderParent(dataSourceOld.getId());

                if(!(dataSourceOld instanceof DataSourceZ3950)){
                    CharacterEncoding targetCharacterEncoding = CharacterEncoding.get(charset);
                    Target target = new Target(address, Integer.valueOf(port), database, user, password, targetCharacterEncoding, recordSyntax);

                    Harvester harvestMethod;
                    try{
                        harvestMethod = new TimestampHarvester(target, DateUtil.string2Date(earliestTimestampString, "yyyyMMdd"));
                    }
                    catch (ParseException e){
                        throw new InvalidArgumentsException("earliestTimestamp");
                    }

                    RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                    DataSource newDataSource = new DataSourceZ3950(dataProviderParent, id, description, schema, namespace,
                            harvestMethod, recordIdPolicy, new TreeMap<String, MetadataTransformation>());
                    newDataSource.setAccessPoints(dataSourceOld.getAccessPoints());
                    newDataSource.setStatus(dataSourceOld.getStatus());

                    setLastIngestDate(true, dataSourceOld, newDataSource);
                    newDataSource.setOldTasksList(dataSourceOld.getOldTasksList());
                    newDataSource.setTags(dataSourceOld.getTags());

                    dataProviderParent.getDataSourceContainers().remove(dataSourceOld.getId());
                    dataProviderParent.getDataSourceContainers().put(newDataSource.getId(), new DefaultDataSourceContainer(newDataSource));
                    dataSourceOld = newDataSource;
                }

                if(description == null)
                    description = dataSourceOld.getDescription();
                if(schema == null)
                    schema = dataSourceOld.getSchema();
                if(namespace == null)
                    namespace = dataSourceOld.getNamespace();
                if(address == null)
                    address = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getAddress();
                if(port == null)
                    port = String.valueOf(((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getPort());
                if(database == null)
                    database = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getDatabase();
                if(user == null)
                    user = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getUser();
                if(password == null)
                    password = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getPassword();
                if(recordSyntax == null)
                    recordSyntax = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getRecordSyntax();

                if(earliestTimestampString == null)
                    earliestTimestampString = DateUtil.date2String(((TimestampHarvester)((DataSourceZ3950)dataSourceOld).getHarvestMethod()).getEarliestTimestamp(), "yyyyMMdd");

                if(charset == null && ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getCharacterEncoding() != null)
                    charset = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getCharacterEncoding().toString();

                // todo... make a lot of tests
                if(recordIdPolicyClass == null){
                    if(dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy)
                        recordIdPolicyClass = IdExtractedRecordIdPolicy.class.getSimpleName();
                    else if(dataSourceOld.getRecordIdPolicy() instanceof IdGeneratedRecordIdPolicy)
                        recordIdPolicyClass = IdGeneratedRecordIdPolicy.class.getSimpleName();
                }

                if(recordIdPolicyClass.equals(IdExtractedRecordIdPolicy.class.getSimpleName())){
                    if(idXpath == null && dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();
                        idXpath = idExtracted.getIdentifierXpath();
                    }
                    if(idXpath == null)
                        throw new InvalidArgumentsException("idXpath is missing");

                    if(namespacePrefix == null && namespaceUri == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();
                        namespaces = idExtracted.getNamespaces();
                    }
                    else if(namespacePrefix == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        // update to new namespaceUri
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();

                        if(idExtracted.getNamespaces() != null && idExtracted.getNamespaces().size() > 0) {
                            for(String prefix : idExtracted.getNamespaces().keySet()) {
                                namespacePrefix = prefix;
                                break;
                            }
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else
                            throw new InvalidArgumentsException("namespacePrefix is missing");
                    }
                    else if(namespaceUri == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        // update to new namespacePrefix
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();

                        if(idExtracted.getNamespaces() != null && idExtracted.getNamespaces().size() > 0) {
                            for(String uri : idExtracted.getNamespaces().values()) {
                                namespaceUri = uri;
                                break;
                            }
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else
                            throw new InvalidArgumentsException("namespaceUri is missing");
                    }
                    else {
                        // use the new values
                        if(namespacePrefix != null && namespaceUri != null) {
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else if(namespacePrefix != null && namespaceUri == null) {
                            throw new InvalidArgumentsException("namespaceUri is missing");
                        }
                        else if(namespacePrefix == null && namespaceUri != null) {
                            throw new InvalidArgumentsException("namespacePrefix is missing");
                        }
                    }
                }
                else{
                    // IdGenerated - empty fields
                    idXpath = null;
                    namespaces = new TreeMap<String, String>();
                }
                if(dataSourceOld.getMetadataTransformations().size() > 0)
                    transformations = dataSourceOld.getMetadataTransformations();
                if(dataSourceOld.getExternalRestServices().size() > 0)
                    externalServices = dataSourceOld.getExternalRestServices();
            }

            DataSource dataSource = ((DefaultDataManager) ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateDataSourceZ3950Timestamp(id, id,
                    description, schema, namespace, address, port, database, user, password,
                    recordSyntax, charset, earliestTimestampString, recordIdPolicyClass, idXpath, namespaces,
                    transformations, externalServices, false);
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error updating a Z39.50 Data Source with Time Stamp. Data Source with id \"" + id + "\" was not found.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error updating a Z39.50 Data Source with Time Stamp. Invalid argument " + e.getMessage() + ".");
        } catch (IncompatibleInstanceException e) {
            createErrorMessage(out, MessageType.INCOMPATIBLE_TYPE, "Error updating a Z39.50 Data Source with Time Stamp. Data Source with id \"" + id + "\" was not a Z39.50 Data Source with Time Stamp.");
        }
    }

    public void updateDataSourceZ3950IdList(OutputStream out, String id, String description, String schema,
                                            String namespace, String address, String port, String database,
                                            String user, String password, String recordSyntax, String charset,
                                            InputStream xslFile, String recordIdPolicyClass, String idXpath,
                                            String namespacePrefix, String namespaceUri) throws DocumentException, IOException, ParseException {
        String filePath = null;
        if(xslFile != null){
            File temporaryFile = IdListHarvester.getIdListFilePermanent();
            filePath = temporaryFile.getAbsolutePath();
            byte[] buffer = new byte[8 * 1024];
            try {
                OutputStream output = new FileOutputStream(temporaryFile);
                try {
                    int bytesRead;
                    while ((bytesRead = xslFile.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                } finally {
                    output.close();
                }
            } finally {
                xslFile.close();
            }
        }

        if(schema != null && namespace != null)
            saveNewMetadataSchema(MetadataFormat.MarcXchange.toString(), schema, namespace, out);
        try {
            Map<String, String> namespaces = new TreeMap<String, String>();
            Map<String, MetadataTransformation> transformations = new HashMap<String, MetadataTransformation>();
            List<ExternalRestService> externalServices = new ArrayList<ExternalRestService>();

            DefaultDataSourceContainer dataSourceContainerOld = (DefaultDataSourceContainer)(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(id));
            if(dataSourceContainerOld != null){
                DataSource dataSourceOld = dataSourceContainerOld.getDataSource();
                DataProvider dataProviderParent = getDataProviderParent(dataSourceOld.getId());

                if(!(dataSourceOld instanceof DataSourceZ3950)){
                    CharacterEncoding targetCharacterEncoding = CharacterEncoding.get(charset);
                    Target target = new Target(address, Integer.valueOf(port), database, user, password, targetCharacterEncoding, recordSyntax);

                    File file;
                    if(!filePath.isEmpty()){
                        try{
                            FileUtils.forceDelete(((IdListHarvester) ((DataSourceZ3950) dataSourceOld).getHarvestMethod()).getIdListFile());
                        }
                        catch (Exception e){
                            log.error("Error removing z39.50 file: " + ((IdListHarvester)((DataSourceZ3950) dataSourceOld).getHarvestMethod()).getIdListFile());
                        }
                        file = ((IdListHarvester)((DataSourceZ3950) dataSourceOld).getHarvestMethod()).getIdListFile();
                        FileUtils.copyFile(new File(filePath), file);
                        FileUtils.forceDelete(new File(filePath));
                        ((IdListHarvester)((DataSourceZ3950) dataSourceOld).getHarvestMethod()).setIdListFile(file);
                    }
                    else{
                        file = ((IdListHarvester)((DataSourceZ3950) dataSourceOld).getHarvestMethod()).getIdListFile();
                    }
                    Harvester harvestMethod = new IdListHarvester(target, file);

                    RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                    DataSource newDataSource = new DataSourceZ3950(dataProviderParent, id, description, schema, namespace,
                            harvestMethod, recordIdPolicy, new TreeMap<String, MetadataTransformation>());
                    newDataSource.setAccessPoints(dataSourceOld.getAccessPoints());
                    newDataSource.setStatus(dataSourceOld.getStatus());

                    setLastIngestDate(true, dataSourceOld, newDataSource);
                    newDataSource.setOldTasksList(dataSourceOld.getOldTasksList());
                    newDataSource.setTags(dataSourceOld.getTags());

                    dataProviderParent.getDataSourceContainers().remove(dataSourceOld.getId());
                    dataProviderParent.getDataSourceContainers().put(newDataSource.getId(), new DefaultDataSourceContainer(newDataSource));
                    dataSourceOld = newDataSource;
                }

                if(description == null)
                    description = dataSourceOld.getDescription();
                if(schema == null)
                    schema = dataSourceOld.getSchema();
                if(namespace == null)
                    namespace = dataSourceOld.getNamespace();
                if(address == null)
                    address = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getAddress();
                if(port == null)
                    port = String.valueOf(((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getPort());
                if(database == null)
                    database = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getDatabase();
                if(user == null)
                    user = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getUser();
                if(password == null)
                    password = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getPassword();
                if(recordSyntax == null)
                    recordSyntax = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getRecordSyntax();
                if(charset == null && ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getCharacterEncoding() != null)
                    charset = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getCharacterEncoding().toString();
                if(filePath == null)
                    filePath = (((IdListHarvester)((DataSourceZ3950)dataSourceOld).getHarvestMethod()).getIdListFile().getAbsolutePath());

                // todo... make a lot of tests
                if(recordIdPolicyClass == null){
                    if(dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy)
                        recordIdPolicyClass = IdExtractedRecordIdPolicy.class.getSimpleName();
                    else if(dataSourceOld.getRecordIdPolicy() instanceof IdGeneratedRecordIdPolicy)
                        recordIdPolicyClass = IdGeneratedRecordIdPolicy.class.getSimpleName();
                }

                if(recordIdPolicyClass.equals(IdExtractedRecordIdPolicy.class.getSimpleName())){
                    if(idXpath == null && dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();
                        idXpath = idExtracted.getIdentifierXpath();
                    }
                    if(idXpath == null)
                        throw new InvalidArgumentsException("idXpath is missing");

                    if(namespacePrefix == null && namespaceUri == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();
                        namespaces = idExtracted.getNamespaces();
                    }
                    else if(namespacePrefix == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        // update to new namespaceUri
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();

                        if(idExtracted.getNamespaces() != null && idExtracted.getNamespaces().size() > 0) {
                            for(String prefix : idExtracted.getNamespaces().keySet()) {
                                namespacePrefix = prefix;
                                break;
                            }
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else
                            throw new InvalidArgumentsException("namespacePrefix is missing");
                    }
                    else if(namespaceUri == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        // update to new namespacePrefix
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();

                        if(idExtracted.getNamespaces() != null && idExtracted.getNamespaces().size() > 0) {
                            for(String uri : idExtracted.getNamespaces().values()) {
                                namespaceUri = uri;
                                break;
                            }
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else
                            throw new InvalidArgumentsException("namespaceUri is missing");
                    }
                    else {
                        // use the new values
                        if(namespacePrefix != null && namespaceUri != null) {
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else if(namespacePrefix != null && namespaceUri == null) {
                            throw new InvalidArgumentsException("namespaceUri is missing");
                        }
                        else if(namespacePrefix == null && namespaceUri != null) {
                            throw new InvalidArgumentsException("namespacePrefix is missing");
                        }
                    }
                }
                else{
                    // IdGenerated - empty fields
                    idXpath = null;
                    namespaces = new TreeMap<String, String>();
                }
                if(dataSourceOld.getMetadataTransformations().size() > 0)
                    transformations = dataSourceOld.getMetadataTransformations();
                if(dataSourceOld.getExternalRestServices().size() > 0)
                    externalServices = dataSourceOld.getExternalRestServices();
            }

            DataSource dataSource = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateDataSourceZ3950IdList(id, id,
                    description, schema, namespace, address, port, database, user, password,
                    recordSyntax, charset, filePath, recordIdPolicyClass, idXpath, namespaces,
                    transformations, externalServices, false);
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error updating a Z39.50 Data Source with ID List. Data Source with id \"" + id + "\" was not found.");
        } catch (IncompatibleInstanceException e) {
            createErrorMessage(out, MessageType.INCOMPATIBLE_TYPE, "Error updating a Z39.50 Data Source with ID List. Data Source with id \"" + id + "\" was not a Z39.50 Data Source with ID List.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error updating a Z39.50 Data Source with ID List. Invalid argument " + e.getMessage() + ".");
        }
    }

    public void updateDataSourceZ3950IdSequence(OutputStream out, String id, String description, String schema,
                                                String namespace, String address, String port, String database,
                                                String user, String password, String recordSyntax, String charset,
                                                String maximumIdString, String recordIdPolicyClass, String idXpath,
                                                String namespacePrefix, String namespaceUri) throws DocumentException, IOException, ParseException {
        if(schema != null && namespace != null)
            saveNewMetadataSchema(MetadataFormat.MarcXchange.toString(), schema, namespace, out);
        try {
            Map<String, String> namespaces = new TreeMap<String, String>();
            Map<String, MetadataTransformation> transformations = new HashMap<String, MetadataTransformation>();
            List<ExternalRestService> externalServices = new ArrayList<ExternalRestService>();

            DefaultDataSourceContainer dataSourceContainerOld = (DefaultDataSourceContainer)(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(id));if(dataSourceContainerOld != null){

                DataSource dataSourceOld = dataSourceContainerOld.getDataSource();
                DataProvider dataProviderParent = getDataProviderParent(dataSourceOld.getId());

                if(!(dataSourceOld instanceof DataSourceZ3950)){
                    CharacterEncoding targetCharacterEncoding = CharacterEncoding.get(charset);
                    Target target = new Target(address, Integer.valueOf(port), database, user, password, targetCharacterEncoding, recordSyntax);

                    Long maximumId = (maximumIdString != null && !maximumIdString.isEmpty() ? Long.valueOf(maximumIdString) : null);
                    Harvester harvestMethod = new IdSequenceHarvester(target, maximumId);

                    RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                    DataSource newDataSource = new DataSourceZ3950(dataProviderParent, id, description, schema, namespace,
                            harvestMethod, recordIdPolicy, new TreeMap<String, MetadataTransformation>());
                    newDataSource.setAccessPoints(dataSourceOld.getAccessPoints());
                    newDataSource.setStatus(dataSourceOld.getStatus());

                    setLastIngestDate(true, dataSourceOld, newDataSource);
                    newDataSource.setOldTasksList(dataSourceOld.getOldTasksList());
                    newDataSource.setTags(dataSourceOld.getTags());

                    dataProviderParent.getDataSourceContainers().remove(dataSourceOld.getId());
                    dataProviderParent.getDataSourceContainers().put(newDataSource.getId(), new DefaultDataSourceContainer(newDataSource));
                    dataSourceOld = newDataSource;
                }

                if(description == null)
                    description = dataSourceOld.getDescription();
                if(schema == null)
                    schema = dataSourceOld.getSchema();
                if(namespace == null)
                    namespace = dataSourceOld.getNamespace();
                if(address == null)
                    address = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getAddress();
                if(port == null)
                    port = String.valueOf(((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getPort());
                if(database == null)
                    database = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getDatabase();
                if(user == null)
                    user = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getUser();
                if(password == null)
                    password = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getPassword();
                if(recordSyntax == null)
                    recordSyntax = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getRecordSyntax();
                if(charset == null && ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getCharacterEncoding() != null)
                    charset = ((DataSourceZ3950)dataSourceOld).getHarvestMethod().getTarget().getCharacterEncoding().toString();
                if(maximumIdString == null)
                    maximumIdString = String.valueOf(((IdSequenceHarvester)((DataSourceZ3950)dataSourceOld).getHarvestMethod()).getMaximumId());

                // todo... make a lot of tests
                if(recordIdPolicyClass == null){
                    if(dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy)
                        recordIdPolicyClass = IdExtractedRecordIdPolicy.class.getSimpleName();
                    else if(dataSourceOld.getRecordIdPolicy() instanceof IdGeneratedRecordIdPolicy)
                        recordIdPolicyClass = IdGeneratedRecordIdPolicy.class.getSimpleName();
                }

                if(recordIdPolicyClass.equals(IdExtractedRecordIdPolicy.class.getSimpleName())){
                    if(idXpath == null && dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();
                        idXpath = idExtracted.getIdentifierXpath();
                    }
                    if(idXpath == null)
                        throw new InvalidArgumentsException("idXpath is missing");

                    if(namespacePrefix == null && namespaceUri == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();
                        namespaces = idExtracted.getNamespaces();
                    }
                    else if(namespacePrefix == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        // update to new namespaceUri
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();

                        if(idExtracted.getNamespaces() != null && idExtracted.getNamespaces().size() > 0) {
                            for(String prefix : idExtracted.getNamespaces().keySet()) {
                                namespacePrefix = prefix;
                                break;
                            }
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else
                            throw new InvalidArgumentsException("namespacePrefix is missing");
                    }
                    else if(namespaceUri == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        // update to new namespacePrefix
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();

                        if(idExtracted.getNamespaces() != null && idExtracted.getNamespaces().size() > 0) {
                            for(String uri : idExtracted.getNamespaces().values()) {
                                namespaceUri = uri;
                                break;
                            }
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else
                            throw new InvalidArgumentsException("namespaceUri is missing");
                    }
                    else {
                        // use the new values
                        if(namespacePrefix != null && namespaceUri != null) {
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else if(namespacePrefix != null && namespaceUri == null) {
                            throw new InvalidArgumentsException("namespaceUri is missing");
                        }
                        else if(namespacePrefix == null && namespaceUri != null) {
                            throw new InvalidArgumentsException("namespacePrefix is missing");
                        }
                    }
                }
                else{
                    // IdGenerated - empty fields
                    idXpath = null;
                    namespaces = new TreeMap<String, String>();
                }
                if(dataSourceOld.getMetadataTransformations().size() > 0)
                    transformations = dataSourceOld.getMetadataTransformations();
                if(dataSourceOld.getExternalRestServices().size() > 0)
                    externalServices = dataSourceOld.getExternalRestServices();
            }

            DataSource dataSource = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateDataSourceZ3950IdSequence(id, id,
                    description, schema, namespace, address, port, database, user, password,
                    recordSyntax, charset, maximumIdString, recordIdPolicyClass, idXpath, namespaces,
                    transformations, externalServices, false);
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error updating a Z39.50 Data Source with ID Sequence. Data Provider was not found.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error updating a Z39.50 Data Source with ID List. Invalid argument " + e.getMessage() + ".");
        } catch (IncompatibleInstanceException e) {
            createErrorMessage(out, MessageType.INCOMPATIBLE_TYPE, "Error updating a Z39.50 Data Source with ID Sequence. Data Source with id \"" + id + "\" was not a Z39.50 Data Source with ID Sequence.");
        }
    }

    public void updateDataSourceFtp(OutputStream out, String id, String description, String schema, String namespace,
                                    String metadataFormat, String isoFormat, String charset, String recordIdPolicyClass,
                                    String idXpath, String namespacePrefix, String namespaceUri, String recordXPath,
                                    String server, String user, String password, String ftpPath, String marcFormat) throws DocumentException {
        if(metadataFormat != null && schema != null && namespace != null)
            saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        try {
            Map<String, String> namespaces = new TreeMap<String, String>();
            Map<String, MetadataTransformation> transformations = new HashMap<String, MetadataTransformation>();
            List<ExternalRestService> externalServices = new ArrayList<ExternalRestService>();

            DefaultDataSourceContainer dataSourceContainerOld = (DefaultDataSourceContainer)(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(id));
            if(dataSourceContainerOld != null){
                DataSource dataSourceOld = dataSourceContainerOld.getDataSource();
                DataProvider dataProviderParent = getDataProviderParent(dataSourceOld.getId());
                if(!(dataSourceOld instanceof DirectoryImporterDataSource)){
                    String accessType;
                    if(user.equals("") && password.equals("")){
                        accessType = FtpFileRetrieveStrategy.ANONYMOUS;
                    }
                    else{
                        accessType = FtpFileRetrieveStrategy.NORMAL;
                    }

                    FileRetrieveStrategy retrieveStrategy = new FtpFileRetrieveStrategy(server, user, password, accessType, ftpPath);
                    RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                    if(recordIdPolicy != null){
                        CharacterEncoding characterEncoding = null;
                        FileExtractStrategy extractStrategy = DataSourceUtil.extractStrategyString(metadataFormat, isoFormat);
                        if(extractStrategy.getClass() == Iso2709FileExtractStrategy.class) {
                            if(charset.equals("")){
                                throw new InvalidArgumentsException("charset");
                            }
                            characterEncoding = CharacterEncoding.get(charset);
                        }
                        else if(extractStrategy.getClass() == MarcXchangeFileExtractStrategy.class) {
                        }
                        else if(extractStrategy.getClass() == SimpleFileExtractStrategy.class) {
                        }

                        DataSource newDataSource = new DirectoryImporterDataSource(dataProviderParent, id, description, schema, namespace, metadataFormat, extractStrategy,
                                retrieveStrategy, characterEncoding, FtpFileRetrieveStrategy.getOutputFtpPath(server, id), recordIdPolicy, new TreeMap<String, MetadataTransformation>(), recordXPath, new HashMap<String, String>());
                        newDataSource.setAccessPoints(dataSourceOld.getAccessPoints());
                        newDataSource.setStatus(dataSourceOld.getStatus());

                        setLastIngestDate(true, dataSourceOld, newDataSource);
                        newDataSource.setOldTasksList(dataSourceOld.getOldTasksList());
                        newDataSource.setTags(dataSourceOld.getTags());

                        dataProviderParent.getDataSourceContainers().remove(dataSourceOld.getId());
                        dataProviderParent.getDataSourceContainers().put(newDataSource.getId(), new DefaultDataSourceContainer(newDataSource));
                        dataSourceOld = newDataSource;
                    }
                }

                if(description == null)
                    description = dataSourceOld.getDescription();
                if(schema == null)
                    schema = dataSourceOld.getSchema();
                if(namespace == null)
                    namespace = dataSourceOld.getNamespace();
                if(metadataFormat == null)
                    metadataFormat = dataSourceOld.getMetadataFormat();

                if(isoFormat == null && metadataFormat.equals(MetadataFormat.ISO2709.toString())){
                    if(((DirectoryImporterDataSource)dataSourceOld).getExtractStrategy() instanceof Iso2709FileExtractStrategy) {
                        Iso2709FileExtractStrategy extractStrategy = (Iso2709FileExtractStrategy) ((DirectoryImporterDataSource)dataSourceOld).getExtractStrategy();
                        isoFormat = extractStrategy.getIsoImplementationClass().toString();
                    }
                    else
                        throw new InvalidArgumentsException("isoFormat is missing");
                }
                if(charset == null && ((DirectoryImporterDataSource)dataSourceOld).getCharacterEncoding() != null)
                    charset = ((DirectoryImporterDataSource)dataSourceOld).getCharacterEncoding().toString();

                if(recordIdPolicyClass == null){
                    if(dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy)
                        recordIdPolicyClass = IdExtractedRecordIdPolicy.class.getSimpleName();
                    else if(dataSourceOld.getRecordIdPolicy() instanceof IdGeneratedRecordIdPolicy)
                        recordIdPolicyClass = IdGeneratedRecordIdPolicy.class.getSimpleName();
                }

                if(recordIdPolicyClass.equals(IdExtractedRecordIdPolicy.class.getSimpleName())){
                    if(idXpath == null && dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();
                        idXpath = idExtracted.getIdentifierXpath();
                    }
                    if(idXpath == null)
                        throw new InvalidArgumentsException("idXpath is missing");

                    if(namespacePrefix == null && namespaceUri == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();
                        namespaces = idExtracted.getNamespaces();
                    }
                    else if(namespacePrefix == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        // update to new namespaceUri
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();

                        if(idExtracted.getNamespaces() != null && idExtracted.getNamespaces().size() > 0) {
                            for(String prefix : idExtracted.getNamespaces().keySet()) {
                                namespacePrefix = prefix;
                                break;
                            }
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else
                            throw new InvalidArgumentsException("namespacePrefix is missing");
                    }
                    else if(namespaceUri == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        // update to new namespacePrefix
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();

                        if(idExtracted.getNamespaces() != null && idExtracted.getNamespaces().size() > 0) {
                            for(String uri : idExtracted.getNamespaces().values()) {
                                namespaceUri = uri;
                                break;
                            }
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else
                            throw new InvalidArgumentsException("namespaceUri is missing");
                    }
                    else {
                        // use the new values
                        if(namespacePrefix != null && namespaceUri != null) {
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else if(namespacePrefix != null && namespaceUri == null) {
                            throw new InvalidArgumentsException("namespaceUri is missing");
                        }
                        else if(namespacePrefix == null && namespaceUri != null) {
                            throw new InvalidArgumentsException("namespacePrefix is missing");
                        }
                    }
                }
                else{
                    // IdGenerated - empty fields
                    idXpath = null;
                    namespaces = new TreeMap<String, String>();
                }

                if(recordXPath == null)
                    recordXPath = ((DirectoryImporterDataSource)dataSourceOld).getRecordXPath();
                if(server == null)
                    server = ((FtpFileRetrieveStrategy)((DirectoryImporterDataSource)dataSourceOld).getRetrieveStrategy()).getServer();
                if(user == null)
                    user = ((FtpFileRetrieveStrategy)((DirectoryImporterDataSource)dataSourceOld).getRetrieveStrategy()).getUser();
                if(password == null)
                    password = ((FtpFileRetrieveStrategy)((DirectoryImporterDataSource)dataSourceOld).getRetrieveStrategy()).getPassword();
                if(ftpPath == null)
                    ftpPath = ((FtpFileRetrieveStrategy)((DirectoryImporterDataSource)dataSourceOld).getRetrieveStrategy()).getFtpPath();
                if(marcFormat == null)
                    marcFormat = dataSourceOld.getMarcFormat();
                if(dataSourceOld.getMetadataTransformations().size() > 0)
                    transformations = dataSourceOld.getMetadataTransformations();
                if(dataSourceOld.getExternalRestServices().size() > 0)
                    externalServices = dataSourceOld.getExternalRestServices();
            }

            if(user == null)
                user = "";

            if(password == null)
                password = "";

            DataSource dataSource = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateDataSourceFtp(id, id,
                    description, schema, namespace, metadataFormat, isoFormat, charset,
                    recordIdPolicyClass, idXpath, namespaces, recordXPath, server, user, password,
                    ftpPath, transformations, externalServices, marcFormat, false);
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error updating Data Source FTP.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error updating Data Source FTP. Data Provider was not found.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error updating Data Source FTP. Invalid argument " + e.getMessage() + ".");
        } catch (IncompatibleInstanceException e) {
            createErrorMessage(out, MessageType.INCOMPATIBLE_TYPE, "Error updating Data Source FTP. Data Source with id \"" + id + "\" was not a FTP data source.");
        }

    }

    public void updateDataSourceHttp(OutputStream out, String id, String description, String schema, String namespace,
                                     String metadataFormat, String isoFormat, String charset, String recordIdPolicyClass,
                                     String idXpath, String namespacePrefix, String namespaceUri, String recordXPath,
                                     String url, String marcFormat) throws DocumentException {
        if(metadataFormat != null && schema != null && namespace != null)
            saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        try {
            Map<String, String> namespaces = new TreeMap<String, String>();
            Map<String, MetadataTransformation> transformations = new HashMap<String, MetadataTransformation>();
            List<ExternalRestService> externalServices = new ArrayList<ExternalRestService>();

            DefaultDataSourceContainer dataSourceContainerOld = (DefaultDataSourceContainer)(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(id));if(dataSourceContainerOld != null){

                DataSource dataSourceOld = dataSourceContainerOld.getDataSource();
                DataProvider dataProviderParent = getDataProviderParent(dataSourceOld.getId());
                if(!(dataSourceOld instanceof DirectoryImporterDataSource)){
                    FileRetrieveStrategy retrieveStrategy = new HttpFileRetrieveStrategy(url);
                    RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                    if(recordIdPolicy != null){
                        CharacterEncoding characterEncoding = null;
                        FileExtractStrategy extractStrategy = DataSourceUtil.extractStrategyString(metadataFormat, isoFormat);
                        if(extractStrategy.getClass() == Iso2709FileExtractStrategy.class) {
                            if(charset.equals("")){
                                throw new InvalidArgumentsException("charset");
                            }
                            characterEncoding = CharacterEncoding.get(charset);
                        }
                        else if(extractStrategy.getClass() == MarcXchangeFileExtractStrategy.class) {
                        }
                        else if(extractStrategy.getClass() == SimpleFileExtractStrategy.class) {
                        }

                        DataSource newDataSource = new DirectoryImporterDataSource(dataProviderParent, id, description, schema, namespace, metadataFormat, extractStrategy,
                                retrieveStrategy, characterEncoding, HttpFileRetrieveStrategy.getOutputHttpPath(url, id), recordIdPolicy, new TreeMap<String, MetadataTransformation>(), recordXPath, new HashMap<String, String>());
                        newDataSource.setAccessPoints(dataSourceOld.getAccessPoints());
                        newDataSource.setStatus(dataSourceOld.getStatus());

                        setLastIngestDate(true, dataSourceOld, newDataSource);
                        newDataSource.setOldTasksList(dataSourceOld.getOldTasksList());
                        newDataSource.setTags(dataSourceOld.getTags());

                        dataProviderParent.getDataSourceContainers().remove(dataSourceOld.getId());
                        dataProviderParent.getDataSourceContainers().put(newDataSource.getId(), new DefaultDataSourceContainer(newDataSource));
                        dataSourceOld = newDataSource;
                    }
                }

                if(description == null)
                    description = dataSourceOld.getDescription();
                if(schema == null)
                    schema = dataSourceOld.getSchema();
                if(namespace == null)
                    namespace = dataSourceOld.getNamespace();
                if(metadataFormat == null)
                    metadataFormat = dataSourceOld.getMetadataFormat();

                if(isoFormat == null && metadataFormat.equals(MetadataFormat.ISO2709.toString())){
                    if(((DirectoryImporterDataSource)dataSourceOld).getExtractStrategy() instanceof Iso2709FileExtractStrategy) {
                        Iso2709FileExtractStrategy extractStrategy = (Iso2709FileExtractStrategy) ((DirectoryImporterDataSource)dataSourceOld).getExtractStrategy();
                        isoFormat = extractStrategy.getIsoImplementationClass().toString();
                    }
                    else
                        throw new InvalidArgumentsException("isoFormat is missing");
                }
                if(charset == null && ((DirectoryImporterDataSource)dataSourceOld).getCharacterEncoding() != null)
                    charset = ((DirectoryImporterDataSource)dataSourceOld).getCharacterEncoding().toString();

                if(recordIdPolicyClass == null){
                    if(dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy)
                        recordIdPolicyClass = IdExtractedRecordIdPolicy.class.getSimpleName();
                    else if(dataSourceOld.getRecordIdPolicy() instanceof IdGeneratedRecordIdPolicy)
                        recordIdPolicyClass = IdGeneratedRecordIdPolicy.class.getSimpleName();
                }

                if(recordIdPolicyClass.equals(IdExtractedRecordIdPolicy.class.getSimpleName())){
                    if(idXpath == null && dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();
                        idXpath = idExtracted.getIdentifierXpath();
                    }
                    if(idXpath == null)
                        throw new InvalidArgumentsException("idXpath is missing");

                    if(namespacePrefix == null && namespaceUri == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();
                        namespaces = idExtracted.getNamespaces();
                    }
                    else if(namespacePrefix == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        // update to new namespaceUri
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();

                        if(idExtracted.getNamespaces() != null && idExtracted.getNamespaces().size() > 0) {
                            for(String prefix : idExtracted.getNamespaces().keySet()) {
                                namespacePrefix = prefix;
                                break;
                            }
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else
                            throw new InvalidArgumentsException("namespacePrefix is missing");
                    }
                    else if(namespaceUri == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        // update to new namespacePrefix
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();

                        if(idExtracted.getNamespaces() != null && idExtracted.getNamespaces().size() > 0) {
                            for(String uri : idExtracted.getNamespaces().values()) {
                                namespaceUri = uri;
                                break;
                            }
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else
                            throw new InvalidArgumentsException("namespaceUri is missing");
                    }
                    else {
                        // use the new values
                        if(namespacePrefix != null && namespaceUri != null) {
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else if(namespacePrefix != null && namespaceUri == null) {
                            throw new InvalidArgumentsException("namespaceUri is missing");
                        }
                        else if(namespacePrefix == null && namespaceUri != null) {
                            throw new InvalidArgumentsException("namespacePrefix is missing");
                        }
                    }
                }
                else{
                    // IdGenerated - empty fields
                    idXpath = null;
                    namespaces = new TreeMap<String, String>();
                }
                if(recordXPath == null)
                    recordXPath = ((DirectoryImporterDataSource)dataSourceOld).getRecordXPath();
                if(url == null)
                    url = ((HttpFileRetrieveStrategy)((DirectoryImporterDataSource)dataSourceOld).getRetrieveStrategy()).getUrl();;
                if(marcFormat == null)
                    marcFormat = dataSourceOld.getMarcFormat();
                if(dataSourceOld.getMetadataTransformations().size() > 0)
                    transformations = dataSourceOld.getMetadataTransformations();
                if(dataSourceOld.getExternalRestServices().size() > 0)
                    externalServices = dataSourceOld.getExternalRestServices();
            }

            DataSource dataSource = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateDataSourceHttp(id, id,
                    description, schema, namespace, metadataFormat, isoFormat, charset,
                    recordIdPolicyClass, idXpath, namespaces, recordXPath, url,
                    transformations, externalServices, marcFormat, false);
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error updating Data Source HTTP.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error updating a Data Source HTTP. Data Provider was not found.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error updating Data Source HTTP. Invalid argument " + e.getMessage() + ".");
        } catch (IncompatibleInstanceException e) {
            createErrorMessage(out, MessageType.INCOMPATIBLE_TYPE, "Error updating Data Source HTTP. Data Source with id \"" + id + "\" was not a HTTP data source.");
        }
    }

    public void updateDataSourceFolder(OutputStream out, String id, String description, String schema, String namespace,
                                       String metadataFormat, String isoFormat, String charset, String recordIdPolicyClass,
                                       String idXpath, String namespacePrefix, String namespaceUri, String recordXPath,
                                       String sourcesDirPath, String marcFormat) throws IOException, DocumentException {
        if(metadataFormat != null && schema != null && namespace != null)
            saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        try {
            Map<String, String> namespaces = new TreeMap<String, String>();
            Map<String, MetadataTransformation> transformations = new HashMap<String, MetadataTransformation>();
            List<ExternalRestService> externalServices = new ArrayList<ExternalRestService>();

            DefaultDataSourceContainer dataSourceContainerOld = (DefaultDataSourceContainer)(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(id));if(dataSourceContainerOld != null){

                DataSource dataSourceOld = dataSourceContainerOld.getDataSource();
                DataProvider dataProviderParent = getDataProviderParent(dataSourceOld.getId());
                if(!(dataSourceOld instanceof DirectoryImporterDataSource)){
                    FileRetrieveStrategy retrieveStrategy = new FolderFileRetrieveStrategy();
                    RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                    if(recordIdPolicy != null){
                        CharacterEncoding characterEncoding = null;
                        FileExtractStrategy extractStrategy = DataSourceUtil.extractStrategyString(metadataFormat, isoFormat);
                        if(extractStrategy.getClass() == Iso2709FileExtractStrategy.class) {
                            if(charset.equals("")){
                                throw new InvalidArgumentsException("charset");
                            }
                            characterEncoding = CharacterEncoding.get(charset);
                        }
                        else if(extractStrategy.getClass() == MarcXchangeFileExtractStrategy.class) {
                        }
                        else if(extractStrategy.getClass() == SimpleFileExtractStrategy.class) {
                        }

                        DataSource newDataSource = new DirectoryImporterDataSource(dataProviderParent, id, description, schema, namespace, metadataFormat, extractStrategy,
                                retrieveStrategy, characterEncoding, sourcesDirPath, recordIdPolicy, new TreeMap<String, MetadataTransformation>(), recordXPath, new HashMap<String, String>());
                        newDataSource.setAccessPoints(dataSourceOld.getAccessPoints());
                        newDataSource.setStatus(dataSourceOld.getStatus());


                        setLastIngestDate(true, dataSourceOld, newDataSource);
                        newDataSource.setOldTasksList(dataSourceOld.getOldTasksList());
                        newDataSource.setTags(dataSourceOld.getTags());

                        dataProviderParent.getDataSourceContainers().remove(dataSourceOld.getId());
                        dataProviderParent.getDataSourceContainers().put(newDataSource.getId(), new DefaultDataSourceContainer(newDataSource));
                        dataSourceOld = newDataSource;
                    }
                }

                if(description == null)
                    description = dataSourceOld.getDescription();
                if(schema == null)
                    schema = dataSourceOld.getSchema();
                if(namespace == null)
                    namespace = dataSourceOld.getNamespace();
                if(metadataFormat == null)
                    metadataFormat = dataSourceOld.getMetadataFormat();

                if(isoFormat == null && metadataFormat.equals(MetadataFormat.ISO2709.toString())){
                    if(((DirectoryImporterDataSource)dataSourceOld).getExtractStrategy() instanceof Iso2709FileExtractStrategy) {
                        Iso2709FileExtractStrategy extractStrategy = (Iso2709FileExtractStrategy) ((DirectoryImporterDataSource)dataSourceOld).getExtractStrategy();
                        isoFormat = extractStrategy.getIsoImplementationClass().toString();
                    }
                    else
                        throw new InvalidArgumentsException("isoFormat is missing");
                }
                //
                if(charset == null && ((DirectoryImporterDataSource)dataSourceOld).getCharacterEncoding() != null)
                    charset = ((DirectoryImporterDataSource)dataSourceOld).getCharacterEncoding().toString();
                //

                if(recordIdPolicyClass == null){
                    if(dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy)
                        recordIdPolicyClass = IdExtractedRecordIdPolicy.class.getSimpleName();
                    else if(dataSourceOld.getRecordIdPolicy() instanceof IdGeneratedRecordIdPolicy)
                        recordIdPolicyClass = IdGeneratedRecordIdPolicy.class.getSimpleName();
                }

                if(recordIdPolicyClass.equals(IdExtractedRecordIdPolicy.class.getSimpleName())){
                    if(idXpath == null && dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();
                        idXpath = idExtracted.getIdentifierXpath();
                    }
                    if(idXpath == null)
                        throw new InvalidArgumentsException("idXpath is missing");

                    if(namespacePrefix == null && namespaceUri == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();
                        namespaces = idExtracted.getNamespaces();
                    }
                    else if(namespacePrefix == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        // update to new namespaceUri
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();

                        if(idExtracted.getNamespaces() != null && idExtracted.getNamespaces().size() > 0) {
                            for(String prefix : idExtracted.getNamespaces().keySet()) {
                                namespacePrefix = prefix;
                                break;
                            }
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else
                            throw new InvalidArgumentsException("namespacePrefix is missing");
                    }
                    else if(namespaceUri == null &&
                            dataSourceOld.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                        // update to new namespacePrefix
                        IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy) dataSourceOld.getRecordIdPolicy();

                        if(idExtracted.getNamespaces() != null && idExtracted.getNamespaces().size() > 0) {
                            for(String uri : idExtracted.getNamespaces().values()) {
                                namespaceUri = uri;
                                break;
                            }
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else
                            throw new InvalidArgumentsException("namespaceUri is missing");
                    }
                    else {
                        // use the new values
                        if(namespacePrefix != null && namespaceUri != null) {
                            namespaces.put(namespacePrefix, namespaceUri);
                        }
                        else if(namespacePrefix != null && namespaceUri == null) {
                            throw new InvalidArgumentsException("namespaceUri is missing");
                        }
                        else if(namespacePrefix == null && namespaceUri != null) {
                            throw new InvalidArgumentsException("namespacePrefix is missing");
                        }
                    }
                }
                else{
                    // IdGenerated - empty fields
                    idXpath = null;
                    namespaces = new TreeMap<String, String>();
                }

                if(recordXPath == null)
                    recordXPath = ((DirectoryImporterDataSource)dataSourceOld).getRecordXPath();
                if(sourcesDirPath == null)
                    sourcesDirPath = ((DirectoryImporterDataSource)dataSourceOld).getSourcesDirPath();
                if(marcFormat == null)
                    marcFormat = dataSourceOld.getMarcFormat();

                if(dataSourceOld.getMetadataTransformations().size() > 0)
                    transformations = dataSourceOld.getMetadataTransformations();

                if(dataSourceOld.getExternalRestServices().size() > 0)
                    externalServices = dataSourceOld.getExternalRestServices();
            }

            DataSource dataSource = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateDataSourceFolder(id, id,
                    description, schema, namespace, metadataFormat, isoFormat, charset,
                    recordIdPolicyClass, idXpath, namespaces, recordXPath, sourcesDirPath,
                    transformations, externalServices, marcFormat, false);
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error updating a Data Source Folder. Data Source with id \"" + id + "\" was not found.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating Data Source. Invalid argument " + e.getMessage() + ".");
        } catch (IncompatibleInstanceException e) {
            createErrorMessage(out, MessageType.INCOMPATIBLE_TYPE, "Error updating Data Source Folder. Data Source with id \"" + id + "\" was not a Folder data source.");
        }
    }

    public void deleteDataSource(OutputStream out, String id) throws DocumentException {
        try {
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().deleteDataSourceContainer(id);
            Element currentDataProviderElement = DocumentHelper.createElement("success");
            currentDataProviderElement.setText("Data Source with id \"" + id + "\" was successfully deleted.");
            RestUtils.writeRestResponse(out, currentDataProviderElement);
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error deleting Data Source with id \"" + id + "\".");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error deleting Data Source with id \"" + id + "\".");
        }
    }

    public void getDataSource(OutputStream out, String dataSourceId) throws DocumentException {
        try {
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId);

            if(dataSourceContainer != null){
                Element dataSourcesElement = dataSourceContainer.getDataSource().createElement();
                RestUtils.writeRestResponse(out, dataSourcesElement);
            }
            else{
                createErrorMessage(out, MessageType.NOT_FOUND, "Error retrieving Data Source. Data Source with id \"" + dataSourceId + "\" was not found.");
            }
        }
        catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error retrieving Data Source with id \"" + dataSourceId + "\".");
        }
    }

    public void countRecordsDataSource(OutputStream out, String dataSourceId) throws DocumentException, IOException, SQLException {
        DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId);
        if(dataSourceContainer != null){
            RecordCount dataSourceCount = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(dataSourceId,true);
            int totalMinusDeleted = dataSourceCount.getCount() - dataSourceCount.getDeleted();
            int deletedRecords = dataSourceCount.getDeleted();

            Element recordNumbersElement = DocumentHelper.createElement("recordNumbers");

            Element totalRecordsMinusDeletedElement = recordNumbersElement.addElement("recordCount");
            Element deletedRecordsElement = recordNumbersElement.addElement("deletedRecords");

            totalRecordsMinusDeletedElement.setText(String.valueOf(totalMinusDeleted));
            deletedRecordsElement.setText(String.valueOf(deletedRecords));

            RestUtils.writeRestResponse(out, recordNumbersElement);
        }
        else{
            createErrorMessage(out, MessageType.NOT_FOUND, "Error counting records. Data source with ID \"" + dataSourceId + "\" was not found.");
        }
    }

    public void lastIngestionDateDataSource(OutputStream out, String dataSourceId) throws DocumentException, IOException, SQLException {
        DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId);
        if(dataSourceContainer != null){
            String lastIngestionDate = dataSourceContainer.getDataSource().getSynchronizationDateString();

            Element successElement = DocumentHelper.createElement("lastIngestionDate");
            successElement.setText(String.valueOf(lastIngestionDate));
            RestUtils.writeRestResponse(out, successElement);
        }
        else{
            createErrorMessage(out, MessageType.NOT_FOUND, "Error retrieving last ingestion date. Data source with ID \"" + dataSourceId + "\" was not found.");
        }
    }

    public void startIngestDataSource(OutputStream out, String dataSourceId, boolean fullIngest) throws DocumentException, IOException {
        try {
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().startIngestDataSource(dataSourceId, fullIngest);
            Element successElement = DocumentHelper.createElement("success");
            successElement.setText("Harvest of Data Source with ID \"" + dataSourceId + "\" will start in a few seconds.");
            RestUtils.writeRestResponse(out, successElement);
        } catch (NoSuchMethodException e) {
            createErrorMessage(out, MessageType.OTHER, "Error starting Data Source ingestion.");
        } catch (ClassNotFoundException e) {
            createErrorMessage(out, MessageType.OTHER, "Error starting Data Source ingestion.");
        } catch (ParseException e) {
            createErrorMessage(out, MessageType.OTHER, "Error starting Data Source ingestion.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error starting the Data Source ingestion. ID \"" + dataSourceId + "\" was not found.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error starting the Data Source ingestion. ID \"" + dataSourceId + "\" is already harvesting.");
        }
    }

    public void stopIngestDataSource(OutputStream out, String dataSourceId) throws DocumentException, IOException, NoSuchMethodException {
        try {
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().stopIngestDataSource(dataSourceId, Task.Status.CANCELED);
            Element successElement = DocumentHelper.createElement("success");
            successElement.setText("Task for Data Source with ID \"" + dataSourceId + "\" was stopped successfully.");
            RestUtils.writeRestResponse(out, successElement);
        } catch (ObjectNotFoundException e) {
            if(e.getMessage().equals(dataSourceId)) {
                createErrorMessage(out, MessageType.OTHER, "Error stopping the Data Source task. No task is running for Data Source with ID \"" + dataSourceId + "\".");
            }
            else{
                createErrorMessage(out, MessageType.NOT_FOUND, "Error stopping the Data Source task. ID \"" + dataSourceId + "\" was not found.");
            }
        } catch (ClassNotFoundException e) {
            createErrorMessage(out, MessageType.OTHER, "Error stopping the Data Source task.");
        } catch (ParseException e) {
            createErrorMessage(out, MessageType.OTHER, "Error stopping the Data Source task.");
        }
    }


    public void scheduleIngestDataSource(OutputStream out, String dataSourceId, String firstRunDate, String firstRunHour,
                                         String frequency, String xmonths, String fullIngest) throws DocumentException, IOException {
        DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId);
        if(dataSourceContainer != null){
            DataSource dataSource = dataSourceContainer.getDataSource();
            String newTaskId = dataSource.getNewTaskId();
            ScheduledTask scheduledTask = new ScheduledTask();
            scheduledTask.setId(newTaskId);
            scheduledTask.setDate(firstRunDate);
            scheduledTask.setHour(Integer.valueOf(firstRunHour.split(":")[0]));
            scheduledTask.setMinute(Integer.valueOf(firstRunHour.split(":")[1]));

            if(frequency.equalsIgnoreCase("once")){
                scheduledTask.setFrequency(ScheduledTask.Frequency.ONCE);
            }
            else if(frequency.equalsIgnoreCase("daily")){
                scheduledTask.setFrequency(ScheduledTask.Frequency.DAILY);
            }
            else if(frequency.equalsIgnoreCase("weekly")){
                scheduledTask.setFrequency(ScheduledTask.Frequency.WEEKLY);
            }
            else if(frequency.equalsIgnoreCase("xmonthly")){
                scheduledTask.setFrequency(ScheduledTask.Frequency.XMONTHLY);
                scheduledTask.setXmonths(Integer.valueOf(xmonths));
            }

            scheduledTask.setTaskClass(IngestDataSource.class);
            // Parameter 0 -> taskId; Parameter 1 -> dataSourceId; Parameter 2 -> isFullIngest?
            String[] parameters = new String[]{newTaskId, dataSource.getId(), (Boolean.valueOf(fullIngest)).toString()};
            scheduledTask.setParameters(parameters);

            if(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().taskAlreadyExists(dataSource.getId(), DateUtil.date2String(scheduledTask.getFirstRun().getTime(), TimeUtil.LONG_DATE_FORMAT_NO_SECS), scheduledTask.getFrequency(), fullIngest)){
                createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error scheduling the Data Source ingestion. A task for this specific hour and data source ID \"" + dataSourceId + "\" is already scheduled.");
            }
            else{
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().saveTask(scheduledTask);

                Element successElement = DocumentHelper.createElement("success");
                successElement.setText("Ingest successfully scheduled for Data Source with ID \"" + dataSourceId + "\" .");
                RestUtils.writeRestResponse(out, successElement);
            }
        }
        else{
            createErrorMessage(out, MessageType.NOT_FOUND, "Error scheduling the Data Source ingestion. ID \"" + dataSourceId + "\" was not found.");
        }
    }

    public void scheduleListDataSource(OutputStream out, String dataSourceId) throws DocumentException, IOException {
        DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId);
        if(dataSourceContainer != null){
            Element scheduleTasksElement = DocumentHelper.createElement("scheduleTasks");

            for (ScheduledTask scheduledTask : ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getScheduledTasks()) {
                if(scheduledTask.getParameters()[1].equals(dataSourceId)){
                    Element scheduledTaskElement = scheduleTasksElement.addElement("task");
                    scheduledTaskElement.addAttribute("id", scheduledTask.getId());

                    Element timeElement = scheduledTaskElement.addElement("time");
                    timeElement.setText(DateUtil.date2String(scheduledTask.getFirstRun().getTime(), TimeUtil.LONG_DATE_FORMAT_NO_SECS));

                    Element frequencyElement = scheduledTaskElement.addElement("frequency");
                    frequencyElement.addAttribute("type", scheduledTask.getFrequency().toString());
                    if(scheduledTask.getFrequency().equals(ScheduledTask.Frequency.XMONTHLY)) {
                        frequencyElement.addAttribute("xmonthsPeriod", scheduledTask.getXmonths().toString());
                    }

                    scheduledTaskElement.addElement("fullIngest").addText(scheduledTask.getParameters()[2]);
                }
            }
            RestUtils.writeRestResponse(out, scheduleTasksElement);
        }
        else{
            createErrorMessage(out, MessageType.NOT_FOUND, "Error scheduling the Data Source ingestion. ID \"" + dataSourceId + "\" was not found.");
        }
    }

    public void harvestStatusDataSource(OutputStream out, String dataSourceId) throws DocumentException, IOException {
        DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId);
        if(dataSourceContainer != null){
            DataSource dataSource = dataSourceContainer.getDataSource();

            Element harvestingStatus = DocumentHelper.createElement("harvestingStatus");

            if(dataSource.getStatus() != null){
                if(dataSource.getStatusString().equalsIgnoreCase(DataSource.StatusDS.RUNNING.name())){
                    try {
                        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(dataSource.getId(), true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    Element statusMessage = DocumentHelper.createElement("status");
                    statusMessage.setText(dataSource.getStatusString());
                    harvestingStatus.add(statusMessage);

                    long timeLeft = dataSource.getTimeLeft();
                    if(timeLeft != -1){
                        Element timeLeftMessage = DocumentHelper.createElement("timeLeft");
                        timeLeftMessage.setText(String.valueOf(timeLeft));
                        harvestingStatus.add(timeLeftMessage);
                    }

                    float percentage = dataSource.getPercentage();
                    if(percentage >= 0){
                        Element percentageMessage = DocumentHelper.createElement("percentage");
                        percentageMessage.setText(String.valueOf(percentage));
                        harvestingStatus.add(percentageMessage);
                    }

                    float totalRecords = dataSource.getTotalRecords2Harvest();
                    if(totalRecords > 0){
                        Element recordsMessage = DocumentHelper.createElement("records");
                        try {
                            recordsMessage.setText(String.valueOf(dataSource.getIntNumberRecords() + "/" + dataSource.getNumberOfRecords2HarvestStr()));
                            harvestingStatus.add(recordsMessage);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    RestUtils.writeRestResponse(out, harvestingStatus);
                }
                else{
                    Element statusMessage = DocumentHelper.createElement("status");
                    statusMessage.setText(dataSource.getStatusString());
                    harvestingStatus.add(statusMessage);

                    if(dataSource.getStatusString().equalsIgnoreCase(DataSource.StatusDS.OK.name())){
                        Element recordsMessage = DocumentHelper.createElement("records");
                        try {
                            recordsMessage.setText(String.valueOf(dataSource.getIntNumberRecords()) + "/" + String.valueOf(dataSource.getIntNumberRecords()));
                            harvestingStatus.add(recordsMessage);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            else{
                Element statusMessage = DocumentHelper.createElement("status");
                statusMessage.setText("undefined");
                harvestingStatus.add(statusMessage);
            }
            RestUtils.writeRestResponse(out, harvestingStatus);
        }
        else{
            createErrorMessage(out, MessageType.NOT_FOUND, "Error scheduling the Data Source ingestion. ID \"" + dataSourceId + "\" was not found.");
        }
    }

    public void logDataSource(OutputStream out, String dataSourceId) throws DocumentException, IOException {
        DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId);
        if(dataSourceContainer != null){
            DataSource dataSource = dataSourceContainer.getDataSource();
            if(dataSource.getLogFilenames().size() > 0){
                Element logElement;
                File logFile = new File(dataSource.getLogsDir(), dataSource.getLogFilenames().get(0));
                try{
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(logFile);
                    logElement = document.getRootElement();
                }catch (DocumentException e){
                    ArrayList<String> logFileContent = FileUtilSecond.readFile(new File(dataSource.getLogsDir(), dataSource.getLogFilenames().get(0)));

                    logElement = DocumentHelper.createElement("log");

                    for (String line : logFileContent) {
                        logElement.addElement("line").addText(line);
                    }
                }

                RestUtils.writeRestResponse(out, logElement);
            }
            else{
                createErrorMessage(out, MessageType.OTHER, "Error showing log file for Data Source. There is no logs for Data Source with ID \"" + dataSourceId + "\".");
            }
        }
        else{
            createErrorMessage(out, MessageType.NOT_FOUND, "Error scheduling the Data Source ingestion. ID \"" + dataSourceId + "\" was not found.");
        }
    }

    public void harvestingDataSources(OutputStream out) throws DocumentException, IOException {
        Element runningTasksElement = DocumentHelper.createElement("runningTasks");
        for (Task task : ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getRunningTasks()){
            runningTasksElement.addElement("dataSource").addText(task.getParameters()[1]);
        }
        RestUtils.writeRestResponse(out, runningTasksElement);
    }

    public void startExportDataSource(OutputStream out, String dataSourceId, String recordsPerFile) throws DocumentException, IOException {
        startExportDataSource(out, dataSourceId, recordsPerFile, null);
    }

    public void startExportDataSource(OutputStream out, String dataSourceId, String recordsPerFile, String metadataExportFormat) throws DocumentException, IOException {
        try {
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().startExportDataSource(dataSourceId, recordsPerFile, metadataExportFormat);
            Element successElement = DocumentHelper.createElement("success");
            successElement.setText("Exportation of Data Source with ID \"" + dataSourceId + "\" will start in a few seconds.");
            RestUtils.writeRestResponse(out, successElement);
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error starting the Data Source exportation. ID \"" + dataSourceId + "\" is already exporting.");
        } catch (ClassNotFoundException e) {
            createErrorMessage(out, MessageType.OTHER, "Error starting Data Source exportation.");
        } catch (NoSuchMethodException e) {
            createErrorMessage(out, MessageType.OTHER, "Error starting Data Source exportation.");
        } catch (ParseException e) {
            createErrorMessage(out, MessageType.OTHER, "Error starting Data Source exportation.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error starting the Data Source exportation. ID \"" + dataSourceId + "\" was not found.");
        }
    }


    public void getRecord(OutputStream out, Urn recordUrn) throws IOException, DocumentException, SQLException {
        byte[] recordMetadata = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().getRecord(recordUrn).getMetadata();
        SAXReader reader = new SAXReader();
        Document recordDocument = reader.read(new ByteArrayInputStream(recordMetadata));

        Element recordResultElement = DocumentHelper.createElement("recordResult");
        recordResultElement.addAttribute("urn", recordUrn.toString());
        Node detachedRecordNode = recordDocument.getRootElement().detach();
        recordResultElement.add(detachedRecordNode);
        RestUtils.writeRestResponse(out, recordResultElement);
    }

    public void saveRecord(OutputStream out, String recordId, String dataSourceId, String recordString) throws IOException, DocumentException {
        DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId);
        if(dataSourceContainer != null){
            try {
                Element recordRoot = (Element) DocumentHelper.parseText(recordString).getRootElement().detach();
                DataSource dataSource = dataSourceContainer.getDataSource();
                RecordRepox recordRepox = dataSource.getRecordIdPolicy().createRecordRepox(recordRoot, recordId, false, false);
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().processRecord(dataSource, recordRepox,null);

                Element successElement = DocumentHelper.createElement("success");
                successElement.setText("Record with id " + recordRepox.getId() + " saved successfully");
                RestUtils.writeRestResponse(out, successElement);
            }
            catch(Exception e) {
                createErrorMessage(out, MessageType.OTHER, "Unable to save Record");
            }
        }
        else{
            createErrorMessage(out, MessageType.NOT_FOUND, "Unable to save or update record. Data source with ID \"" + dataSourceId + "\" was not found.");
        }
    }

    public void deleteRecord(OutputStream out, String recordId) throws IOException {
        try {
            Urn recordUrn = new Urn(recordId);
            boolean result = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().deleteRecord(recordUrn);

            if(result){
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().updateDeletedRecordsCount(recordUrn.getDataSourceId(), 1);

                // new report should be created for data set ("Record XPTO -recordId- was marked as deleted")
                DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(recordUrn.getDataSourceId());
                File logFile = dataSourceContainer.getDataSource().getLogFile(dataSourceContainer.getDataSource().getNewTaskId());

                LogUtil.endLogInfo(logFile, new Date(), new Date(), Task.Status.OK.name(), recordUrn.getDataSourceId(),
                        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(recordUrn.getDataSourceId()).getCount(),
                        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(recordUrn.getDataSourceId()).getDeleted());

                StringUtil.simpleLog("Record with id " + recordId + "  marked as deleted successfully from data set " + recordUrn.getDataSourceId() + ".",
                        this.getClass(), logFile);

                Element successElement = DocumentHelper.createElement("success");
                successElement.setText("Record with id " + recordId + " marked as deleted successfully");
                RestUtils.writeRestResponse(out, successElement);
            }
            else{
                createErrorMessage(out, MessageType.OTHER, "Record with id " + recordId + " was already marked as deleted.");
            }
        }
        catch(Exception e) {
            createErrorMessage(out, MessageType.OTHER, "Unable to permanently remove Record");
        }

    }

    public void eraseRecord(OutputStream out, String recordId) throws IOException {
        try {
            Urn recordUrn = new Urn(recordId);
            boolean marcAsDeleted = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().getRecord(recordUrn).isDeleted();

            boolean result = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().removeRecord(recordUrn);

            if(result){
                // the last parameter from next line - 0 or 1 (if record was marked as deleted)
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().updateEraseRecordsCount(recordUrn.getDataSourceId(), 1, marcAsDeleted ? 1 : 0);

                DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(recordUrn.getDataSourceId());
                File logFile = dataSourceContainer.getDataSource().getLogFile(dataSourceContainer.getDataSource().getNewTaskId());

                LogUtil.endLogInfo(logFile, new Date(), new Date(), Task.Status.OK.name(), recordUrn.getDataSourceId(),
                        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(recordUrn.getDataSourceId()).getCount(),
                        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(recordUrn.getDataSourceId()).getDeleted());

                StringUtil.simpleLog("Record with id " + recordId + "  permanently removed successfully from data set " + recordUrn.getDataSourceId() + ".",
                        this.getClass(), logFile);

                Element successElement = DocumentHelper.createElement("success");
                successElement.setText("Record with id " + recordId + " permanently removed successfully");
                RestUtils.writeRestResponse(out, successElement);
            }
            else{
                createErrorMessage(out, MessageType.OTHER, "Record with id " + recordId + " was not found.");
            }
        }
        catch(Exception e) {
            createErrorMessage(out, MessageType.OTHER, "Unable to permanently remove Record");
        }
    }

    public void createErrorMessage(OutputStream out, MessageType type, String cause) {
        Element errorMessage = DocumentHelper.createElement("error");
        errorMessage.addAttribute("type", type.name());
        errorMessage.addAttribute("requestURI", getRequestURI());
        errorMessage.addAttribute("cause", cause);

        try {
            RestUtils.writeRestResponse(out, errorMessage);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error. RestUtils.writeRestResponse(out, errorMessage): " + e.getMessage());
        }
    }

    public void createMapping(OutputStream out, String id,String description,String srcSchemaId,String srcSchemaVersion,
                              String destSchemaId,String destSchemaVersion,String isXslVersion2,
                              String xslFilename, InputStream xsdFile){
        try {
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("response");

            File xsltDir = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().getXsltDir();
            TransformationsFileManager.Response result = TransformationsFileManager.writeXslFile(xslFilename + ".xsl", xsltDir,xsdFile);

            if(result == TransformationsFileManager.Response.ERROR){
                createErrorMessage(out, MessageType.OTHER, "Error creating Mapping: id \"" + id + "\" error during file saving.");
                return;
            }
            else if(result == TransformationsFileManager.Response.FILE_TOO_BIG){
                createErrorMessage(out, MessageType.OTHER, "Error creating Mapping: id \"" + id + "\" xsd file is too big.");
                return;
            }
            else if(result == TransformationsFileManager.Response.XSL_ALREADY_EXISTS){
                createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating Mapping: id \"" + id + "\" xsd filename already exists.");
                return;
            }

            String srcXsdLink = ConfigSingleton.getRepoxContextUtil().getRepoxManager()
                    .getMetadataSchemaManager().getSchemaXSD(srcSchemaId,Double.valueOf(srcSchemaVersion));

            String destXsdLink = ConfigSingleton.getRepoxContextUtil().getRepoxManager()
                    .getMetadataSchemaManager().getSchemaXSD(destSchemaId,Double.valueOf(destSchemaVersion));

            MetadataTransformation mtdTransformation = new MetadataTransformation(id,
                    description,srcSchemaId,destSchemaId,xslFilename + ".xsl",
                    false,Boolean.valueOf(isXslVersion2),destXsdLink,"");
            mtdTransformation.setSourceSchema(srcXsdLink);
            mtdTransformation.setMDRCompliant(true);

            //If a file was uploaded, then erase its old files
            mtdTransformation.setDeleteOldFiles(true);

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    saveMetadataTransformation(mtdTransformation,"");

            root.addElement("status").setText("OK");
            RestUtils.writeRestResponse(out, root);
        } catch (SameStylesheetTransformationException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Mapping: id \"" + id + "\" stylesheet already exists.");
        }catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating Mapping: id \"" + id + "\" already exists.");
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Mapping: IO Error");
        } catch (DocumentException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Mapping: Document Exception");
        } catch (NumberFormatException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Mapping: Source and Destination Schema versions must be doubles");
        }
    }

    public void updateMapping(OutputStream out, String id,String description,String srcSchemaId,String srcSchemaVersion,
                              String destSchemaId,String destSchemaVersion,String isXslVersion2,
                              String xslFilename,InputStream xsdFile,String oldMtdTransId){
        try {
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("response");

            MetadataTransformation mtdTransformation = ConfigSingleton.getRepoxContextUtil().
                    getRepoxManager().getMetadataTransformationManager().loadMetadataTransformation(id);

            if(xsdFile != null && xslFilename != null){
                File xsltDir = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().getXsltDir();
                TransformationsFileManager.Response result = TransformationsFileManager.writeXslFile(xslFilename + ".xsl", xsltDir,xsdFile);
                if(result == TransformationsFileManager.Response.ERROR){
                    createErrorMessage(out, MessageType.OTHER, "Error creating Mapping: id \"" + id + "\" error during file saving.");
                    return;
                }
                else if(result == TransformationsFileManager.Response.FILE_TOO_BIG){
                    createErrorMessage(out, MessageType.OTHER, "Error creating Mapping: id \"" + id + "\" xsd file is too big.");
                    return;
                }
                else if(result == TransformationsFileManager.Response.XSL_ALREADY_EXISTS){
                    createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating Mapping: id \"" + id + "\" xsd filename already exists.");
                    return;
                }else{
                    mtdTransformation.setStylesheet(xslFilename + ".xsl");
                }
            }

            if(srcSchemaVersion != null){
                String srcXsdLink = ConfigSingleton.getRepoxContextUtil().getRepoxManager()
                        .getMetadataSchemaManager().getSchemaXSD(srcSchemaId,Double.valueOf(srcSchemaVersion));
                mtdTransformation.setSourceSchema(srcXsdLink);
            }

            if(destSchemaVersion != null){
                String destXsdLink = ConfigSingleton.getRepoxContextUtil().getRepoxManager()
                        .getMetadataSchemaManager().getSchemaXSD(destSchemaId,Double.valueOf(destSchemaVersion));
                mtdTransformation.setDestSchema(destXsdLink);
            }

            if(description != null)
                mtdTransformation.setDescription(description);
            if(srcSchemaId != null)
                mtdTransformation.setSourceFormat(srcSchemaId);
            if(destSchemaId != null)
                mtdTransformation.setDestinationFormat(destSchemaId);
            if(isXslVersion2 != null)
                mtdTransformation.setVersionTwo(Boolean.valueOf(isXslVersion2));
            mtdTransformation.setMDRCompliant(true);

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    saveMetadataTransformation(mtdTransformation,oldMtdTransId);

            root.addElement("status").setText("OK");
            RestUtils.writeRestResponse(out, root);
        } catch (SameStylesheetTransformationException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Mapping: id \"" + id + "\" stylesheet already exists.");
        }catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating Mapping: id \"" + id + "\" already exists.");
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Mapping: IO Error");
        } catch (DocumentException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Mapping: Document Exception");
        } catch (NumberFormatException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Mapping: Source and Destination Schema versions must be doubles");
        }
    }

    public void removeMapping(OutputStream out, String transformationID){
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("response");
        try {
            boolean result = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    deleteMetadataTransformation(transformationID);
            if(result)
                root.addElement("status").setText("OK");
            else
                root.addElement("status").setText("ID_NOT_FOUND");
            RestUtils.writeRestResponse(out, root);
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error removing Mapping: IO Error");
        } catch (DocumentException e) {
            createErrorMessage(out, MessageType.OTHER, "Error removing Mapping: Document Exception");
        }
    }

    private void saveNewMetadataSchema(String metadataFormat,String schema, String namespace,OutputStream out){
        boolean exists = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager().
                schemaExists(metadataFormat);

        if(!exists){
            List<MetadataSchemaVersion> metadataSchemaVersions = new ArrayList<MetadataSchemaVersion>();
            metadataSchemaVersions.add(new MetadataSchemaVersion(1.0,schema));
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager().
                    saveMetadataSchema(null,metadataFormat,null,namespace,null,null,metadataSchemaVersions,true);
        }
    }

    public void listMappings(OutputStream out){
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("mappingsList");
        try {
            Map<String,List<MetadataTransformation>> transformations =
                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().getMetadataTransformations();
            Iterator iterator=transformations.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry mapEntry=(Map.Entry)iterator.next();
                for (MetadataTransformation metadataTransformation : (List<MetadataTransformation>)mapEntry.getValue()) {
                    Element transformationElement = root.addElement("mapping");
                    transformationElement.addAttribute("id",metadataTransformation.getId());
                    transformationElement.addElement("description").setText(metadataTransformation.getDescription());
                    transformationElement.addElement("sourceFormat").setText(metadataTransformation.getSourceFormat());
                    transformationElement.addElement("destinationFormat").setText(metadataTransformation.getDestinationFormat());
                }
            }
            RestUtils.writeRestResponse(out, root);
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error listing Mappings: IO Error");
        }
    }

    public void createMetadataSchema(OutputStream out, String id, String oldSchemaId,String namespace,String designation,String description,
                                     String notes,Boolean oaiAvailable,List<MetadataSchemaVersion> metadataSchemaVersions){
        try {
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("response");

            MessageType messageType = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager().
                    saveMetadataSchema(designation,id,description,namespace,
                            notes,oldSchemaId,metadataSchemaVersions, oaiAvailable);

            if(messageType == MessageType.ALREADY_EXISTS){
                createErrorMessage(out, MessageType.OTHER, "Error creating Schema: id \"" + id + "\" schema already exists.");
                return;
            }

            root.addElement("status").setText("OK");
            RestUtils.writeRestResponse(out, root);
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Schema: IO Error");
        } catch (NumberFormatException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Schema: Schema versions must be doubles");
        }
    }

    public void updateMetadataSchema(OutputStream out, String id, String oldSchemaId,String namespace,String designation,String description,
                                     String notes,Boolean oaiAvailable,List<MetadataSchemaVersion> metadataSchemaVersions){
        try {
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("response");

            MessageType messageType = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager().
                    updateMetadataSchema(designation,id,description,namespace,
                            notes,oldSchemaId,metadataSchemaVersions, oaiAvailable);

            if(messageType == MessageType.ALREADY_EXISTS){
                createErrorMessage(out, MessageType.OTHER, "Error creating Schema: id \"" + id + "\" schema already exists.");
                return;
            }

            root.addElement("status").setText("OK");
            RestUtils.writeRestResponse(out, root);
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Schema: IO Error");
        } catch (NumberFormatException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Schema: Schema versions must be doubles");
        }
    }

    public void listMetadataSchemas(OutputStream out){
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("schemasList");
        try {
            List<MetadataSchema> metadataSchemas=ConfigSingleton.getRepoxContextUtil().getRepoxManager().
                    getMetadataSchemaManager().getMetadataSchemas();

            for (MetadataSchema metadataSchema : metadataSchemas) {
                Element schemaElement = root.addElement("schema");
                schemaElement.addAttribute("shortDesignation", metadataSchema.getShortDesignation());
                schemaElement.addElement("namespace").setText(metadataSchema.getNamespace());
                if(metadataSchema.getDesignation() != null)
                    schemaElement.addElement("designation").setText(metadataSchema.getDesignation());
                if(metadataSchema.getDescription() != null)
                    schemaElement.addElement("description").setText(metadataSchema.getDescription());
                if(metadataSchema.getNotes() != null)
                    schemaElement.addElement("notes").setText(metadataSchema.getNotes());
                schemaElement.addElement("oaiAvailable").setText(String.valueOf(metadataSchema.isOAIAvailable()).toUpperCase());

                Element versionsElement = schemaElement.addElement("versions");
                for(MetadataSchemaVersion metadataSchemaVersion : metadataSchema.getMetadataSchemaVersions()){
                    Element versionElement = versionsElement.addElement("version");
                    versionElement.addAttribute("version",String.valueOf(metadataSchemaVersion.getVersion()));
                    versionElement.addAttribute("xsdLink",metadataSchemaVersion.getXsdLink());
                }
            }
            RestUtils.writeRestResponse(out, root);
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error listing Mappings: IO Error");
        }
    }

    public void removeMetadataSchema(OutputStream out, String schemaID){
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("response");
        try {
            boolean result = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager().
                    deleteMetadataSchema(schemaID);
            if(result)
                root.addElement("status").setText("OK");
            else
                root.addElement("status").setText("ID_NOT_FOUND");
            RestUtils.writeRestResponse(out, root);
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error removing schema: IO Error");
        }
    }

    public void getStatistics(OutputStream out, String type){
        try {
            StatisticsManager manager =  ConfigSingleton.getRepoxContextUtil().getRepoxManager().getStatisticsManager();
            RepoxStatistics statistics = manager.generateStatistics(null);

            Document statisticsReport = manager.getStatisticsReport(statistics);
            statisticsReport.getRootElement().addAttribute("type",type);
            RestUtils.writeRestResponse(out, statisticsReport.getRootElement());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized DataProvider getDataProviderParent(String dataSourceId) throws IOException, DocumentException {
        for (DataProvider currentDataProvider : ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProviders()) {
            for (DataSourceContainer dataSourceContainer : currentDataProvider.getDataSourceContainers().values()) {
                if(dataSourceContainer.getDataSource().getId().equals(dataSourceId)){
                    return currentDataProvider;
                }
            }
        }
        return null;
    }

    private void setLastIngestDate(boolean useLastIngestDate, DataSource originalDataSet, DataSource targetDataSet){
        if(!useLastIngestDate && originalDataSet.getLastUpdate() != null){
            Date date = originalDataSet.getLastUpdate();
            date.setYear(70);
            targetDataSet.setLastUpdate(date);
        }else
            targetDataSet.setLastUpdate(originalDataSet.getLastUpdate());
    }
}
