package harvesterUI.client.panels.forms.dataSources;

import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.models.Attribute;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.DatasetType;

import java.util.List;

import pt.utl.ist.dataProvider.dataSource.IdExtractedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.IdGeneratedRecordIdPolicy;
import pt.utl.ist.ftp.FtpFileRetrieveStrategy;
import pt.utl.ist.http.HttpFileRetrieveStrategy;
import pt.utl.ist.marc.FolderFileRetrieveStrategy;
import pt.utl.ist.marc.iso2709.shared.Iso2709Variant;
import pt.utl.ist.util.shared.ProjectType;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-03-2011
 * Time: 14:56
 */
public class DataSourceFolderForm extends DataSourceForm {

    private FormData formData;
    private ComboBox<ModelData> isoVariant;
    private ComboBox<ModelData> characterEncoding;
    private FieldSet dataSet;
    private String oldDataSetId = "";

    // Fields
    private ComboBox<ModelData> idPolicyCombo, retrieveVariantCombo, authenticationCombo;
    private TextField<String> rootName, idXPathField,
            server, folderFtp, httpUrl, user, password, folderPath;

    // Europeana Fields
    private TextField<String> name, nameCode, exportPath;

    public DataSourceFolderForm(FormData data) {
        super(data);
        formData = data;
        dataSourceSchemaForm = new DataSourceSchemaForm();
        setHeaderVisible(false);
        setBodyBorder(false);
        setLayout(new FitLayout());
        setLayoutOnChange(true);

        initOptionalFields();
        createIDPolicyBoxes();
        createRetrieveVariant();

        createISOBoxes();
        createFolderForm();

        dataSet.add(exportPath, formData);

        addIsSampleCheckBox(dataSet);

        add(createOutputSet());
        add(dataSourceServicesPanel);
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setScrollMode(Style.Scroll.AUTO);
    }

    private void createFolderForm() {
        dataSet = new FieldSet();
        dataSet.setAutoHeight(true);
        dataSet.setAutoWidth(true);
        dataSet.setHeading(HarvesterUI.CONSTANTS.dataSet());

        dataSet.setLayout(new EditableFormLayout(DEFAULT_LABEL_WIDTH));

        // test
        dataSet.add(idXPathField,formData);
        idXPathField.hide();
        idXPathField.setAllowBlank(true);
        dataSet.add(folderPath, formData);
        folderPath.hide();
        dataSet.add(httpUrl, formData);
        httpUrl.hide();
        dataSet.add(server, formData);
        server.hide();

        /*
         * Metadata Format ComboBox
        */

        rootName = new TextField<String>();
        rootName.setFieldLabel(HarvesterUI.CONSTANTS.recordRootName());
        rootName.setId("rootNameField");

        dataSourceSchemaForm.addSchemaFolderFormPart(dataSet,smallFixedFormData,formData,isoVariant,characterEncoding,rootName);

        dataSet.add(rootName, formData);

        /*
         *ID Policy ComboBox
        */
        dataSet.add(idPolicyCombo,smallFixedFormData);

        dataSet.add(retrieveVariantCombo,smallFixedFormData);
        // Add default folder value fields
        addFolderFields();

        Button saveButton = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                saveData();
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

        add(dataSet);
    }

    private void createISOBoxes(){
        final ListStore<ModelData> isoVariantStore = new ListStore<ModelData>();
        isoVariantStore.add(new Attribute("country","Standard"));
        isoVariantStore.add(new Attribute("country","Variant From Albania"));
        isoVariantStore.add(new Attribute("country","Variant From Ukraine"));

        isoVariant = new ComboBox<ModelData>();
        isoVariant.setFieldLabel(HarvesterUI.CONSTANTS.iso2709Variant()+ HarvesterUI.REQUIRED_STR);
        isoVariant.setDisplayField("value");
        isoVariant.setValue(isoVariantStore.getModels().get(0));
        isoVariant.setTriggerAction(ComboBox.TriggerAction.ALL);
        isoVariant.setStore(isoVariantStore);
        isoVariant.setEditable(false);

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

        exportPath = new TextField<String>();
        exportPath.setFieldLabel(HarvesterUI.CONSTANTS.exportPath());
        exportPath.setId("exportPathField");
        exportPath.setValue(HarvesterUI.getMainConfigurationData().getDefaultExportFolder() + "/");
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
//        idPolicyCombo.setForceSelection(true);
        idPolicyCombo.setEditable(false);

        idXPathField = new TextField<String>();
        idXPathField.setAllowBlank(false);
        idXPathField.setId("idXpathField");
        idXPathField.setFieldLabel(HarvesterUI.CONSTANTS.identifierXpath()+ HarvesterUI.REQUIRED_STR);

//        namespaces = new FieldSet();
//        namespaces.setHeading("Namespaces");
//        namespaces.setCheckboxToggle(true);
//        FormLayout layout = new FormLayout();
//        layout.setLabelWidth(150);
//        namespaces.setLayout(layout);
//        namespaces.collapse();

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

    private void createRetrieveVariant() {
        final ListStore<ModelData> retrieveVariantStore = new ListStore<ModelData>();
        retrieveVariantStore.add(new Attribute("country","File System"));
        retrieveVariantStore.add(new Attribute("country","FTP"));
        retrieveVariantStore.add(new Attribute("country","HTTP"));

        retrieveVariantCombo = new ComboBox<ModelData>();
        retrieveVariantCombo.setFieldLabel(HarvesterUI.CONSTANTS.retrieveVariant()+ HarvesterUI.REQUIRED_STR);
        retrieveVariantCombo.setId("retrieveVariant");
        retrieveVariantCombo.setDisplayField("value");
        retrieveVariantCombo.setValue(retrieveVariantStore.getModels().get(0));
        retrieveVariantCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        retrieveVariantCombo.setStore(retrieveVariantStore);
        retrieveVariantCombo.setEditable(false);
        retrieveVariantCombo.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> se) {
                if(se.getSelectedItem().get("value").equals("File System"))
                    addFolderFields();
                else {
                    folderPath.hide();
                    if(dataSet.getItems().contains(user)){
                        dataSet.remove(user);
                        dataSet.remove(password);
                    }
                }
                if(se.getSelectedItem().get("value").equals("FTP")){
                    addFTPFields();
                }
                else {
                    if(dataSet.getItems().contains(authenticationCombo)){
                        server.hide();
                        dataSet.remove(authenticationCombo);
                        dataSet.remove(folderFtp);
                    }
                }
                if(se.getSelectedItem().get("value").equals("HTTP"))
                    addHTTPFields();
                else {
                    httpUrl.hide();
                    if(dataSet.getItems().contains(user)){
                        dataSet.remove(user);
                        dataSet.remove(password);
                    }
                }
                if(edit && retrieveVariantCombo.isVisible())
                    HarvesterUI.UTIL_MANAGER.askForIncrementalUpdateDate(dataSourceUI);
            }
        });
    }

    private void addFolderFields() {
        dataSet.add(folderPath,new FormData(MEDIUM_FORM_DATA));
        dataSet.insert(folderPath, dataSet.getItems().indexOf(dataSet.getItemByItemId("retrieveVariant")) + 1);
        folderPath.show();
        folderPath.setAllowBlank(false);
        server.setAllowBlank(true);
        httpUrl.setAllowBlank(true);
        layout();
    }

    private void addHTTPFields() {
        dataSet.add(httpUrl,new FormData(MEDIUM_FORM_DATA));
        dataSet.insert(httpUrl, dataSet.getItems().indexOf(dataSet.getItemByItemId("retrieveVariant")) + 1);
        httpUrl.show();
        folderPath.setAllowBlank(true);
        server.setAllowBlank(true);
        httpUrl.setAllowBlank(false);
        layout();
    }

    private void addFTPFields() {
        dataSet.add(server,new FormData(MEDIUM_FORM_DATA));
        dataSet.add(authenticationCombo,smallFixedFormData);
        dataSet.add(folderFtp,new FormData(MEDIUM_FORM_DATA));
        dataSet.insert(server, dataSet.getItems().indexOf(dataSet.getItemByItemId("retrieveVariant")) + 1);
        dataSet.insert(authenticationCombo, dataSet.getItems().indexOf(dataSet.getItemByItemId("retrieveVariant")) + 2);
        dataSet.insert(folderFtp, dataSet.getItems().indexOf(dataSet.getItemByItemId("retrieveVariant")) + 3);
        server.show();
        folderPath.setAllowBlank(true);
        server.setAllowBlank(false);
        httpUrl.setAllowBlank(true);
        layout();
        authenticationCombo.setValue(authenticationCombo.getStore().getModels().get(0));
    }

    private void initOptionalFields() {
        server = new TextField<String>();
        server.setAllowBlank(false);
        server.setId("serverFieldID");
        server.setValidationDelay(40000);
        server.setFieldLabel(HarvesterUI.CONSTANTS.server()+ HarvesterUI.REQUIRED_STR);

        user = new TextField<String>();
        user.setAllowBlank(false);
        user.setId("telFolderUserField");
        user.setFieldLabel(HarvesterUI.CONSTANTS.user()+ HarvesterUI.REQUIRED_STR);

        password = new TextField<String>();
        password.setAllowBlank(false);
        password.setId("telFolderPass");
        password.setValidationDelay(40000);
        password.setFieldLabel(HarvesterUI.CONSTANTS.password()+ HarvesterUI.REQUIRED_STR);

        httpUrl = new TextField<String>();
        httpUrl.setAllowBlank(false);
        httpUrl.setId("httpUrltelfolder");
        httpUrl.setValidationDelay(40000);
        httpUrl.setFieldLabel(HarvesterUI.CONSTANTS.httpUrl()+ HarvesterUI.REQUIRED_STR);

        folderFtp = new TextField<String>();
        folderFtp.setAllowBlank(false);
        folderFtp.setId("folderFtptel");
        folderFtp.setValidationDelay(40000);
        folderFtp.setFieldLabel(HarvesterUI.CONSTANTS.folderFtp()+ HarvesterUI.REQUIRED_STR);

        folderPath = new TextField<String>();
        folderPath.setAllowBlank(false);
        folderPath.setId("folderPathFieldTel");
        folderPath.setValidationDelay(40000);
        folderPath.setFieldLabel(HarvesterUI.CONSTANTS.folderPath()+ HarvesterUI.REQUIRED_STR);

        final ListStore<ModelData> authenticationStore = new ListStore<ModelData>();
        authenticationStore.add(new Attribute("country","Anonymous"));
        authenticationStore.add(new Attribute("country","Normal"));

        authenticationCombo = new ComboBox<ModelData>();
        authenticationCombo.setFieldLabel(HarvesterUI.CONSTANTS.authentication()+ HarvesterUI.REQUIRED_STR);
        authenticationCombo.setId("authentication");
        authenticationCombo.setDisplayField("value");
        authenticationCombo.setValue(authenticationStore.getModels().get(0));
        authenticationCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        authenticationCombo.setStore(authenticationStore);
        authenticationCombo.setEditable(false);
        authenticationCombo.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> se) {
                if(se.getSelectedItem().get("value").equals("Normal")){
                    dataSet.add(user,smallFixedFormData);
                    dataSet.add(password,smallFixedFormData);
                    dataSet.insert(user, dataSet.getItems().indexOf(dataSet.getItemByItemId("authentication")) + 1);
                    dataSet.insert(password, dataSet.getItems().indexOf(dataSet.getItemByItemId("authentication"))+2);
                    layout();
                    user.clear();
                    password.clear();
                }
                else{
                    if(dataSet.getItems().contains(user)){
                        dataSet.remove(user);
                        dataSet.remove(password);
                    }
                }
            }
        });
    }

    public void setEditMode(DataSourceUI ds) {
        editIsSampleCheckBox(ds.isSample());
        dataSourceUI = ds;
        fillMetadataComboStore(true);
        edit = true;
        oldDataSetId = ds.getDataSourceSet();
        boolean foundMatch = false;

        idPolicyCombo.getStore().clearFilters();
        retrieveVariantCombo.getStore().clearFilters();
        authenticationCombo.getStore().clearFilters();
        characterEncoding.getStore().clearFilters();

        for(ModelData comboSel: isoVariant.getStore().getModels()) {
            if(comboSel.get("value").equals(dataSourceUI.getIsoVariant()))
                isoVariant.setValue(comboSel);
        }

        String idPolicy = dataSourceUI.getRecordIdPolicy();
        if(idPolicy.equals(IdGeneratedRecordIdPolicy.IDGENERATED)){
            idPolicyCombo.setValue(idPolicyCombo.getStore().getAt(0));
        }
        else if(idPolicy.equals(IdExtractedRecordIdPolicy.IDEXTRACTED)){
            idPolicyCombo.setValue(idPolicyCombo.getStore().getAt(1));
            idXPathField.setValue(dataSourceUI.getIdXPath());
            setEditNamespaces(dataSourceUI);
        }

        editTagsContainer(dataSourceUI);
        setEditTransformationCombo(dataSourceUI);
        dataSourceServicesPanel.setEditServices(dataSourceUI);

        for(ModelData comboSel: characterEncoding.getStore().getModels()) {
            if(comboSel.get("value").equals(dataSourceUI.getCharacterEncoding()))
                characterEncoding.setValue(comboSel);
        }

        String dsRetriveStrat = dataSourceUI.getRetrieveStartegy();
        if(dsRetriveStrat == null || dsRetriveStrat.equals(FolderFileRetrieveStrategy.FOLDERFILERETRIEVESTRATEGY)) {
            retrieveVariantCombo.setValue(retrieveVariantCombo.getStore().getAt(0));
            folderPath.setValue(dataSourceUI.getDirPath());
        }
        else if(dsRetriveStrat.equals(HttpFileRetrieveStrategy.HTTPFILERETRIEVESTRATEGY)) {
            retrieveVariantCombo.setValue(retrieveVariantCombo.getStore().getAt(2));
            httpUrl.setValue(dataSourceUI.getHttpURL().trim());
        }
        else if(dsRetriveStrat.equals(FtpFileRetrieveStrategy.FTPFILERETRIEVESTRATEGY)) {
            server.setValue(dataSourceUI.getServer());
            retrieveVariantCombo.setValue(retrieveVariantCombo.getStore().getAt(1));
            if(dataSourceUI.getUser() != null && !dataSourceUI.getUser().isEmpty() &&
                    dataSourceUI.getPassword() != null && !dataSourceUI.getPassword().isEmpty()){
                authenticationCombo.setValue(authenticationCombo.getStore().getAt(1));
                user.setValue(dataSourceUI.getUser());
                password.setValue(dataSourceUI.getPassword());
            }
            else{
                authenticationCombo.setValue(authenticationCombo.getStore().getAt(0));
            }
            folderFtp.setValue(dataSourceUI.getFolderPath());
        }

//        retrieveVariantCombo.disable();
        dataSourceSchemaForm.getSchema().setValue(dataSourceUI.getSchema());
        dataSourceSchemaForm.getMetadataNamespace().setValue(dataSourceUI.getMetadataNamespace());
        dataSourceSchemaForm.editMarcCombo(dataSourceUI.getMarcFormat());
        recordSet.setValue(dataSourceUI.getDataSourceSet());
        description.setValue(dataSourceUI.getDescription());
        rootName.setValue(dataSourceUI.getRecordRootName());
        exportPath.setValue(dataSourceUI.getExportDirectory());

        // Europeana Fields
        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT) {
            name.setValue(dataSourceUI.getName());
            nameCode.setValue(dataSourceUI.getNameCode());
        }
    }

    public void resetValues(DataProviderUI parent){
        fillMetadataComboStore(false);
        edit = false;
        oldDataSetId = "";
        idXPathField.clear();
        rootName.clear();
        httpUrl.clear();
        server.clear();
        folderFtp.clear();
        folderPath.clear();
        setResetNamespaces();
        setResetOutputSet(parent);
        dataSourceServicesPanel.resetValues();
//        exportPath.clear();
        exportPath.setValue(HarvesterUI.getMainConfigurationData().getDefaultExportFolder() + "/");

        idPolicyCombo.getStore().clearFilters();
        retrieveVariantCombo.getStore().clearFilters();
        authenticationCombo.getStore().clearFilters();
        characterEncoding.getStore().clearFilters();

        idPolicyCombo.setValue(idPolicyCombo.getStore().getModels().get(0));
        if(characterEncoding.getStore().getModels().size() > 0)
            characterEncoding.setValue(characterEncoding.getStore().getModels().get(0));
        retrieveVariantCombo.setValue(retrieveVariantCombo.getStore().getModels().get(0));
        retrieveVariantCombo.enable();
        authenticationCombo.setValue(authenticationCombo.getStore().getModels().get(0));

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
        return dataSourceSchemaForm.getMetadataFormatCombo().getValue().getShortDesignation();
    }

    public String getFolderPath() {
        if(folderPath.getValue() != null)
            return folderPath.getValue();
        else
            return null;
    }

    public String getSchema() {
        return dataSourceSchemaForm.getSchema().getValue();
    }

    private void fillMetadataComboStore(final boolean edit){
        dataSourceSchemaForm.loadMetadataFormatComboSchemas(edit, dataSourceUI);
    }

    public void saveData(){
        String metadataFormat = dataSourceSchemaForm.getMetadataFormatCombo().getValue().getShortDesignation();
        String charEnc = characterEncoding.getValue().get("value");
        String idPolicy;
        if(idPolicyCombo.getValue().get("value").equals("ID Generated"))
            idPolicy = IdGeneratedRecordIdPolicy.IDGENERATED;
        else
            idPolicy = IdExtractedRecordIdPolicy.IDEXTRACTED;
        String idXPath = idXPathField.getValue();
        String variant = retrieveVariantCombo.getValue().get("value");
        String dsRetrieveStrat = "";
        if(variant.equals("File System")){
            dsRetrieveStrat = FolderFileRetrieveStrategy.FOLDERFILERETRIEVESTRATEGY;
//            dataSourceUI.setIngest("Folder(File Sytem) " + metadataFormat.trim());
        }
        else if(variant.equals("FTP")) {
            dsRetrieveStrat = FtpFileRetrieveStrategy.FTPFILERETRIEVESTRATEGY;
//            dataSourceUI.setIngest("Folder(FTP) " + metadataFormat.trim());
        } else if(variant.equals("HTTP")) {
            dsRetrieveStrat = HttpFileRetrieveStrategy.HTTPFILERETRIEVESTRATEGY;
//            dataSourceUI.setIngest("Folder(HTTP) " + metadataFormat.trim());
        }
        String serverUrl = server.getValue();
        String userId = user.getValue();
        String pwd = password.getValue();
        String folderFtpStr = folderFtp.getValue();
        String httpURL = httpUrl.getValue();
        if (httpURL != null && !httpURL.startsWith("http://") && !httpURL.startsWith("https://")) {
            httpURL = "http://" + httpURL;
        }
        String folderPathStr = folderPath.getValue();
        String recordRootStr = rootName.getValue();
        String record_set = recordSet.getValue();
        String desc = description.getValue();

        String isoVariantStr = "";
        if(isoVariant.getValue().get("value").equals("Standard"))
            isoVariantStr = Iso2709Variant.STANDARD.getIsoVariant();
        else if(isoVariant.getValue().get("value").equals("Variant From Albania")) {
            isoVariantStr = Iso2709Variant.ALBANIA.getIsoVariant();
        } else if(isoVariant.getValue().get("value").equals("Variant From Ukraine")) {
            isoVariantStr = Iso2709Variant.UKRAINE.getIsoVariant();
        }

        if(dataSourceUI == null) {
            dataSourceUI = new DataSourceUI(parent, desc.trim(), "", metadataFormat.trim() + " | ese", "Folder " + metadataFormat.trim(),
                    parent.getCountryCode().trim(),desc.trim(), "", "", "",
                    "", idPolicy.trim(), metadataFormat.trim());
        }
        
        dataSourceUI.setIngest("Folder " + metadataFormat.trim());
        dataSourceUI.setSourceMDFormat(metadataFormat.trim());
        if(httpURL != null){
            dataSourceUI.setHttpURL(httpURL.trim());
        }
        dataSourceUI.setRecordIdPolicy(idPolicy.trim());
        dataSourceUI.setCharacterEncoding(charEnc.trim());
        dataSourceUI.setIdXPath(idXPath != null ? idXPath.trim() : "");
        dataSourceUI.setRetrieveStartegy(dsRetrieveStrat.trim());
        dataSourceUI.setServer(serverUrl != null ? serverUrl.trim() : "");
        dataSourceUI.setUser(userId != null ? userId.trim() : "");
        dataSourceUI.setPassword(pwd != null ? pwd.trim(): "");
        dataSourceUI.setFolderPath(folderFtpStr != null ? folderFtpStr.trim() : "");
        if(dataSourceUI.getFolderPath() != null && (dataSourceUI.getFolderPath().startsWith("\\")
                || dataSourceUI.getFolderPath().startsWith("/"))){
            dataSourceUI.setFolderPath(dataSourceUI.getFolderPath().substring(1));
        }
        dataSourceUI.setDirPath(folderPathStr != null ? folderPathStr.trim() : null);
        dataSourceUI.setRecordRootName(recordRootStr != null ? recordRootStr.trim() : "");
        dataSourceUI.setDataSourceSet(record_set.trim());
        dataSourceUI.setIsoVariant(isoVariantStr.trim());

        List<String> namespaces = namespacePanelExtension.getFinalNamespacesList();

        dataSourceUI.setNamespaceList(namespaces);

        dataSourceUI.setExportDirectory(exportPath.getValue() != null ? exportPath.getValue().trim() : "");

        dataSourceUI.setMarcFormat(dataSourceSchemaForm.getMarcFormat().trim());

        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT)
            saveDataSource(dataSourceUI,oldDataSetId, DatasetType.FOLDER,dataSourceSchemaForm.getSchema().getValue(),dataSourceSchemaForm.getMetadataNamespace().getValue(),
                    metadataFormat,name.getValue(),nameCode.getValue(),exportPath.getValue());
//        else
//            saveDataSource(dataSourceUI,oldDataSetId,DatasetType.FOLDER,dataSourceSchemaForm.getSchema().getValue(),dataSourceSchemaForm.getMetadataNamespace().getValue(),
//                    metadataFormat,"","","");
    }
}

