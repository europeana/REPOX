package harvesterUI.client.panels.forms.dataSources;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.services.DataSetListParameter;
import harvesterUI.client.servlets.externalServices.ESManagementServiceAsync;
import harvesterUI.client.util.FieldSetWithClickOption;
import harvesterUI.client.util.FieldSetWithExternalService;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.externalServices.ExternalServiceUI;
import harvesterUI.shared.externalServices.ServiceParameterUI;

import java.util.Date;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 29-11-2011
 * Time: 11:53
 */
public class DataSourceServicesPanel extends FieldSetWithClickOption {

    private FormData formData;
    protected ListStore<ExternalServiceUI> externalServicesStore;
    private ESManagementServiceAsync esManagementServiceAsync;
    protected SelectionChangedListener<ExternalServiceUI> selectionChangedComboListener;
    protected EditableFormLayout layout;
    protected ComponentPlugin exampleTextPlugin;
    protected SimpleComboBox<String> executionTypeCombo;
    private LayoutContainer addContainer, executionTypeContainer;

    protected int DEFAULT_LABEL_WIDTH = 150;

    public DataSourceServicesPanel(FormData data) {
        formData = data;
        esManagementServiceAsync = (ESManagementServiceAsync) Registry.get(HarvesterUI.ES_MANAGEMENT_SERVICE);

        layout = new EditableFormLayout(DEFAULT_LABEL_WIDTH);
        setHeading(HarvesterUI.CONSTANTS.externalRestServices());
        setLayout(layout);
        setCheckboxToggle(true);
        setLayoutOnChange(true);

        executionTypeCombo = new SimpleComboBox<String>();
        executionTypeCombo.setEditable(false);
        executionTypeCombo.add("PARALLEL");
        executionTypeCombo.add("SEQUENTIAL");
        executionTypeCombo.setTriggerAction(ComboBox.TriggerAction.ALL);

        exampleTextPlugin = new ComponentPlugin() {
            public void init(Component component) {
                component.addListener(Events.Render, new Listener<ComponentEvent>() {
                    public void handleEvent(ComponentEvent be) {
                        El elem = be.getComponent().el().findParent(".x-form-element", 3);
                        // should style in external CSS  rather than directly
                        elem.appendChild(XDOM.create("<div style='color: grey;padding: 1 0 2 0px;'>" + be.getComponent().getData("text") + "</div>"));
                    }
                });
            }
        };

        externalServicesStore = new ListStore<ExternalServiceUI>();

        createAddServiceButton();
        createChooseExecutionType();
        createComboChangeListener();
    }

    private void createAddServiceButton(){
        addContainer = new LayoutContainer();
        HBoxLayout operationsLayout = new HBoxLayout();
        operationsLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        operationsLayout.setPack(BoxLayout.BoxLayoutPack.CENTER);
        addContainer.setLayout(operationsLayout);
        Button addButton = new Button(HarvesterUI.CONSTANTS.add(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                addNewRestServiceCombo();
            }
        });
        addButton.setIcon(HarvesterUI.ICONS.add());
        addContainer.add(addButton, new HBoxLayoutData(new Margins(0, 5, 5, 0)));
    }

    private void repositionAddButton(){
        insert(addContainer,getItemCount());
    }

    private void createChooseExecutionType(){
        executionTypeContainer = new LayoutContainer();
        HBoxLayout operationsLayout = new HBoxLayout();
        operationsLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        operationsLayout.setPack(BoxLayout.BoxLayoutPack.CENTER);
        executionTypeContainer.setLayout(operationsLayout);
        executionTypeContainer.add(new LabelToolItem(HarvesterUI.CONSTANTS.executionType()), new HBoxLayoutData(new Margins(0, 5, 5, 0)));
        executionTypeCombo.setValue(executionTypeCombo.getStore().getAt(0));
        executionTypeContainer.add(executionTypeCombo, new HBoxLayoutData(new Margins(0, 5, 5, 0)));
        executionTypeContainer.add(executionTypeCombo, new HBoxLayoutData(new Margins(0, 5, 5, 0)));
    }

    private void repositionChooseExecutionTypeCombo(){
        insert(executionTypeContainer,0);
    }

    public void setEditServices(final DataSourceUI dataSourceUI) {
        ((DefaultFormPanel)getParent()).mask(HarvesterUI.CONSTANTS.loadingRestServicesMask());

        AsyncCallback<List<ExternalServiceUI>> callback = new AsyncCallback<List<ExternalServiceUI>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<ExternalServiceUI> services) {
                externalServicesStore.removeAll();
                ExternalServiceUI noneTrans = new ExternalServiceUI("none","None","","","",null, "");
                externalServicesStore.add(noneTrans);
                externalServicesStore.add(services);

                if(dataSourceUI.getRestServiceUIList().size() > 0) {
                    expand();
                    editRestServiceCombos(dataSourceUI.getRestServiceUIList(),dataSourceUI);
                } else
                    resetValues();

                ((DefaultFormPanel)getParent()).unmask();
            }
        };
        esManagementServiceAsync.getAllExternalServices(false,callback);
    }

    public void resetValues() {
        AsyncCallback<List<ExternalServiceUI>> callback = new AsyncCallback<List<ExternalServiceUI>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<ExternalServiceUI> services) {
                externalServicesStore.removeAll();
                ExternalServiceUI noneTrans = new ExternalServiceUI("none","None","","","",null,"");
                externalServicesStore.add(noneTrans);
                externalServicesStore.add(services);

                removeAll();
                addNewRestServiceCombo();
                collapse();
            }
        };
        esManagementServiceAsync.getAllExternalServices(false,callback);
    }

    protected void removeTheContainersParameters(FieldSet fieldSet) {
        Component component = fieldSet.getItem(0);
        fieldSet.removeAll();
        fieldSet.add(component,formData);
    }

    public void addNewRestServiceCombo(){
        final FieldSetWithExternalService newFS = new FieldSetWithExternalService();
        newFS.setLayout(new EditableFormLayout(DEFAULT_LABEL_WIDTH));

        ComboBox<ExternalServiceUI> newRestServicesCombo = new ComboBox<ExternalServiceUI>();
        newRestServicesCombo.setFieldLabel(HarvesterUI.CONSTANTS.restService());
        newRestServicesCombo.setDisplayField("name");
        newRestServicesCombo.setEmptyText(HarvesterUI.CONSTANTS.noServicesAvailable());
        newRestServicesCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        newRestServicesCombo.setStore(externalServicesStore);
        newRestServicesCombo.setEditable(false);
        newRestServicesCombo.setWidth(150);
        newRestServicesCombo.setValue(externalServicesStore.getModels().get(0));
        newRestServicesCombo.addSelectionChangedListener(selectionChangedComboListener);

        Button removeService = new Button(HarvesterUI.CONSTANTS.delete(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                remove(newFS);
            }
        });
        removeService.setIcon(HarvesterUI.ICONS.delete());

        LayoutContainer newRestServiceComboContainer = new LayoutContainer();
        HBoxLayout transformContainerLayout = new HBoxLayout();
        transformContainerLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        newRestServiceComboContainer.setLayout(transformContainerLayout);
        LabelToolItem label = new LabelToolItem(HarvesterUI.CONSTANTS.restService());
        label.setWidth(154);
        label.addStyleName("defaultFormFieldLabel");
        newRestServiceComboContainer.add(label, new HBoxLayoutData(new Margins(0, 2, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
        newRestServiceComboContainer.add(newRestServicesCombo, new HBoxLayoutData(new Margins(0, 3, 5, 0)));
        newRestServiceComboContainer.add(removeService, new HBoxLayoutData(new Margins(0, 5, 5, 0)));
        createIsEnabledCheckbox(newRestServiceComboContainer,null,newFS);
        newFS.add(newRestServiceComboContainer,formData);

        add(newFS, formData);
        repositionAddButton();
        repositionChooseExecutionTypeCombo();
    }

    protected void editRestServiceCombos(List<ExternalServiceUI> externalServiceUIs,DataSourceUI dataSourceUI){
        removeAll();

        for(ExternalServiceUI externalServiceUI : externalServiceUIs){
            final FieldSetWithExternalService newFS = new FieldSetWithExternalService();
            newFS.setLayout(new EditableFormLayout(DEFAULT_LABEL_WIDTH));
            newFS.setIsNewFieldSet(false);

            //edit Execution type combo
            for(SimpleComboValue<String> comboSel: executionTypeCombo.getStore().getModels()) {
                if(comboSel.getValue().equals(dataSourceUI.getExternalServicesRunType())) {
                    executionTypeCombo.setValue(comboSel);
                }
            }

            ComboBox<ExternalServiceUI> newRestServicesCombo = new ComboBox<ExternalServiceUI>();
            newRestServicesCombo.setFieldLabel(HarvesterUI.CONSTANTS.restService());
            newRestServicesCombo.setDisplayField("name");
            newRestServicesCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
            newRestServicesCombo.setStore(externalServicesStore);
            newRestServicesCombo.setEditable(false);
            newRestServicesCombo.addSelectionChangedListener(selectionChangedComboListener);

            Button removeService = new Button(HarvesterUI.CONSTANTS.delete(),new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent ce) {
                    remove(newFS);
                }
            });
            removeService.setIcon(HarvesterUI.ICONS.delete());

            LayoutContainer newRestServiceComboContainer = new LayoutContainer();
            HBoxLayout newRestServiceComboContainerLayout = new HBoxLayout();
            newRestServiceComboContainerLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
            newRestServiceComboContainer.setLayout(newRestServiceComboContainerLayout);
            LabelToolItem label = new LabelToolItem(HarvesterUI.CONSTANTS.restService());
            label.setWidth(154);
            label.addStyleName("defaultFormFieldLabel");
            newRestServiceComboContainer.add(label, new HBoxLayoutData(new Margins(0, 2, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
            newRestServiceComboContainer.add(newRestServicesCombo, new HBoxLayoutData(new Margins(0, 3, 5, 0)));
            newRestServiceComboContainer.add(removeService, new HBoxLayoutData(new Margins(0, 5, 5, 0)));
            createIsEnabledCheckbox(newRestServiceComboContainer,externalServiceUI,newFS);
            newFS.add(newRestServiceComboContainer,formData);

            add(newFS);

            // Edit combo box
            for(ExternalServiceUI storeES : externalServicesStore.getModels()){
                if(externalServiceUI.getId().equals(storeES.getId())){
                    for(ServiceParameterUI serviceParameterUI : externalServiceUI.getServiceParameters()){
                        for(ServiceParameterUI storeSP : storeES.getServiceParameters()){
                            if(serviceParameterUI.getName().equals(storeSP.getName())){
                                storeSP.setValue(serviceParameterUI.getValue());
                            }
                        }
                    }
                }
            }

            for(ExternalServiceUI comboSel: newRestServicesCombo.getStore().getModels()) {
                if(comboSel.get("name").equals(externalServiceUI.getName())) {
                    newRestServicesCombo.setValue(comboSel);
                }
            }
        }
        layout();
        repositionAddButton();
        repositionChooseExecutionTypeCombo();
    }

    private void createIsEnabledCheckbox(LayoutContainer container, ExternalServiceUI externalServiceUI,final FieldSetWithExternalService fieldSetWithExternalService){
        CheckBoxGroup checkBoxGroup = new CheckBoxGroup();
        final CheckBox isEnabled = new CheckBox();
        checkBoxGroup.setId("isEnabled");
        checkBoxGroup.add(isEnabled);
        isEnabled.addListener(Events.OnChange,new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                fieldSetWithExternalService.getExternalServiceUI().setEnabled(isEnabled.getValue());
            }
        });

        if(externalServiceUI == null)
            isEnabled.setValue(true);
        else
            isEnabled.setValue(externalServiceUI.isEnabled());

        container.add(new LabelToolItem("Enabled?"), new HBoxLayoutData(new Margins(0, 5, 5, 5)));
        container.add(checkBoxGroup, new HBoxLayoutData(new Margins(0, 5, 5, 0)));
    }

    protected void createComboChangeListener(){
        selectionChangedComboListener = new SelectionChangedListener<ExternalServiceUI>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<ExternalServiceUI> se) {
                FieldSetWithExternalService parent = ((FieldSetWithExternalService)((LayoutContainer)((ComboBox)se.getSource()).getParent()).getParent());
                parent.setExternalServiceUI(se.getSelectedItem());
                removeTheContainersParameters(parent);

                if(se.getSelectedItem().getName().equals("None")){
                    layout();
                    return;
                }

                LabelField serviceURI = new LabelField();
                serviceURI.setFieldLabel(HarvesterUI.CONSTANTS.uriType());
                serviceURI.setText(se.getSelectedItem().getUri() + " [" + se.getSelectedItem().getType() + "]");
                parent.add(serviceURI,formData);

                // No monitored external services special ui
                if(se.getSelectedItem().getExternalServiceType().equals("NO_MONITOR")){
                    ExternalServiceUI selectedExternalService = se.getSelectedItem();
                    for(ServiceParameterUI serviceParameterUI : selectedExternalService.getServiceParameters()){
                        TextField<String> textField = new TextField<String>();
                        textField.setFieldLabel(serviceParameterUI.getName());
                        textField.setValue(serviceParameterUI.getSemantics());
                        textField.setEnabled(false);
                        parent.add(textField,formData);
                    }
                    layout();
                    return;
                }

                if(se.getSelectedItem().getServiceParameters() != null){
                    for(ServiceParameterUI serviceParameterUI: se.getSelectedItem().getServiceParameters()){
                        // TODO: different field types
                        if(serviceParameterUI.getSemantics().equals("DATA_SET_LIST")){
                            DataSetListParameter dataSetListParameter = new DataSetListParameter(serviceParameterUI);
//                            createExamples(dataSetListParameter,serviceParameterUI);
                            parent.add(dataSetListParameter, formData);
                        }else if(serviceParameterUI.getType().equals("TEXT_FIELD")){
                            TextField<String> textField = new TextField<String>();
                            textField.setId(serviceParameterUI.getId());
                            checkRequired(serviceParameterUI, textField);
                            checkParameterValue(parent, serviceParameterUI, textField);
                            createExamples(textField,serviceParameterUI);
                            createFieldSemantics(serviceParameterUI,textField);
                            parent.add(textField, formData);
                        }else if(serviceParameterUI.getType().equals("DATE_FIELD")){
                            DateField dateField = new DateField();
                            dateField.setEditable(false);
                            dateField.setId(serviceParameterUI.getId());
                            checkRequired(serviceParameterUI,dateField);
                            checkParameterValue(parent,serviceParameterUI,dateField);
                            createExamples(dateField,serviceParameterUI);
                            parent.add(dateField, formData);
                        }else if(serviceParameterUI.getType().equals("BOOLEAN_FIELD")){
                            createBooleanField(parent, serviceParameterUI);
                        }else if(serviceParameterUI.getType().equals("COMBO_FIELD")){
                            SimpleComboBox<String> comboBox = new SimpleComboBox<String>();
                            comboBox.setId(serviceParameterUI.getId());
                            comboBox.setEditable(false);
                            comboBox.setTriggerAction(ComboBox.TriggerAction.ALL);
                            for(String comboValue : serviceParameterUI.getComboValues()){
                                comboBox.add(comboValue);
                            }
                            comboBox.setValue(comboBox.getStore().getAt(0));
                            checkRequired(serviceParameterUI,comboBox);
                            checkParameterValue(parent,serviceParameterUI,comboBox);
                            createExamples(comboBox,serviceParameterUI);
                            parent.add(comboBox, formData);
                        }
                    }
                }
                layout();
            }
        };
    }

    protected void createBooleanField(FieldSetWithExternalService parent, ServiceParameterUI serviceParameterUI){
        CheckBoxGroup checkGroup = new CheckBoxGroup();
        CheckBox checkBox = new CheckBox();
        checkGroup.setId(serviceParameterUI.getId());
        checkGroup.setFieldLabel(serviceParameterUI.getName() + HarvesterUI.REQUIRED_STR);

        checkParameterValue(parent,serviceParameterUI,checkBox);
        createExamples(checkGroup,serviceParameterUI);
        checkGroup.add(checkBox);
        parent.add(checkGroup, formData);
    }

    protected void createExamples(Component component, ServiceParameterUI serviceParameterUI){
        if(serviceParameterUI.getExample().isEmpty())
            return;

        component.addPlugin(exampleTextPlugin);
        component.setData("text",serviceParameterUI.getExample());
    }

    protected void checkRequired(ServiceParameterUI serviceParameterUI,Field field){
        if(serviceParameterUI.getRequired()){
            field.setFieldLabel(serviceParameterUI.getName() + HarvesterUI.REQUIRED_STR);
            // TODO: different field types
            if(field instanceof TextField){
                TextField textField = (TextField)field;
                textField.setAllowBlank(false);
            }
            if(field instanceof DateField){
                DateField dateField = (DateField)field;
                dateField.setAllowBlank(false);
            }
            if(field instanceof SimpleComboBox){
                SimpleComboBox simpleComboBox = (SimpleComboBox)field;
                simpleComboBox.setAllowBlank(false);
            }
        } else
            field.setFieldLabel(serviceParameterUI.getName());
    }

    protected void checkParameterValue(FieldSetWithExternalService parent,ServiceParameterUI serviceParameterUI,Field field){
        // Only load parameter values when editing and not when adding new services
        if(!parent.isNewFieldSet()){
            if(serviceParameterUI.getValue() != null){
                // TODO: different field types
                if(field instanceof TextField && !(field instanceof SimpleComboBox) && !(field instanceof DateField)){
                    TextField<String> textField = (TextField<String>)field;
                    textField.setValue(serviceParameterUI.getValue());
                }
                else if(field instanceof DateField){
                    DateField dateField = (DateField)field;
                    dateField.setValue(new Date(serviceParameterUI.getValue()));
                }
                else if(field instanceof CheckBox){
                    CheckBox checkBox = (CheckBox)field;
                    checkBox.setValue(Boolean.parseBoolean(serviceParameterUI.getValue()));
                }
                else if(field instanceof SimpleComboBox){
                    SimpleComboBox<String> simpleComboBox = (SimpleComboBox)field;
                    for(SimpleComboValue<String> comboSel: simpleComboBox.getStore().getModels()) {
                        if(comboSel.getValue().equals(serviceParameterUI.getValue())) {
                            simpleComboBox.setValue(comboSel);
                        }
                    }
                }
            }
        }
    }

    private void createFieldSemantics(ServiceParameterUI serviceParameterUI,TextField<String> field){
        // Only suggest values when field doesn't have value
        if(field.getValue() == null){
            if(!serviceParameterUI.getSemantics().equals("NONE")){
                if(serviceParameterUI.getSemantics().equals("SERVER_OAI_URL"))
                    field.setValue(UtilManager.getOaiServerUrl());
                else if(serviceParameterUI.getSemantics().equals("DATA_SET_METADATA_FORMAT")){
                    field.setValue(((DataSourceForm)getParent()).getMetadataFormat());
                } else if(serviceParameterUI.getSemantics().equals("DATA_SET_ID"))
                    field.setValue(((DataSourceForm)getParent()).getDataSetId());
                else if(serviceParameterUI.getSemantics().equals("DATA_SET_SCHEMA_URL"))
                    field.setValue(((DataSourceForm)getParent()).getSchema());
                    // Eudml Import Chains
                else if(serviceParameterUI.getSemantics().equals("EUDML_IMPORT_PM_CHAIN")){
                    field.setValue("OAI::http://oai.bn.pt/servlet/OAIHandler::oai_dc::PortugalMatematica;" +
                            "Xslt::/home/jedmundo/importConf/xsl/initial-import/pmToNlm.xsl;" +
                            "{NoTexImprovements};" +
                            "NlmWriter::"+((DataSourceForm)getParent()).getFolderPath()+"::title");
                }else if(serviceParameterUI.getSemantics().equals("EUDML_IMPORT_DMLCZ_SERIAL_CHAIN")){
                    field.setValue("OAI::http://oai.dml.cz/request::nlm::*;" +
                            "NlmWriter::"+((DataSourceForm)getParent()).getFolderPath()+"::title");
                }else if(serviceParameterUI.getSemantics().equals("DATA_PROVIDER_EMAIL")){
                    field.setValue(((DataSourceForm)getParent()).getDataProviderParent().getEmail());
                }
            }
        }
    }

    public SimpleComboBox<String> getExecutionTypeCombo() {
        return executionTypeCombo;
    }
}
