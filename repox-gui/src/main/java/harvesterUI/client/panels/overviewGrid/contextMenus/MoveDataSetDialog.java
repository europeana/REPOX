package harvesterUI.client.panels.overviewGrid.contextMenus;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.servlets.dataManagement.DataManagementServiceAsync;
import harvesterUI.client.servlets.dataManagement.DataSetOperationsServiceAsync;
import harvesterUI.client.util.paging.PageUtil;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.SaveDataResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 16-09-2011
 * Time: 16:52
 */
public class MoveDataSetDialog extends Dialog {
    private DataSetOperationsServiceAsync service;
    private ListStore<ModelData> store;
    private Grid<ModelData> grid;
    private List<DataSourceUI> selectedDataSources;

    public MoveDataSetDialog() {
        service = (DataSetOperationsServiceAsync) Registry.get(HarvesterUI.DATA_SET_OPERATIONS_SERVICE);
        createMoveDSDialog();
    }

    private void createMoveDSDialog() {
        setButtons("");
        setLayout(new FitLayout());
        setHeading(HarvesterUI.CONSTANTS.moveDataSets());
        setIcon(HarvesterUI.ICONS.arrow_move());
        setResizable(true);
        setModal(true);
        setSize(200,300);

        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column =  new ColumnConfig();
        column.setId("name");
        column.setHeader(HarvesterUI.CONSTANTS.name());
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        column.setWidth(75);
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);

        store = new ListStore<ModelData>();

        grid = new Grid<ModelData>(store, cm);
        grid.setBorders(false);
        grid.getView().setEmptyText(HarvesterUI.CONSTANTS.noOtherDataProvidersAvailable());
        grid.setStripeRows(true);
        grid.setColumnLines(true);
        grid.getSelectionModel().setSelectionMode(Style.SelectionMode.SINGLE);
        grid.setHideHeaders(true);
        grid.setColumnReordering(true);
        grid.setLayoutData(new FitLayout());
        grid.getView().setForceFit(true);
        add(grid);

        ToolBar toolBar = new ToolBar();
        toolBar.setAlignment(Style.HorizontalAlignment.CENTER);

        Button move = new Button(HarvesterUI.CONSTANTS.move());
        move.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent me) {
                if(grid.getSelectionModel().getSelectedItems().size() == 0)
                    return;

                AsyncCallback<SaveDataResponse> callback = new AsyncCallback<SaveDataResponse>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(SaveDataResponse response) {
                        hide();
                        PageUtil.setActivePage(response.getPage());
                        HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.moveDataSet(), HarvesterUI.CONSTANTS.moveDataSetSuccessful());
                    }
                };
                service.moveDataSources(selectedDataSources, grid.getSelectionModel().getSelectedItem(), PageUtil.getCurrentPageSize(), callback);
            }
        });
        toolBar.add(move);
        setBottomComponent(toolBar);
    }

    public void setDataSources(final List<DataSourceUI> dataSources) {
        selectedDataSources = dataSources;

        AsyncCallback<List<ModelData>> callback = new AsyncCallback<List<ModelData>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<ModelData> result) {
                // Fill store with data providers
                store.removeAll();
                store.add(result);

                for(DataSourceUI dataSourceUI : dataSources) {
                    for(ModelData dataProviderUI : store.getModels()) {
                        if(dataSourceUI.getDataSetParent().getId().equals(dataProviderUI.get("id")))
                            store.remove(dataProviderUI);
                    }
                }
            }
        };
        ((DataManagementServiceAsync) Registry.get(HarvesterUI.DATA_MANAGEMENT_SERVICE)).getAllDataProviders(callback);

    }
}

