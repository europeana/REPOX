package harvesterUI.server.util;

import harvesterUI.server.dataManagement.dataSets.DataSetOperationsServiceImpl;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import pt.utl.ist.repox.configuration.ConfigSingleton;
import pt.utl.ist.repox.task.DataSourceExportTask;
import pt.utl.ist.repox.task.DataSourceIngestTask;
import pt.utl.ist.repox.task.ScheduledTask;
import pt.utl.ist.repox.task.Task;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 04-10-2011
 * Time: 16:25
 */
@SuppressWarnings("deprecation")
public class Util {

    public static Date getDate(String dateString) {

        String delimDateTime = "[ ]+";
        String[] tokensDateTime = dateString.split(delimDateTime);

        String delimDate = "[-]+";
        String[] tokensDate = tokensDateTime[0].split(delimDate);
        Date date = new Date(Integer.parseInt(tokensDate[0])-1900,
                Integer.parseInt(tokensDate[1])-1,
                Integer.parseInt(tokensDate[2]));

        //Parse Time
        String delimTime = "[:]+";
        String[] tokensTime = tokensDateTime[1].split(delimTime);
        date.setHours(Integer.parseInt(tokensTime[0]));
        date.setMinutes(Integer.parseInt(tokensTime[1]));
        return date;
    }

    public static boolean hasRunningTask(String dataSourceID) {
        Iterator<Task> runningTasksIterator = getRunningTasks().iterator();
        while(runningTasksIterator.hasNext()) {
            Task task = runningTasksIterator.next();
            if(task instanceof DataSourceIngestTask) {
                DataSourceIngestTask dataSourceIngestTask = (DataSourceIngestTask) task;
                if(dataSourceIngestTask.getDataSourceId().equals(dataSourceID))
                    return true;

            } else if(task instanceof DataSourceExportTask) {
                DataSourceExportTask dataSourceExportTask = (DataSourceExportTask) task;
                if(dataSourceExportTask.getDataSourceId().equals(dataSourceID))
                    return true;

            } else if(task instanceof ScheduledTask) {
                ScheduledTask scheduledTask = (ScheduledTask) task;
                String[] params = scheduledTask.getParameters();
                String dsID = params[1];
                if(dsID.equals(dataSourceID))
                    return true;
            }
        }
        return false;
    }

    protected static List<Task> getRunningTasks() {
        List<Task> returnList = new ArrayList<Task>();

        List<Task> runningTasks = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getRunningTasks();
        if(runningTasks != null) {
            returnList.addAll(runningTasks);
        }

        List<Task> onetimeTasks = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getOnetimeTasks();
        if(onetimeTasks != null) {
            returnList.addAll(onetimeTasks);
        }

        return returnList;
    }

    public static ResponseState getUrlStatus(DataSourceUI dataSourceUI) throws ServerSideException {
        // Check http URLs
        String checkUrlResult = DataSetOperationsServiceImpl.checkURL(dataSourceUI.getOaiSource());
        if(checkUrlResult != null) {
            if(checkUrlResult.equals("URL_MALFORMED"))
                return ResponseState.URL_MALFORMED;
            else if(checkUrlResult.equals("URL_NOT_EXISTS"))
                return ResponseState.URL_NOT_EXISTS;
        }

        String checkHttpUrlResult = DataSetOperationsServiceImpl.checkURL(dataSourceUI.getHttpURL());
        if(checkHttpUrlResult !=null) {
            if(checkHttpUrlResult.equals("URL_MALFORMED"))
                return ResponseState.HTTP_URL_MALFORMED;
            else if(checkHttpUrlResult.equals("URL_NOT_EXISTS"))
                return ResponseState.HTTP_URL_NOT_EXISTS;
        }
        return null;
    }
    
    public static String stackTraceToString(Exception e){
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        return result.toString();
    }

    public static boolean compareStrings(String searchValue, String compareString){
        return compareString != null && compareString.toLowerCase().contains(searchValue.toLowerCase());
    }

    public static void addLogEntry(String entry, Logger log) {
        System.out.println(entry);
        log.warn(entry);
    }
}
