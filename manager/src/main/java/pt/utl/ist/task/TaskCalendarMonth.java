package pt.utl.ist.task;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.repox.util.Month;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 */
public class TaskCalendarMonth {
    /* Columns : Monday to Sunday; Lines: weeks of the month */
    private TaskDay[][] monthdays = new TaskDay[6][7];
    private Month       month;
    private int         year;

    @SuppressWarnings("javadoc")
    public TaskDay[][] getMonthdays() {
        return monthdays;
    }

    @SuppressWarnings("javadoc")
    public void setMonthdays(TaskDay[][] monthdays) {
        this.monthdays = monthdays;
    }

    @SuppressWarnings("javadoc")
    public Month getMonth() {
        return month;
    }

    @SuppressWarnings("javadoc")
    public void setMonth(Month month) {
        this.month = month;
    }

    @SuppressWarnings("javadoc")
    public int getYear() {
        return year;
    }

    @SuppressWarnings("javadoc")
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @return Array of Stirng of the weekdays
     */
    /* Get the names of the seven week days */
    public String[] getWeekdays() {
        String[] weekdays = new String[7];

        Calendar calendar = Calendar.getInstance();
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) { //Get the first Monday
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        for (int i = 0; i < weekdays.length; i++) {
            DateFormat format = new SimpleDateFormat("EEE", new Locale("en"));
            weekdays[i] = format.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return weekdays;
    }

    /**
     * Creates a new instance of this class.
     * @param month
     * @param year
     */
    public TaskCalendarMonth(Month month, int year) {
        this.month = month;
        //		this.year = Calendar.getInstance().get(Calendar.YEAR);
        this.year = year;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month.getMonthNumber());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) { //Get the first Monday
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        for (int i = 0; i < monthdays.length; i++) {
            for (int j = 0; j < monthdays[i].length; j++) {
                TaskDay.MonthState monthState = null;
                if (calendar.get(Calendar.MONTH) < month.getMonthNumber()) {
                    monthState = TaskDay.MonthState.PREVIOUS_MONTH;
                } else if (calendar.get(Calendar.MONTH) == month.getMonthNumber()) {
                    monthState = TaskDay.MonthState.CURRENT_MONTH;
                } else {
                    monthState = TaskDay.MonthState.NEXT_MONTH;
                }

                Calendar day = Calendar.getInstance();
                day.setTime(calendar.getTime());
                List<ScheduledTask> tasksForDay = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getTasksForDay(day);
                monthdays[i][j] = new TaskDay(day, monthState, tasksForDay);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
    }
}