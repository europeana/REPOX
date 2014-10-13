/* Aggregators.java - created on Oct 9, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.dom4j.Element;
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

@Path("/aggregators")
public class Aggregators {
    @Context
    UriInfo uriInfo;

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
     * @return
     * @throws AggregatorDoesNotExistException 
     */
    @GET
    @Path("/{aggregatorId}")
    @Produces({ MediaType.APPLICATION_XML })
    public String getAggregator(@PathParam("aggregatorId") String aggregatorId) throws AggregatorDoesNotExistException {
        
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());    
        
        Aggregator aggregator = null;
        Element aggregatorsElement = null;
        try {
            aggregator = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).getAggregator(aggregatorId);
            if(aggregator != null){
                aggregatorsElement = aggregator.createElement(false);
//                System.out.println(aggregatorsElement.asXML());
            }
            else{
                throw new WebApplicationException(300);
//                throw new AggregatorDoesNotExistException("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            }
        }
        catch (Exception e) {
            throw new WebApplicationException(300);
//            throw new AggregatorDoesNotExistException("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
//            System.out.println();
        }
        
        
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//      System.out.println(gson.toJson(aggregator));
        
        
        return aggregatorsElement.asXML();
//        return "Aggregator" + aggregatorId;
    }
}
