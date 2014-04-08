package harvesterUI.client.panels.administration.userManagement;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.controllers.HistoryController;
import harvesterUI.client.servlets.userManagement.UserManagementServiceAsync;
import harvesterUI.client.util.CookieManager;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.dataTypes.UserAuthentication;
import harvesterUI.shared.servletResponseStates.ResponseState;
import harvesterUI.shared.users.UserRole;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 07-04-2011
 * Time: 15:36
 */
public class LoginDialog extends Dialog {

    private TextField<String> userName;
    private TextField<String> password;
    private CheckBox saveCookieBox;
    private Button login, ldapLogin;
    private Button anonymous;
    private FormPanel loginFormPanel;

    private UserManagementServiceAsync service;

    public LoginDialog() {
        service = (UserManagementServiceAsync) Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);
        FormData formData = new FormData("95%");

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
        setButtons("");
        setHeading("");
        setLayout(new FlowLayout());
//        setIcon(HarvesterUI.ICONS.logo16());
        setModal(true);
        setBodyBorder(true);
        setBodyStyle("padding: 8px;background: none");
        setWidth(350);
        setHeight(320);
        setResizable(false);

        add(new LabelToolItem("<img src=\"resources/images/logo/repox-logo-150_improved.png\" " +
                "width=\"100\" height=\"100\" class=\"loginImage\" alt=\"REPOX Logo\" title=\"REPOX Logo\"/>"), formData);

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
        userName.setMinLength(4);
        userName.setAllowBlank(false);
        userName.setFieldLabel(HarvesterUI.CONSTANTS.username() + HarvesterUI.REQUIRED_STR);
        userName.setId("username");
        userName.addKeyListener(keyListener);
        loginFormPanel.add(userName, formData);

        password = new TextField<String>();
        password.setMinLength(4);
        password.setId("password");
        password.setName("password");
        password.setPassword(true);
        password.setAllowBlank(false);
        password.setFieldLabel(HarvesterUI.CONSTANTS.password() + HarvesterUI.REQUIRED_STR);
        password.getElement().setAttribute("type","password");
        password.addKeyListener(keyListener);
        loginFormPanel.add(password, formData);

        saveCookieBox = new CheckBox();
        saveCookieBox.setHideLabel(true);
        saveCookieBox.setBoxLabel(HarvesterUI.CONSTANTS.dontAskPassword());
        saveCookieBox.addKeyListener(keyListener);
        loginFormPanel.add(saveCookieBox);
        loginFormPanel.setId("login");

        String password = "<span style='color:blue" + "'>" + HarvesterUI.CONSTANTS.recoverPassword() + "</span>";
        LabelToolItem recoverPass = new LabelToolItem(password);
        recoverPass.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                createRecoverPasswordForm();
            }
        });
        recoverPass.setStyleName("hyperlink_style_label");
        loginFormPanel.add(recoverPass, formData);

        setFocusWidget(userName);
        add(loginFormPanel, formData);

        loginFormPanel.addButton(login);
        loginFormPanel.addButton(ldapLogin);
        loginFormPanel.addButton(anonymous);

        loginFormPanel.setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(loginFormPanel);
        binding.addButton(login);

        loadLoginInfo();
    }

    protected void createLoginButtons() {
        anonymous = new Button(HarvesterUI.CONSTANTS.anonymous(),HarvesterUI.ICONS.anonymous_icon());
        anonymous.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                LoginDialog.this.hide();
                History.newItem("ANONYMOUS",false);
                HarvesterUI.UTIL_MANAGER.setLoggedUser(HarvesterUI.CONSTANTS.anonymous(), UserRole.ANONYMOUS.name());
                UtilManager.sendUserActivityData();
                History.removeHistoryListener((HarvesterUI)Registry.get("MAIN_ROOT"));
                Dispatcher.get().dispatch(AppEvents.Init);
                Dispatcher.get().dispatch(AppEvents.ViewAccordingToRole);
            }
        });

        login = new Button(HarvesterUI.CONSTANTS.login(),HarvesterUI.ICONS.lock_image16x16());
        login.disable();
        login.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                mask(HarvesterUI.CONSTANTS.verifyingLogin());
                loginFormPanel.submit();
                onSubmit();
            }
        });

        ldapLogin = new Button("LDAP",HarvesterUI.ICONS.ldap_icon());
        ldapLogin.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                new LDAPLoginDialog(LoginDialog.this).showAndCenter();
            }
        });
    }

    protected void onSubmit() {

        AsyncCallback<UserAuthentication> callback = new AsyncCallback<UserAuthentication>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(UserAuthentication loginData) {
                if(loginData.get("status").equals("succeeded")) {
                    String sid = (String)loginData.get("sessionID");
                    String username = (String)loginData.get("userName");
                    String role = (String)loginData.get("role");
                    String language = Cookies.getCookie(CookieManager.REPOX_LANGUAGE);
                    if(language == null || language.isEmpty())
                        language = "en";

                    CookieManager cookieManager = (CookieManager) Registry.get(HarvesterUI.COOKIE_MANAGER);
                    if(saveCookieBox.getValue()){
                        cookieManager.saveLoginForTwoWeeks(sid,username,role);
                    } else{
                        cookieManager.saveLoginForBrowserSessionOnly(sid,username,role);
                    }

                    if(userName.getValue() != null){
                        cookieManager.saveLoginDataForLoginDialog(userName.getValue(),password.getValue());
                    }

                    if(!HarvesterUI.UTIL_MANAGER.isDefaultLanguage(language) && !UtilManager.getUrlLocaleLanguage().equals(language)){
                        cookieManager.saveTempLanguageData(language,username);
                        Window.Location.assign(UtilManager.getServerUrl() + "?locale=" + language);
                    }else{
                        // Only add the history controller for logged users
                        Dispatcher.get().addController(new HistoryController());
                        LoginDialog.this.hide();
                        HarvesterUI.UTIL_MANAGER.setLoggedUser(username,role);
                        UtilManager.sendUserActivityData();
                        Dispatcher.forwardEvent(AppEvents.Init);
                        Dispatcher.forwardEvent(AppEvents.ViewAccordingToRole);
                        History.newItem("HOME");

                        Window.addWindowClosingHandler(new Window.ClosingHandler() {
                            public void onWindowClosing(Window.ClosingEvent closingEvent) {
                                closingEvent.setMessage("Do you really want to leave the page?");
                            }
                        });
                    }
                }else if(loginData.get("status").equals("corrupt")){
                    Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent ce) {
                            Button btn = ce.getButtonClicked();
                            if(btn.getText().equals("OK")){
                                setFocusWidget(userName);
                                unmask();
                            }
                        }
                    };
                    MessageBox msg = MessageBox.alert(HarvesterUI.CONSTANTS.alert(), HarvesterUI.CONSTANTS.usersFileCorrupted(), l);
                    setFocusWidget(msg.getDialog());
                }
                else {
                    Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent ce) {
                            Button btn = ce.getButtonClicked();
                            if(btn.getText().equals("OK")){
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
        service.confirmLogin(userName.getValue(),password.getValue(), callback);
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

    protected void loadLoginInfo(){
        if(Cookies.getCookie("enteredUsername" + UtilManager.getServerUrl()) != null){
            userName.setValue(Cookies.getCookie("enteredUsername" + UtilManager.getServerUrl()));
            password.setValue(Cookies.getCookie("enteredPassword" + UtilManager.getServerUrl()));
            validate();
        }
    }

    private void createRecoverPasswordForm() {
        final Dialog recoverDialog = new Dialog();
        recoverDialog.setButtons("");
        recoverDialog.setLayout(new FitLayout());
        recoverDialog.setHeading(HarvesterUI.CONSTANTS.recoverPassword());
        recoverDialog.setIcon(HarvesterUI.ICONS.send_recovery_pass_icon());
        recoverDialog.setResizable(false);
        recoverDialog.setModal(true);
        recoverDialog.setSize(500,150);

        final DefaultFormPanel recoverFormPanel = new DefaultFormPanel();
        recoverFormPanel.setHeaderVisible(false);

        recoverFormPanel.setLayout(new EditableFormLayout(175));

        final TextField<String> userNameField = new TextField<String>();
        userNameField.setFieldLabel(HarvesterUI.CONSTANTS.username() + HarvesterUI.REQUIRED_STR);
        userNameField.setAllowBlank(false);
        userNameField.setId("username");
        recoverFormPanel.add(userNameField, new FormData("95%"));

        Button b = new Button(HarvesterUI.CONSTANTS.send(),HarvesterUI.ICONS.send_recovery_pass_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                        recoverDialog.unmask();
                    }
                    public void onSuccess(ResponseState responseState) {
                        if(responseState == ResponseState.SUCCESS) {
                            recoverDialog.hide();
                            HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.passwordReset(),HarvesterUI.CONSTANTS.passwordResetSuccess());
                            setFocusWidget(loginFormPanel);
                        } else if(responseState == ResponseState.ERROR)
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.passwordReset(),HarvesterUI.CONSTANTS.userEmailMatchNotFound());
                        else if(responseState == ResponseState.EMAIL_AUTHENTICATION_ERROR)
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.passwordReset(),"Default sender email authentication information not correct.");
                        recoverDialog.unmask();
                    }
                };
                String userName = userNameField.getValue();
                service.resetUserPassword(userName, callback);
                recoverDialog.mask(HarvesterUI.CONSTANTS.resetPasswordMask());
                recoverFormPanel.submit();
            }
        });
        recoverFormPanel.addButton(b);
        recoverFormPanel.addButton(new Button(HarvesterUI.CONSTANTS.cancel(),HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                recoverDialog.hide();
            }
        }));

        recoverFormPanel.setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(recoverFormPanel);
        binding.addButton(b);

        recoverDialog.add(recoverFormPanel);
        recoverDialog.show();
        recoverDialog.center();
    }
}
