package pt.utl.ist.marc.iso2709;

import pt.utl.ist.util.marc.Leader;

/**
 * <p>
 * <code>MARCHandler</code> defines a set of Java callbacks to handle MARC
 * records.
 * </p>
 * 
 * <p>
 * The following code from the <code>TaggedPrinter</code> example demonstrates
 * the use of the <code>MARCHandler</code> interface.
 * </p>
 * 
 * @author Bas Peters - <a href="mailto:mail@bpeters.com">mail@bpeters.com</a>
 * @version 0.2
 */
public interface MARCHandler {

    /**
     * <p>
     * Receives notification at the start of the MARC file.
     * </p>
     * 
     */
    public abstract void startTape();

    /**
     * <p>
     * Receives notification at the end of the MARC file.
     * </p>
     * 
     */
    public abstract void endTape();

    /**
     * <p>
     * Receives notification at the start of each MARC record.
     * </p>
     * 
     * @param leader
     *            the {@link Leader} object containing the record label
     */
    public abstract void startRecord(Leader leader);

    /**
     * <p>
     * Receives notification at the end of each MARC record.
     * </p>
     * 
     */
    public abstract void endRecord();

    /**
     * <p>
     * Receives notification of a control field.
     * </p>
     * 
     * @param tag
     *            the tag name
     * @param data
     *            the control field data
     */
    public abstract void controlField(String tag, String data);

    /**
     * <p>
     * Receives notification at the start of each data field.
     * </p>
     * 
     * @param tag
     *            the tag name
     * @param ind1
     *            the first indicator value
     * @param ind2
     *            the second indicator value
     */
    public abstract void startDataField(String tag, char ind1, char ind2);

    /**
     * <p>
     * Receives notification at the end of each data field
     * </p>
     * 
     * @param tag
     *            the tag name
     */
    public abstract void endDataField(String tag);

    /**
     * <p>
     * Receives notification of a data element (subfield).
     * </p>
     * 
     * @param identifier
     * 
     * @param data
     *            the data element
     */
    public abstract void subfield(char identifier, String data);

}

// End of MARCHandler.java
