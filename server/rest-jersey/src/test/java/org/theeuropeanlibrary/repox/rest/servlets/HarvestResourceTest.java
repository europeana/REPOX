/* HarvestResourceTest.java - created on Nov 17, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;

import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.theeuropeanlibrary.repox.rest.configuration.JerseyConfigMocked;
import org.theeuropeanlibrary.repox.rest.pathOptions.DatasetOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.HarvestOptionListContainer;

import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.task.Task;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

/**
 * Harvest context path handling tests.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Nov 17, 2014
 */
public class HarvestResourceTest extends JerseyTest {
    DefaultDataManager dataManager;

    public HarvestResourceTest() throws Exception {
        super(new JerseyConfigMocked());
        dataManager = JerseyConfigMocked.getDataManager();
    }

    @Before
    public void setUpBeforeMethod() throws Exception {
        //Reset mock before every test
        reset(dataManager);
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.HarvestResource#getOptions()}.
     */
    @Test
    @Ignore
    public void testGetOptions() {
        int numberOfAvailableOptions = 1;
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + HarvestOptionListContainer.HARVEST);

        //Check xml options working
        Response response = target.request(MediaType.APPLICATION_XML).options();
        assertEquals(200, response.getStatus());
        //Check json options working
        response = target.request(MediaType.APPLICATION_JSON).options();
        assertEquals(200, response.getStatus());
        HarvestOptionListContainer holc = response.readEntity(HarvestOptionListContainer.class);
        //Check the number of options provided
        assertEquals(numberOfAvailableOptions, holc.getOptionList().size());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.HarvestResource#startHarvest(String, String)}.
     * @throws Exception
     */
    @Test
    @Ignore
    public void testStartHarvest() throws Exception {
        String datasetId = "SampleId";
        boolean fullIngest = false;
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId + "/" + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.START);

        //Mocking
        doThrow(new IOException()).doThrow(new AlreadyExistsException("Task for dataSource with id : " + datasetId + " already exists!"))
                .doThrow(new ObjectNotFoundException("Datasource with id " + datasetId + " NOT found!")).doNothing().when(dataManager).startIngestDataSource(datasetId, fullIngest);

        //Internal Server Error    
        Response response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(null, MediaType.APPLICATION_XML), Response.class);
        assertEquals(500, response.getStatus());
        //Already exists
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(null, MediaType.APPLICATION_XML), Response.class);
        assertEquals(409, response.getStatus());
        //Non existent
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(null, MediaType.APPLICATION_XML), Response.class);
        assertEquals(404, response.getStatus());

        //Valid call
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(null, MediaType.APPLICATION_XML), Response.class);
        assertEquals(200, response.getStatus());

        target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId + "/" + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.START).queryParam("type",
                HarvestOptionListContainer.FULL);

        //Valid call
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(null, MediaType.APPLICATION_XML), Response.class);
        assertEquals(200, response.getStatus());
        assertEquals(response.readEntity(String.class).contains(HarvestOptionListContainer.FULL), true);

    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.HarvestResource#cancelHarvest(String)}.
     * @throws Exception
     */
    @Test
    //    @Ignore
    public void testCancelHarvest() throws Exception {
        String datasetId = "SampleId";
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId + "/" + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.CANCEL);

        //Mocking
        doThrow(new IOException()).doThrow(new ObjectNotFoundException("Datasource with id " + datasetId + " NOT found!")).doNothing().when(dataManager)
                .stopIngestDataSource(datasetId, Task.Status.CANCELED);

        //Internal Server Error    
        Response response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(500, response.getStatus());
        //Non existent
        response = target.request(MediaType.APPLICATION_JSON).delete();
        assertEquals(404, response.getStatus());

        //Valid call
        response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(200, response.getStatus());

    }
}
