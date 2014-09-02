/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.dataManagement;

import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.SaveDataResponse;

import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dataProvidersService")
public interface DPService extends RemoteService {

    public SaveDataResponse saveDataProvider(boolean update, DataProviderUI dataProvider, int pageSize, String username) throws ServerSideException;
    public SaveDataResponse moveDataProvider(List<DataProviderUI> dataProviderUI, ModelData aggregatorUI,int pageSize) throws ServerSideException;
    public String deleteDataProviders(List<DataProviderUI> dataProviderUIs) throws ServerSideException;

}
