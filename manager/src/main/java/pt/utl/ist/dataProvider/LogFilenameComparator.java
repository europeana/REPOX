package pt.utl.ist.dataProvider;

import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.repox.util.date.DateUtil;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

/**
 */
public class LogFilenameComparator implements Comparator<String> {
    //FORMAT: 2009-11\3_20091109_154534.log
    @Override
    public int compare(String filename1, String filename2) {
        String dateString1 = getFilenameDate(filename1);
        String dateString2 = getFilenameDate(filename2);

        try {
            Date date1 = DateUtil.string2Date(dateString1, TimeUtil.LONG_DATE_FORMAT_COMPACT);
            Date date2 = DateUtil.string2Date(dateString2, TimeUtil.LONG_DATE_FORMAT_COMPACT);

            return (date1.after(date2) ? 1 : -1);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFilenameDate(String filename) {
        //get last 15 chars except file extension
        String dateString = filename.substring(filename.length() - 19, filename.length() - 4);

        return dateString;
    }
}
