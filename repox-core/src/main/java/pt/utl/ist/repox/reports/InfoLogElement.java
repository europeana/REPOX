package pt.utl.ist.repox.reports;

import java.util.Date;

/**
 * Created to project REPOX.
 * User: Edmundo
 * Date: 04/07/12
 * Time: 16:55
 */
public class InfoLogElement extends LogElement{

    private Date occurenceTime;
    private String occurenceClass;

    public InfoLogElement(String name, String value,Date occurenceTime, String occurenceClass) {
        super(name, value);
        this.occurenceTime = occurenceTime;
        this.occurenceClass = occurenceClass;
        this.type = LogEntryType.INFO;
    }

    public Date getOccurenceTime() {
        return occurenceTime;
    }

    public String getOccurenceClass() {
        return occurenceClass;
    }
}
