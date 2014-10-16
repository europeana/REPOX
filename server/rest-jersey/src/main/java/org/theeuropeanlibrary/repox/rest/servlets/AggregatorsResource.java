/* Aggregators.java - created on Oct 9, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.theeuropeanlibrary.repox.rest.pathOptions.AggregatorOptionListContainer;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.Aggregator;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.util.exceptions.AggregatorDoesNotExistException;

/**
 * Aggregators context path handling.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 9, 2014
 */

@Path("/" + AggregatorOptionListContainer.AGGREGATORS)
public class AggregatorsResource {
    @Context
    UriInfo uriInfo;
    
    public DefaultDataManager dataManager;
    
    /**
     * Initialize fields before serving.
     */
    public AggregatorsResource()
    {
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
     * @throws AggregatorDoesNotExistException 
     */
    @GET
    @Path("/" + AggregatorOptionListContainer.AGGREGATORID)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Aggregator getAggregator(@PathParam("aggregatorId") String aggregatorId) throws AggregatorDoesNotExistException {
        Aggregator aggregator = null;
        aggregator = dataManager.getAggregator(aggregatorId);
        if (aggregator == null)
            throw new AggregatorDoesNotExistException("Aggregator does NOT exist!");

        return aggregator;
    }
    
    
    
    
    
    
    
    
    
    

    @GET
    @Produces({ MediaType.TEXT_PLAIN })
    public String test() throws AggregatorDoesNotExistException {
        throw new AggregatorDoesNotExistException("AAAAA");

    }
}
