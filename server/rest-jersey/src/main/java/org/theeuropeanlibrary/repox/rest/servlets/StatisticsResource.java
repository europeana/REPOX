package org.theeuropeanlibrary.repox.rest.servlets;

import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.theeuropeanlibrary.repox.rest.pathOptions.RecordOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.StatisticsOptionListContainer;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.util.Urn;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Statistics context path handling.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Dec 8, 2014
 */
@Path("/" + StatisticsOptionListContainer.STATISTICS)
@Api(value = "/" + StatisticsOptionListContainer.STATISTICS, description = "Rest api for statistics context")
public class StatisticsResource {
	@Context
    UriInfo                   uriInfo;

    public DefaultDataManager dataManager;

    /**
     * Initialize fields before serving.
     */
    public StatisticsResource() {
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
        dataManager = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager());
    }

    /**
     * Creates a new instance by providing the DataManager. (For Tests)
     * @param dataManager
     * @param urn 
     */
    public StatisticsResource(DefaultDataManager dataManager) {
        super();
        this.dataManager = dataManager;
    }
    
    /**
     * Retrieve all the available options for Statistics.
     * Relative path : /statistics
     * @return the list of the options available wrapped in a container
     */
    @OPTIONS
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get options over statistics conext.", httpMethod = "OPTIONS", response = StatisticsOptionListContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)")
    })
    public StatisticsOptionListContainer getOptions() {
    	StatisticsOptionListContainer statisticsOptionListContainer = new StatisticsOptionListContainer(uriInfo.getBaseUri());
        return statisticsOptionListContainer;
    }
}
