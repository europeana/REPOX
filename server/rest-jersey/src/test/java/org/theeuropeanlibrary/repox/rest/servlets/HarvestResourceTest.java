/* HarvestResourceTest.java - created on Nov 17, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

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

import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.dataProvider.DefaultDataSourceContainer;
import pt.utl.ist.dataProvider.dataSource.IdProvidedRecordIdPolicy;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.oai.OaiDataSource;
import pt.utl.ist.task.ScheduledTask;
import pt.utl.ist.task.ScheduledTask.Frequency;
import pt.utl.ist.task.Task;
import pt.utl.ist.task.TaskManager;
import pt.utl.ist.util.ProviderType;
import pt.utl.ist.util.TimeUtil;
import pt.utl.ist.util.date.DateUtil;
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
    TaskManager        taskManager;

    public HarvestResourceTest() throws Exception {
        super(new JerseyConfigMocked());
        dataManager = JerseyConfigMocked.getDataManager();
        taskManager = JerseyConfigMocked.getTaskManager();
    }

    @Before
    public void setUpBeforeMethod() throws Exception {
        //Reset mock before every test
        reset(dataManager);
        reset(taskManager);
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
        String providerId = "SampleProviderId";
        String datasetId = "SampleId";
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId + "/" + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.START).queryParam("type",
                HarvestOptionListContainer.FULL);

        //Mocking
        DataProvider dataProvider = new DataProvider(providerId, "testName", "testCountry", "testDescription", null, "testNameCode", "testHomePage", ProviderType.LIBRARY, "SampleEmail");
        OaiDataSource oaiDataSource = new OaiDataSource(dataProvider, "SampleId", "SampleDescription", "SampleSchema", "SampleNamespace", "SampleMetadataFormat", "SampleOaiSourceURL", "SampleOaiSet",
                new IdProvidedRecordIdPolicy(), new TreeMap<String, MetadataTransformation>());
        oaiDataSource.setExportDir("/Sample/Export/Path");
        oaiDataSource.setMarcFormat("SampleMarcFormat");
        DefaultDataSourceContainer defaultDataSourceContainer = new DefaultDataSourceContainer(oaiDataSource, "SampleNameCode", "SampleName", "/Sample/Export/Path");

        when(dataManager.getDataSourceContainer(datasetId)).thenThrow(new IOException()).thenReturn(null).thenReturn(defaultDataSourceContainer);
        //Internal Server Error    
        Response response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(null, MediaType.APPLICATION_XML), Response.class);
        assertEquals(500, response.getStatus());
        //Non existent
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(null, MediaType.APPLICATION_XML), Response.class);
        assertEquals(404, response.getStatus());

        doThrow(new IOException()).doThrow(new AlreadyExistsException("Task for dataSource with id : " + datasetId + " already exists!"))
                .doThrow(new ObjectNotFoundException("Datasource with id " + datasetId + " NOT found!")).doNothing().when(dataManager).startIngestDataSource(datasetId, true, false);

        //Internal Server Error    
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(null, MediaType.APPLICATION_XML), Response.class);
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
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.HarvestResource#cancelHarvest(String)}.
     * @throws Exception
     */
    @Test
    @Ignore
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

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.HarvestResource#scheduleHarvest(String, Task, boolean)}.
     * @throws Exception
     */
    @Test
    @Ignore
    public void testScheduleHarvest() throws Exception {
        String providerId = "SampleProviderId";
        String datasetId = "SampleId";
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId + "/" + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.SCHEDULE);

        //Mocking
        DataProvider dataProvider = new DataProvider(providerId, "testName", "testCountry", "testDescription", null, "testNameCode", "testHomePage", ProviderType.LIBRARY, "SampleEmail");
        OaiDataSource oaiDataSource = new OaiDataSource(dataProvider, "SampleId", "SampleDescription", "SampleSchema", "SampleNamespace", "SampleMetadataFormat", "SampleOaiSourceURL", "SampleOaiSet",
                new IdProvidedRecordIdPolicy(), new TreeMap<String, MetadataTransformation>());
        oaiDataSource.setExportDir("/Sample/Export/Path");
        oaiDataSource.setMarcFormat("SampleMarcFormat");
        DefaultDataSourceContainer defaultDataSourceContainer = new DefaultDataSourceContainer(oaiDataSource, "SampleNameCode", "SampleName", "/Sample/Export/Path");

        ScheduledTask scheduledTask = new ScheduledTask();
        String newTaskId = "SampleId";
        scheduledTask.setId(newTaskId);
        Calendar calendar = Calendar.getInstance();
        scheduledTask.setFrequency(null);

        when(dataManager.getDataSourceContainer(datasetId)).thenThrow(new IOException()).thenReturn(null).thenReturn(defaultDataSourceContainer);

        //Internal Server Error    
        Response response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(scheduledTask, MediaType.APPLICATION_XML), Response.class);
        assertEquals(500, response.getStatus());
        //Non existent
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(scheduledTask, MediaType.APPLICATION_JSON), Response.class);
        assertEquals(404, response.getStatus());

        //Missing argument firstRun
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(scheduledTask, MediaType.APPLICATION_JSON), Response.class);
        assertEquals(406, response.getStatus());
        calendar.add(Calendar.MONTH, 1);
        scheduledTask.setFirstRun(calendar);
        //Missing argument frequency
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(scheduledTask, MediaType.APPLICATION_JSON), Response.class);
        assertEquals(406, response.getStatus());
        scheduledTask.setFrequency(Frequency.XMONTHLY);
        scheduledTask.setXmonths(null);
        //Missing argument xmonths when frequency XMONTHLY
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(scheduledTask, MediaType.APPLICATION_JSON), Response.class);
        assertEquals(406, response.getStatus());

        scheduledTask.setFrequency(Frequency.ONCE);
        DataSource dataSource = mock(DataSource.class);
        defaultDataSourceContainer.setDataSource(dataSource);

        when(dataSource.getNewTaskId()).thenThrow(new IOException()).thenReturn("TaskId");
        //Internal Server Error    
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(scheduledTask, MediaType.APPLICATION_XML), Response.class);
        assertEquals(500, response.getStatus());

        when(dataSource.getId()).thenReturn(datasetId);
        when(taskManager.taskAlreadyExists(datasetId, DateUtil.date2String(scheduledTask.getFirstRun().getTime(), TimeUtil.LONG_DATE_FORMAT_NO_SECS), scheduledTask.getFrequency()))
                .thenReturn(true).thenReturn(false);

        //Already exist Task
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(scheduledTask, MediaType.APPLICATION_JSON), Response.class);
        assertEquals(409, response.getStatus());
        //Created
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(scheduledTask, MediaType.APPLICATION_XML), Response.class);
        assertEquals(201, response.getStatus());

    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.HarvestResource#getDatasetScheduledTasks(String)}.
     * @throws Exception
     */
    @Test
    @Ignore
    public void testGetDatasetScheduledTasks() throws Exception {
        String providerId = "SampleProviderId";
        String datasetId = "SampleId";
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId + "/" + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.SCHEDULES);

        //Mocking
        DataProvider dataProvider = new DataProvider(providerId, "testName", "testCountry", "testDescription", null, "testNameCode", "testHomePage", ProviderType.LIBRARY, "SampleEmail");
        OaiDataSource oaiDataSource = new OaiDataSource(dataProvider, "SampleId", "SampleDescription", "SampleSchema", "SampleNamespace", "SampleMetadataFormat", "SampleOaiSourceURL", "SampleOaiSet",
                new IdProvidedRecordIdPolicy(), new TreeMap<String, MetadataTransformation>());
        oaiDataSource.setExportDir("/Sample/Export/Path");
        oaiDataSource.setMarcFormat("SampleMarcFormat");
        DefaultDataSourceContainer defaultDataSourceContainer = new DefaultDataSourceContainer(oaiDataSource, "SampleNameCode", "SampleName", "/Sample/Export/Path");

        when(dataManager.getDataSourceContainer(datasetId)).thenThrow(new IOException()).thenReturn(null).thenReturn(defaultDataSourceContainer);

        //Internal Server Error    
        Response response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(500, response.getStatus());
        //Non existent
        response = target.request(MediaType.APPLICATION_JSON).get();
        assertEquals(404, response.getStatus());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.HarvestResource#deleteScheduledTask(String, String)}.
     * @throws Exception
     */
    @Test
    @Ignore
    public void testDeleteScheduledTask() throws Exception {
        String providerId = "SampleProviderId";
        String datasetId = "SampleId";
        String taskId = "SampleTaskId";
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId + "/" + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.SCHEDULES + "/" + taskId);

        //Mocking
        DataProvider dataProvider = new DataProvider(providerId, "testName", "testCountry", "testDescription", null, "testNameCode", "testHomePage", ProviderType.LIBRARY, "SampleEmail");
        OaiDataSource oaiDataSource = new OaiDataSource(dataProvider, "SampleId", "SampleDescription", "SampleSchema", "SampleNamespace", "SampleMetadataFormat", "SampleOaiSourceURL", "SampleOaiSet",
                new IdProvidedRecordIdPolicy(), new TreeMap<String, MetadataTransformation>());
        oaiDataSource.setExportDir("/Sample/Export/Path");
        oaiDataSource.setMarcFormat("SampleMarcFormat");
        DefaultDataSourceContainer defaultDataSourceContainer = new DefaultDataSourceContainer(oaiDataSource, "SampleNameCode", "SampleName", "/Sample/Export/Path");

        when(dataManager.getDataSourceContainer(datasetId)).thenThrow(new IOException()).thenReturn(null).thenReturn(defaultDataSourceContainer);

        //Internal Server Error    
        Response response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(500, response.getStatus());
        //Non existent
        response = target.request(MediaType.APPLICATION_JSON).delete();
        assertEquals(404, response.getStatus());

        when(taskManager.getTask(taskId)).thenReturn(null).thenReturn(new ScheduledTask());
        //Non existent
        response = target.request(MediaType.APPLICATION_JSON).delete();
        assertEquals(404, response.getStatus());

        when(taskManager.deleteTask(taskId)).thenReturn(false).thenReturn(true);
        //Internal Server Error    
        response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(500, response.getStatus());
        //Valid
        response = target.request(MediaType.APPLICATION_JSON).delete();
        assertEquals(200, response.getStatus());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.HarvestResource#getDatasetLastIngestLog(String)}.
     * @throws Exception
     */
    @Test
    @Ignore
    public void testGetDatasetLastIngestLog() throws Exception {
        String providerId = "SampleProviderId";
        String datasetId = "SampleId";
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + datasetId + "/" + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.LOG);

        //Mocking
        DataProvider dataProvider = new DataProvider(providerId, "testName", "testCountry", "testDescription", null, "testNameCode", "testHomePage", ProviderType.LIBRARY, "SampleEmail");
        OaiDataSource oaiDataSource = new OaiDataSource(dataProvider, "SampleId", "SampleDescription", "SampleSchema", "SampleNamespace", "SampleMetadataFormat", "SampleOaiSourceURL", "SampleOaiSet",
                new IdProvidedRecordIdPolicy(), new TreeMap<String, MetadataTransformation>());
        oaiDataSource.setExportDir("/Sample/Export/Path");
        oaiDataSource.setMarcFormat("SampleMarcFormat");
        DefaultDataSourceContainer defaultDataSourceContainer = new DefaultDataSourceContainer(oaiDataSource, "SampleNameCode", "SampleName", "/Sample/Export/Path");

        when(dataManager.getDataSourceContainer(datasetId)).thenThrow(new IOException()).thenReturn(null).thenReturn(defaultDataSourceContainer);

        //Internal Server Error    
        Response response = target.request(MediaType.TEXT_PLAIN).get();
        assertEquals(500, response.getStatus());
        //Non existent
        response = target.request(MediaType.TEXT_PLAIN).get();
        assertEquals(404, response.getStatus());

        DataSource datasource = mock(DataSource.class);
        defaultDataSourceContainer.setDataSource(datasource);
        when(datasource.getLastLogDataSource()).thenThrow(new ObjectNotFoundException("Log file NOT found!")).thenThrow(new IOException()).thenReturn("Log lines...");

        //Non existent
        response = target.request(MediaType.TEXT_PLAIN).get();
        assertEquals(404, response.getStatus());

        //Internal Server Error    
        response = target.request(MediaType.TEXT_PLAIN).get();
        assertEquals(500, response.getStatus());

        //Valid
        response = target.request(MediaType.TEXT_PLAIN).get();
        assertEquals(200, response.getStatus());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.HarvestResource#getCurrentHarvestsList()}.
     */
    @Test
    //    @Ignore
    public void testGetCurrentHarvestsList() {
        WebTarget target = target("/" + DatasetOptionListContainer.DATASETS + "/" + HarvestOptionListContainer.HARVESTS);

        when(taskManager.getRunningTasks()).thenReturn(new ArrayList<Task>());
        
        //Valid
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response.getStatus());
    }
}
