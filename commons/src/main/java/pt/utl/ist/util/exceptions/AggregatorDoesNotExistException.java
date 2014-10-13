/* AggregatorDoesNotExistsException.java - created on Oct 10, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package pt.utl.ist.util.exceptions;

import java.io.Serializable;

/**
 * The aggregator specified is not existent.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 10, 2014
 */
public class AggregatorDoesNotExistException extends Exception implements Serializable {
    private static final long serialVersionUID = 44L;

    public AggregatorDoesNotExistException() {
        super();
    }

    public AggregatorDoesNotExistException(String msg) {
        super(msg);
    }

    public AggregatorDoesNotExistException(String msg, Exception e) {
        super(msg, e);
    }
}
