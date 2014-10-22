/* InternalServerErrorExceptionMapper.java - created on Oct 16, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.exceptionMappers;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

/**
 * Exception handler for the {@link javax.ws.rs.InternalServerErrorException}
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 16, 2014
 */
@Provider
public class InternalServerErrorExceptionMapper implements ExceptionMapper<InternalServerErrorException> {
    @Override
    public Response toResponse(InternalServerErrorException ex) {
        //Status: 500, Info: Internal Server Error
        return Response.status(500).entity(new Result(ex.getMessage())).build();
    }
}
