/*
 * Directory.java
 *
 * Created on 20 de Julho de 2002, 19:18
 */

package pt.utl.ist.marc.util;

import pt.utl.ist.marc.Record;

import java.text.DecimalFormat;

/**
 * <p><code>Directory</code> is a helper class to build a MARC
 * directory.  </p>
 * <p>The directory is an index to the location of the variable
 * fields within a record. The directory consists of entries. Each
 * directory entry is 12 characters in length. The structure of each
 * entry according to the MARC standard is as follows:</p>
 * <pre>
 * TAG     LENGTH_OF_FIELD     STARTING_CHARACTER_POSITION
 * 00-02   03-06               07-11
 * </pre>
 * <p>This structure is returned by the {@link #getSerializedForm()}
 * method.</p>
 * <p>The starting character position gives the starting position of
 * the variable field to which the entry corresponds relative to
 * the base address of data of the record. A starting character position
 * or length of field of fewer than five digits is right justified and
 * unused positions contain zero's.</p>
 *
 * @author Bas Peters - <a href="mailto:mail@bpeters.com">mail@bpeters.com</a>
 * @version 0.2
 */

public class Directory {

    /** The directory. */
    protected StringBuffer directory = new StringBuffer();
    /** Number format for the tag. */
    protected DecimalFormat formatTag = new DecimalFormat("000");
    /** Number format for the length of field. */
    protected DecimalFormat formatLength = new DecimalFormat("0000");
    /** Number format for the starting character position. */
    protected DecimalFormat formatStart = new DecimalFormat("00000");

    /** The starting character position. */
    protected int start = 0;
    /** The length of field for the previous variable field. */
    protected int prev = 0;

    /**
     * <p>Default constructor.</p>
     */
    public Directory() {}

    /**
     * <p>Adds a new entry to the MARC directory.</p>
     *
     * @param tag the tag name
     * @param length the length of field
     */
    public void add(String tag, int length) {
        start = start + prev;
        prev = length;
        directory.append(tag)
	    .append(formatLength.format(length))
	    .append(formatStart.format(start));
    }

    public void add(short tag, int length) {
        add(formatStart.format(tag),  length);
    }    
    
    /**
     * <p>Returns a <code>String</code> representation for the directory
     * following the structure of the MARC directory.</p>
     *
     * @return <code>String</code> - the directory
     */
    public String getSerializedForm() {
        return directory.toString() + Record.FT;
    }

    /**
     * <p>Returns the length of the serialized form of the directory.</p>
     *
     * @return <code>int</code> - the directory length
     */
    public int getLength() {
        return this.getSerializedForm().length();
    }

}
