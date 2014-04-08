package harvesterUI.client.panels.forms.dataSources;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.panels.mdr.forms.ShowConnectedDSDialog;
import harvesterUI.client.panels.tags.TagDialog;
import harvesterUI.client.servlets.dataManagement.TagsServiceAsync;
import harvesterUI.client.util.ImageButton;
import harvesterUI.shared.dataTypes.dataSet.DataSetTagUI;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 03-07-2011
 * Time: 15:31
 */
public class DataSourceTagsDialog extends FormDialog {

    private TagsServiceAsync service;
    private ToolBar topToolbar;
    private Grid<DataSetTagUI> grid;
    private PagingLoader<PagingLoadResult<DataSetTagUI>> loader;

    public DataSourceTagsDialog(final Grid<DataSetTagUI> tagsGrid) {
        super(0.6,0.5);
        service = (TagsServiceAsync)Registry.get(HarvesterUI.TAGS_SERVICE);
        setIcon(HarvesterUI.ICONS.tag_icon());
        setHeading("Available Tags");
        setLayout(new FitLayout());
        setBodyBorder(false);
        setBorders(false);

        topToolbar = new ToolBar();
        setTopComponent(topToolbar);

        createTagsGrid();

        Button addTagButton = new Button();
        addTagButton.setText("Add Selected");
        addTagButton.setIcon(HarvesterUI.ICONS.add());
        addTagButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                if (grid.getSelectionModel().getSelectedItems().size() == 0) {
                    HarvesterUI.UTIL_MANAGER.getInfoBox("Add Tags", "No tags selected.");
                    return;
                }

                for (DataSetTagUI dataSetTagUI : grid.getSelectionModel().getSelectedItems()) {
                    if (!tagsGrid.getStore().getModels().contains(dataSetTagUI))
                        tagsGrid.getStore().add(dataSetTagUI);
                }
                hide();
            }
        });
        topToolbar.insert(addTagButton,0);

        Button newTagButton = new Button();
        newTagButton.setText("&nbsp&nbspNew");
        newTagButton.setIcon(HarvesterUI.ICONS.tag_add_icon());
        newTagButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                new TagDialog().showAndCenter();
            }
        });
        topToolbar.add(newTagButton);

//        topToolbar.add(new FillToolItem());
    }

    private void createTagsGrid(){
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        CheckBoxSelectionModel<DataSetTagUI> sm = new CheckBoxSelectionModel<DataSetTagUI>();
        sm.setSelectionMode(Style.SelectionMode.MULTI);
        configs.add(sm.getColumn());

        configs.add(new ColumnConfig("name",HarvesterUI.CONSTANTS.name(),100));

//        Usage Column dialog button
        ColumnConfig column = new ColumnConfig("usage","Used",75);
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        column.setRenderer(new GridCellRenderer<DataSetTagUI>() {
            public Object render(final DataSetTagUI model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<DataSetTagUI> store, Grid<DataSetTagUI> grid) {
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
                String viewLog = "<div style='text-align: center;'>" + "See Data Sets that use this tag" + "</div>";
                imageButton.setIcon(HarvesterUI.ICONS.table());
                tooltipConfig.setTitle("Tag Usage List");
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

        RpcProxy<PagingLoadResult<DataSetTagUI>> proxy = new RpcProxy<PagingLoadResult<DataSetTagUI>>() {
            @Override
            public void load(Object loadConfig, AsyncCallback<PagingLoadResult<DataSetTagUI>> callback) {
                service.getPagedTags((FilterPagingLoadConfig) loadConfig, callback);
            }
        };

        // loader
        loader = new BasePagingLoader<PagingLoadResult<DataSetTagUI>>(proxy){
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

        ListStore<DataSetTagUI> store = new ListStore<DataSetTagUI>(loader);

        PagingToolBar toolBar = new PagingToolBar(25);
        toolBar.bind(loader);

        setBottomComponent(toolBar);

        grid = new Grid<DataSetTagUI>(store, cm);
        grid.getView().setForceFit(true);
        grid.setSelectionModel(sm);
        grid.setStripeRows(true);
        grid.setLoadMask(true);
        grid.setTrackMouseOver(false);
        grid.addPlugin(sm);
        grid.getView().setEmptyText("No Tags Available.");
        grid.addListener(Events.Attach, new Listener<GridEvent<ScheduledTaskUI>>() {
            public void handleEvent(GridEvent<ScheduledTaskUI> be) {
                loadData();
            }
        });

        add(grid);
    }

    public void loadData(){
        loader.load(0,25);
    }

}
