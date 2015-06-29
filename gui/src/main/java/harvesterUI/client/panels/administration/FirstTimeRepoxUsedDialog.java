//package harvesterUI.client.panels.administration;
//
//import com.extjs.gxt.ui.client.Registry;
//import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
//import com.extjs.gxt.ui.client.event.ButtonEvent;
//import com.extjs.gxt.ui.client.event.ComponentEvent;
//import com.extjs.gxt.ui.client.event.KeyListener;
//import com.extjs.gxt.ui.client.event.SelectionListener;
//import com.extjs.gxt.ui.client.widget.Dialog;
//import com.extjs.gxt.ui.client.widget.button.Button;
//import com.extjs.gxt.ui.client.widget.form.*;
//import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
//import com.extjs.gxt.ui.client.widget.layout.FormData;
//import com.extjs.gxt.ui.client.widget.layout.FormLayout;
//import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
//import com.google.gwt.event.dom.client.KeyCodes;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import harvesterUI.client.HarvesterUI;
//import harvesterUI.client.servlets.userManagement.UserManagementServiceAsync;
//import harvesterUI.client.util.ServerExceptionDialog;
//import harvesterUI.shared.servletResponseStates.RepoxServletResponseStates;
//
///**
// * Created to REPOX.
// * User: Edmundo
// * Date: 07-04-2011
// * Time: 15:36
// */
//public class FirstTimeRepoxUsedDialog extends Dialog {
//
//    protected TextField<String> nameField, mail, institution, skypeContact, repoxUrlField;
//    protected Button registerButton;
//    protected FormPanel registerFormPanel;
//
//    private UserManagementServiceAsync service;
//
//    public FirstTimeRepoxUsedDialog() {
//        service = (UserManagementServiceAsync) Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);
//        FormData formData = new FormData("95%");
//
//        createLoginButtons();
//
//        registerFormPanel = new FormPanel();
//        registerFormPanel.setHeaderVisible(false);
//        registerFormPanel.setBodyBorder(false);
//
//        FormLayout layout = new FormLayout(FormPanel.LabelAlign.TOP);
//        layout.setLabelSeparator("");
//        layout.setLabelWidth(70);
////        layout.setDefaultWidth(155);
//        registerFormPanel.setLayout(layout);
//
//        setButtonAlign(HorizontalAlignment.LEFT);
//        setButtons("");
//        setHeading("");
//        setLayout(new FlowLayout());
//        setModal(true);
//        setBodyBorder(true);
//        setBodyStyle("padding: 8px;background: none");
//        setWidth(350);
//        setHeight(380);
//        setResizable(false);
//        setClosable(false);
//
//        add(new LabelToolItem("<img src=\"resources/images/logo/repox-logo-150_improved.png\" " +
//                "width=\"25\" height=\"25\" alt=\"Repox Logo\" title=\"Repox Logo\"/>"),formData);
//
//        add(new LabelToolItem("Please fill in the registration form (this information will only be used for " +
//                "statistical purposes by beeing added to the REPOX users list)."),formData);
//
//        KeyListener keyListener = new KeyListener() {
//            public void componentKeyDown(ComponentEvent event) {
//                // If ENTER key pressed
//                if(validate() && event.getKeyCode()== KeyCodes.KEY_ENTER){
//                    onSubmit();
//                }
//            }
//        };
//
//        nameField = new TextField<String>();
//        nameField.setAllowBlank(false);
//        nameField.setFieldLabel("Name" + HarvesterUI.REQUIRED_STR);
//        nameField.addKeyListener(keyListener);
//        registerFormPanel.add(nameField,formData);
//
//        mail = new TextField<String>();
//        mail.setAllowBlank(false);
//        mail.setFieldLabel("Email" + HarvesterUI.REQUIRED_STR);
//        mail.addKeyListener(keyListener);
//        registerFormPanel.add(mail,formData);
//        Validator emailValidator = new Validator() {
//            public String validate(Field<?> field, String s) {
//                if(!s.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"))
//                    return "Please insert your email in the correct format. Ex: john@mail.com";
//                return null;
//            }
//        };
//        mail.setValidator(emailValidator);
//
//        institution = new TextField<String>();
//        institution.setAllowBlank(false);
//        institution.setFieldLabel("Institution" + HarvesterUI.REQUIRED_STR);
//        institution.addKeyListener(keyListener);
//        registerFormPanel.add(institution,formData);
//
//        skypeContact = new TextField<String>();
//        skypeContact.setFieldLabel("Skype Contact (if available)");
//        skypeContact.addKeyListener(keyListener);
//        registerFormPanel.add(skypeContact,formData);
//
//        repoxUrlField = new TextField<String>();
//        repoxUrlField.setFieldLabel("REPOX URL (if available)");
//        repoxUrlField.addKeyListener(keyListener);
//        registerFormPanel.add(repoxUrlField,formData);
//
//        setFocusWidget(nameField);
//        add(registerFormPanel,formData);
//
//        registerFormPanel.addButton(registerButton);
//
//        registerFormPanel.setButtonAlign(HorizontalAlignment.CENTER);
//
//        FormButtonBinding binding = new FormButtonBinding(registerFormPanel);
//        binding.addButton(registerButton);
//    }
//
//    protected void createLoginButtons() {
//        registerButton = new Button("Register");
//        registerButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//            public void componentSelected(ButtonEvent ce) {
//                FirstTimeRepoxUsedDialog.this.hide();
//                onSubmit();
//            }
//        });
//    }
//
//    protected void onSubmit() {
//        mask("Performing registration information. Please Wait...");
//        AsyncCallback<RepoxServletResponseStates.GeneralStates> callback = new AsyncCallback<RepoxServletResponseStates.GeneralStates>() {
//            public void onFailure(Throwable caught) {
//                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
//            }
//            public void onSuccess(RepoxServletResponseStates.GeneralStates result) {
//                if(result == RepoxServletResponseStates.GeneralStates.ERROR) {
//                    System.out.println("ERROR During Registration");
//                }else if(result == RepoxServletResponseStates.GeneralStates.SUCCESS ||
//                        result == RepoxServletResponseStates.GeneralStates.NO_INTERNET_CONNECTION){
//                    Window.Location.reload();
//                }
//            }
//        };
//        service.registerNewEntity(nameField.getValue(),mail.getValue(), institution.getValue(),
//                skypeContact.getValue(), repoxUrlField.getValue(), callback);
//    }
//
//    protected boolean validate() {
//        return nameField.getValue() != null &&
//                institution.getValue() != null &&
//                mail.getValue() != null ;
//    }
//}
