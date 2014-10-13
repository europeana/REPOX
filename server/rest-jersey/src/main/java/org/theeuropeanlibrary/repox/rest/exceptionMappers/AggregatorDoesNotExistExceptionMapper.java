/* AggregatorDoesNotExistsExceptionHandler.java - created on Oct 10, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.exceptionMappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import pt.utl.ist.util.exceptions.AggregatorDoesNotExistException;

/**
 * Exception handler for the {@link pt.utl.ist.util.exceptions.AggregatorDoesNotExistException}
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 10, 2014
 */
@Provider
public class AggregatorDoesNotExistExceptionMapper implements ExceptionMapper<AggregatorDoesNotExistException> {
    @Override
    public Response toResponse(AggregatorDoesNotExistException ex) {
        //TODO Fix error code
        return Response.status(404).entity(ex.getMessage()).type("text/plain").build();
    }
}
