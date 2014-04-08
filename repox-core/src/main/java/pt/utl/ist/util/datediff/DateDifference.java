package pt.utl.ist.util.datediff;

/**
 * Title: DateDifference
 * Description: Computes the number of days between 2 dates for the Gregorian
 *              calendar only. The two end days are not counted, just the days
 *              in between. Create a new instance of DateDifference everytime
 *              you want to pass in 2 new dates.
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Peter Lok
 * @version 1.0
 */

public class DateDifference {
  private int totalDays = 0; // COUNT OF DAYS
  private int yearOne = 0;   // FIRST YEAR ENTERED
  private int monthOne = 0;  // FIRST MONTH ENTERED
  private int dayOne = 0;    // FIRST DAY ENTERED
  private int yearTwo = 0;   // SECOND YEAR ENTERED
  private int monthTwo = 0;  // SECOND MONTH ENTERED
  private int dayTwo = 0;    // SECOND DAY ENTERED
  private LeapYear leapYear = new LeapYear();
  private DayOfMonth dayOfMonth = new DayOfMonth();
  private int lowYear = 1901;
  private int highYear = 2099;


  /**
   * DateDifference.
   * Constructor that takes 6 arguments. It does not matter which order the
   * two year,month,day argument sets are entered as the object sorts them
   * internally into low and high dates.
   *
   * @param   year1   First year of first date
   * @param   month1  First month of first date
   * @param   day1    First day of first date
   * @param   year2   Second year of second date
   * @param   month2  Second month of second date
   * @param   day2    Second day of second date
   * @exception IllegalArgumentException. For invalid parameters. The parameter
   *            is typically out of range. For example, the year parameter could
   *            be below 1901 or above 2099.  The month could be < 1 or > 12, or
   *            the day of the month does not exist for the specified month
   *            (leap years are accounted for).
   * @return
   */
  public DateDifference(int year1, int month1, int day1, int year2, int month2, int day2) throws IllegalArgumentException {
    boolean errorFlag = false;
    yearOne  = year1;
    monthOne = month1;
    dayOne   = day1;
    yearTwo  = year2;
    monthTwo = month2;
    dayTwo   = day2;

    // VALIDATE INPUT 
    errorFlag = false;
    if (yearOne < lowYear || yearOne > highYear) {
      errorFlag = true;
    } else if (yearTwo < lowYear || yearTwo > highYear) {
      errorFlag = true;
    } else if (monthOne < 1 || monthOne > 12) {
      errorFlag = true;
    } else if (monthTwo < 1 || monthTwo > 12) {
      errorFlag = true;
    } else if (dayOne < 1 || (dayOne > dayOfMonth.getDaysForMonth(yearOne, monthOne))) {
      errorFlag = true;
    } else if (dayTwo < 1 || (dayTwo > dayOfMonth.getDaysForMonth(yearTwo, monthTwo))) {
      errorFlag = true;
    }

    if (errorFlag) {
      IllegalArgumentException exc = new IllegalArgumentException("Bad value in one of the date parameters."+ year1+" "+ month1+" "+ day1+" "+ year2+" "+ month2+" "+ day2+" ");
      throw exc;
    }

  } // END constructor

  /**
   * getDayDifference.
   * Determines the difference in the number of days between a beginning date
   * and end date.  The begin and end dates themselves are not included in the
   * calculation. For example, 1901,01,01 and 1901,01,03 only returns 1 day in
   * between.
   *
   * @param
   * @return  integer. Number of days
   */
  public int getDayDifference() {
    int yearDiffCount = 0;
    int monthDiffCount = 0;
    int tempDays = 0;
    boolean leapYearFlag = false;

    totalDays = 0;
    sortDates();
    // yearOne, monthOne, dayOne must be the low set of dates
    if (yearOne < yearTwo) { // YEARS ARE DIFFERENT
      yearDiffCount = yearTwo - yearOne;
      // ONLY DO NEXT BIT FOR WHOLE YEARS OF DIFFERENCE
      if (yearDiffCount >= 2) {
        totalDays = getYearDiff(yearOne, yearTwo);
      }
    }

    if (yearOne == yearTwo) { //DATES ARE IN THE SAME YEAR
      //MONTHS ARE THE SAME. DON'T NEED TO WORRY ABOUT LEAP DAY AS IT IS COUNTED
      if (monthOne == monthTwo) {
        tempDays = dayTwo - dayOne - 1;
        if (tempDays < 0) tempDays = 0;
        totalDays = totalDays + tempDays;
      }
      else { //MONTHS ARE DIFFERENT
        totalDays = totalDays + dayOfMonth.getDaysForMonth(yearOne, monthOne) - dayOne;
        monthDiffCount = monthTwo - monthOne;
        if (monthDiffCount >= 2) {
          totalDays = totalDays + getMonthDiff(yearOne, monthOne + 1, monthTwo - 1);
        }
        //COMPUTE DAYS FOR HIGH MONTH FROM BEGINNING OF MONTH
        //AGAIN, DON'T HAVE TO WORRY ABOUT LEAP YEAR, COUNTED IN HIGH_DAY
        totalDays = totalDays + dayTwo - 1; //CAN'T COUNT LAST DAY ITSELF
      }
    }
    else { // YEAR DIFFERENCE IS ONE OR MORE YEARS LIKE 1999,2000 OR 1978,1980
      //FOR LOW YEAR DAYS
      totalDays = totalDays + dayOfMonth.getDaysForMonth(yearOne, monthOne) - dayOne;
      monthDiffCount = 12 - monthOne;
      if (monthDiffCount >= 2) { //COUNT ALL MONTHS TO END OF YEAR
        totalDays = totalDays + getMonthDiff(yearOne, monthOne + 1, 12);
      }
      //FOR HIGH YEAR DAYS
      if (monthTwo > 1) {
        totalDays = totalDays + getMonthDiff(yearTwo, 1, monthTwo - 1);
      }
      //COMPUTE DAYS FOR HIGH MONTH FROM BEGINNING OF MONTH
      //AGAIN, DON'T HAVE TO WORRY ABOUT LEAP YEAR, COUNTED IN HIGH_DAY
      totalDays = totalDays + dayTwo - 1; //CAN'T COUNT LAST DAY ITSELF
    }
    return totalDays;
  }

  /**
   * getYearDiff.
   * Calculates the number of days between a low and a high year for complete
   * years only.
   *
   * @param  lowYear  Low year of the two years.
   * @param  highYear High year of the two years.
   * @return integer. Number of days
   */
  private int getYearDiff(int lowYear, int highYear) {
    int x = 0;
    int daysInYear = 0;
    int totDays = 0;
    for (x = (lowYear + 1); x <= (highYear - 1); x++) {
      if (leapYear.isLeapYear(x))
        daysInYear = 366;
      else
        daysInYear = 365;
      totDays = totDays + daysInYear;
    }
    return totDays;
  }

  /**
   * getMonthDiff.
   * Calculates the number of days between a low and a high month within a year.
   * Only for complete months.
   *
   * @param inCurrYear  The year the two months are in.
   * @param lowYear     Low month of the two months.
   * @param highYear    High month of the two months.
   * @return integer.   Number of days
   */
  private int getMonthDiff(int inCurrYear, int lowMonth, int highMonth) {
    int x = 0;
    int daysInMonth = 0;
    int totDays = 0;

    for (x = (lowMonth); x <= (highMonth); x++) {
      daysInMonth = dayOfMonth.getDaysForMonth(inCurrYear, x);
      totDays = totDays + daysInMonth;
    }
    return totDays;
  }

  /**
   * swapDates.
   * Swaps various fields of two dates around based on the portion of the date
   * to swap. The portion of the date to swap around is determined by the
   * swapScope flag that is passed in.  Y = Year - all the fields are to be
   * swapped. M = Month where only the month and day are to be swapped. D = day
   * where only the days are to be swapped.
   *
   * @param swapScope. String with the values "Y","M", or "D"
   */
  private void swapDates(String swapScope) {
    int tempYear = 0;
    int tempMonth = 0;
    int tempDay = 0;

    tempYear = yearOne;
    tempMonth = monthOne;
    tempDay = dayOne;

    if (swapScope.equals("Y")) {
      yearOne = yearTwo;
      yearTwo = tempYear;
      monthOne = monthTwo;
      monthTwo = tempMonth;
      dayOne = dayTwo;
      dayTwo = tempDay;
    } else if (swapScope.equals("M")) {
      monthOne = monthTwo;
      monthTwo = tempMonth;
      dayOne = dayTwo;
      dayTwo = tempDay;
    } else if (swapScope.equals("D")) {
      dayOne = dayTwo;
      dayTwo = tempDay;
    }

  }

  /**
   * sortDates.
   * Sorts two dates into the order lower date and upper date. If the years are
   * to be sorted, then all the fields need to be swapped. If the years are the
   * same and the months are out of order then only swap the month and day
   * fields. If the dates are the same except for the day then just swap the
   * days.
   *
   * @param
   */
  private void sortDates() {
    // SORT BY YEAR FIRST, THEN MONTH, THEN DAY.
    if (yearOne > yearTwo) {
      swapDates("Y");
    } else if ((yearOne == yearTwo) && (monthOne > monthTwo)) {
       swapDates("M");
    } else if ((yearOne == yearTwo) && (monthOne == monthTwo) && (dayOne > dayTwo)) {
      swapDates("D");
    } else { //DO NOTHING. THEY ARE IN THE RIGHT ORDER
    }
  } // END sortDates
} // END CLASS DateDifference