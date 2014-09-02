package harvesterUI.client.servlets.harvest;

import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.List;

import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TaskManagementServiceAsync {

    public void getScheduledTasks(FilterPagingLoadConfig config, List<FilterQuery> filterQueries, String username, AsyncCallback<PagingLoadResult<ScheduledTaskUI>> callback);
    public void getCalendarTasks(List<FilterQuery> filterQueries, String username,AsyncCallback<ModelData> callback);

}
