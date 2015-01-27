package pt.utl.ist.marc.iso2709.datatype;

/**
 * <p>
 * <code>Identifier</code> defines the data element identifier for a data
 * element.
 * </p>
 * 
 * <p>
 * A data element identifier is a one-character code used to identify data
 * elements within a variable field. According to the MARC stamdard a data
 * element identifier may be any ASCII lowercase alphabetic, numeric, or graphic
 * symbol except blank.
 * </p>
 * 
 * @author Bas Peters - <a href="mailto:mail@bpeters.com">mail@bpeters.com</a>
 * @version 0.2
 */
public class Identifier extends Datatype {

    /**
     * <p>
     * Checks if the given character is a valid data element identifier.
     * </p>
     * 
     * @param value
     *            the data element identifier
     * @return <p>
     *         The method returns false if:
     *         </p>
     *         <ul>
     *         <li>the character value is not a valid ASCII value (decimal value
     *         exceeds 127)
     *         <li>the character value is an uppercase letter (ASCII decimal
     *         character between 065-090)
     *         <li>the character value is a blank (ASCII decimal 032)
     *         </ul>
     *         <p>
     */
    public static boolean isValid(char value) {
        if ((int)value > 127) return false;
        if (isUppercaseLetter(value)) return false;
        if (value == blank) return false;
        return true;
    }

}

// Identifier.java