/* Aggregators.java - created on Oct 9, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.dom4j.DocumentException;
import org.theeuropeanlibrary.repox.rest.pathOptions.AggregatorOptionListContainer;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.Aggregator;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.dataProvider.MessageType;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

/**
 * Aggregators context path handling.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 9, 2014
 */

@Path("/" + AggregatorOptionListContainer.AGGREGATORS)
public class AggregatorsResource {
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
     * Creates a new instance of this class.
     * @param dataManager
     */
    public AggregatorsResource(DefaultDataManager dataManager) {
        super();
        this.dataManager = dataManager;
    }

    /**
     * Retrieve all the available options for Aggregators
     * Relative path : /aggregators
     * @return the list of the options available wrapped in a container
     */
    @OPTIONS
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public AggregatorOptionListContainer getOptions() {
        AggregatorOptionListContainer aggregatorOptionListContainer = new AggregatorOptionListContainer(uriInfo.getBaseUri());
        return aggregatorOptionListContainer;
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
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Aggregator getAggregator(@PathParam("aggregatorId") String aggregatorId) throws DoesNotExistException {
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
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response createAggregator(Aggregator aggregator) throws MissingArgumentsException, AlreadyExistsException, InvalidArgumentsException {
        Aggregator createdAggregator = null;
        if (aggregator.getName() != null && !aggregator.getName().isEmpty()) {
            try {
                createdAggregator = dataManager.createAggregator(aggregator.getName(), aggregator.getNameCode(), aggregator.getHomePage().toString());
            } catch (DocumentException | IOException e) {
                throw new InternalServerErrorException("Error in server : " + e.getMessage());
            } catch (InvalidArgumentsException e) { //This happens when the URL is invalid
                throw new InvalidArgumentsException("Invalid value: " + e.getMessage());
            } catch (AlreadyExistsException e) { //This basically happens if and aggregator already exists with both name and nameCode the same as the one provided 
                throw new AlreadyExistsException("Aggregator with name \"" + e.getMessage() + "\" already exists!");
            }
        } else
            throw new MissingArgumentsException("Missing argument name!");
        return Response.created(null).entity("Aggregator with name = " + createdAggregator.getName() + " and id = " + createdAggregator.getId() + " created successfully").build();
    }
    
    /**
     * Delete an aggregator by specifying the Id.
     * Relative path : /aggregators
     * @param aggregatorId 
     * @return OK or Error Message
     * @throws DoesNotExistException 
     */
    @DELETE
    @Path("/" + AggregatorOptionListContainer.AGGREGATORID)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response deleteAggregator(@PathParam("aggregatorId") String aggregatorId) throws DoesNotExistException{

        if(aggregatorId != null && !aggregatorId.isEmpty()){
            try {
                dataManager.deleteAggregator(aggregatorId);
            } catch (DocumentException | IOException e) {
                throw new InternalServerErrorException("Error in server : " + e.getMessage());
            } catch (ObjectNotFoundException e) {
                throw new DoesNotExistException("A resource of the aggregator or the aggregator itself with id \"" + e.getMessage() + "\" does NOT exist!");
            }
        }
        
        return Response.status(200).entity("Aggregator with id " + aggregatorId + " deleted!").build();
    }

    @GET
    @Produces({ MediaType.TEXT_PLAIN })
    public String test() throws DoesNotExistException {
        throw new DoesNotExistException("AAAAA");

    }
}
