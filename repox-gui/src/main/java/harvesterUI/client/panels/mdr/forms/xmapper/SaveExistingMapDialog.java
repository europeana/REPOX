package harvesterUI.client.panels.mdr.forms.xmapper;

import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.mdr.xmapper.MDRMappingApplicationManager;
import harvesterUI.shared.mdr.TransformationUI;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 08-01-2013
 * Time: 14:34
 */
public class SaveExistingMapDialog extends SaveMapDialog{

    public SaveExistingMapDialog(MDRMappingApplicationManager manager) {
        super(manager);
        TransformationUI t = _manager.getTransformation();
        _identifierField.setValue(t.getIdentifier());
        _identifierField.setEnabled(false);
        _descriptionField.setValue(t.getDescription());
        _descriptionField.setEnabled(false);
        ((CheckBox)_isXslVersion2.get(0)).setValue(t.getIsXslVersion2());
        _isXslVersion2.setEnabled(false);
    }

    @Override
    protected void setupForm() {
        setHeading("Save (Overwrite) Existing Transformation");
        setIcon(HarvesterUI.ICONS.mappings_icon());

    }

    @Override
    protected void setupFileNameField(FormData formData) {
        _fileName = new TextField<String>();
        _fileName.setFieldLabel("File name:");
        _fileName.setValue(_manager.getTransformation().getXslFilePath());
        _fileName.setEnabled(false);
        _saveMapFormPanel.add(_fileName, formData);
    }

    @Override
    protected void onSaveSuccessful() {
        //Dont need anywhing besides the common stuff...
    }

}
