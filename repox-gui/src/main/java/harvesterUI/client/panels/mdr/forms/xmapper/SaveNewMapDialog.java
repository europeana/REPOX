package harvesterUI.client.panels.mdr.forms.xmapper;

import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.mdr.xmapper.MDRMappingApplicationManager;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 08-01-2013
 * Time: 14:33
 */
public class SaveNewMapDialog extends SaveMapDialog {

    public SaveNewMapDialog(MDRMappingApplicationManager manager) {
        super(manager);
    }

    @Override
    protected void setupForm() {
        setHeading("Save New Transformation");
        setIcon(HarvesterUI.ICONS.mapping_new());

    }

    @Override
    protected void setupFileNameField(FormData formData) {
        _fileName = new TextField<String>();
        _fileName.setFieldLabel("File name:");
        _fileName.setAllowBlank(false);
        _saveMapFormPanel.add(_fileName, formData);
    }

    @Override
    protected void onSaveSuccessful() {
        //Needs to change to edit mode...
        _manager.setNewMap(false);
        _manager.setTransformation(_possibleAssociatedTrasformation);
    }
}
