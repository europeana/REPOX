/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.dataManagement;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.filters.FilterAttribute;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.filters.FilterQuery;

import java.util.List;

@RemoteServiceRelativePath("filterService")
public interface FilterService extends RemoteService {

    public List<FilterAttribute> getCountries(List<FilterQuery> filterQueries, String username) throws ServerSideException;
    public List<FilterAttribute> getTags(List<FilterQuery> filterQueries, String username) throws ServerSideException;
    public List<FilterAttribute> getMetadataFormats(List<FilterQuery> filterQueries, String username)throws ServerSideException;
    public List<FilterAttribute> getTransformations(List<FilterQuery> filterQueries, String username)throws ServerSideException;
    public List<FilterAttribute> getDataProviderTypes(List<FilterQuery> filterQueries, String username) throws ServerSideException;
    public List<FilterAttribute> getIngestType(List<FilterQuery> filterQueries, String username)throws ServerSideException;
    public DataContainer getFilteredData(List<FilterQuery> filterQueries,int offset, int limit, String username)throws ServerSideException;
}
