/*
 * AuthorityUtil.java
 *
 * Created on 17 de Maio de 2003, 0:13
 */

package pt.utl.ist.marc.util;

import pt.utl.ist.marc.Field;
import pt.utl.ist.marc.Record;


/**
 *
 * @author  Nuno Freire
 */
public class AuthorityUtil {
    public static final int AT_UNKNOWN=0;
    public static final int AT_AUTHOR=1;
    public static final int AT_SUBJECT=2;
    
    public static int findAuthorityType(Record rec){
        Field mainHeading=getAuthorityMainHeading(rec);
        if (mainHeading==null)
            return AT_UNKNOWN;
        int tag200=mainHeading.getTag();
        //String sistema=MarcUtil.getSingleFieldValue("152",'b',rec);
        if(tag200==200 || tag200==210 || tag200==220 ){
            String scx=mainHeading.getSingleSubfieldValue('x');
            return scx==null ? AT_AUTHOR:AT_SUBJECT;
        }else
            return AT_SUBJECT;
    } 
    
    public static Field getAuthorityMainHeading(Record rec){
        for (Object o : rec.getFields()) {
            Field fld = (Field) o;
            if (fld.getTag() >= 200 && fld.getTag() < 300) {
                return fld;
            }
        }
        return null;
    } 
    
}
