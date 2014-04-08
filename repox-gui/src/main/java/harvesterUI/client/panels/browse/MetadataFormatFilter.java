package harvesterUI.client.panels.browse;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.CheckChangedEvent;
import com.extjs.gxt.ui.client.event.CheckChangedListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
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
 * Time: 13:01
 */
public class MetadataFormatFilter extends FilterButton {

    // Metadata Source Formats
    private TreePanel<FilterAttribute> metadataFormatList;
    private TreeStore<FilterAttribute> metadataFormatListStore;
    private TreePanel<FilterAttribute> checkedMetadataFormatList;
    private TreeStore<FilterAttribute> checkedMetadataFormatListStore;
    private boolean canCheckMDSrcFormat;

    // Transformations 
    private TreePanel<FilterAttribute> transformationsList;
    private TreeStore<FilterAttribute> transformationsListStore;
    private TreePanel<FilterAttribute> checkedTransformationsList;
    private TreeStore<FilterAttribute> checkedTransformationsListStore;
    private boolean canCheckTransformations;

    public MetadataFormatFilter(BrowseFilterPanel browseFilterPanel) {
        super(browseFilterPanel);

        setText(HarvesterUI.CONSTANTS.data());

        Menu rangeMenu = new Menu();
        rangeMenu.add(createSrcMTDFormatMenu());
        rangeMenu.add(createTransformationsMenu());
        setMenu(rangeMenu);
    }

    private MenuItem createSrcMTDFormatMenu() {
        MenuItem mtdFormat = new MenuItem(HarvesterUI.CONSTANTS.sourceMetadataFormat());
        Menu mtdFormatMenu = new Menu();
        mtdFormatMenu.add(createCheckedMTDFormatSrcList());
        mtdFormatMenu.add(new SeparatorMenuItem());
        mtdFormatMenu.add(createMTDFormatSrcList());
        mtdFormat.setSubMenu(mtdFormatMenu);
        return mtdFormat;
    }

    private MenuItem createTransformationsMenu() {
        MenuItem transformationsItem = new MenuItem(HarvesterUI.CONSTANTS.transformations());
        Menu transformationsMenu = new Menu();
        transformationsMenu.add(createCheckedTransformationsList());
        transformationsMenu.add(new SeparatorMenuItem());
        transformationsMenu.add(createTransformationsList());
        transformationsItem.setSubMenu(transformationsMenu);
        return transformationsItem;
    }

    private TreePanel createMTDFormatSrcList(){
        metadataFormatListStore = new TreeStore<FilterAttribute>();

        metadataFormatList = new TreePanel<FilterAttribute>(metadataFormatListStore);
        metadataFormatList.setCheckable(true);
        metadataFormatList.setDisplayProperty("name");
        metadataFormatListStore.sort("name", Style.SortDir.ASC);

        metadataFormatList.addCheckListener(new CheckChangedListener<FilterAttribute>() {
            @Override
            public void checkChanged(CheckChangedEvent<FilterAttribute> event){
                FilterAttribute firstCheck = metadataFormatList.getCheckedSelection().get(0);
                metadataFormatListStore.remove(firstCheck);
                checkedMetadataFormatListStore.add(firstCheck, false);
                canCheckMDSrcFormat = false;
                checkedMetadataFormatList.setChecked(firstCheck,true);
                updateAttributeInfo(checkedMetadataFormatList.getCheckedSelection());

                ((BrowseFilterPanel)Registry.get("browseFilterPanel")).autoSelectFilter(getFilterButton());

                // Grid isn't filtered according to filters, so enable the filter button
                ((Button)Registry.get("startFilterButton")).enable();
            }
        });

        return metadataFormatList;
    }

    private void reGetMetadataFormats(TreeStore<FilterAttribute> saveCheckedMetadataFormat,List<FilterAttribute> result) {

        metadataFormatListStore.add(result,false);

        for(FilterAttribute originalAttribute : metadataFormatListStore.getModels()) {
            for(FilterAttribute savedAttribute : saveCheckedMetadataFormat.getModels()) {
                if(originalAttribute.getValue().equals(savedAttribute.getValue())) {
                    metadataFormatListStore.remove(originalAttribute);
                    checkedMetadataFormatListStore.add(originalAttribute,false);
                    checkedMetadataFormatList.setChecked(originalAttribute,true);
                }
            }
        }

        metadataFormatListStore.sort("value",Style.SortDir.ASC);
        checkedMetadataFormatListStore.sort("value",Style.SortDir.ASC);
    }

    private void reGetTransformations(TreeStore<FilterAttribute> saveCheckedTransformations,List<FilterAttribute> result){

        transformationsListStore.add(result,false);

        for(FilterAttribute FilterAttribute : transformationsListStore.getModels()) {
            for(FilterAttribute savedAttribute : saveCheckedTransformations.getModels()) {
                if(FilterAttribute.getValue().equals(savedAttribute.getValue())) {
                    transformationsListStore.remove(FilterAttribute);
                    checkedTransformationsListStore.add(FilterAttribute,false);
                    checkedTransformationsList.setChecked(FilterAttribute,true);
                }
            }
        }

        transformationsListStore.sort("value",Style.SortDir.ASC);
        checkedTransformationsListStore.sort("value",Style.SortDir.ASC);
    }

    public void updateFilterData(){
        final TreeStore<FilterAttribute> saveCheckedMetadataFormat = new TreeStore<FilterAttribute>();
        saveCheckedMetadataFormat.add(checkedMetadataFormatListStore.getModels(),false);

        final TreeStore<FilterAttribute> saveCheckedTransformations = new TreeStore<FilterAttribute>();
        saveCheckedTransformations.add(checkedTransformationsListStore.getModels(),false);

        Menu rangeMenu = new Menu();
        rangeMenu.add(createSrcMTDFormatMenu());
        rangeMenu.add(createTransformationsMenu());
        setMenu(rangeMenu);
        
        AsyncCallback<List<FilterAttribute>> callback = new AsyncCallback<List<FilterAttribute>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<FilterAttribute> result) {
                reGetMetadataFormats(saveCheckedMetadataFormat,result);
            }
        };
        List<FilterQuery> filterQueries = browseFilterPanel.getAllQueries();
        ((FilterServiceAsync)Registry.get(HarvesterUI.FILTER_SERVICE)).getMetadataFormats(filterQueries,HarvesterUI.UTIL_MANAGER.getLoggedUserName(),callback);

        callback = new AsyncCallback<List<FilterAttribute>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<FilterAttribute> result) {
                reGetTransformations(saveCheckedTransformations,result);
            }
        };
        ((FilterServiceAsync)Registry.get(HarvesterUI.FILTER_SERVICE)).getTransformations(filterQueries,HarvesterUI.UTIL_MANAGER.getLoggedUserName(),callback);
    }

    private TreePanel createCheckedMTDFormatSrcList(){
        checkedMetadataFormatListStore = new TreeStore<FilterAttribute>();

        checkedMetadataFormatList = new TreePanel<FilterAttribute>(checkedMetadataFormatListStore);
        checkedMetadataFormatList.setCheckable(true);
        checkedMetadataFormatList.setDisplayProperty("name");
        checkedMetadataFormatListStore.sort("name",Style.SortDir.ASC);

        checkedMetadataFormatList.addCheckListener(new CheckChangedListener<FilterAttribute>() {
            @Override
            public void checkChanged(CheckChangedEvent<FilterAttribute> event)
            {
                // variable to control the immediate check listener effect done when an item is added to this list
                if(canCheckMDSrcFormat)
                {
                    removeNonCheckedMTDSrcFormatItems();
                    updateAttributeInfo(checkedMetadataFormatList.getCheckedSelection());
                }
                canCheckMDSrcFormat = true;

                // Grid isn't filtered according to filters, so enable the filter button
                ((Button)Registry.get("startFilterButton")).enable();
            }
        });

        return checkedMetadataFormatList;
    }

    private TreePanel createTransformationsList() {
        transformationsListStore = new TreeStore<FilterAttribute>();

        // todo: fill store
//        transformationsListStore.add((List<FilterAttribute>)attributeManager.getEachFilterData().get("transformations"),false);
        transformationsList = new TreePanel<FilterAttribute>(transformationsListStore);
        transformationsList.setCheckable(true);
//        metadataFormatList.setIconProvider(iconProvider);
        transformationsList.setDisplayProperty("name");
        transformationsListStore.sort("name", Style.SortDir.ASC);

        transformationsList.addCheckListener(new CheckChangedListener<FilterAttribute>() {
            @Override
            public void checkChanged(CheckChangedEvent<FilterAttribute> event)
            {
                FilterAttribute firstCheck = transformationsList.getCheckedSelection().get(0);
                transformationsListStore.remove(firstCheck);
                checkedTransformationsListStore.add(firstCheck, false);
                canCheckTransformations = false;
                checkedTransformationsList.setChecked(firstCheck,true);
                updateAttributeInfo(checkedTransformationsList.getCheckedSelection());

                ((BrowseFilterPanel)Registry.get("browseFilterPanel")).autoSelectFilter(getFilterButton());

                // Grid isn't filtered according to filters, so enable the filter button
                ((Button)Registry.get("startFilterButton")).enable();
            }
        });

        return transformationsList;
    }

    private TreePanel createCheckedTransformationsList() {
        checkedTransformationsListStore = new TreeStore<FilterAttribute>();

        checkedTransformationsList = new TreePanel<FilterAttribute>(checkedTransformationsListStore);
        checkedTransformationsList.setCheckable(true);
        checkedTransformationsList.setDisplayProperty("name");
        checkedTransformationsListStore.sort("name",Style.SortDir.ASC);

        checkedTransformationsList.addCheckListener(new CheckChangedListener<FilterAttribute>() {
            @Override
            public void checkChanged(CheckChangedEvent<FilterAttribute> event)
            {
                // variable to control the immediate check listener effect done when an item is added to this list
                if(canCheckTransformations) {
                    removeNonCheckedTransformationItems();
                    updateAttributeInfo(checkedTransformationsList.getCheckedSelection());
                }
                canCheckTransformations = true;

                // Grid isn't filtered according to filters, so enable the filter button
                ((Button)Registry.get("startFilterButton")).enable();
            }
        });

        return checkedTransformationsList;
    }

    private void removeNonCheckedMTDSrcFormatItems(){
        List<FilterAttribute> valuesToRemove = new ArrayList<FilterAttribute>();

        for(FilterAttribute item : checkedMetadataFormatListStore.getModels()) {
            boolean result = false;
            if(checkedMetadataFormatList.getCheckedSelection().size() <= 0) {
                metadataFormatListStore.add(item,false);
                valuesToRemove.add(item);
            }else {
                for(FilterAttribute checkedItem : checkedMetadataFormatList.getCheckedSelection()) {
                    if(checkedItem.getValue().equals(item.getValue()))
                        result = true;
                }
                if(!result) {
                    metadataFormatListStore.add(item,false);
                    valuesToRemove.add(item);
                }
            }
        }
        for(FilterAttribute item : valuesToRemove)
            checkedMetadataFormatListStore.remove(item);
    }

    private void removeNonCheckedTransformationItems(){
        // TODO: does exception due to gxt 2.2.0 version - fixed in 2.2.1
        List<FilterAttribute> valuesToRemove = new ArrayList<FilterAttribute>();

        for(FilterAttribute item : checkedTransformationsListStore.getModels()) {
            boolean result = false;
            if(checkedTransformationsList.getCheckedSelection().size() <= 0) {
                transformationsListStore.add(item,false);
                valuesToRemove.add(item);
            }
            else {
                for(FilterAttribute checkedItem : checkedTransformationsList.getCheckedSelection()) {
                    if(checkedItem.getValue().equals(item.getValue()))
                        result = true;
                }
                if(!result) {
                    transformationsListStore.add(item,false);
                    valuesToRemove.add(item);
                }
            }
        }
        for(FilterAttribute item : valuesToRemove)
            checkedTransformationsListStore.remove(item);
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
        for(FilterAttribute filterAttribute : checkedMetadataFormatList.getCheckedSelection()){
            values.add(filterAttribute.getValue());
        }
        return new FilterQuery(FilterType.METADATA_FORMAT,values);
    }

    public FilterQuery getTransformationsFilterQuery(){
        List<String> values = new ArrayList<String>();
        for(FilterAttribute filterAttribute : checkedTransformationsList.getCheckedSelection()){
            values.add(filterAttribute.getValue());
        }
        return new FilterQuery(FilterType.TRANSFORMATION,values);
    }
}
