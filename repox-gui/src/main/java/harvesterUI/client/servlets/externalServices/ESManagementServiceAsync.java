package harvesterUI.client.servlets.externalServices;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.shared.externalServices.ExternalServiceUI;

import java.util.List;
import java.util.Map;

public interface ESManagementServiceAsync {

    public void removeExternalService(List<ExternalServiceUI> externalServiceUIs,AsyncCallback<String> callback);
    public void saveExternalService(boolean isUpdate,ExternalServiceUI externalServiceUI,AsyncCallback<String> callback);

    public void getAllExternalServices(boolean checkStatus,AsyncCallback<List<ExternalServiceUI>> callback);

}
