/*
 * Created on Nov 15, 2004
 *
 */
package pt.utl.ist.characters;

import pt.utl.ist.marc.Field;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.Subfield;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author Nuno Freire
 *
 */
public class RecordCharactersConverter {

    public static void convertRecord(Record rec, CharacterConverterI converter){
        if (rec==null)
            return;
        List fields=rec.getFields();

        for (Object field : fields) {
            Field f = (Field) field;
            if (f.isControlField()) {
                if (f.getValue() == null)
                    f.setValue("");
                String newData = converter.convert(f.getValue());
                f.setValue(newData);
            } else {
                for (Object o : f.getSubfields()) {
                    Subfield sf = (Subfield) o;
                    String newData = converter.convert(sf.getValue());
                    sf.setValue(newData);
                }
            }
        }
    }



    public static void convertRecord(Record rec, String encoding){
        try{
            if (rec==null)
                return;
            List fields=rec.getFields();

            for (Object field : fields) {
                Field f = (Field) field;
                if (f.isControlField()) {
                    String newData = convertString(f.getValue(), encoding);
                    f.setValue(newData);
                } else {
                    for (Object o : f.getSubfields()) {
                        Subfield sf = (Subfield) o;
                        String newData = convertString(sf.getValue(), encoding);
                        sf.setValue(newData);
                    }
                }
            }
        }catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertString(String str, String encoding) throws UnsupportedEncodingException{
        String newData=new String(str.getBytes("ISO8859_1"),encoding);
        return newData;
    }

    public static void main(String[] args) throws Exception{
        System.err.println(RecordCharactersConverter.convertString("Alfabetiza‡„o em l¡ngua","Cp850"));
    }
}
