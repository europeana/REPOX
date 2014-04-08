package harvesterUI.client.panels.browse;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.CheckChangedEvent;
import com.extjs.gxt.ui.client.event.CheckChangedListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
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
 * Date: 26-04-2011
 * Time: 13:57
 */
public class TypeFilter extends FilterButton {

    private TreePanel<FilterAttribute> typeList;
    private TreeStore<FilterAttribute> typeListStore;
    private TreePanel<FilterAttribute> checkedTypeList;
    private TreeStore<FilterAttribute> checkedTypeListStore;
    private boolean canCheck;

    private ModelIconProvider<FilterAttribute> iconProvider;

    public TypeFilter(BrowseFilterPanel browseFilterPanel) {
        super(browseFilterPanel);

        iconProvider = new ModelIconProvider<FilterAttribute>(){
            public AbstractImagePrototype getIcon(FilterAttribute attribute) {
                return HarvesterUI.ICONS.album();
            }
        };

        setText(HarvesterUI.CONSTANTS.types());

        Menu rangeMenu = new Menu();
        rangeMenu.add(createCheckedTypeList());
        rangeMenu.add(new SeparatorMenuItem());
        rangeMenu.add(createTypeList());
        setMenu(rangeMenu);
    }

    private TreePanel createTypeList(){
        typeListStore = new TreeStore<FilterAttribute>();
        typeList = new TreePanel<FilterAttribute>(typeListStore);
        typeList.setCheckable(true);
        typeList.setIconProvider(iconProvider);
        typeList.setDisplayProperty("value");
        typeListStore.sort("value", Style.SortDir.ASC);

        typeList.addCheckListener(new CheckChangedListener<FilterAttribute>() {
            @Override
            public void checkChanged(CheckChangedEvent<FilterAttribute> event)
            {
                FilterAttribute firstCheck = typeList.getCheckedSelection().get(0);
                typeListStore.remove(firstCheck);
                checkedTypeListStore.add(firstCheck, false);
                canCheck = false;
                checkedTypeList.setChecked(firstCheck,true);
                updateAttributeInfo(checkedTypeList.getCheckedSelection());

                // Grid isn't filtered according to filters, so enable the filter button
                ((Button)Registry.get("startFilterButton")).enable();
            }
        });

        return typeList;
    }

    public void reGetTypes(TreeStore<FilterAttribute> saveChecked, List<FilterAttribute> result) {
        Menu rangeMenu = new Menu();
        rangeMenu.add(createCheckedTypeList());
        rangeMenu.add(new SeparatorMenuItem());
        rangeMenu.add(createTypeList());
        setMenu(rangeMenu);

        typeListStore.add(result,false);

        for(FilterAttribute FilterAttribute : typeListStore.getModels()) {
            for(FilterAttribute savedAttribute : saveChecked.getModels()) {
                if(FilterAttribute.getValue().equals(savedAttribute.getValue())) {
                    typeListStore.remove(FilterAttribute);
                    checkedTypeListStore.add(FilterAttribute,false);
                    checkedTypeList.setChecked(FilterAttribute,true);
                }
            }
        }
    }

    public void updateFilterData(){
        final TreeStore<FilterAttribute> saveChecked = new TreeStore<FilterAttribute>();
        saveChecked.add(checkedTypeListStore.getModels(),false);

        AsyncCallback<List<FilterAttribute>> callback = new AsyncCallback<List<FilterAttribute>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<FilterAttribute> result) {
                reGetTypes(saveChecked,result);
            }
        };
        List<FilterQuery> filterQueries = browseFilterPanel.getAllQueries();
        ((FilterServiceAsync)Registry.get(HarvesterUI.FILTER_SERVICE)).getDataProviderTypes(filterQueries,HarvesterUI.UTIL_MANAGER.getLoggedUserName(),callback);
    }

    private TreePanel createCheckedTypeList(){
        checkedTypeListStore = new TreeStore<FilterAttribute>();

        checkedTypeList = new TreePanel<FilterAttribute>(checkedTypeListStore);
        checkedTypeList.setIconProvider(iconProvider);
        checkedTypeList.setCheckable(true);
        checkedTypeList.setDisplayProperty("value");
        checkedTypeListStore.sort("value",Style.SortDir.ASC);

        checkedTypeList.addCheckListener(new CheckChangedListener<FilterAttribute>() {
            @Override
            public void checkChanged(CheckChangedEvent<FilterAttribute> event){
                // variable to control the immediate check listener effect done when an item is added to this list
                if(canCheck){
                    removeNonCheckedItems();
                    updateAttributeInfo(checkedTypeList.getCheckedSelection());
                }
                canCheck = true;

                // Grid isn't filtered according to filters, so enable the filter button
                ((Button)Registry.get("startFilterButton")).enable();
            }
        });

        return checkedTypeList;
    }

    private void removeNonCheckedItems(){
        // TODO: does exception due to gxt 2.2.0 version - fixed in 2.2.1
        List<FilterAttribute> valuesToRemove = new ArrayList<FilterAttribute>();

        for(FilterAttribute item : checkedTypeListStore.getModels()){
            boolean result = false;

            if(checkedTypeList.getCheckedSelection().size() <= 0){
                typeListStore.add(item,false);
                valuesToRemove.add(item);
            }else{
                for(FilterAttribute checkedItem : checkedTypeList.getCheckedSelection()){
                    if(checkedItem.getValue().equals(item.getValue()))
                        result = true;
                }

                if(!result){
                    typeListStore.add(item,false);
                    valuesToRemove.add(item);
                }
            }
        }

        for(FilterAttribute item : valuesToRemove){
            checkedTypeListStore.remove(item);
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
//                    FilterAttribute country = (FilterAttribute) countryMD;
                    result += country.getValue() + ";";
                }
            }
        }
        return result;
    }

    public FilterQuery getFilterQuery(){
        List<String> values = new ArrayList<String>();
        for(FilterAttribute filterAttribute : checkedTypeList.getCheckedSelection()){
            values.add(filterAttribute.getValue());
        }
        return new FilterQuery(FilterType.DP_TYPE,values);
    }
}
