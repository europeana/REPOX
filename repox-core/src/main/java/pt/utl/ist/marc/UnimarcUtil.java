package pt.utl.ist.marc;

import java.util.List;

/**
 */
public class UnimarcUtil {
    /**
     * gets the 2xx field of the record.
     * 
     * @param r
     * @return the 2xx field of the record, or null if not found
     */
    public static Field getMainHeadingField(Record r) {
        for (Object o : r.getFields()) {
            Field fld = (Field)o;
            if (fld.getTag() >= 200 && fld.getTag() < 300) { return fld; }
        }
        return null;
    }

    /**
     * Gets a String representation of the publication year
     * 
     * @param r
     * @return the 210$d the record, null if the record contains no 210$d
     */
    public static String getYearOfPublication(Record r) {
        String dat = r.getSingleFieldValue(210, 'd');
        if (dat != null)
            dat = dat.trim();
        else {
            String f100 = r.getSingleFieldValue(100, 'a');
            if (f100 != null) {
                dat = f100.substring(9, 12);
            }
        }
        return dat;
    }

    /**
     * Gets a String representation of the form
     * 
     * @param r
     * @return the 105$a 4-7 or Etiq.reg./6 of the record, null if the record
     *         contains none
     */
    public static String getForm(Record r) {
        String dat = r.getSingleFieldValue(105, 'a');
        if (dat != null && dat.length() > 7) return dat.substring(4, 8);
        if (r.getLeader() != null && r.getLeader().length() > 6) return r.getLeader().substring(6, 7);
        return null;
    }

    /**
     * Gets a String representation of the language code
     * @param r 
     * 
     * @return the 101$a of the record, null if the record contains none
     */
    public static String getLanguage(Record r) {
        String dat = r.getSingleFieldValue(101, 'a');
        if (dat != null) dat = dat.trim();
        return dat;
    }

    /**
     * Gets a String representation of the title of the record. It contains the
     * title, the publication location and the publication date. To be used only
     * with bibliographic records
     * @param r 
     * 
     * @return the title of the record, an empty string if the record contains
     *         no title
     */
    public static String getTitle(Record r) {
        String tit = getTitleWithoutPlaceAndDate(r);
        if (tit.equals("")) return "";
        String loc = r.getSingleFieldValue(210, 'a');
        String dat = r.getSingleFieldValue(210, 'd');
        if (tit == null) tit = "";
        if (loc == null || loc.toLowerCase().indexOf("s.l.") != -1)
            loc = "";
        else
            loc = ", " + loc;
        if (dat == null)
            dat = "";
        else
            dat = ", " + dat;
        return tit + loc + dat;
    }

    /**
     * Gets a String representation of the title of the record. It contains the
     * only the title, not the publication location and the publication date. To
     * be used only with bibliographic records
     * @param r 
     * 
     * @return the title of the record, an empty string if the record contains
     *         no title
     */
    public static String getTitleWithoutPlaceAndDate(Record r) {
        List<Field> fields200 = r.getField(200);
        if (fields200.size() == 0) return "";
        Field f200 = r.getField(200).get(0);
        String tit = f200.getSingleSubfieldValue('a');
        if (tit == null) return "";
        for (String sc : f200.getSubfieldValues('c'))
            tit += ". " + sc;
        for (String sc : f200.getSubfieldValues('d'))
            tit += " = " + sc;
        for (String sc : f200.getSubfieldValues('e'))
            tit += ": " + sc;
        for (String sc : f200.getSubfieldValues('i'))
            tit += ". " + sc;

        tit = tit.replaceFirst("^\\s*<([^>]+)>", "$1");
        return tit;
    }

    /**
     * Gets a String representation of the title of the record. It contains the
     * title, the publication location and the publication date. To be used only
     * with bibliographic records
     * @param r 
     * 
     * @return the title of the record
     */
    public static String getAuthorList(Record r) {
        String ret = "";
        int[][] fields = new int[3][];
        fields[0] = new int[] { 700, 710, 720 };
        fields[1] = new int[] { 701, 711, 721 };
        fields[2] = new int[] { 702, 712, 722 };
        for (int i = 0; i < 3; i++) {
            for (Field fld : r.getFields(fields[i])) {
                String sfa = fld.getSingleSubfieldValue('a');
                String sfb = fld.getSingleSubfieldValue('b');
                if (sfa != null) {
                    sfa = sfa.trim();
                    if (sfa.endsWith(",")) sfa = sfa.substring(0, sfa.length() - 1).trim();
                } else {
                    continue;
                }
                if (sfb != null) {
                    sfb = sfb.trim();
                    if (sfb.endsWith(",")) sfb = sfb.substring(0, sfb.length() - 1).trim();
                }
                if (!ret.equals("")) ret += "; ";
                if (sfb == null) {
                    ret += sfa;
                } else {
                    ret += sfa + ", " + sfb;
                }
            }
        }
        return ret;
    }
}
