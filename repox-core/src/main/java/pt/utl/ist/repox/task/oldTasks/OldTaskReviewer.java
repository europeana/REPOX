package pt.utl.ist.repox.task.oldTasks;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.task.OldTask;
import pt.utl.ist.repox.util.ConfigSingleton;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 08-10-2012
 * Time: 17:56
 */
public class OldTaskReviewer {
    private static final Logger log = Logger.getLogger(OldTaskReviewer.class);
    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    public void addNotListedOldTasks(String dataSetId){
        try {
            DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
                    getDataSourceContainer(dataSetId).getDataSource();

            for(String logFileName : dataSource.getLogFilenames()){
                boolean matchFound = false;
                for(OldTask oldTask : dataSource.getOldTasksList()){
                    if(oldTask.getLogName().equals(logFileName)){
                        matchFound = true;
                        break;
                    }
                }

                if(!matchFound && dataSource.getStatus() != DataSource.StatusDS.RUNNING){
                    String oldTaskId = dataSetId + "_" + UUID.randomUUID().toString();
                    StatusAndRecords statusAndRecords = getStatusAndRecordsFromLogFile(dataSetId,logFileName);
                    OldTask oldTask = new OldTask(dataSource,oldTaskId,logFileName,"incrementalIngest",
                            statusAndRecords.getStatus(),"0","3","300",
                            convertDateToString(getLogFileDate(dataSetId,logFileName))
                            ,statusAndRecords.getRecords());
                    dataSource.getOldTasksList().add(oldTask);
                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveOldTask(oldTask);
                    log.warn("New Old Tasks added from Log Files for the data set: " + dataSetId);
                }
            }

        } catch (DocumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (StringIndexOutOfBoundsException e){
            // Do nothing
        } catch (ArrayIndexOutOfBoundsException e){
            // Do nothing
        } catch (Exception e){
            // Do nothing
        }
    }

    private Date getLogFileDate(String dataSetId,String logFileName) throws StringIndexOutOfBoundsException, ArrayIndexOutOfBoundsException {
        logFileName = logFileName.replace(dataSetId,"");
        String[] fileNameTokens = logFileName.split("_");
        String dateAttached = fileNameTokens[2];
        String timeAttached = fileNameTokens[3];

        // Parse Date
        String finalDate = dateAttached.substring(0,4) + "-" +
                dateAttached.substring(4,6) + "-" + dateAttached.substring(6,8);

        // Parse Time
        String finalTime = timeAttached.substring(0, 2) + ":" +
                timeAttached.substring(2, 4);

        return convertStringToDate(finalDate + " " + finalTime);
    }

    private StatusAndRecords getStatusAndRecordsFromLogFile(String dataSetId, String logFileName){
        File logFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getRepositoryPath() +
                File.separator + dataSetId + File.separator + "logs" + File.separator + logFileName);

        try {
            SAXReader reader = new SAXReader();
            Document logDocument = reader.read(logFile);

            String status = logDocument.valueOf("//report/status");
            String records = logDocument.valueOf("//report/records");
            return new StatusAndRecords(status,records);
        } catch (DocumentException e) {
            return new StatusAndRecords("OK","0");
        }
    }

    class StatusAndRecords {
        private String status;
        private String records;

        StatusAndRecords(String status, String records) {
            this.status = status;
            this.records = records;
        }

        public String getStatus() {
            return status;
        }

        public String getRecords() {
            return records;
        }
    }

    private String convertDateToString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    private Date convertStringToDate(String date){
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        String dateString = date.toString();
        try {
            Date parsed = format.parse(dateString);
            return parsed;
        }
        catch(ParseException pe) {
            log.error("ERROR: Cannot parse " + dateString);
            return null;
        }
    }
}
