package pt.utl.ist.util.exceptions;

/**
 */
public class InvalidRequestException extends Exception {

    /**
     * Creates a new instance of this class.
     */
    public InvalidRequestException() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * @param message
     */
    public InvalidRequestException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of this class.
     * @param cause
     */
    public InvalidRequestException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of this class.
     * @param message
     * @param cause
     */
    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}
