/* MissingArgumentException.java - created on Oct 16, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package pt.utl.ist.util.exceptions;

import java.io.Serializable;

/**
 * Missing arguments exception.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 16, 2014
 */
public class MissingArgumentsException extends Exception implements Serializable {
    private static final long serialVersionUID = 44L;

    public MissingArgumentsException() {
    }

    public MissingArgumentsException(String msg) {
        super(msg);
    }

    public MissingArgumentsException(String msg, Exception e) {
        super(msg, e);
    }
}
