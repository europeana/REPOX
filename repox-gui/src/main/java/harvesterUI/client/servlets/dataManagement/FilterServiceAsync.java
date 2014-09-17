package harvesterUI.client.servlets.dataManagement;

import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.filters.FilterAttribute;
import harvesterUI.shared.filters.FilterQuery;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FilterServiceAsync {

    public void getCountries(List<FilterQuery> filterQueries, String username,AsyncCallback<List<FilterAttribute>> callback);
    public void getTags(List<FilterQuery> filterQueries, String username,AsyncCallback<List<FilterAttribute>> callback);
    public void getMetadataFormats(List<FilterQuery> filterQueries, String username,AsyncCallback<List<FilterAttribute>> callback);
    public void getTransformations(List<FilterQuery> filterQueries, String username,AsyncCallback<List<FilterAttribute>> callback);
    public void getDataProviderTypes(List<FilterQuery> filterQueries, String username,AsyncCallback<List<FilterAttribute>> callback);
    public void getIngestType(List<FilterQuery> filterQueries, String username,AsyncCallback<List<FilterAttribute>> callback);
    public void getFilteredData(List<FilterQuery> filterQueries,int offset, int limit, String username,AsyncCallback<DataContainer> callback);

}
