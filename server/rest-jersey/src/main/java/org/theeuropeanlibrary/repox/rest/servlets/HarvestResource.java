/* HarvestResource.java - created on Nov 17, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.theeuropeanlibrary.repox.rest.pathOptions.DatasetOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.HarvestOptionListContainer;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DefaultDataManager;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Harvest context path handling.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Nov 17, 2014
 */
@Path("/" + DatasetOptionListContainer.DATASETS)
@Api(value = "/" + DatasetOptionListContainer.DATASETS, description = "Rest api for datasets")
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

}
