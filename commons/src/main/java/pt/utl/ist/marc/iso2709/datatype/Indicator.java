package pt.utl.ist.marc.iso2709.datatype;

/**
 * <p>
 * <code>Indicator</code> defines behaviour to validate indicator values.
 * </p>
 * 
 * <p>
 * An indicator is a data element associated with a data field that supplies
 * additional information about the field. According to the MARC standard an
 * indicator value may be any ASCII lowercase alphabetic, numeric, or blank.
 * Indicators are not used in control fields.
 * </p>
 * 
 * @author Bas Peters - <a href="mailto:mail@bpeters.com">mail@bpeters.com</a>
 * @version 0.2
 */
public class Indicator extends Datatype {

    /**
     * <p>
     * Returns true if the given character is a valid indicator value.
     * </p>
     * 
     * <p>
     * The method returns true if:
     * </p>
     * <ul>
     * <li>The indicator is a digit,
     * <li>or a lowercase letter,
     * <li>or a blank.
     * </ul>
     * <p>
     * 
     * @param value
     *            the indicator value
     * @return <code>boolean</code> - true if the given character is a valid
     *         indicator, false if not
     */
    public static boolean isValid(char value) {
        if (isDigit(value) || isLowercaseLetter(value) || isBlank(value)) return true;
        return false;
    }

}

// End of Indicator.java
