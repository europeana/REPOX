/* AggregatorsTest.java - created on Oct 9, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.tel.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;

/**
 * 
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 9, 2014
 */
public class AggregatorsTest extends JerseyTest{

    
    
    public AggregatorsTest()throws Exception {
        super("org.tel.servlets");
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
     * Test method for {@link org.tel.servlets.Aggregators#getAggregator(java.lang.String)}.
     */
    @Test
    public final void testGetAggregator() {
        String aggregatorId = "10";
        
        WebResource webResource = resource();
        String responseMsg = webResource.path("/aggregators/" + aggregatorId).get(String.class);
        System.out.println("ss " + responseMsg);
        assertEquals("Aggregator" + aggregatorId, responseMsg);
    }

}
