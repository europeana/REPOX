/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.dataManagement;

import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.SaveDataResponse;
import harvesterUI.shared.dataTypes.dataSet.DataSetStatus;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.DatasetType;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dataSetOperationsService")
public interface DataSetOperationsService extends RemoteService {

    public SaveDataResponse saveDataSource(boolean update, DatasetType type, String originalDSset, DataSourceUI data, int pageSize) throws ServerSideException;
    public Map<String,List<String>> checkOAIURL(String url) throws ServerSideException;
    public String addAllOAIURL(String url,String dataSourceID,String dsSchema,String dsNamespace,
                               String dsMTDFormat, String name, String nameCode, String exportPath) throws ServerSideException;
    public String deleteDataSources(List<DataSourceUI> dataSourceUIs) throws ServerSideException;
    public SaveDataResponse moveDataSources(List<DataSourceUI> dataSourceUIs, ModelData dataProviderUI, int pageSize) throws ServerSideException;
    public String getExportPath(String dataSourceID) throws ServerSideException;
    public Map<String,DataSetStatus> getAllDataSourceStatus(List<DataContainer> dataContainers) throws ServerSideException;
    public String getLogFile(DataSourceUI dataSourceUI) throws ServerSideException;
    public String getLogFileFromFileName(DataSourceUI dataSourceUI,String fileName) throws ServerSideException;

    public ResponseState stopRunningDataSet(String dataSetId) throws ServerSideException;

    public ResponseState startSingleExternalService(String serviceId, String dataSetId) throws ServerSideException;
    public ResponseState forceDataSetRecordUpdate(List<DataSourceUI> dataSourceUIs);
    public ResponseState clearLogsAndOldTasks(List<DataSourceUI> dataSourceUIs);

}
