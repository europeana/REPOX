/*
 * Created on 2006/12/02
 *
 */
package pt.utl.ist.util;

/**
 */
public class InvalidInputException extends Exception {
    /**
     * Creates a new instance of this class.
     * @param message
     */
    public InvalidInputException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of this class.
     * @param thr
     */
    public InvalidInputException(Throwable thr) {
        super(thr);
    }

    /**
     * Creates a new instance of this class.
     * @param message
     * @param thr
     */
    public InvalidInputException(String message, Throwable thr) {
        super(message, thr);
    }
}
