/* DatasetsResourceTest.java - created on Oct 30, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TreeMap;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
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
import pt.utl.ist.dataProvider.dataSource.IdProvidedRecordIdPolicy;
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
        int numberOfAvailableOptions = 1;
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
    public void testCreateDataset() throws DocumentException, IOException, InvalidArgumentsException, AlreadyExistsException, ObjectNotFoundException, SQLException {
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
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.DatasetsResource#deleteDataset(String)}.
     * @throws Exception 
     * @throws DocumentException 
     * @throws ObjectNotFoundException 
     */
    @Test
    //    @Ignore
    public void testDeleteProvider() throws Exception, DocumentException, ObjectNotFoundException {
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
}
