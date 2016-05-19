package harvesterUI.client;

import harvesterUI.client.core.AppEvents;
import harvesterUI.client.icons.BaseIconsManager;
import harvesterUI.client.icons.task_status.taskStatusIcons;
import harvesterUI.client.language.RepoxConstants;
import harvesterUI.client.mvc.controllers.AccountEditController;
import harvesterUI.client.mvc.controllers.AdministrationController;
import harvesterUI.client.mvc.controllers.AppController;
import harvesterUI.client.mvc.controllers.BrowseController;
import harvesterUI.client.mvc.controllers.DataSetActionsController;
import harvesterUI.client.mvc.controllers.DataSetController;
import harvesterUI.client.mvc.controllers.FormController;
import harvesterUI.client.mvc.controllers.HarvestingController;
import harvesterUI.client.mvc.controllers.HistoryController;
import harvesterUI.client.mvc.controllers.OaiTestController;
import harvesterUI.client.mvc.controllers.RssController;
import harvesterUI.client.mvc.controllers.SchemaMapperController;
import harvesterUI.client.mvc.controllers.ServiceManagerController;
import harvesterUI.client.mvc.controllers.StatisticsController;
import harvesterUI.client.panels.mdr.xmapper.usecase.SaveMappingCtrl;
import harvesterUI.client.servlets.RepoxService;
import harvesterUI.client.servlets.RepoxServiceAsync;
import harvesterUI.client.servlets.dataManagement.AGGService;
import harvesterUI.client.servlets.dataManagement.DPService;
import harvesterUI.client.servlets.dataManagement.DataManagementService;
import harvesterUI.client.servlets.dataManagement.DataManagementServiceAsync;
import harvesterUI.client.servlets.dataManagement.DataSetOperationsService;
import harvesterUI.client.servlets.dataManagement.FilterService;
import harvesterUI.client.servlets.dataManagement.TagsService;
import harvesterUI.client.servlets.dataManagement.search.SearchService;
import harvesterUI.client.servlets.externalServices.ESManagementService;
import harvesterUI.client.servlets.harvest.HarvestOperationsService;
import harvesterUI.client.servlets.harvest.TaskManagementService;
import harvesterUI.client.servlets.rss.RssService;
import harvesterUI.client.servlets.transformations.TransformationsService;
import harvesterUI.client.servlets.userManagement.UserManagementService;
import harvesterUI.client.servlets.userManagement.UserManagementServiceAsync;
import harvesterUI.client.util.CookieManager;
import harvesterUI.client.util.SearchUtilManager;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.dataTypes.admin.MainConfigurationInfo;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.users.UserRole;

import java.util.logging.Logger;

import pt.utl.ist.util.shared.ProjectType;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

//import com.extjs.gxt.ui.client.util.Theme;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
@SuppressWarnings("deprecation")
public class HarvesterUI implements EntryPoint,HistoryListener {

    private static final Logger log = Logger.getLogger(HarvesterUI.class.getName());

    public static final String REPOX_SERVICE = "repoxservice";
    public static final String ES_MANAGEMENT_SERVICE = "serviceManagementService";
    public static final String RSS_SERVICE = "rssService";
    public static final String DP_SERVICE = "dpService";
    public static final String AGG_SERVICE = "aggService";
    public static final String USER_MANAGEMENT_SERVICE = "userManagementService";
    public static final String DATA_SET_OPERATIONS_SERVICE = "dataSetOperationsService";
    public static final String HARVEST_OPERATIONS_SERVICE = "harvestOperationsService";
    public static final String DATA_MANAGEMENT_SERVICE = "dataManagementService";
    public static final String FILTER_SERVICE = "filterService";
    public static final String TASK_MANAGEMENT_SERVICE = "taskManagementService";
    public static final String TRANSFORMATIONS_SERVICE = "transformationsService";
    public static final String TAGS_SERVICE = "tagsService";
    public static final String SEARCH_SERVICE = "searchService";
    public static final String COOKIE_MANAGER = "cookieManager";
    public static final String REQUIRED_STR = "<span class='required_txt'> *</span>";
    public static final BaseIconsManager ICONS = GWT.create(BaseIconsManager.class);
    public static final taskStatusIcons TASK_STATUS_ICONS = GWT.create(taskStatusIcons.class);
    public static final UtilManager UTIL_MANAGER = new UtilManager();
    public static final SearchUtilManager SEARCH_UTIL_MANAGER = GWT.create(SearchUtilManager.class);

    public static final RepoxConstants CONSTANTS = GWT.create(RepoxConstants.class);

    private UserManagementServiceAsync userManagementService;
    private RepoxServiceAsync repoxService;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
//        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
//            public void onUncaughtException(Throwable e) {
//                Window.alert("Error: " + e.getMessage());
//                log.log(Level.SEVERE, e.getMessage(), e);
//                e.printStackTrace();
//            }
//        });
//        GXT.setDefaultTheme(Theme.BLUE, true);

        // Initialize servlets
        repoxService = (RepoxServiceAsync)GWT.create(RepoxService.class);
        Registry.register(REPOX_SERVICE, repoxService);
        Registry.register(COOKIE_MANAGER, new CookieManager());
        Registry.register(ES_MANAGEMENT_SERVICE, GWT.create(ESManagementService.class));
        Registry.register(HARVEST_OPERATIONS_SERVICE, GWT.create(HarvestOperationsService.class));
        Registry.register(RSS_SERVICE, GWT.create(RssService.class));
        Registry.register(DP_SERVICE, GWT.create(DPService.class));
        Registry.register(AGG_SERVICE, GWT.create(AGGService.class));
        Registry.register(DATA_SET_OPERATIONS_SERVICE, GWT.create(DataSetOperationsService.class));
        Registry.register(DATA_MANAGEMENT_SERVICE, GWT.create(DataManagementService.class));
        Registry.register(TASK_MANAGEMENT_SERVICE, GWT.create(TaskManagementService.class));
        Registry.register(TRANSFORMATIONS_SERVICE, GWT.create(TransformationsService.class));
        Registry.register(TAGS_SERVICE, GWT.create(TagsService.class));
        Registry.register(SEARCH_SERVICE, GWT.create(SearchService.class));
        Registry.register(FILTER_SERVICE, GWT.create(FilterService.class));
        userManagementService = (UserManagementServiceAsync)GWT.create(UserManagementService.class);
        Registry.register(USER_MANAGEMENT_SERVICE, userManagementService);
        Registry.register("MAIN_ROOT", this);

        // Add all controllers
        Dispatcher dispatcher = Dispatcher.get();
        dispatcher.addController(new AppController());
        dispatcher.addController(new BrowseController());
        dispatcher.addController(new FormController());
        dispatcher.addController(new DataSetController());
        dispatcher.addController(new HarvestingController());
        dispatcher.addController(new StatisticsController());
        dispatcher.addController(new AdministrationController());
        dispatcher.addController(new OaiTestController());
        dispatcher.addController(new AccountEditController());
        dispatcher.addController(new SchemaMapperController());
        dispatcher.addController(new ServiceManagerController());
        dispatcher.addController(new DataSetActionsController());
        dispatcher.addController(new RssController());
        //XMApper Controllers
        dispatcher.addController(new SaveMappingCtrl());

//        checkFirstTimeRepoxUsed();

        startRepoxData();
        History.addHistoryListener(this);
    }

    private void checkWithServerIfSessionIdIsStillLegal(final String sessionId) {
        AsyncCallback<String> acSessionValid = new AsyncCallback<String>(){
            public void onFailure(Throwable caught) {
                Window.alert("Error: session could not be loaded.");
            }
            public void onSuccess(String userRole) {
                String username = Cookies.getCookie(CookieManager.LOGGED_USERNAME);
                String role = Cookies.getCookie(CookieManager.LOGGED_USER_ROLE);
                String language = Cookies.getCookie(CookieManager.REPOX_LANGUAGE);
                if(language == null || language.isEmpty())
                    language = "en";

                if(!UTIL_MANAGER.isDefaultLanguage(language) && !UtilManager.getUrlLocaleLanguage().equals(language))
                    Window.Location.assign(UtilManager.getServerUrl() + "?locale=" + language);
                else{
                    Dispatcher.get().addController(new HistoryController());
                    HarvesterUI.UTIL_MANAGER.setLoggedUser(username,role);
                    UtilManager.sendUserActivityData();
                    Dispatcher.forwardEvent(AppEvents.Init);
                    Dispatcher.forwardEvent(AppEvents.ViewAccordingToRole);

                    Window.addWindowClosingHandler(new Window.ClosingHandler() {
                        public void onWindowClosing(Window.ClosingEvent closingEvent) {
                            closingEvent.setMessage("Do you really want to leave the page?");
                        }
                    });
                }
            }
        };
        userManagementService.validateSessionId(sessionId, acSessionValid);
    }

    public void onHistoryChanged(String historyToken) {
        String params = "[?]+";
        String[] tokensFunc = historyToken.split(params);

        if(tokensFunc.length > 1) {
            // functions with parameters
            String function = tokensFunc[0];
            String paramDelim = "[=]+";
            String[] tokensPrms = tokensFunc[1].split(paramDelim);
            String firstParamID = tokensPrms[0];
            String firstParamValue = tokensPrms[1];

            // types of functions
//            if(function.equals("EDIT_AGG")) {
//                AggregatorUI aggregatorUI = UtilManager.getAggregatorUI(firstParamValue);
//                if(aggregatorUI == null) {
//                    HarvesterUI.UTIL_MANAGER.getErrorBox("Edit Aggregator", "No Aggregator found.");
//                    Dispatcher.forwardEvent(AppEvents.ReloadAllData);
//                } else{
//                    Dispatcher.forwardEvent(AppEvents.ReloadAllData);
//                    Dispatcher.forwardEvent(AppEvents.ViewAggregatorForm, aggregatorUI);
//                }
//            }else if(function.equals("EDIT_DP")) {
//                DataProviderUI dataProviderUI = UtilManager.getDataProviderUI(firstParamValue);
//                if(dataProviderUI == null) {
//                    HarvesterUI.UTIL_MANAGER.getErrorBox("Edit Data Provider", "No Data Provider found.");
//                    Dispatcher.forwardEvent(AppEvents.ReloadAllData);
//                } else{
//                    Dispatcher.forwardEvent(AppEvents.ReloadAllData);
//                    Dispatcher.forwardEvent(AppEvents.ViewDataProviderForm, dataProviderUI);
//                }
//            } else
            if(function.equals("VIEW_DS")) {
                AsyncCallback<DataSourceUI> callback = new AsyncCallback<DataSourceUI>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(DataSourceUI dataSourceUI) {
                        if(dataSourceUI == null) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.viewDataSet(), CONSTANTS.noDataSetFound());
                            Dispatcher.forwardEvent(AppEvents.LoadMainData);
                        } else
                            Dispatcher.forwardEvent(AppEvents.ViewDataSetInfo, dataSourceUI);
                    }
                };
                DataManagementServiceAsync service = (DataManagementServiceAsync) Registry.get(HarvesterUI.DATA_MANAGEMENT_SERVICE);
                service.getDataSetInfo(firstParamValue, callback);
            }

            // Special case for Europeana
//            if(function.equals("CREATE_DATA_PROVIDER")) {
//                AggregatorUI aggregatorUI = UtilManager.getAggregatorUI(firstParamValue);
//                if(aggregatorUI == null) {
//                    HarvesterUI.UTIL_MANAGER.getErrorBox("Edit Aggregator", "No Aggregator found.");
//                    Dispatcher.forwardEvent(AppEvents.ReloadAllData);
//                } else{
//                    Dispatcher.forwardEvent(AppEvents.ReloadAllData);
//                    Dispatcher.forwardEvent(AppEvents.ViewDataProviderForm, aggregatorUI);
//                }
//            }
        } else {
            if(historyToken.equals("HOME"))
                Dispatcher.forwardEvent(AppEvents.LoadMainData);
            else
            if(historyToken.equals("STATISTICS"))
                Dispatcher.forwardEvent(AppEvents.ViewStatistics);
            else if(historyToken.equals("CREATE_DATA_PROVIDER")){
                Dispatcher.forwardEvent(AppEvents.LoadMainData);
                Dispatcher.forwardEvent(AppEvents.ViewDataProviderForm);
            }else if(historyToken.equals("CREATE_AGGREGATOR")){
                Dispatcher.forwardEvent(AppEvents.LoadMainData);
                Dispatcher.forwardEvent(AppEvents.ViewAggregatorForm);
            }else if(historyToken.equals("IMPORT_DATA_PROVIDER")){
                Dispatcher.forwardEvent(AppEvents.LoadMainData);
                Dispatcher.forwardEvent(AppEvents.ViewDPImportForm);
            }else if(historyToken.equals("MDR_SCHEMAS"))
                Dispatcher.forwardEvent(AppEvents.ViewSchemasPanel);
            else if(historyToken.equals("MDR_MAPPINGS"))
                Dispatcher.forwardEvent(AppEvents.ViewMappingsPanel);
            else if(historyToken.equals("MDR_XMAPPER"))
                Dispatcher.forwardEvent(AppEvents.ViewXMApperPanel);
            else if(historyToken.equals("OAI_TESTS"))
                Dispatcher.forwardEvent(AppEvents.ViewOAITest);
                // harvest menu tasks
            else if(historyToken.equals("CALENDAR"))
                Dispatcher.forwardEvent(AppEvents.ViewScheduledTasksCalendar);
            else if(historyToken.equals("SCHEDULED_TASKS"))
                Dispatcher.forwardEvent(AppEvents.ViewScheduledTasksList);
            else if(historyToken.equals("RUNNING_TASKS"))
                Dispatcher.forwardEvent(AppEvents.ViewRunningTasksList);

            else if(historyToken.equals("ADMIN_CONFIG"))
                Dispatcher.forwardEvent(AppEvents.ViewAdminForm);
            else if(historyToken.equals("USER_MANAGEMENT"))
                Dispatcher.forwardEvent(AppEvents.ViewUserManagementForm);
//            else if(historyToken.equals("REST_OPERATIONS"))
//                Dispatcher.forwardEvent(AppEvents.ViewRestRecordOperations);
            else if(historyToken.equals("EDIT_ACCOUNT")){
                Dispatcher.forwardEvent(AppEvents.LoadMainData);
                Dispatcher.forwardEvent(AppEvents.ViewAccountEditForm);
            }else if(historyToken.equals("EXTERNAL_SERVICES_MANAGER"))
                Dispatcher.forwardEvent(AppEvents.ViewServiceManager);
            else if(historyToken.equals("RSS_PANEL"))
                Dispatcher.forwardEvent(AppEvents.ViewRssFeedPanel);
            else if(historyToken.equals("TAGS"))
                Dispatcher.forwardEvent(AppEvents.ViewTagsManager);

            if(!historyToken.equals("HOME"))
                UtilManager.unmaskCentralPanel();
        }
    }

    public static ProjectType getProjectType(){
        return getMainConfigurationData().getProjectType();
    }

    public static MainConfigurationInfo getMainConfigurationData(){
        return (MainConfigurationInfo)Registry.get("MAIN_CONFIGURATION_DATA");
    }

    private void startRepoxData(){
        // Get Project Version
        AsyncCallback<MainConfigurationInfo> projectTypeCall = new AsyncCallback<MainConfigurationInfo>(){
            public void onFailure(Throwable caught) {
                Window.alert("Error: session could not be loaded.");
            }
            public void onSuccess(MainConfigurationInfo mainConfigurationInfo) {
                Registry.register("MAIN_CONFIGURATION_DATA", mainConfigurationInfo);

                // Anonymous case for direct link
                if(History.getToken().equals("ANONYMOUS")){
                    History.removeHistoryListener((HarvesterUI)Registry.get("MAIN_ROOT"));
                    HarvesterUI.UTIL_MANAGER.setLoggedUser(HarvesterUI.CONSTANTS.anonymous(), UserRole.ANONYMOUS.name());
                    Dispatcher.forwardEvent(AppEvents.Init);
                    Dispatcher.forwardEvent(AppEvents.ViewAccordingToRole);
                    UtilManager.sendUserActivityData();
                    return;
                }

                // Check saved session and login info
                String sessionID = Cookies.getCookie("sid");
                if(sessionID != null) {
                    checkWithServerIfSessionIdIsStillLegal(sessionID);
                }
                else {
                    // Check if language already set
                    String language = Cookies.getCookie("tempLang");
                    if(language != null){
                        UTIL_MANAGER.loadUserWithLanguage();
                    }else {
                        Dispatcher.forwardEvent(AppEvents.Login);
                        GXT.hideLoadingPanel("loading");
                    }
                }
            }
        };
        repoxService.getInitialConfigData(projectTypeCall);
    }

//    private void checkFirstTimeRepoxUsed(){
//        //Check if its a first time use
//        AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>(){
//            public void onFailure(Throwable caught) {
//                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
//            }
//            public void onSuccess(Boolean isFirst) {
//                if(isFirst){
//                    GXT.hideLoadingPanel("loading");
//                    FirstTimeRepoxUsedDialog firstTimeRepoxUsedDialog = new FirstTimeRepoxUsedDialog();
//                    firstTimeRepoxUsedDialog.show();
//                    firstTimeRepoxUsedDialog.center();
//                }else{
//                    startRepoxData();
//                }
//            }
//        };
//        UserManagementServiceAsync userMangService = (UserManagementServiceAsync)Registry.get(USER_MANAGEMENT_SERVICE);
//        userMangService.isFirstTimeRepoxUsed(callback);
//    }
}

