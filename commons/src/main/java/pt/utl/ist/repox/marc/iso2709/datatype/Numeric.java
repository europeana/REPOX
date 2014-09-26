/**
 * Numeric.java Version 0.2, November 2001
 *
 * Copyright (C) 2001  Bas Peters (mail@bpeters.com)
 *
 * This file is part of James (Java MARC Events).
 *
 * James is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * James is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with James; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package pt.utl.ist.repox.marc.iso2709.datatype;

/**
 * <p>
 * <code>Numeric</code> defines behaviour to validate a numeric value (decimal
 * integer).
 * </p>
 * 
 * @author Bas Peters - <a href="mailto:mail@bpeters.com">mail@bpeters.com</a>
 * @version 0.2
 */
public class Numeric extends Datatype {

    /**
     * <p>
     * Returns true if the given value is a numeric value.
     * </p>
     * 
     * @param value
     *            the <code>String</code> to validate
     * @return <code>boolean</code> - true if the value is a numeric value,
     *         false if the value is not numeric
     */
    public static boolean isValid(String value) {
        int len = value.length();
        if (len == 0) return false;
        int i = 0;
        do {
            switch (value.charAt(i)) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                break;
            default:
                return false;
            }
        } while (++i < len);
        return true;
    }

    /**
     * <p>
     * Returns the integer value for a String value
     * </p>
     * 
     * @param value
     *            the <code>String</code> to convert
     * @return <code>int</code> - the String parsed as int
     */
    public static int getValue(String value) {
        return Integer.parseInt(value);
    }
}

// End of Numeric.java

