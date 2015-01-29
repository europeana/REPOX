package pt.utl.ist.util.exceptions.marc.iso2709;

import pt.utl.ist.marc.iso2709.datatype.Indicator;

/**
 * <p>
 * <code>IllegalIndicatorException</code> is thrown when an indicator value is
 * supplied that is invalid.
 * </p>
 * 
 * @author Bas Peters - <a href="mailto:mail@bpeters.com">mail@bpeters.com</a>
 * @version 0.2
 */
public class IllegalIndicatorException extends IllegalArgumentException {

    /**
     * <p>
     * Creates an <code>Exception</code> indicating that the name of the
     * indicator is invalid.
     * </p>
     * 
     * @param ind
     *            the illegal indicator
     * @see Indicator
     */
    public IllegalIndicatorException(char ind) {
        super(new StringBuffer().append("The indicator ").append(ind).append(" is not a valid indicator.").toString());
    }

    /**
     * <p>
     * Creates an <code>Exception</code> indicating that the name of the
     * indicator is invalid.
     * </p>
     * 
     * @param tag
     *            the tag value
     * @param ind
     *            the indicator
     * @see Indicator
     */
    public IllegalIndicatorException(String tag, char ind) {
        super(new StringBuffer().append("The indicator ").append(ind).append(" for tag ").append(tag).append(" is not a valid indicator.").toString());
    }

}

// End of IllegalIndicatorException.java
