/* HarvestResource.java - created on Nov 17, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
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
import org.theeuropeanlibrary.repox.rest.pathOptions.HarvestOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.dataProvider.MessageType;
import pt.utl.ist.task.DataSourceIngestTask;
import pt.utl.ist.task.IngestDataSource;
import pt.utl.ist.task.ScheduledTask;
import pt.utl.ist.task.ScheduledTask.Frequency;
import pt.utl.ist.task.Task;
import pt.utl.ist.task.TaskManager;
import pt.utl.ist.util.TimeUtil;
import pt.utl.ist.util.date.DateUtil;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Harvest context path handling.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Nov 17, 2014
 */
@Path("/" + DatasetOptionListContainer.DATASETS)
@Api(value = "/" + HarvestOptionListContainer.HARVEST, description = "Rest api for datasets")
public class HarvestResource {
    @Context
    UriInfo                   uriInfo;

    public DefaultDataManager dataManager;
    public TaskManager        taskManager;

    /**
     * Creates a new instance of this class.
     */
    public HarvestResource() {
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
        dataManager = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager());
        taskManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager();
    }

    /**
     * Creates a new instance by providing the DataManager. (For Tests)
     * @param dataManager
     * @param taskManager 
     */
    public HarvestResource(DefaultDataManager dataManager, TaskManager taskManager) {
        super();
        this.dataManager = dataManager;
        this.taskManager = taskManager;
    }

    /**
     * Retrieve all the available options for Harvest.
     * Relative path : /datasets/harvest
     * @return the list of the options available wrapped in a container
     */
    @OPTIONS
    @Path("/" + HarvestOptionListContainer.HARVEST)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get options over harvest conext.", httpMethod = "OPTIONS", response = HarvestOptionListContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)") })
    public HarvestOptionListContainer getOptions() {
        HarvestOptionListContainer harvestOptionListContainer = new HarvestOptionListContainer(uriInfo.getBaseUri());
        return harvestOptionListContainer;
    }

    /**
     * Initiates a new harvest of the dataset with id.
     * Relative path : /datasets/{datasetId}/harvest/start 
     * @param datasetId 
     * @param type 
     * 
     * @return OK or Error Message
     * @throws InternalServerErrorException 
     * @throws DoesNotExistException 
     * @throws AlreadyExistsException  
     * @throws SecurityException 
     */
    @POST
    @Path("/" + DatasetOptionListContainer.DATASETID + "/" + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.START)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Initiate a new harvest.", httpMethod = "POST", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a String message)"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 409, message = "AlreadyExistsException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response startHarvest(@ApiParam(value = "Id of dataset", required = true) @PathParam("datasetId") String datasetId,
            @ApiParam(value = "full|sample") @DefaultValue(HarvestOptionListContainer.SAMPLE) @QueryParam("type") String type) throws AlreadyExistsException, DoesNotExistException {

        boolean full = true;

        if (type == null || type.equals("") || (!type.equals(HarvestOptionListContainer.FULL) && !type.equals(HarvestOptionListContainer.SAMPLE)))
            full = false;
        else if (type.equals(HarvestOptionListContainer.SAMPLE))
            full = false;

        DataSourceContainer dataSourceContainer;
        try {
            dataSourceContainer = dataManager.getDataSourceContainer(datasetId);
        } catch (DocumentException | IOException e) {
            throw new InternalServerErrorException("Error in server : " + e.getMessage());
        }

        if (dataSourceContainer != null) {
            DataSource dataSource = dataSourceContainer.getDataSource();

            int oldValue = dataSource.getMaxRecord4Sample();
            try {
                dataManager.startIngestDataSource(datasetId, full, oldValue != -1);
            } catch (SecurityException | NoSuchMethodException | ClassNotFoundException | DocumentException | IOException | ParseException e) {
                throw new InternalServerErrorException("Error in server : " + e.getMessage());
            } catch (AlreadyExistsException e) {
                throw new AlreadyExistsException("Already exists: " + e.getMessage());
            } catch (ObjectNotFoundException e) {
                throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
            }
        }
        else
            throw new DoesNotExistException("Dataset with id " + datasetId + " does NOT exist!");

        return Response.status(200)
                .entity(new Result("Harvest(" + (full ? HarvestOptionListContainer.FULL : HarvestOptionListContainer.SAMPLE) + ") of dataset with id " + datasetId + " started!")).build();
    }

    /**
     * Cancels a harvesting ingest.
     * Relative path : /datasets/{datasetId}/harvest/cancel 
     * @param datasetId 
     * @return OK or Error Message
     * @throws DoesNotExistException 
     */
    @DELETE
    @Path("/" + DatasetOptionListContainer.DATASETID + "/" + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.CANCEL)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Cancels a harvesting ingest.", httpMethod = "DELETE", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a String message)"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response cancelHarvest(@ApiParam(value = "Id of dataset", required = true) @PathParam("datasetId") String datasetId) throws DoesNotExistException {

        try {
            dataManager.stopIngestDataSource(datasetId, Task.Status.CANCELED);
        } catch (NoSuchMethodException | ClassNotFoundException | DocumentException | IOException | ParseException e) {
            throw new InternalServerErrorException("Error in server : " + e.getMessage());
        } catch (ObjectNotFoundException e) {
            throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
        }

        return Response.status(200).entity(new Result("Ingest of dataset with id " + datasetId + " cancelled!")).build();
    }

    /**
     * Schedules an automatic harvesting.
     * Relative path : /datasets/{datasetId}/harvest/schedule 
     * @param datasetId 
     * @param task 
     * @param incremental 
     * @return OK or Error Message
     * @throws MissingArgumentsException 
     * @throws DoesNotExistException 
     * @throws AlreadyExistsException 
     */
    @POST
    @Path("/" + DatasetOptionListContainer.DATASETID + "/" + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.SCHEDULE)
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Schedules an automatic harvesting.", httpMethod = "POST", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created (Response containing a String message)"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 406, message = "MissingArgumentsException"),
            @ApiResponse(code = 409, message = "AlreadyExistsException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response scheduleHarvest(@ApiParam(value = "Id of dataset", required = true) @PathParam("datasetId") String datasetId, @ApiParam(value = "Task", required = true) Task task,
            @ApiParam(value = "true|false") @DefaultValue("false") @QueryParam("incremental") boolean incremental)
            throws MissingArgumentsException, DoesNotExistException, AlreadyExistsException {

        DataSourceContainer dataSourceContainer;
        try {
            dataSourceContainer = dataManager.getDataSourceContainer(datasetId);
        } catch (DocumentException | IOException e) {
            throw new InternalServerErrorException("Error in server : " + e.getMessage());
        }

        if (dataSourceContainer != null) {
            DataSource dataSource = dataSourceContainer.getDataSource();
            if (task instanceof ScheduledTask)
            {
                ScheduledTask scheduledTask = (ScheduledTask)task;
                Calendar firstRun = scheduledTask.getFirstRun();
                Frequency frequency = scheduledTask.getFrequency();
                String xmonths = "";
                if (scheduledTask.getXmonths() != null)
                    xmonths = Integer.toString(scheduledTask.getXmonths());

                if (firstRun == null)
                    throw new MissingArgumentsException("Missing value: " + "Date or are time must not be empty");
                else if (frequency == null)
                    throw new MissingArgumentsException("Missing value: " + "frequency must not be empty");
                else if ((frequency == Frequency.XMONTHLY) && (xmonths == null || xmonths.isEmpty()))
                    throw new MissingArgumentsException("Missing value: " + "xmonths must not be empty");

                String newTaskId;
                try {
                    newTaskId = dataSource.getNewTaskId();
                    scheduledTask.setId(newTaskId);
                } catch (IOException e) {
                    throw new InternalServerErrorException("Error in server : " + e.getMessage());
                }

                scheduledTask.setTaskClass(IngestDataSource.class);
                String[] parameters = new String[] { newTaskId, dataSource.getId(), (Boolean.valueOf(!incremental)).toString() };
                scheduledTask.setParameters(parameters);

                try {
                    if (taskManager.taskAlreadyExists(dataSource.getId(), DateUtil.date2String(scheduledTask.getFirstRun().getTime(), TimeUtil.LONG_DATE_FORMAT_NO_SECS), scheduledTask.getFrequency())) {
                        throw new AlreadyExistsException("Already exists: " + "Task already exists!");
                    }
                    else {
                        dataSource.setMaxRecord4Sample(-1);
                        dataManager.setDataSetSampleState(false, dataSource); //Set dataset to isSample false, a scheduled dataset harvest is not a sample anymore
                        taskManager.saveTask(scheduledTask);
                    }
                } catch (IOException e) {
                    throw new InternalServerErrorException("Error in server : " + e.getMessage());
                }

                return Response.status(201).entity(new Result("Task for dataset with id " + datasetId + " created!")).build();
            }
            else
                return Response.status(500).entity(new Result("Invalid task instance in body!")).build();
        }
        else
            throw new DoesNotExistException("Dataset with id " + datasetId + " does NOT exist!");
    }

    /**
     * Retrieves the list of schedules.
     * Relative path : /datasets/{datasetId}/harvest/schedules 
     * @param datasetId 
     * @return XML, JSON: scheduled harvest tasks
     * @throws DoesNotExistException 
     */
    @GET
    @Path("/" + DatasetOptionListContainer.DATASETID + "/" + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.SCHEDULES)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Retrieves the list of schedules.", httpMethod = "GET", response = ScheduledTask.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing an Dataset)"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response getDatasetScheduledTasks(@ApiParam(value = "Id of dataset", required = true) @PathParam("datasetId") String datasetId) throws DoesNotExistException {

        DataSourceContainer dataSourceContainer;
        try {
            dataSourceContainer = dataManager.getDataSourceContainer(datasetId);
            if (dataSourceContainer == null)
                throw new DoesNotExistException("Dataset with id " + datasetId + " does NOT exist!");
        } catch (DocumentException | IOException e) {
            throw new InternalServerErrorException("Error in server : " + e.getMessage());
        }

        List<ScheduledTask> scheduledTasks = null;
        scheduledTasks = taskManager.getDataSourceTasks(datasetId);

        return Response.status(200).entity(new GenericEntity<List<ScheduledTask>>(scheduledTasks) {
        }).build();
    }

    /**
     * Deletes an automatic harvesting.
     * Relative path : /datasets/{datasetId}/harvest/schedules/{taskId}
     * @param datasetId 
     * @param taskId 
     * @return OK or Error Message 
     * @throws DoesNotExistException 
     */
    @DELETE
    @Path("/" + DatasetOptionListContainer.DATASETID + "/" + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.SCHEDULES + "/" + HarvestOptionListContainer.TASKID)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Deletes an automatic harvesting.", httpMethod = "DELETE", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a String message)"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response deleteScheduledTask(@ApiParam(value = "Id of dataset", required = true) @PathParam("datasetId") String datasetId,
            @ApiParam(value = "Id of task", required = true) @PathParam("taskId") String taskId) throws DoesNotExistException {

        DataSourceContainer dataSourceContainer;
        try {
            dataSourceContainer = dataManager.getDataSourceContainer(datasetId);
            if (dataSourceContainer == null)
                throw new DoesNotExistException("Dataset with id " + datasetId + " does NOT exist!");
        } catch (DocumentException | IOException e) {
            throw new InternalServerErrorException("Error in server : " + e.getMessage());
        }

        if (taskManager.getTask(taskId) == null)
            throw new DoesNotExistException("Does NOT exist: " + "Task with id " + taskId + " does NOT exist!");

        try {
            if (taskManager.deleteTask(taskId))
                return Response.status(200).entity(new Result("Task with id " + taskId + " of dataset with id " + datasetId + " deleted!")).build();
            else
                throw new InternalServerErrorException("Error in server : " + "Could NOT delete task with id " + taskId);

        } catch (IOException e) {
            throw new InternalServerErrorException("Error in server : " + e.getMessage());
        }
    }

    /**
     * Gets the status of a specific dataset harvesting.
     * Relative path : /datasets/{datasetId}/harvest/status 
     * @param datasetId 
     * @return Status of the harvesting task
     * @throws DoesNotExistException 
     */
    @GET
    @Path("/" + DatasetOptionListContainer.DATASETID + "/" + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.STATUS)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Gets the status of a specific dataset harvesting.", httpMethod = "GET", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing an Dataset)"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response getDatasetHarvestingStatus(@ApiParam(value = "Id of dataset", required = true) @PathParam("datasetId") String datasetId) throws DoesNotExistException {

        DataSourceContainer dataSourceContainer;
        try {
            dataSourceContainer = dataManager.getDataSourceContainer(datasetId);
            if (dataSourceContainer == null)
                throw new DoesNotExistException("Dataset with id " + datasetId + " does NOT exist!");
        } catch (DocumentException | IOException e) {
            throw new InternalServerErrorException("Error in server : " + e.getMessage());
        }

        DataSource dataSource = dataSourceContainer.getDataSource();
        if (dataSource == null)
            throw new DoesNotExistException("Dataset with id " + datasetId + " does NOT exist!");

        return Response.status(200).entity(new Result(dataSource.getStatus().toString())).build();
    }

    /**
     * Gets the logs of the last ingest.
     * Relative path : /datasets/{datasetId}/harvest/log 
     * @param datasetId 
     * @return Log of last harvest
     * @throws DoesNotExistException 
     */
    @GET
    @Path("/" + DatasetOptionListContainer.DATASETID + "/" + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.LOG)
    @Produces({ MediaType.TEXT_PLAIN })
    @ApiOperation(value = "Gets the logs of the last ingest.", httpMethod = "GET", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing an Dataset)"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response getDatasetLastIngestLog(@ApiParam(value = "Id of dataset", required = true) @PathParam("datasetId") String datasetId) throws DoesNotExistException {

        DataSourceContainer dataSourceContainer;
        try {
            dataSourceContainer = dataManager.getDataSourceContainer(datasetId);
            if (dataSourceContainer == null)
                return Response.status(404).entity("Dataset with id " + datasetId + " does NOT exist!").build();
            //                throw new DoesNotExistException("Dataset with id " + datasetId + " does NOT exist!");
        } catch (DocumentException | IOException e) {
            return Response.status(500).entity("Error in server : " + e.getMessage()).build();
            //            throw new InternalServerErrorException("Error in server : " + e.getMessage());
        }

        DataSource dataSource = dataSourceContainer.getDataSource();
        if (dataSource == null)
            return Response.status(404).entity("Dataset with id " + datasetId + " does NOT exist!").build();
        //            throw new DoesNotExistException("Dataset with id " + datasetId + " does NOT exist!");

        String log = "";
        try {
            log = dataSource.getLastLogDataSource();
        } catch (ObjectNotFoundException e) {
            return Response.status(404).entity("Log of dataset with id " + datasetId + " does NOT exist!").build();
            //            throw new DoesNotExistException("Log of dataset with id " + datasetId + " does NOT exist!");
        } catch (IOException e) {
            return Response.status(500).entity("Error in server : " + e.getMessage()).build();
            //            throw new InternalServerErrorException("Error in server : " + e.getMessage());
        }

        return Response.status(200).entity(log).build();
    }
    
    /**
     * Gets a list of currently executing dataset harvests.
     * Relative path : /datasets/harvests 
     * @return List of currently executing dataset harvests.
     */
    @GET
    @Path("/" + HarvestOptionListContainer.HARVESTS)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Gets a list of currently executing dataset harvests", httpMethod = "GET", response = Task.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing an Dataset)"),
//            @ApiResponse(code = 404, message = "DoesNotExistException")
    })
    public Response getCurrentHarvestsList() {
        List<Task> runningTasks = taskManager.getRunningTasks();
        return Response.status(200).entity(new GenericEntity<List<Task>>(runningTasks) {
        }).build();
    }

}
