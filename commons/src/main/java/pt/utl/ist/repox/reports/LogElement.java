package pt.utl.ist.repox.reports;

/**
 * Created to project REPOX.
 * User: Edmundo
 * Date: 04/07/12
 * Time: 16:55
 */
public class LogElement {

    private String         id;
    private String         value;
    protected LogEntryType type;

    private String         errorCause;
    protected String       failedId;

    /**
     * Creates a new instance of this class.
     * @param id
     * @param value
     */
    public LogElement(String id, String value) {
        this.id = id;
        this.value = value;
        this.type = LogEntryType.NORMAL;
    }

    // Error
    /**
     * Creates a new instance of this class.
     * @param id
     * @param failedId
     * @param errorCause
     */
    public LogElement(String id, String failedId, String errorCause) {
        this.id = id;
        this.errorCause = errorCause;
        this.failedId = failedId;
        this.type = LogEntryType.ERROR;
    }

    @SuppressWarnings("javadoc")
    public String getId() {
        return id;
    }

    @SuppressWarnings("javadoc")
    public String getValue() {
        return value;
    }

    @SuppressWarnings("javadoc")
    public String getFailedId() {
        return failedId;
    }

    @SuppressWarnings("javadoc")
    public String getErrorCause() {
        return errorCause;
    }

    @SuppressWarnings("javadoc")
    public LogEntryType getType() {
        return type;
    }
}
