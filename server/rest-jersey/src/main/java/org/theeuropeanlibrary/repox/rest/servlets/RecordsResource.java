/* RecordsResource.java - created on Dec 5, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.theeuropeanlibrary.repox.rest.pathOptions.RecordOptionListContainer;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DefaultDataManager;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Records context path handling.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Dec 5, 2014
 */
@Path("/" + RecordOptionListContainer.RECORDS)
@Api(value = "/" + RecordOptionListContainer.RECORDS, description = "Rest api for records context")
public class RecordsResource {
    @Context
    UriInfo                   uriInfo;

    public DefaultDataManager dataManager;
    
    /**
     * Initialize fields before serving.
     */
    public RecordsResource() {
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
        dataManager = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager());
    }

    /**
     * Creates a new instance by providing the DataManager. (For Tests)
     * @param dataManager
     */
    public RecordsResource(DefaultDataManager dataManager) {
        super();
        this.dataManager = dataManager;
    }
    
    /**
     * Retrieve all the available options for Records.
     * Relative path : /records
     * @return the list of the options available wrapped in a container
     */
    @OPTIONS
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get options over record conext.", httpMethod = "OPTIONS", response = RecordOptionListContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)")
    })
    public RecordOptionListContainer getOptions() {
        RecordOptionListContainer recordOptionListContainer = new RecordOptionListContainer(uriInfo.getBaseUri());
        return recordOptionListContainer;
    }

}
