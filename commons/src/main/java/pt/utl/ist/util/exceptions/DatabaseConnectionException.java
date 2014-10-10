/* DatabaseConnectionException.java - created on Oct 10, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package pt.utl.ist.util.exceptions;

import java.io.Serializable;

/**
 * Connection to database has failed.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 10, 2014
 */
public class DatabaseConnectionException extends Exception implements Serializable{
    private static final long serialVersionUID = 44L;
    
    public DatabaseConnectionException() {
        super();
    }
    public DatabaseConnectionException(String msg)   {
        super(msg);
    }
    public DatabaseConnectionException(String msg, Exception e)  {
        super(msg, e);
    }
}
