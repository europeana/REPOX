package pt.utl.ist.repox.util;

import java.util.Date;

public class TimeUtil {
	public static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";
	public static final String LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String LONG_DATE_FORMAT_NO_SECS = "yyyy-MM-dd HH:mm";
	public static final String LONG_DATE_FORMAT_TIMEZONE = "yyyy-MM-dd HH:mm:ss zz";
	public static final String LONG_DATE_FORMAT_COMPACT = "yyyyMMdd_HHmmss";    
	public static final String LONG_DATE_FORMAT_WEB = "HH:mm dd/MM/yyyy";

	public static long startTimerMillis;
	public static long lastTimerMillis;
	public static long[] timerArrayMillis = new long[10];

	public static void startTimers() {
		startTimerMillis = new Date().getTime();
		lastTimerMillis = startTimerMillis;
		for (int i = 0; i < timerArrayMillis.length; i++) {
			timerArrayMillis[i] = startTimerMillis;
		}
	}

	public static long getTimeSinceLastTimer() {
		long now = new Date().getTime();
		long timeDifference = now - lastTimerMillis;
		lastTimerMillis = now;
		return timeDifference;
	}

	public static long getTimeSinceLastTimerArray(int timerPos) {
		long now = new Date().getTime();
		long timeDifference = now - timerArrayMillis[timerPos];
		timerArrayMillis[timerPos] = now;
		lastTimerMillis = now;
		return timeDifference;
	}

	public static long getTotalTime() {
		long now = new Date().getTime();
		long timeDifference = now - startTimerMillis;
		lastTimerMillis = now;
		return timeDifference;
	}

}
