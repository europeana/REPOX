/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.dataManagement;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.SaveDataResponse;

import java.util.List;

//import harvesterUI.client.models.FilterAttributes;
//import harvesterUI.client.models.MailItem;

public interface DPServiceAsync {

    public void saveDataProvider(boolean update, DataProviderUI dataProvider, int pageSize, String username, AsyncCallback<SaveDataResponse> callback);
    public void moveDataProvider(List<DataProviderUI> dataProviderUI, ModelData aggregatorUI, int pageSize, AsyncCallback<SaveDataResponse> callback);
    public void deleteDataProviders(List<DataProviderUI> dataProviderUIs, AsyncCallback<String> callback);

}
