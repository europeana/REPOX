package harvesterUI.client.panels.overviewGrid;

import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.browse.BrowseFilterPanel;
import harvesterUI.client.servlets.dataManagement.search.SearchServiceAsync;
import harvesterUI.shared.ProjectType;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.search.BaseSearchResult;

import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created to REPOX project.
 * User: Edmundo
 * Date: 28/03/12
 * Time: 13:01
 */
public class SearchComboBox extends ComboBox<ModelData>{

    private BaseModel lastSavedSearch;

    public SearchComboBox() {
        final SearchServiceAsync searchServiceAsync = (SearchServiceAsync) Registry.get(HarvesterUI.SEARCH_SERVICE);

        RpcProxy<PagingLoadResult<BaseSearchResult>> proxy = new RpcProxy<PagingLoadResult<BaseSearchResult>>() {
            @Override
            public void load(Object loadConfig, AsyncCallback<PagingLoadResult<BaseSearchResult>> callback) {
                List<FilterQuery> filterQueries = ((BrowseFilterPanel) Registry.get("browseFilterPanel")).getAllQueries();
                searchServiceAsync.getPagedMainGridSearchResults((PagingLoadConfig) loadConfig,filterQueries,HarvesterUI.UTIL_MANAGER.getLoggedUserName(),callback);
            }
        };

        PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);

        loader.addListener(Loader.BeforeLoad, new Listener<LoadEvent>() {
            public void handleEvent(LoadEvent be) {
                be.<ModelData>getConfig().set("start", be.<ModelData>getConfig().get("offset"));
                be.<ModelData>getConfig().set("searchString", getComboValue());
            }
        });

        ListStore<ModelData> store = new ListStore<ModelData>(loader);

        setDisplayField("name");
        setItemSelector("div.searchItem");
        setTemplate(getSearchTemplate());
        setStore(store);
        setHideTrigger(true);
        setPageSize(10);
        setMinChars(1);
        setEmptyText("Search...");

        setWidth(200);
//        setTypeAhead(true);
        setTriggerAction(ComboBox.TriggerAction.ALL);
        setMinListWidth(500);

        addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> se) {
                lastSavedSearch = (BaseModel)getListView().getSelectionModel().getSelectedItem();
                if(se.getSelectedItem() != null)
                    HarvesterUI.UTIL_MANAGER.getMainGridSearchResults();
            }
        });
    }

    public void setLastSavedSearch(BaseModel lastSavedSearch) {
        this.lastSavedSearch = lastSavedSearch;
    }

    private String getComboValue(){
        return getRawValue();
    }

    private String getSearchTemplate(){
        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT)
            return getEuropeanaSearchTemplate();
        else
            return getLightSearchTemplate();
    }

    private native String getEuropeanaSearchTemplate() /*-{
        return [
            '<tpl for=".">',
            '<div class="searchItem">',
            '<tpl if="dataType == \'DATA_PROVIDER\'">',
            '<h3>Data Provider: {name}</h3>',
            'NameCode: {nameCode}<br/>',
            'Description: {description}',
            '</tpl>',
            '<tpl if="dataType == \'AGGREGATOR\'">',
            '<h3>Aggregator: {name}</h3>',
            'NameCode: {nameCode}',
            '</tpl>',
            '<tpl if="dataType == \'DATA_SET\'">',
            '<h3>Data Set: {dataSet} <span>Records: {records}</span></h3>',
            'Name: {name}<br/>',
            'NameCode: {nameCode}<br/>',
            'Description: {description}',

            '</tpl>',
            '</div>',
            '</tpl>'
        ].join("");
    }-*/;

    private native String getLightSearchTemplate() /*-{
        return [
            '<tpl for=".">',
            '<div class="searchItem">',
            '<tpl if="dataType == \'DATA_PROVIDER\'">',
            '<h3>Data Provider: {name}</h3>',
            'Description: {description}',
            '</tpl>',
            '<tpl if="dataType == \'DATA_SET\'">',
            '<h3>Data Set: {dataSet} <span>Records: {records}</span></h3>',
            'Description: {description}',
            '</tpl>',
            '</div>',
            '</tpl>'
        ].join("");
    }-*/;

    public BaseModel getLastSavedSearch() {
        return lastSavedSearch;
    }
}
