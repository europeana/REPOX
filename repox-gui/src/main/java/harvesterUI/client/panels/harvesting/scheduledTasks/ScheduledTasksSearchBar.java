package harvesterUI.client.panels.harvesting.scheduledTasks;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.browse.BrowseFilterPanel;
import harvesterUI.client.servlets.dataManagement.search.SearchServiceAsync;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.List;

/**
 * Created to REPOX project.
 * User: Edmundo
 * Date: 28/03/12
 * Time: 13:01
 */
public class ScheduledTasksSearchBar extends ComboBox<ScheduledTaskUI>{

    private Grid<ScheduledTaskUI> grid;
    private ScheduledTaskUI lastSearchResult;

    public ScheduledTasksSearchBar(Grid<ScheduledTaskUI> grid) {
        this.grid = grid;

        final SearchServiceAsync searchServiceAsync = (SearchServiceAsync) Registry.get(HarvesterUI.SEARCH_SERVICE);

        RpcProxy<PagingLoadResult<ScheduledTaskUI>> proxy = new RpcProxy<PagingLoadResult<ScheduledTaskUI>>() {
            @Override
            public void load(Object loadConfig, AsyncCallback<PagingLoadResult<ScheduledTaskUI>> callback) {
                List<FilterQuery> filterQueries = ((BrowseFilterPanel) Registry.get("browseFilterPanel")).getAllQueries();
                String username = HarvesterUI.UTIL_MANAGER.getLoggedUserName();
                searchServiceAsync.getPagedScheduledTasksSearchResults((PagingLoadConfig) loadConfig,filterQueries,username,callback);
            }
        };

        PagingLoader<PagingLoadResult<ScheduledTaskUI>> loader = new BasePagingLoader<PagingLoadResult<ScheduledTaskUI>>(proxy);

        loader.addListener(Loader.BeforeLoad, new Listener<LoadEvent>() {
            public void handleEvent(LoadEvent be) {
                be.<ModelData>getConfig().set("start", be.<ModelData>getConfig().get("offset"));
                be.<ModelData>getConfig().set("searchString", getComboValue());
            }
        });

        ListStore<ScheduledTaskUI> store = new ListStore<ScheduledTaskUI>(loader);

        setDisplayField("id");
        setItemSelector("div.searchItem");
        setTemplate(getSearchTemplate());
        setStore(store);
        setHideTrigger(true);
        setPageSize(10);
        setMinChars(1);
        setEmptyText("Search...");

        setWidth(200);
        setTriggerAction(TriggerAction.ALL);
        setMinListWidth(500);

        addSelectionChangedListener(new SelectionChangedListener<ScheduledTaskUI>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<ScheduledTaskUI> se) {
                lastSearchResult = getListView().getSelectionModel().getSelectedItem();
                loadSearchResults();
            }
        });
    }

    private String getComboValue(){
        return getRawValue();
    }

    private native String getSearchTemplate() /*-{
        return [
            '<tpl for=".">',
            '<div class="searchItem">',
            '<h3>ID: {id} - DataSet: {dataSetId}</h3>',
            'Date String: {dateString}<br/>',
            'Type: {type}<br/>',
            '</div>',
            '</tpl>'
        ].join("");
    }-*/;

    public void loadSearchResults(){
        UtilManager.maskCentralPanel(HarvesterUI.CONSTANTS.loadingSearchResults());
        grid.getStore().removeAll();
        grid.getStore().add(lastSearchResult);
        UtilManager.unmaskCentralPanel();
    }
}
