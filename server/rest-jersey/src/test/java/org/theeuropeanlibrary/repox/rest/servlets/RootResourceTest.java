/* RootResourceTest.java - created on Oct 15, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.theeuropeanlibrary.repox.rest.configuration.JerseyConfigMocked;
import org.theeuropeanlibrary.repox.rest.pathOptions.RootOptionListContainer;

/**
 * 
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 15, 2014
 */
public class RootResourceTest extends JerseyTest {

    /**
     * Initialize with the configuration
     * @throws MalformedURLException 
     */
    public RootResourceTest() throws MalformedURLException {
        super(new JerseyConfigMocked());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.RootResource#getOptions()}.
     */
    @Test
    public void testGetOptions() {
        int numberOfAvailableOptions = 2;
        WebTarget target = target("/" + RootOptionListContainer.OPTIONS);

        //Check xml options working
        Response response = target.request(MediaType.APPLICATION_XML).options();
        assertEquals(200, response.getStatus());
        //Check json options working
        response = target.request(MediaType.APPLICATION_JSON).options();
        assertEquals(200, response.getStatus());
        RootOptionListContainer rolc = response.readEntity(RootOptionListContainer.class);
        //Check the number of options provided
        assertEquals(numberOfAvailableOptions, rolc.getOptionList().size());
    }

}
