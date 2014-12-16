/* Aggregators.java - created on Oct 9, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import java.io.IOException;
import java.util.List;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.annotation.Secured;
import org.theeuropeanlibrary.repox.rest.pathOptions.AggregatorOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.Aggregator;
import pt.utl.ist.dataProvider.DefaultDataManager;
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
 * Aggregators context path handling.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 9, 2014
 */
@Path("/" + AggregatorOptionListContainer.AGGREGATORS)
@Api(value = "/" + AggregatorOptionListContainer.AGGREGATORS, description = "Rest api for aggregators")
public class AggregatorsResource {
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Context
    UriInfo                   uriInfo;

    public DefaultDataManager dataManager;

    /**
     * Initialize fields before serving.
     */
    public AggregatorsResource() {
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
        dataManager = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager());
    }

    /**
     * Creates a new instance by providing the DataManager. (For Tests)
     * @param dataManager
     */
    public AggregatorsResource(DefaultDataManager dataManager) {
        super();
        this.dataManager = dataManager;
    }

    /**
     * Retrieve all the available options for Aggregators.
     * Relative path : /aggregators
     * @return the list of the options available wrapped in a container
     */
    @OPTIONS
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get options over aggregator conext.", httpMethod = "OPTIONS", response = AggregatorOptionListContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)")
    })
    public AggregatorOptionListContainer getOptions() {
        AggregatorOptionListContainer aggregatorOptionListContainer = new AggregatorOptionListContainer(uriInfo.getBaseUri());
        return aggregatorOptionListContainer;
    }

    /**
    * Retrieve all the available options for Aggregators(For browser visibility).
    * Relative path : /aggregators
    * @return the list of the options available wrapped in a container
    */
    @GET
    @Path("/" + AggregatorOptionListContainer.OPTIONS)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get options over aggregator conext.", httpMethod = "GET", response = AggregatorOptionListContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)")
    })
    public AggregatorOptionListContainer getGETOptions() {
        return getOptions();
    }

    /**
     * Retrieve the aggregator with the provided id.
     * Relative path : /aggregators/{aggregatorId}
     * @param aggregatorId
     * @return Aggregator information
     * @throws DoesNotExistException 
     */
    @GET
    @Path("/" + AggregatorOptionListContainer.AGGREGATORID)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get specific aggregator.", httpMethod = "GET", response = Aggregator.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing an Aggregator)"),
            @ApiResponse(code = 404, message = "DoesNotExistException")
    })
    public Aggregator getAggregator(@ApiParam(value = "Id of aggregator", required = true) @PathParam("aggregatorId") String aggregatorId) throws DoesNotExistException {
        Aggregator aggregator = null;
        aggregator = dataManager.getAggregator(aggregatorId);
        if (aggregator == null)
            throw new DoesNotExistException("Aggregator does NOT exist!");

        return aggregator;
    }

    /**
     * Create an aggregator provided in the body of the post call.
     * Relative path : /aggregators
     * @param aggregator 
     * @return OK or Error Message
     * @throws MissingArgumentsException 
     * @throws AlreadyExistsException 
     * @throws InvalidArgumentsException 
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Create an aggregator.", httpMethod = "POST", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created (Response containing a String message)"),
            @ApiResponse(code = 400, message = "InvalidArgumentsException"),
            @ApiResponse(code = 406, message = "MissingArgumentsException"),
            @ApiResponse(code = 409, message = "AlreadyExistsException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response createAggregator(@ApiParam(value = "Aggregator data", required = true) Aggregator aggregator) throws MissingArgumentsException, AlreadyExistsException, InvalidArgumentsException {
        //        if(aggregator.getId() != null)
        //            throw new InvalidArgumentsException("Invalid value: " + "Aggregator Id provided in body must be null");

        Aggregator createdAggregator = null;
        if (aggregator.getName() != null && !aggregator.getName().isEmpty()) {
            try {
                createdAggregator = dataManager.createAggregator(aggregator.getId(), aggregator.getName(), aggregator.getNameCode(), aggregator.getHomepage().toString());
            } catch (DocumentException | IOException e) {
                throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
            } catch (InvalidArgumentsException e) { //This happens when the URL is invalid
                throw new InvalidArgumentsException("Invalid argument: " + e.getMessage());
            } catch (AlreadyExistsException e) { //This basically happens if and aggregator already exists with the same Id 
                throw new AlreadyExistsException("Already exists: " + e.getMessage());
            }
        } else
            throw new MissingArgumentsException("Missing argument name!");
        return Response.created(null)
                .entity(new Result("Aggregator with id = " + createdAggregator.getId() + " and name = " + createdAggregator.getName() + " created successfully"))
                .build();
    }

    /**
     * Delete an aggregator by specifying the Id.
     * Relative path : /aggregators/{aggregatorId}
     * @param aggregatorId 
     * @return OK or Error Message
     * @throws DoesNotExistException 
     */
    @DELETE
    @Path("/" + AggregatorOptionListContainer.AGGREGATORID)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @ApiOperation(value = "Delete an aggregator.", httpMethod = "DELETE", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a String message)"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response deleteAggregator(@ApiParam(value = "Id of aggregator", required = true) @PathParam("aggregatorId") String aggregatorId) throws DoesNotExistException {

        try {
            dataManager.deleteAggregator(aggregatorId);
        } catch (DocumentException | IOException e) {
            throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
        } catch (ObjectNotFoundException e) {
            throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
        }

        return Response.status(200).entity(new Result("Aggregator with id " + aggregatorId + " deleted!")).build();
    }

    /**
     * Update an aggregator by specifying the Id.
     * The Id of the aggregator is provided as a path parameter and in request body there is an aggregator with the updates that are requested(id, name, nameCode, homePage) the remaining fields of the Aggregator class provided can be null
     * Relative path : /aggregators/{aggregatorId}
     * @param aggregatorId 
     * @param aggregator 
     * @return OK or Error Message
     * @throws DoesNotExistException 
     * @throws InvalidArgumentsException 
     * @throws MissingArgumentsException 
     * @throws AlreadyExistsException 
     */
    @PUT
    @Path("/" + AggregatorOptionListContainer.AGGREGATORID)
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Update an aggregator.", httpMethod = "PUT", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a String message)"),
            @ApiResponse(code = 400, message = "InvalidArgumentsException"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 406, message = "MissingArgumentsException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response updateAggregator(@ApiParam(value = "Id of aggregator", required = true) @PathParam("aggregatorId") String aggregatorId,
            @ApiParam(value = "Aggregator data", required = true) Aggregator aggregator) throws DoesNotExistException,
            InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException {

        //        if(aggregator.getId() != null)
        //            throw new InvalidArgumentsException("Invalid value: " + "Aggregator Id provided in body must be null");
        String newAggregatorId = aggregator.getId();
        String name = aggregator.getName();
        String nameCode = aggregator.getNameCode();
        String homePage = aggregator.getHomepage();

        if (aggregator.getName() != null && !aggregator.getName().isEmpty()) {
            try {
                dataManager.updateAggregator(aggregatorId, newAggregatorId, name, nameCode, homePage);
            } catch (IOException e) {
                throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
            } catch (ObjectNotFoundException e) {
                throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
            } catch (InvalidArgumentsException e) { //This happens when the URL is invalid
                throw new InvalidArgumentsException("Invalid argument: " + e.getMessage());
            } catch (AlreadyExistsException e) {
                throw new AlreadyExistsException("Already exists: " + e.getMessage());
            }
        } else
            throw new MissingArgumentsException("Missing argument name!");

        if (newAggregatorId != null && !newAggregatorId.isEmpty() && !aggregatorId.equals(newAggregatorId))
            return Response.status(200).entity(new Result("Aggregator with id " + aggregatorId + " updated and has now id : " + newAggregatorId + "!")).build();
        else
            return Response.status(200).entity(new Result("Aggregator with id " + aggregatorId + " updated!")).build();

    }

    /**
     * Get a list of aggregators in the specified range.
     * Offset not allowed negative. If number is negative then it returns all the items from offset until the total number of items.
     * Relative path : /aggregators
     * @param offset Query parameter on the context
     * @param number Query parameter on the context
     * @return the list of the number of aggregators requested
     * @throws Exception 
     * @throws InvalidArgumentsException 
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get a list of aggregators.", httpMethod = "GET", response = Aggregator.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of aggregators)"),
            @ApiResponse(code = 400, message = "InvalidArgumentsException")
    })
    public Response getAggregatorList(@ApiParam(value = "Index where to start from(negative not allowed)", defaultValue = "0") @DefaultValue("0") @QueryParam("offset") int offset,
            @ApiParam(value = "Number of aggregators requested(-1 to get all)", defaultValue = "-1") @DefaultValue("-1") @QueryParam("number") int number) throws Exception, InvalidArgumentsException {

        if (offset < 0)
            throw new InvalidArgumentsException("Offset negative values not allowed!");

        List<Aggregator> aggregatorsListSorted;
        try {
            aggregatorsListSorted = dataManager.getAggregatorsListSorted(offset, number);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidArgumentsException("Invalid argument: " + e.getMessage());
        }

        return Response.status(200).entity(new GenericEntity<List<Aggregator>>(aggregatorsListSorted) {
        }).build();
    }
}
