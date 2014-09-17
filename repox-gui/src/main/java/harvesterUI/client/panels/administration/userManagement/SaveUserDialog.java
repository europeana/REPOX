package harvesterUI.client.panels.administration.userManagement;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.servlets.userManagement.UserManagementServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.servletResponseStates.ResponseState;
import harvesterUI.shared.users.DataProviderUser;
import harvesterUI.shared.users.User;
import harvesterUI.shared.users.UserRole;

import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-04-2011
 * Time: 17:19
 */
public class SaveUserDialog extends FormDialog {

    private UserManagementServiceAsync service;
    private TextField<String> userNameField, emailField;
    private SimpleComboBox<String> roleCombo;
    private User editedUser;
    private DefaultFormPanel newUserFormPanel;
    private DataProvidersContainer dataProvidersContainer;

    public SaveUserDialog() {
        super(0.4,0.3);
        service = (UserManagementServiceAsync) Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);
        createNewUSerDialog();
        setHeading(HarvesterUI.CONSTANTS.addUser());
        setIcon(HarvesterUI.ICONS.add16());
    }

    public SaveUserDialog(User editUser) {
        this();
        editedUser = editUser;
        edit(editUser);
        setHeading(HarvesterUI.CONSTANTS.edit() + ": " + editUser.getUserName());
        setIcon(HarvesterUI.ICONS.operation_edit());
    }

    private void createNewUSerDialog() {
        final FormData formData = new FormData("95%");

        newUserFormPanel = new DefaultFormPanel();
        newUserFormPanel.setHeaderVisible(false);

        newUserFormPanel.setLayout(new EditableFormLayout(175));

        userNameField = new TextField<String>();
        userNameField.setFieldLabel(HarvesterUI.CONSTANTS.username() + HarvesterUI.REQUIRED_STR);
        userNameField.setId("userNameField");
        userNameField.setMinLength(4);
        userNameField.setAllowBlank(false);
        newUserFormPanel.add(userNameField, formData);

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
        emailField.setId("emailField");
        emailField.setAllowBlank(false);
        newUserFormPanel.add(emailField, formData);

        Validator emailValidator = new Validator() {
            public String validate(Field<?> field, String s) {
                if(!s.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"))
                    return HarvesterUI.CONSTANTS.emailValidateMessage();
                return null;
            }
        };
        emailField.setValidator(emailValidator);

        roleCombo = new SimpleComboBox<String>();
        roleCombo.setEditable(false);
        roleCombo.setFieldLabel(HarvesterUI.CONSTANTS.role() + HarvesterUI.REQUIRED_STR);
        roleCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        for(UserRole userRole : UserRole.values()){
            if(userRole != UserRole.ANONYMOUS)
                roleCombo.add(userRole.name());
        }
        roleCombo.setValue(roleCombo.getStore().getAt(0));
        roleCombo.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
                if(se.getSelectedItem().getValue().equals(UserRole.DATA_PROVIDER.name())){
                    addAllowedDataProvidersBox(formData);
                }else{
                    newUserFormPanel.remove(dataProvidersContainer);
                    layout();
                }
            }
        });
        newUserFormPanel.add(roleCombo, formData);

        Button saveButton = new Button(HarvesterUI.CONSTANTS.save(), HarvesterUI.ICONS.save_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                if(!isValidDP()){
                    HarvesterUI.UTIL_MANAGER.getInfoBox(HarvesterUI.CONSTANTS.newUser(), "Please Add one or more Data Providers");
                    return;
                }

                AsyncCallback<User> callback = new AsyncCallback<User>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server", caught.getMessage()).show();
                    }

                    public void onSuccess(User user) {
                        AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
                            public void onFailure(Throwable caught) {
                                new ServerExceptionDialog("Failed to get response from server", caught.getMessage()).show();
                                unmask();
                            }
                            public void onSuccess(ResponseState result) {
                                unmask();
                                if (result == ResponseState.USER_ALREADY_EXISTS) {
                                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.newUser(), HarvesterUI.CONSTANTS.usernameAlreadyExists());
                                    return;
                                }
                                hide();

                                if(editedUser != null)
                                    HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.updateUser(), HarvesterUI.CONSTANTS.updateUserSuccess());
                                else
                                    HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.newUser(), HarvesterUI.CONSTANTS.saveNewUserSuccess());
                                Dispatcher.forwardEvent(AppEvents.ReloadUsers);
                            }
                        };
                        mask(HarvesterUI.CONSTANTS.saveUserMask());
                        newUserFormPanel.submit();
                        String role = roleCombo.getValue().getValue().trim();
                        String userName = userNameField.getValue().trim();
                        String email = emailField.getValue().trim();

                        User newUser;
                        if(isDPUser()){
                            newUser = new DataProviderUser(userName, null, role, email, dataProvidersContainer.getAllowedDataProviderIds());
                        }else
                            newUser = new User(userName, null, role, email);
                        service.saveUser(newUser, editedUser != null ? editedUser.getUserName() : "",
                                editedUser != null, callback);
                    }
                };
                String userName = userNameField.getValue();
                service.getUser(userName, callback);
            }
        });
        newUserFormPanel.addButton(saveButton);
        newUserFormPanel.addButton(new Button(HarvesterUI.CONSTANTS.cancel(),HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                hide();
                Dispatcher.get().dispatch(AppEvents.ViewUserManagementForm);
            }
        }));

        newUserFormPanel.setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(newUserFormPanel);
        binding.addButton(saveButton);

        add(newUserFormPanel);
    }

    private boolean isValidDP(){
        if(isDPUser() && dataProvidersContainer != null)
            return dataProvidersContainer.getDataProviderUIGrid().getStore().getModels().size() > 0;
        else
            return true;
    }

    private boolean isDPUser(){
        return roleCombo.getSimpleValue().equals(UserRole.DATA_PROVIDER.name());
    }

    private void addAllowedDataProvidersBox(FormData formData){
        if(dataProvidersContainer == null)
            dataProvidersContainer = new DataProvidersContainer();
        newUserFormPanel.add(dataProvidersContainer, formData);
        layout();
    }

    public void edit(final User editUser) {
        userNameField.setValue(editUser.getUserName());
        emailField.setValue(editUser.getMail());
        roleCombo.setSimpleValue(editUser.getRole());

        if(editUser instanceof DataProviderUser){
            AsyncCallback<List<DataProviderUI>> callback = new AsyncCallback<List<DataProviderUI>>() {
                public void onFailure(Throwable caught) {
                    new ServerExceptionDialog("Failed to get response from server", caught.getMessage()).show();
                }
                public void onSuccess(List<DataProviderUI> result) {
                    for(DataProviderUI sourceDPUI : result){
                        for(String dpId : ((DataProviderUser) editUser).getAllowedDataProviderIds()){
                            if(dpId.equals(sourceDPUI.getId())){
                                dataProvidersContainer.getDataProviderUIGrid().getStore().add(sourceDPUI);
                            }
                        }
                    }
                }
            };
            service.getAvailableDataProviders(callback);
        }
    }
}
