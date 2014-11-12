/* DatasetsResourceTest.java - created on Oct 30, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.dom4j.DocumentException;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.theeuropeanlibrary.repox.rest.configuration.JerseyConfigMocked;
import org.theeuropeanlibrary.repox.rest.pathOptions.DatasetOptionListContainer;

import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSource.StatusDS;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.dataProvider.DefaultDataSourceContainer;
import pt.utl.ist.dataProvider.dataSource.IdExtractedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.IdProvidedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.SimpleFileExtractStrategy;
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
import pt.utl.ist.util.ProviderType;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

/**
 * Datasets context path handling tests.
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 30, 2014
 */
public class DatasetsResourceTest extends JerseyTest {

    DefaultDataManager dataManager;

    public DatasetsResourceTest() throws Exception {
        super(new JerseyConfigMocked());
        dataManager = JerseyConfigMocked.getDataManager();
    }

    @Before
    public void setUpBeforeMethod() throws Exception {
        //Reset mock before every test
        reset(dataManager);
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.DatasetsResource#getOptions()}.
     */
    @Test
    @Ignore
    public void testGetOptions() {
        int numberOfAvailableOptions = 6;
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS);

        //Check xml options working
        Response response = target.request(MediaType.APPLICATION_XML).options();
        assertEquals(200, response.getStatus());
        //Check json options working
        response = target.request(MediaType.APPLICATION_JSON).options();
        assertEquals(200, response.getStatus());
        DatasetOptionListContainer dolc = response.readEntity(DatasetOptionListContainer.class);
        //Check the number of options provided
        assertEquals(numberOfAvailableOptions, dolc.getOptionList().size());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.DatasetsResource#getDataset(java.lang.String)}.
     * @throws IOException 
     * @throws DocumentException 
     */
    @Test
    @Ignore
    public void testGetDataset() throws DocumentException, IOException {
        String datasetId = "SampleId";
        OaiDataSource oaiDataSource = new OaiDataSource(null, "SampleId", "description", "schem", "namesp", "format", "oaiURL", "set", null, null);
        oaiDataSource.setStatus(StatusDS.OK);
        //Mocking
        when(dataManager.getDataSourceContainer(datasetId))
                .thenReturn(new DefaultDataSourceContainer(oaiDataSource, "SampleNameCode", "SampleName", null)).thenReturn(null);

        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId);

        Response response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatus());
        //        System.out.println(response.readEntity(String.class));
        DataSourceContainer dataSourceContainer = response.readEntity(DataSourceContainer.class);

        if (dataSourceContainer instanceof DefaultDataSourceContainer)
        {
            DefaultDataSourceContainer defaultDataSourceContainer = (DefaultDataSourceContainer)dataSourceContainer;
            DataSource dataSource = defaultDataSourceContainer.getDataSource();
            if (dataSource instanceof OaiDataSource)
            {
                assertEquals(((OaiDataSource)dataSource).getId(), datasetId);
            }
        }

        //Check Errors
        //Non existent
        response = target.request(MediaType.APPLICATION_JSON).get();
        assertEquals(404, response.getStatus());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.DatasetsResource#createDataset(String, DataSourceContainer)}.
     * @throws AlreadyExistsException 
     * @throws InvalidArgumentsException 
     * @throws IOException 
     * @throws DocumentException 
     * @throws ObjectNotFoundException 
     * @throws SQLException 
     */
    @Test
    @Ignore
    public void testCreateDatasetOai() throws DocumentException, IOException, InvalidArgumentsException, AlreadyExistsException, ObjectNotFoundException, SQLException {
        String providerId = "SampleProviderId";
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS).queryParam("providerId", providerId);

        //Mocking
        DataProvider dataProvider = new DataProvider(providerId, "testName", "testCountry", "testDescription", null, "testNameCode", "testHomePage", ProviderType.LIBRARY, "SampleEmail");
        OaiDataSource oaiDataSource = new OaiDataSource(dataProvider, "SampleId", "SampleDescription", "SampleSchema", "SampleNamespace", "SampleMetadataFormat", "SampleOaiSourceURL", "SampleOaiSet",
                new IdProvidedRecordIdPolicy(), new TreeMap<String, MetadataTransformation>());
        oaiDataSource.setExportDir("/Sample/Export/Path");
        oaiDataSource.setMarcFormat("SampleMarcFormat");
        DefaultDataSourceContainer defaultDataSourceContainer = new DefaultDataSourceContainer(oaiDataSource, "SampleNameCode", "SampleName", "/Sample/Export/Path");

        when(dataManager.createDataSourceOai(providerId, oaiDataSource.getId(), oaiDataSource.getDescription(), defaultDataSourceContainer.getNameCode(), defaultDataSourceContainer.getName(),
                oaiDataSource.getExportDir(), oaiDataSource.getSchema(), oaiDataSource.getNamespace(), oaiDataSource.getMetadataFormat(), oaiDataSource.getOaiSourceURL(), oaiDataSource.getOaiSet(),
                null, null, oaiDataSource.getMarcFormat())).thenReturn(oaiDataSource).thenThrow(new InvalidArgumentsException("Invalid Argument"))
                .thenThrow(new ObjectNotFoundException("Object not found")).thenThrow(new AlreadyExistsException("Already Exists!")).thenThrow(new SQLException("Exception in SQL"));

        //Valid request created
        Response response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(201, response.getStatus());
        //Invalid Argument
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(400, response.getStatus());
        //Provider not existent
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(404, response.getStatus());
        //Already exists
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(409, response.getStatus());
        //Internal Server Error        
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(500, response.getStatus());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.DatasetsResource#createDataset(String, DataSourceContainer)}.
     * @throws AlreadyExistsException 
     * @throws InvalidArgumentsException 
     * @throws IOException 
     * @throws DocumentException 
     * @throws ObjectNotFoundException 
     * @throws SQLException 
     */
    @Test
    @Ignore
    public void testCreateDatasetDir() throws DocumentException, IOException, InvalidArgumentsException, AlreadyExistsException, ObjectNotFoundException, SQLException {
        String providerId = "SampleProviderId";
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS).queryParam("providerId", providerId);

        //Mocking
        DataProvider dataProvider = new DataProvider(providerId, "testName", "testCountry", "testDescription", null, "testNameCode", "testHomePage", ProviderType.LIBRARY, "SampleEmail");
        DirectoryImporterDataSource folderDataSource = new DirectoryImporterDataSource(dataProvider, "SampleId", "SampleDescription", "SampleSchema", "SampleNamespace", "SampleMetadataFormat",
                new SimpleFileExtractStrategy(), new FolderFileRetrieveStrategy(), CharacterEncoding.UTF_8, "/sample/dir", new IdProvidedRecordIdPolicy(),
                new TreeMap<String, MetadataTransformation>(), "SamplerecordXPath", new HashMap<String, String>());
        folderDataSource.setIsoVariant(Iso2709Variant.STANDARD);
        DefaultDataSourceContainer defaultDataSourceContainer = new DefaultDataSourceContainer(folderDataSource, "SampleNameCode", "SampleName", "/Sample/Export/Path");

        when(dataManager.createDataSourceFolder(providerId, folderDataSource.getId(), folderDataSource.getDescription(), defaultDataSourceContainer.getNameCode(),
                defaultDataSourceContainer.getName(), folderDataSource.getExportDir(), folderDataSource.getSchema(), folderDataSource.getNamespace(), folderDataSource.getMetadataFormat(),
                Iso2709Variant.STANDARD.getIsoVariant(), folderDataSource.getCharacterEncoding().toString(), IdProvidedRecordIdPolicy.IDPROVIDED, null, null,
                folderDataSource.getRecordXPath(), folderDataSource.getSourcesDirPath(), null, null, folderDataSource.getMarcFormat())).thenReturn(folderDataSource)
                .thenThrow(new InvalidArgumentsException("Invalid Argument"))
                .thenThrow(new ObjectNotFoundException("Object not found")).thenThrow(new AlreadyExistsException("Already Exists!")).thenThrow(new SQLException("Exception in SQL"));

        //Valid request created
        Response response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(201, response.getStatus());
        //Invalid Argument
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(400, response.getStatus());
        //Provider not existent
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(404, response.getStatus());
        //Already exists
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(409, response.getStatus());
        //Internal Server Error        
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(500, response.getStatus());

        //Non Mocked errors
        //Missing recordIdPolicy
        folderDataSource.setRecordIdPolicy(null);
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missing idXpath when idExtractedRecordIdPolicy
        folderDataSource.setRecordIdPolicy(new IdExtractedRecordIdPolicy(null, null));
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missing export directory
        folderDataSource.setExportDir("");
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missing isoVariant when metadataformat is ISO2709
        folderDataSource.setMetadataFormat(MetadataFormat.ISO2709.toString());
        folderDataSource.setIsoVariant(null);
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());

        //FTP tests
        FtpFileRetrieveStrategy ftpFileRetrieveStrategy = new FtpFileRetrieveStrategy("example.host", "SampleUser", "SamplePassword", "SampleAccess", "SampleFtpPath");
        DirectoryImporterDataSource folderDataSourceFtp = new DirectoryImporterDataSource(dataProvider, "SampleId", "SampleDescription", "SampleSchema", "SampleNamespace", "SampleMetadataFormat",
                new SimpleFileExtractStrategy(), ftpFileRetrieveStrategy, CharacterEncoding.UTF_8, "/sample/dir", new IdProvidedRecordIdPolicy(),
                new TreeMap<String, MetadataTransformation>(), "SamplerecordXPath", new HashMap<String, String>());
        folderDataSourceFtp.setIsoVariant(Iso2709Variant.STANDARD);
        DefaultDataSourceContainer defaultDataSourceContainerFtp = new DefaultDataSourceContainer(folderDataSourceFtp, "SampleNameCode", "SampleName", "/Sample/Export/Path");

        when(
                dataManager.createDataSourceFtp(providerId, folderDataSourceFtp.getId(), folderDataSourceFtp.getDescription(), defaultDataSourceContainerFtp.getNameCode(),
                        defaultDataSourceContainerFtp.getName(), folderDataSourceFtp.getExportDir(), folderDataSourceFtp.getSchema(), folderDataSourceFtp.getNamespace(),
                        folderDataSourceFtp.getMetadataFormat(),
                        Iso2709Variant.STANDARD.getIsoVariant(), folderDataSourceFtp.getCharacterEncoding().toString(), IdProvidedRecordIdPolicy.IDPROVIDED, null, null,
                        folderDataSourceFtp.getRecordXPath(), ftpFileRetrieveStrategy.getServer(), ftpFileRetrieveStrategy.getUser(), ftpFileRetrieveStrategy.getPassword(),
                        ftpFileRetrieveStrategy.getFtpPath(), null, null, folderDataSourceFtp.getMarcFormat()))
                .thenReturn(folderDataSourceFtp)
                .thenThrow(new InvalidArgumentsException("Invalid Argument"))
                .thenThrow(new ObjectNotFoundException("Object not found"))
                .thenThrow(new AlreadyExistsException("Already Exists!"))
                .thenThrow(new SQLException("Exception in SQL"));

        //Valid request created
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(defaultDataSourceContainerFtp, MediaType.APPLICATION_XML), Response.class);
        assertEquals(201, response.getStatus());
        //Invalid Argument
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(defaultDataSourceContainerFtp, MediaType.APPLICATION_XML), Response.class);
        assertEquals(400, response.getStatus());
        //Provider not existent
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(defaultDataSourceContainerFtp, MediaType.APPLICATION_XML), Response.class);
        assertEquals(404, response.getStatus());
        //Already exists
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(defaultDataSourceContainerFtp, MediaType.APPLICATION_XML), Response.class);
        assertEquals(409, response.getStatus());
        //Internal Server Error        
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(defaultDataSourceContainerFtp, MediaType.APPLICATION_XML), Response.class);
        assertEquals(500, response.getStatus());

        //Non Mocked errors
        //Missing server
        ftpFileRetrieveStrategy.setServer(null);
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(defaultDataSourceContainerFtp, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());

        //HTTP tests
        HttpFileRetrieveStrategy httpFileRetrieveStrategy = new HttpFileRetrieveStrategy("example.com");
        DirectoryImporterDataSource folderDataSourceHttp = new DirectoryImporterDataSource(dataProvider, "SampleId", "SampleDescription", "SampleSchema", "SampleNamespace", "SampleMetadataFormat",
                new SimpleFileExtractStrategy(), httpFileRetrieveStrategy, CharacterEncoding.UTF_8, "/sample/dir", new IdProvidedRecordIdPolicy(),
                new TreeMap<String, MetadataTransformation>(), "SamplerecordXPath", new HashMap<String, String>());
        folderDataSourceHttp.setIsoVariant(Iso2709Variant.STANDARD);
        DefaultDataSourceContainer defaultDataSourceContainerHttp = new DefaultDataSourceContainer(folderDataSourceHttp, "SampleNameCode", "SampleName", "/Sample/Export/Path");

        when(dataManager.createDataSourceHttp(providerId, folderDataSourceHttp.getId(), folderDataSourceHttp.getDescription(), defaultDataSourceContainerHttp.getNameCode(),
                defaultDataSourceContainerHttp.getName(), folderDataSourceHttp.getExportDir(), folderDataSourceHttp.getSchema(), folderDataSourceHttp.getNamespace(),
                folderDataSourceHttp.getMetadataFormat(), Iso2709Variant.STANDARD.getIsoVariant(), folderDataSourceHttp.getCharacterEncoding().toString(), IdProvidedRecordIdPolicy.IDPROVIDED,
                null, null, folderDataSourceHttp.getRecordXPath(), httpFileRetrieveStrategy.getUrl(), null, null, folderDataSourceHttp.getMarcFormat()))
                .thenReturn(folderDataSourceHttp)
                .thenThrow(new InvalidArgumentsException("Invalid Argument"))
                .thenThrow(new ObjectNotFoundException("Object not found"))
                .thenThrow(new AlreadyExistsException("Already Exists!"))
                .thenThrow(new SQLException("Exception in SQL"));

        //Valid request created
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(defaultDataSourceContainerHttp, MediaType.APPLICATION_XML), Response.class);
        assertEquals(201, response.getStatus());
        //Invalid Argument
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(defaultDataSourceContainerHttp, MediaType.APPLICATION_XML), Response.class);
        assertEquals(400, response.getStatus());
        //Provider not existent
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(defaultDataSourceContainerHttp, MediaType.APPLICATION_XML), Response.class);
        assertEquals(404, response.getStatus());
        //Already exists
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(defaultDataSourceContainerHttp, MediaType.APPLICATION_XML), Response.class);
        assertEquals(409, response.getStatus());
        //Internal Server Error        
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(defaultDataSourceContainerHttp, MediaType.APPLICATION_XML), Response.class);
        assertEquals(500, response.getStatus());

        //Non Mocked errors
        //Missing server
        httpFileRetrieveStrategy.setUrl(null);
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(defaultDataSourceContainerHttp, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());

    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.DatasetsResource#deleteDataset(String)}.
     * @throws Exception 
     * @throws DocumentException 
     * @throws ObjectNotFoundException 
     */
    @Test
    @Ignore
    public void testDeleteDataset() throws Exception, DocumentException, ObjectNotFoundException {
        String datasetId = "SampleDatasetId";
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId);

        //Mocking
        doNothing().doThrow(new IOException()).doThrow(new ObjectNotFoundException(datasetId)).when(dataManager).deleteDataSourceContainer(datasetId);

        //Valid call
        Response response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(200, response.getStatus());
        //Two internal server error exceptions
        response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(500, response.getStatus());
        //Resource does NOT exist
        response = target.request(MediaType.APPLICATION_JSON).delete();
        assertEquals(404, response.getStatus());
    }

    /**
     * 
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.DatasetsResource#updateDataset(String, DataSourceContainer)}.
     * @throws Exception
     */
    @Test
    @Ignore
    public void testUpdateDatasetOai() throws Exception {
        String providerId = "SampleProviderId";
        String datasetId = "SampleDatasetId";
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId);

        //Mocking
        DataProvider dataProvider = new DataProvider(providerId, "testName", "testCountry", "testDescription", null, "testNameCode", "testHomePage", ProviderType.LIBRARY, "SampleEmail");
        OaiDataSource oaiDataSource = new OaiDataSource(dataProvider, datasetId, "SampleDescription", "SampleSchema", "SampleNamespace", "SampleMetadataFormat", "SampleOaiSourceURL", "SampleOaiSet",
                new IdProvidedRecordIdPolicy(), new TreeMap<String, MetadataTransformation>());
        oaiDataSource.setExportDir("/Sample/Export/Path");
        oaiDataSource.setMarcFormat("SampleMarcFormat");
        oaiDataSource.setExternalRestServices(new ArrayList<ExternalRestService>());
        DefaultDataSourceContainer defaultDataSourceContainer = new DefaultDataSourceContainer(oaiDataSource, "SampleNameCode", "SampleName", "/Sample/Export/Path");

        //DefaultDataSourceContainer exist but null dataSource
        when(dataManager.getDataSourceContainer(datasetId)).thenReturn(new DefaultDataSourceContainer(null, "SampleNameCode", "SampleName", "/Sample/Export/Path")).thenReturn(null)
                .thenReturn(defaultDataSourceContainer);
        Response response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(404, response.getStatus());
        //DefaultDataSourceContainer does NOT exist
        response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(404, response.getStatus());

        when(dataManager.updateDataSourceOai(datasetId, datasetId, oaiDataSource.getDescription(), defaultDataSourceContainer.getNameCode(),
                defaultDataSourceContainer.getName(), oaiDataSource.getExportDir(), oaiDataSource.getSchema(), oaiDataSource.getNamespace(), oaiDataSource.getMetadataFormat(),
                oaiDataSource.getOaiSourceURL(), oaiDataSource.getOaiSet(), new TreeMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>(), oaiDataSource.getMarcFormat(), true))
                .thenReturn(oaiDataSource).thenThrow(new IOException())
                .thenThrow(new ObjectNotFoundException(providerId))
                .thenThrow(new InvalidArgumentsException());

        //Valid call
        response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(200, response.getStatus());
        //Internal server error exception
        response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(500, response.getStatus());
        //Resource does NOT exist
        response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(404, response.getStatus());
        //Invalid URL
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(400, response.getStatus());

        //Non Mocked errors
        //Missing oaiUrl
        oaiDataSource.setOaiSourceURL(null);
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missing oaiSet
        oaiDataSource.setOaiSet(null);
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missing marcFormat when metadata format is MarcXchange
        oaiDataSource.setMetadataFormat(MetadataFormat.MarcXchange.toString());
        oaiDataSource.setMarcFormat(null);
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missing metadataFormat
        oaiDataSource.setMetadataFormat(null);
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missing Namespace
        oaiDataSource.setNamespace(null);
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missinge Schema
        oaiDataSource.setSchema(null);
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());

    }

    /**
     * 
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.DatasetsResource#updateDataset(String, DataSourceContainer)}.
     * @throws Exception
     */
    @Test
    @Ignore
    public void testUpdateDatasetFolder() throws Exception {
        String providerId = "SampleProviderId";
        String datasetId = "SampleDatasetId";
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId);

        //Mocking
        DataProvider dataProvider = new DataProvider(providerId, "testName", "testCountry", "testDescription", null, "testNameCode", "testHomePage", ProviderType.LIBRARY, "SampleEmail");
        DirectoryImporterDataSource folderDataSource = new DirectoryImporterDataSource(dataProvider, datasetId, "SampleDescription", "SampleSchema", "SampleNamespace", "SampleMetadataFormat",
                new SimpleFileExtractStrategy(), new FolderFileRetrieveStrategy(), CharacterEncoding.UTF_8, "/sample/dir", new IdProvidedRecordIdPolicy(),
                new TreeMap<String, MetadataTransformation>(), "SamplerecordXPath", new HashMap<String, String>());
        folderDataSource.setExportDir("/Sample/Export/Path");
        folderDataSource.setMarcFormat("SampleMarcFormat");
        folderDataSource.setExternalRestServices(new ArrayList<ExternalRestService>());
        folderDataSource.setIsoVariant(Iso2709Variant.STANDARD);
        DefaultDataSourceContainer defaultDataSourceContainer = new DefaultDataSourceContainer(folderDataSource, "SampleNameCode", "SampleName", "/Sample/Export/Path");

        when(dataManager.getDataSourceContainer(datasetId)).thenReturn(defaultDataSourceContainer);
        when(dataManager.updateDataSourceFolder(datasetId, datasetId, folderDataSource.getDescription(), defaultDataSourceContainer.getNameCode(), defaultDataSourceContainer.getName(),
                folderDataSource.getExportDir(), folderDataSource.getSchema(), folderDataSource.getNamespace(), folderDataSource.getMetadataFormat(), Iso2709Variant.STANDARD.getIsoVariant(),
                folderDataSource.getCharacterEncoding().toString(), IdProvidedRecordIdPolicy.IDPROVIDED, null, null,
                folderDataSource.getRecordXPath(), folderDataSource.getSourcesDirPath(), new TreeMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>(),
                folderDataSource.getMarcFormat(), true)).thenReturn(folderDataSource).thenThrow(new IOException())
                .thenThrow(new ObjectNotFoundException(providerId))
                .thenThrow(new InvalidArgumentsException());

        //Valid call
        Response response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(200, response.getStatus());
        //Two internal server error exception
        response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(500, response.getStatus());
        //Resource does NOT exist
        response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(404, response.getStatus());
        //Invalid URL
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(400, response.getStatus());

        //Non Mocked errors
        //Missing sourceDirPath
        folderDataSource.setSourcesDirPath(null);
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missing characterEncoding when metadata format is ISO2709
        folderDataSource.setMetadataFormat(MetadataFormat.ISO2709.toString());
        folderDataSource.setCharacterEncoding(null);
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missing isoVariant when metadata format is ISO2709
        folderDataSource.setIsoVariant(null);
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missing identifierXPath when IdExtracted
        folderDataSource.setRecordIdPolicy(new IdExtractedRecordIdPolicy(null, null));
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missing recordIdPolicy
        folderDataSource.setRecordIdPolicy(null);
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
    }

    /**
     * 
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.DatasetsResource#updateDataset(String, DataSourceContainer)}.
     * @throws Exception
     */
    @Test
    @Ignore
    public void testUpdateDatasetFtp() throws Exception {
        String providerId = "SampleProviderId";
        String datasetId = "SampleDatasetId";
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId);

        //Mocking
        DataProvider dataProvider = new DataProvider(providerId, "testName", "testCountry", "testDescription", null, "testNameCode", "testHomePage", ProviderType.LIBRARY, "SampleEmail");
        FtpFileRetrieveStrategy ftpFileRetrieveStrategy = new FtpFileRetrieveStrategy("SampleServer", "SampleUser", "SamplePassword", "SampleIdTypeAccess", "SampleFtpPath");
        DirectoryImporterDataSource ftpDataSource = new DirectoryImporterDataSource(dataProvider, datasetId, "SampleDescription", "SampleSchema", "SampleNamespace", "SampleMetadataFormat",
                new SimpleFileExtractStrategy(), ftpFileRetrieveStrategy, CharacterEncoding.UTF_8, "/sample/dir", new IdProvidedRecordIdPolicy(),
                new TreeMap<String, MetadataTransformation>(), "SamplerecordXPath", new HashMap<String, String>());
        ftpDataSource.setExportDir("/Sample/Export/Path");
        ftpDataSource.setMarcFormat("SampleMarcFormat");
        ftpDataSource.setExternalRestServices(new ArrayList<ExternalRestService>());
        ftpDataSource.setIsoVariant(Iso2709Variant.STANDARD);
        DefaultDataSourceContainer defaultDataSourceContainer = new DefaultDataSourceContainer(ftpDataSource, "SampleNameCode", "SampleName", "/Sample/Export/Path");

        when(dataManager.getDataSourceContainer(datasetId)).thenReturn(defaultDataSourceContainer);
        when(dataManager.updateDataSourceFtp(datasetId, datasetId, ftpDataSource.getDescription(), defaultDataSourceContainer.getNameCode(), defaultDataSourceContainer.getName(),
                ftpDataSource.getExportDir(), ftpDataSource.getSchema(), ftpDataSource.getNamespace(), ftpDataSource.getMetadataFormat(), Iso2709Variant.STANDARD.getIsoVariant(),
                ftpDataSource.getCharacterEncoding().toString(), IdProvidedRecordIdPolicy.IDPROVIDED, null, null,
                ftpDataSource.getRecordXPath(), ftpFileRetrieveStrategy.getServer(), ftpFileRetrieveStrategy.getUser(), ftpFileRetrieveStrategy.getPassword(), ftpFileRetrieveStrategy.getFtpPath(),
                new TreeMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>(),
                ftpDataSource.getMarcFormat(), true)).thenReturn(ftpDataSource).thenThrow(new IOException())
                .thenThrow(new ObjectNotFoundException(providerId))
                .thenThrow(new InvalidArgumentsException());

        //Valid call
        Response response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(200, response.getStatus());
        //Internal server error exception
        response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(500, response.getStatus());
        //Resource does NOT exist
        response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(404, response.getStatus());
        //Invalid URL
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(400, response.getStatus());

        //Non Mocked errors
        //Missing sourceDirPath
        ftpFileRetrieveStrategy.setServer(null);
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
    }

    /**
     * 
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.DatasetsResource#updateDataset(String, DataSourceContainer)}.
     * @throws Exception
     */
    @Test
    @Ignore
    public void testUpdateDatasetHttp() throws Exception {
        String providerId = "SampleProviderId";
        String datasetId = "SampleDatasetId";
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId);

        //Mocking
        DataProvider dataProvider = new DataProvider(providerId, "testName", "testCountry", "testDescription", null, "testNameCode", "testHomePage", ProviderType.LIBRARY, "SampleEmail");
        HttpFileRetrieveStrategy httpFileRetrieveStrategy = new HttpFileRetrieveStrategy("SampleUrl");
        DirectoryImporterDataSource httpDataSource = new DirectoryImporterDataSource(dataProvider, datasetId, "SampleDescription", "SampleSchema", "SampleNamespace", "SampleMetadataFormat",
                new SimpleFileExtractStrategy(), httpFileRetrieveStrategy, CharacterEncoding.UTF_8, "/sample/dir", new IdProvidedRecordIdPolicy(),
                new TreeMap<String, MetadataTransformation>(), "SamplerecordXPath", new HashMap<String, String>());
        httpDataSource.setExportDir("/Sample/Export/Path");
        httpDataSource.setMarcFormat("SampleMarcFormat");
        httpDataSource.setExternalRestServices(new ArrayList<ExternalRestService>());
        httpDataSource.setIsoVariant(Iso2709Variant.STANDARD);
        DefaultDataSourceContainer defaultDataSourceContainer = new DefaultDataSourceContainer(httpDataSource, "SampleNameCode", "SampleName", "/Sample/Export/Path");

        when(dataManager.getDataSourceContainer(datasetId)).thenReturn(defaultDataSourceContainer);
        when(dataManager.updateDataSourceHttp(datasetId, datasetId, httpDataSource.getDescription(), defaultDataSourceContainer.getNameCode(), defaultDataSourceContainer.getName(),
                httpDataSource.getExportDir(), httpDataSource.getSchema(), httpDataSource.getNamespace(), httpDataSource.getMetadataFormat(), Iso2709Variant.STANDARD.getIsoVariant(),
                httpDataSource.getCharacterEncoding().toString(), IdProvidedRecordIdPolicy.IDPROVIDED, null, null,
                httpDataSource.getRecordXPath(), httpFileRetrieveStrategy.getUrl(),
                new TreeMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>(),
                httpDataSource.getMarcFormat(), true)).thenReturn(httpDataSource).thenThrow(new IOException())
                .thenThrow(new ObjectNotFoundException(providerId))
                .thenThrow(new InvalidArgumentsException());

        //Valid call
        Response response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(200, response.getStatus());
        //Internal server error exception
        response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(500, response.getStatus());
        //Resource does NOT exist
        response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(404, response.getStatus());
        //Invalid URL
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(400, response.getStatus());

        //Non Mocked errors
        //Missing httpUrl
        httpFileRetrieveStrategy.setUrl(null);
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(defaultDataSourceContainer, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.DatasetsResource#getDatasetList(String, int, int)}.
     * @throws Exception 
     */
    @Test
    @Ignore
    public void testGetDatasetList() throws Exception {
        int offset = 0;
        int number = 3;
        String providerId = "SampleProviderId";

        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS).queryParam("providerId", providerId).queryParam("offset", offset).queryParam("number", number);

        //Mocking
        OaiDataSource oaiDataSource1 = new OaiDataSource();
        DefaultDataSourceContainer defaultDataSourceContainer1 = new DefaultDataSourceContainer();
        defaultDataSourceContainer1.setDataSource(oaiDataSource1);
        defaultDataSourceContainer1.setName("Name1");
        OaiDataSource oaiDataSource2 = new OaiDataSource();
        DefaultDataSourceContainer defaultDataSourceContainer2 = new DefaultDataSourceContainer();
        defaultDataSourceContainer2.setDataSource(oaiDataSource2);
        defaultDataSourceContainer2.setName("Name2");
        OaiDataSource oaiDataSource3 = new OaiDataSource();
        DefaultDataSourceContainer defaultDataSourceContainer3 = new DefaultDataSourceContainer();
        defaultDataSourceContainer3.setDataSource(oaiDataSource3);
        defaultDataSourceContainer3.setName("Name3");
        List<DataSourceContainer> datasetList = new ArrayList<DataSourceContainer>();
        datasetList.add(defaultDataSourceContainer1);
        datasetList.add(defaultDataSourceContainer2);
        datasetList.add(defaultDataSourceContainer3);

        when(dataManager.getDataSourceContainerListSorted(providerId, offset, number)).thenReturn(datasetList).thenThrow(new ObjectNotFoundException(providerId));

        //Valid call
        Response response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatus());
        List<DataSourceContainer> subList = response.readEntity(new GenericType<List<DataSourceContainer>>() {
        });
        assertEquals(datasetList.size(), subList.size());

        //Internal Server Error
        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(404, response.getStatus());
        //Error because of index
        target = target("/" + DatasetOptionListContainer.DATASETS).queryParam("offset", -1).queryParam("number", number);
        //Notice not mocked here cause it has to throw the exception before the call to the dataManager
        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(400, response.getStatus());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.DatasetsResource#getDatasetLastIngestionDate(String)}.
     * @throws Exception 
     */
    @Test
        @Ignore
    public void testGetDatasetLastIngestionDate() throws Exception {
        String providerId = "SampleProviderId";
        String datasetId = "SampleDatasetId";
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId + "/" + DatasetOptionListContainer.DATE);

        //Mocking
        DataProvider dataProvider = new DataProvider(providerId, "testName", "testCountry", "testDescription", null, "testNameCode", "testHomePage", ProviderType.LIBRARY, "SampleEmail");
        OaiDataSource oaiDataSource = new OaiDataSource(dataProvider, datasetId, "SampleDescription", "SampleSchema", "SampleNamespace", "SampleMetadataFormat", "SampleOaiSourceURL", "SampleOaiSet",
                new IdProvidedRecordIdPolicy(), new TreeMap<String, MetadataTransformation>());
        oaiDataSource.setExternalRestServices(new ArrayList<ExternalRestService>());
        oaiDataSource.setLastUpdate(new Date());
        DefaultDataSourceContainer defaultDataSourceContainer = new DefaultDataSourceContainer(oaiDataSource, "SampleNameCode", "SampleName", "/Sample/Export/Path");

        when(dataManager.getDataSourceContainer(datasetId)).thenReturn(null).thenThrow(new IOException()).thenReturn(defaultDataSourceContainer);

        Response response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(404, response.getStatus());
        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(500, response.getStatus());
        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatus());

        //Ingest date null
        defaultDataSourceContainer.getDataSource().setLastUpdate(null);
        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(404, response.getStatus());

        //Datasource in defaultDataSourceContainer null
        defaultDataSourceContainer.setDataSource(null);
        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(404, response.getStatus());
    }
    
    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.DatasetsResource#getDatasetRecordCount(String)}.
     * @throws Exception 
     */
    @Test
//        @Ignore
    public void getDatasetRecordCount() throws Exception {
        String providerId = "SampleProviderId";
        String datasetId = "SampleDatasetId";
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId + "/" + DatasetOptionListContainer.DATE);

        //Mocking
        DataProvider dataProvider = new DataProvider(providerId, "testName", "testCountry", "testDescription", null, "testNameCode", "testHomePage", ProviderType.LIBRARY, "SampleEmail");
        OaiDataSource oaiDataSource = new OaiDataSource(dataProvider, datasetId, "SampleDescription", "SampleSchema", "SampleNamespace", "SampleMetadataFormat", "SampleOaiSourceURL", "SampleOaiSet",
                new IdProvidedRecordIdPolicy(), new TreeMap<String, MetadataTransformation>());
        oaiDataSource.setExternalRestServices(new ArrayList<ExternalRestService>());
        oaiDataSource.setLastUpdate(new Date());
        DefaultDataSourceContainer defaultDataSourceContainer = new DefaultDataSourceContainer(oaiDataSource, "SampleNameCode", "SampleName", "/Sample/Export/Path");

        when(dataManager.getDataSourceContainer(datasetId)).thenReturn(null).thenThrow(new IOException()).thenReturn(defaultDataSourceContainer);

        Response response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(404, response.getStatus());
        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(500, response.getStatus());
        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatus());

        //Datasource in defaultDataSourceContainer null
        defaultDataSourceContainer.setDataSource(null);
        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(404, response.getStatus());
    }

}
