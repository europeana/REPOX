package pt.utl.ist.characters;

import com.ibm.icu.text.Normalizer;
import org.marc4j.converter.CharConverter;

public class CharConverterMarc4jWrapper implements CharacterConverterI {
	CharConverter converter;
	
	public CharConverterMarc4jWrapper(CharConverter converter) {
		this.converter = converter;
	}
	
	public String convert(String txt) {
		String ret=converter.convert(txt);
		ret=Normalizer.normalize(ret, Normalizer.DEFAULT);
		return ret;
	}

}
