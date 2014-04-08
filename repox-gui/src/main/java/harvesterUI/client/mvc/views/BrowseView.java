/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 *
 * http://extjs.com/license
 */
package harvesterUI.client.mvc.views;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.browse.BrowseFilterPanel;
import harvesterUI.client.panels.overviewGrid.MainGrid;
import harvesterUI.client.panels.overviewGrid.contextMenus.MoveDataProviderDialog;
import harvesterUI.client.panels.overviewGrid.contextMenus.MoveDataSetDialog;
import harvesterUI.client.servlets.dataManagement.DataSetOperationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.dataSet.DataSetStatus;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

import java.util.List;
import java.util.Map;

public class BrowseView extends View {

    private MainGrid mainGrid;
    private BrowseFilterPanel browseFilterPanel;
    private MoveDataProviderDialog moveDataProviderDialog;
    private MoveDataSetDialog moveDataSetDialog;

    public BrowseView(Controller controller) {
        super(controller);
    }

    protected void initUI() {

    }

    @Override
    protected void handleEvent(final AppEvent event) {
        if (event.getType() == AppEvents.Init) {
            initUI();
        }else if (event.getType() == AppEvents.LoadMainData) {
            UtilManager.unmaskCentralPanel();

            LayoutContainer wrapper = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);

            UtilManager.maskCentralPanel(HarvesterUI.CONSTANTS.loadingMainData());

            if(wrapper.getItem(0) != mainGrid.getMainGridPanel()) {
                wrapper.removeAll();
                wrapper.add(mainGrid.getMainGridPanel());
                wrapper.layout();
            }

            // Check if filter are applied or search
            if(!mainGrid.getTopToolbar().getSearchCombo().getRawValue().isEmpty() &&
                    mainGrid.getTopToolbar().getSearchCombo().getLastSavedSearch() != null)
                HarvesterUI.UTIL_MANAGER.getMainGridSearchResults();
            else
                mainGrid.refreshGrid();

            mainGrid.getPagingToolBar().setEnabled(true);
        }
        else if (event.getType() == AppEvents.ViewMoveDataProviderDialog) {
            moveDataProviderDialog.setDataProviders((List<DataProviderUI>) event.getData());
            moveDataProviderDialog.show();
            moveDataProviderDialog.center();
        }else if (event.getType() == AppEvents.ViewMoveDataSetDialog) {
            moveDataSetDialog.setDataSources((List<DataSourceUI>) event.getData());
            moveDataSetDialog.show();
            moveDataSetDialog.center();
        }else if (event.getType() == AppEvents.AutoRefreshData) {
            AsyncCallback<Map<String,DataSetStatus>> callback = new AsyncCallback<Map<String,DataSetStatus>>() {
                public void onFailure(Throwable caught) {
                    new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                }
                public void onSuccess(Map<String,DataSetStatus> result) {
                    for(DataContainer model : mainGrid.getMainDataGrid().getStore().getModels()) {
                        if(model instanceof DataProviderUI && ((DataProviderUI) model).getDataSourceUIList().size() == 1){
                            updateDataSet(((DataProviderUI) model).getDataSourceUIList().get(0), result);
                        }else if(model instanceof DataSourceUI) {
                            DataSourceUI dataSourceUI = (DataSourceUI) model;
                            updateDataSet(dataSourceUI, result);
                        }
                    }
                    mainGrid.getMainDataGrid().getView().refresh(false);
                    mainGrid.resetScrollBarPos();
                }
            };
            DataSetOperationsServiceAsync service = (DataSetOperationsServiceAsync) Registry.get(HarvesterUI.DATA_SET_OPERATIONS_SERVICE);
            service.getAllDataSourceStatus(mainGrid.getMainDataGrid().getStore().getModels(),callback);
        }else if (event.getType() == AppEvents.RemoveGridOperations) {
            mainGrid.getMainGridPanel().setTopComponent(null);
            mainGrid.getMainDataGrid().removeAllListeners();
        }
    }

    @Override
    protected void initialize() {
//        attributeManager = new AttributeManager();
//        Registry.register("attributeManager",attributeManager);

        browseFilterPanel = new BrowseFilterPanel();
        Registry.register("browseFilterPanel",browseFilterPanel);

        moveDataProviderDialog = new MoveDataProviderDialog();
        moveDataSetDialog = new MoveDataSetDialog();

//        MainDataManager mainDataManager = new MainDataManager();
//        Registry.register("mainDataManager", mainDataManager);

        mainGrid = new MainGrid();
        Registry.register("mainGrid",mainGrid);
    }

    private void updateDataSet(DataSourceUI dataSourceUI, Map<String, DataSetStatus> result){
        // Get Status
        if(dataSourceUI.getDataSourceSet() != null && result.get(dataSourceUI.getDataSourceSet()) != null) {
            if(result.get(dataSourceUI.getDataSourceSet()).get("status") != null)
                dataSourceUI.setStatus((String)result.get(dataSourceUI.getDataSourceSet()).get("status"));
            else
                dataSourceUI.setStatus("");
            // Set Old Tasks
//            List<OldTaskUI> oldTaskUIList = (List<OldTaskUI>)result.get(dataSourceUI.getDataSourceSet()).get("oldTasks");
//            for(OldTaskUI oldTaskUI:oldTaskUIList) {
//                oldTaskUI.setDataSourceUI(dataSourceUI);
//            }
//            dataSourceUI.setOldTasks(oldTaskUIList);
            // Set Records
            dataSourceUI.setRecords((String)result.get(dataSourceUI.getDataSourceSet()).get("recordNum"));
            if(!dataSourceUI.getOaiSource().equals("") && result.get(dataSourceUI.getDataSourceSet()).get("totalRecordNum") != null) {
                dataSourceUI.setTotalRecords((Integer)result.get(dataSourceUI.getDataSourceSet()).get("totalRecordNum"));
                dataSourceUI.setTotalRecordsStr((String)result.get(dataSourceUI.getDataSourceSet()).get("totalRecordNumStr"));
                dataSourceUI.setIngestPercentage((Float) result.get(dataSourceUI.getDataSourceSet()).get("ingestPercentage"));
                dataSourceUI.setIngestTimeLeft((Long) result.get(dataSourceUI.getDataSourceSet()).get("ingestTimeLeft"));
            }
        }
    }
}
