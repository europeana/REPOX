package harvesterUI.client.mvc.controllers;

import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.shared.ProjectType;
import harvesterUI.shared.dataTypes.AggregatorUI;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.google.gwt.user.client.History;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-04-2011
 * Time: 20:14
 */
public class HistoryController extends Controller {

    public HistoryController() {
        registerEventTypes(AppEvents.ViewSchemasPanel);
        registerEventTypes(AppEvents.ViewMappingsPanel);
        registerEventTypes(AppEvents.ViewXMApperPanel);
        registerEventTypes(AppEvents.ViewStatistics);
        registerEventTypes(AppEvents.LoadMainData);
//        registerEventTypes(AppEvents.ViewDPImportForm);
        registerEventTypes(AppEvents.ViewOAITest);
        registerEventTypes(AppEvents.ViewScheduledTasksCalendar);
        registerEventTypes(AppEvents.ViewScheduledTasksList);
        registerEventTypes(AppEvents.ViewRunningTasksList);
        registerEventTypes(AppEvents.ViewAdminForm);
        registerEventTypes(AppEvents.ViewUserManagementForm);
        registerEventTypes(AppEvents.ViewRestRecordOperations);
//        registerEventTypes(AppEvents.ViewAccountEditForm);
//        registerEventTypes(AppEvents.ViewAggregatorForm);
//        registerEventTypes(AppEvents.ViewDataProviderForm);
        registerEventTypes(AppEvents.ViewDataSetInfo);
        registerEventTypes(AppEvents.ViewServiceManager);
        registerEventTypes(AppEvents.ViewRssFeedPanel);
        registerEventTypes(AppEvents.ViewTagsManager);
    }

    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.LoadMainData) {
            History.newItem("HOME",false);
        }else if (type == AppEvents.ViewStatistics) {
            History.newItem("STATISTICS",false);
//        }else if (type == AppEvents.ViewDPImportForm) {
//            History.newItem("IMPORT_DATA_PROVIDER",false);
        }else if (type == AppEvents.ViewSchemasPanel) {
            History.newItem("MDR_SCHEMAS",false);
        }else if (type == AppEvents.ViewMappingsPanel) {
            History.newItem("MDR_MAPPINGS",false);
        }else if (type == AppEvents.ViewXMApperPanel) {
            History.newItem("MDR_XMAPPER",false);
        }else if (type == AppEvents.ViewOAITest) {
            History.newItem("OAI_TESTS",false);
        }else if (type == AppEvents.ViewScheduledTasksCalendar) {
            History.newItem("CALENDAR",false);
        }else if (type == AppEvents.ViewScheduledTasksList) {
            History.newItem("SCHEDULED_TASKS",false);
        }else if (type == AppEvents.ViewRunningTasksList) {
            History.newItem("RUNNING_TASKS",false);
        }else if (type == AppEvents.ViewAdminForm) {
            History.newItem("ADMIN_CONFIG",false);
        }else if (type == AppEvents.ViewUserManagementForm) {
            History.newItem("USER_MANAGEMENT",false);
        }else if (type == AppEvents.ViewRestRecordOperations) {
            History.newItem("REST_OPERATIONS",false);
        }else if (type == AppEvents.ViewAccountEditForm) {
            History.newItem("EDIT_ACCOUNT",false);
        }else if (type == AppEvents.ViewServiceManager) {
            History.newItem("EXTERNAL_SERVICES_MANAGER",false);
        }else if (type == AppEvents.ViewRssFeedPanel) {
            History.newItem("RSS_PANEL",false);
        }else if (type == AppEvents.ViewTagsManager) {
            History.newItem("TAGS",false);
        }

        // functions with parameters
        else if (type == AppEvents.ViewAggregatorForm) {
            if(event.getData() instanceof AggregatorUI) {
                AggregatorUI aggregatorUI = (AggregatorUI) event.getData();
                History.newItem("EDIT_AGG?id=" + aggregatorUI.getName(),false);
            } else
                History.newItem("CREATE_AGGREGATOR",false);
        }else if (type == AppEvents.ViewDataProviderForm) {
            if(HarvesterUI.getProjectType() == ProjectType.EUROPEANA) {
                if(event.getData() instanceof DataProviderUI) {
                    DataProviderUI dataProviderUI = (DataProviderUI) event.getData();
                    History.newItem("EDIT_DP?id=" + dataProviderUI.getName(),false);
                } else {
                    AggregatorUI aggregatorUI = (AggregatorUI) event.getData();
                    History.newItem("CREATE_DATA_PROVIDER?aggregatorName=" + aggregatorUI.getName(),false);
                }
            } else {
                if(event.getData() instanceof DataProviderUI) {
                    DataProviderUI dataProviderUI = (DataProviderUI) event.getData();
                    History.newItem("EDIT_DP?id=" + dataProviderUI.getName(),false);
                } else
                    History.newItem("CREATE_DATA_PROVIDER",false);
            }
        }else if (type == AppEvents.ViewDataSetInfo) {
            if(event.getData() instanceof DataSourceUI) {
                DataSourceUI dataSourceUI = (DataSourceUI) event.getData();
                History.newItem("VIEW_DS?id=" + dataSourceUI.getDataSourceSet(),false);
            }
        }
    }

    public void initialize() {
//        accountEditView = new AccountEditView(this);
    }

    private void onInit(AppEvent event) {
//        forwardToView(accountEditView, event);
    }
}
