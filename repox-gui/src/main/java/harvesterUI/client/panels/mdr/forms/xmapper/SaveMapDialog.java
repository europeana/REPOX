package harvesterUI.client.panels.mdr.forms.xmapper;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.panels.mdr.xmapper.MDRMappingApplicationManager;
import harvesterUI.client.panels.mdr.xmapper.OpenMapInfo;
import harvesterUI.client.servlets.xmapper.XMApperStaticServerAcess;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 08-01-2013
 * Time: 14:33
 */

//TODO Multi lang
public abstract class SaveMapDialog extends FormDialog {

    protected DefaultFormPanel _saveMapFormPanel;
    protected TextField<String> _identifierField, _descriptionField, _fileName;
    protected CheckBoxGroup _isXslVersion2;

    //protected SourceAndTargetFields _sourceTargetFields;
    protected MDRMappingApplicationManager _manager;
    protected TransformationUI _possibleAssociatedTrasformation = null;


    public SaveMapDialog(MDRMappingApplicationManager manager) {
        super(0.55,0.42);
        _manager = manager;
        FormData formData = new FormData("95%");
        setupBaseForm();
        setupForm();
        setupBaseElements(formData);
        setupFileNameField(formData);
        setupButtons();
        add(_saveMapFormPanel);
    }

    private void setupBaseForm() {
        _saveMapFormPanel = new DefaultFormPanel();
        _saveMapFormPanel.setHeaderVisible(false);
        _saveMapFormPanel.setLayout(new EditableFormLayout(160));
    }

    protected abstract void setupForm();

    private void setupBaseElements(FormData formData) {

        _identifierField = new TextField<String>();
        _identifierField.setFieldLabel(HarvesterUI.CONSTANTS.identifier() + HarvesterUI.REQUIRED_STR);
        _identifierField.setId("schm_identifier");
        _identifierField.setName("transformationSubmitID");
        _identifierField.setAllowBlank(false);
        _saveMapFormPanel.add(_identifierField, formData);

        _descriptionField = new TextField<String>();
        _descriptionField.setFieldLabel(HarvesterUI.CONSTANTS.description() + HarvesterUI.REQUIRED_STR);
        _descriptionField.setId("schm_desc");
        _descriptionField.setAllowBlank(false);
        _saveMapFormPanel.add(_descriptionField, formData);

        //--- Source and Destination schema info
        //_sourceTargetFields = _manager.getSchemasFields();
        _saveMapFormPanel.add(_manager.getMapInfo().getDetails(), formData);

        _isXslVersion2 = new CheckBoxGroup();
        CheckBox isXslVersion2CB = new CheckBox();
        _isXslVersion2.setFieldLabel(HarvesterUI.CONSTANTS.xslVersion2() + "?" + HarvesterUI.REQUIRED_STR);
        isXslVersion2CB.setValue(false);
        _isXslVersion2.add(isXslVersion2CB);
        _saveMapFormPanel.add(_isXslVersion2, formData);

    }

    private void setupButtons() {
        Button saveButton = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                saveData();
            }
        });
        _saveMapFormPanel.addButton(saveButton);

        _saveMapFormPanel.addButton(new Button(HarvesterUI.CONSTANTS.cancel(), HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                hide();
            }
        }));

        _saveMapFormPanel.setButtonAlign(Style.HorizontalAlignment.CENTER);
        FormButtonBinding binding = new FormButtonBinding(_saveMapFormPanel);
        binding.addButton(saveButton);
    }

    protected abstract void setupFileNameField(FormData formData);

    protected abstract void onSaveSuccessful();

    private void saveData(){
        AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
            public void onFailure(Throwable caught) {
                unmask();
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(ResponseState responseState) {
                unmask();
                if(responseState == ResponseState.MAPPING_SAME_SRC_AND_DEST){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveTransformation(), "Transformation with the same source and destination format already exists.");
                }else if(responseState == ResponseState.MAPPING_SAME_XSL){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveTransformation(), "Transformation with this XSL name already exists.");
                }else if(responseState == ResponseState.ERROR_SAVING_XSL){
                     HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveTransformation(), "Transformation couldn't be saved due to server maximum form content reached or file permissions.");
                } else if(responseState == ResponseState.ALREADY_EXISTS){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveTransformation(), "Transformation with this Identifier already exists.");
                }else if(responseState == ResponseState.SUCCESS) {//SUCCESS
                    _manager.setSaved(true);
                    hide();
                    onSaveSuccessful();
                    _possibleAssociatedTrasformation = null;
                    HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.saveTransformation(), HarvesterUI.CONSTANTS.saveTransformationSuccess());
                } else { //ERROR
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveTransformation(), "Unexpected error while trying to save the transformation...");
                }
            }
        };

        OpenMapInfo mapInfo = _manager.getMapInfo();
        String identifier = _identifierField.getValue();
        String description = _descriptionField.getValue();
        String srcFormat = mapInfo.getSourceSchema().getShortDesignation();
        String destFormat = mapInfo.getDestSchema().getShortDesignation();
        String stylesheet= _fileName.getValue(); //any extension the user writes will be eliminated and only the filename used
        String destSchemaStr = mapInfo.getDestVersion().getXsdLink();
        String destMtdNamespace = mapInfo.getDestSchema().getNamespace();
        String sourceSchemaStr = mapInfo.getSourceVersion().getXsdLink();
        TransformationUI transformationUI = new TransformationUI(identifier,description,srcFormat,destFormat,
                destSchemaStr,destMtdNamespace, stylesheet,(Boolean) _isXslVersion2.get(0).getValue());
        transformationUI.setSourceSchema(sourceSchemaStr);
        transformationUI.setMDRCompliant(true);
        transformationUI.setEditable(true);

        _possibleAssociatedTrasformation = transformationUI;

        mask("Saving Transformation... Please wait...");
        TransformationUI t = _manager.getTransformation();
        String oldId = t == null? "":t.getIdentifier();
        XMApperStaticServerAcess.getService().saveMapping(_manager.getModel(),
                transformationUI,oldId,
                callback);
    }

    public void edit(TransformationUI transformationUI){
        /*TODO
        _associatedTransformation = transformationUI;
        setHeading(HarvesterUI.CONSTANTS.editTransformation()+": " + transformationUI.getIdentifier());
        setIcon(HarvesterUI.ICONS.operation_edit());
        _identifierField.setValue(transformationUI.getIdentifier());
        _descriptionField.setValue(transformationUI.getDescription());
        _sourceTargetFields.loadCombos(transformationUI);

        xsdUploadField.setValue(transformationUI.getXslFilePath());
        ((CheckBox) _isXslVersion2.get(0)).setValue(transformationUI.getIsXslVersion2());*/
    }

    public void resetValues() {
        //_sourceTargetFields.loadCombos(null);
        //_associatedTransformation = null;
        _identifierField.clear();
        _descriptionField.clear();
        //_sourceTargetFields = null;

        _fileName.clear();
        ((CheckBox) _isXslVersion2.get(0)).setValue(true);

        setHeading(HarvesterUI.CONSTANTS.addTransformation());
    }
}
