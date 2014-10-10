/* DatabaseConnectionException.java - created on Oct 10, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.exceptionHandlers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import pt.utl.ist.util.exceptions.DatabaseConnectionException;

/**
 * Exception handler for the {@link pt.utl.ist.util.exceptions.DatabaseConnectionException}
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 10, 2014
 */
@Provider
public class DatabaseConnectionExceptionHandler implements ExceptionMapper<DatabaseConnectionException> {
    @Override
    public Response toResponse(DatabaseConnectionException ex) {
        return Response.status(404).entity(ex.getMessage()).type("text/plain").build();
    }
}
