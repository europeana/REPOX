/*
 * AccentRemover.java
 *
 * Created on 4 de Junho de 2003, 23:28
 */

package pt.utl.ist.characters;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author  Nuno Freire
 */
public class StandardCharsetConverter implements CharacterConverterI{
    
	String charset;
	
	public StandardCharsetConverter(String charset) {
		this.charset = charset;
	}
	
    public String convert(String s){
    	try {
			return new String(s.getBytes(), charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
    }
	
	
}
