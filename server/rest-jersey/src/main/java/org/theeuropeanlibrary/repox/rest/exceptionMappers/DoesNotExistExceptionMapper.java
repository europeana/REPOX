/* AggregatorDoesNotExistsExceptionHandler.java - created on Oct 10, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.exceptionMappers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import pt.utl.ist.util.exceptions.DoesNotExistException;

/**
 * Exception handler for the {@link pt.utl.ist.util.exceptions.DoesNotExistException}
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 10, 2014
 */
@Provider
public class DoesNotExistExceptionMapper implements ExceptionMapper<DoesNotExistException> {
    @Override
    public Response toResponse(DoesNotExistException ex) {
        //Status: 404, Info: Not Found
        return Response.status(404).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
    }
}
