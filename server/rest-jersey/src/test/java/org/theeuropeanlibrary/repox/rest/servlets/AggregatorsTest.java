/* AggregatorsTest.java - created on Oct 9, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import static org.junit.Assert.assertEquals;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 9, 2014
 */
public class AggregatorsTest extends JerseyTest{
    public AggregatorsTest()throws Exception {
        super(new ResourceConfig().packages("org.tel.servlets"));
    }
   
    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.Aggregators#getAggregator(java.lang.String)}.
     */
    @Test
    public final void testGetAggregator() {
        String aggregatorId = "10";
        final String responseMsg = target("/aggregators/" + aggregatorId).request().get(String.class);
        assertEquals("Aggregator" + aggregatorId, responseMsg);
    }

}
