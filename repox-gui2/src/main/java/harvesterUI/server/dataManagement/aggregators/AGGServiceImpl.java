package harvesterUI.server.dataManagement.aggregators;

import harvesterUI.client.servlets.dataManagement.AGGService;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.AggregatorUI;
import harvesterUI.shared.dataTypes.SaveDataResponse;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AGGServiceImpl extends RemoteServiceServlet implements AGGService {

    public AGGServiceImpl() {}

    public SaveDataResponse saveAggregator(boolean update, AggregatorUI aggregatorUI, int pageSize) throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().saveAggregator(update, aggregatorUI, pageSize);
    }

    public String deleteAggregators(List<AggregatorUI> aggregatorUIs) throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().deleteAggregators(aggregatorUIs);
    }

}
