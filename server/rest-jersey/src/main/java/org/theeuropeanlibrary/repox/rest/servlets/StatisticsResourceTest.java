package org.theeuropeanlibrary.repox.rest.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import org.theeuropeanlibrary.repox.rest.configuration.JerseyConfigMocked;
import org.theeuropeanlibrary.repox.rest.pathOptions.RecordOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.StatisticsOptionListContainer;

import pt.utl.ist.dataProvider.DefaultDataManager;

/**
 * Statistics context path handling tests.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Dec 8, 2014
 */
public class StatisticsResourceTest extends JerseyTest  {
	DefaultDataManager dataManager;

    public StatisticsResourceTest() throws Exception {
        super(new JerseyConfigMocked());
        dataManager = JerseyConfigMocked.getDataManager();
    }

    @Before
    public void setUpBeforeMethod() throws Exception {
        //Reset mock before every test
        reset(dataManager);
    }
    
    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.StatisticsResource#getOptions()}.
     */
    @Test
//    @Ignore
    public void testGetOptions() {
        int numberOfAvailableOptions = 1;
        WebTarget target = target("/" + StatisticsOptionListContainer.STATISTICS);

        //Check xml options working
        Response response = target.request(MediaType.APPLICATION_XML).options();
        assertEquals(200, response.getStatus());
        //Check json options working
        response = target.request(MediaType.APPLICATION_JSON).options();
        assertEquals(200, response.getStatus());
        StatisticsOptionListContainer solc = response.readEntity(StatisticsOptionListContainer.class);
        //Check the number of options provided
        assertEquals(numberOfAvailableOptions, solc.getOptionList().size());
    }
}
