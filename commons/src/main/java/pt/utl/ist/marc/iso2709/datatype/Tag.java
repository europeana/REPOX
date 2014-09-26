/**
 * Tag.java Version 0.2, November 2001
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

package pt.utl.ist.marc.iso2709.datatype;

/**
 * <p><code>Tag</code> defines behaviour to validate MARC tags.  </p>
 *
 * <p>A MARC tag is a three character string used to identify an
 * associated variable field. According to the MARC standard the tag may
 * consist of ASCII numeric characters (decimal integers 0-9) and/or
 * ASCII alphabetic characters (uppercase or lowercase, but not both).</p>
 *
 * @author Bas Peters - <a href="mailto:mail@bpeters.com">mail@bpeters.com</a>
 * @version 0.2
 */
public class Tag extends Datatype {

    /** The value zero (ASCII octal 060). */
    protected static final char zero = '\060';

    /**
     * Creates a new instance of this class.
     */
    protected Tag() {}

    /**
     * <p>Returns true if the given value is a valid tag value.  </p>
     *
     * <p>The method returns false if:</p>
     * <ul>
     * <li>the tag length is not three,
     * <li>the tag contains a character other than an ASCII numeric or
     * alphabetic character.
     * </ul>
     * <p><b>Note:</b> mixing uppercase and lowercase letters is not
     * validated.</p>
     *
     * @param tag the tag name
     */
    public static boolean isValid(String tag) {
	boolean uppercase = false;
	if (tag.length() != 3)
	    return false;
	for (int i = 0; i < tag.length(); i++) {
	    if (! isValidChar(tag.charAt(i)))
		return false;
	}
	return true;
    }

    /**
     * <p>Checks if the given character is allowed within a tag.  </p>
     *
     * <p>The method returns true if:</p>
     * <ul>
     * <li>The tag is a digit
     * <li>or a lowercase letter
     * <li>or an uppercase letter.
     * </ul><p>
     * @param value
     * @return <code>boolean</code> - a valid (true) or invalid (false)
     *                                tag character
     */
    public static boolean isValidChar(char value) {
	if (isDigit(value) ||
	    isLowercaseLetter(value) ||
	    isUppercaseLetter(value))
	    return true;
	return false;
    }

    /**
     * <p>Returns true if the tag identifies a control number field.  </p>
     *
     * <p>The method returns false if the tag does not equals 001.</p>
     *
     * @param tag the tag name
     * @return <code>boolean</code> - tag identifies a control number field
     *                                (true) or not (false)
     */
    public static boolean isControlNumberField(String tag) {
	if (tag != "001")
	    return false;
	return true;
    }

    /**
     * <p>Returns true if the tag identifies a control field.  </p>
     *
     * <p>The method returns false if the tag does not begin with
     * two zero's.</p>
     *
     * @param tag the tag name
     * @return <code>boolean</code> - tag identifies a control field (true)
     *                                or a data field (false)
     */
    public static boolean isControlField(String tag) {
	if (tag.charAt(0) != zero)
	    return false;
	if (tag.charAt(1) != zero)
	    return false;
    if (! isValidChar(tag.charAt(2)))
	    return false;
	return true;
    }

    /**
     * <p>Returns true if the tag identifies a data field.  </p>
     *
     * <p>The method returns false if the tag begins with two zero's.</p>
     *
     * @param tag the tag name
     * @return <code>boolean</code> - tag identifies a data field (true)
     *                                or a control field (false)
     */
    public static boolean isDataField(String tag) {
	if (! isControlField(tag))
	    return true;
	return false;
    }

}

// End of Tag.java
