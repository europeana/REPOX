/* AggregatorExistsExceptionHandler.java - created on Oct 10, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.exceptionMappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import pt.utl.ist.util.exceptions.AggregatorExistsException;

/**
 * Exception handler for the {@link pt.utl.ist.util.exceptions.AggregatorExistsException}
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 10, 2014
 */
public class AggregatorExistsExceptionMapper implements ExceptionMapper<AggregatorExistsException> {
    @Override
    public Response toResponse(AggregatorExistsException ex) {
        //TODO Fix error code
        return Response.status(404).entity(ex.getMessage()).type("text/plain").build();
    }
}
