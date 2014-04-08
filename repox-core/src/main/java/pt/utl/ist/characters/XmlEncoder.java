/*
 * HtmlEncoder.java
 *
 * Created on 12 de Abril de 2004, 16:06
 */

package pt.utl.ist.characters;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author  Nuno Freire
 */

public class XmlEncoder {
    /**
     * Encodes 's' by converting each character to its equivalent HTML entity,
     * where one exists.
     * @param s the string to convert.
     * @return a string with entities encoded.
     */
    public static String encode(String s)
    {
        StringBuffer sb = new StringBuffer();
        char[] chars = s.toCharArray();
        for (char aChar : chars) {
            sb.append(getEntity(aChar));
        }
        return sb.toString();
    }

    /**
     * Converts a character into its entity equivalent. If no entity
     * corresponding to 'c' exist, this method returns a string containing 'c'.
     * This means that 'getEntity()' is always guaranteed to return
     * a valid string representing 'c'.
     * @param c the character to convert.
     * @return a string that contains either an entity representing 'c'
     * or 'c' itself.
     */
    public static String getEntity(char c)
    {
        switch (c) {
            //quotation mark = APL quote.
            case '\u0022': return "&quot;";
            //ampersand.
            case '\u0026': return "&amp;";
            //less-than sign.
            case '\u003C': return "&lt;";
            //greater-than sign.
            case '\u003E': return "&gt;";

            // all other characters remain the same.
            default: return String.valueOf(c);
        }
    }
    

     
    /**
     * Creates a Writer that, when written to, writes entity
     * equivalents of each character to 'out'; where entity equivalents do not
     * exist, the original character is written directly to 'out'.
     * @param out the writer to which to write the output.
     */
    public static Writer createWriter(final Writer out)
    {
        return new FilterWriter(out) {
            public void write(char[] cbuf, int off, int len) 
            throws IOException {
                for (int i=off; i<off+len; i++) {
                    this.out.write(getEntity(cbuf[i]));
                }
            }
            // Not sure whether I need to override these or not, so will do to
            // play safe. The Java documentation doesn't bother to say what
            // the implementation of these methods in FilterWriter actually do.
            public void close() throws IOException {
                this.out.close();
            }
            public void flush() throws IOException {
               this.out.flush();
            }
            public void write(int c) throws IOException {
               this.out.write(getEntity((char)c));
            }
            public void write(String str, int off, int len) throws IOException {
                this.write(str.toCharArray(), off, len);
            }
        };
    }
}

