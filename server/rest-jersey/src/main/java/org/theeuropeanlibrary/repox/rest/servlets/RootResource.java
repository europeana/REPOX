/* RootResource.java - created on Oct 15, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.theeuropeanlibrary.repox.rest.pathOptions.RootOptionListContainer;

/**
 * Root context path handling.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 15, 2014
 */
@Path("/")
public class RootResource {
    @Context
    UriInfo uriInfo;

    /**
     * Retrieve all the available options for all the OPTIONS supported.
     * Relative path : /
     * @return the list of the options available wrapped in a container
     */
    @OPTIONS
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public RootOptionListContainer getOptions() {
        RootOptionListContainer rootOptionListContainer = new RootOptionListContainer(uriInfo.getBaseUri());
        return rootOptionListContainer;
    }
}
