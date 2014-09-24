package pt.utl.ist.repox.task;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 */
public class TaskDay {
	/**
	 */
	public enum MonthState { PREVIOUS_MONTH, CURRENT_MONTH, NEXT_MONTH };

	private Calendar day;
	private MonthState monthState;
	private List<ScheduledTask> tasks;

	@SuppressWarnings("javadoc")
    public Calendar getDay() {
		return day;
	}

	@SuppressWarnings("javadoc")
    public void setDay(Calendar day) {
		this.day = day;
	}

	@SuppressWarnings("javadoc")
    public MonthState getMonthState() {
		return monthState;
	}

	@SuppressWarnings("javadoc")
    public void setMonthState(MonthState monthState) {
		this.monthState = monthState;
	}

	@SuppressWarnings("javadoc")
    public List<ScheduledTask> getTasks() {
		return tasks;
	}

	@SuppressWarnings("javadoc")
    public void setTasks(List<ScheduledTask> tasks) {
		this.tasks = tasks;
	}

	@SuppressWarnings("javadoc")
    public int getNumericDay() {
		return day.get(Calendar.DAY_OF_MONTH);
	}

	@SuppressWarnings("javadoc")
    public boolean isCurrentMonth() {
		return monthState.equals(MonthState.CURRENT_MONTH);
	}

	@SuppressWarnings("javadoc")
    public boolean getCurrentMonth() {
		return monthState.equals(MonthState.CURRENT_MONTH);
	}

	/**
	 * Creates a new instance of this class.
	 * @param day
	 * @param monthState
	 * @param tasks
	 */
	public TaskDay(Calendar day, MonthState monthState, List<ScheduledTask> tasks) {
		super();
		this.day = day;
		this.monthState = monthState;
		this.tasks = tasks;
		Collections.sort(this.tasks, new ScheduledTaskHourMinComparator());
	}
}
