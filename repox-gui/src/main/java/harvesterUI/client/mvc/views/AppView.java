/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.mvc.views;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.language.LanguageComboBox;
import harvesterUI.client.panels.overviewGrid.SendFeedbackDialog;
import harvesterUI.client.panels.topToolbarButtons.AdminMenu;
import harvesterUI.client.panels.topToolbarButtons.HarvestTopMenu;
import harvesterUI.client.panels.topToolbarButtons.SchemaMapperButton;
import harvesterUI.client.servlets.RepoxServiceAsync;
import harvesterUI.client.util.CookieManager;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.users.UserRole;

public class AppView extends View {

    public static final String WEST_PANEL = "west";
    public static final String VIEWPORT = "viewport";
    public static final String CENTER_PANEL = "center";
    public static final String EAST_PANEL = "east";

    private Viewport viewport;
    private LayoutContainer center;
    public ToolBar toolBar;
    protected BorderLayout mainBorderLayout;

    private static UserRole role;

    public String REPOX_VERSION = "";

    public int topBorderSize = 30;
    protected SendFeedbackDialog sendFeedbackDialog;
    protected LabelToolItem logosLabel;

    public AppView(Controller controller) {
        super(controller);
    }

    protected void initialize() {
    }

    private void initUI() {
        createDefaultViewport();
    }

    private void createDefaultViewport(){
        viewport = new Viewport();
        viewport.addStyleName("viewportBackground");
        mainBorderLayout = new BorderLayout();
        Registry.register("mainBorderLayout",mainBorderLayout);
        viewport.setLayout(mainBorderLayout);

        createNorth();
        createCenter();

        // registry serves as a global context
        Registry.register(VIEWPORT, viewport);
        Registry.register(CENTER_PANEL, center);

        UtilManager.maskCentralPanel(HarvesterUI.CONSTANTS.loadingInterface());

        RootPanel.get().add(viewport);
    }

    private void createNorth() {
        toolBar = new ToolBar();
        toolBar.addStyleName("topNavToolbar");

        logosLabel = new LabelToolItem("REPOX " + REPOX_VERSION);
        logosLabel.addStyleName("repoxLogoAndTitle");

        // Get Repox Version
        AsyncCallback<String> callback = new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(String repoxVersion) {
                REPOX_VERSION = repoxVersion;
                logosLabel.setLabel("REPOX " + REPOX_VERSION);
            }
        };
        RepoxServiceAsync service = (RepoxServiceAsync) Registry.get("repoxservice");
        service.getRepoxVersion(callback);

        toolBar.add(new LabelToolItem("<img src=resources/images/icons/repox-logo-16.png>"));
        toolBar.add(logosLabel);
        toolBar.add(new FillToolItem());
        toolBar.setSpacing(5);

        Button dashboard = new Button(HarvesterUI.CONSTANTS.home());
        dashboard.setIcon(HarvesterUI.ICONS.home_icon());
        dashboard.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent me) {
                Dispatcher.forwardEvent(AppEvents.LoadMainData);
            }
        });
        toolBar.add(dashboard);

        Button statistics = new Button(HarvesterUI.CONSTANTS.statistics());
        statistics.setIcon(HarvesterUI.ICONS.statistics_icon());
        statistics.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent me) {
                Dispatcher.forwardEvent(AppEvents.ViewStatistics);
            }
        });
        toolBar.add(statistics);

        new SchemaMapperButton(toolBar);

        Button oaiTest = new Button(HarvesterUI.CONSTANTS.oaiPmhTests());
        oaiTest.setIcon(HarvesterUI.ICONS.oai_icon());
        oaiTest.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent me) {
                Dispatcher.forwardEvent(AppEvents.ViewOAITest);
            }
        });
        toolBar.add(oaiTest);

        new HarvestTopMenu(toolBar);

        /**
         * Administration Menu
         */
        new AdminMenu(toolBar);

        /**
         * Send Feedback button
         */
        toolBar.add(new SeparatorToolItem());
        sendFeedbackDialog = new SendFeedbackDialog();
        toolBar.add(addSendFeedbackButton());

        /**
         * Help Menu
         */
        ToolTipConfig config = new ToolTipConfig();
        config.setTitle(HarvesterUI.CONSTANTS.help());
        config.setShowDelay(1000);
        config.setText(HarvesterUI.CONSTANTS.helpTooltipInfo());

        Button help = new Button("");
        help.setIcon(HarvesterUI.ICONS.help_icon());
        help.setToolTip(config);
        help.setSize(23, 23);
        help.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                Window.open("http://repox.ist.utl.pt/doc.html","","");
            }});
        toolBar.add(help);

        // Rss Feed Button
        toolBar.add(createRssFeedButton());

        setUserLoginData();

        BorderLayoutData data = new BorderLayoutData(LayoutRegion.NORTH, topBorderSize);
        data.setMargins(new Margins());
        viewport.add(toolBar, data);
    }

    private void createCenter() {
        center = new LayoutContainer();
        center.setLayout(new FitLayout());

        BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
        data.setMargins(new Margins(5, 5, 5, 5));

        viewport.add(center, data);
    }

    protected void handleEvent(AppEvent event) {
        if (event.getType() == AppEvents.Init) {
            initUI();
        }
    }

    private void createEuDMLViewport() {
        // Add new Viewport type
        Viewport euDmlViewport = new Viewport();
        mainBorderLayout = new BorderLayout();
        euDmlViewport.setLayout(mainBorderLayout);
        BorderLayoutData euDmlVPdata = new BorderLayoutData(LayoutRegion.CENTER);
        euDmlVPdata.setMargins(new Margins(5,5,5,5));
        euDmlViewport.add(viewport,euDmlVPdata);

        RootPanel.get().clear();
        RootPanel.get().add(euDmlViewport);

        // Add EuDML Template Header
//        LayoutContainer euDmlHeaderLC = new LayoutContainer();
//        euDmlHeaderLC.add(new HTML("<div id=\"header\">\n" +
//                "\n" +
//                "    <div id=\"logo-floater\">\n" +
//                "        <a href=\"http://eudml.eu\" title=\"EuDML\" target=\"_blank\"><img src=\"resources/logoEudml.png\" alt=\"EuDML\" id=\"logo\" /></a>\n" +
//                "    </div>\n" +
//                "</div>"));
//        euDmlHeaderLC.setId("eudml_header");
//        euDmlHeaderLC.setLayout(new FitLayout());
//        BorderLayoutData euDMLFooterBD = new BorderLayoutData(LayoutRegion.NORTH, 50);
//        euDMLFooterBD.setMargins(new Margins(5,5,0,5));
//        euDmlViewport.add(euDmlHeaderLC, euDMLFooterBD);
        if(!((toolBar.getItem(toolBar.getItems().indexOf(logosLabel)+1)) instanceof LabelToolItem))
            toolBar.insert(new LabelToolItem("<a href=\"http://www.eudml.eu/\" target=\"_blank\">" +
                    "<img src=\"resources/images/icons/euDML_logo.png\"  alt=\"EuDML\" /></a>"),
                    toolBar.getItems().indexOf(logosLabel)+1);

        // Add EuDML Template Footer
        LayoutContainer euDMLFooterLC = new LayoutContainer();
        euDMLFooterLC.add(new HTML("<div id=\"page-footer\">\n" +
                "    <div class=\"inner\">\n" +
                "        <div class=\"eu-logo\">\n" +
                "\n" +
                "            <p>\n" +
                "                This Project is co-funded by the <br/>\n" +
                "                CIP COMPETITIVENESS AND INNOVATION FRAMEWORK PROGRAMME <br />\n" +
                "                (ICT PSP Digital Libraries) <br />\n" +
                "            </p>\n" +
                "        </div>\n" +
                "        <img id=\"footer-logo\" alt=\"EuDML project\" src=\"resources/eudml-logo-footer.gif\" />\n" +
                "\n" +
                "    </div>\n" +
                "</div>"));
        euDMLFooterLC.setId("eudml_footer");
        euDMLFooterLC.setLayout(new FitLayout());
        BorderLayoutData euDMLHeaderBD = new BorderLayoutData(LayoutRegion.SOUTH, 70);
        euDMLHeaderBD.setMargins(new Margins(0,5,5,5));
        euDmlViewport.add(euDMLFooterLC, euDMLHeaderBD);

        euDmlViewport.layout();
    }

    public void setUserLoginData(){
        LabelToolItem userWelcomeLabel = new LabelToolItem(HarvesterUI.CONSTANTS.welcome() + " " + HarvesterUI.UTIL_MANAGER.getLoggedUserName());
        userWelcomeLabel.setId("userWelcomeLabel");
        toolBar.insert(new SeparatorToolItem(), toolBar.getItems().size());

        // Language Combo
        new LanguageComboBox(toolBar);

        toolBar.insert(userWelcomeLabel, toolBar.getItems().size());

        ToolTipConfig config = new ToolTipConfig();
        config.setTitle(HarvesterUI.CONSTANTS.editAccountButton());
        config.setShowDelay(1000);
        config.setText(HarvesterUI.CONSTANTS.editAccountButtonTooltip());

        // Dont show on Anonymous
        if(HarvesterUI.UTIL_MANAGER.getLoggedUserRole() != UserRole.ANONYMOUS &&
                !Cookies.getCookie(CookieManager.SID).equals("LDAP_SESSION_437")){
            Button editAccount = new Button("");
            editAccount.setIcon(HarvesterUI.ICONS.edit_user());
            editAccount.setToolTip(config);
            editAccount.setSize(28, 28);
            editAccount.addSelectionListener(new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent ce) {
                    Dispatcher.get().dispatch(AppEvents.ViewAccountEditForm);
                }
            });
            toolBar.insert(editAccount, toolBar.getItems().size());
        }

        addLogoutButton();
    }

    public void refreshUserName(){
        LabelToolItem userWelcomeLabel = (LabelToolItem)toolBar.getItemByItemId("userWelcomeLabel");
        userWelcomeLabel.setLabel(HarvesterUI.CONSTANTS.welcome() + " " + HarvesterUI.UTIL_MANAGER.getLoggedUserName());
    }

    public void changeAccordingToRole(UserRole role){
        if(role == UserRole.ANONYMOUS) {
            Button restServicesButton = new Button(HarvesterUI.CONSTANTS.restServices(),new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent ce) {
                    Dispatcher.get().dispatch(AppEvents.ViewRestRecordOperations);
                }
            });
            restServicesButton.setIcon(HarvesterUI.ICONS.web_services_icon());
            toolBar.insert(restServicesButton,6);

            Dispatcher.forwardEvent(AppEvents.RemoveGridOperations);
            Dispatcher.forwardEvent(AppEvents.LoadMainData);
        }else if(role == UserRole.NORMAL) {

        }else if(role == UserRole.ADMIN) {

        }else if(role == UserRole.HARVESTER) {

        }

        if(!History.getToken().isEmpty()){
            History.fireCurrentHistoryState();
        } else
            Dispatcher.forwardEvent(AppEvents.LoadMainData);
    }

    private void addLogoutButton() {
        ToolTipConfig config = new ToolTipConfig();
        config.setTitle(HarvesterUI.CONSTANTS.logoutButton());
        config.setShowDelay(1000);
        config.setText(HarvesterUI.CONSTANTS.logoutButtonTooltip());

        Button logout = new Button("");
        if(HarvesterUI.UTIL_MANAGER.getLoggedUserRole() == UserRole.ANONYMOUS)
            logout.setIcon(HarvesterUI.ICONS.lock_image23x23());
        else
            logout.setIcon(HarvesterUI.ICONS.logout());
        logout.setToolTip(config);
        logout.setSize(28, 28);
        logout.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                Dispatcher.forwardEvent(AppEvents.Logout);
            }
        });

        toolBar.insert(logout, toolBar.getItems().size());
    }

    private Button addSendFeedbackButton() {
        ToolTipConfig config = new ToolTipConfig();
        config.setTitle(HarvesterUI.CONSTANTS.sendFeedbackButton());
        config.setShowDelay(1000);
        config.setText(HarvesterUI.CONSTANTS.sendFeedbackButtonTooltip());

        Button sendFeedback = new Button("");
        sendFeedback.setIcon(HarvesterUI.ICONS.email_icon());
        sendFeedback.setToolTip(config);
        sendFeedback.setSize(23, 23);
        sendFeedback.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                sendFeedbackDialog.setEmail();
                sendFeedbackDialog.show();
                sendFeedbackDialog.center();
            }
        });

        return sendFeedback;
    }

    private Button createRssFeedButton() {
        ToolTipConfig config = new ToolTipConfig();
        config.setTitle(HarvesterUI.CONSTANTS.rssFeedButton());
        config.setShowDelay(1000);
        config.setText(HarvesterUI.CONSTANTS.rssFeedButtonTooltip());

        Button sendFeedback = new Button("");
        sendFeedback.setIcon(HarvesterUI.ICONS.rss_feed_icon());
        sendFeedback.setToolTip(config);
        sendFeedback.setSize(23, 23);
        sendFeedback.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                Dispatcher.forwardEvent(AppEvents.ViewRssFeedPanel);
            }
        });

        return sendFeedback;
    }
}
