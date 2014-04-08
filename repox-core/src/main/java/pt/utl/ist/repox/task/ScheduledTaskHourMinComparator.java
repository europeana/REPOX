package pt.utl.ist.repox.task;

import java.util.Calendar;
import java.util.Comparator;

public class ScheduledTaskHourMinComparator implements Comparator<ScheduledTask> {
	public int compare(ScheduledTask t1, ScheduledTask t2) {
		Calendar t1Cal = t1.getFirstRun();
		Calendar t2Cal = t2.getFirstRun();
		int t1TotalTime = t1Cal.get(Calendar.HOUR_OF_DAY) * 100 + t1Cal.get(Calendar.MINUTE);
		int t2TotalTime = t2Cal.get(Calendar.HOUR_OF_DAY) * 100 + t2Cal.get(Calendar.MINUTE);
		
		return (t1TotalTime > t2TotalTime ? 1 : -1);
	}
}