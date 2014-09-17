package harvesterUI.client.mvc.controllers;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.models.SingleExternalServiceStartData;
import harvesterUI.client.mvc.views.AppView;
import harvesterUI.client.servlets.dataManagement.DataSetOperationsServiceAsync;
import harvesterUI.client.servlets.harvest.HarvestOperationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 12:48
 */
public class DataSetActionsController extends Controller {

    private DataSetOperationsServiceAsync dataSetOperationsService;
    private HarvestOperationsServiceAsync harvestOperationsService;

    public DataSetActionsController() {
        registerEventTypes(AppEvents.IngestDataSet);
        registerEventTypes(AppEvents.EmptyDataSet);
        registerEventTypes(AppEvents.RemoveDataSet);
        registerEventTypes(AppEvents.StartExternalService);
//        registerEventTypes(AppEvents.MoveDataSet);
        registerEventTypes(AppEvents.IngestDataSetSample);
        registerEventTypes(AppEvents.ForceRecordUpdate);
        registerEventTypes(AppEvents.RemoveLogs);
//        registerEventTypes(AppEvents.ScheduleDataSetIngest);
//        registerEventTypes(AppEvents.ScheduleDataSetExport);
//        registerEventTypes(AppEvents.ExportDataSetNow);

        dataSetOperationsService = (DataSetOperationsServiceAsync) Registry.get(HarvesterUI.DATA_SET_OPERATIONS_SERVICE);
        harvestOperationsService = (HarvestOperationsServiceAsync) Registry.get(HarvesterUI.HARVEST_OPERATIONS_SERVICE);
    }

    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.IngestDataSet)
            onStartHarvest(event);
        else if (type == AppEvents.EmptyDataSet)
            onEmptyDataSet(event);
        else if (type == AppEvents.RemoveDataSet)
            onRemoveDataSet(event);
        else if (type == AppEvents.StartExternalService)
            onStartExternalService(event);
        else if (type == AppEvents.RemoveLogs)
            removeLogs(event);
//        else if (type == AppEvents.MoveDataSet)
//            onMoveDataSet(event);
        else if (type == AppEvents.IngestDataSetSample)
            onIngestDataSetSample(event);
        else if (type == AppEvents.ForceRecordUpdate)
            onForceUpdateRecords(event);
//        else if (type == AppEvents.ScheduleDataSetIngest)
//            onScheduleIngestDataSet(event);
//        else if (type == AppEvents.ScheduleDataSetExport)
//            onScheduleExportDataSet(event);
//        else if (type == AppEvents.ExportDataSetNow)
//            onExportDataSetNow(event);
    }

    public void initialize() {
//        administrationView = new AdministrationView(this);
    }

    private void onStartHarvest(AppEvent event) {
        final List<DataSourceUI> dataSourcesSelectedUI = event.getData();
        AsyncCallback<String> callback = new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(String result) {
                if(result.equals("NO_DS_FOUND")) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.ingestNow(), HarvesterUI.CONSTANTS.dataSetNotFound());
                    return;
                }
                else if(result.equals("TASK_EXECUTING")) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.ingestNow(), HarvesterUI.CONSTANTS.taskAlreadyExecuting());
                    return;
                }
                dataSourcesSelectedUI.get(0).setStatus("RUNNING");
                History.fireCurrentHistoryState();
                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.ingestNow(), HarvesterUI.CONSTANTS.harvestWillStart());
            }
        };
        harvestOperationsService.dataSourceIngestNow(dataSourcesSelectedUI, callback);
    }

    private void onEmptyDataSet(AppEvent event){
        UtilManager.maskCentralPanel(HarvesterUI.CONSTANTS.emptyingDataSetMask());
        final List<DataSourceUI> dataSourcesSelectedUI = event.getData();
        AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(Boolean result) {
                if(!result) {
                    UtilManager.unmaskCentralPanel();
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.emptyDataSet(), HarvesterUI.CONSTANTS.dataSetNotFound());
                    return;
                }

                dataSourcesSelectedUI.get(0).setStatus(null);
                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.emptyDataSet(), HarvesterUI.CONSTANTS.emptySuccessful());
                UtilManager.unmaskCentralPanel();
                History.fireCurrentHistoryState();
            }
        };
        harvestOperationsService.dataSourceEmpty(dataSourcesSelectedUI, callback);
    }

    private void onRemoveDataSet(AppEvent event){
        final List<DataSourceUI> dataSourcesSelectedUI = event.getData();
        final LayoutContainer wrapper = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
        AsyncCallback callback = new AsyncCallback() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(Object result) {
                wrapper.unmask();
                HarvesterUI.SEARCH_UTIL_MANAGER.dataSetSearchedDeleted(dataSourcesSelectedUI);
                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.deleteDataSets(), HarvesterUI.CONSTANTS.dataSetDeleted());
                Dispatcher.forwardEvent(AppEvents.LoadMainData);
            }
        };
        wrapper.mask(HarvesterUI.CONSTANTS.removeDataSetMask());
        dataSetOperationsService.deleteDataSources(dataSourcesSelectedUI, callback);
    }

    private void onIngestDataSetSample(AppEvent event){
        final List<DataSourceUI> dataSourcesSelectedUI = event.getData();
        AsyncCallback<String> callback = new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(String result) {
                if(result.equals("NO_DS_FOUND")) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.ingestSample(), HarvesterUI.CONSTANTS.dataSetNotFound());
                    return;
                }
                else if(result.equals("TASK_EXECUTING")) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.ingestSample(), HarvesterUI.CONSTANTS.taskAlreadyExecuting());
                    return;
                }

                dataSourcesSelectedUI.get(0).setStatus("RUNNING");
                History.fireCurrentHistoryState();
                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.ingestSample(), HarvesterUI.CONSTANTS.harvestWillStart());
            }
        };
        harvestOperationsService.dataSourceIngestSample(dataSourcesSelectedUI, callback);
    }

    private void onStartExternalService(AppEvent event){
        UtilManager.maskCentralPanel("Starting external service...");
        final SingleExternalServiceStartData singleExternalServiceStartData = event.getData();
        AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(ResponseState res) {
                if(res != ResponseState.SUCCESS) {
                    UtilManager.unmaskCentralPanel();
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.emptyDataSet(), HarvesterUI.CONSTANTS.dataSetNotFound());
                    return;
                }

                HarvesterUI.UTIL_MANAGER.getSaveBox("Start External Service", "External service: " + singleExternalServiceStartData.getExternalServiceId() + " started successfully.");
                UtilManager.unmaskCentralPanel();
                History.fireCurrentHistoryState();
            }
        };
        dataSetOperationsService.startSingleExternalService(singleExternalServiceStartData.getExternalServiceId(),singleExternalServiceStartData.getDataSetId(), callback);
    }

    private void onForceUpdateRecords(AppEvent event) {
        final List<DataSourceUI> dataSourcesSelectedUI = event.getData();
        AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(ResponseState responseState) {
                if(responseState == ResponseState.ERROR) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox("Force Record Update", "Error forcing record update");
                    return;
                }
                History.fireCurrentHistoryState();
                HarvesterUI.UTIL_MANAGER.getSaveBox("Force Record Update", "Records updated successfully");
            }
        };
        dataSetOperationsService.forceDataSetRecordUpdate(dataSourcesSelectedUI, callback);
    }

    private void removeLogs(final AppEvent event) {
        final SelectionListener<ButtonEvent> emptyDataSetListener = new SelectionListener<ButtonEvent> () {
            public void componentSelected(ButtonEvent be) {
                final List<DataSourceUI> dataSourcesSelectedUI = event.getData();
                AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(ResponseState responseState) {
                        if(responseState == ResponseState.ERROR) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox("Clear Logs", "Error clearing logs");
                            return;
                        }
                        History.fireCurrentHistoryState();
                        HarvesterUI.UTIL_MANAGER.getSaveBox("Clear Logs", "Logs cleared successfully");
                    }
                };
                dataSetOperationsService.clearLogsAndOldTasks(dataSourcesSelectedUI, callback);
            }
        };
        HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), "Are you sure you want to remove all log files?", emptyDataSetListener);
    }

    private void onScheduleIngestDataSet(AppEvent event){

    }

    private void onScheduleExportDataSet(AppEvent event){

    }

    private void onExportDataSetNow(AppEvent event){
    }
}
