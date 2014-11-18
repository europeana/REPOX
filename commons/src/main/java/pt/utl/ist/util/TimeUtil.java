package pt.utl.ist.util;

import java.util.Date;

/**
 */
public class TimeUtil {
    /** TimeUtil SHORT_DATE_FORMAT */
    public static final String SHORT_DATE_FORMAT         = "yyyy-MM-dd";
    /** TimeUtil DATE_FORMAT */
    public static final String DATE_FORMAT               = "dd/MM/yyyy";
    /** TimeUtil LONG_DATE_FORMAT */
    public static final String LONG_DATE_FORMAT          = "yyyy-MM-dd HH:mm:ss";
    /** TimeUtil LONG_DATE_FORMAT_NO_SECS */
    public static final String LONG_DATE_FORMAT_NO_SECS  = "yyyy-MM-dd HH:mm";
    /** TimeUtil LONG_DATE_FORMAT_TIMEZONE */
    public static final String LONG_DATE_FORMAT_TIMEZONE = "yyyy-MM-dd HH:mm:ss zz";
    /** TimeUtil LONG_DATE_FORMAT_COMPACT */
    public static final String LONG_DATE_FORMAT_COMPACT  = "yyyyMMdd_HHmmss";
    /** TimeUtil LONG_DATE_FORMAT_WEB */
    public static final String LONG_DATE_FORMAT_WEB      = "HH:mm dd/MM/yyyy";
    /** TimeUtil TIME_FORMAT */
    public static final String TIME_FORMAT               = "HH:mm";
    public static long         startTimerMillis;
    public static long         lastTimerMillis;
    public static long[]       timerArrayMillis          = new long[10];

    /**
     * 
     */
    public static void startTimers() {
        startTimerMillis = new Date().getTime();
        lastTimerMillis = startTimerMillis;
        for (int i = 0; i < timerArrayMillis.length; i++) {
            timerArrayMillis[i] = startTimerMillis;
        }
    }

    /**
     * @return long of the time difference of the last timer
     */
    public static long getTimeSinceLastTimer() {
        long now = new Date().getTime();
        long timeDifference = now - lastTimerMillis;
        lastTimerMillis = now;
        return timeDifference;
    }

    /**
     * @param timerPos
     * @return long of the time difference of the last timer
     */
    public static long getTimeSinceLastTimerArray(int timerPos) {
        long now = new Date().getTime();
        long timeDifference = now - timerArrayMillis[timerPos];
        timerArrayMillis[timerPos] = now;
        lastTimerMillis = now;
        return timeDifference;
    }

    /**
     * @return long of the time difference of the start timer
     */
    public static long getTotalTime() {
        long now = new Date().getTime();
        long timeDifference = now - startTimerMillis;
        lastTimerMillis = now;
        return timeDifference;
    }

}
