/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.dataManagement.search;

import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.mdr.SchemaTreeUI;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.search.BaseSearchResult;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("searchService")
public interface SearchService extends RemoteService {

    public PagingLoadResult<BaseSearchResult> getPagedMainGridSearchResults(PagingLoadConfig config, List<FilterQuery> filterQueries, String username) throws ServerSideException;
    public PagingLoadResult<TransformationUI> getPagedTransformationsSearchResults(PagingLoadConfig config) throws ServerSideException;
    public PagingLoadResult<SchemaTreeUI> getPagedSchemasSearchResults(PagingLoadConfig config) throws ServerSideException;
    public PagingLoadResult<ScheduledTaskUI> getPagedScheduledTasksSearchResults(PagingLoadConfig config,List<FilterQuery> filterQueries, String username) throws ServerSideException;
    public TransformationUI getMappingsSearchResult(TransformationUI searchedMapping) throws ServerSideException;

}
