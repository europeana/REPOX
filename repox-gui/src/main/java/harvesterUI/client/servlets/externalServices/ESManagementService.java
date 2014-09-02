/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.externalServices;

import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.externalServices.ExternalServiceUI;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("esmservice")
public interface ESManagementService extends RemoteService {

    public String removeExternalService(List<ExternalServiceUI> externalServiceUIs) throws ServerSideException;
    public String saveExternalService(boolean isUpdate, ExternalServiceUI externalServiceUI) throws ServerSideException;

    public List<ExternalServiceUI> getAllExternalServices(boolean checkStatus) throws ServerSideException;

}
