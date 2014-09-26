package pt.utl.ist.repox.util.exceptions.task;

/**
 */
public class IllegalFileFormatException extends Exception {

    /**
     * Creates a new instance of this class.
     */
    public IllegalFileFormatException() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * @param message
     * @param cause
     */
    public IllegalFileFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance of this class.
     * @param cause
     */
    public IllegalFileFormatException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of this class.
     * @param message
     */
    public IllegalFileFormatException(String message) {
        super(message);
    }

}
