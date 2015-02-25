package pt.utl.ist.characters;

import pt.utl.ist.marc.MarcField;
import pt.utl.ist.marc.MarcRecord;
import pt.utl.ist.marc.MarcSubfield;
import pt.utl.ist.util.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class HorizonConverter implements CharacterConverterI {

    private static HashMap<Byte, Byte>                singleChars;
    private static HashMap<Byte, HashMap<Byte, Byte>> doubleChars;
    private static byte                               sepI;
    private static byte                               sepF;

    private static Pattern                            numerosIndexacaoPattern = Pattern.compile("0x95[^0x96]+0x96");
    private static Pattern                            findUnicodeCharsPattern = Pattern.compile("<U\\+(....)>");

    static {
        sepI = (byte)0x95;
        sepF = (byte)0x96;

        doubleChars = new HashMap<Byte, HashMap<Byte, Byte>>();

        HashMap<Byte, Byte> secCharsMap = new HashMap<Byte, Byte>(2);
        doubleChars.put((byte)0xD0, secCharsMap);
        secCharsMap.put((byte)0x63, (byte)0xe7);
        secCharsMap.put((byte)0x43, (byte)0xc7);

        secCharsMap = new HashMap<Byte, Byte>(10);
        doubleChars.put((byte)0xC1, secCharsMap);
        secCharsMap.put((byte)0x61, (byte)0xe0);
        secCharsMap.put((byte)0x41, (byte)0xc0);
        secCharsMap.put((byte)0x65, (byte)0xe8);
        secCharsMap.put((byte)0x45, (byte)0xc8);
        secCharsMap.put((byte)0x69, (byte)0xec);
        secCharsMap.put((byte)0x49, (byte)0xCC);
        secCharsMap.put((byte)0x6F, (byte)0xF2);
        secCharsMap.put((byte)0x4F, (byte)0xD2);
        secCharsMap.put((byte)0x75, (byte)0xF9);
        secCharsMap.put((byte)0x55, (byte)0xD9);

        secCharsMap = new HashMap<Byte, Byte>(12);
        doubleChars.put((byte)0xC2, secCharsMap);
        secCharsMap.put((byte)0x61, (byte)0xE1);
        secCharsMap.put((byte)0x41, (byte)0xC1);
        secCharsMap.put((byte)0x65, (byte)0xE9);
        secCharsMap.put((byte)0x45, (byte)0xC9);
        secCharsMap.put((byte)0x69, (byte)0xED);
        secCharsMap.put((byte)0x49, (byte)0xCD);
        secCharsMap.put((byte)0x6F, (byte)0xF3);
        secCharsMap.put((byte)0x4F, (byte)0xD3);
        secCharsMap.put((byte)0x75, (byte)0xFA);
        secCharsMap.put((byte)0x55, (byte)0xDA);
        secCharsMap.put((byte)0x79, (byte)0xFD);
        secCharsMap.put((byte)0x59, (byte)0xDD);

        secCharsMap = new HashMap<Byte, Byte>(10);
        doubleChars.put((byte)0xC3, secCharsMap);
        secCharsMap.put((byte)0x61, (byte)0xE2);
        secCharsMap.put((byte)0x41, (byte)0xC2);
        secCharsMap.put((byte)0x65, (byte)0xEA);
        secCharsMap.put((byte)0x45, (byte)0xCA);
        secCharsMap.put((byte)0x69, (byte)0xEE);
        secCharsMap.put((byte)0x49, (byte)0xCE);
        secCharsMap.put((byte)0x6F, (byte)0xF4);
        secCharsMap.put((byte)0x4F, (byte)0xD4);
        secCharsMap.put((byte)0x75, (byte)0xFB);
        secCharsMap.put((byte)0x55, (byte)0xDB);

        secCharsMap = new HashMap<Byte, Byte>(6);
        doubleChars.put((byte)0xC4, secCharsMap);
        secCharsMap.put((byte)0x61, (byte)0xE3);
        secCharsMap.put((byte)0x41, (byte)0xC3);
        secCharsMap.put((byte)0x6F, (byte)0xF5);
        secCharsMap.put((byte)0x4F, (byte)0xD5);
        secCharsMap.put((byte)0x6E, (byte)0xF1);
        secCharsMap.put((byte)0x4E, (byte)0xD1);

        secCharsMap = new HashMap<Byte, Byte>(6);
        doubleChars.put((byte)0xC9, secCharsMap);
        secCharsMap.put((byte)0x61, (byte)0xE3);
        secCharsMap.put((byte)0x41, (byte)0xC3);
        secCharsMap.put((byte)0x6F, (byte)0xF5);
        secCharsMap.put((byte)0x4F, (byte)0xD5);
        secCharsMap.put((byte)0x6E, (byte)0xF1);
        secCharsMap.put((byte)0x4E, (byte)0xD1);

        secCharsMap = new HashMap<Byte, Byte>(11);
        doubleChars.put((byte)0xC8, secCharsMap);
        secCharsMap.put((byte)0x61, (byte)0xE4);
        secCharsMap.put((byte)0x41, (byte)0xC4);
        secCharsMap.put((byte)0x65, (byte)0xEB);
        secCharsMap.put((byte)0x45, (byte)0xCB);
        secCharsMap.put((byte)0x69, (byte)0xEF);
        secCharsMap.put((byte)0x49, (byte)0xCF);
        secCharsMap.put((byte)0x6F, (byte)0xF6);
        secCharsMap.put((byte)0x4F, (byte)0xD6);
        secCharsMap.put((byte)0x75, (byte)0xFC);
        secCharsMap.put((byte)0x55, (byte)0xDC);
        secCharsMap.put((byte)0x79, (byte)0xFF);

        singleChars = new HashMap<Byte, Byte>();
        singleChars.put((byte)0x88, (byte)0x3C);
        singleChars.put((byte)0x89, (byte)0x3E);
        singleChars.put((byte)0xB5, (byte)0xBA);
        singleChars.put((byte)0xA7, (byte)0xBA);
        singleChars.put((byte)0xA6, (byte)0xAA);
        singleChars.put((byte)0xB4, (byte)0xAA);
        singleChars.put((byte)0xAA, (byte)0xAB);
        singleChars.put((byte)0xBA, (byte)0xBB);
        singleChars.put((byte)0x00, (byte)0x20);
        singleChars.put((byte)0x01, (byte)0x20);
        singleChars.put((byte)0x02, (byte)0x20);
        singleChars.put((byte)0x03, (byte)0x20);
        singleChars.put((byte)0x04, (byte)0x20);
        singleChars.put((byte)0x05, (byte)0x20);
        singleChars.put((byte)0x06, (byte)0x20);
        singleChars.put((byte)0x07, (byte)0x20);
        singleChars.put((byte)0x08, (byte)0x20);
        singleChars.put((byte)0x09, (byte)0x20);
        singleChars.put((byte)0x0B, (byte)0x20);
        singleChars.put((byte)0x0C, (byte)0x20);
        singleChars.put((byte)0x0D, (byte)0x20);
        singleChars.put((byte)0x0E, (byte)0x20);
        singleChars.put((byte)0x0F, (byte)0x20);
        singleChars.put((byte)0x10, (byte)0x20);
        singleChars.put((byte)0x11, (byte)0x20);
        singleChars.put((byte)0x12, (byte)0x20);
        singleChars.put((byte)0x13, (byte)0x20);
        singleChars.put((byte)0x14, (byte)0x20);
        singleChars.put((byte)0x15, (byte)0x20);
        singleChars.put((byte)0x16, (byte)0x20);
        singleChars.put((byte)0x17, (byte)0x20);
        singleChars.put((byte)0x18, (byte)0x20);
        singleChars.put((byte)0x19, (byte)0x20);
        singleChars.put((byte)0x1A, (byte)0x20);
        singleChars.put((byte)0x1B, (byte)0x20);
        singleChars.put((byte)0x1C, (byte)0x20);
        singleChars.put((byte)0x1D, (byte)0x20);
        singleChars.put((byte)0x1E, (byte)0x20);
        singleChars.put((byte)0x1F, (byte)0x20);
        singleChars.put((byte)0xA4, (byte)0x24);
    }

    @Override
    public String convert(String txt) {
        return convertString(txt);
    }

    /**
     * @param array
     * @return the converted bytes
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
                    newChar = singleChars.get(array[k]);
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
        byte empty = (byte)0x00;
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

        try {
            Matcher m = numerosIndexacaoPattern.matcher(str);
            str = m.replaceAll("");
            str = new String(convertBytes(str.getBytes()));

            m = numerosIndexacaoPattern.matcher(str);
            str = m.replaceAll("");

            for (Matcher hasUnicodeChars = findUnicodeCharsPattern.matcher(str); hasUnicodeChars.find(); hasUnicodeChars = findUnicodeCharsPattern.matcher(str)) {
                String uc = "0x" + hasUnicodeChars.group(1);
                str = hasUnicodeChars.replaceFirst(UnicodeUtil.append32(Integer.decode(uc)));
            }

            return str;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param rec
     */
    public static void convertRecord(MarcRecord rec) {
        if (rec == null) return;

        if (rec.getLeader() != null) rec.setLeader(convertString(rec.getLeader()));

        for (MarcField field : rec.getFields()) {
            if (field.isControlField()) {
                String newData = convertString(field.getValue());
                field.setValue(newData);
            } else {
                for (MarcSubfield subfield : field.getSubfields()) {
                    String newData = convertString(subfield.getValue());
                    subfield.setValue(newData);
                }
            }
        }
    }

    /**
     * ***********************************************************************
     * *********** Private Methods ******************
     * ***********************************************************************
     */

    private static Byte findCharAtDouble(byte c1, byte c2) {
        HashMap<Byte, Byte> secChars = doubleChars.get(c1);
        if (secChars == null) return null;
        return secChars.get(c2);
    }

    private static String toSafeRegExp(String str) {
        Pattern p = Pattern.compile("\\+");
        Matcher m = p.matcher(str);
        str = m.replaceAll("\\\\+");

        p = Pattern.compile("\\*");
        m = p.matcher(str);
        str = m.replaceAll("\\\\*");

        p = Pattern.compile("\\)");
        m = p.matcher(str);
        str = m.replaceAll("\\\\)");

        p = Pattern.compile("\\(");
        m = p.matcher(str);
        str = m.replaceAll("\\\\(");
        return str;
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
        MarcRecord r = new MarcRecord(FileUtil.readFileToString(new File("c:/desktop/teste_cccm_2709.hzr")));
        //    	Record r=HorizonClient.getRecord(""+1106184, HorizonClient.IdentificationSpace.NCB);
        System.err.println(r);
        //HorizonConverter.convertRecord(r);
        //System.err.println(r);
        //System.err.println(HorizonConverter.convertString("ÂeÂA <U+00AA> <U+00BA> <U+007E> <U+00E6>"));

        //        HorizonConverter conv=new HorizonConverter();
        //        
        //        System.out.println(System.getProperty("file.encoding"));
        //        String original = new String("DirecÐcÄao de ServiÐcos");
        //        System.out.println("original = " + original);
        //        System.out.println();
        //        original=new String(conv.convertBytes(original.getBytes()));
        //        try {
        //            byte[] utf8Bytes = original.getBytes("UTF8");
        //                                   
        //            byte[] defaultBytes = original.getBytes();
        //            
        //            String roundTrip = new String(utf8Bytes, "UTF8");
        //            System.out.println("roundTrip = " + roundTrip);            
        //        } catch (UnsupportedEncodingException e) {
        //            e.printStackTrace();
        //        }

    } // main

}

/*
 * public void StringConverterDOS(){ doubleCharsOld=new byte[7][];
 * doubleCharsNew=new byte[7][];
 * 
 * doubleCharsOld[0]=new byte[3]; doubleCharsNew[0]=new byte[3];
 * doubleCharsOld[0][0]=getByte("0xD0"); doubleCharsOld[0][1]=getByte("0x63");
 * doubleCharsOld[0][2]=getByte("0x43"); doubleCharsNew[0][0]=getByte("0x00");
 * doubleCharsNew[0][1]=getByte("0x87");//ç
 * doubleCharsNew[0][2]=getByte("0x80");//Ç
 * 
 * doubleCharsOld[1]=new byte[11]; doubleCharsNew[1]=new byte[11];
 * doubleCharsOld[1][0]=getByte("0xC1"); doubleCharsOld[1][1]=getByte("0x61");
 * doubleCharsOld[1][2]=getByte("0x41"); doubleCharsOld[1][3]=getByte("0x65");
 * doubleCharsOld[1][4]=getByte("0x45"); doubleCharsOld[1][5]=getByte("0x69");
 * doubleCharsOld[1][6]=getByte("0x49"); doubleCharsOld[1][7]=getByte("0x6F");
 * doubleCharsOld[1][8]=getByte("0x4F"); doubleCharsOld[1][9]=getByte("0x75");
 * doubleCharsOld[1][10]=getByte("0x55"); doubleCharsNew[1][0]=getByte("0x00");
 * doubleCharsNew[1][1]=getByte("0x85");//
 * doubleCharsNew[1][2]=getByte("0xB7");//
 * doubleCharsNew[1][3]=getByte("0x8A");//
 * doubleCharsNew[1][4]=getByte("0xD4");//
 * doubleCharsNew[1][5]=getByte("0x8D");//
 * doubleCharsNew[1][6]=getByte("0xDE");//
 * doubleCharsNew[1][7]=getByte("0x95");//
 * doubleCharsNew[1][8]=getByte("0xE3");//
 * doubleCharsNew[1][9]=getByte("0x97");//
 * doubleCharsNew[1][10]=getByte("0xEB");//
 * 
 * doubleCharsOld[2]=new byte[13]; doubleCharsNew[2]=new byte[13];
 * doubleCharsOld[2][0]=getByte("0xC2"); doubleCharsOld[2][1]=getByte("0x61");
 * doubleCharsOld[2][2]=getByte("0x41"); doubleCharsOld[2][3]=getByte("0x65");
 * doubleCharsOld[2][4]=getByte("0x45"); doubleCharsOld[2][5]=getByte("0x69");
 * doubleCharsOld[2][6]=getByte("0x49"); doubleCharsOld[2][7]=getByte("0x6F");
 * doubleCharsOld[2][8]=getByte("0x4F"); doubleCharsOld[2][9]=getByte("0x75");
 * doubleCharsOld[2][10]=getByte("0x55"); doubleCharsOld[2][11]=getByte("0x79");
 * doubleCharsOld[2][12]=getByte("0x59"); doubleCharsNew[2][0]=getByte("0x00");
 * doubleCharsNew[2][1]=getByte("0xA0");//
 * doubleCharsNew[2][2]=getByte("0xB5");//
 * doubleCharsNew[2][3]=getByte("0x82");//
 * doubleCharsNew[2][4]=getByte("0x90");//
 * doubleCharsNew[2][5]=getByte("0xA1");//
 * doubleCharsNew[2][6]=getByte("0xD6");//
 * doubleCharsNew[2][7]=getByte("0xA2");//
 * doubleCharsNew[2][8]=getByte("0xE0");//
 * doubleCharsNew[2][9]=getByte("0xA3");//
 * doubleCharsNew[2][10]=getByte("0xE9");//
 * doubleCharsNew[2][11]=getByte("0xEC");//
 * doubleCharsNew[2][12]=getByte("0xED");//
 * 
 * doubleCharsOld[3]=new byte[11]; doubleCharsNew[3]=new byte[11];
 * doubleCharsOld[3][0]=getByte("0xC3"); doubleCharsOld[3][1]=getByte("0x61");
 * doubleCharsOld[3][2]=getByte("0x41"); doubleCharsOld[3][3]=getByte("0x65");
 * doubleCharsOld[3][4]=getByte("0x45"); doubleCharsOld[3][5]=getByte("0x69");
 * doubleCharsOld[3][6]=getByte("0x49"); doubleCharsOld[3][7]=getByte("0x6F");
 * doubleCharsOld[3][8]=getByte("0x4F"); doubleCharsOld[3][9]=getByte("0x75");
 * doubleCharsOld[3][10]=getByte("0x55"); doubleCharsNew[3][0]=getByte("0x00");
 * doubleCharsNew[3][1]=getByte("0x83");//
 * doubleCharsNew[3][2]=getByte("0xB6");//
 * doubleCharsNew[3][3]=getByte("0x88");//
 * doubleCharsNew[3][4]=getByte("0xD2");//
 * doubleCharsNew[3][5]=getByte("0x8C");//
 * doubleCharsNew[3][6]=getByte("0xD7");//
 * doubleCharsNew[3][7]=getByte("0x93");//
 * doubleCharsNew[3][8]=getByte("0xE2");//
 * doubleCharsNew[3][9]=getByte("0x96");//
 * doubleCharsNew[3][10]=getByte("0xEA");//
 * 
 * doubleCharsOld[4]=new byte[7]; doubleCharsNew[4]=new byte[7];
 * doubleCharsOld[4][0]=getByte("0xC4"); doubleCharsOld[4][1]=getByte("0x61");
 * doubleCharsOld[4][2]=getByte("0x41"); doubleCharsOld[4][3]=getByte("0x6F");
 * doubleCharsOld[4][4]=getByte("0x4F"); doubleCharsOld[4][5]=getByte("0x6E");
 * doubleCharsOld[4][6]=getByte("0x4E"); doubleCharsNew[4][0]=getByte("0x00");
 * doubleCharsNew[4][1]=getByte("0xC6");//
 * doubleCharsNew[4][2]=getByte("0xC7");//
 * doubleCharsNew[4][3]=getByte("0xE4");//
 * doubleCharsNew[4][4]=getByte("0xE5");//
 * doubleCharsNew[4][5]=getByte("0xA4");//
 * doubleCharsNew[4][6]=getByte("0xA5");//
 * 
 * doubleCharsOld[5]=new byte[7]; doubleCharsNew[5]=new byte[7];
 * doubleCharsOld[5][0]=getByte("0xC9"); doubleCharsOld[5][1]=getByte("0x61");
 * doubleCharsOld[5][2]=getByte("0x41"); doubleCharsOld[5][3]=getByte("0x6F");
 * doubleCharsOld[5][4]=getByte("0x4F"); doubleCharsOld[5][5]=getByte("0x6E");
 * doubleCharsOld[5][6]=getByte("0x4E"); doubleCharsNew[5][0]=getByte("0x00");
 * doubleCharsNew[5][1]=getByte("0xC6");//
 * doubleCharsNew[5][2]=getByte("0xC7");//
 * doubleCharsNew[5][3]=getByte("0xE4");//
 * doubleCharsNew[5][4]=getByte("0xE5");//
 * doubleCharsNew[5][5]=getByte("0xA4");//
 * doubleCharsNew[5][6]=getByte("0xA5");//
 * 
 * doubleCharsOld[6]=new byte[12]; doubleCharsNew[6]=new byte[12];
 * doubleCharsOld[6][0]=getByte("0xC8"); doubleCharsOld[6][1]=getByte("0x61");
 * doubleCharsOld[6][2]=getByte("0x41"); doubleCharsOld[6][3]=getByte("0x65");
 * doubleCharsOld[6][4]=getByte("0x45"); doubleCharsOld[6][5]=getByte("0x69");
 * doubleCharsOld[6][6]=getByte("0x49"); doubleCharsOld[6][7]=getByte("0x6F");
 * doubleCharsOld[6][8]=getByte("0x4F"); doubleCharsOld[6][9]=getByte("0x75");
 * doubleCharsOld[6][10]=getByte("0x55"); doubleCharsOld[6][11]=getByte("0x79");
 * doubleCharsNew[6][0]=getByte("0x00"); doubleCharsNew[6][1]=getByte("0x84");//
 * doubleCharsNew[6][2]=getByte("0x8E");//
 * doubleCharsNew[6][3]=getByte("0x89");//
 * doubleCharsNew[6][4]=getByte("0xD3");//
 * doubleCharsNew[6][5]=getByte("0x8B");//
 * doubleCharsNew[6][6]=getByte("0xD8");//
 * doubleCharsNew[6][7]=getByte("0x94");//
 * doubleCharsNew[6][8]=getByte("0x99");//
 * doubleCharsNew[6][9]=getByte("0x81");//
 * doubleCharsNew[6][10]=getByte("0x9A");//
 * doubleCharsNew[6][11]=getByte("0x98");//
 * 
 * charsOld=new byte[30]; charsNew=new byte[30]; charsOld[0]=getByte("0x88");//
 * charsNew[0]=getByte("0x3C");// charsOld[1]=getByte("0x89");//
 * charsNew[1]=getByte("0x3E");// charsOld[2]=getByte("0xB5");//
 * charsNew[2]=getByte("0xA7");// charsOld[3]=getByte("0xB4");//
 * charsNew[3]=getByte("0xA6");// charsOld[4]=getByte("0xAB");//
 * charsNew[4]=getByte("0xAE");// charsOld[5]=getByte("0xAA");//
 * charsNew[5]=getByte("0xAE");// charsOld[6]=getByte("0xBB");//
 * charsNew[6]=getByte("0xAE");// charsOld[7]=getByte("0x00");//
 * charsNew[7]=getByte("0x20");// charsOld[8]=getByte("0x01");//
 * charsNew[8]=getByte("0x20");// charsOld[9]=getByte("0x02");//
 * charsNew[9]=getByte("0x20");// charsOld[10]=getByte("0x03");//
 * charsNew[10]=getByte("0x20");// charsOld[11]=getByte("0x04");//
 * charsNew[11]=getByte("0x20");// charsOld[12]=getByte("0x05");//
 * charsNew[12]=getByte("0x20");// charsOld[13]=getByte("0x06");//
 * charsNew[13]=getByte("0x20");// charsOld[14]=getByte("0x07");//
 * charsNew[14]=getByte("0x20");// charsOld[15]=getByte("0x08");//
 * charsNew[15]=getByte("0x20");// charsOld[16]=getByte("0x09");//
 * charsNew[16]=getByte("0x20");// charsOld[17]=getByte("0x10");//
 * charsNew[17]=getByte("0x20");// charsOld[18]=getByte("0x11");//
 * charsNew[18]=getByte("0x20");// charsOld[19]=getByte("0x12");//
 * charsNew[19]=getByte("0x20");// charsOld[20]=getByte("0x13");//
 * charsNew[20]=getByte("0x20");// charsOld[21]=getByte("0x14");//
 * charsNew[21]=getByte("0x20");// charsOld[22]=getByte("0x15");//
 * charsNew[22]=getByte("0x20");// charsOld[23]=getByte("0x16");//
 * charsNew[23]=getByte("0x20");// charsOld[24]=getByte("0x17");//
 * charsNew[24]=getByte("0x20");// charsOld[25]=getByte("0x18");//
 * charsNew[25]=getByte("0x20");// charsOld[26]=getByte("0x19");//
 * charsNew[26]=getByte("0x20");// charsOld[27]=getByte("0x1A");//
 * charsNew[27]=getByte("0x20");// charsOld[28]=getByte("0x1B");//
 * charsNew[28]=getByte("0x20");// charsOld[29]=getByte("0x1C");//
 * charsNew[29]=getByte("0x20");// }
 */

