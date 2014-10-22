/* DatabaseConnectionException.java - created on Oct 10, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.exceptionMappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.util.exceptions.DatabaseConnectionException;

/**
 * Exception handler for the {@link pt.utl.ist.util.exceptions.DatabaseConnectionException}
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 10, 2014
 */
@Provider
public class DatabaseConnectionExceptionMapper implements ExceptionMapper<DatabaseConnectionException> {
    @Override
    public Response toResponse(DatabaseConnectionException ex) {
        //Status: 503, Info: Service Unavailable
        return Response.status(503).entity(new Result(ex.getMessage())).build();
    }
}
