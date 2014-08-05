package pt.utl.ist.characters;

import pt.utl.ist.marc.Field;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.Subfield;

/**
 * 4
 */
public class AnselConverter {

    private static byte[][] doubleCharsOld;
    private static byte[][] doubleCharsNew;
    private static byte[]   charsOld;
    private static byte[]   charsNew;
    private static byte     sepI;
    private static byte     sepF;

    static {
        sepI = getByte("0x95");
        sepF = getByte("0x96");

        doubleCharsOld = new byte[7][];
        doubleCharsNew = new byte[7][];

        doubleCharsOld[0] = new byte[3];
        doubleCharsNew[0] = new byte[3];
        doubleCharsOld[0][0] = getByte("0xF0");
        doubleCharsOld[0][1] = getByte("0x63");
        doubleCharsOld[0][2] = getByte("0x43");
        doubleCharsNew[0][0] = getByte("0x00");
        doubleCharsNew[0][1] = getByte("0xe7");//ç
        doubleCharsNew[0][2] = getByte("0xc7");//Ç

        doubleCharsOld[1] = new byte[11];
        doubleCharsNew[1] = new byte[11];
        //        doubleCharsOld[1][0]=getByte("0xC1"); 
        doubleCharsOld[1][0] = getByte("0xE1");
        doubleCharsOld[1][1] = getByte("0x61");
        doubleCharsOld[1][2] = getByte("0x41");
        doubleCharsOld[1][3] = getByte("0x65");
        doubleCharsOld[1][4] = getByte("0x45");
        doubleCharsOld[1][5] = getByte("0x69");
        doubleCharsOld[1][6] = getByte("0x49");
        doubleCharsOld[1][7] = getByte("0x6F");
        doubleCharsOld[1][8] = getByte("0x4F");
        doubleCharsOld[1][9] = getByte("0x75");
        doubleCharsOld[1][10] = getByte("0x55");
        doubleCharsNew[1][0] = getByte("0x00");
        doubleCharsNew[1][1] = getByte("0xe0");//
        doubleCharsNew[1][2] = getByte("0xc0");//
        doubleCharsNew[1][3] = getByte("0xe8");//
        doubleCharsNew[1][4] = getByte("0xc8");//
        doubleCharsNew[1][5] = getByte("0xec");//
        doubleCharsNew[1][6] = getByte("0xCC");//
        doubleCharsNew[1][7] = getByte("0xF2");//
        doubleCharsNew[1][8] = getByte("0xD2");//
        doubleCharsNew[1][9] = getByte("0xF9");//
        doubleCharsNew[1][10] = getByte("0xD9");//

        doubleCharsOld[2] = new byte[13];
        doubleCharsNew[2] = new byte[13];
        //        doubleCharsOld[2][0]=getByte("0xC2"); 
        doubleCharsOld[2][0] = getByte("0xE2");
        doubleCharsOld[2][1] = getByte("0x61");
        doubleCharsOld[2][2] = getByte("0x41");
        doubleCharsOld[2][3] = getByte("0x65");
        doubleCharsOld[2][4] = getByte("0x45");
        doubleCharsOld[2][5] = getByte("0x69");
        doubleCharsOld[2][6] = getByte("0x49");
        doubleCharsOld[2][7] = getByte("0x6F");
        doubleCharsOld[2][8] = getByte("0x4F");
        doubleCharsOld[2][9] = getByte("0x75");
        doubleCharsOld[2][10] = getByte("0x55");
        doubleCharsOld[2][11] = getByte("0x79");
        doubleCharsOld[2][12] = getByte("0x59");
        doubleCharsNew[2][0] = getByte("0x00");
        doubleCharsNew[2][1] = getByte("0xE1");//
        doubleCharsNew[2][2] = getByte("0xC1");//
        doubleCharsNew[2][3] = getByte("0xE9");//
        doubleCharsNew[2][4] = getByte("0xC9");//
        doubleCharsNew[2][5] = getByte("0xED");//
        doubleCharsNew[2][6] = getByte("0xCD");//
        doubleCharsNew[2][7] = getByte("0xF3");//
        doubleCharsNew[2][8] = getByte("0xD3");//
        doubleCharsNew[2][9] = getByte("0xFA");//
        doubleCharsNew[2][10] = getByte("0xDA");//
        doubleCharsNew[2][11] = getByte("0xFD");//
        doubleCharsNew[2][12] = getByte("0xDD");//

        doubleCharsOld[3] = new byte[11];
        doubleCharsNew[3] = new byte[11];
        //        doubleCharsOld[3][0]=getByte("0xC3"); 
        doubleCharsOld[3][0] = getByte("0xE3");
        doubleCharsOld[3][1] = getByte("0x61");
        doubleCharsOld[3][2] = getByte("0x41");
        doubleCharsOld[3][3] = getByte("0x65");
        doubleCharsOld[3][4] = getByte("0x45");
        doubleCharsOld[3][5] = getByte("0x69");
        doubleCharsOld[3][6] = getByte("0x49");
        doubleCharsOld[3][7] = getByte("0x6F");
        doubleCharsOld[3][8] = getByte("0x4F");
        doubleCharsOld[3][9] = getByte("0x75");
        doubleCharsOld[3][10] = getByte("0x55");
        doubleCharsNew[3][0] = getByte("0x00");
        doubleCharsNew[3][1] = getByte("0xE2");//
        doubleCharsNew[3][2] = getByte("0xC2");//
        doubleCharsNew[3][3] = getByte("0xEA");//
        doubleCharsNew[3][4] = getByte("0xCA");//
        doubleCharsNew[3][5] = getByte("0xEE");//
        doubleCharsNew[3][6] = getByte("0xCE");//
        doubleCharsNew[3][7] = getByte("0xF4");//
        doubleCharsNew[3][8] = getByte("0xD4");//
        doubleCharsNew[3][9] = getByte("0xFB");//
        doubleCharsNew[3][10] = getByte("0xDB");//

        doubleCharsOld[4] = new byte[7];
        doubleCharsNew[4] = new byte[7];
        //        doubleCharsOld[4][0]=getByte("0xC4"); 
        doubleCharsOld[4][0] = getByte("0xE4");
        doubleCharsOld[4][1] = getByte("0x61");
        doubleCharsOld[4][2] = getByte("0x41");
        doubleCharsOld[4][3] = getByte("0x6F");
        doubleCharsOld[4][4] = getByte("0x4F");
        doubleCharsOld[4][5] = getByte("0x6E");
        doubleCharsOld[4][6] = getByte("0x4E");
        doubleCharsNew[4][0] = getByte("0x00");
        doubleCharsNew[4][1] = getByte("0xE3");//
        doubleCharsNew[4][2] = getByte("0xC3");//
        doubleCharsNew[4][3] = getByte("0xF5");//
        doubleCharsNew[4][4] = getByte("0xD5");//
        doubleCharsNew[4][5] = getByte("0xF1");//
        doubleCharsNew[4][6] = getByte("0xD1");//

        doubleCharsOld[5] = new byte[7];
        doubleCharsNew[5] = new byte[7];
        doubleCharsOld[5][0] = getByte("0xE9");
        doubleCharsOld[5][1] = getByte("0x61");
        doubleCharsOld[5][2] = getByte("0x41");
        doubleCharsOld[5][3] = getByte("0x6F");
        doubleCharsOld[5][4] = getByte("0x4F");
        doubleCharsOld[5][5] = getByte("0x6E");
        doubleCharsOld[5][6] = getByte("0x4E");
        doubleCharsNew[5][0] = getByte("0x00");
        doubleCharsNew[4][1] = getByte("0xE3");//
        doubleCharsNew[4][2] = getByte("0xC3");//
        doubleCharsNew[4][3] = getByte("0xF5");//
        doubleCharsNew[4][4] = getByte("0xD5");//
        doubleCharsNew[4][5] = getByte("0xF1");//
        doubleCharsNew[4][6] = getByte("0xD1");//

        doubleCharsOld[6] = new byte[12];
        doubleCharsNew[6] = new byte[12];
        doubleCharsOld[6][0] = getByte("0xE8");
        doubleCharsOld[6][1] = getByte("0x61");
        doubleCharsOld[6][2] = getByte("0x41");
        doubleCharsOld[6][3] = getByte("0x65");
        doubleCharsOld[6][4] = getByte("0x45");
        doubleCharsOld[6][5] = getByte("0x69");
        doubleCharsOld[6][6] = getByte("0x49");
        doubleCharsOld[6][7] = getByte("0x6F");
        doubleCharsOld[6][8] = getByte("0x4F");
        doubleCharsOld[6][9] = getByte("0x75");
        doubleCharsOld[6][10] = getByte("0x55");
        doubleCharsOld[6][11] = getByte("0x79");
        doubleCharsNew[6][0] = getByte("0x00");
        doubleCharsNew[6][1] = getByte("0xE4");//
        doubleCharsNew[6][2] = getByte("0xC4");//
        doubleCharsNew[6][3] = getByte("0xEB");//
        doubleCharsNew[6][4] = getByte("0xCB");//
        doubleCharsNew[6][5] = getByte("0xEF");//
        doubleCharsNew[6][6] = getByte("0xCF");//
        doubleCharsNew[6][7] = getByte("0xF6");//
        doubleCharsNew[6][8] = getByte("0xD6");//
        doubleCharsNew[6][9] = getByte("0xFC");//
        doubleCharsNew[6][10] = getByte("0xDC");//
        doubleCharsNew[6][11] = getByte("0xFF");//

        charsOld = new byte[6];
        charsNew = new byte[6];
        charsOld[0] = getByte("0x88");//
        charsNew[0] = getByte("0x3C");//
        charsOld[1] = getByte("0x89");//
        charsNew[1] = getByte("0x3E");//
        charsOld[2] = getByte("0x22");//
        charsNew[2] = getByte("0xAB");//
        charsOld[3] = getByte("0x22");//
        charsNew[3] = getByte("0xBB");//
        charsOld[4] = getByte("0xB0");//
        charsNew[4] = getByte("0xAA");//
        charsOld[5] = getByte("0xB0");//
        charsNew[5] = getByte("0xBA");//
    }

    /**
     * @param array
     * @return converted bytes
     */
    public static byte[] convertBytes(byte[] array) {
        byte[] ret = new byte[array.length];
        int r = 0;
        for (int k = 0; k < array.length; k++) {
            if (array[k] == sepI) {
                boolean ends = false;
                int sc = k + 1;
                for (; sc < array.length; sc++) {
                    if (array[sc] == sepF) {
                        ends = true;
                        break;
                    }
                }
                if (ends) {
                    k = sc;
                } else {
                    ret[r] = array[k];
                    r++;
                }
            } else {
                Byte newChar = null;
                if (k < array.length - 1) newChar = findCharAtDouble(array[k], array[k + 1]);
                if (newChar == null)
                    newChar = findChar(array[k]);
                else {
                    k++;

                }
                if (newChar != null) {
                    ret[r] = newChar;
                } else {
                    ret[r] = array[k];
                }
                r++;
            }
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
     * @return converted String
     */
    public static String convertString(String str) {
        return new String(convertBytes(str.getBytes()));
    }

    /**
     * @param rec
     */
    public static void convertRecord(Record rec) {
        if (rec == null) { return; }
        // todo: GDJ: this loop appears many times so the converters should all be restructured. it should be in one place only.
        for (Field field : rec.getFields()) {
            if (field.isControlField()) {
                field.setValue(convertString(field.getValue()));
            } else {
                for (Subfield subfield : field.getSubfields()) {
                    subfield.setValue(convertString(subfield.getValue()));
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

    private static Byte findCharAtDouble(byte c1, byte c2) {
        for (int c = 0; c < doubleCharsOld.length; c++) {
            if (doubleCharsOld[c][0] == c1) {
                for (int i = 0; i < doubleCharsOld[c].length; i++) {
                    if (doubleCharsOld[c][i] == c2) return doubleCharsNew[c][i];
                }
            }
        }
        return null;
    }

    private static Byte findChar(byte ch) {
        for (int i = 0; i < charsOld.length; i++) {
            if (charsOld[i] == ch) { return charsNew[i]; }
        }
        return null;
    }

    /**
     * ***********************************************************************
     * *********** Main ******************
     * ***********************************************************************
     * 
     * @param args
     */
    public static void main(String[] args) {

        System.err.println(AnselConverter.convertString("< >dá  ecoraçauo º ª"));
        ByteInspector.printBytes("u".getBytes());
        ByteInspector.printBytes("ù".getBytes());

    } // main

}
