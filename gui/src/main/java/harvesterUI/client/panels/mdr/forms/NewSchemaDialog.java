package harvesterUI.client.panels.mdr.forms;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
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
import harvesterUI.client.servlets.transformations.TransformationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.mdr.SchemaUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-04-2011
 * Time: 17:19
 */
public class NewSchemaDialog extends FormDialog {

    private TextField<String> designationField, shortDesignationField,descriptionField,
            namespaceField, notesField;
    private SchemaUI associatedSchema = null;
    private DefaultFormPanel newSchemaFormPanel;
    private FieldSet generalInfoSet;
    private NewSchemaVersionFieldSet newSchemaVersionFieldSet;
    private CheckBoxGroup isOAIAvailable;

    public NewSchemaDialog() {
        super(0.55,0.5);
        createNewSchemaDialog();
    }

    private void createNewSchemaDialog() {
        FormData formData = new FormData("95%");
        setIcon(HarvesterUI.ICONS.schema_new());

        newSchemaFormPanel = new DefaultFormPanel();
        newSchemaFormPanel.setHeaderVisible(false);

        generalInfoSet = new FieldSet();
        generalInfoSet.setHeading("Schema Information"); //todo multi lang
        generalInfoSet.setLayout(new EditableFormLayout(130));

        shortDesignationField = new TextField<String>();
        shortDesignationField.setFieldLabel(HarvesterUI.CONSTANTS.shortDesignation() + HarvesterUI.REQUIRED_STR);
        shortDesignationField.setId("schm_shortDesign");
        shortDesignationField.setAllowBlank(false);
        generalInfoSet.add(shortDesignationField, formData);

        namespaceField = new TextField<String>();
        namespaceField.setFieldLabel(HarvesterUI.CONSTANTS.namespace() + HarvesterUI.REQUIRED_STR);
        namespaceField.setId("schm_nmspace");
        namespaceField.setAllowBlank(false);
        generalInfoSet.add(namespaceField, formData);

        designationField = new TextField<String>();
        designationField.setFieldLabel(HarvesterUI.CONSTANTS.designation());
        designationField.setId("schm_design");
        generalInfoSet.add(designationField, formData);

        descriptionField = new TextField<String>();
        descriptionField.setId("schema_descri");
        descriptionField.setFieldLabel(HarvesterUI.CONSTANTS.description());
        generalInfoSet.add(descriptionField, formData);

        notesField = new TextField<String>();
        notesField.setFieldLabel(HarvesterUI.CONSTANTS.notes());
        notesField.setId("schm_notes");
        generalInfoSet.add(notesField, formData);

        isOAIAvailable = new CheckBoxGroup();
        CheckBox isOAIAvailableCB = new CheckBox();
        isOAIAvailable.setFieldLabel("OAI Available" + "?" + HarvesterUI.REQUIRED_STR); //TODO Multi lang
        isOAIAvailableCB.setValue(true);
        isOAIAvailable.add(isOAIAvailableCB);
        generalInfoSet.add(isOAIAvailable,formData);

        Button saveButton = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                saveData();
            }
        });

        newSchemaFormPanel.add(generalInfoSet);

        newSchemaVersionFieldSet = new NewSchemaVersionFieldSet();
        newSchemaFormPanel.add(newSchemaVersionFieldSet);

        LayoutContainer addContainer = new LayoutContainer();
        HBoxLayout operationsLayout = new HBoxLayout();
        operationsLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        operationsLayout.setPack(BoxLayout.BoxLayoutPack.CENTER);
        addContainer.setLayout(operationsLayout);

        Button addButton = new Button("Add Schema Version",HarvesterUI.ICONS.schema_version_new(), new SelectionListener<ButtonEvent>() { //todo: multi lang
            @Override
            public void componentSelected(ButtonEvent be) {
                newSchemaVersionFieldSet.addNewField(false);
            }
        });
        addButton.setIcon(HarvesterUI.ICONS.schema_version_new());
        addContainer.add(addButton, new HBoxLayoutData(new Margins(0, 5, 5, 0)));
        newSchemaFormPanel.add(addContainer);

        newSchemaFormPanel.addButton(saveButton);
        newSchemaFormPanel.addButton(new Button(HarvesterUI.CONSTANTS.cancel(), HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                hide();
            }
        }));

        newSchemaFormPanel.setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(newSchemaFormPanel);
        binding.addButton(saveButton);

        add(newSchemaFormPanel);
    }

    public void saveData() {
        AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(ResponseState responseState) {
                if(responseState == ResponseState.ALREADY_EXISTS){
                    HarvesterUI.UTIL_MANAGER.getInfoBox("New Metadata Schema","Metadata Schema with that short designation already exists"); //todo multi lang
                    return;
                }
                hide();
                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.saveMetadataSchema(), HarvesterUI.CONSTANTS.saveMetadataSchemaSuccess());
                Dispatcher.forwardEvent(AppEvents.ReloadSchemas,shortDesignationField.getValue().trim());
            }
        };
        newSchemaFormPanel.submit();
        String designation = designationField.getValue();
        String shortDesignation = shortDesignationField.getValue();
        String description = descriptionField.getValue();
        String namespace = namespaceField.getValue();
        String notes = notesField.getValue();
        SchemaUI schemaUI = new SchemaUI(designation,shortDesignation,description,namespace,notes);
        schemaUI.getSchemaVersions().addAll(newSchemaVersionFieldSet.getAllSchemaVersions());
        schemaUI.setOAIAvailable((Boolean)isOAIAvailable.get(0).getValue());
        TransformationsServiceAsync service = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
        service.saveMetadataSchema(schemaUI, associatedSchema == null ? null : associatedSchema.getShortDesignation(), callback);
    }

    public void addField(boolean solo){
        newSchemaVersionFieldSet.addNewField(solo);
    }

    public void newDialogFromKnownPrefix(String metadataPrefix){
        resetValues();
        shortDesignationField.setValue(metadataPrefix.trim());
        newSchemaVersionFieldSet.reset();
    }

    public void edit(SchemaUI schemaUI){
        resetValues();
        associatedSchema = schemaUI;
        setHeading(HarvesterUI.CONSTANTS.editSchema()+": " + schemaUI.getShortDesignation());
        setIcon(HarvesterUI.ICONS.schema_edit());
        designationField.setValue(schemaUI.getDesignation());
        shortDesignationField.setValue(schemaUI.getShortDesignation());
        descriptionField.setValue(schemaUI.getDescription());
        namespaceField.setValue(schemaUI.getNamespace());
        notesField.setValue(schemaUI.getNotes());

        newSchemaVersionFieldSet.edit(schemaUI);
        ((CheckBox)isOAIAvailable.get(0)).setValue(schemaUI.isOAIAvailable());
    }

    public void resetValues() {
        associatedSchema = null;
        designationField.clear();
        shortDesignationField.clear();
        descriptionField.clear();
        namespaceField.clear();
        notesField.clear();
        newSchemaVersionFieldSet.reset();
        ((CheckBox)isOAIAvailable.get(0)).setValue(true);

        setHeading(HarvesterUI.CONSTANTS.addSchema());
    }

    @Override
    protected void onResize(int width, int height) {
        super.onResize(width,height);
        super.layout(true);
        generalInfoSet.layout(true);
        newSchemaVersionFieldSet.resize();
        layout(true);
    }
}
