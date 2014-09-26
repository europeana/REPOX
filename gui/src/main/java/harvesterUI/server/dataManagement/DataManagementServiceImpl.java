package harvesterUI.server.dataManagement;

import harvesterUI.client.servlets.dataManagement.DataManagementService;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.SimpleDataSetInfo;

import java.util.ArrayList;
import java.util.List;

import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DataManagementServiceImpl extends RemoteServiceServlet implements DataManagementService {

    public static int showSize;

    public DataManagementServiceImpl() {
        try{
            showSize = RepoxServiceImpl.getRepoxManager().getDataManager().getShowSize();
        } catch (ServerSideException e) {
            e.printStackTrace();
        }
    }

//    public DataContainer getMainData(PagingLoadConfig config) throws ServerSideException {
//
//        List<DataContainer> parsedData = RepoxServiceImpl.getProjectManager().getViewResult(config.getOffset(), config.getLimit(), (String) config.get("VIEW_TYPE"));
//
//        DataContainer dataContainer = new DataContainer(UUID.randomUUID().toString());
//        for(DataContainer model : parsedData)
//            dataContainer.add(model);
//        return dataContainer;
//    }

    public PagingLoadResult<DataContainer> getPagingData(PagingLoadConfig config) throws ServerSideException {
        try{
            int showSize;
            if(!((Boolean)config.get("isFiltered")))
                showSize = RepoxServiceImpl.getRepoxManager().getDataManager().getShowSize();
            else
                showSize = RepoxServiceImpl.getProjectManager().getFilteredDataSize();

            return new BasePagingLoadResult<DataContainer>(null, config.getOffset(), showSize);
        } catch (Exception e) {
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public DataSourceUI getDataSetInfo(String dataSetId) throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().getDataSetInfo(dataSetId);
    }

    public List<ModelData> getAllDataProviders() throws ServerSideException{
        List<ModelData> dataProviderIds = new ArrayList<ModelData>();
        List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
        try{
            for (Object data : allDataList){
                if(data instanceof DataProvider){
                    dataProviderIds.add(createModel(((DataProvider) data).getId(),((DataProvider) data).getName()));
                }
            }
            return dataProviderIds;
        } catch (Exception e) {
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<SimpleDataSetInfo> getAllDataSets() throws ServerSideException{
        List<SimpleDataSetInfo> dataSetSimpleInfoList = new ArrayList<SimpleDataSetInfo>();
        List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
        try{
            for (Object data : allDataList){
                if(data instanceof DataSourceContainer){
                    DataSource dataSource = ((DataSourceContainer) data).getDataSource();
                    dataSetSimpleInfoList.add(new SimpleDataSetInfo(dataSource.getId(),dataSource.getId()));
                }
            }
            return dataSetSimpleInfoList;
        } catch (Exception e) {
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<ModelData> getAllAggregators() throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().getAllAggregators();
    }

    public DataContainer getSearchResult(ModelData data) throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().getSearchResult(data);
    }

    private ModelData createModel(String id,String name) {
        ModelData m = new BaseModelData();
        m.set("id", id);
        m.set("name", name);
        return m;
    }
}
