/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.dataManagement;

import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.shared.dataTypes.AggregatorUI;
import harvesterUI.shared.dataTypes.SaveDataResponse;

import java.util.List;

//import harvesterUI.client.models.FilterAttributes;
//import harvesterUI.client.models.MailItem;

public interface AGGServiceAsync {

    public void saveAggregator(boolean update,AggregatorUI aggregatorUI, int pageSize,AsyncCallback<SaveDataResponse> callback);
    public void deleteAggregators(List<AggregatorUI> aggregatorUIs,AsyncCallback<String> callback);

}
