/* DatasetsResourceTest.java - created on Oct 30, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.dom4j.DocumentException;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.theeuropeanlibrary.repox.rest.configuration.JerseyConfigMocked;
import org.theeuropeanlibrary.repox.rest.data.DataSourceType;
import org.theeuropeanlibrary.repox.rest.data.DatasetTypeContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.DatasetOptionListContainer;

import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSource.StatusDS;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.dataProvider.DefaultDataSourceContainer;
import pt.utl.ist.oai.OaiDataSource;

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
//    @Ignore
    public void testGetDataset() throws DocumentException, IOException {
        String datasetId = "SampleId";
        OaiDataSource oaiDataSource = new OaiDataSource(null, "someId", "description", "schem", "namesp", "format", "oaiURL", "set",  null, null);
        oaiDataSource.setStatus(StatusDS.OK);
        //Mocking
        when(dataManager.getDataSourceContainer(datasetId))
        .thenReturn(new DefaultDataSourceContainer(oaiDataSource, null, null, null));

        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId);

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response.getStatus());
        DatasetTypeContainer datasetTypeContainer = response.readEntity(DatasetTypeContainer.class);

        if(datasetTypeContainer.getDataSourceType() == DataSourceType.OAI)
            System.out.println(((OaiDataSource)datasetTypeContainer.getDatasource()).getNamespace());

//        System.out.println(response.readEntity(String.class));
        

//        //Check Errors
//        //Check get xml working with 404 status
//        target = target("/" + ProviderOptionListContainer.PROVIDERS + "/" + "FakeAggregatorId");
//        response = target.request(MediaType.APPLICATION_XML).get();
//        assertEquals(404, response.getStatus());
//        //Check get xml working with 404 status
//        response = target.request(MediaType.APPLICATION_JSON).get();
//        assertEquals(404, response.getStatus());
    }
}
