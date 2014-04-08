package pt.utl.ist.repox.reports;

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

    public ErrorLogElement(String name, String value, Date occurenceTime, String occurenceClass, Exception inputException) {
        super(name, value);
        this.occurenceTime = occurenceTime;
        this.occurenceClass = occurenceClass;
        this.type = LogEntryType.ERROR;
        this.inputException = inputException;
    }

    public Date getOccurenceTime() {
        return occurenceTime;
    }

    public String getOccurenceClass() {
        return occurenceClass;
    }

    public Exception getInputException() {
        return inputException;
    }
}
