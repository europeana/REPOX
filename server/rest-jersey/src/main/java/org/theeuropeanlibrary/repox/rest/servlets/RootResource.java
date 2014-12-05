/* RootResource.java - created on Oct 15, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.theeuropeanlibrary.repox.rest.pathOptions.RootOptionListContainer;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Root context path handling.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 15, 2014
 */
@Path("/" + RootOptionListContainer.OPTIONS)
@Api(value = "/" + RootOptionListContainer.OPTIONS, description = "Rest api for options context")
public class RootResource {
    @Context
    UriInfo uriInfo;

    /**
     * Retrieve all the available options for all the OPTIONS supported.
     * Relative path : /
     * @return the list of the options available wrapped in a container
     */
    @OPTIONS
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get options over all the conexts.", httpMethod = "OPTIONS", response = RootOptionListContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)")
          })
    public RootOptionListContainer getOptions() {
        RootOptionListContainer rootOptionListContainer = new RootOptionListContainer(uriInfo.getBaseUri());
        return rootOptionListContainer;
    }
    
    /**
     * Retrieve all the available options for all the OPTIONS supported(For browser visibility).
     * Relative path : /
     * @return the list of the options available wrapped in a container
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get options over all the conexts.", httpMethod = "GET", response = RootOptionListContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)")
          })
    public RootOptionListContainer getGETOptions() {
        return getOptions();
    }
}
