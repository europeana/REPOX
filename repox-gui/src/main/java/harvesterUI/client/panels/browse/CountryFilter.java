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
 * Date: 09-03-2011
 * Time: 11:50
 */
public class CountryFilter extends FilterButton {

    private TreePanel<FilterAttribute> countryList;
    private TreePanel<FilterAttribute> checkedCountryList;
    private boolean canCheck;

    public CountryFilter(BrowseFilterPanel browseFilterPanel) {
        super(browseFilterPanel);
        setText(HarvesterUI.CONSTANTS.countries());

        Menu rangeMenu = new Menu();
        rangeMenu.add(createCheckedCountryList());
        rangeMenu.add(new SeparatorMenuItem());
        rangeMenu.add(createCountryList());

        setMenu(rangeMenu);
    }

    private TreePanel createCountryList(){
        TreeStore<FilterAttribute> countryListStore = new TreeStore<FilterAttribute>();

        countryList = new TreePanel<FilterAttribute>(countryListStore);
        countryList.setCheckable(true);
        countryList.setDisplayProperty("name");
        countryListStore.sort("name", Style.SortDir.ASC);

        countryList.addCheckListener(new CheckChangedListener<FilterAttribute>() {
            @Override
            public void checkChanged(CheckChangedEvent<FilterAttribute> event){
                FilterAttribute firstCheck = countryList.getCheckedSelection().get(0);
                countryList.getStore().remove(firstCheck);
                checkedCountryList.getStore().add(firstCheck, false);
                canCheck = false;
                checkedCountryList.setChecked(firstCheck,true);
                updateAttributeInfo(checkedCountryList.getCheckedSelection());

                ((BrowseFilterPanel)Registry.get("browseFilterPanel")).autoSelectFilter(getFilterButton());

                // Grid isn't filtered according to filters, so enable the filter button
                ((Button)Registry.get("startFilterButton")).enable();
            }
        });

        return countryList;
    }

    private TreePanel createCheckedCountryList(){
        TreeStore<FilterAttribute> checkedCountryListStore = new TreeStore<FilterAttribute>();

        checkedCountryList = new TreePanel<FilterAttribute>(checkedCountryListStore);
        checkedCountryList.setCheckable(true);
        checkedCountryList.setDisplayProperty("name");
        checkedCountryListStore.sort("name",Style.SortDir.ASC);

        checkedCountryList.addCheckListener(new CheckChangedListener<FilterAttribute>() {
            @Override
            public void checkChanged(CheckChangedEvent<FilterAttribute> event){
                // variable to control the immediate check listener effect done when an item is added to this list
                if(canCheck) {
                    removeNonCheckedItems();
                    updateAttributeInfo(checkedCountryList.getCheckedSelection());
                }
                canCheck = true;

                // Grid isn't filtered according to filters, so enable the filter button
                ((Button)Registry.get("startFilterButton")).enable();
            }
        });

        return checkedCountryList;
    }

    public void updateFilterData(){
        final TreeStore<FilterAttribute> saveChecked = new TreeStore<FilterAttribute>();
        saveChecked.add(checkedCountryList.getStore().getModels(),false);
        
        AsyncCallback<List<FilterAttribute>> callback = new AsyncCallback<List<FilterAttribute>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<FilterAttribute> result) {
                Menu rangeMenu = new Menu();
                rangeMenu.add(createCheckedCountryList());
                rangeMenu.add(new SeparatorMenuItem());
                rangeMenu.add(createCountryList());
                setMenu(rangeMenu);

                countryList.getStore().add(result,false);

                for(FilterAttribute filterAttribute : countryList.getStore().getModels()) {
                    for(FilterAttribute savedAttribute : saveChecked.getModels()) {
                        if(filterAttribute.getValue().equals(savedAttribute.getValue())) {
                            countryList.getStore().remove(filterAttribute);
                            checkedCountryList.getStore().add(filterAttribute,false);
                            checkedCountryList.setChecked(filterAttribute,true);
                        }
                    }
                }
            }
        };
        List<FilterQuery> filterQueries = browseFilterPanel.getAllQueries();
        ((FilterServiceAsync)Registry.get(HarvesterUI.FILTER_SERVICE)).getCountries(filterQueries,HarvesterUI.UTIL_MANAGER.getLoggedUserName(),callback);
    }

    private void removeNonCheckedItems(){
        List<FilterAttribute> valuesToRemove = new ArrayList<FilterAttribute>();

        for(FilterAttribute item : checkedCountryList.getStore().getModels()){
            boolean result = false;

            if(checkedCountryList.getCheckedSelection().size() <= 0){
                countryList.getStore().add(item,false);
                valuesToRemove.add(item);
            }else{
                for(FilterAttribute checkedItem : checkedCountryList.getCheckedSelection()){
                    if(checkedItem.getValue().equals(item.getValue()))
                        result = true;
                }

                if(!result){
                    countryList.getStore().add(item,false);
                    valuesToRemove.add(item);
                }
            }
        }

        for(FilterAttribute item : valuesToRemove){
            checkedCountryList.getStore().remove(item);
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
        for(FilterAttribute filterAttribute : checkedCountryList.getCheckedSelection()){
            values.add(filterAttribute.getValue());
        }
        return new FilterQuery(FilterType.COUNTRY,values);
    }
}
