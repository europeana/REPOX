/* JerseyConfig.java - created on Oct 14, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.configuration;

import org.glassfish.jersey.server.ResourceConfig;
import org.theeuropeanlibrary.repox.rest.exceptionMappers.AggregatorDoesNotExistExceptionMapper;
import org.theeuropeanlibrary.repox.rest.exceptionMappers.AggregatorExistExceptionMapper;

/**
 * 
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 14, 2014
 */
public class JerseyConfig extends ResourceConfig{

    /**
     * Creates a new instance of this class.
     */
    public JerseyConfig() {
        packages("org.theeuropeanlibrary.repox.rest.servlets");
        register(AggregatorDoesNotExistExceptionMapper.class);
        register(AggregatorExistExceptionMapper.class);
    }

}
