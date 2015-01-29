package pt.utl.ist.marc.iso2709.datatype;

public abstract class Datatype {

    /** The character blank or space (ASCII octal 040). */
    public static final char blank = '\040';

    /**
     * <p>
     * Returns true if the given value is valid.
     * </p>
     * 
     * @return true if the given value is valid.
     */
    public static boolean isValid() {
        return true;
    }

    /**
     * <p>
     * Returns true if the given value is a digit.
     * </p>
     * <p>
     * (ASCII octal 060-067, 070, 071, decimal 048-057).
     * </p>
     * 
     * @param value
     *            the character to validate
     * @return true if the given value is a digit.
     */
    public static boolean isDigit(char value) {
        int i = (int)value;
        if ((i >= 48 && i <= 57)) return true;
        return false;
    }

    /**
     * <p>
     * Returns true if the given value is a lowercase letter.
     * </p>
     * <p>
     * (ASCII octal 141-172, decimal 097-122).
     * </p>
     * 
     * @param value
     *            the character to validate
     * @return true if the given value is a lowercase letter.
     */
    public static boolean isLowercaseLetter(char value) {
        int i = (int)value;
        if ((i >= 97 && i <= 122)) return true;
        return false;
    }

    /**
     * <p>
     * Returns true if the given value is an uppercase letter.
     * </p>
     * <p>
     * (ASCII octal 101-132, decimal 065-090).
     * </p>
     * 
     * @param value
     *            the character to validate
     * @return true if the given value is an uppercase letter.
     */
    public static boolean isUppercaseLetter(char value) {
        int i = (int)value;
        if ((i >= 65 && i <= 90)) return true;
        return false;
    }

    /**
     * <p>
     * Returns true if the given value is a blank.
     * </p>
     * <p>
     * (ASCII octal 040, decimal 032).
     * </p>
     * 
     * @param value
     *            the character to validate
     * @return true if the given value is a blank.
     */
    public static boolean isBlank(char value) {
        if (value == blank) return true;
        return false;
    }

}

// End of Datatype.java