/* DatasetsResource.java - created on Oct 30, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.dom4j.DocumentException;
import org.theeuropeanlibrary.repox.rest.pathOptions.DatasetOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.dataProvider.DefaultDataSourceContainer;
import pt.utl.ist.dataProvider.dataSource.FileRetrieveStrategy;
import pt.utl.ist.dataProvider.dataSource.IdExtractedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.IdGeneratedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.IdProvidedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.RecordIdPolicy;
import pt.utl.ist.externalServices.ExternalRestService;
import pt.utl.ist.ftp.FtpFileRetrieveStrategy;
import pt.utl.ist.http.HttpFileRetrieveStrategy;
import pt.utl.ist.marc.CharacterEncoding;
import pt.utl.ist.marc.DirectoryImporterDataSource;
import pt.utl.ist.marc.FolderFileRetrieveStrategy;
import pt.utl.ist.marc.iso2709.shared.Iso2709Variant;
import pt.utl.ist.metadataTransformation.MetadataFormat;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.oai.OaiDataSource;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Datasets context path handling.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 30, 2014
 */
@Path("/" + DatasetOptionListContainer.DATASETS)
@Api(value = "/" + DatasetOptionListContainer.DATASETS, description = "Rest api for datasets")
public class DatasetsResource {
    @Context
    UriInfo                   uriInfo;

    public DefaultDataManager dataManager;

    /**
     * Initialize fields before serving.
     */
    public DatasetsResource() {
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
        dataManager = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager());
    }

    /**
     * Creates a new instance by providing the DataManager. (For Tests)
     * @param dataManager
     */
    public DatasetsResource(DefaultDataManager dataManager) {
        super();
        this.dataManager = dataManager;
    }

    /**
     * Retrieve all the available options for Datasets.
     * Relative path : /datasets
     * @return the list of the options available wrapped in a container
     */
    @OPTIONS
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get options over datasets conext.", httpMethod = "OPTIONS", response = DatasetOptionListContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)") })
    public DatasetOptionListContainer getOptions() {
        DatasetOptionListContainer datasetOptionListContainer = new DatasetOptionListContainer(uriInfo.getBaseUri());
        return datasetOptionListContainer;
    }
    
    /**
     * Retrieve all the available options for Datasets(For browser visibility).
     * Relative path : /datasets
     * @return the list of the options available wrapped in a container
     */
    @GET
    @Path("/" + DatasetOptionListContainer.OPTIONS)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get options over datasets conext.", httpMethod = "GET", response = DatasetOptionListContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)") })
    public DatasetOptionListContainer getGETOptions() {
        return getOptions();
    }

    /**
     * Retrieve the dataset with the provided id.
     * Relative path : /datasets/{datasetId} 
     * @param datasetId 
     * @return Provider information
     * @throws DoesNotExistException 
     * @throws IOException 
     * @throws DocumentException 
     */
    @GET
    @Path("/" + DatasetOptionListContainer.DATASETID)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get specific dataset.", httpMethod = "GET", response = DataSourceContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing an Dataset)"),
            @ApiResponse(code = 404, message = "DoesNotExistException") })
    public DataSourceContainer getDataset(@ApiParam(value = "Id of dataset", required = true) @PathParam("datasetId") String datasetId) throws DoesNotExistException, DocumentException,
            IOException {

        DataSourceContainer datasourceContainer = null;
        datasourceContainer = dataManager.getDataSourceContainer(datasetId);
        if (datasourceContainer == null)
            throw new DoesNotExistException("Dataset with id " + datasetId + " does NOT exist!");

        return datasourceContainer;
    }

    /**
     * Create an dataset provided in the body of the post call.
     * Relative path : /datasets
     * @param providerId 
     * @param dataSourceContainer 
     * @return OK or Error Message
     * @throws MissingArgumentsException 
     * @throws AlreadyExistsException 
     * @throws InvalidArgumentsException 
     * @throws DoesNotExistException 
     * @throws InternalServerErrorException 
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Create a dataset.", httpMethod = "POST", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created (Response containing a String message)"),
            @ApiResponse(code = 400, message = "InvalidArgumentsException"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 406, message = "MissingArgumentsException"),
            @ApiResponse(code = 409, message = "AlreadyExistsException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException") })
    public Response createDataset(@ApiParam(value = "ProviderId", required = true) @QueryParam("providerId") String providerId,
            @ApiParam(value = "Dataset data", required = true) DataSourceContainer dataSourceContainer) throws AlreadyExistsException, MissingArgumentsException, InvalidArgumentsException,
            DoesNotExistException, InternalServerErrorException {

        if (providerId == null || providerId.equals(""))
            throw new MissingArgumentsException("Missing argument providerId!");

        if (dataSourceContainer instanceof DefaultDataSourceContainer)
        {
            DefaultDataSourceContainer defaultDataSourceContainer = (DefaultDataSourceContainer)dataSourceContainer;
            DataSource dataSource = defaultDataSourceContainer.getDataSource();
            String name = defaultDataSourceContainer.getName();
            String nameCode = defaultDataSourceContainer.getNameCode();

            String id = dataSource.getId();
            String description = dataSource.getDescription();
            String exportPath = dataSource.getExportDir();
            String schema = dataSource.getSchema();
            String namespace = dataSource.getNamespace();
            String metadataFormat = dataSource.getMetadataFormat();
            String marcFormat = dataSource.getMarcFormat();
            boolean isSample = dataSource.isSample();
            Map<String, MetadataTransformation> metadataTransformations = null;
            List<ExternalRestService> externalRestServices = null;

            if (schema == null || schema.equals(""))
                throw new MissingArgumentsException("Missing value: " + "Dataset schema must not be empty");
            else if (namespace == null || namespace.equals(""))
                throw new MissingArgumentsException("Missing value: " + "Dataset namespace must not be empty");
            else if (metadataFormat == null || metadataFormat.equals(""))
                throw new MissingArgumentsException("Missing value: " + "Dataset metadataFormat must not be empty");

            if (metadataFormat.equals(MetadataFormat.MarcXchange.toString()))
            {
                if (marcFormat == null || marcFormat.isEmpty())
                    throw new MissingArgumentsException("Invalid value: " + "Dataset marcFormat must not be empty");
            }

            if (dataSource instanceof OaiDataSource)
            {
                OaiDataSource oaiDataSource = (OaiDataSource)dataSource;
                String oaiSourceURL = oaiDataSource.getOaiSourceURL();
                String oaiSet = oaiDataSource.getOaiSet();

                if (oaiSourceURL == null || oaiSourceURL.isEmpty())
                    throw new MissingArgumentsException("Missing value: " + "Dataset oaiSourceURL must not be empty");
                else if (oaiSet == null || oaiSet.equals(""))
                    throw new MissingArgumentsException("Missing value: " + "Dataset oaiSet must not be empty");

                try {
                    DataSource createdDataSourceOai = dataManager.createDataSourceOai(providerId, id, description, nameCode, name, exportPath, schema, namespace, metadataFormat,
                            oaiSourceURL, oaiSet, metadataTransformations,
                            externalRestServices, marcFormat);
                    dataManager.setDataSetSampleState(isSample, createdDataSourceOai);
                } catch (InvalidArgumentsException e) {
                    throw new InvalidArgumentsException("Invalid argument: " + e.getMessage());
                } catch (ObjectNotFoundException e) {
                    throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
                } catch (AlreadyExistsException e) {
                    throw new AlreadyExistsException("Already exists: " + e.getMessage(), e.getDatasetId());
                } catch (SQLException | DocumentException | IOException e) {
                    throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
                }
            }
            else if (dataSource instanceof DirectoryImporterDataSource)
            {
                DirectoryImporterDataSource directoryImporterDataSource = (DirectoryImporterDataSource)dataSource;
                String sourcesDirPath = directoryImporterDataSource.getSourcesDirPath();
                String recordXPath = directoryImporterDataSource.getRecordXPath();
                CharacterEncoding characterEncoding = directoryImporterDataSource.getCharacterEncoding();
                FileRetrieveStrategy retrieveStrategy = directoryImporterDataSource.getRetrieveStrategy();
                String characterEncodingString = "";
                if (characterEncoding != null)
                    characterEncodingString = characterEncoding.toString();
                Iso2709Variant isoVariant = directoryImporterDataSource.getIsoVariant();
                String isoVariantString = "";
                if (isoVariant != null)
                    isoVariantString = isoVariant.getIsoVariant();
                RecordIdPolicy recordIdPolicy = directoryImporterDataSource.getRecordIdPolicy();
                String recordIdPolicyString = null;
                String idXpath = null;
                if (recordIdPolicy == null)
                    throw new MissingArgumentsException("Missing value: " + "Dataset recordIdPolicy must not be empty");

                Map<String, String> namespaces = null;
                if (recordIdPolicy instanceof IdGeneratedRecordIdPolicy)
                    recordIdPolicyString = IdGeneratedRecordIdPolicy.IDGENERATED;
                else if (recordIdPolicy instanceof IdProvidedRecordIdPolicy)
                    recordIdPolicyString = IdProvidedRecordIdPolicy.IDPROVIDED;
                else if (recordIdPolicy instanceof IdExtractedRecordIdPolicy)
                {
                    recordIdPolicyString = IdExtractedRecordIdPolicy.IDEXTRACTED;
                    idXpath = ((IdExtractedRecordIdPolicy)recordIdPolicy).getIdentifierXpath();

                    if (idXpath == null || idXpath.isEmpty())
                        throw new MissingArgumentsException("Missing value: " + "Dataset identifierXpath must not be empty");
                    namespaces = ((IdExtractedRecordIdPolicy)recordIdPolicy).getNamespaces();
                }

                if (metadataFormat.equals(MetadataFormat.ISO2709.toString())) {
                    if (isoVariant == null)
                        throw new MissingArgumentsException("Missing value: " + "Dataset isoVariant must not be empty");
                    else if (characterEncoding == null)
                        throw new MissingArgumentsException("Missing value: " + "Dataset characterEncoding must not be empty");
                }

                if (retrieveStrategy instanceof FolderFileRetrieveStrategy)
                {
                    if (sourcesDirPath == null || sourcesDirPath.isEmpty())
                        throw new MissingArgumentsException("Invalid value: " + "Dataset sourcesDirPath must not be empty");

                    try {
                        DataSource createdDataSourceFolder = dataManager.createDataSourceFolder(providerId, id, description, nameCode, name, exportPath, schema, namespace, metadataFormat, isoVariantString, characterEncodingString,
                                recordIdPolicyString, idXpath, namespaces, recordXPath, sourcesDirPath, metadataTransformations, externalRestServices, marcFormat);
                        dataManager.setDataSetSampleState(isSample, createdDataSourceFolder);
                    } catch (InvalidArgumentsException e) {
                        throw new InvalidArgumentsException("Invalid argument: " + e.getMessage());
                    } catch (ObjectNotFoundException e) {
                        throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
                    } catch (AlreadyExistsException e) {
                        throw new AlreadyExistsException("Already exists: " + e.getMessage(), e.getDatasetId());
                    } catch (SQLException | DocumentException | IOException e) {
                        throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
                    }
                }
                else if (retrieveStrategy instanceof FtpFileRetrieveStrategy)
                {
                    FtpFileRetrieveStrategy ftpRetrieveStrategy = (FtpFileRetrieveStrategy)retrieveStrategy;
                    String server = ftpRetrieveStrategy.getServer();
                    String userName = ftpRetrieveStrategy.getUser();
                    String password = ftpRetrieveStrategy.getPassword();
                    String ftpPath = ftpRetrieveStrategy.getFtpPath();

                    if (server == null || server.isEmpty())
                        throw new MissingArgumentsException("Missing value: " + "FTP server must not be empty");

                    try {
                        DataSource createdDataSourceFtp = dataManager.createDataSourceFtp(providerId, id, description, nameCode, name, exportPath, schema, namespace, metadataFormat, isoVariantString, characterEncodingString,
                                recordIdPolicyString, idXpath, namespaces, recordXPath, server, userName, password, ftpPath, metadataTransformations, externalRestServices, marcFormat);
                        dataManager.setDataSetSampleState(isSample, createdDataSourceFtp);
                    } catch (InvalidArgumentsException e) {
                        throw new InvalidArgumentsException("Invalid argument: " + e.getMessage());
                    } catch (ObjectNotFoundException e) {
                        throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
                    } catch (AlreadyExistsException e) {
                        throw new AlreadyExistsException("Already exists: " + e.getMessage(), e.getDatasetId());
                    } catch (SQLException | DocumentException | IOException e) {
                        throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
                    }
                }
                else if (retrieveStrategy instanceof HttpFileRetrieveStrategy)
                {
                    HttpFileRetrieveStrategy httpFileRetrieveStrategy = (HttpFileRetrieveStrategy)retrieveStrategy;
                    String httpUrl = httpFileRetrieveStrategy.getUrl();

                    if (httpUrl == null || httpUrl.isEmpty())
                        throw new MissingArgumentsException("Missing value: " + "HTTP Url must not be empty");

                    try {
                        DataSource createdDataSourceHttp = dataManager.createDataSourceHttp(providerId, id, description, nameCode, name, exportPath, schema, namespace, metadataFormat, isoVariantString, characterEncodingString,
                                recordIdPolicyString, idXpath, namespaces, recordXPath, httpUrl, metadataTransformations, externalRestServices, marcFormat);
                        dataManager.setDataSetSampleState(isSample, createdDataSourceHttp);
                    } catch (InvalidArgumentsException e) {
                        throw new InvalidArgumentsException("Invalid argument: " + e.getMessage());
                    } catch (ObjectNotFoundException e) {
                        throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
                    } catch (AlreadyExistsException e) {
                        throw new AlreadyExistsException("Already exists: " + e.getMessage(), e.getDatasetId());
                    } catch (SQLException | DocumentException | IOException e) {
                        throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
                    }
                }
            }
            return Response.created(null).entity(new Result("DataSet with id = <" + id + "> and name = " + name + " created successfully")).build();
        }
        return Response.status(500).entity(new Result("Invalid dataSourceContainer instance in body!")).build();
    }

    /**
     * Copy an dataset to another dataset with a newDatasetId
     * Relative path : /datasets/{datasetId}
     * @param datasetId 
     * @param newDatasetId 
     * 
     * @return OK or Error Message
     * @throws DoesNotExistException 
     * @throws InternalServerErrorException 
     * @throws InvalidArgumentsException 
     * @throws MissingArgumentsException 
     * @throws AlreadyExistsException 
     */
    @POST
    @Path("/" + DatasetOptionListContainer.DATASETID)
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Copy a dataset.", httpMethod = "POST", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created (Response containing a String message)"),
            @ApiResponse(code = 400, message = "InvalidArgumentsException"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 406, message = "MissingArgumentsException"),
            @ApiResponse(code = 409, message = "AlreadyExistsException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException") })
    public Response copyDataset(@ApiParam(value = "Id of dataset", required = true) @PathParam("datasetId") String datasetId,
            @ApiParam(value = "newDatasetId", required = true) @QueryParam("newDatasetId") String newDatasetId) throws DoesNotExistException, InternalServerErrorException, AlreadyExistsException,
            MissingArgumentsException, InvalidArgumentsException {

        DefaultDataSourceContainer oldDataSourceContainer = null;
        try {
            oldDataSourceContainer = (DefaultDataSourceContainer)dataManager.getDataSourceContainer(datasetId);
            if (oldDataSourceContainer == null)
                throw new DoesNotExistException("Dataset with id " + datasetId + " does NOT exist!");

            DataSource oldDataSource = oldDataSourceContainer.getDataSource();
            if (oldDataSource == null)
                throw new DoesNotExistException("Dataset with id " + datasetId + " does NOT exist!");
        } catch (DocumentException | IOException e) {
            throw new InternalServerErrorException("Error in server : " + e.getMessage());
        }

        try {
            DefaultDataSourceContainer newDataSourceContainer = null;
            newDataSourceContainer = (DefaultDataSourceContainer)dataManager.getDataSourceContainer(newDatasetId);
            if (newDataSourceContainer != null)
                throw new AlreadyExistsException("Dataset with newId " + newDatasetId + " already exist!");

        } catch (DocumentException | IOException e) {
            throw new InternalServerErrorException("Error in server : " + e.getMessage());
        }

        DataProvider dataProviderParent = dataManager.getDataProviderParent(datasetId);
        DataSource oldDataSource = oldDataSourceContainer.getDataSource();
        
        DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(oldDataSourceContainer);
        if(oldDataSource instanceof OaiDataSource)
        {
            OaiDataSource oldOaiDataSource = (OaiDataSource)oldDataSource;
            OaiDataSource oaiDataSource = new OaiDataSource(oldOaiDataSource);
            oaiDataSource.setId(newDatasetId);
            dataSourceContainer.setDataSource(oaiDataSource);
        }
        else if (oldDataSource instanceof DirectoryImporterDataSource)
        {
            DirectoryImporterDataSource oldDirectoryImporterDataSource = (DirectoryImporterDataSource)oldDataSource;
            DirectoryImporterDataSource directoryImporterDataSource = new DirectoryImporterDataSource(oldDirectoryImporterDataSource);
            directoryImporterDataSource.setId(newDatasetId);
            dataSourceContainer.setDataSource(directoryImporterDataSource);
        }
        else
            return Response.status(500).entity(new Result("Invalid dataSourceContainer instance in body!")).build();
        
        return createDataset(dataProviderParent.getId(), dataSourceContainer);
    }

    /**
     * Delete a dataset by specifying the Id.
     * Relative path : /datasets/{datasetId} 
     * @param datasetId 
     * @return OK or Error Message
     * @throws DoesNotExistException 
     */
    @DELETE
    @Path("/" + DatasetOptionListContainer.DATASETID)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Delete a dataset.", httpMethod = "DELETE", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a String message)"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException") })
    public Response deleteDataset(@ApiParam(value = "Id of dataset", required = true) @PathParam("datasetId") String datasetId) throws DoesNotExistException {

        try {
            dataManager.deleteDataSourceContainer(datasetId);
        } catch (IOException e) {
            throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
        } catch (ObjectNotFoundException e) {
            throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
        }

        return Response.status(200).entity(new Result("Dataset with id " + datasetId + " deleted!")).build();
    }

    /**
     * Update a dataset by specifying the Id.
     * Relative path : /datasets/{datasetId} 
     * @param datasetId 
     * @param dataSourceContainer 
     * @return OK or Error Message
     * @throws DoesNotExistException 
     * @throws InvalidArgumentsException 
     * @throws MissingArgumentsException  
     * @throws AlreadyExistsException 
     * @throws InternalServerErrorException 
     */
    @PUT
    @Path("/" + DatasetOptionListContainer.DATASETID)
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Update a dataset.", httpMethod = "PUT", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a String message)"),
            @ApiResponse(code = 400, message = "InvalidArgumentsException"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 406, message = "MissingArgumentsException"),
            @ApiResponse(code = 409, message = "AlreadyExistsException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response updateDataset(@ApiParam(value = "Id of dataset", required = true) @PathParam("datasetId") String datasetId,
            @ApiParam(value = "Dataset data", required = true) DataSourceContainer dataSourceContainer) throws MissingArgumentsException, DoesNotExistException,
            InvalidArgumentsException, AlreadyExistsException, InternalServerErrorException {

        if (dataSourceContainer instanceof DefaultDataSourceContainer)
        {
            DefaultDataSourceContainer defaultDataSourceContainer = (DefaultDataSourceContainer)dataSourceContainer;
            DataSource dataSource = defaultDataSourceContainer.getDataSource();
            String name = defaultDataSourceContainer.getName();
            String nameCode = defaultDataSourceContainer.getNameCode();

            String newId = dataSource.getId();
            String description = dataSource.getDescription();
            String exportPath = dataSource.getExportDir();
            String schema = dataSource.getSchema();
            String namespace = dataSource.getNamespace();
            String metadataFormat = dataSource.getMetadataFormat();
            String marcFormat = dataSource.getMarcFormat();
            boolean isSample = dataSource.isSample();
            Map<String, MetadataTransformation> metadataTransformations = null;
            List<ExternalRestService> externalRestServices = null;

            if (schema == null || schema.equals(""))
                throw new MissingArgumentsException("Missing value: " + "Dataset schema must not be empty");
            else if (namespace == null || namespace.equals(""))
                throw new MissingArgumentsException("Missing value: " + "Dataset namespace must not be empty");
            else if (metadataFormat == null || metadataFormat.equals(""))
                throw new MissingArgumentsException("Missing value: " + "Dataset metadataFormat must not be empty");

            if (metadataFormat.equals(MetadataFormat.MarcXchange.toString()))
            {
                if (marcFormat == null || marcFormat.isEmpty())
                    throw new MissingArgumentsException("Invalid value: " + "Dataset marcFormat must not be empty");
            }

            //Retrieve and set the values that are not provided through the REST calls
            DefaultDataSourceContainer oldDataSourceContainer = null;
            try {
                oldDataSourceContainer = (DefaultDataSourceContainer)dataManager.getDataSourceContainer(datasetId);
                if (oldDataSourceContainer == null)
                    throw new DoesNotExistException("Dataset with id " + datasetId + " does NOT exist!");

                DataSource oldDataSource = oldDataSourceContainer.getDataSource();
                if (oldDataSource == null)
                    throw new DoesNotExistException("Dataset with id " + datasetId + " does NOT exist!");

                metadataTransformations = oldDataSource.getMetadataTransformations();
                externalRestServices = oldDataSource.getExternalRestServices();
            } catch (DocumentException | IOException e) {
                throw new InternalServerErrorException("Error in server : " + e.getMessage());
            }

            if (dataSource instanceof OaiDataSource)
            {
                OaiDataSource oaiDataSource = (OaiDataSource)dataSource;
                String oaiSourceURL = oaiDataSource.getOaiSourceURL();
                String oaiSet = oaiDataSource.getOaiSet();

                if (oaiSourceURL == null || oaiSourceURL.isEmpty())
                    throw new MissingArgumentsException("Missing value: " + "Dataset oaiSourceURL must not be empty");
                else if (oaiSet == null || oaiSet.equals(""))
                    throw new MissingArgumentsException("Missing value: " + "Dataset oaiSet must not be empty");

                try {
                    DataSource updatedDataSourceOai = dataManager.updateDataSourceOai(datasetId, newId, description, nameCode, name, exportPath, schema, namespace, metadataFormat, oaiSourceURL, oaiSet, metadataTransformations,
                            externalRestServices, marcFormat, true);
                    dataManager.setDataSetSampleState(isSample, updatedDataSourceOai);
                } catch (InvalidArgumentsException e) {
                    throw new InvalidArgumentsException("Invalid argument: " + e.getMessage());
                } catch (ObjectNotFoundException e) {
                    throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
                } catch (AlreadyExistsException e) {
                    throw new AlreadyExistsException("Already exists: " + e.getMessage());
                } catch (DocumentException | IOException e) {
                    throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
                }
            }
            else if (dataSource instanceof DirectoryImporterDataSource)
            {
                DirectoryImporterDataSource directoryImporterDataSource = (DirectoryImporterDataSource)dataSource;
                String sourcesDirPath = directoryImporterDataSource.getSourcesDirPath();
                String recordXPath = directoryImporterDataSource.getRecordXPath();
                CharacterEncoding characterEncoding = directoryImporterDataSource.getCharacterEncoding();
                FileRetrieveStrategy retrieveStrategy = directoryImporterDataSource.getRetrieveStrategy();
                String characterEncodingString = "";
                if (characterEncoding != null)
                    characterEncodingString = characterEncoding.toString();
                Iso2709Variant isoVariant = directoryImporterDataSource.getIsoVariant();
                String isoVariantString = "";
                if (isoVariant != null)
                    isoVariantString = isoVariant.getIsoVariant();
                RecordIdPolicy recordIdPolicy = directoryImporterDataSource.getRecordIdPolicy();
                String recordIdPolicyString = null;
                String idXpath = null;
                if (recordIdPolicy == null)
                    throw new MissingArgumentsException("Missing value: " + "Dataset recordIdPolicy must not be empty");

                Map<String, String> namespaces = null;
                if (recordIdPolicy instanceof IdGeneratedRecordIdPolicy)
                    recordIdPolicyString = IdGeneratedRecordIdPolicy.IDGENERATED;
                else if (recordIdPolicy instanceof IdProvidedRecordIdPolicy)
                    recordIdPolicyString = IdProvidedRecordIdPolicy.IDPROVIDED;
                else if (recordIdPolicy instanceof IdExtractedRecordIdPolicy)
                {
                    recordIdPolicyString = IdExtractedRecordIdPolicy.IDEXTRACTED;
                    idXpath = ((IdExtractedRecordIdPolicy)recordIdPolicy).getIdentifierXpath();

                    if (idXpath == null || idXpath.isEmpty())
                        throw new MissingArgumentsException("Missing value: " + "Dataset identifierXpath must not be empty");
                    namespaces = ((IdExtractedRecordIdPolicy)recordIdPolicy).getNamespaces();
                }

                if (metadataFormat.equals(MetadataFormat.ISO2709.toString())) {
                    if (isoVariant == null)
                        throw new MissingArgumentsException("Missing value: " + "Dataset isoVariant must not be empty");
                    else if (characterEncoding == null)
                        throw new MissingArgumentsException("Missing value: " + "Dataset characterEncoding must not be empty");
                }

                if (retrieveStrategy instanceof FolderFileRetrieveStrategy)
                {
                    if (sourcesDirPath == null || sourcesDirPath.isEmpty())
                        throw new MissingArgumentsException("Invalid value: " + "Dataset sourcesDirPath must not be empty");

                    try {
                        DataSource updatedDataSourceFolder = dataManager.updateDataSourceFolder(datasetId, newId, description, nameCode, name, exportPath, schema, namespace, metadataFormat, isoVariantString, characterEncodingString,
                                recordIdPolicyString, idXpath, namespaces, recordXPath, sourcesDirPath, metadataTransformations, externalRestServices, marcFormat, true);
                        dataManager.setDataSetSampleState(isSample, updatedDataSourceFolder);
                    } catch (InvalidArgumentsException e) {
                        throw new InvalidArgumentsException("Invalid argument: " + e.getMessage());
                    } catch (ObjectNotFoundException e) {
                        throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
                    } catch (AlreadyExistsException e) {
                        throw new AlreadyExistsException("Already exists: " + e.getMessage());
                    } catch (DocumentException | IOException e) {
                        throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
                    }
                }
                else if (retrieveStrategy instanceof FtpFileRetrieveStrategy)
                {
                    FtpFileRetrieveStrategy ftpRetrieveStrategy = (FtpFileRetrieveStrategy)retrieveStrategy;
                    String server = ftpRetrieveStrategy.getServer();
                    String userName = ftpRetrieveStrategy.getUser();
                    String password = ftpRetrieveStrategy.getPassword();
                    String ftpPath = ftpRetrieveStrategy.getFtpPath();

                    if (server == null || server.isEmpty())
                        throw new MissingArgumentsException("Missing value: " + "FTP server must not be empty");

                    try {
                        DataSource updatedDataSourceFtp = dataManager.updateDataSourceFtp(datasetId, newId, description, nameCode, name, exportPath, schema, namespace, metadataFormat, isoVariantString, characterEncodingString,
                                recordIdPolicyString, idXpath, namespaces, recordXPath, server, userName, password, ftpPath, metadataTransformations, externalRestServices, marcFormat, true);
                        dataManager.setDataSetSampleState(isSample, updatedDataSourceFtp);
                    } catch (InvalidArgumentsException e) {
                        throw new InvalidArgumentsException("Invalid argument: " + e.getMessage());
                    } catch (ObjectNotFoundException e) {
                        throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
                    } catch (AlreadyExistsException e) {
                        throw new AlreadyExistsException("Already exists: " + e.getMessage());
                    } catch (DocumentException | IOException e) {
                        throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
                    }
                }
                else if (retrieveStrategy instanceof HttpFileRetrieveStrategy)
                {
                    HttpFileRetrieveStrategy httpFileRetrieveStrategy = (HttpFileRetrieveStrategy)retrieveStrategy;
                    String httpUrl = httpFileRetrieveStrategy.getUrl();

                    if (httpUrl == null || httpUrl.isEmpty())
                        throw new MissingArgumentsException("Missing value: " + "HTTP Url must not be empty");

                    try {
                        DataSource updatedDataSourceHttp = dataManager.updateDataSourceHttp(datasetId, newId, description, nameCode, name, exportPath, schema, namespace, metadataFormat, isoVariantString, characterEncodingString,
                                recordIdPolicyString, idXpath, namespaces, recordXPath, httpUrl, metadataTransformations, externalRestServices, marcFormat, true);
                        dataManager.setDataSetSampleState(isSample, updatedDataSourceHttp);
                    } catch (InvalidArgumentsException e) {
                        throw new InvalidArgumentsException("Invalid argument: " + e.getMessage());
                    } catch (ObjectNotFoundException e) {
                        throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
                    } catch (AlreadyExistsException e) {
                        throw new AlreadyExistsException("Already exists: " + e.getMessage());
                    } catch (DocumentException | IOException e) {
                        throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
                    }
                }
                else
                    return Response.status(500).entity(new Result("Invalid retrieve strategy!")).build();
            }
            else
                return Response.status(500).entity(new Result("Invalid dataSource instance in body!")).build();

            if (newId != null && !newId.isEmpty() && !datasetId.equals(newId))
                return Response.status(200).entity(new Result("Dataset with id " + datasetId + " updated and has now id : " + newId + "!")).build();
            else
                return Response.status(200).entity(new Result("Dataset with id " + datasetId + " updated!")).build();
        }
        return Response.status(500).entity(new Result("Invalid dataSourceContainer instance in body!")).build();
    }

    /**
     * Get a list of datasets in the specified range.
     * Offset not allowed negative. If number is negative then it returns all the items from offset until the total number of items.
     * Relative path : /datasets
     * @param providerId 
     * @param offset 
     * @param number 
     * @return the list of the number of datasets requested
     * @throws DoesNotExistException 
     * @throws InvalidArgumentsException 
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get a list of datasets.", httpMethod = "GET", response = DataSourceContainer.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of datasets)"),
            @ApiResponse(code = 400, message = "InvalidArgumentsException"),
            @ApiResponse(code = 404, message = "DoesNotExistException")
    })
    public Response getDatasetList(@ApiParam(value = "ProviderId", required = true) @QueryParam("providerId") String providerId,
            @ApiParam(value = "Index where to start from(negative not allowed)", defaultValue = "0") @DefaultValue("0") @QueryParam("offset") int offset,
            @ApiParam(value = "Number of aggregators requested(-1 to get all)", defaultValue = "-1") @DefaultValue("-1") @QueryParam("number") int number) throws DoesNotExistException, InvalidArgumentsException {

        if (offset < 0)
            throw new InvalidArgumentsException("Offset negative values not allowed!");

        List<DataSourceContainer> dataSourceContainerListSorted;

        try {
            dataSourceContainerListSorted = dataManager.getDataSourceContainerListSorted(providerId, offset, number);
        } catch (ObjectNotFoundException e) {
            throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
        }

        return Response.status(200).entity(new GenericEntity<List<DataSourceContainer>>(dataSourceContainerListSorted) {
        }).build();
    }

    /**
     * Get the last ingestion date of the dataset.
     * Relative path : /datasets/{datasetId}/date
     * @param datasetId 
     * @return last ingest date
     * @throws DoesNotExistException 
     * @throws InternalServerErrorException 
     */
    @GET
    @Path("/" + DatasetOptionListContainer.DATASETID + "/" + DatasetOptionListContainer.DATE)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get last ingestion date.", httpMethod = "GET", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of datasets)"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response getDatasetLastIngestionDate(@ApiParam(value = "Id of dataset", required = true) @PathParam("datasetId") String datasetId) throws DoesNotExistException,
            InternalServerErrorException
    {
        try {
            DataSourceContainer dataSourceContainer = dataManager.getDataSourceContainer(datasetId);
            if (dataSourceContainer != null)
            {
                if (dataSourceContainer instanceof DefaultDataSourceContainer)
                {
                    DefaultDataSourceContainer defaultDataSourceContainerdata = (DefaultDataSourceContainer)dataSourceContainer;
                    DataSource dataSource = defaultDataSourceContainerdata.getDataSource();
                    if (dataSource == null)
                        throw new DoesNotExistException("Does NOT exist: " + "Dataset with id " + datasetId + " does NOT exist!");

                    Date lastIngestDate = dataSource.getLastUpdate();
                    if (lastIngestDate != null)
                    {
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        String lastIngestDateString = df.format(lastIngestDate);

                        return Response.status(200).entity(new Result(lastIngestDateString)).build();
                    }
                    else
                        throw new DoesNotExistException("Does NOT exist: " + "Last ingestion Date does NOT exist!");
                }
                else
                    throw new InternalServerErrorException("Internal Server Error : " + "Invalid dataSourceContainer instance!");
            }
            else
                throw new DoesNotExistException("Does NOT exist: " + "Dataset with id " + datasetId + " does NOT exist!");
        } catch (DocumentException | IOException e) {
            throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
        }
    }

    /**
     * Get the number of records of the dataset.
     * Relative path : /datasets/{datasetId}/count
     * @param datasetId 
     * @return number of records
     * @throws DoesNotExistException 
     * @throws InternalServerErrorException 
     */
    @GET
    @Path("/" + DatasetOptionListContainer.DATASETID + "/" + DatasetOptionListContainer.COUNT)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get record count.", httpMethod = "GET", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of datasets)"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response getDatasetRecordCount(@ApiParam(value = "Id of dataset", required = true) @PathParam("datasetId") String datasetId) throws DoesNotExistException, InternalServerErrorException
    {
        try {
            DataSourceContainer dataSourceContainer = dataManager.getDataSourceContainer(datasetId);
            if (dataSourceContainer != null)
            {
                if (dataSourceContainer instanceof DefaultDataSourceContainer)
                {
                    DefaultDataSourceContainer defaultDataSourceContainerdata = (DefaultDataSourceContainer)dataSourceContainer;
                    DataSource dataSource = defaultDataSourceContainerdata.getDataSource();
                    if (dataSource == null)
                        throw new DoesNotExistException("Does NOT exist: " + "Dataset with id " + datasetId + " does NOT exist!");

                    return Response.status(200).entity(new Result(Integer.toString(dataSource.getIntNumberRecords()))).build();
                }
                else
                    throw new InternalServerErrorException("Internal Server Error : " + "Invalid dataSourceContainer instance!");
            }
            else
                throw new DoesNotExistException("Does NOT exist: " + "Dataset with id " + datasetId + " does NOT exist!");
        } catch (DocumentException | IOException | SQLException e) {
            throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
        }
    }
    
    /**
     * Initiates an export of data.
     * Relative path : /datasets/{datasetId}/export
     * @param datasetId 
     * @param format 
     * 
     * @return OK or Error Message
     * @throws DoesNotExistException 
     * @throws AlreadyExistsException, InternalServerErrorException 
     * @throws InternalServerErrorException 
     */
    @POST
    @Path("/" + DatasetOptionListContainer.DATASETID + "/" + DatasetOptionListContainer.EXPORT)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Initiates an export of data.", httpMethod = "POST", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a String message)"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 409, message = "AlreadyExistsException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException") 
            })
    public Response exportDataset(@ApiParam(value = "Id of dataset", required = true) @PathParam("datasetId") String datasetId, @ApiParam(value = "Format of export") @QueryParam("format") String format) throws DoesNotExistException, AlreadyExistsException, InternalServerErrorException {
        
        try {
            dataManager.startExportDataSource(datasetId, "1000", format);
        } catch (ClassNotFoundException | NoSuchMethodException | DocumentException | IOException | ParseException e) {
            throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
        } catch (AlreadyExistsException e) {
            throw new AlreadyExistsException("Already exists: " + e.getMessage());
        } catch (ObjectNotFoundException e) {
            throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
        }
        return Response.status(200).entity(new Result("Export of dataset with id: " + datasetId + " started!")).build();
    }
}
