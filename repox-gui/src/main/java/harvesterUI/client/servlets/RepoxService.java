/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import harvesterUI.shared.statistics.RepoxStatisticsUI;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.admin.AdminInfo;
import harvesterUI.shared.dataTypes.admin.MainConfigurationInfo;
import harvesterUI.shared.externalServices.ExternalServiceResultUI;
import harvesterUI.shared.statistics.StatisticsType;

import java.util.List;
import java.util.Map;

@RemoteServiceRelativePath("repoxservice")
public interface RepoxService extends RemoteService {

    public AdminInfo loadAdminFormInfo() throws ServerSideException;
    public void saveAdminFormInfo(AdminInfo results) throws ServerSideException;

    public Map<String,String> getFullCountryList() throws ServerSideException;
    public List<String> getFullCharacterEncodingList() throws ServerSideException;

    public RepoxStatisticsUI getStatisticsInfo(StatisticsType statisticsType,String username) throws ServerSideException;

    public String getRepoxVersion() throws ServerSideException;
    public MainConfigurationInfo getInitialConfigData() throws ServerSideException;

    public ExternalServiceResultUI getValidationState(String dataSetID) throws ServerSideException;

    public Double getTimezoneOffset(String clientDate);

    public Boolean transformationResultFileExists(String dataSetId, String transformationId);
}
