package pt.utl.ist.characters;

import org.marc4j.converter.impl.AnselToUnicode;
import org.marc4j.converter.impl.Iso5426ToUnicode;
import org.marc4j.converter.impl.Iso6937ToUnicode;

/**
 */
public class CharacterConverters {
    /** CharacterConverters HORIZON */
    public static final String HORIZON = "HORIZON";
    /** CharacterConverters ISO5426 */
    public static final String ISO5426 = "ISO-5426";
    /** CharacterConverters ISO6937 */
    public static final String ISO6937 = "ISO-6937";
    /** CharacterConverters ANSEL */
    public static final String ANSEL = "ANSEL";

    /**
     * @param charset
     * @return a CharacterConverterI
     */
    public static CharacterConverterI getInstance(String charset) {
        if (charset.equals(ANSEL)) {
            return new CharConverterMarc4jWrapper(new AnselToUnicode());
        }
        else if (charset.equals(ISO5426)) {
            return new CharConverterMarc4jWrapper(new Iso5426ToUnicode());
        }
        else if (charset.equals(ISO6937)) {
            return new CharConverterMarc4jWrapper(new Iso6937ToUnicode());
        }
        else if (charset.equals(HORIZON)) {
            return new HorizonConverter();
        }
        return null;
    }
}
