package harvesterUI.client.panels.overviewGrid;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridView;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.browse.BrowseFilterPanel;
import harvesterUI.client.panels.dataSourceView.DataSetViewInfo;
import harvesterUI.client.panels.grid.DataGridContainer;
import harvesterUI.client.panels.overviewGrid.columnRenderes.*;
import harvesterUI.client.panels.overviewGrid.contextMenus.OverviewGridContextMenus;
import harvesterUI.client.servlets.dataManagement.DataManagementServiceAsync;
import harvesterUI.client.servlets.dataManagement.FilterServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.ProjectType;
import harvesterUI.shared.dataTypes.AggregatorUI;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.filters.FilterQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 23-03-2012
 * Time: 15:16
 */
public class MainGrid extends DataGridContainer<DataContainer> {

    public static final String PAGING_TOOLBAR = "mainGridPagingToolbar";

    private ContentPanel mainGridPanel;
    private CheckBoxSelectionModel mainGridSelectionModel;
    private TreeStore<ModelData> store;
    private MyPagingToolBar pagingToolBar;
    private DataManagementServiceAsync dataManagementService;

    private BrowseFilterPanel browseFilterPanel;

    private ContentPanel viewDsInfoPanel;
    private ColumnConfig countryColumn;
    private BorderLayout mainBorderLayout;
    private MainGridTopToolbar topToolbar;

    private int selectedItemIndex = -1;
    private int currentPage = 1;

    private OverviewGridDataSetOperations overviewGridDataSetOperations;
    private OverviewGridAggregatorOperations overviewGridAggregatorOperations;
    private OverviewGridDataProviderOperations overviewGridDataProviderOperations;

    public MainGrid() {
        dataManagementService = (DataManagementServiceAsync) Registry.get(HarvesterUI.DATA_MANAGEMENT_SERVICE);

        createMainPanel();
        createMainGrid();
        createPagingToolbar();
        createTopToolbar();
        createFiltersPanel();
    }

    private void createMainGrid(){
        store = new TreeStore<ModelData>();

        mainDataGrid = new TreeGrid<DataContainer>(store,createColumnModel()){
            @Override
            protected void onRender(Element parent, int index) {
                super.onRender(parent, index);
                if(HarvesterUI.UTIL_MANAGER.getLoggedUserName().equals(HarvesterUI.CONSTANTS.anonymous())){
                    mainDataGrid.removeAllListeners();
                }

                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        browseFilterPanel.setExpanded(false);
                        viewDsInfoPanel.setExpanded(false);
                    }
                });
            }
        };

        mainDataGrid.setAutoExpand(true);
//        mainDataGrid.getTreeView().setBufferEnabled(false);
        mainDataGrid.setTrackMouseOver(false);
        mainDataGrid.setExpandOnFilter(true);
        mainDataGrid.setId("MainDataGrid01");
//        mainDataGrid.setStateId("MainDataGridStateID01");
        mainDataGrid.setColumnLines(true);
        mainDataGrid.getView().setEmptyText(HarvesterUI.CONSTANTS.noDataAvailable());
        mainDataGrid.setStripeRows(true);
//        mainDataGrid.setStateful(true);
        mainDataGrid.setColumnReordering(true);
        mainDataGrid.disableTextSelection(false);
        mainDataGrid.setSelectionModel(mainGridSelectionModel);
        mainDataGrid.addPlugin(mainGridSelectionModel);
        mainDataGrid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<DataContainer>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<DataContainer> se) {
                if(se.getSelectedItem() != null) {
                    selectedItemIndex = mainDataGrid.getStore().indexOf(se.getSelectedItem());
                    if(se.getSelectedItem() instanceof AggregatorUI) {
                        overviewGridAggregatorOperations.showButtons(topToolbar);
                        overviewGridDataSetOperations.hideButtons(topToolbar);
                        overviewGridDataProviderOperations.hideButtons(topToolbar);
                    }else if(se.getSelectedItem() instanceof DataProviderUI) {
                        DataProviderUI dataProviderUI = (DataProviderUI) se.getSelectedItem();
                        if(dataProviderUI.getDataSourceUIList().size() == 1){
                            overviewGridDataSetOperations.showButtons(topToolbar);
                            overviewGridDataProviderOperations.showButtons(topToolbar);
                            overviewGridAggregatorOperations.hideButtons(topToolbar);
                            editPanelInfo(dataProviderUI.getDataSourceUIList().get(0));
                        } else {
                            overviewGridDataProviderOperations.showButtons(topToolbar);
                            overviewGridDataSetOperations.hideButtons(topToolbar);
                            overviewGridAggregatorOperations.hideButtons(topToolbar);
                        }
                    }else if(se.getSelectedItem() instanceof DataSourceUI) {
                        overviewGridDataSetOperations.showButtons(topToolbar);
                        overviewGridAggregatorOperations.hideButtons(topToolbar);
                        overviewGridDataProviderOperations.hideButtons(topToolbar);
                        editPanelInfo((DataSourceUI) se.getSelectedItem());
                    }
                } else {
                    emptyDataSetPanel();
                    overviewGridDataSetOperations.hideButtons(topToolbar);
                    overviewGridAggregatorOperations.hideButtons(topToolbar);
                    overviewGridDataProviderOperations.hideButtons(topToolbar);
                }
            }
        });

//        mainDataGrid.addListener(Events.Attach, new Listener<GridEvent<BaseTreeModel>>() {
//            public void handleEvent(GridEvent<BaseTreeModel> be) {
//                reloadMainData();
//            }
//        });

        // Comparator
        final MainGridDefaultComparator mainGridDefaultComparator = new MainGridDefaultComparator();
        store.setStoreSorter(new StoreSorter<ModelData>() {
            @Override
            public int compare(Store<ModelData> store, ModelData m1, ModelData m2, String property) {
                return mainGridDefaultComparator.compareMainGridItem(store,m1,m2,property);
            }
        });

        TreeGridView mainGridViewConfig = new TreeGridView();
        mainGridViewConfig.setViewConfig(new GridViewConfig() {
            @Override
            public String getRowStyle(ModelData model, int rowIndex, ListStore<ModelData> ds) {
                if (model instanceof AggregatorUI) {
                    return "AGGREGATOR_STYLE";
                } else if (model instanceof DataProviderUI) {
                    if (((DataProviderUI) model).getDataSourceUIList().size() == 1) {
                        return "DP_SINGLE_DS_STYLE";
                    } else if (((DataProviderUI) model).getDataSourceUIList().size() > 1)
                        return "DP_MANY_DS_STYLE";
                    return "WHITE";
                }
                return "WHITE";
            }
        });
        mainGridViewConfig.setForceFit(true);
        mainDataGrid.setView(mainGridViewConfig);

        mainDataGrid.setIconProvider(new ModelIconProvider<DataContainer>() {
            public AbstractImagePrototype getIcon(DataContainer model) {
                if (model instanceof AggregatorUI) {
                    return HarvesterUI.ICONS.aggregator_icon();
                }else if (model instanceof DataProviderUI) {
                    return HarvesterUI.ICONS.data_provider_icon();
                }else if (model instanceof DataSourceUI) {
                    return HarvesterUI.ICONS.data_set_icon();
                }
                return null;
            }
        });

        OverviewGridContextMenus overviewGridContextMenus = new OverviewGridContextMenus(mainDataGrid);
        overviewGridContextMenus.createTreeContextMenu();

        overviewGridDataSetOperations = new OverviewGridDataSetOperations(mainDataGrid);
        overviewGridAggregatorOperations = new OverviewGridAggregatorOperations(mainDataGrid);
        overviewGridDataProviderOperations = new OverviewGridDataProviderOperations(mainDataGrid);

        BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.CENTER);
        mainGridPanel.add(mainDataGrid, data);
    }

    private ColumnModel createColumnModel(){
        ArrayList<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        mainGridSelectionModel = new CheckBoxSelectionModel<ModelData>();
        mainGridSelectionModel.setSelectionMode(Style.SelectionMode.MULTI);
        columns.add(mainGridSelectionModel.getColumn());

        countryColumn = new ColumnConfig("country", HarvesterUI.ICONS.worldmap().getHTML(), 35);
        countryColumn.setAlignment(Style.HorizontalAlignment.CENTER);

        countryColumn.setRenderer(new GridCellRenderer<ModelData>() {
            public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<ModelData> store, Grid<ModelData> grid) {
                if(model instanceof DataProviderUI) {
                    if(model.get("country") != null && !model.get("country").equals("none")){
                        return "<img src=\"resources/images/countries/" +
                                model.get("country") + ".png\" alt=\"" + model.get("countryName") + "\" title=\"" +
                                model.get("countryName") + "\"/> ";
                    }
                }
                return "";
            }
        });
        columns.add(countryColumn);

        ColumnConfig name = new ColumnConfig("name", "<CENTER>"+HarvesterUI.CONSTANTS.name()+"</CENTER>", 100);
        name.setRenderer(new TreeGridCellRenderer<ModelData>());
        name.setWidth(260);
        columns.add(name);

        // Europeana column only
        if(HarvesterUI.getProjectType() == ProjectType.EUROPEANA) {
            ColumnConfig nameCode = new ColumnConfig("nameCode", "<CENTER>"+HarvesterUI.CONSTANTS.nameCodeHeader()+"</CENTER>", 100);
            columns.add(nameCode);
        }

        ColumnConfig dataSourceSet = new ColumnConfig("dataSourceSet", "<CENTER>"+HarvesterUI.CONSTANTS.dataSetHeader()+"</CENTER>",100);
        columns.add(dataSourceSet);

        ColumnConfig metadataFormat = new ColumnConfig("metadataFormat", "<CENTER>"+HarvesterUI.CONSTANTS.oaiSchemasHeader()+"</CENTER>", 100);
        metadataFormat.setWidth(110);
        metadataFormat.setRenderer(new OaiSchemasRenderer());
        columns.add(metadataFormat);

        ColumnConfig ingest = new ColumnConfig("ingest", "<CENTER>"+HarvesterUI.CONSTANTS.ingestTypeHeader()+"</CENTER>", 150);
        ingest.setRenderer(new IngestTypeRenderer());
        columns.add(ingest);
        ColumnConfig lastIngest = new ColumnConfig("lastIngest", "<CENTER>"+HarvesterUI.CONSTANTS.lastIngestHeader()+"</CENTER>", 100);
        lastIngest.setWidth(130);
        lastIngest.setAlignment(Style.HorizontalAlignment.CENTER);
        lastIngest.setRenderer(new LastIngestRenderer());
        columns.add(lastIngest);
        ColumnConfig nextIngest = new ColumnConfig("nextIngest", "<CENTER>"+HarvesterUI.CONSTANTS.nextIngestHeader()+"</CENTER>", 100);
        nextIngest.setRenderer(new NextIngestRenderer());
        nextIngest.setAlignment(Style.HorizontalAlignment.CENTER);
        nextIngest.setWidth(130);
        columns.add(nextIngest);
        ColumnConfig records = new ColumnConfig("records", "<CENTER>"+HarvesterUI.CONSTANTS.records()+"</CENTER>", 100);
        records.setAlignment(Style.HorizontalAlignment.RIGHT);
        records.setRenderer(new RecordsRenderer());
        columns.add(records);
        ColumnConfig ingestStatus = new ColumnConfig("status", "<CENTER>"+HarvesterUI.CONSTANTS.ingestStatusHeader()+"</CENTER>", 100);
        ingestStatus.setRenderer(new IngestStatusRenderer());
        ingestStatus.setAlignment(Style.HorizontalAlignment.CENTER);
        columns.add(ingestStatus);

        return new ColumnModel(columns);
    }

//    public void initMainGridData(){
//        PagingLoadConfig config = new BasePagingLoadConfig();
//        config.setOffset(0);
//        config.setLimit(Integer.valueOf(pagingToolBar.getPerPageCombo().getSimpleValue()));
//
//        Map<String, Object> state = mainDataGrid.getState();
//        if (state.containsKey("offset")) {
//            int offset = (Integer) state.get("offset");
//            int limit = (Integer) state.get("limit");
//            config.setOffset(offset);
//            config.setLimit(limit);
//        }
//        if (state.containsKey("sortField")) {
//            config.setSortField((String) state.get("sortField"));
//            config.setSortDir(Style.SortDir.valueOf((String) state.get("sortDir")));
//        }
//        pagingToolBar.load(config);
//    }

    @Override
    public void loadGridData(PagingLoadConfig config){
        List<FilterQuery> filterQueries = browseFilterPanel.getAllQueries();

        UtilManager.maskCentralPanel(HarvesterUI.CONSTANTS.loadingMainData());
        getPagingToolBar().showRefreshIconRunning(true);
        AsyncCallback<DataContainer> callback = new AsyncCallback<DataContainer>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(DataContainer mainDataParent) {
                store.removeAll();
                store.add(mainDataParent.getChildren(),true);

                mainDataGrid.expandAll();

                selectPreviouslySelectedItem();
                resetScrollBarPos();

                getPagingToolBar().loadPagingInfo();
                getPagingToolBar().showRefreshIconRunning(false);
                UtilManager.unmaskCentralPanel();
            }
        };
        FilterServiceAsync service = (FilterServiceAsync) Registry.get(HarvesterUI.FILTER_SERVICE);
        service.getFilteredData(filterQueries,config.getOffset(),config.getLimit(),HarvesterUI.UTIL_MANAGER.getLoggedUserName(), callback);
    }

    public void refreshGrid(){
        pagingToolBar.refresh();
    }

    private void createPagingToolbar(){
        RpcProxy<PagingLoadResult<DataContainer>> proxy = new RpcProxy<PagingLoadResult<DataContainer>>() {
            @Override
            public void load(Object loadConfig, AsyncCallback<PagingLoadResult<DataContainer>> callback) {
                PagingLoadConfig pagingLoadConfig = (PagingLoadConfig) loadConfig;
                pagingLoadConfig.set("isFiltered",browseFilterPanel.isFilterApplied());
                dataManagementService.getPagingData((PagingLoadConfig) loadConfig,callback);
            }
        };

        pagingToolBar = new MyPagingToolBar(50, this);
        Registry.register(PAGING_TOOLBAR,pagingToolBar);

        PagingLoader<PagingLoadResult<DataProviderUI>> loader = new BasePagingLoader<PagingLoadResult<DataProviderUI>>(proxy);
        loader.setRemoteSort(true);
        pagingToolBar.bind(loader);
        mainGridPanel.setBottomComponent(pagingToolBar);
    }

    private void createMainPanel(){
        mainGridPanel = new ContentPanel(){
            @Override
            protected void onResize(int width, int height) {
                super.onResize(width,height);
                mainGridPanel.layout(true);
                mainDataGrid.repaint();
                mainDataGrid.getView().refresh(false);
            }
        };

        mainBorderLayout = new BorderLayout();
        mainGridPanel.setLayout(mainBorderLayout);
        mainGridPanel.setHeaderVisible(false);

        browseFilterPanel = (BrowseFilterPanel) Registry.get("browseFilterPanel");

        createViewDSInfoPanel();
    }

    private void createViewDSInfoPanel(){
        BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.EAST, 475, 100, 750);
        data.setMargins(new Margins(1, 1, 1, 5));
        data.setCollapsible(true);

        viewDsInfoPanel = new ContentPanel();
        viewDsInfoPanel.setLayout(new CenterLayout());
        viewDsInfoPanel.setLayoutOnChange(true);
//        filterPanel.setAnimCollapse(true);
        viewDsInfoPanel.setId("eastPanel");
        viewDsInfoPanel.setBodyBorder(false);
        viewDsInfoPanel.setIcon(HarvesterUI.ICONS.view_info_icon());
        viewDsInfoPanel.setHeading(HarvesterUI.CONSTANTS.viewDataSetInformation());
        LabelToolItem noDsSelectedLabel = new LabelToolItem(HarvesterUI.CONSTANTS.noDataSetSelected());
        noDsSelectedLabel.setStyleName("noDataSetSelected");
        viewDsInfoPanel.add(noDsSelectedLabel);

        mainBorderLayout.addListener(Events.Expand, new Listener<BorderLayoutEvent>() {
            @Override
            public void handleEvent(BorderLayoutEvent be) {
                if (be.getRegion().equals(Style.LayoutRegion.EAST))
                    setOnExpandDataSet();
            }
        });

        mainGridPanel.add(viewDsInfoPanel, data);
    }

    private void createTopToolbar(){
        topToolbar = new MainGridTopToolbar(this);
        mainGridPanel.setTopComponent(topToolbar);
    }

    private void createFiltersPanel(){
        BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.WEST, 300, 300, 350);
        data.setMargins(new Margins(1, 5, 1, 1));
        data.setCollapsible(true);

        mainGridPanel.add(browseFilterPanel, data);
    }


    /*********************************************************
     View Info Side Panel Functions
     **********************************************************/
    private void editPanelInfo(DataSourceUI dataSourceUI){
        // Only do when visible to enhance performance
        if(viewDsInfoPanel.isExpanded()){
            DataSetViewInfo dataSetViewInfo = new DataSetViewInfo();
            dataSetViewInfo.createForm(dataSourceUI);
            viewDsInfoPanel.removeAll();
            viewDsInfoPanel.setLayout(new FitLayout());
            viewDsInfoPanel.add(dataSetViewInfo);
            viewDsInfoPanel.setHeading(dataSetViewInfo.getHeading());
        }
    }

    private void emptyDataSetPanel(){
        // Only do when visible to enhance performance
        if(viewDsInfoPanel.isExpanded()){
            viewDsInfoPanel.removeAll();
            LabelToolItem noDsSelectedLabel = new LabelToolItem(HarvesterUI.CONSTANTS.noDataSetSelected());
            noDsSelectedLabel.setStyleName("noDataSetSelected");
            viewDsInfoPanel.setLayout(new CenterLayout());
            viewDsInfoPanel.setHeading(HarvesterUI.CONSTANTS.viewDataSetInformation());
            viewDsInfoPanel.add(noDsSelectedLabel);
        }
    }

    private void setOnExpandDataSet(){
        if(mainDataGrid.getSelectionModel().getSelectedItem() != null){
            selectedItemIndex = mainDataGrid.getStore().indexOf(mainDataGrid.getSelectionModel().getSelectedItem());
            if(mainDataGrid.getSelectionModel().getSelectedItem() instanceof DataProviderUI) {
                DataProviderUI dataProviderUI = (DataProviderUI) mainDataGrid.getSelectionModel().getSelectedItem();
                if(dataProviderUI.getDataSourceUIList().size() == 1)
                    editPanelInfo(dataProviderUI.getDataSourceUIList().get(0));
            } else if(mainDataGrid.getSelectionModel().getSelectedItem() instanceof DataSourceUI)
                editPanelInfo((DataSourceUI)mainDataGrid.getSelectionModel().getSelectedItem());
        }
    }

    /*********************************************************
     Scrolling Functions
     **********************************************************/
    private void selectPreviouslySelectedItem() {
        if(selectedItemIndex < store.getModels().size() && selectedItemIndex >= 0)
            mainGridSelectionModel.select(selectedItemIndex, true);
    }

    public void resetScrollBarPos(){
        try{
            mainDataGrid.getView().getScroller().setScrollTop(scrollBarY);
        }catch (NullPointerException e){

        }
    }

    /*********************************************************
     Public Functions
     **********************************************************/

    public ContentPanel getMainGridPanel() {
        return mainGridPanel;
    }

    public TreeGrid<DataContainer> getMainDataGrid() {
        return mainDataGrid;
    }

    public MyPagingToolBar getPagingToolBar() {
        return pagingToolBar;
    }

    public TreeStore<ModelData> getStore() {
        return store;
    }

    public BrowseFilterPanel getBrowseFilterPanel() {
        return browseFilterPanel;
    }

    public MainGridTopToolbar getTopToolbar() {
        return topToolbar;
    }
}
