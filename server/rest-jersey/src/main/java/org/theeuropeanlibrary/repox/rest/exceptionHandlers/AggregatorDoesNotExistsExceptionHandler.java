/* AggregatorDoesNotExistsExceptionHandler.java - created on Oct 10, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.exceptionHandlers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import pt.utl.ist.util.exceptions.AggregatorDoesNotExistsException;

/**
 * Exception handler for the {@link pt.utl.ist.util.exceptions.AggregatorDoesNotExistsException}
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 10, 2014
 */
public class AggregatorDoesNotExistsExceptionHandler implements ExceptionMapper<AggregatorDoesNotExistsException> {
    @Override
    public Response toResponse(AggregatorDoesNotExistsException ex) {
        return Response.status(404).entity(ex.getMessage()).type("text/plain").build();
    }
}
