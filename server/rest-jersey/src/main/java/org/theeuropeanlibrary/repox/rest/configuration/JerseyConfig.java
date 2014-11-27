/* JerseyConfig.java - created on Oct 14, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.configuration;

import java.net.MalformedURLException;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.theeuropeanlibrary.repox.rest.exceptionMappers.AlreadyExistsExceptionMapper;
import org.theeuropeanlibrary.repox.rest.exceptionMappers.DoesNotExistExceptionMapper;
import org.theeuropeanlibrary.repox.rest.exceptionMappers.InternalServerErrorExceptionMapper;
import org.theeuropeanlibrary.repox.rest.exceptionMappers.InvalidArgumentsExceptionMapper;
import org.theeuropeanlibrary.repox.rest.exceptionMappers.MissingArgumentsExceptionMapper;

/**
 * Register all the resources for the jersey configuration
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 14, 2014
 */
public class JerseyConfig extends ResourceConfig{

    /**
     * Creates a new instance of this class.
     * @throws MalformedURLException 
     */
    public JerseyConfig() throws MalformedURLException {
        packages("org.theeuropeanlibrary.repox.rest.servlets");
        
        //Exceptions
        register(DoesNotExistExceptionMapper.class);
        register(AlreadyExistsExceptionMapper.class);
        register(MissingArgumentsExceptionMapper.class);
        register(InvalidArgumentsExceptionMapper.class);
        register(InternalServerErrorExceptionMapper.class);
        
        //Features
        packages("org.glassfish.jersey.examples.multipart");
        register(MultiPartFeature.class);
    }

}
