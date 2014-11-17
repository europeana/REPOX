/* HarvestResource.java - created on Nov 17, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import java.io.IOException;
import java.text.ParseException;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.dom4j.DocumentException;
import org.theeuropeanlibrary.repox.rest.pathOptions.DatasetOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.HarvestOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.task.Task;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
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

    /**
     * Creates a new instance of this class.
     */
    public HarvestResource() {
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
        dataManager = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager());
    }

    /**
     * Creates a new instance by providing the DataManager. (For Tests)
     * @param dataManager
     */
    public HarvestResource(DefaultDataManager dataManager) {
        super();
        this.dataManager = dataManager;
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
            @ApiParam(value = "Initiate a new harvest.") @DefaultValue(HarvestOptionListContainer.SAMPLE) @QueryParam("type") String type) throws InternalServerErrorException, DoesNotExistException,
            AlreadyExistsException {

        boolean fullIngest = true;

        if (type == null || type.equals("") || (!type.equals(HarvestOptionListContainer.FULL) && !type.equals(HarvestOptionListContainer.SAMPLE)))
            fullIngest = false;
        else if (type.equals(HarvestOptionListContainer.SAMPLE))
            fullIngest = false;

        try {
            dataManager.startIngestDataSource(datasetId, fullIngest);
        } catch (SecurityException | NoSuchMethodException | ClassNotFoundException | DocumentException | IOException | ParseException e) {
            throw new InternalServerErrorException("Error in server : " + e.getMessage());
        } catch (AlreadyExistsException e) {
            throw new AlreadyExistsException("Already exists: " + e.getMessage());
        } catch (ObjectNotFoundException e) {
            throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
        }

        return Response.status(200)
                .entity(new Result("Harvest(" + (fullIngest ? HarvestOptionListContainer.FULL : HarvestOptionListContainer.SAMPLE) + ") of dataset with id " + datasetId + " started!")).build();
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

}
