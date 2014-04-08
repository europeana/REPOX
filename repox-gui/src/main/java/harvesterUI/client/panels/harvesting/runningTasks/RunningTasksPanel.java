package harvesterUI.client.panels.harvesting.runningTasks;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.browse.BrowseFilterPanel;
import harvesterUI.client.servlets.harvest.HarvestOperationsServiceAsync;
import harvesterUI.client.util.ImageButton;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.tasks.RunningTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 18-07-2012
 * Time: 15:13
 */
public class RunningTasksPanel extends ContentPanel {

    private Grid<ModelData> grid;
    private HarvestOperationsServiceAsync service;

    public RunningTasksPanel() {
        service = (HarvestOperationsServiceAsync) Registry.get(HarvesterUI.HARVEST_OPERATIONS_SERVICE);

        setHeading(HarvesterUI.CONSTANTS.runningTasks());
        setIcon(HarvesterUI.ICONS.running_tasks_icon());
        setScrollMode(Style.Scroll.AUTO);
        setLayout(new FitLayout());

        createGridList();
    }

    private void createGridList() {
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        GridCellRenderer<ModelData> cancelTask = new GridCellRenderer<ModelData>() {
            public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<ModelData> store, Grid<ModelData> grid) {
                final RunningTask task = (RunningTask) model;

                ImageButton deleteButton = new ImageButton();
                deleteButton.setIcon(HarvesterUI.ICONS.delete());
                deleteButton.setToolTip("Cancel a Running Task");
                deleteButton.addListener(Events.OnMouseDown, new Listener<ButtonEvent>() {
                    public void handleEvent(ButtonEvent be) {
                        AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
                            public void onFailure(Throwable caught) {
                                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                            }
                            public void onSuccess(Boolean result) {
                                if(!result) {
                                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.cancelHarvest(),HarvesterUI.CONSTANTS.taskOfDataSet() + " "
                                            +task.getDataSet()+" " + HarvesterUI.CONSTANTS.notFound());
                                    return;
                                }

                                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.cancelHarvest(), HarvesterUI.CONSTANTS.taskOfDataSet() + " "
                                        +task.getDataSet()+" "+HarvesterUI.CONSTANTS.cancelTaskWarning());
                                Dispatcher.get().dispatch(AppEvents.ViewRunningTasksList);
                            }
                        };
                        service.deleteRunningTask(task, callback);
                    }
                });

                return deleteButton;
            }
        };
        ColumnConfig type = new ColumnConfig("listType", HarvesterUI.CONSTANTS.type(), 100);
        columns.add(type);

        ColumnConfig parameters = new ColumnConfig("parameters", HarvesterUI.CONSTANTS.parameters(), 170);
        columns.add(parameters);

        ColumnConfig status = new ColumnConfig("statusListString", HarvesterUI.CONSTANTS.status(), 100);
        columns.add(status);

        ColumnConfig delete = new ColumnConfig("", HarvesterUI.CONSTANTS.operations(), 25);
        delete.setRenderer(cancelTask);
        columns.add(delete);

        ColumnModel cm = new ColumnModel(columns);

        ListStore<ModelData> store = new ListStore<ModelData>();

        grid = new Grid<ModelData>(store, cm);
        grid.getView().setEmptyText(HarvesterUI.CONSTANTS.noTasksRunning());
        grid.setBorders(false);
        grid.setTrackMouseOver(false);
        grid.setLayoutData(new FitLayout());
        grid.setStripeRows(true);
        grid.setColumnLines(true);
        grid.getView().setForceFit(true);

        add(grid);

        // Refresh export button
        ToolButton refreshExport = new ToolButton("x-tool-refresh");
        refreshExport.addSelectionListener(new SelectionListener<IconButtonEvent>() {
            @Override
            public void componentSelected(IconButtonEvent ce) {
                Dispatcher.forwardEvent(AppEvents.ViewRunningTasksList);
            }
        });
        getHeader().addTool(refreshExport);
    }

    public void loadTasks() {
//        mask("Loading Running Tasks...");
        AsyncCallback<List<RunningTask>> callbackRT = new AsyncCallback<List<RunningTask>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<RunningTask> result) {
                grid.getStore().removeAll();
                grid.getStore().add(result);
//                unmask();
            }
        };
        List<FilterQuery> filterQueries = ((BrowseFilterPanel) Registry.get("browseFilterPanel")).getAllQueries();
        String username = HarvesterUI.UTIL_MANAGER.getLoggedUserName();
        service.getAllRunningTasks(filterQueries,username,callbackRT);
    }
}
