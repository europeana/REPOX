/*
 * Created on 2007/08/21
 *
 */
package pt.utl.ist.repox.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;

/**
 */
public class PrintStreamString {

    ByteArrayOutputStream os;
    public PrintStream    out;

    /**
     * Creates a new instance of this class.
     */
    public PrintStreamString() {
        os = new ByteArrayOutputStream();
        out = new PrintStream(os);
    }

    //	public PrintStream out(){
    //		return out;
    //	}

    @Override
    public String toString() {
        out.flush();
        return new String(os.toByteArray());
    }

    /**
     * @param o
     * @return PrintStreamString
     */
    public PrintStreamString print(Object o) {
        out.print(o);
        return this;
    }

    /**
     * @param o
     * @return PrintStreamString
     */
    public PrintStreamString println(Object o) {
        out.println(o);
        return this;
    }

    /**
     * @return PrintStreamString
     */
    public PrintStreamString println() {
        out.println();
        return this;
    }

    /**
     * @param format
     * @param args
     * @return PrintStreamString
     */
    public PrintStreamString printf(String format, Object... args) {
        out.printf(format, args);
        return this;
    }

    /**
     * @param l
     * @param format
     * @param args
     * @return PrintStreamString
     */
    public PrintStreamString printf(Locale l, String format, Object... args) {
        out.printf(l, format, args);
        return this;
    }
}
