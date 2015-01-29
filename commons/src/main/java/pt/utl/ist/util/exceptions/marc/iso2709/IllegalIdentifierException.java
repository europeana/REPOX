package pt.utl.ist.util.exceptions.marc.iso2709;

import pt.utl.ist.marc.iso2709.datatype.Identifier;

/**
 * <p>
 * <code>IllegalTagException</code> is thrown when a data element identifier is
 * supplied that is invalid.
 * </p>
 * 
 * @author Bas Peters - <a href="mailto:mail@bpeters.com">mail@bpeters.com</a>
 * @version 0.2
 */
public class IllegalIdentifierException extends IllegalArgumentException {

    /**
     * <p>
     * Creates an <code>Exception</code> indicating that the data element
     * identifier is invalid.
     * </p>
     * 
     * @param identifier
     *            the invalid data element identifier
     * @see Identifier
     */
    public IllegalIdentifierException(char identifier) {
        super(new StringBuffer().append("The data element identifier ").append(identifier).append(" is not a valid data element identifier.").toString());
    }

}

// End of IllegalIdentifierException.java
