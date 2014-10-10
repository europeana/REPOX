/* InvalidValueException.java - created on Oct 10, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package pt.utl.ist.util.exceptions;

import java.io.Serializable;

/**
 * Value provided is invalid.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 10, 2014
 */
public class InvalidValueException extends Exception implements Serializable {
    private static final long serialVersionUID = 44L;
    
    public InvalidValueException() {
        super();
    }
    public InvalidValueException(String msg)   {
        super(msg);
    }
    public InvalidValueException(String msg, Exception e)  {
        super(msg, e);
    }
}
