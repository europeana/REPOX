package harvesterUI.client.panels.harvesting.runningTasks;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.browse.BrowseFilterPanel;
import harvesterUI.client.servlets.dataManagement.DataSetOperationsServiceAsync;
import harvesterUI.client.servlets.harvest.HarvestOperationsServiceAsync;
import harvesterUI.client.util.ImageButton;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.dataTypes.dataSet.SimpleDataSetInfo;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 18-07-2012
 * Time: 15:16
 */
public class RunningDataSetsPanel extends ContentPanel {

    private Grid<SimpleDataSetInfo> runningDataSetGrid;
    private HarvestOperationsServiceAsync harvestOperationsService;
    private DataSetOperationsServiceAsync dataSetOperationsService;

    public RunningDataSetsPanel() {
        harvestOperationsService = (HarvestOperationsServiceAsync) Registry.get(HarvesterUI.HARVEST_OPERATIONS_SERVICE);
        dataSetOperationsService = (DataSetOperationsServiceAsync) Registry.get(HarvesterUI.DATA_SET_OPERATIONS_SERVICE);

        setHeading("Running Data Sets");
        setIcon(HarvesterUI.ICONS.data_set_icon());
        setScrollMode(Style.Scroll.AUTO);
        setLayout(new FitLayout());

        createGhostDataSetsGrid();
    }

    private void createGhostDataSetsGrid(){

        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        GridCellRenderer<SimpleDataSetInfo> cancelTask = new GridCellRenderer<SimpleDataSetInfo>() {
            public Object render(final SimpleDataSetInfo model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<SimpleDataSetInfo> store, Grid<SimpleDataSetInfo> grid) {

                LayoutContainer container = new LayoutContainer();
                FlowLayout columnLayout = new FlowLayout();
                columnLayout.setExtraStyle("columnLayoutCenter");
                container.setLayout(columnLayout);

                ImageButton deleteButton = new ImageButton();
                deleteButton.setIcon(HarvesterUI.ICONS.delete());
                deleteButton.setToolTip("Cancel a Running Data Set");
                deleteButton.addListener(Events.OnMouseDown, new Listener<ButtonEvent>() {
                    public void handleEvent(ButtonEvent be) {
                        AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
                            public void onFailure(Throwable caught) {
                                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                            }
                            public void onSuccess(ResponseState response) {
                                if(response != ResponseState.SUCCESS) {
                                    HarvesterUI.UTIL_MANAGER.getErrorBox("Cancel Running Data Set","Running Data Set" + " "
                                            +model.getName()+" " + HarvesterUI.CONSTANTS.notFound());
                                    return;
                                }

                                HarvesterUI.UTIL_MANAGER.getSaveBox("Cancel Running Data Set", "Running Data Set" + " "
                                        +model.getName()+" "+ " canceled successfully.");
                                Dispatcher.get().dispatch(AppEvents.ViewRunningTasksList);
                            }
                        };
                        dataSetOperationsService.stopRunningDataSet(model.getId(), callback);
                    }
                });

                container.add(deleteButton);
                return container;
            }
        };

        ColumnConfig type = new ColumnConfig("name", HarvesterUI.CONSTANTS.dataSet(), 110);
        columns.add(type);

        ColumnConfig delete = new ColumnConfig("", HarvesterUI.CONSTANTS.operations(), 50);
        delete.setRenderer(cancelTask);
        delete.setAlignment(Style.HorizontalAlignment.CENTER);
        columns.add(delete);

        ColumnModel cm = new ColumnModel(columns);

        ListStore<SimpleDataSetInfo> store = new ListStore<SimpleDataSetInfo>();

        runningDataSetGrid = new Grid<SimpleDataSetInfo>(store, cm);
        runningDataSetGrid.getView().setEmptyText("There are no running Data Sets now.");
        runningDataSetGrid.setBorders(false);
        runningDataSetGrid.setTrackMouseOver(false);
        runningDataSetGrid.setLayoutData(new FitLayout());
        runningDataSetGrid.setStripeRows(true);
        runningDataSetGrid.setColumnLines(true);
        runningDataSetGrid.getView().setForceFit(true);

        add(runningDataSetGrid);
    }

    public void loadRunningDataSets() {
//        mask("Loading Data Sets...");
        AsyncCallback<List<SimpleDataSetInfo>> callbackRT = new AsyncCallback<List<SimpleDataSetInfo>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<SimpleDataSetInfo> result) {
                runningDataSetGrid.getStore().removeAll();
                runningDataSetGrid.getStore().add(result);
//                unmask();
            }
        };
        List<FilterQuery> filterQueries = ((BrowseFilterPanel) Registry.get("browseFilterPanel")).getAllQueries();
        String username = HarvesterUI.UTIL_MANAGER.getLoggedUserName();
        harvestOperationsService.getAllRunningDataSets(filterQueries,username,callbackRT);
    }

}
