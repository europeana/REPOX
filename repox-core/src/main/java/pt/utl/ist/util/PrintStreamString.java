/*
 * Created on 2007/08/21
 *
 */
package pt.utl.ist.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;

public class PrintStreamString {
	
	ByteArrayOutputStream os;
	public PrintStream out;
	
	public PrintStreamString() {
		os=new ByteArrayOutputStream();
		out=new PrintStream(os);
	}
	
//	public PrintStream out(){
//		return out;
//	}
	
	public String toString() {
		out.flush();
		return new String(os.toByteArray());		
	}	
	
	public PrintStreamString print(Object o) {
		out.print(o);
		return this;
	}
	public PrintStreamString println(Object o) {
		out.println(o);
		return this;
	}
	public PrintStreamString println() {
		out.println();
		return this;
	}
	public PrintStreamString printf(String format, Object... args) {
		out.printf(format, args);
		return this;
	}
	public PrintStreamString printf(Locale l, String format, Object... args) {
		out.printf(l,format, args);
		return this;
	}
}
