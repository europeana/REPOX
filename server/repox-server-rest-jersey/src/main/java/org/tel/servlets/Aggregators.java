/* Aggregators.java - created on Oct 9, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.tel.servlets;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
/**
 * 
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 9, 2014
 */

@Path("/aggregators")
public class Aggregators {
    
    // This method is called if TEXT_PLAIN is request
    @GET
    @Path("/{aggregatorId}")
    @Produces({MediaType.TEXT_PLAIN})
    public String getAggregator(@PathParam("aggregatorId") String aggregatorId) {
      return "Aggregator" + aggregatorId;
    }
}
