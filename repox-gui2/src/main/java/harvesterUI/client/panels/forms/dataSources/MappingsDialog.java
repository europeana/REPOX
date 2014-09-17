package harvesterUI.client.panels.forms.dataSources;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.panels.mdr.MappingsSearchBar;
import harvesterUI.client.panels.mdr.forms.ShowConnectedDSDialog;
import harvesterUI.client.servlets.transformations.TransformationsServiceAsync;
import harvesterUI.client.util.ImageButton;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 03-07-2011
 * Time: 15:31
 */
public class MappingsDialog extends FormDialog {

    private TransformationsServiceAsync service;
    private ToolBar topToolbar;
    private Grid<TransformationUI> grid;
    private PagingLoader<PagingLoadResult<ModelData>> loader;
    private MappingsSearchBar mappingsSearchBar;

    private String sourceFormat;
    private StringFilter srcFilter;

    public MappingsDialog(final Grid<TransformationUI> transformationsGrid, String sourceFormat) {
        super(0.6,0.5);
        this.sourceFormat = sourceFormat;
        service = (TransformationsServiceAsync)Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
        setIcon(HarvesterUI.ICONS.schema_mapper_icon());
        setHeading("Available Transformations");
        setLayout(new FitLayout());
        setBodyBorder(false);
        setBorders(false);

        topToolbar = new ToolBar();
        setTopComponent(topToolbar);

        createTransformationsGrid();

        Button addTransformationButton = new Button();
        addTransformationButton.setText("Add Selected");
        addTransformationButton.setIcon(HarvesterUI.ICONS.add());
        addTransformationButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                if(grid.getSelectionModel().getSelectedItems().size() == 0){
                    HarvesterUI.UTIL_MANAGER.getInfoBox("Add Transformations","No transformations selected.");
                    return;
                }

                for(TransformationUI transformationUI : grid.getSelectionModel().getSelectedItems()){
                    if(!transformationsGrid.getStore().getModels().contains(transformationUI))
                        transformationsGrid.getStore().add(transformationUI);
                }
                hide();
            }
        });
        topToolbar.insert(addTransformationButton,0);

        Button newTransformationButton = new Button();
        newTransformationButton.setText("New");
        newTransformationButton.setIcon(HarvesterUI.ICONS.mapping_new());
        newTransformationButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                Dispatcher.forwardEvent(AppEvents.ViewAddMappingDialog);
            }
        });
        topToolbar.add(newTransformationButton);

        topToolbar.add(new FillToolItem());
        topToolbar.add(new LabelToolItem(HarvesterUI.ICONS.search_icon().getHTML()));
        mappingsSearchBar = new MappingsSearchBar(grid);
        topToolbar.add(mappingsSearchBar);
        Button clearSearchButton = new Button("",HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                mappingsSearchBar.clear();
                loadData();
            }
        });
        clearSearchButton.setToolTip("Clears Search results");
        topToolbar.add(clearSearchButton);
    }

    private void createTransformationsGrid(){
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        CheckBoxSelectionModel<TransformationUI> sm = new CheckBoxSelectionModel<TransformationUI>();
        sm.setSelectionMode(Style.SelectionMode.MULTI);
        configs.add(sm.getColumn());

//        configs.add(new ColumnConfig("identifier",HarvesterUI.CONSTANTS.id(),75));
        configs.add(new ColumnConfig("description",HarvesterUI.CONSTANTS.description(),100));
        ColumnConfig column = new ColumnConfig("srcFormat",HarvesterUI.CONSTANTS.sourceFormat(),55);
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        configs.add(column);
        column = new ColumnConfig("destFormat",HarvesterUI.CONSTANTS.destinationFormat(),65);
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        configs.add(column);
//        configs.add(new ColumnConfig("dsStringFormat","Composed ID",150));
        configs.add(new ColumnConfig("destSchema",HarvesterUI.CONSTANTS.schema(),150));
        configs.add(new ColumnConfig("destMtdNamespace",HarvesterUI.CONSTANTS.metadataNamespace(),150));
//        configs.add(new ColumnConfig("xslFilePath",HarvesterUI.CONSTANTS.xsl(),75));

        // Usage Column dialog button
        column = new ColumnConfig("usage","Used",75);
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        column.setRenderer(new GridCellRenderer<TransformationUI>() {
            public Object render(final TransformationUI model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<TransformationUI> store, Grid<TransformationUI> grid) {
                if(model.getMdrDataStatistics().getNumberTimesUsedInDataSets() == 0)
                    return "0";

                ImageButton imageButton = new ImageButton();
                imageButton.addListener(Events.OnMouseDown, new Listener<ButtonEvent>() {
                    public void handleEvent(ButtonEvent be) {
                        new ShowConnectedDSDialog(model.getMdrDataStatistics().getUsedInDataSetsList()).showAndCenter();
                    }
                });

                ToolTipConfig tooltipConfig = new ToolTipConfig();
                tooltipConfig.setShowDelay(10);
                String viewLog = "<div style='text-align: center;'>" + HarvesterUI.CONSTANTS.dataSetUsageTootip() + "</div>";
                imageButton.setIcon(HarvesterUI.ICONS.table());
                tooltipConfig.setTitle(HarvesterUI.CONSTANTS.transformationsUsageList());
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

        ColumnModel cm = new ColumnModel(configs);

        RpcProxy<PagingLoadResult<TransformationUI>> proxy = new RpcProxy<PagingLoadResult<TransformationUI>>() {
            @Override
            public void load(Object loadConfig, AsyncCallback<PagingLoadResult<TransformationUI>> callback) {
                service.getPagedTransformations((FilterPagingLoadConfig) loadConfig, callback);
            }
        };

        // loader
        loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy){
            @Override
            protected Object newLoadConfig() {
                BasePagingLoadConfig config = new BaseFilterPagingLoadConfig();
//                List<FilterConfig> filters = new ArrayList<FilterConfig>();
//                BaseFilterConfig c = new BaseStringFilterConfig("string", sourceFormat);
//                c.setField("srcFormat");
//                filters.add(c);
//                config.set("filters",filters);
                return config;
            }

        };
        loader.setRemoteSort(true);

        ListStore<TransformationUI> store = new ListStore<TransformationUI>(loader);

        PagingToolBar toolBar = new PagingToolBar(25);
        toolBar.bind(loader);

        setBottomComponent(toolBar);

        GridFilters filters = new GridFilters();
        StringFilter descriptionFilter = new StringFilter("description");
        srcFilter = new StringFilter("srcFormat");
        StringFilter destFilter = new StringFilter("destFormat");
        StringFilter dssStringFilter = new StringFilter("dsStringFormat");
        StringFilter schemaFilter = new StringFilter("schema");
        StringFilter metadataNamespaceFilter = new StringFilter("mtdNamespace");
        filters.addFilter(descriptionFilter);
        filters.addFilter(srcFilter);
        filters.addFilter(destFilter);
        filters.addFilter(dssStringFilter);
        filters.addFilter(schemaFilter);
        filters.addFilter(metadataNamespaceFilter);

        grid = new Grid<TransformationUI>(store, cm);
        grid.getView().setForceFit(true);
        grid.setSelectionModel(sm);
        grid.setStripeRows(true);
        grid.setLoadMask(true);
        grid.setTrackMouseOver(false);
        grid.addPlugin(sm);
        grid.addPlugin(filters);
        grid.getView().setEmptyText(HarvesterUI.CONSTANTS.noTransformationsAvailable());
        grid.addListener(Events.Attach, new Listener<GridEvent<ScheduledTaskUI>>() {
            public void handleEvent(GridEvent<ScheduledTaskUI> be) {
                loadData();
            }
        });

//        grid.getStore().addFilter(new StoreFilter<TransformationUI>() {
//            @Override
//            public boolean select(Store<TransformationUI> transformationUIStore, TransformationUI parent, TransformationUI item, String property) {
//                return item.getSrcFormat().contains(sourceFormat);
//            }
//        });
//        grid.getStore().applyFilters("srcFormat");

        add(grid);
    }

    public void loadData(){
        if(!mappingsSearchBar.getRawValue().isEmpty())
            mappingsSearchBar.loadSearchResults();
        else
            loader.load(0,25);

        srcFilter.setValue(sourceFormat);
    }

}
