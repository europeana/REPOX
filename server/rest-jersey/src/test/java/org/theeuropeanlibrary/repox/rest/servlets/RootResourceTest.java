/* RootResourceTest.java - created on Oct 15, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Test;
import org.theeuropeanlibrary.repox.rest.configuration.JerseyConfig;
import org.theeuropeanlibrary.repox.rest.pathOptions.RootOptionListContainer;

/**
 * 
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 15, 2014
 */
public class RootResourceTest extends JerseyTest  {

    
    /**
     * Initialize with the configuration
     */
    public RootResourceTest() {
        super(new JerseyConfig());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.RootResource#getOptions()}.
     */
    @Test
    public final void testGetOptions() {
        int numberOfAvailableOptions = 1;
        Response response = target("/").request(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatus());
        RootOptionListContainer rolc = response.readEntity(RootOptionListContainer.class);
        //Check the number of options provided
        assertEquals(numberOfAvailableOptions, rolc.getOptionList().size());
    }

}
