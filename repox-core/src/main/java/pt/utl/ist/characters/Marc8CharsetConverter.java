package pt.utl.ist.characters;

import pt.utl.ist.marc.Field;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.Subfield;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Marc8CharsetConverter {

    private byte[][] doubleCharsOld;
    private byte[][] doubleCharsNew;
    private byte[] charsOld;
    private byte[] charsNew;
    private String[] stringsOld;
    private String[] stringsNew;
    private byte sepI;
    private byte sepF;

    public Marc8CharsetConverter(){
        init();
    }

    public byte[] convertBytes(byte[] array) {
        byte[] ret=new byte[array.length];
        int r=0;
        for (int k = 0; k < array.length; k++) {
            if (array[k] == sepI){
                boolean ends=false;
                int sc=k+1;
                for ( ; sc<array.length ; sc++){
                    if (array[sc] == sepF){
                        ends=true;
                        break;
                    }
                }
                if (ends){
                    k=sc;
                }else{
                    ret[r]=array[k];
                    r++;
                }
            }else{
                Byte newChar=null;
                if (k < array.length - 1)
                    newChar=findCharAtDouble(array[k],array[k+1]);
                if (newChar == null)
                    newChar=findChar(array[k]);
                else
                    k++;
                if (newChar != null){
                    ret[r]=newChar.byteValue();
                }else{
                    ret[r]=array[k];
                }
                r++;
            }
        }

        //remove possible extra characters 0x00 in the array
        int realsz=ret.length;
        byte empty=getByte("0x00");
        for (int idx=ret.length-1 ; idx>=0 ; idx--) {
            if (ret[idx]==empty){
                realsz=idx;
            }else
                break;
        }
        if (realsz != ret.length){
            byte[] newret=new byte[realsz];
            for (int idx=realsz-1 ; idx>=0 ; idx--) {
                newret[idx]=ret[idx];
            }
            ret=newret;
        }
        return ret;
    }

    public String convertString(String str) {
        String ret=null;
        ret=new String(convertBytes(str.getBytes()));
        for (int idx=stringsOld.length-1 ; idx>=0 ; idx--) {
            Pattern p = Pattern.compile(toSafeRegExp(stringsOld[idx]));
            Matcher m = p.matcher(ret);
            ret=m.replaceAll(stringsNew[idx]);
        }

        Pattern p = Pattern.compile(toSafeRegExp("0x95[^0x96]+0x96"));
        Matcher m = p.matcher(ret);
        ret=m.replaceAll("");

        return ret;
    }


    public void convertRecord(Record rec){
        List fields=rec.getFields();

        for (Object field : fields) {
            Field f = (Field) field;
            if (f.isControlField()) {
                String newData = convertString(f.getValue());
                f.setValue(newData);
            } else {
                for (Object o : f.getSubfields()) {
                    Subfield sf = (Subfield) o;
                    String newData = convertString(sf.getValue());
                    sf.setValue(newData);
                }
            }
        }
    }

    /**************************************************************************
     ************                  Private Methods           ******************
     *************************************************************************/
    private static byte getByte(String b){
        return Short.decode(b).byteValue();
    }

    private Byte findCharAtDouble(byte c1, byte c2){
        for (int c=0; c < doubleCharsOld.length; c++){
            if (doubleCharsOld[c][0]==c1){
                for (int i=0; i < doubleCharsOld[c].length; i++){
                    if (doubleCharsOld[c][i]==c2)
                        return new Byte(doubleCharsNew[c][i]);
                }
            }
        }
        return null;
    }

    private String toSafeRegExp(String str){
        Pattern p = Pattern.compile("\\+");
        Matcher m = p.matcher(str);
        str=m.replaceAll("\\\\+");

        p = Pattern.compile("\\*");
        m = p.matcher(str);
        str=m.replaceAll("\\\\*");

        p = Pattern.compile("\\)");
        m = p.matcher(str);
        str=m.replaceAll("\\\\)");

        p = Pattern.compile("\\(");
        m = p.matcher(str);
        str=m.replaceAll("\\\\(");
        return str;
    }

    private Byte findChar(byte ch){
        for (int i=0; i < charsOld.length; i++){
            if (charsOld[i]==ch){
                return new Byte(charsNew[i]);
            }
        }
        return null;
    }


    private void init(){
        sepI=getByte("0x95");
        sepF=getByte("0x96");

        doubleCharsOld=new byte[4][];
        doubleCharsNew=new byte[4][];

        doubleCharsOld[0]=new byte[3];
        doubleCharsNew[0]=new byte[3];
        doubleCharsOld[0][0]=getByte("0xF0");
        doubleCharsOld[0][1]=getByte("0x63");
        doubleCharsOld[0][2]=getByte("0x43");
        doubleCharsNew[0][0]=getByte("0x00");
        doubleCharsNew[0][1]=getByte("0xe7");//ç
        doubleCharsNew[0][2]=getByte("0xc7");//Ç

        doubleCharsOld[1]=new byte[11];
        doubleCharsNew[1]=new byte[11];
        doubleCharsOld[1][0]=getByte("0xE1");
        doubleCharsOld[1][1]=getByte("0x61");
        doubleCharsOld[1][2]=getByte("0x41");
        doubleCharsOld[1][3]=getByte("0x65");
        doubleCharsOld[1][4]=getByte("0x45");
        doubleCharsOld[1][5]=getByte("0x69");
        doubleCharsOld[1][6]=getByte("0x49");
        doubleCharsOld[1][7]=getByte("0x6F");
        doubleCharsOld[1][8]=getByte("0x4F");
        doubleCharsOld[1][9]=getByte("0x75");
        doubleCharsOld[1][10]=getByte("0x55");
        doubleCharsNew[1][0]=getByte("0x00");
        doubleCharsNew[1][1]=getByte("0xe0");//
        doubleCharsNew[1][2]=getByte("0xc0");//
        doubleCharsNew[1][3]=getByte("0xe8");//
        doubleCharsNew[1][4]=getByte("0xc8");//
        doubleCharsNew[1][5]=getByte("0xec");//
        doubleCharsNew[1][6]=getByte("0xCC");//
        doubleCharsNew[1][7]=getByte("0xF2");//
        doubleCharsNew[1][8]=getByte("0xD2");//
        doubleCharsNew[1][9]=getByte("0x75");//
        doubleCharsNew[1][10]=getByte("0xD9");//

        doubleCharsOld[2]=new byte[13];
        doubleCharsNew[2]=new byte[13];
        doubleCharsOld[2][0]=getByte("0xE2");
        doubleCharsOld[2][1]=getByte("0x61");
        doubleCharsOld[2][2]=getByte("0x41");
        doubleCharsOld[2][3]=getByte("0x65");
        doubleCharsOld[2][4]=getByte("0x45");
        doubleCharsOld[2][5]=getByte("0x69");
        doubleCharsOld[2][6]=getByte("0x49");
        doubleCharsOld[2][7]=getByte("0x6F");
        doubleCharsOld[2][8]=getByte("0x4F");
        doubleCharsOld[2][9]=getByte("0x75");
        doubleCharsOld[2][10]=getByte("0x55");
        doubleCharsOld[2][11]=getByte("0x79");
        doubleCharsOld[2][12]=getByte("0x59");
        doubleCharsNew[2][0]=getByte("0x00");
        doubleCharsNew[2][1]=getByte("0xE1");//
        doubleCharsNew[2][2]=getByte("0xC1");//
        doubleCharsNew[2][3]=getByte("0xE9");//
        doubleCharsNew[2][4]=getByte("0xC9");//
        doubleCharsNew[2][5]=getByte("0xED");//
        doubleCharsNew[2][6]=getByte("0xCD");//
        doubleCharsNew[2][7]=getByte("0xF3");//
        doubleCharsNew[2][8]=getByte("0xD3");//
        doubleCharsNew[2][9]=getByte("0xFA");//
        doubleCharsNew[2][10]=getByte("0xDA");//
        doubleCharsNew[2][11]=getByte("0xFD");//
        doubleCharsNew[2][12]=getByte("0xDD");//

//        doubleCharsOld[3]=new byte[11];
//        doubleCharsNew[3]=new byte[11];
//        doubleCharsOld[3][0]=getByte("0xC3"); 
//        doubleCharsOld[3][1]=getByte("0x61");
//        doubleCharsOld[3][2]=getByte("0x41");
//        doubleCharsOld[3][3]=getByte("0x65");
//        doubleCharsOld[3][4]=getByte("0x45");
//        doubleCharsOld[3][5]=getByte("0x69");
//        doubleCharsOld[3][6]=getByte("0x49");
//        doubleCharsOld[3][7]=getByte("0x6F");
//        doubleCharsOld[3][8]=getByte("0x4F");
//        doubleCharsOld[3][9]=getByte("0x75");
//        doubleCharsOld[3][10]=getByte("0x55");
//        doubleCharsNew[3][0]=getByte("0x00");
//        doubleCharsNew[3][1]=getByte("0xE2");//
//        doubleCharsNew[3][2]=getByte("0xC2");//
//        doubleCharsNew[3][3]=getByte("0xEA");//
//        doubleCharsNew[3][4]=getByte("0xCA");//
//        doubleCharsNew[3][5]=getByte("0xEE");//
//        doubleCharsNew[3][6]=getByte("0xCE");//
//        doubleCharsNew[3][7]=getByte("0xF4");//
//        doubleCharsNew[3][8]=getByte("0xD4");//
//        doubleCharsNew[3][9]=getByte("0xFB");//
//        doubleCharsNew[3][10]=getByte("0xDB");//

        doubleCharsOld[3]=new byte[7];
        doubleCharsNew[3]=new byte[7];
        doubleCharsOld[3][0]=getByte("0xE4");
        doubleCharsOld[3][1]=getByte("0x61");
        doubleCharsOld[3][2]=getByte("0x41");
        doubleCharsOld[3][3]=getByte("0x6F");
        doubleCharsOld[3][4]=getByte("0x4F");
        doubleCharsOld[3][5]=getByte("0x6E");
        doubleCharsOld[3][6]=getByte("0x4E");
        doubleCharsNew[3][0]=getByte("0x00");
        doubleCharsNew[3][1]=getByte("0xE3");//
        doubleCharsNew[3][2]=getByte("0xC3");//
        doubleCharsNew[3][3]=getByte("0xF5");//
        doubleCharsNew[3][4]=getByte("0xD5");//
        doubleCharsNew[3][5]=getByte("0xF1");//
        doubleCharsNew[3][6]=getByte("0xD1");//

//        doubleCharsOld[5]=new byte[7];
//        doubleCharsNew[5]=new byte[7];
//        doubleCharsOld[5][0]=getByte("0xC9"); 
//        doubleCharsOld[5][1]=getByte("0x61");
//        doubleCharsOld[5][2]=getByte("0x41");
//        doubleCharsOld[5][3]=getByte("0x6F");
//        doubleCharsOld[5][4]=getByte("0x4F");
//        doubleCharsOld[5][5]=getByte("0x6E");
//        doubleCharsOld[5][6]=getByte("0x4E");
//        doubleCharsNew[5][0]=getByte("0x00");
//        doubleCharsNew[4][1]=getByte("0xE3");//
//        doubleCharsNew[4][2]=getByte("0xC3");//
//        doubleCharsNew[4][3]=getByte("0xF5");//
//        doubleCharsNew[4][4]=getByte("0xD5");//
//        doubleCharsNew[4][5]=getByte("0xF1");//
//        doubleCharsNew[4][6]=getByte("0xD1");//
//        
//        doubleCharsOld[6]=new byte[12];
//        doubleCharsNew[6]=new byte[12];
//        doubleCharsOld[6][0]=getByte("0xC8"); 
//        doubleCharsOld[6][1]=getByte("0x61");
//        doubleCharsOld[6][2]=getByte("0x41");
//        doubleCharsOld[6][3]=getByte("0x65");
//        doubleCharsOld[6][4]=getByte("0x45");
//        doubleCharsOld[6][5]=getByte("0x69");
//        doubleCharsOld[6][6]=getByte("0x49");
//        doubleCharsOld[6][7]=getByte("0x6F");
//        doubleCharsOld[6][8]=getByte("0x4F");
//        doubleCharsOld[6][9]=getByte("0x75");
//        doubleCharsOld[6][10]=getByte("0x55");
//        doubleCharsOld[6][11]=getByte("0x79");
//        doubleCharsNew[6][0]=getByte("0x00");
//        doubleCharsNew[6][1]=getByte("0xE4");//
//        doubleCharsNew[6][2]=getByte("0xC4");//
//        doubleCharsNew[6][3]=getByte("0xEB");//
//        doubleCharsNew[6][4]=getByte("0xCB");//
//        doubleCharsNew[6][5]=getByte("0xEF");//
//        doubleCharsNew[6][6]=getByte("0xCF");//
//        doubleCharsNew[6][7]=getByte("0xF6");//
//        doubleCharsNew[6][8]=getByte("0xD6");//
//        doubleCharsNew[6][9]=getByte("0xFC");//
//        doubleCharsNew[6][10]=getByte("0xDC");//
//        doubleCharsNew[6][11]=getByte("0xFF");//

        charsOld=new byte[4];
        charsNew=new byte[4];
        charsOld[0]=getByte("0xE7");//
        charsNew[0]=getByte("0xAA");//        
        charsOld[1]=getByte("0xE6");//
        charsNew[1]=getByte("0xBA");//        
        charsOld[2]=getByte("0x88");//
        charsNew[2]=getByte("0x3C");//        
        charsOld[3]=getByte("0x89");//
        charsNew[3]=getByte("0x3E");//        



//        charsOld[0]=getByte("0x88");//
//        charsNew[0]=getByte("0x3C");//        
//        charsOld[1]=getByte("0x89");//
//        charsNew[1]=getByte("0x3E");//        
//        charsOld[2]=getByte("0xB5");//
//        charsNew[2]=getByte("0xBA");//
//        charsOld[3]=getByte("0xA7");//
//        charsNew[3]=getByte("0xBA");//
//        charsOld[4]=getByte("0xA6");//
//        charsNew[4]=getByte("0xAA");//
//        charsOld[5]=getByte("0xB4");//
//        charsNew[5]=getByte("0xAA");//
//        charsOld[6]=getByte("0xAA");//
//        charsNew[6]=getByte("0xAB");//
//        charsOld[7]=getByte("0x00");//
//        charsNew[7]=getByte("0x20");//
//        charsOld[8]=getByte("0x01");//
//        charsNew[8]=getByte("0x20");//
//        charsOld[9]=getByte("0x02");//
//        charsNew[9]=getByte("0x20");//
//        charsOld[10]=getByte("0x03");//
//        charsNew[10]=getByte("0x20");//
//        charsOld[11]=getByte("0x04");//
//        charsNew[11]=getByte("0x20");//
//        charsOld[12]=getByte("0x05");//
//        charsNew[12]=getByte("0x20");//
//        charsOld[13]=getByte("0x06");//
//        charsNew[13]=getByte("0x20");//
//        charsOld[14]=getByte("0x07");//
//        charsNew[14]=getByte("0x20");//
//        charsOld[15]=getByte("0x08");//
//        charsNew[15]=getByte("0x20");//
//        charsOld[16]=getByte("0x09");//
//        charsNew[16]=getByte("0x20");//
//        charsOld[17]=getByte("0x10");//
//        charsNew[17]=getByte("0x20");//
//        charsOld[18]=getByte("0x11");//
//        charsNew[18]=getByte("0x20");//
//        charsOld[19]=getByte("0x12");//
//        charsNew[19]=getByte("0x20");//
//        charsOld[20]=getByte("0x13");//
//        charsNew[20]=getByte("0x20");//
//        charsOld[21]=getByte("0x14");//
//        charsNew[21]=getByte("0x20");//
//        charsOld[22]=getByte("0x15");//
//        charsNew[22]=getByte("0x20");//
//        charsOld[23]=getByte("0x16");//
//        charsNew[23]=getByte("0x20");//
//        charsOld[24]=getByte("0x17");//
//        charsNew[24]=getByte("0x20");//
//        charsOld[25]=getByte("0x18");//
//        charsNew[25]=getByte("0x20");//
//        charsOld[26]=getByte("0x19");//
//        charsNew[26]=getByte("0x20");//
//        charsOld[27]=getByte("0x1A");//
//        charsNew[27]=getByte("0x20");//
//        charsOld[28]=getByte("0x1B");//
//        charsNew[28]=getByte("0x20");//
//        charsOld[29]=getByte("0x1C");//
//        charsNew[29]=getByte("0x20");//

        stringsOld=new String[2];
        stringsNew=new String[2];
        byte[] tmp=new byte[1];
        stringsOld[0]="<U+00AA>";
        stringsNew[0]="ª";
        stringsOld[1]="<U+00BA>";
        stringsNew[1]="º";

        //Atenção existem mais conversões no metodo convertString        
    }


    /**************************************************************************
     ************                    Main                    ******************
     *************************************************************************/
    public static void main(String[] args) {

        HorizonConverter conv=new HorizonConverter();

        System.out.println(System.getProperty("file.encoding"));
        String original = new String("Direcção de Serviços");
        System.out.println("original = " + original);
        System.out.println();
        original=new String(conv.convertBytes(original.getBytes()));
        try {
            byte[] utf8Bytes = original.getBytes("UTF8");

            byte[] defaultBytes = original.getBytes();

            String roundTrip = new String(utf8Bytes, "UTF8");
            System.out.println("roundTrip = " + roundTrip);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    } // main

}












/*
    public void StringConverterDOS(){
        doubleCharsOld=new byte[7][];
        doubleCharsNew=new byte[7][];

        doubleCharsOld[0]=new byte[3];
        doubleCharsNew[0]=new byte[3];
        doubleCharsOld[0][0]=getByte("0xD0");
        doubleCharsOld[0][1]=getByte("0x63");
        doubleCharsOld[0][2]=getByte("0x43");
        doubleCharsNew[0][0]=getByte("0x00");
        doubleCharsNew[0][1]=getByte("0x87");//ç
        doubleCharsNew[0][2]=getByte("0x80");//Ç                  
        
        doubleCharsOld[1]=new byte[11];
        doubleCharsNew[1]=new byte[11];
        doubleCharsOld[1][0]=getByte("0xC1"); 
        doubleCharsOld[1][1]=getByte("0x61");
        doubleCharsOld[1][2]=getByte("0x41");
        doubleCharsOld[1][3]=getByte("0x65");
        doubleCharsOld[1][4]=getByte("0x45");
        doubleCharsOld[1][5]=getByte("0x69");
        doubleCharsOld[1][6]=getByte("0x49");
        doubleCharsOld[1][7]=getByte("0x6F");
        doubleCharsOld[1][8]=getByte("0x4F");
        doubleCharsOld[1][9]=getByte("0x75");
        doubleCharsOld[1][10]=getByte("0x55");
        doubleCharsNew[1][0]=getByte("0x00");
        doubleCharsNew[1][1]=getByte("0x85");//
        doubleCharsNew[1][2]=getByte("0xB7");//
        doubleCharsNew[1][3]=getByte("0x8A");//
        doubleCharsNew[1][4]=getByte("0xD4");//
        doubleCharsNew[1][5]=getByte("0x8D");//
        doubleCharsNew[1][6]=getByte("0xDE");//
        doubleCharsNew[1][7]=getByte("0x95");//
        doubleCharsNew[1][8]=getByte("0xE3");//
        doubleCharsNew[1][9]=getByte("0x97");//
        doubleCharsNew[1][10]=getByte("0xEB");//

        doubleCharsOld[1]=new byte[13];
        doubleCharsNew[1]=new byte[13];
        doubleCharsOld[2][0]=getByte("0xC2"); 
        doubleCharsOld[2][1]=getByte("0x61");
        doubleCharsOld[2][2]=getByte("0x41");
        doubleCharsOld[2][3]=getByte("0x65");
        doubleCharsOld[2][4]=getByte("0x45");
        doubleCharsOld[2][5]=getByte("0x69");
        doubleCharsOld[2][6]=getByte("0x49");
        doubleCharsOld[2][7]=getByte("0x6F");
        doubleCharsOld[2][8]=getByte("0x4F");
        doubleCharsOld[2][9]=getByte("0x75");
        doubleCharsOld[2][10]=getByte("0x55");
        doubleCharsOld[2][11]=getByte("0x79");
        doubleCharsOld[2][12]=getByte("0x59");
        doubleCharsNew[2][0]=getByte("0x00");
        doubleCharsNew[2][1]=getByte("0xA0");//
        doubleCharsNew[2][2]=getByte("0xB5");//
        doubleCharsNew[2][3]=getByte("0x82");//
        doubleCharsNew[2][4]=getByte("0x90");//
        doubleCharsNew[2][5]=getByte("0xA1");//
        doubleCharsNew[2][6]=getByte("0xD6");//
        doubleCharsNew[2][7]=getByte("0xA2");//
        doubleCharsNew[2][8]=getByte("0xE0");//
        doubleCharsNew[2][9]=getByte("0xA3");//
        doubleCharsNew[2][10]=getByte("0xE9");//
        doubleCharsNew[2][11]=getByte("0xEC");//
        doubleCharsNew[2][12]=getByte("0xED");//

        doubleCharsOld[3]=new byte[11];
        doubleCharsNew[3]=new byte[11];
        doubleCharsOld[3][0]=getByte("0xC3"); 
        doubleCharsOld[3][1]=getByte("0x61");
        doubleCharsOld[3][2]=getByte("0x41");
        doubleCharsOld[3][3]=getByte("0x65");
        doubleCharsOld[3][4]=getByte("0x45");
        doubleCharsOld[3][5]=getByte("0x69");
        doubleCharsOld[3][6]=getByte("0x49");
        doubleCharsOld[3][7]=getByte("0x6F");
        doubleCharsOld[3][8]=getByte("0x4F");
        doubleCharsOld[3][9]=getByte("0x75");
        doubleCharsOld[3][10]=getByte("0x55");
        doubleCharsNew[3][0]=getByte("0x00");
        doubleCharsNew[3][1]=getByte("0x83");//
        doubleCharsNew[3][2]=getByte("0xB6");//
        doubleCharsNew[3][3]=getByte("0x88");//
        doubleCharsNew[3][4]=getByte("0xD2");//
        doubleCharsNew[3][5]=getByte("0x8C");//
        doubleCharsNew[3][6]=getByte("0xD7");//
        doubleCharsNew[3][7]=getByte("0x93");//
        doubleCharsNew[3][8]=getByte("0xE2");//
        doubleCharsNew[3][9]=getByte("0x96");//
        doubleCharsNew[3][10]=getByte("0xEA");//

        doubleCharsOld[4]=new byte[7];
        doubleCharsNew[4]=new byte[7];
        doubleCharsOld[4][0]=getByte("0xC4"); 
        doubleCharsOld[4][1]=getByte("0x61");
        doubleCharsOld[4][2]=getByte("0x41");
        doubleCharsOld[4][3]=getByte("0x6F");
        doubleCharsOld[4][4]=getByte("0x4F");
        doubleCharsOld[4][5]=getByte("0x6E");
        doubleCharsOld[4][6]=getByte("0x4E");
        doubleCharsNew[4][0]=getByte("0x00");
        doubleCharsNew[4][1]=getByte("0xC6");//
        doubleCharsNew[4][2]=getByte("0xC7");//
        doubleCharsNew[4][3]=getByte("0xE4");//
        doubleCharsNew[4][4]=getByte("0xE5");//
        doubleCharsNew[4][5]=getByte("0xA4");//
        doubleCharsNew[4][6]=getByte("0xA5");//

        doubleCharsOld[5]=new byte[7];
        doubleCharsNew[5]=new byte[7];
        doubleCharsOld[5][0]=getByte("0xC9"); 
        doubleCharsOld[5][1]=getByte("0x61");
        doubleCharsOld[5][2]=getByte("0x41");
        doubleCharsOld[5][3]=getByte("0x6F");
        doubleCharsOld[5][4]=getByte("0x4F");
        doubleCharsOld[5][5]=getByte("0x6E");
        doubleCharsOld[5][6]=getByte("0x4E");
        doubleCharsNew[5][0]=getByte("0x00");
        doubleCharsNew[5][1]=getByte("0xC6");//
        doubleCharsNew[5][2]=getByte("0xC7");//
        doubleCharsNew[5][3]=getByte("0xE4");//
        doubleCharsNew[5][4]=getByte("0xE5");//
        doubleCharsNew[5][5]=getByte("0xA4");//
        doubleCharsNew[5][6]=getByte("0xA5");//
        
        doubleCharsOld[6]=new byte[12];
        doubleCharsNew[6]=new byte[12];
        doubleCharsOld[6][0]=getByte("0xC8"); 
        doubleCharsOld[6][1]=getByte("0x61");
        doubleCharsOld[6][2]=getByte("0x41");
        doubleCharsOld[6][3]=getByte("0x65");
        doubleCharsOld[6][4]=getByte("0x45");
        doubleCharsOld[6][5]=getByte("0x69");
        doubleCharsOld[6][6]=getByte("0x49");
        doubleCharsOld[6][7]=getByte("0x6F");
        doubleCharsOld[6][8]=getByte("0x4F");
        doubleCharsOld[6][9]=getByte("0x75");
        doubleCharsOld[6][10]=getByte("0x55");
        doubleCharsOld[6][11]=getByte("0x79");
        doubleCharsNew[6][0]=getByte("0x00");
        doubleCharsNew[6][1]=getByte("0x84");//
        doubleCharsNew[6][2]=getByte("0x8E");//
        doubleCharsNew[6][3]=getByte("0x89");//
        doubleCharsNew[6][4]=getByte("0xD3");//
        doubleCharsNew[6][5]=getByte("0x8B");//
        doubleCharsNew[6][6]=getByte("0xD8");//
        doubleCharsNew[6][7]=getByte("0x94");//
        doubleCharsNew[6][8]=getByte("0x99");//
        doubleCharsNew[6][9]=getByte("0x81");//
        doubleCharsNew[6][10]=getByte("0x9A");//
        doubleCharsNew[6][11]=getByte("0x98");//
        
        charsOld=new byte[30];
        charsNew=new byte[30];        
        charsOld[0]=getByte("0x88");//
        charsNew[0]=getByte("0x3C");//
        charsOld[1]=getByte("0x89");//
        charsNew[1]=getByte("0x3E");//
        charsOld[2]=getByte("0xB5");//
        charsNew[2]=getByte("0xA7");//
        charsOld[3]=getByte("0xB4");//
        charsNew[3]=getByte("0xA6");//
        charsOld[4]=getByte("0xAB");//
        charsNew[4]=getByte("0xAE");//
        charsOld[5]=getByte("0xAA");//
        charsNew[5]=getByte("0xAE");//
        charsOld[6]=getByte("0xBB");//
        charsNew[6]=getByte("0xAE");//
        charsOld[7]=getByte("0x00");//
        charsNew[7]=getByte("0x20");//
        charsOld[8]=getByte("0x01");//
        charsNew[8]=getByte("0x20");//
        charsOld[9]=getByte("0x02");//
        charsNew[9]=getByte("0x20");//
        charsOld[10]=getByte("0x03");//
        charsNew[10]=getByte("0x20");//
        charsOld[11]=getByte("0x04");//
        charsNew[11]=getByte("0x20");//
        charsOld[12]=getByte("0x05");//
        charsNew[12]=getByte("0x20");//
        charsOld[13]=getByte("0x06");//
        charsNew[13]=getByte("0x20");//
        charsOld[14]=getByte("0x07");//
        charsNew[14]=getByte("0x20");//
        charsOld[15]=getByte("0x08");//
        charsNew[15]=getByte("0x20");//
        charsOld[16]=getByte("0x09");//
        charsNew[16]=getByte("0x20");//
        charsOld[17]=getByte("0x10");//
        charsNew[17]=getByte("0x20");//
        charsOld[18]=getByte("0x11");//
        charsNew[18]=getByte("0x20");//
        charsOld[19]=getByte("0x12");//
        charsNew[19]=getByte("0x20");//
        charsOld[20]=getByte("0x13");//
        charsNew[20]=getByte("0x20");//
        charsOld[21]=getByte("0x14");//
        charsNew[21]=getByte("0x20");//
        charsOld[22]=getByte("0x15");//
        charsNew[22]=getByte("0x20");//
        charsOld[23]=getByte("0x16");//
        charsNew[23]=getByte("0x20");//
        charsOld[24]=getByte("0x17");//
        charsNew[24]=getByte("0x20");//
        charsOld[25]=getByte("0x18");//
        charsNew[25]=getByte("0x20");//
        charsOld[26]=getByte("0x19");//
        charsNew[26]=getByte("0x20");//
        charsOld[27]=getByte("0x1A");//
        charsNew[27]=getByte("0x20");//
        charsOld[28]=getByte("0x1B");//
        charsNew[28]=getByte("0x20");//
        charsOld[29]=getByte("0x1C");//
        charsNew[29]=getByte("0x20");//
    }
*/    

