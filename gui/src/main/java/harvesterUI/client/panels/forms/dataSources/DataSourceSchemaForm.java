package harvesterUI.client.panels.forms.dataSources;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.servlets.transformations.TransformationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.mdr.MarcFormat;
import harvesterUI.shared.mdr.SchemaUI;
import harvesterUI.shared.mdr.SchemaVersionUI;

import java.util.List;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 25-06-2012
 * Time: 14:25
 */
public class DataSourceSchemaForm {

    private TextField<String> schema, metadataNamespace;
    private ComboBox<SchemaUI> metadataFormatCombo;

    protected String DEFAULT_METADATA_PREFIX = "ese";

    private ComboBox<SchemaVersionUI> schemaVersionCombo;
    private LayoutContainer metadataFormatContainer;

    private FormData formData;
    private FieldSet dataSet;

    private LabelToolItem noSchemaFoundWarning;

    private SimpleComboBox<String> marcFormatCombo;

    private static int METADATA_FORMAT_COMBO_INDEX;

    public void addSchemaOAIFormaPart(FieldSet dataSet, FormData smallFixedFormData, FormData formData){
        this.formData = formData;
        this.dataSet = dataSet;

        marcFormatCombo = createMarcFormatCombo();
        METADATA_FORMAT_COMBO_INDEX = 4;

        createTextFields();

        createMetadataFormatComboOAI(smallFixedFormData);

        createSchemaVersionCombo();

        addToFieldSet(smallFixedFormData);
    }

    public void addSchemaSRUFormPart(FieldSet dataSet, FormData smallFixedFormData, FormData formData){
        this.formData = formData;
        this.dataSet = dataSet;

        marcFormatCombo = createMarcFormatCombo();
        METADATA_FORMAT_COMBO_INDEX = 0;

        createTextFields();

        createMetadataFormatComboOAI(smallFixedFormData);

        createSchemaVersionCombo();

        addToFieldSet(smallFixedFormData);
    }

    public void addSchemaYaddaFormatPart(FieldSet dataSet, FormData smallFixedFormData, FormData formData){
        this.formData = formData;
        this.dataSet = dataSet;

        marcFormatCombo = createMarcFormatCombo();
        METADATA_FORMAT_COMBO_INDEX = 0;

        createTextFields();

        createMetadataFormatComboOAI(smallFixedFormData);

        createSchemaVersionCombo();

        addMetadataFormatContainer(metadataFormatCombo,0,false);
        addToFieldSet(smallFixedFormData);
    }

    public void addSchemaFolderFormPart(FieldSet dataSet, FormData smallFixedFormData, FormData formData,
                                        ComboBox<ModelData> isoVariant, ComboBox<ModelData> characterEncoding,
                                        TextField<String> rootName){
        this.formData = formData;
        this.dataSet = dataSet;

        marcFormatCombo = createMarcFormatCombo();
        METADATA_FORMAT_COMBO_INDEX = -1;

        createTextFields();

        createMetadataFormatComboFolder(smallFixedFormData,isoVariant,characterEncoding,rootName);

        createSchemaVersionCombo();

        addMetadataFormatContainer(metadataFormatCombo,4,false);
        addToFieldSet(smallFixedFormData);
    }

    public void createTextFields(){
        schema = new TextField<String>();
        schema.setFieldLabel(HarvesterUI.CONSTANTS.schema() + HarvesterUI.REQUIRED_STR);
        schema.setId("schemaField");
        schema.setAllowBlank(false);
        schema.setReadOnly(true);

        metadataNamespace = new TextField<String>();
        metadataNamespace.setFieldLabel(HarvesterUI.CONSTANTS.metadataNamespace() + HarvesterUI.REQUIRED_STR);
        metadataNamespace.setId("mtdNmspacField");
        metadataNamespace.setAllowBlank(false);
        metadataNamespace.setReadOnly(true);
    }

    private void createMetadataFormatComboOAI(final FormData smallFixedFormData){
        ListStore<SchemaUI> metadataFormatStore = new ListStore<SchemaUI>();
        metadataFormatCombo = new ComboBox<SchemaUI>();
        metadataFormatCombo.setEmptyText("Loading Schemas...");
        metadataFormatCombo.setFieldLabel(HarvesterUI.CONSTANTS.metadataFormat());
        metadataFormatCombo.setId("metadataFormatComboId");
        metadataFormatCombo.setDisplayField("shortDesignation");
        metadataFormatCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        metadataFormatCombo.setStore(metadataFormatStore);
        metadataFormatCombo.setEditable(false);
        metadataFormatCombo.addSelectionChangedListener(new SelectionChangedListener<SchemaUI>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<SchemaUI> se) {
                fillSchemaVersionCombo(se.getSelectedItem());
                metadataNamespace.setValue(se.getSelectedItem().getNamespace());

                if(se.getSelectedItem().getShortDesignation().equals("MarcXchange")) {
                    dataSet.add(marcFormatCombo,smallFixedFormData);
                    dataSet.insert(marcFormatCombo,METADATA_FORMAT_COMBO_INDEX+1);
                    dataSet.layout();
                } else {
                    if(dataSet.getItems().contains(marcFormatCombo))
                        dataSet.remove(marcFormatCombo);
                }
            }
        });
    }

    private void createMetadataFormatComboFolder(final FormData smallFixedFormData,
                                                 final ComboBox<ModelData> isoVariant, final ComboBox<ModelData> characterEncoding,
                                                 final TextField<String> rootName){
        ListStore<SchemaUI> metadataFormatStore = new ListStore<SchemaUI>();

        metadataFormatCombo = new ComboBox<SchemaUI>();
        metadataFormatCombo.setFieldLabel(HarvesterUI.CONSTANTS.metadataFormat() + HarvesterUI.REQUIRED_STR);
        metadataFormatCombo.setDisplayField("shortDesignation");
        metadataFormatCombo.setEmptyText("Loading Schemas...");
        metadataFormatCombo.setId("metadatFormatComboId");
        metadataFormatCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        metadataFormatCombo.setStore(metadataFormatStore);
        metadataFormatCombo.setEditable(false);
        metadataFormatCombo.addSelectionChangedListener(new SelectionChangedListener<SchemaUI>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<SchemaUI> se) {

                fillSchemaVersionCombo(se.getSelectedItem());
                if(se.getSelectedItem().getShortDesignation().equals("MarcXchange")) {
//                    rootName.setEnabled(false);
                    dataSet.add(marcFormatCombo,smallFixedFormData);
                    dataSet.insert(marcFormatCombo, METADATA_FORMAT_COMBO_INDEX + 1);
                    dataSet.layout();
                } else {
//                    rootName.setEnabled(true);
                    if(dataSet.getItems().contains(marcFormatCombo))
                        dataSet.remove(marcFormatCombo);
                }

                if(se.getSelectedItem().getShortDesignation().equals("ISO2709")) {
                    dataSet.add(isoVariant,smallFixedFormData);
                    dataSet.insert(isoVariant,METADATA_FORMAT_COMBO_INDEX + 1);
                    dataSet.add(characterEncoding,smallFixedFormData);
                    dataSet.insert(characterEncoding,METADATA_FORMAT_COMBO_INDEX + 2);
                    dataSet.layout();
                } else {
                    if(dataSet.getItems().contains(isoVariant)) {
                        dataSet.remove(isoVariant);
                        dataSet.remove(characterEncoding);
                    }
                }
                metadataNamespace.setValue(se.getSelectedItem().getNamespace());
            }
        });
    }

    private void createSchemaVersionCombo(){
        ListStore<SchemaVersionUI> schemaVersionComboStore = new ListStore<SchemaVersionUI>();
        schemaVersionCombo = new ComboBox<SchemaVersionUI>();
        schemaVersionCombo.setFieldLabel("Schema Version");
        schemaVersionCombo.setEmptyText("Loading Schema Versions...");
        schemaVersionCombo.setDisplayField("version");
        schemaVersionCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        schemaVersionCombo.setStore(schemaVersionComboStore);
        schemaVersionCombo.setEditable(false);
        schemaVersionCombo.addSelectionChangedListener(new SelectionChangedListener<SchemaVersionUI>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<SchemaVersionUI> se) {
                schema.setValue(se.getSelectedItem().getXsdLink());
            }
        });
    }

    public void fillSchemaVersionCombo(SchemaUI schemaUI){
        schemaVersionCombo.getStore().removeAll();
        schemaVersionCombo.getStore().add(schemaUI.getSchemaVersions());
        schemaVersionCombo.setValue(schemaUI.getSchemaVersions().get(0));
    }

    public void setNoSchemasAvailable(){
        schemaVersionCombo.getStore().removeAll();
        schemaVersionCombo.getStore().add(new SchemaVersionUI(0,"",null));
        schemaVersionCombo.setValue(schemaVersionCombo.getStore().getAt(0));
        schema.setValue(null);
        metadataNamespace.setValue(null);
        addNoSchemaFoundWarningText();
    }

    private void addToFieldSet(FormData smallFixedFormData){
        dataSet.add(schemaVersionCombo,smallFixedFormData);
        dataSet.add(schema, formData);
        dataSet.add(metadataNamespace, formData);
    }

    public void addMetadataFormatContainer(final ComboBox mtfCombo, int index, boolean isObtainedPrefixes){
        Button newSchemaButton = new Button(HarvesterUI.CONSTANTS.newStr(),HarvesterUI.ICONS.schema_new(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                Dispatcher.forwardEvent(AppEvents.ViewAddSchemaDialog);
            }
        });

        if(dataSet.getItems().contains(metadataFormatContainer))
            dataSet.remove(metadataFormatContainer);

        metadataFormatContainer = new LayoutContainer();
        HBoxLayout oaiUrlContainerLayout = new HBoxLayout();
        oaiUrlContainerLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        metadataFormatContainer.setLayout(oaiUrlContainerLayout);
        LabelToolItem label = new LabelToolItem(HarvesterUI.CONSTANTS.metadataFormat());
        label.setWidth(DataSourceForm.SPECIAL_FIELDS_LABEL_WIDTH);
        label.addStyleName("defaultFormFieldLabel");
        metadataFormatContainer.add(label, new HBoxLayoutData(new Margins(0, 2, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
        HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0));
        flex.setFlex(1.0);
        metadataFormatContainer.add(mtfCombo, flex);

        if(!isObtainedPrefixes){
            Button editSchemaButton = new Button(HarvesterUI.CONSTANTS.edit(),HarvesterUI.ICONS.schema_edit(),new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent ce) {
                    Dispatcher.forwardEvent(AppEvents.ViewAddSchemaDialog,metadataFormatCombo.getValue());
                }
            });
            metadataFormatContainer.add(newSchemaButton, new HBoxLayoutData(new Margins(0, 5, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
            metadataFormatContainer.add(editSchemaButton, new HBoxLayoutData(new Margins(0, 150, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
        }else{
            Button newSchemaButtonWithPrefix = new Button(HarvesterUI.CONSTANTS.newStr(),HarvesterUI.ICONS.schema_new(),new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent ce) {
                    if(mtfCombo instanceof SimpleComboBox)
                        Dispatcher.forwardEvent(AppEvents.ViewAddSchemaDialog,((SimpleComboBox<String>)mtfCombo).getValue().getValue());
                }
            });
            metadataFormatContainer.add(newSchemaButtonWithPrefix, new HBoxLayoutData(new Margins(0, 165, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
        }

        dataSet.add(metadataFormatContainer,formData);
        dataSet.insert(metadataFormatContainer,index);
        dataSet.layout();
    }

    private void addNoSchemaFoundWarningText(){
        int index = dataSet.indexOf(metadataFormatContainer);

        if(noSchemaFoundWarning != null && dataSet.getItems().contains(noSchemaFoundWarning))
            dataSet.remove(noSchemaFoundWarning);

        noSchemaFoundWarning = new LabelToolItem("WARNING: No matching schema found on the system. Please create a new one using the \"New\" button");
        noSchemaFoundWarning.setStyleName("noSchemaFoundWarning");
        dataSet.insert(noSchemaFoundWarning, index + 1);
        dataSet.layout(true);
    }

    public void removeNoSchemaFoundWarning(){
        if(noSchemaFoundWarning != null && dataSet.getItems().contains(noSchemaFoundWarning))
            dataSet.remove(noSchemaFoundWarning);
        dataSet.layout(true);
    }

    public void loadMetadataFormatComboSchemas(final boolean edit, final DataSourceUI dataSourceUI){
        AsyncCallback<List<SchemaUI>> callback = new AsyncCallback<List<SchemaUI>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<SchemaUI> results) {
                getMetadataFormatCombo().getStore().removeAll();
                getMetadataFormatCombo().getStore().add(results);

                // clear combo boxes filters
                getMetadataFormatCombo().getStore().clearFilters();

                if(edit){
                    SchemaUI schemaUI = getMetadataFormatCombo().getStore().findModel("shortDesignation",dataSourceUI.getSourceMDFormat());
                    if(schemaUI != null){
                        getMetadataFormatCombo().setValue(schemaUI);
                        schemaVersionCombo.setValue(schemaVersionCombo.getStore().findModel("xsdLink",dataSourceUI.getSchema()));
                    }else{
                        getMetadataFormatCombo().setValue(getMetadataFormatCombo().getStore().findModel("shortDesignation",DEFAULT_METADATA_PREFIX));
                    }
                } else
                    getMetadataFormatCombo().setValue(getMetadataFormatCombo().getStore().findModel("shortDesignation",DEFAULT_METADATA_PREFIX));
            }
        };
        TransformationsServiceAsync service = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
        service.getAllMetadataSchemas(callback);
    }

    public void reloadMetadataSchemas(final String schemaShortDesignation){
        AsyncCallback<List<SchemaUI>> callback = new AsyncCallback<List<SchemaUI>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<SchemaUI> results) {
                getMetadataFormatCombo().getStore().removeAll();
                getMetadataFormatCombo().getStore().add(results);
                // clear combo boxes filters
                getMetadataFormatCombo().getStore().clearFilters();

                SchemaUI schemaUI = getMetadataFormatCombo().getStore().findModel("shortDesignation",schemaShortDesignation);
                if(schemaUI != null)
                    getMetadataFormatCombo().setValue(schemaUI);
                else
                    getMetadataFormatCombo().setValue(getMetadataFormatCombo().getStore().findModel("shortDesignation",DEFAULT_METADATA_PREFIX));
            }
        };
        TransformationsServiceAsync service = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
        service.getAllMetadataSchemas(callback);
    }

    public TextField<String> getSchema() {
        return schema;
    }

    public TextField<String> getMetadataNamespace() {
        return metadataNamespace;
    }

    public ComboBox<SchemaUI> getMetadataFormatCombo() {
        return metadataFormatCombo;
    }

    public String getMarcFormat(){
        if(marcFormatCombo.isVisible())
            return marcFormatCombo.getSimpleValue();
        else
            return "";
    }

    public void editMarcCombo(String marcFormat){
        marcFormatCombo.setSimpleValue(marcFormat);
    }

    private SimpleComboBox<String> createMarcFormatCombo(){
        SimpleComboBox<String> marcFormatCombo = new SimpleComboBox<String>();
        marcFormatCombo.setEditable(false);
        marcFormatCombo.setFieldLabel("Marc Format" + HarvesterUI.REQUIRED_STR);
        marcFormatCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        for(MarcFormat marcFormat : MarcFormat.values()){
            marcFormatCombo.add(marcFormat.name());
        }
        marcFormatCombo.setValue(marcFormatCombo.findModel(MarcFormat.None.toString()));

        return marcFormatCombo;
    }
}
