package harvesterUI.server.harvest;

import harvesterUI.client.servlets.harvest.TaskManagementService;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.dataManagement.filters.FilterManagementUtil;
import harvesterUI.server.dataManagement.filters.FilteredDataResponse;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.tasks.OldTaskUI;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.task.ScheduledTask;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TaskManagementServiceImpl extends RemoteServiceServlet implements TaskManagementService {

    public TaskManagementServiceImpl() {
    }

    public PagingLoadResult<ScheduledTaskUI> getScheduledTasks(FilterPagingLoadConfig config, List<FilterQuery> filterQueries, String username) throws ServerSideException{
        FilterManagementUtil.getInstance().createDataProviderUserFilter(filterQueries, username);
        List<ScheduledTaskUI> scheduledTasks = getScheduledTasksUI(filterQueries);

        if (config.getSortInfo().getSortField() != null) {
            final String sortField = config.getSortInfo().getSortField();
            if (sortField != null) {
                Collections.sort(scheduledTasks, config.getSortInfo().getSortDir().comparator(new Comparator<ScheduledTaskUI>() {
                    public int compare(ScheduledTaskUI p1, ScheduledTaskUI p2) {
                        if (sortField.equals("id")) {
                            return p1.getId().compareTo(p2.getId());
                        } else if (sortField.equals("listType")) {
                            return p1.getType().compareTo(p2.getType());
                        } else if (sortField.equals("dateString")) {
                            return p1.getDateString().compareTo(p2.getDateString());
                        } else if (sortField.equals("parameters")) {
                            return p1.getParameters().compareTo(p2.getParameters());
                        }
                        return 0;
                    }
                }));
            }
        }

        ArrayList<ScheduledTaskUI> temp = new ArrayList<ScheduledTaskUI>();
        ArrayList<ScheduledTaskUI> remove = new ArrayList<ScheduledTaskUI>();
        for (ScheduledTaskUI s : scheduledTasks) {
            temp.add(s);
        }

        List<FilterConfig> filters = config.getFilterConfigs();
        for (FilterConfig f : filters) {
            String type = f.getType();
            String test = (String)f.getValue();
            String path = f.getField();
            String comparison = f.getComparison();

            String safeTest = test == null ? "" : test.toString();

            for (ScheduledTaskUI s : scheduledTasks) {
                String value = getScheduledTaskValue(s, path);
                String safeValue = value == null ? null : value.toString();

                if (safeTest.length() == 0 && (safeValue == null || safeValue.length() == 0)) {
                    continue;
                } else if (safeValue == null) {
                    remove.add(s);
                    continue;
                }

                if ("string".equals(type)) {
                    if (safeValue.toLowerCase().indexOf(safeTest.toLowerCase()) == -1) {
                        remove.add(s);
                    }
                }
            }
        }

        for (ScheduledTaskUI s : remove) {
            temp.remove(s);
        }

        ArrayList<ScheduledTaskUI> sublist = new ArrayList<ScheduledTaskUI>();
        int start = config.getOffset();
        int limit = temp.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }
        for (int i = config.getOffset(); i < limit; i++) {
            sublist.add(temp.get(i));
        }

        int totalSize = temp.size();

        return new BasePagingLoadResult<ScheduledTaskUI>(sublist, config.getOffset(), totalSize);
    }

    private String getScheduledTaskValue(ScheduledTaskUI scheduledTaskUI, String property) {
        if (property.equals("id")) {
            return scheduledTaskUI.getId();
        } else if (property.equals("listType")) {
            return scheduledTaskUI.getType();
        } else if (property.equals("dateString")) {
            return scheduledTaskUI.getDateString();
        } else if (property.equals("parameters")) {
            return scheduledTaskUI.getParameters();
        }
        return "";
    }

    public static List<ScheduledTaskUI> getScheduledTasksUI(List<FilterQuery> filterQueries) throws ServerSideException{
        List<ScheduledTaskUI> result = new ArrayList<ScheduledTaskUI>();
        List<ScheduledTask> scheduledTasks = getFilteredScheduledTasks(filterQueries);
        for(ScheduledTask scheduledTask : scheduledTasks){
            ScheduledTaskUI scheduledTaskUI = parseScheduledTask(scheduledTask);
            if(scheduledTaskUI != null)
                result.add(scheduledTaskUI);
        }
        return result;
    }

    private List<ScheduledTaskUI> getParsedScheduledTasks(int offSet, int limit,List<FilterQuery> filterQueries) throws ServerSideException{
        List<ScheduledTaskUI> scheduledTaskUIs = new ArrayList<ScheduledTaskUI>();
        List<ScheduledTask> scheduledTasks = getFilteredScheduledTasks(filterQueries);
        for (int i = offSet; i < limit && i<scheduledTasks.size(); i++) {
            ScheduledTaskUI scheduledTaskUI = parseScheduledTask(scheduledTasks.get(i));
            if(scheduledTaskUI != null)
                scheduledTaskUIs.add(scheduledTaskUI);
        }
        return scheduledTaskUIs;
    }

    private static ScheduledTaskUI parseScheduledTask(ScheduledTask scheduledTask) throws ServerSideException{
        try{
            ScheduledTaskUI scheduledTaskUI = null;
            String[] setTokens = scheduledTask.getId().split("_");
            String dataSetId = setTokens[0];
            // Load only tasks after today that aren't frequency ONCE
            if(scheduledTask.getFirstRun().getTime().after(Calendar.getInstance().getTime()) || !scheduledTask.getFrequency().name().equals("ONCE")) {
                if(scheduledTask.getTaskClass().getSimpleName().equals("IngestDataSource")) {
                    String scheduledTaskId = scheduledTask.getId();
                    String firstRunStr = scheduledTask.getFirstRunString();
                    String freq = scheduledTask.getFrequency().name();
                    Integer xmonths = scheduledTask.getXmonths();
                    String fullIngest = scheduledTask.getParameters()[2];
                    scheduledTaskUI = new ScheduledTaskUI(dataSetId,scheduledTaskId,firstRunStr,freq,xmonths,fullIngest);
                    scheduledTaskUI.setScheduleType(0);
                } else if(scheduledTask.getTaskClass().getSimpleName().equals("ExportToFilesystem")) {
                    String scheduledTaskId = scheduledTask.getId();
                    String firstRunStr = scheduledTask.getFirstRunString();
                    String freq = scheduledTask.getFrequency().name();
                    Integer xmonths = scheduledTask.getXmonths();
                    String recordsPerFile = scheduledTask.getParameters()[3];
                    String exportDirectory = scheduledTask.getParameters()[2];
                    scheduledTaskUI = new ScheduledTaskUI(dataSetId,scheduledTaskId,firstRunStr,freq,xmonths,"");
                    scheduledTaskUI.setScheduleType(1);
                    scheduledTaskUI.createDateString(1);
                    scheduledTaskUI.setScheduleType("Data Set Export");
                    DataSourceContainer dataSourceContainer = RepoxServiceImpl.getRepoxManager().getDataManager().getDataSourceContainer(dataSetId);
                    String exportDir = dataSourceContainer.getDataSource().getExportDir().getAbsolutePath();
                    scheduledTaskUI.setParameters("Data Set: " + dataSetId + " -- Folder: " + exportDir);
                    scheduledTaskUI.setRecordsPerFile(recordsPerFile);
                    scheduledTaskUI.setExportDirectory(exportDirectory);
                }
            }
            return scheduledTaskUI;
        } catch (NullPointerException e){
            // do nothing
            return null;
        } catch (Exception e){
            throw  new ServerSideException(Util.stackTraceToString(e));
        }
    }

    private List<OldTaskUI> getParsedOldTasks(List<FilterQuery> filterQueries) throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().getParsedOldTasks(filterQueries);
    }

    public static List<ScheduledTask> getFilteredScheduledTasks(List<FilterQuery> filterQueries) throws ServerSideException {
        List<ScheduledTask> scheduledTasks = new ArrayList<ScheduledTask>(RepoxServiceImpl.getRepoxManager().getTaskManager().getScheduledTasks());
        Iterator<ScheduledTask> iterator = scheduledTasks.iterator();
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
            }
        }
        return null;
    }

    public ModelData getCalendarTasks(List<FilterQuery> filterQueries, String username) throws ServerSideException{
        try{
            FilterManagementUtil.getInstance().createDataProviderUserFilter(filterQueries, username);
            ModelData data = new BaseModelData();
            List<ScheduledTask> scheduledTasks = getFilteredScheduledTasks(filterQueries);
            List<ScheduledTaskUI> scheduledTaskUIs = getParsedScheduledTasks(0,scheduledTasks.size(),filterQueries);
            data.set("schedules",scheduledTaskUIs);
            data.set("oldTasks",getParsedOldTasks(filterQueries));
            return data;
        } catch (Exception e){
            throw  new ServerSideException(Util.stackTraceToString(e));
        }
    }

}
