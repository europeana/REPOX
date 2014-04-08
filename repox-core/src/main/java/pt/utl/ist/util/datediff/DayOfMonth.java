package pt.utl.ist.util.datediff;

/**
 * Title: DayOfMonth
 * Description: Populates an array of Monthly values in the order the months
 * occur and allows for the lookup of the number of days for any particular
 * month.
 *
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Peter Lok
 * @version 1.0
 */
public class DayOfMonth {
  Month[] monthList = new Month[13];
  //FOR INTERNAL USE ONLY
  private class Month {
    String monthName;
    int    daysInMonth;
    public Month(int inDaysInMonth, String inMonthName) {
      daysInMonth = inDaysInMonth;
      monthName   = inMonthName;
    }
    //RETURN DAYS FOR THE CURRENT MONTH
    public int getDays() {
      return daysInMonth;
    }
  } //END CLASS MONTH

  public DayOfMonth() {
    monthList[0] = null;
    monthList[1] = new Month(31,"January");
    monthList[2] = new Month(28,"February");
    monthList[3] = new Month(31,"March");
    monthList[4] = new Month(30,"April");
    monthList[5] = new Month(31,"May");
    monthList[6] = new Month(30,"June");
    monthList[7] = new Month(31,"July");
    monthList[8] = new Month(31,"August");
    monthList[9] = new Month(30,"September");
    monthList[10] = new Month(31,"October");
    monthList[11] = new Month(30,"November");
    monthList[12] = new Month(31,"December");
  } //END DayOfMonth

  /**
   * getDaysForMonth.
   * Return the number of days in a month for a particular year. Adds an extra
   * day if the month is February in a leap year.
   *
   * @param  inYear  Year the month occurs in. For leap year calculation.
   * @param  inMonth Month you wish to find the number of days for.
   * @return integer Returns the number of days in a given month.
   */
  public int getDaysForMonth(int inYear, int inMonth) {
    int daysForMonth = 0;
    LeapYear leapYear = new LeapYear();
    boolean leapYearFlag = false;

    leapYearFlag = leapYear.isLeapYear(inYear);

    daysForMonth = monthList[inMonth].getDays();
    if ((leapYearFlag) && (inMonth == 2))
      daysForMonth++;

    return daysForMonth;
  }

} // END CLASS DayOfMonth