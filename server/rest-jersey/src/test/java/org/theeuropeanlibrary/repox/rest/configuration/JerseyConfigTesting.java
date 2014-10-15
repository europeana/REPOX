/* JerseyConfig.java - created on Oct 14, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.configuration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;

import org.glassfish.jersey.server.ResourceConfig;
import org.theeuropeanlibrary.repox.rest.exceptionMappers.AggregatorDoesNotExistExceptionMapper;
import org.theeuropeanlibrary.repox.rest.exceptionMappers.AggregatorExistExceptionMapper;
import org.theeuropeanlibrary.repox.rest.servlets.AggregatorsResource;

import pt.utl.ist.dataProvider.Aggregator;
import pt.utl.ist.dataProvider.DefaultDataManager;

/**
 * Register all the resources for the jersey configuration for Testing
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 14, 2014
 */
public class JerseyConfigTesting extends ResourceConfig {

    public static DefaultDataManager dataManager = mock(DefaultDataManager.class);

    /**
     * Creates a new instance of this class.
     * @throws MalformedURLException 
     */
    public JerseyConfigTesting() throws MalformedURLException {
        packages("org.theeuropeanlibrary.repox.rest.servlets");
        register(AggregatorDoesNotExistExceptionMapper.class);
        register(AggregatorExistExceptionMapper.class);

        //Register resource with mocks
        AggregatorsResource ar = new AggregatorsResource(dataManager);
        register(ar);
    }

}
