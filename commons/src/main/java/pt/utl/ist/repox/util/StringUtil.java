package pt.utl.ist.repox.util;

import org.apache.log4j.Logger;

import pt.utl.ist.repox.reports.LogUtil;
import pt.utl.ist.repox.util.date.DateUtil;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;

/**
 */
public class StringUtil {
    private static final Logger  log                         = Logger.getLogger(StringUtil.class);
    private static final boolean LOG_TO_LOG4J                = true;
    private static final String  COLLECTION_SEPARATOR_STRING = " ";

    /**
     * @param array
     * @return String representation of the array
     */
    public static String getArrayAsString(Object[] array) {
        StringBuffer result = new StringBuffer();

        result.append("[");
        for (Object currentObject : array) {
            result.append("<");
            result.append(currentObject);
            result.append(">").append(COLLECTION_SEPARATOR_STRING);
        }
        result.append("]");

        return result.toString();
    }

    /**
     * @param collection
     * @param stringMethod
     * @return String representation of the collection
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static String getCollectionAsString(Collection collection, Method stringMethod) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        StringBuffer result = new StringBuffer();

        result.append("[");
        for (Object currentObject : collection) {
            result.append("<");
            result.append(stringMethod.invoke(currentObject));
            result.append(">").append(COLLECTION_SEPARATOR_STRING);
        }
        result.append("]");

        return result.toString();
    }

    /**
     * @param numberToParse
     * @param numberDigits
     * @return String
     */
    public static String getFixedSizeNumber(int numberToParse, int numberDigits) {
        String numberToParseAsString = String.valueOf(numberToParse);
        if (numberToParseAsString.length() > numberDigits) {
            return String.valueOf((int)Math.pow(10, numberDigits) - 1);
        } else {
            String resultNumber = numberToParseAsString;
            while (resultNumber.length() < numberDigits) {
                resultNumber = "0" + resultNumber;
            }

            return resultNumber;
        }
    }

    /**
     * @param file
     * @return String of the date of the last modification time of the file provided
     */
    public static String getFileModificationTime(File file) {
        return DateUtil.date2String(new Date(file.lastModified()), TimeUtil.SHORT_DATE_FORMAT);
    }

    /**
     * @param stringToEncode
     * @return String encoded
     * @throws UnsupportedEncodingException
     */
    public static String encode(String stringToEncode) throws UnsupportedEncodingException {
        return URLEncoder.encode(stringToEncode, "UTF-8");

    }

    /**
     * @param originalString
     * @return String sanitized
     */
    public static String sanitizeJavascript(String originalString) {
        return originalString.replaceAll("[\\\\(\"\')\\]\\[]", "\\\\$0");
    }

    /**
     * @param message
     * @param clazz
     * @param file
     */
    public static void simpleLog(String message, Class clazz, File file) {
        //        System.out.println("INFO--------- " + message);
        LogUtil.addSimpleInfoLog(message, clazz, file, true);
        //		BufferedWriter writer = null;
        //        try {
        //			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
        //			String messageToLog = DateUtil.date2String(new Date(), TimeUtil.LONG_DATE_FORMAT_TIMEZONE)
        //									+ " " + clazz.getName() + " " + message;
        //			if(LOG_TO_LOG4J) {
        //				Logger.getLogger(clazz).debug(messageToLog);
        //			}
        //			messageToLog = messageToLog + "\n";
        //			writer.write(messageToLog);
        //		}
        //		catch (IOException e) {
        //			log.error("Error writting to REPOX log file: " + file.getAbsolutePath() + " message: " + message, e);
        //		}
        //        finally {
        //            if(writer != null){
        //                try {
        //                    writer.close();
        //                } catch (IOException e) {
        //                    log.error("Error closing REPOX log file: " + file.getAbsolutePath() + " message: " + message, e);
        //                }
        //            }
        //        }
    }

    /**
     * @param message
     * @param clazz
     * @param file
     * @param writeToXML
     */
    public static void simpleLog(String message, Class clazz, File file, boolean writeToXML) {
        //        System.out.println("INFO--------- " + message);
        LogUtil.addSimpleInfoLog(message, clazz, file, writeToXML);
    }

    /**
     * @param message
     * @param cause
     * @param clazz
     * @param file
     */
    public static void simpleLog(String message, Exception cause, Class clazz, File file) {
        LogUtil.addSimpleErrorLog(message, clazz, file, cause);
        //		BufferedWriter writer = null;
        //        try {
        //			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
        //			String messageToLog = DateUtil.date2String(new Date(), TimeUtil.LONG_DATE_FORMAT_TIMEZONE)
        //			+ " " + clazz.getName() + " " + message;
        //			if(LOG_TO_LOG4J) {
        //				Logger.getLogger(clazz).debug(messageToLog);
        //			}
        //
        //			StringWriter sw = new StringWriter();
        //			cause.printStackTrace(new PrintWriter(sw));
        //			String stacktrace = sw.toString();
        //
        //			messageToLog = messageToLog + ". Cause: \n"+ stacktrace.toString() +"\n";
        //			writer.write(messageToLog);
        //		}
        //		catch (IOException e) {
        //			log.error("Error writting to REPOX log file: " + file.getAbsolutePath() + " message: " + message, e);
        //		}
        //        finally {
        //            if(writer != null){
        //                try {
        //                    writer.close();
        //                } catch (IOException e) {
        //                    log.error("Error closing REPOX log file: " + file.getAbsolutePath() + " message: " + message, e);
        //                }
        //            }
        //        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(StringUtil.sanitizeJavascript("f:\\lixo"));
        System.out.println(StringUtil.sanitizeJavascript("([\"\'])\\"));
        System.out.println(StringUtil.sanitizeJavascript("a("));
        System.out.println(StringUtil.sanitizeJavascript("a["));
        System.out.println(StringUtil.sanitizeJavascript("a\""));
        System.out.println(StringUtil.sanitizeJavascript("a\'"));
        System.out.println(StringUtil.sanitizeJavascript("a]"));
        System.out.println(StringUtil.sanitizeJavascript("a)"));
        System.out.println(StringUtil.sanitizeJavascript("a\\"));
    }
}
