package harvesterUI.server.dataManagement.filters;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import harvesterUI.client.servlets.dataManagement.FilterService;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.filters.FilterAttribute;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.filters.FilterType;

import java.util.List;

public class FilterServiceImpl extends RemoteServiceServlet implements FilterService {

    private FilterManagementUtil filterManagementUtil;

    public FilterServiceImpl() {
        filterManagementUtil = FilterManagementUtil.getInstance();
    }

    public List<FilterAttribute> getDataProviderTypes(List<FilterQuery> filterQueries, String username) throws ServerSideException {
        filterManagementUtil.createDataProviderUserFilter(filterQueries, username);
        return filterManagementUtil.removeDuplicates(filterManagementUtil.getDPAttributes(FilterType.DP_TYPE,filterQueries));
    }

    public List<FilterAttribute> getCountries(List<FilterQuery> filterQueries, String username) throws ServerSideException {
        filterManagementUtil.createDataProviderUserFilter(filterQueries, username);
        return filterManagementUtil.removeDuplicates(filterManagementUtil.getDPAttributes(FilterType.COUNTRY,filterQueries));
    }

    public List<FilterAttribute> getTags(List<FilterQuery> filterQueries, String username) throws ServerSideException {
        filterManagementUtil.createDataProviderUserFilter(filterQueries, username);
        return filterManagementUtil.removeDuplicates(filterManagementUtil.getDSAttributes(FilterType.TAG,filterQueries));
    }

    public List<FilterAttribute> getMetadataFormats(List<FilterQuery> filterQueries, String username)throws ServerSideException{
        filterManagementUtil.createDataProviderUserFilter(filterQueries, username);
        return filterManagementUtil.removeDuplicates(filterManagementUtil.getDSAttributes(FilterType.METADATA_FORMAT,filterQueries));
    }

    public List<FilterAttribute> getTransformations(List<FilterQuery> filterQueries, String username)throws ServerSideException{
        filterManagementUtil.createDataProviderUserFilter(filterQueries, username);
        return filterManagementUtil.removeDuplicates(filterManagementUtil.getDSAttributes(FilterType.TRANSFORMATION,filterQueries));
    }

    public List<FilterAttribute> getIngestType(List<FilterQuery> filterQueries, String username)throws ServerSideException{
        filterManagementUtil.createDataProviderUserFilter(filterQueries, username);
        return filterManagementUtil.removeDuplicates(filterManagementUtil.getDSAttributes(FilterType.INGEST_TYPE,filterQueries));
    }

    public DataContainer getFilteredData(List<FilterQuery> filterQueries,int offset, int limit, String username)throws ServerSideException{
        filterManagementUtil.createDataProviderUserFilter(filterQueries, username);
        return RepoxServiceImpl.getProjectManager().getFilteredData(filterQueries,offset,limit);
    }
}
