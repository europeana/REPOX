package pt.utl.ist.repox.util;

import java.util.Calendar;

public enum Month {
	JANUARY (Calendar.JANUARY),
	FEBRUARY (Calendar.FEBRUARY),
	MARCH (Calendar.MARCH),
	APRIL (Calendar.APRIL),
	MAY (Calendar.MAY),
	JUNE (Calendar.JUNE),
	JULY (Calendar.JULY),
	AUGUST (Calendar.AUGUST),
	SEPTEMBER (Calendar.SEPTEMBER),
	OCTOBER (Calendar.OCTOBER),
	NOVEMBER (Calendar.NOVEMBER),
	DECEMBER (Calendar.DECEMBER);

	private int monthNumber;

	public int getMonthNumber() {
		return monthNumber;
	}
	
	public static Month getMonth(int monthNumber) {
		for (Month	currentMonth : Month.values()) {
			if(currentMonth.getMonthNumber() == monthNumber) {
				return currentMonth;
			}
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
