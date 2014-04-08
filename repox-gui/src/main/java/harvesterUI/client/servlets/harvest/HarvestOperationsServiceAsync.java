/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.harvest;

import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.SimpleDataSetInfo;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.servletResponseStates.ResponseState;
import harvesterUI.shared.tasks.RunningTask;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.List;

public interface HarvestOperationsServiceAsync {

    public void dataSourceIngestNow(List<DataSourceUI> dataSourceUI,AsyncCallback<String> callback);
    public void dataSourceIngestSample(List<DataSourceUI> dataSourceUI,AsyncCallback<String> callback);

    public void getAllRunningTasks(List<FilterQuery> filterQueries, String username,AsyncCallback<List<RunningTask>> callback);
    public void dataSourceEmpty(List<DataSourceUI> dataSourceUI,AsyncCallback<Boolean> callback);
    public void dataSourceExport(DataSourceUI dataSourceUI,AsyncCallback<Boolean> callback);
    public void getRunningTask(String dataSourceId, List<FilterQuery> filterQueries, String username,AsyncCallback<RunningTask> callback);

    public void deleteRunningTask(RunningTask runningTask,AsyncCallback<Boolean> callback);

    public void addScheduledTask(ScheduledTaskUI taskUI, AsyncCallback<String> callback);
    public void deleteScheduledTask(String scheduledTaskID,AsyncCallback<Boolean> callback);
    public void updateScheduledTask(ScheduledTaskUI scheduledTaskUI,AsyncCallback<Boolean> callback);

    public void getAllRunningDataSets(List<FilterQuery> filterQueries, String username,AsyncCallback<List<SimpleDataSetInfo>> callback);

    public void changeLastIngestDate(String lastIngestDate, String lastIngestHour, String dataSetId,AsyncCallback<ResponseState> callback);
}
