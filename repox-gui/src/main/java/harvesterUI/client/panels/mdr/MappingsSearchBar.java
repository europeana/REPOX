package harvesterUI.client.panels.mdr;

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
import harvesterUI.client.servlets.dataManagement.search.SearchServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.mdr.TransformationUI;

/**
 * Created to REPOX project.
 * User: Edmundo
 * Date: 28/03/12
 * Time: 13:01
 */
public class MappingsSearchBar extends ComboBox<TransformationUI>{

    private Grid<TransformationUI> grid;
    private TransformationUI lastSearchResult;

    public MappingsSearchBar(Grid<TransformationUI> grid) {
        this.grid = grid;

        final SearchServiceAsync searchServiceAsync = (SearchServiceAsync) Registry.get(HarvesterUI.SEARCH_SERVICE);

        RpcProxy<PagingLoadResult<TransformationUI>> proxy = new RpcProxy<PagingLoadResult<TransformationUI>>() {
            @Override
            public void load(Object loadConfig, AsyncCallback<PagingLoadResult<TransformationUI>> callback) {
                searchServiceAsync.getPagedTransformationsSearchResults((PagingLoadConfig) loadConfig,callback);
            }
        };

        PagingLoader<PagingLoadResult<TransformationUI>> loader = new BasePagingLoader<PagingLoadResult<TransformationUI>>(proxy);

        loader.addListener(Loader.BeforeLoad, new Listener<LoadEvent>() {
            public void handleEvent(LoadEvent be) {
                be.<ModelData>getConfig().set("start", be.<ModelData>getConfig().get("offset"));
                be.<ModelData>getConfig().set("searchString", getComboValue());
            }
        });

        ListStore<TransformationUI> store = new ListStore<TransformationUI>(loader);

        setDisplayField("description");
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

        addSelectionChangedListener(new SelectionChangedListener<TransformationUI>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<TransformationUI> se) {
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
            '<h3>{description}</h3>',
            'Source Format: {srcFormat}<br/>',
            'Destination Format: {destFormat}<br/>',
//            'Compose String: {dsStringFormat}',
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
        AsyncCallback<TransformationUI> callback = new AsyncCallback<TransformationUI>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(TransformationUI result) {
                if(result == null)
                    return;

                grid.getStore().removeAll();
                grid.getStore().add(result);
                UtilManager.unmaskCentralPanel();
            }
        };
        if(lastSearchResult != null)
            ((SearchServiceAsync) Registry.get(HarvesterUI.SEARCH_SERVICE)).getMappingsSearchResult(lastSearchResult, callback);
    }

    public void setLastSearchResult(TransformationUI lastSearchResult) {
        this.lastSearchResult = lastSearchResult;
    }
}
