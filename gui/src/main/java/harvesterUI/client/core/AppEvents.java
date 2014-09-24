/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.core;

import com.extjs.gxt.ui.client.event.EventType;

public class AppEvents {

    public static final EventType Init = new EventType();
//    public static final EventType InitMainAppOnly = new EventType();

    public static final EventType Login = new EventType();
    public static final EventType Logout = new EventType();

//    public static final EventType Error = new EventType();

    public static final EventType ViewDataSetInfo = new EventType();

    public static final EventType ViewAccordingToRole = new EventType();
    public static final EventType ViewOAITest = new EventType();
    public static final EventType ViewOAISpecificSet = new EventType();
    public static final EventType ViewRestRecordOperations = new EventType();
    public static final EventType ViewMoveDataProviderDialog = new EventType();
    public static final EventType ViewMoveDataSetDialog = new EventType();
    public static final EventType ViewAddMappingDialog = new EventType();
    public static final EventType ViewAddSchemaDialog = new EventType();
    public static final EventType ReloadTransformations = new EventType();
    public static final EventType ReloadSchemas = new EventType();

//    public static final EventType FilterData = new EventType();
    public static final EventType RemoveGridOperations = new EventType();
    public static final EventType LoadMainData = new EventType();
//    public static final EventType ReloadAllData = new EventType();
//    public static final EventType ReloadDSInfo = new EventType();
    public static final EventType AutoRefreshData = new EventType();

    public static final EventType ViewUserManagementForm = new EventType();
    public static final EventType ReloadUsers = new EventType();
    public static final EventType ViewAccountEditForm = new EventType();

    public static final EventType ViewAdminForm = new EventType();
    public static final EventType CompareDataSets = new EventType();
//    public static final EventType LoadMainConfigs = new EventType();

    public static final EventType ViewAggregatorForm = new EventType();
    public static final EventType ViewDataProviderForm = new EventType();
    public static final EventType ViewDataSourceForm = new EventType();
    public static final EventType ViewDPImportForm = new EventType();
    public static final EventType HideDataSourceForm = new EventType();

    public static final EventType ViewScheduledTasksCalendar = new EventType();
    public static final EventType ViewScheduledTasksList = new EventType();
    public static final EventType ViewRunningTasksList = new EventType();

    public static final EventType ViewStatistics = new EventType();

    public static final EventType ViewServiceManager = new EventType();
    public static final EventType CreateService = new EventType();

//    public static final EventType FinishRunningTask = new EventType();

    public static final EventType ResetFilter = new EventType();

    public static final EventType ViewSchemasPanel = new EventType();
    public static final EventType ViewMappingsPanel = new EventType();
    public static final EventType ViewXMApperPanel = new EventType();

    // Data Set Actions
    public static final EventType IngestDataSet = new EventType();
    public static final EventType ForceRecordUpdate = new EventType();
    public static final EventType RemoveLogs = new EventType();
    public static final EventType StartExternalService = new EventType();
    public static final EventType EmptyDataSet = new EventType();
    public static final EventType RemoveDataSet = new EventType();
    public static final EventType IngestDataSetSample = new EventType();
    public static final EventType ViewRssFeedPanel = new EventType();

    public static final EventType ViewTagsManager = new EventType();
    public static final EventType ViewTagDialog = new EventType();
    public static final EventType ReloadTags = new EventType();

}
