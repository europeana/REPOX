/* DatasetsResource.java - created on Oct 30, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.dom4j.DocumentException;
import org.theeuropeanlibrary.repox.rest.pathOptions.DatasetOptionListContainer;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.util.exceptions.DoesNotExistException;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Datasets context path handling.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 30, 2014
 */
@Path("/" + DatasetOptionListContainer.DATASETS)
@Api(value = "/" + DatasetOptionListContainer.DATASETS, description = "Rest api for datasets")
public class DatasetsResource {
    @Context
    UriInfo                   uriInfo;
    
    public DefaultDataManager dataManager;

    /**
     * Initialize fields before serving.
     */
    public DatasetsResource() {
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
        dataManager = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager());
    }
    
    /**
     * Creates a new instance by providing the DataManager. (For Tests)
     * @param dataManager
     */
    public DatasetsResource(DefaultDataManager dataManager) {
        super();
        this.dataManager = dataManager;
    }
    
    /**
     * Retrieve all the available options for Datasets.
     * Relative path : /datasets
     * @return the list of the options available wrapped in a container
     */
    @OPTIONS
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get options over datasets conext.", httpMethod = "OPTIONS", response = DatasetOptionListContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)") })
    public DatasetOptionListContainer getOptions() {
        DatasetOptionListContainer datasetOptionListContainer = new DatasetOptionListContainer(uriInfo.getBaseUri());
        return datasetOptionListContainer;
    }
    
    /**
     * Retrieve the dataset with the provided id.
     * Relative path : /datasets/{datasetId} 
     * @param datasetId 
     * @return Provider information
     * @throws DoesNotExistException 
     * @throws IOException 
     * @throws DocumentException 
     */
    @GET
    @Path("/" + DatasetOptionListContainer.DATASETID)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get specific dataset.", httpMethod = "GET", response = DataSource.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing an Dataset)"),
            @ApiResponse(code = 404, message = "DoesNotExistException") })
    public DataSource getDataset(@ApiParam(value = "Id of dataset", required = true) @PathParam("datasetId") String datasetId) throws DoesNotExistException, DocumentException, IOException {
        
        DataSourceContainer datasourceContainer = null;
        datasourceContainer = dataManager.getDataSourceContainer(datasetId);
        if (datasourceContainer == null)
            throw new DoesNotExistException("Dataset with id " + datasetId + " does NOT exist!");

        return datasourceContainer.getDataSource();
    }

}
