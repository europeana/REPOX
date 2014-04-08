/*
 * ByteInspector.java
 *
 * Created on 8 de Abril de 2002, 17:21
 */

package pt.utl.ist.characters;

/**
 *
 * @author  Nuno Freire
 */
public class ByteInspector {

    public static String printBytes(byte[] array) {
        StringBuffer sb=new StringBuffer();
        for (byte b : array) {
            System.out.print("0x" + byteToHex(b));
            sb.append("0x").append(byteToHex(b));
        }
        return sb.toString();
    }

    public static String printBytes(String str, String enc){
        StringBuffer sb=new StringBuffer();
        int sz=str.length();
        for (int idx=0 ; idx<sz ; idx++) {
            String c=str.substring(idx,idx+1);
            System.out.print(c);
            System.out.print("-");
            try{
                sb.append(printBytes(c.getBytes( (enc.equals("") ? "8859_1" : enc) )));
            }catch(Exception e){
                sb.append(e.getMessage());
                System.out.print(e.getMessage());
            }
            sb.append("\n");
            System.out.println();
        }
        return sb.toString();
    }
    
/**************************************************************************
 ************                  Private Methods           ******************
 *************************************************************************/
    
    private static String byteToHex(byte b) {
        // Returns hex String representation of byte b
        char hexDigit[] = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };
        char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
        return new String(array);
    }
}
