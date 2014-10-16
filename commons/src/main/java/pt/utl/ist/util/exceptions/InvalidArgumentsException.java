package pt.utl.ist.util.exceptions;

import java.io.Serializable;

/**
 * Invalid arguments.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 16, 2014
 */
public class InvalidArgumentsException extends Exception implements Serializable {
    private static final long serialVersionUID = 44L;

    public InvalidArgumentsException() {
        super();
    }

    public InvalidArgumentsException(String msg) {
        super(msg);
    }

    public InvalidArgumentsException(String msg, Exception e) {
        super(msg, e);
    }
}
