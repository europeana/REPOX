package harvesterUI.client.panels.forms.dataSources;

import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.services.DataSetListParameter;
import harvesterUI.client.servlets.RepoxServiceAsync;
import harvesterUI.client.servlets.dataManagement.DataSetOperationsServiceAsync;
import harvesterUI.client.servlets.transformations.TransformationsServiceAsync;
import harvesterUI.client.util.FieldSetWithClickOption;
import harvesterUI.client.util.FieldSetWithExternalService;
import harvesterUI.client.util.NamespacePanelExtension;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.client.util.paging.PageUtil;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.SaveDataResponse;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.DatasetType;
import harvesterUI.shared.externalServices.ExternalServiceUI;
import harvesterUI.shared.externalServices.ServiceParameterUI;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pt.utl.ist.util.shared.ProjectType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 18-03-2011
 * Time: 17:06
 */
public abstract class DataSourceForm extends DefaultFormPanel {

    private FormData formData;
    protected boolean edit = false;
    protected DataSourceUI dataSourceUI;
    protected DataProviderUI parent;

    protected DataSourceSchemaForm dataSourceSchemaForm;

    protected FieldSet dataSet,outputSet;
    protected FieldSetWithClickOption namespaces;
    protected TextField<String> recordSet, description;
    protected NamespacePanelExtension namespacePanelExtension;
    private TransformationsContainer transformationsContainer;

    protected DataSetOperationsServiceAsync dataSetOperationsService;
    protected RepoxServiceAsync repoxService;
    protected TransformationsServiceAsync transformationsService;
    protected DataSourceServicesPanel dataSourceServicesPanel;
    protected DataSourceTagContainer dataSourceTagContainer;

    protected String MEDIUM_FORM_DATA = "80%";
    protected FormData smallFixedFormData;
    protected int DEFAULT_LABEL_WIDTH = 190;
    public static int SPECIAL_FIELDS_LABEL_WIDTH = 194;

    // Eudml Only
    private CheckBox storeInYadda, isSample;

    public DataSourceForm(FormData data) {
        formData = data;

        setLayoutOnChange(true);

        smallFixedFormData = new FormData(250,22);

        dataSourceServicesPanel = new DataSourceServicesPanel(formData);

        dataSetOperationsService = (DataSetOperationsServiceAsync) Registry.get(HarvesterUI.DATA_SET_OPERATIONS_SERVICE);
        repoxService = (RepoxServiceAsync) Registry.get(HarvesterUI.REPOX_SERVICE);
        transformationsService = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);

        namespaces = new FieldSetWithClickOption();
        namespaces.setHeading(HarvesterUI.CONSTANTS.namespaces());
        namespaces.setCheckboxToggle(true);

        namespacePanelExtension = new NamespacePanelExtension(namespaces,formData);

        outputSet = new FieldSet();
        outputSet.setHeading(HarvesterUI.CONSTANTS.output());
        outputSet.setLayout(new EditableFormLayout(DEFAULT_LABEL_WIDTH));
    }

    /**
     * Create the Namespace Field set for Folder and Z39 Id extraction form
     */
    public FieldSetWithClickOption createNamespaceSet(){
        namespaces.setLayout(new EditableFormLayout(150));
        namespacePanelExtension.createNewNamespace();
        return namespaces;
    }

    public void setEditNamespaces(DataSourceUI treeDataSourceUI){
        List<String> namespaceList = treeDataSourceUI.getNamespaceList();
        int namespaceListSize = namespaceList.size();

        setResetNamespaces();

        if(namespaceListSize > 0){
            namespaces.expand();
            namespacePanelExtension.editNamespaces(namespaceList);
        }
    }

    public void setResetNamespaces(){
        namespacePanelExtension.clearNamespacesList(1);
    }

    /**
     * Create the Output Field set for each data source Form
     */
    public FieldSet createOutputSet() {
        recordSet = new TextField<String>();
        recordSet.setFieldLabel(HarvesterUI.CONSTANTS.recordSet()+ HarvesterUI.REQUIRED_STR);
        recordSet.setId("rcrdSet_europ");
        recordSet.setAllowBlank(false);
        KeyListener keyListener = new KeyListener() {
            public void componentKeyUp(ComponentEvent event) {
                updateExternalServicesDataSetService(recordSet.getValue());
            }
        };
        recordSet.addKeyListener(keyListener);
        outputSet.add(recordSet,formData);

        description = new TextField<String>();
        description.setFieldLabel(HarvesterUI.CONSTANTS.description() + HarvesterUI.REQUIRED_STR);
        description.setId("desc_europ");
        description.setAllowBlank(false);
        outputSet.add(description, formData);

        dataSourceTagContainer = new DataSourceTagContainer();
        outputSet.add(dataSourceTagContainer, formData);

        transformationsContainer = new TransformationsContainer(dataSourceSchemaForm);
        outputSet.add(transformationsContainer, formData);

        return outputSet;
    }

    private void updateExternalServicesDataSetService(String dataSetId){
        if(dataSourceServicesPanel != null && dataSourceServicesPanel.isExpanded()){
            for(Component fieldSetC : dataSourceServicesPanel.getItems()){
                FieldSetWithExternalService fieldSet = (FieldSetWithExternalService)fieldSetC;
                for(Component component : fieldSet.getItems()){
                    for(ServiceParameterUI serviceParameterUI: fieldSet.getExternalServiceUI().getServiceParameters()){
                        if(component.getId().equals(serviceParameterUI.getId())){
                            if(component instanceof TextField && serviceParameterUI.getSemantics().equals("DATA_SET_ID")){
                                ((TextField) component).setValue(dataSetId);
                            }
                        }
                    }
                }
            }
        }
    }

    public void setEditTransformationCombo(DataSourceUI treeDataSourceUI) {
        // Save the date this data source was edited
        treeDataSourceUI.setUsed(new Date());

        setEditTransformationComboWithLoad(treeDataSourceUI);
    }

    public void setResetOutputSet(DataProviderUI par){
        parent = par;
        dataSourceUI = null;
        recordSet.clear();
        description.clear();

        dataSourceTagContainer.reset();
        resetTransCombo();
        repaint();
    }

    public void reloadTransformations(){
//        AsyncCallback<List<TransformationUI>> callback = new AsyncCallback<List<TransformationUI>>() {
//            public void onFailure(Throwable caught) {
//                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
//            }
//            public void onSuccess(List<TransformationUI> mdTrans) {
////                transformationsStore.removeAll();
////                transformationsStore.add(new TransformationUI("-", "", "", "", "","","",false));
////                transformationsStore.add(mdTrans);
////                firstTransformationsCombo.setValue(firstTransformationsCombo.getStore().getAt(0));
//            }
//        };
//        transformationsService.getFullTransformationsList(callback);
        if(transformationsContainer.getLastMappingsDialog() != null)
            transformationsContainer.getLastMappingsDialog().loadData();
    }

    public void reloadSchemas(String schemaShortDesignation){
        if(dataSourceSchemaForm != null){
            dataSourceSchemaForm.reloadMetadataSchemas(schemaShortDesignation);

            if(this instanceof DataSourceOAIForm){
                dataSourceSchemaForm.addMetadataFormatContainer(dataSourceSchemaForm.getMetadataFormatCombo(),3,false);
                dataSourceSchemaForm.removeNoSchemaFoundWarning();
            }
        }
    }

    public void reloadTags(){
        if(dataSourceTagContainer.getDataSourceTagsDialog() != null)
            dataSourceTagContainer.getDataSourceTagsDialog().loadData();
    }

    private void resetTransCombo(){
        transformationsContainer.getTransformationsGrid().getStore().removeAll();
    }

    private void setEditTransformationComboWithLoad(DataSourceUI dataSourceUI) {
        resetTransCombo();
        List<TransformationUI> dsMetadataTransformations = dataSourceUI.getMetadataTransformations();
        transformationsContainer.getTransformationsGrid().getStore().add(dsMetadataTransformations);
    }

    protected void editTagsContainer(DataSourceUI dataSourceUI) {
        dataSourceTagContainer.loadEditData(dataSourceUI);
    }

    public void saveDataSource(final DataSourceUI dataSourceUI, final String oldDataSetId, DatasetType type, String schema,
                               String metadataNamespace,String metadataFormat,String name,String nameCode, String exportPath) {
        mask(HarvesterUI.CONSTANTS.saveDataSetMask());
        AsyncCallback<SaveDataResponse> callback = new AsyncCallback<SaveDataResponse>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(SaveDataResponse response) {
                unmask();
                ResponseState responseState = response.getResponseState();
                if(responseState == ResponseState.INVALID_ARGUMENTS) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.invalidArguments());
                    dataSourceUI.setDataSourceSet(oldDataSetId);
                    return;
                } else if(responseState == ResponseState.URL_MALFORMED) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.oaiUrlMalformed());
                    return;
                } else if(responseState == ResponseState.URL_NOT_EXISTS) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.oaiUrlNotExists());
                    return;
                } else if(responseState == ResponseState.HTTP_URL_MALFORMED) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.httpUrlMalformed());
                    return;
                } else if(responseState == ResponseState.HTTP_URL_NOT_EXISTS) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.httpUrlNotExists());
                    return;
                }else if(responseState == ResponseState.NOT_FOUND){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.dataSetNotFound());
                    return;
                } else if(responseState == ResponseState.INCOMPATIBLE_TYPE){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.incompatibleType());
                    return;
                } else if(responseState == ResponseState.ERROR_DATABASE){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.errorAccessDatabase());
                    return;
                } else if(responseState == ResponseState.ALREADY_EXISTS){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.dataSetAlreadyExists());
                    dataSourceUI.setDataSourceSet(oldDataSetId);
                    return;
                } else if(responseState == ResponseState.FTP_CONNECTION_FAILED){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.ftpConnectionFailed());
                    return;
                } else if(responseState == ResponseState.OTHER){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.errorSaveDataSet());
                    return;
                }

                submit();
                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.saveDataSetSuccess());

                // Special case for data source set change
                if(History.getToken().contains("VIEW_DS"))
                    Dispatcher.forwardEvent(AppEvents.ViewDataSetInfo,dataSourceUI);
                else
                    PageUtil.reloadMainData(response.getPage());

                Dispatcher.forwardEvent(AppEvents.HideDataSourceForm);
            }
        };

        // Add General attributes
        String record_set = recordSet.getValue();
        String desc = description.getValue();

        dataSourceUI.setName(desc.trim());
        dataSourceUI.setDescription(desc != null ? desc.trim() : "");
        dataSourceUI.setDataSourceSet(record_set != null ? record_set.trim() : "");

        dataSourceUI.setSchema(schema.trim());
        dataSourceUI.setMetadataNamespace(metadataNamespace.trim());

        String oaiSchemas = metadataFormat.trim();
        List<TransformationUI> transformations = transformationsContainer.getTransformationsGrid().getStore().getModels();
        for(TransformationUI transformationUI : transformations){
            oaiSchemas += " | " + transformationUI.getDestFormat();
        }
        dataSourceUI.setMetadataFormat(oaiSchemas != null ? oaiSchemas.trim() : "");
        dataSourceUI.setMetadataTransformations(transformations);
        dataSourceUI.setTags(dataSourceTagContainer.getTags());

        // Fields
        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT) {
//            String exprtP;
//            if(exportPath == null) {
//                exprtP = HarvesterUI.getMainConfigurationData().getDefaultExportFolder() + "/" + record_set;
//            } else {
//                exprtP= exportPath;
//            }
            dataSourceUI.setName(name != null ? name.trim() : "");
            dataSourceUI.setNameCode(nameCode != null ? nameCode.trim() : "");
//            dataSourceUI.setExportDirectory(exprtP != null ? exprtP.trim() : "");
        }

        if(dataSourceUI.getExportDirectory().isEmpty() || dataSourceUI.getExportDirectory().equals(HarvesterUI.getMainConfigurationData().getDefaultExportFolder() + "/")) {
            String dsExportPath = null;
            if(HarvesterUI.getProjectType() == ProjectType.DEFAULT)
                dsExportPath = HarvesterUI.getMainConfigurationData().getDefaultExportFolder() + "/" + record_set;
//            else
//                dsExportPath = HarvesterUI.getMainConfigurationData().getRepositoryFolderPath() + "/" + record_set + "/export";
            dataSourceUI.setExportDirectory(dsExportPath);
        } else {
            dataSourceUI.setExportDirectory(dataSourceUI.getExportDirectory());
        }

        if(isSample != null)
            dataSourceUI.setIsSample(isSample.getValue());

        saveDataSetExternalServices(dataSourceUI);

        dataSetOperationsService.saveDataSource(edit, type, oldDataSetId, dataSourceUI,PageUtil.getCurrentPageSize(), callback);
    }

    private void saveDataSetExternalServices(DataSourceUI dataSourceUI){
        dataSourceUI.getRestServiceUIList().clear();
        if(dataSourceServicesPanel != null && dataSourceServicesPanel.isExpanded() && dataSourceServicesPanel.getItemCount() > 2){
            dataSourceUI.setExternalServicesRunType(dataSourceServicesPanel.getExecutionTypeCombo().getSimpleValue());

            for(Component fieldSetC : dataSourceServicesPanel.getItems()){
                FieldSetWithExternalService fieldSet;
                // Ignore if any external service field set has None
                try{
                    fieldSet = (FieldSetWithExternalService)fieldSetC;
                }catch (ClassCastException e){
                    continue;
                }

                if(fieldSet.getExternalServiceUI() == null)
                    continue;

                for(Component component : fieldSet.getItems()){
                    for(ServiceParameterUI serviceParameterUI: fieldSet.getExternalServiceUI().getServiceParameters()){
                        if(component.getId().equals(serviceParameterUI.getId())){
                            // TODO: different field types
                            if(component instanceof TextField && !(component instanceof DateField) && !(component instanceof SimpleComboBox)){
                                serviceParameterUI.setValue(((TextField<String>)component).getValue());
                            }else if(component instanceof DateField){
                                serviceParameterUI.setValue(((DateField)component).getValue().toString());
                            }else if(component instanceof CheckBoxGroup){
                                CheckBoxGroup checkBoxGroup = (CheckBoxGroup)component;
                                CheckBox checkBox = (CheckBox)checkBoxGroup.get(0);
                                serviceParameterUI.setValue(checkBox.getValue().toString());
                            }else if(component instanceof SimpleComboBox){
                                serviceParameterUI.setValue(((SimpleComboBox<String>)component).getSimpleValue());
                            }else if(component instanceof DataSetListParameter){
                                serviceParameterUI.setValue(((DataSetListParameter)component).getTextField().getValue());
                            }
                        }
                    }
                }
                ExternalServiceUI currentExternalService = fieldSet.getExternalServiceUI();
                ExternalServiceUI newExternalService = new ExternalServiceUI(currentExternalService.getId(),
                        currentExternalService.getName(),currentExternalService.getUri(),
                        currentExternalService.getStatusUri(),
                        currentExternalService.getType(),
                        new ArrayList<ServiceParameterUI>(),currentExternalService.getExternalServiceType());
                copyServiceParameters(currentExternalService,newExternalService);

                // extra fields
                newExternalService.setEnabled(fieldSet.getExternalServiceUI().isEnabled());
                if(currentExternalService.getExternalResultUI() != null)
                    newExternalService.setExternalResultUI(currentExternalService.getExternalResultUI());

                dataSourceUI.getRestServiceUIList().add(newExternalService);
            }
        }
    }

    protected void copyServiceParameters(ExternalServiceUI toCopy, ExternalServiceUI newService){
        for(ServiceParameterUI serviceParameterUI: toCopy.getServiceParameters()){
            ServiceParameterUI newSPUI = new ServiceParameterUI(serviceParameterUI.getName(),
                    serviceParameterUI.getType(),serviceParameterUI.getRequired(),serviceParameterUI.getExample(),
                    serviceParameterUI.getSemantics());
            newSPUI.setValue(serviceParameterUI.getValue());
            newService.getServiceParameters().add(newSPUI);
        }
    }

    public String getDataSetId() {
        return recordSet.getValue();
    }

    public abstract String getMetadataFormat();
    public abstract String getFolderPath();
    public abstract String getSchema();
    public abstract void saveData();
    public abstract void setEditMode(DataSourceUI dataSourceUI);

    public void resetLayout(){
        super.layout(true);
        outputSet.layout(true);
        dataSourceServicesPanel.layout(true);
        layout(true);
    }

    protected void addIsSampleCheckBox(FieldSet fieldSet){
        CheckBoxGroup checkBoxGroup = new CheckBoxGroup();
        isSample = new CheckBox();
        isSample.setValue(false);
        checkBoxGroup.setId("isSampleCB");
        checkBoxGroup.setFieldLabel("Is Sample?");
        checkBoxGroup.add(isSample);
        fieldSet.add(checkBoxGroup);
    }

    protected void editIsSampleCheckBox(Boolean value){
        isSample.setValue(value);
    }

    public FieldSet getDataSet() {
        return dataSet;
    }

    @Override
    protected void onResize(int width, int height) {
        super.onResize(width,height);
        resetLayout();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        resetLayout();
    }

    public DataProviderUI getDataProviderParent() {
        return parent;
    }
}
