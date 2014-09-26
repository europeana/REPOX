package pt.utl.ist.reports;

import java.util.Date;

/**
 * Created to project REPOX.
 * User: Edmundo
 * Date: 04/07/12
 * Time: 16:55
 */
public class ErrorLogElement extends LogElement{

    private Date occurenceTime;
    private String occurenceClass;
    private Exception inputException;

    /**
     * Creates a new instance of this class.
     * @param name
     * @param value
     * @param occurenceTime
     * @param occurenceClass
     * @param inputException
     */
    public ErrorLogElement(String name, String value, Date occurenceTime, String occurenceClass, Exception inputException) {
        super(name, value);
        this.occurenceTime = occurenceTime;
        this.occurenceClass = occurenceClass;
        this.type = LogEntryType.ERROR;
        this.inputException = inputException;
    }

    @SuppressWarnings("javadoc")
    public Date getOccurenceTime() {
        return occurenceTime;
    }

    @SuppressWarnings("javadoc")
    public String getOccurenceClass() {
        return occurenceClass;
    }

    @SuppressWarnings("javadoc")
    public Exception getInputException() {
        return inputException;
    }
}
