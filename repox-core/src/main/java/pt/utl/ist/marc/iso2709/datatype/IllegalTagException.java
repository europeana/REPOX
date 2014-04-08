/**
 * IllegalTagException.java Version 0.2, November 2001
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
 * <p><code>IllegalTagException</code> is thrown when a tag is supplied
 * that is invalid.  </p>
 *
 * @author Bas Peters - <a href="mailto:mail@bpeters.com">mail@bpeters.com</a>
 * @version 0.2
 */
public class IllegalTagException extends IllegalArgumentException {

    /**
     * <p>Creates an <code>Exception</code> indicating that the name of the
     * tag is invalid.</p>
     *
     * @param tag the tag name
     * @see Tag
     */
    public IllegalTagException(String tag) {
	super(new StringBuffer()
	    .append("The tag name ")
	    .append(tag)
	    .append(" is not a valid tag name.")
	    .toString());
  }

    /**
     * <p>Creates an <code>Exception</code> indicating that the name of the
     * tag is invalid.</p>
     *
     * @param tag the tag name
     * @param reason the reason why the exception is thrown
     */
    public IllegalTagException(String tag, String reason) {
	super(new StringBuffer()
	    .append("The tag name ")
	    .append(tag)
	    .append(" is not valid: ")
	    .append(reason)
	    .append(".")
	    .toString());
  }

}

// End of IllegalTagException.java
