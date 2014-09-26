package pt.utl.ist.z3950;

/**
 */
public class HarvestFailureException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of this class.
     */
    public HarvestFailureException() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * @param e
     */
    public HarvestFailureException(Exception e) {
        super(e);
    }

    /**
     * Creates a new instance of this class.
     * @param message
     * @param e
     */
    public HarvestFailureException(String message, Exception e) {
        super(message, e);
    }

    /**
     * Creates a new instance of this class.
     * @param message
     */
    public HarvestFailureException(String message) {
        super(message);
    }
}
