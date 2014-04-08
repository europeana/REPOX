/*
 * UnicodeUtil.java
 *
 * Created on 3 de Julho de 2004, 12:26
 */

package pt.utl.ist.characters;

/**
 *
 * @author  Nuno Freire
 */
public class UnicodeUtil {
    
    /**
     * Unicode value used when translating into Unicode encoding form
     * and there is no existing character.
     */
	public static final char REPLACEMENT_CHAR = '\uFFFD';

    /**
     * Value returned in <code><a href="#bounds32(java.lang.String, int)">bounds32()</a></code>.
     */
    public static final int SINGLE = 1, LEAD = 2, TRAIL = 5;
    
    
   /**
     * Maximum code point values for UTF-32.
     */
    private static final int MAX_UNICODE = 0x10FFFF;

   /**
     * Maximum values for Basic code points (BMP).
     */
    private static final int MAX_BASIC = 0xFFFF;

   /**
     * Minimum value for Supplementary code points (SMP).
     */
    private static final int MIN_SUPPLEMENTARY = 0x10000;

    /**
     * Used to mask off single plane in checking for NON_CHARACTER
     */
    private static final int PLANE_MASK = 0xFFFF;    
    
    /**
     * Range of non-characters in each plane
     */
    private static final int
        NON_CHARACTER_BASE = 0xFFFE,
        NON_CHARACTER_END = 0xFFFF;

    // useful statics and tables for fast lookup

	/**
	 * Values for surrogate detection. X is a surrogate iff X & SURROGATE_MASK == SURROGATE_MASK.
	 */
    static final int SURROGATE_MASK = 0xD800;

    /**
     * Bottom 10 bits for use in surrogates.
     */
	private static final int TRAIL_MASK = 0x3FF;

    /**
     * Shift value for surrogates.
     */
	private static final int SURROGATE_SHIFT = 10;

	/**
	 * Lead surrogates go from LEAD_BASE up to LEAD_LIMIT-1.
	 */
	private static final int LEAD_BASE = 0xD800, LEAD_LIMIT = 0xDC00;

	/**
	 * Trail surrogates go from TRAIL_BASE up to TRAIL_LIMIT-1.
	 */
	private static final int TRAIL_BASE = 0xDC00, TRAIL_LIMIT = 0xE000;

	/**
	 * Surrogates go from SURROGATE_BASE up to SURROGATE_LIMIT-1.
	 */
	private static final int SURROGATE_BASE = 0xD800, SURROGATE_LIMIT = 0xE000;

    /**
     * Any codepoint at or greater than SURROGATE_SPACE_BASE requires 2 16-bit code units.
     */
	//private static final int SURROGATE_SPACE_BASE = 0x10000;

    /**
     * Offset to add to combined surrogate pair to avoid masking.
     */
	private static final int SURROGATE_OFFSET = MIN_SUPPLEMENTARY
	    - (LEAD_BASE << SURROGATE_SHIFT) - TRAIL_BASE;

	private static final int LEAD_BASE_OFFSET = LEAD_BASE - (MIN_SUPPLEMENTARY >> SURROGATE_SHIFT);
    
    
    
    
    
    
    public static String append32(int char32) {
        // Check for irregular values
        if (char32 < 0 || char32 > MAX_UNICODE) char32 = REPLACEMENT_CHAR;

        // Write the UTF-16 values
	    if (char32 >= MIN_SUPPLEMENTARY) {
	        return String.valueOf((char)(LEAD_BASE_OFFSET + (char32 >> SURROGATE_SHIFT))) +
                ((char)(TRAIL_BASE + (char32 & TRAIL_MASK)));
	    } else {
	        return String.valueOf((char)char32);
	    }
    }
    
}
