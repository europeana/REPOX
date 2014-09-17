package harvesterUI.server.dataManagement.search;

import harvesterUI.client.servlets.dataManagement.search.SearchService;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.dataManagement.RepoxDataExchangeManager;
import harvesterUI.server.dataManagement.filters.FilterManagementUtil;
import harvesterUI.server.harvest.TaskManagementServiceImpl;
import harvesterUI.server.transformations.TransformationsServiceImpl;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.mdr.SchemaTreeUI;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.search.BaseSearchResult;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SearchServiceImpl extends RemoteServiceServlet implements SearchService {

    public SearchServiceImpl() {}

    public PagingLoadResult<BaseSearchResult> getPagedMainGridSearchResults(PagingLoadConfig config, List<FilterQuery> filterQueries, String username) throws ServerSideException{
        String searchValue = config.get("searchString");
        FilterManagementUtil.getInstance().createDataProviderUserFilter(filterQueries, username);
        List<BaseSearchResult> transformationUIList = RepoxServiceImpl.getProjectManager().getMainGridSearchResults(searchValue,filterQueries);

        ArrayList<BaseSearchResult> sublist = new ArrayList<BaseSearchResult>();
        int start = config.getOffset();
        int limit = transformationUIList.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }
        for (int i = config.getOffset(); i < limit; i++) {
            sublist.add(transformationUIList.get(i));
        }
        return new BasePagingLoadResult<BaseSearchResult>(sublist, config.getOffset(), transformationUIList.size());
    }

    public PagingLoadResult<TransformationUI> getPagedTransformationsSearchResults(PagingLoadConfig config) throws ServerSideException{
        String searchValue = config.get("searchString");
        List<TransformationUI> transformationUIList = RepoxDataExchangeManager.getFullTransformationsList(TransformationsServiceImpl.getTransformationMatcher());
        doTransformationsSearchComparison(transformationUIList,searchValue);

        ArrayList<TransformationUI> sublist = new ArrayList<TransformationUI>();
        int start = config.getOffset();
        int limit = transformationUIList.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }
        for (int i = config.getOffset(); i < limit; i++) {
            sublist.add(transformationUIList.get(i));
        }
        return new BasePagingLoadResult<TransformationUI>(sublist, config.getOffset(), transformationUIList.size());
    }

    private void doTransformationsSearchComparison(List<TransformationUI> transformationUIList, String searchValue){
        Iterator<TransformationUI> transIterator = transformationUIList.iterator();
        while(transIterator.hasNext()){
            TransformationUI transformationUI = transIterator.next();
            if(!Util.compareStrings(searchValue,transformationUI.getDescription()) &&
                    !Util.compareStrings(searchValue,transformationUI.getSrcFormat()) &&
                    !Util.compareStrings(searchValue,transformationUI.getDSStringFormat()) &&
                    !Util.compareStrings(searchValue,transformationUI.getDestFormat())){
                transIterator.remove();
            }
        }
    }

    public PagingLoadResult<SchemaTreeUI> getPagedSchemasSearchResults(PagingLoadConfig config) throws ServerSideException{
        String searchValue = config.get("searchString");
        List<SchemaTreeUI> transformationUIList = TransformationsServiceImpl.getSchemasTreeRaw();
        doSchemasSearchComparison(transformationUIList,searchValue);

        ArrayList<SchemaTreeUI> sublist = new ArrayList<SchemaTreeUI>();
        int start = config.getOffset();
        int limit = transformationUIList.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }
        for (int i = config.getOffset(); i < limit; i++) {
            sublist.add(transformationUIList.get(i));
        }
        return new BasePagingLoadResult<SchemaTreeUI>(sublist, config.getOffset(), transformationUIList.size());
    }

    private void doSchemasSearchComparison(List<SchemaTreeUI> transformationUIList, String searchValue){
        Iterator<SchemaTreeUI> transIterator = transformationUIList.iterator();
        while(transIterator.hasNext()){
            SchemaTreeUI transformationUI = transIterator.next();
            if(!Util.compareStrings(searchValue,transformationUI.getSchema()) &&
                    !Util.compareStrings(searchValue,transformationUI.getNamespace())){
                transIterator.remove();
            }
        }
    }

    public PagingLoadResult<ScheduledTaskUI> getPagedScheduledTasksSearchResults(PagingLoadConfig config, List<FilterQuery> filterQueries, String username) throws ServerSideException{
        String searchValue = config.get("searchString");
        FilterManagementUtil.getInstance().createDataProviderUserFilter(filterQueries, username);
        List<ScheduledTaskUI> scheduledTasksList = TaskManagementServiceImpl.getScheduledTasksUI(filterQueries);
        doScheduledTasksSearchComparison(scheduledTasksList, searchValue);

        ArrayList<ScheduledTaskUI> sublist = new ArrayList<ScheduledTaskUI>();
        int start = config.getOffset();
        int limit = scheduledTasksList.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }
        for (int i = config.getOffset(); i < limit; i++) {
            sublist.add(scheduledTasksList.get(i));
        }
        return new BasePagingLoadResult<ScheduledTaskUI>(sublist, config.getOffset(), scheduledTasksList.size());
    }

    private void doScheduledTasksSearchComparison(List<ScheduledTaskUI> scheduledTaskUIs, String searchValue){
        Iterator<ScheduledTaskUI> scheduledTasksIterator = scheduledTaskUIs.iterator();
        while(scheduledTasksIterator.hasNext()){
            ScheduledTaskUI scheduledTaskUI = scheduledTasksIterator.next();
            if(!Util.compareStrings(searchValue,scheduledTaskUI.getDataSetId()) &&
                    !Util.compareStrings(searchValue,String.valueOf(scheduledTaskUI.getMonthPeriod())) &&
                    !Util.compareStrings(searchValue,scheduledTaskUI.getDateString()) &&
                    !Util.compareStrings(searchValue,scheduledTaskUI.getId()) &&
                    !Util.compareStrings(searchValue,scheduledTaskUI.getType())){
                scheduledTasksIterator.remove();
            }
        }
    }

    public TransformationUI getMappingsSearchResult(TransformationUI searchedMapping) throws ServerSideException {
        List<TransformationUI> transformationUIList = RepoxDataExchangeManager.getFullTransformationsList(TransformationsServiceImpl.getTransformationMatcher());
        for(TransformationUI currentTransformation : transformationUIList){
            if(currentTransformation.getIdentifier().equals(searchedMapping.getIdentifier()))
                return currentTransformation;
        }
        return null;
    }
}
