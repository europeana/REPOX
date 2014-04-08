package harvesterUI.client.panels.services;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.servlets.externalServices.ESManagementServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.externalServices.ExternalServiceUI;

import java.util.Date;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-04-2011
 * Time: 17:19
 */
public class NewServiceDialog extends FormDialog {

    private ESManagementServiceAsync esManagementServiceAsync;
    private TextField<String> uriField, nameField, statusUriField, externalResultsUriField;
    private SimpleComboBox<String> typeCombo, externalServiceTypeCombo;
    private Button saveButton;
    private DefaultFormPanel newServiceFormPanel;
    private FieldSet generalInfoSet;
    private NewServiceFieldSet newServiceFieldSet;
    private boolean isEdit;
    private ExternalServiceUI externalServiceUI;

    public NewServiceDialog() {
        super(0.7,0.6);
        setHeading(HarvesterUI.CONSTANTS.addExternalService());
        esManagementServiceAsync = (ESManagementServiceAsync) Registry.get(HarvesterUI.ES_MANAGEMENT_SERVICE);
        createNewServiceDialog();
    }

    private void createNewServiceDialog() {
        FormData formData = new FormData("95%");
        newServiceFormPanel = new DefaultFormPanel();
        newServiceFormPanel.setHeaderVisible(false);

        generalInfoSet = new FieldSet();
        generalInfoSet.setHeading(HarvesterUI.CONSTANTS.generalInfo());
        generalInfoSet.setLayout(new EditableFormLayout(130));

        nameField = new TextField<String>();
        nameField.setFieldLabel(HarvesterUI.CONSTANTS.serviceName() + HarvesterUI.REQUIRED_STR);
        nameField.setId("serviceNameField");
        nameField.setAllowBlank(false);
        generalInfoSet.add(nameField, formData);

        uriField = new TextField<String>();
        uriField.setFieldLabel(HarvesterUI.CONSTANTS.uri() + HarvesterUI.REQUIRED_STR);
        uriField.setId("serviceUriField");
        uriField.setAllowBlank(false);
        generalInfoSet.add(uriField, formData);

        externalServiceTypeCombo = new SimpleComboBox<String>();
        externalServiceTypeCombo.setEditable(false);
        externalServiceTypeCombo.setFieldLabel("Monitoring Type" + HarvesterUI.REQUIRED_STR);
        externalServiceTypeCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        externalServiceTypeCombo.add("MONITORED");
        externalServiceTypeCombo.add("NO_MONITOR");
        externalServiceTypeCombo.setAllowBlank(false);
        externalServiceTypeCombo.setValue(externalServiceTypeCombo.getStore().getAt(0));
        externalServiceTypeCombo.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
                if(se.getSelectedItem().getValue().equals("NO_MONITOR")){
                    statusUriField.hide();
                    typeCombo.hide();
                    statusUriField.setAllowBlank(true);
                    typeCombo.setAllowBlank(true);
                    typeCombo.setSimpleValue("POST_PROCESS");
                }else{
                    statusUriField.show();
                    typeCombo.show();
                    statusUriField.setAllowBlank(false);
                    typeCombo.setAllowBlank(false);
                }
            }
        });
        generalInfoSet.add(externalServiceTypeCombo, formData);

        statusUriField = new TextField<String>();
        statusUriField.setFieldLabel(HarvesterUI.CONSTANTS.statusUri() + HarvesterUI.REQUIRED_STR);
        statusUriField.setId("serviceStatusUriField");
        statusUriField.setAllowBlank(false);
        generalInfoSet.add(statusUriField, formData);

        typeCombo = new SimpleComboBox<String>();
        typeCombo.setEditable(false);
        typeCombo.setFieldLabel(HarvesterUI.CONSTANTS.type() + HarvesterUI.REQUIRED_STR);
        typeCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        typeCombo.add("PRE_PROCESS");
        typeCombo.add("POST_PROCESS");
        typeCombo.setAllowBlank(false);
        typeCombo.setValue(typeCombo.getStore().getAt(0));
        generalInfoSet.add(typeCombo, formData);

        externalResultsUriField = new TextField<String>();
        externalResultsUriField.setFieldLabel("External Results Uri");
        externalResultsUriField.setId("externalResultsESUri");
        generalInfoSet.add(externalResultsUriField, formData);

        newServiceFormPanel.add(generalInfoSet);

        newServiceFieldSet = new NewServiceFieldSet();
        newServiceFormPanel.add(newServiceFieldSet);

        LayoutContainer addContainer = new LayoutContainer();
        HBoxLayout operationsLayout = new HBoxLayout();
        operationsLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        operationsLayout.setPack(BoxLayout.BoxLayoutPack.CENTER);
        addContainer.setLayout(operationsLayout);

        Button addButton = new Button(HarvesterUI.CONSTANTS.addField(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                newServiceFieldSet.addNewField();
            }
        });
        addButton.setIcon(HarvesterUI.ICONS.add());
        addContainer.add(addButton, new HBoxLayoutData(new Margins(0, 5, 5, 0)));
        newServiceFormPanel.add(addContainer);

        saveButton = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                AsyncCallback<String> callback = new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(String response) {
                        if(response.equals("ERROR")) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveExternalService(),HarvesterUI.CONSTANTS.errorSavingExternalService());
                            Dispatcher.forwardEvent(AppEvents.ViewServiceManager);
                        } else {
                            hide();
                            unmask();
                            newServiceFormPanel.submit();
                            HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.saveExternalService(),HarvesterUI.CONSTANTS.saveExternalServiceSuccess());
                            Dispatcher.forwardEvent(AppEvents.ViewServiceManager);
                        }
                    }
                };

                if(!isEdit){
                    externalServiceUI = new ExternalServiceUI(nameField.getValue()+"_created_"+new Date().toString(),nameField.getValue(),
                            uriField.getValue(),statusUriField.getValue(),typeCombo.getSimpleValue(),newServiceFieldSet.getAllFields(),
                            externalServiceTypeCombo.getSimpleValue());
                    externalServiceUI.setExternalResultUI(externalResultsUriField.getValue());
                }else {
                    externalServiceUI.setName(nameField.getValue());
                    externalServiceUI.setUri(uriField.getValue());
                    externalServiceUI.setStatusUri(statusUriField.getValue());
                    externalServiceUI.setType(typeCombo.getSimpleValue());
                    externalServiceUI.setServiceParameters(newServiceFieldSet.getAllFields());
                    externalServiceUI.setExternalServiceType(externalServiceTypeCombo.getSimpleValue());
                    externalServiceUI.setExternalResultUI(externalResultsUriField.getValue());
                }
                mask(HarvesterUI.CONSTANTS.saveExternalServiceMask());
                esManagementServiceAsync.saveExternalService(isEdit,externalServiceUI, callback);
            }
        });
        newServiceFormPanel.addButton(saveButton);
        newServiceFormPanel.addButton(new Button(HarvesterUI.CONSTANTS.cancel(),HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                hide();
                Dispatcher.get().dispatch(AppEvents.ViewServiceManager);
            }
        }));

        newServiceFormPanel.setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(newServiceFormPanel);
        binding.addButton(saveButton);

        add(newServiceFormPanel);
    }

    public void edit(ExternalServiceUI externalServiceUI){
        resetValues();
        setHeading(HarvesterUI.CONSTANTS.editExternalService() + ": " + externalServiceUI.getName());
        setIcon(HarvesterUI.ICONS.operation_edit());
        isEdit = true;
        nameField.setValue(externalServiceUI.getName());
        uriField.setValue(externalServiceUI.getUri());
        statusUriField.setValue(externalServiceUI.getStatusUri());
        externalResultsUriField.setValue((externalServiceUI.getExternalResultUI()));
        typeCombo.setSimpleValue(externalServiceUI.getType());
        externalServiceTypeCombo.setSimpleValue(externalServiceUI.getExternalServiceType());

        newServiceFieldSet.edit(externalServiceUI);
        this.externalServiceUI = externalServiceUI;
    }

    public void resetValues() {
        setHeading(HarvesterUI.CONSTANTS.addExternalService());
        setIcon(HarvesterUI.ICONS.add());
        newServiceFormPanel.reset();
        newServiceFieldSet.reset();
        isEdit = false;
        externalServiceUI = null;
    }

    public void addField(){
        newServiceFieldSet.addNewField();
    }

    @Override
    protected void onResize(int width, int height) {
        super.onResize(width,height);
//        super.layout();
//        newServiceFormPanel.layout(true);
        newServiceFieldSet.resize();
        layout(true);
        repaint();
    }
}
