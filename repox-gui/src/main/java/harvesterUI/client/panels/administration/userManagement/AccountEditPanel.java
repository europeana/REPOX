package harvesterUI.client.panels.administration.userManagement;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.AppView;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.servlets.userManagement.UserManagementServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.users.User;
import harvesterUI.shared.servletResponseStates.ResponseState;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-04-2011
 * Time: 20:16
 */
public class AccountEditPanel extends FormDialog {

    private UserManagementServiceAsync service;
    private User currentUser;
    private FormData formData;

    private TextField<String> userNameField, emailField, newPassField, retypeNewPassField;
    private DefaultFormPanel editAccountFormPanel;

    public AccountEditPanel() {
        super(0.4,0.5);
        service = (UserManagementServiceAsync) Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);
        formData = new FormData("90%");
        createEditAccountForm();
    }

    private void createEditAccountForm() {
        setIcon(HarvesterUI.ICONS.edit_user());

        editAccountFormPanel = new DefaultFormPanel();
        editAccountFormPanel.setHeaderVisible(false);

        editAccountFormPanel.setLayout(new EditableFormLayout(UtilManager.DEFAULT_DATASET_VIEWINFO_LABEL_WIDTH));

        userNameField = new TextField<String>();
        userNameField.setFieldLabel(HarvesterUI.CONSTANTS.username() + HarvesterUI.REQUIRED_STR);
        userNameField.setAllowBlank(false);
        userNameField.setMinLength(4);
        editAccountFormPanel.add(userNameField, formData);

        Validator usernameValidator = new Validator() {
            public String validate(Field<?> field, String s) {
                if(!s.matches("^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$"))
                    return HarvesterUI.CONSTANTS.usernameValidateMessage();
                return null;
            }
        };
        userNameField.setValidator(usernameValidator);

        emailField = new TextField<String>();
        emailField.setFieldLabel(HarvesterUI.CONSTANTS.email() + HarvesterUI.REQUIRED_STR);
        emailField.setAllowBlank(false);
        editAccountFormPanel.add(emailField, formData);

        Validator emailValidator = new Validator() {
            public String validate(Field<?> field, String s) {
                if(!s.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"))
                    return HarvesterUI.CONSTANTS.emailValidateMessage();
                return null;
            }
        };
        emailField.setValidator(emailValidator);

//        pageSizeField = new NumberField();
//        pageSizeField.setFieldLabel("Providers Per Page" + HarvesterUI.REQUIRED_STR);
//        pageSizeField.setAllowBlank(false);
//        editAccountFormPanel.add(pageSizeField, formData);

        Validator retypePassValidator = new Validator() {
            public String validate(Field<?> field, String s) {
                if(!s.equals(newPassField.getValue()))
                    return HarvesterUI.CONSTANTS.samePasswordValidateMessage();
                return null;
            }
        };

        newPassField = new TextField<String>();
        newPassField.setFieldLabel(HarvesterUI.CONSTANTS.newPassword());
        newPassField.setPassword(true);
        newPassField.setMinLength(4);
        newPassField.addKeyListener(new KeyListener(){
            @Override
            public void componentKeyUp(final ComponentEvent event) {
                if(newPassField.getValue() == null)
                    retypeNewPassField.setAllowBlank(true);
                else if(!newPassField.getValue().equals(""))
                    retypeNewPassField.setAllowBlank(false);
                super.componentKeyPress(event);
            }
        });
        editAccountFormPanel.add(newPassField, formData);

        retypeNewPassField = new TextField<String>();
        retypeNewPassField.setFieldLabel(HarvesterUI.CONSTANTS.confirmPassword());
        retypeNewPassField.setPassword(true);
        retypeNewPassField.setMinLength(4);
        retypeNewPassField.setValidator(retypePassValidator);
        editAccountFormPanel.add(retypeNewPassField, formData);

        Button b = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                final AppView appView = (AppView) Registry.get("appView");
                mask(HarvesterUI.CONSTANTS.saveUserMask());
                AsyncCallback<User> callback = new AsyncCallback<User>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(User user) {
                        AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
                            public void onFailure(Throwable caught) {
                                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                            }
                            public void onSuccess(ResponseState result) {
                                unmask();
                                if(result == ResponseState.USER_ALREADY_EXISTS){
                                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.editAccount(),HarvesterUI.CONSTANTS.usernameAlreadyExists());
                                    return;
                                }
                                Registry.register("LOGGED_USER_NAME",currentUser.getUserName());
                                appView.refreshUserName();
                                editAccountFormPanel.submit();
                                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.editAccount(),HarvesterUI.CONSTANTS.editAccountSuccess());
                                Dispatcher.forwardEvent(AppEvents.ReloadUsers);
                                hide();
                            }
                        };
                        String userName = userNameField.getValue();
                        String email = emailField.getValue();
                        currentUser.setUserName(userName);
                        currentUser.setMail(email);
                        currentUser.setRole(HarvesterUI.UTIL_MANAGER.getLoggedUserRole().name());
                        if(validatePasswords())
                            currentUser.setPassword(newPassField.getValue());
                        else
                            currentUser.setPassword(null);

                        service.saveUser(currentUser,HarvesterUI.UTIL_MANAGER.getLoggedUserName(),true, callback);
                    }
                };
                String userName = userNameField.getValue();
                service.getUser(userName, callback);
            }
        });
        editAccountFormPanel.addButton(b);
        editAccountFormPanel.addButton(new Button(HarvesterUI.CONSTANTS.cancel(),HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                hide();
            }
        }));

        editAccountFormPanel.setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(editAccountFormPanel);
        binding.addButton(b);

        add(editAccountFormPanel);
    }

    public void edit() {
        AsyncCallback<User> callback = new AsyncCallback<User>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(User user) {
                currentUser = user;
                setHeading(HarvesterUI.CONSTANTS.editAccountSettings() + " - " + HarvesterUI.CONSTANTS.role() +": " + currentUser.getRole());
                userNameField.setValue(currentUser.getUserName());
                emailField.setValue(currentUser.getMail());
//                pageSizeField.setValue(currentUser.getPageSize());
            }
        };
        service.getUser(HarvesterUI.UTIL_MANAGER.getLoggedUserName(),callback);
    }

    private boolean validatePasswords() {
        if(newPassField.getValue() == null || /*currentPassField.getValue() == null ||*/ retypeNewPassField == null)
            return false;
        else {
            return true;
        }

    }
}
