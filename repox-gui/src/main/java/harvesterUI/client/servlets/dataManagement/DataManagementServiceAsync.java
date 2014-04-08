package harvesterUI.client.servlets.dataManagement;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.SimpleDataSetInfo;

import java.util.List;

public interface DataManagementServiceAsync {

//    public void getMainData(PagingLoadConfig config,AsyncCallback<DataContainer> callback);
    public void getPagingData(PagingLoadConfig config, AsyncCallback<PagingLoadResult<DataContainer>> callback);
    public void getDataSetInfo(String dataSetId,AsyncCallback<DataSourceUI> callback);
    public void getAllDataProviders(AsyncCallback<List<ModelData>> callback);
    public void getAllDataSets(AsyncCallback<List<SimpleDataSetInfo>> callback);
    public void getAllAggregators(AsyncCallback<List<ModelData>> callback);
//    public void getSearchComboData(AsyncCallback<List<ModelData>> callback);
    public void getSearchResult(ModelData data,AsyncCallback<DataContainer> callback);

}
