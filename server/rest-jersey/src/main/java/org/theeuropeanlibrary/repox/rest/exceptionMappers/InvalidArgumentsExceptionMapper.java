/* InvalidValueExceptionHandler.java - created on Oct 10, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.exceptionMappers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import pt.utl.ist.util.exceptions.InvalidArgumentsException;

/**
 * Exception handler for the {@link pt.utl.ist.util.exceptions.InvalidArgumentsException}
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 10, 2014
 */
@Provider
public class InvalidArgumentsExceptionMapper implements ExceptionMapper<InvalidArgumentsException> {
    @Override
    public Response toResponse(InvalidArgumentsException ex) {
        //Status: 400, Info: Bad Request
        return Response.status(400).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
    }
}
