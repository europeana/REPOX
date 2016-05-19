/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.userManagement;

import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.UserAuthentication;
import harvesterUI.shared.servletResponseStates.RepoxServletResponseStates;
import harvesterUI.shared.servletResponseStates.ResponseState;
import harvesterUI.shared.users.User;

import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("userManagementService")
public interface UserManagementService extends RemoteService {

    public String savePerPageData(String username,int dataPerPage) throws ServerSideException;
    public PagingLoadResult<User> getPagedUsers(PagingLoadConfig config) throws ServerSideException;
    public PagingLoadResult<DataProviderUI> getPagedAvailableDataProviders(PagingLoadConfig config) throws ServerSideException;
    public List<DataProviderUI> getAvailableDataProviders();
//    public boolean isFirstTimeRepoxUsed() throws ServerSideException;
//    public RepoxServletResponseStates.GeneralStates registerNewEntity(String name, String mail, String institution, String skypeContact,String repoxUrl) throws ServerSideException;
    public void addUserActivityData(String serverUrl);

    public String validateSessionId(String sessionId) throws ServerSideException;
    public UserAuthentication confirmLogin(String user, String password) throws ServerSideException;
    public List<User> getUsers() throws ServerSideException;
    public User getUser(String userName) throws ServerSideException;
    public ResponseState saveUser(User user, String oldUsername, boolean isUpdate) throws ServerSideException;
    public ResponseState resetUserPassword(String userName) throws ServerSideException;
    public void removeUsers(List<User> users) throws ServerSideException;

    public String sendFeedbackEmail(String userEmail, String title, String message, String messageType) throws ServerSideException;

    public boolean checkLDAPAuthentication(String loginDN, String password) throws ServerSideException;

}
