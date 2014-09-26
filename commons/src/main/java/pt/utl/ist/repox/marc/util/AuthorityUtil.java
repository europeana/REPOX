/*
 * AuthorityUtil.java
 *
 * Created on 17 de Maio de 2003, 0:13
 */

package pt.utl.ist.repox.marc.util;

import pt.utl.ist.repox.marc.Field;
import pt.utl.ist.repox.marc.Record;

/**
 * 
 * @author Nuno Freire
 */
public class AuthorityUtil {
    /** AuthorityUtil AT_UNKNOWN */
    public static final int AT_UNKNOWN = 0;
    /** AuthorityUtil AT_AUTHOR */
    public static final int AT_AUTHOR  = 1;
    /** AuthorityUtil AT_SUBJECT */
    public static final int AT_SUBJECT = 2;

    /**
     * @param rec
     * @return int
     */
    public static int findAuthorityType(Record rec) {
        Field mainHeading = getAuthorityMainHeading(rec);
        if (mainHeading == null) return AT_UNKNOWN;
        int tag200 = mainHeading.getTag();
        //String sistema=MarcUtil.getSingleFieldValue("152",'b',rec);
        if (tag200 == 200 || tag200 == 210 || tag200 == 220) {
            String scx = mainHeading.getSingleSubfieldValue('x');
            return scx == null ? AT_AUTHOR : AT_SUBJECT;
        } else
            return AT_SUBJECT;
    }

    /**
     * @param rec
     * @return Field
     */
    public static Field getAuthorityMainHeading(Record rec) {
        for (Object o : rec.getFields()) {
            Field fld = (Field)o;
            if (fld.getTag() >= 200 && fld.getTag() < 300) { return fld; }
        }
        return null;
    }

}
