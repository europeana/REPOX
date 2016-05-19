package harvesterUI.server.harvest;

import harvesterUI.client.servlets.harvest.HarvestOperationsService;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.dataManagement.filters.FilterManagementUtil;
import harvesterUI.server.dataManagement.filters.FilteredDataResponse;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.SimpleDataSetInfo;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.servletResponseStates.ResponseState;
import harvesterUI.shared.tasks.RunningTask;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.task.DataSourceExportTask;
import pt.utl.ist.task.DataSourceIngestTask;
import pt.utl.ist.task.ScheduledTask;
import pt.utl.ist.task.Task;
import pt.utl.ist.task.TaskManager;
import pt.utl.ist.util.TimeUtil;
import pt.utl.ist.util.date.DateUtil;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import com.extjs.gxt.ui.client.util.DateWrapper;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class HarvestOperationsServiceImpl extends RemoteServiceServlet implements HarvestOperationsService {

    private static Logger logger = Logger.getLogger(HarvestOperationsServiceImpl.class);

    public HarvestOperationsServiceImpl() {}

    @Override
    public String dataSourceIngestNow(List<DataSourceUI> dataSourceUIList) throws ServerSideException{
        try {
            for (DataSourceUI dataSourceUI : dataSourceUIList) {
                DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager()
                        .getDataManager().getDataSourceContainer(dataSourceUI.getDataSourceSet()).getDataSource();

                if(dataSource == null) {
                    return "NO_DS_FOUND";
                }

//                int oldValue = dataSource.getMaxRecord4Sample();
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().startIngestDataSource(dataSourceUI.getDataSourceSet(), true, !dataSource.isSample());
            }
        }catch (ObjectNotFoundException e) {
            return "NO_DS_FOUND";
        } catch (AlreadyExistsException e) {
            return "TASK_EXECUTING";
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return "SUCCESS";
    }

    @Override
    public String dataSourceIngestSample(List<DataSourceUI> dataSourceUIList) throws ServerSideException{
        try {
            for (DataSourceUI dataSourceUI : dataSourceUIList) {
                DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().
                        getDataManager().getDataSourceContainer(dataSourceUI.getDataSourceSet()).getDataSource();

                if(dataSource == null) {
                    return "NO_DS_FOUND";
                } else {
                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().startIngestDataSource(dataSourceUI.getDataSourceSet(), false, true);
//                    dataSource.setMaxRecord4Sample(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getSampleRecords());
//
//                    Task harvestSubsetTask = new DataSourceIngestTask(String.valueOf(dataSource.getNewTaskId()),dataSource.getId(),"true");
//
//                    if(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().isTaskExecuting(harvestSubsetTask ))
//                        return "TASK_EXECUTING";
//
//                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().addOnetimeTask(harvestSubsetTask );
//                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().setDataSetSampleState(true,dataSource);
                }
            }
            return "SUCCESS";
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<RunningTask> getAllRunningTasks(List<FilterQuery> filterQueries, String username) throws ServerSideException{
        try{
            List<RunningTask> results = new ArrayList<RunningTask>();
            FilterManagementUtil.getInstance().createDataProviderUserFilter(filterQueries, username);
            Iterator<Task> runningTasksIterator = getFilteredRunningTasks(filterQueries).iterator();
            while(runningTasksIterator.hasNext()) {
                Task task = runningTasksIterator.next();
                if(task instanceof DataSourceIngestTask) {
                    DataSourceIngestTask dataSourceIngestTask = (DataSourceIngestTask) task;

                    if(dataSourceIngestTask.getStartTime() != null){
                        results.add(new RunningTask(dataSourceIngestTask.getDataSourceId(),"DATA_SOURCE_INGEST",
                                dataSourceIngestTask.getTaskClass().getName(), dataSourceIngestTask.getTaskId(),
                                dataSourceIngestTask.getFullIngest() + "", "OK",
                                dataSourceIngestTask.getRetries() + "", dataSourceIngestTask.getMaxRetries() + "",
                                dataSourceIngestTask.getRetryDelay() + "",
                                dataSourceIngestTask.getStartTime().toString()));
                    }
                } else if(task instanceof DataSourceExportTask) {
                    DataSourceExportTask dataSourceExportTask = (DataSourceExportTask) task;
                    String exportStartTime = dataSourceExportTask.getStartTime() == null ? Calendar.getInstance().getTime().toString() : dataSourceExportTask.getStartTime().toString();
                    RunningTask newExportTask = new RunningTask(dataSourceExportTask.getDataSourceId(),"DATA_SOURCE_EXPORT",
                            dataSourceExportTask.getTaskClass().getName(), dataSourceExportTask.getTaskId(),
                            "", "OK", dataSourceExportTask.getRetries()+"", dataSourceExportTask.getMaxRetries() + "",
                            dataSourceExportTask.getRetryDelay() + "",
                            exportStartTime);
                    newExportTask.setRecordsPerFile(dataSourceExportTask.getRecordsPerFile());
                    newExportTask.setExportDirectory(dataSourceExportTask.getExportDirectory());
                    results.add(newExportTask);
                } else if(task instanceof ScheduledTask) {
                    ScheduledTask scheduledTask = (ScheduledTask) task;
                    String[] params = scheduledTask.getParameters();
                    String dsID = params[1];
                    String fullIngestStr = params[2];
                    if(params.length <= 3) {
                        if(scheduledTask.getStartTime() != null){
                            results.add(new RunningTask(dsID,"DATA_SOURCE_INGEST",
                                    scheduledTask.getTaskClass().getName(), scheduledTask.getId(),
                                    fullIngestStr + "", "OK",
                                    scheduledTask.getRetries() + "", scheduledTask.getMaxRetries() + "",
                                    scheduledTask.getRetryDelay() + "",
                                    scheduledTask.getStartTime().toString()));
                        }
                    } else {
                        RunningTask newExportTask = new RunningTask(dsID,"DATA_SOURCE_EXPORT",
                                scheduledTask.getTaskClass().getName(), scheduledTask.getId(),
                                "", "OK", scheduledTask.getRetries()+"", scheduledTask.getMaxRetries() + "",
                                scheduledTask.getRetryDelay() + "",
                                scheduledTask.getStartTime().toString());
                        String recordPerFile = params[2];
                        String exportPath = params[3];
                        newExportTask.setRecordsPerFile(recordPerFile);
                        newExportTask.setExportDirectory(exportPath);
                        results.add(newExportTask);
                    }
                }
            }
            return results;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<SimpleDataSetInfo> getAllRunningDataSets(List<FilterQuery> filterQueries, String username) throws ServerSideException {
        List<SimpleDataSetInfo> runningDataSets = new ArrayList<SimpleDataSetInfo>();
        FilterManagementUtil.getInstance().createDataProviderUserFilter(filterQueries, username);
        List<Object> allDataList = FilterManagementUtil.getInstance().getRawFilteredData(filterQueries).getFilteredData();
        for(Object model: allDataList){
            if(model instanceof DataSourceContainer){
                DataSource dataSource = ((DataSourceContainer)model).getDataSource();
                if(dataSource.getStatus() == DataSource.StatusDS.RUNNING)
                    runningDataSets.add(new SimpleDataSetInfo(dataSource.getId(),dataSource.getId()));
            }
        }
        return runningDataSets;
    }

    public Boolean dataSourceEmpty(List<DataSourceUI> dataSourceUIList) throws ServerSideException{
        boolean result = false;
        try {
            for (DataSourceUI dataSourceUI : dataSourceUIList) {
                DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
                        getDataSourceContainer(dataSourceUI.getDataSourceSet()).getDataSource();

                if (dataSource == null) {
                    result = false;
                } else {
                    dataSource.cleanUp();
                    result = true;
                }
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public Boolean deleteRunningTask(RunningTask runningTask) throws ServerSideException{
        try {
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
                    stopIngestDataSource(runningTask.getDataSet(), Task.Status.CANCELED);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    protected List<Task> getRunningTasks() throws ServerSideException{
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

    private List<Task> getFilteredRunningTasks(List<FilterQuery> filterQueries) throws ServerSideException {
        List<Task> scheduledTasks = new ArrayList<Task>(getRunningTasks());
        Iterator<Task> iterator = scheduledTasks.iterator();
        while (iterator.hasNext()){
            String dataSetId = iterator.next().getParameters()[1];
            String dataSetsDataProviderId = getDataProviderId(dataSetId,filterQueries);
            if(dataSetsDataProviderId == null)
                iterator.remove();
        }
        return scheduledTasks;
    }

    public static String getDataProviderId(String dataSetID,List<FilterQuery> filterQueries) throws ServerSideException {
        FilteredDataResponse filteredDataResponse =  FilterManagementUtil.getInstance().getRawFilteredData(filterQueries);
        for(Object model : filteredDataResponse.getFilteredData()){
            if(model instanceof DataProvider){
                if(((DataProvider) model).getDataSource(dataSetID) != null)
                    return ((DataProvider) model).getId();
            }else
                return "OK";
        }
        return null;
    }

    public RunningTask getRunningTask(String dataSourceId,List<FilterQuery> filterQueries, String username) throws ServerSideException{
        try{
            FilterManagementUtil.getInstance().createDataProviderUserFilter(filterQueries, username);
            Iterator<Task> runningTasksIterator = getFilteredRunningTasks(filterQueries).iterator();
            while(runningTasksIterator.hasNext()) {
                Task task = runningTasksIterator.next();
                if(task instanceof DataSourceIngestTask) {
                    DataSourceIngestTask dataSourceIngestTask = (DataSourceIngestTask) task;
                    if(dataSourceIngestTask.getStartTime() != null && dataSourceIngestTask.getDataSourceId().equals(dataSourceId)){
                        return new RunningTask(dataSourceIngestTask.getDataSourceId(),"DATA_SOURCE_INGEST",
                                dataSourceIngestTask.getTaskClass().getName(), dataSourceIngestTask.getTaskId(),
                                dataSourceIngestTask.getFullIngest() + "", "OK",
                                dataSourceIngestTask.getRetries() + "", dataSourceIngestTask.getMaxRetries() + "",
                                dataSourceIngestTask.getRetryDelay() + "",
                                dataSourceIngestTask.getStartTime().toString());
                    }
                } else if(task instanceof DataSourceExportTask) {
                    DataSourceExportTask dataSourceExportTask = (DataSourceExportTask) task;
                    if(dataSourceExportTask.getDataSourceId().equals(dataSourceId)){
                        RunningTask newExportTask = new RunningTask(dataSourceExportTask.getDataSourceId(),"DATA_SOURCE_EXPORT",
                                dataSourceExportTask.getTaskClass().getName(), dataSourceExportTask.getTaskId(),
                                "", "OK", dataSourceExportTask.getRetries()+"", dataSourceExportTask.getMaxRetries() + "",
                                dataSourceExportTask.getRetryDelay() + "",
                                dataSourceExportTask.getStartTime().toString());
                        newExportTask.setRecordsPerFile(dataSourceExportTask.getRecordsPerFile());
                        newExportTask.setExportDirectory(dataSourceExportTask.getExportDirectory());
                        return newExportTask;
                    }
                } else if(task instanceof ScheduledTask) {
                    ScheduledTask scheduledTask = (ScheduledTask) task;
                    String[] params = scheduledTask.getParameters();
                    String dsID = params[1];
                    String fullIngestStr = params[2];
                    if(dsID.equals(dataSourceId)){
                        if(params.length <= 3) {
                            if(scheduledTask.getStartTime() != null){
                                return new RunningTask(dsID,"DATA_SOURCE_INGEST",
                                        scheduledTask.getTaskClass().getName(), scheduledTask.getId(),
                                        fullIngestStr + "", "OK",
                                        scheduledTask.getRetries() + "", scheduledTask.getMaxRetries() + "",
                                        scheduledTask.getRetryDelay() + "",
                                        scheduledTask.getStartTime().toString());
                            }
                        } else {
                            RunningTask newExportTask = new RunningTask(dsID,"DATA_SOURCE_EXPORT",
                                    scheduledTask.getTaskClass().getName(), scheduledTask.getId(),
                                    "", "OK", scheduledTask.getRetries()+"", scheduledTask.getMaxRetries() + "",
                                    scheduledTask.getRetryDelay() + "",
                                    scheduledTask.getStartTime().toString());
                            String recordPerFile = params[2];
                            String exportPath = params[3];
                            newExportTask.setRecordsPerFile(recordPerFile);
                            newExportTask.setExportDirectory(exportPath);
                            return newExportTask;
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return null;
    }

    public String addScheduledTask(ScheduledTaskUI taskUI) throws ServerSideException {
        String taskId = "";
        try {
            // Check if scheduled task already exists
            if(RepoxServiceImpl.getRepoxManager().getTaskManager().taskAlreadyExists(taskUI.getDataSetId(),
                    DateUtil.date2String(taskUI.getDate(), TimeUtil.LONG_DATE_FORMAT_NO_SECS),
                    ScheduledTask.Frequency.valueOf(taskUI.getType())))
                return "alreadyExists";

            DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
                    getDataSourceContainer(taskUI.getDataSetId()).getDataSource();

            if(dataSource == null) {
                return "notFound";
            }
            ScheduledTask scheduledTask = null;
            if(taskUI.getScheduleType() == 0) {
                Calendar cal=Calendar.getInstance();
                cal.setTime(getServerTimeScheduledTaskDate(taskUI.getDate()));
                cal.add(Calendar.HOUR_OF_DAY,taskUI.getHourDiff());
                cal.add(Calendar.MINUTE,taskUI.getMinuteDiff());
                String newTaskId = dataSource.getNewTaskId();
                taskId = newTaskId;
                scheduledTask = new ScheduledTask(newTaskId, cal, ScheduledTask.Frequency.valueOf(taskUI.getType()), taskUI.getMonthPeriod(),
                        new DataSourceIngestTask(newTaskId, taskUI.getDataSetId(), taskUI.getFullIngest()));
            }else{
                Calendar cal=Calendar.getInstance();
                cal.setTime(getServerTimeScheduledTaskDate(taskUI.getDate()));
                cal.add(Calendar.HOUR_OF_DAY,taskUI.getHourDiff());
                cal.add(Calendar.MINUTE,taskUI.getMinuteDiff());
                String newTaskId = dataSource.getNewTaskId();
                taskId = newTaskId;

                String recordsPerFile;
                if(taskUI.getRecordsPerFile().equals("All"))
                    recordsPerFile = "-1";
                else
                    recordsPerFile = taskUI.getRecordsPerFile();

                scheduledTask = new ScheduledTask(newTaskId, cal, ScheduledTask.Frequency.valueOf(taskUI.getType()), taskUI.getMonthPeriod(),
                        new DataSourceExportTask(newTaskId, taskUI.getDataSetId(),
                                taskUI.getExportDirectory(),
                                recordsPerFile,
                                taskUI.getExportFormat() != null ? taskUI.getExportFormat() : ""));
                dataSource.setExportDir(taskUI.getExportDirectory());
                RepoxServiceImpl.getRepoxManager().getDataManager().saveData();
            }
            RepoxServiceImpl.getRepoxManager().getTaskManager().saveTask(scheduledTask);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return taskId;
    }

    @SuppressWarnings("deprecation")
    private Date getServerTimeScheduledTaskDate(Date scheduledTaskDate){
        Date date = new Date();
        DateWrapper dw = new DateWrapper(scheduledTaskDate);
        date.setYear(scheduledTaskDate.getYear());
        date.setMonth(dw.getMonth());
        date.setDate(dw.getDate());
        date.setHours(dw.getHours());
        date.setMinutes(dw.getMinutes());
        return date;
    }

    public Boolean deleteScheduledTask(String scheduledTaskID) throws ServerSideException{
        TaskManager manager = RepoxServiceImpl.getRepoxManager().getTaskManager();
        try {
            return manager.deleteTask(scheduledTaskID);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public Boolean updateScheduledTask(ScheduledTaskUI scheduledTaskUI) throws ServerSideException{
        ScheduledTask scheduledTask = RepoxServiceImpl.getRepoxManager().getTaskManager().getTask(scheduledTaskUI.getId());
        Calendar testDateChange=Calendar.getInstance();
        testDateChange.setTime(scheduledTaskUI.getDate());
        if(!testDateChange.equals(scheduledTask.getFirstRun())) {
            try {
                String date = DateUtil.date2String(scheduledTaskUI.getDate(), TimeUtil.LONG_DATE_FORMAT_NO_SECS);
                if(RepoxServiceImpl.getRepoxManager().getTaskManager().taskAlreadyExists(scheduledTaskUI.getDataSetId(),
                        date, ScheduledTask.Frequency.valueOf(scheduledTaskUI.getType()))){
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new ServerSideException(Util.stackTraceToString(e));
            }
        }

        try {
            if(scheduledTask.getTaskClass().getSimpleName().equals("IngestDataSource")){
                Calendar cal=Calendar.getInstance();
                cal.setTime(scheduledTaskUI.getDate());
                scheduledTask.setFirstRun(cal);
                scheduledTask.setXmonths(scheduledTaskUI.getMonthPeriod());
                scheduledTask.getParameters()[2] = scheduledTaskUI.getFullIngest();
                scheduledTask.setFrequency(ScheduledTask.Frequency.valueOf(scheduledTaskUI.getType()));
            } else if(scheduledTask.getTaskClass().getSimpleName().equals("ExportToFilesystem")) {
                Calendar cal=Calendar.getInstance();
                cal.setTime(scheduledTaskUI.getDate());
                scheduledTask.setFirstRun(cal);

                String recordsPerFile;
                if(scheduledTaskUI.getRecordsPerFile().equals("All"))
                    recordsPerFile = "-1";
                else
                    recordsPerFile = scheduledTaskUI.getRecordsPerFile();

                scheduledTask.setXmonths(scheduledTaskUI.getMonthPeriod());
                scheduledTask.getParameters()[3] = recordsPerFile;
                scheduledTask.getParameters()[2] = scheduledTaskUI.getExportDirectory();
                scheduledTask.setFrequency(ScheduledTask.Frequency.valueOf(scheduledTaskUI.getType()));

                DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
                        getDataSourceContainer(scheduledTaskUI.getDataSetId()).getDataSource();
                dataSource.setExportDir(scheduledTaskUI.getExportDirectory());
                RepoxServiceImpl.getRepoxManager().getDataManager().saveData();
            }
            RepoxServiceImpl.getRepoxManager().getTaskManager().deleteTask(scheduledTask.getId());
            RepoxServiceImpl.getRepoxManager().getTaskManager().saveTask(scheduledTask);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public Boolean dataSourceExport(DataSourceUI dataSourceUI) throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().dataSourceExport(dataSourceUI);
    }

    public ResponseState changeLastIngestDate(String lastIngestDate, String lastIngestHour, String dataSetId) throws ServerSideException{
        try {
            DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSetId).getDataSource();
            dataSource.setLastUpdate(convertStringToDate(lastIngestDate + " " + lastIngestHour));
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
            return ResponseState.SUCCESS;
        } catch (DocumentException e) {
            return ResponseState.ERROR;
        } catch (IOException e) {
            return ResponseState.ERROR;
        }
    }

    private Date convertStringToDate(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        try {
            return format.parse(date);
        }
        catch(ParseException pe) {
            Util.addLogEntry("ERROR: Cannot parse " + date,logger);
            return null;
        }
    }
}
