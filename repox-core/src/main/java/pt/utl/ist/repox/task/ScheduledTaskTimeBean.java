package pt.utl.ist.repox.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 */
public class ScheduledTaskTimeBean {
    private String        frequency;
    private Integer       xmonths;
    private List<Integer> minutes;  //00-59 (5 minutes interval)
    private List<Integer> hours;    //00-23	
    private Calendar      firstRun;

    @SuppressWarnings("javadoc")
    public String getFrequency() {
        return frequency;
    }

    @SuppressWarnings("javadoc")
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    @SuppressWarnings("javadoc")
    public Integer getXmonths() {
        return xmonths;
    }

    @SuppressWarnings("javadoc")
    public void setXmonths(Integer xmonths) {
        this.xmonths = xmonths;
    }

    @SuppressWarnings("javadoc")
    public List<Integer> getMinutes() {
        return minutes;
    }

    @SuppressWarnings("javadoc")
    public void setMinutes(List<Integer> minutes) {
        this.minutes = minutes;
    }

    @SuppressWarnings("javadoc")
    public List<Integer> getHours() {
        return hours;
    }

    @SuppressWarnings("javadoc")
    public void setHours(List<Integer> hours) {
        this.hours = hours;
    }

    @SuppressWarnings("javadoc")
    public Calendar getFirstRun() {
        return firstRun;
    }

    @SuppressWarnings("javadoc")
    public void setFirstRun(Calendar firstRun) {
        this.firstRun = firstRun;
    }

    /**
     * 
     */
    public void init() {
        minutes = new ArrayList<Integer>();
        hours = new ArrayList<Integer>();

        for (int i = 0; i < 60; i = i + 5) {
            minutes.add(i);
        }
        for (int i = 0; i < 24; i++) {
            hours.add(i);
        }

        Locale locale = new Locale("en");
        /*
        		Locale locale = context.getLocale();

        		String langParameter = (String) context.getRequest().getSession().getAttribute("lang");

        		if(langParameter != null) {
        			locale = new Locale(langParameter);
        		}
        */

    }
}
