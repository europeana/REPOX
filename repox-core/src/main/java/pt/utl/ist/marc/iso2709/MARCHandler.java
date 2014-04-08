/**
 * MARCHandler.java Version 0.2, November 2001
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

import pt.utl.ist.marc.util.Leader;

/**
 * <p><code>MARCHandler</code> defines a set of Java callbacks to handle
 * MARC records.  </p>
 *
 * <p>The following code from the <code>TaggedPrinter</code> example
 * demonstrates the use of the  <code>MARCHandler</code> interface.</p>
 *
 * <pre>
 * class MARCHandlerImpl implements MARCHandler {
 *
 *     public void startTape() {}
 *
 *     public void startRecord(Leader leader) {
 *         System.out.println("Leader " + leader.getSerializedForm());
 *     }
 *
 *     public void controlField(String tag, char[] data) {
 *	   System.out.println(tag + " " + data);
 *     }
 *
 *     public void startDataField(String tag, char ind1, char ind2) {
 *	   System.out.print(tag + " " + ind1 + ind2);
 *     }
 *
 *     public void subfield(char identifier, char[] data) {
 *         // the dollar sign is used as a character
 *         // representation for delimiter
 *         System.out.print("$" + identifier + data);
 *     }
 *
 *     public void endDataField(String tag) {
 *	   System.out.println();
 *     }
 *
 *     public void endRecord() {
 *         System.out.println();
 *     }
 *
 *     public void endTape() {}
 *
 * }
 * </pre>
 *
 * @author Bas Peters - <a href="mailto:mail@bpeters.com">mail@bpeters.com</a>
 * @version 0.2
 */
public interface MARCHandler {

    /**
     * <p>Receives notification at the start of the MARC file.</p>
     *
     */
    public abstract void startTape();

    /**
     * <p>Receives notification at the end of the MARC file.</p>
     *
     */
    public abstract void endTape();

    /**
     * <p>Receives notification at the start of each MARC record.</p>
     *
     * @param leader the {@link Leader} object containing the record label
     */
    public abstract void startRecord(Leader leader);

    /**
     * <p>Receives notification at the end of each MARC record.</p>
     *
     */
    public abstract void endRecord();

    /**
     * <p>Receives notification of a control field.</p>
     *
     * @param tag the tag name
     * @param data the control field data
     */
    public abstract void controlField(String tag, String data);

    /**
     * <p>Receives notification at the start of each data field.</p>
     *
     * @param tag the tag name
     * @param ind1 the first indicator value
     * @param ind2 the second indicator value
     */
    public abstract void startDataField(String tag, char ind1, char ind2);

    /**
     * <p>Receives notification at the end of each data field</p>
     *
     * @param tag the tag name
     */
    public abstract void endDataField(String tag);

    /**
     * <p>Receives notification of a data element (subfield).</p>
     *
     * @param code the data element identifier
     * @param data the data element
     */
    public abstract void subfield(char identifier, String data);

}

// End of MARCHandler.java
