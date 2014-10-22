/* MissingArgumentExceptionMapper.java - created on Oct 16, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.exceptionMappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.util.exceptions.MissingArgumentsException;

/**
 * Exception handler for the {@link pt.utl.ist.util.exceptions.MissingArgumentsException}
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 16, 2014
 */
@Provider
public class MissingArgumentsExceptionMapper implements ExceptionMapper<MissingArgumentsException> {
    @Override
    public Response toResponse(MissingArgumentsException ex) {
        //Status: 406, Info: Not Acceptable
        return Response.status(406).entity(new Result(ex.getMessage())).build();
    }
}
