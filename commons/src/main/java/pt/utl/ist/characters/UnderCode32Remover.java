/*
 * AccentRemover.java
 *
 * Created on 4 de Junho de 2003, 23:28
 */

package pt.utl.ist.characters;

/**
 * 
 * @author Nuno Freire
 */
public class UnderCode32Remover implements CharacterConverterI {

    /**
     * @param c
     * @return a space if the character is unaccented
     */
    public char unaccentedUppercaseChar(char c) {
        if (c < 32) return ' ';
        return c;
    }

    @Override
    public String convert(String s) {
        StringBuffer ret = new StringBuffer(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            ret.append(unaccentedUppercaseChar(c));
        }
        return ret.toString();
    }

}
