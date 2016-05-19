/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.userManagement;

import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.UserAuthentication;
import harvesterUI.shared.servletResponseStates.RepoxServletResponseStates;
import harvesterUI.shared.servletResponseStates.ResponseState;
import harvesterUI.shared.users.User;

import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

//import harvesterUI.client.models.FilterAttributes;
//import harvesterUI.client.models.MailItem;

public interface UserManagementServiceAsync {

    public void savePerPageData(String username,int dataPerPage,AsyncCallback<String> callback);
    public void getPagedUsers(PagingLoadConfig config, AsyncCallback<PagingLoadResult<User>> callback);
    public void getPagedAvailableDataProviders(PagingLoadConfig config, AsyncCallback<PagingLoadResult<DataProviderUI>> callback);
    public void getAvailableDataProviders(AsyncCallback<List<DataProviderUI>> callback);
//    public void isFirstTimeRepoxUsed(AsyncCallback<Boolean> callback);
//    public void registerNewEntity(String name, String mail,String institution, String skypeContact, String repoxUrl, AsyncCallback<RepoxServletResponseStates.GeneralStates> callback);
    public void addUserActivityData(String serverUrl,AsyncCallback callback);

    public void validateSessionId(String sessionId,AsyncCallback<String> callback);
    public void confirmLogin(String user, String password, AsyncCallback<UserAuthentication> callback);
    public void getUsers(AsyncCallback<List<User>> callback);
    public void getUser(String userName, AsyncCallback<User> callback);
    public void saveUser(User user, String oldUsername, boolean isUpdate,AsyncCallback<ResponseState> callback);
    public void resetUserPassword(String userName,AsyncCallback<ResponseState> callback);
    public void removeUsers(List<User> users,AsyncCallback callback);

    public void sendFeedbackEmail(String userEmail, String title, String message, String messageType, AsyncCallback<String> callback);

    public void checkLDAPAuthentication(String loginDN, String password, AsyncCallback<Boolean> callback);
}
