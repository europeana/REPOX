/* AggregatorExistsExceptionHandler.java - created on Oct 10, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.exceptionMappers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import pt.utl.ist.util.exceptions.AggregatorExistException;

/**
 * Exception handler for the {@link pt.utl.ist.util.exceptions.AggregatorExistException}
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 10, 2014
 */
@Provider
public class AggregatorExistExceptionMapper implements ExceptionMapper<AggregatorExistException> {
    @Override
    public Response toResponse(AggregatorExistException ex) {
        //Status: 409, Info: Conflict
        return Response.status(409).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
    }
}
