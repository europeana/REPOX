package harvesterUI.client.servlets.externalServices;

import harvesterUI.shared.externalServices.ExternalServiceUI;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ESManagementServiceAsync {

    public void removeExternalService(List<ExternalServiceUI> externalServiceUIs,AsyncCallback<String> callback);
    public void saveExternalService(boolean isUpdate,ExternalServiceUI externalServiceUI,AsyncCallback<String> callback);

    public void getAllExternalServices(boolean checkStatus,AsyncCallback<List<ExternalServiceUI>> callback);

}
