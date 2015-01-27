package pt.utl.ist.marc.iso2709;

/**
 * <p>
 * <code>ParseVariableFieldException</code> is thrown when an error occurs while
 * parsing a variable field (control field or data field).
 * </p>
 * 
 * @author Bas Peters - <a href="mailto:mail@bpeters.com">mail@bpeters.com</a>
 * @version 0.2
 */
public class ParseVariableFieldException extends RuntimeException {

    /**
     * <p>
     * Creates an <code>Exception</code> indicating that an error occured while
     * parsing a variable field.
     * </p>
     * 
     * @param tag
     *            the tag name
     * @param reason
     *            the reason why the exception is thrown
     */
    public ParseVariableFieldException(String tag, String reason) {
        super(new StringBuffer().append("Invalid variable field for tag ").append(tag).append(": ").append(reason).append(".").toString());
    }

}

// End of ParseVariableFieldException.java
