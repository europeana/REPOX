package harvesterUI.client.panels.administration.userManagement;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.controllers.HistoryController;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.servlets.userManagement.UserManagementServiceAsync;
import harvesterUI.client.util.CookieManager;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 07-04-2011
 * Time: 15:36
 */
public class LDAPLoginDialog extends FormDialog {

    private TextField<String> userName,password;
    private CheckBox saveCookieBox;
    private Button login;
    private FormPanel loginFormPanel;

    private UserManagementServiceAsync service;
    private LoginDialog loginDialog;

    public LDAPLoginDialog(LoginDialog loginDialog) {
        super(0.4,0.4);
        this.loginDialog = loginDialog;
        service = (UserManagementServiceAsync) Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);
        FormData formData = new FormData("95%");

        setIcon(HarvesterUI.ICONS.ldap_icon());

        createLoginButtons();

        loginFormPanel = new FormPanel();
        loginFormPanel.setHeaderVisible(false);
        loginFormPanel.setMethod(FormPanel.Method.POST);
        loginFormPanel.setBodyBorder(false);

        FormLayout layout = new FormLayout(FormPanel.LabelAlign.TOP);
        layout.setLabelSeparator("");
        layout.setLabelWidth(70);
        loginFormPanel.setLayout(layout);

        setButtonAlign(HorizontalAlignment.LEFT);
        setHeading("LDAP Authentication");
        setLayout(new FlowLayout());
//        setIcon(HarvesterUI.ICONS.logo16());
        setLayoutOnChange(true);
        setModal(true);
        setResizable(true);
        setWidth(350);
        setHeight(240);

//        add(new LabelToolItem("<img src=\"resources/images/logo/repox-logo-150_improved.png\" " +
//                "width=\"100\" height=\"100\" class=\"loginImage\" alt=\"REPOX Logo\" title=\"REPOX Logo\"/>"), formData);

        KeyListener keyListener = new KeyListener() {
            public void componentKeyDown(ComponentEvent event) {
                validate();
                // If ENTER key pressed
                if(event.getKeyCode()== KeyCodes.KEY_ENTER && isValidInfo()){
                    mask(HarvesterUI.CONSTANTS.verifyingLogin());
                    onSubmit();
                }
            }
        };

        userName = new TextField<String>();
        userName.setAllowBlank(false);
        userName.setFieldLabel("LDAP Username" + HarvesterUI.REQUIRED_STR);
        userName.setId("ldapLoginDN");
        userName.setName("ldapLoginDN");
        userName.addKeyListener(keyListener);
        loginFormPanel.add(userName, formData);

        password = new TextField<String>();
        password.setId("ldapPass");
        password.setName("ldapPass");
        password.setPassword(true);
        password.setAllowBlank(false);
        password.setFieldLabel(HarvesterUI.CONSTANTS.password() + HarvesterUI.REQUIRED_STR);
        password.addKeyListener(keyListener);
        loginFormPanel.add(password, formData);

        saveCookieBox = new CheckBox();
        saveCookieBox.setHideLabel(true);
        saveCookieBox.setBoxLabel(HarvesterUI.CONSTANTS.dontAskPassword());
        saveCookieBox.addKeyListener(keyListener);
        loginFormPanel.add(saveCookieBox);
        loginFormPanel.setId("ldapLogin");

        setFocusWidget(userName);
        add(loginFormPanel, formData);

        loginFormPanel.addButton(login);

        loginFormPanel.setButtonAlign(HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(loginFormPanel);
        binding.addButton(login);

//        loadLoginInfo();
    }

    protected void createLoginButtons() {
        login = new Button(HarvesterUI.CONSTANTS.login(),HarvesterUI.ICONS.lock_image16x16());
        login.disable();
        login.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                mask(HarvesterUI.CONSTANTS.verifyingLogin());
                loginFormPanel.submit();
                onSubmit();
            }
        });
    }

    protected void onSubmit() {
        AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(Boolean loginSuccessful) {
                if(loginSuccessful) {
                    String username = userName.getValue();
                    String role = "ADMIN";
                    String sid = "LDAP_SESSION_437";

                    String language = Cookies.getCookie(CookieManager.REPOX_LANGUAGE);
                    if(language == null || language.isEmpty())
                        language = "en";

                    CookieManager cookieManager = (CookieManager) Registry.get(HarvesterUI.COOKIE_MANAGER);
                    if(saveCookieBox.getValue()){
                        cookieManager.saveLoginForTwoWeeks(sid,username,role);
                    } else{
                        cookieManager.saveLoginForBrowserSessionOnly(sid,username,role);
                    }

                    if(!HarvesterUI.UTIL_MANAGER.isDefaultLanguage(language) && !UtilManager.getUrlLocaleLanguage().equals(language)){
                        cookieManager.saveTempLanguageData(language,username);
                        Window.Location.assign(UtilManager.getServerUrl() + "?locale=" + language);
                    }else{
                        // Only add the history controller for logged users
                        loginDialog.hide();
                        Dispatcher.get().addController(new HistoryController());
                        LDAPLoginDialog.this.hide();
                        HarvesterUI.UTIL_MANAGER.setLoggedUser(username,role);
                        Dispatcher.forwardEvent(AppEvents.Init);
                        Dispatcher.forwardEvent(AppEvents.ViewAccordingToRole);
                        History.newItem("HOME");

                        Window.addWindowClosingHandler(new Window.ClosingHandler() {
                            public void onWindowClosing(Window.ClosingEvent closingEvent) {
                                closingEvent.setMessage("Do you really want to leave the page?");
                            }
                        });
                    }
                    System.out.println("OK LOGIN LDAP");
                }else {
                    Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent ce) {
                            Button btn = ce.getButtonClicked();
                            if(btn.getText().equals("OK")){
                                unmaskLDAP();
                                setFocusWidget(userName);
                                unmask();
                            }
                        }
                    };
                    MessageBox msg = MessageBox.alert(HarvesterUI.CONSTANTS.alert(), HarvesterUI.CONSTANTS.userNameOrPasswordIncorrect(), l);
                    setFocusWidget(msg.getDialog());
                }
            }
        };
        service.checkLDAPAuthentication(userName.getValue(), password.getValue(), callback);
    }

    public void unmaskLDAP(){
        unmask();
    }

    protected boolean hasValue(TextField<String> field) {
        return field.getValue() != null && field.getValue().length() > 0;
    }

    protected void validate() {
        login.setEnabled(isValidInfo());
    }

    private boolean isValidInfo(){
        return hasValue(userName) && hasValue(password)
                && password.getValue().length() > 3;
    }

//    protected void loadLoginInfo(){
//        if(Cookies.getCookie("enteredUsername" + UtilManager.getServerUrl()) != null){
//            userName.setValue(Cookies.getCookie("enteredUsername" + UtilManager.getServerUrl()));
//            password.setValue(Cookies.getCookie("enteredPassword" + UtilManager.getServerUrl()));
//            validate();
//        }
//    }
}
