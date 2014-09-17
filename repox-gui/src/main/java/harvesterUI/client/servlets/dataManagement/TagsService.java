/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.dataManagement;

import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.dataSet.DataSetTagUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.util.List;

import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("tagsService")
public interface TagsService extends RemoteService {

    public PagingLoadResult<DataSetTagUI> getPagedTags(FilterPagingLoadConfig config) throws ServerSideException;
    public ResponseState removeTag(List<DataSetTagUI> dataSetTagUIs) throws ServerSideException;
    public ResponseState saveTag(boolean isUpdate, DataSetTagUI dataSetTagUI, String oldId) throws ServerSideException;
    public List<DataSetTagUI> getAllTags() throws ServerSideException;
}
