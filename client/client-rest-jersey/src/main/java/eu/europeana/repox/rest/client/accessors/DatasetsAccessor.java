/*
 * Copyright 2007-2015 The Europeana Foundation
 * 
 * Licensed under the EUPL, Version 1.1 (the "License") and subsequent versions as approved by the
 * European Commission; You may not use this work except in compliance with the License.
 * 
 * You may obtain a copy of the License at: http://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" basis, without warranties or conditions of any kind, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package eu.europeana.repox.rest.client.accessors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.theeuropeanlibrary.repox.rest.pathOptions.DatasetOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.ProviderOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.DefaultDataSourceContainer;
import pt.utl.ist.dataProvider.dataSource.FileExtractStrategy;
import pt.utl.ist.dataProvider.dataSource.FileRetrieveStrategy;
import pt.utl.ist.dataProvider.dataSource.RecordIdPolicy;
import pt.utl.ist.marc.CharacterEncoding;
import pt.utl.ist.marc.DirectoryImporterDataSource;
import pt.utl.ist.marc.iso2709.shared.Iso2709Variant;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.oai.OaiDataSource;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;

/**
 * Access functionality to the datasets.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Apr 7, 2015
 */
public class DatasetsAccessor {
  private URL restUrl;
  private Client client = JerseyClientBuilder.newClient();
  private static final Logger LOGGER = LoggerFactory.getLogger(DatasetsAccessor.class);

  /**
   * Setup DatasetsAccessor with the target Url, username and password
   * 
   * @param restUrl
   * @throws MalformedURLException
   */
  public DatasetsAccessor(URL restUrl, String username, String password)
      throws MalformedURLException {
    super();

    this.restUrl =
        restUrl.toString().charAt(restUrl.toString().length() - 1) == '/' ? new URL(restUrl
            .toString().substring(0, restUrl.toString().length() - 1)) : restUrl;
    HttpAuthenticationFeature authfeature = HttpAuthenticationFeature.basic(username, password);
    client.register(authfeature);
    LOGGER.info("DatasetsAccessor created with target url: {}", this.restUrl);
  }

  /**
   * Only used for tests. Setup DatasetsAccessor with the target Url, username and password
   * 
   * @param restUrl
   * @param username
   * @param password
   * @param target
   * @throws MalformedURLException
   */
  DatasetsAccessor(URL restUrl, String username, String password, Client client)
      throws MalformedURLException {
    super();
    this.restUrl =
        restUrl.toString().charAt(restUrl.toString().length() - 1) == '/' ? new URL(restUrl
            .toString().substring(0, restUrl.toString().length() - 1)) : restUrl;
    this.client = client;
    HttpAuthenticationFeature authfeature = HttpAuthenticationFeature.basic(username, password);
    client.register(authfeature);
    LOGGER.info("DatasetsAccessor created with target url: {}", this.restUrl);
  }

  /**
   * Retrieve the dataset with the provided id.
   * 
   * @param datasetId
   * @return DataSourceContainer
   * @throws DoesNotExistException
   */
  public DataSourceContainer getDataset(String datasetId) throws DoesNotExistException {
    WebTarget target =
        client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS + "/" + datasetId);
    Response response = target.request(MediaType.APPLICATION_JSON).get();
    if (response.getStatus() == 404) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("getDataset(..) failure! : " + errorMessage.getResult());
      throw new DoesNotExistException(errorMessage.getResult());
    }
    DataSourceContainer datasetContainer = response.readEntity(DataSourceContainer.class);
    LOGGER.info("getDataset(..) success!");

    return datasetContainer;
  }

  /**
   * Delete an dataset by specifying the Id.
   * 
   * @param datasetId
   * @throws DoesNotExistException
   */
  public void deleteDataset(String datasetId) throws DoesNotExistException {
    WebTarget target =
        client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS + "/" + datasetId);
    Response response = target.request(MediaType.APPLICATION_JSON).delete();
    if (response.getStatus() == 404) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("deleteDataset(..) failure! : " + errorMessage.getResult());
      throw new DoesNotExistException(errorMessage.getResult());
    } else if (response.getStatus() == 500) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("deleteDataset(..) failure! : " + errorMessage.getResult());
      throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("deleteDataset(..) success!");
  }

  /**
   * Get a list of datasets in the specified range. Returned number can be smaller than the
   * requested. Offset not allowed negative. If number is negative then it returns all the items
   * from offset until the total number of items.
   * 
   * @param providerId
   * @param offset
   * @param number
   * @return
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   */
  public List<DataSourceContainer> getDatasetList(String providerId, int offset, int number)
      throws InvalidArgumentsException, DoesNotExistException {
    WebTarget target =
        client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS)
            .queryParam(DatasetOptionListContainer.PROVIDERID, providerId)
            .queryParam(ProviderOptionListContainer.OFFSET, offset)
            .queryParam(ProviderOptionListContainer.NUMBER, number);
    Response response = target.request(MediaType.APPLICATION_JSON).get();
    if (response.getStatus() == 400) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("getDatasetList(..) failure! : " + errorMessage.getResult());
      throw new InvalidArgumentsException(errorMessage.getResult());
    } else if (response.getStatus() == 404) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("getDatasetList(..) failure! : " + errorMessage.getResult());
      throw new DoesNotExistException(errorMessage.getResult());
    }
    List<DataSourceContainer> subList =
        response.readEntity(new GenericType<List<DataSourceContainer>>() {});
    LOGGER.info("getDatasetList(..) success!");

    return subList;    // TODO Auto-generated method stub
  }


  /**
   * Create a dataset oai.
   * 
   * @param providerId
   * @param id
   * @param name
   * @param schema
   * @param description
   * @param namespace
   * @param metadataFormat
   * @param marcFormat
   * @param oaiUrl
   * @param oaiSet
   * @param exportDir
   * @param recordIdPolicy
   * @param metadataTransformations
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   * @throws InternalServerErrorException
   */
  public String createDatasetOai(String providerId, String id, String name, String nameCode,
      boolean isSample, String schema, String description, String namespace, String metadataFormat,
      String marcFormat, String oaiUrl, String oaiSet, String exportDir,
      RecordIdPolicy recordIdPolicy, Map<String, MetadataTransformation> metadataTransformations)
      throws InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException, InternalServerErrorException {
    WebTarget target =
        client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS).queryParam(
            DatasetOptionListContainer.PROVIDERID, providerId);
    OaiDataSource oaiDataSource =
        new OaiDataSource(null, id, description, schema, namespace, metadataFormat, oaiUrl, oaiSet,
            recordIdPolicy, metadataTransformations);
    oaiDataSource.setMarcFormat(marcFormat);
    oaiDataSource.setExportDir(exportDir);
    oaiDataSource.setIsSample(isSample);
    DefaultDataSourceContainer defaultDataSourceContainer =
        new DefaultDataSourceContainer(oaiDataSource, nameCode, name, null);

    Response response =
        target.request(MediaType.APPLICATION_JSON).post(
            Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_JSON), Response.class);

    switch (response.getStatus()) {
      case 400:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createDatasetOai(..) failure! : " + errorMessage.getResult());
        throw new InvalidArgumentsException(errorMessage.getResult());
      case 404:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createDatasetOai(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 406:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createDatasetOai(..) failure! : " + errorMessage.getResult());
        throw new MissingArgumentsException(errorMessage.getResult());
      case 409:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createDatasetOai(..) failure! : " + errorMessage.getResult());
        throw new AlreadyExistsException(errorMessage.getResult(), errorMessage.getResult().substring(errorMessage.getResult().indexOf("<") + 1, errorMessage.getResult().indexOf(">")));
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createDatasetOai(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("createDatasetOai(..) success!");
    Result result = response.readEntity(Result.class);
    return result.getResult().substring(result.getResult().indexOf("<") + 1, result.getResult().indexOf(">"));
  }

  /**
   * Create a dataset directory, ftp, http.
   * 
   * @param providerId
   * @param id
   * @param name
   * @param schema
   * @param description
   * @param namespace
   * @param metadataFormat
   * @param marcFormat
   * @param oaiUrl
   * @param oaiSet
   * @param exportDir
   * @param extractStrategy
   * @param retrieveStrategy
   * @param characterEncoding
   * @param isoVariant
   * @param sourceDirectory
   * @param recordXPath
   * @param recordIdPolicy
   * @param metadataTransformations
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   * @throws InternalServerErrorException
   */
  public String createDatasetFile(String providerId, String id, String name, String nameCode,
      boolean isSample, String schema, String description, String namespace, String metadataFormat,
      String marcFormat, String exportDir, RecordIdPolicy recordIdPolicy,
      FileExtractStrategy extractStrategy, FileRetrieveStrategy retrieveStrategy,
      CharacterEncoding characterEncoding, Iso2709Variant isoVariant, String sourceDirectory,
      String recordXPath, Map<String, MetadataTransformation> metadataTransformations)
      throws InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException, InternalServerErrorException {
    WebTarget target =
        client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS).queryParam(
            DatasetOptionListContainer.PROVIDERID, providerId);

    DirectoryImporterDataSource directoryImporterDataSource =
        new DirectoryImporterDataSource(null, id, description, schema, namespace, metadataFormat,
            extractStrategy, retrieveStrategy, characterEncoding, sourceDirectory, recordIdPolicy,
            metadataTransformations, recordXPath, null);
    directoryImporterDataSource.setIsoVariant(isoVariant);
    directoryImporterDataSource.setExportDir(exportDir);
    directoryImporterDataSource.setIsSample(isSample);

    DefaultDataSourceContainer defaultDataSourceContainer =
        new DefaultDataSourceContainer(directoryImporterDataSource, nameCode, name, null);

    Response response =
        target.request(MediaType.APPLICATION_JSON).post(
            Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_JSON), Response.class);

    switch (response.getStatus()) {
      case 400:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createDatasetFile(..) failure! : " + errorMessage.getResult());
        throw new InvalidArgumentsException(errorMessage.getResult());
      case 404:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createDatasetFile(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 406:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createDatasetFile(..) failure! : " + errorMessage.getResult());
        throw new MissingArgumentsException(errorMessage.getResult());
      case 409:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createDatasetFile(..) failure! : " + errorMessage.getResult());
        throw new AlreadyExistsException(errorMessage.getResult(), errorMessage.getResult().substring(errorMessage.getResult().indexOf("<") + 1, errorMessage.getResult().indexOf(">")));
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createDatasetFile(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("createDatasetFile(..) success!");
    Result result = response.readEntity(Result.class);
    return result.getResult().substring(result.getResult().indexOf("<") + 1, result.getResult().indexOf(">"));
  }

  /**
   * Update a dataset by specifying the Id.
   * 
   * @param id    // TODO Auto-generated method stub
   * @param newId
   * @param name
   * @param nameCode
   * @param isSample
   * @param schema
   * @param description
   * @param namespace
   * @param metadataFormat
   * @param marcFormat
   * @param oaiUrl
   * @param oaiSet
   * @param exportDir
   * @param recordIdPolicy
   * @param metadataTransformations
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   * @throws InternalServerErrorException
   */
  public void updateDatasetOai(String id, String newId, String name, String nameCode,
      boolean isSample, String schema, String description, String namespace, String metadataFormat,
      String marcFormat, String oaiUrl, String oaiSet, String exportDir,
      RecordIdPolicy recordIdPolicy, Map<String, MetadataTransformation> metadataTransformations)
      throws InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException, InternalServerErrorException {
    WebTarget target =
        client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS + "/" + id);

    OaiDataSource oaiDataSource =
        new OaiDataSource(null, newId, description, schema, namespace, metadataFormat, oaiUrl,
            oaiSet, recordIdPolicy, metadataTransformations);
    oaiDataSource.setMarcFormat(marcFormat);
    oaiDataSource.setExportDir(exportDir);
    oaiDataSource.setIsSample(isSample);
    DefaultDataSourceContainer defaultDataSourceContainer =
        new DefaultDataSourceContainer(oaiDataSource, nameCode, name, null);

    Response response =
        target.request(MediaType.APPLICATION_JSON).put(
            Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_JSON), Response.class);

    switch (response.getStatus()) {
      case 400:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateDatasetOai(..) failure! : " + errorMessage.getResult());
        throw new InvalidArgumentsException(errorMessage.getResult());
      case 404:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateDatasetOai(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 406:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateDatasetOai(..) failure! : " + errorMessage.getResult());
        throw new MissingArgumentsException(errorMessage.getResult());
      case 409:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateDatasetOai(..) failure! : " + errorMessage.getResult());
        throw new AlreadyExistsException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateDatasetOai(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("updateDatasetOai(..) success!");
  }

  /**
   * Update a dataset by specifying the Id.
   * 
   * @param id
   * @param newId
   * @param name
   * @param nameCode
   * @param isSample
   * @param schema
   * @param description
   * @param namespace
   * @param metadataFormat
   * @param marcFormat
   * @param exportDir
   * @param recordIdPolicy
   * @param extractStrategy
   * @param retrieveStrategy
   * @param characterEncoding
   * @param isoVariant
   * @param sourceDirectory
   * @param recordXPath
   * @param metadataTransformations
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   * @throws InternalServerErrorException
   */
  public void updateDatasetFile(String id, String newId, String name, String nameCode,
      boolean isSample, String schema, String description, String namespace, String metadataFormat,
      String marcFormat, String exportDir, RecordIdPolicy recordIdPolicy,
      FileExtractStrategy extractStrategy, FileRetrieveStrategy retrieveStrategy,
      CharacterEncoding characterEncoding, Iso2709Variant isoVariant, String sourceDirectory,
      String recordXPath, Map<String, MetadataTransformation> metadataTransformations)
      throws InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException, InternalServerErrorException {
    WebTarget target =
        client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS + "/" + id);

    DirectoryImporterDataSource directoryImporterDataSource =
        new DirectoryImporterDataSource(null, id, description, schema, namespace, metadataFormat,
            extractStrategy, retrieveStrategy, characterEncoding, sourceDirectory, recordIdPolicy,
            metadataTransformations, recordXPath, null);
    directoryImporterDataSource.setIsoVariant(isoVariant);
    directoryImporterDataSource.setExportDir(exportDir);
    directoryImporterDataSource.setIsSample(isSample);

    DefaultDataSourceContainer defaultDataSourceContainer =
        new DefaultDataSourceContainer(directoryImporterDataSource, nameCode, name, null);

    Response response =
        target.request(MediaType.APPLICATION_JSON).put(
            Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_JSON), Response.class);

    switch (response.getStatus()) {
      case 400:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateDatasetFile(..) failure! : " + errorMessage.getResult());
        throw new InvalidArgumentsException(errorMessage.getResult());
      case 404:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateDatasetFile(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 406:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateDatasetFile(..) failure! : " + errorMessage.getResult());
        throw new MissingArgumentsException(errorMessage.getResult());
      case 409:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateDatasetFile(..) failure! : " + errorMessage.getResult());
        throw new AlreadyExistsException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateDatasetFile(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("updateDatasetFile(..) success!");
  }

  /**
   * Copy an dataset to another dataset with a newDatasetId.
   * 
   * @param id
   * @param newId
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   */
  public void copyDataset(String id, String newId) throws InvalidArgumentsException,
      DoesNotExistException, MissingArgumentsException, AlreadyExistsException {
    WebTarget target =
        client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS + "/" + id).queryParam(
            "newDatasetId", newId);
    Response response =
        target.request(MediaType.APPLICATION_JSON).post(
            Entity.entity(null, MediaType.APPLICATION_JSON), Response.class);

    switch (response.getStatus()) {
      case 400:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("copyDataset(..) failure! : " + errorMessage.getResult());
        throw new InvalidArgumentsException(errorMessage.getResult());
      case 404:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("copyDataset(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 406:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("copyDataset(..) failure! : " + errorMessage.getResult());
        throw new MissingArgumentsException(errorMessage.getResult());
      case 409:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("copyDataset(..) failure! : " + errorMessage.getResult());
        throw new AlreadyExistsException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("copyDataset(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("copyDataset(..) success!");
  }

  /**
   * Get the last ingestion date of the dataset.
   * @param id
   * @return
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  public String getDatasetLastIngestionDate(String id) throws DoesNotExistException,
      InternalServerErrorException {
    WebTarget target =
        client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS + "/" + id + "/" + DatasetOptionListContainer.DATE);
    Response response = target.request(MediaType.APPLICATION_JSON).get();

    switch (response.getStatus()) {
      case 404:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("getDatasetLastIngestionDate(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("getDatasetLastIngestionDate(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("getDatasetLastIngestionDate(..) success!");
    Result result = response.readEntity(Result.class);
    return result.getResult();
  }
  
  /**
   * Get the number of records of the dataset.
   * @param id
   * @return
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  public int getDatasetRecordCount(String id) throws DoesNotExistException, InternalServerErrorException
  {
    WebTarget target =
        client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS + "/" + id + "/" + DatasetOptionListContainer.COUNT);
    Response response = target.request(MediaType.APPLICATION_JSON).get();

    switch (response.getStatus()) {
      case 404:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("getDatasetRecordCount(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("getDatasetRecordCount(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("getDatasetRecordCount(..) success!");
    Result result = response.readEntity(Result.class);
    return Integer.parseInt(result.getResult());
  }
  
  /**
   * Initiates an export of data.
   * @param id
   * @param format
   * @throws DoesNotExistException
   * @throws AlreadyExistsException
   */
  public void exportDataset(String id, String format) throws DoesNotExistException, AlreadyExistsException
  {
    WebTarget target = client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS + "/" + id + "/" + DatasetOptionListContainer.EXPORT).queryParam(DatasetOptionListContainer.FORMAT, format);
    Response response = target.request(MediaType.APPLICATION_JSON).post(null, Response.class);
    
    switch (response.getStatus()) {
      case 404:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("exportDataset(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 409:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("exportDataset(..) failure! : " + errorMessage.getResult());
        throw new AlreadyExistsException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("exportDataset(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("exportDataset(..) success!");
  }

//  public static void main(String[] args) throws MalformedURLException, DoesNotExistException,
//      InvalidArgumentsException, InternalServerErrorException, MissingArgumentsException,
//      AlreadyExistsException {
//    DatasetsAccessor da =
//        new DatasetsAccessor(new URL("http://localhost:8080/repox/rest"), "temporary", "temporary");
//    // DefaultDataSourceContainer dataset = (DefaultDataSourceContainer) da.getDataset("a0660");
//    // System.out.println(dataset.getDataSource());
//    // da.deleteDataset("exd0");
//    // List<DataSourceContainer> datasetList = da.getDatasetList("P0r0", 0, 5);
//    // System.out.println(datasetList.get(0).getDataSource().getId());
//
//    // da.createDatasetFile("P0r0", null, "ExampleOAI", "nc", true,
//    // "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
//    // "http://www.europeana.eu/schemas/ese/", "ese", null, "/tmp/export/a0661",
//    // new IdProvidedRecordIdPolicy(), new SimpleFileExtractStrategy(), new
//    // FolderFileRetrieveStrategy(),
//    // CharacterEncoding.UTF_8, Iso2709Variant.STANDARD, "/sample/dir", "SamplerecordXPath", null);
//    // da.updateDatasetOai("a0662", "a0660", "ABO", "a0660", true,
//    // "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
//    // "http://www.europeana.eu/schemas/ese/", "ese", null,
//    // "http://oai.onb.ac.at/repox2/OAIHandler", "abo", "/tmp/export3/a0660",
//    // new IdProvidedRecordIdPolicy(), null);
//
//    // da.updateDatasetFile("ncr0", null, "ExampleOAIAfter", "nc", true,
//    // "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
//    // "http://www.europeana.eu/schemas/ese/", "ese", null, "/tmp/export/a0661",
//    // new IdProvidedRecordIdPolicy(), new SimpleFileExtractStrategy(), new
//    // FolderFileRetrieveStrategy(),
//    // CharacterEncoding.UTF_8, Iso2709Variant.STANDARD, "/sample/dir", "SamplerecordXPath", null);
//
////    da.copyDataset("a0660", "a0662");
////    System.out.println(da.getDatasetLastIngestionDate("a0660"));
////    System.out.println(da.getDatasetRecordCount("a0660"));
//    da.exportDataset("a0660", null);
//  }

}
