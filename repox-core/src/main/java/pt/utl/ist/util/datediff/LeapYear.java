package pt.utl.ist.util.datediff;

/**
 * Title: LeapYear
 * Description: Determines if any specified year in the Gregorian calendar is a
 * leap year.
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Peter Lok
 * @version 1.0
 */

public class LeapYear {

  public LeapYear() {
  } // END constructor

  /**
   * isLeapYear.
   * For Gregorian calendar only. Determines if the year specified is a leap
   * year and returns true if it is.
   *
   * @param inYear The year to check if it is a leap year.
   * @return boolean True if it is a leap year.
   */
  public boolean isLeapYear(int inYear) {
    boolean leapFlag = false;
    if (inYear % 4 == 0) {
      if (inYear % 100 == 0) {
        if (inYear % 400 == 0)
          leapFlag = true;
        else
          leapFlag = false;
      } else
        leapFlag = true;
    }
    return leapFlag;
  } // END isLeapYEar
} // END CLASS LeapYear