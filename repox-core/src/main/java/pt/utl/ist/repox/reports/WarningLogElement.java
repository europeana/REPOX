package pt.utl.ist.repox.reports;

/**
 * Created to project REPOX.
 * User: Edmundo
 * Date: 04/07/12
 * Time: 16:55
 */
public class WarningLogElement extends LogElement{

    private String resultLink;

    public WarningLogElement(String failedId, String resultLink) {
        super(failedId, resultLink);
        this.resultLink = resultLink;
        this.failedId = failedId;
        this.type = LogEntryType.WARNING;
    }

    public String getResultLink() {
        return resultLink;
    }
}
