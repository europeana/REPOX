package harvesterUI.server.dataManagement.filters;

import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.projects.europeana.EuropeanaManager;
import harvesterUI.server.userManagement.UserManagementServiceImpl;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.filters.FilterAttribute;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.filters.FilterQueryLastIngest;
import harvesterUI.shared.filters.FilterQueryRecords;
import harvesterUI.shared.filters.FilterType;
import harvesterUI.shared.users.DataProviderUser;
import harvesterUI.shared.users.User;
import harvesterUI.shared.users.UserRole;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.dataSource.DataSourceTag;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.rest.dataProvider.Aggregator;
import pt.utl.ist.rest.dataProvider.DefaultDataManager;
import pt.utl.ist.rest.dataProvider.DefualtDataProvider;

import com.extjs.gxt.ui.client.util.DateWrapper;

/**
 * Created to project REPOX.
 * User: Edmundo
 * Date: 06/02/13
 * Time: 15:12
 */
public class FilterManagementUtil {

    private static FilterManagementUtil instance = null;

    public static FilterManagementUtil getInstance() {
        if(instance == null) {
            instance = new FilterManagementUtil();
        }
        return instance;
    }

    protected FilterManagementUtil() {
    }

    public List<FilterAttribute> getDPAttributes(FilterType filterType, List<FilterQuery> filterQueries) throws ServerSideException {
        return RepoxServiceImpl.getProjectManager().getDPAttributes(filterType,filterQueries);
    }

    public List<FilterAttribute> getDSAttributes(FilterType filterType, List<FilterQuery> filterQueries) throws ServerSideException {
        List<FilterAttribute> values = new ArrayList<FilterAttribute>();
        List<Object> allDataList = FilterManagementUtil.getInstance().getRawFilteredData(filterQueries).getFilteredData();
        for(Object object : allDataList){
            if(object instanceof DataSourceContainer){
                DataSourceContainer dataSourceContainer = (DataSourceContainer)object;
                DataSource dataSource = dataSourceContainer.getDataSource();
                if(filterType.equals(FilterType.METADATA_FORMAT)){
                    values.add(new FilterAttribute(dataSource.getMetadataFormat(),dataSource.getMetadataFormat()));
                }else if(filterType.equals(FilterType.TRANSFORMATION)){
                    for(MetadataTransformation metadataTransformation : dataSource.getMetadataTransformations().values())
                        values.add(new FilterAttribute(metadataTransformation.getId(),metadataTransformation.getId()));
                }else if(filterType.equals(FilterType.TAG)){
                    for(DataSourceTag dataSourceTag : dataSource.getTags())
                        values.add(new FilterAttribute(dataSourceTag.getName(),dataSourceTag.getName()));
                }else if(filterType.equals(FilterType.INGEST_TYPE)){
//                        dataSource.get
//                        values.add(new FilterAttribute(metadataTransformation.getId(),metadataTransformation.getId()));
                }
            }
        }
        return values;
    }

    public void createDataProviderUserFilter(List<FilterQuery> filterQueries, String username) throws ServerSideException{
        if(username != null && !username.isEmpty()){
            User user = UserManagementServiceImpl.getInstance().getUser(username);
            if(user != null && user.getRole().equals(UserRole.DATA_PROVIDER.name())){
                DataProviderUser dataProviderUser = (DataProviderUser) user;
                FilterQuery filterQuery = new FilterQuery(FilterType.DATA_PROVIDER_USER,dataProviderUser.getAllowedDataProviderIds());
                filterQueries.add(filterQuery);
            }
        }
    }

    public void compareDataProviderValues(DataProvider dataProvider, FilterQuery filterQuery, List<Object> dataToRemove){
        Boolean result = isSameCountry(dataProvider.getCountry(), filterQuery);
        if(result != null && !result)
            deleteDataProviderFromList(dataProvider, dataToRemove);

        result = isSameDataProviderId(dataProvider.getId(), filterQuery);
        if(result != null && !result)
            deleteDataProviderFromList(dataProvider, dataToRemove);

        if(RepoxServiceImpl.getProjectManager() instanceof EuropeanaManager){
            DefualtDataProvider dataProviderEuropeana = (DefualtDataProvider) dataProvider;
            result = isSameDPType(dataProviderEuropeana.getDataSetType().name(), filterQuery);
            if(result != null && !result)
                deleteDataProviderFromList(dataProvider, dataToRemove);
        }
    }

    public void compareDataSetValues(DataSourceContainer dataSourceContainer, FilterQuery filterQuery, List<Object> dataToRemove) throws ServerSideException{
        try{
            DataSource dataSource = dataSourceContainer.getDataSource();
            Boolean result = compareRecords(dataSource.getIntNumberRecords(), filterQuery);
            if(result != null && !result)
                deleteDataSourceFromList(dataSourceContainer, dataToRemove);

            if(dataSource.getOldTasksList().size() > 0){
                result = compareLastIngest(dataSource.getOldTasksList().get(0).getActualDate(), filterQuery);
                if(result != null && !result)
                    deleteDataSourceFromList(dataSourceContainer, dataToRemove);
            }else if(filterQuery.getFilterType() == FilterType.LAST_INGEST)
                deleteDataSourceFromList(dataSourceContainer, dataToRemove);

            result = compareMetadataFormat(dataSource.getMetadataFormat(), filterQuery);
            if(result != null && !result)
                deleteDataSourceFromList(dataSourceContainer, dataToRemove);

            result = compareTransformation(dataSource, filterQuery);
            if(result != null && !result)
                deleteDataSourceFromList(dataSourceContainer, dataToRemove);

            result = compareTags(dataSource, filterQuery);
            if(result != null && !result)
                deleteDataSourceFromList(dataSourceContainer, dataToRemove);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public Boolean isSameCountry(String countryToCompare, FilterQuery filterQuery){
        if(filterQuery.getFilterType() == FilterType.COUNTRY){
            for(String countryValue : filterQuery.getValues()){
                if(countryValue.equals(countryToCompare))
                    return true;
            }
            return false;
        }else
            return null;
    }

    public Boolean isSameDataProviderId(String idToCompare, FilterQuery filterQuery){
        if(filterQuery.getFilterType() == FilterType.DATA_PROVIDER_USER){
            for(String dpId : filterQuery.getValues()){
                if(idToCompare.equals(dpId))
                    return true;
            }
            return false;
        }else
            return null;
    }

    public Boolean isSameDPType(String typeToCompare, FilterQuery filterQuery){
        if(filterQuery.getFilterType() == FilterType.DP_TYPE){
            for(String typeValue : filterQuery.getValues()){
                if(typeValue.equals(typeToCompare))
                    return true;
            }
            return false;
        }else
            return null;
    }

    public Boolean compareRecords(int recordsToCompare, FilterQuery filterQuery){
        if(filterQuery.getFilterType() == FilterType.RECORDS){
            FilterQueryRecords filterQueryRecords = (FilterQueryRecords) filterQuery;
            int beginRecords = filterQueryRecords.getBeginRecords();
            int endRecords = filterQueryRecords.getEndRecords();
            int onRecords = filterQueryRecords.getOnRecords();

            if(onRecords != -1)
                return onRecords == recordsToCompare;
            else if(beginRecords != -1 && endRecords != -1)
                return recordsToCompare >= beginRecords && recordsToCompare <= endRecords;
            else if(beginRecords != -1)
                return recordsToCompare >= beginRecords;
            else if(endRecords != -1)
                return recordsToCompare <= endRecords;


            return false;
        }else
            return null;
    }

    public Boolean compareLastIngest(Date lastIngestDate, FilterQuery filterQuery){
        if(filterQuery.getFilterType() == FilterType.LAST_INGEST){
            FilterQueryLastIngest query = (FilterQueryLastIngest) filterQuery;
            boolean result = false;

            if(query.getOnDate() != null){
                lastIngestDate.setHours(0);
                lastIngestDate.setMinutes(0);
                lastIngestDate.setSeconds(0);
                result = query.getOnDate().getDate() == lastIngestDate.getDate() &&
                        query.getOnDate().getMonth() == lastIngestDate.getMonth() &&
                        query.getOnDate().getYear() == lastIngestDate.getYear();
            } else if(query.getBeginDate() != null && query.getEndDate() != null)
                result = lastIngestDate.after(query.getBeginDate()) && lastIngestDate.before(query.getEndDate());
            else if(query.getBeginDate() != null)
                result = lastIngestDate.after(query.getBeginDate());
            else if(query.getEndDate() != null)
                result = lastIngestDate.before(query.getEndDate());


            if(result || (query.getBeginDate() == null && query.getEndDate() == null && query.getOnDate() == null)){
                if(query.getBeginTime() != null && query.getEndTime() != null) {
                    boolean result1;
                    boolean result2;
                    result1 = compareTime(query.getBeginTime(),lastIngestDate,1);
                    result2 = compareTime(query.getEndTime(),lastIngestDate,2);
                    result = result1&&result2;
                } else {
                    if(query.getBeginTime() != null) {
                        result = compareTime(query.getBeginTime(),lastIngestDate,1);
                    }
                    else if(query.getEndTime() != null) {
                        result = compareTime(query.getEndTime(),lastIngestDate,2);
                    }
                }
            }

            return result;
        }else
            return null;
    }

    public boolean compareTime(Date date1, Date date2, int type)  {
        DateWrapper dw1 = new DateWrapper(date1);
        int hrs = dw1.getHours();
        int min = dw1.getMinutes();
        int sec = dw1.getSeconds();

        DateWrapper dw2 = new DateWrapper(date2);
        int hrs2 = dw2.getHours();
        int min2 = dw2.getMinutes();
        int sec2 = dw2.getSeconds();

        return compareRawTime(hrs,hrs2,min,min2,sec,sec2,type);
    }

    public boolean compareRawTime(int hrs,int hrs2,int min,int min2, int sec, int sec2,int type){
        if(type == 1)
            return hrs2 > hrs || hrs == hrs2 && min2 >= min || hrs == hrs2 && min == min2 && sec >= sec2;
        else if(type == 2)
            return hrs2 < hrs || hrs == hrs2 && min2 <= min || hrs == hrs2 && min == min2 && sec <= sec2;
        return true;
    }

    public Boolean compareMetadataFormat(String metadataFormat, FilterQuery filterQuery){
        if(filterQuery.getFilterType() == FilterType.METADATA_FORMAT){
            for(String metadataFormatValue : filterQuery.getValues()){
                if(metadataFormatValue.equals(metadataFormat))
                    return true;
            }
            return false;
        }else
            return null;
    }

    public Boolean compareTransformation(DataSource dataSource, FilterQuery filterQuery){
        if(filterQuery.getFilterType() == FilterType.TRANSFORMATION){
            for(String transformationValue : filterQuery.getValues()){
                for(MetadataTransformation metadataTransformation : dataSource.getMetadataTransformations().values()){
                    if(transformationValue.equals(metadataTransformation.getId()))
                        return true;
                }
            }
            return false;
        }else
            return null;
    }

    public Boolean compareTags(DataSource dataSource, FilterQuery filterQuery){
        if(filterQuery.getFilterType() == FilterType.TAG){
            for(String tagName : filterQuery.getValues()){
                for(DataSourceTag dataSourceTag : dataSource.getTags()){
                    if(tagName.equals(dataSourceTag.getName()))
                        return true;
                }
            }
            return false;
        }else
            return null;
    }

    private void deleteDataSourceFromList(DataSourceContainer dataSourceContainer, List<Object> dataToRemove){
        addDataObjectToList(dataToRemove,dataSourceContainer);
        DataSource currentDataSource = dataSourceContainer.getDataSource();
        try {
            DataProvider dataProvider = RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviderParent(currentDataSource.getId());

            if(RepoxServiceImpl.getProjectManager() instanceof EuropeanaManager){
                Aggregator aggregatorEuropeana = ((DefaultDataManager) RepoxServiceImpl.getRepoxManager().
                        getDataManager()).getAggregatorParent(dataProvider.getId());

                addDataObjectToList(dataToRemove,aggregatorEuropeana);
            }

            addDataObjectToList(dataToRemove,dataProvider);
        } catch (ServerSideException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void addDataObjectToList(List<Object> finalList, Object dataObject){
        if(!finalList.contains(dataObject))
            finalList.add(dataObject);
    }

    // Remove duplicates of a list of FilterAttributes compared with their value
    public List<FilterAttribute> removeDuplicates(List<FilterAttribute> list) {
        List<FilterAttribute> noDuplicates = new ArrayList<FilterAttribute>();
        boolean doAdd = true;
        for (int i = 0; i < list.size(); i++) {
            FilterAttribute testValue = list.get(i);
            // Check if the tree node has the attribute
            if(list.get(i).getValue() != null && testValue.getValue() != null
                    && !list.get(i).getValue().equals("")) {
                for (int j = 0; j < list.size(); j++) {
                    // Check if the tree node has the attribute
                    if(list.get(j).getValue() != null && testValue.getValue() != null
                            && !list.get(j).getValue().equals("")) {
                        if (i == j)
                            break;
                        else if (list.get(j).getValue().equals(testValue.getValue())) {
                            doAdd = false;
                            break;
                        }
                    }
                }
                if (doAdd)
                    noDuplicates.add(testValue);
                else
                    doAdd = true;
            }
        }
        return noDuplicates;
    }

    public FilteredDataResponse getRawFilteredData(List<FilterQuery> filterQueries) throws ServerSideException {
        List<Object> filteredData = new ArrayList<Object>(RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList());
        List<Object> dataToRemove = new ArrayList<Object>();
        boolean dataWasFiltered = false;
        for(FilterQuery filterQuery : filterQueries){
            dataWasFiltered = true;
            dataToRemove.clear();
            for(Object model : filteredData){
                if(model instanceof DataProvider){
                    FilterManagementUtil.getInstance().compareDataProviderValues((DataProvider) model, filterQuery, dataToRemove);
                } else if(model instanceof DataSourceContainer){
                    FilterManagementUtil.getInstance().compareDataSetValues((DataSourceContainer)model, filterQuery, dataToRemove);
                }
            }
            removeElementsFromOneListInAnother(filteredData, dataToRemove);
        }
        removeEmptyDataProvidersFromDataSetFilters(filterQueries,filteredData);
        return new FilteredDataResponse(filteredData,dataWasFiltered);
    }

    private void removeEmptyDataProvidersFromDataSetFilters(List<FilterQuery> filterQueries,List<Object> filteredData){
        boolean noDPFilters = true;
        for(FilterQuery filterQuery : filterQueries){
            if(filterQuery.getFilterType() == FilterType.COUNTRY ||
                    filterQuery.getFilterType() == FilterType.DATA_PROVIDER_USER ||
                    filterQuery.getFilterType() == FilterType.DP_TYPE){
                noDPFilters = false;
                break;
            }
        }

        if(filterQueries.size() == 0)
            return;

        Iterator<Object> iterator = filteredData.iterator();
        while (iterator.hasNext()){
            Object model = iterator.next();
            if((model instanceof DataProvider || model instanceof Aggregator) && noDPFilters){
                iterator.remove();
            }
        }
    }

    private void removeElementsFromOneListInAnother(List<Object> allDataList, List<Object> toRemoveList){
        for(Object object: toRemoveList)
            allDataList.remove(object);
    }

    public void deleteDataProviderFromList(DataProvider dataProvider, List<Object> dataToRemove){
        deleteSubDataSources(dataProvider,dataToRemove);
        dataToRemove.add(dataProvider);
        if(RepoxServiceImpl.getProjectManager() instanceof EuropeanaManager)
            deleteDataProviderAggregator(dataProvider,dataToRemove);
    }

    protected void deleteSubDataSources(DataProvider dataProvider, List<Object> dataToRemove){
        dataToRemove.addAll(dataProvider.getDataSourceContainers().values());
    }

    protected void deleteDataProviderAggregator(DataProvider dataProvider, List<Object> dataToRemove){
        try {
            Aggregator aggregatorEuropeana = ((DefaultDataManager) RepoxServiceImpl.getRepoxManager().
                    getDataManager()).getAggregatorParent(dataProvider.getId());
            dataToRemove.add(aggregatorEuropeana);
        } catch (ServerSideException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
