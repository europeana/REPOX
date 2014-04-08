package harvesterUI.client.panels.tags;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.mdr.forms.ShowConnectedDSDialog;
import harvesterUI.client.servlets.dataManagement.TagsServiceAsync;
import harvesterUI.client.util.ImageButton;
import harvesterUI.client.util.paging.ListPagingToolBar;
import harvesterUI.shared.dataTypes.dataSet.DataSetTagUI;
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
public class TagManagementPanel extends ContentPanel {

    private TagsServiceAsync service;
    private ToolBar topToolbar;
    private Grid<DataSetTagUI> grid;
    private PagingLoader<PagingLoadResult<DataSetTagUI>> loader;

    public TagManagementPanel() {
        service = (TagsServiceAsync)Registry.get(HarvesterUI.TAGS_SERVICE);
        setIcon(HarvesterUI.ICONS.tag_icon());
        setHeading("Tags Manager");
        setLayout(new FitLayout());

        topToolbar = new ToolBar();
        setTopComponent(topToolbar);

        createTagsGrid();

        // todo: enhance code
        UserRole userRole = HarvesterUI.UTIL_MANAGER.getLoggedUserRole();
        if(userRole == UserRole.ADMIN || userRole == UserRole.NORMAL){
            Button addTagButton = new Button();
            addTagButton.setText("&nbsp&nbsp" + HarvesterUI.CONSTANTS.add());
            addTagButton.setIcon(HarvesterUI.ICONS.tag_add_icon());
            addTagButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    new TagDialog().showAndCenter();
                }
            });
            topToolbar.insert(addTagButton,0);

            final TagsOperations tagsOperations = new TagsOperations(grid);
            grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<DataSetTagUI>() {
                @Override
                public void selectionChanged(SelectionChangedEvent<DataSetTagUI> se) {
                    if (se.getSelectedItem() != null) {
                        tagsOperations.showTagButtons(topToolbar);
                    } else {
                        tagsOperations.hideTagButtons(topToolbar);
                    }
                }
            });
        }
    }

    private void createTagsGrid(){
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        CheckBoxSelectionModel<DataSetTagUI> sm = new CheckBoxSelectionModel<DataSetTagUI>();
        sm.setSelectionMode(Style.SelectionMode.MULTI);
        configs.add(sm.getColumn());

        configs.add(new ColumnConfig("name",HarvesterUI.CONSTANTS.name(),75));

        // Usage Column dialog button
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
                return new BaseFilterPagingLoadConfig();
            }

        };
        loader.setRemoteSort(true);

        ListStore<DataSetTagUI> store = new ListStore<DataSetTagUI>(loader);

        ListPagingToolBar toolBar = new ListPagingToolBar(25);
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
