/* JerseyConfig.java - created on Oct 14, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.configuration;

import static org.mockito.Mockito.mock;

import java.net.MalformedURLException;

import org.glassfish.jersey.server.ResourceConfig;
import org.theeuropeanlibrary.repox.rest.exceptionMappers.DoesNotExistExceptionMapper;
import org.theeuropeanlibrary.repox.rest.exceptionMappers.AlreadyExistsExceptionMapper;
import org.theeuropeanlibrary.repox.rest.exceptionMappers.InternalServerErrorExceptionMapper;
import org.theeuropeanlibrary.repox.rest.exceptionMappers.InvalidArgumentsExceptionMapper;
import org.theeuropeanlibrary.repox.rest.exceptionMappers.MissingArgumentsExceptionMapper;
import org.theeuropeanlibrary.repox.rest.servlets.AggregatorsResource;

import pt.utl.ist.dataProvider.DefaultDataManager;

/**
 * Register all the resources for the jersey configuration for Testing
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 14, 2014
 */
public class JerseyConfigMocked extends ResourceConfig {

    private static DefaultDataManager dataManager = mock(DefaultDataManager.class);

    /**
     * Creates a new instance of this class.
     * @throws MalformedURLException 
     */
    public JerseyConfigMocked() throws MalformedURLException {
        packages("org.theeuropeanlibrary.repox.rest.servlets");
        //Exceptions
        register(DoesNotExistExceptionMapper.class);
        register(AlreadyExistsExceptionMapper.class);
        register(MissingArgumentsExceptionMapper.class);
        register(InvalidArgumentsExceptionMapper.class);
        register(InternalServerErrorExceptionMapper.class);

        //Register resource with mocks
        AggregatorsResource ar = new AggregatorsResource(dataManager);
        register(ar);
    }

    /**
     * Returns the dataManager.
     * @return the dataManager
     */
    public static DefaultDataManager getDataManager() {
        return dataManager;
    }

}
