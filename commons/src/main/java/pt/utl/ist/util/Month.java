package pt.utl.ist.util;

import java.util.Calendar;

/**
 */
public enum Month {
    /** Month JANUARY */
    JANUARY(Calendar.JANUARY),
    /** Month FEBRUARY */
    FEBRUARY(Calendar.FEBRUARY),
    /** Month MARCH */
    MARCH(Calendar.MARCH),
    /** Month APRIL */
    APRIL(Calendar.APRIL),
    /** Month MAY */
    MAY(Calendar.MAY),
    /** Month JUNE */
    JUNE(Calendar.JUNE),
    /** Month JULY */
    JULY(Calendar.JULY),
    /** Month AUGUST */
    AUGUST(Calendar.AUGUST),
    /** Month SEPTEMBER */
    SEPTEMBER(Calendar.SEPTEMBER),
    /** Month OCTOBER */
    OCTOBER(Calendar.OCTOBER),
    /** Month NOVEMBER */
    NOVEMBER(Calendar.NOVEMBER),
    /** Month DECEMBER */
    DECEMBER(Calendar.DECEMBER);

    private int monthNumber;

    public int getMonthNumber() {
        return monthNumber;
    }

    public static Month getMonth(int monthNumber) {
        for (Month currentMonth : Month.values()) {
            if (currentMonth.getMonthNumber() == monthNumber) { return currentMonth; }
        }
        return null;
    }

    public static Month getCurrentMonth() {
        int currentMonthInt = Calendar.getInstance().get(Calendar.MONTH);
        return getMonth(currentMonthInt);
    }

    Month(int monthNumber) {
        this.monthNumber = monthNumber;
    }
}
