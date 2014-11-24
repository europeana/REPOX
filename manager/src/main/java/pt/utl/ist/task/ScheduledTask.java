package pt.utl.ist.task;

import pt.utl.ist.util.CompareUtil;
import pt.utl.ist.util.TimeUtil;
import pt.utl.ist.util.date.DateUtil;

import java.util.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 */
@XmlRootElement(name = "ScheduledTask")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "A ScheduledTask")
public class ScheduledTask extends Task {
    /**
     */
    @XmlEnum(String.class)
    public enum Frequency {
        ONCE, DAILY, WEEKLY, XMONTHLY
    }

    @ApiModelProperty(hidden = true)
    private List<String> minutesList; //00-59 (5 minutes interval)
    @ApiModelProperty(hidden = true)
    private List<String> hoursList;  //00-23

    @XmlElement
    @ApiModelProperty
    private String       id;
    @ApiModelProperty(hidden = true)
    private Calendar     firstRun;   // time of first execution
    @XmlElement
    @ApiModelProperty(required = true)
    private Frequency    frequency;  // frequency of the execution - ONCE , DAILY, WEEKLY, XMONTHLY
    @XmlElement
    @ApiModelProperty
    private Integer      xmonths;    // month frequency for XMONTHLY (1..12 months)

    @Override
    protected int getNumberParameters() {
        throw new UnsupportedOperationException("This method is not supposed to be called here");
    }

    public List<String> getMinutesList() {
        return minutesList;
    }

    public void setMinutesList(List<String> minutesList) {
        this.minutesList = minutesList;
    }

    public List<String> getHoursList() {
        return hoursList;
    }

    public void setHoursList(List<String> hoursList) {
        this.hoursList = hoursList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Calendar getFirstRun() {
        return firstRun;
    }

    public void setFirstRun(Calendar firstRun) {
        this.firstRun = firstRun;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Integer getXmonths() {
        return xmonths;
    }

    public void setXmonths(Integer xmonths) {
        this.xmonths = xmonths;
    }

    @ApiModelProperty(hidden = true)
    public Integer getMinute() {
        if (firstRun == null) {
            firstRun = Calendar.getInstance();
        }
        return firstRun.get(Calendar.MINUTE);
    }

    public void setMinute(Integer minute) {
        if (firstRun == null) {
            firstRun = Calendar.getInstance();
        }
        firstRun.set(Calendar.MINUTE, minute);
    }

    @ApiModelProperty(hidden = true)
    public Integer getHour() {
        if (firstRun == null) {
            firstRun = Calendar.getInstance();
        }
        return firstRun.get(Calendar.HOUR_OF_DAY);
    }

    public void setHour(Integer hour) {
        if (firstRun == null) {
            firstRun = Calendar.getInstance();
        }
        firstRun.set(Calendar.HOUR_OF_DAY, hour);
    }

    @XmlElement
    @ApiModelProperty(required = true)
    public String getDate() {
        if (firstRun == null) {
            firstRun = Calendar.getInstance();
        }
        return DateUtil.date2String(firstRun.getTime(), TimeUtil.DATE_FORMAT);
    }

    public void setDate(String date) {
        if (firstRun == null) {
            firstRun = Calendar.getInstance();
        }

        String[] dateComponents = null;
        if (date.contains("-"))
            dateComponents = date.split("-");
        else if (date.contains("/"))
            dateComponents = date.split("/");

        firstRun.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateComponents[0]));
        firstRun.set(Calendar.MONTH, Integer.valueOf(dateComponents[1]) - 1);
        firstRun.set(Calendar.YEAR, Integer.valueOf(dateComponents[2]));
    }

    @XmlElement
    @ApiModelProperty(required = true)
    public String getTime() {
        if (firstRun == null) {
            firstRun = Calendar.getInstance();
        }
        return DateUtil.date2String(firstRun.getTime(), TimeUtil.TIME_FORMAT);
    }

    public void setTime(String time)
    {
        if (firstRun == null) {
            firstRun = Calendar.getInstance();
        }

        String[] dateComponents = time.split(":");
        firstRun.set(Calendar.HOUR_OF_DAY, Integer.valueOf(dateComponents[0]));
        firstRun.set(Calendar.MINUTE, Integer.valueOf(dateComponents[1]));
    }

    @ApiModelProperty(hidden = true)
    public Integer getDay() {
        if (firstRun == null) {
            firstRun = Calendar.getInstance();
        }
        return firstRun.get(Calendar.DAY_OF_MONTH);
    }

    public void setDay(Integer day) {
        if (firstRun == null) {
            firstRun = Calendar.getInstance();
        }
        firstRun.set(Calendar.DAY_OF_MONTH, day);
    }

    @ApiModelProperty(hidden = true)
    public Integer getMonth() {
        if (firstRun == null) {
            firstRun = Calendar.getInstance();
        }
        return firstRun.get(Calendar.MONTH);
    }

    public void setMonth(Integer month) {
        if (firstRun == null) {
            firstRun = Calendar.getInstance();
        }
        firstRun.set(Calendar.MONTH, month - 1);
    }

    @ApiModelProperty(hidden = true)
    public Integer getYear() {
        if (firstRun == null) {
            firstRun = Calendar.getInstance();
        }
        return firstRun.get(Calendar.YEAR);
    }

    public void setYear(Integer year) {
        if (firstRun == null) {
            firstRun = Calendar.getInstance();
        }
        firstRun.set(Calendar.YEAR, year);
    }

    @ApiModelProperty(hidden = true)
    public String getFirstRunString() {
        return DateUtil.date2String(firstRun.getTime(), TimeUtil.LONG_DATE_FORMAT_NO_SECS);
    }

    @ApiModelProperty(hidden = true)
    public String getFirstRunStringHour() {
        return DateUtil.date2String(firstRun.getTime(), "HH:mm");
    }

    @ApiModelProperty(hidden = true)
    public String getFirstRunStringDate() {

        return DateUtil.date2String(firstRun.getTime(), "dd/MM/yyyy");
        //lizreturn DateUtil.date2String(firstRun.getTime(), "dd-MM-yyyy");
    }

    /**
     * Get the next ingest time
     * @return next ingest time
     */
    @ApiModelProperty(hidden = true)
    public String getNextIngestDate() {
        Calendar now = Calendar.getInstance();

        if (firstRun.after(now)) {
            return DateUtil.date2String(firstRun.getTime(), TimeUtil.LONG_DATE_FORMAT_NO_SECS);
        }

        Calendar maxTime = Calendar.getInstance();
        maxTime.add(Calendar.YEAR, 2); // setting up a max time for checks to avoid infinite loops
        switch (frequency) {
        case ONCE:
            break;
        case DAILY:
            Calendar testCalendar = Calendar.getInstance();
            testCalendar.set(Calendar.HOUR_OF_DAY, firstRun.get(Calendar.HOUR_OF_DAY));
            testCalendar.set(Calendar.MINUTE, firstRun.get(Calendar.MINUTE));

            if (testCalendar.before(now)) {
                now.add(Calendar.DATE, 1);
            }
            now.set(Calendar.HOUR_OF_DAY, firstRun.get(Calendar.HOUR_OF_DAY));
            now.set(Calendar.MINUTE, firstRun.get(Calendar.MINUTE));

            return DateUtil.date2String(now.getTime(), TimeUtil.LONG_DATE_FORMAT_NO_SECS);
        case WEEKLY:
            testCalendar = (Calendar)firstRun.clone();

            while ((testCalendar.equals(firstRun) || testCalendar.after(firstRun)) && testCalendar.before(maxTime)) {
                testCalendar.add(Calendar.DATE, 7);

                // used for next time comparation
                now.set(Calendar.SECOND, 59);
                if (testCalendar.after(now)) {
                    break;
                }
            }
            return DateUtil.date2String(testCalendar.getTime(), TimeUtil.LONG_DATE_FORMAT_NO_SECS);

        case XMONTHLY:
            testCalendar = (Calendar)firstRun.clone();

            while ((testCalendar.equals(firstRun) || testCalendar.after(firstRun)) && testCalendar.before(maxTime)) {
                testCalendar.add(Calendar.MONTH, xmonths);

                if (testCalendar.after(now)) {
                    break;
                }
            }
            return DateUtil.date2String(testCalendar.getTime(), TimeUtil.LONG_DATE_FORMAT_NO_SECS);
        default:
            break;
        }

        return "";
    }

    @ApiModelProperty(hidden = true)
    public String getWeekdayAsString() {
        Locale locale = new Locale("en");
        return firstRun.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
    }

    private void init() {
        minutesList = new ArrayList<String>();
        hoursList = new ArrayList<String>();

        for (int i = 0; i < 60; i = i + 5) {

            if (i < 10) {
                minutesList.add("0" + i);
            } else {
                minutesList.add(String.valueOf(i));
            }
        }
        for (int i = 0; i < 24; i++) {
            hoursList.add(String.valueOf(i));
        }
    }

    /**
     * Creates a new instance of this class.
     */
    public ScheduledTask() {
        super();
        init();
    }

    /**
     * Creates a new instance of this class.
     * @param id
     * @param firstRun
     * @param frequency
     * @param xmonths
     * @param taskToRun
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public ScheduledTask(String id, Calendar firstRun, Frequency frequency, Integer xmonths, Task taskToRun) throws SecurityException, NoSuchMethodException {
        super(taskToRun.getTaskClass(), taskToRun.getParameters(), taskToRun.getStartTime(), taskToRun.getFinishTime(), taskToRun.getStatus(), taskToRun.getMaxRetries(), taskToRun.getRetries(),
                taskToRun.getRetryDelay());

        init();
        this.id = id;
        this.firstRun = firstRun;
        this.frequency = frequency; //ONCE, DAILY, WEEKLY, XMONTHLY
        this.xmonths = xmonths; //01-12
    }

    @Override
    public boolean isTimeToRun(Calendar calendar) {
        if (isRunning()) {
            return false;
        } else if (status != null && status.equals(Task.Status.FAILED)) {
            return isTimeToRetry(calendar);
        }
        else if (status != null && status.equals(Task.Status.CANCELED) && isHourMinuteDateFailed(calendar))
        {
            return false;
        }

        return firstRun == null || isHourMinuteToRun(calendar) && isDayToRun(calendar);

    }

    public boolean isHourMinuteDateFailed(Calendar calendar) {
        return (calendar.get(Calendar.DAY_OF_MONTH) == failTime.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == failTime.get(Calendar.MONTH) && calendar.get(Calendar.YEAR) == failTime
                .get(Calendar.YEAR) && failTime.get(Calendar.MINUTE) == calendar.get(Calendar.MINUTE) && failTime.get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY));
    }

    /**
     * @param calendar
     * @return boolean indicating if its hour and minute to run
     */
    public boolean isHourMinuteToRun(Calendar calendar) {
        return (firstRun.get(Calendar.MINUTE) == calendar.get(Calendar.MINUTE) && firstRun.get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY));
    }

    /**
     * @param calendar
     * @return boolean indicating if its the day to run
     */
    public boolean isDayToRun(Calendar calendar) {
        Calendar dayStartCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        if (firstRun.after(dayStartCalendar)) {
            return false;
        }

        switch (frequency) {
        case ONCE:
            return (dayStartCalendar.get(Calendar.DAY_OF_MONTH) == firstRun.get(Calendar.DAY_OF_MONTH) && dayStartCalendar.get(Calendar.MONTH) == firstRun.get(Calendar.MONTH) && dayStartCalendar
                    .get(Calendar.YEAR) == firstRun.get(Calendar.YEAR));
        case DAILY:
            return true;
        case WEEKLY:
            return (dayStartCalendar.get(Calendar.DAY_OF_WEEK) == firstRun.get(Calendar.DAY_OF_WEEK));
        case XMONTHLY:
            int monthDiff = (dayStartCalendar.get(Calendar.YEAR) - firstRun.get(Calendar.YEAR)) * 12 + dayStartCalendar.get(Calendar.MONTH) - firstRun.get(Calendar.MONTH);

            return ((monthDiff % xmonths == 0) && (dayStartCalendar.get(Calendar.DAY_OF_MONTH) == firstRun.get(Calendar.DAY_OF_MONTH)));
        default:
            throw new UnsupportedOperationException("Unsupported frequency: " + frequency.toString());
        }
    }

    /**
     * Returns false if it's the same hour/minute as execution time to avoid executing more than once
     */
    @ApiModelProperty(hidden = true)
    @Override
    public boolean isTimeToRemove() {
        Calendar now = Calendar.getInstance();

        if (firstRun == null && startTime == null) {
            return true;
        } else if (firstRun == null) {
            return (!(now.get(Calendar.HOUR_OF_DAY) == startTime.get(Calendar.HOUR_OF_DAY) && now.get(Calendar.MINUTE) == startTime.get(Calendar.MINUTE)));
        } else {
            return (!(now.get(Calendar.HOUR_OF_DAY) == firstRun.get(Calendar.HOUR_OF_DAY) && now.get(Calendar.MINUTE) == firstRun.get(Calendar.MINUTE)));
        }
    }

    @Override
    public boolean equalActionParameters(Task otherTask) {
        boolean equal = true;

        if (otherTask == null || !CompareUtil.compareArraysAndNull(parameters, otherTask.getParameters())) {
            equal = false;
        }

        return equal;
    }

}
