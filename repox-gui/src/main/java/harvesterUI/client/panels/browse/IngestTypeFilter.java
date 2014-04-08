package harvesterUI.client.panels.browse;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.CheckChangedEvent;
import com.extjs.gxt.ui.client.event.CheckChangedListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.models.FilterButton;
import harvesterUI.client.servlets.dataManagement.FilterServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.filters.FilterAttribute;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.filters.FilterType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 28-04-2011
 * Time: 12:21
 */
public class IngestTypeFilter extends FilterButton {

    private TreePanel<FilterAttribute> ingestTypeList;
    private TreeStore<FilterAttribute> ingestTypeListStore;
    private TreePanel<FilterAttribute> checkedIngestTypeList;
    private TreeStore<FilterAttribute> checkedIngestTypeListStore;
    private boolean canCheck;

    public IngestTypeFilter(BrowseFilterPanel browseFilterPanel) {
        super(browseFilterPanel);

        setText(HarvesterUI.CONSTANTS.ingestTypes());

        Menu rangeMenu = new Menu();
        rangeMenu.add(createCheckedCountryList());
        rangeMenu.add(new SeparatorMenuItem());
        rangeMenu.add(createCountryList());
        setMenu(rangeMenu);
    }

    private TreePanel createCountryList(){
        ingestTypeListStore = new TreeStore<FilterAttribute>();

        ingestTypeList = new TreePanel<FilterAttribute>(ingestTypeListStore);
        ingestTypeList.setCheckable(true);
        ingestTypeList.setDisplayProperty("value");
        ingestTypeListStore.sort("value", Style.SortDir.ASC);

        ingestTypeList.addCheckListener(new CheckChangedListener<FilterAttribute>() {
            @Override
            public void checkChanged(CheckChangedEvent<FilterAttribute> event){
                FilterAttribute firstCheck = ingestTypeList.getCheckedSelection().get(0);
                ingestTypeListStore.remove(firstCheck);
                checkedIngestTypeListStore.add(firstCheck, false);
                canCheck = false;
                checkedIngestTypeList.setChecked(firstCheck,true);
                updateAttributeInfo(checkedIngestTypeList.getCheckedSelection());

                // Grid isn't filtered according to filters, so enable the filter button
                ((Button)Registry.get("startFilterButton")).enable();
            }
        });

        return ingestTypeList;
    }

    public void updateFilterData(){
        AsyncCallback<List<FilterAttribute>> callback = new AsyncCallback<List<FilterAttribute>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<FilterAttribute> result) {
                ingestTypeListStore.removeAll();
                ingestTypeListStore.add(result,false);
            }
        };
        List<FilterQuery> filterQueries = browseFilterPanel.getAllQueries();
        ((FilterServiceAsync)Registry.get(HarvesterUI.FILTER_SERVICE)).getIngestType(filterQueries,HarvesterUI.UTIL_MANAGER.getLoggedUserName(),callback);
    }

    private TreePanel createCheckedCountryList(){
        checkedIngestTypeListStore = new TreeStore<FilterAttribute>();

        checkedIngestTypeList = new TreePanel<FilterAttribute>(checkedIngestTypeListStore);
        checkedIngestTypeList.setCheckable(true);
        checkedIngestTypeList.setDisplayProperty("value");
        checkedIngestTypeListStore.sort("value",Style.SortDir.ASC);

        checkedIngestTypeList.addCheckListener(new CheckChangedListener<FilterAttribute>() {
            @Override
            public void checkChanged(CheckChangedEvent<FilterAttribute> event)
            {
                // variable to control the immediate check listener effect done when an item is added to this list
                if(canCheck)
                {
                    removeNonCheckedItems();
                    updateAttributeInfo(checkedIngestTypeList.getCheckedSelection());
                }
                canCheck = true;

                // Grid isn't filtered according to filters, so enable the filter button
                ((Button)Registry.get("startFilterButton")).enable();
            }
        });

        return checkedIngestTypeList;
    }

    public void reGetIngestTypes() {
        TreeStore<FilterAttribute> saveChecked = new TreeStore<FilterAttribute>();
        saveChecked.add(checkedIngestTypeListStore.getModels(),false);
        
        Menu rangeMenu = new Menu();
        rangeMenu.add(createCheckedCountryList());
        rangeMenu.add(new SeparatorMenuItem());
        rangeMenu.add(createCountryList());
        setMenu(rangeMenu);

        for(FilterAttribute FilterAttribute : ingestTypeListStore.getModels()) {
            for(FilterAttribute savedAttribute : saveChecked.getModels()) {
                if(FilterAttribute.getValue().equals(savedAttribute.getValue())) {
                    ingestTypeListStore.remove(FilterAttribute);
                    checkedIngestTypeListStore.add(FilterAttribute,false);
                    checkedIngestTypeList.setChecked(FilterAttribute,true);
                }
            }
        }

//        associatedAttribute.setRangeInfo("");
//        browseFilterPanel.getAttributesListStore().update(associatedAttribute);
    }

    private void removeNonCheckedItems(){
        // TODO: does exception due to gxt 2.2.0 version - fixed in 2.2.1
        List<FilterAttribute> valuesToRemove = new ArrayList<FilterAttribute>();

        for(FilterAttribute item : checkedIngestTypeListStore.getModels()){
            boolean result = false;

            if(checkedIngestTypeList.getCheckedSelection().size() <= 0){
                ingestTypeListStore.add(item,false);
                valuesToRemove.add(item);
            }else{
                for(FilterAttribute checkedItem : checkedIngestTypeList.getCheckedSelection()){
                    if(checkedItem.getValue().equals(item.getValue()))
                        result = true;
                }

                if(!result){
                    ingestTypeListStore.add(item,false);
                    valuesToRemove.add(item);
                }
            }
        }

        for(FilterAttribute item : valuesToRemove){
            checkedIngestTypeListStore.remove(item);
        }
    }

    private void updateAttributeInfo(List<FilterAttribute> selection){
        dataFilter.setRangeInfo(createInfoString(selection));
        // Update the attribute info value written on the store
        if(browseFilterPanel.getAttributesListStore().contains(dataFilter))
            browseFilterPanel.getAttributesListStore().update(dataFilter);
        else if(browseFilterPanel.getAttributesSelected().contains(dataFilter)){
            browseFilterPanel.getAttributesListStore().update(dataFilter);
            if(dataFilter.getChecked() == 1)
                Dispatcher.get().dispatch(AppEvents.LoadMainData);
        }
    }

    private String createInfoString(List<FilterAttribute> selection){
        String result = "";

        if(selection.size() > 0){
            if(selection.size() > 1)
                result = selection.size() + " selected";
            else{
                for(FilterAttribute country : selection){
                    result += country.getValue() + ";";
                }
            }
        }

        return result;
    }

    public FilterQuery getFilterQuery(){
        List<String> values = new ArrayList<String>();
        for(FilterAttribute filterAttribute : checkedIngestTypeList.getCheckedSelection()){
            values.add(filterAttribute.getValue());
        }
        return new FilterQuery(FilterType.INGEST_TYPE,values);
    }
}
