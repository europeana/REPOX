/**
 * ParseDirectoryException.java Version 0.2, November 2001
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

package pt.utl.ist.marc.iso2709;

/**
 * <p><code>ParseDirectoryException</code> is thrown when an error
 * occurs while parsing the directory of a MARC record.  </p>
 *
 * @author Bas Peters - <a href="mailto:mail@bpeters.com">mail@bpeters.com</a>
 * @version 0.2
 */
public class ParseDirectoryException extends RuntimeException {

    /**
     * <p>Creates an <code>Exception</code> indicating that an error
     * occured while parsing the directory.</p>
     *
     * @param reason the reason why the exception is thrown
     */
    public ParseDirectoryException(String reason) {
	super(new StringBuffer()
	    .append("Invalid directory: ")
	    .append(reason)
	    .append(".")
	    .toString());
    }

}

// End of ParseDirectoryException.java
