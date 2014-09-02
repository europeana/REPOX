package harvesterUI.client.panels.overviewGrid;

import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.browse.BrowseFilterPanel;
import harvesterUI.client.panels.dataProviderButtons.CreateDataProviderButton;
import harvesterUI.client.servlets.harvest.HarvestOperationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.ProjectType;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.tasks.RunningTask;

import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created to REPOX project.
 * User: Edmundo
 * Date: 26/03/12
 * Time: 11:57
 */
public class MainGridTopToolbar extends ToolBar{

    private ToggleButton dss;
    private ToggleButton autoRefreshButton;
    private SearchComboBox searchCombo;
    private MainGrid mainGrid;

    public MainGridTopToolbar(MainGrid mainGrid) {
        this.mainGrid = mainGrid;
        setSpacing(0);

        new CreateDataProviderButton(this);

        Button viewButton = new Button(HarvesterUI.CONSTANTS.view());
        viewButton.setIcon(HarvesterUI.ICONS.view_filter_icon());
        Menu viewMenu = new Menu();
        viewButton.setId("view");
        viewButton.setMenu(viewMenu);
        add(viewButton);

        final ToggleButton aggs = new ToggleButton (HarvesterUI.CONSTANTS.aggregators(), HarvesterUI.ICONS.form());
        aggs.setId("aggs");
        final ToggleButton dps = new ToggleButton(HarvesterUI.CONSTANTS.dataProviders(), HarvesterUI.ICONS.form());
        dps.setId("dps");
        dss = new ToggleButton(HarvesterUI.CONSTANTS.dataSets(), HarvesterUI.ICONS.form());
        dss.setId("dss");
        aggs.toggle();
        aggs.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            public void handleEvent(ButtonEvent ce) {
                dps.toggle(false);
                dps.enable();
                dss.toggle(false);
                dss.enable();
                aggs.toggle(true);
                aggs.disable();
                showAggregators();
            }
        });
        dps.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            public void handleEvent(ButtonEvent ce) {
                aggs.toggle(false);
                aggs.enable();
                dss.toggle(false);
                dss.enable();
                dps.toggle(true);
                dps.disable();
                showDataProviders();
            }
        });
        dss.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            public void handleEvent(ButtonEvent ce) {
                aggs.toggle(false);
                aggs.enable();
                dps.toggle(false);
                dps.enable();
                dss.toggle(true);
                dss.disable();
                showDataSources();
            }
        });

        viewMenu.add(aggs);
        viewMenu.add(dps);
        viewMenu.add(dss);

        if(HarvesterUI.getProjectType() != ProjectType.EUROPEANA){
            viewButton.getMenu().getItems().remove(viewButton.getMenu().getItemByItemId("aggs"));
            ((ToggleButton)viewButton.getMenu().getItemByItemId("dps")).toggle();
        }

        autoRefreshButton = new ToggleButton(HarvesterUI.CONSTANTS.autoRefresh(), HarvesterUI.ICONS.accordion());
        autoRefreshButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            public void handleEvent(ButtonEvent ce) {
                toggleRefreshCycle();
            }
        });
        add(autoRefreshButton);
        add(new FillToolItem());
        add(new LabelToolItem(HarvesterUI.ICONS.search_icon().getHTML()));
        createFilterCombo();
    }

    private void createFilterCombo() {
        searchCombo = new SearchComboBox();
        add(searchCombo);
        Button clearSearchButton = new Button("",HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                searchCombo.clear();
                Dispatcher.forwardEvent(AppEvents.LoadMainData);
            }
        });
        clearSearchButton.setToolTip("Clears Search results");
        add(clearSearchButton);
    }

    public void toggleRefreshCycle() {
        Timer t = new Timer() {
            public void run() {
                if(autoRefreshButton.isPressed()) {
                    checkRunningTasks();
                    mainGrid.setScrollBarY();
                    Dispatcher.get().dispatch(AppEvents.LoadMainData);
                    schedule(5000);
                }
            }
        };

        if(autoRefreshButton.isPressed())
            t.schedule(3000);
    }

    private void checkRunningTasks() {
        AsyncCallback<List<RunningTask>> callback = new AsyncCallback<List<RunningTask>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<RunningTask> result) {
                if(result.size() == 0)
                    autoRefreshButton.toggle(false);
            }
        };
        HarvestOperationsServiceAsync service = (HarvestOperationsServiceAsync) Registry.get(HarvesterUI.HARVEST_OPERATIONS_SERVICE);
        List<FilterQuery> filterQueries = ((BrowseFilterPanel) Registry.get("browseFilterPanel")).getAllQueries();
        String username = HarvesterUI.UTIL_MANAGER.getLoggedUserName();
        service.getAllRunningTasks(filterQueries,username,callback);
    }

    // Show different data
    private void showAggregators() {
        PagingLoadConfig config = new BasePagingLoadConfig();
        config.setOffset(0);
        config.setLimit(25);
        config.set("VIEW_TYPE","AGGREAGATORS");
        mainGrid.getPagingToolBar().load(config);
    }

    private void showDataProviders() {
        PagingLoadConfig config = new BasePagingLoadConfig();
        config.setOffset(0);
        config.setLimit(25);
        config.set("VIEW_TYPE","DATA_PROVIDERS");
        mainGrid.getPagingToolBar().load(config);
    }

    private void showDataSources() {
        PagingLoadConfig config = new BasePagingLoadConfig();
        config.setOffset(0);
        config.setLimit(25);
        config.set("VIEW_TYPE","DATA_SETS");
        mainGrid.getPagingToolBar().load(config);
    }

    public SearchComboBox getSearchCombo() {
        return searchCombo;
    }
}
