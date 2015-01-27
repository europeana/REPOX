package pt.utl.ist.marc.iso2709;

/**
 * <p>
 * <code>ParseRecordException</code> is thrown when an error occurs while
 * parsing a MARC record.
 * </p>
 * 
 * @author Bas Peters - <a href="mailto:mail@bpeters.com">mail@bpeters.com</a>
 * @version 0.2
 */
public class ParseRecordException extends RuntimeException {

    /**
     * <p>
     * Creates an <code>Exception</code> indicating that an error occured while
     * parsing the record.
     * </p>
     * 
     * @param reason
     *            the reason why the exception is thrown
     */
    public ParseRecordException(String reason) {
        super(new StringBuffer().append("Invalid record: ").append(reason).append(".").toString());
    }

}

// End of ParseRecordException.java
