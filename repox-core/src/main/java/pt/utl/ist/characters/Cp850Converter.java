package pt.utl.ist.characters;

import pt.utl.ist.marc.Field;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.Subfield;
import pt.utl.ist.repox.util.FileUtilSecond;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class Cp850Converter {

    private static Map<Byte, Byte> singleChars;

    static {
        singleChars = new HashMap<Byte, Byte>(130);
        singleChars.put((byte)0x80, (byte)0xC7);
        singleChars.put((byte)0x81, (byte)0xFC);
        singleChars.put((byte)0x82, (byte)0xE9);
        singleChars.put((byte)0x83, (byte)0xE2);
        singleChars.put((byte)0x84, (byte)0xE3);
        singleChars.put((byte)0x85, (byte)0xE0);
        singleChars.put((byte)0x86, (byte)0xE5);
        singleChars.put((byte)0x87, (byte)0xE7);
        singleChars.put((byte)0x88, (byte)0xEA);
        singleChars.put((byte)0x89, (byte)0xEB);
        singleChars.put((byte)0x8A, (byte)0xE8);
        singleChars.put((byte)0x8B, (byte)0xEF);
        singleChars.put((byte)0x8C, (byte)0xEE);
        singleChars.put((byte)0x8D, (byte)0xEC);
        singleChars.put((byte)0x8E, (byte)0xC3);
        singleChars.put((byte)0x8F, (byte)0xC5);
        singleChars.put((byte)0x90, (byte)0xC9);
        singleChars.put((byte)0x91, (byte)0xE6);
        singleChars.put((byte)0x92, (byte)0xC6);
        singleChars.put((byte)0x93, (byte)0xF4);
        singleChars.put((byte)0x94, (byte)0xF6);
        singleChars.put((byte)0x95, (byte)0xF2);
        singleChars.put((byte)0x96, (byte)0xFB);
        singleChars.put((byte)0x97, (byte)0xF9);
        singleChars.put((byte)0x98, (byte)0xFF);
        singleChars.put((byte)0x99, (byte)0xD6);
        singleChars.put((byte)0x9A, (byte)0xDC);
        singleChars.put((byte)0x9B, (byte)0xF8);
        singleChars.put((byte)0x9C, (byte)0xA3);
        singleChars.put((byte)0x9D, (byte)0xD8);
        singleChars.put((byte)0x9E, (byte)0xD7);
        singleChars.put((byte)0x9F, (byte)0x20);
        singleChars.put((byte)0xA0, (byte)0xE1);
        singleChars.put((byte)0xA1, (byte)0xED);
        singleChars.put((byte)0xA2, (byte)0xF3);
        singleChars.put((byte)0xA3, (byte)0xFA);
        singleChars.put((byte)0xA4, (byte)0xF1);
        singleChars.put((byte)0xA5, (byte)0xD1);
        singleChars.put((byte)0xA6, (byte)0xAA);
        singleChars.put((byte)0xA7, (byte)0xBA);
        singleChars.put((byte)0xA8, (byte)0xBF);
        singleChars.put((byte)0xA9, (byte)0xAE);
        singleChars.put((byte)0xAA, (byte)0xAC);
        singleChars.put((byte)0xAB, (byte)0xBD);
        singleChars.put((byte)0xAC, (byte)0xBC);
        singleChars.put((byte)0xAD, (byte)0xA1);
        singleChars.put((byte)0xAE, (byte)0xAB);
        singleChars.put((byte)0xAF, (byte)0xBB);
        singleChars.put((byte)0xB0, (byte)0x20);
        singleChars.put((byte)0xB1, (byte)0x20);
        singleChars.put((byte)0xB2, (byte)0x20);
        singleChars.put((byte)0xB3, (byte)0x20);
        singleChars.put((byte)0xB4, (byte)0x20);
        singleChars.put((byte)0xB5, (byte)0xC1);
        singleChars.put((byte)0xB6, (byte)0xC2);
        singleChars.put((byte)0xB7, (byte)0xC0);
        singleChars.put((byte)0xB8, (byte)0xA9);
        singleChars.put((byte)0xB9, (byte)0x20);
        singleChars.put((byte)0xBA, (byte)0x20);
        singleChars.put((byte)0xBB, (byte)0x20);
        singleChars.put((byte)0xBC, (byte)0x20);
        singleChars.put((byte)0xBD, (byte)0xA2);
        singleChars.put((byte)0xBE, (byte)0xA5);
        singleChars.put((byte)0xBF, (byte)0x20);
        singleChars.put((byte)0xC0, (byte)0x20);
        singleChars.put((byte)0xC1, (byte)0x20);
        singleChars.put((byte)0xC2, (byte)0x20);
        singleChars.put((byte)0xC3, (byte)0x20);
        singleChars.put((byte)0xC4, (byte)0x20);
        singleChars.put((byte)0xC5, (byte)0x20);
        singleChars.put((byte)0xC6, (byte)0xE3);
        singleChars.put((byte)0xC7, (byte)0xC3);
        singleChars.put((byte)0xC8, (byte)0x20);
        singleChars.put((byte)0xC9, (byte)0x20);
        singleChars.put((byte)0xCA, (byte)0x20);
        singleChars.put((byte)0xCB, (byte)0x20);
        singleChars.put((byte)0xCC, (byte)0x20);
        singleChars.put((byte)0xCD, (byte)0x20);
        singleChars.put((byte)0xCE, (byte)0x20);
        singleChars.put((byte)0xCF, (byte)0xA4);
        singleChars.put((byte)0xD0, (byte)0xF0);
        singleChars.put((byte)0xD1, (byte)0xD0);
        singleChars.put((byte)0xD2, (byte)0xCA);
        singleChars.put((byte)0xD3, (byte)0xCB);
        singleChars.put((byte)0xD4, (byte)0xC8);
        singleChars.put((byte)0xD5, (byte)0x20);
        singleChars.put((byte)0xD6, (byte)0xCD);
        singleChars.put((byte)0xD7, (byte)0xCE);
        singleChars.put((byte)0xD8, (byte)0xCF);
        singleChars.put((byte)0xD9, (byte)0x20);
        singleChars.put((byte)0xDA, (byte)0x20);
        singleChars.put((byte)0xDB, (byte)0x20);
        singleChars.put((byte)0xDC, (byte)0x20);
        singleChars.put((byte)0xDD, (byte)0xA6);
        singleChars.put((byte)0xDE, (byte)0xCC);
        singleChars.put((byte)0xDF, (byte)0x20);
        singleChars.put((byte)0xE0, (byte)0xD3);
        singleChars.put((byte)0xE1, (byte)0xDF);
        singleChars.put((byte)0xE2, (byte)0xD4);
        singleChars.put((byte)0xE3, (byte)0xD2);
        singleChars.put((byte)0xE4, (byte)0xF5);
        singleChars.put((byte)0xE5, (byte)0xD5);
        singleChars.put((byte)0xE6, (byte)0xB5);
        singleChars.put((byte)0xE7, (byte)0xFE);
        singleChars.put((byte)0xE8, (byte)0xDE);
        singleChars.put((byte)0xE9, (byte)0xDA);
        singleChars.put((byte)0xEA, (byte)0xDB);
        singleChars.put((byte)0xEB, (byte)0xD9);
        singleChars.put((byte)0xEC, (byte)0xFD);
        singleChars.put((byte)0xED, (byte)0xDD);
        singleChars.put((byte)0xEE, (byte)0xAF);
        singleChars.put((byte)0xEF, (byte)0xB4);
        singleChars.put((byte)0xF0, (byte)0xAD);
        singleChars.put((byte)0xF1, (byte)0xB1);
        singleChars.put((byte)0xF2, (byte)0x20);
        singleChars.put((byte)0xF3, (byte)0xBE);
        singleChars.put((byte)0xF4, (byte)0xB6);
        singleChars.put((byte)0xF5, (byte)0xA7);
        singleChars.put((byte)0xF6, (byte)0xF7);
        singleChars.put((byte)0xF7, (byte)0xB8);
        singleChars.put((byte)0xF8, (byte)0xB0);
        singleChars.put((byte)0xF9, (byte)0xA8);
        singleChars.put((byte)0xFA, (byte)0xB7);
        singleChars.put((byte)0xFB, (byte)0xB9);
        singleChars.put((byte)0xFC, (byte)0xB3);
        singleChars.put((byte)0xFD, (byte)0xB2);
        singleChars.put((byte)0xFE, (byte)0x20);
        singleChars.put((byte)0xFF, (byte)0xA0);
    }

    /**
     * @param array
     * @return the converted bytes
     */
    public static byte[] convertBytes(byte[] array) {
        byte[] ret = new byte[array.length];
        int r = 0;
        for (byte b : array) {
            Byte newChar = singleChars.get(b);
            if (newChar != null) {
                ret[r] = newChar;
            } else {
                ret[r] = b;
            }
            r++;
        }

        //remove possible extra characters 0x00 in the array
        int realsz = ret.length;
        byte empty = getByte("0x00");
        for (int idx = ret.length - 1; idx >= 0; idx--) {
            if (ret[idx] == empty) {
                realsz = idx;
            } else
                break;
        }
        if (realsz != ret.length) {
            byte[] newret = new byte[realsz];
            for (int idx = realsz - 1; idx >= 0; idx--) {
                newret[idx] = ret[idx];
            }
            ret = newret;
        }
        return ret;
    }

    /**
     * @param str
     * @return the converted String
     */
    public static String convertString(String str) {
        str = new String(convertBytes(str.getBytes()));
        return str;
    }

    /**
     * @param rec
     */
    public static void convertRecord(Record rec) {
        if (rec == null) { return; }

        if (rec.getLeader() != null) {
            rec.setLeader(convertString(rec.getLeader()));
        }

        List<Field> fields = rec.getFields();

        for (Field field : fields) {
            if (field.isControlField()) {
                String newData = convertString(field.getValue());
                field.setValue(newData);
            } else {
                for (Subfield sf : field.getSubfields()) {
                    String newData = convertString(sf.getValue());
                    sf.setValue(newData);
                }
            }
        }
    }

    /**
     * ***********************************************************************
     * *********** Private Methods ******************
     * ***********************************************************************
     */
    private static byte getByte(String b) {
        return Short.decode(b).byteValue();
    }

    /**
     * ***********************************************************************
     * *********** Main ******************
     * ***********************************************************************
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Record r = new Record(FileUtilSecond.readFileToString(new File("c:/desktop/teste_cccm_2709.hzr")));
        System.err.println(r);

    } // main

}
