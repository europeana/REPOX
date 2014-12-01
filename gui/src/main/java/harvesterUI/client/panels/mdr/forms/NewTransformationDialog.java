package harvesterUI.client.panels.mdr.forms;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.panels.mdr.forms.xmapper.SourceAndTargetFields;
import harvesterUI.client.servlets.transformations.TransformationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-04-2011
 * Time: 17:19
 */
public class NewTransformationDialog extends FormDialog {

    private DefaultFormPanel newTransformationFormPanel;
    private TextField<String> identifierField, descriptionField;
    private FileUploadField xslUploadField;
    private CheckBoxGroup isXslVersion2;
    private TransformationUI associatedTransformation = null;

    private SourceAndTargetFields _sourceTargetFields;


    public NewTransformationDialog() {
        super(0.65,0.5);
        createNewUserDialog();
    }

    private void createNewUserDialog() {
        FormData formData = new FormData("95%");
        setHeading(HarvesterUI.CONSTANTS.addTransformation());
        setIcon(HarvesterUI.ICONS.mapping_new());

        newTransformationFormPanel = new DefaultFormPanel();
        newTransformationFormPanel.setHeaderVisible(false);
        newTransformationFormPanel.setLayout(new EditableFormLayout(160));

        newTransformationFormPanel.setMethod(FormPanel.Method.POST);
        newTransformationFormPanel.setEncoding(FormPanel.Encoding.MULTIPART);
        newTransformationFormPanel.setAction(GWT.getModuleBaseURL() + "transformationfileupload");

        newTransformationFormPanel.addListener(Events.Submit, new Listener<ComponentEvent>() {
            public void handleEvent(ComponentEvent be) {
                saveData();
            }
        });

        identifierField = new TextField<String>();
        identifierField.setFieldLabel(HarvesterUI.CONSTANTS.identifier()+ HarvesterUI.REQUIRED_STR);
        identifierField.setId("schm_identifier");
        identifierField.setName("transformationSubmitID");
        identifierField.setAllowBlank(false);
        newTransformationFormPanel.add(identifierField, formData);

        descriptionField = new TextField<String>();
        descriptionField.setFieldLabel(HarvesterUI.CONSTANTS.description()+ HarvesterUI.REQUIRED_STR);
        descriptionField.setId("schm_desc");
        descriptionField.setAllowBlank(false);
        newTransformationFormPanel.add(descriptionField, formData);

        //--- Source and Target fields of the form
        _sourceTargetFields = new SourceAndTargetFields(newTransformationFormPanel,formData);
        _sourceTargetFields.loadCombos(null);
        newTransformationFormPanel.add(_sourceTargetFields.getDetailsContainer(), formData);

        isXslVersion2 = new CheckBoxGroup();
        CheckBox isXslVersion2CB = new CheckBox();
        isXslVersion2.setFieldLabel(HarvesterUI.CONSTANTS.xslVersion2() + "?" + HarvesterUI.REQUIRED_STR);
        isXslVersion2CB.setValue(true);
        isXslVersion2.add(isXslVersion2CB);
        newTransformationFormPanel.add(isXslVersion2,formData);

        xslUploadField = new FileUploadField();
        xslUploadField.setId("upload");
        xslUploadField.setName("upload");
        xslUploadField.setAllowBlank(false);
        xslUploadField.setFieldLabel(HarvesterUI.CONSTANTS.transformationFile());
        newTransformationFormPanel.add(xslUploadField,formData);

        Button saveButton = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                validateSending();
            }
        });

        newTransformationFormPanel.addButton(saveButton);
        newTransformationFormPanel.addButton(new Button(HarvesterUI.CONSTANTS.cancel(),HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                hide();
                //Dispatcher.forwardEvent(AppEvents.ReloadTransformations);
            }
        }));

        newTransformationFormPanel.setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(newTransformationFormPanel);
        binding.addButton(saveButton);

        add(newTransformationFormPanel);
    }

    private void validateSending() {
        AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(ResponseState responseState) {
                if(responseState == ResponseState.MAPPING_SAME_SRC_AND_DEST){ //todo multi lang
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveTransformation(), "Transformation with the same source and destination format already exists.");
                }else if(responseState == ResponseState.MAPPING_SAME_XSL){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveTransformation(), "Transformation with this XSL name already exists.");
                }else if(responseState == ResponseState.ERROR_SAVING_XSL){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveTransformation(), "Transformation couldn't be saved due to server maximum form content reached or file permissions.");
                }else if(responseState == ResponseState.ALREADY_EXISTS){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveTransformation(), "Transformation with this Identifier already exists.");
                }else if(responseState == ResponseState.DOES_NOT_END_XSL){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveTransformation(), "File has to end in .xsl");
                }else if(responseState == ResponseState.SUCCESS){
                    newTransformationFormPanel.submit();
                }
            }
        };

        TransformationsServiceAsync service = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
        service.validateTransformation(identifierField.getValue(), xslUploadField.getValue(),associatedTransformation == null? "":associatedTransformation.getIdentifier(), callback);
    }

    private void saveData(){
        AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(ResponseState responseState) {
                if(responseState == ResponseState.MAPPING_SAME_SRC_AND_DEST){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveTransformation(), "Transformation with the same source and destination format already exists.");
                }else if(responseState == ResponseState.MAPPING_SAME_XSL){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveTransformation(), "Transformation with this XSL name already exists.");
                }else if(responseState == ResponseState.ERROR_SAVING_XSL){
                   HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveTransformation(), "Transformation couldn't be saved due to server maximum form content reached or file permissions.");
                } else if(responseState == ResponseState.ALREADY_EXISTS){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveTransformation(), "Transformation with this Identifier already exists.");
                }else{//SUCCESS
                    hide();
                    HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.saveTransformation(), HarvesterUI.CONSTANTS.saveTransformationSuccess());
                    //Update schema versions statistics
                    Dispatcher.forwardEvent(AppEvents.ReloadTransformations);
                }
            }
        };

        String identifier = identifierField.getValue();
        String description = descriptionField.getValue();
        String srcFormat = _sourceTargetFields.getSourceFormat();
        String destFormat = _sourceTargetFields.getTargetFormat();
        String stylesheet= xslUploadField.getValue().replace("C:\\fakepath\\","").replace("c:\\fakepath\\","");
        String destSchemaStr = _sourceTargetFields.getTargetSchema();
        String destMtdNamespace = _sourceTargetFields.getTargetMNamespace();
        String sourceSchemaStr = _sourceTargetFields.getSourceSchema();
        TransformationUI transformationUI = new TransformationUI(identifier,description,srcFormat,destFormat,
                destSchemaStr,destMtdNamespace,stylesheet,(Boolean)isXslVersion2.get(0).getValue());
        transformationUI.setSourceSchema(sourceSchemaStr);
        transformationUI.setMDRCompliant(true);
        transformationUI.setEditable(associatedTransformation == null ? false : associatedTransformation.isEditable());
        TransformationsServiceAsync service = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
        service.saveTransformation(transformationUI,associatedTransformation == null? "":associatedTransformation.getIdentifier(), callback);
    }

    public void edit(TransformationUI transformationUI){
        associatedTransformation = transformationUI;
        setHeading(HarvesterUI.CONSTANTS.editTransformation()+": " + transformationUI.getIdentifier());
        setIcon(HarvesterUI.ICONS.operation_edit());
        identifierField.setValue(transformationUI.getIdentifier());
        descriptionField.setValue(transformationUI.getDescription());
        _sourceTargetFields.loadCombos(transformationUI);

        xslUploadField.setValue(transformationUI.getXslFilePath());
        ((CheckBox)isXslVersion2.get(0)).setValue(transformationUI.getIsXslVersion2());
    }

    public void resetValues() {
        _sourceTargetFields.loadCombos(null);
        associatedTransformation = null;
        identifierField.clear();
        descriptionField.clear();
        _sourceTargetFields.resetFields();

        xslUploadField.clear();
        ((CheckBox)isXslVersion2.get(0)).setValue(false);

        setHeading(HarvesterUI.CONSTANTS.addTransformation());
    }
}
