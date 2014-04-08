package pt.utl.ist.repox.task;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class TaskDay {
	public enum MonthState { PREVIOUS_MONTH, CURRENT_MONTH, NEXT_MONTH };

	private Calendar day;
	private MonthState monthState;
	private List<ScheduledTask> tasks;

	public Calendar getDay() {
		return day;
	}

	public void setDay(Calendar day) {
		this.day = day;
	}

	public MonthState getMonthState() {
		return monthState;
	}

	public void setMonthState(MonthState monthState) {
		this.monthState = monthState;
	}

	public List<ScheduledTask> getTasks() {
		return tasks;
	}

	public void setTasks(List<ScheduledTask> tasks) {
		this.tasks = tasks;
	}

	public int getNumericDay() {
		return day.get(Calendar.DAY_OF_MONTH);
	}

	public boolean isCurrentMonth() {
		return monthState.equals(MonthState.CURRENT_MONTH);
	}

	public boolean getCurrentMonth() {
		return monthState.equals(MonthState.CURRENT_MONTH);
	}

	public TaskDay(Calendar day, MonthState monthState, List<ScheduledTask> tasks) {
		super();
		this.day = day;
		this.monthState = monthState;
		this.tasks = tasks;
		Collections.sort(this.tasks, new ScheduledTaskHourMinComparator());
	}
}
