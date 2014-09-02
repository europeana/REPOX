/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.dataManagement;

import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.SaveDataResponse;
import harvesterUI.shared.dataTypes.dataSet.DataSetStatus;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.DatasetType;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.client.rpc.AsyncCallback;

//import harvesterUI.client.models.FilterAttributes;
//import harvesterUI.client.models.MailItem;

public interface DataSetOperationsServiceAsync {

    public void saveDataSource(boolean update,DatasetType type, String originalDSset, DataSourceUI data, int pageSize, AsyncCallback<SaveDataResponse> callback);
    public void checkOAIURL(String url,AsyncCallback<Map<String,List<String>>> callback);
    public void addAllOAIURL(String url,String dataSourceID,String dsSchema,String dsNamespace,
                             String dsMTDFormat, String name, String nameCode, String exportPath, AsyncCallback<String> callback);
    public void moveDataSources(List<DataSourceUI> dataSourceUIs, ModelData dataProviderUI, int pageSize,AsyncCallback<SaveDataResponse> callback);
    public void deleteDataSources(List<DataSourceUI> dataSourceUIs,AsyncCallback<String> callback);
    public void getExportPath(String dataSourceID,AsyncCallback<String> callback);

    public void getAllDataSourceStatus(List<DataContainer> dataContainers,AsyncCallback<Map<String,DataSetStatus>> callback);
    public void getLogFile(DataSourceUI dataSourceUI,AsyncCallback<String> callback);
    public void getLogFileFromFileName(DataSourceUI dataSourceUI,String fileName,AsyncCallback<String> callback);

    public void stopRunningDataSet(String dataSetId,AsyncCallback<ResponseState> callback);
    public void startSingleExternalService(String serviceId, String dataSetId,AsyncCallback<ResponseState> callback);
    public void forceDataSetRecordUpdate(List<DataSourceUI> dataSourceUIs,AsyncCallback<ResponseState> callback);
    public void clearLogsAndOldTasks(List<DataSourceUI> dataSourceUIs,AsyncCallback<ResponseState> callback);
}
