package pt.utl.ist.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.XmlUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created to project REPOX.
 * User: Edmundo
 * Date: 02/01/13
 * Time: 17:59
 */
public class TransformationResultLogger {

    private String        DATE_FORMAT                = "yyyy-MM-dd HH:mm:ss";
    private static String TRANSFORMATION_FILE_PREFIX = "trans_results___";

    private Document      document;
    private File          logFile;

    /**
     * Creates a new instance of this class.
     */
    public TransformationResultLogger() {
    }

    /**
     * Creates a new instance of this class.
     * @param dataSetId
     * @param transformationId
     */
    public TransformationResultLogger(String dataSetId, String transformationId) {
        logFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getRepositoryPath() + File.separator + dataSetId + File.separator + "logs" + File.separator + TRANSFORMATION_FILE_PREFIX + encodeTransformationResultFileId(transformationId) + ".xml");

        if (logFile.exists()) logFile.delete();

        document = DocumentHelper.createDocument();
        Element root = document.addElement("resultLog");
        root.addElement("date").addText(convertDateToString(new Date()));
        root.addElement("transformationID").addText(transformationId);
        root.addElement("errors");
    }

    /**
     * @param errorCause
     */
    public void addOverallErrorEntry(String errorCause) {
        addRecordErrorEntry("JAVA_ERROR", errorCause);
    }

    /**
     * @param recordId
     * @param errorCause
     */
    public void addRecordErrorEntry(String recordId, String errorCause) {
        if (logFile != null && document != null) {
            Element errors = (Element)document.getRootElement().selectSingleNode("errors");
            Element error = errors.addElement("error");
            error.addAttribute("recordID", recordId);
            error.setText(errorCause);
        }
    }

    /**
     * 
     */
    public void persistData() {
        if (logFile != null && document != null) {
            try {
                XmlUtil.writePrettyPrint(logFile, document);
            } catch (IOException e) {
                e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    /**
     * @param date
     * @return String of the Date
     */
    public String convertDateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * @param transformationId
     * @return encoded String
     */
    public static String encodeTransformationResultFileId(String transformationId) {
        return transformationId.replace(" ", "_");
    }

    /**
     * @param dataSetId
     * @param transformationId
     * @return File
     */
    public static File getTransformationResultFile(String dataSetId, String transformationId) {
        return new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getRepositoryPath() + File.separator + dataSetId + File.separator + "logs" + File.separator + TRANSFORMATION_FILE_PREFIX + encodeTransformationResultFileId(transformationId) + ".xml");
    }

    /**
     * @param dataSetId
     * @param transformationId
     * @return boolean
     */
    public static boolean hasTransformationResultFile(String dataSetId, String transformationId) {
        return getTransformationResultFile(dataSetId, transformationId).exists();
    }
}
