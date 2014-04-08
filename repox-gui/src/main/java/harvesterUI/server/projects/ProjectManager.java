package harvesterUI.server.projects;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.dataManagement.DataType;
import harvesterUI.server.dataManagement.dataSets.DataSetOperationsServiceImpl;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.AggregatorUI;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.SaveDataResponse;
import harvesterUI.shared.dataTypes.admin.AdminInfo;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.DatasetType;
import harvesterUI.shared.filters.FilterAttribute;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.filters.FilterType;
import harvesterUI.shared.search.BaseSearchResult;
import harvesterUI.shared.search.EuropeanaSearchResult;
import harvesterUI.shared.servletResponseStates.ResponseState;
import harvesterUI.shared.statistics.RepoxStatisticsUI;
import harvesterUI.shared.statistics.StatisticsType;
import harvesterUI.shared.tasks.OldTaskUI;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 30-04-2012
 * Time: 11:31
 */
public abstract class ProjectManager {

    public abstract RepoxStatisticsUI getStatisticsInfo(StatisticsType statisticsType, String username) throws ServerSideException;
    public abstract Map<String,String> getFullCountryList() throws ServerSideException;
    public abstract AdminInfo loadAdminFormInfo() throws ServerSideException;
    public abstract void saveAdminFormInfo(AdminInfo results) throws ServerSideException;

    public abstract List<DataContainer> getParsedData(int offSet, int limit) throws ServerSideException;
    public abstract DataContainer getSearchResult(ModelData data) throws ServerSideException;

    public abstract List<DataContainer> getViewResult(int offset, int limit, String type) throws ServerSideException;

    public abstract List<FilterAttribute> getDPAttributes(FilterType filterType,List<FilterQuery> filterQueries) throws ServerSideException;

    public abstract DataContainer getFilteredData(List<FilterQuery> filterQueries,int offset, int limit)throws ServerSideException;

    public abstract int getFilteredDataSize();

    protected void reloadOAIProperties(String oaiPropertiesReloadUrl) throws IOException {
        URL yahoo = new URL(oaiPropertiesReloadUrl);
        URLConnection yc = yahoo.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        yc.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();
    }

    /*********************************************************
     Save Functions
     **********************************************************/

    public abstract SaveDataResponse saveDataProvider(boolean update, DataProviderUI dataProviderUI, int pageSize, String username) throws ServerSideException;
    public abstract String deleteDataProviders(List<DataProviderUI> dataProviderUIs) throws ServerSideException;


    public abstract SaveDataResponse saveDataSource(boolean update, DatasetType type, String originalDSset, DataSourceUI dataSourceUI, int pageSize) throws ServerSideException;
    public abstract String addAllOAIURL(String url,String dataProviderID,String dsSchema,String dsNamespace,
                                        String dsMTDFormat, String name, String nameCode, String exportPath,DataSetOperationsServiceImpl dataSetOperationsService) throws ServerSideException;

    public abstract SaveDataResponse moveDataSources(List<DataSourceUI> dataSourceUIs, ModelData dataProviderUI, int pageSize) throws ServerSideException;
    public abstract String deleteDataSources(List<DataSourceUI> dataSourceUIs) throws ServerSideException;

    public abstract Boolean dataSourceExport(DataSourceUI dataSourceUI) throws ServerSideException;
    public abstract List<OldTaskUI> getParsedOldTasks(List<FilterQuery> filterQueries) throws ServerSideException;

    public abstract String sendFeedbackEmail(String userEmail, String title, String message, String messageType) throws ServerSideException;
    public abstract ResponseState sendUserDataEmail(String username, String email, String password) throws ServerSideException;

    public abstract DataSourceUI getDataSetInfo(String dataSetId) throws ServerSideException;

    public abstract List<BaseSearchResult> getMainGridSearchResults(String searchString, List<FilterQuery> filterQueries) throws ServerSideException;

    public abstract int getDataPage(String id, int pageSize);

    /*********************************************************
     Europeana Only Functions
     **********************************************************/

    public abstract SaveDataResponse moveDataProvider(List<DataProviderUI> dataProviders, ModelData aggregatorUI, int pageSize) throws ServerSideException;
    public abstract boolean isCorrectAggregator(String dataSetId, String aggregatorId) throws ServerSideException;
    public abstract List<ModelData> getAllAggregators() throws ServerSideException;
    public abstract SaveDataResponse saveAggregator(boolean update, AggregatorUI aggregatorUI, int pageSize) throws ServerSideException;
    public abstract String deleteAggregators(List<AggregatorUI> aggregatorUIs) throws ServerSideException;

    protected boolean isDifferentDataProvider(DataProviderUI dataProviderUI, DataSourceContainer dataSourceContainer) throws ServerSideException{
        if(dataProviderUI == null)
            return false;

        String currentDataProviderId = dataProviderUI.getId();
        String dsDataProviderID = RepoxServiceImpl.getRepoxManager().getDataManager().
                getDataProviderParent(dataSourceContainer.getDataSource().getId()).getId();

        return !currentDataProviderId.equals(dsDataProviderID);
    }

    // Search Functions

    protected ModelData createModel(String id,String name) {
        ModelData m = new BaseModelData();
        m.set("id", id);
        m.set("name", name);
        return m;
    }

    protected EuropeanaSearchResult createModelEuropeana(String id, String name, String nameCode, String description, String dataSet, DataType dataType) {
        return new EuropeanaSearchResult(id,name,nameCode,description,dataSet,dataType.toString());
    }

    protected BaseSearchResult createModelLight(String id,String name, String description, String dataSet,DataType dataType) {
        return new BaseSearchResult(id,name,description,dataSet,dataType.toString());
    }
}
