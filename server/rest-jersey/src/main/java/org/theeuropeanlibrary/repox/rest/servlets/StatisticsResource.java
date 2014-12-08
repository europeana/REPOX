package org.theeuropeanlibrary.repox.rest.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.theeuropeanlibrary.repox.rest.pathOptions.StatisticsOptionListContainer;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.statistics.DefaultStatisticsManager;
import pt.utl.ist.statistics.RepoxStatistics;
import pt.utl.ist.statistics.StatisticsManager;

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
	UriInfo uriInfo;

	public StatisticsManager statisticsManager;

	/**
	 * Initialize fields before serving.
	 */
	public StatisticsResource() {
		ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
		statisticsManager = ConfigSingleton.getRepoxContextUtil()
				.getRepoxManager().getStatisticsManager();
	}

	/**
	 * Creates a new instance by providing the DataManager. (For Tests)
	 * 
	 * @param dataManager
	 * @param urn
	 */
	public StatisticsResource(StatisticsManager statisticsManager) {
		super();
		this.statisticsManager = statisticsManager;
	}

	/**
	 * Retrieve all the available options for Statistics. Relative path :
	 * /statistics
	 * 
	 * @return the list of the options available wrapped in a container
	 */
	@OPTIONS
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get options over statistics conext.", httpMethod = "OPTIONS", response = StatisticsOptionListContainer.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)") })
	public StatisticsOptionListContainer getOptions() {
		StatisticsOptionListContainer statisticsOptionListContainer = new StatisticsOptionListContainer(
				uriInfo.getBaseUri());
		return statisticsOptionListContainer;
	}

	/**
	 * Retrieve all the available options for Statistics. Relative path :
	 * /statistics/options
	 * 
	 * @return the list of the options available wrapped in a container
	 */
	@GET
	@Path("/" + StatisticsOptionListContainer.OPTIONS)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get options over statistics conext.", httpMethod = "GET", response = StatisticsOptionListContainer.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)") })
	public StatisticsOptionListContainer getGETOptions() {
		StatisticsOptionListContainer statisticsOptionListContainer = new StatisticsOptionListContainer(
				uriInfo.getBaseUri());
		return statisticsOptionListContainer;
	}

	/**
	 * Retrieve the statistics. 
	 * Relative path : /statistics
	 * 
	 * @return the list of the options available wrapped in a container
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML })
	@ApiOperation(value = "Retrieve the statistics.", httpMethod = "GET", response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK (Response containing a String message)"),
			@ApiResponse(code = 500, message = "InternalServerErrorException")
			})
	public Response getStatistics() throws InternalServerErrorException{
		RepoxStatistics statistics;
		Document statisticsReport;
		try {
			statistics = statisticsManager.generateStatistics(null);
			statisticsReport = statisticsManager
					.getStatisticsReport(statistics);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (statisticsReport != null) {
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
						baos, "UTF-8");
				OutputFormat format = OutputFormat.createPrettyPrint();
				XMLWriter writer = new XMLWriter(outputStreamWriter, format);
				writer.write(statisticsReport);
				writer.close();
			}
			return Response.status(200).entity(baos.toString("UTF-8")).build();
		} catch (IOException | DocumentException | SQLException e) {
			throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
		}
	}
}
