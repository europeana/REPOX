/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.dataManagement;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.dataSet.SimpleDataSetInfo;

import java.util.List;

@RemoteServiceRelativePath("dataManagementService")
public interface DataManagementService extends RemoteService {

//    public DataContainer getMainData(PagingLoadConfig config) throws ServerSideException;
    public PagingLoadResult<DataContainer> getPagingData(PagingLoadConfig config) throws ServerSideException;
    public DataSourceUI getDataSetInfo(String dataSetId) throws ServerSideException;
    public List<ModelData> getAllDataProviders() throws ServerSideException;
    public List<SimpleDataSetInfo> getAllDataSets() throws ServerSideException;
    public List<ModelData> getAllAggregators() throws ServerSideException;
//    public List<ModelData> getSearchComboData() throws ServerSideException;
    public DataContainer getSearchResult(ModelData data) throws ServerSideException;

}
