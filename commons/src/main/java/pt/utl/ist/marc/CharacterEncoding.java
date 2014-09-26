package pt.utl.ist.marc;

/**
 */
public enum CharacterEncoding {
    /** CharacterEncoding UTF_8 */
    UTF_8,
    /** CharacterEncoding ISO8859_1 */
    ISO8859_1,
    /** CharacterEncoding ISO8859_2 */
    ISO8859_2,
    /** CharacterEncoding ISO8859_3 */
    ISO8859_3,
    /** CharacterEncoding ISO8859_4 */
    ISO8859_4,
    /** CharacterEncoding ISO8859_5 */
    ISO8859_5,
    /** CharacterEncoding ISO8859_6 */
    ISO8859_6,
    /** CharacterEncoding ISO8859_7 */
    ISO8859_7,
    /** CharacterEncoding ISO8859_8 */
    ISO8859_8,
    /** CharacterEncoding ISO8859_9 */
    ISO8859_9,
    /** CharacterEncoding ISO8859_10 */
    ISO8859_10,
    /** CharacterEncoding ISO8859_11 */
    ISO8859_11,
    /** CharacterEncoding ISO8859_12 */
    ISO8859_12,
    /** CharacterEncoding ISO8859_13 */
    ISO8859_13,
    /** CharacterEncoding ISO8859_14 */
    ISO8859_14,
    /** CharacterEncoding ISO8859_15 */
    ISO8859_15,
    /** CharacterEncoding ISO8859_16 */
    ISO8859_16,
    /** CharacterEncoding WINDOWS_1250 */
    WINDOWS_1250,
    /** CharacterEncoding WINDOWS_1252 */
    WINDOWS_1252,
    /** CharacterEncoding ISO_5426 */
    ISO_5426,
    /** CharacterEncoding ISO_6937 */
    ISO_6937,
    /** CharacterEncoding ANSEL */
    ANSEL,
    /** CharacterEncoding CP1251 */
    CP1251;

    @SuppressWarnings("javadoc")
    public static CharacterEncoding[] getValues() {
        return values();
    }

    /**
     * @param value
     * @return the CharacterEncoding that corresponds to value or null if its
     *         not supported.
     */
    public static CharacterEncoding get(String value) {
        for (CharacterEncoding currentCharacterEncoding : values()) {
            if (currentCharacterEncoding.toString().equals(value)) { return currentCharacterEncoding; }
        }

        return null;
    }

    @Override
    public String toString() {
        return name().replace('_', '-');
    }
}
