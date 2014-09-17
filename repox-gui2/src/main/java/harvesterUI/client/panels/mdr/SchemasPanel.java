package harvesterUI.client.panels.mdr;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.mdr.forms.ShowConnectedDSDialog;
import harvesterUI.client.panels.overviewGrid.MyPagingToolBar;
import harvesterUI.client.servlets.transformations.TransformationsServiceAsync;
import harvesterUI.client.util.ImageButton;
import harvesterUI.shared.mdr.SchemaTreeUI;
import harvesterUI.shared.mdr.SchemaUI;
import harvesterUI.shared.mdr.SchemaVersionUI;
import harvesterUI.shared.tasks.ScheduledTaskUI;
import harvesterUI.shared.users.UserRole;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 03-07-2011
 * Time: 15:31
 */
public class SchemasPanel extends ContentPanel {

    private ToolBar topToolbar;
    private SchemaGridContainer gridContainer;
    private TreeGrid<SchemaTreeUI> grid;
    private SchemasSearchBar schemasSearchBar;
    private MyPagingToolBar pagingToolBar;
    private TransformationsServiceAsync transformationService;

    public SchemasPanel() {
        setLayout(new FitLayout());
        setHeaderVisible(false);
        setBodyBorder(false);
        setBorders(false);

        topToolbar = new ToolBar();
        setTopComponent(topToolbar);
        createSchemasGrid();

        gridContainer = new SchemaGridContainer(this, grid);
        transformationService = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
        createPagingToolbar();

        // todo: enhance code
        // only show when user is admin or normal
        UserRole userRole = HarvesterUI.UTIL_MANAGER.getLoggedUserRole();
        if(userRole == UserRole.ADMIN || userRole == UserRole.NORMAL){

            Button addSchema = new Button();
            addSchema.setText(HarvesterUI.CONSTANTS.addSchema());
            addSchema.setIcon(HarvesterUI.ICONS.schema_new());
            addSchema.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    Dispatcher.forwardEvent(AppEvents.ViewAddSchemaDialog);
                }
            });
            topToolbar.add(addSchema);

            final SchemasOperations schemasOperations = new SchemasOperations(grid);
            grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<SchemaTreeUI>() {
                @Override
                public void selectionChanged(SelectionChangedEvent<SchemaTreeUI> se) {
                    List<SchemaTreeUI> selected = se.getSelection();
                    int size = selected.size();
                    if(size == 0 || se.getSelectedItem() == null) {
                        schemasOperations.hideSchemasButtons(topToolbar);
                        return;
                    }
                    if(size>1){
                        int nschemas = countSchemasInList(selected);
                        if(size == nschemas) {
                            schemasOperations.showSchemasButtons(topToolbar, true);
                            schemasOperations.hideEditButton(topToolbar);
                        }
                        else
                            schemasOperations.hideSchemasButtons(topToolbar);

                    }
                    else { //size ==1
                        if(se.getSelectedItem() instanceof SchemaUI)
                            schemasOperations.showSchemasButtons(topToolbar, true);
                        else{
                            schemasOperations.showSchemasButtons(topToolbar, false);
                        }
                    }

                }
            });
        }

        topToolbar.add(new FillToolItem());
        topToolbar.add(new LabelToolItem(HarvesterUI.ICONS.search_icon().getHTML()));
        schemasSearchBar = new SchemasSearchBar(grid);
        topToolbar.add(schemasSearchBar);
        Button clearSearchButton = new Button("",HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                schemasSearchBar.clear();
                loadData();
            }
        });
        clearSearchButton.setToolTip("Clears Search results");
        topToolbar.add(clearSearchButton);
    }

    /*** UTILITY STUFF **/
    private int countSchemasInList(List<SchemaTreeUI> list) {
        int c = 0;
        for(SchemaTreeUI s : list) {
            if(s instanceof SchemaUI)
                c++;
        }
        return c;
    }

    private int countVersionsInList(List<SchemaTreeUI> list) {
        int c = 0;
        for(SchemaTreeUI s : list) {
            if(s instanceof SchemaUI)
                c++;
        }
        return c;
    }
    /****/

    private void createPagingToolbar() {
        RpcProxy<PagingLoadResult<SchemaTreeUI>> proxy = new RpcProxy<PagingLoadResult<SchemaTreeUI>>() {
            @Override
            public void load(Object loadConfig, AsyncCallback<PagingLoadResult<SchemaTreeUI>> callback) {
                transformationService.getPagingData((PagingLoadConfig) loadConfig,callback);
            }
        };

        pagingToolBar = new MyPagingToolBar(50, gridContainer);

        PagingLoader<PagingLoadResult<SchemaTreeUI>> loader = new BasePagingLoader<PagingLoadResult<SchemaTreeUI>>(proxy);
        loader.setRemoteSort(true);
        pagingToolBar.bind(loader);
        setBottomComponent(pagingToolBar);
    }

    private void createSchemasGrid(){
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        boolean bDisableMenus = true;

        CheckBoxSelectionModel<SchemaTreeUI> sm = new CheckBoxSelectionModel<SchemaTreeUI>();
        sm.setSelectionMode(Style.SelectionMode.MULTI);
        configs.add(sm.getColumn());

        ColumnConfig cc = new ColumnConfig("schema",HarvesterUI.CONSTANTS.schema(),100);
        cc.setRenderer(new TreeGridCellRenderer<ModelData>());
        cc.setMenuDisabled(bDisableMenus);
        configs.add(cc);

        GridCellRenderer linkRenderer = new GridCellRenderer<SchemaTreeUI>() {
            public Object render(final SchemaTreeUI model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<SchemaTreeUI> store, Grid<SchemaTreeUI> grid) {
                if(property.equals("namespace") && model instanceof SchemaVersionUI){
                    String link = model.getParent().get("namespace");
                    if(link.startsWith("http://"))
                        return "<a href='"+link+"' target='_blank'>"+link+"</a>";
                    else
                        return link;
                }

                String link = model.get(property);
                if(link.startsWith("http://"))
                    return "<a href='"+link+"' target='_blank'>"+link+"</a>";
                else
                    return link;
            }
        };

//        configs.add(new ColumnConfig("version","Version",75));

        ColumnConfig column = new ColumnConfig("namespace",HarvesterUI.CONSTANTS.namespace(),150);
        column.setMenuDisabled(bDisableMenus);
        column.setRenderer(linkRenderer);
        configs.add(column);

        column = new ColumnConfig("xsdLink",HarvesterUI.CONSTANTS.xsdLink(),150);
        column.setMenuDisabled(bDisableMenus);
        column.setRenderer(linkRenderer);
        configs.add(column);

        //OAI Available column
        column = new ColumnConfig("bOAIAvailable","OAI Available", 30);  //TODO: multi-lang
        column.setMenuDisabled(bDisableMenus);
        column.setAlignment(Style.HorizontalAlignment.CENTER);

        column.setRenderer(new GridCellRenderer<SchemaTreeUI>() {
            public Object render(final SchemaTreeUI model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<SchemaTreeUI> store, Grid<SchemaTreeUI> grid) {
                LayoutContainer container = new LayoutContainer();
                if(model instanceof SchemaUI) {
                    ImageButton imageButton = new ImageButton();
//                    imageButton.setEnabled(false);

                    ToolTipConfig tooltipConfig = new ToolTipConfig();
                    tooltipConfig.setShowDelay(1000);
                    String viewLog;
                    if(((SchemaUI) model).isOAIAvailable()) {
                        viewLog = "<div style='text-align: center;'>" + "OAI is available" + "</div>"; //TODO: multi lang;
                        imageButton.setIcon(HarvesterUI.ICONS.oai_check());
                        tooltipConfig.setTitle(HarvesterUI.CONSTANTS.yes()); //todo new text?
                    }
                    else {
                        viewLog = "<div style='text-align: center;'>" + "OAI is not available" + "</div>"; //TODO: multi lang
                        imageButton.setIcon(HarvesterUI.ICONS.oai_uncheck());
                        tooltipConfig.setTitle(HarvesterUI.CONSTANTS.no()); //todo new text?
                    }

                    tooltipConfig.setText(viewLog);
                    imageButton.setToolTip(tooltipConfig);

                    HBoxLayout layout = new HBoxLayout();
                    layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
                    layout.setPack(BoxLayout.BoxLayoutPack.CENTER);
                    container.setLayout(layout);
                    container.add(imageButton,new HBoxLayoutData(new Margins(0, 0, 0, 0)));
                }

                return container;
            }
        });

        configs.add(column);

        // Usage DataSets Column dialog button
        column = new ColumnConfig("usage","Used in Data Sets", 40); //todo multi lang
        column.setMenuDisabled(bDisableMenus);
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        column.setRenderer(new GridCellRenderer<SchemaTreeUI>() {
            public Object render(final SchemaTreeUI model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<SchemaTreeUI> store, Grid<SchemaTreeUI> grid) {
                // Sum schema version numbers if it is a schema
                if(model instanceof SchemaUI){
                    return ((SchemaUI)model).getTotalTimesUsed(true);
                }

                if(model.getMdrDataStatistics() == null || model.getMdrDataStatistics().getNumberTimesUsedInDataSets() == 0)
                    return "0";

                ImageButton imageButton = new ImageButton();
                imageButton.addListener(Events.OnMouseDown, new Listener<ButtonEvent>() {
                    public void handleEvent(ButtonEvent be) {
                        new ShowConnectedDSDialog(model.getMdrDataStatistics().getUsedInDataSetsList()).showAndCenter();
                    }
                });

                ToolTipConfig tooltipConfig = new ToolTipConfig();
                tooltipConfig.setShowDelay(10);
                String viewLog = "<div style='text-align: center;'>" + "See Data Sets used in this schema" + "</div>"; //todo multi lang
                imageButton.setIcon(HarvesterUI.ICONS.table());
                tooltipConfig.setTitle("Schema Usage List");
                tooltipConfig.setText(viewLog);
                imageButton.setToolTip(tooltipConfig);

                LayoutContainer container = new LayoutContainer();
                HBoxLayout layout = new HBoxLayout();
                layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
                layout.setPack(BoxLayout.BoxLayoutPack.CENTER);
                container.setLayout(layout);
                container.add(new LabelToolItem(String.valueOf(model.getMdrDataStatistics().getNumberTimesUsedInDataSets())), new HBoxLayoutData(new Margins(0, 10, 0, 0)));
                container.add(imageButton,new HBoxLayoutData(new Margins(0, 0, 0, 0)));

                return container;
            }
        });
        configs.add(column);

        // Usage in Transformations Column dialog button
        column = new ColumnConfig("usageTrans","Used in Transformation", 40); //todo multi lang
        column.setMenuDisabled(bDisableMenus);
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        column.setRenderer(new GridCellRenderer<SchemaTreeUI>() {
            public Object render(final SchemaTreeUI model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<SchemaTreeUI> store, Grid<SchemaTreeUI> grid) {
                // Sum schema version numbers if it is a schema
                if(model instanceof SchemaUI){
                    return ((SchemaUI)model).getTotalTimesUsed(false);
                }

                if(model.getMdrDataStatistics() == null || model.getMdrDataStatistics().getNumberTimesUsedInTransformations() == 0)
                    return "0";

                /*ImageButton imageButton = new ImageButton();
                imageButton.addListener(Events.OnMouseDown, new Listener<ButtonEvent>() {
                    public void handleEvent(ButtonEvent be) {
                        new ShowConnectedDSDialog(model.getMdrDataStatistics().getUsedInDataSetsList()).showAndCenter();
                    }
                });

                ToolTipConfig tooltipConfig = new ToolTipConfig();
                tooltipConfig.setShowDelay(10);
                String viewLog = "<div style='text-align: center;'>" + "See Data Sets used in this schema" + "</div>";
                imageButton.setIcon(HarvesterUI.ICONS.table());
                tooltipConfig.setTitle("Schema Usage List");
                tooltipConfig.setText(viewLog);
                imageButton.setToolTip(tooltipConfig);*/

                /*LayoutContainer container = new LayoutContainer();
                HBoxLayout layout = new HBoxLayout();
                layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
                layout.setPack(BoxLayout.BoxLayoutPack.CENTER);
                container.setLayout(layout);
                container.add(new LabelToolItem(String.valueOf(model.getMdrDataStatistics().getNumberTimesUsedInTransformations())), new HBoxLayoutData(new Margins(0, 10, 0, 0)));
                //container.add(imageButton,new HBoxLayoutData(new Margins(0, 0, 0, 0)));

                return container;*/
                return model.getMdrDataStatistics().getNumberTimesUsedInTransformations();
            }
        });
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);

//        RpcProxy<PagingLoadResult<SchemaUI>> proxy = new RpcProxy<PagingLoadResult<SchemaUI>>() {
//            @Override
//            public void load(Object loadConfig, AsyncCallback<PagingLoadResult<SchemaUI>> callback) {
//                service.getPagedSchemas((PagingLoadConfig) loadConfig, callback);
//            }
//        };

        // loader
//        final PagingLoader<PagingLoadResult<SchemaUI>> loader = new BasePagingLoader<PagingLoadResult<SchemaUI>>(
//                proxy);
//        loader.setRemoteSort(true);

        final TreeStore<SchemaTreeUI> store = new TreeStore<SchemaTreeUI>();

//        PagingToolBar toolBar = new PagingToolBar(25);
//        toolBar.bind(loader);

//        setBottomComponent(toolBar);

        grid = new TreeGrid<SchemaTreeUI>(store, cm);
        grid.getView().setSortingEnabled(false);
        grid.getView().setForceFit(true);
        grid.setSelectionModel(sm);
        grid.setStripeRows(true);
        grid.setLoadMask(true);
        grid.setTrackMouseOver(false);
        grid.addPlugin(sm);
        grid.getView().setEmptyText(HarvesterUI.CONSTANTS.noTransformationsAvailable());
        grid.addListener(Events.Attach, new Listener<GridEvent<ScheduledTaskUI>>() {
            public void handleEvent(GridEvent<ScheduledTaskUI> be) {
                loadData();
            }
        });
        add(grid);

        grid.setIconProvider(new ModelIconProvider<SchemaTreeUI>() {
            public AbstractImagePrototype getIcon(SchemaTreeUI model) {
                if (model instanceof SchemaUI) {
                    return HarvesterUI.ICONS.schema();
                }else if (model instanceof SchemaVersionUI) {
                    return HarvesterUI.ICONS.schema_version();
                }
                return null;
            }
        });

        Menu contextMenu = new Menu();
        grid.setContextMenu(contextMenu);
        grid.addListener(Events.ContextMenu, new Listener<TreeGridEvent<ModelData>>() {
            public void handleEvent(TreeGridEvent<ModelData> event) {
                if(grid.getSelectionModel().getSelectedItems().size() > 0){
                    SchemaTreeUI selectedNode = grid.getSelectionModel().getSelectedItems().get(0);
                    if(selectedNode != null){
                        if(selectedNode instanceof SchemaUI)
                            grid.setContextMenu(new SchemaContextMenu(grid));
                        else if(selectedNode instanceof SchemaVersionUI) {
                            grid.setContextMenu(new SchemaVersionContextMenu(grid));
                        }
                    }
                }
            }
        });
    }

    private void loadData(){
        if(!schemasSearchBar.getRawValue().isEmpty())
            schemasSearchBar.loadSearchResults();
        else {
            pagingToolBar.refresh();
        }
    }

    public MyPagingToolBar getPagingToolbar() {
        return pagingToolBar;
    }

//    @Override
//    protected void onAttach() {
//        super.onAttach();
//    }

}
