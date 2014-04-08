/*
 * AccentRemover.java
 *
 * Created on 4 de Junho de 2003, 23:28
 */

package pt.utl.ist.characters;

import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.iso2709.IteratorIso2709;

import java.io.File;
import java.util.regex.Pattern;

/**
 *
 * @author  Nuno Freire
 */
public class NonSortingCharsRemover implements CharacterConverterI{

    private static Pattern nonSortingCharsPattern=Pattern.compile("[\u0098\u0088]([^\u009C\u0089]+)[\\u009C\\u0089]\\s*");
//    private static Pattern nonSortingCharsPattern=Pattern.compile("[<\u0098\u0088]([^>\u009C\u0089]+)[>\\u009C\\u0089]\\s*");
    
    public String convert(String s){
    	return nonSortingCharsPattern.matcher(s).replaceAll("$1");
    }
	
	public static void main(String[] args) throws Exception{
//		IteratorMarcXChange it=new IteratorMarcXChange(new File("c:\\desktop\\970335032.xml"));
//		for(Record rec: it) {
//			System.out.println(rec);
//			RecordCharactersConverter.convertRecord(rec, new NonSortingCharsRemover());
//			System.out.println(rec);
//		}
		
		IteratorIso2709 it=new IteratorIso2709(new File("c:\\desktop\\KBR-FRBR-nobel"));
		for(Record rec: it) {
			System.out.println(rec);
			RecordCharactersConverter.convertRecord(rec, new NonSortingCharsRemover());
			System.out.println(rec);
		}
		
		
//		String s=FileUtil.readFileToString(new File("C:\\desktop\\noname1.txt"));
//		System.out.println(s);
//		for(int i=0; i<s.length(); i++) {
//			System.out.println(s.charAt(i)+" "+Character.codePointAt(s, i));
//		}

		
//		 c=='\u02C6'|| c=='\u2030'
		System.out.println('\u02C6'+" "+'\u2030');
		
		
		
	}
	
	
}
