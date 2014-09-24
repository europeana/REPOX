/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.dataManagement.search;

import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.mdr.SchemaTreeUI;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.search.BaseSearchResult;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface SearchServiceAsync {

    public void getPagedMainGridSearchResults(PagingLoadConfig config, List<FilterQuery> filterQueries, String username, AsyncCallback<PagingLoadResult<BaseSearchResult>> callback);
    public void getPagedTransformationsSearchResults(PagingLoadConfig config, AsyncCallback<PagingLoadResult<TransformationUI>> callback);
    public void getPagedSchemasSearchResults(PagingLoadConfig config, AsyncCallback<PagingLoadResult<SchemaTreeUI>> callback);
    public void getPagedScheduledTasksSearchResults(PagingLoadConfig config,List<FilterQuery> filterQueries, String username, AsyncCallback<PagingLoadResult<ScheduledTaskUI>> callback);
    public void getMappingsSearchResult(TransformationUI searchedMapping,AsyncCallback<TransformationUI> callback);
}
