package pt.utl.ist.util;

import java.util.List;

/**
 */
public class CompareUtil {
    /**
     * @param object1
     * @param object2
     * @return boolean comparing the 2 objects
     */
    public static boolean compareObjectsAndNull(Object object1, Object object2) {
        if ((object1 == null && object2 == null) || (object1 != null && object1.equals(object2))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param arg1
     * @param arg2
     * @return boolean comparing the 2 arrays
     */
    public static boolean compareArraysAndNull(Object[] arg1, Object[] arg2) {
        if (arg1 == null && arg2 == null) {
            return true;
        } else if ((arg1 == null && arg2 != null) || (arg1 != null && arg2 == null) || arg1.length != arg2.length) {
            return false;
        } else {
            for (int i = 0; i < arg1.length; i++) {
                if (!compareObjectsAndNull(arg1[i], arg2[i])) { return false; }
            }

            return true;
        }
    }

    /**
     * @param arg1
     * @param arg2
     * @return boolean comparing the 2 Lists
     */
    public static boolean compareListsAndNull(List arg1, List arg2) {
        if (arg1 == null && arg2 == null) {
            return true;
        } else if ((arg1 == null && arg2 != null) || (arg1 != null && arg2 == null) || arg1.size() != arg2.size()) {
            return false;
        } else {
            for (int i = 0; i < arg1.size(); i++) {
                if (!compareObjectsAndNull(arg1.get(i), arg2.get(i))) { return false; }
            }

            return true;
        }
    }
}
