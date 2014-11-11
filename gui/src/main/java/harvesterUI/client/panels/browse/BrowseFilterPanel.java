/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 *
 * http://extjs.com/license
 */
package harvesterUI.client.panels.browse;

import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.models.DataFilter;
import harvesterUI.client.models.FilterButton;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.filters.FilterQueryLastIngest;
import harvesterUI.shared.filters.FilterQueryRecords;
import harvesterUI.shared.filters.FilterType;
import harvesterUI.shared.users.UserRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.utl.ist.util.shared.ProjectType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class BrowseFilterPanel extends ContentPanel {

    private ListStore<DataFilter> attributesListStore;
    private Grid<DataFilter> attributesGrid;
    private Button startFilterButton;

    public BrowseFilterPanel() {
        setLayout(new FitLayout());
        setLayoutOnChange(true);
        setHeading(HarvesterUI.CONSTANTS.filters());

        ToolBar toolBar = new ToolBar();
        startFilterButton = new Button(HarvesterUI.CONSTANTS.startFilter());
        startFilterButton.setIcon(HarvesterUI.ICONS.search_icon());
        startFilterButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent me) {
                applyFilter();
            }
        });
        Registry.register("startFilterButton",startFilterButton);

        Button resetFilter = new Button(HarvesterUI.CONSTANTS.resetFilter());
        resetFilter.setIcon(HarvesterUI.ICONS.delete());
        resetFilter.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent me) {
                attributesGrid.getSelectionModel().deselectAll();
                Dispatcher.get().dispatch(AppEvents.LoadMainData);
            }
        });

        startFilterButton.setEnabled(false);
        toolBar.add(startFilterButton);
        toolBar.add(resetFilter);

        toolBar.setAlignment(Style.HorizontalAlignment.CENTER);
        setTopComponent(toolBar);

        createMainGrid();
        setStoreData();
    }

    public void applyFilter(){
        Dispatcher.forwardEvent(AppEvents.LoadMainData);
    }

    private void sortList() {
        for(DataFilter attribute : attributesGrid.getSelectionModel().getSelectedItems())
            attribute.setChecked(1);

        for(DataFilter attribute : attributesListStore.getModels()) {
            if(!attributesGrid.getSelectionModel().getSelectedItems().contains(attribute))
                attribute.setChecked(0);
        }

        attributesListStore.sort("checked",Style.SortDir.DESC);
    }

    public void setStoreData() {
        attributesListStore.removeAll();
        DataFilter country = new DataFilter(HarvesterUI.CONSTANTS.country(),"country",new CountryFilter(this));
        DataFilter tagFilter = new DataFilter("Tag","tag",new TagsFilter(this));
        DataFilter records = new DataFilter(HarvesterUI.CONSTANTS.records(),"records",new RecordsFilter(this));

        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT) {
            DataFilter type = new DataFilter(HarvesterUI.CONSTANTS.type(),"type",new TypeFilter(this));
            attributesListStore.add(type);
        }
        DataFilter metadataFormat = new DataFilter(HarvesterUI.CONSTANTS.metadata(),"metadataFormat",new MetadataFormatFilter(this));
        DataFilter lastIngest = new DataFilter(HarvesterUI.CONSTANTS.lastIngest(),"lastIngest",new LastIngestFilter(this));
        attributesListStore.add(country);
        attributesListStore.add(metadataFormat);
        attributesListStore.add(records);
        attributesListStore.add(lastIngest);
        attributesListStore.add(tagFilter);

        sortList();
    }

    public void autoSelectFilter(FilterButton filterButton){
        attributesGrid.getSelectionModel().select(filterButton.getDataFilter(),true);
    }

    public List<DataFilter> getAttributesSelected() {
        return attributesGrid.getSelectionModel().getSelectedItems();
    }

    public ListStore<DataFilter> getAttributesListStore() { return attributesListStore;}

    private native String buildTemplate(String dateInterval, String timeInterval) /*-{
        var html = [
            '<dl>',
            '<dt><b>Date Interval:</b></dt>',
            '<dd> -> ',dateInterval,'</dd>',
            '<br />',
            '<dt><b>Time Interval:</b></dt>',
            '<dd> -> ',timeInterval,'</dd>',
            '</dl>'
        ];
        return html.join("");
    }-*/;

    private native String buildDateTemplate(String dateInterval) /*-{
        var html = [
            '<dl>',
            '<dt><b>Date Interval:</b></dt>',
            '<dd> -> ',dateInterval,'</dd>',
            '</dl>'
        ];
        return html.join("");
    }-*/;

    private void createMainGrid() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        CheckBoxSelectionModel<DataFilter> sm = new CheckBoxSelectionModel<DataFilter>();
        sm.setSelectionMode(Style.SelectionMode.SIMPLE);
        configs.add(sm.getColumn());

        ColumnConfig column = new ColumnConfig();
        column.setId("name");
        column.setWidth(50);
        column.setAlignment(Style.HorizontalAlignment.LEFT);
        configs.add(column);

        GridCellRenderer<DataFilter> infoRenderer = new GridCellRenderer<DataFilter>() {
            public Object render(final DataFilter model, String property, ColumnData config, final int rowIndex,
                                 final int colIndex, ListStore<DataFilter> store, Grid<DataFilter> grid) {
                if(model.getRangeInfo() != null) {
                    String rangeInfo = model.getRangeInfo();
                    if(model.getFilterButton() instanceof LastIngestFilter) {
                        // Value to change into tooltip button
                        if(rangeInfo.length() > 19) {
                            ToolTipConfig infoToolTip = new ToolTipConfig();
                            infoToolTip.setCloseable(true);
                            infoToolTip.setAnchor("right");
                            String delimIntervals = "[_]+";
                            String[] tokensLIInterval = model.getRangeInfo().split(delimIntervals);
                            // Case for which date range between 2 values and no time value
                            if(tokensLIInterval.length == 1)
                                infoToolTip.setTemplate(new Template(buildDateTemplate(tokensLIInterval[0])));
                            else
                                infoToolTip.setTemplate(new Template(buildTemplate(tokensLIInterval[0],tokensLIInterval[1])));

                            Button btn = new Button("Range Info");
                            btn.setToolTip(infoToolTip);
                            return btn;
                        }else
                            return rangeInfo;
                    }else
                        return rangeInfo;
                }else
                    return null;
            }
        };

        column = new ColumnConfig();
        column.setId("rangeInfo");
        column.setResizable(true);
        column.setWidth(50);
        column.setAlignment(Style.HorizontalAlignment.LEFT);
        column.setRenderer(infoRenderer);
        configs.add(column);

        GridCellRenderer<DataFilter> change = new GridCellRenderer<DataFilter>() {
            public Object render(DataFilter model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<DataFilter> store, Grid<DataFilter> grid){
                if(model.getFilterButton() != null){
                    return model.getFilterButton();
                }else
                    return null;
            }
        };

        column = new ColumnConfig();
        column.setId("date");
        column.setWidth(75);
        column.setAlignment(Style.HorizontalAlignment.RIGHT);
        column.setRenderer(change);
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);

        attributesListStore = new ListStore<DataFilter>();
        attributesGrid = new Grid<DataFilter>(attributesListStore,cm);
        attributesGrid.addPlugin(sm);
        attributesGrid.setSelectionModel(sm);
        attributesGrid.setHideHeaders(true);
        attributesGrid.getView().setForceFit(true);
        attributesGrid.setTrackMouseOver(false);

        attributesGrid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<DataFilter>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<DataFilter> event) {
                sortList();
                setStartFilterButtonState();
            }
        });

        BorderLayout layout = new BorderLayout();
        layout.setEnableState(false);
        setLayout(layout);

        BorderLayoutData topData = new BorderLayoutData(Style.LayoutRegion.CENTER,0.65f);
        topData.setMargins(new Margins(0, 0, 5, 0));

        add(attributesGrid, topData);
    }

    private void setStartFilterButtonState(){
        if(attributesGrid.getSelectionModel().getSelectedItems().size() <= 0)
            startFilterButton.setEnabled(false);
        else
            startFilterButton.setEnabled(true);
    }

    public void updateAllFilterValues(){
        for(DataFilter dataFilter : attributesListStore.getModels()){
            dataFilter.getFilterButton().updateFilterData();
        }
    }

    public List<FilterQuery> getAllQueries(){
        List<FilterQuery> filterQueries = new ArrayList<FilterQuery>();
        for(DataFilter dataFilter : attributesGrid.getSelectionModel().getSelectedItems()){
            if(dataFilter.getFilterButton() instanceof MetadataFormatFilter){
                FilterQuery filterQuery = dataFilter.getFilterButton().getFilterQuery();
                if(filterQuery.getValues() != null && filterQuery.getValues().size() > 0)
                    filterQueries.add(filterQuery);
                FilterQuery filterQueryTransformation = ((MetadataFormatFilter) dataFilter.getFilterButton()).getTransformationsFilterQuery();
                if(filterQueryTransformation.getValues() != null && filterQueryTransformation.getValues().size() > 0)
                    filterQueries.add(filterQueryTransformation);
            }else{
                FilterQuery filterQuery = dataFilter.getFilterButton().getFilterQuery();
                if(filterQuery.getValues() != null && filterQuery.getValues().size() > 0)
                    filterQueries.add(filterQuery);
                if(filterQuery instanceof FilterQueryRecords || filterQuery instanceof FilterQueryLastIngest)
                    filterQueries.add(filterQuery);
            }
        }
        return reorderResultQueriesByType(filterQueries);
    }

    // Data Provider filters must appear first on the list -> because of the filtering algorithm
    private List<FilterQuery> reorderResultQueriesByType(List<FilterQuery> filterQueries){
        for(int i=0; i<filterQueries.size() ; i++){
            FilterQuery filterQuery = filterQueries.get(i);
            if(filterQuery.getFilterType() == FilterType.COUNTRY)
                Collections.swap(filterQueries,i,0);
            else if(filterQuery.getFilterType() == FilterType.DP_TYPE)
                Collections.swap(filterQueries,i,0);
        }
        return filterQueries;
    }

    public boolean isFilterApplied(){
        return HarvesterUI.UTIL_MANAGER.getLoggedUserRole() == UserRole.DATA_PROVIDER || attributesGrid.getSelectionModel().getSelectedItems().size() > 0;
    }

    public void deselectFilter(DataFilter dataFilter){
        attributesGrid.getSelectionModel().deselect(dataFilter);
    }
}
