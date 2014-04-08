///*
// * Ext GWT 2.2.1 - Ext for GWT
// * Copyright(c) 2007-2010, Ext JS, LLC.
// * licensing@extjs.com
// *
// * http://extjs.com/license
// */
//package harvesterUI.client.panels.overviewGrid;
//
//import com.extjs.gxt.ui.client.Registry;
//import com.extjs.gxt.ui.client.Style;
//import com.extjs.gxt.ui.client.data.*;
//import com.extjs.gxt.ui.client.event.BorderLayoutEvent;
//import com.extjs.gxt.ui.client.event.ButtonEvent;
//import com.extjs.gxt.ui.client.event.Events;
//import com.extjs.gxt.ui.client.event.Listener;
//import com.extjs.gxt.ui.client.mvc.Dispatcher;
//import com.extjs.gxt.ui.client.store.ListStore;
//import com.extjs.gxt.ui.client.store.Store;
//import com.extjs.gxt.ui.client.store.StoreSorter;
//import com.extjs.gxt.ui.client.store.TreeStore;
//import com.extjs.gxt.ui.client.util.DefaultComparator;
//import com.extjs.gxt.ui.client.util.Margins;
//import com.extjs.gxt.ui.client.widget.ContentPanel;
//import com.extjs.gxt.ui.client.widget.button.Button;
//import com.extjs.gxt.ui.client.widget.button.ToggleButton;
//import com.extjs.gxt.ui.client.widget.form.ComboBox;
//import com.extjs.gxt.ui.client.widget.grid.*;
//import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
//import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
//import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
//import com.extjs.gxt.ui.client.widget.menu.Menu;
//import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
//import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
//import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
//import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
//import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
//import com.google.gwt.user.client.Command;
//import com.google.gwt.user.client.DeferredCommand;
//import com.google.gwt.user.client.Element;
//import com.google.gwt.user.client.Timer;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import harvesterUI.client.HarvesterUI;
//import harvesterUI.client.core.AppEvents;
//import harvesterUI.client.models.FilterAttribute;
//import harvesterUI.client.panels.browse.BrowseFilterPanel;
//import harvesterUI.client.panels.dataProviderButtons.CreateDataProviderButton;
//import harvesterUI.client.panels.overviewGrid.columnRenderes.*;
//import harvesterUI.client.servlets.dataManagement.DataManagementServiceAsync;
//import harvesterUI.client.servlets.harvest.HarvestOperationsServiceAsync;
//import harvesterUI.client.util.ServerExceptionDialog;
//import harvesterUI.client.util.UtilManager;
//import harvesterUI.shared.dataTypes.DataProviderUI;
//import harvesterUI.shared.tasks.RunningTask;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class OverviewGrid extends ContentPanel {
//
//    private TreeStore<ModelData> store;
//    private ToolBar toolBar;
//    private BrowseFilterPanel browseFilterPanel;
//
//    private MainDataManager mainDataManager;
////    private MyPagingToolBar pagingToolBar;
////    private PagingModelMemoryProxy proxy;
//    private ToggleButton dss;
//
//    private ToggleButton autoRefreshButton;
//    private ComboBox<FilterAttribute> searchCombo;
//
//    private Button viewButton;
//    private ContentPanel viewDsInfoPanel;
////    private MainDataGrid mainDataGrid;
//    private ColumnConfig countryColumn;
//    private BorderLayout mainBorderLayout;
//
//    private DataManagementServiceAsync service;
//
//    public OverviewGrid() {
//        mainBorderLayout = new BorderLayout();
//        setLayout(mainBorderLayout);
//        setHeaderVisible(false);
//
//        mainDataManager = (MainDataManager) Registry.get("mainDataManager");
//        browseFilterPanel = (BrowseFilterPanel) Registry.get("browseFilterPanel");
//
//        service = (DataManagementServiceAsync) Registry.get(HarvesterUI.DATA_MANAGEMENT_SERVICE);
//
//        createViewDSInfoPanel();
//        createOverviewGrid();
//        createFiltersPanel();
//        createPagingControls();
//    }
//
//    public void makeLightChanges() {
//        viewButton.getMenu().getItems().remove(viewButton.getMenu().getItemByItemId("aggs"));
//        ((ToggleButton)viewButton.getMenu().getItemByItemId("dps")).toggle();
//    }
//
//    public void makeEuDMLChanges() {
//        viewButton.getMenu().getItems().remove(viewButton.getMenu().getItemByItemId("aggs"));
//        ((ToggleButton)viewButton.getMenu().getItemByItemId("dps")).toggle();
//    }
//
//    public void makeEuropeanaChanges() {
//        // Special case for anonymous entry in europeana project type
//        if(HarvesterUI.UTIL_MANAGER.getLoggedUserName().equals(HarvesterUI.CONSTANTS.anonymous())){
////            mainDataGrid.removeAllListeners();
//        }
//    }
//
//    public void createPagingControls() {
//
////        countryColumn.setHidden(false);
////        tree.getView().refresh(true);
////        if(HarvesterUI.getProjectType().equals("LIGHT") || HarvesterUI.getProjectType() == ProjectType.EUDML) {
////            ((ToggleButton)viewButton.getMenu().getItemByItemId("dps")).toggle(true);
////            viewButton.getMenu().getItemByItemId("dps").disable();
////            ((ToggleButton)viewButton.getMenu().getItemByItemId("dss")).toggle(false);
////            viewButton.getMenu().getItemByItemId("dss").enable();
////
////            proxy = new PagingModelMemoryProxy(mainDataManager.getMainDataTel().getDataProviderUIList());
////            PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
////            pagingToolBar.bind(loader);
////        }
////        else {
////            ((ToggleButton)viewButton.getMenu().getItemByItemId("aggs")).toggle(true);
////            viewButton.getMenu().getItemByItemId("aggs").disable();
////            ((ToggleButton)viewButton.getMenu().getItemByItemId("dps")).toggle(false);
////            viewButton.getMenu().getItemByItemId("dps").enable();
////            ((ToggleButton)viewButton.getMenu().getItemByItemId("dss")).toggle(false);
////            viewButton.getMenu().getItemByItemId("dss").enable();
////
////            proxy = new PagingModelMemoryProxy(mainDataManager.getMainDataEuropeana().getDataProviderUIList());
////            PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
////            pagingToolBar.bind(loader);
////        }
////
////        AsyncCallback<User> callback = new AsyncCallback<User>() {
////            public void onFailure(Throwable caught) {
////                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
////            }
////            public void onSuccess(User user) {
////                // Anonymous user
////                if(user == null)
////                    pagingToolBar.getPerPageCombo().setValue(pagingToolBar.getPerPageCombo().getStore().getAt(1));
////                else{
////                    for(SimpleComboValue<String> value : pagingToolBar.getPerPageCombo().getStore().getModels()){
////                        if(value.getValue().equals(String.valueOf(user.getPageSize()))){
////                            pagingToolBar.getPerPageCombo().setValue(value);
////                            break;
////                        }
////                    }
////                }
////
////                // Don't unmask when reloading the main grid
////                if(!History.getToken().equals("HOME"))
////                    UtilManager.unmaskCentralPanel();
////            }
////        };
////        UserManagementServiceAsync service = (UserManagementServiceAsync) Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);
////        service.getUser(HarvesterUI.UTIL_MANAGER.getLoggedUserName(),callback);
////
////        // Add data to the search combo
////        searchCombo.addSelectionChangedListener(new SelectionChangedListener<FilterAttribute>() {
////            @Override
////            public void selectionChanged(SelectionChangedEvent<FilterAttribute> se) {
////                store.removeAll();
////                store.add(mainDataManager.getModelFromSearchBox(se.getSelectedItem()).getChildren(), true);
////                proxy.setData(store.getModels());
////                pagingToolBar.doLoadRequest(pagingToolBar.getStart(), pagingToolBar.getPageSize());
////            }
////        });
////        searchCombo.getStore().add(mainDataManager.getSearchComboData());
////        modifyFiltering(searchCombo);
//    }
//
//    // modify the specified Combo to filter anywhere, not just at the start.
////    private void modifyFiltering(final ComboBox<FilterAttribute> combo) {
////        StoreFilter<FilterAttribute> filter = new StoreFilter<FilterAttribute>() {
////            public boolean select(Store<FilterAttribute> store, FilterAttribute parent,
////                                  FilterAttribute item, String property) {
////                String v = combo.getRawValue();
////                if (v == null || v.equals(""))
////                    return true;
////                return item != null && item.getAttributeName() != null &&
////                        item.getAttributeName().toLowerCase().indexOf(v.toLowerCase()) >= 0;
////            }
////        };
////        // have to override the filter method
////        ListStore<FilterAttribute> store = new ListStore<FilterAttribute>() {
////            @Override
////            public void filter(String property, String beginsWith) {
////                // ignore the beginsWith string
////                super.filter(property);
////            }
////        };
////        store.addFilter(filter);
////        store.add(combo.getStore().getModels());
////        combo.setStore(store);
////    }
//
//    public void refreshTree(final boolean reset) {
//        // When we are showing Data Sets only
////        if(!dss.isEnabled()) {
////            tree.getTreeView().refresh(true);
////            UtilManager.unmaskCentralPanel();
////        } else {
////        AsyncCallback<User> callback = new AsyncCallback<User>() {
////            public void onFailure(Throwable caught) {
////                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
////            }
////            public void onSuccess(User user) {
////                // Anonymous user
////                if(user == null)
////                    pagingToolBar.getPerPageCombo().setValue(pagingToolBar.getPerPageCombo().getStore().getAt(1));
////                else{
////                    for(SimpleComboValue<String> value : pagingToolBar.getPerPageCombo().getStore().getModels()){
////                        if(value.getValue().equals(String.valueOf(user.getPageSize()))){
////                            pagingToolBar.getPerPageCombo().setValue(value);
////                            break;
////                        }
////                    }
////                }
////                pagingToolBar.doLoadRequest(pagingToolBar.getStart(), pagingToolBar.getPageSize());
////                createStoreData(pagingToolBar.getStart(), pagingToolBar.getPageSize(),reset);
////            }
////        };
////        UserManagementServiceAsync service = (UserManagementServiceAsync) Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);
////        service.getUser(HarvesterUI.UTIL_MANAGER.getLoggedUserName(),callback);
////        }
//
//        // Update data to the search combo
////        ComboBox<FilterAttribute> searchCombo = (ComboBox<FilterAttribute>)Registry.get("searchCombo");
////        searchCombo.getStore().removeAll();
////        searchCombo.getStore().add(mainDataManager.getSearchComboData());
//    }
//
//    public void createStoreData(final int start, final int interval,final boolean reset) {
////        mainDataGrid.setScrollBarY();
////        DeferredCommand.addCommand(new Command() {
////            public void execute() {
//////                if(mainDataManager.getTreeModel().getChildCount() == 0 || browseFilterPanel.getAttributesSelected().size() > 0 || reset) {
////                store.removeAll();
////                mainDataManager.createTreeModel(start, interval);
////                store.add(mainDataManager.getTreeModel().getChildren(), true);
//////                }
//////                else
//////                    getTreeGrid().getView().refresh(false);
////
////                if(mainDataManager.getSortByDate())
////                    store.sort("usedDate", Style.SortDir.DESC);
//////                else
//////                    store.sort("name",Style.SortDir.ASC);
////                mainDataGrid.expandAll();
////            }
////        });
////
////        DeferredCommand.addCommand(new Command() {
////            public void execute() {
////                mainDataGrid.selectPreviouslySelectedItem();
////                getMainDataGrid().resetScrollBarPos();
////                UtilManager.unmaskCentralPanel();
////            }
////        });
//    }
//
////    public ToolBar createPagingToolbar() {
////        pagingToolBar = new MyPagingToolBar(10);
////        Registry.register("pagingToolBar", pagingToolBar);
////        return pagingToolBar;
////    }
//
//    public void loadFilterAttributes() {
////        LayoutContainer wrapper = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
////        if(!wrapper.isMasked()) {
////            UtilManager.maskCentralPanel(HarvesterUI.CONSTANTS.loadingFilter());
////        }
////        DeferredCommand.addCommand(new Command() {
////            public void execute() {
////                if(mainDataManager != null && proxy != null) {
////                    proxy.setData(mainDataManager.getFilteredDataDPs());
////                    mainDataManager.showOnlyFilteredData(browseFilterPanel.getAttributesSelected(),store);
////                }
////            }
////        });
//    }
//
//    private void createOverviewGrid() {
//        createTopToolbar();
//
//        RpcProxy<PagingLoadResult<BaseTreeModel>> proxy = new RpcProxy<PagingLoadResult<BaseTreeModel>>() {
//            @Override
//            public void load(Object loadConfig, AsyncCallback<PagingLoadResult<BaseTreeModel>> callback) {
////                service.getMainData((PagingLoadConfig) loadConfig, callback);
//            }
//        };
//
//        // loader
//        PagingLoader<PagingLoadResult<BaseTreeModel>> pagingLoader = new BasePagingLoader<PagingLoadResult<BaseTreeModel>>(
//                proxy);
//        pagingLoader.setRemoteSort(true);
//
//        store = new TreeStore<ModelData>();
//
//        PagingToolBar toolBar = new PagingToolBar(50);
//        toolBar.bind(pagingLoader);
//
//        setBottomComponent(toolBar);
//
//        ArrayList<ColumnConfig> columns = new ArrayList<ColumnConfig>();
//
//        CheckBoxSelectionModel<ModelData> mainGridSelectionModel = new CheckBoxSelectionModel<ModelData>();
//        mainGridSelectionModel.setSelectionMode(Style.SelectionMode.MULTI);
//        columns.add(mainGridSelectionModel.getColumn());
//
//        countryColumn = new ColumnConfig("country", HarvesterUI.ICONS.worldmap().getHTML(), 35);
//        countryColumn.setAlignment(Style.HorizontalAlignment.CENTER);
//
//        countryColumn.setRenderer(new GridCellRenderer<ModelData>() {
//            public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
//                                 ListStore<ModelData> store, Grid<ModelData> grid) {
//                if(model instanceof DataProviderUI) {
//                    if(model.get("country") != null && !model.get("country").equals("none")){
//                        return "<img src=\"resources/images/countries/" +
//                                model.get("country") + ".png\" alt=\"" + model.get("countryName") + "\" title=\"" +
//                                model.get("countryName") + "\"/> ";
//                    }
//                }
//                return "";
//            }
//        });
//        columns.add(countryColumn);
//
//        ColumnConfig name = new ColumnConfig("name", "<CENTER>"+HarvesterUI.CONSTANTS.name()+"</CENTER>", 100);
//        name.setRenderer(new TreeGridCellRenderer<ModelData>());
//        name.setWidth(260);
//        columns.add(name);
//
//        // Europeana column only
//        if(HarvesterUI.getProjectType() == ProjectType.EUROPEANA) {
//            ColumnConfig nameCode = new ColumnConfig("nameCode", "<CENTER>"+HarvesterUI.CONSTANTS.nameCodeHeader()+"</CENTER>", 100);
//            columns.add(nameCode);
//        }
//
//        ColumnConfig dataSourceSet = new ColumnConfig("dataSourceSet", "<CENTER>"+HarvesterUI.CONSTANTS.dataSetHeader()+"</CENTER>",100);
//        columns.add(dataSourceSet);
//
//        ColumnConfig metadataFormat = new ColumnConfig("metadataFormat", "<CENTER>"+HarvesterUI.CONSTANTS.oaiSchemasHeader()+"</CENTER>", 100);
//        metadataFormat.setWidth(110);
//        columns.add(metadataFormat);
//
//        ColumnConfig ingest = new ColumnConfig("ingest", "<CENTER>"+HarvesterUI.CONSTANTS.ingestTypeHeader()+"</CENTER>", 150);
//        ingest.setRenderer(new IngestTypeRenderer());
//        columns.add(ingest);
//        ColumnConfig lastIngest = new ColumnConfig("lastIngest", "<CENTER>"+HarvesterUI.CONSTANTS.lastIngestHeader()+"</CENTER>", 100);
//        lastIngest.setWidth(130);
//        lastIngest.setAlignment(Style.HorizontalAlignment.CENTER);
//        lastIngest.setRenderer(new LastIngestRenderer());
//        columns.add(lastIngest);
//        ColumnConfig nextIngest = new ColumnConfig("nextIngest", "<CENTER>"+HarvesterUI.CONSTANTS.nextIngestHeader()+"</CENTER>", 100);
//        nextIngest.setRenderer(new NextIngestRenderer());
//        nextIngest.setAlignment(Style.HorizontalAlignment.CENTER);
//        nextIngest.setWidth(130);
//        columns.add(nextIngest);
//        ColumnConfig records = new ColumnConfig("records", "<CENTER>"+HarvesterUI.CONSTANTS.records()+"</CENTER>", 100);
//        records.setAlignment(Style.HorizontalAlignment.RIGHT);
//        records.setRenderer(new RecordsRenderer());
//        columns.add(records);
//        ColumnConfig ingestStatus = new ColumnConfig("status", "<CENTER>"+HarvesterUI.CONSTANTS.ingestStatusHeader()+"</CENTER>", 100);
//        ingestStatus.setRenderer(new IngestStatusRenderer());
//        ingestStatus.setAlignment(Style.HorizontalAlignment.CENTER);
//        columns.add(ingestStatus);
//
//        ColumnModel cm = new ColumnModel(columns);
//
////        UtilManager.maskCentralPanel(HarvesterUI.CONSTANTS.loadingMainData());
//        BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.CENTER);
////        mainDataGrid = new MainDataGrid(store,cm,toolBar,mainGridSelectionModel, viewDsInfoPanel,pagingLoader);
////        add(mainDataGrid,data);
//
////        setBottomComponent(createPagingToolbar());
//
//        store.setStoreSorter(new StoreSorter<ModelData>() {
//            @Override
//            public int compare(Store<ModelData> store, ModelData m1, ModelData m2, String property) {
//                return chooseComparator(store,m1,m2,property);
//            }
//        });
//
////        StoreFilterField<ModelData> filter2 = new StoreFilterField<ModelData>() {
////
////            @Override
////            protected boolean doSelect(Store<ModelData> store, ModelData parent,
////                                       ModelData record, String property, String filter) {
////                // only match leaf nodes
//////                if (record instanceof DataProviderUI) {
//////                    return false;
//////                }
////                String childId = record.get("name");
////                childId = childId.toLowerCase();
////                return childId.contains(filter.toLowerCase());
////            }
////        };
////        filter2.bind(store);
////        toolBar.add(filter2);
//    }
//
//    private void createTopToolbar(){
//        // Operations toolbar on grid
//        toolBar = new ToolBar();
//        toolBar.setSpacing(0);
//
//        new CreateDataProviderButton(toolBar);
//
//        viewButton = new Button(HarvesterUI.CONSTANTS.view());
//        viewButton.setIcon(HarvesterUI.ICONS.view_filter_icon());
//        Menu viewMenu = new Menu();
//        viewButton.setId("view");
//        viewButton.setMenu(viewMenu);
//        toolBar.add(viewButton);
//        final ToggleButton  aggs = new ToggleButton (HarvesterUI.CONSTANTS.aggregators(), HarvesterUI.ICONS.form());
//        aggs.setId("aggs");
//        final ToggleButton dps = new ToggleButton(HarvesterUI.CONSTANTS.dataProviders(), HarvesterUI.ICONS.form());
//        dps.setId("dps");
//        dss = new ToggleButton(HarvesterUI.CONSTANTS.dataSets(), HarvesterUI.ICONS.form());
//        dss.setId("dss");
//        aggs.toggle();
//        aggs.addListener(Events.OnClick, new Listener<ButtonEvent>() {
//            public void handleEvent(ButtonEvent ce) {
//                dps.toggle(false);
//                dps.enable();
//                dss.toggle(false);
//                dss.enable();
//                aggs.toggle(true);
//                aggs.disable();
//                showAggregators();
//            }
//        });
//        dps.addListener(Events.OnClick, new Listener<ButtonEvent>() {
//            public void handleEvent(ButtonEvent ce) {
//                aggs.toggle(false);
//                aggs.enable();
//                dss.toggle(false);
//                dss.enable();
//                dps.toggle(true);
//                dps.disable();
//                showDataProviders();
//            }
//        });
//        dss.addListener(Events.OnClick, new Listener<ButtonEvent>() {
//            public void handleEvent(ButtonEvent ce) {
//                aggs.toggle(false);
//                aggs.enable();
//                dps.toggle(false);
//                dps.enable();
//                dss.toggle(true);
//                dss.disable();
//                showDataSources();
//            }
//        });
//
//        viewMenu.add(aggs);
//        viewMenu.add(dps);
//        viewMenu.add(dss);
//
//        autoRefreshButton = new ToggleButton(HarvesterUI.CONSTANTS.autoRefresh(), HarvesterUI.ICONS.accordion());
//        autoRefreshButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
//            public void handleEvent(ButtonEvent ce) {
//                toggleRefreshCycle();
//            }
//        });
//        toolBar.add(autoRefreshButton);
//        toolBar.add(new FillToolItem());
//        toolBar.add(new LabelToolItem(HarvesterUI.ICONS.search_icon().getHTML()));
//        toolBar.add(createFilterCombo());
//
//        setTopComponent(toolBar);
//    }
//
//    private void createFiltersPanel(){
//        BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.WEST, 300, 300, 350);
//        data.setMargins(new Margins(1, 5, 1, 1));
//        data.setCollapsible(true);
//
//        add(browseFilterPanel, data);
//    }
//
//    private void createViewDSInfoPanel(){
//        BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.EAST, 475, 100, 750);
//        data.setMargins(new Margins(1, 1, 1, 5));
//        data.setCollapsible(true);
//
//        viewDsInfoPanel = new ContentPanel();
//        viewDsInfoPanel.setLayout(new CenterLayout());
//        viewDsInfoPanel.setLayoutOnChange(true);
////        filterPanel.setAnimCollapse(true);
//        viewDsInfoPanel.setId("eastPanel");
//        viewDsInfoPanel.setBodyBorder(false);
//        viewDsInfoPanel.setIcon(HarvesterUI.ICONS.view_info_icon());
//        viewDsInfoPanel.setHeading(HarvesterUI.CONSTANTS.viewDataSetInformation());
//        LabelToolItem noDsSelectedLabel = new LabelToolItem(HarvesterUI.CONSTANTS.noDataSetSelected());
//        noDsSelectedLabel.setStyleName("noDataSetSelected");
//        viewDsInfoPanel.add(noDsSelectedLabel);
//
//        mainBorderLayout.addListener(Events.Expand, new Listener<BorderLayoutEvent>() {
//            @Override
//            public void handleEvent(BorderLayoutEvent be) {
////                if (be.getRegion().equals(Style.LayoutRegion.EAST))
////                    mainDataGrid.setOnExpandDataSet();
//            }
//        });
//
//        add(viewDsInfoPanel, data);
//    }
//
//    public void toggleRefreshCycle() {
//        Timer t = new Timer() {
//            public void run() {
//                if(autoRefreshButton.isPressed()) {
//                    checkRunningTasks();
////                    mainDataGrid.setScrollBarY();
//                    Dispatcher.get().dispatch(AppEvents.AutoRefreshData);
//                    schedule(5000);
//                }
//            }
//        };
//
//        if(autoRefreshButton.isPressed())
//            t.schedule(3000);
//    }
//
//    private void checkRunningTasks() {
//        AsyncCallback<List<RunningTask>> callback = new AsyncCallback<List<RunningTask>>() {
//            public void onFailure(Throwable caught) {
//                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
//            }
//            public void onSuccess(List<RunningTask> result) {
//                if(result.size() == 0)
//                    autoRefreshButton.toggle(false);
//            }
//        };
//        HarvestOperationsServiceAsync service = (HarvestOperationsServiceAsync) Registry.get(HarvesterUI.HARVEST_OPERATIONS_SERVICE);
//        service.getAllRunningTasks(callback);
//    }
//
//    private ComboBox<FilterAttribute> createFilterCombo() {
//        ListStore<FilterAttribute> searchComboStore = new ListStore<FilterAttribute>();
//
//        searchCombo = new ComboBox<FilterAttribute>();
//        Registry.register("searchCombo",searchCombo);
//        searchCombo.setEmptyText(HarvesterUI.CONSTANTS.goTo());
//        searchCombo.setDisplayField("attributeName");
//        searchCombo.setWidth(200);
//        searchCombo.setStore(searchComboStore);
//        searchCombo.setTypeAhead(true);
//        searchCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
//        searchCombo.setMinListWidth(300);
//        searchCombo.setTemplate(getTemplate());
////        modifyFiltering(searchCombo);
////        searchComboStore.addFilter(new StoreFilter<FilterAttribute>() {
////            public boolean select(Store<FilterAttribute> store, FilterAttribute parent,
////                                  FilterAttribute item, String property) {
////                for(ModelData model : store.getModels()) {
////                    String v = searchCombo.getRawValue();
////                    String name = ((FilterAttribute)model).getAttributeName();
////                    name = name.toLowerCase();
////                    return name.contains(v.toLowerCase());
////                }
////                return false;
////            }
////        });
////        searchCombo.setStore(searchComboStore);
//
////        toolBar.insert(new SeparatorToolItem(), toolBar.getItems().indexOf(toolBar.getItemByItemId("helpButton")) + 1);
////        toolBar.insert(searchCombo, toolBar.getItems().indexOf(toolBar.getItemByItemId("helpButton")) + 2);
//        return searchCombo;
//    }
//
//    private native String getTemplate() /*-{
//        return  [
//            '<tpl for=".">',
//            '<div class="x-combo-list-item">',
////            '<tpl if="{[values.value]} == \'DP\'">',
////            '{[values.attributeName]}',
////            '</tpl>',
//            '{[values.value]} - {[values.attributeName]}',
//            '</div>',
//            '</tpl>'
//        ].join("");
//    }-*/;
//
//    private void showAggregators() {
////        UtilManager.maskCentralPanel(HarvesterUI.CONSTANTS.loadingMainData());
//        DeferredCommand.addCommand(new Command() {
//            public void execute() {
//                countryColumn.setHidden(false);
////                mainDataGrid.getView().getHeader().refresh();
//
//                mainDataManager.showOnlyAggregators(store);
//                store.add(mainDataManager.getTreeModel().getChildren(), true);
//                store.sort("name",Style.SortDir.ASC);
////                mainDataGrid.expandAll();
//            }
//        });
//
//        DeferredCommand.addCommand(new Command() {
//            public void execute() {
//                UtilManager.unmaskCentralPanel();
//            }
//        });
//    }
//
//    private void showDataProviders() {
////        UtilManager.maskCentralPanel(HarvesterUI.CONSTANTS.loadingMainData());
//        DeferredCommand.addCommand(new Command() {
//            public void execute() {
//                countryColumn.setHidden(false);
////                mainDataGrid.getView().getHeader().refresh();
//
//                mainDataManager.showOnlyDataProviders(store);
//                store.add(mainDataManager.getTreeModel().getChildren(), true);
//                store.sort("name",Style.SortDir.ASC);
////                mainDataGrid.expandAll();
//            }
//        });
//
//        DeferredCommand.addCommand(new Command() {
//            public void execute() {
//                UtilManager.unmaskCentralPanel();
//            }
//        });
//    }
//
//    private void showDataSources() {
////        UtilManager.maskCentralPanel(HarvesterUI.CONSTANTS.loadingMainData());
//        DeferredCommand.addCommand(new Command() {
//            public void execute() {
//                countryColumn.setHidden(true);
////                mainDataGrid.getView().getHeader().refresh();
//                mainDataManager.showOnlyDataSources(store);
//                store.removeAll();
//                store.add(mainDataManager.getTreeModel().getChildren(), true);
////                mainDataGrid.expandAll();
//            }
//        });
//
//        DeferredCommand.addCommand(new Command() {
//            public void execute() {
//                UtilManager.unmaskCentralPanel();
//            }
//        });
//    }
//
//    private int chooseComparator(Store<ModelData> store, ModelData m1, ModelData m2, String property){
//        if (property != null){
//            if(property.equals("records")){
//                if (m1 instanceof DataProviderUI && m2 instanceof DataProviderUI) {
//                    DataProviderUI dp1 = (DataProviderUI) m1;
//                    DataProviderUI dp2 = (DataProviderUI) m2;
//                    int r1, r2;
//
//                    if(dp1.getDataSourceUIList().size() == 1 && dp2.getDataSourceUIList().size() == 1) {
//                        if(dp1.getDataSourceUIList().get(0).get(property) == null)
//                            r1 = 0;
//                        else
//                            r1 = Integer.parseInt(((String)dp1.getDataSourceUIList().get(0).get(property)).replace(".", ""));
//
//                        if(dp2.getDataSourceUIList().get(0).get(property) == null)
//                            r2 = 0;
//                        else
//                            r2 = Integer.parseInt(((String)dp2.getDataSourceUIList().get(0).get(property)).replace(".", ""));
//
//                        if(r1 > r2)
//                            return 1;
//                        else if(r1 < r2)
//                            return -1;
//                        else
//                            return 0;
//                    }
//                    else if(dp1.getChildCount() > 0 && dp2.getChildCount() > 0){
//                        if(dp1.getChild(0).get(property) == null)
//                            r1 = 0;
//                        else
//                            r1 = Integer.parseInt(((String)dp1.getChild(0).get(property)).replace(".", ""));
//
//                        if(dp2.getChild(0).get(property) == null)
//                            r2 = 0;
//                        else
//                            r2 = Integer.parseInt(((String)dp2.getChild(0).get(property)).replace(".", ""));
//
//                        if(r1 > r2)
//                            return 1;
//                        else if(r1 < r2)
//                            return -1;
//                        else
//                            return 0;
//                    }
//                }
//                return 0;
//            } else {
//                Object v1 = m1.get(property);
//                Object v2 = m2.get(property);
//                DefaultComparator<Object> compr = new DefaultComparator<Object>();
//                return compr.compare(v1, v2);
//            }
//        }
//        return 0;
//    }
//
//    public TreeStore<ModelData> getStore(){
//        return store;
//    }
//
////    public TreeGrid<ModelData> getTreeGrid(){
////        return mainDataGrid;
////    }
//
//    public ContentPanel getContentPanel(){
//        return this;
//    }
//
//    public MyPagingToolBar getPagingToolBar() {
//        return null;
//    }
//
//    public BrowseFilterPanel getBrowseFilterPanel() {
//        return browseFilterPanel;
//    }
//
//    public ComboBox<FilterAttribute> getSearchCombo() {
//        return searchCombo;
//    }
//
////    public MainDataGrid getMainDataGrid() {
////        return mainDataGrid;
////    }
//
//    @Override
//    protected void onResize(int width, int height) {
//        super.onResize(width,height);
//        super.layout(true);
////        mainDataGrid.repaint();
////        mainDataGrid.getView().refresh(false);
//        layout(true);
//    }
//
//    @Override
//    protected void onRender(Element parent, int index) {
//        super.onRender(parent, index);
//        DeferredCommand.addCommand(new Command() {
//            public void execute() {
//                browseFilterPanel.setExpanded(false);
//                viewDsInfoPanel.setExpanded(false);
//            }
//        });
//    }
//}