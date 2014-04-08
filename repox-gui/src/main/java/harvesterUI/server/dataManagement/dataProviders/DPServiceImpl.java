package harvesterUI.server.dataManagement.dataProviders;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import harvesterUI.client.servlets.dataManagement.DPService;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.SaveDataResponse;

import java.util.List;

public class DPServiceImpl extends RemoteServiceServlet implements DPService {

    public DPServiceImpl() {
    }

    public SaveDataResponse saveDataProvider(boolean update, DataProviderUI dataProviderUI, int pageSize, String username) throws ServerSideException {
        return RepoxServiceImpl.getProjectManager().saveDataProvider(update,dataProviderUI,pageSize,username);
    }

    public SaveDataResponse moveDataProvider(List<DataProviderUI> dataProviders, ModelData aggregatorUI, int pageSize) throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().moveDataProvider(dataProviders, aggregatorUI, pageSize);
    }

    public String deleteDataProviders(List<DataProviderUI> dataProviderUIs) throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().deleteDataProviders(dataProviderUIs);
    }

}
