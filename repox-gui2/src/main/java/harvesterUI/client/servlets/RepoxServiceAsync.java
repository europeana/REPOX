/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets;

import harvesterUI.shared.dataTypes.admin.AdminInfo;
import harvesterUI.shared.dataTypes.admin.MainConfigurationInfo;
import harvesterUI.shared.externalServices.ExternalServiceResultUI;
import harvesterUI.shared.statistics.RepoxStatisticsUI;
import harvesterUI.shared.statistics.StatisticsType;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RepoxServiceAsync {

    public void loadAdminFormInfo(AsyncCallback<AdminInfo> callback);
    public void saveAdminFormInfo(AdminInfo results, AsyncCallback callback);

    public void getFullCountryList(AsyncCallback<Map<String,String>> callback);
    public void getFullCharacterEncodingList(AsyncCallback<List<String>> callback);

    public void getStatisticsInfo(StatisticsType statisticsType,String username, AsyncCallback<RepoxStatisticsUI> callback);

    public void getRepoxVersion(AsyncCallback<String> callback);
    public void getInitialConfigData(AsyncCallback<MainConfigurationInfo> callback);

    public void getValidationState(String dataSetID,AsyncCallback<ExternalServiceResultUI> callback);
    public void getTimezoneOffset(String clientDate,AsyncCallback<Double> callback);

    public void transformationResultFileExists(String dataSetId, String transformationId,AsyncCallback<Boolean> callback);

}
