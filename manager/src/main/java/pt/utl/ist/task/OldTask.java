package pt.utl.ist.task;

import java.util.Date;

import pt.utl.ist.dataProvider.DataSource;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 05-05-2011
 * Time: 17:37
 */
public class OldTask {

    protected DataSource dataSource;
    protected String     id;
    protected String     logName;
    protected String     ingestType;
    protected String     status;
    protected String     retries;
    protected String     retryMax;
    protected String     delay;
    protected String     dateString;
    protected String     records;
    protected String     onlyDate;
    protected String     onlyTime;
    protected Date       actualDate;

    protected int        year;
    protected int        month;
    protected int        day;
    protected int        hours;
    protected int        minutes;

    /**
     * Creates a new instance of this class.
     * @param dataSource
     * @param id
     * @param logName
     * @param ingestType
     * @param status
     * @param retries
     * @param retryMax
     * @param delay
     * @param dateString
     * @param records
     */
    public OldTask(DataSource dataSource, String id, String logName, String ingestType, String status, String retries, String retryMax, String delay, String dateString, String records) {
        super();
        this.dataSource = dataSource;
        this.id = id;
        this.logName = logName;
        this.ingestType = ingestType;
        this.status = status;
        this.retries = retries;
        this.retryMax = retryMax;
        this.delay = delay;
        this.dateString = dateString;
        this.records = records;

        parseDate();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getStatus() {
        return status;
    }

    public String getIngestType() {
        return ingestType;
    }

    public String getDelay() {
        return delay;
    }

    public String getRecords() {
        return records;
    }

    public String getRetries() {
        return retries;
    }

    public String getRetryMax() {
        return retryMax;
    }

    public String getDateString() {
        return dateString;
    }

    public String getLogName() {
        return logName;
    }

    public String getId() {
        return id;
    }

    public Integer getDay() {
        return day;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getHours() {
        return hours;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public String getOnlyDate() {
        return onlyDate;
    }

    public String getOnlyTime() {
        return onlyTime;
    }

    public Date getActualDate() {
        return actualDate;
    }

    /**
     * 
     */
    @SuppressWarnings("deprecation")
    public void parseDate() {
        String date = dateString;
        String delimDateTime = "[ ]+";
        String[] tokensDateTime = date.split(delimDateTime);

        String delimDate = "[-]+";
        String[] tokensDate = tokensDateTime[0].split(delimDate);
        year = Integer.parseInt(tokensDate[0]);
        month = Integer.parseInt(tokensDate[1]);
        day = Integer.parseInt(tokensDate[2]);

        //Parse Time
        String delimTime = "[:]+";
        String[] tokensTime = tokensDateTime[1].split(delimTime);
        hours = Integer.parseInt(tokensTime[0]);
        minutes = Integer.parseInt(tokensTime[1]);

        //Create Date type
        Date actualDate = new Date(year-1900, month-1, day, hours, minutes);
        this.actualDate = actualDate;
        
        System.out.println(actualDate.toString());

        String minString = "" + getMinutes();
        if (getMinutes() < 10) minString = "0" + getMinutes();

        String hoursString = "" + getHours();
        if (getHours() < 10) hoursString = "0" + getHours();

        onlyTime = hoursString + ":" + minString;
        onlyDate = getDay() + "/" + getMonth() + "/" + getYear();
    }
}
