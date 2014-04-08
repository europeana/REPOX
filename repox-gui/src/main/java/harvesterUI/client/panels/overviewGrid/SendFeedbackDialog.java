package harvesterUI.client.panels.overviewGrid;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.servlets.userManagement.UserManagementServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.users.User;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 28-10-2011
 * Time: 18:22
 */
public class SendFeedbackDialog extends Dialog {
    private UserManagementServiceAsync userManagementService;
    public TextField<String> userEmailField, titleField;
    TextArea messageField;
    private SimpleComboBox<String> roleCombo;
    private Button saveButton;
    protected FormPanel feedbackFormPanel;

    public SendFeedbackDialog() {
        userManagementService = (UserManagementServiceAsync) Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);
        createNewSendFeedbackDialog();
    }

    private void createNewSendFeedbackDialog() {
        setButtons("");
        setLayout(new FitLayout());
        setHeading(HarvesterUI.CONSTANTS.feedback());
        setIcon(HarvesterUI.ICONS.email_icon());
        setResizable(false);
        setModal(true);
        setSize(600,400);

        FormData formData = new FormData("95%");

        feedbackFormPanel = new FormPanel();
        feedbackFormPanel.setHeaderVisible(false);
        feedbackFormPanel.setFrame(false);
        feedbackFormPanel.setBodyBorder(false);
        feedbackFormPanel.setScrollMode(Style.Scroll.AUTO);
        setStyleName("repoxFormBackground");

        LayoutContainer main = new LayoutContainer();
        main.setLayout(new ColumnLayout());

        FormLayout layout = new FormLayout();
        layout.setHideLabels(true);
        feedbackFormPanel.setLayout(layout);

        messageField = new TextArea();
        messageField.setId("messageField");
        messageField.setHeight(200);
//        messageField.setLayoutData(new FillLayout());
        messageField.setAllowBlank(false);

        roleCombo = new SimpleComboBox<String>();
        roleCombo.add(HarvesterUI.CONSTANTS.idea());
        roleCombo.add(HarvesterUI.CONSTANTS.problem());
        roleCombo.add(HarvesterUI.CONSTANTS.question());
        roleCombo.setEditable(false);
        roleCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        roleCombo.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
                if(se.getSelectedItem().getValue().equals(HarvesterUI.CONSTANTS.idea()))
                    messageField.setEmptyText(HarvesterUI.CONSTANTS.ideaExample());
                else if(se.getSelectedItem().getValue().equals(HarvesterUI.CONSTANTS.problem()))
                    messageField.setEmptyText(HarvesterUI.CONSTANTS.problemExample());
                else if(se.getSelectedItem().getValue().equals(HarvesterUI.CONSTANTS.question()))
                    messageField.setEmptyText(HarvesterUI.CONSTANTS.questionExample());
            }
        });
        roleCombo.setValue(roleCombo.getStore().getAt(1));
        feedbackFormPanel.add(roleCombo, formData);

        userEmailField = new TextField<String>();
        userEmailField.setId("userEmailField");
        userEmailField.setAllowBlank(false);
//        feedbackFormPanel.add(userEmailField, new FormData("95%"));

        titleField = new TextField<String>();
        titleField.setEmptyText(HarvesterUI.CONSTANTS.feedbackTitle());
        titleField.setId("titleField");
        titleField.setAllowBlank(false);
        feedbackFormPanel.add(titleField, formData);

        feedbackFormPanel.add(messageField, formData);

        saveButton = new Button(HarvesterUI.CONSTANTS.sendMessage(),HarvesterUI.ICONS.email_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                AsyncCallback<String> callback = new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                        unmask();
                    }
                    public void onSuccess(String response) {
                        feedbackFormPanel.submit();
                        unmask();
                        resetValues();
                    }
                };
                String userName = userEmailField.getValue();
                String title = titleField.getValue();
                String message = messageField.getValue();
                String type = roleCombo.getValue().getValue();
                mask(HarvesterUI.CONSTANTS.sendEmailMask());
                userManagementService.sendFeedbackEmail(userName,title,message,type, callback);
            }
        });
        feedbackFormPanel.addButton(saveButton);
        feedbackFormPanel.addButton(new Button(HarvesterUI.CONSTANTS.cancel(),HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                hide();
            }
        }));

        feedbackFormPanel.setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(feedbackFormPanel);
        binding.addButton(saveButton);

        main.add(feedbackFormPanel, new ColumnData(.5));
        main.add(new LabelToolItem("<br/><br/>"+HarvesterUI.CONSTANTS.repoxMessage()), new ColumnData(.5));

        add(main, formData);
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
    }

    public void setEmail() {
        AsyncCallback<User> callback = new AsyncCallback<User>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(User user) {
                userEmailField.setValue(user.getMail());
            }
        };
        userManagementService.getUser(HarvesterUI.UTIL_MANAGER.getLoggedUserName(),callback);
    }

    public void resetValues() {
        userEmailField.clear();
        titleField.clear();
        messageField.clear();
        roleCombo.setValue(roleCombo.getStore().getAt(1));
    }
}
