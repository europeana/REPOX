package org.theeuropeanlibrary.repox.rest.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import java.io.IOException;
import java.sql.SQLException;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import org.theeuropeanlibrary.repox.rest.configuration.JerseyConfigMocked;
import org.theeuropeanlibrary.repox.rest.pathOptions.StatisticsOptionListContainer;

import pt.utl.ist.statistics.DefaultRepoxStatistics;
import pt.utl.ist.statistics.StatisticsManager;

/**
 * Statistics context path handling tests.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Dec 8, 2014
 */
public class StatisticsResourceTest extends JerseyTest {
	StatisticsManager statisticsManager;

	public StatisticsResourceTest() throws Exception {
		super(new JerseyConfigMocked());
		statisticsManager = JerseyConfigMocked.getStatisticsManager();
	}

	@Before
	public void setUpBeforeMethod() throws Exception {
		// Reset mock before every test
		reset(statisticsManager);
	}

	/**
	 * Test method for
	 * {@link org.theeuropeanlibrary.repox.rest.servlets.StatisticsResource#getOptions()}
	 * .
	 */
	@Test
	// @Ignore
	public void testGetOptions() {
		int numberOfAvailableOptions = 3;
		WebTarget target = target("/"
				+ StatisticsOptionListContainer.STATISTICS);

		// Check xml options working
		Response response = target.request(MediaType.APPLICATION_XML).options();
		assertEquals(200, response.getStatus());
		// Check json options working
		response = target.request(MediaType.APPLICATION_JSON).options();
		assertEquals(200, response.getStatus());
		StatisticsOptionListContainer solc = response
				.readEntity(StatisticsOptionListContainer.class);
		// Check the number of options provided
		assertEquals(numberOfAvailableOptions, solc.getOptionList().size());
	}

	/**
	 * Test method for
	 * {@link org.theeuropeanlibrary.repox.rest.servlets.StatisticsResource#getGETOptions()}
	 * .
	 */
	@Test
	// @Ignore
	public void testGetGETOptions() {
		int numberOfAvailableOptions = 3;
		WebTarget target = target("/"
				+ StatisticsOptionListContainer.STATISTICS + "/" + StatisticsOptionListContainer.OPTIONS);

		// Check xml options working
		Response response = target.request(MediaType.APPLICATION_XML).get();
		assertEquals(200, response.getStatus());
		// Check json options working
		response = target.request(MediaType.APPLICATION_JSON).get();
		assertEquals(200, response.getStatus());
		StatisticsOptionListContainer solc = response
				.readEntity(StatisticsOptionListContainer.class);
		// Check the number of options provided
		assertEquals(numberOfAvailableOptions, solc.getOptionList().size());
	}
	
	/**
	 * Test method for
	 * {@link org.theeuropeanlibrary.repox.rest.servlets.StatisticsResource#getStatistics()}
	 * .
	 * @throws SQLException 
	 * @throws DocumentException 
	 * @throws IOException 
	 */
	@Test
	// @Ignore
	public void testGetStatistics() throws IOException, DocumentException, SQLException {
		WebTarget target = target("/"
				+ StatisticsOptionListContainer.STATISTICS);
		
		//Mocking
		DefaultRepoxStatistics defaultRepoxStatistics = any(DefaultRepoxStatistics.class);
		when(statisticsManager.generateStatistics(null)).thenThrow(new IOException()).thenReturn(defaultRepoxStatistics);
		Document document = DocumentHelper.createDocument();
        Element root = document.addElement("root");
        Element node = root.addElement("author")
                .addAttribute("name", "exampleName")
                .addText("Some Text");
		when(statisticsManager.getStatisticsReport(defaultRepoxStatistics)).thenThrow(new IOException()).thenReturn(document);
		
		//Internal Server Error
		Response response = target.request(MediaType.APPLICATION_XML).get();
		assertEquals(500, response.getStatus());
		//Internal Server Error, second mock
		response = target.request(MediaType.APPLICATION_XML).get();
		assertEquals(500, response.getStatus());
		//Valid call
		response = target.request(MediaType.APPLICATION_XML).get();
		assertEquals(200, response.getStatus());
	}
}
