package harvesterUI.client.panels.mdr;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.servlets.dataManagement.search.SearchServiceAsync;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.mdr.SchemaTreeUI;
import harvesterUI.shared.mdr.TransformationUI;

/**
 * Created to REPOX project.
 * User: Edmundo
 * Date: 28/03/12
 * Time: 13:01
 */
public class SchemasSearchBar extends ComboBox<SchemaTreeUI>{

    private TreeGrid<SchemaTreeUI> grid;
    private SchemaTreeUI lastSearchResult;

    public SchemasSearchBar(final TreeGrid<SchemaTreeUI> grid) {
        this.grid = grid;

        final SearchServiceAsync searchServiceAsync = (SearchServiceAsync) Registry.get(HarvesterUI.SEARCH_SERVICE);

        RpcProxy<PagingLoadResult<SchemaTreeUI>> proxy = new RpcProxy<PagingLoadResult<SchemaTreeUI>>() {
            @Override
            public void load(Object loadConfig, AsyncCallback<PagingLoadResult<SchemaTreeUI>> callback) {
                searchServiceAsync.getPagedSchemasSearchResults((PagingLoadConfig) loadConfig, callback);
            }
        };

        PagingLoader<PagingLoadResult<SchemaTreeUI>> loader = new BasePagingLoader<PagingLoadResult<SchemaTreeUI>>(proxy);

        loader.addListener(Loader.BeforeLoad, new Listener<LoadEvent>() {
            public void handleEvent(LoadEvent be) {
                be.<ModelData>getConfig().set("start", be.<ModelData>getConfig().get("offset"));
                be.<ModelData>getConfig().set("searchString", getComboValue());
            }
        });

        ListStore<SchemaTreeUI> store = new ListStore<SchemaTreeUI>(loader);

        setDisplayField("schema");
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

        addSelectionChangedListener(new SelectionChangedListener<SchemaTreeUI>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<SchemaTreeUI> se) {
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
            '<h3>{schema}</h3>',
            'Namespace: {namespace}',
            '</div>',
            '</tpl>'
        ].join("");
    }-*/;

//    private native String getSearchTemplate() /*-{
//        return  [
//            '<tpl for=".">',
//            '<div class="x-combo-list-item">',
////            '<tpl if="{[values.value]} == \'DP\'">',
////            '{[values.attributeName]}',
////            '</tpl>',
//            '{[values.prefix]} - {[values.name]}',
//            '</div>',
//            '</tpl>'
//        ].join("");
//    }-*/;

    public void loadSearchResults(){
        UtilManager.maskCentralPanel(HarvesterUI.CONSTANTS.loadingSearchResults());
        grid.getStore().removeAll();
        grid.getTreeStore().add(lastSearchResult,true);
        grid.expandAll();
        UtilManager.unmaskCentralPanel();
    }
}
