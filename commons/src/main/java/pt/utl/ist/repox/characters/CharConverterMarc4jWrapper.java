package pt.utl.ist.repox.characters;

import com.ibm.icu.text.Normalizer;
import org.marc4j.converter.CharConverter;

/**
 */
public class CharConverterMarc4jWrapper implements CharacterConverterI {
	CharConverter converter;
	
	/**
	 * Creates a new instance of this class.
	 * @param converter
	 */
	public CharConverterMarc4jWrapper(CharConverter converter) {
		this.converter = converter;
	}
	
	@Override
    public String convert(String txt) {
		String ret=converter.convert(txt);
		ret=Normalizer.normalize(ret, Normalizer.DEFAULT);
		return ret;
	}

}
