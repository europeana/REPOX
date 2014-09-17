package harvesterUI.client.util;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.controllers.HistoryController;
import harvesterUI.client.mvc.views.AppView;
import harvesterUI.client.panels.overviewGrid.MainGrid;
import harvesterUI.client.panels.overviewGrid.TooltipListener;
import harvesterUI.client.servlets.dataManagement.DataManagementServiceAsync;
import harvesterUI.client.servlets.userManagement.UserManagementServiceAsync;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.servletResponseStates.RepoxServletResponseStates;
import harvesterUI.shared.tasks.OldTaskUI;
import harvesterUI.shared.users.User;
import harvesterUI.shared.users.UserRole;

import java.util.Date;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 04-04-2011
 * Time: 18:37
 */
@SuppressWarnings("deprecation")
public class UtilManager {

    public static int DEFAULT_HBOX_BOTTOM_MARGIN = 4;
    public static int DEFAULT_HBOX_RIGHT_MARGIN = 2;
    public static int SPECIAL_HBOX_BOTTOM_MARGIN = 2;
    public static int DEFAULT_DATASET_VIEWINFO_LABEL_WIDTH = 170;
    public static int SPECIAL_DATASET_VIEWINFO_LABEL_WIDTH = 173;

    //    private String sessionID;
    protected static int HIDE_DELAY = 5000;

    public UtilManager() {}

    public void getErrorBox(String title, String text) {
        new AlertMessageBox(HIDE_DELAY,title,text,true,AlertMessageBox.ERROR_MESSAGE).show();
    }

    public void getInfoBox(String title, String text) {
        new AlertMessageBox(HIDE_DELAY,title,text,true,AlertMessageBox.INFORMATION_MESSAGE).show();
    }

    public void getSaveBox(String title, String text) {
        new AlertMessageBox(HIDE_DELAY,title,text,true,AlertMessageBox.SUCCESS_MESSAGE).show();
    }

    public void createConfirmMessageBox(String title, String msg, SelectionListener<ButtonEvent> buttonListener){
        ConfirmMessageBox confirmMessageBox = new ConfirmMessageBox(title,msg,buttonListener);
        confirmMessageBox.show();
        confirmMessageBox.center();
    }

    public static Image createOAIImage() {
        Image image = new Image();
        image.addMouseDownHandler(new MouseDownHandler(){
            public void onMouseDown(MouseDownEvent event) {
                Dispatcher.get().dispatch(AppEvents.ViewOAITest);
            }
        });

        image.setStyleName("hyperlink_icon");
        image.setSize("16","16");
        image.setUrl("resources/images/icons/network-transmit.png");
        image.addMouseListener( new TooltipListener( "&nbsp;"+HarvesterUI.CONSTANTS.testOaiPMH()+"&nbsp;", 1000,"toolTip"));
        return image;
    }

    public static void maskCentralPanel(String text) {
        LayoutContainer wrapper = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
        wrapper.mask(text);
    }

    public static void unmaskCentralPanel() {
        LayoutContainer wrapper = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
        wrapper.unmask();
    }

    public static String formatIntoHHMMSS(long secsIn) {
        long hours = secsIn / 3600;
        long remainder = secsIn % 3600;
        long minutes = remainder / 60;
        long seconds = remainder % 60;

        return ( (hours < 10 ? "0" : "") + hours
                + ":" + (minutes < 10 ? "0" : "") + minutes
                + ":" + (seconds< 10 ? "0" : "") + seconds );
    }

    // Related to metadata format box creation

    public static String getSchema(String metadataFormat){
        if(metadataFormat.equalsIgnoreCase("ese")){
            return "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd";
        }
        else if(metadataFormat.equalsIgnoreCase("MarcXchange")|| (metadataFormat.equalsIgnoreCase("ISO2709"))){
            return "info:lc/xmlns/marcxchange-v1.xsd";
        }
        else if(metadataFormat.equalsIgnoreCase("tel")){
            return "http://krait.kb.nl/coop/tel/handbook/telterms.html";
        }
        else if(metadataFormat.equalsIgnoreCase("oai_dc")){
            return "http://www.openarchives.org/OAI/2.0/oai_dc.xsd";
        }
        else if(metadataFormat.equalsIgnoreCase("NLM-AI") ||
                metadataFormat.equalsIgnoreCase("NLM-Book")){
            return "ncbi-mathml2/mathml2.xsd";
        }
        else if(metadataFormat.equalsIgnoreCase("lido")){
            return "http://www.lido-schema.org/schema/v1.0/lido-v1.0.xsd";
        }
        else if(metadataFormat.equalsIgnoreCase("edm")){
            return "http://www.europeana.eu/schemas/edm/EDM.xsd";
        }
        return "";
    }

    public static String getNamespace(String metadataFormat){
        if(metadataFormat.equalsIgnoreCase("ese")){
            return "http://www.europeana.eu/schemas/ese/";
        }
        else if(metadataFormat.equalsIgnoreCase("MarcXchange")|| (metadataFormat.equalsIgnoreCase("ISO2709"))){
            return "info:lc/xmlns/marcxchange-v1";
        }
        else if(metadataFormat.equalsIgnoreCase("tel")){
            return "http://krait.kb.nl/coop/tel/handbook/telterms.html";
        }
        else if(metadataFormat.equalsIgnoreCase("oai_dc")){
            return "http://www.openarchives.org/OAI/2.0/";
        }
        else if(metadataFormat.equalsIgnoreCase("NLM-AI") ||
                metadataFormat.equalsIgnoreCase("NLM-Book")){
            return "http://www.w3.org/1998/Math/MathML";
        }
        else if(metadataFormat.equalsIgnoreCase("lido")){
            return "http://www.lido-schema.org";
        }
        else if(metadataFormat.equalsIgnoreCase("edm")){
            return "http://www.europeana.eu/schemas/edm/";
        }
        return "";
    }

    public static void showLog(DataSourceUI dataSourceUI){
        String serverUrl;
        if(Window.Location.getHost().contains("127.0.0.1:8888"))
            serverUrl = "http://" + Window.Location.getHost() + "/";
        else
            serverUrl = getServerUrl();

//        System.out.println(serverUrl);
        Window.open(serverUrl + "harvesterui/openLogFile?dataSetId=" +  dataSourceUI.getDataSourceSet(), "_blank", "");
    }

    public static void exportDataFile(){
        String serverUrl;
        if(Window.Location.getHost().contains("127.0.0.1:8888"))
            serverUrl = "http://" + Window.Location.getHost() + "/";
        else
            serverUrl = getServerUrl();

//        System.out.println(serverUrl);
        Window.open(serverUrl + "harvesterui/exportDataFile", "_blank", "");
    }

    public static void showLogFromByLogName(OldTaskUI taskUI) {
        String serverUrl;
        if(Window.Location.getHost().contains("127.0.0.1:8888"))
            serverUrl = "http://" + Window.Location.getHost() + "/";
        else
            serverUrl = getServerUrl();

//        System.out.println(serverUrl);
        Window.open(serverUrl + "harvesterui/openLogFile?dataSetId=" +  taskUI.getDataSetId() + "&logName=" + taskUI.getLogName(), "_blank", "");
    }

    public static void showTransformationResultLog(String dataSetId, String transformationId){
        String serverUrl;
        if(Window.Location.getHost().contains("127.0.0.1:8888"))
            serverUrl = "http://" + Window.Location.getHost() + "/";
        else
            serverUrl = getServerUrl();

        System.out.println(serverUrl);
        Window.open(serverUrl + "harvesterui/openTransformationResultLogFile?dataSetId=" + dataSetId + "&transformationId=" + transformationId, "_blank", "");
    }

    public static String getOaiServerUrl(){
        String serverUrl;
        if(Window.Location.getHost().contains("127.0.0.1:8888")) {
            String url = Window.Location.getHost();
            if(url.contains("#")) {
                String paramDelim = "#";
                String[] tokensPrms = url.split(paramDelim);
                String onlyUrl = tokensPrms[0];
                serverUrl = "http://" + onlyUrl + "/OAIHandler";
            } else
                serverUrl = "http://" + url + "/OAIHandler";
        } else {
            serverUrl = getServerUrl() + "OAIHandler";
        }
        return serverUrl;
    }

    public static String getServerUrl(){
        if(Window.Location.getHost().contains("127.0.0.1:8888")) {
            return "http://127.0.0.1:8888/HarvesterUI.html?gwt.codesvr=127.0.0.1:9997";
        }
        else{
            String[] noHistoryCurrLocArray = Window.Location.getHref().split("#");
            String noHistoryCurrLoc = noHistoryCurrLocArray[0];
            String[] serverArray = noHistoryCurrLoc.split("\\?");
            return serverArray[0];
        }
    }

    public static String getUrlLocaleLanguage(){
        try{
            String[] serverArray = Window.Location.getHref().split("\\?locale");
            String[] languageArray = serverArray[1].split("=");
            String[] languageNoHistoryArray = languageArray[1].split("#");
            return languageNoHistoryArray[0];
        } catch (Exception e){
            return "";
        }
    }

    public boolean isDefaultLanguage(String language){
        return language.equals("en");
    }

    public void loadUserWithLanguage(){
        AsyncCallback<User> callback = new AsyncCallback<User>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(User user) {
                String language = Cookies.getCookie(CookieManager.REPOX_LANGUAGE);
                if(language == null || language.isEmpty())
                    language = "en";

                if(!isDefaultLanguage(language) && !UtilManager.getUrlLocaleLanguage().equals(language))
                    Window.Location.assign(UtilManager.getServerUrl() + "?locale=" + language);
                else{
                    Dispatcher.get().addController(new HistoryController());
                    HarvesterUI.UTIL_MANAGER.setLoggedUser(user.getUserName(),user.getRole());
                    Dispatcher.forwardEvent(AppEvents.Init);
                    Dispatcher.forwardEvent(AppEvents.ViewAccordingToRole);

                    Window.addWindowClosingHandler(new Window.ClosingHandler() {
                        public void onWindowClosing(Window.ClosingEvent closingEvent) {
                            closingEvent.setMessage("Do you really want to leave the page?");
                        }
                    });
                    Cookies.removeCookie("tempLang");
                    Cookies.removeCookie("tempUsername");
                }
            }
        };
        UserManagementServiceAsync userManagementService = (UserManagementServiceAsync)Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);
        String usrName = Cookies.getCookie("tempUsername");
        userManagementService.getUser(usrName,callback);
    }

    public void setImageUrl(ImageButton imageButton, String status) {
        ToolTipConfig config = new ToolTipConfig();
        config.setShowDelay(1000);
        String viewLog = "<div style='text-align: center;'>" + HarvesterUI.CONSTANTS.viewLog() + "</div>";
        if(status.equals("RUNNING")){
            imageButton.setIcon(HarvesterUI.TASK_STATUS_ICONS.running());
            config.setTitle(HarvesterUI.CONSTANTS.running());
            config.setText(viewLog);
            imageButton.setToolTip(config);
        }else if(status.equals("WARNING")){
            imageButton.setIcon(HarvesterUI.TASK_STATUS_ICONS.warning());
            config.setTitle(HarvesterUI.CONSTANTS.warning());
            config.setText(viewLog);
            imageButton.setToolTip(config);
        }else if(status.equals("RETRYING")){
            imageButton.setIcon(HarvesterUI.TASK_STATUS_ICONS.retrying());
            config.setTitle(HarvesterUI.CONSTANTS.taskFailedRetry());
            config.setText(HarvesterUI.CONSTANTS.viewRunningTasks());
            imageButton.setToolTip(config);
        }else if(status.equals("ERROR")){
            imageButton.setIcon(HarvesterUI.TASK_STATUS_ICONS.error());
            config.setTitle(HarvesterUI.CONSTANTS.error());
            config.setText(viewLog);
            imageButton.setToolTip(config);
        }else if(status.equals("CANCELED")){
            imageButton.setIcon(HarvesterUI.TASK_STATUS_ICONS.canceled_task());
            config.setTitle(HarvesterUI.CONSTANTS.taskCanceled());
            config.setText(viewLog);
            imageButton.setToolTip(config);
        }else if(status.equals("OK")){
            imageButton.setIcon(HarvesterUI.TASK_STATUS_ICONS.ok());
            config.setTitle(HarvesterUI.CONSTANTS.ingestSuccessful());
            config.setText(viewLog);
            imageButton.setToolTip(config);
        }else if(status.equals("PRE_PROCESSING")){
            imageButton.setIcon(HarvesterUI.TASK_STATUS_ICONS.pre_process_16x16());
            config.setTitle(HarvesterUI.CONSTANTS.preProcessing());
            config.setText(viewLog);
            imageButton.setToolTip(config);
        }else if(status.equals("POST_PROCESSING")){
            imageButton.setIcon(HarvesterUI.TASK_STATUS_ICONS.post_process_16x16());
            config.setTitle(HarvesterUI.CONSTANTS.postProcessing());
            config.setText(viewLog);
            imageButton.setToolTip(config);
        }else if(status.equals("PRE_PROCESS_ERROR")){
            imageButton.setIcon(HarvesterUI.TASK_STATUS_ICONS.pre_process_error_16x16());
            config.setTitle(HarvesterUI.CONSTANTS.preProcessError());
            config.setText(viewLog);
            imageButton.setToolTip(config);
        }else if(status.equals("POST_PROCESS_ERROR")){
            imageButton.setIcon(HarvesterUI.TASK_STATUS_ICONS.post_process_error_16x16());
            config.setTitle(HarvesterUI.CONSTANTS.postProcessError());
            config.setText(viewLog);
            imageButton.setToolTip(config);
        }else if(status.equals("RUNNING_SAMPLE")){
            imageButton.setIcon(HarvesterUI.TASK_STATUS_ICONS.sample());
            config.setTitle(HarvesterUI.CONSTANTS.ingestingSample());
            config.setText(viewLog);
            imageButton.setToolTip(config);
        }
        else if(status.endsWith("SAMPLE")){
            String state = status.substring(0, status.indexOf("_"));
            setImageUrl(imageButton,state);
        }
    }

    public void setLoggedUser(String userName, String role){
        Registry.register("LOGGED_USER_NAME",userName);
        Registry.register("LOGGED_USER_ROLE",role);
    }

    public static String getLoggedUserName(){
        return (String)Registry.get("LOGGED_USER_NAME");
    }

    public UserRole getLoggedUserRole(){
        return UserRole.valueOf((String) Registry.get("LOGGED_USER_ROLE"));
    }

    public static String dateDiff(Date date1, Date date2) {
        long milliseconds1 = date1.getTime();
        long milliseconds2 = date2.getTime();
        long diff = milliseconds2 - milliseconds1;
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        diffSeconds = diffSeconds - (diffMinutes * 60);
        diffMinutes = diffMinutes - (diffHours * 60);
        diffHours = diffHours - (diffDays * 24);

        return ( (diffHours < 10 ? "0" : "") + diffHours
                + ":" + (diffMinutes < 10 ? "0" : "") + diffMinutes
                + ":" + (diffSeconds< 10 ? "0" : "") + diffSeconds );
    }

    public void getMainGridSearchResults(){
        final MainGrid mainGrid = Registry.get("mainGrid");
        BaseModel dataContainer = mainGrid.getTopToolbar().getSearchCombo().getLastSavedSearch();

        UtilManager.maskCentralPanel(HarvesterUI.CONSTANTS.loadingSearchResults());
        AsyncCallback<DataContainer> callback = new AsyncCallback<DataContainer>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(DataContainer result) {
                mainGrid.getStore().removeAll();
                mainGrid.getStore().add(result.getChildren(), true);

                mainGrid.getMainDataGrid().expandAll();
//                        int totalSize = mainGrid.getStore().getModels().size();
//                        mainGrid.getPagingToolBar().setEnabled(false);
                UtilManager.unmaskCentralPanel();
            }
        };
        if(dataContainer != null)
            ((DataManagementServiceAsync) Registry.get(HarvesterUI.DATA_MANAGEMENT_SERVICE)).getSearchResult(dataContainer, callback);
    }

    public void askForIncrementalUpdateDate(final DataSourceUI dataSourceUI){
        final SelectionListener<ButtonEvent> useLastDateListener = new SelectionListener<ButtonEvent> () {
            public void componentSelected(ButtonEvent ce) {
                dataSourceUI.setUseLastUpdateDate(true);
            }
        };
        HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(),
                "Do you want to use the last ingest date for incremental update? <br/><br/>" +
                        "<b>Yes</b> - All the records modified before the last update wont be ingested <br/>" +
                        "<b>No</b> - All records will be harvested independent of their modified date", useLastDateListener);
    }

    public static void sendUserActivityData(){
        String serverUrl;
        if(Window.Location.getHost().contains("127.0.0.1:8888"))
            serverUrl = "http://" + Window.Location.getHost() + "/";
        else
            serverUrl = getServerUrl();

        AsyncCallback<RepoxServletResponseStates.GeneralStates> callback = new AsyncCallback<RepoxServletResponseStates.GeneralStates>() {
            public void onFailure(Throwable caught) {
            }
            public void onSuccess(RepoxServletResponseStates.GeneralStates result) {
            }
        };
        UserManagementServiceAsync service = (UserManagementServiceAsync) Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);
        service.addUserActivityData(serverUrl, callback);
    }
}
