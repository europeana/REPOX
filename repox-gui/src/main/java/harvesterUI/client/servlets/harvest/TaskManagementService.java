/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.harvest;

import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.List;

import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("taskManagementService")
public interface TaskManagementService extends RemoteService {

    public PagingLoadResult<ScheduledTaskUI> getScheduledTasks(FilterPagingLoadConfig config, List<FilterQuery> filterQueries, String username) throws ServerSideException;
    public ModelData getCalendarTasks(List<FilterQuery> filterQueries, String username) throws ServerSideException;

}
