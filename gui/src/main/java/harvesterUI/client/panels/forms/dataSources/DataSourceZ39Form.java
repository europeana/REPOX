package harvesterUI.client.panels.forms.dataSources;

import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.models.Attribute;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.DatasetType;

import java.util.Date;
import java.util.List;

import pt.utl.ist.dataProvider.dataSource.IdExtractedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.IdGeneratedRecordIdPolicy;
import pt.utl.ist.util.shared.ProjectType;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 15-03-2011
 * Time: 14:14
 */
public class DataSourceZ39Form extends DataSourceForm {

    private FieldSet dataSet;
    private FormData formData;
    private FileUploadField fileUploadField;
    private String oldDataSetId = "";

    private DateField earliestDate;
    private ComboBox<ModelData> harvestMethodCombo, idPolicyCombo, recordSyntaxCombo, characterEncoding;
    private TextField<String> address, port, database, user, password,idXPathField;
    private NumberField maximumID;

    // Europeana Fields
    private TextField<String> name, nameCode,exportPath;

    public DataSourceZ39Form(FormData data) {
        super(data);
        formData = data;
        setHeaderVisible(false);
        setLayout(new FitLayout());
        setBodyBorder(false);

        setMethod(Method.POST);
        setEncoding(Encoding.MULTIPART);
        setAction(GWT.getModuleBaseURL() + "z39fileupload");

        createIDPolicyBoxes();
        createDataSet();
        add(dataSet);
        add(createOutputSet());
        add(dataSourceServicesPanel);

        Button saveButton = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                submitParentForm();
            }
        });
        addButton(saveButton);

        addButton(new Button("View Other Data Set",HarvesterUI.ICONS.search_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                Dispatcher.forwardEvent(AppEvents.CompareDataSets);
            }
        }));

        addButton(new Button(HarvesterUI.CONSTANTS.cancel(),HarvesterUI.ICONS.cancel_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                Dispatcher.forwardEvent(AppEvents.HideDataSourceForm);
            }
        }));

        setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(this);
        binding.addButton(saveButton);
    }

    private void submitParentForm(){
        super.submit();
    }

    public void saveData(){
        String idPolicy;
        if(idPolicyCombo.getValue().get("value").equals("ID Generated"))
            idPolicy = IdGeneratedRecordIdPolicy.IDGENERATED;
        else
            idPolicy = IdExtractedRecordIdPolicy.IDEXTRACTED;
        String idXPath = idXPathField.getValue();
        String harvestMethod = "";
        if(harvestMethodCombo.getValue().get("value").equals("ID Sequence"))
            harvestMethod = "IdSequenceHarvester";
        else if(harvestMethodCombo.getValue().get("value").equals("File (one Id per line)"))
            harvestMethod = "IdListHarvester";
        else
            harvestMethod = "TimestampHarvester";
        String charEnc = characterEncoding.getValue().get("value");
        String recordSyntax = recordSyntaxCombo.getValue().get("value");
        String userId = user.getValue();
        String pwd = password.getValue();
        String fileUploadPath = fileUploadField.getValue();
        String maxId = null;
        if(maximumID.getValue() != null)
            maxId = String.valueOf(maximumID.getValue().longValue());
        Date earlyDate = earliestDate.getValue();
        String addr = address.getValue();
        String prt = port.getValue();
        String db = database.getValue();

        String desc = description.getValue();
        if(dataSourceUI == null) {
            dataSourceUI = new DataSourceUI(parent,desc.trim(), "", "", "Z3950 MarcXchange",parent.getCountryCode().trim(),desc.trim(),
                    "", "", "","", idPolicy.trim(), "");
        }

        dataSourceUI.setIngest("Z3950 MarcXchange");

        if(idXPath != null){
            dataSourceUI.setIdXPath(idXPath.trim());
        }
        if(fileUploadPath != null){
            dataSourceUI.setZ39IdListFile(fileUploadPath.trim());
        }
        if(maxId != null){
            dataSourceUI.setZ39MaximumId(maxId.trim());
        }
        if(userId != null){
            dataSourceUI.setZ39User(userId.trim());
        }
        if(pwd != null){
            dataSourceUI.setZ39Password(pwd.trim());
        }
        dataSourceUI.setCharacterEncoding(charEnc.trim());
        dataSourceUI.setRecordIdPolicy(idPolicy.trim());
        dataSourceUI.setZ39HarvestMethod(harvestMethod.trim());
        dataSourceUI.setZ39RecordSyntax(recordSyntax.trim());
        if(earlyDate != null){
            dataSourceUI.setZ39EarliestDate(earlyDate);
        }
        dataSourceUI.setZ39Address(addr.trim());
        dataSourceUI.setZ39Port(prt.trim());
        dataSourceUI.setZ39Database(db.trim());

        List<String> namespaces = namespacePanelExtension.getFinalNamespacesList();

        dataSourceUI.setNamespaceList(namespaces);

        dataSourceUI.setExportDirectory(exportPath.getValue() != null ? exportPath.getValue().trim() : "");

        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT)
            saveDataSource(dataSourceUI,oldDataSetId, DatasetType.Z39,"info:lc/xmlns/marcxchange-v1.xsd","info:lc/xmlns/marcxchange-v1",
                    "MarcXchange",name.getValue(),nameCode.getValue(),exportPath.getValue());
        else
            saveDataSource(dataSourceUI,oldDataSetId, DatasetType.Z39,"info:lc/xmlns/marcxchange-v1.xsd","info:lc/xmlns/marcxchange-v1",
                    "MarcXchange","","","");
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setScrollMode(Style.Scroll.AUTO);
    }

    private void createDataSet() {
        dataSet = new FieldSet();
        dataSet.setHeading(HarvesterUI.CONSTANTS.dataSet());
        dataSet.setAutoWidth(true);
        dataSet.setAutoHeight(true);
        dataSet.setLayout(new EditableFormLayout(DEFAULT_LABEL_WIDTH));

        address = new TextField<String>();
        address.setFieldLabel(HarvesterUI.CONSTANTS.address()+ HarvesterUI.REQUIRED_STR);
        address.setAllowBlank(false);
        address.setId("adressField");
        dataSet.add(address, formData);

        port = new TextField<String>();
        port.setFieldLabel(HarvesterUI.CONSTANTS.port()+ HarvesterUI.REQUIRED_STR);
        port.setId("portField");
        port.setAllowBlank(false);
        dataSet.add(port, formData);

        database = new TextField<String>();
        database.setFieldLabel(HarvesterUI.CONSTANTS.database()+ HarvesterUI.REQUIRED_STR);
        database.setId("databaseField");
        database.setAllowBlank(false);
        dataSet.add(database, formData);

        user = new TextField<String>();
        user.setFieldLabel(HarvesterUI.CONSTANTS.user());
        user.setId("userField");
        dataSet.add(user, formData);

        password = new TextField<String>();
        password.setFieldLabel(HarvesterUI.CONSTANTS.password());
        password.setId("passField");
        password.setPassword(true);
        dataSet.add(password, formData);

        dataSet.add(createCharacterEncodingBox(), smallFixedFormData);

        // test
        dataSet.add(idXPathField,formData);
        idXPathField.setAllowBlank(true);
        idXPathField.hide();

        /*
         * Record Syntax ComboBox
        */

        final ListStore<ModelData> recordSyntaxStore = new ListStore<ModelData>();
        recordSyntaxStore.add(new Attribute("country","unimarc"));
        recordSyntaxStore.add(new Attribute("country","usmarc"));

        recordSyntaxCombo = new ComboBox<ModelData>();
        recordSyntaxCombo.setFieldLabel(HarvesterUI.CONSTANTS.recordSyntax()+ HarvesterUI.REQUIRED_STR);
        recordSyntaxCombo.setEditable(false);
        recordSyntaxCombo.setDisplayField("value");
        recordSyntaxCombo.setValue(recordSyntaxStore.getModels().get(0));
        recordSyntaxCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        recordSyntaxCombo.setStore(recordSyntaxStore);
        dataSet.add(recordSyntaxCombo,smallFixedFormData);

        /*
         * Harvest Method ComboBox
        */

        final ListStore<ModelData> harvestMethodStore = new ListStore<ModelData>();
        harvestMethodStore.add(new Attribute("country", "Timestamp"));
        harvestMethodStore.add(new Attribute("country", "File (one Id per line)"));
        harvestMethodStore.add(new Attribute("country", "ID Sequence"));

        harvestMethodCombo = new ComboBox<ModelData>();
        harvestMethodCombo.setFieldLabel(HarvesterUI.CONSTANTS.harvestMethod()+ HarvesterUI.REQUIRED_STR);
        harvestMethodCombo.setDisplayField("value");
        harvestMethodCombo.setId("harvestMethodCombo");
        harvestMethodCombo.setValue(harvestMethodStore.getModels().get(0));
        harvestMethodCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        harvestMethodCombo.setStore(harvestMethodStore);
        harvestMethodCombo.setEditable(false);
        harvestMethodCombo.setValue(recordSyntaxStore.getModels().get(0));

        maximumID = new NumberField();
        maximumID.setAllowBlank(false);
        maximumID.setId("maximumIDField");
        maximumID.setFieldLabel(HarvesterUI.CONSTANTS.maximumID()+ HarvesterUI.REQUIRED_STR);

        fileUploadField = new FileUploadField();
        fileUploadField.setAllowBlank(false);
        fileUploadField.setFieldLabel(HarvesterUI.CONSTANTS.fileOnePerID()+ HarvesterUI.REQUIRED_STR);
        fileUploadField.setName("upload");
        fileUploadField.setId("upload");

        super.addListener(Events.Submit, new Listener<ComponentEvent>() {
            public void handleEvent(ComponentEvent be) {
                saveData();
            }
        });

        harvestMethodCombo.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> se) {
                if(se.getSelectedItem().get("value").equals("Timestamp")) {
                    dataSet.add(earliestDate,smallFixedFormData);
                    dataSet.insert(earliestDate, dataSet.getItems().indexOf(dataSet.getItemByItemId("harvestMethodCombo")) + 1);
                } else {
                    if(dataSet.getItems().contains(earliestDate))
                        dataSet.remove(earliestDate);
                }
                if(se.getSelectedItem().get("value").equals("File (one Id per line)")) {
//                    dataSet.add(fileUploadField,smallFixedFormData);
                    dataSet.insert(fileUploadField, dataSet.getItems().indexOf(dataSet.getItemByItemId("harvestMethodCombo")) + 1);
                } else {
                    if(dataSet.getItems().contains(fileUploadField))
                        dataSet.remove(fileUploadField);
                }
                if(se.getSelectedItem().get("value").equals("ID Sequence")) {
                    dataSet.add(maximumID,smallFixedFormData);
                    dataSet.insert(maximumID, dataSet.getItems().indexOf(dataSet.getItemByItemId("harvestMethodCombo")) + 1);

                } else {
                    if(dataSet.getItems().contains(maximumID))
                        dataSet.remove(maximumID);
                }
                layout();
            }
        });
        dataSet.add(harvestMethodCombo,smallFixedFormData);

        earliestDate = new DateField();
        earliestDate.setFieldLabel(HarvesterUI.CONSTANTS.earliestDate()+ HarvesterUI.REQUIRED_STR);
        earliestDate.setPropertyEditor(new DateTimePropertyEditor("dd/MM/yyyy"));
        earliestDate.setValidateOnBlur(true);
        earliestDate.setEditable(false);
        earliestDate.setAllowBlank(false);

        dataSet.add(idPolicyCombo,smallFixedFormData);

        exportPath = new TextField<String>();
        exportPath.setFieldLabel(HarvesterUI.CONSTANTS.exportPath());
        exportPath.setId("exportPathField");
        exportPath.setValue(HarvesterUI.getMainConfigurationData().getDefaultExportFolder() + "/");
        dataSet.add(exportPath, formData);

        addIsSampleCheckBox(dataSet);
    }

    private void createIDPolicyBoxes(){
        final ListStore<ModelData> idPolicyStore = new ListStore<ModelData>();
        idPolicyStore.add(new Attribute("country","ID Generated"));
        idPolicyStore.add(new Attribute("country","ID Extracted"));

        idPolicyCombo = new ComboBox<ModelData>();
        idPolicyCombo.setFieldLabel(HarvesterUI.CONSTANTS.idPolicy()+ HarvesterUI.REQUIRED_STR);
        idPolicyCombo.setId("idPolicy");
        idPolicyCombo.setDisplayField("value");
        idPolicyCombo.setValue(idPolicyStore.getModels().get(0));
        idPolicyCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        idPolicyCombo.setStore(idPolicyStore);
        idPolicyCombo.setEditable(false);

        idXPathField = new TextField<String>();
        idXPathField.setAllowBlank(false);
        idXPathField.setId("idXpathField");
        idXPathField.setFieldLabel(HarvesterUI.CONSTANTS.identifierXpath()+ HarvesterUI.REQUIRED_STR);

        createNamespaceSet();

        idPolicyCombo.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> se) {
                if(se.getSelectedItem().get("value").equals("ID Extracted")){
                    dataSet.insert(idXPathField, dataSet.getItems().indexOf(dataSet.getItemByItemId("idPolicy")) + 1);
                    idXPathField.show();
                    idXPathField.setAllowBlank(false);
                    dataSet.add(namespaces,new FormData(MEDIUM_FORM_DATA));
                    dataSet.insert(namespaces, dataSet.getItems().indexOf(dataSet.getItemByItemId("idPolicy"))+2);
                    layout();
                }else{
                    if(dataSet.getItems().contains(idXPathField)){
                        dataSet.remove(idXPathField);
                        dataSet.remove(namespaces);
                    }
                }
            }
        });
    }

    private ComboBox<ModelData> createCharacterEncodingBox() {
        final ListStore<ModelData> characterEncStore = new ListStore<ModelData>();
        AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<String> charSets) {
                for(String charSet : charSets)
                    characterEncStore.add(new Attribute("country",charSet));
                characterEncoding.setValue(characterEncStore.getModels().get(0));
            }
        };
        repoxService.getFullCharacterEncodingList(callback);

        characterEncoding = new ComboBox<ModelData>();
        characterEncoding.setFieldLabel(HarvesterUI.CONSTANTS.characterEncoding()+ HarvesterUI.REQUIRED_STR);
        characterEncoding.setDisplayField("value");
        characterEncoding.setTriggerAction(ComboBox.TriggerAction.ALL);
        characterEncoding.setStore(characterEncStore);
        characterEncoding.setEditable(false);
        return characterEncoding;
    }

    public void setEditMode(DataSourceUI ds) {
        editIsSampleCheckBox(ds.isSample());
        setScrollMode(Style.Scroll.AUTO);
        dataSourceUI = ds;
        edit = true;
        oldDataSetId = ds.getDataSourceSet();
        editTagsContainer(dataSourceUI);
        setEditTransformationCombo(dataSourceUI);
        dataSourceServicesPanel.setEditServices(dataSourceUI);

        harvestMethodCombo.getStore().clearFilters();
        idPolicyCombo.getStore().clearFilters();
        recordSyntaxCombo.getStore().clearFilters();
        characterEncoding.getStore().clearFilters();

        String idPolicy = dataSourceUI.getRecordIdPolicy();
        if(idPolicy.equals(IdGeneratedRecordIdPolicy.IDGENERATED)) {
            idPolicyCombo.setValue(idPolicyCombo.getStore().getAt(0));
        }
        else if(idPolicy.equals(IdExtractedRecordIdPolicy.IDEXTRACTED)) {
            idPolicyCombo.setValue(idPolicyCombo.getStore().getAt(1));
            idXPathField.setValue(dataSourceUI.getIdXPath());
            setEditNamespaces(dataSourceUI);
        }

        for(ModelData comboSel: recordSyntaxCombo.getStore().getModels()) {
            if(comboSel.get("value").equals(dataSourceUI.getZ39RecordSyntax()))
                recordSyntaxCombo.setValue(comboSel);
        }

        for(ModelData comboSel: characterEncoding.getStore().getModels()) {
            if(comboSel.get("value").equals(dataSourceUI.getCharacterEncoding()))
                characterEncoding.setValue(comboSel);
        }

        if(dataSourceUI.getZ39HarvestMethod() == null){
            harvestMethodCombo.setValue(harvestMethodCombo.getStore().getAt(2));
            maximumID.clear();
        } else if(dataSourceUI.getZ39HarvestMethod().equals("IdSequenceHarvester")) {
            harvestMethodCombo.setValue(harvestMethodCombo.getStore().getAt(2));
            if(!dataSourceUI.getZ39MaximumId().equals("null") && !dataSourceUI.getZ39MaximumId().isEmpty() && dataSourceUI.getZ39MaximumId() != null)
                maximumID.setValue(Long.valueOf(dataSourceUI.getZ39MaximumId()));
        } else if(dataSourceUI.getZ39HarvestMethod().equals("IdListHarvester")) {
            harvestMethodCombo.setValue(harvestMethodCombo.getStore().getAt(1));
            fileUploadField.setValue(dataSourceUI.getZ39IdListFile());
        } else if(dataSourceUI.getZ39HarvestMethod().equals("TimestampHarvester")) {
            harvestMethodCombo.setValue(harvestMethodCombo.getStore().getAt(0));
            earliestDate.setValue(dataSourceUI.getZ39EarlistDate());
        }

        address.setValue(dataSourceUI.getZ39Address());
        recordSet.setValue(dataSourceUI.getDataSourceSet());
        description.setValue(dataSourceUI.getDescription());
        address.setValue(dataSourceUI.getZ39Address());
        port.setValue(dataSourceUI.getZ39Port());
        database.setValue(dataSourceUI.getZ39Database());
        user.setValue(dataSourceUI.getZ39User());
        password.setValue(dataSourceUI.getZ39Password());
        exportPath.setValue(dataSourceUI.getExportDirectory());

        // Europeana Fields
        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT) {
            name.setValue(dataSourceUI.getName());
            nameCode.setValue(dataSourceUI.getNameCode());
        }
    }

    public void resetValues(DataProviderUI parent) {
        edit = false;
        oldDataSetId = "";
        idXPathField.clear();
        user.clear();
        password.clear();
        address.clear();
        port.clear();
        user.clear();
        password.clear();
        database.clear();
        maximumID.clear();
        earliestDate.clear();
//        exportPath.clear();
        exportPath.setValue(HarvesterUI.getMainConfigurationData().getDefaultExportFolder() + "/");
        if(fileUploadField.isRendered())
            fileUploadField.clear();

        setResetNamespaces();
        setResetOutputSet(parent);
        dataSourceServicesPanel.resetValues();

        harvestMethodCombo.getStore().clearFilters();
        idPolicyCombo.getStore().clearFilters();
        recordSyntaxCombo.getStore().clearFilters();
        characterEncoding.getStore().clearFilters();

        idPolicyCombo.setValue(idPolicyCombo.getStore().getModels().get(0));
        recordSyntaxCombo.setValue(recordSyntaxCombo.getStore().getModels().get(0));
        harvestMethodCombo.setValue(harvestMethodCombo.getStore().getModels().get(0));
        if(characterEncoding.getStore().getModels().size() > 0)
            characterEncoding.setValue(characterEncoding.getStore().getModels().get(0));

        // Europeana Fields
        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT) {
            name.clear();
            nameCode.clear();
        }
    }

    public void addEuropeanaFields() {
        name = new TextField<String>();
        name.setFieldLabel(HarvesterUI.CONSTANTS.name());
        name.setId("nameField");
        dataSet.add(name, formData);

        nameCode = new TextField<String>();
        nameCode.setFieldLabel(HarvesterUI.CONSTANTS.nameCode());
        nameCode.setId("nameCodeField");
        dataSet.add(nameCode, formData);
    }

    public String getMetadataFormat() {
        return null;
    }

    public String getFolderPath() {
        return null;
    }

    public String getSchema() {
        return "";
    }
}
