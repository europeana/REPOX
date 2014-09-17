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
public class TagsFilter extends FilterButton {

    private TreePanel<FilterAttribute> tagsList;
    private TreePanel<FilterAttribute> checkedTagsList;
    private boolean canCheck;

    public TagsFilter(BrowseFilterPanel browseFilterPanel) {
        super(browseFilterPanel);
        setText("Tags");

        Menu rangeMenu = new Menu();
        rangeMenu.add(createCheckedTagsList());
        rangeMenu.add(new SeparatorMenuItem());
        rangeMenu.add(createTagsList());

        setMenu(rangeMenu);
    }

    private TreePanel createTagsList(){
        TreeStore<FilterAttribute> countryListStore = new TreeStore<FilterAttribute>();

        tagsList = new TreePanel<FilterAttribute>(countryListStore);
        tagsList.setCheckable(true);
        tagsList.setDisplayProperty("name");
        countryListStore.sort("name", Style.SortDir.ASC);

        tagsList.addCheckListener(new CheckChangedListener<FilterAttribute>() {
            @Override
            public void checkChanged(CheckChangedEvent<FilterAttribute> event) {
                FilterAttribute firstCheck = tagsList.getCheckedSelection().get(0);
                tagsList.getStore().remove(firstCheck);
                checkedTagsList.getStore().add(firstCheck, false);
                canCheck = false;
                checkedTagsList.setChecked(firstCheck, true);
                updateAttributeInfo(checkedTagsList.getCheckedSelection());

                ((BrowseFilterPanel)Registry.get("browseFilterPanel")).autoSelectFilter(getFilterButton());

                // Grid isn't filtered according to filters, so enable the filter button
                ((Button) Registry.get("startFilterButton")).enable();
            }
        });

        return tagsList;
    }

    private TreePanel createCheckedTagsList(){
        TreeStore<FilterAttribute> checkedTagsListStore = new TreeStore<FilterAttribute>();

        checkedTagsList = new TreePanel<FilterAttribute>(checkedTagsListStore);
        checkedTagsList.setCheckable(true);
        checkedTagsList.setDisplayProperty("name");
        checkedTagsListStore.sort("name", Style.SortDir.ASC);

        checkedTagsList.addCheckListener(new CheckChangedListener<FilterAttribute>() {
            @Override
            public void checkChanged(CheckChangedEvent<FilterAttribute> event) {
                // variable to control the immediate check listener effect done when an item is added to this list
                if (canCheck) {
                    removeNonCheckedItems();
                    updateAttributeInfo(checkedTagsList.getCheckedSelection());
                }
                canCheck = true;

                // Grid isn't filtered according to filters, so enable the filter button
                ((Button) Registry.get("startFilterButton")).enable();
            }
        });

        return checkedTagsList;
    }

    public void updateFilterData(){
        final TreeStore<FilterAttribute> saveChecked = new TreeStore<FilterAttribute>();
        saveChecked.add(checkedTagsList.getStore().getModels(),false);

        AsyncCallback<List<FilterAttribute>> callback = new AsyncCallback<List<FilterAttribute>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<FilterAttribute> result) {
                Menu rangeMenu = new Menu();
                rangeMenu.add(createCheckedTagsList());
                rangeMenu.add(new SeparatorMenuItem());
                rangeMenu.add(createTagsList());
                setMenu(rangeMenu);

                tagsList.getStore().add(result,false);

                for(FilterAttribute filterAttribute : tagsList.getStore().getModels()) {
                    for(FilterAttribute savedAttribute : saveChecked.getModels()) {
                        if(filterAttribute.getValue().equals(savedAttribute.getValue())) {
                            tagsList.getStore().remove(filterAttribute);
                            checkedTagsList.getStore().add(filterAttribute,false);
                            checkedTagsList.setChecked(filterAttribute, true);
                        }
                    }
                }

                updateAttributeInfo(checkedTagsList.getCheckedSelection());
            }
        };
        List<FilterQuery> filterQueries = browseFilterPanel.getAllQueries();
        ((FilterServiceAsync)Registry.get(HarvesterUI.FILTER_SERVICE)).getTags(filterQueries,HarvesterUI.UTIL_MANAGER.getLoggedUserName(),callback);
    }

    private void removeNonCheckedItems(){
        List<FilterAttribute> valuesToRemove = new ArrayList<FilterAttribute>();

        for(FilterAttribute item : checkedTagsList.getStore().getModels()){
            boolean result = false;

            if(checkedTagsList.getCheckedSelection().size() <= 0){
                tagsList.getStore().add(item,false);
                valuesToRemove.add(item);
            }else{
                for(FilterAttribute checkedItem : checkedTagsList.getCheckedSelection()){
                    if(checkedItem.getValue().equals(item.getValue()))
                        result = true;
                }

                if(!result){
                    tagsList.getStore().add(item,false);
                    valuesToRemove.add(item);
                }
            }
        }

        for(FilterAttribute item : valuesToRemove){
            checkedTagsList.getStore().remove(item);
        }
    }

    private void updateAttributeInfo(List<FilterAttribute> selection){
        dataFilter.setRangeInfo(createInfoString(selection));
        if(selection.size() == 0 && browseFilterPanel.getAttributesSelected().contains(dataFilter)){
            browseFilterPanel.deselectFilter(dataFilter);
            Dispatcher.forwardEvent(AppEvents.LoadMainData);
            return;
        }
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
        for(FilterAttribute filterAttribute : checkedTagsList.getCheckedSelection()){
            values.add(filterAttribute.getValue());
        }
        return new FilterQuery(FilterType.TAG,values);
    }
}
