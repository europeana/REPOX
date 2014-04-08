package harvesterUI.client.servlets.dataManagement;

import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.shared.dataTypes.dataSet.DataSetTagUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.util.List;

public interface TagsServiceAsync {

    public void saveTag(boolean isUpdate, DataSetTagUI dataSetTagUI, String oldId, AsyncCallback<ResponseState> callback);
    public void removeTag(List<DataSetTagUI> dataSetTagUIs, AsyncCallback<ResponseState> callback);
    public void getPagedTags(FilterPagingLoadConfig config, AsyncCallback<PagingLoadResult<DataSetTagUI>> callback);
    public void getAllTags(AsyncCallback<List<DataSetTagUI>> callback);

}
